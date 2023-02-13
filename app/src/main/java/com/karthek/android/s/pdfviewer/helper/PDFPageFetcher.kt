package com.karthek.android.s.pdfviewer.helper

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import androidx.annotation.Px
import androidx.core.graphics.drawable.toDrawable
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.PixelSize
import coil.size.Size

class PDFPageFetcher(private val renderer: PdfRenderer) : Fetcher<Int> {

	override suspend fun fetch(
		pool: BitmapPool,
		data: Int,
		size: Size,
		options: Options
	): FetchResult {
		val pixelSize = size as PixelSize
		val bitmap = getPdfThumbnail(data, pixelSize.width, pixelSize.height, pool)
		return DrawableResult(bitmap.toDrawable(options.context.resources), true, DataSource.DISK)
	}

	override fun key(data: Int): String {
		return data.toString()
	}

	private fun getPdfThumbnail(
		pageNum: Int,
		@Px width: Int,
		@Px height: Int,
		bitmapPool: BitmapPool
	): Bitmap {
		val page = renderer.openPage(pageNum)
		val bitmap = bitmapPool.get(
			(page.width * 1.7).toInt(),
			(page.height * 1.7).toInt(),
			Bitmap.Config.ARGB_8888
		)
		bitmap.eraseColor(Color.WHITE)
		page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
		page.close()
		return bitmap
	}

}