package com.wj.server.register;


import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import org.I0Itec.zkclient.ZkClient;
import com.alibaba.fastjson.JSON;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyService;
import com.wj.common.serializer.ZookeeperSerializer;

public class ZookeeperServerRegister extends DefaultServerRegister {
	
	private ZkClient zkClient;

	public ZookeeperServerRegister(String zkAddress, Integer port, String protocol, Integer weight) {
		zkClient = new ZkClient(zkAddress);
		zkClient.setZkSerializer(new ZookeeperSerializer());
		this.port = port;
		this.weight = weight;
	}
	
	public void register(ServiceObject so) throws Exception {
		// 注册到本地
		super.register(so);
		MyService service = new MyService();
		String host = InetAddress.getLocalHost().getHostAddress();
		String address = host + ":" + port;
		service.setAddress(address);
		service.setName(so.getClazz().getName());
		service.setWeight(this.weight);
		this.exportService(service);
	}
	
	/**
	 * 暴露服务到zookeeper中
	 */
	private void exportService(MyService serviceResource) {
		String serviceName = serviceResource.getName();
		String uri = JSON.toJSONString(serviceResource);
		try {
			uri = URLEncoder.encode(uri, RpcConstant.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + serviceName + "/service";
		if (!zkClient.exists(servicePath)) {
			zkClient.createPersistent(servicePath, true);
		}
		String uriPath = servicePath + RpcConstant.PATH_DELIMITER + uri;
		if (zkClient.exists(uriPath)) {
			zkClient.delete(uriPath);
		}
		zkClient.createEphemeral(uriPath);
	}
	
}
