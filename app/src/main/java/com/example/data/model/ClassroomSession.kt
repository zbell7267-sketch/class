package com.example.data.model

data class ClassroomSession(
    val subjectCode: String,
    val subjectName: String,
    val classroom: String,
    val dateString: String,
    val timeSlot: String,
    val sessionSecret: String
) {
    /**
     * Standard CampusQR payload format:
     * CAMPUSQR|SUBJECT_CODE|SUBJECT_NAME|CLASSROOM|DATE|TIME_SLOT|SECRET
     */
    fun toQrPayload(): String {
        return "CAMPUSQR|$subjectCode|$subjectName|$classroom|$dateString|$timeSlot|$sessionSecret"
    }

    companion object {
        fun fromQrPayload(payload: String): ClassroomSession? {
            val trimmed = payload.trim()
            if (!trimmed.startsWith("CAMPUSQR|")) return null
            val parts = trimmed.split("|")
            if (parts.size < 7) return null
            return ClassroomSession(
                subjectCode = parts[1],
                subjectName = parts[2],
                classroom = parts[3],
                dateString = parts[4],
                timeSlot = parts[5],
                sessionSecret = parts[6]
            )
        }
    }
}

sealed class ScanAttendanceResult {
    data class Success(val record: AttendanceRecord) : ScanAttendanceResult()
    data class AlreadyMarked(val record: AttendanceRecord) : ScanAttendanceResult()
    data class InvalidQr(val reason: String) : ScanAttendanceResult()
    data class Error(val message: String) : ScanAttendanceResult()
}
