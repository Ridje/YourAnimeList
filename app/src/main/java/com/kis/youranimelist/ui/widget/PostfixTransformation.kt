package com.kis.youranimelist.ui.widget

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PostfixTransformation(val postfix: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return postfixFilter(text, postfix)
    }
}


fun postfixFilter(text: AnnotatedString, postfix: String): TransformedText {

    val out = text.text + postfix
    val postfixOffset = postfix.length

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return offset
        }

        override fun transformedToOriginal(offset: Int): Int {
            return if (offset > text.text.length)  {
                out.length - postfixOffset
            } else {
                offset
            }
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}
