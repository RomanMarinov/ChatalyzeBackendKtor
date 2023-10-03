package ru.marinovdev.features.send_email

import javax.mail.Message
import javax.mail.Session

interface SendEmailToTheUserI {
    fun sendEmail(emailForSending: String, password: String)
    fun createEmailMessage(emailForSending: String, password: String): Message
    fun createSession(): Session
}