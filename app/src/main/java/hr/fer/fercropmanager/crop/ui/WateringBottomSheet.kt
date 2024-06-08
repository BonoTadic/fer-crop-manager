package hr.fer.fercropmanager.crop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import hr.fer.fercropmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringBottomSheet(
    sheetState: SheetState,
    isVisible: Boolean,
    targetValue: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            BottomSheetContent(
                targetValue = targetValue,
                onValueChange = onValueChange,
                onConfirm = onConfirm,
                onCancel = onCancel,
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    targetValue: String,
    onValueChange: (String) -> Unit,
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
            text = "Crop watering",
            style = MaterialTheme.typography.headlineMedium,
        )
        Image(
            modifier = Modifier.size(128.dp),
            painter = painterResource(id = R.drawable.ic_irrigation),
            contentDescription = "Irrigation Icon"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "Enter the target percentage value (between 20% and 30%)",
            )
            OutlinedTextField(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                value = targetValue,
                label = {
                    Text(text = "Target value")
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onValueChange = onValueChange,
            )
        }
        OutlinedButton(
            enabled = targetValue.isNotEmpty(),
            onClick = { onConfirm() },
        ) {
            Text(text = "Start watering")
        }
        Button(onClick = onCancel) {
            Text(text = "Cancel")
        }
    }
}