package com.example.phuongnam19973.utils

import com.example.phuongnam19973.utils.Constants.OPEN_GOOGLE
import com.example.phuongnam19973.utils.Constants.OPEN_SEARCH
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {

    fun basicResponses(_message: String): String {

        val random = (0..2).random()
        val message = _message.toLowerCase()

        return when {

            // Lật đồng xu
            message.contains("chơi") && message.contains("lật đồng xu") -> {
                val r = (0..1).random()
                val result = if (r == 0) "mặt sấp" else "mặt ngửa"

                "Mình đã lật đồng xu và kết quả là $result"
            }

            // Giải toán
            message.contains("giải toán") -> {
                val equation = _message.substringAfter("giải toán")
                try {
                    val result = SolveMath.solveMath(equation.trim())
                    "$result"
                } catch (e: Exception) {
                    "Xin lỗi, mình không thể giải được bài toán này."
                }
            }

            // Chào hỏi
            message.contains("xin chào") -> {
                when (random) {
                    0 -> "Chào bạn!"
                    1 -> "Xin chào"
                    2 -> "Buổi sáng tốt lành!"
                    else -> "Lỗi"
                }
            }

            // Hỏi thăm
            message.contains("bạn khoẻ không") -> {
                when (random) {
                    0 -> "Mình ổn, cảm ơn bạn!"
                    1 -> "Mình đang đói..."
                    2 -> "Mình khá ổn! Còn bạn thì sao?"
                    else -> "Lỗi"
                }
            }

            // Hỏi giờ
            message.contains("bây giờ mấy giờ")  -> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            // Mở Google
            message.contains("mở") && message.contains("google") -> {
                OPEN_GOOGLE
            }

            // Tìm kiếm trên internet
            message.contains("tìm cho tôi") -> {
                OPEN_SEARCH
            }

            // Trường hợp mua đồ ở đây có tốt không
            message.contains("mua đồ ở đây có tốt không") -> {
                when (random) {
                    0 -> "Bạn cứ yên tâm nhé, đồ ở đây rất chất lượng!"
                    1 -> "Rất tốt là đằng khác..."
                    2 -> "Cũng tạm"
                    else -> "Lỗi"
                }
            }

            // Trường hợp mua đồ ở đây có rẻ không
            message.contains("mua đồ ở đây có rẻ không") -> {
                when (random) {
                    0 -> "Đồ ở đây khá rẻ đấy!"
                    1 -> "Giá hợp lý, bạn nên thử"
                    2 -> "Giá cả phải chăng, không quá đắt"
                    else -> "Lỗi"
                }
            }

            // Trường hợp hỏi về sản phẩm linh kiện điện tử
            message.contains("linh kiện điện tử") -> {
                when (random) {
                    0 -> "Linh kiện điện tử ở đây rất đa dạng và chất lượng!"
                    1 -> "Chúng tôi có nhiều linh kiện điện tử, từ các bộ phận máy tính đến điện thoại."
                    2 -> "Các linh kiện điện tử của chúng tôi được nhập khẩu chính hãng, bảo hành tốt."
                    else -> "Lỗi"
                }
            }

            // Trường hợp hỏi về giá linh kiện điện tử
            message.contains("giá linh kiện điện tử") -> {
                when (random) {
                    0 -> "Giá linh kiện điện tử dao động tùy thuộc vào loại sản phẩm, bạn có thể tham khảo thêm!"
                    1 -> "Các linh kiện điện tử có giá từ vài nghìn đến vài triệu đồng, tùy vào loại sản phẩm."
                    2 -> "Giá linh kiện sẽ được báo cụ thể khi bạn lựa chọn sản phẩm."
                    else -> "Lỗi"
                }
            }

            // Trường hợp về chất lượng linh kiện điện tử
            message.contains("chất lượng linh kiện điện tử") -> {
                when (random) {
                    0 -> "Linh kiện điện tử ở đây rất bền và đạt tiêu chuẩn quốc tế!"
                    1 -> "Chất lượng linh kiện điện tử của chúng tôi luôn được đảm bảo với các thương hiệu nổi tiếng."
                    2 -> "Chất lượng rất tốt, bạn hoàn toàn có thể yên tâm khi sử dụng."
                    else -> "Lỗi"
                }
            }
            message.contains("giá thay vỏ samsung m35") -> {
                when (random) {
                    0 -> "Giá thay vỏ samsung m35 hiện tại là 99.999 VND ạ!"
                    1 -> "Giá thay vỏ samsung m35 hiện tại là 99.999 VND! Bạn có thể tham khảo trong của hàng của tôi!"
                    2 -> "Giá thay vỏ samsung m35 hiện tại là 99.999 VND! Cam kết sản phẩm chất lượng, giá cả hợp lí."
                    else -> "Lỗi"
                }
            }
            message.contains("giá") -> {
                when (random) {
                    0 -> "Bạn có thể cho mình biết sản phẩm cụ thể để mình đưa giá nhé!!!"
                    1 -> "Bạn muốn xem giá của sản phẩm nào ạ!"
                    2 -> "Bạn muốn xem giá của sản phẩm nào ạ!"
                    else -> "Lỗi"
                }
            }
            message.contains("hôm nay doanh thu thế nào") -> {
                when (random) {
                    0 -> "Hôm nay doanh thu không tệ!!!"
                    1 -> "Hôm nay doanh thu được trên 500.000 VND!!!"
                    2 -> "Hôm nay doanh thu gần 2.000.000 VND!!!"
                    else -> "Lỗi"
                }
            }


            // Trường hợp gây lỗi nhưng xử lý bằng try-catch
            message.contains("gây lỗi") -> {
                try {
                    val number: Int? = null
                    // Gây lỗi khi truy cập vào biến null
                    val result = number!!.toString()  // Sẽ ném NullPointerException
                    result
                } catch (e: NullPointerException) {
                    "Có lỗi xảy ra, vui lòng thử lại sau."
                }
            }

            // Khi bot không hiểu
            else -> {
                when (random) {
                    0 -> "Mình không hiểu..."
                    1 -> "Hãy thử hỏi mình một câu hỏi khác"
                    2 -> "Mình không biết"
                    else -> "Lỗi"
                }
            }
        }
    }
}
