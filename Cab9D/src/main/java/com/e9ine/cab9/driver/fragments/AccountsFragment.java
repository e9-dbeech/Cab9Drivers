package com.e9ine.cab9.driver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.adapters.PaymentAdapter;
import com.e9ine.cab9.driver.adapters.ShiftAdapter;
import com.e9ine.cab9.driver.model.Payment;
import com.e9ine.cab9.driver.model.Shift;

import java.util.ArrayList;

/**
 * Created by David on 06/01/14 for com.e9ine.cab9.driver.fragments.
 */
public class AccountsFragment extends Fragment {
	private ArrayList<Payment> receivables;
	private ArrayList<Payment> payable;

	private PaymentAdapter recAdapter;
	private PaymentAdapter payAdapter;

	ListView recList;
	ListView payList;

	ArrayList<Payment> recs = new ArrayList<Payment>();
	ArrayList<Payment> pays = new ArrayList<Payment>();

	private Payment.Api.GetBillsTask BillsTask;
	private Payment.Api.GetInvoicesTask InvoicesTask;

	public static AccountsFragment newInstance(){
		return new AccountsFragment();
	}
	private AccountsFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_accounts_split, container, false);

		recList = (ListView) rootView.findViewById(R.id.acc_rec);
		payList = (ListView) rootView.findViewById(R.id.acc_pay);

		recAdapter = new PaymentAdapter(getActivity(), recs);
		payAdapter = new PaymentAdapter(getActivity(), pays);

		recList.setAdapter(recAdapter);
		payList.setAdapter(payAdapter);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		fetchPayments();
	}

	private void fetchPayments() {
		BillsTask = new Payment.Api.GetBillsTask() {
			@Override
			protected void onPreExecute() {
				mQueryValues.put("driverid", String.valueOf(ApplicationClass.getCurrentDriver().ID));
			}

			@Override
			protected void onResultReturned(ArrayList<Payment> result) {
				handleBillsReturned(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {

			}
		};
		BillsTask.execute();

		InvoicesTask = new Payment.Api.GetInvoicesTask() {
			@Override
			protected void onPreExecute() {
				mQueryValues.put("driverid", String.valueOf(ApplicationClass.getCurrentDriver().ID));
			}

			@Override
			protected void onResultReturned(ArrayList<Payment> result) {
				handleInvoicesReturned(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {

			}
		};

		InvoicesTask.execute();
	}

	private void handleBillsReturned(ArrayList<Payment> result) {
		recs.clear();
		recs.addAll(result);
		if (recAdapter != null) {
			recAdapter.notifyDataSetChanged();
		}
	}

	private void handleInvoicesReturned(ArrayList<Payment> result) {
		pays.clear();
		pays.addAll(result);
		if (payAdapter != null) {
			payAdapter.notifyDataSetChanged();
		}
	}
}
