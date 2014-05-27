package com.e9ine.cab9.driver.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.api.WrappedApiResult;
import com.e9ine.cab9.driver.model.Credentials;
import com.e9ine.cab9.driver.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by David on 05/01/14 for com.e9ine.cab9.driver.activities.
 */
public class LoginActivity extends Activity {
	final int PASSWORD_MIN_LENGTH = 4;
	final int USERNAME_MIN_LENGTH = 4;

	EditText email;
	EditText password;
	Button login;

	RelativeLayout spinner_overlay;
	ProgressBar spinner;
	TextView spinner_status;

	User.Api.GetCurrentUserTask mCurrentAttempt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupInputs();
		if (checkPlayServices()) {
			checkForPreviousCredentials();
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		checkPlayServices();
	}

	private void checkForPreviousCredentials() {
		Credentials cred = Credentials.getStoredCredentials();
		if (cred != null) {
			if (!TextUtils.isEmpty(cred.Password)) {
				toggleSpinnerWithStatus(true, getString(R.string.login_spinner_logging));
				mCurrentAttempt = new User.Api.GetCurrentUserTask() {
					@Override
					protected void onPreExecute() {
					}

					@Override
					protected void onResultReturned(User result) {
						handleLoginSuccess(result);
					}

					@Override
					protected void onErrorResponse(int failureCode) {
						handleLoginFailure(failureCode);
					}
				};
				mCurrentAttempt.execute();
			}
			if (!TextUtils.isEmpty(cred.Username)) {
				email.setText(cred.Username);
				password.requestFocus();
			}
		}
	}

	private void setupInputs() {
		TextView title = (TextView) findViewById(R.id.login_text_title);
		TextView footer = (TextView) findViewById(R.id.login_text_feedback);
		email = (EditText) findViewById(R.id.login_text_email);
		password = (EditText) findViewById(R.id.login_text_password);
		login = (Button) findViewById(R.id.login_button_login);
		spinner_overlay = (RelativeLayout) findViewById(R.id.global_spinner_container);
		spinner = (ProgressBar) findViewById(R.id.global_spinner);
		spinner_status = (TextView) findViewById(R.id.global_spinner_message);

		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/helveticaneue.ttf");
		title.setTypeface(font);
		footer.setTypeface(font);
		email.setTypeface(font);
		password.setTypeface(font);
		login.setTypeface(font);

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleLoginClick();
			}
		});
	}

	private void handleLoginSuccess(User result) {
		Log.i("LOGIN", "UserLoggedIn: " + result.Name);
		ApplicationClass.setCurrentUser(result, true);
		Intent gotoMainActivity = new Intent(this, MainActivity.class);
		gotoMainActivity.putExtra(MainActivity.FRAGMENT_EXTRA, MainActivity.DASHBOARD_FRAGMENT);
		startActivity(gotoMainActivity);
		finish();
	}

	private void handleLoginFailure(int failureCode) {
		switch (failureCode) {
			case WrappedApiResult.UNAUTHORIZED_CODE:
				password.setError(surroundError(R.string.login_error_invalid_credentials));
				break;
			case WrappedApiResult.SERVER_ERROR_CODE:
				password.setError(surroundError(R.string.login_error_server_unavailable));
				break;
			default:
				password.setError(surroundError(R.string.login_error_other));
				break;
		}
		toggleSpinnerWithStatus(false, "");
		password.requestFocus();
		mCurrentAttempt = null;
	}

	private void handleLoginClick() {
		if (mCurrentAttempt == null) {
			toggleSpinnerWithStatus(true, getString(R.string.login_spinner_logging));

			String _username = email.getText().toString();
			String _password = password.getText().toString();

			boolean cancel = false;
			View focusView = null;

			if (TextUtils.isEmpty(_password)) {
				password.setError(surroundError(R.string.login_error_missing_password));
				focusView = password;
				cancel = true;
			}  else if (_password.length() <= PASSWORD_MIN_LENGTH) {
				password.setError(surroundError(R.string.login_error_short_password));
				focusView = password;
				cancel = true;
			}

			if (TextUtils.isEmpty(_username)) {
				email.setError(surroundError(R.string.login_error_missing_email));
				focusView = email;
				cancel = true;
			} else if (_username.length() <= USERNAME_MIN_LENGTH) {
				email.setError(surroundError(R.string.login_error_short_email));
				focusView = email;
				cancel = true;
			} else if (!_username.contains("@")) {
				email.setError(surroundError(R.string.login_error_format_email));
				focusView = email;
				cancel = true;
			}

			if (cancel) {
				toggleSpinnerWithStatus(false, "");
				focusView.requestFocus();
			} else {
				Credentials.putStoredCredentials(new Credentials(_username, _password));
				Credentials.setCurrent(new Credentials(_username, _password));
				mCurrentAttempt = new User.Api.GetCurrentUserTask() {
					@Override
					protected void onPreExecute() {
					}

					@Override
					protected void onResultReturned(User result) {
						handleLoginSuccess(result);
					}

					@Override
					protected void onErrorResponse(int failureCode) {
						handleLoginFailure(failureCode);
					}
				};
				mCurrentAttempt.execute();
			}
		}
	}

	private void toggleSpinnerWithStatus(Boolean showSpinner, String status) {
		if (showSpinner) {
			View focus = getCurrentFocus();
			if (focus != null)
				focus.clearFocus();
			spinner_overlay.setVisibility(View.VISIBLE);
			spinner_status.setText(status);
		} else {
			spinner_overlay.setVisibility(View.INVISIBLE);
			spinner_status.setText("");
		}
	}

	private CharSequence surroundError(int resID) {
		return Html.fromHtml("<font color='red'>" + getString(resID) + "</font>");
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
			} else {
				finish();
			}
			return false;
		}
		return true;
	}
}
