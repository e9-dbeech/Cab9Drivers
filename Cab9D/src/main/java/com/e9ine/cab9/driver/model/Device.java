package com.e9ine.cab9.driver.model;

import com.e9ine.cab9.driver.api.AsyncPost;

import org.json.JSONObject;

/**
 * Created by David on 11/02/14 for com.e9ine.cab9.driver.model.
 */
public class Device extends ServerModel{
	public String DeviceToken;
	public String DeviceType = "Android";

	public Device(JSONObject json) {
		DeviceToken = json.optString("DeviceToken", "");
		DeviceType = "Android";
	}

	public Device(String token) {
		DeviceToken = token;
		DeviceType = "Android";
	}

	@Override
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		TryPut(result, "DeviceToken", DeviceToken);
		TryPut(result, "DeviceType", "Android");
		return result;
	}

	public static class Api {
		public static String MODEL_PATH = API_PATH + "/User";
		public static String REGISTER_DEVICE = MODEL_PATH + "/RegisterDevice";

		public abstract static class RegisterDevice extends AsyncPost<Device>{
			@Override
			protected String getApiPath() {
				return REGISTER_DEVICE;
			}

			@Override
			protected Device fromJSON(JSONObject json) {
				return new Device(json);
			}

			@Override
			protected abstract void onResultReturned(Device result);

			@Override
			protected abstract void onErrorResponse(int failureCode);
		}
	}
}
