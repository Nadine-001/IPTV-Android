package com.example.tvapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.tvapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class HotelInfo : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentPageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_info)
        supportActionBar?.hide()

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = DateFormat.getDateInstance().format(calendar.time)

        val dateTextView = findViewById<TextView>(R.id.text_date)
        dateTextView.text = dateFormat

        val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val dayTextView = findViewById<TextView>(R.id.text_day)
        val dayOfWeek = dayFormat.format(calendar.time)
        dayTextView.text = dayOfWeek

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager)

        adapter = FragmentPageAdapter(supportFragmentManager, lifecycle, tabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("About"))
//        tabLayout.addTab(tabLayout.newTab().setText("Location"))
        tabLayout.addTab(tabLayout.newTab().setText("Facilities"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })


    }
}