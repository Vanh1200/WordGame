package com.vanh1200.wordgame.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class Word(
    @PrimaryKey
    @ColumnInfo(name = "value")
    val value: String = "",
    @ColumnInfo(name = "meaning")
    val meaning: String = ""
)