package com.terraable.app.data.tomtom

import com.terraable.app.BuildConfig
import com.terraable.app.core.model.Coordinates
import com.terraable.app.core.model.LocationSuggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

interface RouteRepository {
    suspend fun searchLocation(query: String): List<LocationSuggestion>
    suspend fun getCoordinates(placeName: String): Coordinates
}

class TomTomRouteRepository(
    private val apiKey: String = BuildConfig.TOMTOM_API_KEY
) : RouteRepository {

    private val knownCities = listOf(
        LocationSuggestion("1", "Mumbai", "Mumbai, Maharashtra, India", Coordinates(18.9690, 72.8205)),
        LocationSuggestion("2", "Goa (Madgaon)", "Madgaon, Goa, India", Coordinates(15.2736, 73.9582)),
        LocationSuggestion("3", "Goa (Panaji)", "Panaji, Goa, India", Coordinates(15.4909, 73.8278)),
        LocationSuggestion("4", "Pune", "Pune, Maharashtra, India", Coordinates(18.5204, 73.8567)),
        LocationSuggestion("5", "Bangalore", "Bengaluru, Karnataka, India", Coordinates(12.9716, 77.5946)),
        LocationSuggestion("6", "Mysore", "Mysuru, Karnataka, India", Coordinates(12.2958, 76.6394)),
        LocationSuggestion("7", "Delhi", "New Delhi, Delhi, India", Coordinates(28.6139, 77.2090)),
        LocationSuggestion("8", "Jaipur", "Jaipur, Rajasthan, India", Coordinates(26.9124, 75.7873))
    )

    override suspend fun searchLocation(query: String): List<LocationSuggestion> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext knownCities.take(4)

        if (apiKey.isNotBlank() && apiKey != "DEMO_MODE") {
            try {
                val encoded = URLEncoder.encode(query, "UTF-8")
                val urlString = "https://api.tomtom.com/search/2/search/$encoded.json?key=$apiKey&countrySet=IN&limit=5"
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 4000
                conn.readTimeout = 4000

                if (conn.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val body = reader.readText()
                    reader.close()

                    val json = JSONObject(body)
                    val results = json.optJSONArray("results")
                    if (results != null && results.length() > 0) {
                        val list = mutableListOf<LocationSuggestion>()
                        for (i in 0 until results.length()) {
                            val item = results.getJSONObject(i)
                            val id = item.optString("id", "$i")
                            val address = item.optJSONObject("address")
                            val freeform = address?.optString("freeformAddress") ?: query
                            val position = item.getJSONObject("position")
                            val lat = position.getDouble("lat")
                            val lon = position.getDouble("lon")
                            list.add(LocationSuggestion(id, freeform.split(",").firstOrNull()?.trim() ?: query, freeform, Coordinates(lat, lon)))
                        }
                        if (list.isNotEmpty()) return@withContext list
                    }
                }
            } catch (_: Exception) {
                // Fall through to known cities / fallback
            }
        }

        val filtered = knownCities.filter {
            it.name.contains(query, ignoreCase = true) || it.formattedAddress.contains(query, ignoreCase = true)
        }
        if (filtered.isNotEmpty()) filtered else listOf(
            LocationSuggestion("custom", query.trim(), "$query, India", Coordinates(19.0760, 72.8777))
        )
    }

    override suspend fun getCoordinates(placeName: String): Coordinates = withContext(Dispatchers.IO) {
        val search = searchLocation(placeName)
        search.firstOrNull()?.coordinates ?: Coordinates(19.0760, 72.8777)
    }
}
