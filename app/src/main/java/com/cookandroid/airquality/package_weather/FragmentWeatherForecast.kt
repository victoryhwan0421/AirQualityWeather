package com.cookandroid.airquality.package_weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.cookandroid.airquality.data.Repository
import com.cookandroid.airquality.data.models.ModelWeather.ITEM
import com.cookandroid.airquality.data.models.ModelWeather.ModelWeather
import com.cookandroid.airquality.data.models.ModelWeather.WEATHER
import com.cookandroid.airquality.databinding.FragmentWeatherForecastBinding
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class FragmentWeatherForecast : Fragment() {
    /// 선언부
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null
    private val binding_w by lazy { FragmentWeatherForecastBinding.inflate(layoutInflater) }
    private val scope = MainScope()

    private var baseDate = "20210510" // 발표 일자
    private var baseTime = "1400"     // 발표 시각
    private var curPoint: Point? = null  // 현재 위치의 격자 좌표를 저장할 포인트


    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef : DatabaseReference = database.getReference("WeatherInfo")




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Weather", "Oncreate()")

        bindViews()
        initVariables()
        requestLocationPermissions()
        onStartTextView()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
        scope.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_weather_forecast, container, false)
        Log.v("Weather", "onCreateView()")
        return binding_w.root
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val locationPermissionGranted =
            requestCode == REQUEST_ACCESS_LOCATION_PERMISSIONS &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        val backgroundLocationPermissionGranted =
            requestCode == REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // background 권한이 없을 경우 요청
            if (!backgroundLocationPermissionGranted) {
                requestBackgroundLocationPermissions()
            } else {
                fetchWeatherForecastData()
            }

        } else {
            if (!locationPermissionGranted) {
                activity?.finish()
            } else {
                // fetchData
                fetchWeatherForecastData()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onStartTextView(){
        // 오늘 날짜 텍스트뷰 설정
        binding_w.tvDate.text = SimpleDateFormat("MM월 dd일", Locale.getDefault())
            .format(
                Calendar.getInstance()
                    .time
            ) + "날씨"
    }

    // 스와프하여 새로고침
    private fun bindViews() {
        binding_w.refreshWeather.setOnRefreshListener {
            Log.v("Weather", "refresh()")
            fetchWeatherForecastData()
        }
    }

    private fun initVariables() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(getActivity())
        // this -> getActivity()
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocationPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS
        )
    }

    fun WriteWeatherModel(rainType: String, humidity: String, sky: String, temp: String, fcstTime: String, count: Int){
        val weathermodel = ModelWeather(rainType, humidity, sky, temp, fcstTime)
        when(count < 6){
            count == 0 -> myRef.child("WeatherInfo").child("WeatherModel0").setValue(weathermodel)
            count == 1 -> myRef.child("WeatherInfo").child("WeatherModel1").setValue(weathermodel)
            count == 2 -> myRef.child("WeatherInfo").child("WeatherModel2").setValue(weathermodel)
            count == 3 -> myRef.child("WeatherInfo").child("WeatherModel3").setValue(weathermodel)
            count == 4 -> myRef.child("WeatherInfo").child("WeatherModel4").setValue(weathermodel)
            count == 5 -> myRef.child("WeatherInfo").child("WeatherModel5").setValue(weathermodel)
        }
    }

    /// 날씨 가져와서 설정하기
    private fun setWeather(nx: Int, ny: Int) {
        Log.v("Weather", "setWeather()")
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        val cal = Calendar.getInstance()
        // 현재 날짜
        baseDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        // 현재 시각
        val timeH = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time)
        // 현재 분
        val timeM = SimpleDateFormat("mm", Locale.getDefault()).format(cal.time)

        // api 호출
        baseTime = Common().getBaseTime(timeH, timeM)
        Log.v("Weather", "API 호출")

        // 현재 시각이 00시이고, 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if (timeH == "00" && baseTime == "2330") {
            cal.add(Calendar.DATE, -1).toString()
            baseDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }
        // 날씨 정보 가져오기
        // (한 페이지 결과 수 = 60, 페이지 번호 = 1, 응답 자료 형식 = "JSON", 발표 날짜, 발표 시각, 예보지점 좌표
        val call = Repository.getRetrofitService()
            .getWeather(60, 1, "JSON", baseDate, baseTime, nx, ny)
        Log.v("Weather", "Retrofit call")

        // 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    Log.v("Weather", "onResponse")
                    // 날씨 정보 가져오기
                    val it: List<ITEM> = response.body()!!.response.body.items.item

                    // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
                    val weatherArr = arrayOf(ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather())

                    // 배열 채우기
                    var index = 0
                    var count = 0
                    weatherArr[0].fcstTime = "지금" //
                    val totalCount = response.body()!!.response.body.totalCount - 1
                    for (i in 0..totalCount) {
                        index %= 6
                        when (it[i].category) {
                            "PTY" -> weatherArr[index].rainType = it[i].fcstValue // 강수형태
                            "REH" -> weatherArr[index].humidity = it[i].fcstValue // 습도
                            "SKY" -> weatherArr[index].sky = it[i].fcstValue // 하늘 상태
                            "T1H" -> weatherArr[index].temp = it[i].fcstValue // 기온
                            else -> continue
                        }
                        weatherArr[index].fcstTime = it[index].fcstTime //
                        if(count < 6){
                            Log.v("count", count.toString())
                            WriteWeatherModel(weatherArr[index].rainType, weatherArr[index].humidity, weatherArr[index].sky, weatherArr[index].temp, weatherArr[index].fcstTime, count)
                            index++
                            count++
                        }else{
                            count = 0
                            continue
                        }

                    }
