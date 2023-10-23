package com.example.faceimage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.faceimage.ImagesGallery.Companion.listOfImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ImageProcessingCallback {

    lateinit var recyclerView: RecyclerView
    var images: List<String> = ArrayList()
    lateinit var images2: MutableList<String>
    lateinit var galleryAdapter: GalleryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rv_gallery_images)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 101)
//            loadImages()
        } else {
            loadImages()
        }
    }

    private fun loadImages() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val callback = object : ImageProcessingCallback {
            override fun onImagesProcessed(listOfFaceImages: List<String>) {
                images = listOfFaceImages
                Log.d("hafizsize", images.size.toString())
                // Update the adapter with the processed images
                galleryAdapter = GalleryAdapter(this@MainActivity, images)
                recyclerView.adapter = galleryAdapter
            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            listOfImages(this@MainActivity, callback)
            Log.d("hafizsize", images.size.toString())
        }
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