package io.maslick.denial.server

class Baket(private val capacity: Long, private val windowTimeInMillis: Long) {
    private var tokensLeft: Long = 0
    private var lastRefillTimeStamp: Long = 0

    init {
        tokensLeft = capacity
        lastRefillTimeStamp = System.currentTimeMillis()
    }

    @Synchronized
    fun tryConsume(): Boolean {
        refill()
        return if (tokensLeft > 0) {
            --tokensLeft
            true
        } else false
    }

    private fun refill() {
        val now = System.currentTimeMillis()
        if (now - lastRefillTimeStamp > windowTimeInMillis) {
            tokensLeft = capacity
            lastRefillTimeStamp = now
        }
    }
}