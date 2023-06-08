package com.aknown389.dm.pageView.profile.dataClass

import com.aknown389.dm.db.local.UserProfileData
import com.aknown389.dm.models.homepostmodels.PostDataModel


fun UserProfileData.toPostDataModel():PostDataModel{
    return PostDataModel(
        id = "profileDisplay",
        backgroundImage = backgroundImage,
        bio = bio,
        gender = gender,
        hobby = hobby,
        interested = interested,
        location = location,
        name = name,
        profileImage = profileImage,
        school = school,
        user = user,
        works = works,
        email = email,
        firstName = firstName,
        followers = followers,
        following = following,
        last_name = last_name,
        username = username,
        post_lenght = post_lenght,
        media_type = 8,
    )
}