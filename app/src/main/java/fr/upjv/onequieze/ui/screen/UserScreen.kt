package fr.upjv.onequieze.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import fr.upjv.onequieze.R
import fr.upjv.onequieze.ui.navigation.NavigationPath
import fr.upjv.onequieze.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController, viewModel: UserViewModel = viewModel()
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.profile), color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    viewModel.logout()
                    navController.navigate(NavigationPath.LOGIN_SCREEN)
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.logout),
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF9A1A19)
            )
        )
    }) { padding ->
        ProfileContent(Modifier.padding(padding), viewModel)
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier, viewModel: UserViewModel
) {
    val username by viewModel.username.collectAsState()
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()
    val userId by remember { mutableStateOf(viewModel.userId) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateProfileImage(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF9A1A19))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileImage(profileImageUrl, onClick = { imagePickerLauncher.launch("image/*") })

        Text(
            text = "${stringResource(R.string.username)} ${username ?: stringResource(R.string.loading)}",
            color = Color.White,
            fontSize = 20.sp
        )
        Text(
            text = "${stringResource(R.string.id)} : $userId", fontSize = 14.sp, color = Color.Gray
        )
    }
}

@Composable
private fun ProfileImage(profileImageUrl: String?, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .shadow(10.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFF405A9E))
            .clickable { onClick() }) {
        if (profileImageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(profileImageUrl),
                contentDescription = stringResource(R.string.profile_image),
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )
        } else {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.default_profile_image),
                tint = Color.Gray,
                modifier = Modifier.size(140.dp)
            )
        }
    }
}