package com.example

import com.example.data.model.ClassroomSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify classroom qr payload serialization and parsing`() {
    val session = ClassroomSession(
      subjectCode = "CS301",
      subjectName = "Computer Networks",
      classroom = "Hall 304 - CS Wing",
      dateString = "2026-11-02",
      timeSlot = "09:00 AM - 10:00 AM",
      sessionSecret = "SEC99214"
    )

    val payload = session.toQrPayload()
    val parsed = ClassroomSession.fromQrPayload(payload)

    assertNotNull(parsed)
    assertEquals("CS301", parsed?.subjectCode)
    assertEquals("Computer Networks", parsed?.subjectName)
    assertEquals("Hall 304 - CS Wing", parsed?.classroom)
    assertEquals("2026-11-02", parsed?.dateString)
    assertEquals("09:00 AM - 10:00 AM", parsed?.timeSlot)
    assertEquals("SEC99214", parsed?.sessionSecret)
  }

  @Test
  fun `verify invalid qr payload returns null`() {
    val invalidPayload = "RANDOM_QR_CODE_DATA"
    val parsed = ClassroomSession.fromQrPayload(invalidPayload)
    assertNull(parsed)
  }
}
