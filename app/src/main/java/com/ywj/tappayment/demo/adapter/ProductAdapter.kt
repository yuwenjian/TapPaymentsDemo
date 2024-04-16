package com.ywj.tappayment.demo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taptap.payment.api.bean.ProductDetails
import com.ywj.tappayment.demo.R

/**
 * 2024/4/16
 * Describe：
 */
class ProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var productList: MutableList<ProductDetails> = ArrayList()


    fun addData(data: List<ProductDetails>?) {
        if (data != null) {
            productList.clear()
            productList.addAll(data)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myViewHolder = holder as MyViewHolder
        myViewHolder.setData(productList[position], position)

    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView
        private val productId: TextView
        private val productType: TextView
        private val productContent: TextView
        private val productPrice: TextView
        private val payButton: Button


        init {
            productName = itemView.findViewById(R.id.product_name)
            productId = itemView.findViewById(R.id.product_id)
            productType = itemView.findViewById(R.id.product_type)
            productContent = itemView.findViewById(R.id.product_content)
            productPrice = itemView.findViewById(R.id.product_price)
            payButton = itemView.findViewById(R.id.pay_button)
        }

        fun setData(productDetails: ProductDetails, position: Int) {
            productName.text = productDetails.name
            productId.text = productDetails.productId
            productType.text = productDetails.productType
            productContent.text = productDetails.description
            productPrice.text = productDetails.oneTimePurchaseOfferDetails.formatterPrice
            payButton.setOnClickListener {

                onItemListener1?.itemClick(productDetails)
            }
        }




    }


    var onItemListener1: OnItemListener? = null

    fun setOnItemListener(onItemListener: OnItemListener?) {
        this.onItemListener1 = onItemListener
    }


    interface OnItemListener {
        fun itemClick( data: ProductDetails)
    }


}