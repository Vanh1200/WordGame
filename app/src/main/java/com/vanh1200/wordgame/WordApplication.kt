package com.vanh1200.wordgame

import android.app.Application
import com.vanh1200.wordgame.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WordApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WordApplication)
            modules(wordModule)
        }
    }
}