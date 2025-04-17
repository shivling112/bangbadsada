package com.example.collegeapp.util

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File

/**
 * Helper class for Firebase operations
 * Centralizes all Firebase-related code for easier management
 */
class FirebaseHelper {
    
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference
    
    // Authentication methods
    
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }
    
    fun signUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun resetPassword(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }
    
    // Database methods
    
    fun getUserData(userId: String, callback: (userData: UserData?) -> Unit) {
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserData::class.java)
                callback(userData)
            }
            
            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
    
    fun saveUserData(userId: String, userData: UserData): Task<Void> {
        return database.child("users").child(userId).setValue(userData)
    }
    
    fun getAttendanceData(userId: String, callback: (attendanceData: AttendanceData?) -> Unit) {
        database.child("attendance").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendanceData = snapshot.getValue(AttendanceData::class.java)
                callback(attendanceData)
            }
            
            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
    
    fun saveAttendanceData(userId: String, attendanceData: AttendanceData): Task<Void> {
        return database.child("attendance").child(userId).setValue(attendanceData)
    }
    
    fun getTimetableData(userId: String, callback: (timetableData: TimetableData?) -> Unit) {
        database.child("timetable").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val timetableData = snapshot.getValue(TimetableData::class.java)
                callback(timetableData)
            }
            
            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
    
    // Storage methods
    
    fun uploadFile(userId: String, fileUri: File, fileType: String): UploadTask {
        val fileName = "${fileType}_${System.currentTimeMillis()}"
        return storage.child("files").child(userId).child(fileName).putFile(fileUri.toUri())
    }
    
    fun getFileDownloadUrl(userId: String, fileName: String): Task<String> {
        return storage.child("files").child(userId).child(fileName).getDownloadUrl()
            .continueWith { task -> task.result.toString() }
    }
    
    // Data classes
    
    data class UserData(
        val name: String = "",
        val email: String = "",
        val studentId: String = "",
        val department: String = "",
        val profilePicUrl: String = ""
    )
    
    data class AttendanceData(
        val overallPercentage: Float = 0f,
        val subjectsAttendance: Map<String, SubjectAttendance> = emptyMap()
    )
    
    data class SubjectAttendance(
        val subjectName: String = "",
        val totalClasses: Int = 0,
        val attendedClasses: Int = 0,
        val percentage: Float = 0f
    )
    
    data class TimetableData(
        val days: Map<String, List<ClassSession>> = emptyMap()
    )
    
    data class ClassSession(
        val subjectName: String = "",
        val startTime: String = "",
        val endTime: String = "",
        val roomNumber: String = "",
        val teacherName: String = ""
    )
    
    // Helper extension function
    private fun File.toUri() = android.net.Uri.fromFile(this)
}