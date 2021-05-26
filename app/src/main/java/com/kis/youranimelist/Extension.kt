package com.kis.youranimelist

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(
    text: String,
    actionText: String,
    action: (View) -> Unit,
    length: Int = Snackbar.LENGTH_INDEFINITE
) {
    Snackbar.make(this, text, length).setAction(actionText, action).show()
}

fun View.showSnackBar(
    textResource: Int,
    actionTextResource: Int,
    action: (View) -> Unit,
    length: Int = Snackbar.LENGTH_INDEFINITE,

) {
    Snackbar.make(this, resources.getString(textResource), length).setAction(resources.getString(textResource), action).show()
}
