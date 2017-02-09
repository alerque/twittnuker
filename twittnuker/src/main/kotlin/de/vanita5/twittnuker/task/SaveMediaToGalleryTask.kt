/*
 *  Twittnuker - Twitter client for Android
 *
 *  Copyright (C) 2013-2017 vanita5 <mail@vanit.as>
 *
 *  This program incorporates a modified version of Twidere.
 *  Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.vanita5.twittnuker.task

import android.app.Activity
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.widget.Toast

import de.vanita5.twittnuker.R
import de.vanita5.twittnuker.annotation.CacheFileType
import de.vanita5.twittnuker.provider.CacheProvider

import java.io.File

class SaveMediaToGalleryTask(
        activity: Activity,
        source: Uri,
        destination: File,
        type: String
) : ProgressSaveFileTask(activity, source, destination, CacheProvider.CacheFileTypeCallback(activity, type)) {

    override fun onFileSaved(savedFile: File, mimeType: String?) {
        val context = context ?: return
        MediaScannerConnection.scanFile(context, arrayOf(savedFile.path),
                arrayOf(mimeType), null)
        Toast.makeText(context, R.string.message_toast_saved_to_gallery, Toast.LENGTH_SHORT).show()
    }

    override fun onFileSaveFailed() {
        val context = context ?: return
        Toast.makeText(context, R.string.message_toast_error_occurred, Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun create(activity: Activity, source: Uri, @CacheFileType type: String): SaveFileTask {
            val pubDir: File
            when (type) {
                CacheFileType.VIDEO -> {
                    pubDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                }
                CacheFileType.IMAGE -> {
                    pubDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                }
                else -> {
                    pubDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                }
            }
            val saveDir = File(pubDir, "Twittnuker")
            return SaveMediaToGalleryTask(activity, source, saveDir, type)
        }
    }

}