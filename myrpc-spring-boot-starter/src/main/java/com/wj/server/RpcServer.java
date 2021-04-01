package com.wj.server;

public abstract class RpcServer {

    protected int port;

    protected RequestHandler requestHandler;

	public RpcServer(int port, RequestHandler requestHandler) {
		super();
		this.port = port;
		this.requestHandler = requestHandler;
	}
    
	 /**
     * 开启服务
     */
    public abstract void start();

    /**
     * 关闭服务
     */
    public abstract void stop();

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}
	
}
