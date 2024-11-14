package fr.upjv.onequieze.ui.screen

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.apollographql.apollo.api.ApolloResponse
import fr.upjv.onequieze.CharacterQuery
import fr.upjv.onequieze.R
import fr.upjv.onequieze.TotalCharactersCountQuery
import fr.upjv.onequieze.data.firebase.repository.AuthRepository
import fr.upjv.onequieze.data.repository.GameRepository
import fr.upjv.onequieze.data.repository.isGameOver
import fr.upjv.onequieze.data.repository.matchCharacter
import fr.upjv.onequieze.data.repository.refreshCharacter
import fr.upjv.onequieze.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch


var totalCharactersResponse: ApolloResponse<TotalCharactersCountQuery.Data>? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    onScoreboardButtonClick: () -> Unit,
    gameRepository: GameRepository = GameRepository(AuthRepository())
) {
    var score by remember { mutableIntStateOf(0) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Jeu de quieze", color = Color.White) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF9A1A19)
        )
        )
    }) { padding ->
        MyGameScreen(Modifier.padding(padding),
            score = score,
            onScoreUpdate = { newScore -> score = newScore },
            onGameOver = {
                val finalScore = score
                gameRepository.saveGameScore(score = finalScore,
                    onSuccess = { onScoreboardButtonClick() },
                    onFailure = { exception ->
                        println("Erreur lors de l'enregistrement du score : ${exception.message}")
                    })
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyGameScreen(
    modifier: Modifier, score: Int, onGameOver: () -> Unit, onScoreUpdate: (Int) -> Unit
) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.binks) }
    var isPlaying by remember { mutableStateOf(false) }
    val character = remember { mutableStateOf<CharacterQuery.Node?>(null) }
    val mediaId = 21
    val scope = rememberCoroutineScope()
    val previouslyFetchedCharacters = remember { mutableSetOf<Int>() }
    var nameInput by remember { mutableStateOf("") }
    var life by remember { mutableIntStateOf(3) }
    var showGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        refreshCharacter(mediaId, character, previouslyFetchedCharacters)
        mediaPlayer.start()
        isPlaying = true
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF9A1A19)), color = Color(0xFF9A1A19)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                character.value?.let {
                    it.name?.full?.let { name ->
                        Text(
                            text = name, color = Color.White, fontSize = 18.sp
                        )
                    }
                }
                character.value?.image?.medium.let {
                    if (it != null) {
                        CharCoil(it)
                    }
                }

                Text(
                    text = stringResource(R.string.your_score, score),
                    color = Color.White,
                    fontSize = 18.sp
                )

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
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )

                Button(
                    onClick = {
                        scope.launch {
                            val isGameOver = matchCharacter(context,
                                score,
                                character.value?.name?.full.toString(),
                                nameInput,
                                life,
                                mediaId,
                                character,
                                previouslyFetchedCharacters,
                                onScoreUpdate = onScoreUpdate,
                                onLifeUpdate = { newLife -> life = newLife })
                            if (!isGameOver) showGameOver = true
                        }

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF405A9E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.submit_game_answer), color = Color.White)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF405A9E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.skip_game_answer), color = Color.White)
                }
            }

            Text(
                text = stringResource(R.string.game_owner),
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }

        if (showGameOver) {
            mediaPlayer.pause()
            showGameOverPopup(
                mediaPlayer,
                score = score,
                onScoreUpdate = onScoreUpdate,
                onLifeUpdate = { newLife -> life = newLife },
                onDismiss = { showGameOver = false },
                onConfirm = onGameOver
            )
        }
    }
}


@Composable
fun showGameOverPopup(
    mediaPlayer: MediaPlayer,
    score: Int,
    onScoreUpdate: (Int) -> Unit,
    onLifeUpdate: (Int) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = {
        onLifeUpdate(3)
        onScoreUpdate(0)
        mediaPlayer.start()
        onDismiss()
    }, title = { Text(stringResource(R.string.game_over), color = Color.White) }, text = {
        Text(
            "${stringResource(R.string.popup_message)} \n\r ${stringResource(R.string.popup_info)} \n\r ${
                stringResource(
                    R.string.your_score, score
                )
            }", textAlign = TextAlign.Center, color = Color.White

        )
    }, confirmButton = {
        Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF405A9E))
        ) {
            Text(stringResource(R.string.ok), color = Color.White)
        }
    }, dismissButton = {
        Button(
            onClick = { onLifeUpdate(3); onScoreUpdate(0); mediaPlayer.start(); onDismiss() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF405A9E))
        ) {
            Text(stringResource(R.string.play_again), color = Color.White)
        }
    }, containerColor = Color(0xFF9A1A19)

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
