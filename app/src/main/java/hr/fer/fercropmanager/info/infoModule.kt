package hr.fer.fercropmanager.info

import hr.fer.fercropmanager.info.ui.InfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val infoModule = module {
    viewModelOf(::InfoViewModel)
}
