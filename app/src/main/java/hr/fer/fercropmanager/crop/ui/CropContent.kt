package hr.fer.fercropmanager.crop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import hr.fer.fercropmanager.crop.ui.plants.Plant
import hr.fer.fercropmanager.crop.ui.plants.PlantsDialog
import hr.fer.fercropmanager.crop.ui.utils.FillIndicator
import hr.fer.fercropmanager.crop.ui.utils.WaterLevelIndicator
import hr.fer.fercropmanager.crop.ui.utils.formatFloat
import hr.fer.fercropmanager.crop.ui.plants.painter
import hr.fer.fercropmanager.crop.usecase.Crop
import hr.fer.fercropmanager.crop.usecase.CropState
import hr.fer.fercropmanager.crop.usecase.Wind
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
    onInfoClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sprinklerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val ledSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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

    LedBottomSheet(
        sheetState = ledSheetState,
        isVisible = state.isLedBottomSheetVisible,
        isChecked = state.isLedEnabled,
        onCheckedChange = { viewModel.onInteraction(CropInteraction.OnCheckedChange(it)) },
        onConfirm = {
            scope.launch {
                ledSheetState.hide()
            }.invokeOnCompletion { viewModel.onInteraction(CropInteraction.LedStateChangeConfirm) }
        },
        onCancel = {
            scope.launch {
                ledSheetState.hide()
            }.invokeOnCompletion { viewModel.onInteraction(CropInteraction.HideBottomSheet) }
        },
        onDismissRequest = { viewModel.onInteraction(CropInteraction.HideBottomSheet) },
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarManager = snackbarManager) },
        topBar = {
            if (state.cropState is CropState.Loaded.Available) {
                CropHeader(
                    name = state.cropState.userData.name,
                    onAlarmsClick = onAlarmIconClick,
                    onInfoClick = onInfoClick,
                )
            }
        },
        floatingActionButton = {
            when (val cropState = state.cropState) {
                is CropState.Error,
                is CropState.Loaded.Empty,
                is CropState.Loaded.Loading -> Unit
                is CropState.Loaded.Available -> if (cropState.isLedButtonVisible) {
                    CropFloatingActionButton(
                        isLoading = cropState.isLedButtonLoading,
                        onClick = { viewModel.onInteraction(CropInteraction.LightButtonClick) },
                    )
                }
            }
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
                    isDialogVisible = state.isPlantsDialogVisible,
                    onTabChange = { index, id -> viewModel.onInteraction(CropInteraction.TabChange(index, id)) },
                    onSprinklerClick = { viewModel.onInteraction(CropInteraction.SprinklerClick) },
                    onPlantsSettingsClick = { viewModel.onInteraction(CropInteraction.PlantsSettingsClick) },
                    onConfirm = { viewModel.onInteraction(CropInteraction.PlantsDialogConfirm(it)) },
                    onCancel = { viewModel.onInteraction(CropInteraction.PlantsDialogClose) },
                )
            }
        }
    }
}

@Composable
private fun LoadedContent(
    state: CropState.Loaded,
    selectedIndex: Int,
    isDialogVisible: Boolean,
    onTabChange: (Int, String) -> Unit,
    onSprinklerClick: () -> Unit,
    onPlantsSettingsClick: () -> Unit,
    onConfirm: (List<Plant>) -> Unit,
    onCancel: () -> Unit,
) {
    when (state) {
        is CropState.Loaded.Available -> CropsTabRowContent(
            state = state,
            selectedIndex = selectedIndex,
            isDialogVisible = isDialogVisible,
            onTabChange = onTabChange,
            onSprinklerClick = onSprinklerClick,
            onPlantsSettingsClick = onPlantsSettingsClick,
            onConfirm = onConfirm,
            onCancel = onCancel,
        )
        is CropState.Loaded.Empty -> EmptyContent()
        is CropState.Loaded.Loading -> LoadingContent()
    }
}

