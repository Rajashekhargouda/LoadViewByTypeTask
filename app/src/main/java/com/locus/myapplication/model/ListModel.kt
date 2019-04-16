package com.locus.myapplication.model

import com.google.gson.annotations.SerializedName

data class ListModel(@SerializedName("type")var type:String?,
                     @SerializedName("id")var id:String?,
                     @SerializedName("title")var title:String?,
                     @SerializedName("dataMap")var dataMap:Option?)

data class Option(@SerializedName("options")var options:ArrayList<String?>?)

