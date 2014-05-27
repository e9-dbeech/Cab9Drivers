package com.e9ine.cab9.driver.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.e9ine.cab9.driver.api.AsyncGetById;
import com.e9ine.cab9.driver.api.AsyncGetSingle;
import com.e9ine.cab9.driver.api.WrappedApiResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by David on 03/02/14 for com.e9ine.cab9.driver.model.
 */
public class Driver extends ServerModel {
	public int ID;
	public String CallSign;
	public String Fullname;
	public String Address1;
	public String Address2;
	public String Area;
	public String Town;
	public String County;
	public String Postcode;
	public String Email;
	public String Mobile;
	public int Status;
	public long CurrentShiftID;
	public long CurrentBookingID;
	public int CurrentVehicleID;

	public Driver(JSONObject json) {
		ID = json.optInt(Fields.ID, -1);
		CallSign = json.optString(Fields.CALL_SIGN, "");
		Fullname = json.optString(Fields.FULLNAME, "");
		Address1 = json.optString(Fields.ADDRESS_1, "");
		Address2 = json.optString(Fields.ADDRESS_2, "");
		Area = json.optString(Fields.AREA, "");
		Town = json.optString(Fields.TOWN, "");
		County = json.optString(Fields.COUNTY, "");
		Postcode = json.optString(Fields.POSTCODE, "");
		Email = json.optString(Fields.EMAIL, "");
		Mobile = json.optString(Fields.MOBILE, "");
		Status = json.optInt(Fields.STATUS, -1);
		CurrentShiftID = json.optLong(Fields.CURRENTSHIFTID, -1);
		CurrentBookingID = json.optLong(Fields.CURRENTBOOKINGID, -1);
		CurrentVehicleID = json.optInt(Fields.CURRENTVEHICLEID, -1);
	}

	public static class Fields {
		public final static String ID = "ID";
		public final static String CALL_SIGN = "CallSign";
		public final static String FULLNAME = "Fullname";
		public final static String ADDRESS_1 = "Address1";
		public final static String ADDRESS_2 = "Address2";
		public final static String AREA = "Area";
		public final static String TOWN = "Town";
		public final static String COUNTY = "County";
		public final static String POSTCODE = "Postcode";
		public final static String EMAIL = "Email";
		public final static String MOBILE = "Mobile";
		public final static String STATUS = "Status";
		public final static String CURRENTSHIFTID = "CurrentShiftID";
		public final static String CURRENTBOOKINGID = "CurrentBookingID";
		public final static String CURRENTVEHICLEID = "CurrentVehicleID";
	}

	@Override
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		TryPut(result, Fields.ID, ID);
		TryPut(result, Fields.CALL_SIGN, CallSign);
		TryPut(result, Fields.FULLNAME, Fullname);
		TryPut(result, Fields.ADDRESS_1, Address1);
		TryPut(result, Fields.ADDRESS_2, Address2);
		TryPut(result, Fields.AREA, Area);
		TryPut(result, Fields.TOWN, Town);
		TryPut(result, Fields.COUNTY, County);
		TryPut(result, Fields.POSTCODE, Postcode);
		TryPut(result, Fields.EMAIL, Email);
		TryPut(result, Fields.MOBILE, Mobile);
		TryPut(result, Fields.STATUS, Status);
		TryPut(result, Fields.CURRENTSHIFTID, CurrentShiftID);
		TryPut(result, Fields.CURRENTBOOKINGID, CurrentBookingID);
		TryPut(result, Fields.CURRENTVEHICLEID, CurrentVehicleID);
		return result;
	}

	public static class Api {
		public static final String IMAGE_PATH = API_PATH + "/Image?ownerType=DRIVER&imageType=PROFILE&ownerId=";
		public static final String MODEL_PATH = API_PATH + "/Driver";
		public static final String GET_BY_ID_PATH = MODEL_PATH + "/GetByID";
		public static final String PUT_WITH_IMAGE_PATH = MODEL_PATH + "/PutWithImage";

		public static abstract class GetDriverTask extends AsyncGetSingle<Driver> {
			@Override
			protected String getApiPath() {
				return GET_BY_ID_PATH;
			}

			@Override
			protected Driver fromJSON(JSONObject json) {
				return new Driver(json);
			}
		}

		public static abstract class GetDriverImageTask extends AsyncTask<Driver, Void, Bitmap> {
			@Override
			protected Bitmap doInBackground(Driver... params) {
				Driver driver = params[0];
				if (driver == null) return null;
				String urlString = IMAGE_PATH + driver.ID;
				InputStream stream;

				Bitmap result = null;
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inSampleSize = 1;

				try {
					URL url = new URL(urlString);
					URLConnection connection = url.openConnection();
					HttpURLConnection httpConnection = (HttpURLConnection) connection;
					httpConnection.setRequestMethod("GET");
					httpConnection.connect();

					if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						stream = httpConnection.getInputStream();
						result = BitmapFactory.decodeStream(stream, null, bmOptions);
						stream.close();
						return result;
					}
				} catch (Exception e) {
						e.printStackTrace();
				}
				return null;
			}

			@Override
			protected abstract void onPostExecute(Bitmap bitmap);
		}

		public static abstract class PostDriverWithNewImage extends AsyncTask<Void, Void, Void> {
			public String mFilePath;
			public Driver mDriver;

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient client = new DefaultHttpClient();
				String path = GET_BY_ID_PATH + "?driverid=" + String.valueOf(mDriver.ID);
				HttpGet request = new HttpGet(path);
				request.setHeader("content-type", "application/json");
				request.setHeader("authorization", Credentials.getAuthenticatedHeader("GET", path));
				HttpResponse response;
				InputStream stream;
				String asString = "";

				try {
					response = client.execute(request);
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
				} catch (Exception ignored) {}
				HttpClient httpclient = new DefaultHttpClient();
				HttpPut httppost = new HttpPut(PUT_WITH_IMAGE_PATH);
				httppost.setHeader("authorization", Credentials.getAuthenticatedHeader("PUT", PUT_WITH_IMAGE_PATH));
				MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
				multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				multipartEntity.addTextBody("model", asString);
				multipartEntity.addTextBody("newpicture", "true");
				multipartEntity.addPart("file", new FileBody(new File(mFilePath)));
				httppost.setEntity(multipartEntity.build());
				try {
					HttpResponse response2 = httpclient.execute(httppost);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
	}
}
