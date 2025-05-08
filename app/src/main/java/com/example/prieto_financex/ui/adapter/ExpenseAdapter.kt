package com.example.prieto_financex.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prieto_financex.R

class ExpenseAdapter(
    private val expenseList: List<Map<String, String>>,
    private val onEdit: ((Int) -> Unit)? = null,
    private val onDelete: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvExpenseName)
        val amountTextView: TextView = itemView.findViewById(R.id.tvExpenseCost)
        val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.nameTextView.text = expense["expenseName"]
        holder.amountTextView.text = "₱${expense["expenseAmount"] ?: "0"}"

        holder.editButton.setOnClickListener { onEdit?.invoke(position) }
        holder.deleteButton.setOnClickListener { onDelete?.invoke(position) }
    }

    override fun getItemCount(): Int = expenseList.size
}