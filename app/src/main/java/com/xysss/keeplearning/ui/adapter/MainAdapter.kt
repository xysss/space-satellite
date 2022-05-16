package com.xysss.keeplearning.ui.adapter

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xysss.keeplearning.ui.fragment.*

/**
 * Author:bysd-2
 * Time:2021/9/2811:10
 */

class MainAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    companion object {
        const val PAGE_ONE = 0
        const val PAGE_TWO = 1
        const val PAGE_THREE = 2
        const val PAGE_FOUR = 3
        const val PAGE_FIVE = 4

    }

    private val fragments: SparseArray<Fragment> = SparseArray()

    init {
        fragments.put(PAGE_ONE, OneFragment())
        fragments.put(PAGE_TWO, TwoFragment())
        fragments.put(PAGE_THREE, ThreeFragment())
        fragments.put(PAGE_FOUR, FourFragment())
        fragments.put(PAGE_FIVE, PlaceFragment())
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }
}