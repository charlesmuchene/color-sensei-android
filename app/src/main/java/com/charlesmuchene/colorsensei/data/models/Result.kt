package com.charlesmuchene.colorsensei.data.models

sealed class Result

object Success : Result()

object Failure : Result()