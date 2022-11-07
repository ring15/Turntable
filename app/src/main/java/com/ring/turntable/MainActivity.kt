package com.ring.turntable

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var turntable: TurntableView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        turntable = findViewById(R.id.turntable)
        findViewById<Button>(R.id.reset).setOnClickListener(this)
        findViewById<Button>(R.id.continueGame).setOnClickListener(this)
        findViewById<Button>(R.id.input).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.reset -> turntable.resetTagList()
            R.id.continueGame -> turntable.continueLast()
            R.id.input -> {
                val view = View.inflate(this, R.layout.layout_input_tag, null)
                val alert = AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setView(view)
                    .create()
                val inputText = view.findViewById<EditText>(R.id.inputText)
                val confirm = view.findViewById<Button>(R.id.confirm)
                confirm.setOnClickListener {
                    if (TextUtils.isEmpty(inputText.text.toString())) {
                        Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    turntable.addTag(inputText.text.toString())
                    alert.dismiss()
                }
                alert.show()
            }
        }
    }
}