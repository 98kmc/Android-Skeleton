@file:Suppress("unused")
package com.example.jetpack_compose_skeleton.utils.networking

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

/**
 * An enumeration representing HTTP request methods for network requests.
 * These methods include GET, POST, PUT, PATCH, and DELETE.
 */
enum class NetworkRequestMethod {
    Get, Post, Put, Patch, Delete
}

/**
 * Represents a network response containing information about the response status and data.
 *
 * @param code The HTTP status code returned in the response.
 * @param status A human-readable status message.
 * @param success Indicates whether the request was successful or not.
 * @param error A nullable string containing error details if the request was not successful.
 * @param data A nullable generic type containing the response data.
 * @param T The type of data expected in the response body.
 */
data class NetworkResponse<T>(
    val code: Int,
    val status: String,
    val success: Boolean,
    val error: String?,
    val data: T?
)

/**
 * Singleton object representing the Networking layer for managing network requests and API configurations.
 * This layer provides utilities for making HTTP requests and handling responses.
 *
 * Usage:
 *
 * // initialize the Networking with a BaseURL.
 *```
 *    initNetworking(baseURL)
 *```
 *
 * // Or You can Initialize the Networking with a custom Retrofit Instance and client too.
 *```
 *    initNetworking(
 *             retrofit = Retrofit.Builder()
 *                 .baseUrl(baseURL)
 *                 .addConverterFactory(GsonConverterFactory.create())
 *                 .build(),
 *             client = OkHttpClient.Builder()
 *                 .connectTimeout(30, TimeUnit.SECONDS)
 *                 .readTimeout(30, TimeUnit.SECONDS)
 *                 .build()
 *      )
 *```
 *
 * // Making a simple Get Request.
 *```
 *      CoroutineScope(Dispatchers.IO).launch {
 *
 *          val result = networkRequest(
 *             endpoint = "/yourEndpoint",
 *             method = NetworkRequestMethod.Get,
 *             responseType = AnyResponseType::class.java
 *         )
 *
 *          // Handle the 'result' here
 *      }
 *```
 */
private object Networking {

    /**
     * Internal interface representing the API service with HTTP request methods.
     */
    private interface ApiService {

        /**
         * Performs a GET request to the specified endpoint.
         *
         * @param endpoint The URL endpoint for the GET request.
         * @return A [Call] object with a [JsonElement] response.
         */
        @GET
        fun get(@Url endpoint: String): Call<JsonElement>

        /**
         * Performs a POST request to the specified endpoint without a request body.
         *
         * @param endpoint The URL endpoint for the POST request.
         * @return A [Call] object with a [JsonElement] response.
         */
        @POST
        fun post(@Url endpoint: String): Call<JsonElement>

        /**
         * Performs a POST request to the specified endpoint with a request body.
         *
         * @param endpoint The URL endpoint for the POST request.
         * @param body The request body as a [RequestBody].
         * @return A [Call] object with a [JsonElement] response.
         */
        @POST
        fun post(@Url endpoint: String, @Body body: RequestBody): Call<JsonElement>

        /**
         * Performs a PUT request to the specified endpoint without a request body.
         *
         * @param endpoint The URL endpoint for the PUT request.
         * @return A [Call] object with a [JsonElement] response.
         */
        @PUT
        fun put(@Url endpoint: String): Call<JsonElement>

        /**
         * Performs a PUT request to the specified endpoint with a request body.
         *
         * @param endpoint The URL endpoint for the PUT request.
         * @param body The request body as a [RequestBody].
         * @return A [Call] object with a [JsonElement] response.
         */
        @PUT
        fun put(@Url endpoint: String, @Body body: RequestBody): Call<JsonElement>

        /**
         * Performs a PATCH request to the specified endpoint without a request body.
         *
         * @param endpoint The URL endpoint for the PATCH request.
         * @return A [Call] object with a [JsonElement] response.
         */
        @PATCH
        fun patch(@Url endpoint: String): Call<JsonElement>

        /**
         * Performs a PATCH request to the specified endpoint with a request body.
         *
         * @param endpoint The URL endpoint for the PATCH request.
         * @param body The request body as a [RequestBody].
         * @return A [Call] object with a [JsonElement] response.
         */
        @PATCH
        fun patch(@Url endpoint: String, @Body body: RequestBody): Call<JsonElement>

