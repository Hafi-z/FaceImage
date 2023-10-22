package com.example.faceimage

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import android.util.Log

class ImagesGallery {

    companion object {
        fun listOfImages(context: Context): ArrayList<String> {

            val listOfAllImages: ArrayList<String> = ArrayList()
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

            return listOfAllImages
        }
    }
}