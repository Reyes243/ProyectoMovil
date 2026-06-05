
package com.movil.proyecto

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.Locale

object PdfManager {

    suspend fun generatePurchaseTicket(context: Context, order: OrderData) = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        var y = 50f

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 24f
        titlePaint.color = Color.rgb(101, 123, 104) 
        canvas.drawText("RAÍZ VIVA - TICKET DE COMPRA", 50f, y, titlePaint)
        
        y += 40f
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Folio del pedido: ${order.id}", 50f, y, paint)
        
        y += 20f
        canvas.drawText("Fecha: ${order.date}", 50f, y, paint)

        y += 30f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DETALLES DEL CLIENTE", 50f, y, paint)
        
        y += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        UserManager.currentUser?.let { user ->
            canvas.drawText("Nombre: ${user.fullName}", 50f, y, paint)
            y += 15f
            canvas.drawText("Correo: ${user.email}", 50f, y, paint)
            y += 15f
            canvas.drawText("Dirección: ${user.address}", 50f, y, paint)
        }

        y += 40f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PRODUCTOS", 50f, y, paint)
        canvas.drawText("CANT.", 350f, y, paint)
        canvas.drawText("PRECIO", 420f, y, paint)
        canvas.drawText("SUBTOTAL", 500f, y, paint)
        
        y += 10f
        canvas.drawLine(50f, y, 550f, y, paint)
        
        y += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        for (item in order.items) {
            val nameLimit = if (item.name.length > 30) item.name.substring(0, 27) + "..." else item.name
            canvas.drawText(nameLimit, 50f, y, paint)
            canvas.drawText(item.quantity.toString(), 350f, y, paint)
            canvas.drawText(item.price, 420f, y, paint)
            
            val priceVal = item.price.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
            val subtotal = priceVal * item.quantity
            canvas.drawText("$ ${String.format(Locale.US, "%.2f", subtotal)}", 500f, y, paint)
            
            y += 20f
            if (y > 780) break 
        }

        y += 10f
        canvas.drawLine(50f, y, 550f, y, paint)
        
        y += 30f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 16f
        canvas.drawText("TOTAL A PAGAR: $ ${String.format(Locale.US, "%.2f", order.total)}", 350f, y, paint)

        y += 60f
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("¡Gracias por confiar en Raíz Viva! Tus plantas te lo agradecerán.", 595f/2, y, paint)

        pdfDocument.finishPage(page)

        val fileName = "Ticket_${order.id.takeLast(6)}.pdf"
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ticket guardado en Descargas", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = java.io.File(downloadsDir, fileName)
                val outputStream = java.io.FileOutputStream(file)
                pdfDocument.writeTo(outputStream)
                outputStream.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ticket guardado en Descargas", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("PdfManager", "Error PDF: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al descargar ticket", Toast.LENGTH_SHORT).show()
            }
        } finally {
            pdfDocument.close()
        }
    }
}
