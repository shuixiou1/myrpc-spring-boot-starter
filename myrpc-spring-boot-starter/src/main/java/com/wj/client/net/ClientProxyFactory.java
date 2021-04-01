package com.wj.client.net;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.wj.client.balance.LoadBalance;
import com.wj.client.cache.ServerDiscoveryCache;
import com.wj.client.discovery.ServerDiscovery;
import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;
import com.wj.common.model.MyService;
import com.wj.common.protocol.MessageProtocol;
import com.wj.exception.RpcException;

/**
 * 客户端代理工厂
 */
public class ClientProxyFactory {

    private ServerDiscovery serverDiscovery;

    private NetClient netClient;

    private Map<String, MessageProtocol> supportMessageProtocols;

    private Map<Class<?>, Object> objectCache = new HashMap<>();

    private LoadBalance loadBalance;
    
    private MessageProtocol messageProtocol;

    /**
     * 通过Java动态代理获取服务代理类
     */
    @SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> clazz) {
        return (T) objectCache.computeIfAbsent(clazz, clz ->
        	Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new ClientInvocationHandler(clz))
        );
    }

    private class ClientInvocationHandler implements InvocationHandler {
        private Class<?> clazz;
        
        public ClientInvocationHandler(Class<?> clazz) {
            this.clazz = clazz;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                return proxy.toString();
            }
            
            if (method.getName().equals("hashCode")) {
                return 0;
            }
            
            // 1.获得服务信息
            String serviceName = clazz.getName();
            List<MyService> services = getServiceList(serviceName);
            MyService service = loadBalance.chooseOne(services);
            
            // 2.构造request对象
            MyRequest request = new MyRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setServiceName(service.getName());
            request.setMethod(method.getName());
            request.setParameters(args);
            request.setParameterTypes(method.getParameterTypes());
            
            // 3.netty客户端发送请求
            MessageProtocol messageProtocol = getMessageProtocol();
            MyResponse response = netClient.sendRequest(request, service, messageProtocol);
			if (response == null) {
				throw new RpcException("the response is null");
			}
            // 4.结果处理
			if (response.getException() != null) {
				return response.getException();
			}
            return response.getReturnValue();
        }
    }

    /**
     * 根据服务名获取可用的服务地址列表
     */
    private List<MyService> getServiceList(String serviceName) {
        List<MyService> services;
		synchronized (serviceName) {
			if (ServerDiscoveryCache.isEmpty(serviceName)) {
				services = serverDiscovery.findServiceList(serviceName);
				if (services == null || services.size() == 0) {
					throw new RpcException("No provider available!");
				}
				ServerDiscoveryCache.put(serviceName, services);
			} else {
				services = ServerDiscoveryCache.get(serviceName);
			}
		}
        return services;
    }

    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public ServerDiscovery getServerDiscovery() {
        return serverDiscovery;
    }

    public void setServerDiscovery(ServerDiscovery serverDiscovery) {
        this.serverDiscovery = serverDiscovery;
    }

    public NetClient getNetClient() {
        return netClient;
    }

    public void setNetClient(NetClient netClient) {
        this.netClient = netClient;
    }

    public Map<String, MessageProtocol> getSupportMessageProtocols() {
        return supportMessageProtocols;
    }

    public void setSupportMessageProtocols(Map<String, MessageProtocol> supportMessageProtocols) {
        this.supportMessageProtocols = supportMessageProtocols;
    }

    public Map<Class<?>, Object> getObjectCache() {
        return objectCache;
    }

    public void setObjectCache(Map<Class<?>, Object> objectCache) {
        this.objectCache = objectCache;
    }

	public MessageProtocol getMessageProtocol() {
		return messageProtocol;
	}

	public void setMessageProtocol(MessageProtocol messageProtocol) {
		this.messageProtocol = messageProtocol;
	}
    
}
