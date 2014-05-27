package com.e9ine.cab9.driver.api;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public abstract class AsyncGetGoogleMatrix extends AsyncTask<Long, Void, JSONObject> {

	public static final String googleApiKey = "";
	public static final String BASE_PATH = "http://maps.googleapis.com/maps/api/distancematrix/json?";
	public LatLng mOrigin;
	public ArrayList<LatLng> mDestinations = new ArrayList<LatLng>();

	@Override
	protected final JSONObject doInBackground(Long... params) {
		HttpClient client = new DefaultHttpClient();
		StringBuilder pre = new StringBuilder();
		pre.append(BASE_PATH);
		if (!TextUtils.isEmpty(googleApiKey)) {
			pre.append("key=").append(googleApiKey).append("&");
		}
		pre.append("origins=").append(String.valueOf(mOrigin.latitude)).append(",").append(String.valueOf(mOrigin.longitude)).append("&");
		pre.append(GenerateQueryString());
		pre.append("&sensor=true");

		HttpGet request = new HttpGet(pre.toString());
		request.setHeader("content-type", "application/json");
		//request.setHeader("authorization", Credentials.getAuthenticatedHeader("GET", path));
		HttpResponse response;
		int HttpStatusCode;
		InputStream stream;
		String asString;
		JSONObject result;

		try {
			response = client.execute(request);
			HttpStatusCode = response.getStatusLine().getStatusCode();
			switch (HttpStatusCode) {
				case HttpStatus.SC_UNAUTHORIZED:
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
				case HttpStatus.SC_NOT_FOUND:
				case HttpStatus.SC_BAD_REQUEST:
					return null;
				case HttpStatus.SC_OK:
					break;
				default:
					return null;
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
			result = new JSONObject(asString);

		} catch (Exception ignored) {
			return null;
		}

		return result;
	}

	protected String GenerateQueryString() {
		StringBuilder sb = new StringBuilder();
		sb.append("destinations=");
		for (LatLng p : mDestinations) {
			sb.append(String.valueOf(p.latitude));
			sb.append(",");
			sb.append(String.valueOf(p.longitude));
			sb.append("%7C");
		}
		return sb.substring(0, sb.lastIndexOf("%7C"));
	}

	@Override
	protected abstract void onPreExecute();
}

