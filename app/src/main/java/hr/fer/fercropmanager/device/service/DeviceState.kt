package hr.fer.fercropmanager.device.service

import hr.fer.fercropmanager.device.api.Data
import hr.fer.fercropmanager.device.api.DeviceDataDto
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

sealed interface DeviceState {

    data object Initial : DeviceState
    data object Loading : DeviceState
    data object Error : DeviceState

    sealed interface Loaded : DeviceState {
        data object Empty : Loaded
        data class Available(val devices: List<Device>) : Loaded
    }
}

data class Device(
    val id: String,
    val name: String,
    val type: String,
)

data class DeviceValues(
    val humidity: Float? = null,
    val temperature: Float? = null,
    val moisture: Float? = null,
)

fun Data.toDevice() = Device(
    id = id.id,
    name = name,
    type = type,
)

fun DeviceDataDto.toDeviceValues() = DeviceValues(
    humidity = data.humidity.floatValue,
    temperature = data.temperature.floatValue,
    moisture = data.moisture.floatValue,
)

private val List<List<JsonElement>>?.floatValue: Float?
    get() = this?.firstOrNull()?.getOrNull(1)?.jsonPrimitive?.contentOrNull?.toFloat()
