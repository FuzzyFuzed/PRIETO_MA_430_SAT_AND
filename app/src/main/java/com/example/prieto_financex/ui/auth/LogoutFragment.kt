package com.example.prieto_financex.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.prieto_financex.R
import com.example.prieto_financex.viewmodel.auth.LogoutViewModel

class LogoutFragment : Fragment() {

    private val viewModel: LogoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observeViewModel()
        showLogoutConfirmationDialog()
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    private fun observeViewModel() {
        // Observe the logout event
        viewModel.logoutEvent.observe(viewLifecycleOwner, Observer { shouldLogout ->
            if (shouldLogout) {
                // Redirect to LoginActivity after successful logout
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish() // Ensuring the current activity is closed after logout
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        // Display a confirmation dialog to the user before logging out
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_message))
            .setPositiveButton(getString(R.string.logout_positive_button)) { _, _ ->
                // Call ViewModel to perform the logout
                viewModel.confirmLogout()
            }
            .setNegativeButton(getString(R.string.logout_negative_button)) { _, _ ->
                // Close the fragment and return to previous state
                requireActivity().supportFragmentManager.popBackStack()
            }
            .setCancelable(false)
            .show()
    }
}