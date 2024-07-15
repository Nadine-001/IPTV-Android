import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.example.tvapp.R
import kotlinx.coroutines.NonCancellable.start
import java.io.IOException

class ChannelTV : AppCompatActivity(), SurfaceHolder.Callback {
    private val TAG: String = "ChannelTV"
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mPreview: SurfaceView
    private lateinit var holder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_tv1)
        supportActionBar?.hide()

        // Set SurfaceView and SurfaceHolder
        mPreview = findViewById(R.id.surface)
        holder = mPreview.holder
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Implementation of surfaceChanged method goes here
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Implementation of surfaceDestroyed method goes here
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Initialize and start MediaPlayer when Surface is created
        val url = "https://cdn-telkomsel-01.akamaized.net/Content/HLS/Live/channel(8e867ae0-b2c0-4968-9f60-a11aee8c0987)/index.m3u8"
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setDisplay(holder)
                prepare() // might take long! (for buffering, etc)
                start()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error setting data source", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer when the activity is destroyed
        mediaPlayer.release()
    }
}
