package animes.com.otanima.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import animes.com.otanima.R
import animes.com.otanima.adapters.TodayAdapter
import animes.com.otanima.models.Home
import animes.com.otanima.observables.HomeObservable
import animes.com.otanima.singletons.AppController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_today.*
import java.lang.reflect.Method
import java.util.*

class TodayFragment : Fragment(), Observer {

    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is HomeObservable) {
            val home = p0.getValue()
            mAdapter.setData(home!!.today)
        }
    }

    private val mAdapter = TodayAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerview.adapter = mAdapter
    }

    companion object {
        fun newInstance() = TodayFragment()
    }
}