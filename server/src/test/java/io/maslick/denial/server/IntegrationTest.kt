package io.maslick.denial.server

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.test.context.junit4.SpringRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class IntegrationTest {
    @LocalServerPort private var port: Int? = null
    @Autowired private lateinit var restTemplate: TestRestTemplate

    @Test
    fun make5callsAndWait5000() {
        val clientId = "test1"
        for (i in 1..3) {
            println("iteration #$i")
            assertEquals(OK, makeRestCall(clientId))
            assertEquals(OK, makeRestCall(clientId))
            assertEquals(OK, makeRestCall(clientId))
            assertEquals(OK, makeRestCall(clientId))
            assertEquals(OK, makeRestCall(clientId))
            assertEquals(SERVICE_UNAVAILABLE, makeRestCall(clientId))
            Thread.sleep(5000)
        }
    }

    @Test
    fun make1callAndWait5000() {
        val clientId = "test2"
        for (i in 1..3) {
            println("iteration #$i")
            assertEquals(OK, makeRestCall(clientId))
            Thread.sleep(5000)
        }
    }

    @Test
    fun make1callEvery1000() {
        val clientId = "test3"
        for (i in 1..16) {
            println("\niteration #$i")
            assertEquals(OK, makeRestCall(clientId))
            Thread.sleep(1000)
        }
    }

    @Test
    fun countSuccessesWithin10s() {
        var counter = 0
        val now = System.currentTimeMillis()
        while (System.currentTimeMillis() - now < 10000) {
            if (makeRestCall("testSuccesses") == OK) counter++
            Thread.sleep(100)
        }
        assertEquals(10, counter)
    }

    private fun makeRestCall(clientId: String): HttpStatus {
        val url = "http://localhost:$port/?clientId=$clientId"
        val response = restTemplate.getForEntity(url, String::class.java)
        println("${System.currentTimeMillis().formatDate()} -> " + (response.body ?: "limit reached :("))
        return response.statusCode
    }

    private fun Long.formatDate(): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        return formatter.format(Date(this))
    }
}