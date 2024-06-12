package hr.fer.fercropmanager.device.api

import hr.fer.fercropmanager.network.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

private const val DEVICES_URL = "api/customer"
private const val RPC_URL = "api/rpc/oneway"
private const val SPRAY_RPC_CMD = "spray"
private const val LED_RPC_CMD = "setLedStatus"

class DeviceApiImpl(private val httpClient: HttpClient) : DeviceApi {

    override suspend fun getDevices(token: String, customerId: String): Result<DeviceDto> {
        return try {
            val response: DeviceDto = httpClient.get("$BASE_URL$DEVICES_URL/$customerId/devices") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url {
                    parameters.append("pageSize", "10")
                    parameters.append("page", "0")
                }
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }

    override suspend fun activateSprinkler(token: String, entityId: String): Result<Unit> {
        return try {
            httpClient.post("$BASE_URL$RPC_URL/$entityId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                setBody(SprayRpcRequest(method = SPRAY_RPC_CMD))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }

    override suspend fun setLedStatus(token: String, entityId: String, targetValue: Int): Result<Unit> {
        return try {
            httpClient.post("$BASE_URL$RPC_URL/$entityId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                setBody(LedRpcRequest(method = LED_RPC_CMD, params = targetValue))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }
}
