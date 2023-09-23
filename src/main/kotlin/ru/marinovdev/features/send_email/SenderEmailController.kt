package ru.marinovdev.features.send_email

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.marinovdev.utils.isValidEmail

class SenderEmailController(private val call: ApplicationCall) {

    // передаем модель которую получили от клиента
    suspend fun sendEmail() {
        try {
            val senderEmailReceiveRemote = call.receive<SenderEmailReceiveRemote>()
            if (!senderEmailReceiveRemote.email.isValidEmail()) {
                call.respond(HttpStatusCode.BadRequest, "Email is not valid")
            }
            println("Пришло от клиента при отправке от него почты если он забыл пароль =$senderEmailReceiveRemote")

            // для реальной ситуации нужна проверка наличия такой почты в бд
            // и возвращать пароль для этой почты
            val email = "marinov37@mail.ru"
            val password = "123qweRT"
            if (email != null) {
                // тут уже нужно отправить полученную почту на почту клиента


                SendEmailToUser.sendEmail(emailForSending = email, password = password)


                // тут надо отпарвить клиенту сообщение что пароль успешно отправлен на указанную почту
                call.respond(HttpStatusCode.Conflict, "This is email $email already exist")
            } else {
                call.respond(HttpStatusCode.BadRequest, "This is email $email doesn't exist")
            }
        } catch (e: Exception) {
            println("try catch SenderEmailController e=" + e)
        }
    }
}