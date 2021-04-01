package com.wj.exception;

/**
 * 远程调用异常
 */
public class RpcException extends RuntimeException {

	private static final long serialVersionUID = -8612796174033183652L;

	public RpcException(String message) {
        super(message);
    }
}
