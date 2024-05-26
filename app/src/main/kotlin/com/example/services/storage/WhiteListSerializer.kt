package com.example.services.storage

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dagger.hilt.android.scopes.ServiceScoped
import java.io.InputStream
import java.io.OutputStream

/**
 *@since 26/05/2024
 *@author Paval Shlyk
 */
@ServiceScoped
class WhiteSerializer : Serializer<WhiteList> {
    override val defaultValue: WhiteList
        get() = WhiteList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): WhiteList {
        try {
            return WhiteList.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Proto parsing error", e)
        }
    }

    override suspend fun writeTo(t: WhiteList, output: OutputStream) {
        t.writeTo(output);
    }
}

