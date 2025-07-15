package com.ikhut.messengerapp.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ikhut.messengerapp.data.utils.ImageUtils
import com.ikhut.messengerapp.domain.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel : ViewModel() {

    private val _imageUpdateState = MutableLiveData<Resource<String>>()
    val imageUpdateState: LiveData<Resource<String>> = _imageUpdateState

    fun handleImageSelected(
        context: Context, uri: Uri, currentLocalPath: String?, userId: String
    ) {
        _imageUpdateState.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val newPath = withContext(Dispatchers.IO) {
                    ImageUtils.saveImageToInternalStorage(context, uri, userId)
                }

                if (newPath != null) {
                    // Delete old image in background
                    withContext(Dispatchers.IO) {
                        currentLocalPath?.let { ImageUtils.deleteOldProfileImage(it) }
                    }

                    _imageUpdateState.postValue(Resource.Success(newPath))
                } else {
                    _imageUpdateState.postValue(Resource.Error("Failed to save image locally"))
                }
            } catch (e: Exception) {
                _imageUpdateState.postValue(Resource.Error(e.message ?: "Unexpected error"))
            }
        }
    }

    companion object {
        fun create(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ImageViewModel() as T
                }
            }
        }
    }
}
