package com.wj.common.protocol;

import com.wj.annotation.MessageProtocolAno;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;
import com.wj.common.serializer.SerializingUtil;

/**
 * protoBuf编码解码
 */
@MessageProtocolAno(RpcConstant.PROTOCOL_PROTOBUF)
public class ProtoBufProtocol implements MessageProtocol {

	@Override
	public byte[] marshallingRequest(MyRequest request) throws Exception {
		byte[] res = SerializingUtil.serialize(request);
		return res;
	}

	@Override
	public MyRequest unmarshallingRequest(byte[] data) throws Exception {
		MyRequest myRequest = SerializingUtil.deserialize(data, MyRequest.class);
		return myRequest;
	}

	@Override
	public byte[] marshallingResponse(MyResponse response) throws Exception {
		byte[] res = SerializingUtil.serialize(response);
		return res;
	}

	@Override
	public MyResponse unmarshallingResponse(byte[] data) throws Exception {
		MyResponse response = SerializingUtil.deserialize(data, MyResponse.class);
		return response;
	}

}
