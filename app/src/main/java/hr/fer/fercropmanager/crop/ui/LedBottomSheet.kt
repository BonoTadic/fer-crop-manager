package hr.fer.fercropmanager.crop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hr.fer.fercropmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedBottomSheet(
    sheetState: SheetState,
    isVisible: Boolean,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            LedBottomSheetContent(
                isChecked = isChecked,
                onCheckedChange = onCheckedChange,
                onConfirm = onConfirm,
                onCancel = onCancel,
            )
        }
    }
}

@Composable
private fun LedBottomSheetContent(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "LED Status",
            style = MaterialTheme.typography.headlineMedium,
        )
        Image(
            modifier = Modifier
                .size(128.dp)
                .padding(vertical = 16.dp),
            painter = painterResource(id = R.drawable.ic_lightbulb),
            contentDescription = "Irrigation Icon"
        )
        Text(
            modifier = Modifier.padding(bottom = 24.dp),
            text = "Update the status of the LED light."
        )
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "LED enabled:",
            )
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onConfirm
        ) {
            Text(text = "Update LED state")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCancel,
        ) {
            Text(text = "Cancel")
        }
    }
}
