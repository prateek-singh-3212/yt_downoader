package com.bitlogger.pawxy.ui.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitlogger.pawxy.R
import com.bitlogger.pawxy.data.DownloadStatus
import com.bitlogger.pawxy.network.Data
import com.bitlogger.pawxy.network.YoutubeAPI
import com.bitlogger.pawxy.network.YoutubeURL
import com.bitlogger.pawxy.ui.MainViewModel
import com.bitlogger.pawxy.ui.Screens
import com.bitlogger.pawxy.ui.theme.PawxyTheme
import com.bitlogger.pawxy.ui.theme.font
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
fun YTLinkScreen(
    navHostController: NavHostController,
    youtubeAPI: YoutubeAPI,
    sharedViewModel: MainViewModel,
    activityResultLauncher: ActivityResultLauncher<Intent>
) {
    val ctx = LocalContext.current

    PawxyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var ytLink by remember { mutableStateOf("") }
            var isLoading by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "YouTube Link",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    value = ytLink,
                    enabled = !isLoading,
                    onValueChange = {
                        ytLink = if (it.contains("youtu.be")) {
                            "www.youtube.com/watch?v=" + it.slice(IntRange(17, it.length - 1))
                        } else {
                            it
                        }
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.link),
                            contentDescription = "Link"
                        )
                    },
                    trailingIcon = {
                        if (ytLink.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    ytLink = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "clear"
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                        focusedIndicatorColor = Color(0xFFD3D3D3),
                        unfocusedIndicatorColor = Color(0xFFE8E8E8),
                        containerColor = Color.White,
                        focusedTrailingIconColor = Color(0xFF858181),
                        unfocusedTrailingIconColor = Color(0xFF858181),
                        disabledTrailingIconColor = Color(0xFF858181)
                    ),
                    textStyle = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold),
                    shape = RoundedCornerShape(8.dp)
                )

                Text(
                    text = "Destination Folder",
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    modifier = Modifier.padding(start = 12.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                                            addCategory(Intent.CATEGORY_DEFAULT)
                                        }
                                        activityResultLauncher.launch(
                                            Intent.createChooser(
                                                intent,
                                                "Choose directory"
                                            )
                                        )
                                    }
                                }
                            }
                        },
                    readOnly = true,
                    value = (sharedViewModel.saveLocation  ?: " ").split("%3A").last(),
                    enabled = !isLoading,
                    onValueChange = {
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.folder),
                            contentDescription = "Link"
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                        focusedIndicatorColor = Color(0xFFD3D3D3),
                        unfocusedIndicatorColor = Color(0xFFE8E8E8),
                        containerColor = Color.White,
                        focusedTrailingIconColor = Color(0xFF858181),
                        unfocusedTrailingIconColor = Color(0xFF858181),
                        disabledTrailingIconColor = Color(0xFF858181)
                    ),
                    textStyle = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.padding(
                        top = 0.dp,
                        end = 12.dp,
                        bottom = 12.dp,
                        start = 12.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .width(26.dp)
                            .height(18.dp),
                        painter = painterResource(id = R.drawable.info),
                        contentDescription = "YT-Icon"
                    )
                    Text(
                        text = "Where you want to save the MP3",
                        fontFamily = font,
                        fontWeight = FontWeight.Normal,
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        modifier = Modifier.padding(start = 2.dp),
                        color = Color(0XFF858181)
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (ytLink.isEmpty()) {
                                Toast.makeText(ctx, "Type URL", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            if (sharedViewModel.saveLocation == null) {
                                Toast.makeText(ctx, "Select Directory", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            isLoading = true
                            try {
                                val response = youtubeAPI.getVideoData(YoutubeURL(Data(ytLink)))
                                sharedViewModel.addData(response)
                                sharedViewModel.downloadJob.let {
                                    if (it?.isActive == true) {
                                        it.cancel()
                                    } else {
                                        sharedViewModel.downloadJob =
                                            startDownload(ctx, sharedViewModel)
                                    }
                                }

                                navHostController.navigate(Screens.YTDownloadScreen.route)
                            } catch (ex: Exception) {
                                Toast.makeText(ctx, "No info found", Toast.LENGTH_SHORT).show()
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    enabled = !isLoading,
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 1.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Grabbing Info…",
                            fontFamily = font,
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } else {
                        Text(
                            text = "Download",
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

fun startDownload(ctx: Context, sharedViewModel: MainViewModel): Job {
    return CoroutineScope(Dispatchers.IO).launch {
        try {
            sharedViewModel.startAudioDownloadProcess(ctx, this.coroutineContext)
            sharedViewModel.downloadJob = null
        } catch (ex: Exception) {
            Log.e("worker", ex.toString())
            sharedViewModel.downloadJob = null
            sharedViewModel.downloadStates = DownloadStatus.Error(ex.message.toString())
        }
    }
}