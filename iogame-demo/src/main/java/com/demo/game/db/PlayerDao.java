package com.demo.game.db;

import com.demo.game.proto.PlayerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * 玩家数据访问对象，负责 MySQL 的 CRUD 操作
 * <pre>
 *     优化点：
 *     1. loginOrRegister 使用 INSERT ON DUPLICATE KEY 避免竞态条件
 *     2. updateNickname/addGold 合并 UPDATE+SELECT 为单连接事务，消除 N+1 查询
 * </pre>
 */
public class PlayerDao {
    private static final Logger log = LoggerFactory.getLogger(PlayerDao.class);
    private static final PlayerDao INSTANCE = new PlayerDao();

    public static PlayerDao me() {
        return INSTANCE;
    }

    /**
     * 根据用户名查询玩家
     *
     * @return PlayerInfo 或 null（不存在时）
     */
    public PlayerInfo findByUsername(String username) {
        String sql = "SELECT id, username, nickname, gold FROM t_player WHERE username = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            log.error("查询玩家失败: username={}", username, e);
            throw new RuntimeException("查询玩家失败", e);
        }

        return null;
    }

    /**
     * 创建新玩家
     */
    public PlayerInfo create(String username) {
        String sql = "INSERT INTO t_player (username, nickname, gold) VALUES (?, '', 0)";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    PlayerInfo info = new PlayerInfo();
                    info.id = keys.getLong(1);
                    info.username = username;
                    info.nickname = "";
                    info.gold = 0;
                    log.info("新玩家注册成功: {}", info);
                    return info;
                }
            }
        } catch (SQLException e) {
            log.error("创建玩家失败: username={}", username, e);
            throw new RuntimeException("创建玩家失败", e);
        }

        return null;
    }

    /**
     * 登录：根据用户名查找，不存在则自动注册
     * <pre>
     *     使用 INSERT ... ON DUPLICATE KEY UPDATE 保证原子性，避免竞态条件
     * </pre>
     */
    public PlayerInfo loginOrRegister(String username) {
        String sql = "INSERT INTO t_player (username, nickname, gold) VALUES (?, '', 0) "
                + "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    log.info("loginOrRegister 完成: username={}, id={}", username, id);
                }
            }
        } catch (SQLException e) {
            log.error("loginOrRegister 失败: username={}", username, e);
            throw new RuntimeException("loginOrRegister 失败", e);
        }

        // 查询完整的玩家信息返回
        return findByUsername(username);
    }

    /**
     * 更新玩家昵称
     * <pre>
     *     优化：UPDATE + SELECT 在同一连接的事务中执行，消除 N+1 查询
     * </pre>
     */
    public PlayerInfo updateNickname(String username, String nickname) {
        String updateSql = "UPDATE t_player SET nickname = ? WHERE username = ?";
        String selectSql = "SELECT id, username, nickname, gold FROM t_player WHERE username = ?";

        try (Connection conn = DbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. 执行更新
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, nickname);
                    ps.setString(2, username);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        conn.rollback();
                        return null;
                    }
                }

                // 2. 查询更新后的数据
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            PlayerInfo info = mapRow(rs);
                            conn.commit();
                            return info;
                        }
                    }
                }

                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("更新昵称失败: username={}, nickname={}", username, nickname, e);
            throw new RuntimeException("更新昵称失败", e);
        }

        return null;
    }

    /**
     * 增加金币
     * <pre>
     *     优化：UPDATE + SELECT 在同一连接的事务中执行，消除 N+1 查询
     * </pre>
     */
    public PlayerInfo addGold(String username, long gold) {
        String updateSql = "UPDATE t_player SET gold = gold + ? WHERE username = ?";
        String selectSql = "SELECT id, username, nickname, gold FROM t_player WHERE username = ?";

        try (Connection conn = DbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. 执行更新
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setLong(1, gold);
                    ps.setString(2, username);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        conn.rollback();
                        return null;
                    }
                }

                // 2. 查询更新后的数据
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            PlayerInfo info = mapRow(rs);
                            conn.commit();
                            return info;
                        }
                    }
                }

                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("增加金币失败: username={}, gold={}", username, gold, e);
            throw new RuntimeException("增加金币失败", e);
        }

        return null;
    }

    private PlayerInfo mapRow(ResultSet rs) throws SQLException {
        PlayerInfo info = new PlayerInfo();
        info.id = rs.getLong("id");
        info.username = rs.getString("username");
        info.nickname = rs.getString("nickname");
        info.gold = rs.getLong("gold");
        return info;
    }
}
