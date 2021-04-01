package com.wj.client.discovery;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wj.client.cache.ServerDiscoveryCache;

/**
 * 子节点事件监听处理类
 */
public class ZkChildListenerImpl implements IZkChildListener {

    /**
     * 监听子节点的删除和新增事件
     */
    @Override
    public void handleChildChange(String parentPath, List<String> childList) throws Exception {
        String[] arr = parentPath.split("/");
        ServerDiscoveryCache.removeAll(arr[2]);
    }
}
