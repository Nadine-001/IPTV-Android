package com.example.tvapp

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class TV : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)
        supportActionBar?.hide()
        val videoView = findViewById<VideoView>(R.id.videoView)

        val videoUrl = "https://jawapostv.siar.us/jawapostv/live/playlist.m3u8"
        val videoUri = Uri.parse(videoUrl)

        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mp ->
            mp.start()
        }

        val mediaPlayer = MediaPlayer.create(this, videoUri)
        mediaPlayer.setOnPreparedListener { mp ->
            mp.start()
        }
    }
}
