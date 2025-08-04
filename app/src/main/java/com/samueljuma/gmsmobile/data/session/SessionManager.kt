package com.samueljuma.gmsmobile.data.session

import android.content.Context
import android.util.Log
import com.samueljuma.gmsmobile.data.models.User
import com.samueljuma.gmsmobile.domain.models.UserDomain
import androidx.core.content.edit

class SessionManager(context: Context){

    private val sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "auth_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_ID = "user_id"
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
        private const val USER_ROLE = "user_role"
        private const val USER_PROFILE_PICTURE = "user_profile_picture"
    }

    fun saveAuthAndRefreshTokens(
        authToken: String,
        refreshToken: String,
    ){
        sharedPreferences.edit().apply {
            putString(AUTH_TOKEN, authToken)
            putString(REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun saveUserDetails(user: User){
        sharedPreferences.edit().apply {
            putInt(USER_ID, user.id)
            putString(USER_NAME, user.username)
            putString(USER_EMAIL, user.email)
            putString(USER_ROLE, user.role)
            putString(USER_PROFILE_PICTURE, user.profile_picture)
            apply()
        }
        Log.d("SessionManager", "saveUserDetails: $user")
    }

    fun getUserDetails(): UserDomain{
        return UserDomain(
            id = sharedPreferences.getInt(USER_ID, 0),
            username = sharedPreferences.getString(USER_NAME, null),
            email = sharedPreferences.getString(USER_EMAIL, null),
            role = sharedPreferences.getString(USER_ROLE, null),
            profile_picture = sharedPreferences.getString(USER_PROFILE_PICTURE, null)
        )
    }

//    fun getAuthCookiesString(): String? {
//        val authToken = sharedPreferences.getString(AUTH_TOKEN, null)
//        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, null)
//        return if (authToken != null && refreshToken != null) {
//            "Authentication=$authToken; Refresh=$refreshToken"
//        } else {
//            null
//        }
//    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN, null)
    }

    fun clearAllUserDetails(){
        sharedPreferences.edit { clear() }
    }

}

