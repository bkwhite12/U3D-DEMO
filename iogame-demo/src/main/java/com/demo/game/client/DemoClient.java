package com.demo.game.client;

import com.iohao.game.external.client.join.ClientRunOne;
import com.iohao.game.external.client.kit.ClientUserConfigs;

import java.util.List;

/**
 * 模拟客户端启动入口
 * <pre>
 *     启动前请确保服务端 DemoApplication 已经启动
 *
 *     启动后会在控制台显示可用命令列表：
 *     输入 1-0 发送登录请求
 *     输入 1-1 发送设置昵称请求
 *     输入 1-2 发送增加金币请求
 *     输入 1-3 发送查询玩家信息请求
 * </pre>
 */
public class DemoClient {
    public static void main(String[] args) {
        // 关闭框架内部冗余日志
        ClientUserConfigs.closeLog();

        // 启动模拟客户端，连接到 ws://127.0.0.1:10100/websocket
        new ClientRunOne()
                .setInputCommandRegions(List.of(new PlayerRegion()))
                .startup();
    }
}
