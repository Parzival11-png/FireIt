package com.example.fireit.ui.home

import android.net.Uri


data class HomeUiState(
    val currentItemId: Int = 0,
    val currentItemList: List<Uri> = emptyList(),
    val listToBurn: List<Uri> = emptyList()
)
data class PermissionUiState (
    val hasFilesPermission : Boolean = false,
    val shouldOpenSettings : Boolean = false,
    val permissionToRequest: String? = null,
    val showDeniedState: Boolean = false
)
