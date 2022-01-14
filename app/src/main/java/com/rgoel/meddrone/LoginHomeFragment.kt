package com.rgoel.meddrone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_login_home, container, false)

        view.findViewById<Button>(R.id.btn_dir_register).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), false)
        }

        view.findViewById<Button>(R.id.btn_dir_login).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(), false)
        }

        return view
    }
}