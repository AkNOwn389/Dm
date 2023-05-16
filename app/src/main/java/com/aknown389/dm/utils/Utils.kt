package com.aknown389.dm.utils

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import java.time.format.DateTimeParseException
import java.time.*
import java.time.format.DateTimeFormatter

fun View.snackbar(message: String){
    Snackbar.make(this,
        message,
        Snackbar.LENGTH_LONG).also {
            snackbar ->
        snackbar.setAction("Ok"){
            snackbar.dismiss()
        }
    }.show()
}

fun ContentResolver.getFileName(selectedImage: Uri): String {
    var name  = ""
    val returnCursor = this.query(selectedImage, null, null, null, null)
    if (returnCursor != null){
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }

    return name
}



@RequiresApi(Build.VERSION_CODES.O)
fun getStringTime(date: String): String {
    val parsedDate = try {
        ZonedDateTime.parse(date)
    } catch (e: DateTimeParseException) {
        return date
    }
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val diff = Duration.between(parsedDate, now)
    val days = diff.toDays()
    return when {
        days >= 365 -> {
            val years = days / 365
            if (years > 1) "$years years ago" else "$years year ago"
        }
        days >= 30 -> {
            val months = days / 30
            if (months == 1L) "${months}m" else parsedDate.format(DateTimeFormatter.ofPattern("MMM dd"))
        }
        days >= 7 -> {
            val weeks = days / 7
            if (weeks == 1L) "${weeks}w" else parsedDate.format(DateTimeFormatter.ofPattern("MMM dd"))
        }
        days > 0 -> "${days}d"
        diff.toHours() > 0 -> "${diff.toHours()}h"
        diff.toMinutes() > 0 -> "${diff.toMinutes()}m"
        diff.seconds in 16..59 -> "${diff.seconds}s"
        else -> "just now"
    }
}