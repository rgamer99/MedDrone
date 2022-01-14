package com.rgoel.meddrone

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class LoginFragment : Fragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var forgot: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_login, container, false)

        username = view.findViewById(R.id.log_username)
        password = view.findViewById(R.id.log_password)
        fAuth = Firebase.auth
        forgot = view.findViewById(R.id.forgot_pwd)

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            validateForm()
        }


        forgot.setOnClickListener {
            forgotpassword()
        }

        return view
    }

    private fun forgotpassword() {
        forgot.setClickable(false);
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter Your Registered Email-Id", icon)
            }

            username.text.toString().isNotEmpty() -> {
                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    Firebase.auth.sendPasswordResetEmail(username.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Password reset email sent.")
                                Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                                forgot.setClickable(true)
                            }
                        }
                } else {
                    username.setError("Please enter valid email-id.", icon)
                }
            }
        }
    }

    private fun validateForm() {
        val icon = AppCompatResources.getDrawable(requireContext(),R.drawable.ic_warning)

        icon?.setBounds(0,0,icon.intrinsicWidth,icon.intrinsicHeight)
        when{
            TextUtils.isEmpty(username.text.toString().trim())->{
                username.setError("Please Enter Username", icon)
                btn_register_reg.isEnabled = false
            }
            TextUtils.isEmpty(password.text.toString().trim())->{
                password.setError("Please Enter Password", icon)
                btn_register_reg.isEnabled = false
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() -> {

                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    firebaseSignIn()
                } else {
                    username.setError("Please enter valid email-id.", icon)
                }
            }
        }
    }

    private fun firebaseSignIn() {
        btn_login.isEnabled = false
        btn_login.alpha = 0.5f
        fAuth.signInWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        activity?.let {
                            val intent = Intent(it, HomeActivity::class.java)
                            it.startActivity(intent)
                        }
                } else {
                    btn_login.isEnabled = true
                    btn_login.alpha = 1f
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}