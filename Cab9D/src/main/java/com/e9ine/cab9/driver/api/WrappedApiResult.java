package com.e9ine.cab9.driver.api;


/**
 * Created by David on 04/01/14 for com.e9ine.cab9.drivers.helpers.
 */
public class WrappedApiResult<T> {
	public final static int SUCCESS_CODE = 0;
	public final static int PARAMETER_ERROR_CODE = 100;
	public final static int UNAUTHORIZED_CODE = 200;
	public final static int SERVER_ERROR_CODE = 300;
	public final static int NOT_FOUND_CODE = 400;
	public final static int BAD_REQUEST_CODE = 500;
	public final static int NO_CONNECTION_CODE = 600;
	public final static int OTHER_ERROR_CODE = 999;

	public boolean Success;
	public int FailureCode;
	public T Result;

	public WrappedApiResult(int reason) {
		Success = false;
		FailureCode = reason;
		Result = null;
	}

	public WrappedApiResult(T result) {
		Success = true;
		FailureCode = SUCCESS_CODE;
		Result = result;
	}
}

