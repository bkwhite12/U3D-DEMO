package com.demo.game;

import com.iohao.game.action.skeleton.core.exception.MsgExceptionInfo;

/**
 * 业务错误码
 */
public enum GameCode implements MsgExceptionInfo {
    /** 用户名不能为空 */
    usernameEmpty(100, "用户名不能为空"),
    /** 昵称不能为空 */
    nicknameEmpty(101, "昵称不能为空"),
    /** 金币数量必须大于0 */
    goldInvalid(102, "金币数量必须大于0"),
    /** 玩家不存在，请先登录 */
    playerNotFound(103, "玩家不存在，请先登录"),
    /** 数据库操作失败 */
    dbError(104, "数据库操作失败"),
    /** 用户名长度超限 */
    usernameTooLong(105, "用户名长度不能超过32个字符"),
    /** 昵称长度超限 */
    nicknameTooLong(106, "昵称长度不能超过32个字符"),
    ;

    final int code;
    final String msg;

    GameCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
