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

            // Supabase PostgreSQL Configuration - Clean version
            config.setJdbcUrl("jdbc:postgresql://db.zihonqgcioggsycncheh.supabase.co:5432/postgres?sslmode=require");
            config.setUsername("postgres");
            config.setPassword("Emseize.90."); // Password with trailing dot

            config.setMaximumPoolSize(5);
            config.setConnectionTimeout(30000);

            // Optimization properties
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            logger.info("Initializing HikariCP for Supabase...");
            ds = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool: {}", e.getMessage(), e);
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
