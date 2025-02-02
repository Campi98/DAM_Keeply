package pt.ipt.dam.a25269a24639.keeply.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Utilitário para gestão e manipulação de imagens.
 *
 * Esta classe fornece métodos para:
 * - Converter imagens entre diferentes formatos
 * - Redimensionar imagens para otimização
 * - Gerir codificação/descodificação base64
 */
object ImageUtils {

    /** Dimensão máxima permitida para imagens */
    private const val MAX_IMAGE_DIMENSION = 1024

    /** Qualidade de compressão JPEG (0-100) */
    private const val COMPRESSION_QUALITY = 80

    /**
     * Converte um ficheiro de imagem para string base64.
     *
     * @param file Ficheiro de imagem a converter
     * @return String com a imagem codificada em base64
     */
    fun fileToBase64(file: File): String {

        // Carregar e redimensionar bitmap
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)
        bitmap = resizeBitmap(bitmap)

        // Comprimir e converter para base64
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Converte uma string base64 para Bitmap.
     *
     * @param base64String String com a imagem codificada em base64
     * @return Bitmap descodificado
     * @throws Exception se a descodificação falhar
     */
    fun base64ToBitmap(base64String: String): Bitmap {
        try {
            // Remover espaços em branco e quebras de linha
            val cleanBase64 = base64String.trim()
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ?: throw Exception("Failed to decode bitmap")
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error decoding base64: ${e.message}")
            throw e
        }
    }

    /**
     * Redimensiona um bitmap para não exceder as dimensões máximas.
     *
     * @param bitmap Bitmap a redimensionar
     * @return Bitmap redimensionado
     */
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

// Calcular razão de aspeto para manter proporções
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