package com.kis.youranimelist.core

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

public interface ResourceProvider : StringResourceProvider {

    public fun getColor(@ColorRes resId: Int): Int
}

public interface StringResourceProvider {

    public fun getString(@StringRes resId: Int): String

    public fun getString(@StringRes resId: Int, vararg objects: Any): String

    public fun getQuantityString(
        @PluralsRes resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String
}

public class ResourceProviderImpl(
    private val context: Context,
) : ResourceProvider {

    private val resources: Resources
        get() = context.resources

    override fun getString(resId: Int): String =
        resources.getString(resId)

    override fun getString(resId: Int, vararg objects: Any): String =
        resources.getString(resId, *objects)

    override fun getColor(resId: Int): Int =
        ContextCompat.getColor(context, resId)

    override fun getQuantityString(
        @PluralsRes resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String {
        return resources.getQuantityString(resId, quantity, *formatArgs)
    }
}
