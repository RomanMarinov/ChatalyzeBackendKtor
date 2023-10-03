package ru.marinovdev.features.auth_lackner

class MongoUserDataSourceImpl(
    // тут у лакнера БД

) : UserDataSource {
    override suspend fun getUserByUsername(username: String): User? { // вход
        // тут поместить логику по вытаскиванию юзера из таблицы
        return User(
            username = "username",
            password = "123",
            salt = "salt",
            id = 0
        )

    }

    override suspend fun insertUser(user: User): Boolean { // регистрация
        return true
    }
}