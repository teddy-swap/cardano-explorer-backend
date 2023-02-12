package io.ergolabs.cardano.explorer.api

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.ergolabs.cardano.explorer.api.configs.PgConfig

object PostgresTransactor {

  def make[F[_]: Async: ContextShift](
    poolName: String,
    config: PgConfig
  ): Resource[F, HikariTransactor[F]] =
    for {
      cp      <- ExecutionContexts.fixedThreadPool(size = 16)
      blocker <- Blocker[F]
      xa <- HikariTransactor.newHikariTransactor[F](
              driverClassName = "org.postgresql.Driver",
              "jdbc:postgresql://" + scala.util.Properties.envOrElse(
                "DBSYNC_POSTGRESQL_HOSTNAME",
                config.url
              ) + ":" + scala.util.Properties.envOrElse("DBSYNC_POSTGRESQL_PORT", "5432") + scala.util.Properties
                .envOrElse("DBSYNC_POSTGRESQL_DBNAME", "cardanodbsync") + "?schema=public",
              scala.util.Properties.envOrElse("DBSYNC_POSTGRESQL_USER", config.user),
              scala.util.Properties.envOrElse("DBSYNC_POSTGRESQL_PASSWORD", config.pass),
              cp,
              blocker
            )
      _ <- Resource.eval(configure(xa)(poolName, config))
    } yield xa

  private def configure[F[_]: Sync](
    xa: HikariTransactor[F]
  )(name: String, config: PgConfig): F[Unit] =
    xa.configure { c =>
      Sync[F].delay {
        c.setAutoCommit(false)
        c.setPoolName(name)
        c.setMaxLifetime(600000)
        c.setIdleTimeout(30000)
        c.setMaximumPoolSize(config.maxConnections)
        c.setMinimumIdle(config.minConnections)
      }
    }
}
