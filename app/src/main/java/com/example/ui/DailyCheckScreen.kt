package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.WaterCupIcon
import com.example.ui.theme.EveningIconBg
import com.example.ui.theme.EveningIconTint
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.IvoryBackgroundBottom
import com.example.ui.theme.IvoryBorder
import com.example.ui.theme.IvorySurface
import com.example.ui.theme.IvorySurfaceVariant
import com.example.ui.theme.LunchIconBg
import com.example.ui.theme.LunchIconTint
import com.example.ui.theme.MorningIconBg
import com.example.ui.theme.MorningIconTint
import com.example.ui.theme.TakenGreen
import com.example.ui.theme.TakenGreenBorder
import com.example.ui.theme.TakenGreenSurface
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBorder
import com.example.ui.theme.WaterSurface

private const val DAILY_WATER_GOAL_CUPS = 8

@Composable
fun DailyCheckScreen(
    uiState: DailyCheckUiState,
    onToggleMorning: () -> Unit,
    onToggleLunch: () -> Unit,
    onToggleDinner: () -> Unit,
    onAddWaterCup: () -> Unit,
    onRemoveWaterCup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(IvoryBackground, IvoryBackgroundBottom)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.TopCenter,
    ) {
        when (uiState) {
            DailyCheckUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = TakenGreen,
                        strokeWidth = 5.dp,
                        modifier = Modifier.size(56.dp),
                    )
                }
            }

            is DailyCheckUiState.Ready -> {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    DateHeaderCard(
                        formattedDate = uiState.formattedDate,
                        dayOfWeek = uiState.dayOfWeek,
                        takenCount = uiState.takenMedicationCount,
                    )

                    MedicationSection(
                        morningTaken = uiState.morningTaken,
                        lunchTaken = uiState.lunchTaken,
                        dinnerTaken = uiState.dinnerTaken,
                        onToggleMorning = onToggleMorning,
                        onToggleLunch = onToggleLunch,
                        onToggleDinner = onToggleDinner,
                    )

                    WaterIntakeCard(
                        waterCups = uiState.waterCups,
                        onAddWaterCup = onAddWaterCup,
                        onRemoveWaterCup = onRemoveWaterCup,
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun DateHeaderCard(
    formattedDate: String,
    dayOfWeek: String,
    takenCount: Int,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_date_header"),
        shape = RoundedCornerShape(26.dp),
        color = IvorySurfaceVariant,
        border = BorderStroke(1.5.dp, IvoryBorder),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = TakenGreen,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = stringResource(R.string.today_label),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color.White,
                        )
                    }
                }

                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = InkSecondary,
                )
            }

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 42.sp,
                ),
                color = InkPrimary,
            )

            val progressText = if (takenCount == 3) {
                stringResource(R.string.med_progress_all_done)
            } else {
                stringResource(R.string.med_progress_format, 3, takenCount)
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (takenCount == 3) TakenGreenSurface else IvorySurface,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (takenCount == 3) TakenGreen else IvoryBorder,
                ),
            ) {
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = if (takenCount == 3) TakenGreenBorder else InkSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun MedicationSection(
    morningTaken: Boolean,
    lunchTaken: Boolean,
    dinnerTaken: Boolean,
    onToggleMorning: () -> Unit,
    onToggleLunch: () -> Unit,
    onToggleDinner: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text(
            text = stringResource(R.string.med_section_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = InkPrimary,
            modifier = Modifier.padding(start = 4.dp),
        )

        MedicationCheckButton(
            title = stringResource(R.string.med_morning),
            icon = Icons.Rounded.WbTwilight,
            uncheckedIconBg = MorningIconBg,
            uncheckedIconTint = MorningIconTint,
            isTaken = morningTaken,
            onClick = onToggleMorning,
            testTag = "med_morning_button",
        )

        MedicationCheckButton(
            title = stringResource(R.string.med_lunch),
            icon = Icons.Rounded.LightMode,
            uncheckedIconBg = LunchIconBg,
            uncheckedIconTint = LunchIconTint,
            isTaken = lunchTaken,
            onClick = onToggleLunch,
            testTag = "med_lunch_button",
        )

        MedicationCheckButton(
            title = stringResource(R.string.med_dinner),
            icon = Icons.Rounded.NightsStay,
            uncheckedIconBg = EveningIconBg,
            uncheckedIconTint = EveningIconTint,
            isTaken = dinnerTaken,
            onClick = onToggleDinner,
            testTag = "med_dinner_button",
        )

        val allMedsTaken = morningTaken && lunchTaken && dinnerTaken
        AnimatedVisibility(
            visible = allMedsTaken,
            enter = fadeIn() + expandVertically() + scaleIn(initialScale = 0.92f),
            exit = fadeOut() + shrinkVertically(),
        ) {
            MedicationCelebrationBanner()
        }
    }
}

@Composable
private fun MedicationCelebrationBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("all_meds_celebration_banner"),
        shape = RoundedCornerShape(26.dp),
        color = TakenGreenSurface,
        border = BorderStroke(2.5.dp, TakenGreen),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(MorningIconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Celebration,
                    contentDescription = null,
                    tint = TakenGreenBorder,
                    modifier = Modifier.size(40.dp),
                )
            }

            Text(
                text = stringResource(R.string.med_celebration_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 38.sp,
                ),
                color = TakenGreenBorder,
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(R.string.med_celebration_subtitle),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = InkPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MedicationCheckButton(
    title: String,
    icon: ImageVector,
    uncheckedIconBg: Color,
    uncheckedIconTint: Color,
    isTaken: Boolean,
    onClick: () -> Unit,
    testTag: String,
) {
    val haptics = LocalHapticFeedback.current

    val containerColor by animateColorAsState(
        targetValue = if (isTaken) TakenGreen else IvorySurface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_button_bg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isTaken) TakenGreenBorder else IvoryBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_button_border",
    )
    val elevation by animateDpAsState(
        targetValue = if (isTaken) 6.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "med_button_elevation",
    )

    val takenLabel = stringResource(R.string.status_taken)
    val notTakenLabel = stringResource(R.string.status_not_taken)

    Surface(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 104.dp)
            .testTag(testTag)
            .semantics {
                role = Role.Checkbox
                stateDescription = if (isTaken) takenLabel else notTakenLabel
                contentDescription = "$title, ${if (isTaken) takenLabel else notTakenLabel}"
            },
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = BorderStroke(2.dp, borderColor),
        shadowElevation = elevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(
                            if (isTaken) Color.White.copy(alpha = 0.20f) else uncheckedIconBg
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isTaken) Color.White else uncheckedIconTint,
                        modifier = Modifier.size(34.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        color = if (isTaken) Color.White else InkPrimary,
                    )

                    Text(
                        text = if (isTaken) takenLabel else notTakenLabel,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
                        ),
                        color = if (isTaken) Color.White.copy(alpha = 0.95f) else InkSecondary,
                    )
                }
            }

            AnimatedContent(
                targetState = isTaken,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.8f)).togetherWith(fadeOut())
                },
                label = "med_check_icon",
            ) { checked ->
                if (checked) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = takenLabel,
                        tint = Color.White,
                        modifier = Modifier.size(46.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = notTakenLabel,
                        tint = IvoryBorder,
                        modifier = Modifier.size(46.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun WaterIntakeCard(
    waterCups: Int,
    onAddWaterCup: () -> Unit,
    onRemoveWaterCup: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("water_intake_card"),
        shape = RoundedCornerShape(28.dp),
        color = WaterSurface,
        border = BorderStroke(2.dp, WaterBorder),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.water_section_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = InkPrimary,
                    )
                    Text(
                        text = if (waterCups >= DAILY_WATER_GOAL_CUPS) {
                            stringResource(R.string.water_goal_reached)
                        } else {
                            stringResource(R.string.water_goal_hint)
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = WaterBlue,
                    )
                }

                AnimatedContent(
                    targetState = waterCups,
                    label = "water_count_animation",
                ) { cups ->
                    Text(
                        text = stringResource(R.string.water_cups_format, cups),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        color = WaterBlue,
                        modifier = Modifier.testTag("water_cup_count_text"),
                    )
                }
            }

            // 8 visual cups arranged cleanly in two 4-cup rows for clear senior visibility
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                for (rowIndex in 0 until 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        for (colIndex in 0 until 4) {
                            val cupNumber = rowIndex * 4 + colIndex + 1
                            val isFilled = waterCups >= cupNumber
                            val desc = if (isFilled) {
                                stringResource(R.string.water_cup_filled_desc, cupNumber)
                            } else {
                                stringResource(R.string.water_cup_empty_desc, cupNumber)
                            }
                            WaterCupIcon(
                                isFilled = isFilled,
                                description = desc,
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddWaterCup()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 84.dp)
                    .testTag("add_water_button"),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WaterBlue,
                    contentColor = Color.White,
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.water_add_button),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
            }

            if (waterCups > 0) {
                OutlinedButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onRemoveWaterCup()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                        .testTag("undo_water_button"),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.5.dp, WaterBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.8f),
                        contentColor = WaterBlue,
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.water_undo_button),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }
        }
    }
}
