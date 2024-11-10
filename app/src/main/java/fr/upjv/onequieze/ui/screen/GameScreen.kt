package fr.upjv.onequieze.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.apollographql.apollo.api.ApolloResponse
import fr.upjv.onequieze.CharacterQuery
import fr.upjv.onequieze.R
import fr.upjv.onequieze.TotalCharactersCountQuery
import fr.upjv.onequieze.data.repository.isGameOver
import fr.upjv.onequieze.data.repository.matchCharacter
import fr.upjv.onequieze.data.repository.refreshCharacter
import kotlinx.coroutines.launch


var totalCharactersResponse: ApolloResponse<TotalCharactersCountQuery.Data>? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    onScoreboardButtonClick: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Jeu de quieze") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) { padding ->
        MyGameScreen(Modifier.padding(padding),onScoreboardButtonClick)
    }
}

@Composable
private fun MyGameScreen(modifier: Modifier, onScoreboardButtonClick: () -> Unit) {
    val character = remember { mutableStateOf<CharacterQuery.Node?>(null) }
    val mediaId = 21
    val scope = rememberCoroutineScope()
    val previouslyFetchedCharacters = remember { mutableSetOf<Int>() }
    var nameInput by remember { mutableStateOf("") }
    var life by remember { mutableIntStateOf(3) }
    var showGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        refreshCharacter(mediaId, character, previouslyFetchedCharacters)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.game_owner))
            character.value?.let {
                it.name?.full?.let { it1 -> Text(it1) }
            }
            character.value?.image?.medium.let {
                if (it != null) {
                    CharCoil(it)
                }
            }

            Text(stringResource(R.string.your_score, score))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.life),
                        tint = if (index < life) Color.Red else Color.Gray
                    )
                }
            }

            TextField(value = nameInput,
                onValueChange = { newInput -> nameInput = newInput },
                placeholder = { Text(stringResource(R.string.paceholder_what_name)) },
                singleLine = true
            )

            Button(
                onClick = {
                    scope.launch {
                        val isGameOver = matchCharacter(score,
                            character.value?.name?.full.toString(),
                            nameInput,
                            life,
                            mediaId,
                            character,
                            previouslyFetchedCharacters,
                            onScoreUpdate = { newScore -> score = newScore },
                            onLifeUpdate = { newLife -> life = newLife })
                        if (!isGameOver) showGameOver = true
                    }

                },
            ) {
                Text(stringResource(R.string.submit_game_answer))
            }

            Button(
                onClick = {
                    scope.launch {
                        val isGameOver = isGameOver(life) { newLife ->
                            life = newLife
                        }
                        if (!isGameOver) refreshCharacter(
                            mediaId, character, previouslyFetchedCharacters
                        )
                        else {
                            showGameOver = true
                        }
                    }
                },
            ) {
                Text(stringResource(R.string.skip_game_answer))
            }

        }
        if (showGameOver) {
            showGameOverPopup(score,
                onScoreUpdate = {newScore -> score = newScore},
                onLifeUpdate = { newLife -> life = newLife },
                onDismiss = { showGameOver = false },
                onConfirm = { onScoreboardButtonClick() })
        }
    }
}


@Composable
fun showGameOverPopup(
    score: Int, onScoreUpdate: (Int) -> Unit, onLifeUpdate: (Int) -> Unit, onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = {
        onLifeUpdate(3)
        onScoreUpdate(0)
        onDismiss()
    }, title = { Text(stringResource(R.string.game_over)) }, text = {
        Text(
            "${stringResource(R.string.popup_message)} \n\r ${stringResource(R.string.popup_info)} \n\r ${
                stringResource(
                    R.string.your_score, score
                )
            }", textAlign = TextAlign.Center
        )
    }, confirmButton = {
        Button(onClick = onConfirm) {
            Text(stringResource(R.string.ok))
        }
    }, dismissButton = {
        Button(onClick = { onLifeUpdate(3); onScoreUpdate(0); onDismiss() }) {
            Text(stringResource(R.string.play_again))
        }
    }

    )
}


@Composable
private fun CharCoil(url: String) {
    val imageSize = 256.dp

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = url).build()
    )

    Image(
        painter = painter, contentDescription = null, modifier = Modifier.size(imageSize)
    )
}
