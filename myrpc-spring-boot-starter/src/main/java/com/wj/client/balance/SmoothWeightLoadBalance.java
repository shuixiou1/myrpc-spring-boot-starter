package com.wj.client.balance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wj.annotation.LoadBalanceAno;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyService;

/**
 * 平滑轮询算法
 * 		1、首选计算出所有weight的尺寸 , total。
 * 		2、将service为key，自身权重为value (current Weght值)，存入map。
 * 		3、割草开始，取出当前最大weight的service，并将最大weight的service的current - total
 *		4、最后每个service + 自身初始的wegith值，总计加的值也为total
 */
@LoadBalanceAno(RpcConstant.BALANCE_SMOOTH_WEIGHT)
public class SmoothWeightLoadBalance implements LoadBalance{
	
	private Map<String,Integer> map = new HashMap<>();

	@Override
	public MyService chooseOne(List<MyService> services) {
		services.forEach(service -> map.computeIfAbsent(service.toString(), key -> service.getWeight()));
        MyService maxWeightServer = null; // 最大的权重Server
        int sum = services.stream().mapToInt(MyService::getWeight).sum();
        for (MyService service : services) {
            Integer currentWeight = map.get(service.toString());
            if (maxWeightServer == null || currentWeight > map.get(maxWeightServer.toString())) {
                maxWeightServer = service;
            }
        }
        assert maxWeightServer != null;
        //  max - all size
        map.put(maxWeightServer.toString(), map.get(maxWeightServer.toString()) - sum);
        // 全部  + self weight == all size
        for (MyService service : services) {
            Integer currentWeight = map.get(service.toString());
            map.put(service.toString(), currentWeight + service.getWeight());
        }
		return maxWeightServer;
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
