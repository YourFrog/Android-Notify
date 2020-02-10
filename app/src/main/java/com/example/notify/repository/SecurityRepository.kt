package com.example.notify.repository

import com.example.notify.structure.response.Register
import com.example.notify.structure.response.SignIn
import com.google.gson.Gson
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request

class SecurityRepository
{
    fun signIn(deviceUID: String, hash: String): Single<SignIn?> {
        return Single.fromCallable {
            val request = Request
                .Builder()
                .url("http://yourfrog12.usermd.net/notify/sign.php?device_uid=$deviceUID&hash=$hash")
                .get()
                .build()

            OkHttpClient()
                .newCall(request)
                .execute()
        }.map {
            it.body?.string()?.let { content ->
                val gson = Gson()
                val obj = gson.fromJson(content, SignIn::class.java)

                return@map obj
            }

            return@map null
        }
    }

    fun register(deviceUID: String): Single<Register?> {
        return Single.fromCallable {
            val request = Request
                .Builder()
                .url("http://yourfrog12.usermd.net/notify/register.php?device_uid=$deviceUID")
                .get()
                .build()

            OkHttpClient()
                .newCall(request)
                .execute()
        }.map {
            it.body?.string()?.let { content ->
                val gson = Gson()
                val obj = gson.fromJson(content, Register::class.java)

                return@map obj
            }

            return@map null
        }
    }
}