package com.example.cafespot.cafepage

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cafespot.R
import com.example.cafespot.cafepage.fragments.*
import com.example.cafespot.hotpage.HotItem
import com.example.cafespot.hotpage.HotViewModel

class CafeAdapter(val fragmentManager: FragmentManager, lifecycle: Lifecycle, val location: Location) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                return BlankFragmentInfo()
            }
            1 -> {
                return BlankFragment2.newInstance()
            }
            2 -> {
                return BlankFragment.newInstance()
            }
            else -> BlankFragment()
        }
    }
}