package com.nwanneka.yokyo.view.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.SharedPreferenceManager
import com.nwanneka.yokyo.databinding.FragmentProfileBinding
import com.nwanneka.yokyo.view.auth.YokyoAuthActivity
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.minimumEightInLength
import com.nwanneka.yokyo.view.utils.showToastMessage
import com.nwanneka.yokyo.view.utils.takeText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentProfile : Fragment() {

    private var binding: FragmentProfileBinding by autoCleared()

    private val viewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var sharedPref: SharedPreferenceManager

    private var oldEmailAddress: String? = null
    private var oldPassword: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProfileMenu()
        populatePersonalData()

        binding.inputEmailAddress.doAfterTextChanged {
            val errorText = if (oldEmailAddress.isNullOrEmpty() && it.isNullOrEmpty() && oldEmailAddress == it.toString()) {
                "Enter a different email address"
            } else if (binding.inputEmailAddress.takeText().isEmpty()) {
                getString(R.string.field_empty)
            } else {
                ""
            }
            binding.emailAddressTIL.isErrorEnabled = errorText.isNotEmpty()
            binding.emailAddressTIL.error = errorText
            enableSaveEmailButton(email = errorText)
        }

        binding.inputOldPassword.doAfterTextChanged {
            val errorText = viewModel.verifyPasswordText(requireContext(), it.toString())
            binding.oldPasswordTIL.isErrorEnabled = errorText.isNotEmpty()
            binding.oldPasswordTIL.error = errorText
            enableChangePassButton(oldPass = errorText)
        }

        binding.inputNewPassword.doAfterTextChanged {
            val errorText = when {
                it.toString().isEmpty() -> getString(R.string.field_empty)
                !it.toString().minimumEightInLength() -> getString(R.string.password_8_characters_long)
                it.toString() != oldPassword -> "Incorrect Password"
                else -> ""
            }
            binding.newPasswordTIL.isErrorEnabled = errorText.isNotEmpty()
            binding.newPasswordTIL.error = errorText
            enableChangePassButton(newPass = errorText)
        }

        onDeleteAccount()
        onUpdateEmailAddress()
        onChangePassword()

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showToastMessage(it)
        }

    }

    private fun populatePersonalData() {
        oldPassword = sharedPref.getOldPassword()
        viewModel.firebaseUser.observe(viewLifecycleOwner) {
            if (it != null) {
                it.email?.let { it1 -> holdEmailAddress(it1) }
            }
        }
    }

    private fun onDeleteAccount() {
        binding.btnDeleteAccount.setOnClickListener {
            it.isEnabled = false
            viewModel.deleteMyAccount()
        }

        viewModel.isDeleted.observe(viewLifecycleOwner) {
            binding.btnDeleteAccount.isEnabled = true
            if (it) {
                showToastMessage(getString(R.string.account_delete_success))
                startActivity(Intent(requireContext(), YokyoAuthActivity::class.java))
            } else showToastMessage(getString(R.string.account_delete_error))
        }
    }


    private fun onUpdateEmailAddress() {
        binding.btnSaveEmail.setOnClickListener {
            it.isEnabled = false
            viewModel.updateEmail(binding.inputEmailAddress.takeText())
        }

        viewModel.isEmailUpdated.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.firebaseUser.observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        if (user.email != oldEmailAddress) {
                            showToastMessage(getString(R.string.email_update_success))
                            user.email?.let { it1 -> holdEmailAddress(it1) }
                        }
                    }
                }
            }
        }
    }

    private fun onChangePassword() {
        binding.btnChangePassword.setOnClickListener {
            viewModel.updatePassword(binding.inputNewPassword.takeText())
        }

        viewModel.isChangePassword.observe(viewLifecycleOwner) {
            if (it) {
                showToastMessage(getString(R.string.pass_update_success))
                viewModel.firebaseUser.observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        sharedPref.setUserPassword(binding.inputNewPassword.takeText())
                        clearPasswordFields()
                    }
                }
            } else {
                showToastMessage(getString(R.string.pass_update_error))
            }
        }
    }

    private fun clearPasswordFields() {
        binding.inputOldPassword.text?.clear()
        binding.inputNewPassword.text?.clear()
    }

    private fun setupProfileMenu() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.logout -> {
                        Firebase.auth.signOut()
                        startActivity(Intent(requireContext(), YokyoAuthActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun enableSaveEmailButton(email: String? = null) {
        binding.apply {
            val emailError = email
                ?: getString(R.string.field_empty)
            binding.btnSaveEmail.isEnabled = emailError.isEmpty()
        }
    }

    private fun enableChangePassButton(oldPass: String? = null, newPass: String? = null) {
        binding.apply {
            val oldPassError = oldPass
                ?: viewModel.verifyPasswordText(requireContext(), binding.inputOldPassword.takeText())
            val newPassError = newPass
                ?: viewModel.verifyPasswordText(requireContext(), binding.inputNewPassword.takeText())
            binding.btnSaveEmail.isEnabled = oldPassError.isEmpty() && newPassError.isEmpty() && oldPass != newPass
        }
    }

    private fun holdEmailAddress(email: String) {
        oldEmailAddress = email
        binding.inputEmailAddress.setText(email)
    }
}