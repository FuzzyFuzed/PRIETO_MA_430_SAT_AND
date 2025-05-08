package com.example.prieto_financex.ui.exp_and_cat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.prieto_financex.R
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.google.firebase.auth.FirebaseAuth

class ExpenseFragment : Fragment() {

    private lateinit var dbHelper: AppDatabaseHelper
    private var selectedCategoryId: Int? = null
    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = AppDatabaseHelper(requireContext())
        val categorySpinner = view.findViewById<Spinner>(R.id.expensecategoryspinner)
        val addCatText = view.findViewById<TextView>(R.id.AddInitialCat)

        // Get user email from Firebase to fetch user ID
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return
        val localUser = dbHelper.getUserByEmail(email) ?: return
        userId = localUser.userId

        // Fetch categories for this user from local database
        val categoryList = dbHelper.getCategoriesByUserId(userId!!)
        if (categoryList.isNotEmpty()) {
            categorySpinner.visibility = View.VISIBLE
            addCatText.visibility = View.GONE

            // Create an adapter for the spinner using category names
            val categoryNames = categoryList.map { it["itemName"] ?: "Unnamed" }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter

            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedCategoryId = categoryList[position]["itemID"]?.toIntOrNull()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        } else {
            categorySpinner.visibility = View.GONE
            addCatText.visibility = View.VISIBLE
        }

        // Handle submit button click
        view.findViewById<Button>(R.id.expensesubmitbutton).setOnClickListener {
            submitExpense()
        }
    }

    /**
     * Submits the expense details to the database.
     */
    private fun submitExpense() {
        val name = view?.findViewById<EditText>(R.id.expensenameedittext)?.text.toString().trim()
        val amountText = view?.findViewById<EditText>(R.id.expenseamountedittext)?.text.toString().trim()
        val desc = view?.findViewById<EditText>(R.id.expensedescedittext)?.text.toString().trim()
        val categoryId = selectedCategoryId

        // Validate input fields
        if (name.isEmpty() || amountText.isEmpty() || categoryId == null) {
            Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // Validate amount format
        val amount = amountText.toIntOrNull()
        if (amount == null) {
            Toast.makeText(context, getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show()
            return
        }

        val uid = userId ?: return
        val success = dbHelper.insertExpense(
            expenseName = name,
            expenseAmount = amount,
            expenseDesc = desc,
            userId = uid,
            itemId = categoryId
        )

        // Show appropriate message based on success or failure
        if (success) {
            Toast.makeText(context, getString(R.string.expense_saved), Toast.LENGTH_SHORT).show()
            // Optionally clear form or navigate
        } else {
            Toast.makeText(context, getString(R.string.error_saving_expense), Toast.LENGTH_SHORT).show()
        }
    }
}