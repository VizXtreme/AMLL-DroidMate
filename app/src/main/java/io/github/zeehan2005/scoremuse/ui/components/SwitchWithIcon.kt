package io.github.zeehan2005.scoremuse.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics

/**
 * A settings-style switch that shows a small check/cross icon near the thumb to match Android Settings appearance.
 * It composes the regular Material3 Switch and overlays a small circular icon at the start/end depending on checked state.
 */
@Composable
fun SwitchWithIcon(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.colors(),
) {
    // Use Material3 native thumbContent to show icons inside the switch thumb. This matches the
    // official M3 example and avoids overlay hacks.
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = colors,
        modifier = modifier.semantics { },
        thumbContent = {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    )
}