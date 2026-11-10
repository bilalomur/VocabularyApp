package com.example.vocabularyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vocabularyapp.ui.theme.VocabularyApp
import com.example.vocabularyapp.ui.theme.VocabularyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VocabularyAppTheme {
                val repository = (application as VocabularyApplication).wordRepository
                VocabularyApp(repository)
            }
        }
    }
}