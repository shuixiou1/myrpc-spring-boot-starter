package com.wj.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyRequest implements Serializable{

	private static final long serialVersionUID = 1029003086377123585L;
	
	// 请求id
	private String requestId;
  
	// 服务名称
    private String serviceName;

    // 请求方法名
    private String method;

    // 请求头-附加信息用途
    private Map<String,String> headers = new HashMap<>();
    
    // 请求参数类型
    private Class<?>[] parameterTypes;
    
    // 请求参数
    private Object[] parameters;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

}
