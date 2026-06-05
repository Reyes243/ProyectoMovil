
package com.movil.proyecto

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class RaizVivaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            Log.d("RaizVivaApp", "Initializing Firebase...")
            FirebaseApp.initializeApp(this)
            Log.d("RaizVivaApp", "Firebase initialized successfully")
            
            UserManager.init(this)
            ProductManager.init(this)
            CartManager.init(this)
            OrderManager.init(this)
        } catch (e: Exception) {
            Log.e("RaizVivaApp", "Error during app init: ${e.message}", e)
        }
    }
}


