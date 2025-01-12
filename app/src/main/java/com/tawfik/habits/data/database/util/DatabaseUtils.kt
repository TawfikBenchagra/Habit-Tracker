package com.tawfik.habits.data.database.util

import java.io.File

interface DatabaseUtils {

    fun isDatabaseValid(): Boolean

    fun getDatabasePath(): File

    fun closeDatabase()

    fun isDatabaseOpen(): Boolean

}