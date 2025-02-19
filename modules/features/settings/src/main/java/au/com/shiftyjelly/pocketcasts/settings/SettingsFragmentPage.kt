package au.com.shiftyjelly.pocketcasts.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import au.com.shiftyjelly.pocketcasts.compose.AppThemeWithBackground
import au.com.shiftyjelly.pocketcasts.compose.bars.ThemedTopAppBar
import au.com.shiftyjelly.pocketcasts.compose.components.GradientIconData
import au.com.shiftyjelly.pocketcasts.compose.components.SettingRow
import au.com.shiftyjelly.pocketcasts.compose.preview.ThemePreviewParameterProvider
import au.com.shiftyjelly.pocketcasts.compose.theme
import au.com.shiftyjelly.pocketcasts.models.to.SignInState
import au.com.shiftyjelly.pocketcasts.settings.about.AboutFragment
import au.com.shiftyjelly.pocketcasts.settings.privacy.PrivacyFragment
import au.com.shiftyjelly.pocketcasts.ui.theme.Theme
import au.com.shiftyjelly.pocketcasts.views.fragments.BatteryRestrictionsSettingsFragment
import au.com.shiftyjelly.pocketcasts.images.R as IR
import au.com.shiftyjelly.pocketcasts.localization.R as LR
import au.com.shiftyjelly.pocketcasts.settings.R as SR

@Composable
fun SettingsFragmentPage(
    signInState: SignInState,
    isDebug: Boolean,
    isUnrestrictedBattery: Boolean,
    onBackPressed: () -> Unit,
    openFragment: (Fragment) -> Unit
) {
    Column {
        ThemedTopAppBar(
            title = stringResource(LR.string.settings),
            bottomShadow = true,
            onNavigationClick = onBackPressed,
        )

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.theme.colors.primaryUi02)
                .padding(vertical = 8.dp)
        ) {
            if (isDebug) {
                DeveloperRow(onClick = { openFragment(DeveloperFragment()) })
            }

            if (!isUnrestrictedBattery && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                BatteryOptimizationRow(onClick = { openFragment(BatteryRestrictionsSettingsFragment.newInstance(closeButton = false)) })
            }

            if (!signInState.isSignedIn || signInState.isSignedInAsFree) {
                PlusRow(onClick = { openFragment(PlusSettingsFragment()) })
            }

            GeneralRow(onClick = { openFragment(PlaybackSettingsFragment()) })
            NotificationRow(onClick = { openFragment(NotificationsSettingsFragment()) })
            AppearanceRow(
                isSignedInAsPlus = signInState.isSignedInAsPlus,
                onClick = { openFragment(AppearanceSettingsFragment.newInstance()) }
            )
            StorageAndDataUseRow(onClick = { openFragment(StorageSettingsFragment()) })
            AutoArchiveRow(onClick = { openFragment(AutoArchiveFragment()) })
            AutoDownloadRow(onClick = { openFragment(AutoDownloadSettingsFragment.newInstance()) })
            AutoAddToUpNextRow(onClick = { openFragment(AutoAddSettingsFragment()) })
            HelpAndFeedbackRow(onClick = { openFragment(HelpFragment()) })
            ImportAndExportOpmlRow(onClick = { openFragment(ExportSettingsFragment()) })
            PrivacyRow(onClick = { openFragment(PrivacyFragment()) })
            AboutRow(onClick = { openFragment(AboutFragment()) })
        }
    }
}

@Composable
private fun DeveloperRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_developer),
        icon = GradientIconData(
            res = SR.drawable.ic_developer_mode,
            colors = listOf(
                MaterialTheme.theme.colors.gradient03A,
                MaterialTheme.theme.colors.gradient03E,
            )
        ),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun BatteryOptimizationRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_battery_settings),
        icon = GradientIconData(
            res = SR.drawable.ic_baseline_warning_amber_24,
            colors = listOf(
                MaterialTheme.theme.colors.gradient03A,
                MaterialTheme.theme.colors.gradient03E
            )
        ),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun PlusRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.pocket_casts_plus),
        icon = GradientIconData(
            res = IR.drawable.ic_plus,
            colors = listOf(
                MaterialTheme.theme.colors.gradient01A,
                MaterialTheme.theme.colors.gradient01E,
            )
        ),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun GeneralRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_playback),
        icon = GradientIconData(IR.drawable.ic_profile_settings),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun NotificationRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_notifications),
        icon = GradientIconData(SR.drawable.settings_notifications),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun AppearanceRow(isSignedInAsPlus: Boolean, onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_appearance),
        icon = GradientIconData(SR.drawable.settings_appearance),
        primaryTextEndDrawable = if (isSignedInAsPlus) null else IR.drawable.ic_plus,
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun StorageAndDataUseRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_storage),
        icon = GradientIconData(SR.drawable.settings_storage),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun AutoArchiveRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_auto_archive),
        icon = GradientIconData(SR.drawable.settings_auto_archive),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun AutoDownloadRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_auto_download),
        icon = GradientIconData(SR.drawable.settings_auto_download),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun AutoAddToUpNextRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_auto_add_to_up_next),
        icon = GradientIconData(IR.drawable.ic_upnext_playlast),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun HelpAndFeedbackRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_help),
        icon = GradientIconData(SR.drawable.settings_help),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun ImportAndExportOpmlRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_import_export),
        icon = GradientIconData(SR.drawable.settings_import_export),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun PrivacyRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_privacy),
        icon = GradientIconData(SR.drawable.whatsnew_privacy),
        modifier = rowModifier(onClick)
    )
}

@Composable
private fun AboutRow(onClick: () -> Unit) {
    SettingRow(
        primaryText = stringResource(LR.string.settings_title_about),
        icon = GradientIconData(SR.drawable.settings_about),
        modifier = rowModifier(onClick)
    )
}

private fun rowModifier(onClick: () -> Unit) =
    Modifier
        .clickable { onClick() }
        .padding(vertical = 6.dp)

@Preview
@Composable
private fun SettingsPagePreview(@PreviewParameter(ThemePreviewParameterProvider::class) themeType: Theme.ThemeType) {
    AppThemeWithBackground(themeType) {
        SettingsFragmentPage(
            signInState = SignInState.SignedOut(),
            isDebug = true,
            isUnrestrictedBattery = false,
            onBackPressed = {},
            openFragment = {}
        )
    }
}
