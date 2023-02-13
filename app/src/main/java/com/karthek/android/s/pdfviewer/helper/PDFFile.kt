package com.karthek.android.s.pdfviewer.helper

import android.net.Uri

data class PDFFile(val uri: Uri, val name: String, val pageCount: Int, val size: Long)