package ru.marinovdev.utils

object StringResource {
    const val USER_SIGN_IN_ERROR = "User sign in error! Try again later"
    const val INSERT_REFRESH_TOKEN_ERROR = "Insert refresh token error! Try again later"
    const val UPDATE_REFRESH_TOKEN_ERROR = "Update refresh token error! Try again later"

    const val REFRESH_TOKEN_NOT_FOUND = "Refresh token not found! Try again log in"
    const val REFRESH_TOKEN_UNAUTHORIZED = "Refresh token does not match! Try again log in"
    const val ACCESS_TOKEN_UNAUTHORIZED = "Access token does not match! Try again log in"
    const val GET_TOKEN_ERROR = "Get token error! Try again later"

    const val TOKEN_VERIFIED = "TOKEN_VERIFIED"
    const val TOKEN_NOT_VERIFIED = "TOKEN_NOT_VERIFIED"
    const val TOKEN_EXPIRED = "TOKEN_EXPIRED"

    const val USER_HAS_SUCCESSFULLY_REGISTERED = "Successful registration!"
    const val USER_REGISTRATION_ERROR = "User registration error! Try again later!"

    const val USER_ID_NOT_FOUND = "User id not found! Try again"

    const val FETCH_USER_ID_BY_EMAIL_ERROR = "Fetch user id by email error! Try again later"
    const val FETCH_CODE_ERROR = "Fetch code error! Try again later"
    const val INSERT_CODE_ERROR = "Insert code error! Try again later"
    const val DELETE_CODE_ERROR = "Delete code error! Try again later"
    const val CORRECT_CODE = "Correct code!"
    const val INCORRECT_CODE = "Incorrect code!"
    const val TIME_IS_UP = "Time is up! Try again"

    const val USER_CREATION_ERROR = "User creation error! Try again later"
    const val GET_USER_ERROR = "Get user error! Try again later"
    const val DELETE_USER_ERROR = "Delete user error! Try again later"
    const val FETCH_AND_SEND_ERROR = "Fetch and send error! Try again later"
    const val CHECK_EMAIL_EXISTS_ERROR = "Check email error!"
    const val CHECK_REFRESH_TOKEN_ERROR = "Check refresh token error!"
    const val THIS_REFRESH_NOT_FOUND = "This refresh token not found!"
    const val DELETE_REFRESH_TOKEN_ERROR = "Delete refresh token error!"
    const val USER_ALREADY_EXISTS = "The user already exists!"

    const val FIREBASE_MAKE_PUSH_SUCCESS = "Firebase make push success!"
    const val FIREBASE_MAKE_PUSH_FAILURE = "Firebase make push failure!"
    const val FIREBASE_MAKE_CALL_SUCCESS = "Firebase make call success!"
    const val FIREBASE_MAKE_CALL_FAILURE = "Firebase make call failure!"
    const val FIREBASE_OVER_WRITE_FAILURE = "Firebase over write failure!"
    const val FIREBASE_OVER_WRITE_SUCCESS = "Firebase over write success!"
    const val FIREBASE_INSERT_SUCCESS = "Firebase over write success!"
    const val FIREBASE_INSERT_FAILURE = "Firebase over write success!"

    const val TYPE_FIREBASE_MESSAGE_MESSAGE = "TYPE_FIREBASE_MESSAGE_MESSAGE"
    const val TYPE_FIREBASE_MESSAGE_CALL = "TYPE_FIREBASE_MESSAGE_CALL"
    const val TYPE_FIREBASE_MESSAGE_READY_STREAM = "TYPE_FIREBASE_MESSAGE_READY_STREAM"
}