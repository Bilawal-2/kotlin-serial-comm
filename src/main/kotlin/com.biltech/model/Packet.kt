package com.biltech.model

import kotlin.properties.Delegates.observable

class Packet() {

    var msg: String by observable("msg") { _, oldValue, newValue ->
        onMsgChanged?.invoke(oldValue, newValue)
    }
    var onMsgChanged: ((String, String) -> Unit)? = null
    var connection: Boolean by observable(false) { _, oldValue, newValue ->
        onConnectionChanged?.invoke(oldValue,newValue)
    }
    var onConnectionChanged: ((Boolean, Boolean) -> Unit)? = null

    init {
        msg = ""
        connection= false
    }


}