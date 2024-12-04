package com.yosua.authentication.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yosua.authentication.model.AppRepository
import com.yosua.authentication.model.StoryRepository
import com.yosua.authentication.model.di.Injection
import com.yosua.authentication.model.di.InjectionPaging
import com.yosua.authentication.view.detail.DetailViewModel
import com.yosua.authentication.view.login.LoginViewModel
import com.yosua.authentication.view.main.DashboardViewModel
import com.yosua.authentication.view.post.PostStoryViewModel
import com.yosua.authentication.view.register.RegisterViewModel
import com.yosua.authentication.viewmodel.MapsViewModel
import java.lang.IllegalArgumentException
import kotlin.also
import kotlin.jvm.java

class ViewModelFactory private constructor(private val appRepository : AppRepository, private val storyRepository : StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(appRepository, storyRepository) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(PostStoryViewModel::class.java)){
            return PostStoryViewModel(appRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(appRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance : ViewModelFactory? = null
        fun getInstance(context : Context) : ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provide(context), InjectionPaging.provideRepository(context))
            }.also { instance = it }
    }
}