package com.example.phuongnam19973.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.phuongnam19973.Model.Order
import com.example.phuongnam19973.Model.Product
import com.example.phuongnam19973.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.*
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticsActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart  // Thay đổi thành BarChart

    // Tạo một danh sách để lưu trữ tên sản phẩm
    private val productNames = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Khởi tạo BarChart
        barChart = findViewById(R.id.rlineChart)  // Đảm bảo rằng ID trong layout là đúng

        // Lấy dữ liệu từ Firebase
        getOrderData()
    }

    private fun getOrderData() {
        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("orders")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                snapshot.children.forEach {
                    val order = it.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }

                // Sau khi có danh sách đơn hàng, thực hiện thống kê tất cả sản phẩm
                calculateProductStats(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun calculateProductStats(orders: List<Order>) {
        val productCounts = mutableMapOf<String, Int>()

        // Duyệt qua các đơn hàng và tính số lượng mỗi sản phẩm
        for (order in orders) {
            for (item in order.items) {
                val currentQuantity = productCounts[item.id] ?: 0
                productCounts[item.id] = currentQuantity + item.quantity
            }
        }

        // Lấy thông tin tên sản phẩm từ Firebase
        getProductNames(productCounts.keys) {
            // Sau khi có tên sản phẩm, cập nhật biểu đồ
            updateChartWithProductData(productCounts)
        }
    }

    private fun getProductNames(productIds: Set<String>, callback: () -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        // Lấy thông tin sản phẩm từ Firebase
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val product = it.getValue(Product::class.java)
                    if (product != null && productIds.contains(product.id)) {
                        // Lưu tên sản phẩm vào danh sách
                        productNames[product.id] = product.name
                    }
                }

                // Gọi callback sau khi lấy xong tên sản phẩm
                callback()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun updateChartWithProductData(productCounts: Map<String, Int>) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        var index = 0f

        // Duyệt qua các sản phẩm và thêm vào danh sách biểu đồ
        for ((productId, quantity) in productCounts) {
            val productName = productNames[productId] ?: "Không rõ"  // Nếu không có tên sản phẩm, hiển thị "Không rõ"
            entries.add(BarEntry(index++, quantity.toFloat())) // Dữ liệu cột
            labels.add(productName) // Lưu tên sản phẩm
        }

        // Tạo BarDataSet từ các entries
        val dataSet = BarDataSet(entries, "Sản phẩm")
        dataSet.color = resources.getColor(R.color.blue)  // Đặt màu đường biểu đồ

        // Bật hiển thị giá trị chú thích trên các điểm dữ liệu
        dataSet.setDrawValues(true)
        dataSet.setValueTextColor(resources.getColor(R.color.black))  // Đặt màu cho chú thích
        dataSet.setValueTextSize(10f)  // Đặt kích thước chữ chú thích

        // Áp dụng ValueFormatter tùy chỉnh cho BarDataSet
        dataSet.valueFormatter = ProductValueFormatter(labels)

        val barData = BarData(dataSet)

        // Cập nhật BarChart với dữ liệu
        barChart.data = barData
        barChart.invalidate() // Cập nhật biểu đồ

        // Thiết lập các label cho các điểm trên trục X
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Thiết lập số lượng label trên trục X không bị chồng chéo
        barChart.xAxis.granularity = 1f // Đảm bảo mỗi item trên trục X có một label
        barChart.xAxis.isGranularityEnabled = true
        barChart.xAxis.setLabelCount(labels.size, true)

        // Xoay nhãn trên trục X để chúng không bị chồng lên nhau
        barChart.xAxis.setLabelRotationAngle(45f) // Xoay nhãn 45 độ

        // Thiết lập kích thước font trục X nhỏ hơn
        barChart.xAxis.textSize = 8f  // Giảm kích thước font chữ
    }

    // Lớp ValueFormatter tùy chỉnh
    class ProductValueFormatter(private val labels: List<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val productName = labels.getOrNull(value.toInt()) ?: "Không rõ"  // Lấy tên sản phẩm từ labels
            return "$productName: ${value.toInt()}"  // Hiển thị tên sản phẩm và số lượng
        }
    }

}
