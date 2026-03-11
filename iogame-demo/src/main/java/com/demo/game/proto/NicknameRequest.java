package com.demo.game.proto;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * 设置昵称请求
 */
@ProtobufClass
public class NicknameRequest {
    /** 用户名（标识玩家） */
    public String username;
    /** 新昵称 */
    public String nickname;

    @Override
    public String toString() {
        return "NicknameRequest{username='" + username + "', nickname='" + nickname + "'}";
    }
}
