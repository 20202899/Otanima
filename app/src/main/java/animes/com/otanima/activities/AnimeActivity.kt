package animes.com.otanima.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.adapters.EpisodesAdapter
import animes.com.otanima.models.Anime
import animes.com.otanima.models.Home
import animes.com.otanima.singletons.AppController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_anime.*

class AnimeActivity : AppCompatActivity() {

    private var mAnime: Anime? = null
    private val mGson = Gson()
    private val mAdapter = EpisodesAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()
        initRequest()
    }

    private fun initView() {
        mAnime = intent.extras["data"] as Anime?
        title = mAnime?.name

        load.setOnClickListener {
            initRequest()
        }

        Glide.with(this)
            .load(mAnime?.img)
            .into(content_img)

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.adapter = mAdapter
    }

    private fun initRequest() {
        recyclerview.visibility = RecyclerView.GONE
        progress_circular.visibility = ProgressBar.VISIBLE
        load.visibility = TextView.GONE
        val stringRequest = object :
            StringRequest(Method.POST, "https://app-otanima.herokuapp.com/episodes-anime", {
                val anime = mGson.fromJson<Home>(it, Home::class.java)
                mAdapter.setData(anime.episodes)
                recyclerview.visibility = RecyclerView.VISIBLE
                progress_circular.visibility = ProgressBar.GONE
            }, {
                recyclerview.visibility = RecyclerView.GONE
                progress_circular.visibility = ProgressBar.GONE
                load.visibility = TextView.VISIBLE
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val map = HashMap<String, String?>()
                map["url"] = mAnime?.url
                return mGson.toJson(map).toByteArray(Charsets.UTF_8)
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        AppController.sInstance?.addRequest(stringRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return return when (item?.itemId) {
            android.R.id.home -> {
                finishAfterTransition(); false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
