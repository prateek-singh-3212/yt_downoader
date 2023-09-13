package com.bitlogger.pawxy

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.bitlogger.pawxy.network.RetrofitClient
import com.bitlogger.pawxy.ui.MainViewModel
import com.bitlogger.pawxy.ui.Navigation
import com.bitlogger.pawxy.ui.component.YTHeader
import com.bitlogger.pawxy.ui.theme.PawxyTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import java.io.File

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedViewModel: MainViewModel by viewModels()

        val youtubeAPI = RetrofitClient.instance!!.getYoutubeAPI()
        val requestMultiplePermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
            var isGranted = false
            it.forEach { (s, b) ->
                isGranted = b
            }

            if (!isGranted){
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val uri: Uri = result.data!!.data ?: "".toUri()
            sharedViewModel.saveLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri.toString()
            }else {
                getDirectoryPathFromTreeUri(uri)
            }
        }

        setContent {
            PawxyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    requestMultiplePermission.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        YTHeader()
                        Navigation(
                            navController = rememberAnimatedNavController(),
                            youtubeAPI = youtubeAPI,
                            activityResultLauncher = activityResultLauncher,
                            sharedViewModel = sharedViewModel
                        )
                    }
                }
            }
        }
    }

    /**
     * For API level below Q
     * */
    private fun getDirectoryPathFromTreeUri(treeUri: Uri): String {
        val documentId = DocumentsContract.getTreeDocumentId(treeUri)
        val parts = documentId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return if (parts.size > 1) {
            Environment.getExternalStorageDirectory().absolutePath + File.separator + parts[1]
        } else {
            Environment.getExternalStorageDirectory().absolutePath
        }
    }
}