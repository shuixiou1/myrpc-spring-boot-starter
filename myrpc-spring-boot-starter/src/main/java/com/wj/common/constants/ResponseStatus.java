package com.wj.common.constants;

/**
 * 响应状态
 */
public enum ResponseStatus {
	
    SUCCESS(200, "SUCCESS"),
    
    ERROR(500, "ERROR"),

    NOT_FOUND(404, "NOT FOUND");

    private int code;

    private String desc;

    ResponseStatus(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    
}
