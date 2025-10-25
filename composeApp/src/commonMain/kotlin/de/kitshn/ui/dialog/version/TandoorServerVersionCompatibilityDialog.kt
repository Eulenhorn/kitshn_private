package de.kitshn.ui.dialog.version

import androidx.compose.runtime.Composable
import de.kitshn.KitshnViewModel

@Composable
fun TandoorServerVersionCompatibilityDialog(
    vm: KitshnViewModel,
    shown: Boolean = false,
    autoDisplay: Boolean = true,
    onDismiss: () -> Unit = {}
) {
    // Disabled — does nothing intentionally
}
