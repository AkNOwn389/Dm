package com.aknown389.dm.reactionTesting

import com.aknown389.dm.R

object FbReactions {
    var defaultReactDay = Reaction(
        ReactionsConstance.LIKE,
        ReactionsConstance.DEFAULT,
        R.drawable.like_thumbs_up_alt_flat_icon
    )
    var defaultReactNight = Reaction(
        ReactionsConstance.DEFAULT,
        ReactionsConstance.GRAY,
        R.drawable.like_alt_night_flat_icon
    )
    var reactions = arrayOf(
    Reaction(ReactionsConstance.LIKE, ReactionsConstance.BLUE, R.drawable.like_blue_full_flat_icon),
    Reaction(ReactionsConstance.LOVE, ReactionsConstance.RED_LOVE, R.drawable.ic_heart),
    Reaction(ReactionsConstance.SMILE, ReactionsConstance.YELLOW_WOW, R.drawable.ic_happy),
    Reaction(ReactionsConstance.WOW, ReactionsConstance.YELLOW_WOW, R.drawable.ic_surprise),
    Reaction(ReactionsConstance.SAD, ReactionsConstance.YELLOW_HAHA, R.drawable.ic_sad),
    Reaction(ReactionsConstance.ANGRY, ReactionsConstance.RED_ANGRY, R.drawable.ic_angry)
    )
    var reactionsGift = arrayOf(
        Reaction(ReactionsConstance.LIKE, ReactionsConstance.BLUE, R.drawable.like),
        Reaction(ReactionsConstance.LOVE, ReactionsConstance.RED_LOVE, R.drawable.love),
        Reaction(ReactionsConstance.SMILE, ReactionsConstance.YELLOW_WOW, R.drawable.haha),
        Reaction(ReactionsConstance.WOW, ReactionsConstance.YELLOW_WOW, R.drawable.wow),
        Reaction(ReactionsConstance.SAD, ReactionsConstance.YELLOW_HAHA, R.drawable.sad),
        Reaction(ReactionsConstance.ANGRY, ReactionsConstance.RED_ANGRY, R.drawable.angry)
    )
}