package com.example.splitzy.presentation.common

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// Picks any contact and hands back their name. Deliberately not filtered to
// contacts that have an email — most people in a phonebook don't, and a member
// here is just a label to split against, so a name is enough.
//
// The picker UI runs outside the app, so Splitzy only ever receives the single
// contact the user chose, never the address book.
@Composable
fun rememberContactPicker(onContactPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current

    val pickContact = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        val contactUri = uri ?: return@rememberLauncherForActivityResult
        context.contentResolver.query(
            contactUri,
            arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(0)?.takeIf { it.isNotBlank() }?.let(onContactPicked)
            }
        }
    }

    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) pickContact.launch(null) }

    return {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) pickContact.launch(null) else requestPermission.launch(Manifest.permission.READ_CONTACTS)
    }
}
