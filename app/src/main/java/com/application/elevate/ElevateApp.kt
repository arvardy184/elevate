package com.application.elevate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent

@HiltAndroidApp
class ElevateApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Ganti "YOUR_APP_TOKEN" dengan token asli dari dashboard Instabug kamu
        Instabug.Builder(this, "e3b365527ecc2b037604658069037425")
            .setInvocationEvents(InstabugInvocationEvent.SHAKE)
            .build()
    }
}