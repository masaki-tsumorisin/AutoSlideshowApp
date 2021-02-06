package jp.techacademy.masaki.tsumori.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mTimer: Timer? = null

    private var active1 = true

    private var active2 = false

    private var mHandler = Handler()

    private val PERMISSIONS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)

            var count = 1
            Log.d("zasu", "${count}")


            nextButton.setOnClickListener {

                if (active1 == true) {
                    if (count < cursor.count) {

                        cursor.moveToNext()

                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                        imageView.setImageURI(imageUri)

                        count++
                        Log.d("zasu", "${count}")
                    } else {

                        cursor.moveToFirst()

                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        imageView.setImageURI(imageUri)

                        count = 1
                        Log.d("zasu", "${count}")
                    }
                }
            }

            backButton.setOnClickListener {

                if (active1 == true) {

                    if (count > 1) {

                        cursor.moveToPrevious()

                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                        imageView.setImageURI(imageUri)

                        count--
                        Log.d("zasu", "${count}")
                    } else {

                        cursor.moveToLast()

                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        imageView.setImageURI(imageUri)

                        count = cursor.count
                        Log.d("zasu", "${count}")
                    }
                }
            }


            //自動送り処理

            playButton.setOnClickListener {

                if (active2 == false) {

                    playButton.text = "停止"

                    active1 = false      //進む、戻るボタンをタップ不可にする
                    active2 = true       //activeがtrueかfalseかによって再生、停止を制御

                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {

                            if (count < cursor.count) {

                                cursor.moveToNext()

                                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                                mHandler.post {
                                    imageView.setImageURI(imageUri)
                                }


                                count++
                                Log.d("zasu", "${count}")
                            } else {

                                cursor.moveToFirst()

                                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                mHandler.post {
                                    imageView.setImageURI(imageUri)
                                }

                                count = 1
                                Log.d("zasu", "${count}")
                            }
                        }
                    },2000, 2000)
                } else {

                    active1 = true          //進む、戻るボタンをタップ可にする
                    active2 = false         //activeがtrueかfalseかによって再生、停止を制御
                    playButton.text = "再生"
                    mTimer!!.cancel()
                }
            }
        }
    }
}