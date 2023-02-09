package me.ikvarxt.request

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap


/**
 * @author ikvarxt
 */
interface GitHubService {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Call<List<Repo>>

    @GET("orgs/{org}/members")
    fun listOrgMembers(@Path("org") orgName: String, @QueryMap filters: Map<String, String>): Call<List<User>>
}

private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request()
        val builder = request.newBuilder()
        val url = request.url()

        // to check out the real request url
        Log.d("TAG", "interceptor url: $url")

        chain.proceed(builder.build())
    }
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val service: GitHubService = retrofit.create(GitHubService::class.java)

fun getRepos(
    block: (repos: List<Repo>) -> Unit,
    error: (t: String?) -> Unit
) {
    service.listRepos("octocat").enqueue(object : Callback<List<Repo>> {
        override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
            response.body()?.let {
                block(it)
            } ?: error(response.errorBody()?.string())
        }

        override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
            error(t)
        }
    })
}

fun listOrgMembers(
    orgName: String,
    map: Map<String, String>,
    block: (users: List<User>) -> Unit,
    error: (t: String?) -> Unit
) {
    service.listOrgMembers(orgName, map).enqueue(object : Callback<List<User>> {
        override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
            response.body()?.let {
                block(it)
            } ?: error(response.errorBody()?.string())
        }

        override fun onFailure(call: Call<List<User>>, t: Throwable) {
            error(t)
        }
    })
}

@Keep
data class Repo(
    val id: Int,
    val name: String,
    @SerializedName(value = "full_name") val fullName: String,
    @SerializedName(value = "html_url") val htmlUrl: String
) {
    fun shortToString(): String {
        return "Repo(fullName='$fullName')"
    }

    override fun toString(): String {
        return "Repo(id=$id, name='$name', fullName='$fullName', htmlUrl='$htmlUrl')"
    }
}

@Keep
data class User(
    val login: String,
    val url: String,
) {
    override fun toString(): String {
        return "User(login='$login', url='$url')"
    }
}