package com.wj.server;

import java.lang.reflect.Method;

import com.wj.common.constants.ResponseStatus;
import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;
import com.wj.common.protocol.MessageProtocol;
import com.wj.server.register.ServerRegister;
import com.wj.server.register.ServiceObject;

public class RequestHandler {
	
	private MessageProtocol protocol;

	private ServerRegister serverRegister;
	
	private boolean is_debug;

	public RequestHandler(MessageProtocol protocol, ServerRegister serverRegister) {
		this.protocol = protocol;
		this.serverRegister = serverRegister;
	}
	
	/**
	 * 构造函数只传一个参数，是测试模式
	 */
	public RequestHandler(MessageProtocol protocol) {
		super();
		this.protocol = protocol;
		this.is_debug = true;
	}

	public byte[] handleRequest(byte[] reqData) throws Exception {
		if (is_debug) { // 测试用途
			MyRequest req = this.protocol.unmarshallingRequest(reqData);
			MyResponse response = new MyResponse(ResponseStatus.SUCCESS);
			response.setRequestId(req.getRequestId());
			response.setReturnValue("服务名:" + req.getServiceName() + ",返回结果");
			return this.protocol.marshallingResponse(response);
		}
		// 消息解码
		MyRequest req = this.protocol.unmarshallingRequest(reqData);
		// 服务注册时本机缓存了obj，class信息包装成了ServiceObject对象。
		ServiceObject so = serverRegister.getServiceObject(req.getServiceName());
		MyResponse response = null;
		if (so == null) {
			response = new MyResponse(ResponseStatus.NOT_FOUND);
		} else {
			try {
				// 反射方法调用
				Method method = so.getClazz().getMethod(req.getMethod(), req.getParameterTypes());
				Object returnValue = method.invoke(so.getObj(), req.getParameters());
				response = new MyResponse(ResponseStatus.SUCCESS);
				response.setReturnValue(returnValue);
			} catch (Exception e) {
				response = new MyResponse(ResponseStatus.ERROR);
				response.setException(e);
			}
		}
		response.setRequestId(req.getRequestId());
		return this.protocol.marshallingResponse(response);
	}

}