@Composable
private fun CropsTabRowContent(
    state: CropState.Loaded.Available,
    selectedIndex: Int,
    isDialogVisible: Boolean,
    onTabChange: (Int, String) -> Unit,
    onSprinklerClick: () -> Unit,
    onPlantsSettingsClick: () -> Unit,
    onConfirm: (List<Plant>) -> Unit,
    onCancel: () -> Unit,
) {
    val tabs = state.crops.map { crop -> crop.cropName }
    val pagerState = rememberPagerState(initialPage = selectedIndex, pageCount = { tabs.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    LaunchedEffect(Unit) {
        onTabChange(selectedTabIndex, state.crops[selectedTabIndex].id)
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex != selectedTabIndex) pagerState.animateScrollToPage(selectedIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != selectedIndex) {
            onTabChange(pagerState.currentPage, state.crops[pagerState.currentPage].id)
        }
    }

    if (isDialogVisible) {
        PlantsDialog(
            currentPlants = state.crops[selectedTabIndex].plants,
            onConfirm = onConfirm,
            onCancel = onCancel,
        )
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
                isShortcutLoading = state.crops[pageIndex].isWateringButtonLoading,
                onSprinklerClick = onSprinklerClick,
                onPlantsSettingsClick = onPlantsSettingsClick,
            )
        }
    }
}

@Composable
private fun CropDetails(
    crop: Crop,
    isShortcutLoading: Boolean,
    onSprinklerClick: () -> Unit,
    onPlantsSettingsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PlantsCard(plants = crop.plants, onPlantsSettingsClick = onPlantsSettingsClick)
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
            MoistureCard(
                moisture = moisture,
                isWateringInProgress = crop.isWateringInProgress,
                isShortcutLoading = isShortcutLoading,
                onSprinklerClick = onSprinklerClick,
            )
        }
        crop.temperature?.let { temperature ->
            TemperatureCard(temperature = temperature)
        }
        crop.humidity?.let { humidity ->
            HumidityCard(humidity)
        }
        WindCard(wind = crop.wind)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlantsCard(plants: List<Plant>, onPlantsSettingsClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier
                    .padding(top = 16.dp, end = 12.dp)
                    .size(20.dp)
                    .clickable(onClick = onPlantsSettingsClick)
                    .align(Alignment.TopEnd),
                painter = painterResource(id = R.drawable.ic_adjust),
                contentDescription = "Adjust Plants Icon",
            )
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    modifier = Modifier.padding(start = 20.dp, bottom = 16.dp),
                    text = "Plants:",
                    style = MaterialTheme.typography.titleLarge,
                )
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (plants.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "No Plants Icon",
                            )
                            Text(text = "No plants added.")
                        }
                    } else {
                        plants.forEach { plant -> PlantItem(plant = plant) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlantItem(plant: Plant) {
    Surface(
        color = MaterialTheme.colorScheme.inversePrimary,
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

@Composable
fun HumidityCard(humidity: Float) {
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

@Composable
private fun TemperatureCard(temperature: Float) {
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

@Composable
private fun MoistureCard(
    moisture: Float,
    isWateringInProgress: Boolean,
    isShortcutLoading: Boolean,
    onSprinklerClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Box {
            val currentValue = moisture.toInt()
            if (currentValue < recommendedMinLevel && !isWateringInProgress) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 12.dp)
                        .size(32.dp)
                        .clickable(onClick = onSprinklerClick)
                        .align(Alignment.TopEnd),
                ) {
                    if (isShortcutLoading) {
                        CircularProgressIndicator()
                    } else {
                        Image(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(R.drawable.ic_humidity),
                            contentDescription = "Sprinkler Icon",
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Soil moisture: ${moisture.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                )
                WaterLevelIndicator(
                    currentValue = currentValue,
                    modifier = Modifier.fillMaxWidth(),
                    recommendedMin = recommendedMinLevel,
                    recommendedMax = recommendedMaxLevel,
                    isWateringInProgress = isWateringInProgress,
                )
            }
        }
    }
}

@Composable
private fun WindCard(wind: Wind) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
            Text(
                text = "Wind direction: ${wind.direction}",
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_wind), contentDescription = "Wind")
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "${wind.speed.toInt()} km/h",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }
    }
}

@Composable
private fun CropFloatingActionButton(isLoading: Boolean, onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Image(
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp),
                painter = painterResource(id = R.drawable.ic_lightbulb),
                contentDescription = "Light Bulb Icon"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CropHeader(name: String, onAlarmsClick: () -> Unit, onInfoClick: () -> Unit) {
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
                    .clickable(onClick = onInfoClick)
                    .padding(horizontal = 12.dp),
                imageVector = Icons.Default.Info,
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

private const val recommendedMinLevel = 20
private const val recommendedMaxLevel = 30
