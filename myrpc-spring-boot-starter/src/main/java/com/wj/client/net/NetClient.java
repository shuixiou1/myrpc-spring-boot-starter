package com.wj.client.net;

import com.wj.common.model.MyRequest;
import com.wj.common.model.MyResponse;
import com.wj.common.model.MyService;
import com.wj.common.protocol.MessageProtocol;

/**
 * 客户端
 */
public interface NetClient {

    MyResponse sendRequest(MyRequest rpcRequest, MyService service, MessageProtocol messageProtocol);
    
}
