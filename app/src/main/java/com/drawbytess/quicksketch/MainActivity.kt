package com.drawbytess.quicksketch

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawing_view.setSizeForBrush(10.toFloat())

        // Sets color of brush
        mImageButtonCurrentPaint = ll_paint_color[0] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        // Links brush size button with bush size functionality
        ib_brush_size.setOnClickListener{
            showBrushSizeChooseDialog()
        }

        // Links gallery button with gallery functions
        ib_gallery.setOnClickListener {
            if(isReadStorageAllowed()){

                // run code to get the image from gallery
                val pickPhotoIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(pickPhotoIntent, GALLERY)

            } else {
                requestStoragePermission()
            }
        }

        // Link undo button with undo function
        ib_undo.setOnClickListener {
            drawing_view.onClickUndo()
        }

        // Links save button with save functions
        ib_save.setOnClickListener {
            if (isReadStorageAllowed()){
                BitmapAsyncTask(getBitmapFromView(fl_draw_view_container)).execute()
                requestStoragePermission()
            }
        }
    }

    // Gets the photo from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY){
                try {
                    if (data!!.data != null){
                        iv_background.visibility = View.VISIBLE // makes background image visible
                        iv_background.setImageURI(data.data) // Sets image from users gallery
                    } else {
                        Toast.makeText(
                            this,
                            "Error in parsing the image or image file is corrupt",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }
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
                ContextCompat.getDrawable(this, R.drawable.pallet_normal))

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

    // Converts draw_view to bitmap so people can save it
    private fun getBitmapFromView(view: View): Bitmap {

        // Creates bitmap
        val returnedBitmap = Bitmap.createBitmap(view.width,
                view.height, Bitmap.Config.ARGB_8888)

        // Saved current draw_view
        val canvas = Canvas(returnedBitmap)

        // Saves background image or white canvas
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return returnedBitmap
    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap):
            AsyncTask<Any, Void, String>(){
        override fun doInBackground(vararg params: Any?): String {

            var result = ""
            if (mBitmap != null){
                try {
                    val bytes = ByteArrayOutputStream()

                    // Makes image into a .png file
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    // Creates a unique file name for saved photo
                    val f = File(externalCacheDir!!.absoluteFile.toString()
                            + File.separator
                            + "QuickSketchApp_"
                            + System.currentTimeMillis() / 1000
                            + ".png")

                    val fos = FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    fos.close()

                    // Saves to the postion on the phone specified in variable f
                    result = f.absolutePath

                } catch (e:Exception){
                    result = ""
                    e.printStackTrace()
                }
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!result!!.isEmpty()){
                Toast.makeText(this@MainActivity,
                        "File saved successfully : $result",
                        Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                        this@MainActivity,
                        "Something went wrong while saving the file",
                        Toast.LENGTH_LONG
                ).show()
            }
        }

    }


    // Codes for permissions
    companion object {
        private const val STORAGE_PERMISSION_CODE =1
        private const val GALLERY = 2
    }
}