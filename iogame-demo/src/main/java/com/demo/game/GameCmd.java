package com.demo.game;

/**
 * 路由常量定义
 * <pre>
 *     主路由 cmd = 1，代表玩家模块
 *     子路由分别对应不同的操作
 * </pre>
 */
public interface GameCmd {
    /** 玩家模块 - 主路由 */
    int cmd = 1;
    /** 登录/注册 */
    int login = 0;
    /** 设置昵称 */
    int setNickname = 1;
    /** 增加金币 */
    int addGold = 2;
    /** 查询玩家信息 */
    int getInfo = 3;
}
