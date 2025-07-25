package com.ikhut.messengerapp.application.config

object Constants {
    const val FIREBASE_USERS = "users"
    const val FIREBASE_MESSAGES = "messages"
    const val FIREBASE_CONVERSATIONS = "conversations"

    const val PREFS_USER = "user_prefs"

    const val PAGE_SIZE = 20
    const val LAZY_UPDATE_SIZE = 8
    const val MIN_PASSWORD_SIZE = 4
    const val MAX_MESSAGE_LENGTH = 57
    const val MIN_SEARCH_LENGTH = 3

    const val DOT_RADIUS = 15f
    const val DOT_COUNT = 8
    const val MAX_OPACITY = 255
    const val CIRCLE_RADIUS = 100f
    const val ANIM_DURATION = 1000L
    const val ANIM_DELAY = 125
    const val SEARCH_DELAY = 300L

    const val UI_AVATARS_BASE = "https://ui-avatars.com/api/"
    const val PLACEHOLDER_NAME = "User"
    const val EXTRA_USERNAME = "extra_username"

    const val PROFILE_IMAGES_DIR = "profile_images"
    const val IMAGE_QUALITY = 80
    const val MAX_IMAGE_SIZE = 1024

    const val HEADER_SIGN_OUT = "Sign Out"
    const val QUERY_SIGN_OUT = "Are you sure you want to sign out?"
    const val POSITIVE_RESPONSE_SIGN_OUT = "Yes"
    const val NEGATIVE_RESPONSE_SIGN_OUT = "No"

    const val PARAM_LAST_MESSAGE_TIME = "lastMessageTime"
    const val PARAM_TIMESTAMP = "timestamp"
    const val PARAM_USERNAME = "username"
    const val PARAM_ALPHA = "alpha"
    const val PARAM_SCALE = "scale"
    const val PARAM_NICKNAME = "Nickname"
    const val PARAM_JOB = "Job"
    const val PARAM_SEARCH_USERS = "search_users"

    const val SUCCESS_PROFILE_UPDATED = "Profile updated successfully!"
    const val SUCCESS_IMAGE_UPDATED = "Profile image updated!"

    const val ERROR_USER_ALREADY_EXISTS = "Username already exists"
    const val ERROR_USER_NOT_FOUND = "User not found"
    const val ERROR_NO_USER_LOGGED_IN = "No user logged in"
    const val ERROR_WRONG_PASSWORD = "Wrong password"
    const val ERROR_USERNAME_CANNOT_BE_EMPTY = "Username cannot be empty"
    const val ERROR_JOB_CANNOT_BE_EMPTY = "Job cannot be empty"
    const val ERROR_PASSWORD_CHECK = "Password must be at least 4 characters"
    const val ERROR_PERMISSION_DENIED = "Permission denied. Cannot access gallery."
    const val ERROR_EMPTY_LOGIN = "Username or password empty"
    const val ERROR_UNKNOWN = "Unknown error"
    const val ERROR_FAILED_TO_LOAD_CONVERSATIONS = "Failed to load conversations"
    const val ERROR_NICKNAME_CANNOT_BE_EMPTY = "Nickname cannot be empty"
    const val ERROR_UPDATE_FAILED = "Update failed"
    const val ERROR_NETWORK = "Network error"
    const val ERROR_UNEXPECTED = "Unexpected error"
    const val ERROR_FAILED_TO_SAVE_IMAGE_LOCALLY = "Failed to save image locally"
    const val ERROR_FAILED_TO_UPDATE_PROFILE_PICTURE = "Failed to update profile picture"
}