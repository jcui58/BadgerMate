package com.cs407.badgermate.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cs407.badgermate.data.AppDatabase
import com.cs407.badgermate.data.profile.ProfileEntity

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileDao = AppDatabase.getInstance(application).profileDao()

    private val _profile = MutableLiveData<ProfileEntity>()
    val profile: LiveData<ProfileEntity> = _profile

    init {
        loadProfile()
    }

    private fun loadProfile() {
        var p = profileDao.getProfile()
        if (p == null) {
            p = ProfileEntity(
                name = "Alex Johnson",
                grade = "Junior",
                major = "Computer Science"
            )
            profileDao.saveProfile(p)
        }
        _profile.value = p
    }

    fun updateProfile(name: String, grade: String, major: String) {
        val current = _profile.value
        val newEntity = (current ?: ProfileEntity(
            name = name,
            grade = grade,
            major = major
        )).copy(
            name = name,
            grade = grade,
            major = major
        )
        profileDao.saveProfile(newEntity)
        _profile.value = newEntity
    }
}
