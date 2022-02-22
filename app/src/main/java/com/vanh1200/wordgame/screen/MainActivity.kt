package com.vanh1200.wordgame.screen

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.vanh1200.wordgame.Configuration
import com.vanh1200.wordgame.characterstate.CharacterState
import com.vanh1200.wordgame.databinding.ActivityMainBinding
import com.vanh1200.wordgame.hideSoftInput
import com.vanh1200.wordgame.viewmodel.WordViewModel
import com.vanh1200.wordgame.viewstate.WordGameState
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<WordViewModel>()
    private lateinit var viewBinding: ActivityMainBinding
    private val viewMatrix by lazy {
        val firstRow = arrayOf(
            viewBinding.rowFirst.textFirst,
            viewBinding.rowFirst.textSecond,
            viewBinding.rowFirst.textThird,
            viewBinding.rowFirst.textForth,
            viewBinding.rowFirst.textFifth
        )
        val secondRow = arrayOf(
            viewBinding.rowSecond.textFirst,
            viewBinding.rowSecond.textSecond,
            viewBinding.rowSecond.textThird,
            viewBinding.rowSecond.textForth,
            viewBinding.rowSecond.textFifth
        )
        val thirdRow = arrayOf(
            viewBinding.rowThird.textFirst,
            viewBinding.rowThird.textSecond,
            viewBinding.rowThird.textThird,
            viewBinding.rowThird.textForth,
            viewBinding.rowThird.textFifth
        )
        val forthRow = arrayOf(
            viewBinding.rowForth.textFirst,
            viewBinding.rowForth.textSecond,
            viewBinding.rowForth.textThird,
            viewBinding.rowForth.textForth,
            viewBinding.rowForth.textFifth
        )
        val fifthRow = arrayOf(
            viewBinding.rowFifth.textFirst,
            viewBinding.rowFifth.textSecond,
            viewBinding.rowFifth.textThird,
            viewBinding.rowFifth.textForth,
            viewBinding.rowFifth.textFifth
        )
        arrayOf(firstRow, secondRow, thirdRow, forthRow, fifthRow)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)
        readWordFromFileAndSaveToLocal()
        observeData()
        initListeners()
    }

    private fun initListeners() {
        viewBinding.editTemp.setOnClickListener {
            viewBinding.editTemp.setText("")
        }
        viewBinding.editTemp.doAfterTextChanged {
            displayText(it.toString())
        }
        viewBinding.editTemp.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                viewBinding.editTemp.setText("")
                viewBinding.editTemp.hideSoftInput()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        viewBinding.buttonSubmit.setOnClickListener {
            viewModel.submittedWord(getCurrentDisplayedText())
        }
    }

    private fun displayText(text: String) {
        displayRow(text)
    }

    private fun getCurrentDisplayedText(): String {
        val resultBuilder = StringBuffer()
        for (i in 0 until Configuration.LENGTH) {
            resultBuilder.append(viewMatrix[viewModel.currentRow][i].text)
        }
        return resultBuilder.toString()
    }

    private fun observeData() {
        viewModel.viewStateLiveData.observe(this) {
            when (it) {
                is WordGameState.GameOverState -> {
                    Toast.makeText(this, "Game over", Toast.LENGTH_SHORT).show()
                }
                is WordGameState.WinState -> {
                    viewBinding.buttonSubmit.isEnabled = false
                    Toast.makeText(this, "You win", Toast.LENGTH_SHORT).show()
                }
                is WordGameState.WordNotExistedState -> {
                    Toast.makeText(this, "Not found word", Toast.LENGTH_SHORT).show()
                }
                is WordGameState.InvalidLengthState -> {
                    Toast.makeText(this, "Input atleast 5 character", Toast.LENGTH_SHORT).show()
                }
                is WordGameState.CheckWordDoneState -> {
                    updateRowState(it.listState)
                }
            }
        }
    }

    private fun updateRowState(states: List<CharacterState>) {
        for (i in 0 until Configuration.LENGTH) {
            viewMatrix[viewModel.currentRow][i].setBackgroundResource(states[i].color)
        }
        viewModel.currentRow++
    }

    private fun readWordFromFileAndSaveToLocal() {
        viewModel.readWordsFromFileAndSaveToLocal()
    }

    private fun displayRow(text: String) {
        for (i in 0 until Configuration.LENGTH) {
            if (text.length > i) {
                viewMatrix[viewModel.currentRow][i].text = text[i].toString()
            }
        }
    }
}
