package de.kitshn.ui.view.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import de.kitshn.api.tandoor.TandoorRequestState
import de.kitshn.closeAppHandler
import de.kitshn.launchWebsiteHandler
import de.kitshn.ui.component.buttons.BackButton
import de.kitshn.ui.component.settings.SettingsListItem
import de.kitshn.ui.component.settings.SettingsListItemPosition
import de.kitshn.ui.dialog.version.TandoorServerVersionCompatibilityDialog
import de.kitshn.ui.view.ViewParameters
import kitshn.composeapp.generated.resources.Res
import kitshn.composeapp.generated.resources.action_sign_out
import kitshn.composeapp.generated.resources.action_sign_out_description
import kitshn.composeapp.generated.resources.common_account
import kitshn.composeapp.generated.resources.common_instance_url
import kitshn.composeapp.generated.resources.common_manage_space
import kitshn.composeapp.generated.resources.common_unknown
import kitshn.composeapp.generated.resources.common_version
import kitshn.composeapp.generated.resources.settings_section_server_delete_and_manage_data_description
import kitshn.composeapp.generated.resources.settings_section_server_delete_and_manage_data_dialog_line_1
import kitshn.composeapp.generated.resources.settings_section_server_delete_and_manage_data_dialog_line_2
import kitshn.composeapp.generated.resources.settings_section_server_delete_and_manage_data_dialog_line_3
import kitshn.composeapp.generated.resources.settings_section_server_delete_and_manage_data_label
import kitshn.composeapp.generated.resources.settings_section_server_label
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSettingsServer(
    p: ViewParameters
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()

    val launchWebsiteHandler = launchWebsiteHandler()
    val closeAppHandler = closeAppHandler()

    var showVersionCompatibilityBottomSheet by remember { mutableStateOf(false) }

    var showDataManagementDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TandoorRequestState().wrapRequest {
            val user = p.vm.tandoorClient?.user?.get()
            if(user != null) p.vm.uiState.userDisplayName = user.display_name
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = { BackButton(p.back) },
                title = { Text(stringResource(Res.string.settings_section_server_label)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                SettingsListItem(
                    position = SettingsListItemPosition.TOP,
                    label = { Text(stringResource(Res.string.common_account)) },
                    description = {
                        Text(
                            p.vm.uiState.userDisplayName.ifBlank {
                                stringResource(Res.string.common_unknown)
                            }
                        )
                    },
                    icon = Icons.Rounded.AccountCircle,
                    enabled = p.vm.uiState.userDisplayName.isNotBlank(),
                    contentDescription = stringResource(Res.string.common_account)
                )

                SettingsListItem(
                    position = SettingsListItemPosition.BETWEEN,
                    label = { Text(stringResource(Res.string.common_instance_url)) },
                    description = {
                        Text(
                            p.vm.tandoorClient?.credentials?.instanceUrl
                                ?: stringResource(Res.string.common_unknown)
                        )
                    },
                    icon = Icons.Rounded.Web,
                    contentDescription = stringResource(Res.string.common_instance_url)
                )

                SettingsListItem(
                    position = SettingsListItemPosition.BOTTOM,
                    label = { Text(stringResource(Res.string.common_version)) },
                    description = {
                        Text(
                            p.vm.tandoorClient?.container?.serverSettings?.version
                                ?: stringResource(Res.string.common_unknown)
                        )
                    },
                    icon = Icons.Rounded.Numbers,
                    enabled = p.vm.tandoorClient?.container?.serverSettings?.version != null,
                    contentDescription = stringResource(Res.string.common_version)
                ) {
                    coroutineScope.launch {
                        showVersionCompatibilityBottomSheet = true
                    }
                }

                SettingsListItem(
                    position = SettingsListItemPosition.SINGULAR,
                    label = { Text(stringResource(Res.string.action_sign_out)) },
                    description = { Text(stringResource(Res.string.action_sign_out_description)) },
                    icon = Icons.AutoMirrored.Rounded.Logout,
                    contentDescription = stringResource(Res.string.action_sign_out)
                ) {
                    p.vm.signOut()
                    closeAppHandler()
                }

                // needed for iOS because app gets denied (reason: https://developer.apple.com/app-store/review/guidelines/#data-collection-and-storage)
                SettingsListItem(
                    position = SettingsListItemPosition.SINGULAR,
                    label = { Text(stringResource(Res.string.settings_section_server_delete_and_manage_data_label)) },
                    description = { Text(stringResource(Res.string.settings_section_server_delete_and_manage_data_description)) },
                    icon = Icons.Rounded.Delete,
                    contentDescription = stringResource(Res.string.settings_section_server_delete_and_manage_data_description)
                ) {
                    showDataManagementDialog = true
                }
            }
        }
    }

    TandoorServerVersionCompatibilityDialog(
        vm = p.vm,
        shown = showVersionCompatibilityBottomSheet,
        autoDisplay = false
    ) {
        showVersionCompatibilityBottomSheet = false
    }

    // needed for iOS because app gets denied (reason: https://developer.apple.com/app-store/review/guidelines/#data-collection-and-storage)
    if(showDataManagementDialog) AlertDialog(
        onDismissRequest = {
            showDataManagementDialog = false
        },
        icon = {
            Icon(
                Icons.Rounded.Delete,
                stringResource(Res.string.settings_section_server_delete_and_manage_data_label)
            )
        },
        title = {
            Text(stringResource(Res.string.settings_section_server_delete_and_manage_data_label))
        },
        text = {
            Text(
                buildAnnotatedString {
                    append(stringResource(Res.string.settings_section_server_delete_and_manage_data_dialog_line_1))

                    if(p.vm.tandoorClient?.credentials?.instanceUrl?.contains("app.tandoor.dev") == true) {
                        append("\n\n")
                        append("${stringResource(Res.string.settings_section_server_delete_and_manage_data_dialog_line_2)} ")
                        withStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            withLink(LinkAnnotation.Url("mailto:info@tandoor.dev")) {
                                append("info@tandoor.dev")
                            }
                        }
                        append(" ${stringResource(Res.string.settings_section_server_delete_and_manage_data_dialog_line_3)}")
                    }
                }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val spaceId = p.vm.tandoorClient?.space?.current()?.id ?: -1
                        launchWebsiteHandler.invoke("${p.vm.tandoorClient?.credentials?.instanceUrl}/space-manage/${spaceId}")
                    }
                }
            ) {
                Text(stringResource(Res.string.common_manage_space))
            }
        }
    )
}