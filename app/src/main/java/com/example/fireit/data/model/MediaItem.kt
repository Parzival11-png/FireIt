package com.example.fireit.data.model

import android.net.Uri

data class MediaItem(
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val size: Long
)