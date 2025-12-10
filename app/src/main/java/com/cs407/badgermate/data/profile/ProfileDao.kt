package com.cs407.badgermate.data.profile

import androidx.room.*

@Dao
interface ProfileDao {


    @Query("SELECT * FROM profile WHERE uid = :uid LIMIT 1")
    fun getProfileForUid(uid: String): ProfileEntity?


    @Query("SELECT * FROM profile LIMIT 1")
    fun getProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveProfile(profile: ProfileEntity)

    @Update
    fun updateProfile(profile: ProfileEntity)


    @Query("DELETE FROM profile WHERE uid = :uid")
    fun deleteProfileForUid(uid: String)


    @Query("DELETE FROM profile")
    fun deleteAllProfiles()
}
