package fr.miage.sgpa.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        // Supabase PostgreSQL Configuration
        // Note: Check if there's a trailing space or dot in your password if connection fails.
        // We're using "Emseize.90" as requested in the latest update.
        String url = "jdbc:postgresql://db.zihonqgcioggsycncheh.supabase.co:5432/postgres?sslmode=require";
        String user = "postgres";
        String password = "Emseize.90";

        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        // Performance and Stability settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);

        ds = new HikariDataSource(config);
    }

    private DatabaseConfig() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            if (conn.isValid(2)) {
                System.out.println("Connection to Supabase successful!");
            }
        }
    }

    public static void close() {
        if (ds != null) {
            ds.close();
        }
    }
}
