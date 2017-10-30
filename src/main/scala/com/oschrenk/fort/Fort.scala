package com.oschrenk.delight

import com.typesafe.config.ConfigFactory

object Fort {

  def main(args: Array[String]): Unit = {
    println("Hello World")
    println(Config.vault)
  }

}

object Config {

  private val config = ConfigFactory.load()

  val vault = config.getString("vault")
  val roleId = config.getString("role_id")
  val secretId = config.getString("secret_id")

}
