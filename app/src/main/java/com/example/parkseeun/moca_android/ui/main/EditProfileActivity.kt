package com.example.parkseeun.moca_android.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.session.MediaSession
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.bumptech.glide.Glide
import com.example.parkseeun.moca_android.R
import com.example.parkseeun.moca_android.model.get.GetMypageEditprofileResponse
import com.example.parkseeun.moca_android.model.get.ProfileData
import com.example.parkseeun.moca_android.network.ApplicationController
import com.example.parkseeun.moca_android.ui.loginJoin.LoginActivity
import com.example.parkseeun.moca_android.util.SharedPreferenceController
import com.example.parkseeun.moca_android.util.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity(), KeyboardVisibilityEventListener, TextWatcher {
    private val TAG = "EditProfileActivity"
    private val My_READ_STORAGE_REQUEST_CODE = 1004
    private val REQUEST_CODE_SELECT_IMAGE = 2004
    lateinit var profileData: ProfileData
    val networkService by lazy {
        ApplicationController.instance.networkService
    }


    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            sv_act_edit_profile.scrollTo(0, sv_act_edit_profile.bottom)
        } else {
            sv_act_edit_profile.scrollTo(0, sv_act_edit_profile.top)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(toolbar_edit_profile)

        setNetwork()

        setBtnSetting()

        setOnBtnClickListeners()
    }

    private fun setNetwork() {
        getMypageEditprofileResponse()
    }

    private fun getMypageEditprofileResponse() {
        val getMypageEditprofileResponse = networkService.getMypageEditprofileResponse(User.token, User.user_id)

        getMypageEditprofileResponse.enqueue(object : Callback<GetMypageEditprofileResponse> {
            override fun onFailure(call: Call<GetMypageEditprofileResponse>, t: Throwable) {
                Log.e(TAG, "load failed")
            }

            override fun onResponse(
                call: Call<GetMypageEditprofileResponse>,
                response: Response<GetMypageEditprofileResponse>
            ) {
                if (response.isSuccessful) {
                    Log.v(TAG, "load success")

                    profileData = response.body()!!.data
                    setProfile(profileData)

                }
            }
        })
    }

    private fun setProfile(data: ProfileData) {
        Glide.with(this).load(data.user_img_url).into(iv_act_editprofile_photo)

        et_ect_edit_prof_nick.setText(data.user_name)
        et_ect_edit_prof_status.setText(data.user_status_comment)
        et_ect_edit_prof_phone.setText(data.user_phone)
    }


    private fun setOnBtnClickListeners() {
        btn_act_edit_prof_complete.setOnClickListener {
            finish()
        }
//전체 기록 지우고 SharedPreference에서 로그인 기록 지우고 로그인화면으로 넘어감
        rl_act_edit_profile_logout.setOnClickListener {
            SharedPreferenceController.clearSPC(this)

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        //텍스트 지우기
        iv_act_editprofile_nickname.setOnClickListener {
            var nickname = et_ect_edit_prof_nick.text
            nickname.clear()

        }
        iv_act_editprofile_status.setOnClickListener {
            var status = et_ect_edit_prof_status.text
            status.clear()

        }
        iv_act_editprofile_phone.setOnClickListener {
            var phone = et_ect_edit_prof_phone.text
            phone.clear()

        }

    }

    private fun setBtnSetting() {

        btn_act_edit_prof_complete.isEnabled = true

        et_ect_edit_prof_nick.addTextChangedListener(this)
        et_ect_edit_prof_status.addTextChangedListener(this)
        et_ect_edit_prof_phone.addTextChangedListener(this)

        rl_act_editprofile_photo.setOnClickListener {
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    My_READ_STORAGE_REQUEST_CODE
                )
            }
        } else {

            showAlbum()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == My_READ_STORAGE_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAlbum()
            } else {

            }
        }
    }

    private fun showAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {

                    val selectedImageUri: Uri = data.data
                    Glide.with(this@EditProfileActivity)
                        .load(selectedImageUri)
                        .into(iv_act_editprofile_photo)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        btn_act_edit_prof_complete.isEnabled =
                et_ect_edit_prof_nick.text.toString().isNotEmpty()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


}
