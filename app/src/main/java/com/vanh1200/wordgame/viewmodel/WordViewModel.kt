package com.vanh1200.wordgame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanh1200.wordgame.Configuration
import com.vanh1200.wordgame.FileUtils
import com.vanh1200.wordgame.WordApplication
import com.vanh1200.wordgame.database.WordDao
import com.vanh1200.wordgame.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class WordViewModel(application: Application, val wordDao: WordDao) : AndroidViewModel(application) {
    private val _getAllWordsLiveData = MutableLiveData<List<Word>>()
    val getAllWordsLiveData = _getAllWordsLiveData

    //Stored values
    var tryCount = Configuration.MAX_TRY_COUNT
    var listSubmittedWord = mutableListOf<String>()
    var currentRow = 0

    fun readWordsFromFileAndSaveToLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = getApplication<WordApplication>().assets.open("dictionary.json")
            val string = FileUtils.readFileAsTextUsingInputStream(inputStream)
            val type: Type = object : TypeToken<Map<String?, String?>?>() {}.type
            val wordMap: Map<String, String> = Gson().fromJson(string, type)
            wordDao.insertAll(wordMap.toList().map { Word(value = it.first, meaning = it.second) })
            inputStream.close()
        }
    }

    fun submittedWord(word: String) {

    }

    fun getAllWords() {
        viewModelScope.launch(Dispatchers.IO) {
            wordDao.getAll().collect {
                _getAllWordsLiveData.postValue(it)
            }
        }
    }

    fun checkLengthValid(word: String) : Boolean {
        return word.length == Configuration.LENGTH
    }

    fun checkExistedWord(word: String, wordList: List<Word>) : Boolean {
        val result = wordList.find {
            it.value == word
        }
        return result != null
    }
}