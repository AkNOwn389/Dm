package com.aknown389.dm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aknown389.dm.databinding.FragmentChatStoriesNavBinding

class ChatStoriesNav : Fragment() {
    private var binding: FragmentChatStoriesNavBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.binding = FragmentChatStoriesNavBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}