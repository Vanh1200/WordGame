package com.vanh1200.wordgame.viewstate

import com.vanh1200.wordgame.enum.CharacterState

sealed class WordGameState {
    object InvalidLengthState : WordGameState()
    object GameOverState: WordGameState()
    object WordNotExistedState: WordGameState()
    object WinState: WordGameState()
    data class CheckWordDoneState(
        val listState: List<CharacterState>
    ): WordGameState()
}