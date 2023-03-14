package com.kis.youranimelist.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val SHARED_IMAGES_DIR = "shared_images"
private const val MAX_COMPRESSION_QUALITY = 100

object FileUtils {
    suspend fun createCachedShareFile(context: Context, image: Drawable): Uri {
        val savedFile = with(File(context.cacheDir, SHARED_IMAGES_DIR)) {
            if (!this.exists()) {
                this.mkdir()
            }
            withContext(Dispatchers.IO) {
                val file = File.createTempFile("${image.hashCode()}", ".jpg", this@with)
                val stream = FileOutputStream(file)
                image.toBitmap()
                    .compress(Bitmap.CompressFormat.JPEG, MAX_COMPRESSION_QUALITY, stream)
                stream.flush()
                stream.close()
                file
            }
        }

        return FileProvider.getUriForFile(
            context,
            context.getFileProviderAuthorities(),
            savedFile
        )
    }
}
