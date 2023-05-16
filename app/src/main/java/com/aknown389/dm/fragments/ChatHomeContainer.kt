package com.aknown389.dm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.aknown389.dm.R
import com.aknown389.dm.databinding.FragmentChatHomeContainerBinding
import com.aknown389.dm.pageView.indexViewPager.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
class ChatHomeContainer : Fragment() {
    private var binding: FragmentChatHomeContainerBinding?=null
    private lateinit var chatHome: ChatHomeNav
    private lateinit var chatCall: ChatCallsNav
    private lateinit var chatpeople: ChatPeopleNav
    private lateinit var chatStories: ChatStoriesNav
    private lateinit var bottomNavView:BottomNavigationView
    private var fragmentList = ArrayList<Fragment>()
    private lateinit var viewPager:ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chatHome = ChatHomeNav()
        chatCall = ChatCallsNav()
        chatpeople = ChatPeopleNav()
        chatStories = ChatStoriesNav()
        this.binding = FragmentChatHomeContainerBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValue()
        setListener()
    }

    private fun setListener() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        bottomNavView.selectedItemId = R.id.chat_home_nav
                    }
                    1 -> {
                        bottomNavView.selectedItemId = R.id.chat_calls_nav
                    }
                    2 -> {
                        bottomNavView.selectedItemId = R.id.chat_people_nav
                    }
                    3 -> {
                        bottomNavView.selectedItemId = R.id.chat_stories_nav
                    }
                }
                super.onPageSelected(position)
            }
        })
        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.chat_home_nav -> loadChatHome()
                R.id.chat_calls_nav -> loadChatCalls()
                R.id.chat_people_nav -> loadChatPeople()
                R.id.chat_stories_nav -> loadChatStories()
                else -> loadChatHome()
            }
        }
    }

    private fun setValue() {
        bottomNavView = binding?.ChatNavigationView!!
        this.viewPager = binding?.ViewPager!!
        this.fragmentList.add(chatHome)
        this.fragmentList.add(chatCall)
        this.fragmentList.add(chatpeople)
        this.fragmentList.add(chatStories)
        if (viewPager.adapter == null){
            val adapterviewpager = ViewPagerAdapter(fragmentList, (context as AppCompatActivity).supportFragmentManager, lifecycle)
            viewPager.adapter = adapterviewpager
        }
    }

    private fun loadChatStories(): Boolean {
        viewPager.currentItem = 3
        return true
    }

    private fun loadChatPeople(): Boolean {
        viewPager.currentItem = 2
        return true
    }

    private fun loadChatCalls(): Boolean {
        viewPager.currentItem = 1
        return true
    }

    private fun loadChatHome(): Boolean {
        viewPager.currentItem = 0
        return true
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}