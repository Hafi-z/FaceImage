package com.example.faceimage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImagesGallery {

    companion object {
        suspend fun listOfImages(context: Context): ArrayList<String> {

            val listOfAllImages: ArrayList<String> = ArrayList()
            val listOfAllFaceImages: ArrayList<String> = ArrayList()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
            val orderBy = MediaStore.Images.Media._ID
            val cursor = context.contentResolver.query(uri, projection, null, null, orderBy)
            val columnIndexData = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
            var cnt = 1

            while (cursor?.moveToNext() == true) {
                val absolutePathOfImage = cursor.getString(columnIndexData!!)
                listOfAllImages.add(absolutePathOfImage)
//                if(cnt>100)break
                cnt++
            }
            cursor?.close()
            Log.d("hafiz", cnt.toString())

            ///PROCESSING PART
            val minFaceSize = FaceDetectorOptions.Builder()
                .setMinFaceSize(.5f)
                .build()
            val detector = FaceDetection.getClient(minFaceSize)
//            for (img in listOfAllImages) {
//                var bitmap = decodeSampledBitmapFromFilePath(img, 500, 500)
////        holder.image.setImageBitmap(bitmap)
//                Log.d("hafiz", "success1")
//                val a = System.currentTimeMillis()
//                Log.d("hafiztime", "start time: " + a)
//
//                //1
//                val image = InputImage.fromBitmap(bitmap, 0)
//                Log.d("hafiz", "success2")
//////        //2
//
//
//                Log.d("hafiz", "success3")
////        // Or, to use options:
////        // val detector = FaceDetection.getClient(option);
//
////        //3
//                try {
////                    GlobalScope.launch(Dispatchers.Default) {
//                        // This code will run on a background thread
//
//                        val result = detector.process(image)
//                        result
//                            .addOnSuccessListener { faces ->
//                                for (face in faces) {
//                                    Log.d("hafiz", "success4")
//                                    Log.d("hafiz", img)
//                                    // Process the face if needed
//                                    // bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false)
//                                    // Update the ImageView on the main thread
//                                    listOfAllFaceImages.add(img)
//                                    break
//                                }
//                            }
//                            .addOnFailureListener { e ->
//                                Log.d("hafiz", "failed")
//                            }
////                    }
//                } catch (e: Exception) {
//                    Log.e("hafiz", "An exception occurred: $e")
//                }
//                val b = System.currentTimeMillis()
//                Log.d("hafiztime", "end time: " + b)
//                Log.d("hafiztime", "difference: " + ((b-a)/1000.0f))
//                avgtime += b-a
////                Log.d("hafiztime", "avg time: " + (avgtime.toFloat() / images.size))
//
////        holder.image.setImageBitmap(bitmap)
////        holder.image.setImageURI(images[position].toUri())
//            }

            val deferredResults = listOfAllImages.map { img ->
                GlobalScope.async(Dispatchers.Default) {
                    val bitmap = decodeSampledBitmapFromFilePath(img, 500, 500)
                    val image = InputImage.fromBitmap(bitmap, 0)
                    val result = detector.process(image)
                    if (result.isSuccessful) {
                        val faces = result.result
                        if (faces.isNotEmpty()) {
                            Log.d("hafiz", img)
                            listOfAllFaceImages.add(img)
                        }
                    }
                }
            }

            deferredResults.awaitAll()
            Log.d("hafizsize", listOfAllFaceImages.toString())
            return listOfAllFaceImages
        }

        fun decodeSampledBitmapFromFilePath(
            path: String,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(path, this)

                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                BitmapFactory.decodeFile(path, this)
            }
        }
        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }
    }
}