package com.nwanneka.yokyo.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nwanneka.yokyo.MainActivity
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.FragmentSignInBinding
import com.nwanneka.yokyo.view.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSignIn : Fragment() {

    private val TAG: String? = FragmentSignIn::class.simpleName
    private var binding: FragmentSignInBinding by autoCleared()

    private val viewModel: AuthViewModel by viewModels()


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

        binding.btnSignIn.setOnClickListener {
            it.isEnabled = false
            viewModel.signIn(email = binding.inputEmailAddress.takeText(), password = binding.inputPassword.takeText())
        }

        binding.inputEmailAddress.doAfterTextChanged {
            val errorText = viewModel.verifyMailText(requireContext(), it.toString())
            binding.emailAddressTIL.isErrorEnabled = errorText.isNotEmpty()
            binding.emailAddressTIL.error = errorText
            enableButton(email = errorText)
        }

        binding.inputPassword.doAfterTextChanged {
            val errorText = viewModel.verifyPasswordText(requireContext(), it.toString())
            binding.passwordTIL.isErrorEnabled = errorText.isNotEmpty()
            binding.passwordTIL.error = errorText
            enableButton(password = errorText)
        }

        viewModel.firebaseUser.observe(viewLifecycleOwner) {
            if (it != null) {
                showToastMessage("Authentication Successful")
                startActivity(Intent(requireActivity(), MainActivity::class.java))
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                showToastMessage(it)
            }
        }
    }

    private fun configureSignUp() {
        val signup = getString(R.string.text_label_sign_up)
        val startIndex = binding.newAccount.text.indexOf(signup, ignoreCase = true)
        val color = requireContext().getColorPrimary()
        binding.newAccount.setClickableText(startIndex, startIndex + signup.length, color) {
            findNavController().navigate(R.id.to_fragmentSignUp)
        }
    }


    private fun enableButton(email: String? = null, password: String? = null) {
        binding.apply {
            val emailError = email
                ?: viewModel.verifyMailText(requireContext(), binding.inputEmailAddress.takeText())
            val passwordError = password
                ?: viewModel.verifyPasswordText(requireContext(), binding.inputPassword.takeText())
            binding.btnSignIn.isEnabled = emailError.isEmpty() && passwordError.isEmpty()
        }
    }

}