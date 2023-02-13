package com.karthek.android.s.pdfviewer.ui.screens

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import com.karthek.android.s.pdfviewer.R
import com.karthek.android.s.pdfviewer.helper.PDFFile

@Composable
fun PDFViewerScreen(pdfFile: PDFFile, callback: () -> Unit) {
	Scaffold(
		topBar = { ViewerTopBar(name = pdfFile.name, callback = callback) },
		modifier = Modifier.navigationBarsPadding(bottom = false)
	) {
		//ViewerContent(pdfFile.pageCount)
		PdfiumContent(uri = pdfFile.uri)
	}
}

@Composable
fun PdfiumContent(uri: Uri) {
	val bgColor =
		if (MaterialTheme.colors.isLight) android.graphics.Color.LTGRAY else android.graphics.Color.BLACK
	AndroidView(factory = { context ->
		PDFView(context, null)
	}, modifier = Modifier.padding(horizontal = 16.dp)) { pdfView ->
		pdfView.layoutParams = ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT
		)
		pdfView.setBackgroundColor(bgColor)
		pdfView.useBestQuality(true)
		pdfView.fromUri(uri)
			.scrollHandle(DefaultScrollHandle(pdfView.context))
			.spacing(16)
			.load()
	}
}

@Composable
fun ViewerTopBar(name: String = stringResource(id = R.string.app_name), callback: () -> Unit) {
	TopAppBar(
		title = { Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
		contentPadding = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.statusBars),
		navigationIcon = {
			Icon(
				imageVector = Icons.Outlined.ArrowBack,
				contentDescription = "",
				modifier = Modifier
					.clickable(onClick = callback)
					.padding(start = 12.dp, top = 2.dp)
			)
		},
		actions = { ViewerActions() },
		backgroundColor = MaterialTheme.colors.surface,
		elevation = 8.dp
	)
}

@Composable
fun ViewerActions() {
	val context = LocalContext.current
	var expanded by remember { mutableStateOf(false) }
	CompositionLocalProvider(
		LocalContentAlpha provides ContentAlpha.high
	) {
		IconButton(onClick = { /*TODO*/ }) {
			Icon(imageVector = Icons.Outlined.Search, contentDescription = "")
		}
		Box {
			IconButton(onClick = { expanded = true }) {
				Icon(
					imageVector = Icons.Outlined.MoreVert,
					contentDescription = "",
					modifier = Modifier
						.padding(start = 8.dp, end = 16.dp)
				)
			}
			ViewerActionsMenu(expanded) { expanded = false }
		}
	}
}

@Composable
fun ViewerActionsMenu(expanded: Boolean, onDismissRequest: () -> Unit) {
	DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
		ViewerActionsMenuItem(Icons.Outlined.Share, text = "Share") {}
		ViewerActionsMenuItem(Icons.Outlined.ExitToApp, text = "Open with...") {}
		ViewerActionsMenuItem(Icons.Outlined.SaveAlt, text = "Save as") {}
		ViewerActionsMenuItem(Icons.Outlined.Print, text = "Print") {}
		ViewerActionsMenuItem(Icons.Outlined.Info, text = "About") {}
	}
}

@Composable
fun ViewerActionsMenuItem(imageVector: ImageVector, text: String, onClick: () -> Unit) {
	DropdownMenuItem(onClick = onClick) {
		Icon(imageVector = imageVector, contentDescription = text)
		Text(text = text, softWrap = false, modifier = Modifier.padding(start = 16.dp, end = 48.dp))
	}
}

@Composable
fun ViewerContent(pageCount: Int) {
	val bgColor = if (MaterialTheme.colors.isLight) Color.LightGray else Color.Black
	LazyColumn(modifier = Modifier.background(bgColor)) {
		items(pageCount) { i ->
			PageView(pageNum = i)
		}
	}
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PageView(pageNum: Int) {
	val painter = rememberImagePainter(data = pageNum)
	Image(
		painter = painter,
		contentDescription = "",
		modifier = Modifier
			.padding(horizontal = 16.dp, vertical = 6.dp)
			.heightIn(max = 460.dp)
	)
}