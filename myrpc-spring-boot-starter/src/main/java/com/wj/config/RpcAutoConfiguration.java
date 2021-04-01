package com.wj.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.wj.annotation.LoadBalanceAno;
import com.wj.annotation.MessageProtocolAno;
import com.wj.client.balance.LoadBalance;
import com.wj.client.discovery.ZookeeperServerDiscovery;
import com.wj.client.net.ClientProxyFactory;
import com.wj.client.net.MyNettyClient;
import com.wj.common.protocol.MessageProtocol;
import com.wj.config.props.RpcConfig;
import com.wj.exception.RpcException;
import com.wj.server.MyRpcServer;
import com.wj.server.RequestHandler;
import com.wj.server.RpcServer;
import com.wj.server.register.DefaultRpcProcessor;
import com.wj.server.register.ServerRegister;
import com.wj.server.register.ZookeeperServerRegister;

/**
 * 注入需要的bean
 */
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcAutoConfiguration {

    @Bean
    public RpcConfig rpcConfig() {
        return new RpcConfig();
    }

    @Bean
    public ServerRegister serverRegister(@Autowired RpcConfig rpcConfig) {
        return new ZookeeperServerRegister(
                rpcConfig.getRegisterAddress(),
                rpcConfig.getServerPort(),
                rpcConfig.getProtocol(),
                rpcConfig.getWeight());
    }

    @Bean
    public RequestHandler requestHandler(@Autowired ServerRegister serverRegister,
                                         @Autowired RpcConfig rpcConfig) {
        return new RequestHandler(getMessageProtocol(rpcConfig.getProtocol()), serverRegister);
    }

    @Bean
    public RpcServer rpcServer(@Autowired RequestHandler requestHandler,
                               @Autowired RpcConfig rpcConfig) {
        return new MyRpcServer(rpcConfig.getServerPort(), requestHandler);
    }

    /**
     * 创建clientProxyFactory，其中设置了协议、负载均衡、网络实现。
     */
    @Bean
    public ClientProxyFactory proxyFactory(@Autowired RpcConfig rpcConfig) {
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();
        // 设置服务发现着
        clientProxyFactory.setServerDiscovery(new ZookeeperServerDiscovery(rpcConfig.getRegisterAddress()));

        // 设置支持的协议
        Map<String, MessageProtocol> supportMessageProtocols = buildSupportMessageProtocols();
        clientProxyFactory.setSupportMessageProtocols(supportMessageProtocols);
        // 设置负载均衡算法
        LoadBalance loadBalance = getLoadBalance(rpcConfig.getLoadBalance());
        clientProxyFactory.setLoadBalance(loadBalance);
        // 设置网络层实现
        clientProxyFactory.setNetClient(new MyNettyClient());
        
        clientProxyFactory.setMessageProtocol(supportMessageProtocols.get(rpcConfig.getProtocol()));

        return clientProxyFactory;
    }

    private MessageProtocol getMessageProtocol(String name) {
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        Iterator<MessageProtocol> iterator = loader.iterator();
        while (iterator.hasNext()) {
            MessageProtocol messageProtocol = iterator.next();
            MessageProtocolAno ano = messageProtocol.getClass().getAnnotation(MessageProtocolAno.class);
            Assert.notNull(ano, "message protocol name can not be empty!");
            if (name.equals(ano.value())) {
                return messageProtocol;
            }
        }
        throw new RpcException("invalid message protocol config!");
    }

    private Map<String, MessageProtocol> buildSupportMessageProtocols() {
        Map<String, MessageProtocol> supportMessageProtocols = new HashMap<>();
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        Iterator<MessageProtocol> iterator = loader.iterator();
        while (iterator.hasNext()) {
            MessageProtocol messageProtocol = iterator.next();
            MessageProtocolAno ano = messageProtocol.getClass().getAnnotation(MessageProtocolAno.class);
            Assert.notNull(ano, "message protocol name can not be empty!");
            supportMessageProtocols.put(ano.value(), messageProtocol);
        }
        return supportMessageProtocols;
    }

    private LoadBalance getLoadBalance(String name) {
        ServiceLoader<LoadBalance> loader = ServiceLoader.load(LoadBalance.class);
        Iterator<LoadBalance> iterator = loader.iterator();
        while (iterator.hasNext()) {
            LoadBalance loadBalance = iterator.next();
            LoadBalanceAno ano = loadBalance.getClass().getAnnotation(LoadBalanceAno.class);
            Assert.notNull(ano, "load balance name can not be empty!");
            if (name.equals(ano.value())) {
                return loadBalance;
            }
        }
        throw new RpcException("invalid load balance config");
    }

    @Bean
    public DefaultRpcProcessor rpcProcessor(@Autowired ClientProxyFactory clientProxyFactory,
                                            @Autowired ServerRegister serverRegister,
                                            @Autowired RpcServer rpcServer) {
        return new DefaultRpcProcessor(clientProxyFactory, serverRegister, rpcServer);
    }


}
