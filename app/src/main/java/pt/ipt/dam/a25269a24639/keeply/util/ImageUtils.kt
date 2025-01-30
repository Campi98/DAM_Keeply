package pt.ipt.dam.a25269a24639.keeply.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtils {
    private const val MAX_IMAGE_DIMENSION = 1024
    private const val COMPRESSION_QUALITY = 80

    fun fileToBase64(file: File): String {
        // Load and resize bitmap
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)
        bitmap = resizeBitmap(bitmap)

        // Compress and convert to base64
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap {
        try {
            // Remove any whitespace or newlines
            val cleanBase64 = base64String.trim()
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ?: throw Exception("Failed to decode bitmap")
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error decoding base64: ${e.message}")
            throw e
        }
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = MAX_IMAGE_DIMENSION
            newHeight = (MAX_IMAGE_DIMENSION / ratio).toInt()
        } else {
            newHeight = MAX_IMAGE_DIMENSION
            newWidth = (MAX_IMAGE_DIMENSION * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}