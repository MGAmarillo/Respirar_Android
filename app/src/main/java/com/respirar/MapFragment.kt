package com.respirar

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.respirar.model.Station
import com.respirar.service.StationService
import com.respirar.service.StationServiceApiBuilder
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment() {

    private val mapPoints: MutableList<GeoPoint> = mutableListOf()
    private lateinit var stationService: StationService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val ctx = getActivity()?.getApplicationContext();

        Configuration.getInstance().load(ctx,PreferenceManager.getDefaultSharedPreferences(ctx))

        val map = view.findViewById<MapView>(R.id.map)

        map.setTileSource(TileSourceFactory.MAPNIK)

        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(4)
        stationService = StationServiceApiBuilder.create()
        getStations()

        val startPoint = GeoPoint(-34.0000000, -64.0000000)
        addMapPoint(map, startPoint)
        mapController.setCenter(startPoint)

        return view
    }

    private fun getStations() {
        stationService.getStations().enqueue(object :
            Callback<List<Station>> {
            override fun onResponse(call: Call<List<Station>>, response: Response<List<Station>>) {
                if (response.isSuccessful) {
                    val stations = response.body()
                    Log.d("stations",stations.toString())
                    // iterar la respuesta e ir creando los markers
                }
            }

            override fun onFailure(call: Call<List<Station>>, t: Throwable) {
                Log.e("Example", t.stackTraceToString())
            }
        })
    }

    private fun addMapPoint(map: MapView, geoPoint: GeoPoint) {
        mapPoints.add(geoPoint)
        val marker = Marker(map)
        marker.position = geoPoint
        map.overlays.add(marker)
        map.invalidate()
    }


}