package animes.com.otanima.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.TransitionManager
import android.util.Log
import android.view.MenuItem
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.adapters.EpisodesAdapter
import animes.com.otanima.database.AppDataBase
import animes.com.otanima.models.Anime
import animes.com.otanima.models.AnimeEpisodes
import animes.com.otanima.models.Home
import animes.com.otanima.singletons.AppController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_anime.*
import kotlin.math.hypot

class AnimeActivity : AppCompatActivity() {

    private var mAnime: Anime? = null
    private var mHome: Home? = null
    private val mGson = Gson()
    private val mAdapter = EpisodesAdapter()
    private var isExist = false
    private var mAnimeEpisodes: AnimeEpisodes? = null
    private val mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()
    }

    private fun initView() {

        sinopse.movementMethod = ScrollingMovementMethod()
        sinopse.setOnTouchListener { view, motionEvent ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener false
        }

        val obj = intent.extras["data"]

        if (obj is Anime) {
            mAnime = intent.extras["data"] as Anime?
            initRequest()
        } else {
            mAnimeEpisodes = intent.extras["data"] as AnimeEpisodes?
            mAnime = mAnimeEpisodes?.anime

            mHandler.postDelayed({
                mAdapter.setData(mAnimeEpisodes!!.episodes)
                recyclerview.visibility = RecyclerView.VISIBLE
                progress_circular.visibility = ProgressBar.GONE
                fab.startAnimation(AnimationUtils.loadAnimation(this@AnimeActivity, R.anim.anim_in))
                fab.visibility = FloatingActionButton.VISIBLE
                container_anime.visibility = FrameLayout.VISIBLE

                loadImage()
                sinopse.text = mAnime?.sinopse
            }, 1000)
        }

        isExistsAnime()
        title = mAnime?.name

        fab.setOnClickListener {
            val db = AppDataBase.getDataBase(this)
            val dao = db.getDao()
            if (mAnime != null && !isExist) {
                val episodes = mAdapter.getData()
                dao.insert(mAnime!!)
                episodes.forEach { it.animeId = mAnime!!.id }
                dao.insert(episodes)
                fab.setImageResource(R.drawable.ic_star_white_24dp)
                db.close()
                isExist = true
                return@setOnClickListener
            }

            if (mAnime != null && isExist) {
                val animeEpisodes = dao.getAnimeById(mAnime!!.id)
                dao.deleteAnime(animeEpisodes?.anime)
                fab.setImageResource(R.drawable.ic_star_border_white_24dp)
                db.close()
                isExist = false
                return@setOnClickListener
            }
        }

        load.setOnClickListener {
            initRequest()
        }

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
                mHome = mGson.fromJson<Home>(it, Home::class.java)
                mAdapter.setData(mHome!!.episodes)
                mAnime?.sinopse = mHome?.sinopse
                recyclerview.visibility = RecyclerView.VISIBLE
                progress_circular.visibility = ProgressBar.GONE
                fab.startAnimation(AnimationUtils.loadAnimation(this@AnimeActivity, R.anim.anim_in))
                fab.visibility = FloatingActionButton.VISIBLE
                loadImage()
                sinopse.text = mHome?.sinopse
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

    private fun loadImage() {
        Glide.with(this)
            .asBitmap()
            .load(mAnime?.img)
            .into(object : BitmapImageViewTarget(content_img) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    createPaletteFromImageView()
                }
            })

        container_anime.visibility = FrameLayout.VISIBLE
        circularAnim()
    }

    private fun createPaletteFromImageView() {
        val bitmap = (content_img.drawable as BitmapDrawable?)?.bitmap
        if (bitmap != null) {
            Palette.from(bitmap)
                .generate {
                    val changeBounds = ChangeBounds()
                    changeBounds.duration = 700
                    changeBounds.interpolator = AccelerateDecelerateInterpolator()
                    with(toolbar) {
                        setBackgroundColor(
                            it?.vibrantSwatch?.rgb ?: ContextCompat.getColor(
                                this@AnimeActivity,
                                R.color.colorPrimary
                            )
                        )
                        setTitleTextColor(
                            it?.vibrantSwatch?.titleTextColor ?: ContextCompat.getColor(
                                context,
                                android.R.color.white
                            )
                        )
                    }

                    with(appbar) {
                        setBackgroundColor(
                            it?.vibrantSwatch?.rgb ?: ContextCompat.getColor(
                                this@AnimeActivity,
                                R.color.colorPrimary
                            )
                        )
                    }

                    window.statusBarColor = it?.vibrantSwatch?.rgb ?: ContextCompat.getColor(
                        this@AnimeActivity,
                        R.color.colorPrimary
                    )

                    layout_master.setBackgroundColor(
                        it?.vibrantSwatch?.rgb ?: ContextCompat.getColor(
                            this@AnimeActivity,
                            R.color.colorPrimary
                        )
                    )

                    with(collapsebar) {
                        contentScrim = ColorDrawable(
                            it?.vibrantSwatch?.rgb ?: ContextCompat.getColor(
                                this@AnimeActivity,
                                R.color.colorPrimary
                            )
                        )
                    }

                    with(sinopse) {
                        setTextColor(
                            it?.vibrantSwatch?.titleTextColor ?: ContextCompat.getColor(
                                context,
                                R.color.colorPrimaryDark
                            )
                        )
                    }

                    val backIcon = getDrawable(R.drawable.ic_arrow_back_white_24dp)
                    backIcon?.setColorFilter(
                        it?.vibrantSwatch?.titleTextColor ?: ContextCompat.getColor(
                            this@AnimeActivity,
                            R.color.colorPrimaryDark
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    supportActionBar?.setHomeAsUpIndicator(backIcon)

                    mAdapter.color = it?.vibrantSwatch?.titleTextColor ?: ContextCompat.getColor(
                        this@AnimeActivity,
                        android.R.color.white
                    )

                    mAdapter.notifyDataSetChanged()

//                    TransitionManager.beginDelayedTransition(container, changeBounds)
                }
        }
    }

    private fun isExistsAnime() {
        if (mAnime != null) {
            val db = AppDataBase.getDataBase(this)
            val dao = db.getDao()
            val anime = dao.getAnimeById(mAnime!!.id)
            db.close()

            mAnimeEpisodes = anime

            if (anime != null) {
                isExist = true
                fab.setImageResource(R.drawable.ic_star_white_24dp)
            }

        }
    }

    private fun circularAnim() {
        val x = layout_master.right
        val y = layout_master.bottom
        val startRadius = 0
        val endRadius = hypot(container.width.toDouble(), container.height.toDouble())
        val animator = ViewAnimationUtils.createCircularReveal(
            layout_master, x, y,
            startRadius.toFloat(), endRadius.toFloat()
        )
        layout_master.visibility = CoordinatorLayout.VISIBLE
        animator.duration = 700
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
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
