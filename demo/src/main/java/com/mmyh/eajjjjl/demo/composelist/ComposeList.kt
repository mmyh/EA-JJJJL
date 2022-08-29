package com.mmyh.eajjjjl.demo.composelist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mmyh.eajjjjl.demo.BaseActivity
import com.mmyh.eajjjjl.widget.composelist.IPageReq
import com.mmyh.eajjjjl.widget.composelist.QueryType
import com.mmyh.eajjjjl.widget.composelist.SwipeRefreshLoadMore
import com.mmyh.eajjjjl.widget.composelist.rememberSwipeRefreshLoadMoreState
import com.mmyh.eajjjjl.widget.composelist.List

class ComposeList : BaseActivity() {

    val myReq = MyReq()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContentView()
        }
    }

    @Composable
    fun ContentView(composeListVM: ComposeListVM = viewModel()) {
        val swipeRefreshLoadMoreState =
            rememberSwipeRefreshLoadMoreState<ComposeListVM.Product>(myReq) {
                composeListVM.query(it == QueryType.LoadMore)
            }
        List(
            data = composeListVM.list,
            swipeRefreshLoadMoreState = swipeRefreshLoadMoreState
        ) {
            SwipeRefreshLoadMore(
                swipeRefreshLoadMoreState = it,
                headContent = {
                    Text(
                        text = "head", modifier = Modifier
                            .height(200.dp)
                            .background(Color.Green)
                            .fillMaxWidth()
                    )
                },
                footContent = {
                    Text(
                        text = "foot", modifier = Modifier
                            .height(100.dp)
                            .background(Color.Blue)
                            .fillMaxWidth()
                    )
                }
            ) { index, value ->
                Text(text = value.index ?: "", modifier = Modifier.height(40.dp))
                Row {
                    Text(
                        text = value.name ?: "", modifier = Modifier
                            .height(40.dp)
                            .weight(1.0f)
                    )
                    Text(text = value.price ?: "", modifier = Modifier.height(40.dp))
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Red)
                )
            }
        }
        LaunchedEffect(key1 = Unit) {
            composeListVM.query(false)
        }
    }

    class MyReq : IPageReq {

        var page: Int = 0

        override fun nextPage() {
            page++
        }

        override fun firstPage() {
            page = 0
        }

    }
}