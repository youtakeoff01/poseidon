package com.hand.bdss.web.common.vo;

import com.hand.bdss.web.common.util.JsonUtils;

public class BaseResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String returnCode;// code 为1 或者 0 ，1表示用户操作成功，0表示用户操作失败

	private String returnMessage;

	private Object returnObject;

	public BaseResponse() {
	}

	public BaseResponse(String returnCode) {
		this.returnCode = returnCode;
	}

	public BaseResponse(String returnCode, Object returnObject) {
		this.returnCode = returnCode;
		this.returnObject = returnObject;
	}

	public BaseResponse(String returnCode, String returnMessage, Object returnObject) {
		this.returnCode = returnCode;
		this.returnMessage = returnMessage;
		this.returnObject = returnObject;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public Object getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}
