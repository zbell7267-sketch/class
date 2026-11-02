package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ClassroomSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    val collegeName = context.getString(R.string.college_name)
    assertEquals("GGGC Attendance", appName)
    assertEquals("Government Graduate Girls College", collegeName)
  }

  @Test
  fun `verify classroom qr payload encoding and decoding`() {
    val session = ClassroomSession(
      subjectCode = "CS301",
      subjectName = "Computer Networks",
      classroom = "Hall 304 - CS Wing",
      dateString = "2026-11-02",
      timeSlot = "09:00 AM - 10:00 AM",
      sessionSecret = "SEC77123"
    )

    val payload = session.toQrPayload()
    val decoded = ClassroomSession.fromQrPayload(payload)

    assertNotNull(decoded)
    assertEquals("CS301", decoded?.subjectCode)
    assertEquals("Computer Networks", decoded?.subjectName)
    assertEquals("Hall 304 - CS Wing", decoded?.classroom)
    assertEquals("2026-11-02", decoded?.dateString)
    assertEquals("09:00 AM - 10:00 AM", decoded?.timeSlot)
    assertEquals("SEC77123", decoded?.sessionSecret)
  }
}
