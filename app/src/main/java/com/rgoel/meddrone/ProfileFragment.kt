package com.rgoel.meddrone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

class ProfileFragment : Fragment() {

    private lateinit var picture_of_user: ImageView
    private lateinit var name_of_user: TextView
    private lateinit var email_of_user: TextView
    private lateinit var emergency_contact1: TextView
    private lateinit var emergency_contact2: TextView
    private lateinit var phone_number: TextView
    private lateinit var database: DatabaseReference
    private lateinit var userUID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_profile, container, false)

        picture_of_user = view.findViewById(R.id.picture_of_user)
        name_of_user = view.findViewById(R.id.name_of_user)
        name_of_user.setEnabled(false)
        email_of_user = view.findViewById(R.id.email_of_user)
        email_of_user.setEnabled(false)
        phone_number = view.findViewById(R.id.phone_number)
        phone_number.setEnabled(false)
        emergency_contact1 = view.findViewById(R.id.emergency_contact1)
        emergency_contact1.setEnabled(false)
        emergency_contact2 = view.findViewById(R.id.emergency_contact2)
        emergency_contact2.setEnabled(false)

        val user = Firebase.auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                userUID = user.uid
                email_of_user.setText(user.email)
            }
        }

        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userUID).get()
            .addOnSuccessListener {

                name_of_user.setText(it.child("name").value.toString())
                phone_number.setText(it.child("phone").value.toString())
                emergency_contact1.setText(it.child("ec1").value.toString())
                emergency_contact2.setText(it.child("ec2").value.toString())

        }.addOnFailureListener {
            Toast.makeText(context,"This shouldn't be happening that's all we know. Please try again later", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.del_account).setOnClickListener {
            if (user != null) {
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                            activity?.let{
                                val intent = Intent (it, MainKotlinActivity::class.java)
                                it.startActivity(intent)
                            }
                        } else {
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        view.findViewById<Button>(R.id.logout).setOnClickListener {
            if (user != null) {
                Firebase.auth.signOut()
                Toast.makeText(context, "Logout Successful!!", Toast.LENGTH_SHORT).show()
                activity?.let {
                    val intent = Intent (it, MainKotlinActivity::class.java)
                    it.startActivity(intent)
                }
            } else {
                var navRegister = activity as FragmentNavigation
                navRegister.navigateFrag(LoginHomeFragment(), false)
            }
        }

        return view
    }
}