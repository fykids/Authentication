package com.yosua.authentication.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.di.Injection
import com.yosua.authentication.view.login.LoginViewModel
import com.yosua.authentication.view.register.RegisterViewModel
import java.lang.IllegalArgumentException
import kotlin.also
import kotlin.jvm.java

class ViewModelFactory private constructor(private val appRepository : AppRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(appRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance : ViewModelFactory? = null
        fun getInstance(context : Context) : ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provide(context))
            }.also { instance = it }
    }
}