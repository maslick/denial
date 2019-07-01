package io.maslick.denial.server

import org.junit.Assert.assertEquals
import org.junit.Test


class ComponentTest {
    @Test
    fun test1_bucket4j() {
        fire7requestsAndSleep5s(LuckyBuckets())
    }

    @Test
    fun test2_bucket4j() {
        countSuccessWithin10s(LuckyBuckets())
    }

    @Test
    fun test1_baket() {
        fire7requestsAndSleep5s(NaiveBuckets())
    }

    @Test
    fun test2_baket() {
        countSuccessWithin10s(NaiveBuckets())
    }

    private fun fire7requestsAndSleep5s(service: IDenial) {
        val clientId = "test"
        assertEquals(true,  service.checkOk(clientId))
        assertEquals(true,  service.checkOk(clientId))
        assertEquals(true,  service.checkOk(clientId))
        assertEquals(true,  service.checkOk(clientId))
        assertEquals(true,  service.checkOk(clientId))
        assertEquals(false, service.checkOk(clientId))
        assertEquals(false, service.checkOk(clientId))

        Thread.sleep(5000)
        assertEquals(true,  service.checkOk(clientId))
    }

    private fun countSuccessWithin10s(service: IDenial) {
        val clientId = "test"

        var counter = 0
        val now = System.currentTimeMillis()

        while (System.currentTimeMillis() - now < 10000)
            if (service.checkOk(clientId)) counter++

        assertEquals(10, counter)
    }
}