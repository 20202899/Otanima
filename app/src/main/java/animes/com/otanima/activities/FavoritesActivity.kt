package animes.com.otanima.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import animes.com.otanima.R
import animes.com.otanima.adapters.FavoritesAdapter
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoritesActivity : AppCompatActivity() {

    private val mAdater = FavoritesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    private fun initViews() {
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = GridLayoutManager(this,
            2, LinearLayoutManager.VERTICAL, false)
        recyclerview.adapter = mAdater
    }

    override fun onResume() {
        super.onResume()

        mAdater.load(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}