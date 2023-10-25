package com.example.smartalarmclock

import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smartalarmclock.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var ringtone: Ringtone
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRingtone()
    }

    private fun setRingtone() {
        var notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, notification)
        if (ringtone == null) {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(this, notification)
        } else {
            ringtone.isLooping = true
            ringtone.play()
        }
    }

    override fun onResume() {
        super.onResume()
        val task = generateExperssion()
        binding.expressText.text = task
    }

    override fun onDestroy() {
        super.onDestroy()
        if(ringtone!=null && ringtone.isPlaying){
            ringtone.stop()
        }
    }
    fun onClickGetAnswerB(view: View){
        val task = binding.expressText.text.toString()
        if(task.isNotEmpty()){
            val answer = calculateRPN(toRPN(task))
            val userAnswer = binding.answerInput.text.toString()
            if(userAnswer.isNotEmpty()){
                if(answer == userAnswer.toInt()){
                    finish()
                }
                else{
                    binding.answerText.visibility = View.VISIBLE
                    binding.answerText.setTextColor(Color.RED)
                    binding.answerText.text = "НЕ ВЕРНО!\n"
                }
            }
            binding.expressText.text = generateExperssion()
            binding.answerInput.setText("")
        }
    }

    private fun generateExperssion(): String{
        var expression: String = ""
        for(i in 0..2){
            expression += (Random.nextInt(9) + 1).toString()
            if(i!=2){
                expression += when(Random.nextInt(3)){
                    0 -> "+"
                    1 -> "-"
                    else -> "*"
                }
            }
        }
        return expression
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

    private fun isNumeric(s: String): Boolean {
        return try {
            s.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun toRPN(expression: String): List<String> {
        val output = mutableListOf<String>()
        val stack = mutableListOf<Char>()

        for (c in expression) {
            when {
                c.isDigit() -> output.add(c.toString())
                c == '(' -> stack.add(c)
                c == ')' -> {
                    while (stack.isNotEmpty() && stack.last() != '(') {
                        output.add(stack.removeLast().toString())
                    }
                    stack.removeLastOrNull()
                }
                else -> {
                    while (stack.isNotEmpty() && stack.last().priority >= c.priority) {
                        output.add(stack.removeLast().toString())
                    }
                    stack.add(c)
                }
            }
        }

        while (stack.isNotEmpty()) {
            output.add(stack.removeLast().toString())
        }

        return output
    }

    private val Char.priority: Int
        get() = when (this) {
            '+', '-' -> 1
            '*' -> 2
            else -> 0
        }
}