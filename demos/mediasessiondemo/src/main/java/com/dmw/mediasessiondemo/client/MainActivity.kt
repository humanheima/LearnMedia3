package com.dmw.mediasessiondemo.client

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmw.mediasessiondemo.databinding.ActivityMainBinding
import com.dmw.mediasessiondemo.server.MusicService
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture

/**
 * Created by p_dmweidu on 2024/8/1
 * Desc:
 */
class MainActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "MainActivity"
    }

    private var list: MutableList<MediaItem> = mutableListOf()

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>

    private val mediaBrowser: MediaBrowser?
        get() = if (browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null

    private var rvAdapter: DemoAdapter? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvAdapter = DemoAdapter(list)
        rvAdapter?.onClickAction = onClick@{
            Log.d(TAG, "onClick: $it")
            if (it.mediaMetadata.isBrowsable == true) {
                getChildList(it)
            } else {

                toast("可以播放")

                val browser = this.mediaBrowser ?: return@onClick
                browser.setMediaItems(mutableListOf(it), 0, C.TIME_UNSET)
                browser.prepare()
                browser.play()

                PlayerActivity.launch(this)

            }
        }

        binding.rv.adapter = rvAdapter
        binding.rv.layoutManager = LinearLayoutManager(this)

        browserFuture = MediaBrowser.Builder(
            this,
            SessionToken(this, ComponentName(this, MusicService::class.java))
        ).buildAsync()

        browserFuture.addListener({

            Log.d(TAG, "onCreate: connected to MediaBrowserService")

        }, ContextCompat.getMainExecutor(this))

        binding.btnGetRoot.setOnClickListener {

            Log.d(TAG, "onCreate: mediaBrowser = $mediaBrowser")

            val listenableFuture: ListenableFuture<LibraryResult<MediaItem>>? =
                mediaBrowser?.getLibraryRoot(null)

            listenableFuture?.addListener({

                val result: LibraryResult<MediaItem> = listenableFuture.get()
                val rootMediaItem: MediaItem? = result.value

                if (rootMediaItem != null) {
                    list.add(rootMediaItem)
                    rvAdapter?.notifyDataSetChanged()
                }

                //binding.tvMediaInfo.text = rootMediaItem?.mediaMetadata?.title


            }, ContextCompat.getMainExecutor(this))


        }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun getChildList(parent: MediaItem) {
        val browser = this.mediaBrowser ?: return

        val childrenFuture = browser.getChildren(parent.mediaId, 0, 100, null)
        childrenFuture.addListener({

            val result: LibraryResult<ImmutableList<MediaItem>> = childrenFuture.get()
            if (result.value != null) {
                list.clear()
                list.addAll(result.value!!)
                rvAdapter?.notifyDataSetChanged()
            } else {
                Log.d(TAG, "getChildList: result.value is null")
            }
        }, ContextCompat.getMainExecutor(this))
    }

}