package com.example.faceimage
import android.Manifest
import android.content.Context
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
import androidx.room.Room
import com.example.faceimage.ImagesGallery.Companion.listOfImages
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    var isloaded = false

    var isProcessed: MutableMap<String, Int> = mutableMapOf()

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ImageDatabase::class.java,
            "images.db"
        ).build()
    }

    var job: Job? = null


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



        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) and !isloaded) {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                   // Toast.makeText(this@MainActivity, "Last", Toast.LENGTH_LONG).show()
                }
                else findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            }
        })

    }

    private fun loadImages() {
//        tempImages = listOfImages(this@MainActivity)
//        images.clear()



        job = lifecycleScope.launch(Dispatchers.Default) {
            var currentIndex = 0
            isloaded = false
            val imageRepository = ImageRepository(db.imageDao())
            tempImages = listOfImages(this@MainActivity)
            images.clear()

            for (img in 0 until tempImages.size) {
                currentIndex++
                val curImage = imageRepository.getImageByPath(tempImages[img])
                if (curImage == null) {
                    isProcessed[tempImages[img]] = 1
                    // Execute this in a coroutine
//                withContext(Dispatchers.Default) {
                    val bitmap =
                        ImagesGallery.decodeSampledBitmapFromFilePath(tempImages[img], 300, 300)
                    val image = InputImage.fromBitmap(bitmap, 0)
                    val task = detector.process(image)
                    try {
                        val result = Tasks.await(task)
                        if (result.isNotEmpty()) {
                            isProcessed[tempImages[img]] = 2
                            imageRepository.insertImage(ImageEntity(tempImages[img], true, true))
                            images.add(tempImages[img])
                            withContext(Dispatchers.Main) {
                                galleryAdapter.update(images)
                            }
                        } else {
                            imageRepository.insertImage(ImageEntity(tempImages[img], true, false))
                        }
                    } catch (_: InterruptedException) {

                    } catch (_: TimeoutException) {

                    } catch (_: ExecutionException) {

                    }

//                result
//                    .addOnSuccessListener { faces ->
//                        Log.d("hafiz", tempImages[img])
//                        if (faces.isNotEmpty()) {
//                            isProcessed[tempImages[img]] = 2
//                            images.add(tempImages[img])
//                            galleryAdapter.update(images)
//                        }
//                    }
//                    .addOnFailureListener { e ->
//                    }
                }
                else if (curImage.isFace) {
                    images.add(tempImages[img])
                    withContext(Dispatchers.Main) {
                        galleryAdapter.update(images)
                    }
                }


                if (currentIndex >= 27) {
                    if (currentIndex % 15 == 0) {

                        withContext(Dispatchers.Main) {
                            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            galleryAdapter.update(images)
                            //findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                        }

                    }
                }

            }

            withContext(Dispatchers.Main) {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                galleryAdapter.update(images)
                isloaded = true
            }

            Log.d("hafizsize", images.size.toString())
        }
    }


//    fun fetchAndStoreImages(context: Context, imageRepository: ImageRepository) {
//        val images = images // Implement your method for fetching images
//        val imageEntities = images.mapIndexed { _, imagePath ->
//            ImageEntity(imagePath)
//        }
//        imageRepository.insertImage(imageEntities)
//    }


    override fun onResume() {
        super.onResume()

        loadImages()

    }

    override fun onPause() {
        if(job!=null)
        {
            job!!.cancel()
        }
        super.onPause()
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