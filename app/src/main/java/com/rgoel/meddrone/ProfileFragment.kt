package com.rgoel.meddrone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var picture_of_user: ImageView
    private lateinit var name_of_user: TextView
    private lateinit var email_of_user: TextView
    private lateinit var pp_img_url: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_profile, container, false)

        picture_of_user = view.findViewById(R.id.picture_of_user)
        name_of_user = view.findViewById(R.id.name_of_user)
        email_of_user = view.findViewById(R.id.email_of_user)
        email_of_user.setEnabled(false)
        name_of_user.setEnabled(false)

        val user = Firebase.auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                val photoUrl = profile.photoUrl

                email_of_user.setText(user.email)
                name_of_user.setText(user.displayName)
                picture_of_user.setImageURI(photoUrl)
            }
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
            Firebase.auth.signOut()
            Toast.makeText(context, "Logout Successful!!", Toast.LENGTH_SHORT).show()
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginHomeFragment(), false)
        }
        return view
    }
}