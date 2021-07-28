package com.example.cafespot.ui.login

data class RegisterFormState(
    val nameError:Int? = null,
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)