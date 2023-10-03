package ru.marinovdev.model

import kotlinx.serialization.Serializable

//@Serializable
//data class MessageResponse(
//    val httpStatusCode: Int,
//    val message: String,
//    val contentType: String = "application/json"
//)


@Serializable
data class MessageResponse(
    val httpStatusCode: Int,
    val message: String
)
