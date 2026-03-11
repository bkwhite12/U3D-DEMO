-- 创建数据库
CREATE DATABASE IF NOT EXISTS iogame_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE iogame_demo;

-- 玩家表
CREATE TABLE IF NOT EXISTS t_player (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '玩家ID',
    username   VARCHAR(64) NOT NULL UNIQUE       COMMENT '登录账号（唯一标识）',
    nickname   VARCHAR(64) NOT NULL DEFAULT ''   COMMENT '玩家昵称',
    gold       BIGINT      NOT NULL DEFAULT 0    COMMENT '游戏金币',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP               COMMENT '创建时间',
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='玩家表';
