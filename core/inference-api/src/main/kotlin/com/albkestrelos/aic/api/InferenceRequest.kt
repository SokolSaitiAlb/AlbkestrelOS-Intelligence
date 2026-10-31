package com.albkestrelos.aic.api

/**
 * One bounded inference request. Permission and purpose authorization must
 * happen in the trusted host before this object is sent to the runtime.
 */
data class InferenceRequest(
    val requestId: String,
    val task: InferenceTask,
    val input: String,
    val maxOutputTokens: Int,
    val timeoutMillis: Long
) {
    init {
        require(requestId.isNotBlank()) { "requestId must not be blank" }
        require(requestId.length <= RequestLimits.MAX_REQUEST_ID_CHARS) {
            "requestId exceeds the protocol limit"
        }
        require(input.isNotBlank()) { "input must not be blank" }
        require(input.length <= RequestLimits.MAX_INPUT_CHARS) {
            "input exceeds the protocol limit"
        }
        require(maxOutputTokens in 1..RequestLimits.MAX_OUTPUT_TOKENS) {
            "maxOutputTokens is outside the supported protocol range"
        }
        require(timeoutMillis in RequestLimits.MIN_TIMEOUT_MILLIS..RequestLimits.MAX_TIMEOUT_MILLIS) {
            "timeoutMillis is outside the supported protocol range"
        }
    }
}
