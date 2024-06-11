package hr.fer.fercropmanager.alarms.service

import hr.fer.fercropmanager.alarms.api.AlarmDto
import hr.fer.fercropmanager.alarms.api.Data

sealed interface AlarmsListState {

    data object Loading : AlarmsListState
    data object Empty : AlarmsListState
    data class Available(val alarms: List<Alarm>) : AlarmsListState
}

sealed interface AlarmState {

    data object Loading : AlarmState
    data object Error : AlarmState
    data class Loaded(val alarm: Alarm, val isButtonLoading: Boolean = false) : AlarmState
}

data class Alarm(
    val id: String,
    val name: String,
    val originatorName: String,
    val type: AlarmType,
    val severity: AlarmSeverity,
    val status: AlarmStatus,
)

enum class AlarmType {
    HighTemperature,
    HighHumidity;

    companion object {
        fun getFromSerialName(serialName: String) = when (serialName) {
            "High Temperature" -> HighTemperature
            "High Humidity" -> HighHumidity
            else -> HighHumidity
        }
    }
}

enum class AlarmSeverity {
    Critical;

    companion object {
        fun getFromSerialName(serialName: String) = when (serialName) {
            "CRITICAL" -> Critical
            else -> Critical
        }
    }
}

 enum class AlarmStatus {
     ActiveUnack,
     ActiveAck,
     ClearedUnack,
     ClearedAck;

     companion object {
         fun getFromSerialName(serialName: String) = when (serialName) {
             "ACTIVE_UNACK" -> ActiveUnack
             "ACTIVE_ACK" -> ActiveAck
             "CLEARED_UNACK" -> ClearedUnack
             "CLEARED_ACK" -> ClearedAck
             else -> ClearedAck
         }
     }
 }

fun Data.toAlarm() = Alarm(
    id = id.id,
    name = name,
    originatorName = originatorName ?: "Unknown originator",
    status = AlarmStatus.getFromSerialName(status),
    severity = AlarmSeverity.getFromSerialName(severity),
    type = AlarmType.getFromSerialName(type),
)

fun AlarmDto.toAlarm() = Alarm(
    id = id.id,
    name = name,
    originatorName = "Unknown originator",
    status = AlarmStatus.getFromSerialName(status),
    severity = AlarmSeverity.getFromSerialName(severity),
    type = AlarmType.getFromSerialName(type),
)
