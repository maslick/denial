package io.maslick.denial.cli

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.ConnectException
import java.util.concurrent.Executors


fun main(args: Array<String>) {
    var numberOfClients = 1
    val maxNumberOfClients = 30
    if (args.isNotEmpty()) numberOfClients = args[0].toInt()
    if (numberOfClients > maxNumberOfClients) numberOfClients = maxNumberOfClients

    val threadPool = Executors.newFixedThreadPool(numberOfClients)
    println("Number of clients: $numberOfClients")

    val url = System.getProperty("server", "http://localhost:8080")
    val api = Retrofit.Builder()
        .baseUrl(url)
        .client(OkHttpClient())
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(IRestDenial::class.java)

    for (i in 1..numberOfClients) threadPool.submit { Process(api, url).start("client$i") }

    println("Press ENTER to exit...\n")
    System.`in`.read()
    println("Shutting down...")
    threadPool.shutdownNow()
}

interface IProcess {
    fun start(clientId: String)
}

interface IRestDenial {
    @GET("/")
    fun call(@Query("clientId") clientId: String): Call<String>
}

class Process(private val api: IRestDenial, private val url: String) : IProcess {
    override fun start(clientId: String) {
        while (!Thread.interrupted()) {
            try {
                val resp = api.call(clientId).execute()
                val code = resp.code()
                val body = resp.body()?:"-"
                print("calling $url/?clientId=$clientId -> $code : ($body)\n")
            }
            catch (e: ConnectException) { println("Error: could not connect") }
            Thread.sleep((10..1000).shuffled().first().toLong())
        }
    }
}
