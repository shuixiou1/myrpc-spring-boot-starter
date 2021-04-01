package com.wj.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wj.codec.LengthDecode;
import com.wj.codec.LengthEncode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;

public class MyRpcServer extends RpcServer{
	
    private static Logger logger = LoggerFactory.getLogger(MyRpcServer.class);
    
    private Channel channel;
    
    private static final ExecutorService pool = new ThreadPoolExecutor(4, 8,
            200, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder().setNameFormat("rpcServer-%d").build());

	public MyRpcServer(int port, RequestHandler requestHandler) {
		super(port, requestHandler);
	}
	
	private final EventLoopGroup parentGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup childGroup = new NioEventLoopGroup();
    
	@Override
	public void start() {
		try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthDecode()); // 自定义长度字段解码
                    ch.pipeline().addLast(new LengthEncode()); // 自定义长度字段编码
                    ch.pipeline().addLast(new ChannelRequestHandler());
                }
            });
            ChannelFuture future = b.bind(port).sync();
            logger.debug("server start successful.");
            setChannel(future.channel());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("start netty sever failed,msg:{}", e.getMessage());
        } 
	}
	
	private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.debug("Channel active :{}", ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            pool.submit(()->{
                try {
                    logger.debug("the server receives message :{}", msg);
                    ByteBuf byteBuf = (ByteBuf) msg;
                    byte[] reqData = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(reqData);
                    
                    ReferenceCountUtil.release(byteBuf);
                    byte[] respData = requestHandler.handleRequest(reqData);  // 交由requestHandler处理
                    ByteBuf respBuf = Unpooled.buffer(respData.length);
                    respBuf.writeBytes(respData);
                    
                    logger.debug("Send response:{}", respBuf);
                    ctx.writeAndFlush(respBuf);
                } catch (Exception e) {
                    logger.error("server read exception",e);
                }
            });
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            logger.error("Exception occurred:{}", cause.getMessage());
            ctx.close();
        }
    }

	@Override
	public void stop() {
		parentGroup.shutdownGracefully();
		childGroup.shutdownGracefully();
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
