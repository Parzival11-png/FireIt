package com.example.fireit.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Debug
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fireit.data.repository.local_preferences.LocalPreferencesRepository
import com.example.fireit.data.repository.media.MediaStoreRepository
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val mediaStoreRepository: MediaStoreRepository,
    private val userPreferencesRepository: LocalPreferencesRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val _permissionUiState = MutableStateFlow(PermissionUiState())
    val permissionUiState = _permissionUiState.asStateFlow()
    val hasFilePermission = userPreferencesRepository.hasFilePermission
    // REPO STATE VARS



    fun onAction(action: HomeAction){
        when(action){

            is HomeAction.ThrowPhotoCard -> throwPhotoCard(action.photo)
            is HomeAction.KeepPhotoCard -> keepPhotoCard(action.photo)
            HomeAction.InvertItemList -> invertItemList()

            HomeAction.CheckPermissionAndLoadData -> checkPermissionAndLoadData()
            HomeAction.OffShouldOpenSettings -> offShouldOpenSettings()
            HomeAction.OnShouldOpenSettings -> onShouldOpenSettings()
        }
    }


    private fun throwPhotoCard(photo: Uri?) {
        if (photo != null) {
            Log.d("FireIt", "Throw card")
            _uiState.update {
                it.copy(
                    currentItemList = it.currentItemList - photo,
                    listToBurn = it.listToBurn + photo
                )
            }
            goToNextPhotoCard()
            showLisToBurn()
        }
    }

    private fun keepPhotoCard(photo: Uri?) {
        Log.d("FireIt", "Keep card")
        goToNextPhotoCard()
    }

    private fun goToNextPhotoCard() {
        _uiState.update {
            it.copy(currentItemId = it.currentItemId + 1)
        }
    }

    private fun invertItemList() {
        _uiState.update {
            it.copy(
                currentItemList = it.currentItemList.reversed(),
                currentItemId = 0 // Reiniciar el índice para empezar desde el nuevo inicio
            )
        }
    }


    //Debug
    private fun showLisToBurn(){
        Log.d("Debug", _uiState.value.listToBurn.toString())
    }


    private fun startGallery(){
        Log.d("Chamoy"," Start Gallery ")
        Log.d("Chamoy"," Loading Gallery ")
        _uiState.update {
            it.copy(currentItemList = mediaStoreRepository.getUris())
        }
        Log.d("Chamoy"," Total Gallery:  ")
        Log.d("Chamoy", _uiState.value.currentItemList?.size.toString())
        Log.d("Chamoy", _uiState.value.currentItemList.toString())
    }

    private fun deleteItems(items : List<Uri>){
        if(permissionUiState.value.hasFilesPermission){
            viewModelScope.launch {
                val result = mediaStoreRepository.deleteImages(items)
                Log.d("Delete", result.toString())
            }
        }
    }







    private fun offShouldOpenSettings(){
        _permissionUiState.update { it.copy(shouldOpenSettings = false) }
    }
    private fun onShouldOpenSettings(){
        _permissionUiState.update { it.copy(shouldOpenSettings = true) }
    }
    private fun checkPermissionAndLoadData() {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        setFilePermission(hasPermission)
        _permissionUiState.update { it.copy(hasFilesPermission = hasPermission) }
        if (hasPermission) {
            startGallery()
        }else{
            onShouldOpenSettings()
        }
    }
    private fun setFilePermission(st : Boolean){
        viewModelScope.launch {
            userPreferencesRepository.setFilePermissionSt(st)
        }
    }

}