package com.kis.youranimelist.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.kis.youranimelist.R

object ContextCompatUtils {
    fun startJPEGChooser(context: Context, uri: Uri, title: String) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND

            // share content
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, title)

            // https://developer.android.com/training/sharing/send#adding-rich-content-previews
            // chooser text title
            putExtra(Intent.EXTRA_TITLE, context.getString(R.string.share_image))
            // image preview - without type telegram can't load directly as an image
            setDataAndType(uri, "image/jpeg")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        ContextCompat.startActivity(
            context,
            Intent.createChooser(shareIntent, null),
            null,
        )
    }
}
