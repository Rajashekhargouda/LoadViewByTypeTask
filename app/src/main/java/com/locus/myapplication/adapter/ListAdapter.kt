package com.locus.myapplication.adapter

import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.locus.myapplication.Constants.ModelType
import com.locus.myapplication.R
import com.locus.myapplication.model.ListModel
import java.lang.Exception

class ListAdapter(var itemList:ArrayList<ListModel>,
                  var function:(position:Int,item:ListModel,view:View)->Unit)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            ModelType.PHOTO ->{
                val view = LayoutInflater.from(viewGroup.context).
                    inflate(R.layout.photo_item_layout,viewGroup,false)
                return PhotoViewHolder(view)

            }
            ModelType.SINGLE_CHOCE ->{
                val view = LayoutInflater.from(viewGroup.context).
                    inflate(R.layout.single_choice_item_layout,viewGroup,false)
                return SingleChoiceViewHolder(view)
            }

            ModelType.COMMENT ->{
                val view = LayoutInflater.from(viewGroup.context).
                    inflate(R.layout.comment_item_layout,viewGroup,false)
                return CommentViewHolder(view)
            }
            else -> throw Exception()
         }

    }

    override fun getItemCount(): Int {
       return itemList.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position]
        when(viewHolder){
            is PhotoViewHolder ->{
                with(viewHolder){
                    photoTitle.text = item.title
                    photoImgview.setOnClickListener {
                        function.invoke(position,item,it)
                    }

                    imgRemove.setOnClickListener {
                        photoImgview.background = it.context.getDrawable(R.drawable.rectale_drawable)
                        photoImgview.setImageDrawable(null)
                    }
                }
            }
            is CommentViewHolder ->{
                with(viewHolder){
                    commentTitle.text = item.title
                    toggleSwitch.setOnClickListener {
                        if (toggleSwitch.isChecked){
                            edtxtComment.visibility = View.VISIBLE
                        }else edtxtComment.visibility = View.GONE
                    }
                }
            }
            is SingleChoiceViewHolder ->{
                with(viewHolder){
                    singleChoiceTitle.text = item.title
                    item.dataMap?.let {
                        it.options?.let {
                            generateViewForRadio(radioGroup,item.dataMap?.options!!)
                        }

                    }



                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val type = itemList.get(position).type
        return when(type){
            "PHOTO" ->{
                ModelType.PHOTO
            }
            "SINGLE_CHOICE" ->{
                ModelType.SINGLE_CHOCE
            }
            "COMMENT" ->{
                ModelType.COMMENT
            }
            else -> -1
        }


    }


    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImgview = itemView.findViewById<ImageView>(R.id.img_item_photo)
        val photoTitle = itemView.findViewById<TextView>(R.id.txt_photo_title)
        val imgRemove = itemView.findViewById<ImageView>(R.id.img_remove)
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val edtxtComment = itemView.findViewById<EditText>(R.id.edtxt_comment)
        val toggleSwitch = itemView.findViewById<SwitchCompat>(R.id.toggle_switch)
        val commentTitle = itemView.findViewById<TextView>(R.id.txt_comment_title)

    }

    class SingleChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val radioGroup = itemView.findViewById<RadioGroup>(R.id.radio_grp)
        val singleChoiceTitle = itemView.findViewById<TextView>(R.id.txt_single_choice_title)

    }

    private fun generateViewForRadio(view:ViewGroup, optionsList:ArrayList<String?>?){
        view.removeAllViews()
        optionsList?.let {
            for (option in optionsList){
                val radioButton = AppCompatRadioButton(view.context)
                radioButton.text = option
                view.addView(radioButton)
            }
        }

    }

}
