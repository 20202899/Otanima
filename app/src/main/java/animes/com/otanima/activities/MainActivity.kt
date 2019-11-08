package animes.com.otanima.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import animes.com.otanima.R
import animes.com.otanima.adapters.SearchAdapter
import animes.com.otanima.adapters.TodayAdapter
import animes.com.otanima.fragments.AddedEpisodesFragment
import animes.com.otanima.fragments.TodayFragment
import animes.com.otanima.models.Anime
import animes.com.otanima.models.Home
import animes.com.otanima.models.Url
import animes.com.otanima.observables.HomeObservable
import animes.com.otanima.singletons.AppController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_search.*
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    val mHomeObservable = HomeObservable()
    lateinit var mSearchView: SearchView
    private val mGson = Gson()
    private val mAdapter = SearchAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initFragments()
        initRequest()
        initViews()
    }

    private fun initViews() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        mBottomSheetBehavior.isHideable = true
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        recyclerview_sheet.setHasFixedSize(true)
        recyclerview_sheet.layoutManager = GridLayoutManager(
            this, 2,
            GridLayoutManager.VERTICAL, false
        ).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0)
                        2
                    else
                        1
                }

            }
        }
        recyclerview_sheet.adapter = mAdapter
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
        val stringRequest = object :
            StringRequest(Request.Method.POST, "https://app-otanima.herokuapp.com/home-anime", {
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


    private fun findFragmentById(id: Int): Fragment? {
        return supportFragmentManager
            .findFragmentById(R.id.lastAdded)
    }

    private fun requestSearch(text: String?) {

        if (text.isNullOrEmpty())
            return

        val url = "https://www.animesonehd.org/?s=$text"
        val stringRequest = StringRequest(Request.Method.GET, url, {
            (findFragmentById(R.id.lastAdded) as? AddedEpisodesFragment)?.setListEnabled(false)
            val doc = Jsoup.parse(it)
            val params = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT)
            val type = object : TypeToken<MutableList<Anime>>() {}.type
            val animesItem = doc.getElementsByClass("AnimesItem")
                .map { i ->
                    val a = i.getElementsByTag("a")
                    val capa = Jsoup.parse(a.toString())
                    val urlImg = capa.getElementsByTag("img")

                    return@map Anime(
                        a.attr("title"),
                        a.attr("href"), urlImg.attr("src")
                    )
                }
            mBottomSheetBehavior.isHideable = true
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            mAdapter.setData(animesItem)
            mBottomSheetBehavior.isHideable = false
            appbar.setExpanded(false)
            ViewCompat.setNestedScrollingEnabled(recyclerview_sheet, false)
            bottom_sheet.layoutParams = params

        }, {

        })

        AppController.sInstance?.addRequest(stringRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mSearchView = menu.findItem(R.id.action_search).actionView as SearchView
        mSearchView.maxWidth = Int.MAX_VALUE
        mSearchView.queryHint = "Pesquisar anime"
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                requestSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        mSearchView.setOnCloseListener {

            if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                (findFragmentById(R.id.lastAdded) as? AddedEpisodesFragment)?.setListEnabled(true)
                val params = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT)
                mBottomSheetBehavior.isHideable = true
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                mBottomSheetBehavior.isHideable = false
                mAdapter.setData(mutableListOf())
                appbar.setExpanded(true)
                ViewCompat.setNestedScrollingEnabled(recyclerview_sheet, true)
                bottom_sheet.layoutParams = params
            }
            return@setOnCloseListener false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }
}
