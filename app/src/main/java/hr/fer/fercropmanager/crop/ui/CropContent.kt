package hr.fer.fercropmanager.crop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.fer.fercropmanager.R
import hr.fer.fercropmanager.crop.ui.utils.WaterLevelIndicator
import hr.fer.fercropmanager.crop.ui.utils.formatFloat
import hr.fer.fercropmanager.crop.ui.utils.painter
import hr.fer.fercropmanager.crop.usecase.Crop
import hr.fer.fercropmanager.crop.usecase.CropState
import hr.fer.fercropmanager.snackbar.SnackbarManager
import hr.fer.fercropmanager.snackbar.ui.SnackbarHost
import hr.fer.fercropmanager.ui.common.ErrorContent
import hr.fer.fercropmanager.ui.common.LoadingContent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropContent(
    viewModel: CropViewModel = koinViewModel(),
    snackbarManager: SnackbarManager = koinInject(),
    onAlarmIconClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sprinklerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    SprinklerBottomSheet(
        sheetState = sprinklerSheetState,
        isVisible = state.isSprinkleBottomSheetVisible,
        onConfirm = {
            scope.launch {
                sprinklerSheetState.hide()
            }.invokeOnCompletion { viewModel.onInteraction(CropInteraction.ActivateSprinkler) }
        },
        onCancel = {
            scope.launch {
                sprinklerSheetState.hide()
            }.invokeOnCompletion { viewModel.onInteraction(CropInteraction.HideBottomSheet) }
        },
        onDismissRequest = { viewModel.onInteraction(CropInteraction.HideBottomSheet) },
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarManager = snackbarManager) },
        topBar = {
            CropHeader(
                name = state.cropState.userData.name,
                onAlarmsClick = onAlarmIconClick,
                onSettingsClick = { viewModel.onInteraction(CropInteraction.SettingsClick) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when (val cropState = state.cropState) {
                is CropState.Error -> ErrorContent(onRetry = { viewModel.onInteraction(CropInteraction.RetryClick) })
                is CropState.Loaded -> LoadedContent(
                    state = cropState,
                    selectedIndex = state.selectedIndex,
                    onTabChange = { index, id -> viewModel.onInteraction(CropInteraction.TabChange(index, id)) },
                    onStartWateringClick = { viewModel.onInteraction(CropInteraction.StartSprinklerClick) },
                )
            }
        }
    }
}

@Composable
private fun LoadedContent(
    state: CropState.Loaded,
    selectedIndex: Int,
    onTabChange: (Int, String) -> Unit,
    onStartWateringClick: () -> Unit,
) {
    when (state) {
        is CropState.Loaded.Available -> CropsTabRowContent(
            state = state,
            selectedIndex = selectedIndex,
            onTabChange = onTabChange,
            onStartWateringClick = onStartWateringClick,
        )
        is CropState.Loaded.Empty -> EmptyContent()
        is CropState.Loaded.Loading -> LoadingContent()
    }
}

@Composable
private fun CropsTabRowContent(
    state: CropState.Loaded.Available,
    selectedIndex: Int,
    onTabChange: (Int, String) -> Unit,
    onStartWateringClick: () -> Unit,
) {
    val tabs = state.crops.map { crop -> crop.cropName }
    val pagerState = rememberPagerState(initialPage = selectedIndex, pageCount = { tabs.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    LaunchedEffect(Unit, selectedIndex) {
        if (selectedIndex != selectedTabIndex) pagerState.animateScrollToPage(selectedIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != selectedIndex) {
            onTabChange(pagerState.currentPage, state.crops[pagerState.currentPage].id)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
            text = "Here is an overview of your crops.",
        )
        ScrollableTabRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
        ) {
            tabs.forEachIndexed { index, cropName ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabChange(index, state.crops[index].id) },
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = cropName,
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) { pageIndex ->
            CropDetails(
                crop = state.crops[pageIndex],
                isShortcutLoading = state.isShortcutLoading,
                onStartWateringClick = onStartWateringClick,
            )
        }
    }
}

@Composable
private fun CropDetails(crop: Crop, isShortcutLoading: Boolean, onStartWateringClick: () -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    modifier = Modifier.padding(start = 20.dp, bottom = 16.dp),
                    text = "Plants:",
                    style = MaterialTheme.typography.titleLarge,
                )
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    crop.plants.forEach { plant ->
                        Surface(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            modifier = Modifier.padding(horizontal = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Image(
                                    modifier = Modifier.size(64.dp),
                                    painter = plant.painter,
                                    contentDescription = "Plant Icon"
                                )
                                Text(text = plant.name)
                            }
                        }
                    }
                }
            }
        }
        with(crop) {
            if (soilMoisture == null && temperature == null && humidity == null) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        crop.soilMoisture?.let { moisture ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = "Soil moisture: ${moisture.toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    WaterLevelIndicator(
                        currentValue = moisture.toInt(),
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = isShortcutLoading,
                        isWateringInProgress = crop.isWateringInProgress,
                        onStartWateringClick = onStartWateringClick,
                    )
                }
            }
        }
        crop.temperature?.let { temperature ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                    Text(
                        text = "Temperature: ${temperature.formatFloat()}°C",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    val fillIndicatorData = FillIndicatorData(
                        unit = "°C",
                        current = temperature,
                        min = 8.0f,
                        max = 35.0f
                    )
                    FillIndicator(fillIndicatorData = fillIndicatorData)
                }
            }
        }
        crop.humidity?.let { humidity ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                    Text(
                        text = "Humidity: ${humidity.formatFloat()}%",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    val fillIndicatorData = FillIndicatorData(
                        unit = "%",
                        current = humidity,
                        min = 0.0f,
                        max = 99.0f
                    )
                    FillIndicator(fillIndicatorData = fillIndicatorData)
                }
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                Text(
                    text = "Wind direction: ${crop.wind.direction}",
                    style = MaterialTheme.typography.titleLarge,
                )
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_wind), contentDescription = "Wind")
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "${crop.wind.speed.toInt()} km/h",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropHeader(name: String, onAlarmsClick: () -> Unit, onSettingsClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Welcome $name!") },
        actions = {
            Icon(
                modifier = Modifier.clickable(onClick = onAlarmsClick),
                imageVector = Icons.Default.Notifications,
                contentDescription = "Alarms",
            )
            Icon(
                modifier = Modifier
                    .clickable(onClick = onSettingsClick)
                    .padding(horizontal = 12.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
            )
        }
    )
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        // TODO: Add Empty icon
        Text(
            text = "You have no crops at the moment. Contact support to help you create one!",
            textAlign = TextAlign.Center,
        )
    }
}
