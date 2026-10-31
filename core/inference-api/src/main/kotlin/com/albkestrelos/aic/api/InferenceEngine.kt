package com.albkestrelos.aic.api

/**
 * Backend-neutral local inference lifecycle.
 *
 * Implementations must serialize engine ownership according to the selected
 * backend's threading rules. The host must invoke initialization/generation
 * off the Android main thread and must treat every result as untrusted.
 */
interface InferenceEngine {
    suspend fun initialize(model: ModelDescriptor)

    suspend fun generate(request: InferenceRequest): InferenceResult

    suspend fun cancel(requestId: String)

    suspend fun shutdown()
}
