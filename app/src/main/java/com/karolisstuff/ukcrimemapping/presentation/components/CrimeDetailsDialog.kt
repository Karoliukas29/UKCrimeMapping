package com.karolisstuff.ukcrimemapping.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.karolisstuff.ukcrimemapping.domain.model.Crime

@Composable
fun CrimeDetailsDialog(crime: Crime, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Crime Details") },
        text = {
            Column {
                Text(text = "Street: ${crime.street}")
                Text(text = "Category: ${crime.category}")
                Text(text = "Outcome: ${crime.outcome}")
                Text(text = "Date: ${crime.date}")
                Text(text = "Latitude: ${crime.latitude}")
                Text(text = "Longitude: ${crime.longitude}")

            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("OK")
            }
        }
    )
}