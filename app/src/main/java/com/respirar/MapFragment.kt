package com.respirar

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.respirar.model.LoginCredentials
import com.respirar.model.LoginResponse
import com.respirar.model.Station
import com.respirar.model.StationHistory
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
import java.text.SimpleDateFormat
import java.time.LocalDate


class MapFragment : Fragment() {

    private val mapPoints: MutableList<GeoPoint> = mutableListOf()
    private val allStations : MutableList<Station> = mutableListOf()
    private lateinit var stationService: StationService
    private lateinit var currentStationId : String
    lateinit var btnGoToHistoric: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val ctx = getActivity()?.getApplicationContext();

        Configuration.getInstance().load(ctx,PreferenceManager.getDefaultSharedPreferences(ctx))

        btnGoToHistoric = view.findViewById(R.id.datosHistoricos)

        val map = view.findViewById<MapView>(R.id.map)

        map.setTileSource(TileSourceFactory.MAPNIK)

        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(4)
        stationService = StationServiceApiBuilder.create()

        getStations(map, view)

        //hardocdeado para probar
        // login("user@respirar.com", "user1234")

        val startPoint = GeoPoint(-34.0000000, -64.0000000)

        map.setClickable(true)
        map.setMultiTouchControls(true)
        mapController.setCenter(startPoint)
        return view
    }

    override fun onStart() {
        super.onStart()

        requireActivity().title = "Prueba"

        btnGoToHistoric.setOnClickListener {

            val actionToHistoric = MapFragmentDirections.actionMapFragmentToHistoricFragment3(currentStationId)
            view?.findNavController()?.navigate(actionToHistoric)

        }
    }

    private fun getStations(map:MapView, view: View) {
        stationService.getStations().enqueue(object :
            Callback<List<Station>> {
            override fun onResponse(call: Call<List<Station>>, response: Response<List<Station>>) {
                if (response.isSuccessful) {
                    val stations = response.body()
                    Log.d("stations",stations.toString())
                    // iterar la respuesta e ir creando los markers
                    if (stations != null) {
                        stations.forEach(){station ->
                                allStations.add(station)
                            }
                            // Llamar a una función para agregar los marcadores al mapa
                            addMarkersToMap(map,view)

                    }
                    }

                }

            override fun onFailure(call: Call<List<Station>>, t: Throwable) {
                Log.e("Example", t.stackTraceToString())
            }
        })
    }

    private fun addMarkersToMap(map: MapView, view: View) {
        allStations.forEach { station ->
            val coordinates = station.coordinates
            val geoPoint = GeoPoint(coordinates.latitude, coordinates.longitude)
            val marker = Marker(map)
            marker.position = geoPoint
            addMapPoint(map, geoPoint)
            marker.setInfoWindow(null)
            map.overlays.add(marker)
            marker.setOnMarkerClickListener { marker, mapView ->
                // Obtener los datos de la estación asociada al marcador
                val station = getStationFromMarker(marker)
                if (station != null) {
                    showDialogWithStationData(map,marker,view,station)
                }
                true // Devolver `true` para indicar que el evento de toque ha sido gestionado
            }
        }
        map.invalidate()

    }

    private fun getStationFromMarker(marker: Marker): Station? {
        // Iterar sobre `allStations` y buscar la estación que coincide con el marcador
        return allStations.find { station ->
            station.coordinates.latitude == marker.position.latitude &&
                    station.coordinates.longitude == marker.position.longitude
        }
    }

    private fun showDialogWithStationData(map:MapView, marker:Marker, view:View, station: Station) {
       currentStationId = station.id
       val mapController = map.controller
       mapController.setCenter(marker.position)
       mapController.setZoom(15)
       val name: TextView = view.findViewById(R.id.nombreEstación)
       val temperatura: TextView = view.findViewById(R.id.estadoTemperatura)
       val fiabilidad: TextView = view.findViewById(R.id.estadoFiabilidad)
       val pm1: TextView = view.findViewById(R.id.estadoPM1)
       val pm10: TextView = view.findViewById(R.id.estadoPM10)
       val pm25: TextView = view.findViewById(R.id.estadoPM25)
       name.text = station.name.toString()
       temperatura.text = station.temperature.toString()+("°")
       fiabilidad.text = station.reliability.toString()
       pm1.text = station.pm1.toString()
       pm10.text = station.pm10.toString()
       pm25.text = station.pm25.toString()
       val drawerLayout: DrawerLayout = view.findViewById(R.id.drawerLayoutMapFragment)
       drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun addMapPoint(map: MapView, geoPoint: GeoPoint) {
        mapPoints.add(geoPoint)
        val marker = Marker(map)
        marker.position = geoPoint
        map.overlays.add(marker)
        map.invalidate()
    }



//    private fun login(userId: String, password: String) {
//        val loginCredentials = LoginCredentials(userId, password)
//        stationService.login(loginCredentials).enqueue(object :
//            Callback<LoginResponse> {
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                if (response.isSuccessful) {
//                    val stations = response.body()
//                    //guardar el token de alguna forma
//                    Log.d("stationsHistory",stations.toString())
//                }
//
//            }
//
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                Log.e("Example", t.stackTraceToString())
//            }
//        })
//    }
}