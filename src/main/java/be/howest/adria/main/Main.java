package be.howest.adria.main;

import be.howest.adria.infrastructure.persistence.PersistenceModule;
import be.howest.adria.infrastructure.pushnotifications.PushNotificationModule;
import be.howest.adria.infrastructure.pushnotifications.server.PushServer;
import be.howest.adria.infrastructure.shared.utils.Config;
import be.howest.adria.infrastructure.webapi.WebApiModule;
import be.howest.adria.main.factories.WebApiControllerFactory;

public class Main {
  public static void main(String[] args) {
    Config config = new Config("/config/config.properties");

    PersistenceModule.init(config);

    PushNotificationModule.init(config);
    
    WebApiModule.init(config,
        WebApiControllerFactory.instance()::createController,
        PushServer.instance());
  }

  public static void tearDown() {
    WebApiModule.tearDown();
  }
}
