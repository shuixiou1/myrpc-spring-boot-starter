package com.wj.common.constants;

/**
 * 常量配置
 */
public class RpcConstant {

    private RpcConstant(){}

    /**
     * rpc路径
     */
    public static final String ZK_SERVICE_PATH = "/rpc";
    
    /***
     * 编码
     */
    public static final String UTF_8 = "UTF-8";
    
    /**
     * 路径分隔符
     */
    public static final String PATH_DELIMITER = "/";
    
    /**
     * java序列化协议
     */
    public static final String PROTOCOL_JAVA = "java";
    
    /**
     * protobuf序列化协议
     */
    public static final String PROTOCOL_PROTOBUF = "protobuf";
    
    /**
     * 随机
     */
    public static final String BALANCE_RANDOM = "random";
    
    /**
     * 轮询
     */
    public static final String BALANCE_SEQUECE = "sequece";
    
    /**
     * 加权轮询
     */
    public static final String BALANCE_WEIGHT = "weight";
    
    /**
     * 平滑加权轮询
     */
    public static final String BALANCE_SMOOTH_WEIGHT = "smoothWeight";

}
