package fr.miage.sgpa.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource ds;

    static {
        try {
            // Configuration parameters
            String host = "localhost";
            String port = "5432";
            String dbName = "sgpa";
            String user = "postgres";
            String password = "";

            // 1. Try Environment Variables
            if (System.getenv("SGPA_DB_HOST") != null) {
                host = System.getenv("SGPA_DB_HOST");
                port = System.getenv("SGPA_DB_PORT") != null ? System.getenv("SGPA_DB_PORT") : port;
                dbName = System.getenv("SGPA_DB_NAME") != null ? System.getenv("SGPA_DB_NAME") : dbName;
                user = System.getenv("SGPA_DB_USER") != null ? System.getenv("SGPA_DB_USER") : user;
                password = System.getenv("SGPA_DB_PASSWORD") != null ? System.getenv("SGPA_DB_PASSWORD") : password;
                logger.info("Using database configuration from Environment Variables.");
            }
            // 2. Try Local Properties File
            else {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream("config/local.properties")) {
                    props.load(fis);
                    host = props.getProperty("db.host", host);
                    port = props.getProperty("db.port", port);
                    dbName = props.getProperty("db.name", dbName);
                    user = props.getProperty("db.user", user);
                    password = props.getProperty("db.password", password);
                    logger.info("Using database configuration from config/local.properties.");
                } catch (IOException e) {
                    logger.warn("Could not find or read config/local.properties. Using default values.");
                    if (password.isEmpty()) {
                        logger.warn("No database password provided in environment or properties file.");
                    }
                }
            }

            logger.info("Effective database configuration -> Host: {}, Port: {}, Database: {}, User: {}", host, port, dbName, user);

            String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            config.setMaximumPoolSize(5);
            config.setConnectionTimeout(30000);

            // Optimization properties
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

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
