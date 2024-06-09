package hr.fer.fercropmanager.alarms.api

import hr.fer.fercropmanager.network.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

private const val ALARM_URL = "api/alarm"

class AlarmsApiImpl(private val httpClient: HttpClient) : AlarmsApi {

    override suspend fun getAlarm(token: String, alarmId: String): Result<AlarmDto> {
        return try {
            val response: AlarmDto = httpClient.get("$BASE_URL$ALARM_URL/$alarmId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }

    override suspend fun getAlarms(token: String, entityType: String, entityId: String): Result<AlarmsDto> {
        return try {
            val response: AlarmsDto = httpClient.get("$BASE_URL$ALARM_URL/$entityType/$entityId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url {
                    parameters.append("pageSize", "10")
                    parameters.append("page", "0")
                    parameters.append("sortProperty", "createdTime")
                    parameters.append("sortOrder", "DESC")
                }
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.fillInStackTrace())
        }
    }
}