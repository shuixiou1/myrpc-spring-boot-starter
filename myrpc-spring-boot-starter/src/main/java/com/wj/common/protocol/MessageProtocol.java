package com.wj.common.protocol;

import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;

/**
 * 消息解码编码
 */
public interface MessageProtocol {
	
    /**
     * 编组请求
     */
    byte[] marshallingRequest(MyRequest request) throws Exception;

    /**
     * 解码请求
     */
    MyRequest unmarshallingRequest(byte[] data) throws Exception;
    
    /**
     * 编码响应
     */
    byte[] marshallingResponse(MyResponse response) throws Exception;

    /**
     * 解码响应
     */
    MyResponse unmarshallingResponse(byte[] data) throws Exception;
}
