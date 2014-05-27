package com.e9ine.cab9.driver.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.appcompat.R;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.api.AsyncPost;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by David on 04/01/14 for com.e9ine.cab9.drivers.model.
 */
public class Credentials {
	public final static String PREF_FILENAME = "com.e9ine.cab9.driver.CREDENTIALS";
	public final static String PREF_EMAIL_KEY = "com.e9ine.cab9.driver.CREDENTIALS.EMAIL";
	public final static String PREF_PASSWORD_KEY = "com.e9ine.cab9.driver.CREDENTIALS.PASSWORD";
	public final static String PREF_MESSAGING_ID = "com.e9ine.cab9.driver.CREDENTIALS.MESSAGING";

	private final static String SENDER_ID = "529633363188";

	private static Credentials currentUser = null;

	private static GoogleCloudMessaging googleCloudMessaging;
	private static String registrationId;

	public String Username = "";
	public String Password = "";

	public Credentials(String _username, String _password) {
		Username = _username;
		Password = _password;
	}

	public static String getAuthenticatedHeader(String method, String uri) {
		if (currentUser != null) {
			String preHash = method + "&" + uri;
			String result = encode(currentUser.Password, preHash);
			return "Hash " + currentUser.Username + ":" + result;
		} else {
			return "Hash NOTSET:NOTSET";
		}
	}

	public static String encode(String key, String data) {
		String result = "";
		try {
			Mac sha256_hmac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
			sha256_hmac.init(secret_key);

			byte[] bytes = sha256_hmac.doFinal(data.getBytes());
			StringBuffer hash = new StringBuffer();
			for (byte aByte : bytes) {
				String hex = Integer.toHexString(0xFF & aByte);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}

			result = hash.toString();
		} catch (InvalidKeyException ignored) {
		} catch (NoSuchAlgorithmException ignored) {
		}
		return result;
	}

	public static Credentials getCurrent() {
		return getStoredCredentials();
	}

	public static void setCurrent(Credentials user) {
		currentUser = user;
	}

	public static Credentials getStoredCredentials() {
		if (currentUser != null) {
			return currentUser;
		} else {
			SharedPreferences prefs =  ApplicationClass.getInstance().getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
			String username = prefs.getString(PREF_EMAIL_KEY, null);
			String password = prefs.getString(PREF_PASSWORD_KEY, null);
			if (username == null || password == null) {
				currentUser = null;
			} else {
				currentUser = new Credentials(username, password);
			}
			return currentUser;
		}
	}

	public static void putStoredCredentials(Credentials credentials) {
		if (credentials == null || credentials.Username == null || credentials.Password == null) {
			SharedPreferences prefs =  ApplicationClass.getInstance().getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove(PREF_PASSWORD_KEY);
			currentUser = null;
		} else {
			SharedPreferences prefs =  ApplicationClass.getInstance().getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(PREF_EMAIL_KEY, credentials.Username);
			editor.putString(PREF_PASSWORD_KEY, credentials.Password);
			editor.commit();
			currentUser = credentials;
		}

	}

	public static void registerForCloudMessages(){
		Context context = ApplicationClass.getInstance().getApplicationContext();
		googleCloudMessaging = GoogleCloudMessaging.getInstance(context);

		final SharedPreferences prefs = ApplicationClass.getInstance().getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
		registrationId = prefs.getString(PREF_MESSAGING_ID, "");

		if (registrationId.isEmpty()){
			AsyncTask<String, String, String> register = new AsyncTask<String, String, String>() {
				@Override
				protected String doInBackground(String... params) {
					try {
						registrationId = googleCloudMessaging.register(SENDER_ID);
						return registrationId;
					} catch (Exception ignored) {
						Exception next = ignored;
						ignored.printStackTrace();
						return "";
					}
				}

				@Override
				protected void onPostExecute(String s) {
					if (!s.isEmpty()){
						Device.Api.RegisterDevice registerAttempt = new Device.Api.RegisterDevice() {
							@Override
							protected void onResultReturned(Device result) {
								SharedPreferences.Editor editor = prefs.edit();
								editor.putString(PREF_MESSAGING_ID, result.DeviceToken);
								editor.commit();
							}

							@Override
							protected void onErrorResponse(int failureCode) {}
						};
						registerAttempt.execute(new Device(s));
					}
				}
			};
			register.execute();
		}
	}
}
