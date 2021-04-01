package com.wj.client.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wj.client.net.handler.SendHandlerV2;
import com.wj.codec.LengthDecode;
import com.wj.codec.LengthEncode;
import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;
import com.wj.common.model.MyService;
import com.wj.common.protocol.MessageProtocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * netty请求客户端
 */
public class MyNettyClient implements NetClient{
	
    private EventLoopGroup loopGroup = new NioEventLoopGroup(4);
    
    /**
     *  1个地址绑定一个处理器，用于性能优化用途。
     */
    public static Map<String, SendHandlerV2> connectedServerNodes = new ConcurrentHashMap<>();

	public ChannelFuture connect(String adress, Integer port, SendHandlerV2 handlerV2) {
		Bootstrap b = new Bootstrap();
		b.group(loopGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel sh) throws Exception {
						sh.pipeline().addLast(new LengthDecode()); // 长度解码 - 按长度读取，并且去除长度字段。
						sh.pipeline().addLast(new LengthEncode()); // 长度编码 - 多写长度字段。
						sh.pipeline().addLast(handlerV2);
					}
				});
		ChannelFuture channelFuture = b.connect(adress, port);
		return channelFuture;
	}
	
	@Override
	public MyResponse sendRequest(MyRequest request, MyService service, MessageProtocol messageProtocol) {
		String address = service.getAddress();
		synchronized (address) {
			if (connectedServerNodes.containsKey(address)) {
				SendHandlerV2 handler = connectedServerNodes.get(address);
				return handler.sendRequest(request);
			}
			String[] addrInfo = address.split(":");
			final String serverAddress = addrInfo[0];
			final int serverPort = Integer.parseInt(addrInfo[1]);
			final SendHandlerV2 handler = new SendHandlerV2(messageProtocol, address);
			// 连接
			ChannelFuture future = connect(serverAddress, serverPort, handler);

			// 添加监听 --- 连接成功后设置到缓存中
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) throws Exception {
					connectedServerNodes.put(address, handler);
				}
			});
			return handler.sendRequest(request);
		}
	}

}
