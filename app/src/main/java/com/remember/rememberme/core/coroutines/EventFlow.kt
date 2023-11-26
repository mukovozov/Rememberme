package com.remember.rememberme.core.coroutines

import kotlinx.coroutines.flow.MutableSharedFlow

fun<T> EventFlow() = MutableSharedFlow<T>(extraBufferCapacity = 1)