        /**
         * Performs a DELETE request to the specified endpoint.
         *
         * @param endpoint The URL endpoint for the DELETE request.
         * @return A [Call] object with a [JsonElement] response.
         */
        @DELETE
        fun delete(@Url endpoint: String): Call<JsonElement>
    }

    /**
     * Indicates whether the Networking object has been initialized or not.
     */
    var isInitialized: Boolean = false

    private const val tag = "Networking.service:" // A constant tag used for logging purposes.

    private lateinit var api: ApiService

    private val gson = Gson()

    /**
     * The default OkHttpClient with a 30-second timeout for network requests.
     */
    private var defaultOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * The Retrofit instance used for network communication.
     */
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://google.com")
        .client(defaultOkHttpClient)
        .addConverterFactory(GsonConverterFactory .create())
        .build()

    /**
     * Initializes the Networking object with a custom Retrofit instance and optional OkHttpClient.
     *
     * @param retrofitInstance A custom Retrofit instance for network communication.
     * @param client An optional custom OkHttpClient. If provided, it replaces the default client.
     */
    fun init(
        retrofitInstance: Retrofit,
        client: OkHttpClient? = null
    ) {

        retrofit = retrofitInstance

        if (client != null) {
            retrofit = retrofit.newBuilder()
                .client(client)
                .build()
        }

        api = retrofit.create(ApiService::class.java)
        isInitialized = true
    }

    /**
     * Initializes the Networking object with a custom base URL.
     *
     * @param baseUrl The base URL for the network requests.
     */
    fun init(
        baseUrl: String
    ) {

        retrofit = retrofit.newBuilder()
            .baseUrl(baseUrl)
            .build()

        api = retrofit.create(ApiService::class.java)
        isInitialized = true
    }

    /**
     * Executes a network request with the specified parameters.
     *
     * @param method The HTTP request method (GET, POST, PUT, PATCH, DELETE).
     * @param endpoint The URL endpoint for the request.
     * @param headers Optional headers to be included in the request.
     * @param params Optional parameters to be included in the request body (for POST, PUT, and PATCH).
     * @param responseType The response type class for deserialization.
     * @return A [NetworkResponse] object containing the response data and status.
     */
    fun <T> executeNetworkRequest(
        method: NetworkRequestMethod,
        endpoint: String,
        headers: Map<String, String>?,
        params: Map<String, Any>?,
        responseType: Class<T>
    ): NetworkResponse<T> {

        val service = getApiService(headers)

        val response = when (method) {

            NetworkRequestMethod.Get -> service.get(endpoint).execute()

            NetworkRequestMethod.Post -> if (params != null) {
                service.post(endpoint, createBodyRequest(params)).execute()
            } else {
                service.post(endpoint).execute()
            }


            NetworkRequestMethod.Put -> if (params != null) {
                service.put(endpoint, createBodyRequest(params)).execute()
            } else {
                service.put(endpoint).execute()
            }

            NetworkRequestMethod.Patch -> if (params != null) {
                service.patch(endpoint, createBodyRequest(params)).execute()
            } else {
                service.patch(endpoint).execute()
            }

            NetworkRequestMethod.Delete -> service.delete(endpoint).execute()
        }

        printApiResponse(
            response = response,
            headers = headers,
            params = params
        )

        return buildNetworkResponse(
            response = response,
            responseType = responseType
        )
    }

    /**
     * Internal helper function to get the API service with optional headers.
     *
     * @param headers Optional headers to be included in the request.
     * @return The [ApiService] with headers if provided, or the default [api] if headers are null.
     */
    private fun getApiService(
        headers: Map<String, String>?
    ) = headers?.let { addHeaders(it) } ?: api


    /**
     * Internal helper function to add headers to the OkHttpClient.
     *
     * @param headers The headers to be added to the request.
     * @return An [ApiService] with the updated OkHttpClient including headers.
     */
    private fun addHeaders(
        headers: Map<String, String>
    ): ApiService {

        val newOkHttpClient = defaultOkHttpClient.newBuilder().addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            chain.proceed(requestBuilder.build())
        }.build()

        val newRetrofit = retrofit.newBuilder().client(newOkHttpClient).build()

