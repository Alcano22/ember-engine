package org.emberstudios.networking.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.emberstudios.core.logger.getLogger
import java.io.File

typealias YamlProperty = JsonProperty

class ServerConfig(
	@YamlProperty("tcp_port") val tcpPort: Int = 9115,
	@YamlProperty("udp_port") val udpPort: Int = 9125
)

fun loadConfig(): ServerConfig {
	val logger = getLogger<ServerConfig>()

	val configFile = File("config.yml")
	val mapper = ObjectMapper(YAMLFactory())

	return if (!configFile.exists()) {
		val defaultConfig = ServerConfig()
		mapper.writeValue(configFile, defaultConfig)
		logger.info { "Created default config.yml with" }
		defaultConfig
	} else
		mapper.readValue(configFile, ServerConfig::class.java)
}
