package com.biltech

import com.biltech.comm.Serial
import com.biltech.model.Packet


fun main(args: Array<String>){
    _communicationSetup()

}
fun _communicationSetup() {

    val serial = Serial()
    serial.packet.onMsgChanged = { _, newValue ->
        try {
            println("Value From Serial Port: $newValue")
        } catch (ex: Exception) {
            println("Exception:$ex")
        }
    }
    serial.packet.onConnectionChanged = { _, b1 ->
        println(
                if (b1) "Connected to: ${serial.portName}" else "Not connected"
        )
    }
    serial.connect()
    serial.packet.connection = !serial.portName.isEmpty()
}

