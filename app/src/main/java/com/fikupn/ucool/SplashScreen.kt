package com.fikupn.ucool

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fikupn.ucool.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private val timeOut:Long = 2000
    private lateinit var bindingSplashScreen: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSplashScreen = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(bindingSplashScreen.root)

        supportActionBar?.hide()

        bindingSplashScreen.ivIcLogo.animate().setDuration(timeOut).alpha(1f).withEndAction{
            startActivity(Intent(this,LogIn::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}