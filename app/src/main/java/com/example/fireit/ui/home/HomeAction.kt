package com.example.fireit.ui.home

import android.net.Uri

sealed interface HomeAction {

    data class ThrowPhotoCard(val photo: Uri?) : HomeAction
    data class KeepPhotoCard(val photo: Uri?) : HomeAction
    data object InvertItemList : HomeAction

    data object CheckPermissionAndLoadData : HomeAction
    data object OffShouldOpenSettings : HomeAction
    data object OnShouldOpenSettings : HomeAction

}