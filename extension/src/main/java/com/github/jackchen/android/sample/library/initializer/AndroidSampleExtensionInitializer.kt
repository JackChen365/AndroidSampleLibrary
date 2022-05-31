package com.github.jackchen.android.sample.library.initializer

import android.content.Context
import androidx.startup.Initializer
import com.github.jackchen.android.sample.library.component.document.DocumentAssetsManager

@Suppress("unused")
class AndroidSampleExtensionInitializer : Initializer<AndroidSampleExtensions> {
    override fun create(context: Context): AndroidSampleExtensions {
        val androidSampleExtensions = AndroidSampleExtensions()
        androidSampleExtensions.initialize(context)
        DocumentAssetsManager.getInstance().onCreate(context)
        return androidSampleExtensions
    }

    override fun dependencies(): List<Class<out Initializer<*>?>> {
        return emptyList()
    }
}