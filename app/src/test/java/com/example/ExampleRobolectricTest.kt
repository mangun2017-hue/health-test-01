package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.DailyRecordRepository
import com.example.ui.DailyCheckScreen
import com.example.ui.DailyCheckUiState
import com.example.ui.DailyCheckViewModel
import com.example.ui.theme.SeniorHealthTheme
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `app name matches senior check title`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("약·물 체크", appName)
    }

    @Test
    fun `room repository persists daily medication and water intake`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val repo = DailyRecordRepository(db.dailyRecordDao())
            val dateKey = "2026-11-16"

            repo.toggleMorning(dateKey)
            repo.toggleDinner(dateKey)
            repo.incrementWater(dateKey)
            repo.incrementWater(dateKey)

            val saved = repo.observeRecord(dateKey).first()
            assertTrue(saved.morningTaken)
            assertFalse(saved.lunchTaken)
            assertTrue(saved.dinnerTaken)
            assertEquals(2, saved.waterCups)
        } finally {
            db.close()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `crossing midnight resets medications and water count and clears yesterday record`() = runBlocking {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        try {
            val dao = db.dailyRecordDao()
            val repo = DailyRecordRepository(dao)

            var simulatedNow = Calendar.getInstance().apply {
                set(2026, Calendar.NOVEMBER, 16, 23, 59, 58)
                set(Calendar.MILLISECOND, 0)
            }.time

            val viewModel = DailyCheckViewModel(
                repository = repo,
                currentDateProvider = { simulatedNow },
                enableMidnightTicker = false,
            )

            // Wait for initial Ready state on 2026-11-16
            val day1Initial = viewModel.uiState.first { it is DailyCheckUiState.Ready } as DailyCheckUiState.Ready
            assertEquals("2026-11-16", day1Initial.dateKey)

            viewModel.toggleMorning()
            viewModel.toggleLunch()
            viewModel.toggleDinner()
            viewModel.addWaterCup()
            viewModel.addWaterCup()
            viewModel.addWaterCup()

            val day1Updated = viewModel.uiState.first {
                it is DailyCheckUiState.Ready && it.waterCups == 3
            } as DailyCheckUiState.Ready
            assertTrue(day1Updated.morningTaken)
            assertTrue(day1Updated.lunchTaken)
            assertTrue(day1Updated.dinnerTaken)
            assertEquals(3, day1Updated.waterCups)

            // Simulate crossing midnight into 2026-11-17 00:00:01
            simulatedNow = Calendar.getInstance().apply {
                set(2026, Calendar.NOVEMBER, 17, 0, 0, 1)
                set(Calendar.MILLISECOND, 0)
            }.time

            assertTrue(viewModel.refreshDateIfNeeded())

            val day2State = viewModel.uiState.first {
                it is DailyCheckUiState.Ready && it.dateKey == "2026-11-17"
            } as DailyCheckUiState.Ready

            assertFalse(day2State.morningTaken)
            assertFalse(day2State.lunchTaken)
            assertFalse(day2State.dinnerTaken)
            assertEquals(0, day2State.waterCups)
            assertEquals("2026년 11월 17일", day2State.formattedDate)

            // Yesterday's DB row should be cleared automatically
            assertNull(dao.getRecord("2026-11-16"))
        } finally {
            db.close()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `calculateMillisUntilNextMidnight returns exact delay to midnight`() {
        val nearMidnight: Date = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 16, 23, 59, 50)
            set(Calendar.MILLISECOND, 0)
        }.time

        val delayMillis = DailyCheckViewModel.calculateMillisUntilNextMidnight(nearMidnight)
        assertEquals(10_050L, delayMillis)
    }

    @Test
    fun `daily check screen displays date, meds, and water controls`() {
        var morningClicked = false
        var waterClicked = false

        composeTestRule.setContent {
            SeniorHealthTheme {
                DailyCheckScreen(
                    uiState = DailyCheckUiState.Ready(
                        dateKey = "2026-11-16",
                        formattedDate = "2026년 11월 16일",
                        dayOfWeek = "월요일",
                        morningTaken = true,
                        lunchTaken = false,
                        dinnerTaken = false,
                        waterCups = 3,
                    ),
                    onToggleMorning = { morningClicked = true },
                    onToggleLunch = {},
                    onToggleDinner = {},
                    onAddWaterCup = { waterClicked = true },
                    onRemoveWaterCup = {},
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("2026년 11월 16일").assertIsDisplayed()
        composeTestRule.onNodeWithText("아침 약").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("점심 약").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("저녁 약").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("복용 완료").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("오늘도 건강 챙기기 성공!").assertDoesNotExist()
        composeTestRule.onNodeWithTag("water_cup_count_text").performScrollTo().assertTextContains("3 잔")

        composeTestRule.onNodeWithTag("med_morning_button").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("add_water_button").performScrollTo().performClick()

        assertTrue(morningClicked)
        assertTrue(waterClicked)
    }

    @Test
    fun `celebration message displays prominently when all three medications are taken`() {
        composeTestRule.setContent {
            SeniorHealthTheme {
                DailyCheckScreen(
                    uiState = DailyCheckUiState.Ready(
                        dateKey = "2026-11-16",
                        formattedDate = "2026년 11월 16일",
                        dayOfWeek = "월요일",
                        morningTaken = true,
                        lunchTaken = true,
                        dinnerTaken = true,
                        waterCups = 4,
                    ),
                    onToggleMorning = {},
                    onToggleLunch = {},
                    onToggleDinner = {},
                    onAddWaterCup = {},
                    onRemoveWaterCup = {},
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("all_meds_celebration_banner").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("오늘도 건강 챙기기 성공!").performScrollTo().assertIsDisplayed()
    }
}
