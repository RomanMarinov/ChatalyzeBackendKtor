//package ru.marinovdev.features.auth_lackner
//
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.auth.jwt.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import ru.marinovdev.features.auth_lackner.requests.AuthRequest
//import ru.marinovdev.features.auth_lackner.responses.AuthResponse
//import ru.marinovdev.features.auth_lackner.security.hashing.HashingService
//import ru.marinovdev.features.auth_lackner.security.hashing.SaltedHash
//import ru.marinovdev.features.auth_lackner.security.token.TokenClaim
//import ru.marinovdev.features.auth_lackner.security.token.TokenConfig
//import ru.marinovdev.features.auth_lackner.security.token.TokenService
//
//fun Route.signUp(
//    hashingService: HashingService,
//    userDataSource: UserDataSource
//) {
//    // получаю от юзера новый имя и пароль
//    post("signup") {
//        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
//            // если запрос не содержит тела или тело не соответствует ожидаемому формату), то возвращается код ошибки
//            call.respond(HttpStatusCode.BadRequest)
//            return@post
//        }
//
//        // простая проверка но ее можно не проходить
//        val areFieldsBlank = request.email.isBlank() || request.password.isBlank()
//        val isPasswordShort = request.password.length < 5
//        if (areFieldsBlank || isPasswordShort) {
//            call.respond(HttpStatusCode.Conflict)
//            return@post
//        }
//
//        // сгенерировать отсортировнный хеш и пароль пользователя
//        val saltHash: SaltedHash = hashingService.generateSaltHash(request.password)
//        val user = User(
//            email = request.email,
//            password = saltHash.hash,
//            salt = saltHash.salt,
//        )
//
//        // проверим было ли это подтверждено
//        // записываем юзера в бд
//        val wasAcknowLedged = userDataSource.insertUser(user = user)
//        if (!wasAcknowLedged) {
//            call.respond(HttpStatusCode.Conflict)
//            return@post
//        }
//
//        // запись прошла успешно
//        call.respond(HttpStatusCode.OK)
//    }
//}
//
//
//// вход в систему
//fun Route.signIn(
//    hashingService: HashingService,
//    userDataSource: UserDataSource,
//    tokenService: TokenService,
//    tokenConfig: TokenConfig
//) {
//    post("signin") {
//        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
//            call.respond(HttpStatusCode.BadRequest)
//            return@post
//        }
//
//        // найти юзера в бд под которым он пытался войти в систему
//        val user = userDataSource.getUserByEmail(request.email)
//        if (user == null) {
//            call.respond(HttpStatusCode.Conflict, "Неправльное имя пользователя или пароль")
//            return@post
//        }
//
//        // если мы дойдем сюда то будем уверены что пользователь с таким именем в бд существует
//        // используем функицю проверки для сравнения хешей
//        val isValidPassword = hashingService.verify(
//            value = request.password, // пароль запроса
//            saltedHash = SaltedHash( // новый экз который создаем и это хешированное значение из бд
//                hash = user.password,
//                salt = user.salt
//            )
//        )
//
//        if (!isValidPassword) {
//            call.respond(HttpStatusCode.Conflict, "Неправльное имя пользователя или пароль")
//            return@post
//        }
//
//        // тут мы уверены что юзер ввел правильный пароль
//        // и генерируем  токен и прикрепить его к ответу чтобы пользователь мог сохранить его в настройках
//        val token = tokenService.generate(
//            config = tokenConfig, //объект конфигурации токена, содержащий настройки эмитента, аудитории, срока действия токена и секретный ключ для подписи токена.
//            TokenClaim( // - объект, содержащий имя и значение утверждения (claim), которое будет добавлено в токен.
//                email = "email",
//                value = user.email
//            )
//        )
//
//        // отправить клиенту
//        call.respond(
//            HttpStatusCode.OK,
//            message = AuthResponse(
//                token = token
//            )
//        )
//
//    }
//}
//
//// функия для того чтобы при закрытия приложения и повторном запуске
//// юзер не проходил вход а поппал на главный экран
//fun Route.authenticate() {
//    authenticate {
//        get("authenticate") {
//            call.respond(HttpStatusCode.OK)
//        }
//    }
//}
//
//fun Route.getSecretInfo() {
//    authenticate {
//        get("secret") {
//            val principal = call.principal<JWTPrincipal>()
//            val email = principal?.getClaim("email", String::class)
//            call.respond(HttpStatusCode.OK, "Your email $email")
//        }
//    }
//}