package com.bitlogger.pawxy.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.bitlogger.pawxy.R
import com.bitlogger.pawxy.ui.theme.font

@OptIn(ExperimentalUnitApi::class)
@Composable
fun YTHeader() {
    Row(
        modifier = Modifier.padding(top = 24.dp, end = 12.dp, bottom = 12.dp, start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .width(26.dp)
                .height(18.dp),
            painter = painterResource(id = R.drawable.yt),
            contentDescription = "YT-Icon"
        )
        Text(
            text = "MP3 Downloader",
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit(20f, TextUnitType.Sp),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}