package com.cookandroid.airquality

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cookandroid.airquality.package_airquality.FragmentAirQuality
import com.cookandroid.airquality.package_firebase.FragmentStorage
import com.cookandroid.airquality.package_weather.FragmentWeatherForecast

import com.cookandroid.airquality.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /// 뷰 페이저 어댑터
    class FragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        val fragments: List<Fragment>
        init {
            fragments = listOf(FragmentAirQuality(), FragmentWeatherForecast(), FragmentStorage())
        }
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /// 뷰 페이저 어댑터 적용w
        val adapter = FragmentPagerAdapter(this)
        binding.viewpager.adapter = adapter

        /// Fragment_AirQuality()가 첫 번째(메인) 화면이 되도록
        binding.viewpager.setCurrentItem(0, true)

    }
}