        return newRetrofit.create(ApiService::class.java)
    }

    /**
     * Internal helper function to convert the JSON response to a data object.
     *
     * @param responseBody The JSON response body.
     * @param model The data model class for deserialization.
     * @return The deserialized data object of type [T].
     */
    private fun <T> convertResponseToData(
        responseBody: JsonElement?,
        model: Class<T>
    ) = gson.fromJson(responseBody, model)

    /**
     * Internal helper function to create a request body from parameters.
     *
     * @param params The parameters to be included in the request body.
     * @return A [RequestBody] containing the JSON representation of the parameters.
     */
    private fun createBodyRequest(
        params: Map<String, Any>
    ) = gson.toJson(params).toRequestBody("application/json".toMediaTypeOrNull())

    /**
     * Internal helper function to build a [NetworkResponse] object from a Retrofit response.
     *
     * @param response The Retrofit response.
     * @param responseType The response type class for deserialization.
     * @return A [NetworkResponse] object containing the response data and status.
     */
    private fun <T> buildNetworkResponse(
        response: Response<JsonElement>,
        responseType: Class<T>
    ): NetworkResponse<T> {

        val code = response.code()
        val status = response.message()
        val success = response.isSuccessful
        val error: String?
        val data: T?

        if (success) {
            error = null
            data = convertResponseToData(response.body(), responseType)
        } else {
            error = response.errorBody()?.string() ?: "Unknown Error"
            data = null
        }

        return NetworkResponse(
            code = code, status = status, success = success, error = error, data = data
        )
    }

    private fun printApiResponse(
        response: Response<JsonElement>,
        headers: Map<String, String>? = null,
        params: Map<String, Any>? = null
    ) {

        val code = response.code()
        val method = response.raw().request.method
        val timeout = response.raw().receivedResponseAtMillis - response.raw().sentRequestAtMillis
        val url = response.raw().request.url
        val spacer = "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
        val results = response.body().toString().split(Regex("\\},"))

        Log.i(tag, "Api Response Info:\n${response.headers()}")

        Log.d(tag, spacer)
        Log.i(tag, "[$timeout ms] >> $method | $code : $url")

        headers?.let {

            Log.i(tag, "@Headers >>")
            for (header in headers) {
                Log.i(tag, "[ ${header.key} : ${header.value} ]")
            }
        }

        params?.let {

            Log.i(tag, "@Params >>")
            for (param in params) {
                Log.i(tag, "[ ${param.key} : ${param.value} ]")
            }
        }

        Log.i(tag, "Api Response: ")
        for (result in results) Log.d(">>", result + if (result.length < 5) " " else "},")

        Log.d(tag, spacer)
    }
}

/**
 * Initializes the Networking class with a custom Retrofit instance and optional OkHttpClient.
 *
 * @param retrofit A custom Retrofit instance for network communication.
 * @param client An optional custom OkHttpClient. If provided, it replaces the default client.
 */
fun initNetworking(retrofit: Retrofit, client: OkHttpClient? = null) {

    Networking.init(
        retrofitInstance = retrofit,
        client = client
    )
}

/**
 * Initializes the Networking class with a custom base URL.
 *
 * @param baseUrl The base URL for network requests.
 */
fun initNetworking(baseUrl: String) {

    Networking.init(baseUrl = baseUrl)
}

/**
 * Performs a network request with the specified parameters.
 *
 * @param endpoint The URL endpoint for the request.
 * @param method The HTTP request method (GET, POST, PUT, PATCH, DELETE).
 * @param responseType The response type class for deserialization.
 * @param params Optional parameters to be included in the request body (for POST, PUT, and PATCH).
 * @param headers Optional headers to be included in the request.
 * @return A [NetworkResponse] object containing the response data and status.
 *
 * @throws NullPointerException if Networking class has not been initialized using [initNetworking].
 */
fun <T> networkRequest(
    endpoint: String,
    method: NetworkRequestMethod,
    responseType: Class<T>,
    params: Map<String, Any>? = null,
    headers: Map<String, String>? = null
): NetworkResponse<T> {

    if (!Networking.isInitialized) {

        throw NullPointerException("The Networking class has not been initialized; please execute initNetworking() before fetching data.")
    }

    return try {

        Networking.executeNetworkRequest(
            method = method,
            endpoint = endpoint,
            headers = headers,
            params = params,
            responseType = responseType
        )
    } catch (e: Exception) {

        Log.e("Networking.service:", "Networking Request Error: ${e.message}")
        Log.e("Networking.service:", e.stackTraceToString())

        NetworkResponse(
            code = 0,
            status = "Error",
            success = false,
            error = e.message ?: "Unknown Error",
            data = null
        )
    }
}

