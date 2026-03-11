package com.demo.game;

import com.demo.game.db.PlayerDao;
import com.demo.game.proto.*;
import com.iohao.game.action.skeleton.annotation.ActionController;
import com.iohao.game.action.skeleton.annotation.ActionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 玩家业务 Action
 * <pre>
 *     路由: 1-0 登录/注册
 *     路由: 1-1 设置昵称
 *     路由: 1-2 增加金币
 *     路由: 1-3 查询玩家信息
 * </pre>
 */
@ActionController(GameCmd.cmd)
public class PlayerAction {
    private static final Logger log = LoggerFactory.getLogger(PlayerAction.class);
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final int MAX_NICKNAME_LENGTH = 32;
    private final PlayerDao playerDao = PlayerDao.me();

    /**
     * 登录（不存在则自动注册）
     * <pre>
     *     路由: 1-0
     * </pre>
     */
    @ActionMethod(GameCmd.login)
    public PlayerInfo login(LoginRequest request) {
        // 校验用户名
        String username = request.username;
        GameCode.usernameEmpty.assertTrue(username != null && !username.isBlank());
        GameCode.usernameTooLong.assertTrue(username.length() <= MAX_USERNAME_LENGTH);

        PlayerInfo info = playerDao.loginOrRegister(username.trim());
        GameCode.dbError.assertTrue(info != null);

        log.info("登录成功: {}", info);
        return info;
    }

    /**
     * 设置昵称
     * <pre>
     *     路由: 1-1
     * </pre>
     */
    @ActionMethod(GameCmd.setNickname)
    public PlayerInfo setNickname(NicknameRequest request) {
        GameCode.usernameEmpty.assertTrue(request.username != null && !request.username.isBlank());
        GameCode.usernameTooLong.assertTrue(request.username.length() <= MAX_USERNAME_LENGTH);
        GameCode.nicknameEmpty.assertTrue(request.nickname != null && !request.nickname.isBlank());
        GameCode.nicknameTooLong.assertTrue(request.nickname.length() <= MAX_NICKNAME_LENGTH);

        PlayerInfo info = playerDao.updateNickname(request.username.trim(), request.nickname.trim());
        GameCode.playerNotFound.assertTrue(info != null);

        log.info("设置昵称成功: {}", info);
        return info;
    }

    /**
     * 增加金币
     * <pre>
     *     路由: 1-2
     *     注意: 此路由已通过 AccessAuthenticationHook 配置为拒绝外部访问
     * </pre>
     */
    @ActionMethod(GameCmd.addGold)
    public PlayerInfo addGold(GoldRequest request) {
        GameCode.usernameEmpty.assertTrue(request.username != null && !request.username.isBlank());
        GameCode.usernameTooLong.assertTrue(request.username.length() <= MAX_USERNAME_LENGTH);
        GameCode.goldInvalid.assertTrue(request.gold > 0);

        PlayerInfo info = playerDao.addGold(request.username.trim(), request.gold);
        GameCode.playerNotFound.assertTrue(info != null);

        log.info("增加金币成功: {}", info);
        return info;
    }

    /**
     * 查询玩家信息
     * <pre>
     *     路由: 1-3
     * </pre>
     */
    @ActionMethod(GameCmd.getInfo)
    public PlayerInfo getInfo(LoginRequest request) {
        GameCode.usernameEmpty.assertTrue(request.username != null && !request.username.isBlank());
        GameCode.usernameTooLong.assertTrue(request.username.length() <= MAX_USERNAME_LENGTH);

        PlayerInfo info = playerDao.findByUsername(request.username.trim());
        GameCode.playerNotFound.assertTrue(info != null);

        log.info("查询玩家信息: {}", info);
        return info;
    }
}
