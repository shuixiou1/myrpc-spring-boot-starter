package com.wj.common.serializer;

import java.nio.charset.StandardCharsets;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class ZookeeperSerializer implements ZkSerializer {

	 /**
     * 序列化
     */
    public byte[] serialize(Object o) throws ZkMarshallingError {
        return String.valueOf(o).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 反序列化
     */
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
}
