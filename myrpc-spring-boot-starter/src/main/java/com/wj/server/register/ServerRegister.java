package com.wj.server.register;

/**
 * 服务注册
 */
public interface ServerRegister {
	
	void register(ServiceObject so) throws Exception;

	ServiceObject getServiceObject(String name) throws Exception;
	
}
