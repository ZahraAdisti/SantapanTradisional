package com.example.santapantradisional.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.santapantradisional.models.Users

@Dao
interface IUserDao {
    @Insert fun insertUsers(users: Users)
    @Query("SELECT * FROM Users") fun getAllUsers():List<Users>
    @Query("DELETE FROM Users") fun deleteAllUsers()
    @Query("DELETE FROM Users WHERE id = :id") fun deleteById(id:Int)
}