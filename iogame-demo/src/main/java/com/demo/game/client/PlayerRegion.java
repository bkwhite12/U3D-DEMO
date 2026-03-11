package com.demo.game.client;

import com.demo.game.GameCmd;
import com.demo.game.proto.*;
import com.iohao.game.external.client.AbstractInputCommandRegion;
import com.iohao.game.external.client.kit.ScannerKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 玩家模块 - 客户端命令定义
 * <pre>
 *     启动后在控制台输入对应命令编号即可发送请求：
 *     1-0 : 登录
 *     1-1 : 设置昵称
 *     1-2 : 增加金币
 *     1-3 : 查询玩家信息
 * </pre>
 */
public class PlayerRegion extends AbstractInputCommandRegion {
    private static final Logger log = LoggerFactory.getLogger(PlayerRegion.class);

    @Override
    public void initInputCommand() {
        // 设置主路由
        inputCommandCreate.cmd = GameCmd.cmd;

        // ---- 1-0 : 登录（不存在则自动注册） ----
        ofCommand(GameCmd.login).setTitle("登录（输入用户名，不存在自动注册）").setRequestData(() -> {
            LoginRequest req = new LoginRequest();
            log.info("请输入用户名:");
            req.username = ScannerKit.nextLine();
            return req;
        }).callback(result -> {
            PlayerInfo info = result.getValue(PlayerInfo.class);
            log.info("登录结果 => {}", info);
        });

        // ---- 1-1 : 设置昵称 ----
        ofCommand(GameCmd.setNickname).setTitle("设置昵称").setRequestData(() -> {
            NicknameRequest req = new NicknameRequest();
            log.info("请输入用户名:");
            req.username = ScannerKit.nextLine();
            log.info("请输入新昵称:");
            req.nickname = ScannerKit.nextLine();
            return req;
        }).callback(result -> {
            PlayerInfo info = result.getValue(PlayerInfo.class);
            log.info("设置昵称结果 => {}", info);
        });

        // ---- 1-2 : 增加金币 ----
        ofCommand(GameCmd.addGold).setTitle("增加金币").setRequestData(() -> {
            GoldRequest req = new GoldRequest();
            log.info("请输入用户名:");
            req.username = ScannerKit.nextLine();
            log.info("请输入增加的金币数量:");
            req.gold = ScannerKit.nextLong();
            return req;
        }).callback(result -> {
            PlayerInfo info = result.getValue(PlayerInfo.class);
            log.info("增加金币结果 => {}", info);
        });

        // ---- 1-3 : 查询玩家信息 ----
        ofCommand(GameCmd.getInfo).setTitle("查询玩家信息").setRequestData(() -> {
            LoginRequest req = new LoginRequest();
            log.info("请输入用户名:");
            req.username = ScannerKit.nextLine();
            return req;
        }).callback(result -> {
            PlayerInfo info = result.getValue(PlayerInfo.class);
            log.info("查询结果 => {}", info);
        });
    }
}
