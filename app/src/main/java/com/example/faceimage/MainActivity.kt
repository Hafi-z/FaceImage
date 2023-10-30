package com.example.faceimage
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
import androidx.room.Room
import com.example.faceimage.ImagesGallery.Companion.listOfImages
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException
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
        Log.d("hafizsize", images.size.toString())
        recyclerView.adapter = galleryAdapter

        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (galleryAdapter.getItemViewType(position)) {
                    GalleryAdapter.VIEW_TYPE_IMAGE -> 1 // Image items span 1 column
                    GalleryAdapter.VIEW_TYPE_PROGRESS -> 3 // Progress items span 3 columns
                    else -> -1
                }
            }
        }
        recyclerView.layoutManager = gridLayoutManager


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



//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (!recyclerView.canScrollVertically(1) and !isloaded) {
//                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
//                   // Toast.makeText(this@MainActivity, "Last", Toast.LENGTH_LONG).show()
//                }
//                else findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
//            }
//        })

    }

    private fun loadImages() {

        tempImages = listOfImages(this@MainActivity)

        if(tempImages1!=tempImages) {
            images.clear()


            job = lifecycleScope.launch(Dispatchers.Default) {

                images.add("dummy")

                withContext(Dispatchers.Main) {
                    galleryAdapter.update(images)
                }

                if (images.isNotEmpty()) {
                    if(images[images.size-1]=="dummy") {
                        images.removeAt(images.lastIndex)
                    }
                }

                val imageRepository = ImageRepository(db.imageDao())


                for (img in 0 until tempImages.size) {


                    Log.d("imageSize", images.size.toString())




                    val curImage = imageRepository.getImageByPath(tempImages[img])
                    if (curImage == null) {
                        isProcessed[tempImages[img]] = 1

//                        val bitmap =
//                            ImagesGallery.decodeSampledBitmapFromFilePath(tempImages[img], 300, 300)
//                        val image = InputImage.fromBitmap(bitmap, 0)
//                        val task = detector.process(image)
                        try {
                            val bitmap =
                                ImagesGallery.decodeSampledBitmapFromFilePath(tempImages[img], 300, 300)
                            val image = InputImage.fromBitmap(bitmap, 0)
                            val task = detector.process(image)

                            val result = Tasks.await(task)
                            if (result.isNotEmpty()) {

                                isProcessed[tempImages[img]] = 2
                                imageRepository.insertImage(
                                    ImageEntity(
                                        tempImages[img],
                                        true,
                                        true
                                    )
                                )
                                images.add(tempImages[img])
//                                withContext(Dispatchers.Main) {
//                                    galleryAdapter.update(images)
//                                }
                            } else {
                                imageRepository.insertImage(
                                    ImageEntity(
                                        tempImages[img],
                                        true,
                                        false
                                    )
                                )
                            }
                        } catch (_: InterruptedException) {
                            Log.d("exception", "loadImages: InterruptedException")
                        } catch (_: TimeoutException) {
                            Log.d("exception", "loadImages: InterruptedException")
                        } catch (_: ExecutionException) {
                            Log.d("exception", "loadImages: InterruptedException")
                        } catch (e:Exception){
                            Log.d("exception", "loadImages: AnyException")
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
                    } else if (curImage.isFace) {
                        images.add(tempImages[img])
//                        withContext(Dispatchers.Main) {
//                            galleryAdapter.update(images)
//                        }
                    }



                    if ( images.size>0 && images.size % 6 == 0) {
                        images.add("dummy")

                        withContext(Dispatchers.Main) {
                            galleryAdapter.update(images)
                        }

                        if (images.isNotEmpty()) {
                            if(images[images.size-1]=="dummy") {
                                images.removeAt(images.lastIndex)
                            }
                        }
                    }
                }


                withContext(Dispatchers.Main) {
                    galleryAdapter.update(images)
                }

                tempImages1 = tempImages

                Log.d("hafizsize", images.size.toString())
            }
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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