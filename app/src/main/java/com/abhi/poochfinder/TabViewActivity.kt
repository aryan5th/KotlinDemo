package com.abhi.poochfinder

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.abhi.poochfinder.Adapters.TabPagerAdapter
import com.abhi.poochfinder.Fragments.DetailsFragment
import kotlinx.android.synthetic.main.activity_tab_view.*


class TabViewActivity: AppCompatActivity(), DetailsFragment.OnFragmentInteractionListener {
    var TAG = TabViewActivity::class.java.simpleName as String

    var adapter: TabPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_view)

        configureTabLayout()
    }

    private fun configureTabLayout() {

        tabs.addTab(tabs.newTab().setText(getString(R.string.home_tab_title)))
        tabs.addTab(tabs.newTab().setText(getString(R.string.my_dog_tab_title)))

        adapter = TabPagerAdapter(supportFragmentManager,
                tabs.tabCount)
        pager.adapter = adapter

        pager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(tabs))

        tabs.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
                Log.e(TAG,"Selected tab is : " + tab.position)

                when(tab.position) {
                    1 -> {
                        Log.e(TAG,"Tab 1 selected")
                        var detailsPage = adapter!!.getItem(tab.position) as DetailsFragment
                        detailsPage.fetchDataFromServer(tabs.context)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.e(TAG,"onFragmentInteraction for uri : " + uri)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        Log.e(TAG, "onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
    }
}