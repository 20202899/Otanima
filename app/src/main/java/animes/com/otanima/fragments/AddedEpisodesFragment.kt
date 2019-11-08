package animes.com.otanima.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import animes.com.otanima.R
import animes.com.otanima.activities.MainActivity
import animes.com.otanima.adapters.LastAddedAdapter
import animes.com.otanima.interfaces.EndlessRecyclerViewScrollListener
import animes.com.otanima.models.Home
import animes.com.otanima.models.Url
import animes.com.otanima.observables.HomeObservable
import animes.com.otanima.singletons.AppController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_search.*
import kotlinx.android.synthetic.main.fragment_lastadded.*
import java.util.*

class AddedEpisodesFragment : Fragment(), Observer {

    private var mHomeObservable: HomeObservable? = null

    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is HomeObservable) {
            mHomeObservable = p0
            val home = p0.getValue()
            mAdapter.setData(home!!.lastAddedEpisodes)
        }
    }

    val mAdapter = LastAddedAdapter()
    val mGson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lastadded, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val mainActivity = activity as? MainActivity

        mainActivity?.fab_top?.setOnClickListener {
            recyclerview.scrollToPosition(0)
            mainActivity.appbar?.setExpanded(true)
            mainActivity.fab_top?.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.anim_out
                )
            )
            mainActivity.fab_top?.visibility = View.INVISIBLE
        }

        recyclerview.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) {
                        2
                    } else if (position == 1) {
                        return 2
                    }else{

                        if (position % 3 == 1)
                            2
                        else
                            1
                    }

                }

            }
        }
        recyclerview.layoutManager = layoutManager

        recyclerview.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (mHomeObservable != null) {
                    getModeEpisodies(mHomeObservable!!.getValue()!!.nextAddedEpisodes)
                }
            }

            override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(view, dx, dy)

                if (mainActivity?.fab_top?.visibility == View.INVISIBLE && dy > 0) {
                    mainActivity.fab_top?.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.anim_in
                        )
                    )
                    mainActivity.fab_top?.visibility = View.VISIBLE
                } else if (mainActivity?.fab_top?.visibility == View.VISIBLE && dy < 0) {
                    mainActivity.fab_top?.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.anim_out
                        )
                    )
                    mainActivity.fab_top?.visibility = View.INVISIBLE
                }
            }
        })

        recyclerview.adapter = mAdapter
    }

    private fun getModeEpisodies(url: String?) {
        val stringRequest =
            object : StringRequest(Method.POST, "https://app-otanima.herokuapp.com/home-anime", {
                val home = mGson.fromJson<Home>(it, Home::class.java)

                with(activity) {
                    if (this is MainActivity) {
                        mHomeObservable.setValue(home)
                    }
                }

            }, {
                Log.d("t", "t")
            }) {

                override fun getBodyContentType(): String {
                    return "application/json"
                }

                override fun getBody(): ByteArray {
                    val ob = Url(url!!)
                    return mGson.toJson(ob).toByteArray(Charsets.UTF_8)
                }
            }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        AppController.sInstance?.addRequest(stringRequest)
    }

    fun setListEnabled(b: Boolean) {
        recyclerview.isEnabled = b
        ViewCompat.setNestedScrollingEnabled(recyclerview, b)
    }

    companion object {
        fun newInstance() = AddedEpisodesFragment()
    }
}