package com.mmyh.eajjjjl.widget

import androidx.compose.runtime.Composable

@Composable
fun ComposableWrapper(content: @Composable () -> Unit) {
    content()
}