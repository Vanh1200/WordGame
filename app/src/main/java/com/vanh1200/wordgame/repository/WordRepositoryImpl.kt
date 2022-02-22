package com.vanh1200.wordgame.repository

import com.vanh1200.wordgame.database.WordDao
import com.vanh1200.wordgame.model.Word
import kotlinx.coroutines.flow.Flow

class WordRepositoryImpl(private val dao: WordDao) : WordRepository{
    override suspend fun readAllWordAndSaveToLocal(words: List<Word>) {
        dao.insertAll(words)
    }

    override suspend fun findWord(word: String): Flow<List<Word>> {
        return dao.getByName(word)
    }

    override suspend fun getAll(): Flow<List<Word>> {
        return dao.getAll()
    }
}