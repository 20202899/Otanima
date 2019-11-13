package animes.com.otanima.views

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class CustomGridLayoutManager : GridLayoutManager {

    private var isScrollEnabled = true

    constructor(context: Context?, spanCount: Int, orientation: Int, reverse: Boolean)
            : super(context, spanCount, orientation, reverse)

    fun setIsScrollEnabled(enabled: Boolean) {
        isScrollEnabled = enabled
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled
    }
}