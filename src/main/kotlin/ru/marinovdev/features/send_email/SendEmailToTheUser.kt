package ru.marinovdev.features.send_email

import java.util.*
import java.util.concurrent.Executors
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendEmailToTheUser {
    companion object {
        fun sendEmail(
            emailForSending: String,
            code: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            try {
                val executorService = Executors.newSingleThreadExecutor()
                executorService.execute {
                    val message: Message = createEmailMessage(
                        emailForSending = emailForSending,
                        code = code
                    )
                    Transport.send(message)
                    executorService.shutdown() // Завершение работы ExecutorService
                }
                onSuccess()
            } catch (e: Exception) {
                println(":::::::::::try catch sendEmail onFailure e=" + e.localizedMessage)
                onFailure(e)
            }
        }

        @Throws(MessagingException::class)
        private fun createEmailMessage(
            emailForSending: String,
            code: String
        ): Message {

            val session = createSession()
            val message: Message = MimeMessage(session)
            message.subject = "Verification code the Chatalyze app"
            val htmlContent = "<html><head><meta charset='UTF-8'></head><body>" +
                    "<p><strong>My dear friend!</strong></p>" +
                    "<p><strong>Your code: </strong></p><h1 style='color: #008080;'>" + code + "</h1>" +
                    "</body></html>"
            message.setContent(htmlContent, "text/html; charset=utf-8")
            val iAm = "marinov.dev.88@gmail.com"
            val iAmY = "marinov37@mail.ru"
            val address: Address = InternetAddress(emailForSending)
            message.setRecipient(Message.RecipientType.TO, address)
            return message
        }

        private fun createSession(): Session {
            val properties = Properties()
            properties["mail.smtp.auth"] = true
            properties["mail.smtp.host"] = "smtp.gmail.com"
            properties["mail.smtp.port"] = 587
            properties["mail.smtp.starttls.enable"] = true
            properties["mail.transport.protocol"] = "smtp"
            properties["mail.smtp.ssl.protocols"] = "TLSv1.2"
            return Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    val name_passwords = "pnfhesmchfrqepii"
                    val gmail = "chatalyze.help@gmail.com"
                    val passwordGmail = "nanSag-japgyr-xorru5"
                    return PasswordAuthentication(gmail, name_passwords)
                }
            })
        }
    }
}