package com.example.faceimage

class ImageRepository(private val imageDao: ImageDao) {
    suspend fun getAllImages() = imageDao.getAllImages()
    suspend fun getImageByPath(imagePath: String): ImageEntity? {
        return imageDao.getImageByPath(imagePath)
    }
    suspend fun insertImage(image: ImageEntity) = imageDao.insertImage(image)
    suspend fun deleteImage(image: ImageEntity) = imageDao.deleteImage(image)
}
