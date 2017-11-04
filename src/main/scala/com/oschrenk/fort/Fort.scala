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

class VaultCubbyholeConfig(val config: TypesafeConfig) extends LazyLogging {
  import scala.concurrent.ExecutionContext.Implicits.global

  val RetryCount = 5
  val RetryOffsetMilliseconds = 1000

  lazy val address = config.getString("vault.address")
  lazy val tempToken = config.getString("vault.temp_token")
  lazy val vault = getPermanentVault(address, tempToken)

  def getPermanentVault(address: String, tempToken: String): Vault = {
    def getVault(address: String, tempToken: String): Vault = {
      new Vault(new VaultConfig()
        .address(address)
        .token(tempToken)
        .build())
    }

    val tempVault = getVault(address, tempToken)
    val permToken = fetch(tempVault, "cubbyhole/app-token", "token")
    logger.info(s"Fetched permanent token $permToken")
    getVault(address, permToken)
  }

  def fetch(vault: Vault, path: String, key: String): String = {
      val response = vault.withRetries(RetryCount, RetryOffsetMilliseconds)
        .logical()
        .read(path)
        logger.info(response.toString)
        logger.info(response.getData.toString)
      response.getData.get(key)
  }

  def getString(localKey: String, vaultKey: String, vaultSubKey: String) = {
    if (config.hasPath(localKey)) {
      logger.info(s"Loading from local $localKey")
      config.getString(localKey)
    } else {
      logger.info(s"Loading from vault $vaultKey/$vaultSubKey")
      fetch(vault, s"secret/$vaultKey", vaultSubKey)
    }
  }
}

object Config {
  private val config = ConfigFactory.load()
  val vault = new VaultCubbyholeConfig(config)

  val appSecret = vault.getString("app_secret", "app", "password")
}

