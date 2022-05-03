package com.nwanneka.yokyo.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.FragmentSignUpBinding
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.getColorPrimary
import com.nwanneka.yokyo.view.utils.setClickableText


class FragmentSignUp : Fragment() {

    private var binding: FragmentSignUpBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSignInButton()
    }

    private fun setupSignInButton() {
        val signin = getString(R.string.text_label_sign_in)
        val startIndex = binding.alreadyHaveAccount.text.indexOf(signin, ignoreCase = true)
        val color = requireContext().getColorPrimary()
        binding.alreadyHaveAccount.setClickableText(startIndex, startIndex + signin.length, color) {
            findNavController().popBackStack()
        }
    }


}