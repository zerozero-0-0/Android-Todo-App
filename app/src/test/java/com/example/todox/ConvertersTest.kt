package com.example.todox

import com.example.todox.data.db.Converters
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters(
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    )

    @Test
    fun `round trip tags`() {
        val tags = listOf("work", "urgent", "home")
        val serialized = converters.fromTags(tags)
        val restored = converters.toTags(serialized)

        assertEquals(tags, restored)
    }

    @Test
    fun `blank string becomes empty list`() {
        val restored = converters.toTags("")
        assertEquals(emptyList<String>(), restored)
    }
}
