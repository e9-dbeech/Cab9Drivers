package com.e9ine.cab9.driver.model;

import com.e9ine.cab9.driver.api.AsyncGet;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by David on 07/03/14 for com.e9ine.cab9.driver.model.
 */
public class Payment extends ServerModel {
	public Long ID;
	public String Reference;
	public String Type;
	public Date Date;
	public Double Amount;

	public Payment(JSONObject json, String type) {
		ID = json.optLong("ID", -1);
		Type = type;
		Reference = json.optString("Reference", "");
		Date = optDate(json, "DueDate", null);
		Amount = json.optDouble("TotalDue", 0.00);
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	public static class Api {
		public static final String BILL_MODEL_PATH = API_PATH + "/Bill";
		public static final String INV_MODEL_PATH = API_PATH + "/Invoice";

		public abstract static class GetBillsTask extends AsyncGet<Payment> {
			@Override
			protected String getApiPath() {
				return BILL_MODEL_PATH;
			}

			@Override
			protected Payment fromJSON(JSONObject json) {
				return new Payment(json, "Bill");
			}
		}

		public abstract static class GetInvoicesTask extends AsyncGet<Payment> {
			@Override
			protected String getApiPath() {
				return INV_MODEL_PATH;
			}

			@Override
			protected Payment fromJSON(JSONObject json) {
				return new Payment(json, "Invoice");
			}
		}
	}
}
