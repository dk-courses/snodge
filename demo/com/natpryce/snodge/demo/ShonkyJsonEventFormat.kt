package com.natpryce.snodge.demo

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.net.URI

/*
 * Defects in this class are corrected in the RobustJsonEventFormat
 */
class ShonkyJsonEventFormat : EventFormat {
    override fun serialise(event: ServiceEvent): String {
        val serialised = StringWriter()
        
        val writer = JsonWriter(serialised)
        writer.beginObject()
        writer.name("timestamp")
        writer.value(event.timestamp)
        writer.name("service")
        writer.value(event.service.toString())
        writer.name("type")
        writer.value(event.serviceState.javaClass.name)
        if (event.serviceState is FAILED) {
            writer.name("error")
            writer.value(event.serviceState.error)
        }
        writer.endObject()
        writer.flush()
        
        return serialised.toString()
    }
    
    override fun deserialise(input: String): ServiceEvent {
        val reader = JsonReader(StringReader(input))
        
        expect(reader, JsonToken.BEGIN_OBJECT)
        reader.beginObject()
        
        // Defect: cannot handle properties in any order
        // Caught by JsonEventFormatSnodgeTest.jsonPropertiesCanOccurInAnyOrder
        
        expectProperty(reader, "timestamp", JsonToken.NUMBER)
        val timestamp = reader.nextLong()
        
        expectProperty(reader, "service", JsonToken.STRING)
        // Defect: throws IllegalArgumentException on invalid URI syntax.
        // Caught by JsonEventFormatSnodgeTest.parsesEventSuccessfullyOrThrowsIOException
        val service = URI.create(reader.nextString())
        
        expectProperty(reader, "type", JsonToken.STRING)
        
        // Defect: throws ClassNotFoundException on unrecognised class name & loads arbitrary classes.
        // Caught by JsonEventFormatSnodgeTest.parsesEventSuccessfullyOrThrowsIOException
        val serviceStateClass = Class.forName(reader.nextString())
        val serviceState = if (serviceStateClass == FAILED::class.java) {
            expectProperty(reader, "error", JsonToken.STRING)
            FAILED(reader.nextString())
        } else {
            serviceStateClass.kotlin.objectInstance as ServiceState
        }
        
        // Defect: fails if new fields added to protocol.
        // Caught by JsonEventFormatSnodgeTest.ignoresUnrecognisedProperties
        expect(reader, JsonToken.END_OBJECT)
        
        return ServiceEvent(timestamp, service, serviceState)
    }
    
    private fun expectProperty(reader: JsonReader, expectedName: String, expectedJsonType: JsonToken) {
        if (reader.peek() != JsonToken.NAME || reader.nextName() != expectedName || reader.peek() != expectedJsonType) {
            throw IOException("expected property named " + expectedName + " of type " + expectedJsonType.name)
        }
    }
    
    private fun expect(reader: JsonReader, expectedToken: JsonToken) {
        val currentToken = reader.peek()
        if (currentToken != expectedToken) {
            throw IOException("expected $expectedToken but was $currentToken")
        }
    }
}
