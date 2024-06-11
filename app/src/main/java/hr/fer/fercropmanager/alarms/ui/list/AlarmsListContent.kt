package hr.fer.fercropmanager.alarms.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.fer.fercropmanager.alarms.service.Alarm
import hr.fer.fercropmanager.alarms.service.AlarmsListState
import hr.fer.fercropmanager.ui.common.LoadingContent
import hr.fer.fercropmanager.ui.common.TopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AlarmsListContent(
    viewModel: AlarmsListViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onAlarmClick: (String) -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = { TopBar(title = "Alarms", onBackClick = onBackClick) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (state) {
                is AlarmsListState.Available -> AlarmsAvailableContent(
                    alarms = state.alarms,
                    onAlarmClick = {
                        viewModel.onInteraction(AlarmsListInteraction.AlarmClick(it))
                        onAlarmClick(it)
                    },
                )
                AlarmsListState.Empty -> AlarmsEmptyContent()
                AlarmsListState.Loading -> LoadingContent()
            }
        }
    }
}

@Composable
private fun AlarmsAvailableContent(alarms: List<Alarm>, onAlarmClick: (String) -> Unit) {
    LazyColumn {
        items(alarms) { alarm ->
            AlarmItem(alarm = alarm, onAlarmClick = onAlarmClick)
        }
    }
}

@Composable
private fun AlarmsEmptyContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // TODO Add Empty State image
        Text(text = "Good news! No alarms.")
    }
}