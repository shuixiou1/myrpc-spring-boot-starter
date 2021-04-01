package com.wj.client.balance;

import java.util.ArrayList;
import java.util.List;

import com.wj.annotation.LoadBalanceAno;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyService;

/**
 * 加权轮询
 */
@LoadBalanceAno(RpcConstant.BALANCE_WEIGHT)
public class WeigthBalance implements LoadBalance{
	
	private volatile static int index;

	@Override
	public synchronized MyService chooseOne(List<MyService> services) {
        int size = services.stream().mapToInt(MyService::getWeight).sum();
        int temp  = index++ % size;  
        int current = 0;
		for (MyService myMyService : services) {
			current += myMyService.getWeight();
			if (temp < current) {
				return myMyService;
			}
		}
        return null;
	}
	
	public static void main(String[] args) {
		List<MyService> services = new ArrayList<>(3);
		MyService service1 = new MyService();
		service1.setAddress("196.128.6.1");
		service1.setWeight(1);
		services.add(service1);

		MyService service2 = new MyService();
		service2.setAddress("196.128.6.2");
		service2.setWeight(3);
		services.add(service2);

		MyService service3 = new MyService();
		service3.setAddress("196.128.6.3");
		service3.setWeight(5);
		services.add(service3);
	
		LoadBalance loadBalance = new WeigthBalance();
		System.out.println("20次请求负载均衡结果为:");
		for (int i = 1; i <= 20; i++) {
			System.out.println("第" + i + "次请求服务ip为：" + loadBalance.chooseOne(services).getAddress());
		}
	}

}
