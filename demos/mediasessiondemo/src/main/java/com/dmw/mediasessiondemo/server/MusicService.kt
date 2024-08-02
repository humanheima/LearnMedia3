package com.dmw.mediasessiondemo.server

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * Created by p_dmweidu on 2024/8/1
 * Desc:
 */
class MusicService : MediaLibraryService() {


    companion object {
        private const val TAG = "MusicService"
    }

    private var exoPlayer: ExoPlayer? = null
    private var mSession: MediaLibrarySession? = null


    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()

        exoPlayer?.addListener(object : Player.Listener {


            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d(TAG, "onPlaybackStateChanged: playbackState = $playbackState")
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                Log.d(TAG, "onPlayWhenReadyChanged: playWhenReady = $playWhenReady")
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.d(TAG, "onPlayerError: error = $error")
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                Log.d(TAG, "onPlayerErrorChanged: error = $error")
            }

        })

        mSession = MediaLibrarySession.Builder(
            this,
            exoPlayer!!,
            object : MediaLibrarySession.Callback {
                override fun onGetLibraryRoot(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    params: LibraryParams?
                ): ListenableFuture<LibraryResult<MediaItem>> {
                    //return super.onGetLibraryRoot(session, browser, params)
                    val rootItem = MediaItemTree.getRootItem()
                    Log.d(TAG, "onGetLibraryRoot: rootItem = $rootItem")

                    return Futures.immediateFuture(
                        LibraryResult.ofItem(
                            rootItem,
                            params
                        )
                    )
                }


                override fun onGetChildren(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    parentId: String,
                    page: Int,
                    pageSize: Int,
                    params: LibraryParams?
                ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                    val children = MediaItemTree.getChildren(parentId)
                    if (children.isNotEmpty()) {
                        return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
                    }
                    return super.onGetChildren(session, browser, parentId, page, pageSize, params)
                }

                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): MediaSession.ConnectionResult {
                    // warning：2024/8/2:
                    Log.d(
                        TAG,
                        "来自客户端的连接请求 onConnect: session = $session, controller = $controller"
                    )
                    return super.onConnect(session, controller)
                }

                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<List<MediaItem>> {
                    Log.d(TAG, "onAddMediaItems: ")
                    mediaItems.forEach {
                        Log.d(TAG, "onAddMediaItems: mediaItem = ${it.mediaMetadata.title}")
                    }

                    return Futures.immediateFuture(resolveMediaItems(mediaItems))
                }

                @UnstableApi
                override fun onSetMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>,
                    startIndex: Int,
                    startPositionMs: Long
                ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                    Log.d(TAG, "onSetMediaItems: mediaItems = ${mediaItems.size}")

                    if (mediaItems.size == 1) {
                        // Try to expand a single item to a playlist.
                        maybeExpandSingleItemToPlaylist(
                            mediaItems.first(),
                            startIndex,
                            startPositionMs
                        )?.also {
                            return Futures.immediateFuture(it)
                        }
                    }
                    return Futures.immediateFuture(
                        MediaSession.MediaItemsWithStartPosition(
                            resolveMediaItems(mediaItems),
                            startIndex,
                            startPositionMs
                        )
                    )

                }
            })
            .build()

        MediaItemTree.initialize(this.assets)

    }

    private fun resolveMediaItems(mediaItems: List<MediaItem>): List<MediaItem> {
        val playlist = mutableListOf<MediaItem>()
        mediaItems.forEach { mediaItem ->
            if (mediaItem.mediaId.isNotEmpty()) {
                MediaItemTree.expandItem(mediaItem)?.let { playlist.add(it) }
            } else if (mediaItem.requestMetadata.searchQuery != null) {
                playlist.addAll(MediaItemTree.search(mediaItem.requestMetadata.searchQuery!!))
            }
        }
        return playlist
    }

    @OptIn(UnstableApi::class) // MediaSession.MediaItemsWithStartPosition
    private fun maybeExpandSingleItemToPlaylist(
        mediaItem: MediaItem,
        startIndex: Int,
        startPositionMs: Long,
    ): MediaSession.MediaItemsWithStartPosition? {
        var playlist = listOf<MediaItem>()
        var indexInPlaylist = startIndex
        MediaItemTree.getItem(mediaItem.mediaId)?.apply {
            if (mediaMetadata.isBrowsable == true) {
                // Get children browsable item.
                playlist = MediaItemTree.getChildren(mediaId)
            } else if (requestMetadata.searchQuery == null) {
                // Try to get the parent and its children.
                MediaItemTree.getParentId(mediaId)?.let {
                    playlist =
                        MediaItemTree.getChildren(it).map { mediaItem ->
                            if (mediaItem.mediaId == mediaId) MediaItemTree.expandItem(mediaItem)!! else mediaItem
                        }
                    indexInPlaylist = MediaItemTree.getIndexInMediaItems(mediaId, playlist)
                }
            }
        }
        if (playlist.isNotEmpty()) {
            return MediaSession.MediaItemsWithStartPosition(
                playlist,
                indexInPlaylist,
                startPositionMs
            )
        }
        return null
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mSession
    }

}