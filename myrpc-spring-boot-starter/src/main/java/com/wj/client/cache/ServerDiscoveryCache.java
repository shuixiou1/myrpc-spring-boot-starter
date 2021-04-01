package com.wj.client.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.wj.common.model.MyService;

/**
 * 服务发现本地缓存
 */
public class ServerDiscoveryCache {
 
    private static final Map<String, List<MyService>> SERVER_MAP = new ConcurrentHashMap<>();
  
    public static final List<String> SERVICE_CLASS_NAMES = new ArrayList<>();

    public static void put(String serviceName, List<MyService> serviceList) {
        SERVER_MAP.put(serviceName, serviceList);
    }

    /**
     * 移除指定的缓存
     */
    public static void remove(String serviceName, MyService service) {
        SERVER_MAP.computeIfPresent(serviceName, (key, value) ->
                value.stream().filter(o -> !o.toString().equals(service.toString())).collect(Collectors.toList())
        );
    }

    /**
     * 移除所有的缓存
     */
    public static void removeAll(String serviceName) {
        SERVER_MAP.remove(serviceName);
    }

    /**
     * 判断缓存是否为空
     */
    public static boolean isEmpty(String serviceName) {
        return SERVER_MAP.get(serviceName) == null || SERVER_MAP.get(serviceName).size() == 0;
    }

    /**
     * 从缓存取出服务列表
     */
    public static List<MyService> get(String serviceName) {
        return SERVER_MAP.get(serviceName);
    }
    
}
