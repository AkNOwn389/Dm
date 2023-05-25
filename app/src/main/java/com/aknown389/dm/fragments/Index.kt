package com.aknown389.dm.fragments

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aknown389.dm.R
import com.aknown389.dm.activities.ProfilePageActivity
import com.aknown389.dm.databinding.ActivityIndexBinding
import com.aknown389.dm.pageView.indexViewPager.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class Index : Fragment() {
    private var binding: ActivityIndexBinding? = null
    private var fragmentlist = ArrayList<Fragment>()
    private lateinit var bottomNavView :BottomNavigationView
    private lateinit var viewPager: ViewPager2
    private lateinit var context:Context


    override fun onDestroyView() {

        val viewPager2 = binding?.flFragment

        viewPager2?.let {
            it.adapter = null
        }
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        when(viewPager.currentItem){
            0 -> {
                bottomNavView.selectedItemId = R.id.homefeed
            }
            1 -> {
                bottomNavView.selectedItemId = R.id.showfriend
            }
            2 -> {
                bottomNavView.selectedItemId = R.id.watchsvideo
            }
            3 -> {
                bottomNavView.selectedItemId = R.id.news
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.flFragment)
        bottomNavView = view.findViewById(R.id.bottomNavigationView)
        fragmentlist.add(Homefeed())
        fragmentlist.add(SuggestedUser())
        fragmentlist.add(WatchVideo())
        fragmentlist.add(News())

        if (viewPager.adapter == null){
            val adapterviewpager = ViewPagerAdapter(fragmentlist, (context as AppCompatActivity).supportFragmentManager, lifecycle)
            viewPager.adapter = adapterviewpager
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        bottomNavView.selectedItemId = R.id.homefeed
                    }
                    1 -> {
                        bottomNavView.selectedItemId = R.id.showfriend
                    }
                    2 -> {
                        bottomNavView.selectedItemId = R.id.watchsvideo
                    }
                    3 -> {
                        bottomNavView.selectedItemId = R.id.news
                    }
                    else -> {
                        bottomNavView.selectedItemId = R.id.homefeed
                    }
                }
                super.onPageSelected(position)
            }
        })
        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homefeed -> loadHomefeed()
                R.id.showfriend -> loadShowFriend()
                R.id.watchsvideo -> loadWatchVideo()
                R.id.news -> loadNews()
                R.id.profilePage -> loadProfilePage()
                else -> loadHomefeed()
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            this.context = container.context
        }
        if (binding == null){
            binding = ActivityIndexBinding.inflate(layoutInflater, container, false)
        }
        return binding?.root
    }

    private fun loadProfilePage(): Boolean {
        Intent(activity, ProfilePageActivity::class.java).also {
            startActivity(it)
        }
        return true
    }
    private fun loadNews(): Boolean {
        viewPager.currentItem = 3
        return true
    }

    private fun loadWatchVideo(): Boolean {
        viewPager.currentItem = 2
        return true
    }

    private fun loadShowFriend(): Boolean {
        viewPager.currentItem = 1
        return true
    }

    private fun loadHomefeed(): Boolean {
        viewPager.currentItem = 0
        return true
    }
}