package com.skygolf.script

import okhttp3.OkHttpClient
import okhttp3.Request

fun main(args: Array<String>) {

    val m = MyClass()
    m.run()

    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://httpbin.org/get")
        .build()

    client.newCall(request).execute().use { response ->
        println("HTTP статус: ${response.code}")
        println("Тіло відповіді:")
        println(response.body.string())
    }

    println("All done")
}