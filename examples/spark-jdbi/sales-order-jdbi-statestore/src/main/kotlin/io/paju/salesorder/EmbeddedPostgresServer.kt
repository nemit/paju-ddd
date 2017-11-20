package io.paju.salesorder

import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.cachedRuntimeConfig
import ru.yandex.qatools.embed.postgresql.distribution.Version
import org.flywaydb.core.Flyway
import java.nio.file.Paths

// For testing and demonstration purposes only
class EmbeddedPostgresServer
private constructor(val postgres: EmbeddedPostgres, var url: String)
{
    companion object {
        val instance: EmbeddedPostgresServer by lazy {
            val postgres = EmbeddedPostgres(Version.V10_0);
            val runtimeCachePath = Paths.get(System.getProperty("user.home"), ".embedpostgresql" )
            val url = postgres.start(cachedRuntimeConfig(runtimeCachePath))
            EmbeddedPostgresServer(postgres, url).apply {
                // migrate
                val flyway = Flyway()
                flyway.setDataSource(url, EmbeddedPostgres.DEFAULT_USER, EmbeddedPostgres.DEFAULT_PASSWORD)
                flyway.migrate()
            }
        }
    }

}