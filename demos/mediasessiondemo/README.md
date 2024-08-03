### MediaSessionDemo

### MediaSession 类似客户端服务端架构

这个项目用来学习 MediaSession 的使用。

MediaBrowser从连接服务到向其订阅数据的流程：

```xml
connect → onConnected → subscribe → onChildrenLoaded

```

最终设置播放列表的地方

```
java.lang.Throwable
                                                                       	at com.dmw.mediasessiondemo.server.MusicService$onCreate$2.onSetMediaItems(MusicService.kt:135)
                                                                       	at androidx.media3.session.MediaSessionImpl.onSetMediaItemsOnHandler(MediaSessionImpl.java:751)
                                                                       	at androidx.media3.session.MediaSessionStub.lambda$setMediaItemsWithStartIndex$32(MediaSessionStub.java:1115)
                                                                       	at androidx.media3.session.MediaSessionStub$$ExternalSyntheticLambda71.run(Unknown Source:9)
                                                                       	at androidx.media3.session.MediaSessionStub.lambda$handleMediaItemsWithStartPositionWhenReady$9(MediaSessionStub.java:241)
                                                                       	at androidx.media3.session.MediaSessionStub$$ExternalSyntheticLambda59.run(Unknown Source:4)
                                                                       	at androidx.media3.session.MediaSessionStub.handleSessionTaskWhenReady(MediaSessionStub.java:423)
                                                                       	at androidx.media3.session.MediaSessionStub.lambda$sendSessionResultWhenReady$3(MediaSessionStub.java:183)
                                                                       	at androidx.media3.session.MediaSessionStub$$ExternalSyntheticLambda39.run(Unknown Source:2)
                                                                       	at androidx.media3.session.MediaSessionStub.lambda$queueSessionTaskWithPlayerCommandForControllerInfo$13(MediaSessionStub.java:342)
                                                                       	at androidx.media3.session.MediaSessionStub$$ExternalSyntheticLambda57.run(Unknown Source:8)
                                                                       	at androidx.media3.session.ConnectedControllersManager.lambda$flushCommandQueue$3$androidx-media3-session-ConnectedControllersManager(ConnectedControllersManager.java:295)
                                                                       	at androidx.media3.session.ConnectedControllersManager$$ExternalSyntheticLambda1.run(Unknown Source:10)
                                                                       	at androidx.media3.session.MediaSessionImpl.lambda$callWithControllerForCurrentRequestSet$3$androidx-media3-session-MediaSessionImpl(MediaSessionImpl.java:344)
                                                                       	at androidx.media3.session.MediaSessionImpl$$ExternalSyntheticLambda22.run(Unknown Source:6)
                                                                       	at androidx.media3.common.util.Util.postOrRun(Util.java:794)
                                                                       	at androidx.media3.session.ConnectedControllersManager.flushCommandQueue(ConnectedControllersManager.java:289)
                                                                       	at androidx.media3.session.ConnectedControllersManager.flushCommandQueue(ConnectedControllersManager.java:270)
                                                                       	at androidx.media3.session.MediaSessionStub.lambda$flushCommandQueue$64$androidx-media3-session-MediaSessionStub(MediaSessionStub.java:1682)
                                                                       	at androidx.media3.session.MediaSessionStub$$ExternalSyntheticLambda41.run(Unknown Source:4)
                                                                       	at androidx.media3.common.util.Util.postOrRun(Util.java:794)
                                                                       	at androidx.media3.session.MediaSessionStub.flushCommandQueue(MediaSessionStub.java:1680)
                                                                       	at androidx.media3.session.MediaControllerImplBase$FlushCommandQueueHandler.flushCommandQueue(MediaControllerImplBase.java:3405)
                                                                       	at androidx.media3.session.MediaControllerImplBase$FlushCommandQueueHandler.handleMessage(MediaControllerImplBase.java:3398)
                                                                       	at androidx.media3.session.MediaControllerImplBase$FlushCommandQueueHandler.$r8$lambda$NOnasKvWwPWqpbhPrsEnWR2iDWA(Unknown Source:0)
                                                                       	at androidx.media3.session.MediaControllerImplBase$FlushCommandQueueHandler$$ExternalSyntheticLambda0.handleMessage(Unknown Source:2)
                                                                       	at android.os.Handler.dispatchMessage(Handler.java:102)
                                                                       	at android.os.Looper.loopOnce(Looper.java:201)
                                                                       	at android.os.Looper.loop(Looper.java:288)
                                                                       	at android.app.ActivityThread.main(ActivityThread.java:7918)
                                                                       	at java.lang.reflect.Method.invoke(Native Method)
                                                                       	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:548)
                                                                       	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)

```



参考链接：

* [](https://developer.android.com/media/media3/session/control-playback?hl=zh-cn)


