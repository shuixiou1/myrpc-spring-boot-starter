package com.wj.client.discovery;

import java.util.List;

import com.wj.common.model.MyService;

public interface ServerDiscovery {

	 List<MyService> findServiceList(String name);
}
