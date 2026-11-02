package com.example.util

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader()
    private var isScanningEnabled = true

    fun setScanningEnabled(enabled: Boolean) {
        isScanningEnabled = enabled
    }

    override fun analyze(image: ImageProxy) {
        if (!isScanningEnabled) {
            image.close()
            return
        }

        val buffer = image.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)
        val width = image.width
        val height = image.height

        try {
            val source = PlanarYUVLuminanceSource(
                data,
                width,
                height,
                0,
                0,
                width,
                height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val result = reader.decodeWithState(binaryBitmap)
            reader.reset()
            if (result != null && result.text.isNotBlank()) {
                onQrCodeScanned(result.text)
            }
        } catch (_: Exception) {
            // QR not detected in current frame
        } finally {
            image.close()
        }
    }
}
