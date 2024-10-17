package fr.upjv.onequieze.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp


@Composable
fun MainScreen(
    onGameButtonClick: () -> Unit,
    onScoreboardButtonClick: () -> Unit,
    onUserButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Thomas Lambert & Pierre Beaubecq")
            Button(content = {
                Text("Jeu", fontSize = 25.sp)
            }, onClick = { onGameButtonClick() })
            Button(content = {
                Text("Scoreboard", fontSize = 25.sp)
            }, onClick = { onScoreboardButtonClick() })
            Button(content = {
                Text("User", fontSize = 25.sp)
            }, onClick = { onUserButtonClick() })
        }
    }
}
