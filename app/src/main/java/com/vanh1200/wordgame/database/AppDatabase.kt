package com.vanh1200.wordgame.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vanh1200.wordgame.model.Word

@Database(entities = [Word::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun wordDao(): WordDao
}