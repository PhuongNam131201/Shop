package com.example.phuongnam19973.utils

import net.objecthunter.exp4j.ExpressionBuilder

object SolveMath {

    fun solveMath(equation: String): Double {
        try {
            val expression = ExpressionBuilder(equation).build()
            return expression.evaluate()
        } catch (e: Exception) {
            println("Xin lỗi tôi không thể giải bài này, nó quá khó")
            return 0.0
        }
    }
}
