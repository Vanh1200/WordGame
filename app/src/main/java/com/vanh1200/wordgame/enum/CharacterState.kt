package com.vanh1200.wordgame.enum

import androidx.annotation.ColorInt
import com.vanh1200.wordgame.R

enum class CharacterState(@ColorInt val color: Int) {
    WRONG_ALL(R.color.character_color_wrong_all),
    WRONG_POSITION(R.color.character_color_wrong_position),
    CORRECT(R.color.character_color_correct)
}