package hr.fer.fercropmanager.device.api

import hr.fer.fercropmanager.network.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.delay

private const val DEVICES_URL = "api/customer"

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

    override suspend fun startActuation(token: String, entityId: String, targetValue: String): Result<Unit> {
        return try {
            // TODO Implement actuation
            delay(5000L)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }
}
