package be.howest.adria.infrastructure.persistence.migrations;

import be.howest.adria.infrastructure.persistence.shared.utils.JdbcConnection;
import be.howest.adria.infrastructure.persistence.shared.utils.JdbcDatabase;

public class Migration {

  public static void migrate(String migrationFilePath, JdbcConnection jdbcConnection) {
    try {
      JdbcDatabase.executeSqlFile(jdbcConnection, migrationFilePath);
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Failed to migrate database", e);
    }
  }

  private Migration() {
    throw new IllegalStateException("Utility class");
  }
}

