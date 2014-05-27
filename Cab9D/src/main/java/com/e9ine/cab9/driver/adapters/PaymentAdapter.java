package com.e9ine.cab9.driver.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.fragments.BaseFragment;
import com.e9ine.cab9.driver.model.Payment;
import com.e9ine.cab9.driver.model.ServerModel;
import com.e9ine.cab9.driver.model.Shift;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by David on 04/02/14 for com.e9ine.cab9.driver.adapters.
 */
public class PaymentAdapter extends ArrayAdapter<Payment>{

	private Context mContext;
	private ArrayList<Payment> mPayments;

	public PaymentAdapter(Context context, ArrayList<Payment> payments) {
		super(context, R.layout.include_account_list_item, payments);
		mContext = context;
		mPayments = payments;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).ID;
	}

	@Override
	public Payment getItem(int position) {
		return mPayments.get(getCount() - 1 - position);
		//To reverse
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Payment current = getItem(position);
		View target;
		if (convertView != null) {
			target = convertView;
		} else {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			target = inflater.inflate(R.layout.include_account_list_item, parent, false);
		}

		target.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (current.Type.equals("Invoice")) {
					handleInvoiceRowClick(current.ID);
				} else {
					handleBillRowClick(current.ID);
				}
			}
		});

		TextView title = ((TextView) target.findViewById(R.id.acc_date));
		title.setText(BaseFragment.shortDateFormat.format(current.Date));

		TextView subtitle = ((TextView) target.findViewById(R.id.acc_id));
		subtitle.setText(current.Reference);

		TextView total = ((TextView) target.findViewById(R.id.acc_amount));
		total.setText("£" + String.format("%.2f", current.Amount));

		return target;
	}

	private void handleInvoiceRowClick(long id) {
		downloadAndOpenPDF(ServerModel.API_PATH + "/Document/GetInvoicePdf?invoiceid=" + id, "Invoice-" + id + ".pdf");
	}

	private void handleBillRowClick(long id) {
		downloadAndOpenPDF(ServerModel.API_PATH + "/Document/GetBillPdf?billid=" + id, "Bill-" + id + ".pdf");
	}

	void downloadAndOpenPDF(final String download_file_url, final String dest_file_path) {
		new Thread(new Runnable() {
			public void run() {
				Uri path = Uri.fromFile(downloadFile(download_file_url, dest_file_path));
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(path, "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mContext.startActivity(intent);
			}
		}).start();
	}

	File downloadFile(String dwnload_file_path, String dest_file_path) {
		File file = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet pageGet = new HttpGet(dwnload_file_path);
			HttpResponse response = httpClient.execute(pageGet);

			long fileSize = response.getEntity().getContentLength();
			InputStream stream = response.getEntity().getContent();
			file = new File(Environment.getExternalStorageDirectory() + "/" + dest_file_path);
			FileOutputStream fileOutput = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = stream.read(bytes)) != -1) {
				fileOutput.write(bytes, 0, read);
			}
			fileOutput.close();
			stream.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return file;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
