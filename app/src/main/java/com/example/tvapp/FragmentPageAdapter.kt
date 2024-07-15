package com.example.tvapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout

class FragmentPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val tabLayout: TabLayout
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return tabLayout.tabCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AboutFragment()
//            1 -> LocationFragment()
            1 -> FacilityFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}