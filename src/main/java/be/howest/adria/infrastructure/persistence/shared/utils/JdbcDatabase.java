package be.howest.adria.infrastructure.persistence.shared.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcDatabase {

  public static void executeSqlFile(JdbcConnection jdbcConnection, String sqlFilePath) {
    String[] sqlStatements = readSqlFile(sqlFilePath).split(";");

    try (Connection connection = jdbcConnection.getConnection()) {
      for (String statementString : sqlStatements) {
        if (statementString.trim().isEmpty())
          continue;

        executeStatement(connection, statementString);
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Failed to connect to the database", e);
    }
  }


  private static void executeStatement(Connection connection, String statementString) {
    try (PreparedStatement statement = connection.prepareStatement(statementString.trim())) {
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Failed to execute statement: " + statementString, e);
    }
  }

  private static String readSqlFile(String migrationFilePath) {
    try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(migrationFilePath)) {
      if (is == null)
        throw new IllegalStateException("Resource not found: " + migrationFilePath);

      return new String(is.readAllBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read migration file", e);
    }
  }

  private JdbcDatabase() {
    throw new IllegalStateException("Utility class");
  }
}
