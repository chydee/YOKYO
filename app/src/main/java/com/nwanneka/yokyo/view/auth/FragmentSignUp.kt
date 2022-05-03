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
import com.nwanneka.yokyo.databinding.FragmentSignUpBinding
import com.nwanneka.yokyo.view.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSignUp : Fragment() {

    private val TAG: String? = FragmentSignUp::class.simpleName

    private val viewModel: AuthViewModel by viewModels()

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

        binding.btnSignUp.setOnClickListener {
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
                showToastMessage("Account Created Successfully")
                startActivity(Intent(requireActivity(), MainActivity::class.java))
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                showToastMessage(it)
            }
        }
    }

    private fun setupSignInButton() {
        val signin = getString(R.string.text_label_sign_in)
        val startIndex = binding.alreadyHaveAccount.text.indexOf(signin, ignoreCase = true)
        val color = requireContext().getColorPrimary()
        binding.alreadyHaveAccount.setClickableText(startIndex, startIndex + signin.length, color) {
            findNavController().popBackStack()
        }
    }

    private fun enableButton(email: String? = null, password: String? = null) {
        binding.apply {
            val emailError = email
                ?: viewModel.verifyMailText(requireContext(), binding.inputEmailAddress.takeText())
            val passwordError = password
                ?: viewModel.verifyPasswordText(requireContext(), binding.inputPassword.takeText())
            binding.btnSignUp.isEnabled = emailError.isEmpty() && passwordError.isEmpty()
        }
    }


}