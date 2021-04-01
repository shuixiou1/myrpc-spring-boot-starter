package com.wj.client.net;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyFuture<T> implements Future<T> {

	private T response;

	private CountDownLatch countDownLatch = new CountDownLatch(1);

	/**
	 * 设置response时，打开闭锁
	 */
	public void setResponse(T t) {
		this.response = t;
		countDownLatch.countDown();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		if (response != null)
			return true;
		return false;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		countDownLatch.await();
		return response;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (countDownLatch.await(timeout, unit)) {
			return response;
		}
		return null;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		MyFuture<String> future = new MyFuture<>();
		// 执行异步操作
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			future.setResponse("response result");
		}).start();
		// 阻塞等待异步操作的结果
		String result = future.get();
		System.out.println(result);
	}

}
