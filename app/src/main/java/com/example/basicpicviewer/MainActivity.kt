package com.example.basicpicviewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
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
    lateinit var bitMap: Bitmap
    var isConverted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        initializeApp()

        findViewById<Button>(R.id.button).setOnClickListener {
            findViewById<ProgressBar>(R.id.loader).isVisible = true
            coroutineScope.launch {
                bitMap = fetchImage()
                findViewById<ImageView>(R.id.app_image).setImageBitmap(bitMap)
                findViewById<ProgressBar>(R.id.loader).isVisible = false
            }
        }

        findViewById<Button>(R.id.black_white_button).setOnClickListener {
            coroutineScope.launch {
                isConverted = if(isConverted) {
                    findViewById<Button>(R.id.black_white_button).setText(R.string.convert)
                    findViewById<ImageView>(R.id.app_image).setImageBitmap(bitMap)
                    false
                } else {
                    val converted = CoroutineScope(Dispatchers.IO).async {
                        toGrayscale(bitMap)
                    }
                    findViewById<ImageView>(R.id.app_image).setImageBitmap(converted.await())
                    findViewById<Button>(R.id.black_white_button).setText(R.string.restore)
                    true
                }
            }
        }
    }

    private fun initializeApp() {
        coroutineScope.launch {
            bitMap = fetchImage()
            findViewById<ImageView>(R.id.app_image).setImageBitmap(bitMap)
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

    private fun toGrayscale(srcImage: Bitmap): Bitmap {
        val bmpGrayscale =
            Bitmap.createBitmap(srcImage.width, srcImage.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0F)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(srcImage, 0F, 0F, paint)
        return bmpGrayscale
    }
}
