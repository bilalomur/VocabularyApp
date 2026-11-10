package com.example.vocabularyapp.ui.theme

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.vocabularyapp.R
import com.example.vocabularyapp.database.WordRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

/** The routes of the app. */
sealed interface VocabDestination {
    @get:StringRes
    val titleResId: Int

    @Serializable
    data object List : VocabDestination {
        override val titleResId: Int
            get() = R.string.list_screen_title
    }

    @Serializable
    data object Add : VocabDestination {
        override val titleResId: Int
            get() = R.string.add_screen_title
    }

    @Serializable
    data class Edit(val wordId: Long) : VocabDestination {
        override val titleResId: Int
            get() = R.string.edit_screen_title
    }
}

@Composable
fun VocabularyApp(
    repository: WordRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Observe the back stack to find out which screen is displayed
    val backStackEntry by navController.currentBackStackEntryAsState()
    val screenNameResId = when {
        backStackEntry?.destination?.hasRoute<VocabDestination.Add>() == true ->
            backStackEntry?.toRoute<VocabDestination.Add>()?.titleResId

        backStackEntry?.destination?.hasRoute<VocabDestination.Edit>() == true ->
            backStackEntry?.toRoute<VocabDestination.Edit>()?.titleResId

        else ->
            backStackEntry?.toRoute<VocabDestination.List>()?.titleResId
    } ?: R.string.list_screen_title

    // The add button is shown only on the list screen
    val isAddOrEditScreen =
        backStackEntry?.destination?.hasRoute<VocabDestination.Add>() == true ||
                backStackEntry?.destination?.hasRoute<VocabDestination.Edit>() == true

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            VocabularyTopBar(
                titleResId = screenNameResId,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            if (!isAddOrEditScreen) {
                FloatingActionButton(onClick = {
                    navController.navigate(VocabDestination.Add)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add word")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = VocabDestination.List,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable<VocabDestination.List> {
                WordListScreen(
                    repository = repository,
                    onEdit = { wordToEdit ->
                        navController.navigate(VocabDestination.Edit(wordToEdit.wordId))
                    }
                )
            }
            composable<VocabDestination.Add> {
                AddEditWordScreen(
                    onSave = {
                        coroutineScope.launch {
                            repository.addWord(it)
                        }
                        navController.navigateUp()
                    },
                    onCancel = {
                        navController.navigateUp()
                    }
                )
            }
            composable<VocabDestination.Edit> { entry ->
                val wordId = entry.toRoute<VocabDestination.Edit>().wordId
                val word by remember(wordId) { repository.getWordById(wordId) }
                    .collectAsStateWithLifecycle(initialValue = null)
                if (word == null) {
                    // Loading indicator while waiting for the word
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AddEditWordScreen(
                        word = word,
                        onSave = {
                            coroutineScope.launch {
                                repository.updateWord(it)
                            }
                            navController.navigateUp()
                        },
                        onCancel = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyTopBar(
    titleResId: Int,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = titleResId)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}
