package com.example.vocabularyapp
import android.app.Application
import com.example.vocabularyapp.database.WordDatabase
import com.example.vocabularyapp.database.WordRepository

class VocabularyApplication : Application() {

    lateinit var wordRepository: WordRepository
        private set

    override fun onCreate() {
        super.onCreate()
        wordRepository = WordRepository(WordDatabase.getDatabase(this).dao())
    }
}
