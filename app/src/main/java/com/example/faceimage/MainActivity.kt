package com.example.faceimage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.faceimage.ImagesGallery.Companion.listOfImages
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ImageProcessingCallback {

    lateinit var recyclerView: RecyclerView
    var images: ArrayList<String> = ArrayList()
    var tempImages: ArrayList<String> = ArrayList()
    lateinit var images2: MutableList<String>
    lateinit var galleryAdapter: GalleryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rv_gallery_images)

        val greaterThanEqualTiramisu = android.Manifest.permission.READ_MEDIA_IMAGES
        val lessThanTiramisu = android.Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(
                this,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) greaterThanEqualTiramisu else lessThanTiramisu
            )
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) greaterThanEqualTiramisu else lessThanTiramisu), 101)
//            loadImages()
        } else {
            loadImages()
        }
    }

    private fun loadImages2() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val callback = object : ImageProcessingCallback {
            override fun onImagesProcessed(listOfFaceImages: List<String>) {
//                images = listOfFaceImages
                Log.d("hafizsize", images.size.toString())
                // Update the adapter with the processed images
                galleryAdapter = GalleryAdapter(this@MainActivity, images)
                recyclerView.adapter = galleryAdapter
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            listOfImages(this@MainActivity, callback)
//            listOfImages(this@MainActivity)
            Log.d("hafizsize", images.size.toString())
        }
    }

    private fun loadImages() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        Log.d("hafizsize", images.size.toString())
        // Update the adapter with the processed images
        galleryAdapter = GalleryAdapter(this@MainActivity, images)
        recyclerView.adapter = galleryAdapter
        tempImages = listOfImages(this@MainActivity)

        for (img in 0 until tempImages.size) {
            // Execute this in a coroutine
//                withContext(Dispatchers.Default) {
            val bitmap =
                ImagesGallery.decodeSampledBitmapFromFilePath(tempImages[img], 500, 500)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = detector.process(image)

            result
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    Log.d("hafiz", tempImages[img])
                    if (faces.isNotEmpty()) {
                        images.add(tempImages[img])
                        galleryAdapter.notifyItemInserted(images.size - 1)
                    }
                    Log.d("hafiz", images.size.toString())
                    Log.d("hafiz", Thread.currentThread().name)
//                            if (img == tempImages.size-1) {
//                    callback.onImagesProcessed(listOfAllFaceImages)
//                            }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
//                            if (img == tempImages.size-1) {
//                    callback.onImagesProcessed(listOfAllFaceImages)
//                            }
                }
//                Log.d("hafiz", listOfAllFaceImages.size.toString())
        }

        Log.d("hafizsize", images.size.toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                loadImages()
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onImagesProcessed(listOfFaceImages: List<String>) {
        TODO("Not yet implemented")
    }
}