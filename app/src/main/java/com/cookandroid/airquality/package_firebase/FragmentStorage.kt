package com.cookandroid.airquality.package_firebase

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cookandroid.airquality.databinding.FragmentStorageBinding
import com.cookandroid.airquality.databinding.FragmentWeatherForecastBinding
import com.google.firebase.messaging.FirebaseMessaging


class FragmentStorage : Fragment() {

    private val binding by lazy { FragmentStorageBinding.inflate(layoutInflater) }
    private val binding_w by lazy { FragmentWeatherForecastBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()
        updateResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


    private fun initFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    binding.firebaseTokenTextViewS.text = task.result
                }
            }
    }


    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false){
//        binding.resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱 런처") +
//        if(isNewIntent){
//            "(으)로 갱신했습니다."
//        }else{
//            "(으)로 실행했습니다."
//        }
        //binding_w.weatherRecyclerView.adapter = WeatherAdapter(weatherArr)

        binding.resultTextView.text = if(isNewIntent){
            "(으)로 갱신했습니다."
        }else{
            "(으)로 실행했습니다."
        }
    }

    private fun AlarmAlgo(){

        // 날씨가 좋으면 좋다는 알림을 보낸다.
        // 좋다는 판단은 어떻게 할건데?


        // 그렇지 않으면 그렇지 않다는 알림을 보낸다.
    }

}