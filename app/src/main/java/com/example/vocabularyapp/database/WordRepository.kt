package com.example.vocabularyapp.database


import com.example.vocabularyapp.data.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordRepository(private val dao: WordDao) {

    val wordsFlow = dao.getAllWords()

    fun getWordById(wordId: Long) = dao.getWordById(wordId)

    suspend fun addWord(word: Word) {
        withContext(Dispatchers.IO) { dao.insertWord(word) }
    }

    suspend fun updateWord(word: Word) {
        withContext(Dispatchers.IO) { dao.updateWord(word) }
    }

    suspend fun deleteWord(word: Word) {
        withContext(Dispatchers.IO) { dao.deleteWord(word) }
    }
}