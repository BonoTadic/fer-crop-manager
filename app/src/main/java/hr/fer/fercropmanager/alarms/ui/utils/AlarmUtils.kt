package hr.fer.fercropmanager.alarms.ui.utils

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.composables.materialcolors.MaterialColors
import hr.fer.fercropmanager.R
import hr.fer.fercropmanager.alarms.service.Alarm
import hr.fer.fercropmanager.alarms.service.AlarmSeverity
import hr.fer.fercropmanager.alarms.service.AlarmStatus
import hr.fer.fercropmanager.alarms.service.AlarmType

val Alarm.statusLabel: String
    get() = when (status) {
        AlarmStatus.ActiveAck,
        AlarmStatus.ActiveUnack -> "Active"
        AlarmStatus.ClearedAck,
        AlarmStatus.ClearedUnack -> "Cleared"
    }

val Alarm.statusColor: Color
    get() = when (status) {
        AlarmStatus.ClearedUnack,
        AlarmStatus.ClearedAck -> MaterialColors.Green.`700`
        AlarmStatus.ActiveAck,
        AlarmStatus.ActiveUnack -> MaterialColors.Orange.`700`
    }

val Alarm.isActive: Boolean
    get() = when (status) {
        AlarmStatus.ActiveAck,
        AlarmStatus.ActiveUnack -> true
        AlarmStatus.ClearedAck,
        AlarmStatus.ClearedUnack -> false
    }

val Alarm.isUnacknowledged: Boolean
    get() = when (status) {
        AlarmStatus.ActiveUnack,
        AlarmStatus.ClearedUnack -> true
        AlarmStatus.ActiveAck,
        AlarmStatus.ClearedAck -> false
    }

val AlarmType.iconRes: Int
    @DrawableRes
    get() = when (this) {
        AlarmType.HighTemperature -> R.drawable.ic_temperature
        AlarmType.HighHumidity -> R.drawable.ic_humidity
    }

val AlarmSeverity.color: Color
    @ReadOnlyComposable
    @Composable
    get() = when (this) {
        AlarmSeverity.Critical -> Color.Red
    }