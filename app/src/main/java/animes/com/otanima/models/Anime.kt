package animes.com.otanima.models

import java.io.Serializable

class Anime (var name: String, var url: String, var img: String) : Serializable

class Episode (var name: String, var url: String, var data: String, var img: String) : Serializable

class Link (var url: String) : Serializable