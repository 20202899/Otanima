package animes.com.otanima.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.ProgressBar
import animes.com.otanima.R
import animes.com.otanima.fragments.AddedEpisodesFragment
import animes.com.otanima.fragments.TodayFragment
import animes.com.otanima.models.Home
import animes.com.otanima.models.Url
import animes.com.otanima.observables.HomeObservable
import animes.com.otanima.singletons.AppController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val mHomeObservable = HomeObservable()

    private var mUrl = "https://www.animesonehd.org/"
    private val mGson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initFragments()
        initRequest()
    }

    private fun initFragments() {

        supportFragmentManager
            .beginTransaction()
            .add(R.id.today, TodayFragment.newInstance().apply {
                mHomeObservable.addObserver(this@apply)
            }).add(R.id.lastAdded, AddedEpisodesFragment.newInstance().apply {
                mHomeObservable.addObserver(this@apply)
            })
            .commit()
    }

    private fun initRequest() {
        progress_circular.visibility = ProgressBar.VISIBLE
//        container_today.visibility = LinearLayout.GONE
        val stringRequest = object : StringRequest(Request.Method.POST, "https://app-otanima.herokuapp.com/home-anime", {
            val home = mGson.fromJson<Home>(it, Home::class.java)
            mHomeObservable.setValue(home)
            progress_circular.visibility = ProgressBar.GONE
            container_today.visibility = LinearLayout.VISIBLE
        }, {
            Log.d("t", "t")
        }) {

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val ob = Url("https://www.animesonehd.org")
                return mGson.toJson(ob).toByteArray(Charsets.UTF_8)
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        AppController.sInstance?.addRequest(stringRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
//            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
