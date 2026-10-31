package com.albkestrelos.aic.api

/**
 * Protocol-level guardrails. These are not claims about the final safe
 * throughput or memory capacity of any specific Pixel 8a model/backend.
 */
object RequestLimits {
    const val MAX_REQUEST_ID_CHARS = 128
    const val MAX_INPUT_CHARS = 32_768
    const val MAX_OUTPUT_TOKENS = 2_048
    const val MIN_TIMEOUT_MILLIS = 100L
    const val MAX_TIMEOUT_MILLIS = 120_000L

    const val MAX_MODEL_ID_CHARS = 96
    const val MAX_MODEL_VERSION_CHARS = 64
    const val MAX_LICENSE_ID_CHARS = 128
    const val MAX_MODEL_FILE_BYTES = 4L * 1024L * 1024L * 1024L
}
