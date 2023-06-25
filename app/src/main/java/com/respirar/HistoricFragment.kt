package com.respirar

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.icu.util.Calendar
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private const val INPUT_DATE_PATTERN = "yyyy-MM-dd"

class HistoricFragment : Fragment() {

    private lateinit var graphView: GraphView
    private lateinit var stationService: StationService
    lateinit var btnGoBack: Button
    lateinit var btnUpdateHistory: Button
    lateinit var spinner: Spinner
    lateinit var calendar: Calendar
    lateinit var dateFromTextInput: EditText
    lateinit var dateToTextInput: EditText
    lateinit var currentStationId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historic, container, false)

        val ctx = activity?.applicationContext

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        calendar = Calendar.getInstance()

        dateToTextInput = view.findViewById(R.id.DateTo)
        dateFromTextInput = view.findViewById(R.id.DateFrom)

        val dateToDate =
            OnDateSetListener { view, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel(dateToTextInput)
            }

        dateToTextInput.setOnClickListener {
            this@HistoricFragment.context?.let { it1 ->
                DatePickerDialog(
                    it1, dateToDate, calendar.get(
                        Calendar.YEAR
                    ), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        val dateFromDate =
            OnDateSetListener { view, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel(dateFromTextInput)
            }

        dateFromTextInput.setOnClickListener {
            this@HistoricFragment.context?.let { it1 ->
                DatePickerDialog(
                    it1, dateFromDate, calendar.get(
                        Calendar.YEAR
                    ), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }


        btnGoBack = view.findViewById(R.id.idVolver)
        btnUpdateHistory = view.findViewById(R.id.idActualizar)


        spinner = view.findViewById(R.id.idParameterSpinner)
        val parametros = resources.getStringArray(R.array.opciones_parametros)
        if (spinner != null) {
            val adapter = context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item, parametros
                )
            }
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Toast.makeText(this@HistoricFragment.context,
                             parametros[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        stationService = StationServiceApiBuilder.create()

        currentStationId = HistoricFragmentArgs.fromBundle(requireArguments()).stationId

        val formatter = DateTimeFormatter.ofPattern(INPUT_DATE_PATTERN)
        val current = LocalDateTime.now().format(formatter)
        getStationHistory(currentStationId, current, current, "SO2")

        return view
    }

    override fun onStart() {
        super.onStart()


        btnGoBack.setOnClickListener {
            val goBack = HistoricFragmentDirections.actionHistoricFragmentToMapFragment2()
            view?.findNavController()?.navigate(goBack)
        }

        btnUpdateHistory.setOnClickListener {
            graphView.removeAllSeries()
            getStationHistory(currentStationId,  dateFromTextInput.text.toString(), dateToTextInput.text.toString(),  spinner.selectedItem.toString())
        }
    }

    private fun updateLabel(textToUpdate: TextView) {
        val formatter = SimpleDateFormat(INPUT_DATE_PATTERN)
        textToUpdate.text = formatter.format(calendar.time);
    }

    // OBTENER HISTORIA
    private fun getStationHistory(stationId: String, fromDate: String, toDate: String, parameter: String) {
        stationService.getStationHistory(stationId, fromDate, toDate, parameter).enqueue(object :
            Callback<StationHistory> {
            override fun onResponse(call: Call<StationHistory>, response: Response<StationHistory>) {
                if (response.isSuccessful) {
                    val history = response.body()
                    val datapointList = ArrayList<DataPoint>()

                    history?.values?.forEach() {value ->
                        Log.d("value",value.toString())
                        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        val text = value.date
                        val date = formatter.parse(text)
                        datapointList.add(DataPoint(date, value.value.toDouble()))
                    }

                    graphView = view?.findViewById(R.id.idGraphView)!!
                    if(history?.values?.size!! < 3) {
                        graphView.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this@HistoricFragment.activity, SimpleDateFormat("dd-M-yyyy hh:mm"))
                    } else if (fromDate == toDate) {
                        graphView.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this@HistoricFragment.activity, SimpleDateFormat("hh:mm"))
                    } else {
                        graphView.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this@HistoricFragment.activity, SimpleDateFormat("dd-MM-yyyy"))
                    }

                    graphView.titleTextSize = 18F;

                    graphView.addSeries(LineGraphSeries(datapointList.toTypedArray()));
                }

            }

            override fun onFailure(call: Call<StationHistory>, t: Throwable) {
                Log.e("Example", t.stackTraceToString())
            }
        })
    }
}