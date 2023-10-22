package com.example.faceimage

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class GalleryAdapter(
    private var context: Context,
    private var images: List<String>
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
//        init {
//            image = itemView.findViewById(R.id.image)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
//        Glide.with(context).load(images[position]).placeholder(R.drawable.ic_launcher_background).into(holder.image)
//        Log.d("Hafiz_image",images[position])
        System.out.println(images[position].toUri())
//        Picasso.get().load(images[position]).error(R.drawable.ic_launcher_background).into(holder.image)
//        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, images[position].toUri())
//        holder.image.setImageBitmap(bitmap)
//        var bitmap = BitmapFactory.decodeFile(images[position])
        var bitmap = decodeSampledBitmapFromFilePath(images[position], 500, 500)
//        holder.image.setImageBitmap(bitmap)
        Log.d("hafiz", "success1")

        //1
        val image = InputImage.fromBitmap(bitmap, 0)
        Log.d("hafiz", "success2")
////        //2
        val detector = FaceDetection.getClient()
        Log.d("hafiz", "success3")
//        // Or, to use options:
//        // val detector = FaceDetection.getClient(option);
//        //3
        try {
            GlobalScope.launch(Dispatchers.Default) {
                // This code will run on a background thread

                val result = detector.process(image)
                withContext(Dispatchers.Main) {
                    // This code will run on the main thread

                    result
                        .addOnSuccessListener { faces ->
                            for (face in faces) {
                                Log.d("hafiz", "success4")

                                // Process the face if needed

                                // Assuming you have a 'bitmap' variable to work with
                                // bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false)

                                // Update the ImageView on the main thread
                                holder.image.setImageBitmap(bitmap)
                                break
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.d("hafiz", "failed")
                        }
                }
            }
        } catch (e: Exception) {
            Log.e("hafiz", "An exception occurred: $e")
        }

//        holder.image.setImageBitmap(bitmap)
//        holder.image.setImageURI(images[position].toUri())
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