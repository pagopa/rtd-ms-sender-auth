package it.gov.pagopa.rtd.ms.rtdmssenderauth;

import com.mongodb.event.ConnectionCheckOutFailedEvent;
import com.mongodb.event.ConnectionCheckOutStartedEvent;
import com.mongodb.event.ConnectionCheckedInEvent;
import com.mongodb.event.ConnectionCheckedOutEvent;
import com.mongodb.event.ConnectionClosedEvent;
import com.mongodb.event.ConnectionCreatedEvent;
import com.mongodb.event.ConnectionPoolClosedEvent;
import com.mongodb.event.ConnectionPoolCreatedEvent;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

  // some values are take from https://learn.microsoft.com/en-us/azure/cosmos-db/mongodb/error-codes-solutions
  // Reference: https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/connection/connection-options/
  private static final int COSMOS_MONGO_MAX_IDLE_TIME_MS = 120000;
  private static final int COSMOS_MONGO_SOCKET_TIMEOUT_MS = COSMOS_MONGO_MAX_IDLE_TIME_MS;
  private static final int COSMOS_MONGO_MAX_WAIT_TIME = COSMOS_MONGO_MAX_IDLE_TIME_MS;
  private static final int COSMOS_MONGO_MAX_LIFE_TIME_MS = 0;

  @Bean
  public MongoClientSettingsBuilderCustomizer mongoConnectionPoolConfiguration() {
    return builder -> builder
        .applyToServerSettings(srv -> srv.addServerMonitorListener(new HeartBeatLogger()))
        .applyToConnectionPoolSettings(pool -> {
          pool.maxWaitTime(COSMOS_MONGO_MAX_WAIT_TIME, TimeUnit.MILLISECONDS);
          pool.maxConnectionLifeTime(COSMOS_MONGO_MAX_LIFE_TIME_MS, TimeUnit.MILLISECONDS);
          pool.maxConnectionIdleTime(COSMOS_MONGO_MAX_IDLE_TIME_MS, TimeUnit.MILLISECONDS);
          pool.addConnectionPoolListener(new ConnectionPoolLogger());
        })
        .applyToSocketSettings(socket -> {
          socket.readTimeout(COSMOS_MONGO_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS);
          socket.connectTimeout(COSMOS_MONGO_SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        });
  }

  @Slf4j
  private static final class HeartBeatLogger implements ServerMonitorListener {

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
      ServerMonitorListener.super.serverHeartbeatFailed(event);
      log.info(event.toString());
    }
  }

  @Slf4j
  private static final class ConnectionPoolLogger implements ConnectionPoolListener {
    @Override
    public void connectionPoolCreated(ConnectionPoolCreatedEvent event) {
      ConnectionPoolListener.super.connectionPoolCreated(event);
      log.info(event.toString());
    }

    @Override
    public void connectionPoolClosed(ConnectionPoolClosedEvent event) {
      ConnectionPoolListener.super.connectionPoolClosed(event);
      log.info(event.toString());
    }

    @Override
    public void connectionCheckOutStarted(ConnectionCheckOutStartedEvent event) {
      ConnectionPoolListener.super.connectionCheckOutStarted(event);
      log.info(event.toString());
    }

    @Override
    public void connectionCheckedOut(ConnectionCheckedOutEvent event) {
      ConnectionPoolListener.super.connectionCheckedOut(event);
      log.info(event.toString());
    }

    @Override
    public void connectionCheckOutFailed(ConnectionCheckOutFailedEvent event) {
      ConnectionPoolListener.super.connectionCheckOutFailed(event);
      log.info(event.toString());
    }

    @Override
    public void connectionCreated(ConnectionCreatedEvent event) {
      ConnectionPoolListener.super.connectionCreated(event);
      log.info(event.toString());
    }

    @Override
    public void connectionClosed(ConnectionClosedEvent event) {
      ConnectionPoolListener.super.connectionClosed(event);
      log.info(event.toString());
    }

    @Override
    public void connectionCheckedIn(ConnectionCheckedInEvent event) {
      ConnectionPoolListener.super.connectionCheckedIn(event);
      log.info(event.toString());
    }
  }
}

