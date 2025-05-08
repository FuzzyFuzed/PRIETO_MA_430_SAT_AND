package com.example.prieto_financex.data.model

data class CategoryItem(
    val itemId: Int = 0,             // changed itemID to itemId for consistency
    val itemName: String,
    val itemDesc: String?            // updated itemDescription to itemDesc for consistency and made nullable
)