package com.mmyh.eajjjjl.widget.composelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import com.mmyh.eajjjjl.widget.ComposableWrapper

@Composable
fun <T> List(
    data: MutableLiveData<out IPageRes<T>>,
    swipeRefreshLoadMoreState: SwipeRefreshLoadMoreState<T>,
    swipeRefreshLoadMore: @Composable ((SwipeRefreshLoadMoreState<T>) -> Unit)? = null
) {
    ComposableWrapper {
        val dataState = data.observeAsState()
        dataState.value?.let {
            swipeRefreshLoadMoreState.setData(it)
        }
    }
    swipeRefreshLoadMore?.invoke(swipeRefreshLoadMoreState)
}