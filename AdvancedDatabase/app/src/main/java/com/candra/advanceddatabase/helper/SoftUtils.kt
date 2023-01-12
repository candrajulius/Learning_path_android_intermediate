package com.candra.advanceddatabase.helper

import androidx.sqlite.db.SimpleSQLiteQuery

object SoftUtils {

    fun getSortedQuery(sortType: SortType): SimpleSQLiteQuery{
        val simpeleQuery = StringBuilder().append("SELECT * FROM student ")
        when(sortType){
            SortType.ASCENDING -> {
                simpeleQuery.append("ORDER BY name ASC")
            }
            SortType.DESCENDING -> {
                simpeleQuery.append("ORDER BY name DESC")
            }
            SortType.RANDOM -> {
                simpeleQuery.append("ORDER BY RANDOM()")
            }
        }
        return SimpleSQLiteQuery(simpeleQuery.toString())
    }

    /*
    Method ini mengembalikan nilai SimpleSQLiteQuery yang dibutuhkan RawQuery. Ada tiga query yang bisa dihasilkan method ini, antara lain:

Jika tipe sort-nya berupa ASCENDING, query-nya adalah “SELECT * FROM student ORDER BY name ASC”.
Sedangkan jika tipe sort-nya berupa DESCENDING, query-nya adalah “SELECT * FROM student ORDER BY name DESC”.
Sedangkan jika tipe sort-nya berupa RANDOM, query-nya adalah “SELECT * FROM student ORDER BY RANDOM()”.
     */

}