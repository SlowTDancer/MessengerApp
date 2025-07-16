package com.ikhut.messengerapp.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.ikhut.messengerapp.application.config.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

object ImageUtils {
    fun saveImageToInternalStorage(context: Context, imageUri: Uri, userId: String): String? {
        return try {
            val profileImagesDir = File(context.filesDir, Constants.PROFILE_IMAGES_DIR)
            if (!profileImagesDir.exists()) {
                profileImagesDir.mkdirs()
            }

            val fileName = "${userId}_${UUID.randomUUID()}.jpg"
            val imageFile = File(profileImagesDir, fileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) {
                return null
            }

            val resizedBitmap = resizeBitmap(bitmap)

            val outputStream = FileOutputStream(imageFile)
            resizedBitmap.compress(
                Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY, outputStream
            )
            outputStream.close()

            if (bitmap != resizedBitmap) {
                resizedBitmap.recycle()
            }
            bitmap.recycle()

            imageFile.absolutePath

        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun deleteOldProfileImage(imagePath: String?): Boolean {
        return try {
            if (!imagePath.isNullOrEmpty()) {
                val file = File(imagePath)
                if (file.exists()) {
                    val deleted = file.delete()
                    deleted
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int = Constants.MAX_IMAGE_SIZE): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val ratio = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun fileExists(imagePath: String?): Boolean {
        return if (!imagePath.isNullOrEmpty()) {
            File(imagePath).exists()
        } else {
            false
        }
    }
}