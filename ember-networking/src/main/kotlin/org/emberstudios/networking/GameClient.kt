package org.emberstudios.networking

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.emberstudios.core.logger.getLogger

class GameClient(
	private val tcpHost: String = "127.0.0.1",
	private val tcpPort: Int = 9115,
	private val udpHost: String = "127.0.0.1",
	private val udpPort: Int = 9125
) {

	companion object {
		val LOGGER = getLogger<GameClient>()
	}

	private val selectorManager = SelectorManager(Dispatchers.IO)

	private var tcpSocket: Socket? = null
	private var tcpReadChannel: ByteReadChannel? = null
	private var tcpWriteChannel: ByteWriteChannel? = null
	private var udpSocket: BoundDatagramSocket? = null

	suspend fun connect() = withContext(Dispatchers.IO) {
		launch {
			tcpSocket = aSocket(selectorManager).tcp().connect(tcpHost, tcpPort)
			tcpReadChannel = tcpSocket?.openReadChannel()
			tcpWriteChannel = tcpSocket?.openWriteChannel(autoFlush = true)
			LOGGER.info { "Connected to TCP server at $tcpHost:$tcpPort" }

			launch {
				try {
					tcpReadChannel?.let { channel ->
						while (true) {
							val line = channel.readUTF8Line() ?: break
							LOGGER.info { "TCP server says: $line" }
						}
					}
				} catch (e: Exception) {
					LOGGER.error { "Error reading from TCP: ${e.message}" }
				}
			}

			udpSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", 0))
			LOGGER.info { "UDP client bound to local address ${udpSocket?.localAddress}" }

			launch {
				try {
					while (true) {
						val datagram = udpSocket?.receive()
						val receivedText = datagram?.packet?.readText()
						LOGGER.info { "UDP server says: $receivedText" }
					}
				} catch (e: Exception) {
					LOGGER.error { "Error reading from UDP: ${e.message}" }
				}
			}
		}
	}

	suspend fun disconnect() = withContext(Dispatchers.IO) {
		tcpWriteChannel?.writeStringUtf8("DISCONNECT\n")
		tcpSocket?.close()
		udpSocket?.close()
		LOGGER.info { "Disconnected from server." }
	}

	suspend fun sendMessage(message: String) = withContext(Dispatchers.IO) {
		try {
			tcpWriteChannel?.writeStringUtf8("$message\n")
			LOGGER.info { "Sent message: $message" }
		} catch (e: Exception) {
			LOGGER.error { "Error sending to TCP: ${e.message} " }
		}
	}

	suspend fun sendTransform(netTransform: NetTransform) = withContext(Dispatchers.IO) {
		val jsonString = Json.encodeToString(netTransform)
		val packet = buildPacket { writeFully(jsonString.encodeToByteArray()) }
		udpSocket?.send(Datagram(packet, InetSocketAddress(udpHost, udpPort)))
		LOGGER.info { "Sent transform: $jsonString" }
	}

}