package com.wj.client.balance;


import java.util.List;

import com.wj.common.model.MyService;


/**
 * 负载均衡算法接口
 */
public interface LoadBalance {
  
    /**
     * 负载均衡接口实现
     */
    MyService chooseOne(List<MyService> services);
    
}
