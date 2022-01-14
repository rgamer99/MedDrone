package com.rgoel.meddrone

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_change_cred.*
import kotlinx.android.synthetic.main.fragment_register.*

class ChangeCred : Fragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var cnfPassword: EditText
    private lateinit var user_name: EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_register, container, false)

        password = view.findViewById(R.id.pwd_password)
        cnfPassword = view.findViewById(R.id.pwd_new_password)
        user_name = view.findViewById(R.id.pwd_name)
        fAuth = Firebase.auth

        user = Firebase.auth.currentUser!!
        username.isEnabled = false
        username.setText(user?.email.toString())
        user_name.isEnabled = false
        user_name.setText(user?.displayName.toString())

        view.findViewById<Button>(R.id.change_cred).setOnClickListener() {
            changethecred()
        }

        return view
    }

    private fun changethecred() {
        val icon = AppCompatResources.getDrawable(requireContext(),R.drawable.ic_warning)

        icon?.setBounds(0,0,icon.intrinsicWidth,icon.intrinsicHeight)
        when{
            TextUtils.isEmpty(password.text.toString().trim())->{
                password.setError("Please Enter Current Password", icon)
                change_cred.isEnabled = false
            }
            TextUtils.isEmpty(cnfPassword.text.toString().trim())-> {
                cnfPassword.setError("Please Enter New Password", icon)
                change_cred.isEnabled = false
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfPassword.text.toString().isNotEmpty() &&
                    user_name.text.toString().isNotEmpty() ->
            {
                if (cnfPassword.text.toString().matches(Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"))){
                    if(password.text.toString().length>=6){
                        if(password.text.toString().matches(Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"))){
                            if(cnfPassword.text.toString().length>=6){
                                passwordChange()
                            }else{
                                cnfPassword.setError("New Password should be 6 characters long.", icon)
                            }
                        } else{
                            password.setError("Old Password would have contained at least 1 letter, 1 number, 1 special character and should have no whitespace. ", icon)
                        }
                    }else{
                        password.setError("Old Password would have been 6 characters long.", icon)
                    }
                }   else{
                    cnfPassword.setError("New Password should contain at least 1 letter, 1 number, 1 special character and should have no whitespace.", icon)
                }
            }
        }
    }

    private fun passwordChange() {
        change_cred.isEnabled = false
        change_cred.alpha = 0.5f

        val credential = EmailAuthProvider
            .getCredential(user.email.toString(), password.toString())

        user.reauthenticate(credential).addOnCompleteListener {
                task->
            if (task.isSuccessful){
                val user = Firebase.auth.currentUser
                user!!.updatePassword(cnfPassword.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context,"Password Changed", Toast.LENGTH_SHORT).show()
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