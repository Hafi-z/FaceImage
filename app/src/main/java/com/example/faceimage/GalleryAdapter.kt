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
        Glide.with(context).load(images[position]).placeholder(R.drawable.ic_launcher_background).into(holder.image)
        Log.d("Hafiz_image",images[position])
        System.out.println(images[position].toUri())
//        Picasso.get().load(images[position]).error(R.drawable.ic_launcher_background).into(holder.image)
//        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, images[position].toUri())
//        holder.image.setImageBitmap(bitmap)
//        val bitmap = BitmapFactory.decodeFile(images[position])
//        holder.image.setImageBitmap(bitmap)
    }
}