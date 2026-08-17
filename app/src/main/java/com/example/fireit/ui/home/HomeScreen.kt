package com.example.fireit.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.example.fireit.R
import kotlin.math.roundToInt

@Composable
fun HomeRoute(
    vM: HomeViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val st by vM.uiState.collectAsStateWithLifecycle()

    val hasFilePermission by vM.hasFilePermission.collectAsStateWithLifecycle(false)

    val stp by vM.permissionUiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        vM.onAction(HomeAction.CheckPermissionAndLoadData)
    }


    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        vM.onAction(HomeAction.CheckPermissionAndLoadData)
    }


    LaunchedEffect(stp.shouldOpenSettings) {
        if (!hasFilePermission){
            if (stp.shouldOpenSettings){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    launcher.launch(intent)
                }
                vM.onAction(HomeAction.OffShouldOpenSettings)
            }
        }

    }



    HomeContent(
        state = st,
        onAction = vM::onAction,
        oSC = onSettingsClick
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    oSC: () -> Unit
) {
    val currentPhoto = state.currentItemList.firstOrNull()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.DarkGray)

    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Bonfire(
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize(),
        ) {
            PhotoCard(
                currentPhoto,
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(50.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {onAction(HomeAction.ThrowPhotoCard(currentPhoto))},
                    enabled = currentPhoto != null
                ) {
                    Text("THROW")
                }
                Button(
                    onClick = {onAction(HomeAction.KeepPhotoCard(currentPhoto))},
                    enabled = currentPhoto != null
                ) {
                    Text("KEEP")
                }
                Button(
                    onClick = {onAction(HomeAction.InvertItemList)}
                ) {
                    Text("REVERSE")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

    }

}

@Composable
fun Bonfire(
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                shape = RectangleShape,
                color = Color.Red
            )
    ){
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = R.drawable.erasebg_transformed,
            contentDescription = "Bonfire",
            contentScale = ContentScale.FillBounds
        )
    }
}


@Composable
fun PhotoCard(
    model: Any?,
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,

) {
    val height = width * 1.2f
    val paddingIntern = width * 0.08f
    val bottomPadding = width * 0.2f

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RectangleShape,
                clip = false
            )

            .width(width)
            .height(height)
            .background(Color.White)
            .background(Color.White, shape = RoundedCornerShape(2.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = paddingIntern, top = paddingIntern, end = paddingIntern, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
            ) {
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = "PhotoCard",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds // Corta para llenar sin deformar
                    )
                }
            }

        }
    }
}