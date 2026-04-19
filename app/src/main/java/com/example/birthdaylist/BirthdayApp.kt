package com.example.birthdaylist


import android.app.Application
import com.example.birthdaylist.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BirthdayApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BirthdayApp)
            modules(appModule)
        }
    }
}