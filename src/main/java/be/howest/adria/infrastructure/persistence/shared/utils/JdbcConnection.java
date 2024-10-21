package be.howest.adria.infrastructure.persistence.shared.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Warning: this singleton implementation is not thread-safe but is sufficient for this project
public class JdbcConnection {
  private static JdbcConnection instance;
  private final String url;

  private JdbcConnection(String url) {
    this.url = url;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url);
  }

  public static JdbcConnection initialize(String url) {
    if (instance != null)
      return instance;

    instance = new JdbcConnection(url);
    return instance();
  }

  public static JdbcConnection instance() {
    if (instance == null)
      throw new IllegalStateException("JdbcConnection not initialized");

    return instance;
  }
}
