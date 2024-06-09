package hr.fer.fercropmanager

import android.app.Application
import hr.fer.fercropmanager.alarms.alertsModule
import hr.fer.fercropmanager.auth.authModule
import hr.fer.fercropmanager.crop.cropModule
import hr.fer.fercropmanager.device.deviceModule
import hr.fer.fercropmanager.login.loginModule
import hr.fer.fercropmanager.network.networkModule
import hr.fer.fercropmanager.snackbar.snackbarModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CropManagerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CropManagerApp)
            modules(
                listOf(networkModule, loginModule, cropModule, snackbarModule, deviceModule, authModule, alertsModule)
            )
        }
    }
}