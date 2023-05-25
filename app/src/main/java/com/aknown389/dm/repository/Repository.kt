package com.aknown389.dm.repository

import com.aknown389.dm.models.loginRegModels.RegRequestCodeResponse
import com.aknown389.dm.api.retroInstance.LoginRegisterInstance
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.models.global.LoginBahavior
import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.models.loginRegModels.LogoutResponseModel
import com.aknown389.dm.models.profileModel.MeModel
import com.aknown389.dm.models.profileGalleryModels.MyGalleryResponseModel
import com.aknown389.dm.models.loginRegModels.RegReqCodeModel
import com.aknown389.dm.models.profileModel.RequestAvatarResponse
import com.aknown389.dm.models.profileModel.UpdateDetailsBodyModel
import com.aknown389.dm.models.postmodel.UploadPostResponseModel
import com.aknown389.dm.models.postmodel.UploadTextPostBody
import com.aknown389.dm.models.showFriendModels.UserCardViewResponseModel
import com.aknown389.dm.models.logoutmodel.LogOutBodyModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response


class Repository(){
    suspend fun behavior(token: String):Response<LoginBahavior>{
        return RetrofitInstance.api.bahavior(token)
    }
    suspend fun getFeed(token: String, page: Int):Response<FeedResponseModelV2>{
        return RetrofitInstance.api.newsfeed(page, token)
    }
    suspend fun uploadImage(token: String, file: List<MultipartBody.Part>, caption: RequestBody,  privacy:RequestBody): Response<UploadPostResponseModel> {
        return PostInstance.api.uploadMultipleImages(token, caption = caption, images = file, privacy = privacy)
    }

    suspend fun uploadTextPost(token: String, caption: UploadTextPostBody): Response<UploadPostResponseModel> {
        return PostInstance.api.uploadTextPost(token, caption)
    }
    suspend fun getAvatar(token: String, username: String): Response<RequestAvatarResponse> {
        return RetrofitInstance.api.getAvatarR(username, token)
    }
    suspend fun logout(token: String, body: LogOutBodyModel): Response<LogoutResponseModel>{
        return  RetrofitInstance.api.logout(token, body)
    }
    suspend fun me(token: String): Response<MeModel>{
        return RetrofitInstance.api.me(token)
    }
    suspend fun myGallery(page:Int, token:String): Response<MyGalleryResponseModel>{
        return PostInstance.api.myGallery(page, token)
    }
    suspend fun updateProfilePicture(token: String, image: MultipartBody.Part, caption: RequestBody): Response<UploadPostResponseModel>{
        return RetrofitInstance.api.updateProfilePicture(token, image, caption)

    }

    suspend fun updateProfileCover(token: String, image: MultipartBody.Part, caption: RequestBody): Response<UploadPostResponseModel>{
        return RetrofitInstance.api.updateProfileCover(token, image, caption)
    }

    suspend fun updateDetails(token:String, body: UpdateDetailsBodyModel): Response<UploadPostResponseModel>{
        return  RetrofitInstance.api.updateDetails(token, body)
    }

    suspend fun getUserSuggested(token: String, page: Int): Response<UserCardViewResponseModel>{
        return  RetrofitInstance.api.getSuggestedFriend(token, page)
    }

    suspend fun getFollowing(token: String, page: Int):Response<UserCardViewResponseModel> {
        return RetrofitInstance.api.getFollowing(token, page)
    }

    suspend fun getFriends(token: String, page: Int):Response<UserCardViewResponseModel> {
        return RetrofitInstance.api.getFriends(token, page)
    }

    suspend fun getFollower(token: String, page: Int):Response<UserCardViewResponseModel> {
        return RetrofitInstance.api.getFollower(token, page)
    }

    suspend fun getRegCode(body: RegReqCodeModel): Response<RegRequestCodeResponse>{
        return LoginRegisterInstance.api.getRegCode(body)
    }
}