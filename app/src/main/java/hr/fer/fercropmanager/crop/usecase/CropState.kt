package hr.fer.fercropmanager.crop.usecase

import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.crop.ui.plants.Plant
import hr.fer.fercropmanager.device.service.Device
import hr.fer.fercropmanager.device.service.DeviceValues

sealed interface CropState {

    val userData: UserData

    data class Error(override val userData: UserData) : CropState

    sealed interface Loaded : CropState {
        data class Loading(override val userData: UserData) : Loaded
        data class Empty(override val userData: UserData) : Loaded
        data class Available(
            override val userData: UserData,
            val crops: List<Crop>,
            val isShortcutLoading: Boolean = false,
        ) : Loaded
    }
}

data class Crop(
    val id: String,
    val cropName: String,
    val soilMoisture: Float?,
    val temperature: Float?,
    val humidity: Float?,
    val wind: Wind,
    val isWateringInProgress: Boolean,
    val plants: List<Plant>,
)

data class Wind(val direction: WindDirection, val speed: Float)

data class UserData(val name: String, val email: String)

enum class WindDirection {
    North, South, East, West,
}

fun AuthState.Success.toUserData() = UserData(name = name, email = email)

fun Device.toCrop(deviceValues: DeviceValues?, isWateringInProgress: Boolean, plantsMap: Map<String, List<Plant>>) = Crop(
    id = id,
    cropName = name,
    soilMoisture = deviceValues?.moisture,
    temperature = deviceValues?.temperature,
    humidity = deviceValues?.humidity,
    wind = Wind(
        direction = WindDirection.East,
        speed = 6f,
    ),
    isWateringInProgress = isWateringInProgress,
    plants = plantsMap[id] ?: emptyList(),
)
