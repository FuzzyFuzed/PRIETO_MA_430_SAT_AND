package com.example.prieto_financex.data.model

data class ExpenseItem(
    val expenseId: Int,              // changed expenseID to expenseId for consistency
    val expenseName: String,
    val expenseAmount: Int,       // updated type to Double for the amount
    val expenseDesc: String?,        // updated variable name to match project context
    val userId: Int,                 // changed userID to userId for consistency
    val itemId: Int                  // changed itemID to itemId for consistency
)