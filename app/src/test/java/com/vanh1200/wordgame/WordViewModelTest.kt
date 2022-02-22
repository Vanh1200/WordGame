package com.vanh1200.wordgame

import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.vanh1200.wordgame.model.Word
import com.vanh1200.wordgame.repository.WordRepository
import com.vanh1200.wordgame.viewmodel.WordViewModel
import com.vanh1200.wordgame.viewstate.WordGameState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class WordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WordViewModel

    private val wordRepository = mockk<WordRepository>()
    private val application = mockk<WordApplication>()

    @Before
    fun setup() {

        viewModel = WordViewModel(
            application = application,
            wordRepository = wordRepository
        )
    }

    @Test
    fun submittedWord_invalidLength() {
        val word = "happ".uppercase()
        viewModel.submittedWord(word)
        assert(viewModel.viewStateLiveData.getOrAwaitValue() is WordGameState.InvalidLengthState)
    }

    @Test
    fun submittedWord_correct() {
        val word = "thorn".uppercase()
        viewModel.submittedWord(word)
        assert(viewModel.viewStateLiveData.getOrAwaitValue() is WordGameState.WinState)
    }

    @Test
    fun submittedWord_notExisted_tryCountGreaterThanZero() {
        val word = "happy".uppercase()
        coEvery { wordRepository.findWord(word) } returns flow {
            emit(emptyList<Word>())
        }
        viewModel.tryCount = 5
        viewModel.submittedWord(word)
        assert(viewModel.viewStateLiveData.getOrAwaitValue() is WordGameState.WordNotExistedState)
    }

    @Test
    fun submittedWord_notExisted_tryCountNotGreaterThanZero() {
        val word = "happy".uppercase()
        coEvery { wordRepository.findWord(word) } returns flow {
            emit(emptyList<Word>())
        }
        viewModel.tryCount = 0
        viewModel.submittedWord(word)
        assert(viewModel.viewStateLiveData.getOrAwaitValue(isRemoveAfterFirstOne = false) is WordGameState.GameOverState)
    }

    @Test
    fun submittedWord_Existed_tryCountGreaterThanZero() {
        val word = "happy".uppercase()
        val foundInDictionary = mutableListOf(Word(value = "HAPPY"))
        coEvery { wordRepository.findWord(word) } returns flow {
            emit(foundInDictionary)
        }
        viewModel.tryCount = 5
        viewModel.submittedWord(word)
        assert(viewModel.viewStateLiveData.getOrAwaitValue() is WordGameState.CheckWordDoneState)
    }

    @Test
    fun submittedWord_Existed_tryCountNotGreaterThanZero() {
        val word = "happy".uppercase()
        val foundInDictionary = mutableListOf(Word(value = "HAPPY"))
        coEvery { wordRepository.findWord(word) } returns flow {
            emit(foundInDictionary)
        }
        viewModel.tryCount = 0
        viewModel.submittedWord(word)
        assert(viewModel.viewStateLiveData.getOrAwaitValue(isRemoveAfterFirstOne = false)  is WordGameState.GameOverState)
    }
}



@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    isRemoveAfterFirstOne : Boolean = true,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            if (isRemoveAfterFirstOne) {
                this@getOrAwaitValue.removeObserver(this)
            }
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}