package com.samueljuma.gmsmobile.domain.models

import com.samueljuma.gmsmobile.presentation.screens.gymusers.GymUserField


data class GymUserEntry(
    val userName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "Member",

    val usernameError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
) {
    fun validateEntry(field: GymUserField): GymUserEntry {
        return when (field) {
            GymUserField.UserName -> copy(usernameError = userName.validateEntry())
            GymUserField.FirstName -> copy(firstNameError = firstName.validateEntry())
            GymUserField.LastName -> copy(lastNameError = lastName.validateEntry())
            GymUserField.Email -> copy(emailError = email.validateEmail())
            GymUserField.PhoneNumber -> copy(phoneNumberError = phoneNumber.validatePhoneNumber())
        }
    }

    fun validateAll(): GymUserEntry {
        return this.copy(
            usernameError = userName.validateEntry(),
            firstNameError = firstName.validateEntry(),
            lastNameError = lastName.validateEntry(),
            emailError = email.validateEmail(),
            phoneNumberError = phoneNumber.validatePhoneNumber()
        )
    }

    fun isValid(): Boolean {
        return usernameError == null &&
                firstNameError == null &&
                lastNameError == null &&
                emailError == null &&
                phoneNumberError == null
    }
}



// For username, first name, last name, password
fun String.validateEntry(): String? {
    when{
        this.isBlank() -> return "Required"
        this.length < 2 -> return "Too short"
        else -> return null
    }
}

fun String.validateEmail(): String? {
    when{
        this.isBlank() -> return null
        !this.contains("@") -> return "Invalid email"
        else -> return null
    }
}

fun String.validatePhoneNumber(): String? {
    val phoneRegex = Regex("^(?:0)?(7\\d{8}|1\\d{8})$")
    return when{
        this.isBlank() -> null
        !this.matches(phoneRegex) -> "Invalid"
        else -> null
    }
}

