package com.example.prieto_financex.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.prieto_financex.R
import com.example.prieto_financex.viewmodel.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ProfileFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private lateinit var profileImage: CircleImageView

    private val viewModel: ProfileViewModel by viewModels()

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val savedPath = saveImageToInternalStorage(it)
                if (savedPath != null) {
                    selectedImageUri = Uri.parse(savedPath)
                    profileImage.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(context, getString(R.string.failed_to_save_image), Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameET = view.findViewById<EditText>(R.id.profileUsername)
        val emailET = view.findViewById<EditText>(R.id.profileEmail)
        val oldPassET = view.findViewById<EditText>(R.id.profileOldPassword)
        val newPassET = view.findViewById<EditText>(R.id.profileNewPassword)
        val confirmPassET = view.findViewById<EditText>(R.id.profileConfirmPassword)
        val submitBtn = view.findViewById<View>(R.id.profileSubmitButton)

        profileImage = view.findViewById(R.id.profileImage)

        val email = FirebaseAuth.getInstance().currentUser?.email
        email?.let { viewModel.loadUserData(it) }

        profileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        submitBtn.setOnClickListener {
            viewModel.updateUserProfile(
                updatedUsername = usernameET.text.toString().trim(),
                updatedEmail = emailET.text.toString().trim(),
                oldPassword = oldPassET.text.toString().trim(),
                newPassword = newPassET.text.toString().trim(),
                confirmPassword = confirmPassET.text.toString().trim(),
                selectedImageUri = selectedImageUri,
                getString = { resId -> getString(resId) }
            )
        }

        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                usernameET.setText(it.userName)
                emailET.setText(it.userEmail)
                emailET.isEnabled = false

                it.profileImageUrl?.let { imgPath ->
                    val file = File(imgPath)
                    if (file.exists()) {
                        selectedImageUri = Uri.fromFile(file)
                        profileImage.setImageURI(selectedImageUri)
                    }
                }
            }
        })

        viewModel.updateSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val filename = "profile_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, filename)

        return try {
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream.close()
        }
    }
}