package com.candra.latihanstackwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
    }

    private fun setToolbar(){
        supportActionBar?.apply {
            title = resources.getString(R.string.name_developer)
            subtitle = resources.getString(R.string.app_name)
        }
    }

}