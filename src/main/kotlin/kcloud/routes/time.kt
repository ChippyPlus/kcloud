package kcloud.routes

import kotlinx.coroutines.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Thread.sleep

class Channel {
    private var count = 0

    fun increment() {
        count += 1
    }

    fun get(): Int = count

    fun reset() {
        count = 0
    }

    fun set(value: Int) {
        count = value
    }

    fun append(value: Int) {
        count += value
    }

    fun deAppend(value: Int) {
        count -= value
    }
}

val channel = Channel()

fun count() {
    while (true) {
        sleep(1000)
        channel.increment()
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun Application.configureTimeRouting() {
    GlobalScope.launch { count() }
    routing {
        get("/time/get") {
            call.respond(channel.get())
        }
        put("/time/reset") {
            channel.reset()
            call.response.status(HttpStatusCode.NoContent)
        }
        patch("/time/increment") {
            val value = call.receive<Map<String, Int>>()["arg1"]!!
            channel.append(value)
            call.response.status(HttpStatusCode.NoContent)
        }
        patch("/time/decrement") {
            val value = call.receive<Map<String, Int>>()["arg1"]!!
            channel.deAppend(value)
            call.response.status(HttpStatusCode.NoContent)
        }
    }
}



