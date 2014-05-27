package com.e9ine.cab9.driver.api;

import android.os.AsyncTask;

import com.e9ine.cab9.driver.model.Credentials;
import com.e9ine.cab9.driver.model.ServerModel;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class AsyncPut<T extends ServerModel> extends AsyncTask<T, Void, WrappedApiResult<T>> {
	@SafeVarargs
	@Override
	protected final WrappedApiResult<T> doInBackground(T... params) {
		if (params.length != 1) {
			return new WrappedApiResult<T>(WrappedApiResult.PARAMETER_ERROR_CODE);
		}
		T input = params[0];
		HttpClient client = new DefaultHttpClient();
		String path = getApiPath();
		HttpPut request = new HttpPut(path);
		request.setHeader("content-type", "application/json");
		request.setHeader("authorization", Credentials.getAuthenticatedHeader("PUT", path));
		HttpResponse response;
		int HttpStatusCode;
		InputStream stream;
		String asString;
		JSONObject jsonObject;
		T result;

		try {
			StringEntity entity = new StringEntity(input.toJSONString());
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			request.setEntity(entity);
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
		if (result == null) {
			return new WrappedApiResult<T>(WrappedApiResult.OTHER_ERROR_CODE);
		} else {
			return new WrappedApiResult<T>(result);
		}
	}

	@Override
	protected void onPostExecute(WrappedApiResult<T> response) {
		if (response.Success) {
			onResultReturned(response.Result);
		} else {
			onErrorResponse(response.FailureCode);
		}
	}

	protected abstract String getApiPath();

	protected abstract T fromJSON(JSONObject json);

	protected abstract void onResultReturned(T result);

	protected abstract void onErrorResponse(int failureCode);
}

