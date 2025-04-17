package com.example.collegeapp.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Manager class for Firebase operations
 */
class FirebaseManager private constructor() {
    private val TAG = "FirebaseManager"
    
    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    // Collection references
    private val usersCollection = firestore.collection("users")
    private val teacherRequestsCollection = firestore.collection("teacherRequests")
    private val adminRequestsCollection = firestore.collection("adminRequests")
    private val coursesCollection = firestore.collection("courses")
    
    // Current user
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    companion object {
        @Volatile
        private var instance: FirebaseManager? = null
        
        fun getInstance(): FirebaseManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseManager().also { instance = it }
            }
        }
    }
    
    /**
     * Register a new user
     */
    suspend fun registerUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("User registration failed"))
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error registering user: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Login an existing user
     */
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("User login failed"))
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error logging in user: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Logout the current user
     */
    fun logout() {
        auth.signOut()
    }
    
    /**
     * Create user profile in Firestore
     */
    suspend fun createUserProfile(
        userId: String,
        name: String,
        email: String,
        userType: UserType
    ): Result<Unit> {
        return try {
            val userProfile = UserProfile(
                userId = userId,
                name = name,
                email = email,
                userType = userType
            )
            
            usersCollection.document(userId).set(userProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user profile: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Get user profile from Firestore
     */
    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val document = usersCollection.document(userId).get().await()
            
            if (document.exists()) {
                val userProfile = document.toObject(UserProfile::class.java)
                    ?: return Result.failure(Exception("Failed to parse user profile"))
                
                Result.success(userProfile)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile in Firestore
     */
    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            usersCollection.document(userProfile.userId).set(userProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Create a teacher role request
     */
    suspend fun createTeacherRequest(
        userId: String,
        name: String,
        email: String,
        details: String
    ): Result<String> {
        return try {
            val requestId = UUID.randomUUID().toString()
            
            val request = RoleRequest(
                id = requestId,
                userId = userId,
                name = name,
                email = email,
                details = details,
                requestedRole = UserType.TEACHER,
                status = "pending",
                timestamp = System.currentTimeMillis()
            )
            
            teacherRequestsCollection.document(requestId).set(request).await()
            Result.success(requestId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating teacher request: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Create an admin role request
     */
    suspend fun createAdminRequest(
        userId: String,
        name: String,
        email: String,
        details: String
    ): Result<String> {
        return try {
            val requestId = UUID.randomUUID().toString()
            
            val request = RoleRequest(
                id = requestId,
                userId = userId,
                name = name,
                email = email,
                details = details,
                requestedRole = UserType.ADMIN,
                status = "pending",
                timestamp = System.currentTimeMillis()
            )
            
            adminRequestsCollection.document(requestId).set(request).await()
            Result.success(requestId)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating admin request: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Get all teacher requests
     */
    suspend fun getAllTeacherRequests(): Result<List<RoleRequest>> {
        return try {
            val snapshot = teacherRequestsCollection.get().await()
            val requests = snapshot.toObjects(RoleRequest::class.java)
            Result.success(requests)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting teacher requests: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Get all admin requests
     */
    suspend fun getAllAdminRequests(): Result<List<RoleRequest>> {
        return try {
            val snapshot = adminRequestsCollection.get().await()
            val requests = snapshot.toObjects(RoleRequest::class.java)
            Result.success(requests)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting admin requests: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Process teacher request (approve/reject)
     */
    suspend fun processTeacherRequest(requestId: String, approved: Boolean): Result<Unit> {
        return try {
            val status = if (approved) "approved" else "rejected"
            
            // Update request status
            teacherRequestsCollection.document(requestId)
                .update("status", status)
                .await()
            
            if (approved) {
                // Get the request to get the user ID
                val request = teacherRequestsCollection.document(requestId).get().await()
                    .toObject(RoleRequest::class.java)
                    ?: return Result.failure(Exception("Request not found"))
                
                // Update user's role to teacher
                usersCollection.document(request.userId)
                    .update("userType", UserType.TEACHER)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing teacher request: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Process admin request (approve/reject)
     */
    suspend fun processAdminRequest(requestId: String, approved: Boolean): Result<Unit> {
        return try {
            val status = if (approved) "approved" else "rejected"
            
            // Update request status
            adminRequestsCollection.document(requestId)
                .update("status", status)
                .await()
            
            if (approved) {
                // Get the request to get the user ID
                val request = adminRequestsCollection.document(requestId).get().await()
                    .toObject(RoleRequest::class.java)
                    ?: return Result.failure(Exception("Request not found"))
                
                // Update user's role to admin
                usersCollection.document(request.userId)
                    .update("userType", UserType.ADMIN)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing admin request: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Add a new course
     */
    suspend fun addCourse(course: com.example.collegeapp.ui.teacher.models.Course): Result<String> {
        return try {
            val document = coursesCollection.document()
            val courseWithId = course.copy(id = document.id)
            
            document.set(courseWithId).await()
            Result.success(document.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding course: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Get courses by teacher ID
     */
    suspend fun getCoursesByTeacher(teacherId: String): Result<List<com.example.collegeapp.ui.teacher.models.Course>> {
        return try {
            val snapshot = coursesCollection.whereEqualTo("teacherId", teacherId).get().await()
            val courses = snapshot.toObjects(com.example.collegeapp.ui.teacher.models.Course::class.java)
            Result.success(courses)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting courses by teacher: ${e.message}")
            Result.failure(e)
        }
    }
}