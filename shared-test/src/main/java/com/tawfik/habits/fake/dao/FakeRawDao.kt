package com.tawfik.habits.fake.dao

import androidx.sqlite.db.SupportSQLiteQuery
import com.tawfik.habits.data.database.dao.RawDao

class FakeRawDao : RawDao {

    override suspend fun rawQuery(supportSQLiteQuery: SupportSQLiteQuery): Int = 1

}