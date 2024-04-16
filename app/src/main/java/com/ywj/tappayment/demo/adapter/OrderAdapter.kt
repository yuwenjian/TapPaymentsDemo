package com.ywj.tappayment.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taptap.payment.api.bean.Purchase
import com.ywj.tappayment.demo.R

/**
 * 2024/4/16
 * Describe：
 */
class OrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var purchaseList: MutableList<Purchase> = ArrayList()


    fun addData(data: List<Purchase>?) {
        if (data != null) {
            purchaseList.clear()
            purchaseList.addAll(data)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return purchaseList.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myViewHolder = holder as MyViewHolder
        myViewHolder.setData(purchaseList[position], position)

    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderId: TextView
        private val orderProduct: TextView
        private val orderToken: TextView
        private val purchaseToken: TextView
        private val delay: Button


        init {
            orderId = itemView.findViewById(R.id.order_id)
            orderProduct = itemView.findViewById(R.id.order_product)
            orderToken = itemView.findViewById(R.id.order_token)
            purchaseToken = itemView.findViewById(R.id.purchase_token)
            delay = itemView.findViewById(R.id.delay)
        }

        fun setData(purchase: Purchase, position: Int) {
            orderId.text = "orderId: "+purchase.orderId
            orderProduct.text = "商品名: "+purchase.productId
            orderToken.text = "orderToken: "+purchase.orderToken
            purchaseToken.text = "purchaseToken: "+purchase.purchaseToken
            delay.setOnClickListener {
                onItemListener1?.itemClick(purchase)
            }
        }




    }


    var onItemListener1: OnItemListener? = null

    fun setOnItemListener(onItemListener: OnItemListener?) {
        this.onItemListener1 = onItemListener
    }


    interface OnItemListener {
        fun itemClick( data: Purchase)
    }


}