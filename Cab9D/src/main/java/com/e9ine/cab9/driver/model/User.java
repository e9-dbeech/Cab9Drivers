package com.e9ine.cab9.driver.model;

import com.e9ine.cab9.driver.api.AsyncGet;
import com.e9ine.cab9.driver.api.AsyncGetSingle;

import org.json.JSONObject;

/**
 * Created by David on 05/01/14 for com.e9ine.cab9.driver.model.
 */
public class User extends ServerModel {
	public String Username;
	public String Email;
	public Integer CompanyID;
	public String UserType;
	public Integer UserID;
	public String Name;

	public User(JSONObject json) {
		Username = json.optString(Fields.USERNAME, "");
		Email = json.optString(Fields.EMAIL, "");
		CompanyID = json.optInt(Fields.COMPANY_ID, -1);
		UserType = json.optString(Fields.USER_TYPE, "Driver");
		UserID = json.optInt(Fields.USER_ID, -1);
		Name = json.optString(Fields.NAME, "");
	}

	@Override
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		TryPut(result, Fields.USERNAME, Username);
		TryPut(result, Fields.EMAIL, Email);
		TryPut(result, Fields.COMPANY_ID, CompanyID);
		TryPut(result, Fields.USER_TYPE, UserType);
		TryPut(result, Fields.USER_ID, UserID);
		TryPut(result, Fields.NAME, Name);
		return result;
	}

	public static class Fields {
		public final static String USERNAME = "Username";
		public final static String EMAIL = "Email";
		public final static String COMPANY_ID = "CompanyID";
		public final static String USER_TYPE = "UserType";
		public final static String USER_ID = "UserID";
		public final static String NAME = "Name";
	}

	public static class Api {
		public static final String MODEL_PATH = API_PATH + "/User";
		public static final String GET_CURRENT_PATH = MODEL_PATH + "/GetCurrent";

		public static abstract class GetCurrentUserTask extends AsyncGetSingle<User> {
			@Override
			protected User fromJSON(JSONObject json) {
				return new User(json);
			}

			@Override
			protected String getApiPath() {
				return GET_CURRENT_PATH;
			}
		}
	}

	public class LocalDB {
		public static final String TABLE_NAME = "USERS_TABLE";
	}
}
