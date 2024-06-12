package hr.fer.fercropmanager.crop.ui.plants

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsDialog(
    currentPlants: List<Plant>,
    onConfirm: (List<Plant>) -> Unit,
    onCancel: () -> Unit,
) {
    var plantsMap by remember {
        mutableStateOf(Plant.entries.associate {
            it.name to currentPlants.any { selectedItem ->
                selectedItem.name == it.name
            }
        })
    }
    BasicAlertDialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = "Plants on this crop:",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Plant.entries.forEach { plant ->
                    PlantItem(
                        plant = plant,
                        checked = plantsMap[plant.name]!!,
                        onCheckedChange = { isChecked ->
                            plantsMap = plantsMap.toMutableMap().apply {
                                this[plant.name] = isChecked
                            }
                        },
                    )
                }
                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        val selectedPlants = Plant.entries.filter { plantsMap[it.name] == true }
                        onConfirm(selectedPlants)
                    },
                ) {
                    Text(text = "Confirm")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onCancel,
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

@Composable
private fun PlantItem(plant: Plant, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = plant.name)
            Image(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(24.dp),
                painter = plant.painter,
                contentDescription = "Plant Icon"
            )
        }
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}
