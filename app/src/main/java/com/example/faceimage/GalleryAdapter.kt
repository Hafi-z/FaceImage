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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

var avgtime = 0L
val minFaceSize = FaceDetectorOptions.Builder()
    .setMinFaceSize(.5f)
    .build()

val detector = FaceDetection.getClient(minFaceSize)

class GalleryAdapter(
    private var context: Context
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    private var images: ArrayList<String> = ArrayList()

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
//        init {
//            image = itemView.findViewById(R.id.image)
//        }
    }

    inner class ThemeDiffUtilCallback(
        val oldItem:ArrayList<String>,
        val newItem:ArrayList<String>
    ): DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem.size
        }

        override fun getNewListSize(): Int {
            return newItem.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] == newItem[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] == newItem[newItemPosition]
        }

    }

    fun update(themeList: ArrayList<String>) {
        Log.d("Sizeeee", "" + themeList.size)
        val callback = ThemeDiffUtilCallback(this.images, themeList)
        val diffResult =  DiffUtil.calculateDiff(callback)
        this.images.clear()
        this.images.addAll(themeList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setImageUri(imagesUri: String) {
        this.images.add(imagesUri)
        notifyDataSetChanged() // Notify the adapter that the dataset has changed.
    }
    fun setImages(images: ArrayList<String>) {
        this.images = images
        notifyDataSetChanged() // Notify the adapter that the dataset has changed.
    }

    fun addImage(image: String) {
//        this.images.add(image)
        notifyItemInserted(this.images.size-1) // Notify the adapter that the dataset has changed.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        Glide.with(context)
            .load(images[position])
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.image)

//        Log.d("Hafiz_image",images[position])
        System.out.println(images[position].toUri())
//        Picasso.get().load(images[position]).error(R.drawable.ic_launcher_background).into(holder.image)
//        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, images[position].toUri())
//        holder.image.setImageBitmap(bitmap)
//        var bitmap = BitmapFactory.decodeFile(images[position])
//        var bitmap = decodeSampledBitmapFromFilePath(images[position], 300, 300)
//        holder.image.setImageBitmap(bitmap)
//        Log.d("hafiz", "success1")
        val a = System.currentTimeMillis()
        Log.d("hafiztime", "start time: " + a)

        //1
//        val image = InputImage.fromBitmap(bitmap, 0)
//        Log.d("hafiz", "success2")
////        //2


//        Log.d("hafiz", "success3")
//        // Or, to use options:
//        // val detector = FaceDetection.getClient(option);



//        //3
//        try {
//            GlobalScope.launch(Dispatchers.Default) {
//                // This code will run on a background thread
//
//                val result = detector.process(image)
//                withContext(Dispatchers.Main) {
//                    // This code will run on the main thread
//
//                    result
//                        .addOnSuccessListener { faces ->
//                            for (face in faces) {
//                                Log.d("hafiz", "success4")
//                                // Process the face if needed
//                                // bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false)
//                                // Update the ImageView on the main thread
////                                holder.image.setImageBitmap(bitmap)
//                                Glide.with(context).load(images[position]).placeholder(R.drawable.ic_launcher_background).into(holder.image)
//                                break
//                            }
//                        }
//                        .addOnFailureListener { e ->
//                            Log.d("hafiz", "failed")
//                        }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("hafiz", "An exception occurred: $e")
//        }
        val b = System.currentTimeMillis()
        Log.d("hafiztime", "end time: " + b)
        Log.d("hafiztime", "difference: " + ((b-a)/1000.0f))
        avgtime += b-a
        Log.d("hafiztime", "avg time: " + (avgtime.toFloat() / images.size))

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