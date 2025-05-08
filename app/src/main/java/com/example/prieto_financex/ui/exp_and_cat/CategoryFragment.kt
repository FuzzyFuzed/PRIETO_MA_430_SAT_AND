package com.example.prieto_financex.ui.exp_and_cat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prieto_financex.R
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.example.prieto_financex.data.model.CategoryItem
import com.example.prieto_financex.ui.adapter.CategoryAdapter
import com.google.firebase.auth.FirebaseAuth

class CategoryFragment : Fragment() {

    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = AppDatabaseHelper(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.CategoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val nameEditText = view.findViewById<EditText>(R.id.CategoryNameEditText)
        val descEditText = view.findViewById<EditText>(R.id.CategoryDescEditText)
        val submitBtn = view.findViewById<Button>(R.id.CategorySubmitBtn)

        // Load existing categories
        loadCategories()

        submitBtn.setOnClickListener {
            val itemName = nameEditText.text.toString().trim()
            val itemDesc = descEditText.text.toString().trim()
            val email = FirebaseAuth.getInstance().currentUser?.email
            val localUser = dbHelper.getUserByEmail(email ?: "")
            val userId = localUser?.userId

            if (itemName.isEmpty() || itemDesc.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.fill_both_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == null) {
                Toast.makeText(requireContext(), getString(R.string.user_not_logged_in), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insert category into the database
            val success = dbHelper.insertCategory(itemName, itemDesc, userId)
            if (success) {
                Toast.makeText(requireContext(), getString(R.string.category_saved), Toast.LENGTH_SHORT).show()
                nameEditText.text.clear()
                descEditText.text.clear()
                loadCategories()
            } else {
                Toast.makeText(requireContext(), getString(R.string.category_save_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategories() {
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return
        val localUser = dbHelper.getUserByEmail(email) ?: return
        val userId = localUser.userId

        // Fetch the categories from the database based on the user's ID
        val categoryList = dbHelper.getCategoriesByUserId(userId)
            .map {
                CategoryItem(
                    itemId = it["itemID"]?.toIntOrNull() ?: 0,
                    itemName = it["itemName"] ?: "",
                    itemDesc = it["itemDescription"] ?: ""
                )
            }

        // Update the RecyclerView with the fetched categories
        val adapter = CategoryAdapter(categoryList)
        view?.findViewById<RecyclerView>(R.id.CategoryRecyclerView)?.adapter = adapter
    }
}