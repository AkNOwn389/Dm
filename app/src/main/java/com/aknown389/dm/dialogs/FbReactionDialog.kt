package com.aknown389.dm.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.databinding.DialogFbReactionsBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FbReactionDialog:DialogFragment(
) {
    private lateinit var binding: DialogFbReactionsBinding
    private val reactionIcons = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFbReactionsBinding.inflate(layoutInflater, container, false)

        // Add reaction icons to the layout
        val reactions = listOf(
            R.drawable.ic_like,
            R.drawable.ic_heart,
            R.drawable.ic_happy,
            R.drawable.ic_surprise,
            R.drawable.ic_sad,
            R.drawable.ic_angry
        )
        val iconContainer = binding.linearLayout

        for (reaction in reactions) {
            val icon = ImageView(requireContext()).apply {
                setImageResource(reaction)
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    // Set the result when an icon is clicked
                    setFragmentResult("reaction", Bundle().apply {
                        putInt("icon", reaction)
                    })
                    dismiss()
                }
            }
            reactionIcons.add(icon)
            iconContainer.addView(icon)
        }



        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Remove the title and background of the dialog
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Animate the reaction icons when the dialog is shown
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        for (icon in reactionIcons) {
           icon.startAnimation(animation)
        }
        //animate()

    }
    private fun animate(){
        lifecycleScope.launch {
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            binding.like.apply {
                startAnimation(animation)
                setOnClickListener {
                    setFragmentResult("reaction", Bundle().apply {
                        putString("icon", "like")
                    })
                    dismiss()
                }
            }
            binding.like.startAnimation(animation)
            delay(300)
            binding.like.isVisible = true
            binding.love.apply {
                startAnimation(animation)
                setOnClickListener {
                    setFragmentResult("reaction", Bundle().apply {
                        putString("icon", "love")
                    })
                    dismiss()
                }
            }
            binding.love.startAnimation(animation)
            delay(300)
            binding.love.isVisible = true
            binding.happy.apply {
                startAnimation(animation)
                setOnClickListener {
                    setFragmentResult("reaction", Bundle().apply {
                        putString("icon", "love")
                    })
                    dismiss()
                }
            }
            binding.happy.startAnimation(animation)
            delay(300)
            binding.happy.isVisible = true
            binding.wow.apply {
                setOnClickListener {
                    setFragmentResult("reaction", Bundle().apply {
                        putString("icon", "love")
                    })
                    dismiss()
                }
            }
            binding.wow.startAnimation(animation)
            delay(100)
            binding.wow.isVisible = true
            binding.sad.apply {
                startAnimation(animation)
                setOnClickListener {
                    setFragmentResult("reaction", Bundle().apply {
                        putString("icon", "love")
                    })
                    dismiss()
                }
            }
            binding.sad.startAnimation(animation)
            delay(300)
            binding.sad.isVisible = true
            binding.angry.apply {
                startAnimation(animation)
                setOnClickListener {
                    setFragmentResult("reaction", Bundle().apply {
                        putString("icon", "love")
                    })
                    dismiss()
                }
            }
            binding.angry.startAnimation(animation)
            delay(300)
            binding.angry.isVisible = true
        }
    }
}