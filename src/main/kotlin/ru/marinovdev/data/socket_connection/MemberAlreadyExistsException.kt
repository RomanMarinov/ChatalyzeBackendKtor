package ru.marinovdev.data.socket_connection

class MemberAlreadyExistsException: Exception(
    "There is already a member with that username in the room."
)