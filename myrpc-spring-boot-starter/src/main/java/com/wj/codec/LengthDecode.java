package com.wj.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 长度解码处理器 : 只负责长度处理， 数据流还是byteBuf
 */
public class LengthDecode extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readableBytes = in.readableBytes();
		if (readableBytes < 4) {
			return;
		}
		in.markReaderIndex();
		int length = in.readInt();
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
		}
		// 读数据
		byte[] data = new byte[length];
		in.readBytes(data);
		
		// 写数据到新buff
		ByteBuf buff = Unpooled.buffer(length);
		buff.writeBytes(data);
		out.add(buff);
	}

}
