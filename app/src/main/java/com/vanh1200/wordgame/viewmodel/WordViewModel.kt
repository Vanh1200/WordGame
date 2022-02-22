package com.vanh1200.wordgame.viewmodel

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanh1200.wordgame.Configuration
import com.vanh1200.wordgame.FileUtils
import com.vanh1200.wordgame.WordApplication
import com.vanh1200.wordgame.characterstate.CharacterState
import com.vanh1200.wordgame.model.Word
import com.vanh1200.wordgame.repository.WordRepository
import com.vanh1200.wordgame.viewstate.WordGameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class WordViewModel(
    application: Application,
    private val wordRepository: WordRepository
    ) : AndroidViewModel(application) {

    private val _viewStateLiveData = MutableLiveData<WordGameState>()
    val viewStateLiveData = _viewStateLiveData

    //Stored values
    var tryCount = Configuration.MAX_TRY_COUNT
    var currentRow = 0

    fun readWordsFromFileAndSaveToLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = getApplication<WordApplication>().assets.open("dictionary.json")
            val string = FileUtils.readFileAsTextUsingInputStream(inputStream)
            val type: Type = object : TypeToken<Map<String?, String?>?>() {}.type
            val wordMap: Map<String, String> = Gson().fromJson(string, type)
            wordRepository.insertAll(wordMap.toList().map { Word(value = it.first, meaning = it.second) })
            inputStream.close()
        }
    }

    fun submittedWord(word: String) {
        when {
            !checkLengthValid(word) -> {
                _viewStateLiveData.value = WordGameState.InvalidLengthState
            }
            word.equals(Configuration.ANSWER, ignoreCase = true) -> {
                _viewStateLiveData.value =
                    WordGameState.CheckWordDoneState(calculateWordState(word))
                _viewStateLiveData.value = WordGameState.WinState
            }
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    wordRepository.findWord(word.uppercase()).collect {
                        if (it.isEmpty()) {
                            currentRow++
                            _viewStateLiveData.postValue(WordGameState.WordNotExistedState)
                            if (tryCount <= 0) {
                                _viewStateLiveData.postValue(WordGameState.GameOverState)
                            }
                            tryCount--
                        } else {
                            _viewStateLiveData.postValue(
                                WordGameState.CheckWordDoneState(
                                    calculateWordState(word)
                                )
                            )
                            if (tryCount <= 0) {
                                _viewStateLiveData.postValue(WordGameState.GameOverState)
                            }
                            tryCount--
                        }
                    }
                }
            }
        }
    }

    private fun calculateWordState(word: String): List<CharacterState> {
        val result = mutableListOf<CharacterState>()
        for (i in 0 until Configuration.LENGTH) {
            val character = word[i]
            val answerCharacter = Configuration.ANSWER[i]
            when {
                character.equals(answerCharacter, ignoreCase = true) ->
                    result.add(CharacterState.CORRECT)
                Configuration.ANSWER.contains(character, ignoreCase = true) ->
                    result.add(CharacterState.WRONG_POSITION)
                else -> result.add(CharacterState.WRONG_ALL)
            }
        }
        return result
    }

    private fun checkLengthValid(word: String): Boolean {
        return word.length == Configuration.LENGTH
    }
}
