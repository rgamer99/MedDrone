package com.rgoel.meddrone

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Property
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var cnfPassword: EditText
    private lateinit var user_name: EditText
    private lateinit var fAuth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_register, container, false)

        username = view.findViewById(R.id.reg_username)
        password = view.findViewById(R.id.reg_password)
        cnfPassword = view.findViewById(R.id.reg_cnf_password)
        user_name = view.findViewById(R.id.reg_name)
        fAuth = Firebase.auth

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")

        view.findViewById<Button>(R.id.btn_login_reg).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(), false)
        }

        view.findViewById<Button>(R.id.btn_register_reg).setOnClickListener {
            validateEmptyForm()
        }
        return view
    }

    private fun validateEmptyForm() {
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
            TextUtils.isEmpty(cnfPassword.text.toString().trim())-> {
                cnfPassword.setError("Please Re-enter Password", icon)
                btn_register_reg.isEnabled = false
            }
            TextUtils.isEmpty(user_name.text.toString().trim())-> {
                user_name.setError("Please Enter Your Name", icon)
                btn_register_reg.isEnabled = false
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfPassword.text.toString().isNotEmpty() &&
                    user_name.text.toString().isNotEmpty() ->
            {
                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){
                    if(password.text.toString().length>=6){
                        if(password.text.toString().matches(Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"))){
                            if(password.text.toString() == cnfPassword.text.toString()){
                                firebaseSignUp()
                            }else{
                                cnfPassword.setError("Password doesn't match.", icon)
                            }
                        } else{
                            password.setError("Password should contain at least 1 letter, 1 number, 1 special character and should have no whitespace. ", icon)
                        }
                    }else{
                        password.setError("Password should be at least 6 characters long.", icon)
                    }
                }   else{
                    username.setError("Please enter valid email-id.", icon)
                }
            }
        }
    }

    private fun firebaseSignUp() {
        btn_register_reg.isEnabled = false
        btn_register_reg.alpha = 0.5f
        fAuth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString()).addOnCompleteListener{
            task->
            if(task.isSuccessful){
                val user = Firebase.auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = user_name.text.toString()
                }

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context,"Registration Successful", Toast.LENGTH_SHORT).show()
                            activity?.let{
                                val intent = Intent (it, HomeActivity::class.java)
                                it.startActivity(intent)
                                        }
                                    }else{
                                        btn_register_reg.isEnabled = true
                                        btn_register_reg.alpha = 1f
                                        Toast.makeText(context,task.exception?.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
            } else{
                btn_register_reg.isEnabled = true
                btn_register_reg.alpha = 1f
                Toast.makeText(context,task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}