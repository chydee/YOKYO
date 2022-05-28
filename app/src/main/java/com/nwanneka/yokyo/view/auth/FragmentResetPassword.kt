package com.nwanneka.yokyo.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nwanneka.yokyo.databinding.FragmentResetPasswordBinding
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.showToastMessage
import com.nwanneka.yokyo.view.utils.takeText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentResetPassword : Fragment() {

    private var binding: FragmentResetPasswordBinding by autoCleared()

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmitEmailAddress.setOnClickListener {
            it.isEnabled = false
            authViewModel.resetPassword(email = binding.inputEmailAddress.takeText())
        }

        binding.inputEmailAddress.doAfterTextChanged {
            val errorText = authViewModel.verifyMailText(requireContext(), it.toString())
            binding.emailAddressTIL.isErrorEnabled = errorText.isNotEmpty()
            binding.emailAddressTIL.error = errorText
            enableButton(email = errorText)
        }

        authViewModel.resetLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                showToastMessage(it)
                findNavController().popBackStack()
            }
        }

        authViewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                showToastMessage(it)
            }
        }
    }

    private fun enableButton(email: String? = null, password: String? = null) {
        binding.apply {
            val emailError = email
                ?: authViewModel.verifyMailText(requireContext(), binding.inputEmailAddress.takeText())
            binding.btnSubmitEmailAddress.isEnabled = emailError.isEmpty()
        }
    }

}