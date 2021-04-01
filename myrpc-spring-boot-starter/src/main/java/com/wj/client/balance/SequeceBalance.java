package com.wj.client.balance;

import java.util.List;

import com.wj.annotation.LoadBalanceAno;
import com.wj.common.constants.RpcConstant;
import com.wj.common.model.MyService;

/**
 * 顺序轮询
 */
@LoadBalanceAno(RpcConstant.BALANCE_SEQUECE)
public class SequeceBalance implements LoadBalance{
	
	private volatile int index = 0;

	@Override
	public synchronized MyService chooseOne(List<MyService> services) {
		if (index == services.size()) {
			index = 0;
		}
		return services.get(index++);
	}

}
