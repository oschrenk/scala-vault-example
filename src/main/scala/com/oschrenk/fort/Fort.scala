package com.oschrenk.fort

import com.typesafe.config.ConfigFactory
import com.typesafe.config.{Config => TypesafeConfig}
import janstenpickle.vault.core.VaultConfig
import janstenpickle.vault.core.WSClient
import janstenpickle.vault.core.Secrets

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.Await
import java.net.URL

import uscala.result.Result.{Fail, Ok}

object Fort {

  def main(args: Array[String]): Unit = {
    println("Hello World")
    println(Config.vault.address)
    println(Config.appSecret)

  }

}

class VaultFallbackConfig(val config: TypesafeConfig) extends LazyLogging {
  import scala.concurrent.ExecutionContext.Implicits.global

  lazy private val SecretBackend = "secret"
  lazy private val timeout = 10.seconds

  lazy val address = config.getString("vault.address")
  lazy val token = config.getString("vault.token")
  lazy val vaultConfig = VaultConfig(WSClient(new URL(address)), token)
  lazy val secrets = Secrets(vaultConfig, SecretBackend)

  def getString(localKey: String, vaultKey: String, vaultSubKey: String) = {
    if (config.hasPath(localKey)) {
      logger.info(s"Loading from local $localKey")
      config.getString(localKey)
    } else {
      logger.info(s"Loading from vault $vaultKey/$vaultSubKey")
      val secretsFuture = secrets.get(vaultKey, vaultSubKey).underlying
      val response = Await.result(secretsFuture, timeout)
      response match {
        case Ok(ok) => ok
        case Fail(fail) => new IllegalArgumentException(s"Got $fail from $address for $vaultKey $vaultSubKey")
      }
    }
  }
}

object Config {
  private val config = ConfigFactory.load()
  val vault = new VaultFallbackConfig(config)

  val appSecret = vault.getString("app_secret", "service/app", "password")

  // once config is initialized, close http to vault
  dispatch.Http.shutdown()
}

