package io.maslick.denial.server

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap


@SpringBootApplication
open class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}

@RestController
class API(private val service: IDenial) {
    @GetMapping("/")
    fun getEndpoint(@RequestParam clientId: String): ResponseEntity<String> {
        return if (!service.checkOk(clientId)) ResponseEntity(SERVICE_UNAVAILABLE)
        else ResponseEntity.ok("ok")
    }
}

interface IDenial {
    fun checkOk(id: String): Boolean
}

@Service
@ConditionalOnProperty(name = ["bucket.implementation"], havingValue = "bucket4j")
class LuckyBuckets: IDenial {
    private val hache = ConcurrentHashMap<String, Bucket>()
    override fun checkOk(id: String) = hache.pull(id).tryConsume(1)

    private fun ConcurrentHashMap<String, Bucket>.pull(key: String): Bucket {
        return this.computeIfAbsent(key) { Bucket4j.builder()
            .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(5))))
            .build()
        }
    }
}

@Service
@ConditionalOnProperty(name = ["bucket.implementation"], havingValue = "naive", matchIfMissing = true)
class NaiveBuckets: IDenial {
    private val hache = ConcurrentHashMap<String, Baket>()
    override fun checkOk(id: String) = hache.computeIfAbsent(id) { Baket(5L, 5000L) }.tryConsume()
}