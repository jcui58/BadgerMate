package com.cs407.badgermate.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cs407.badgermate.data.AppDatabase
import com.cs407.badgermate.data.profile.ProfileEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileDao = AppDatabase.getInstance(application).profileDao()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _profile = MutableLiveData<ProfileEntity?>()
    val profile: LiveData<ProfileEntity?> = _profile

    init {
        loadProfileForCurrentUser()
    }

    /** 从本地 Room 读取当前 Firebase 用户的 Profile */
    private fun loadProfileForCurrentUser() {
        val user = auth.currentUser
        _profile.value = if (user != null) {
            profileDao.getProfileForUid(user.uid)
        } else {
            null
        }
    }

    fun refreshProfile() {
        loadProfileForCurrentUser()
    }


    private fun baseEntityForCurrentUser(): ProfileEntity? {
        val user = auth.currentUser ?: return null
        val existing = profileDao.getProfileForUid(user.uid)
        return existing ?: ProfileEntity(
            uid = user.uid,
            email = user.email ?: ""
        )
    }


    fun updateNameGradeMajor(name: String, grade: String, major: String) {
        val user = auth.currentUser ?: return
        val base = baseEntityForCurrentUser() ?: return

        val updated = base.copy(
            name = name,
            grade = grade,
            major = major
        )


        profileDao.saveProfile(updated)
        _profile.value = updated

        // Firestore
        val map = mapOf(
            "name" to name,
            "grade" to grade,
            "major" to major,
            "email" to (updated.email)
        )
        firestore.collection("users")
            .document(user.uid)
            .set(map, SetOptions.merge())
    }

    fun updatePersonalInfo(
        heightFeet: String,
        heightInches: String,
        weight: String,
        gender: String,
        targetWeight: String
    ) {
        val user = auth.currentUser ?: return
        val base = baseEntityForCurrentUser() ?: return

        val updated = base.copy(
            heightFeet = heightFeet,
            heightInches = heightInches,
            weight = weight,
            gender = gender,
            targetWeight = targetWeight
        )

        // Room
        profileDao.saveProfile(updated)
        _profile.value = updated

        // Firestore
        val map = mapOf(
            "heightFeet" to heightFeet,
            "heightInches" to heightInches,
            "weight" to weight,
            "gender" to gender,
            "targetWeight" to targetWeight
        )
        firestore.collection("users")
            .document(user.uid)
            .set(map, SetOptions.merge())
    }


    fun updateProfile(name: String, grade: String, major: String) {
        updateNameGradeMajor(name, grade, major)
    }


    fun clearAccountInfo() {
        _profile.value = null
    }


}
