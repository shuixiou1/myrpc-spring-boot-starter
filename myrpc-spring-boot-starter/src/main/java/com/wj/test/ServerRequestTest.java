package com.wj.test;

import java.util.UUID;

import org.junit.Test;

import com.wj.client.net.MyNettyClient;
import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;
import com.wj.common.model.MyService;
import com.wj.common.protocol.ProtoBufProtocol;
import com.wj.server.MyRpcServer;
import com.wj.server.RequestHandler;

/**
 * netty服务和客户端测试
 */
public class ServerRequestTest {

	/**
	 * 服务端
	 */
	public static void main(String[] args) {
		RequestHandler handler = new RequestHandler(new ProtoBufProtocol());
		MyRpcServer server = new MyRpcServer(7000, handler);
		server.start();
	}
	
	/**
	 * 客户端
	 */
	@Test
	public void test1() {
		MyNettyClient client = new MyNettyClient();
		MyRequest request = new MyRequest();
		request.setRequestId(UUID.randomUUID().toString());
		request.setMethod("request method");
		request.setParameters(new Object[]{});
		request.setParameterTypes(new Class[]{});
		request.setServiceName("test_servername");
		
		MyService service = new MyService();
		
		service.setAddress("127.0.0.1:7000");
		MyResponse response = client.sendRequest(request, service, new ProtoBufProtocol());
		System.out.println(response);
	}
	
}
