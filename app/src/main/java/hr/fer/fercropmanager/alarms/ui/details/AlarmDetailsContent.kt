package hr.fer.fercropmanager.alarms.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.fer.fercropmanager.R
import hr.fer.fercropmanager.alarms.service.Alarm
import hr.fer.fercropmanager.alarms.service.AlarmState
import hr.fer.fercropmanager.alarms.ui.utils.iconRes
import hr.fer.fercropmanager.alarms.ui.utils.isActive
import hr.fer.fercropmanager.alarms.ui.utils.statusColor
import hr.fer.fercropmanager.alarms.ui.utils.statusLabel
import hr.fer.fercropmanager.ui.common.ErrorContent
import hr.fer.fercropmanager.ui.common.LoadingContent
import hr.fer.fercropmanager.ui.common.TopBar
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AlarmDetailsContent(
    alarmId: String,
    viewModel: AlarmDetailsViewModel = koinViewModel { parametersOf(alarmId) },
    onBackClick: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = { TopBar(title = "Alarm details", onBackClick = onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (state) {
                AlarmState.Error -> ErrorContent(
                    onRetry = { viewModel.onInteraction(AlarmDetailsInteraction.RetryClick) },
                )
                is AlarmState.Loaded -> LoadedContent(
                    alarm = state.alarm,
                    isButtonLoading = state.isButtonLoading,
                    onClear = { viewModel.onInteraction(AlarmDetailsInteraction.ClearAlarm(it)) },
                )
                AlarmState.Loading -> LoadingContent()
            }
        }
    }
}

@Composable
private fun LoadedContent(alarm: Alarm, isButtonLoading: Boolean, onClear: (String) -> Unit) {
    Card(modifier = Modifier.padding(horizontal = 12.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Image(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                painter = painterResource(id = R.drawable.ic_alarm),
                contentDescription = "Alarm Icon",
            )
            Text(text = "Name: ${alarm.name}")
            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(text = "Type: ${alarm.type}")
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = alarm.type.iconRes),
                    contentDescription = "Alarm Type Icon",
                )
            }
            Row {
                Text(text = "Status: ")
                Text(
                    text = alarm.statusLabel,
                    color = alarm.statusColor,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .heightIn(min = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (alarm.isActive) {
                    Button(
                        modifier = Modifier.sizeIn(minHeight = 24.dp),
                        onClick = { onClear(alarm.id) },
                    ) {
                        if (isButtonLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                            )
                        } else {
                            Text(text = "Clear")
                        }
                    }
                } else {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.ic_checkmark_green),
                        contentDescription = "All Good Checkmark",
                    )
                }
            }
        }
    }
}