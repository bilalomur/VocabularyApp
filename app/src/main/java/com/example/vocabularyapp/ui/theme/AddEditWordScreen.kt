package com.example.vocabularyapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vocabularyapp.R
import com.example.vocabularyapp.data.Difficulty
import com.example.vocabularyapp.data.Word
import com.example.vocabularyapp.ui.theme.VocabularyAppTheme

/** The word and its translation are required. */
private fun validateInput(term: String, translation: String): Boolean {
    return term.isNotBlank() && translation.isNotBlank()
}

/**
 * Add/Edit screen.
 * @param word The word to edit, if null a new word will be created.
 * @param onSave Called with the finished word when it is saved.
 * @param onCancel Called when the user cancels.
 */
@Composable
fun AddEditWordScreen(
    modifier: Modifier = Modifier,
    word: Word? = null,
    onSave: (Word) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var term by remember { mutableStateOf(word?.term ?: "") }
    var translation by remember { mutableStateOf(word?.translation ?: "") }
    var example by remember { mutableStateOf(word?.exampleSentence ?: "") }
    var subject by remember { mutableStateOf(word?.subject ?: "") }
    var difficulty by remember { mutableStateOf(word?.difficulty ?: Difficulty.EASY) }
    var validationError by remember { mutableStateOf(false) }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    if (word == null) R.string.add_screen_title else R.string.edit_screen_title
                ),
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextFieldWithClearAndError(
                value = term,
                onValueChange = { term = it },
                label = stringResource(R.string.term_label),
                errorMessage = stringResource(R.string.term_error),
                isError = validationError && term.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextFieldWithClearAndError(
                value = translation,
                onValueChange = { translation = it },
                label = stringResource(R.string.translation_label),
                errorMessage = stringResource(R.string.translation_error),
                isError = validationError && translation.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextFieldWithClearAndError(
                value = example,
                onValueChange = { example = it },
                label = stringResource(R.string.example_label),
                errorMessage = "",
                isError = false,
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextFieldWithClearAndError(
                value = subject,
                onValueChange = { subject = it },
                label = stringResource(R.string.subject_label),
                errorMessage = "",
                isError = false,
                modifier = Modifier.fillMaxWidth()
            )

            DifficultySelector(
                selected = difficulty,
                onSelected = { difficulty = it },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onCancel() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.errorContainer,
                        contentColor = colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        validationError = !validateInput(term, translation)
                        if (!validationError)
                            onSave(
                                Word(
                                    term = term.trim(),
                                    translation = translation.trim(),
                                    exampleSentence = example.trim(),
                                    subject = subject.trim(),
                                    difficulty = difficulty,
                                    isLearned = word?.isLearned ?: false,
                                    wordId = word?.wordId ?: System.currentTimeMillis()
                                )
                            )
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

/** Three chips (Easy / Medium / Hard) with a coloured dot; one is always selected. */
@Composable
fun DifficultySelector(
    selected: Difficulty,
    onSelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.difficulty_label),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Difficulty.entries.forEach { difficulty ->
                FilterChip(
                    selected = difficulty == selected,
                    onClick = { onSelected(difficulty) },
                    label = { Text(stringResource(difficulty.labelResId)) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(difficultyColor(difficulty), CircleShape)
                        )
                    }
                )
            }
        }
    }
}

/** An [OutlinedTextField] with a clear button and an error message. */
@Composable
fun OutlinedTextFieldWithClearAndError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(
                            R.string.clear_content_description,
                            label
                        )
                    )
                }
            }
        },
        isError = isError,
        supportingText = {
            Row {
                if (isError)
                    Text(errorMessage)
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddEditWordScreenPreview() {
    VocabularyAppTheme {
        AddEditWordScreen()
    }
}
