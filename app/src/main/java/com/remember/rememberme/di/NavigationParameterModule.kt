package com.remember.rememberme.di

import androidx.lifecycle.SavedStateHandle
import com.remember.rememberme.feature.card.ui.navigation.SET_ID_PARAMETER
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SetIdParameter

@Module
@InstallIn(ViewModelComponent::class)
class NavigationParameterModule {

    @Provides
    @SetIdParameter
    @ViewModelScoped
    fun provideSetIdParameter(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle.get<Int>(SET_ID_PARAMETER)
            ?: throw IllegalArgumentException("Couldn't get set_id")
}