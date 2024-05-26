package com.example.services.storage

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import dagger.hilt.android.scopes.ServiceScoped
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@ServiceScoped
class BlackSerializer : Serializer<BlackPhoneNumber?> {
    override val defaultValue: BlackPhoneNumber?
        get() = null

    override suspend fun readFrom(input: InputStream): BlackPhoneNumber? {
        try {
            return BlackPhoneNumber.parseFrom(input)
        } catch (e: IOException) {
            val msg = "Proto serialization failed"
            Log.e(TAG, msg, e)
            throw CorruptionException(msg, e)
        }
    }

    override suspend fun writeTo(t: BlackPhoneNumber?, output: OutputStream) {
        try {
            t?.writeTo(output);
        } catch (e: IOException) {
            val msg = "Proto deserialization failed";
            Log.e(TAG, msg);
            throw CorruptionException(msg, e)
        }
    }

    companion object {
        private const val TAG = "BlackSerializer"
    }
}
