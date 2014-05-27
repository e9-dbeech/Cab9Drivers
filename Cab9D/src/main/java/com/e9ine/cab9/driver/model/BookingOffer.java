package com.e9ine.cab9.driver.model;

import com.e9ine.cab9.driver.api.AsyncGetSingle;

import org.json.JSONObject;

/**
 * Created by David on 12/02/14 for com.e9ine.cab9.driver.model.
 */
public class BookingOffer extends ServerModel {

	@Override
	public JSONObject toJSON() {
		return null;
	}

	public static class Api {
		public static final String MODEL_PATH = API_PATH + "/BookingOffer";
		public static final String ACK_PATH = MODEL_PATH + "/Acknowledge";
		public static final String ACCEPT_PATH = MODEL_PATH + "/AcceptBookingOffer";
		public static final String REJECT_PATH = MODEL_PATH + "/RejectBookingOffer";

		public static abstract class MarkAsRead extends AsyncGetSingle<Void> {
			@Override
			protected String getApiPath() {
				return ACK_PATH;
			}

			@Override
			protected Void fromJSON(JSONObject json) {
				return (null);
			}
		}

		public static abstract class AcceptBookingOffer extends  AsyncGetSingle<Booking> {
			@Override
			protected Booking fromJSON(JSONObject json) {
				return new Booking(json);
			}

			@Override
			protected String getApiPath() {
				return ACCEPT_PATH;
			}
		}

		public static abstract class RejectBookingOffer extends  AsyncGetSingle<Void> {
			@Override
			protected Void fromJSON(JSONObject json) {
				return (null);
			}

			@Override
			protected String getApiPath() {
				return REJECT_PATH;
			}
		}
	}
}
