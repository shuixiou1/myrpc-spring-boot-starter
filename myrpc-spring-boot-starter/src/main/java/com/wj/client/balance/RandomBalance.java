package com.wj.client.balance;

import java.util.List;
import java.util.Random;

import com.wj.annotation.LoadBalanceAno;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyService;


@LoadBalanceAno(RpcConstant.BALANCE_RANDOM)
public class RandomBalance implements LoadBalance{
	
    private static Random random = new Random();

	@Override
	public MyService chooseOne(List<MyService> services) {
		return services.get(random.nextInt(services.size()));
	}

}
