package animes.com.otanima.models

class Home(var today: MutableList<Anime>, var lastAddedEpisodes: MutableList<Episode>,
           var nextAddedEpisodes: String?, var videos: MutableList<Link>, var streams: MutableList<StreamVideo>)