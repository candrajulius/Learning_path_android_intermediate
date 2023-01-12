package com.candra.latihanstackwidget

import android.content.Intent
import android.widget.RemoteViewsService

class StackWidgetService: RemoteViewsService()
{
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory =  StackRemoteFactory(this.applicationContext)
}