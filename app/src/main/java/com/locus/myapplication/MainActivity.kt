package com.locus.myapplication

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import com.locus.myapplication.adapter.ListAdapter
import com.locus.myapplication.model.ListModel
import com.locus.myapplication.model.ViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_enlarge_image.*


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: ViewModel
    var listAdapter:ListAdapter? = null
    private  val CAMERA_PERMISSION_CODE = 1234
    private var position:Int = -1
    private val CAMERA_REQUEST_CODE = 2222


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this)[ViewModel::class.java]
        observeDataList()
        viewModel.fetchItemList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_submit ->{
                onSubmitClicked()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onSubmitClicked() {
       val itemCount =  listAdapter?.itemCount
        val itemsList = listAdapter?.itemList
        for (position in 0..itemCount!!){
            try {
                val viewHolder = recycler_items.getChildViewHolder(recycler_items.getChildAt(position))
                when(viewHolder){
                    is ListAdapter.PhotoViewHolder ->{
                        Log.e("photoViewHolder", ""+viewHolder.photoTitle)
                        Log.e("photoViewHolder", ""+itemsList?.get(position)?.id)
                        Log.e("photoViewHolder", ""+itemsList?.get(position)?.title)
                    }
                    is ListAdapter.SingleChoiceViewHolder ->{
                        val radioGroup = viewHolder.radioGroup
                        for (childPosition in 0 until radioGroup.childCount){
                            val radioButton = radioGroup.getChildAt(childPosition) as AppCompatRadioButton
                            if (radioButton.isChecked)
                                Log.e("SingleChoiceViewHolder", ""+ radioButton.text)
                        }
                        Log.e("SingleChoiceViewHolder", ""+itemsList?.get(position)?.id)
                        Log.e("SingleChoiceViewHolder", ""+itemsList?.get(position)?.title)

                    }
                    is ListAdapter.CommentViewHolder ->{
                        Log.e("CommentViewHolder", ""+(viewHolder.edtxtComment.text))
                        Log.e("CommentViewHolder", ""+itemsList?.get(position)?.id)
                        Log.e("CommentViewHolder", ""+itemsList?.get(position)?.title)

                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun observeDataList(){
        viewModel.itemListData.observe(this, Observer {
            when(it){
                is ViewModel.ItemListResponse.Loading ->{
                    Toast.makeText(this@MainActivity,getString(R.string.loading),Toast.LENGTH_SHORT).show()

                }
                is ViewModel.ItemListResponse.Success ->{
                    setUpRecyclerList(it.responseList)
                }
                is ViewModel.ItemListResponse.Error ->{
                    Toast.makeText(this@MainActivity,it.msg,Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    private fun setUpRecyclerList(dataList:ArrayList<ListModel>){
        recycler_items.layoutManager = LinearLayoutManager(this)
        listAdapter = ListAdapter(dataList,{position:Int,item:ListModel,view:View->onItemClicked(position,item,view)})
        recycler_items.adapter = listAdapter
    }


    private fun onItemClicked(position:Int,item:ListModel,view: View){
        this.position = position
        when(item.type){
            "PHOTO" ->{
                if ((view as ImageView).drawable==null)
                    checkPermission()
                else showEnlargeImage(view.drawable)

            }
        }
    }

    private fun showEnlargeImage(drawable: Drawable){
       val settingsDialog =  Dialog(this)
        settingsDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        settingsDialog.setContentView(layoutInflater.inflate(R.layout.layout_enlarge_image
        , null))
        settingsDialog.imageView.setImageDrawable(drawable)
        settingsDialog.show()
    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }else launchPhoto()
        } else {
            launchPhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchPhoto()
            } else {
                Toast.makeText(this, getString(R.string.camera_denied), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun launchPhoto(){
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode== Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE){
           data?.let {
               setImage(position,it.extras?.get("data") as Bitmap )

           }
        }
    }

    private fun setImage(position:Int,imageBitMap:Bitmap){
        try {
            val viewHolder = recycler_items.getChildViewHolder(recycler_items.getChildAt(position))
            when(viewHolder){
                is ListAdapter.PhotoViewHolder ->{
                    val drawable = BitmapDrawable(resources,imageBitMap)
                    viewHolder.photoImgview.background = null
                    viewHolder.photoImgview.setImageDrawable(drawable)
                }


            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}
