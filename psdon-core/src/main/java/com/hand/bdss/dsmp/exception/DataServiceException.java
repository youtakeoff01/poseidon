package com.hand.bdss.dsmp.exception;

public class DataServiceException extends Exception {

	private static final long serialVersionUID = -8140257490406419220L;

	public DataServiceException(Throwable exception) {
		super(exception);
	}

	public DataServiceException(String exception) {
		super(exception);
	}

	public DataServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
