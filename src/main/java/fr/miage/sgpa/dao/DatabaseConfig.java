package fr.miage.sgpa.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource ds;

    static {
        try {
            HikariConfig config = new HikariConfig();

            // Supabase PostgreSQL Configuration
            // Password updated to 'Emseize.90' as per latest instructions
            String url = "jdbc:postgresql://db.zihonqgcioggsycncheh.supabase.co:5432/postgres?sslmode=require";
            String user = "postgres";
            String password = "Emseize.90";

            logger.info("Initializing HikariCP connection pool for Supabase...");
            logger.info("URL: {}", url);
            logger.info("User: {}", user);

            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            // Performance and Stability settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            config.setMinimumIdle(2);
            config.setMaximumPoolSize(5);
            config.setConnectionTimeout(20000); // 20 seconds
            config.setIdleTimeout(300000); // 5 minutes

            ds = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool: {}", e.getMessage(), e);
            // We don't rethrow here to allow the class to load, but getConnection will fail
        }
    }

    private DatabaseConfig() {}

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new SQLException("Database connection pool is not initialized. Check logs for errors.");
        }
        return ds.getConnection();
    }

    public static void close() {
        if (ds != null) {
            ds.close();
            logger.info("HikariCP connection pool closed.");
        }
    }
}
