package com.wj.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 长度编码: 只负责长度处理， 数据流还是byteBuf
 */
public class LengthEncode extends MessageToByteEncoder<ByteBuf>{

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		out.writeInt(msg.readableBytes());
		out.writeBytes(msg);
	}
	
}
