package com.demo.game.proto;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * 增加金币请求
 */
@ProtobufClass
public class GoldRequest {
    /** 用户名（标识玩家） */
    public String username;
    /** 增加的金币数量 */
    public long gold;

    @Override
    public String toString() {
        return "GoldRequest{username='" + username + "', gold=" + gold + "}";
    }
}
