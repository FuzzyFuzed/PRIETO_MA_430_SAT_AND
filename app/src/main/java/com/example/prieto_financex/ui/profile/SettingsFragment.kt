package com.example.prieto_financex.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.prieto_financex.R
import com.example.prieto_financex.ui.auth.LoginActivity
import com.example.prieto_financex.viewmodel.profile.SettingsViewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deleteBtn = view.findViewById<Button>(R.id.deleteAccountBtn)

        deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        observeViewModel()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_account_title))
            .setMessage(getString(R.string.delete_account_message))
            .setPositiveButton(getString(R.string.delete_account_positive_button)) { _, _ ->
                viewModel.deleteUser(
                    getString = { resId -> getString(resId) },
                    getStringWithArg = { resId, arg -> getString(resId, arg) }
                )
            }
            .setNegativeButton(getString(R.string.delete_account_negative_button), null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.deletionSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.account_deleted),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        })
    }
}