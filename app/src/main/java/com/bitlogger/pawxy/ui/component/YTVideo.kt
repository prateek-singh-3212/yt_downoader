package com.bitlogger.pawxy.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.bitlogger.pawxy.R
import com.bitlogger.pawxy.data.YoutubeData
import com.bitlogger.pawxy.ui.theme.font

@OptIn(ExperimentalUnitApi::class)
@Composable
fun YTVideo(ytData: YoutubeData?) {
    Column(
        modifier = Modifier
            .padding(18.dp)
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(12.dp)),
            painter = rememberAsyncImagePainter(ytData?.thumbnail) ?: painterResource(id = R.drawable.thumbnail),
            contentDescription = "YT Thumbnail",
            contentScale = ContentScale.FillWidth
        )

        Text(
            modifier = Modifier
                .padding(12.dp),
            text = ytData?.title ?: "Big Buck Bunny 60fps 4K - Official Blender Foundation Short Film…",
            maxLines = 2,
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit(14f, TextUnitType.Sp),
        )

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.views),
                contentDescription = "Views"
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "${ytData?.views} Views",
                maxLines = 2,
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                fontSize = TextUnit(12f, TextUnitType.Sp),
                color = Color(0XFF858181)
            )
            Image(
                modifier = Modifier.padding(start = 36.dp),
                painter = painterResource(id = R.drawable.likes),
                contentDescription = "Likes"
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "${ytData?.likes} Likes",
                maxLines = 2,
                fontFamily = font,
                fontWeight = FontWeight.Normal,
                fontSize = TextUnit(12f, TextUnitType.Sp),
                color = Color(0XFF858181)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun YTVideoPreview() {
//    YTVideo(
//        ytData = YoutubeData("A")
//    )
//}