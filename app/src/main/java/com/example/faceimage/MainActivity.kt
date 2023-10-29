package com.example.faceimage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.faceimage.ImagesGallery.Companion.listOfImages
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity(), ImageProcessingCallback {

    lateinit var recyclerView: RecyclerView
    var images: ArrayList<String> = ArrayList()
    var tempImages: ArrayList<String> = ArrayList()
    var tempImages1: ArrayList<String> = ArrayList()
    lateinit var images2: MutableList<String>
    lateinit var galleryAdapter: GalleryAdapter
    var callback = MyCallback()

    var isProcessed: MutableMap<String, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rv_gallery_images)

        galleryAdapter = GalleryAdapter(this@MainActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        Log.d("hafizsize", images.size.toString())
        recyclerView.adapter = galleryAdapter



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
            //loadImages()
        }
    }

    private fun loadImages() {
        tempImages = listOfImages(this@MainActivity)
//        images.retainAll { it in tempImages }

        if(tempImages1!=tempImages) {
            images.clear()
            tempImages1 = tempImages


//        galleryAdapter.update(images)

            lifecycleScope.launch(Dispatchers.Default) {
                var currentIndex = 0

                for (img in 0 until tempImages.size) {
                    currentIndex++
                    if (isProcessed[tempImages[img]] == null) {
                        isProcessed[tempImages[img]] = 1
                        // Execute this in a coroutine
//                withContext(Dispatchers.Default) {
                        val bitmap =
                            ImagesGallery.decodeSampledBitmapFromFilePath(tempImages[img], 300, 300)
                        val image = InputImage.fromBitmap(bitmap, 0)
                        val task = detector.process(image)
                        try {
                            val result = Tasks.await(task, 300, TimeUnit.MILLISECONDS)
                            if (result.isNotEmpty()) {
                                isProcessed[tempImages[img]] = 2
                                images.add(tempImages[img])
//                            withContext(Dispatchers.Main) {
//                                galleryAdapter.update(images)
//                            }
                            }
                        } catch (_: InterruptedException) {

                        } catch (_: TimeoutException) {

                        } catch (_: ExecutionException) {

                        }

//                result
//                    .addOnSuccessListener { faces ->
//                        // Task completed successfully
//                        Log.d("hafiz", tempImages[img])
//                        if (faces.isNotEmpty()) {
//                            isProcessed[tempImages[img]] = 2
//                            images.add(tempImages[img])
//                            galleryAdapter.update(images)
////                            galleryAdapter.addImage(tempImages[img])
////                            galleryAdapter.notifyItemInserted(images.size - 1)
//                        }
//                        Log.d("hafiz", images.size.toString())
//                        Log.d("hafiz", Thread.currentThread().name)
////                            if (img == tempImages.size-1) {
////                    callback.onImagesProcessed(listOfAllFaceImages)
////                            }
//                    }
//                    .addOnFailureListener { e ->
//                        // Task failed with an exception
////                            if (img == tempImages.size-1) {
////                    callback.onImagesProcessed(listOfAllFaceImages)
////                            }
//                    }
//                Log.d("hafiz", listOfAllFaceImages.size.toString())
                    } else if (isProcessed[tempImages[img]] == 2) {
                        images.add(tempImages[img])
//                    withContext(Dispatchers.Main) {
//                        galleryAdapter.update(images)
//                    }

                    }


                    if (currentIndex >= 27) {
                        if (currentIndex % 15 == 0) {

                            withContext(Dispatchers.Main) {
                                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                                galleryAdapter.update(images)
                                findViewById<ProgressBar>(R.id.progressBar).visibility =
                                    View.VISIBLE
                            }

                        }
                    }

                }

                withContext(Dispatchers.Main) {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    galleryAdapter.update(images)


                }

                Log.d("hafizsize", images.size.toString())
            }
        }

    }

    override fun onResume() {
        super.onResume()

        loadImages()

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
                //loadImages3()
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onImagesProcessed(listOfFaceImages: ArrayList<String>) {
        TODO("Not yet implemented")
    }
}