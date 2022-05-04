package com.nwanneka.yokyo.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nwanneka.yokyo.databinding.FragmentResetPasswordBinding
import com.nwanneka.yokyo.view.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentResetPassword : Fragment() {

    private var binding: FragmentResetPasswordBinding by autoCleared()

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(inflater)
        return binding.root
    }

}