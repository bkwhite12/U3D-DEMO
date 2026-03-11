package com.demo.game.proto;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * 玩家信息（服务端响应）
 */
@ProtobufClass
public class PlayerInfo {
    /** 玩家ID */
    public long id;
    /** 用户名 */
    public String username;
    /** 昵称 */
    public String nickname;
    /** 金币 */
    public long gold;

    @Override
    public String toString() {
        return "PlayerInfo{id=" + id
                + ", username='" + username + "'"
                + ", nickname='" + nickname + "'"
                + ", gold=" + gold + "}";
    }
}
