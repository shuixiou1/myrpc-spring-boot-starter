package com.wj.client.discovery;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.I0Itec.zkclient.ZkClient;
import com.alibaba.fastjson.JSON;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyService;
import com.wj.common.serializer.ZookeeperSerializer;

public class ZookeeperServerDiscovery implements ServerDiscovery{
	
	private ZkClient zkClient;

	public ZookeeperServerDiscovery(String zkAddress) {
		zkClient = new ZkClient(zkAddress);
		zkClient.setZkSerializer(new ZookeeperSerializer());
	}

	@Override
	public List<MyService> findServiceList(String name) {
		String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + name + "/service";
		List<String> children = zkClient.getChildren(servicePath);
		return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(str -> {
			String deCh = null;
			try {
				deCh = URLDecoder.decode(str, RpcConstant.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return JSON.parseObject(deCh, MyService.class);
		}).collect(Collectors.toList());
	}

	public ZkClient getZkClient() {
		return zkClient;
	}

}
