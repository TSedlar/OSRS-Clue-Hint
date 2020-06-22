package me.sedlar.osrs_clue_hint

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.obsez.android.lib.filechooser.ChooserDialog
import me.sedlar.osrs_clue_hint.component.ClueDialog
import me.sedlar.osrs_clue_hint.data.*


private const val SCREENSHOT_KEY = "screenshot_dir"
private const val DEFAULT_SCREENSHOT_DIR = "/storage/emulated/0/Pictures/Screenshot"

class MainActivity : AppCompatActivity() {

    private val channelId = "os-clue-hint"
    private val channelDescription = "OSRS Clue Hints"

    companion object {

        internal var mainActivityContext: Context? = null
        internal var fragmentSupportManager: FragmentManager? = null

        internal var solutionDialog: ClueDialog? = null

        internal val anagramData = ArrayList<AnagramData>()
        internal val cipherData = ArrayList<CipherData>()
        internal val coordData = ArrayList<CoordData>()
        internal val crypticData = ArrayList<CrypticData>()
        internal val mapData = ArrayList<MapData>()

        internal fun requestFilePermissions(activity: Activity) {
            // Request write permissions to delete RS screenshot

            val permissionCheck =
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    9999
                )
            }
        }

        internal fun requestDrawOverlayPermission(context: Context) {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)) {   //Android M Or Over
                val intent =
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.packageName))
                context.startActivity(intent)
                return
            }
        }
    }

    init {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        fragmentSupportManager = this.supportFragmentManager

        mainActivityContext = this

//        requestFilePermissions(this) // TODO: add screenshot recognition
        requestDrawOverlayPermission(this)

        Thread {
            if (anagramData.isEmpty()) {
                anagramData.addAll(ClueDataParser.parseAnagramData())
            }

            if (cipherData.isEmpty()) {
                cipherData.addAll(ClueDataParser.parseCipherData())
            }

            if (coordData.isEmpty()) {
                coordData.addAll(ClueDataParser.parseCoordData())
            }

            if (crypticData.isEmpty()) {
                crypticData.addAll(ClueDataParser.parseCrypticData())
            }

            if (mapData.isEmpty()) {
                mapData.addAll(ClueDataParser.parseMapData())
            }
        }.start()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelId, importance).apply {
                description = channelDescription
            }
            // Register the channel with the system
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun onCreateService(view: View?) {
        val intentAction = Intent(applicationContext, ActionReceiver::class.java)

        intentAction.putExtra("action", ACTION_SHOW_SOLUTIONS)

//        chooseScreenshotDir()
//        val prefs = getPreferences(Context.MODE_PRIVATE)
//        intentAction.putExtra("picdir", prefs.getString(SCREENSHOT_KEY, DEFAULT_SCREENSHOT_DIR))

        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle("Clue Scroll Solver")
            .setContentText("Click here for solutions")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder)
        }
    }

    fun onDestroyService(view: View?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

    @SuppressLint("ApplySharedPref")
    private fun chooseScreenshotDir() {
        val prefs = getPreferences(Context.MODE_PRIVATE)
        if (!prefs.contains(SCREENSHOT_KEY)) {
            Toast.makeText(
                this@MainActivity,
                "Choose the screenshot directory",
                Toast.LENGTH_SHORT
            ).show()
            ChooserDialog(this@MainActivity)
                .withFilter(true, false)
                .withDateFormat("HH:mm") // see also SimpleDateFormat format specifiers
                .withChosenListener { path, _ ->
                    prefs.edit().putString(SCREENSHOT_KEY, path).commit()
                    Toast.makeText(
                        this@MainActivity,
                        "FOLDER: $path",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .build()
                .show()
        }
    }
}