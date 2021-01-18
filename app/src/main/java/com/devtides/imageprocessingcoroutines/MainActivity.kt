package com.devtides.imageprocessingcoroutines

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val imageUrl =
        "https://raw.githubusercontent.com/DevTides/JetpackDogsApp/master/app/src/main/res/drawable/dog.png"
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coroutineScope.launch {
            val getImageDeferred =
                coroutineScope.async(Dispatchers.IO) { getImageUrl() }
            val bitmap = getImageDeferred.await()

            loadImage(bitmap)

            delay(5000L)

            val filterImageDeferred =
                coroutineScope.async(Dispatchers.Default) { filterImage(bitmap) }
            val filteredBitmap = filterImageDeferred.await()

            loadImage(filteredBitmap)
        }
    }

    private fun getImageUrl(): Bitmap {
        return URL(imageUrl)
            .openStream()
            .use {
                BitmapFactory.decodeStream(it)
            }
    }

    private fun filterImage(bitmap: Bitmap) = Filter.apply(bitmap)

    private fun loadImage(bitmap: Bitmap?) {
        progressBar.visibility = View.GONE
        imageView.setImageBitmap(bitmap)
        imageView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}