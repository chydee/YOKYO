package com.nwanneka.yokyo.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.FragmentSignInBinding
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.getColorPrimary
import com.nwanneka.yokyo.view.utils.setClickableText

class FragmentSignIn : Fragment() {

    private var binding: FragmentSignInBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSignUp()
    }

    private fun configureSignUp() {
        val signup = getString(R.string.text_label_sign_up)
        val startIndex = binding.newAccount.text.indexOf(signup, ignoreCase = true)
        val color = requireContext().getColorPrimary()
        binding.newAccount.setClickableText(startIndex, startIndex + signup.length, color) {
            // findNavController().popBackStack()
        }
    }

}