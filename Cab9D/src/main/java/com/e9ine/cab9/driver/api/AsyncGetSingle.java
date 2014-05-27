package com.e9ine.cab9.driver.api;

import android.os.AsyncTask;
import android.support.v4.util.SimpleArrayMap;

import com.e9ine.cab9.driver.model.Credentials;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public abstract class AsyncGetSingle<T> extends AsyncTask<Long, Void, WrappedApiResult<T>> {

	public SimpleArrayMap<String, String> mQueryValues = new SimpleArrayMap<String, String>();

	@Override
	protected final WrappedApiResult<T> doInBackground(Long... params) {
		HttpClient client = new DefaultHttpClient();
		String path = getApiPath() + GenerateQueryString();
		HttpGet request = new HttpGet(path);
		request.setHeader("content-type", "application/json");
		request.setHeader("authorization", Credentials.getAuthenticatedHeader("GET", path));
		HttpResponse response;
		int HttpStatusCode;
		InputStream stream;
		String asString;
		JSONObject jsonObject;
		T result;

		try {
			response = client.execute(request);
			HttpStatusCode = response.getStatusLine().getStatusCode();
			switch (HttpStatusCode) {
				case HttpStatus.SC_UNAUTHORIZED:
					return new WrappedApiResult<T>(WrappedApiResult.UNAUTHORIZED_CODE);
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					return new WrappedApiResult<T>(WrappedApiResult.SERVER_ERROR_CODE);
				case HttpStatus.SC_NOT_FOUND:
					return new WrappedApiResult<T>(WrappedApiResult.NOT_FOUND_CODE);
				case HttpStatus.SC_BAD_REQUEST:
					return new WrappedApiResult<T>(WrappedApiResult.BAD_REQUEST_CODE);
				case HttpStatus.SC_OK:
					break;
				default:
					return new WrappedApiResult<T>(WrappedApiResult.OTHER_ERROR_CODE);
			}
			stream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			stream.close();
			asString = sb.toString();
			jsonObject = new JSONObject(asString);
			result = fromJSON(jsonObject);
		} catch (Exception ignored) {
			return new WrappedApiResult<T>(WrappedApiResult.OTHER_ERROR_CODE);
		}

		return new WrappedApiResult<T>(result);
	}

	@Override
	protected void onPostExecute(WrappedApiResult<T> response) {
		if (response.Success) {
			onResultReturned(response.Result);
		} else {
			onErrorResponse(response.FailureCode);
		}
	}

	protected String GenerateQueryString() {
		if (mQueryValues.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		for (int i = 0; i < mQueryValues.size(); i++) {
			try {
				String key = mQueryValues.keyAt(i);
				String value = mQueryValues.valueAt(i);
				sb.append(URLEncoder.encode(key, "UTF-8"));
				sb.append("=");
				sb.append(URLEncoder.encode(value, "UTF-8"));
				if (i < (mQueryValues.size() - 1)) {
					sb.append("&");
				}
			} catch (UnsupportedEncodingException ignored) {

			}
		}
		return sb.toString();
	}

	protected void AddQueryParameter(String key, String value) {
		mQueryValues.put(key, value);
	}

	protected abstract String getApiPath();

	protected abstract T fromJSON(JSONObject json);

	@Override
	protected abstract void onPreExecute();

	protected abstract void onResultReturned(T result);

	protected abstract void onErrorResponse(int failureCode);
}
