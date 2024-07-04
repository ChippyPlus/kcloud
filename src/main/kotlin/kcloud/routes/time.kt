package kcloud.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep

class Channel {
    private var count: ULong = 0u

    fun increment() {
        count += 1u
    }

    fun get(): ULong = count

    fun reset() {
        count = 0u
    }

    fun set(value: Int) {
        count = value.toULong()
    }

    fun append(value: ULong) {
        count += value
    }

    fun deAppend(value: ULong) {
        count -= value
    }
}

val timeChannel = Channel()

suspend fun count() {
    while (true) {
        withContext(Dispatchers.IO) {
            sleep(1000)
        }
        timeChannel.increment()
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun Application.configureTimeRouting() {
    GlobalScope.launch { count() }
    routing {
        authenticate("basic-auth") {

            get("/time/get") {
                call.respond(timeChannel.get())
            }
            put("/time/reset") {
                timeChannel.reset()
                call.response.status(HttpStatusCode.NoContent)
            }
            patch("/time/increment") {
                val value = call.receive<Map<String, Int>>()["arg1"]!!
                timeChannel.append(value.toULong())
                call.response.status(HttpStatusCode.NoContent)
            }
            patch("/time/decrement") {
                val value = call.receive<Map<String, Int>>()["arg1"]!!
                timeChannel.deAppend(value.toULong())
                call.response.status(HttpStatusCode.NoContent)
            }
            put("/time/set") {
                val value = call.receive<Map<String, Int>>()["arg1"]!!
                timeChannel.set(value)
                call.response.status(HttpStatusCode.NoContent)
            }
        }
    }
}



