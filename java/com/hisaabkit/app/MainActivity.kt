package com.hisaabkit.app

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)

        textView.text = "HisaabKit Test"
        textView.textSize = 24f
        textView.setPadding(40, 80, 40, 40)

        setContentView(textView)
    }
}
