package be.howest.adria.infrastructure.persistence;

import be.howest.adria.infrastructure.persistence.migrations.Migration;
import be.howest.adria.infrastructure.persistence.migrations.Seeder;
import be.howest.adria.infrastructure.persistence.repositories.SqliteTodoListRepository;
import be.howest.adria.infrastructure.persistence.repositories.SqliteUserRepository;
import be.howest.adria.infrastructure.persistence.repositories.mappers.TodoListMapper;
import be.howest.adria.infrastructure.persistence.shared.utils.JdbcConnection;
import be.howest.adria.infrastructure.shared.utils.Config;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PersistenceModule {
  private static final Logger LOGGER = Logger.getLogger(PersistenceModule.class.getName());
  private static Config config;

  public static void init(Config cfg) {
    config = cfg;
    JdbcConnection.initialize(config.readSetting("sqlite.connectionString"));
    buildRepositories();
    migrateDatabase();
    seedDatabase();
  }

  private static void buildRepositories() {
    SqliteUserRepository.initialize(JdbcConnection.instance());
    SqliteTodoListRepository.initialize(JdbcConnection.instance(), new TodoListMapper());
  }

  private static void migrateReadDatabase() {
    String migrationFilePath = config.readSetting("sqlite.migration.file");
    LOGGER.log(Level.INFO, "Migrating database with file {0}", migrationFilePath);
    Migration.migrate(migrationFilePath, JdbcConnection.instance());
  }

  private static void migrateDatabase() {
    migrateReadDatabase();
  }

  private static void seedDatabase() {
    Seeder seeder = new Seeder(
        SqliteTodoListRepository.instance(),
        SqliteUserRepository.instance());
    seeder.seed();
  }

  private PersistenceModule() {
    throw new IllegalStateException("Utility class");
  }
}
