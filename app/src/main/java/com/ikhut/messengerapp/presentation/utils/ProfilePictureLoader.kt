package com.ikhut.messengerapp.presentation.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ikhut.messengerapp.R
import com.ikhut.messengerapp.application.config.Constants
import com.ikhut.messengerapp.data.utils.ImageUtils
import java.io.File

object ProfilePictureLoader {

    fun loadProfilePicture(
        context: Context,
        imageView: ImageView,
        imageRes: Int,
        imageUrl: String?,
        localImagePath: String? = null,
        placeholderName: String = Constants.PLACEHOLDER_NAME,
    ) {
        val requestOptions = RequestOptions().placeholder(R.drawable.avatar_image_placeholder)
            .error(R.drawable.avatar_image_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)
            .circleCrop().override(Target.SIZE_ORIGINAL)

        when {
            !localImagePath.isNullOrEmpty() && ImageUtils.fileExists(localImagePath) -> {
                Glide.with(context).load(File(localImagePath)).apply(requestOptions).into(imageView)
            }

            !imageUrl.isNullOrEmpty() -> {
                Glide.with(context).load(imageUrl).apply(requestOptions).into(imageView)
            }

            imageRes != 0 -> {
                Glide.with(context).load(imageRes).apply(requestOptions).into(imageView)
            }

            else -> {
                val fallbackUrl = generateFallbackUrl(placeholderName)
                Glide.with(context).load(fallbackUrl).apply(requestOptions).into(imageView)
            }
        }
    }

    private fun generateFallbackUrl(
        name: String
    ): String {
        val cleanName = name.replace(" ", "+")

        return "${Constants.UI_AVATARS_BASE}?name=$cleanName&background=random&color=fff&size=200&rounded=true"
    }
}
