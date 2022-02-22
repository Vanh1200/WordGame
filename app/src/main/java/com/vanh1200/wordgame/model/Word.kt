package com.vanh1200.wordgame.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class Word(
    @PrimaryKey
    @ColumnInfo(name = "value", collate = ColumnInfo.NOCASE)
    val value: String = "",
    @ColumnInfo(name = "meaning")
    val meaning: String = ""
)
