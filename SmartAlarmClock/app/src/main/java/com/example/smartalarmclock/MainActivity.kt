package com.example.smartalarmclock

import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.smartalarmclock.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var ringtone: Ringtone
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setAlarmClockB.setOnClickListener {
            val i= Intent(this, AlarmActivity::class.java)
            startActivity(i)
        }

        var notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, notification)
        if(ringtone == null){
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(this, notification)
        }
        else{
            ringtone.isLooping = true
            ringtone.play()
        }
    }
    override fun onResume() {
        super.onResume()
        val task = generateExperssion()
        binding.expressText.text = task
    }

    override fun onStop() {
        super.onStop()
        if(ringtone!=null && ringtone.isPlaying){
            ringtone.stop()
        }
    }
    fun onClickGetAnswerB(view: View){
        val task = binding.expressText.text.toString()
        val express = toRPN(task)
        for(str in express)
            Log.d("RPN", str)
        val answer = calculateRPN(express)
        binding.answerText.visibility = View.VISIBLE
        if(answer == binding.answerInput.text.toString().toInt()){
            binding.answerText.setTextColor(Color.GREEN)
            binding.answerText.text = "ВЕРНО"
        }
        else{
            binding.answerText.setTextColor(Color.RED)
            binding.answerText.text = "НЕ ВЕРНО!\nОтвет: " + answer.toString()
        }
        binding.expressText.text = generateExperssion()
        binding.answerInput.setText("")
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