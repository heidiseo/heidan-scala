package config

import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

object DatabaseConfig {
  lazy val db: PostgresProfile.backend.Database = Database.forConfig("database")
}
