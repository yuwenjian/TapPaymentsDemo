package com.ywj.tappayment.demo.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Alignment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.taptap.payment.api.BillingFlowParams
import com.taptap.payment.api.BillingFlowParams.ProductDetailsParams
import com.taptap.payment.api.FinishPurchaseParams
import com.taptap.payment.api.FinishPurchaseResponseListener
import com.taptap.payment.api.ITapPayment.ProductType
import com.taptap.payment.api.ProductDetailsResponseListener
import com.taptap.payment.api.PurchaseUpdatedListener
import com.taptap.payment.api.PurchasesResponseListener
import com.taptap.payment.api.QueryProductDetailsParams
import com.taptap.payment.api.QueryProductDetailsParams.Product
import com.taptap.payment.api.TapPayment
import com.taptap.payment.api.bean.ProductDetails
import com.taptap.payment.api.bean.Purchase
import com.taptap.payment.api.bean.TapPaymentResponseCode
import com.taptap.payment.api.bean.TapPaymentResult
import com.ywj.tappayment.demo.adapter.OrderAdapter
import com.ywj.tappayment.demo.adapter.ProductAdapter
import com.ywj.tappayment.demo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    private var productDetails: ProductDetails? = null
    private var tapPayment: TapPayment? = null
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1-> {
                    // 显示商品列表
                    showdata(msg.obj as List<ProductDetails>)
                }
                2 -> {
                    showNoFinishOrder(msg.obj as List<Purchase>)
                }
                3 -> {
                    dealOrder(msg.obj as Purchase)
                }
                4 -> {
                   Toast.makeText(this@MainActivity, msg.obj as String , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

          this.tapPayment = TapPayment.newBuilder(this) // 将会用来获取 ApplicationContext.
            .setClientId("您的 ClientId") // 必填，开发者中心对应 Client ID
            .setClientToken("您的 Client Token") // 必填，开发者中心对应 Client Token
            .build()

        // 查询商品列表
        findProduct(tapPayment)
        getNoFinishOrerList()

        binding.noFinishButton.setOnClickListener {
          getNoFinishOrerList()

        }

    }

    /**
     * 去完成订单
     */
    private fun dealOrder(purchase:Purchase) {
        var params: FinishPurchaseParams = FinishPurchaseParams.newBuilder()
            .setId(purchase.getOrderId()) // Required
            .setOrderToken(purchase.getOrderToken()) // Required
            .setPurchaseToken(purchase.getPurchaseToken()) // Required
            .build();
        tapPayment?.finishPurchaseAsync(params, object: FinishPurchaseResponseListener {
            override fun onFinishPurchaseResponse( result: TapPaymentResult,  purchase: Purchase) {
                if (result.responseCode == TapPaymentResponseCode.OK){
                    var msg = Message()
                    msg.what = 4
                    msg.obj = "处理完成！"
                    mHandler.sendMessage(msg)

                }
            }
        });
    }

    /**
     * 获取未完成的订单列表
     */
    private fun getNoFinishOrerList() {
        tapPayment?.queryUnfinishedPurchaseAsync(object: PurchasesResponseListener {
            override fun onQueryPurchasesResponse( result: TapPaymentResult,  purchases: List<Purchase>) {

                if(result?.responseCode == TapPaymentResponseCode.OK && purchases!!.size >=0){

                    var msg = Message()
                    msg.what = 2
                    msg.obj = purchases
                    mHandler.sendMessage(msg)

                }
            }
        });
    }


    /**
     *
     * 展示未完成的订单
     */
    fun showNoFinishOrder(purchases: List<Purchase>) {
        var orderAdapter = OrderAdapter()
        var linearLayoutManager = LinearLayoutManager(this@MainActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        orderAdapter.addData(purchases)
        binding.orderRecycle.layoutManager = linearLayoutManager
        binding.orderRecycle.adapter = orderAdapter

        orderAdapter.setOnItemListener(object : OrderAdapter.OnItemListener {
            override fun itemClick(data: Purchase) {
                var msg = Message()
                msg.what = 3
                msg.obj = data
                mHandler.sendMessage(msg)
                Log.e("TAG", "=======::::"+data.toString()  )
            }

        })
    }


    /**
     * 查询商品详情
     */
    private fun findProduct(tapPayment: TapPayment?) {

        val queryProductList: ArrayList<Product> = ArrayList()
        var products: ArrayList<String> = ArrayList()
        products.add("test_01")
        products.add("test_02")
        products.add("Xtest_01")
        products.add("Xtest_02")

        for (i in 0 until products.size) {
            val productId: String = products[i]
            val product = Product.newBuilder()
                .setProductId(productId)
                .setProductType(ProductType.INAPP)
                .build()
            queryProductList.add(product)
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(queryProductList)
            .build()

        tapPayment?.queryProductDetailsAsync(params, object: ProductDetailsResponseListener {
            override fun onProductDetailsResponse(
                p0: TapPaymentResult?,
                productList: List<ProductDetails>?,
                p2: MutableList<String>?
            ) {

                productDetails  = productList?.get(0)
                if(p0?.responseCode == TapPaymentResponseCode.OK && productList!!.size >=0){

                    var msg = Message()
                    msg.what = 1
                    msg.obj = productList
                    mHandler.sendMessage(msg)

                }


            }

        })

    }


    /**
     * 显示商品列表
     */
    fun showdata(data: List<ProductDetails>?){
        var adapter = ProductAdapter()
        var layoutManager = GridLayoutManager(this, 2)
        binding.productRecycle.setLayoutManager(layoutManager)
        binding.productRecycle.adapter = adapter
        adapter.addData(data)

        adapter.setOnItemListener(object : ProductAdapter.OnItemListener {
            override fun itemClick(productDetails1: ProductDetails) {

                buyProduct(tapPayment,productDetails1 )

            }
        })
    }

    /**
     * 购买商品
     */
    private fun buyProduct(tapPayment: TapPayment?, productDetails: ProductDetails) {
        var activity: Activity = this
        val productDetailsParams = ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParams(productDetailsParams)
            .setObfuscatedAccountId("my_userId")
            .build()
        val result = tapPayment!!.launchBillingFlow(activity, billingFlowParams, object: PurchaseUpdatedListener {
            override fun onPurchaseUpdated(result: TapPaymentResult?, purchases: Purchase?) {
                if (result?.responseCode == TapPaymentResponseCode.OK
                    && purchases != null) {
                    Log.e("TAG", "购买成功： " + purchases.toString() )

//                    dealOrder(purchases)

                } else if (result?.responseCode == TapPaymentResponseCode.USER_CANCELED) {

                    Log.e("TAG", "购买取消： ")

                } else {
                    Log.e("TAG", "购买其他的异常： ")


                }
            }

        })


        Log.e("TAG", "buyProduct: "+result.toString() )




    }

}