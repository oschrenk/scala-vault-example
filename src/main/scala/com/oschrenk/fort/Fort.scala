package com.oschrenk.fort

import com.typesafe.config.ConfigFactory
import com.typesafe.config.{Config => TypesafeConfig}

import com.typesafe.scalalogging.LazyLogging
import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig

object Fort {

  def main(args: Array[String]): Unit = {
    println("Hello World")
    println(Config.vault.address)
    println(Config.appSecret)
  }
}

class VaultFallbackConfig(val config: TypesafeConfig) extends LazyLogging {
  import scala.concurrent.ExecutionContext.Implicits.global

  val RetryCount = 5
  val RetryOffsetMilliseconds = 1000

  lazy val address = config.getString("vault.address")
  lazy val token = config.getString("vault.token")
  lazy val vaultConfig = new VaultConfig()
    .address(address)
    .token(token)
    .build()
  lazy val vault = new Vault(vaultConfig)

  def getString(localKey: String, vaultKey: String, vaultSubKey: String) = {
    if (config.hasPath(localKey)) {
      logger.info(s"Loading from local $localKey")
      config.getString(localKey)
    } else {
      logger.info(s"Loading from vault $vaultKey/$vaultSubKey")
      val response = vault.withRetries(RetryCount, RetryOffsetMilliseconds)
        .logical()
        .read(s"secret/$vaultKey")
       response.getData.get(vaultSubKey)
      }
  }
}

object Config {
  private val config = ConfigFactory.load()
  val vault = new VaultFallbackConfig(config)

  val appSecret = vault.getString("app_secret", "app", "password")
}

