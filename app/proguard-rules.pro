# Keep Room DAO and entities
-keep class androidx.room.** { *; }
-keep class com.example.todox.data.model.** { *; }

# Keep WorkManager workers
-keep class androidx.work.impl.** { *; }
-keep class com.example.todox.workers.** extends androidx.work.ListenableWorker { *; }

# Compose reflection metadata
-keep class androidx.compose.** { *; }
