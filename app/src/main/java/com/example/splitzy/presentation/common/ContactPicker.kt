package com.example.splitzy.presentation.common

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// Opens the system contact picker and hands back the chosen email address.
// The picker itself lives outside the app, so Splitzy only ever sees the one
// contact the user deliberately selected — never the whole address book.
@Composable
fun rememberContactEmailPicker(onEmailPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current

    val pickContact = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
        context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(0)?.takeIf { it.isNotBlank() }?.let(onEmailPicked)
            }
        }
    }

    val pickIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI)

    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pickContact.launch(pickIntent)
    }

    return {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) pickContact.launch(pickIntent) else requestPermission.launch(Manifest.permission.READ_CONTACTS)
    }
}
