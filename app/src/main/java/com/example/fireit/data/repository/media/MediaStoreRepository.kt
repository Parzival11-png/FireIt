package com.example.fireit.data.repository.media

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.fireit.data.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaStoreRepository @Inject constructor(
    private val contentResolver: ContentResolver
) {
    fun getImages(): List<MediaItem> {

        val images = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE
        )

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->

            val idColumn =
                cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media._ID
                )

            val nameColumn =
                cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.DISPLAY_NAME
                )

            val mimeColumn =
                cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.MIME_TYPE
                )

            val sizeColumn =
                cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.SIZE
                )

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                images += MediaItem(
                    uri = uri,
                    name = cursor.getString(nameColumn),
                    mimeType = cursor.getString(mimeColumn),
                    size = cursor.getLong(sizeColumn)
                )
            }
        }

        return images
    }
    fun getUris(): List<Uri> {
        val items = getImages().map { it.uri }
        return items
    }
    suspend fun deleteImages(
        uris: List<Uri>,
    ): DeleteResult = withContext(Dispatchers.IO) {
        var successful = 0
        val unsuccessful = mutableListOf<Uri>()

        for (uri in uris){
            try {
                val filas = contentResolver.delete(uri,null,null)
                if (filas > 0) successful++ else unsuccessful.add(uri)
            } catch (e: SecurityException) {
                unsuccessful.add(uri)
            } catch (e: Exception) {
                unsuccessful.add(uri)
            }
        }
        DeleteResult(successful = successful, unsuccessful = unsuccessful)
    }
    data class DeleteResult(
        val successful: Int,
        val unsuccessful: List<Uri>
    )
}


