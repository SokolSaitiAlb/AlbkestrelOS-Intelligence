package com.albkestrelos.aic.api

/**
 * A bounded task requested from the local inference engine.
 *
 * These values are part of the host/runtime contract. Do not accept arbitrary
 * tool names or executable instructions as task identifiers.
 */
enum class InferenceTask {
    CHAT_COMPLETION,
    SUMMARIZATION,
    CLASSIFICATION,
    STRUCTURED_EXTRACTION
}
