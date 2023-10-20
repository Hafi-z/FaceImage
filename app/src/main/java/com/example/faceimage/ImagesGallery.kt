package com.example.faceimage

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore

class ImagesGallery {

    companion object {
        fun listOfImages(context: Context): ArrayList<String> {

            val listOfAllImages: ArrayList<String> = ArrayList()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
            val orderBy = MediaStore.Images.Media._ID
            val cursor = context.contentResolver.query(uri, projection, null, null, orderBy)
            val columnIndexData = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)

            while (cursor?.moveToNext() == true) {
                val absolutePathOfImage = cursor.getString(columnIndexData!!)
                listOfAllImages.add(absolutePathOfImage)
            }
            cursor?.close()

            return listOfAllImages
        }
    }
}