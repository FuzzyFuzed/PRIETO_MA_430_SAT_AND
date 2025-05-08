package com.example.prieto_financex.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prieto_financex.R
import com.example.prieto_financex.data.model.CategoryItem

class CategoryExpenseAdapter(
    private val categories: List<CategoryItem>,
    private val expenseMap: Map<Int, Int>,
    private val onCategoryClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.categoryName)
        val totalTextView: TextView = itemView.findViewById(R.id.totalAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_summary, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val categoryItem = categories[position]
        val totalAmount = expenseMap[categoryItem.itemId] ?: 0

        holder.nameTextView.text = categoryItem.itemName
        holder.totalTextView.text = "₱%,d".format(totalAmount)

        holder.itemView.setOnClickListener {
            onCategoryClick(categoryItem)
        }
    }

    override fun getItemCount(): Int = categories.size
}