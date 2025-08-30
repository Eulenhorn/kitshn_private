package de.kitshn.ui.component.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kitshn.composeapp.generated.resources.Res
import kitshn.composeapp.generated.resources.action_back
import kitshn.composeapp.generated.resources.action_close
import org.jetbrains.compose.resources.stringResource

enum class BackButtonType {
    DEFAULT,
    CLOSE
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BackButton(
    onBack: (() -> Unit)?,
    overlay: Boolean = false,
    type: BackButtonType = BackButtonType.DEFAULT,
    modifier: Modifier = Modifier
) {
    if(onBack == null) return

    @Composable
    fun Icon() {
        when(type) {
            BackButtonType.DEFAULT ->
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(Res.string.action_back))

            BackButtonType.CLOSE ->
                Icon(Icons.Rounded.Close, stringResource(Res.string.action_close))
        }
    }

    if(overlay) {
        FilledIconButton(
            modifier = modifier,
            onClick = onBack,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Icon()
        }
        return
    }

    FilledIconButton(
        modifier = modifier,
        onClick = onBack,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Icon()
    }
}