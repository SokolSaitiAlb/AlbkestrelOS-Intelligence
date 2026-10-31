package com.albkestrelos.aic.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InferenceContractsTest {
    @Test
    fun acceptsRequestWithinProtocolBounds() {
        val request = InferenceRequest(
            requestId = "req-001",
            task = InferenceTask.SUMMARIZATION,
            input = "Summarize the selected text.",
            maxOutputTokens = 128,
            timeoutMillis = 30_000
        )

        assertEquals("req-001", request.requestId)
        assertEquals(InferenceTask.SUMMARIZATION, request.task)
    }

    @Test
    fun rejectsBlankRequestId() {
        assertFailsWith<IllegalArgumentException> {
            InferenceRequest("", InferenceTask.CHAT_COMPLETION, "hello", 64, 10_000)
        }
    }

    @Test
    fun rejectsOversizedInput() {
        assertFailsWith<IllegalArgumentException> {
            InferenceRequest(
                "req-oversized",
                InferenceTask.CHAT_COMPLETION,
                "x".repeat(RequestLimits.MAX_INPUT_CHARS + 1),
                64,
                10_000
            )
        }
    }

    @Test
    fun rejectsOutputTokenLimitOutsideRange() {
        assertFailsWith<IllegalArgumentException> {
            InferenceRequest("req-limit", InferenceTask.CHAT_COMPLETION, "hello", 0, 10_000)
        }
    }

    @Test
    fun rejectsTimeoutOutsideRange() {
        assertFailsWith<IllegalArgumentException> {
            InferenceRequest("req-timeout", InferenceTask.CHAT_COMPLETION, "hello", 64, 99)
        }
    }

    @Test
    fun acceptsValidModelDescriptor() {
        val model = ModelDescriptor(
            modelId = "test-model",
            version = "0.1",
            format = ModelFormat.ONNX,
            sha256 = "a".repeat(64),
            fileSizeBytes = 1_024,
            licenseId = "Apache-2.0"
        )

        assertEquals(ModelFormat.ONNX, model.format)
    }

    @Test
    fun rejectsMalformedModelDigest() {
        assertFailsWith<IllegalArgumentException> {
            ModelDescriptor(
                modelId = "test-model",
                version = "0.1",
                format = ModelFormat.LITERT_LM,
                sha256 = "not-a-digest",
                fileSizeBytes = 1_024,
                licenseId = "Apache-2.0"
            )
        }
    }

    @Test
    fun rejectsModelLargerThanProtocolMaximum() {
        assertFailsWith<IllegalArgumentException> {
            ModelDescriptor(
                modelId = "oversized",
                version = "0.1",
                format = ModelFormat.ONNX,
                sha256 = "b".repeat(64),
                fileSizeBytes = RequestLimits.MAX_MODEL_FILE_BYTES + 1,
                licenseId = "Apache-2.0"
            )
        }
    }
}
