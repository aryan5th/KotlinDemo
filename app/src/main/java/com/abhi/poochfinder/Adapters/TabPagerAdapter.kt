package com.abhi.poochfinder.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.abhi.poochfinder.Fragments.DetailsFragment
import com.abhi.poochfinder.Fragments.HomeFragment

/**
 * Simple PagerAdapter class to create new tabs
 *
 */
class TabPagerAdapter(fm: FragmentManager, private val tabCount: Int): FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return tabCount
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return HomeFragment.newInstance()
            1 -> return DetailsFragment.newInstance()
        }
        return HomeFragment()
    }

}