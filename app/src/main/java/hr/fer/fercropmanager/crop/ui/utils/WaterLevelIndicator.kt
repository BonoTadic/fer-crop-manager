package hr.fer.fercropmanager.crop.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.composables.materialcolors.MaterialColors

@Composable
fun WaterLevelIndicator(
    currentValue: Int,
    modifier: Modifier = Modifier,
    recommendedMin: Int,
    recommendedMax: Int,
    isWateringInProgress: Boolean = false,
) {
    val isInRange = currentValue in recommendedMin..recommendedMax
    val backgroundCircleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val arcColor = when {
        isWateringInProgress -> MaterialColors.Orange.`400`
        !isInRange -> Color.Red
        else -> MaterialTheme.colorScheme.primary
    }
    val currentValueTextColor = when {
        isWateringInProgress -> MaterialColors.Orange.`400`
        !isInRange -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }
    val recommendedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Box {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f
                val sweepAngle = currentValue * 360f / 50f

                drawCircle(
                    color = backgroundCircleColor,
                    center = center,
                    radius = radius,
                    style = Stroke(width = 16.dp.toPx())
                )

                drawArc(
                    color = arcColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(16.dp.toPx(), 16.dp.toPx()),
                    size = Size(size.width - 32.dp.toPx(), size.height - 32.dp.toPx()),
                    style = Stroke(width = 16.dp.toPx())
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Current Value: $currentValue%",
                style = MaterialTheme.typography.bodyLarge,
                color = currentValueTextColor,
            )

            when {
                isWateringInProgress -> Text(
                    text = "Watering is in progress.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialColors.Orange.`400`
                )
                !isInRange -> Text(
                    text = "Warning: Value outside recommended range ($recommendedMin% - $recommendedMax%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }

            Text(
                text = "Recommended Range: $recommendedMin% - $recommendedMax%",
                style = MaterialTheme.typography.bodySmall,
                color = recommendedTextColor,
            )
        }
    }
}
