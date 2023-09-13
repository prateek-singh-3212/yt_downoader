package com.bitlogger.pawxy.ui.screen

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitlogger.pawxy.R
import com.bitlogger.pawxy.data.DownloadStatus
import com.bitlogger.pawxy.ui.MainViewModel
import com.bitlogger.pawxy.ui.Screens
import com.bitlogger.pawxy.ui.component.YTVideo
import com.bitlogger.pawxy.ui.theme.PawxyTheme
import com.bitlogger.pawxy.ui.theme.font
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlin.math.roundToInt

@OptIn(ExperimentalUnitApi::class)
@Composable
fun YTDownloadScreen(
    navHostController: NavHostController,
    sharedViewModel: MainViewModel
) {
    val onBack = {
        navigateBackSafely(sharedViewModel, navHostController)
    }

    BackPressHandler(onBackPressed = onBack)
    PawxyTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                YTVideo(sharedViewModel.ytData)
                ProgressSate(sharedViewModel.downloadStates)

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                if (sharedViewModel.downloadJob == null) {
                    Button(
                        onClick = {
                            navigateBackSafely(sharedViewModel, navHostController)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        contentPadding = PaddingValues(
                            top = 18.dp,
                            bottom = 18.dp,
                            start = 24.dp,
                            end = 24.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0XFF892EFF),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0XFF9D7CC8),
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Download Another MP3",
                            fontFamily = font,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

fun navigateBackSafely(sharedViewModel: MainViewModel, navHostController: NavHostController) {
    val job = sharedViewModel.downloadJob
    if (job != null && job.isActive){
        job.cancel()
        sharedViewModel.downloadJob = null
    }
    navHostController.navigate(Screens.YTLinkScreen.route)
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ProgressSate(downloadStates: DownloadStatus) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, start = 18.dp, end = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        when(downloadStates) {
            is DownloadStatus.Error -> {
                Text(
                    text = downloadStates.message,
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC4A4A),
                    fontSize = TextUnit(12f, TextUnitType.Sp)
                )
                Image(
                    painter = painterResource(id = R.drawable.cross),
                    contentDescription = "Failed"
                )
            }
            is DownloadStatus.Progress -> {
                Text(
                    text = "Downloading...",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(12f, TextUnitType.Sp)
                )
                Text(
                    text = "${(downloadStates.progress.toFloat() / 100000)} MB / ${(downloadStates.totalSize.toFloat() / 100000)} MB",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = Color(0XFF858181)
                )
            }
            is DownloadStatus.Success -> {
                Text(
                    text = "Success",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF72BD6C),
                    fontSize = TextUnit(12f, TextUnitType.Sp)
                )
                Image(
                    painter = painterResource(id = R.drawable.tick),
                    contentDescription = "Success"
                )
            }

            is DownloadStatus.Convert -> {
                Text(
                    text = "Converting...",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(12f, TextUnitType.Sp)
                )
                Text(
                    text = "${downloadStates.progress} / ${downloadStates.totalTime}",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = Color(0XFF858181)
                )
            }
            is DownloadStatus.Save -> {
                Text(
                    text = "Saving...",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(12f, TextUnitType.Sp)
                )
                Text(
                    text = "${(downloadStates.progress / downloadStates.totalSize) * 100}",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = Color(0XFF858181)
                )
            }
        }
    }

    when(downloadStates) {
        is DownloadStatus.Convert -> {
            LinearProgressIndicator(
                progress = downloadStates.progress.let {
                    if (it == 0L) {
                        0.toFloat()
                    }else {
                        it.toFloat() / downloadStates.totalTime.toFloat()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(18.dp)
                    .padding(18.dp),
                trackColor = Color(0xFFEEEEEE),
                color = Color(0xFFFFBB0E)
            )
        }
        is DownloadStatus.Error -> {
            LinearProgressIndicator(
                progress = 1F,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(18.dp)
                    .padding(18.dp),
                trackColor = Color(0xFFEEEEEE),
                color = Color(0xFFDC4A4A)
            )
        }
        is DownloadStatus.Progress -> {
            LinearProgressIndicator(
                progress = downloadStates.progress.let {
                    if (it == 0L) {
                        0.toFloat()
                    }else {
                        it.toFloat() / downloadStates.totalSize.toFloat()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(18.dp)
                    .padding(18.dp),
                trackColor = Color(0xFFEEEEEE),
                color = Color(0xFF3690FA)
            )
        }
        is DownloadStatus.Save -> {
            LinearProgressIndicator(
                progress = downloadStates.progress.toFloat() / downloadStates.totalSize.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .width(18.dp)
                    .padding(18.dp),
                trackColor = Color(0xFFEEEEEE),
                color = Color(0xFF28C6D0)
            )
        }
        is DownloadStatus.Success -> {
            LinearProgressIndicator(
                progress = 1F,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(18.dp)
                    .padding(18.dp),
                trackColor = Color(0xFFEEEEEE),
                color = Color(0xFF72BD6C)
            )
        }
    }
}

@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun YTDownloadScreenPreview() {
    YTDownloadScreen(
        navHostController = rememberAnimatedNavController(),
        sharedViewModel = viewModel()
    )
}