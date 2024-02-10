package com.example.basicpicviewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

const val IMAGE_URL = "https://picsum.photos/350"
private val coroutineScope = CoroutineScope(Dispatchers.Main)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        initializeApp()

        findViewById<Button>(R.id.button).setOnClickListener {
            findViewById<ProgressBar>(R.id.loader).isVisible = true
            coroutineScope.launch {
                findViewById<ImageView>(R.id.app_image).setImageBitmap(fetchImage())
                findViewById<ProgressBar>(R.id.loader).isVisible = false
            }
        }
    }

    private fun initializeApp() {
        coroutineScope.launch {
            findViewById<ImageView>(R.id.app_image).setImageBitmap(fetchImage())
            findViewById<ProgressBar>(R.id.loader).isVisible = false
        }
    }

    private suspend fun fetchImage(): Bitmap {
        val deferred = CoroutineScope(Dispatchers.IO).async {
            fetchImageBitmap()
        }

        return deferred.await()
    }

    private fun fetchImageBitmap() = URL(IMAGE_URL).openStream().use {
        BitmapFactory.decodeStream(it)
    }
}
