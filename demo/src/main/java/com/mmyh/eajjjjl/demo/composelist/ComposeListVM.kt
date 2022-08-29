package com.mmyh.eajjjjl.demo.composelist

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmyh.eajjjjl.widget.composelist.IPageRes

class ComposeListVM : ViewModel() {

    var i: Int = 1

    val handler = Handler(Looper.getMainLooper())

    val list: MutableLiveData<Res> = MutableLiveData()

    fun query(loadmore: Boolean) {
        Thread {
            Thread.sleep(2000)
            handler.post {
                val res = Res()
                res.dataList = ArrayList()
                if (!loadmore) {
                    i = 1
                }
                for (index in 0..9) {
                    val product = Product()
                    product.index = i.toString()
                    product.name = System.currentTimeMillis().toString()
                    product.price = (i * 100).toString()
                    res.dataList!!.add(product)
                    i++
                }
                if (i >= 30) {
                    res.hasNextPage = false
                }
                list.value = res
            }
        }.start()
    }

    class Res : IPageRes<Product> {

        var hasNextPage = true

        override fun hasNextPage(): Boolean {
            return hasNextPage
        }

        override var dataList: ArrayList<Product>? = null

    }

    class Product {

        var index: String? = null

        var name: String? = null

        var price: String? = null

    }
}