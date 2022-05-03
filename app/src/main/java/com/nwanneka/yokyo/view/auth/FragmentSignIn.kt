package com.nwanneka.yokyo.view.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.databinding.FragmentSignInBinding
import com.nwanneka.yokyo.view.base.BaseFragment
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.getColorPrimary
import com.nwanneka.yokyo.view.utils.setClickableText
import com.nwanneka.yokyo.view.utils.showToastMessage

class FragmentSignIn : BaseFragment() {

    private val TAG: String? = FragmentSignIn::class.simpleName
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
            findNavController().navigate(R.id.to_fragmentSignUp)
        }
    }


    private fun collectDataAndProcess(email: String, password: String) {

    }

    private fun processSignIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    showToastMessage("Authentication Successful")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    showToastMessage("Authentication failed.")
                }
            }

    }

}