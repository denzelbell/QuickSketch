package com.drawbytess.quicksketch

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*


class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawing_view.setSizeForBrush(10.toFloat())

        // Sets color of brush
        mImageButtonCurrentPaint = ll_paint_color[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        // Calls functionality for brush size
        ib_brush_size.setOnClickListener{
            showBrushSizeChooseDialog()
        }

        ib_gallery.setOnClickListener {
            if(isReadStorageAllowed()){
                // run code to get the image from gallery
            } else {
                requestStoragePermission()
            }
        }
    }

    // Sets color change
    fun paintClicked(view: View){
        if (view !== mImageButtonCurrentPaint){
            val imageButton = view as ImageButton

            val colorTag = imageButton.tag.toString() // Reading the tag element on activity_main
            drawing_view.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed))

            mImageButtonCurrentPaint = view
        }

    }

    // Sets the paint brush pop up
    private fun showBrushSizeChooseDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")

        // Set brush size for buttons
        val xsmallBtn = brushDialog.ib_xsmall_brush
        xsmallBtn.setOnClickListener{
            drawing_view.setSizeForBrush(5.toFloat())
            brushDialog.dismiss()
        }

        val smallBtn = brushDialog.ib_small_brush
        smallBtn.setOnClickListener{
            drawing_view.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        val smBtn = brushDialog.ib_smallmedium_brush
        smBtn.setOnClickListener{
            drawing_view.setSizeForBrush(15.toFloat())
            brushDialog.dismiss()
        }

        val mediumBtn = brushDialog.ib_medium_brush
        mediumBtn.setOnClickListener{
            drawing_view.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        val mlBtn = brushDialog.ib_mediumlarge_brush
        mlBtn.setOnClickListener{
            drawing_view.setSizeForBrush(25.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn = brushDialog.ib_large_brush
        largeBtn.setOnClickListener{
            drawing_view.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }


        brushDialog.show()
    }

    // Request permission for storage access outside onCreate
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this, "Need permission to add a background",
            Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    // What happens if the user gives permission
    // Not typically needed
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(
                    this,
                    "Permission granted now you can read the storage files.",
                    Toast.LENGTH_LONG)
                    .show()
        } else {
            Toast.makeText(
                this,
                "Oops, you just denied the permission.",
                Toast.LENGTH_LONG)
                .show()
        }
    }

    // Checks if access was granted to storage
    private fun isReadStorageAllowed(): Boolean{
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    // Codes for permissions
    companion object {
        private const val STORAGE_PERMISSION_CODE =1
    }
}