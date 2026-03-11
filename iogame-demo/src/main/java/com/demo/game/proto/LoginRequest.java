package com.demo.game.proto;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * 登录请求 / 查询请求
 */
@ProtobufClass
public class LoginRequest {
    /** 用户名 */
    public String username;

    @Override
    public String toString() {
        return "LoginRequest{username='" + username + "'}";
    }
}
