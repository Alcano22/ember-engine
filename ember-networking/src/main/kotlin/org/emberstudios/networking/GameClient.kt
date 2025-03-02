package org.emberstudios.networking

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.emberstudios.core.logger.getLogger

class GameClient(
	val hostAddress: String = "127.0.0.1",
	val tcpPort: Int = 9115,
	val udpPort: Int = 9125
) {

	companion object {
		val LOGGER = getLogger<GameClient>()
	}

	var traceMessages = false

	private val selectorManager = SelectorManager(Dispatchers.IO)

	private var tcpSocket: Socket? = null
	private var tcpReadChannel: ByteReadChannel? = null
	private var tcpWriteChannel: ByteWriteChannel? = null
	private var udpSocket: BoundDatagramSocket? = null

	private var connectTime = 0L
	val connectionDuration get() = (System.currentTimeMillis() - connectTime).toFloat() / 1000f

	var isConnected = false
		private set

	private var pendingPing: CompletableDeferred<Long>? = null

	suspend fun connect(onConnected: suspend (Boolean) -> Unit = {}) = withContext(Dispatchers.IO) {
		try {
			tcpSocket = aSocket(selectorManager).tcp().connect(hostAddress, tcpPort)
			tcpReadChannel = tcpSocket?.openReadChannel()
			tcpWriteChannel = tcpSocket?.openWriteChannel(autoFlush = true)
			LOGGER.info { "Connected to TCP server at $hostAddress:$tcpPort" }

			udpSocket = aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", 0))
			LOGGER.info { "UDP client bound to local address ${udpSocket?.localAddress}" }

			isConnected = true
			onConnected(true)

			connectTime = System.currentTimeMillis()

			launch {
				try {
					tcpReadChannel?.let { channel ->
						while (true) {
							val line = channel.readUTF8Line()?.trim() ?: break
							if (line.startsWith("PONG:")) {
								val timestamp = line.removePrefix("PONG:").trim().toLongOrNull()
								if (timestamp != null) {
									val latency = System.currentTimeMillis() - timestamp
									pendingPing?.complete(latency)
									pendingPing = null
								}
							} else {
								traceMessage("TCP server says: $line")
							}
						}
					}
				} catch (e: CancellationException) {
					throw e
				} catch (e: Exception) {
					LOGGER.error { "Error reading from TCP: ${e.message}" }
				}
			}

			launch {
				try {
					while (true) {
						val datagram = udpSocket?.receive()
						val receivedText = datagram?.packet?.readText()
						traceMessage("UDP server says: $receivedText")
					}
				} catch (e: CancellationException) {
					throw e
				} catch (e: Exception) {
					LOGGER.error { "Error reading from UDP: ${e.message}" }
				}
			}
		} catch (e: Exception) {
			LOGGER.error { "Error connecting to server: ${e.message}" }
			onConnected(false)
		}
	}

	suspend fun disconnect() = withContext(Dispatchers.IO) {
		tcpWriteChannel?.writeStringUtf8("DISCONNECT\n")
		tcpSocket?.close()
		udpSocket?.close()
		LOGGER.info { "Disconnected from server." }
		isConnected = false
	}

	suspend fun sendMessage(message: String) = withContext(Dispatchers.IO) {
		try {
			tcpWriteChannel?.writeStringUtf8("$message\n")
			traceMessage("Sent message: $message")
		} catch (e: Exception) {
			LOGGER.error { "Error sending to TCP: ${e.message} " }
		}
	}

	suspend fun sendTransform(netTransform: NetTransform) = withContext(Dispatchers.IO) {
		val jsonString = Json.encodeToString(netTransform)
		val packet = buildPacket { writeFully(jsonString.encodeToByteArray()) }
		udpSocket?.send(Datagram(packet, InetSocketAddress(hostAddress, udpPort)))
		LOGGER.trace { "Sent transform: $jsonString" }
	}

	suspend fun measureLatency(): Long = withContext(Dispatchers.IO) {
		val deferred = CompletableDeferred<Long>()
		pendingPing = deferred
		val now = System.currentTimeMillis()
		sendMessage("PING:$now")
		deferred.await()
	}

	private fun traceMessage(message: String) {
		if (traceMessages)
			LOGGER.trace { message }
	}

}