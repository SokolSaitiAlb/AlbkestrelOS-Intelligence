package com.albkestrelos.aic.api

enum class FinishReason {
    COMPLETED,
    OUTPUT_LIMIT,
    CANCELLED
}

/**
 * Raw model output remains untrusted data. The host must parse and validate
 * structured outputs before any action policy is evaluated.
 */
data class InferenceResult(
    val requestId: String,
    val output: String,
    val finishReason: FinishReason
) {
    init {
        require(requestId.isNotBlank()) { "requestId must not be blank" }
        require(requestId.length <= RequestLimits.MAX_REQUEST_ID_CHARS) {
            "requestId exceeds the protocol limit"
        }
        require(output.length <= RequestLimits.MAX_INPUT_CHARS * 4) {
            "output exceeds the protocol limit"
        }
    }
}
