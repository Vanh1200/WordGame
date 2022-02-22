package com.vanh1200.wordgame

import android.app.Application
import androidx.room.Room
import com.vanh1200.wordgame.database.AppDatabase
import com.vanh1200.wordgame.database.WordDao
import com.vanh1200.wordgame.repository.WordRepositoryImpl
import com.vanh1200.wordgame.viewmodel.WordViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val wordModule = module {
    viewModel { WordViewModel(androidApplication(), get()) }
    single { provideRoomDatabase(androidApplication()) }
    single { get<AppDatabase>().wordDao() }
    single { WordRepositoryImpl(get())}
}

fun provideRoomDatabase(application: Application) : AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "wordgame")
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()
}
