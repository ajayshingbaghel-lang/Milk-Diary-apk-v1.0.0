package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AuditLogEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Milk Diary", appName)
  }

  @Test
  fun `test milk auto calculation logic`() {
    val totalLiters = 74.5
    val rate = 60.0
    val milkCost = totalLiters * rate
    val goodsCost = 650.0 // Desi Ghee
    val advance = 1000.0 // Cash Advance
    // Formula: doodh ki kimat - advance - saman ke paise = total
    val netPayable = milkCost - advance - goodsCost

    assertEquals(4470.0, milkCost, 0.01)
    assertEquals(2820.0, netPayable, 0.01)
  }

  @Test
  fun `test audit log transparency entry`() {
    val audit = AuditLogEntry(
      accountId = 1L,
      accountName = "Ramesh Gwalla",
      targetType = "MONTHLY_RATE",
      dateOrPeriod = "2026-10",
      title = "दूध का भाव बदला गया",
      oldValue = "पुराना भाव: ₹60.00 / L",
      newValue = "नया भाव: ₹65.00 / L",
      differenceText = "+₹5.00/L",
      formattedTime = "29 Oct 2026, 08:30 PM",
      reason = "मासिक भाव संशोधन"
    )

    assertEquals("MONTHLY_RATE", audit.targetType)
    assertTrue(audit.oldValue.contains("60.00"))
    assertTrue(audit.newValue.contains("65.00"))
    assertEquals("+₹5.00/L", audit.differenceText)
  }

  @Test
  fun `test app version and founder metadata`() {
    val version = BuildConfig.VERSION_NAME
    val code = BuildConfig.VERSION_CODE
    assertEquals("2.1.0", version)
    assertEquals(2, code)
  }
}
