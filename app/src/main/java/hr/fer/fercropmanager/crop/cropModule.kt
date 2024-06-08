package hr.fer.fercropmanager.crop

import hr.fer.fercropmanager.crop.usecase.CropUseCase
import hr.fer.fercropmanager.crop.usecase.CropUseCaseImpl
import hr.fer.fercropmanager.crop.ui.CropViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val cropModule = module {
    singleOf(::CropUseCaseImpl) bind CropUseCase::class
    viewModelOf(::CropViewModel)
}