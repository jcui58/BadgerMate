package com.cs407.badgermate.data

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", null, System.currentTimeMillis())

    // Convert to Firestore map
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "profileImage" to profileImage,
            "createdAt" to createdAt
        )
    }

    companion object {
        // Create User from Firestore document
        fun fromMap(map: Map<String, Any?>): User {
            return User(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                profileImage = map["profileImage"] as? String?,
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}