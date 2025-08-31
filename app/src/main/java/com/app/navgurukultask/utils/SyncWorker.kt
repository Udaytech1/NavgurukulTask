import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.app.navgurukultask.data.local.dao.StudentDao
import com.app.navgurukultask.data.local.entities.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val studentDao: StudentDao,
    private val firestore: FirebaseFirestore
) : Worker(context, workerParams) {
    override fun doWork(): Result {

        return try {
            Log.d("Worker", "work started.....")
            CoroutineScope(Dispatchers.IO).launch {
                syncStudents()
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    private suspend fun syncStudents() {
        val students = studentDao.getUnsyncedStudents()
        val collection = firestore.collection("students")
        Log.d("Worker", "Student lis ..... ${students.size}")

        for (student in students) {
            val updatedStudent = try {
                collection.document(student.id.toString())
                    .set(student)
                    .await()

                student.copy(syncStatus = SyncStatus.SYNCED)
            } catch (e: Exception) {
                e.printStackTrace()
                student.copy(syncStatus = SyncStatus.FAILED)
            }
            studentDao.updateStudent(updatedStudent)
        }
    }

}
