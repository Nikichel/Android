package com.example.smartalarmclock

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class Tests {

    private fun isNumeric(s: String): Boolean {
        return try {
            s.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
    private fun calculateRPN(expression: List<String>): Int{
        val stack = mutableListOf<Int>()
        for(element in expression){
            if(isNumeric(element))
                stack.add(element.toInt())
            else{
                val b = stack.removeLast()
                val a = stack.removeLast()
                val result = when (element){
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    else -> throw IllegalArgumentException("Unknown operator: $element")
                }
                stack.add(result)
            }
        }
        return stack.last()
    }
    @Test
    fun calculateRPN_shouldReturnCorrectResult() {
        val testCases = listOf(
            listOf("3", "4", "+") to 7,
            listOf("5", "2", "-") to 3,
            listOf("2", "3", "*") to 6
            // Добавьте здесь другие тестовые случаи по мере необходимости
        )

        for ((expression, expectedResult) in testCases) {
            val result = calculateRPN(expression)
            assertEquals(expectedResult, result)
        }
    }
}
