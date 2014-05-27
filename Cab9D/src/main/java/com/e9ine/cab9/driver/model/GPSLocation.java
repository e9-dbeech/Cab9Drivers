package com.e9ine.cab9.driver.model;

import android.os.AsyncTask;

import com.e9ine.cab9.driver.api.WrappedApiResult;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by David on 05/01/14 for com.e9ine.cab9.drivers.model.
 */
public class GPSLocation extends ServerModel {
	public Date Timestamp;
	public LatLng Location;
	public Integer ShiftId;

	public GPSLocation(double latitude, double longitude, int shiftId) {
		Timestamp = new Date();
		Location = new LatLng(latitude, longitude);
		ShiftId = shiftId;
	}

	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		TryPut(result, "Timestamp", ISO_8601.format(Timestamp));
		TryPut(result, "Latitude", Location.latitude);
		TryPut(result, "Latitude", Location.longitude);
		TryPut(result, "ShiftId", ShiftId);
		return result;
	}

	public static class Api {
		public static final String POST_LOCATION_PATH = "/Shift/PostPoint";

		public abstract class PostLocationTask extends AsyncTask<GPSLocation, Void, WrappedApiResult<Boolean>> {
			@Override
			protected final WrappedApiResult<Boolean> doInBackground(GPSLocation... params) {
				if (params.length != 1) {
					return new WrappedApiResult<Boolean>(WrappedApiResult.PARAMETER_ERROR_CODE);
				}
				GPSLocation input = params[0];
				HttpClient client = new DefaultHttpClient();
				String path = POST_LOCATION_PATH;
				HttpPost request = new HttpPost(path);
				request.setHeader("content-type", "application/json");
				request.setHeader("authorization", Credentials.getAuthenticatedHeader("PUT", path));
				HttpResponse response;
				int HttpStatusCode;

				try {
					StringEntity entity = new StringEntity(input.toJSONString());
					entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					request.setEntity(entity);
					response = client.execute(request);
					HttpStatusCode = response.getStatusLine().getStatusCode();
					switch (HttpStatusCode) {
						case HttpStatus.SC_UNAUTHORIZED:
							return new WrappedApiResult<Boolean>(WrappedApiResult.UNAUTHORIZED_CODE);
						case HttpStatus.SC_INTERNAL_SERVER_ERROR:
							return new WrappedApiResult<Boolean>(WrappedApiResult.SERVER_ERROR_CODE);
						case HttpStatus.SC_NOT_FOUND:
							return new WrappedApiResult<Boolean>(WrappedApiResult.NOT_FOUND_CODE);
						case HttpStatus.SC_BAD_REQUEST:
							return new WrappedApiResult<Boolean>(WrappedApiResult.BAD_REQUEST_CODE);
						case HttpStatus.SC_OK:
							return new WrappedApiResult<Boolean>(true);
						case HttpStatus.SC_CREATED:
							return new WrappedApiResult<Boolean>(true);
						default:
							return new WrappedApiResult<Boolean>(WrappedApiResult.OTHER_ERROR_CODE);
					}
				} catch (Exception ignored) {
					return new WrappedApiResult<Boolean>(WrappedApiResult.OTHER_ERROR_CODE);
				}
			}

			@Override
			protected void onPostExecute(WrappedApiResult<Boolean> response) {
				if (response.Success) {
					onResultReturned(response.Result);
				} else {
					onErrorResponse(response.FailureCode);
				}
			}

			protected abstract void onResultReturned(Boolean result);

			protected abstract void onErrorResponse(int failureCode);
		}
	}
}
