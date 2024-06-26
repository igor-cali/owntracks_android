package org.owntracks.android.ui.mixins

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import org.owntracks.android.preferences.Preferences
import org.owntracks.android.ui.NotificationsStash

interface NotificationsPermissionRequested {
  class Impl : NotificationsPermissionRequested {

    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var context: AppCompatActivity
    private lateinit var preferences: Preferences

    override fun postNotificationsPermissionInit(
        context: AppCompatActivity,
        preferences: Preferences,
        notificationsStash: NotificationsStash
    ) {
      this.context = context
      this.preferences = preferences
      launcher =
          context.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            preferences.userDeclinedEnableNotificationPermissions = !it
            if (it && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
              notificationsStash.showAll(NotificationManagerCompat.from(context))
            }
          }
    }

    override fun requestNotificationsPermission() {
      if (!NotificationManagerCompat.from(context).areNotificationsEnabled() &&
          !preferences.userDeclinedEnableNotificationPermissions &&
          Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  fun postNotificationsPermissionInit(
      context: AppCompatActivity,
      preferences: Preferences,
      notificationsStash: NotificationsStash
  )

  fun requestNotificationsPermission()
}