//                    weatherArr[0].fcstTime = "지금"
//                    // 각 날짜 배열 시간 설정
//                    for (i in 1..5) {
//                        weatherArr[i].fcstTime = it[i].fcstTime
//                    }

                    // 리사이클러 뷰에 데이터 연결
                    binding_w.weatherRecyclerView.adapter = WeatherAdapter(weatherArr)
                    Log.v("weatherArr", WeatherAdapter(weatherArr).toString())
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                //binding_w.tvError.text = "api fail : " + t.message.toString() + "\n 다시 시도해주세요."
                //binding_w.tvError.visibility = View.VISIBLE
                Log.d("api fail", t.message.toString())
            }
        })
    }




    // 현재 위치의 위경도를 격자 좌표로 변환 후 해당 위치의 날씨정보 설정하기
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun fetchWeatherForecastData() {
        val locationClient = LocationServices.getFusedLocationProviderClient(activity)

        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient
            .getCurrentLocation(LocationRequest.
                PRIORITY_HIGH_ACCURACY, cancellationTokenSource!!.token)
            .addOnSuccessListener { location ->
                scope.launch {
                    binding_w.errorDescriptionTextViewWeather.visibility = View.GONE
                try {
                    // 실행 내용
                        Log.v("test", "여기까지는 실행이 되네요")
                    val monitoringStation =
                        Repository.getNearbyMonitoringStation(location.latitude, location.longitude)

                    val locationRequest = LocationRequest.create() // 현재 위치 요청
                    locationRequest.run {
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        //interval = 60 * 1000
                    }
                    val locationCallback = object : LocationCallback() {
                    @SuppressLint("SetTextI18n")
                    override fun onLocationResult(p0: LocationResult) {
                        Log.v("Weather", "onLocationResult()")
                        p0.let {
                            for (location in it.locations) {

                                // 현재 위치의 위경도를 격자 좌표로 변환
                                curPoint = Common().dfsXyConv(location.latitude, location.longitude)
                                Log.v("Weather", "curPoint: curPoint" + curPoint)

                                binding_w.measuringStationNameTextViewWeather.text =
                                    monitoringStation!!.stationName

                                // 오늘 날씨 텍스트뷰 설정
                                binding_w.tvDate.text =
                                    SimpleDateFormat("MM월 dd일 ", Locale.getDefault())
                                        .format(Calendar.getInstance().time) + "날씨"
                                Log.v("Weather", "MM월 dd일" + binding_w.tvDate.text)
                                // nx, ny 지점의 날씨 가져와서 실정하기
                                setWeather(curPoint!!.x, curPoint!!.y)
                            }
                        }
                    }
                }
                    // 내 위치 실시간으로 감지
                    Looper.myLooper()?.let {
                        locationClient.requestLocationUpdates(locationRequest, locationCallback, it)
                    }
                    displayfetchWeatherData()

                } catch (exception: Exception) {
                    // Error 발생 시, errorDescriptionTextViewWeather 보이기
                        Log.v("WeatherError", "Error")
                    binding_w.errorDescriptionTextViewWeather.visibility = View.VISIBLE

                    // 정상적으로 로딩 후 재시도하여 Exception이 발생하여
                    // errorDescriptionTextViewWeather 의 중복을 방지하기 위해 alpha = 0 으로 설정
                    binding_w.contentsLayoutWeather.alpha = 0F
                } finally {
                    // 모든 처리에 문제 없다면, 로딩창과 새로고침 마무리
                    binding_w.progressBarWeather.visibility = View.GONE
                    binding_w.refreshWeather.isRefreshing = false
                }
            }
        }
    }

    private fun displayfetchWeatherData() {
        Log.v("Weather", "displayfetchWeatherData()")
        binding_w.contentsLayoutWeather.animate()
            // alpha값을 default로 설정되어 있는 시간에 걸쳐 1로 변환(fade-in효과)
            .alpha(1F)
            .start()

        binding_w.weatherRecyclerView.animate()
            .alpha(1F)
            .start()
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
        private const val REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS = 101
    }
}



