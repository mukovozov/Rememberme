package com.remember.rememberme.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.remember.rememberme.core.AppDatabase
import com.remember.rememberme.feature.card.database.dao.CardDao
import com.remember.rememberme.feature.card.database.dao.SetDao
import com.remember.rememberme.feature.card.database.test.SetCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context,
            cardDaoProvider: Provider<CardDao>,
            setDaoProvider: Provider<SetDao>
        ): AppDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "CardsDatabase"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        SetCallback(setDaoProvider, cardDaoProvider).onCreate(db)
                    }
                })
                .build()
        }

        @Provides
        @Singleton
        fun provideSetDao(database: AppDatabase) = database.setDao()

        @Provides
        @Singleton
        fun provideCardDao(database: AppDatabase) = database.cardDao()
    }

}