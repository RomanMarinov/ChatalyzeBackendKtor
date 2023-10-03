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
            password: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {
                    val message: Message = createEmailMessage(
                        emailForSending = emailForSending,
                        password = password
                    )
                    Transport.send(message)
                    onSuccess()
                } catch (e: Exception) {
                    onFailure(e)
                }
            }
            executorService.shutdown() // Завершение работы ExecutorService
        }

        @Throws(MessagingException::class)
        private fun createEmailMessage(emailForSending: String, password: String): Message {

            val session = createSession()
            val message: Message = MimeMessage(session)
            message.subject = "Password to enter the Chatalyze app"
            val htmlContent = "<html><head><meta charset='UTF-8'></head><body>" +
                    "<p><strong>Hello my dear friend!</strong></p>" +
                    "<p><strong>Password: </strong></p><h1 style='color: #008080;'>" + password + "</h1>" +
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