package hr.fer.fercropmanager.device.websocket

import hr.fer.fercropmanager.network.DEVICE_ENTITY_TYPE
import hr.fer.fercropmanager.network.HOST
import hr.fer.fercropmanager.network.LATEST_TELEMETRY_SCOPE
import hr.fer.fercropmanager.network.PORT
import hr.fer.fercropmanager.network.WEB_SOCKET_PATH
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class DeviceWebSocketImpl(private val httpClient: HttpClient) : DeviceWebSocket {

    private val subscriptionIdToDeviceMap = mutableMapOf<Int, String>()

    override suspend fun connectWebSocket(
        token: String,
        entityIdList: List<String>,
        onDataReceived: (String?) -> Unit
    ) {
        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = HOST,
                port = PORT,
                path = "$WEB_SOCKET_PATH$token",
            ) {
                entityIdList.forEachIndexed { index, deviceId ->
                    val subscriptionMessage = createSubscriptionMessage(deviceId, index)
                    send(Frame.Text(subscriptionMessage))
                }

                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Text -> onDataReceived(frame.readText())
                        is Frame.Binary,
                        is Frame.Close,
                        is Frame.Ping,
                        is Frame.Pong -> Unit
                    }
                }
            }
        } catch (e: Exception) {
            e.fillInStackTrace()
            onDataReceived(null)
        } finally {
            httpClient.close()
        }
    }

    override fun retrieveDeviceId(subscriptionId: Int) =
        subscriptionIdToDeviceMap.getOrDefault(subscriptionId, "0")

    private fun createSubscriptionMessage(deviceId: String, subscriptionId: Int): String {
        subscriptionIdToDeviceMap[subscriptionId] = deviceId
        val json = buildJsonObject {
            put("tsSubCmds", buildJsonArray {
                add(
                    buildJsonObject {
                        put("entityType", DEVICE_ENTITY_TYPE)
                        put("entityId", deviceId)
                        put("scope", LATEST_TELEMETRY_SCOPE)
                        put("cmdId", subscriptionId)
                    }
                )
            })
            put("historyCmds", buildJsonArray {  })
            put("attrSubCmds", buildJsonArray {  })
        }
        return json.toString()
    }
}
