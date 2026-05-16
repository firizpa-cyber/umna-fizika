package uz.fizika.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NeonDarkColorScheme = darkColorScheme(
    primary              = NeonColors.Primary,
    onPrimary            = NeonColors.OnPrimary,
    primaryContainer     = NeonColors.PrimaryContainer,
    onPrimaryContainer   = NeonColors.OnPrimaryContainer,
    secondary            = NeonColors.Secondary,
    onSecondary          = NeonColors.OnSecondary,
    secondaryContainer   = NeonColors.SecondaryContainer,
    onSecondaryContainer = NeonColors.OnSecondaryContainer,
    tertiary             = NeonColors.Tertiary,
    onTertiary           = NeonColors.OnTertiary,
    tertiaryContainer    = NeonColors.TertiaryContainer,
    error                = NeonColors.Error,
    errorContainer       = NeonColors.ErrorContainer,
    onError              = NeonColors.OnError,
    background           = NeonColors.Background,
    onBackground         = NeonColors.OnBackground,
    surface              = NeonColors.Surface,
    onSurface            = NeonColors.OnSurface,
    surfaceVariant       = NeonColors.SurfaceVariant,
    onSurfaceVariant     = NeonColors.OnSurfaceVariant,
    outline              = NeonColors.Outline,
    outlineVariant       = NeonColors.OutlineVariant,
    surfaceContainer     = NeonColors.SurfaceContainer,
)

@Composable
fun UmnaFizikaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NeonDarkColorScheme,
        typography  = NeonTypography,
        content     = content
    )
}
