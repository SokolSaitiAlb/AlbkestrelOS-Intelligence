package com.albkestrelos.aic.api

import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

enum class ModelVerificationFailure {
    SIZE_MISMATCH,
    DIGEST_MISMATCH
}

sealed interface ModelVerificationResult {
    data class Verified(
        val bytesRead: Long,
        val sha256: String
    ) : ModelVerificationResult

    data class Rejected(
        val failure: ModelVerificationFailure,
        val bytesRead: Long,
        val actualSha256: String?
    ) : ModelVerificationResult
}

/**
 * Streams a model file through SHA-256 without loading the full model into RAM.
 *
 * The caller owns [input] and must close it. This verifier does not read beyond
 * the declared model size plus one byte, and does not decide whether the
 * declared publisher/signing key is trusted; signature/provenance checks are
 * a separate Model Manager responsibility.
 */
object ModelIntegrityVerifier {
    private const val BUFFER_SIZE = 64 * 1024

    fun verify(
        model: ModelDescriptor,
        input: InputStream
    ): ModelVerificationResult {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(BUFFER_SIZE)
        var totalBytes = 0L

        while (true) {
            val count = input.read(buffer)
            if (count < 0) break

            if (count == 0) {
                val singleByte = input.read()
                if (singleByte < 0) break
                totalBytes += 1
                if (totalBytes > model.fileSizeBytes ||
                    totalBytes > RequestLimits.MAX_MODEL_FILE_BYTES
                ) {
                    return ModelVerificationResult.Rejected(
                        failure = ModelVerificationFailure.SIZE_MISMATCH,
                        bytesRead = totalBytes,
                        actualSha256 = null
                    )
                }
                digest.update(singleByte.toByte())
                continue
            }

            totalBytes += count
            if (totalBytes > model.fileSizeBytes ||
                totalBytes > RequestLimits.MAX_MODEL_FILE_BYTES
            ) {
                return ModelVerificationResult.Rejected(
                    failure = ModelVerificationFailure.SIZE_MISMATCH,
                    bytesRead = totalBytes,
                    actualSha256 = null
                )
            }
            digest.update(buffer, 0, count)
        }

        val actualDigest = digest.digest().toHex()
        if (totalBytes != model.fileSizeBytes) {
            return ModelVerificationResult.Rejected(
                failure = ModelVerificationFailure.SIZE_MISMATCH,
                bytesRead = totalBytes,
                actualSha256 = actualDigest
            )
        }

        val expectedBytes = model.sha256.lowercase().toByteArray(StandardCharsets.US_ASCII)
        val actualBytes = actualDigest.toByteArray(StandardCharsets.US_ASCII)
        if (!MessageDigest.isEqual(expectedBytes, actualBytes)) {
            return ModelVerificationResult.Rejected(
                failure = ModelVerificationFailure.DIGEST_MISMATCH,
                bytesRead = totalBytes,
                actualSha256 = actualDigest
            )
        }

        return ModelVerificationResult.Verified(
            bytesRead = totalBytes,
            sha256 = actualDigest
        )
    }

    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { byte -> "%02x".format(byte) }
}
