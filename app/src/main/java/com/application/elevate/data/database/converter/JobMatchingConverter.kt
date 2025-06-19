package com.application.elevate.data.database.converter

import androidx.room.TypeConverter
import com.application.elevate.model.JobMatch
import com.application.elevate.model.AIJobAnalysis
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JobMatchingConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromJobMatchList(jobMatches: List<JobMatch>): String {
        return gson.toJson(jobMatches)
    }
    
    @TypeConverter
    fun toJobMatchList(data: String): List<JobMatch> {
        val listType = object : TypeToken<List<JobMatch>>() {}.type
        return gson.fromJson(data, listType)
    }
    
    @TypeConverter
    fun fromAIJobAnalysis(aiAnalysis: AIJobAnalysis): String {
        return gson.toJson(aiAnalysis)
    }
    
    @TypeConverter
    fun toAIJobAnalysis(data: String): AIJobAnalysis {
        return gson.fromJson(data, AIJobAnalysis::class.java)
    }
} 