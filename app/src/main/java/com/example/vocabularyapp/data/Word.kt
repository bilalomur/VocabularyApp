
package com.example.vocabularyapp.data

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vocabularyapp.R

/** Difficulty level of a word. Stored in the database as text (EASY, MEDIUM, HARD). */
enum class Difficulty(@StringRes val labelResId: Int) {
    EASY(R.string.difficulty_easy),
    MEDIUM(R.string.difficulty_medium),
    HARD(R.string.difficulty_hard)
}

/**
 * A vocabulary word (also a Room entity / table row).
 *
 * @param term The word in the language being learned.
 * @param translation The translation.
 * @param exampleSentence An optional example sentence.
 * @param subject An optional subject, e.g. travel or business.
 * @param difficulty Easy, Medium or Hard.
 * @param isLearned True when the user has learned the word.
 * @param wordId Unique id (timestamp of creation).
 */
@Entity(tableName = "words")
data class Word(
    val term: String,
    val translation: String,
    val exampleSentence: String = "",
    val subject: String = "",
    val difficulty: Difficulty = Difficulty.EASY,
    val isLearned: Boolean = false,
    @PrimaryKey
    val wordId: Long = System.currentTimeMillis()
)
