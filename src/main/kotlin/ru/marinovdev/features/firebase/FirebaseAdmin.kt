package ru.marinovdev.features.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.InputStream

object FirebaseAdmin {

    // получаем поток из ресурсов нашего json
    private val serviceAccount: InputStream? =
        this::class.java.classLoader.getResourceAsStream("firebaseservice.json")

    // предоставляем учетные данные
    private val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    fun init(): FirebaseApp = FirebaseApp.initializeApp(options)
}