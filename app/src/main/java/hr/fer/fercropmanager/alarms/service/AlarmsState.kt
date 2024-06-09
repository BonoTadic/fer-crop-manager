package hr.fer.fercropmanager.alarms.service

import hr.fer.fercropmanager.alarms.api.Data
import hr.fer.fercropmanager.device.service.Device

sealed interface AlarmsState {

    data object Empty : AlarmsState
    data class Available(val alarms: List<Alarm>) : AlarmsState
}

data class Alarm(
    val id: String,
    val name: String,
    val originatorName: String,
    val type: AlarmType,
    val severity: AlarmSeverity,
    val status: AlarmStatus,
)

enum class AlarmType(val label: String) {
    HighTemperature("High Temperature"),
    HighHumidity("High Humidity");

    companion object {
        fun getFromSerialName(serialName: String) = when (serialName) {
            "High Temperature" -> HighTemperature
            "High Humidity" -> HighHumidity
            else -> HighHumidity
        }
    }
}

enum class AlarmSeverity(val label: String) {
    Critical("CRITICAL");

    companion object {
        fun getFromSerialName(serialName: String) = when (serialName) {
            "CRITICAL" -> Critical
            else -> Critical
        }
    }
}

 enum class AlarmStatus(val label: String) {
     ActiveUnack("ACTIVE_UNACK"),
     ActiveAck("ACTIVE_ACK"),
     ClearedUnack("CLEARED_UNACK"),
     ClearedAck("CLEARED_ACK");

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

fun Data.toAlarm(devices: List<Device>) = Alarm(
    id = id.id,
    name = name,
    originatorName = devices.first { it.id == originator.id }.name,
    status = AlarmStatus.getFromSerialName(status),
    severity = AlarmSeverity.getFromSerialName(severity),
    type = AlarmType.getFromSerialName(type),
)