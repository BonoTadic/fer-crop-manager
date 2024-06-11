package hr.fer.fercropmanager.alarms.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.fer.fercropmanager.alarms.service.Alarm
import hr.fer.fercropmanager.alarms.service.AlarmSeverity
import hr.fer.fercropmanager.alarms.service.AlarmStatus
import hr.fer.fercropmanager.alarms.service.AlarmType
import hr.fer.fercropmanager.alarms.ui.utils.color
import hr.fer.fercropmanager.alarms.ui.utils.iconRes
import hr.fer.fercropmanager.alarms.ui.utils.isUnacknowledged
import hr.fer.fercropmanager.alarms.ui.utils.statusColor
import hr.fer.fercropmanager.alarms.ui.utils.statusLabel

@Composable
fun AlarmItem(alarm: Alarm, onAlarmClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onAlarmClick(alarm.id) },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = alarm.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (alarm.isUnacknowledged) FontWeight.Bold else null,
                )
                if (alarm.isUnacknowledged) {
                    Text(
                        text = " * ",
                        color = Color.Red,
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Triggered by: ${alarm.originatorName}",
                    )
                    Row {
                        Text(text = "Severity: ")
                        Text(
                            text = alarm.severity.name,
                            color = alarm.severity.color,
                        )
                    }
                    Row {
                        Text(text = "Status: ")
                        Text(
                            text = alarm.statusLabel,
                            color = alarm.statusColor,
                        )
                    }
                }
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = alarm.type.iconRes),
                    contentDescription = "Alarm Icon",
                )
            }
        }
    }
}

@Preview
@Composable
fun AlarmItemPreview() {
    val alarm = Alarm(
        id = "alarm_id",
        name = "High Temperature alarm",
        originatorName = "Device 1",
        severity = AlarmSeverity.Critical,
        status = AlarmStatus.ActiveUnack,
        type = AlarmType.HighTemperature,
    )
    AlarmItem(alarm, onAlarmClick = {})
}

@Preview
@Composable
fun AlarmItemLoadingPreview() {
    val alarm = Alarm(
        id = "alarm_id",
        name = "High Humidity alarm",
        originatorName = "Device 2",
        severity = AlarmSeverity.Critical,
        status = AlarmStatus.ActiveUnack,
        type = AlarmType.HighHumidity,
    )
    AlarmItem(alarm, onAlarmClick = {})
}

@Preview
@Composable
fun AlarmItemAckPreview() {
    val alarm = Alarm(
        id = "alarm_id",
        name = "High Humidity alarm",
        originatorName = "Device 2",
        severity = AlarmSeverity.Critical,
        status = AlarmStatus.ActiveAck,
        type = AlarmType.HighHumidity,
    )
    AlarmItem(alarm, onAlarmClick = {})
}

@Preview
@Composable
fun AlarmItemClearedPreview() {
    val alarm = Alarm(
        id = "alarm_id",
        name = "High Humidity alarm",
        originatorName = "Device 2",
        severity = AlarmSeverity.Critical,
        status = AlarmStatus.ClearedAck,
        type = AlarmType.HighHumidity,
    )
    AlarmItem(alarm, onAlarmClick = {})
}