package com.biltech.comm

import com.biltech.model.Packet
import jssc.SerialPort
import jssc.SerialPortEvent
import jssc.SerialPortException
import jssc.SerialPortList
import java.util.*


class Serial{
    private val ardPort: String
    private var serPort: SerialPort? = null
    private var sb = StringBuilder()

    val packet = Packet()
    constructor() {
        ardPort = ""
    }

    constructor(port: String) {
        ardPort = port
    }

    fun connect(): Boolean {
        Arrays.asList(*SerialPortList.getPortNames())
                .stream()
                .filter { name: String ->
                    !ardPort.isEmpty() && name == ardPort ||
                            ardPort.isEmpty() &&
                            USUAL_PORTS.stream()
                                    .anyMatch { p: String? -> name.startsWith(p!!) }
                }
                .findFirst()
                .ifPresent { name: String ->
                    try {
                        serPort = SerialPort(name)
                        println("Connecting to " + serPort!!.portName)
                        if (serPort!!.openPort()) {
                            serPort!!.setParams(115200,
                                    SerialPort.DATABITS_8,
                                    SerialPort.STOPBITS_1,
                                    SerialPort.PARITY_NONE)
                            serPort!!.eventsMask = SerialPort.MASK_RXCHAR
                            serPort!!.addEventListener { event: SerialPortEvent ->
                                if (event.isRXCHAR) {
                                    try {
                                        sb.append(serPort!!.readString(event.eventValue))
                                        val ch = sb.toString()

                                        if (ch.endsWith("\n")) {
                                            packet.msg = ch
                                            sb = StringBuilder()
                                        }
                                    } catch (e: SerialPortException) {
                                        println("SerialEvent error:$e")
                                    }
                                }
                            }
                        }
                    } catch (ex: SerialPortException) {
                        println("ERROR: Port '$name': $ex")
                    }
                }
        return serPort != null
    }

    fun disconnect() {
        if (serPort != null) {
            try {
                serPort!!.removeEventListener()
                if (serPort!!.isOpened) {
                    serPort!!.closePort()
                }
            } catch (ex: SerialPortException) {
                println("ERROR closing port exception: $ex")
            }
            println("Disconnecting: comm port closed.")
        }
    }

    val portName: String
        get() = if (serPort != null) serPort!!.portName else ""

    companion object {
        /* List of usual serial ports. Add more or remove those you don't need */
        private val USUAL_PORTS = Arrays.asList(
                "/dev/tty.usbmodem", "/dev/tty.usbserial",  // Mac OS X
                "/dev/usbdev", "/dev/ttyUSB", "/dev/ttyACM", "/dev/serial", "/dev/ttyS0",  // Linux
                "COM3", "COM4", "COM5", "COM6" // Windows
        )
        const val SEPARATOR = ";"
    }
}
