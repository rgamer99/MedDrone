package com.rgoel.meddrone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainKotlinActivity : AppCompatActivity(), FragmentNavigation {
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kotlin)
        fAuth = Firebase.auth

        val currentUser = fAuth.currentUser
        if(currentUser != null){
            openHomeActivity()
        }else{
            supportFragmentManager.beginTransaction()
                .add(R.id.container,LoginHomeFragment())
                .commit()
        }
    }

    private fun openHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java).apply {        }
        startActivity(intent)
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,fragment)

        if (addToStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}