package com.respirar

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.respirar.model.StationHistory
import com.respirar.service.StationService
import com.respirar.service.StationServiceApiBuilder
import org.osmdroid.config.Configuration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class HistoricFragment : Fragment() {

    private lateinit var graphView: GraphView
    private lateinit var stationService: StationService
    lateinit var btnGoBack: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historic, container, false)

        val ctx = getActivity()?.getApplicationContext()

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        btnGoBack = view.findViewById(R.id.volver)


        stationService = StationServiceApiBuilder.create()
        getStationHistory("station-1", "2023-06-16", "2023-06-20", "SO2")
        return view
    }

    override fun onStart() {
        super.onStart()

        requireActivity().title = "Prueba"

        btnGoBack.setOnClickListener {

            val goBack = HistoricFragmentDirections.actionHistoricFragmentToMapFragment2()
            view?.findNavController()?.navigate(goBack)

        }
    }

    // OBTENER HISTORIA
    private fun getStationHistory(stationId: String, fromDate: String, toDate: String, parameter: String) {
        stationService.getStationHistory(stationId, fromDate, toDate, parameter).enqueue(object :
            Callback<StationHistory> {
            override fun onResponse(call: Call<StationHistory>, response: Response<StationHistory>) {
                if (response.isSuccessful) {
                    val history = response.body()
                    //armar gráfico con la data
                    Log.d("stationsHistory",history.toString())
                    val datapointList = ArrayList<DataPoint>()

                    history?.values?.forEach() {value ->
                        Log.d("value",value.toString())
                        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        val text = value.date
                        val date = formatter.parse(text)
                        datapointList.add(DataPoint(date, value.value.toDouble()))
                    }

                    graphView = view?.findViewById(R.id.idGraphView)!!
                    val series = LineGraphSeries(
                        datapointList.toTypedArray()
                    )

                    graphView.title = "My Graph View";


                    // on below line we are setting
                    // our title text size.
                    graphView.titleTextSize = 18F;

                    // on below line we are adding
                    // data series to our graph view.
                    graphView.addSeries(series);
                }

            }

            override fun onFailure(call: Call<StationHistory>, t: Throwable) {
                Log.e("Example", t.stackTraceToString())
            }
        })
    }
}