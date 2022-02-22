package com.vanh1200.wordgame.repository

import com.vanh1200.wordgame.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun readAllWordAndSaveToLocal(words: List<Word>)

    suspend fun findWord(word: String): Flow<List<Word>>

    suspend fun getAll() : Flow<List<Word>>
}