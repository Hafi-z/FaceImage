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
import android.widget.ProgressBar
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
    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
    .setMinFaceSize(1f)
    .build()

val detector = FaceDetection.getClient(minFaceSize)

class GalleryAdapter(
    private var context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_PROGRESS = 2
    }

    private var images: ArrayList<String> = ArrayList()

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.image)
//        init {
//            image = itemView.findViewById(R.id.image)
//        }
    }

    // A view holder for displaying a progress bar
    inner class ProgressViewHolder(val progressBar: View) : RecyclerView.ViewHolder(progressBar)

    override fun getItemViewType(position: Int): Int {
        return if (images[position]!="dummy") {
// Return image view type for the first n items, where n is the size of the image list
            VIEW_TYPE_IMAGE
        } else {
// Return progress view type for the last item
            VIEW_TYPE_PROGRESS
        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
//        return GalleryViewHolder(view)

        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
// Inflate an image view layout
                val view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
                GalleryViewHolder(view)
            }
            VIEW_TYPE_PROGRESS -> {
// Inflate a progress bar layout
                val progressBar = LayoutInflater.from(context).inflate(R.layout.progress_bar, parent, false)
                ProgressViewHolder(progressBar)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(images[position]!="dummy") {
            Glide.with(context)
                .load(images[position])
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .placeholder(R.drawable.ic_launcher_foreground)
                .into((holder as GalleryViewHolder).image)
        }
        else{
            ;
        }

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