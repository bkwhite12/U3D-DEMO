package com.demo.game;

import com.demo.game.db.DbConfig;
import com.iohao.game.external.core.netty.simple.NettySimpleHelper;

import java.util.List;
import java.util.Locale;

/**
 * 服务端启动入口
 * <pre>
 *     启动前请确保：
 *     1. MySQL 已启动
 *     2. 已执行 sql/init.sql 建库建表
 *     3. db.properties 中的数据库连接信息正确
 * </pre>
 */
public class DemoApplication {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // 1. 初始化数据库连接池
        DbConfig.init();

        // 2. 游戏逻辑服
        var demoLogicServer = new DemoLogicServer();

        // 3. 游戏对外服端口（WebSocket）
        int port = 10100;

        // 4. 启动服务器（对外服 + 网关 + 逻辑服，单体模式）
        NettySimpleHelper.run(port, List.of(demoLogicServer));

        /*
         * 生产环境建议：可通过 ExternalGlobalConfig.accessAuthenticationHook 配置路由访问权限
         * 例如：
         *   accessHook.addRejectionCmd(GameCmd.cmd, GameCmd.addGold);  // 拒绝 addGold 外部访问
         *   accessHook.setVerifyIdentity(true);                        // 开启登录验证
         *   accessHook.addIgnoreAuthCmd(GameCmd.cmd, GameCmd.login);   // 登录路由白名单
         */
    }
}
