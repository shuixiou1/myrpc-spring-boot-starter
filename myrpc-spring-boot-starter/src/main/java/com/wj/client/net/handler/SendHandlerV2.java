package com.wj.client.net.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wj.client.net.MyFuture;
import com.wj.client.net.MyNettyClient;
import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;
import com.wj.common.protocol.MessageProtocol;
import com.wj.common.serializer.SerializingUtil;
import com.wj.exception.RpcException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class SendHandlerV2 extends ChannelInboundHandlerAdapter{
	
    private static Logger logger = LoggerFactory.getLogger(SendHandlerV2.class);
	
	private MessageProtocol messageProtocol;
	
	private String adress;
	
	private volatile Channel channel;
	
    static final int CHANNEL_WAIT_TIME = 4;
   
    static final int RESPONSE_WAIT_TIME = 8;
    
	private CountDownLatch latch = new CountDownLatch(1);
	
	private static Map<String, MyFuture<MyResponse>> requestMap = new ConcurrentHashMap<>();

	public SendHandlerV2(MessageProtocol messageProtocol, String adress) {
		super();
		this.messageProtocol = messageProtocol;
		this.adress = adress;
	}

	public MyResponse sendRequest(MyRequest request) {
		MyResponse response;
		MyFuture<MyResponse> future = new MyFuture<>();
		// request和response使用的是同一个requestId
		requestMap.put(request.getRequestId(), future);
		try {
			byte[] data = SerializingUtil.serialize(request);
			ByteBuf reqBuf = Unpooled.buffer(data.length);
			reqBuf.writeBytes(data);
			// 等待channel初始化
			if (latch.await(CHANNEL_WAIT_TIME, TimeUnit.SECONDS)) {
				channel.writeAndFlush(reqBuf);
				// 等待读
				response = future.get(RESPONSE_WAIT_TIME, TimeUnit.SECONDS);
			} else {
				throw new RpcException("wait channel time out");
			}
		} catch (Exception e) {
			throw new RpcException(e.getMessage());
		} finally {
			requestMap.remove(request.getRequestId());
		}
		return response;
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
		latch.countDown();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		 logger.debug("Connect to server successfully:{}", ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		byte[] resp = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(resp);
		ReferenceCountUtil.release(byteBuf);
		MyResponse myResponse = messageProtocol.unmarshallingResponse(resp);
		MyFuture<MyResponse> future = requestMap.get(myResponse.getRequestId());
		future.setResponse(myResponse);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		 ctx.flush();
	}
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.error("channel inactive with remoteAddress:[{}]", adress);
		MyNettyClient.connectedServerNodes.remove(adress);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

}
