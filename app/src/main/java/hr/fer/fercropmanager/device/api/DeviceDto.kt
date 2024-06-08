package hr.fer.fercropmanager.device.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class DeviceDto(
    val data: List<Data>,
    val hasNext: Boolean,
    val totalElements: Int,
    val totalPages: Int
)

@Serializable
data class Data(
    val additionalInfo: AdditionalInfo,
    val createdTime: Long,
    val customerId: CustomerId,
    val deviceData: DeviceData,
    val deviceProfileId: DeviceProfileId,
    val id: Id,
    val label: String,
    val name: String,
    val tenantId: TenantId,
    val type: String,
    val externalId: String? = null,
    val firmwareId: String? = null,
    val softwareId: String? = null,
)

@Serializable
data class AdditionalInfo(
    val description: String,
    val gateway: Boolean,
    val overwriteActivityTime: Boolean,
)

@Serializable
data class CustomerId(
    val entityType: String,
    val id: String,
)

@Serializable
data class DeviceData(
    val configuration: Configuration,
    val transportConfiguration: TransportConfiguration,
)

@Serializable
data class DeviceProfileId(
    val entityType: String,
    val id: String,
)

@Serializable
data class Id(
    val entityType: String,
    val id: String,
)

@Serializable
data class TenantId(
    val entityType: String,
    val id: String,
)

@Serializable
data class Configuration(val type: String)

@Serializable
data class TransportConfiguration(val type: String)

@Serializable
data class DeviceDataDto(
    val data: DeviceValuesDto,
    val subscriptionId: Int,
    val errorCode: Int,
    val latestValues: LatestValuesDto,
    val errorMsg: String? = null,
)

@Serializable
data class DeviceValuesDto(
    val humidity: List<List<JsonElement>>,
    val temperature: List<List<JsonElement>>,
    val moisture: List<List<JsonElement>>? = null,
    val isWateringInProgress: Boolean = false,
)

@Serializable
data class LatestValuesDto(
    val humidity: Long,
    val temperature: Long,
    val moisture: Long = 0L,
    val isWateringInProgress: Boolean = false,
)

data class DeviceValues(
    val humidity: Float,
    val temperature: Float,
    val moisture: Float,
    val isWateringInProgress: Boolean,
)

fun DeviceDataDto.toDeviceValues() = DeviceValues(
    humidity = data.humidity.floatValue,
    temperature = data.temperature.floatValue,
    moisture = 17f,
    isWateringInProgress = data.isWateringInProgress,
)

private val List<List<JsonElement>>.floatValue: Float
    get() = firstOrNull()?.getOrNull(1)?.jsonPrimitive?.contentOrNull?.toFloat() ?: 0.0f
