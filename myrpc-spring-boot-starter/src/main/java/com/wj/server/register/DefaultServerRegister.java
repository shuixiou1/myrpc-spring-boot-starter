package com.wj.server.register;

import java.util.HashMap;
import java.util.Map;


/**
 * 服务注册默认实现
 */
public abstract class DefaultServerRegister implements ServerRegister{
	
	protected int port;
	protected int weight;
	
    private Map<String,ServiceObject> serviceMap = new HashMap<>();

    // 本地服务注册
	@Override
	public void register(ServiceObject so) throws Exception {
		if (so == null){
            throw new IllegalArgumentException("parameter cannot be empty");
        }
        serviceMap.put(so.getName(),so);
	}

	// 得到注册的本地服务
	@Override
	public ServiceObject getServiceObject(String name) throws Exception {
		return serviceMap.get(name);
	}

}
