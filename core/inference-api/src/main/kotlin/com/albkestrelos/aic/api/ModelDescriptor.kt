package com.albkestrelos.aic.api

enum class ModelFormat {
    LITERT_LM,
    ONNX
}

/**
 * Verified model metadata. The descriptor is not itself proof of integrity:
 * Model Manager must calculate the digest from the opened file and compare it
 * with sha256 before passing a read-only file descriptor to the runtime.
 */
data class ModelDescriptor(
    val modelId: String,
    val version: String,
    val format: ModelFormat,
    val sha256: String,
    val fileSizeBytes: Long,
    val licenseId: String
) {
    init {
        require(modelId.isNotBlank() && modelId.length <= RequestLimits.MAX_MODEL_ID_CHARS) {
            "modelId is invalid"
        }
        require(version.isNotBlank() && version.length <= RequestLimits.MAX_MODEL_VERSION_CHARS) {
            "version is invalid"
        }
        require(sha256.matches(Regex("^[A-Fa-f0-9]{64}$"))) {
            "sha256 must be a 64-character hexadecimal digest"
        }
        require(fileSizeBytes in 1L..RequestLimits.MAX_MODEL_FILE_BYTES) {
            "fileSizeBytes is outside the supported protocol range"
        }
        require(licenseId.isNotBlank() && licenseId.length <= RequestLimits.MAX_LICENSE_ID_CHARS) {
            "licenseId is invalid"
        }
    }
}
