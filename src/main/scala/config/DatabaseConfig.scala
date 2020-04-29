package config

import slick.jdbc.PostgresProfile.api._

object DatabaseConfig {
  lazy val db = Database.forConfig("database")
}
