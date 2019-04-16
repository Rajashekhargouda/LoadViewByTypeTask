package com.locus.myapplication.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.locus.myapplication.R

class ViewModel(application: Application) : AndroidViewModel(application) {
    val itemListData = MutableLiveData<ItemListResponse>()
    val somethingWentWrong = application.applicationContext.getString(R.string.something_went_wrong)
    val noItems= application.applicationContext.getString(R.string.no_items)

    fun fetchItemList(){
        itemListData.value = ItemListResponse.Loading
        val jsonResponse = "[ \n" +
                "{ \n" +
                "\"type\" : \"PHOTO\", \"id\" : \"pic1\", \"title\" : \"Photo 1\", \"dataMap\" : {} \n" +
                "}, { \n" +
                "\"type\" : \"SINGLE_CHOICE\", \"id\" : \"choice1\", \"title\" : \"Photo 1 choice\", \"dataMap\" : { \"options\" : [ \"Good\", \"OK\", \"Bad\" ] } \n" +
                "}, { \n" +
                "\"type\" : \"COMMENT\", \"id\" : \"comment1\", \"title\" : \"Photo 1 comments\", \"dataMap\" : {} \n" +
                "}, { \n" +
                "\"type\" : \"PHOTO\", \"id\" : \"pic2\", \"title\" : \"Photo 2\", \"dataMap\" : {} \n" +
                "}, { \n" +
                "\"type\" : \"SINGLE_CHOICE\", \"id\" : \"choice2\", \"title\" : \"Photo 2 choice\", \"dataMap\" : { \"options\" : [ \"Good\", \"OK\", \"Bad\" ] } \n" +
                "}, { \n" +
                "\"type\" : \"COMMENT\", \"id\" : \"comment2\", \"title\" : \"Photo 2 comments\", \"dataMap\" : {} \n" +
                "} \n" +
                "] "


        convertResponseToModel(jsonResponse)
    }

    private fun convertResponseToModel(jsonResponse:String){
         try {
             val type =  object :TypeToken<ArrayList<ListModel>>(){}.type
            val itemList = Gson().fromJson<ArrayList<ListModel>>(jsonResponse,type)
             if (itemList.isNotEmpty())
                 itemListData.value = ItemListResponse.Success(itemList)
            else itemListData.value = ItemListResponse.Error(noItems)
         }catch (e:Exception){
            e.printStackTrace()
            itemListData.value = ItemListResponse.Error(somethingWentWrong)
        }
    }
    sealed class ItemListResponse(){
        data class Success(var responseList:ArrayList<ListModel>): ItemListResponse()
        data class Error(var msg:String):ItemListResponse()
        object Loading:ItemListResponse()
    }

}