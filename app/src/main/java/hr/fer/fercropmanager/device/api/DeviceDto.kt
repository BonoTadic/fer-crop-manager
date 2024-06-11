package hr.fer.fercropmanager.device.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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
    val humidity: List<List<JsonElement>>? = null,
    val temperature: List<List<JsonElement>>? = null,
    val moisture: List<List<JsonElement>>? = null,
    val isWateringInProgress: Boolean = false,
)

@Serializable
data class LatestValuesDto(
    val humidity: Long? = null,
    val temperature: Long? = null,
    val moisture: Long? = null,
    val isWateringInProgress: Boolean = false,
)
