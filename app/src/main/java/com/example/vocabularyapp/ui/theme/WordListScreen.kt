package com.example.vocabularyapp.ui.theme
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabularyapp.R
import com.example.vocabularyapp.data.Difficulty
import com.example.vocabularyapp.data.Word
import com.example.vocabularyapp.database.WordRepository
import com.example.vocabularyapp.ui.theme.VocabularyAppTheme
import kotlinx.coroutines.launch

/** Colour used for a difficulty level (list bar and chips). */
fun difficultyColor(difficulty: Difficulty): Color {
    return when (difficulty) {
        Difficulty.EASY -> Color(0xFF4CAF50)   // green
        Difficulty.MEDIUM -> Color(0xFFFFA000) // orange
        Difficulty.HARD -> Color(0xFFD32F2F)   // red
    }
}

/** One list item: the word and its translation; expands to show subject and example. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordItem(
    word: Word,
    onEditClick: () -> Unit,
    onDelete: () -> Unit,
    onLearnedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "Chevron Rotation"
    )
    val contentHeight by animateDpAsState(
        targetValue = if (expanded) 180.dp else 72.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "Content Height Animation"
    )
    val surfaceColor by animateColorAsState(
        targetValue = if (word.isLearned)
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.75f)
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 500),
        label = "Surface Color Animation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = onDelete // Long press to delete
            )
            .height(contentHeight),
        color = surfaceColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Difficulty marker (bar on the left side)
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(difficultyColor(word.difficulty))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = word.term,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (word.isLearned)
                        TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = word.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (expanded) {
                    if (word.subject.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.subject_format, word.subject),
                            style = MaterialTheme.typography.labelMedium,
                            color = difficultyColor(word.difficulty)
                        )
                    }
                    if (word.exampleSentence.isNotBlank()) {
                        Text(
                            text = word.exampleSentence,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
            Checkbox(
                checked = word.isLearned,
                onCheckedChange = { onLearnedChange(it) }
            )
            IconButton(onClick = onEditClick) {
                Icon(Icons.TwoTone.Edit, contentDescription = "Edit word")
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
                    .rotate(rotationAngle)
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    word: Word,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_word_title)) },
        text = { Text(stringResource(R.string.delete_msg, word.term)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete), color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordListScreen(
    repository: WordRepository,
    modifier: Modifier = Modifier,
    onEdit: (Word) -> Unit = {},
) {
    // Updates automatically when the database changes
    val words: List<Word> by repository.wordsFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val coroutineScope = rememberCoroutineScope()

    // null = show all difficulties
    var filter by remember { mutableStateOf<Difficulty?>(null) }

    // Filtered by difficulty and sorted alphabetically
    val visibleWords = remember(words, filter) {
        words
            .filter { filter == null || it.difficulty == filter }
            .sortedBy { it.term.lowercase() }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var wordToDelete: Word? by remember { mutableStateOf(null) }

    Surface(modifier = modifier.fillMaxSize().padding(8.dp)) {
        Column {
            // Filter chips: All / Easy / Medium / Hard
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == null,
                    onClick = { filter = null },
                    label = { Text(stringResource(R.string.filter_all)) }
                )
                Difficulty.entries.forEach { difficulty ->
                    FilterChip(
                        selected = filter == difficulty,
                        onClick = { filter = difficulty },
                        label = { Text(stringResource(difficulty.labelResId)) }
                    )
                }
            }

            if (visibleWords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.empty_list))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp)
                ) {
                    items(
                        items = visibleWords,
                        key = { word: Word -> word.wordId }
                    ) { word: Word ->
                        WordItem(
                            word = word,
                            onEditClick = { onEdit(word) },
                            onDelete = {
                                showDeleteDialog = true
                                wordToDelete = word
                            },
                            onLearnedChange = { learned: Boolean ->
                                coroutineScope.launch {
                                    repository.updateWord(word.copy(isLearned = learned))
                                }
                            },
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(1000),
                                fadeOutSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                ),
                                placementSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            )
                        )
                    }
                }
            }

            val wordForDialog = wordToDelete
            if (showDeleteDialog && wordForDialog != null) {
                DeleteConfirmationDialog(
                    word = wordForDialog,
                    onConfirm = {
                        coroutineScope.launch {
                            repository.deleteWord(wordForDialog)
                        }
                        wordToDelete = null
                        showDeleteDialog = false
                    },
                    onDismiss = {
                        wordToDelete = null
                        showDeleteDialog = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordItemPreview() {
    VocabularyAppTheme {
        WordItem(
            word = Word(
                term = "der Bahnhof",
                translation = "train station",
                exampleSentence = "Wo ist der Bahnhof?",
                subject = "travel",
                difficulty = Difficulty.MEDIUM
            ),
            onEditClick = {},
            onDelete = {},
            onLearnedChange = {}
        )
    }
}
