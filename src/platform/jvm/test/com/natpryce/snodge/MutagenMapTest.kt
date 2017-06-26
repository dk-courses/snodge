package com.natpryce.snodge

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test


class MutagenMapTest {
    @Test
    fun `applies a function once to the original`() {
        val mutagen = map<Int> { n -> -n }
        
        assertThat(mutagen(Random(), 10).toList().map { it.value }, equalTo(listOf(-10)))
    }
    
    @Test
    fun `for strings`() {
        val mutagen = map(String::reversed)
        
        assertThat(mutagen(Random(), "abc").toList().map { it.value }, equalTo(listOf("cba")))
    }
}