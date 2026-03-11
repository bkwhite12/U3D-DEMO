package com.demo.game.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库配置与连接管理（使用 HikariCP 连接池）
 */
public class DbConfig {
    private static final Logger log = LoggerFactory.getLogger(DbConfig.class);

    private static HikariDataSource dataSource;

    /**
     * 初始化数据库连接池，并测试连接
     */
    public static void init() {
        try {
            String url;
            String username;
            String password;

            Properties props = new Properties();
            InputStream is = DbConfig.class.getClassLoader().getResourceAsStream("db.properties");
            if (is != null) {
                props.load(is);
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
            } else {
                // 默认值
                url = "jdbc:mysql://localhost:3306/iogame_demo?sslMode=DISABLED&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
                username = "root";
                password = "root";
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // 连接池参数
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(5000);
            config.setIdleTimeout(300000);
            config.setMaxLifetime(600000);

            dataSource = new HikariDataSource(config);

            // 测试连接是否正常
            try (Connection conn = getConnection()) {
                log.info("数据库连接池初始化成功: {}", url);
            }
        } catch (Exception e) {
            log.error("数据库初始化失败，请检查 MySQL 是否启动以及 db.properties 配置是否正确", e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    /**
     * 从连接池获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭连接池
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("数据库连接池已关闭");
        }
    }
}
