package com.fikupn.ucool

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.fikupn.ucool.databinding.ActivityLogInBinding
import com.fikupn.ucool.helper.Constant
import com.fikupn.ucool.helper.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LogIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var bindingLogIn: ActivityLogInBinding
    private lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingLogIn = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(bindingLogIn.root)

        sharedPref = PreferenceHelper(this)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        bindingLogIn.btnLogin.setOnClickListener {
            val email = bindingLogIn.editTxtEmail.text.toString()
            val password = bindingLogIn.editTxtPassword.text.toString()

            if (email.isEmpty()) {
                bindingLogIn.editTxtEmail.error = "Email harus diisi"
                bindingLogIn.editTxtEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                bindingLogIn.editTxtEmail.error = "Email tidak valid"
                bindingLogIn.editTxtEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                bindingLogIn.editTxtPassword.error = "Password harus diisi"
                bindingLogIn.editTxtPassword.requestFocus()
                return@setOnClickListener
            }

            loginFirebase(email, password)
        }
    }

    override fun onStart() {
        super.onStart()
        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loginFirebase(email: String, password: String) {
        val replaceEmail = email.replace(".", "-")
        var nama = ""
        var mail = ""
        database = FirebaseDatabase.getInstance("https://ucool-ef64f-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
        database.child(replaceEmail).get().addOnSuccessListener {
            if (it.exists()) {
                nama = it.child("nama").value as String
                mail = it.child("email").value as String
            }
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    sharedPref.put(Constant.PREF_EMAIL, mail)
                    sharedPref.put(Constant.PREF_NAMA, nama)
                    sharedPref.put(Constant.PREF_IS_LOGIN, true)
                    Toast.makeText(this, "Selamat Datang $nama", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}