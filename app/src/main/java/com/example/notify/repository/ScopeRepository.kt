package com.example.notify.repository

import com.example.notify.storage.AccessStorage
import com.example.notify.structure.response.*
import com.google.gson.Gson
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request

class ScopeRepository (
    val accessStorage: AccessStorage
) {
    fun join(code: String): Single<Boolean> {
        val session = accessStorage.getSession() ?: ""

        return Single.fromCallable {
            val request = Request
                .Builder()
                .url("http://yourfrog12.usermd.net/notify/scope.php?action=join&session=$session&code=$code")
                .get()
                .build()

            OkHttpClient()
                .newCall(request)
                .execute()
        }.map {
            it.body?.string()?.let { content ->
                val gson = Gson()
                val obj = gson.fromJson(content, SimpleBoolean::class.java)

                return@map obj.content?.result ?: false
            }

            return@map false
        }
    }

    fun share(scopeID: Int): Single<String> {
        val session = accessStorage.getSession() ?: ""

        return Single.fromCallable {
            val request = Request
                .Builder()
                .url("http://yourfrog12.usermd.net/notify/scope.php?action=share&session=$session&scope=$scopeID")
                .get()
                .build()

            OkHttpClient()
                .newCall(request)
                .execute()
        }.map {
            it.body?.string()?.let { content ->
                val gson = Gson()
                val obj = gson.fromJson(content, Share::class.java)

                return@map obj.content?.code ?: ""
            }

            return@map ""
        }
    }

    fun add(name: String): Single<Boolean> {
        val session = accessStorage.getSession() ?: ""

        return Single.fromCallable {
            val request = Request
                .Builder()
                .url("http://yourfrog12.usermd.net/notify/scope.php?action=add&session=$session&name=$name")
                .get()
                .build()

            OkHttpClient()
                .newCall(request)
                .execute()
        }.map {
            it.body?.string()?.let { content ->
                val gson = Gson()
                val obj = gson.fromJson(content, SimpleBoolean::class.java)

                return@map obj.content?.result ?: false
            }

            return@map false
        }
    }

    fun getList(): Single<ScopeList?> {
        val session = accessStorage.getSession() ?: ""

        return Single.fromCallable {
            val request = Request
                .Builder()
                .url("http://yourfrog12.usermd.net/notify/scope.php?session=$session")
                .get()
                .build()

            OkHttpClient()
                .newCall(request)
                .execute()
        }.map {
            it.body?.string()?.let { content ->
                val gson = Gson()
                val obj = gson.fromJson(content, ScopeList::class.java)

                return@map obj
            }

            return@map null
        }
    }
}