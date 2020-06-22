package me.sedlar.osrs_clue_hint

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import me.sedlar.osrs_clue_hint.component.ClueDialog
import java.io.File


const val ACTION_SOLVE_CLUE = "SOLVE_CLUE"
const val ACTION_SHOW_SOLUTIONS = "SHOW_SOLUTIONS"

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        Toast.makeText(context, "Solving clue..", Toast.LENGTH_LONG).show()
        val action = intent.getStringExtra("action")
        if (action == ACTION_SOLVE_CLUE) {
            val dir = intent.getStringExtra("picdir")
            parseLatestScreenshot(context, dir!!)
        } else if (action == ACTION_SHOW_SOLUTIONS) {
            showSolutionDialog(MainActivity.mainActivityContext!!)
        }
    }

    companion object {

        fun parseLatestScreenshot(context: Context, dir: String) {
            println("Screenshots @ $dir")
            val screenshotDir = File(dir)
            var latestFile: File? = null

            screenshotDir.listFiles()?.forEach { child ->
                val childPath = child.absolutePath
                if (childPath.endsWith(".jpg") || childPath.endsWith(".png")) {
                    if (latestFile == null || child.lastModified() > latestFile!!.lastModified()) {
                        latestFile = child
                    }
                }
            }

            if (latestFile == null) {
                println("  No screenshots found")
            }

            latestFile?.let { screenshot ->
                println("  using: ${screenshot.absolutePath}")
                val bitmap = BitmapFactory.decodeFile(screenshot.absolutePath)

                val isRSImage = ClueSolver.solveClue(context, bitmap)

                if (isRSImage) {
                    context.contentResolver.delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "_data=?", arrayOf<String>(screenshot.absolutePath)
                    )
                }
            }
        }

        fun showSolutionDialog(context: Context) {
            MainActivity.requestDrawOverlayPermission(context)

            if (MainActivity.solutionDialog != null) {
                MainActivity.solutionDialog!!.show()
                return
            }

            MainActivity.solutionDialog = ClueDialog(context)

            MainActivity.solutionDialog!!.window?.let { dialogWindow ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialogWindow.setType(WindowManager.LayoutParams.TYPE_TOAST)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                }
            }

            try {
                MainActivity.solutionDialog!!.show()
            } catch (exception: WindowManager.BadTokenException) {
                // needs to accept permission to overlay
            }
        }
    }
}