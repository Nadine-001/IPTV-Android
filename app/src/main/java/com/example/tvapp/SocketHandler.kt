import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            mSocket = IO.socket("http://10.218.15.221:8000")
        } catch (e: URISyntaxException) {
            Log.e("SocketHandler", "URISyntaxException: ${e.message}")
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection(context: Context) {
        mSocket.connect()
        setupSocketListeners(context)
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

    private fun setupSocketListeners(context: Context) {
        mSocket.on(Socket.EVENT_CONNECT) {
            Log.i("Socket", "Connected to the server")
//trttrtr
            // Emit the MAC address when connected
            val macAddress = "02:00:00:00:00:00"
            val jsonObject = JSONObject()
            jsonObject.put("mac_address", macAddress)
            mSocket.emit("television", jsonObject)
            Log.i("Socket", "Emitting mac_address: $macAddress")
        }

        mSocket.on("deliverNotif") { args ->
            if (args.isNotEmpty()) {
                val message = args[0] as String
                Log.i("Socket", "Received data: $message")

                // Run on UI thread to show alert dialog
                (context as? Activity)?.runOnUiThread {
                    val alertDialog = AlertDialog.Builder(context).apply {
                        setTitle("Notification")
                        setMessage(message)
                        setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        create()
                    }.show()

                    alertDialog.setOnKeyListener { dialog, keyCode, event ->
                        if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) && event.action == KeyEvent.ACTION_UP) {
                            dialog.dismiss()
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        }
    }
}
