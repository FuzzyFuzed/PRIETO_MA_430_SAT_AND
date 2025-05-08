package com.example.prieto_financex.ui.bottom_nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prieto_financex.R
import com.example.prieto_financex.data.locals.AppDatabaseHelper
import com.example.prieto_financex.data.model.CategoryItem
import com.example.prieto_financex.ui.adapter.CategoryExpenseAdapter
import com.example.prieto_financex.ui.adapter.ExpenseAdapter
import com.example.prieto_financex.ui.budgetplan.BudgetPlanningFragment
import com.example.prieto_financex.ui.exp_and_cat.CategoryFragment
import com.example.prieto_financex.ui.exp_and_cat.ExpenseFragment
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val optBudget = view.findViewById<LinearLayout>(R.id.opt_budget)
        val optExpense = view.findViewById<LinearLayout>(R.id.opt_expense)
        val optCategory = view.findViewById<LinearLayout>(R.id.opt_category)
        val recyclerView = view.findViewById<RecyclerView>(R.id.ReportGenRecyclerView)

        // Navigate to Budget Planning Fragment
        optBudget.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_content, BudgetPlanningFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Expense Fragment
        optExpense.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_content, ExpenseFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Category Fragment
        optCategory.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_content, CategoryFragment())
                .addToBackStack(null)
                .commit()
        }

        // Set up database helper and get user data
        val dbHelper = AppDatabaseHelper(requireContext())
        val user = FirebaseAuth.getInstance().currentUser
        val userData = user?.email?.let { dbHelper.getUserByEmail(it) }

        // If user data exists, populate categories and expenses
        userData?.let {
            val categories = dbHelper.getCategoriesByUserId(it.userId).map {
                CategoryItem(
                    itemId = it["itemID"]?.toIntOrNull() ?: 0,
                    itemName = it["itemName"] ?: "",
                    itemDesc = it["itemDescription"] ?: ""
                )
            }

            val expenseMap = dbHelper.getTotalExpensesPerItem(it.userId)

            // Calculate the total expenses
            val totalExpenses = expenseMap.values.sum()
            val expenseTotalTextView = view.findViewById<TextView>(R.id.expenseTotalAmount)
            expenseTotalTextView.text = "₱%,d".format(totalExpenses)

            // Set up the CategoryExpenseAdapter for displaying the categories and expenses
            val adapter = CategoryExpenseAdapter(categories, expenseMap) { category ->
                val expenses = dbHelper.getExpensesByItemId(it.userId, category.itemId)
                showExpenseDialog(category.itemName, expenses)
            }

            // Set RecyclerView adapter and layout manager
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    // Function to show expense details in a dialog
    private fun showExpenseDialog(categoryName: String, expenseList: List<Map<String, String>>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.expense_dialog, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvExpenses)

        val dbHelper = AppDatabaseHelper(requireContext())
        val userId = FirebaseAuth.getInstance().currentUser?.let {
            dbHelper.getUserByEmail(it.email ?: "")?.userId
        } ?: return

        val expenses = expenseList.toMutableList()
        titleTextView.text = categoryName
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lateinit var adapter: ExpenseAdapter

        adapter = ExpenseAdapter(
            expenses,
            onDelete = { position ->
                val expense = expenses[position]
                val expenseName = expense["expenseName"] ?: ""
                val expenseAmount = expense["expenseAmount"] ?: "0"
                val itemId = dbHelper.getCategoryIdByName(userId, categoryName)

                val success = dbHelper.deleteExpense(expenseName, expenseAmount, userId, itemId)
                if (success) {
                    expenses.removeAt(position)
                    adapter.notifyItemRemoved(position)  // ✅ Now works
                }
            }
        )

        recyclerView.adapter = adapter

        // Display the dialog
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()
    }
}