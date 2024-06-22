package com.example.santapantradisional.database.repository

import android.app.Application
import com.example.santapantradisional.database.AppDatabase
import com.example.santapantradisional.database.dao.IUserDao
import com.example.santapantradisional.models.Users
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UsersRepository(application: Application) {
    private val userDao: IUserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDatabase.getDatabase(application)
        userDao = db.usersDao()
    }

    fun allUsers(): List<Users> {
        return userDao.getAllUsers()
    }

    fun deleteAllUsers() {
        executorService.execute {
            userDao.deleteAllUsers()
        }
    }

    fun deleteById(id: Int) {
        executorService.execute {
            userDao.deleteById(id)
        }
    }

    fun insert(users: Users) {
        executorService.execute {
            userDao.insertUsers(users)
        }
    }
}
