package com.wj.common.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.wj.annotation.MessageProtocolAno;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;

/**
 * java编码解码实现
 */
@MessageProtocolAno(RpcConstant.PROTOCOL_JAVA)
public class JavaMessageProtocol implements MessageProtocol {
	
	public byte[] marshallingRequest(MyRequest request) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(request);
		return bout.toByteArray();
	}

	public MyRequest unmarshallingRequest(byte[] data) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bin);
		MyRequest myRequset = (MyRequest) in.readObject();
		return myRequset;
	}

	@Override
	public byte[] marshallingResponse(MyResponse response) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(response);
		return bout.toByteArray();
	}

	@Override
	public MyResponse unmarshallingResponse(byte[] data) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bin);
		MyResponse myResponse = (MyResponse) in.readObject();
		return myResponse;
	}

}
