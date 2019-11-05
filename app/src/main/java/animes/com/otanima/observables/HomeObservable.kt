package animes.com.otanima.observables

import animes.com.otanima.models.Home
import java.util.*

class HomeObservable : Observable() {
    private var value: Home? = null

    fun setValue(home: Home) {

        if (value == null) {
            value = home
            setChanged()
            notifyObservers()
            return
        }

        value!!.today = home.today
        value!!.lastAddedEpisodes.addAll(home.lastAddedEpisodes)
        value!!.nextAddedEpisodes = home.nextAddedEpisodes

        setChanged()
        notifyObservers()
    }

    fun getValue() = value
}