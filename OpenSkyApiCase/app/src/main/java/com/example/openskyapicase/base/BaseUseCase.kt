package com.example.openskyapicase.base

import com.example.openskyapicase.common.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

abstract class BaseUseCase<in P, R> {

    operator fun invoke(param: P?): Flow<State<R?>> = flow {
        emit(State.Loading)
        val result = execute(param)
        emit(State.Success(result))
    }
        .catch { e -> emit(State.Error(e.localizedMessage ?: "Unknown error")) }
        .flowOn(Dispatchers.IO)

    protected abstract suspend fun execute(param: P?): R
}