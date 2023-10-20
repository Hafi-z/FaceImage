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
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
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

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
//        Glide.with(context).load(images[position]).placeholder(R.drawable.ic_launcher_background).into(holder.image)
        Log.d("Hafiz_image",images[position])
        System.out.println(images[position].toUri())
//        Picasso.get().load(images[position]).error(R.drawable.ic_launcher_background).into(holder.image)
//        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, images[position].toUri())
//        holder.image.setImageBitmap(bitmap)
//        val bitmap = BitmapFactory.decodeFile(images[position])

        //1
//        var image: InputImage = InputImage.fromFilePath(context, images[position].toUri())
//        try {
//            image = InputImage.fromFilePath(context, images[position].toUri())
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
////        //2
//        val detector = FaceDetection.getClient()
//        // Or, to use options:
//        // val detector = FaceDetection.getClient(option);
//        //3
//        val result = detector.process(image)
//            .addOnSuccessListener {
//                // Task completed successfully
//                holder.image.setImageURI(images[position].toUri())
//            }
//            .addOnFailureListener { e ->
//                // Task failed with an exception
////                holder.image = (R.drawable.ic_launcher_foreground)
//            }

//        holder.image.setImageBitmap(bitmap)
        holder.image.setImageURI(images[position].toUri())
    }
}