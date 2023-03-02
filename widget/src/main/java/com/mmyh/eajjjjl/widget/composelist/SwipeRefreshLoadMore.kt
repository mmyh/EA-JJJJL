package com.mmyh.eajjjjl.widget.composelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.ArrayList

@Composable
fun <T> SwipeRefreshLoadMore(
    modifier: Modifier = Modifier,
    swipeEnabled: Boolean = true,
    refreshTriggerDistance: Dp = 80.dp,
    indicatorAlignment: Alignment = Alignment.TopCenter,
    indicatorPadding: PaddingValues = PaddingValues(0.dp),
    indicator: @Composable (state: SwipeRefreshState, refreshTrigger: Dp) -> Unit = { s, trigger ->
        SwipeRefreshIndicator(
            s,
            trigger,
            backgroundColor = Color.White,
            contentColor = Color.Black
        )
    },
    clipIndicatorToPadding: Boolean = true,
    itemDecoration: Dp = 10.dp,
    swipeRefreshLoadMoreState: SwipeRefreshLoadMoreState<T>,
    loadMoreContent: @Composable (LazyItemScope.() -> Unit)? = null,
    emptyContent: @Composable (modifier: Modifier) -> Unit = {},
    netErrorContent: @Composable (modifier: Modifier) -> Unit = {},
    headContent: @Composable (() -> Unit)? = null,
    footContent: @Composable (() -> Unit)? = null,
    renderItem: @Composable (index: Int, t: T) -> Unit
) {
    SwipeRefresh(
        state = swipeRefreshLoadMoreState.swipeRefreshState,
        onRefresh = {
            swipeRefreshLoadMoreState.refresh(true)
        },
        modifier = modifier,
        swipeEnabled = swipeEnabled,
        refreshTriggerDistance = refreshTriggerDistance,
        indicatorAlignment = indicatorAlignment,
        indicatorPadding = indicatorPadding,
        indicator = indicator,
        clipIndicatorToPadding = clipIndicatorToPadding
    ) {
        val coroutineScope = rememberCoroutineScope()
        val scrollState = swipeRefreshLoadMoreState.scrollState
        val isReachedBottom by remember {
            derivedStateOf {
                (scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size == scrollState.layoutInfo.totalItemsCount)
                        && !swipeRefreshLoadMoreState.swipeRefreshState.isRefreshing
            }
        }
        LaunchedEffect(Unit) {
            snapshotFlow { isReachedBottom }
                .collect { isReached ->
                    if (isReached
                        && swipeRefreshLoadMoreState.canLoadMore
                    ) {
                        swipeRefreshLoadMoreState.canLoadMore = false
                        swipeRefreshLoadMoreState.swipeRefreshState
                        swipeRefreshLoadMoreState.loadMore()
                    }
                }
        }
        LazyColumn(state = scrollState) {
            swipeRefreshLoadMoreState.refresh(false)
            item {
                var tmpM: Modifier = Modifier
                if (swipeRefreshLoadMoreState.data.size == 0) {
                    tmpM = tmpM.fillParentMaxHeight()
                }
                Column(
                    modifier = tmpM
                ) {
                    if (headContent != null) {
                        headContent()
                    }
                    if (swipeRefreshLoadMoreState.data.size == 0 && swipeRefreshLoadMoreState.dataSetted) {
                        if (swipeRefreshLoadMoreState.isNetError) {
                            netErrorContent(
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            )
                        } else {
                            emptyContent(
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
            items(count = swipeRefreshLoadMoreState.data.size) { index ->
                Column(Modifier.fillMaxWidth()) {
                    if (index == 0) {
                        Spacer(modifier = Modifier.height(itemDecoration))
                    }
                    renderItem(index, swipeRefreshLoadMoreState.data[index])
                    Spacer(modifier = Modifier.height(itemDecoration))
                }
            }
            if (swipeRefreshLoadMoreState.canLoadMore) {
                item {
                    if (loadMoreContent == null) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp, 24.dp),
                                color = Color.Red,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = "加载更多",
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        loadMoreContent()
                    }
                }
            } else {
                if (footContent != null
                    && swipeRefreshLoadMoreState.dataSetted
                    && swipeRefreshLoadMoreState.data.size > 0
                ) {
                    item {
                        footContent()
                    }
                }
            }
            if (swipeRefreshLoadMoreState.scrollToFirst) {
                swipeRefreshLoadMoreState.scrollToFirst = false
                coroutineScope.launch {
                    scrollState.scrollToItem(0, 0)
                }
            }
        }
    }
}

@Composable
fun <T> rememberSwipeRefreshLoadMoreState(
    pageReq: IPageReq,
    query: (queryType: QueryType) -> Unit,
    onDataUpdated: ((MutableList<T>) -> Unit)? = null,
): SwipeRefreshLoadMoreState<T> {
    return remember {
        SwipeRefreshLoadMoreState(pageReq, query, onDataUpdated)
    }
}

class SwipeRefreshLoadMoreState<T>(
    private var pageReq: IPageReq,
    internal var query: (queryType: QueryType) -> Unit,
    private var onDataUpdated: ((MutableList<T>) -> Unit)? = null,
) {

    internal var canLoadMore: Boolean = false

    internal var data = mutableStateListOf<T>()

    internal var queryType: QueryType = QueryType.Init

    internal val swipeRefreshState: SwipeRefreshState = SwipeRefreshState(false)

    internal var dataSetted = false

    internal var isNetError = false

    internal var scrollToFirst = false

    var scrollState = LazyListState()

    fun getData(): MutableList<T> {
        return data
    }

    fun setData(listResponse: IPageRes<T>?) {
        dataSetted = true
        if (QueryType.Init == queryType) {
            scrollToFirst = true
        }
        this.isNetError = listResponse?.isNetError() == true
        if (QueryType.LoadMore == queryType) {
            listResponse?.dataList?.let {
                data.addAll(it)
            }
        } else {
            data.clear()
            listResponse?.dataList?.let {
                data.addAll(it)
            }
        }
        listResponse?.let {
            canLoadMore = it.hasNextPage()
        }
        onDataUpdated?.invoke(data.toMutableList())
    }

    fun updateData(t: T, index: Int) {
        queryType = QueryType.Null
        val src = (t as Any).javaClass
        val tmp = src.newInstance()
        val dest = tmp.javaClass
        cloneData(dest, src, tmp, t)
        data[index] = tmp as T
    }

    private fun cloneData(dest: Class<Any>, src: Class<Any>, destObj: Any, srcObj: Any) {
        dest.declaredFields.forEach {
            it.isAccessible = true
            val srcFiled = src.getDeclaredField(it.name)
            srcFiled.isAccessible = true
            it.set(destObj, srcFiled.get(srcObj))
        }
        if (dest.superclass != null) {
            cloneData(dest.superclass!!, src.superclass!!, destObj, srcObj)
        }
    }

    internal fun refresh(isRefreshing: Boolean) {
        swipeRefreshState.isRefreshing = isRefreshing
        if (isRefreshing) {
            queryType = QueryType.Refresh
            pageReq.firstPage()
            query(queryType)
        }
    }

    internal fun loadMore() {
        queryType = QueryType.LoadMore
        pageReq.nextPage()
        query(queryType)
    }

    fun initQuery() {
        queryType = QueryType.Init
        pageReq.firstPage()
        query(queryType)
    }
}

interface IPageRes<T> {

    fun hasNextPage(): Boolean

    fun isNetError(): Boolean

    val dataList: ArrayList<T>?

}

interface IPageReq {

    fun nextPage()

    fun firstPage()

}

enum class QueryType {
    Init, Refresh, LoadMore, Null;
}