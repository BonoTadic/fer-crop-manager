package hr.fer.fercropmanager.crop.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.fer.fercropmanager.crop.ui.utils.formatFloat

@Composable
fun FillIndicator(
    fillIndicatorData: FillIndicatorData,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 16.dp),
    ) {
        FillBar(
            unit = fillIndicatorData.unit,
            currentTemperature = fillIndicatorData.current,
            minTemperature = fillIndicatorData.min,
            maxTemperature = fillIndicatorData.max
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Min: ${fillIndicatorData.min}${fillIndicatorData.unit}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Blue
            )
            Text(
                text = "Max: ${fillIndicatorData.max}${fillIndicatorData.unit}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
            )
        }
    }
}

@Composable
private fun FillBar(
    unit: String,
    currentTemperature: Float,
    minTemperature: Float,
    maxTemperature: Float,
    modifier: Modifier = Modifier
) {
    val barWidth = 200.dp
    val barHeight = 32.dp
    val range = maxTemperature - minTemperature
    val position = (currentTemperature - minTemperature) / range

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .width(barWidth)
            .height(barHeight)
            .background(Color.Gray, shape = RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = Color.Green,
                topLeft = Offset(0f, 0f),
                size = Size((barWidth.toPx() * position).coerceAtMost(barWidth.toPx()), barHeight.toPx()),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
            )
        }
        Text(
            text = "${currentTemperature.formatFloat()}$unit",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp),
            color = Color.White,
            fontSize = 14.sp
        )
    }
}
