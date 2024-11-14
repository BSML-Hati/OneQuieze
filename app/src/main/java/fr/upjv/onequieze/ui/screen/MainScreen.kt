package fr.upjv.onequieze.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.upjv.onequieze.R


@Composable
fun MainScreen(
    onGameButtonClick: () -> Unit,
    onScoreboardButtonClick: () -> Unit,
    onUserButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF9A1A19))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.onequieze),
                contentDescription = "App Logo",
                modifier = Modifier.size(240.dp)
            )

            Text(
                text = "One Quieze",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Button(
                onClick = { onGameButtonClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF405A9E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.play), fontSize = 20.sp, color = Color.White)
            }

            Button(
                onClick = { onScoreboardButtonClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF405A9E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.scoreboard), fontSize = 20.sp, color = Color.White)
            }

            Button(
                onClick = { onUserButtonClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF405A9E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.user), fontSize = 20.sp, color = Color.White)
            }

        }

        Text(
            text = stringResource(R.string.owner),
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
