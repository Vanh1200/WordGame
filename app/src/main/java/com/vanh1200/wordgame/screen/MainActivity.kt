package com.vanh1200.wordgame.screen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.vanh1200.wordgame.FileUtils.readFileAsTextUsingInputStream
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.vanh1200.wordgame.R
import com.vanh1200.wordgame.databinding.ActivityMainBinding
import com.vanh1200.wordgame.viewmodel.WordViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<WordViewModel>()
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)
        readWordFromFileAndSaveToLocal()
        observeData()
        viewModel.getAllWords()
    }

    private fun observeData() {
        viewModel.getAllWordsLiveData.observe(this) {
            Toast.makeText(this, "done ${it.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readWordFromFileAndSaveToLocal() {
        viewModel.readWordsFromFileAndSaveToLocal()
    }
}