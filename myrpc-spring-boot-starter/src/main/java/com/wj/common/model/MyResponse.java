package com.wj.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.wj.common.constants.ResponseStatus;

public class MyResponse implements Serializable {
	
	public MyResponse(ResponseStatus responseStatus) {
		super();
		this.responseStatus = responseStatus;
	}

	private static final long serialVersionUID = -2161906425420550577L;

	// 请求id
	private String requestId;

	// 请求头附加信息
    private Map<String, String> headers = new HashMap<>();

    // 返回值对象
    private Object returnValue; 

    // 异常对象 
    private Exception exception; 
    
    // 响应状态
    private ResponseStatus responseStatus;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	@Override
	public String toString() {
		return "MyResponse [requestId=" + requestId + ", headers=" + headers + ", returnValue=" + returnValue + ", exception=" + exception
				+ ", responseStatus=" + responseStatus + "]";
	}
    
	
}
