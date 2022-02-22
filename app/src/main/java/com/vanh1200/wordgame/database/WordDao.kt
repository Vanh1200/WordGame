package com.vanh1200.wordgame.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vanh1200.wordgame.model.Word
import kotlinx.coroutines.flow.Flow


@Dao
interface WordDao {
    @Query("SELECT * FROM word")
    fun getAll(): Flow<List<Word>>

    @Query("SELECT * FROM word WHERE value = :wordName")
    fun getByName(wordName: String): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(order: List<Word>)
}