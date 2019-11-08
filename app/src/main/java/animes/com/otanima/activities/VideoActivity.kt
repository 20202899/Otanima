package animes.com.otanima.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import animes.com.otanima.R
import animes.com.otanima.models.Episode
import animes.com.otanima.models.Home
import animes.com.otanima.singletons.AppController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_video.*
import org.jsoup.Jsoup



class VideoActivity : AppCompatActivity() {
    private lateinit var mEpisode: Episode
    private val mGson = Gson()
    private lateinit var mHome: Home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        window.decorView.systemUiVisibility = flags

        setContentView(R.layout.activity_video)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initVideo()
    }

    private fun initVideo() {
        val videoIntent = Intent(Intent.ACTION_VIEW)
        mEpisode = intent.extras.getSerializable("data") as Episode
        title = mEpisode.name

        hd.setOnClickListener {
//            container.visibility = View.GONE
//            video.setVideoPath(mHome.streams[1].play_url)
//            video.start()

            videoIntent.setDataAndType(Uri.parse(mHome.streams[1].play_url), "video/*")
            startActivity(videoIntent)
        }

        sd.setOnClickListener {
//            container.visibility = View.GONE
//            video.setVideoPath(mHome.streams[0].play_url)
//            video.start()
            videoIntent.setDataAndType(Uri.parse(mHome.streams[0].play_url), "video/*")
            startActivity(videoIntent)
        }

        requestScraping(mEpisode.url)
    }

    private fun requestScraping(url: String) {
        val stringRequest =
            StringRequest(Request.Method.GET, url, {
                val document = Jsoup.parse(it)
                val v = document.getElementsByTag("script")
                val find = v.map { it.toString() }.find { v -> v.trim().contains("VIDEO_CONFIG") }
                val index = find?.indexOf('{')
                val lastIndex = find?.lastIndexOf('}')
                if (index != null && lastIndex != null) {
                    val value = find.substring(index, lastIndex + 1)
                    mHome = mGson.fromJson<Home>(value, Home::class.java)
                    opt.visibility = View.VISIBLE
                    progress_circular.visibility = View.GONE
                }

            }, {
                Log.d("t", "t")
            })

        stringRequest.retryPolicy = DefaultRetryPolicy(
            9000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        AppController.sInstance?.addRequest(stringRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
