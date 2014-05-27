package com.e9ine.cab9.driver.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.model.Driver;

import java.io.FileNotFoundException;

/**
 * Created by David on 06/01/14 for com.e9ine.cab9.driver.fragments.
 */
public class ProfileFragment extends BaseFragment {
	private static final String ARG_DRIVERID = "driverid";

	Integer mDriverId;

	View mRootView;
	TextView mCallSignText;
	TextView mNameText;
	TextView mPhoneText;
	TextView mEmailText;
	TextView mAddressText;
	ImageView mDriverImage;
	Button mChangePicture;

	Driver.Api.GetDriverTask mGetDriverTask;
	Driver.Api.GetDriverImageTask mImageFetch;

	public static ProfileFragment newInstance(int driverid) {
		Bundle args = new Bundle();
		args.putInt(ARG_DRIVERID, driverid);
		return new ProfileFragment(args);
	}

	public ProfileFragment(Bundle args) {
		if (args != null)
			setArguments(args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null){
			mDriverId = getArguments().getInt(ARG_DRIVERID);
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		Driver driver = ApplicationClass.getCurrentDriver();
		if (driver != null) {
			SetDriverFields(driver);
		}

		mGetDriverTask = new Driver.Api.GetDriverTask() {
			@Override
			protected void onPreExecute() {
				if (mDriverId != null) {
					mQueryValues.put(ARG_DRIVERID, mDriverId.toString());
				} else {
					cancel(true);
				}
			}

			@Override
			protected void onResultReturned(Driver result) {
				handleDriverResult(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {
				handleErrorResult(failureCode);
			}
		};
		mGetDriverTask.execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
		mCallSignText = (TextView) mRootView.findViewById(R.id.profile_text_callsign);
		mNameText = (TextView) mRootView.findViewById(R.id.profile_text_name);
		mPhoneText = (TextView) mRootView.findViewById(R.id.profile_text_mobile);
		mEmailText = (TextView) mRootView.findViewById(R.id.profile_text_email);
		mAddressText = (TextView) mRootView.findViewById(R.id.profile_text_address);
		mDriverImage = (ImageView) mRootView.findViewById(R.id.profile_image_driver);
		mChangePicture = (Button) mRootView.findViewById(R.id.profile_change_pic);
		return mRootView;
	}

	private void setDriverImage(Bitmap bitmap) {
		mImageFetch = null;
		mDriverImage.setImageBitmap(bitmap);
	}

	private void handleDriverResult(Driver result) {
		mGetDriverTask = null;
		ApplicationClass.setCurrentDriver(result, true);
		SetDriverFields(result);
	}

	private void SetDriverFields(Driver result) {
		mCallSignText.setText(result.CallSign);
		mNameText.setText(result.Fullname);
		mPhoneText.setText(result.Mobile);
		mEmailText.setText(result.Email);
		String addressString = "";
		if (!result.Address1.isEmpty() && !result.Address1.equalsIgnoreCase("null"))
			addressString += result.Address1 + "\n";
		if (!result.Address2.isEmpty() && !result.Address2.equalsIgnoreCase("null"))
			addressString += result.Address2 + "\n";
		if (!result.Area.isEmpty() && !result.Area.equalsIgnoreCase("null"))
			addressString += result.Area + "\n";
		if (!result.Town.isEmpty() && !result.Town.equalsIgnoreCase("null"))
			addressString += result.Town + "\n";
		if (!result.County.isEmpty() && !result.County.equalsIgnoreCase("null"))
			addressString += result.County + "\n";
		if (!result.Postcode.isEmpty() && !result.Postcode.equalsIgnoreCase("null"))
			addressString += result.Postcode + "\n";
		addressString = addressString.substring(0, addressString.length() - 2);
		mAddressText.setText(addressString);
		if(mImageFetch != null) {
			mImageFetch.cancel(true);
		}
		mImageFetch = new Driver.Api.GetDriverImageTask() {
			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if (bitmap != null)
					setDriverImage(bitmap);
			}
		};
		mImageFetch.execute(result);
		mChangePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleChangePictureClick();
			}
		});
	}

	private void handleErrorResult(int failureCode) {
		mGetDriverTask = null;
	}

	@Override
	public void onPause() {
		super.onPause();
		if(mGetDriverTask != null) {
			mGetDriverTask.cancel(true);
			mGetDriverTask = null;
		}
		if(mImageFetch != null) {
			mImageFetch.cancel(true);
			mImageFetch = null;
		}
	}

	private void handleChangePictureClick(){
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				final String filePath = getPath(selectedImage);
				String file_extn = filePath.substring(filePath.lastIndexOf(".")+1);
				if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
					Driver.Api.PostDriverWithNewImage task = new Driver.Api.PostDriverWithNewImage() {
						@Override
						protected void onPreExecute() {
							this.mDriver = ApplicationClass.getCurrentDriver();
							this.mFilePath = filePath;
						}

						@Override
						protected void onPostExecute(Void aVoid) {
							if(mImageFetch != null) {
								mImageFetch.cancel(true);
							}
							mImageFetch = new Driver.Api.GetDriverImageTask() {
								@Override
								protected void onPostExecute(Bitmap bitmap) {
									if (bitmap != null)
										setDriverImage(bitmap);
								}
							};
							mImageFetch.execute(ApplicationClass.getCurrentDriver());
						}
					};
					task.execute();
				}
				else{
					//NOT IN REQUIRED FORMAT
				}
			}
	}

	public String getPath(Uri uri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
		if(cursor.moveToFirst()){
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}
}
