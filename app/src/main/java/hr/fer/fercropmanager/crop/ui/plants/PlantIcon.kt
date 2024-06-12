package hr.fer.fercropmanager.crop.ui.plants

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import hr.fer.fercropmanager.R

val Plant.painter: Painter
    @Composable
    get() = when (this) {
        Plant.Corn -> painterResource(id = R.drawable.ic_corn)
        Plant.Wheat -> painterResource(id = R.drawable.ic_wheat)
        Plant.Sunflower -> painterResource(id = R.drawable.ic_sunflower)
        Plant.Apple -> painterResource(id = R.drawable.ic_apple)
        Plant.Pear -> painterResource(id = R.drawable.ic_pear)
        Plant.Orange -> painterResource(id = R.drawable.ic_orange)
        Plant.Cherry -> painterResource(id = R.drawable.ic_cherry)
    }
