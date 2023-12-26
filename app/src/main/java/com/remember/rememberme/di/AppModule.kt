package com.remember.rememberme.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.remember.rememberme.core.AppDatabase
import com.remember.rememberme.core.coroutines.DispatchersProvider
import com.remember.rememberme.core.coroutines.DispatchersProviderImpl
import com.remember.rememberme.feature.card.data.CardsRepository
import com.remember.rememberme.feature.card.data.CardsRepositoryImpl
import com.remember.rememberme.feature.card.data.SetRepository
import com.remember.rememberme.feature.card.data.SetRepositoryImpl
import com.remember.rememberme.feature.card.data.database.dao.CardDao
import com.remember.rememberme.feature.card.data.database.dao.SetDao
import com.remember.rememberme.feature.card.data.database.test.SetCallback
import com.remember.rememberme.feature.create_set.data.MockSetCreationRepositoryImpl
import com.remember.rememberme.feature.create_set.data.SetCreationRepository
import com.remember.rememberme.feature.create_set.data.SetCreationRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fuel.Fuel
import fuel.FuelBuilder
import fuel.HttpLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    @Singleton
    fun provideDispatchersProvider(dispatchersProviderImpl: DispatchersProviderImpl): DispatchersProvider

    @Binds
    @Singleton
    fun provideSetRepository(setRepositoryImpl: SetRepositoryImpl): SetRepository

    @Binds
    @Singleton
    fun provideSetCreationRepository(setCreationRepositoryImpl: SetCreationRepositoryImpl): SetCreationRepository

//    @Binds
//    @Singleton
//    fun provideSetCreationRepository(setCreationRepositoryImpl: MockSetCreationRepositoryImpl): SetCreationRepository


    @Binds
    @Singleton
    fun provideCardsRepository(cardsRepositoryImpl: CardsRepositoryImpl): CardsRepository

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
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        }

        @Provides
        @Singleton
        fun provideOkHttp(moshi: Moshi): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        fun provideFuel(okHttpClient: OkHttpClient): HttpLoader {
            return FuelBuilder()
                .config(okHttpClient)
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