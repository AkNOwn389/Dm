package com.aknown389.dm.reactionTesting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.GridView
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.aknown389.dm.R


/*
* MIT License
*
* Copyright (c) 2018 AmrDeveloper (Amr Hesham)
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
/**
 * ReactButton custom class based on Button View
 *
 * Provide like and unlike feature depend on use single click
 * and reactions dialog feature when user provide long click event
 */
@SuppressLint("AppCompatCustomView")
class ReactImageButton : ImageButton, View.OnClickListener,
    OnLongClickListener{

    val TAG = "ReactImageButton"
    /**
     * ReactButton custom view object to make easy to change attribute
     */
    private val mReactButton: ReactImageButton = this

    /**
     * Reaction Alert Dialog to show Reaction layout with 6 Reactions
     */
    private var mReactAlertDialog: AlertDialog? = null

    /**
     * True only if current reaction is not the default reaction
     */
    private var isReactButtonUpdated = false

    /**
     * Reaction Object to save default Reaction
     */
    private var mDefaultReaction: Reaction? = null

    /**
     * Reaction Object to save the current Reaction
     */
    private var mCurrentReaction: Reaction? = null

    /**
     * List of Reaction to show them in the dialog
     */
    private val mReactions:MutableList<Reaction> = ArrayList()
    private var mDisplayReactions:MutableList<Reaction> = ArrayList()

    /**
     * The number of reactions in the same row,
     * default value will be reactions size
     */
    private var mDialogColumnsNumber = 0

    /**
     * The dim amount for the reactions dialog
     * 0 for no dim to 1 for full dim, default is 0
     */
    private var mDialogDimAmount = 0f

    /**
     * Integer variable to change react dialog shape
     * Default value is react_dialog_shape
     */
    private var mReactDialogShape: Int = R.drawable.react_dialog_shape

    /**
     * The current gravity for the reactions dialog
     */
    private var currentDialogGravity = Gravity.TOP or Gravity.START

    /**
     * Enable/Disable showing the reactions dialog in relative position to the react button
     */
    private var enableDialogDynamicPosition = true

    /**
     * Enable/Disable the reactions tooltip feature
     */
    private var enableReactionTooltip = false

    /**
     * The offset between tooltip and the reaction icon
     */
    private var mTooltipOffsetFromReaction = 100

    /**
     * The default color for React tooltip text
     */
    @ColorInt
    private var mReactTooltipTextColor = Color.WHITE

    /**
     * The default drawable shape for tooltip text
     */
    @DrawableRes
    private var mReactTooltipShape: Int = R.drawable.react_tooltip_shape

    /**
     * Full reaction icon size converted from dp
     */
    val REACTION_ICON_SIZE: Int = ICON_SIZE_WITH_PADDING *
            resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

    /**
     * The maximum width of the screen in pixels
     */
    private val SCREEN_MAX_WIDTH = resources.displayMetrics.widthPixels

    interface OnReactionDialogStateListener {
        fun onDialogOpened()
        fun onDialogDismiss()
    }

    private var mOnReactionDialogStateListener: OnReactionDialogStateListener? = null

    interface OnReactionChangeListener {
        fun onReactionButtonClick(reaction: Reaction?)
    }

    private var mOnReactionChangeListener: OnReactionChangeListener? = null

    interface OnButtonClickListener{
        fun onBackToDefault(reaction: Reaction?)
        fun onFirstReactionClick(reaction: Reaction?)
    }

    constructor(context: Context?) : super(context) {
        setupReactButtonDefaultSettings()
    }
    private var mOnButtonClickListener:OnButtonClickListener? = null
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setupReactButtonDefaultSettings()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupReactButtonDefaultSettings()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setupReactButtonDefaultSettings()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // Handle Screen rotation, should dismiss the dialog
        if (mReactAlertDialog != null && mReactAlertDialog!!.isShowing) mReactAlertDialog!!.cancel()
    }

    /**
     * Setup default settings for ReactButton Constructors
     * - Set ReactButton Listeners
     */
    private fun setupReactButtonDefaultSettings() {
        mReactButton.setOnClickListener(this)
        mReactButton.setOnLongClickListener(this)
    }

    /**
     * Method with 2 state set first React or back to default state
     */
    private fun onReactionButtonClick() {
        val reaction = if (isReactButtonUpdated) mDefaultReaction else mDisplayReactions[0]
        if (reaction != null) updateReactButtonByReaction(reaction)
    }

    /**
     * Show Reaction dialog when user long click on react button
     */

    @SuppressLint("ClickableViewAccessibility")
    private fun showReactionsDialog() {
        box_up = MediaPlayer.create(context, R.raw.dialog_up);
        reaction_popup = MediaPlayer.create(context, R.raw.reaction_choose);
        if (isSoundEnabled){
            playSound(box_up!!);
        }
        val context = context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.react_dialog_layout, null)
        val reactionsGrid = dialogView.findViewById<GridView>(R.id.reactionsList)
        val adapter = ReactionAdapter(context, mReactions as ArrayList<Reaction>)
        reactionsGrid.adapter = adapter
        reactionsGrid.numColumns = mDialogColumnsNumber
        reactionsGrid.onItemClickListener =
            OnItemClickListener { parent, v, position, id ->
                currentReaction = try {mDisplayReactions[position]}catch (e:Exception){mReactions[position] }
                //updateReactButtonByReaction(currentReaction!!)
                if (isSoundEnabled){
                    playSound(reaction_popup!!)
                }
                mReactAlertDialog!!.cancel()
            }
        if (enableReactionTooltip) {
            val popupWindow = arrayOfNulls<PopupWindow>(1)
            reactionsGrid.onItemLongClickListener =
                OnItemLongClickListener { adapterView, view, position, id ->
                    val currentReaction = mReactions[position]
                    val tooltipView: View = LayoutInflater.from(context).inflate(R.layout.react_tooltip_layout, null)
                    tooltipView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    tooltipView.setBackgroundResource(mReactTooltipShape)

                    val tooltipTextView =
                        tooltipView.findViewById<TextView>(R.id.react_tooltip_text)
                    tooltipTextView.text = currentReaction.reactText
                    tooltipTextView.setTextColor(mReactTooltipTextColor)
                    popupWindow[0] = PopupWindow(
                        tooltipView,
                        tooltipView.measuredWidth,
                        tooltipView.measuredHeight,
                        true
                    )
                    popupWindow[0]!!.isOutsideTouchable = true
                    val viewLocation = IntArray(2)
                    view.getLocationOnScreen(viewLocation)
                    val xOffset = viewLocation[0].toFloat()
                    var yOffset = (viewLocation[1] - mTooltipOffsetFromReaction).toFloat()
                    if (yOffset <= TOOLTIP_VIEW_MIN_HEIGHT) yOffset += (mTooltipOffsetFromReaction * 2 + TOOLTIP_VIEW_MIN_HEIGHT).toFloat()
                    popupWindow[0]!!
                        .showAtLocation(
                            tooltipView,
                            Gravity.NO_GRAVITY,
                            xOffset.toInt(),
                            yOffset.toInt()
                        )
                    false
                }
            reactionsGrid.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (popupWindow[0] != null) popupWindow[0]!!.dismiss()
                }
                false
            }
        }
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(dialogView)
        mReactAlertDialog = dialogBuilder.create()
        mReactAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = mReactAlertDialog?.window
        window!!.setBackgroundDrawableResource(mReactDialogShape)
        window.setDimAmount(mDialogDimAmount)

        // Setup dialog gravity and dynamic position
        val windowManagerAttributes = window.attributes
        windowManagerAttributes.gravity = currentDialogGravity
        var dialogWidth = REACTION_ICON_SIZE * mDialogColumnsNumber
        if (dialogWidth > SCREEN_MAX_WIDTH) dialogWidth = SCREEN_MAX_WIDTH
        if (enableDialogDynamicPosition) {
            // Calculate the x and y positions only if the flag is enable
            val react = Rect()
            getGlobalVisibleRect(react)

            // Can be optimized and calculated once and modified only when size changed
            // Calculate x and y from global visible position to work also in Jetpack Compose
            windowManagerAttributes.x = react.left + react.width() / 2 - dialogWidth / 2
            windowManagerAttributes.y = react.top - react.height() * 2
        }
        mReactAlertDialog!!.show()
        if (mOnReactionDialogStateListener != null) mOnReactionDialogStateListener!!.onDialogOpened()
        window.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
        mReactAlertDialog!!.setOnDismissListener(DialogInterface.OnDismissListener {
            if (mOnReactionDialogStateListener != null) {
                mOnReactionDialogStateListener!!.onDialogDismiss()
            }
        })
    }
    /**
     * @param react Reaction to update UI by take attribute from it
     */
    private fun updateReactButtonByReaction(react: Reaction) {
        mCurrentReaction = react
        mReactButton.setImageResource(react.reactIconId)
        isReactButtonUpdated = react != mDefaultReaction
        if (mOnReactionChangeListener != null
            && react.reactIconId != mDisplayReactions[0].reactIconId
            && react.reactIconId != mDefaultReaction?.reactIconId
            ) mOnReactionChangeListener!!.onReactionButtonClick(react)

        if (react.reactIconId == mDisplayReactions[0].reactIconId
            && mOnButtonClickListener != null
        ) {mOnButtonClickListener?.onFirstReactionClick(react)}

        if (react.reactIconId == mDefaultReaction?.reactIconId
            && mOnButtonClickListener != null
            ) {mOnButtonClickListener?.onBackToDefault(react)}
    }

    /**
     * Modify the reactions dialog layout shape
     * @param drawableShape the new xml shape
     */
    fun setReactionDialogShape(@DrawableRes drawableShape: Int) {
        mReactDialogShape = drawableShape
    }

    fun setCurrentImageResource(react:Reaction){
        mCurrentReaction = react
        isReactButtonUpdated = react != mDefaultReaction
        mReactButton.setImageResource(react.reactIconId)
    }
    fun returnToDefaultReaction(){
        isReactButtonUpdated = false
        mCurrentReaction = mDefaultReaction
        mReactButton.setImageResource(mDefaultReaction?.reactIconId!!)
    }

    /**
     * Modify the reactions dialog gravity value, by default it Top | Start
     * @param gravity the new gravity
     * @since 2.1.0
     */
    fun setReactionDialogGravity(gravity: Int) {
        currentDialogGravity = gravity
    }

    /**
     * Enable/Disable the reactions dialog dynamic position
     * @param enable True to enable the relative position
     * @since 2.1.0
     */
    fun enableReactionDialogDynamicPosition(enable: Boolean) {
        enableDialogDynamicPosition = enable
    }

    /**
     * Modify the tooltip offset from the reaction icon
     * @param offset the new tooltip offset value
     */
    fun setTooltipOffsetFromReaction(offset: Int) {
        mTooltipOffsetFromReaction = offset
    }

    /**
     * Modify the tooltip text color
     * @param color the new text color value
     */
    fun setReactionTooltipTextColor(@ColorInt color: Int) {
        mReactTooltipTextColor = color
    }

    /**
     * Modify the tooltip layout shape
     * @param drawableShape the new xml shape
     */
    fun setReactionTooltipShape(@DrawableRes drawableShape: Int) {
        mReactTooltipShape = drawableShape
    }

    /**
     * Enable or disable the reactions tooltip feature
     * @param isEnable True to enable reactions tooltip feature
     */
    fun setEnableReactionTooltip(isEnable: Boolean) {
        enableReactionTooltip = isEnable
    }

    /**
     * Set the reactions list to new reactions
     * @param reactions Array of Reactions
     */

    fun setReactions(vararg reactions: Reaction) {
        mReactions.clear()
        addReactions(*reactions)
    }

    fun setDisplayReactions(vararg react: Reaction){
        mDisplayReactions.clear()
        addDisplayReactions(react)
    }

    private fun addDisplayReactions(react: Array<out Reaction>) {
        mDisplayReactions.addAll(listOf(*react) as Collection<Reaction>)
    }

    /**
     * Append new reactions to the current reactions list
     * @param reactions Array of Reactions
     * @since 2.1.0
     */

    fun addReactions(vararg reactions: Reaction) {
        mReactions.addAll(listOf(*reactions) as Collection<Reaction>)
        if (mDialogColumnsNumber == 0) mDialogColumnsNumber = mReactions.size
    }



    var currentReaction: Reaction?
        /**
         * Return the current reaction object
         * @return The Current reaction
         */
        get() = mCurrentReaction
        /**
         * Modify the current reactions with new one
         * @param reaction new reactions to set as a current reaction
         */
        set(reaction) {
            if (reaction != null) {
                updateReactButtonByReaction(reaction)
            }
        }
    var defaultReaction: Reaction?
        /**
         * Return the default reaction object
         * @return The default Reaction
         */
        get() = mDefaultReaction
        /**
         * Update the default Reaction by other Reaction
         * @param reaction the new reaction be to set as a default reaction
         */
        set(reaction) {
            mDefaultReaction = reaction
            mCurrentReaction = mDefaultReaction
            mReactButton.setImageResource(reaction?.reactIconId!!)
        }

    /**
     * Modify the columns number for the reactions dialog
     * @param number the new columns number valud
     */
    fun setDialogColumnsNumber(number: Int) {
        if (number > 0) mDialogColumnsNumber = number
    }

    /**
     * Modify the reactions dialog dim amount value
     * @param amount The new dim amount, from 0 for no dim to 1 for full dim.
     */
    fun setDimAmount(amount: Float) {
        mDialogDimAmount = amount
    }

    /**
     * @param listener OnReactionChangeListener to listen when user click on ReactButton
     */
    fun setOnButtonClickListener(listener: OnButtonClickListener?) {
        mOnButtonClickListener = listener
    }
    fun setOnReactionChangeListener(listener: OnReactionChangeListener?) {
        mOnReactionChangeListener = listener
    }

    /**
     * @param listener OnReactionDialogStateListener to listen when reaction dialog open or closed
     */
    fun setOnReactionDialogStateListener(listener: OnReactionDialogStateListener?) {
        mOnReactionDialogStateListener = listener
    }

    /**
     * @return true if current reaction type is default
     */
    fun isDefaultReaction(): Boolean {
        return !isReactButtonUpdated
    }

    override fun onClick(view: View) {
        onReactionButtonClick()
    }

    override fun onLongClick(view: View): Boolean {
        showReactionsDialog()
        return true
    }

    companion object {
        /**
         * The min height of tooltip
         */
        private const val TOOLTIP_VIEW_MIN_HEIGHT = 50

        /**
         * Reaction image view padding
         */
        const val ICON_PADDING = 10

        /**
         * The size of reaction icon in dp
         * Icon size + icon padding * 2
         */
        val ICON_SIZE_WITH_PADDING: Int = 45 + ICON_PADDING
    }
    private fun playSound(mediaPlayer: MediaPlayer) {
        if (isSoundEnabled) {
            mediaPlayer.setVolume(0.3f, 0.3f)
            mediaPlayer.start()
        }
    }
    private val isSoundEnabled = true
    var box_up: MediaPlayer? = null
    var reaction_popup: MediaPlayer? = null
}