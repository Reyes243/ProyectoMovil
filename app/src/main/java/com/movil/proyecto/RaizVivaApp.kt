
package com.movil.proyecto

import android.app.Application

class RaizVivaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UserManager.init(this)
        ProductManager.init(this)
        CartManager.init(this)
        OrderManager.init(this)
    }
}
