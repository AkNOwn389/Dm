package com.aknown389.dm.pageView.indexViewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(private val listFragment: ArrayList<Fragment>,
                       fm: FragmentManager,
                       lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return listFragment.size
    }

    override fun containsItem(itemId: Long): Boolean {
        return super.containsItem(itemId)
    }


    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }

}