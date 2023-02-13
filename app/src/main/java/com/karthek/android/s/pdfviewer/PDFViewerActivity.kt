package com.karthek.android.s.pdfviewer

import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import coil.Coil
import coil.ImageLoader
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karthek.android.s.pdfviewer.helper.PDFFile
import com.karthek.android.s.pdfviewer.helper.PDFPageFetcher
import com.karthek.android.s.pdfviewer.ui.screens.PDFViewerScreen
import com.karthek.android.s.pdfviewer.ui.theme.PDFViewerTheme
import java.io.IOException

class PDFViewerActivity : ComponentActivity() {

	private var pdfFile: PDFFile? = null
	private var renderer: PdfRenderer? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		try {
			pdfFile = getPdfFile()
		} catch (e: Exception) {
			e.printStackTrace()
			finish()
		}
		Coil.setImageLoader {
			ImageLoader.Builder(this)
				.componentRegistry {
					add(PDFPageFetcher(renderer!!))
				}
				.build()
		}
		setContent {
			PDFViewerTheme {
				Surface(color = MaterialTheme.colors.background) {
					val systemUiController = rememberSystemUiController()
					val useDarkIcons = MaterialTheme.colors.isLight
					SideEffect {
						systemUiController.setSystemBarsColor(Color.Transparent, useDarkIcons)
					}
					ProvideWindowInsets {
						PDFViewerScreen(pdfFile = pdfFile!!) {
							finish()
						}
					}
				}
			}
		}
	}

	override fun onDestroy() {
		renderer?.close()
		super.onDestroy()
	}

	private fun getPdfFile(): PDFFile {
		val uri = intent.data ?: throw IllegalArgumentException()
		val fd = contentResolver.openFileDescriptor(uri, "r")!!
		val cursor =
			contentResolver.query(uri, null, null, null, null) ?: throw IllegalArgumentException()
		val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
		val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
		cursor.moveToFirst()
		val name = cursor.getString(nameIndex)
		val size = cursor.getLong(sizeIndex)
		cursor.close()
		renderer = PdfRenderer(fd)
		val pageCount = renderer?.pageCount ?: throw IOException()
		return PDFFile(uri, name, pageCount, size)
	}
}