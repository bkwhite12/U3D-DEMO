package com.demo.game;

import com.iohao.game.action.skeleton.core.BarSkeleton;
import com.iohao.game.action.skeleton.core.BarSkeletonBuilderParamConfig;
import com.iohao.game.action.skeleton.core.flow.internal.DebugInOut;
import com.iohao.game.bolt.broker.client.AbstractBrokerClientStartup;
import com.iohao.game.bolt.broker.core.client.BrokerAddress;
import com.iohao.game.bolt.broker.core.client.BrokerClient;
import com.iohao.game.bolt.broker.core.client.BrokerClientBuilder;
import com.iohao.game.bolt.broker.core.common.IoGameGlobalConfig;

/**
 * 游戏逻辑服
 * <pre>
 *     负责注册 Action 控制器，处理玩家请求
 * </pre>
 */
public class DemoLogicServer extends AbstractBrokerClientStartup {

    @Override
    public BarSkeleton createBarSkeleton() {
        // 业务框架构建器配置 - 扫描 PlayerAction 所在包
        var config = new BarSkeletonBuilderParamConfig()
                .scanActionPackage(PlayerAction.class);

        var builder = config.createBuilder();
        // 添加控制台调试输出插件
        builder.addInOut(new DebugInOut());

        return builder.build();
    }

    @Override
    public BrokerClientBuilder createBrokerClientBuilder() {
        BrokerClientBuilder builder = BrokerClient.newBuilder();
        builder.appName("iogame-demo-logic");
        return builder;
    }

    @Override
    public BrokerAddress createBrokerAddress() {
        return new BrokerAddress("127.0.0.1", IoGameGlobalConfig.brokerPort);
    }
}
