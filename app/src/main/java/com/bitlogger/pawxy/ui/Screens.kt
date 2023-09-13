package com.bitlogger.pawxy.ui

sealed class Screens(val route: String) {
    object YTLinkScreen: Screens(route = "yt_link")
    object YTDownloadScreen: Screens(route = "yt_download")
}