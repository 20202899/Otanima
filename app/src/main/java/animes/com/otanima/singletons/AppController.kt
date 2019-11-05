package animes.com.otanima.singletons

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class AppController : Application() {

    private lateinit var mQueue: RequestQueue

    companion object {
        var sInstance: AppController? = null
    }

    override fun onCreate() {
        super.onCreate()
        mQueue = Volley.newRequestQueue(this)
        sInstance = this
    }

    fun<T> addRequest(request: Request<T>) {
        mQueue.add(request)
    }
}