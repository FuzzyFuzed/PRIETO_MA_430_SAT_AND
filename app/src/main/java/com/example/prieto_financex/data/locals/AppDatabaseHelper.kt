package com.example.prieto_financex.data.locals

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.prieto_financex.data.model.ExpenseItem
import com.example.prieto_financex.data.model.UserData

class AppDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 5

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "userID"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_IS_VERIFIED = "is_verified"
        private const val COLUMN_PROFILE_IMAGE = "profileImage"

        // Category table
        private const val TABLE_CATEGORY = "category"
        private const val COLUMN_ITEM_ID = "itemID"
        private const val COLUMN_ITEM_NAME = "itemName"
        private const val COLUMN_ITEM_DESC = "itemDescription"
        private const val COLUMN_USER_ID_FK = "userID"

        // Expenses table
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_EXPENSE_ID = "expenseID"
        private const val COLUMN_EXPENSE_NAME = "expenseName"
        private const val COLUMN_EXPENSE_AMOUNT = "expenseAmount"
        private const val COLUMN_EXPENSE_DESC = "expenseDescription"
        private const val COLUMN_EXPENSE_USER_ID_FK = "userID"
        private const val COLUMN_EXPENSE_ITEM_ID_FK = "itemID"

        //Budget table
        private const val TABLE_BUDGETS = "budgets"
        private const val COLUMN_BUDGET_ID = "budgetID"
        private const val COLUMN_BUDGET_LIMIT = "budgetLimit"
        private const val COLUMN_BUDGET_USER_ID = "userID"
        private const val COLUMN_BUDGET_ITEM_ID = "itemID"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_IS_VERIFIED INTEGER DEFAULT 0,
                 $COLUMN_PROFILE_IMAGE TEXT
            )
        """.trimIndent()

        //Category
        val createCategoryTable = """
            CREATE TABLE $TABLE_CATEGORY (
                $COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ITEM_NAME TEXT NOT NULL,
                $COLUMN_ITEM_DESC TEXT,
                $COLUMN_USER_ID_FK INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()

        //Expenses
        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXPENSE_NAME TEXT NOT NULL,
                $COLUMN_EXPENSE_AMOUNT INTEGER NOT NULL,
                $COLUMN_EXPENSE_DESC TEXT,
                $COLUMN_EXPENSE_USER_ID_FK INTEGER NOT NULL,
                $COLUMN_EXPENSE_ITEM_ID_FK INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_EXPENSE_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_ID),
                FOREIGN KEY($COLUMN_EXPENSE_ITEM_ID_FK) REFERENCES $TABLE_CATEGORY($COLUMN_ITEM_ID)
            )
        """.trimIndent()

        //Budgets
        val createBudgetTable = """
            CREATE TABLE $TABLE_BUDGETS (
                $COLUMN_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BUDGET_LIMIT INTEGER NOT NULL,
                $COLUMN_BUDGET_USER_ID INTEGER NOT NULL,
                $COLUMN_BUDGET_ITEM_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_BUDGET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
                FOREIGN KEY($COLUMN_BUDGET_ITEM_ID) REFERENCES $TABLE_CATEGORY($COLUMN_ITEM_ID)
            )
        """.trimIndent()

        db.execSQL(createTable)
        db.execSQL(createCategoryTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createBudgetTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_PROFILE_IMAGE TEXT")
        }
        if (oldVersion < 3) {
            val createCategoryTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_CATEGORY (
                    $COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_ITEM_NAME TEXT NOT NULL,
                    $COLUMN_ITEM_DESC TEXT,
                    $COLUMN_USER_ID_FK INTEGER NOT NULL,
                    FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_ID)
                )
            """.trimIndent()
            db.execSQL(createCategoryTable)
        }
        if (oldVersion < 4) {
            val createExpensesTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_EXPENSES (
                    $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_EXPENSE_NAME TEXT NOT NULL,
                    $COLUMN_EXPENSE_AMOUNT INTEGER NOT NULL,
                    $COLUMN_EXPENSE_DESC TEXT,
                    $COLUMN_EXPENSE_USER_ID_FK INTEGER NOT NULL,
                    $COLUMN_EXPENSE_ITEM_ID_FK INTEGER NOT NULL,
                    FOREIGN KEY($COLUMN_EXPENSE_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_ID),
                    FOREIGN KEY($COLUMN_EXPENSE_ITEM_ID_FK) REFERENCES $TABLE_CATEGORY($COLUMN_ITEM_ID)
                )
            """.trimIndent()
            db.execSQL(createExpensesTable)
        }
        if (oldVersion < 5) {
            val createBudgetTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_BUDGETS (
                    $COLUMN_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_BUDGET_LIMIT INTEGER NOT NULL,
                    $COLUMN_BUDGET_USER_ID INTEGER NOT NULL,
                    $COLUMN_BUDGET_ITEM_ID INTEGER NOT NULL,
                    FOREIGN KEY($COLUMN_BUDGET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
                    FOREIGN KEY($COLUMN_BUDGET_ITEM_ID) REFERENCES $TABLE_CATEGORY($COLUMN_ITEM_ID)
                )
            """.trimIndent()
            db.execSQL(createBudgetTable)
        }
    }

    //User Methods
    fun insertUser(user: UserData): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.userName)
            put(COLUMN_EMAIL, user.userEmail)
            put(COLUMN_PASSWORD, user.userPassword)
            put(COLUMN_IS_VERIFIED, if (user.isVerified) 1 else 0)
            put(COLUMN_PROFILE_IMAGE, user.profileImageUrl)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun updateVerificationStatus(email: String, isVerified: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_VERIFIED, if (isVerified) 1 else 0)
        }
        return db.update(TABLE_USERS, values, "$COLUMN_EMAIL=?", arrayOf(email))
    }

    fun getUserByEmail(email: String): UserData? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS, null,
            "$COLUMN_EMAIL=?", arrayOf(email),
            null, null, null
        )

        var user: UserData? = null
        if (cursor.moveToFirst()) {
            user = UserData(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                userPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                isVerified = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_VERIFIED)) == 1,
                profileImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE))
            )
        }
        cursor.close()
        return user
    }

    fun updateUser(user: UserData): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.userName)
            put(COLUMN_EMAIL, user.userEmail)
            put(COLUMN_PASSWORD, user.userPassword)
            put(COLUMN_IS_VERIFIED, if (user.isVerified) 1 else 0)
            put(COLUMN_PROFILE_IMAGE, user.profileImageUrl)
        }
        return db.update(
            TABLE_USERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(user.userId.toString())
        )
    }

    fun deleteUserByEmail(email: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_USERS, "$COLUMN_EMAIL = ?", arrayOf(email))
    }

    // Category Methods
    fun insertCategory(itemName: String, itemDesc: String, userId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_NAME, itemName)
            put(COLUMN_ITEM_DESC, itemDesc)
            put(COLUMN_USER_ID_FK, userId)
        }
        return db.insert(TABLE_CATEGORY, null, values) != -1L
    }

    fun getCategoriesByUserId(userId: Int): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CATEGORY,
            arrayOf(COLUMN_ITEM_ID, COLUMN_ITEM_NAME, COLUMN_ITEM_DESC),
            "$COLUMN_USER_ID_FK = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        val categories = mutableListOf<Map<String, String>>()
        try {
            while (cursor.moveToNext()) {
                val map = mapOf(
                    "itemID" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID))
                        .toString(),
                    "itemName" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME)),
                    "itemDescription" to (cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESC)) ?: "")
                )
                categories.add(map)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close() // Ensure the cursor is closed
            db.close() // Ensure the database is closed
        }
        return categories
    }

    // Expense Methods
    fun insertExpense(expenseName: String, expenseAmount: Int, expenseDesc: String?, userId: Int, itemId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EXPENSE_NAME, expenseName)
            put(COLUMN_EXPENSE_AMOUNT, expenseAmount)
            put(COLUMN_EXPENSE_DESC, expenseDesc)
            put(COLUMN_EXPENSE_USER_ID_FK, userId)
            put(COLUMN_EXPENSE_ITEM_ID_FK, itemId)
        }
        return db.insert(TABLE_EXPENSES, null, values) != -1L
    }

    fun getExpensesByUserId(userId: Int): MutableList<ExpenseItem> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(
                COLUMN_EXPENSE_ID,
                COLUMN_EXPENSE_NAME,
                COLUMN_EXPENSE_AMOUNT,
                COLUMN_EXPENSE_DESC,
                COLUMN_EXPENSE_USER_ID_FK,
                COLUMN_EXPENSE_ITEM_ID_FK
            ),
            "$COLUMN_EXPENSE_USER_ID_FK = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        val expenses = mutableListOf<ExpenseItem>()
        try {
            while (cursor.moveToNext()) {
                val expense = ExpenseItem(
                    expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)),
                    expenseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_NAME)),
                    expenseAmount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_AMOUNT)),
                    expenseDesc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DESC)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_USER_ID_FK)),
                    itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ITEM_ID_FK))
                )
                expenses.add(expense)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return expenses
    }

    fun deleteExpense(expenseName: String, expenseAmount: String, userId: Int, itemId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TABLE_EXPENSES,
            "$COLUMN_EXPENSE_NAME = ? AND $COLUMN_EXPENSE_AMOUNT = ? AND $COLUMN_EXPENSE_USER_ID_FK = ? AND $COLUMN_EXPENSE_ITEM_ID_FK = ?",
            arrayOf(expenseName, expenseAmount, userId.toString(), itemId.toString())
        )
        db.close()
        return result > 0
    }

    fun getCategoryIdByName(userId: Int, itemName: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CATEGORY,
            arrayOf(COLUMN_ITEM_ID),
            "$COLUMN_USER_ID_FK = ? AND $COLUMN_ITEM_NAME = ?",
            arrayOf(userId.toString(), itemName),
            null, null, null
        )

        var itemId = -1
        if (cursor.moveToFirst()) {
            itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID))
        }
        cursor.close()
        return itemId
    }

    //Category Expense
    fun getTotalExpensesPerItem(userId: Int): Map<Int, Int> {
        val db = readableDatabase
        val result = mutableMapOf<Int, Int>()

        val query = """
        SELECT $COLUMN_EXPENSE_ITEM_ID_FK, SUM($COLUMN_EXPENSE_AMOUNT) as totalAmount
        FROM $TABLE_EXPENSES
        WHERE $COLUMN_EXPENSE_USER_ID_FK = ?
        GROUP BY $COLUMN_EXPENSE_ITEM_ID_FK
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        while (cursor.moveToNext()) {
            val itemID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ITEM_ID_FK))
            val totalAmount = cursor.getInt(cursor.getColumnIndexOrThrow("totalAmount"))
            result[itemID] = totalAmount
        }
        cursor.close()
        return result
    }

    fun getExpensesByItemId(userId: Int, itemId: Int): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            "SELECT $COLUMN_EXPENSE_NAME, $COLUMN_EXPENSE_AMOUNT FROM $TABLE_EXPENSES WHERE $COLUMN_EXPENSE_USER_ID_FK = ? AND $COLUMN_EXPENSE_ITEM_ID_FK = ?",
            arrayOf(userId.toString(), itemId.toString())
        )
        while (cursor.moveToNext()) {
            val map = mapOf(
                "expenseName" to cursor.getString(0),
                "expenseAmount" to cursor.getString(1)
            )
            list.add(map)
        }
        cursor.close()
        return list
    }

    //Budget Methods
    fun insertBudget(limit: Int, userID: Int, itemID: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BUDGET_LIMIT, limit)
            put(COLUMN_BUDGET_USER_ID, userID)
            put(COLUMN_BUDGET_ITEM_ID, itemID)
        }
        return db.insert(TABLE_BUDGETS, null, values) != -1L
    }

    //Check if existed
    fun getBudgetId(userId: Int, itemId: Int): Int? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT budgetID FROM budgets WHERE userID = ? AND itemID = ?",
            arrayOf(userId.toString(), itemId.toString())
        )

        val id = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()
        return id
    }

    fun updateBudgetLimit(budgetId: Int, newAmountToAdd: Int): Boolean {
        val db = writableDatabase

        // First, get the current budgetLimit
        val cursor = db.rawQuery("SELECT budgetLimit FROM budgets WHERE budgetID = ?", arrayOf(budgetId.toString()))
        var currentLimit = 0
        if (cursor.moveToFirst()) {
            currentLimit = cursor.getInt(cursor.getColumnIndexOrThrow("budgetLimit"))
        }
        cursor.close()

        val updatedLimit = currentLimit + newAmountToAdd

        val values = ContentValues().apply {
            put("budgetLimit", updatedLimit)
        }

        val result = db.update("budgets", values, "budgetID = ?", arrayOf(budgetId.toString()))
        return result > 0
    }

    fun getTotalBudgetForItem(userId: Int, itemId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(budgetLimit) as total FROM budgets WHERE userID = ? AND itemID = ?",
            arrayOf(userId.toString(), itemId.toString())
        )

        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        return total

    }

    fun getTotalBudgetForUser(userId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(budgetLimit) as totalBudget FROM budgets WHERE userID = ?",
            arrayOf(userId.toString())
        )

        var totalBudget = 0
        if (cursor.moveToFirst()) {
            totalBudget = cursor.getInt(cursor.getColumnIndexOrThrow("totalBudget"))
        }
        cursor.close()
        return totalBudget
    }
}