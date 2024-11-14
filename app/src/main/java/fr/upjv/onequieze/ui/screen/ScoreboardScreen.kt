package fr.upjv.onequieze.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import fr.upjv.onequieze.R
import fr.upjv.onequieze.data.firebase.repository.AuthRepository
import fr.upjv.onequieze.data.model.GameScore
import fr.upjv.onequieze.data.repository.GameRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreboardScreen(
    navController: NavController,
    onMainScreenButtonClick: () -> Unit,
    gameRepository: GameRepository = GameRepository(AuthRepository())
) {
    val scores = remember { mutableStateListOf<GameScore>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            scores.addAll(gameRepository.getCompleteScores())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scoreboard), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onMainScreenButtonClick() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9A1A19))
            )
        }, containerColor = Color(0xFF9A1A19)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                if (scores.isEmpty()) {
                    item {
                        Text(
                            "Aucun score disponible",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    items(scores) { score ->
                        ScoreItem(score)
                    }
                }
            }

            Text(
                text = "Scoreboard Thomas Lambert & Pierre Beaubecq",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ScoreItem(score: GameScore) {
    val formattedDate =
        SimpleDateFormat("dd MMM yyyy 'à' HH:mm", Locale.getDefault()).format(score.date)

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF405A9E)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = score.username,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(score.profileImageUrl),
                    contentDescription = "Photo de profil",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(text = "Score: ${score.score}", color = Color.White)
            }
            Text(
                text = "Date: $formattedDate",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}