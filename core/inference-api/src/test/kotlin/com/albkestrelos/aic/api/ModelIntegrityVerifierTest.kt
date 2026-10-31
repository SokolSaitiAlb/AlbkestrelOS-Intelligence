package com.albkestrelos.aic.api

import java.io.ByteArrayInputStream
import java.security.MessageDigest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ModelIntegrityVerifierTest {
    private val bytes = "small test model payload".toByteArray()

    @Test
    fun verifiesMatchingSizeAndDigest() {
        val model = descriptor(bytes)

        val result = ModelIntegrityVerifier.verify(model, ByteArrayInputStream(bytes))

        val verified = assertIs<ModelVerificationResult.Verified>(result)
        assertEquals(bytes.size.toLong(), verified.bytesRead)
        assertEquals(model.sha256, verified.sha256)
    }

    @Test
    fun rejectsDigestMismatch() {
        val model = descriptor(bytes, sha256 = "0".repeat(64))

        val result = ModelIntegrityVerifier.verify(model, ByteArrayInputStream(bytes))

        val rejected = assertIs<ModelVerificationResult.Rejected>(result)
        assertEquals(ModelVerificationFailure.DIGEST_MISMATCH, rejected.failure)
    }

    @Test
    fun rejectsFileShorterThanDeclaredSize() {
        val model = descriptor(bytes, declaredSize = bytes.size + 1L)

        val result = ModelIntegrityVerifier.verify(model, ByteArrayInputStream(bytes))

        val rejected = assertIs<ModelVerificationResult.Rejected>(result)
        assertEquals(ModelVerificationFailure.SIZE_MISMATCH, rejected.failure)
        assertEquals(bytes.size.toLong(), rejected.bytesRead)
    }

    @Test
    fun rejectsFileLongerThanDeclaredSize() {
        val model = descriptor(bytes, declaredSize = bytes.size - 1L)

        val result = ModelIntegrityVerifier.verify(model, ByteArrayInputStream(bytes))

        val rejected = assertIs<ModelVerificationResult.Rejected>(result)
        assertEquals(ModelVerificationFailure.SIZE_MISMATCH, rejected.failure)
        assertEquals(bytes.size.toLong(), rejected.bytesRead)
    }

    private fun descriptor(
        content: ByteArray,
        sha256: String = sha256(content),
        declaredSize: Long = content.size.toLong()
    ) = ModelDescriptor(
        modelId = "unit-test-model",
        version = "1",
        format = ModelFormat.ONNX,
        sha256 = sha256,
        fileSizeBytes = declaredSize,
        licenseId = "test-only"
    )

    private fun sha256(content: ByteArray): String =
        MessageDigest.getInstance("SHA-256")
            .digest(content)
            .joinToString("") { "%02x".format(it) }
}
