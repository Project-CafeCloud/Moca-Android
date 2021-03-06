package com.example.parkseeun.moca_android.ui.communitySearch

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.parkseeun.moca_android.R
import com.example.parkseeun.moca_android.model.get.*
import com.example.parkseeun.moca_android.network.ApplicationController
import com.example.parkseeun.moca_android.ui.detail.detailReviewAll.GridSpacingItemDecoration
import com.example.parkseeun.moca_android.ui.detail.detailReviewAll.ReviewAllPopularAdapter
import com.example.parkseeun.moca_android.ui.detail.detailReviewAll.ReviewAllRecentAdapter
import com.example.parkseeun.moca_android.util.User
import kotlinx.android.synthetic.main.activity_community_search.*
import kotlinx.android.synthetic.main.fragment_com_sear_all.*
import kotlinx.android.synthetic.main.fragment_com_sear_cafe.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunitySearchActivity : AppCompatActivity() {
    private val TAG = "CommunitySearchActivity"

    // 통신
    private val networkService = ApplicationController.instance.networkService

    // 이번주 리뷰 많은 카페
    private lateinit var getBestCafeListResponse: Call<GetBestCafeListResponse>
    private var getBestCafeListData = ArrayList<GetBestCafeListData>()
    lateinit var comSearAllReviewTopAdapter: ComSearAllReviewTopAdapter

    // 이번주 인기 많은 사용자
    private lateinit var getBestUserResponse: Call<GetBestUserResponse>
    private var getBestUserData = ArrayList<GetBestUserData>()
    lateinit var comSearAllPopUserAdapter: ComSearAllPopUserAdapter

    //검색후
    // 전체 데이터
    private lateinit var getCommunitySearchListResponse: Call<GetCommunitySearchListResponse>
    //인기 리뷰
    private var popularReviewList = ArrayList<ReviewData>()
    lateinit var reviewAllPopularAdapter: ReviewAllPopularAdapter
    //최신 리뷰
    private var reviewListOrderByLatest = ArrayList<ReviewData>()
    lateinit var reviewAllRecentAdapter: ReviewAllRecentAdapter
    //사용자
    private var searchUserList = ArrayList<SearchUserData>()
    lateinit var comSearUserAdapter: ComSearUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_search)

        setOnBtnClickListeners()

        setNetwork()

        et_act_com_sear_search.addTextChangedListener(object : TextWatcher {

            //val layoutNothing : View = findViewById(R.id.ll_frag_com_sear_all_nothing)
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (et_act_com_sear_search.text.toString() == "") {
                    ll_act_com_sear_all_nothing.visibility = View.VISIBLE
                    ll_act_com_sear_cafe_nothing.visibility = View.VISIBLE
                    ll_act_com_sear_user_nothing.visibility = View.VISIBLE
                } else {
                    ll_act_com_sear_all_nothing.visibility = View.GONE
                    ll_act_com_sear_cafe_nothing.visibility = View.GONE
                    ll_act_com_sear_user_nothing.visibility = View.GONE

                    getAllResult(et_act_com_sear_search.text.toString())
                }
            }
        })

    }
    // 통신 (전체 탭)
    private fun getAllResult(searchString: String) {
        getCommunitySearchListResponse = networkService.getCommunitySearchResult(User.token!!, searchString)
        getCommunitySearchListResponse.enqueue(object : Callback<GetCommunitySearchListResponse> {
            override fun onFailure(call: Call<GetCommunitySearchListResponse>, t: Throwable) {
                toast(t.message.toString())
            }

            override fun onResponse(
                call: Call<GetCommunitySearchListResponse>,
                response: Response<GetCommunitySearchListResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == 200) {
                        var temp = response.body()!!.data
                        popularReviewList = temp.popularReviewList
                        reviewListOrderByLatest = temp.reviewListOrderByLatest
                        searchUserList = temp.searchUserList

                        //전체 검색통신
                        ll_act_com_sear_all_all.visibility = View.VISIBLE

                        reviewAllPopularAdapter= ReviewAllPopularAdapter(this@CommunitySearchActivity, popularReviewList)
                        rv_act_com_sear_all_cafe.layoutManager = LinearLayoutManager(this@CommunitySearchActivity, LinearLayoutManager.HORIZONTAL, false)
                        rv_act_com_sear_all_cafe.adapter = reviewAllPopularAdapter

                        comSearUserAdapter= ComSearUserAdapter(this@CommunitySearchActivity, searchUserList)
                        rv_act_com_sear_all_user.layoutManager = LinearLayoutManager(this@CommunitySearchActivity)
                        rv_act_com_sear_all_user.adapter =comSearUserAdapter

                        //카페명 검색통신
                        ll_act_com_sear_cafe_cafe.visibility = View.VISIBLE

                        rv_act_com_sear_cafe_popular.layoutManager = LinearLayoutManager(this@CommunitySearchActivity, LinearLayoutManager.HORIZONTAL,false)
                        rv_act_com_sear_cafe_popular.adapter = reviewAllPopularAdapter

                        reviewAllRecentAdapter= ReviewAllRecentAdapter(this@CommunitySearchActivity, reviewListOrderByLatest)
                        rv_act_com_sear_cafe_recent.layoutManager = GridLayoutManager(this@CommunitySearchActivity, 3)
                        rv_act_com_sear_cafe_recent.adapter = reviewAllRecentAdapter

                        //사용자명 검색통신
                        ll_act_com_sear_user_user.visibility = View.VISIBLE
                        rv_act_com_sear_user.layoutManager = LinearLayoutManager(this@CommunitySearchActivity)
                        rv_act_com_sear_user.adapter = comSearUserAdapter



/*
                        popularReviewList = temp.popularReviewList
                        reviewListOrderByLatest = temp.reviewListOrderByLatest
                        searchUserList = temp.searchUserList

                        Log.v("popReviewList", popularReviewList.size.toString())
                        Log.v("latestReview", reviewListOrderByLatest.size.toString())
                        Log.v("searchUserList", searchUserList.size.toString())


                        val positionPopCafe = reviewAllPopularAdapter.itemCount
                        val positionRecCafe = reviewAllRecentAdapter.itemCount
                        val positionUser = comSearUserAdapter.itemCount

                        reviewAllPopularAdapter.dataList = popularReviewList
                        reviewAllPopularAdapter.notifyItemChanged(positionPopCafe)

                        reviewAllRecentAdapter.dataList = reviewListOrderByLatest
                        reviewAllRecentAdapter.notifyItemChanged(positionRecCafe)

                        comSearUserAdapter.dataList = searchUserList
                        comSearUserAdapter.notifyItemChanged(positionUser)
                        */


                    }else {
                        ll_act_com_sear_all_all.visibility = View.GONE
                        ll_act_com_sear_cafe_cafe.visibility = View.GONE
                        ll_act_com_sear_user_user.visibility = View.GONE
                    }
                }

            }
        })
    }

/*
    private fun setRecyclerView() {
        // 검색 전 화면 - 이번주 리뷰 많은 카페 RecyclerView 설정
        comSearAllReviewTopAdapter = ComSearAllReviewTopAdapter(this, getBestCafeListData)
        rv_act_com_sear_all_reviewtop.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_act_com_sear_all_reviewtop.adapter = comSearAllReviewTopAdapter

        // 검색 전 - 이번주 인기 많은 사용자
        comSearAllPopUserAdapter = ComSearAllPopUserAdapter(this, getBestUserData)
        rv_act_comm_sear_all_popularuser.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_act_comm_sear_all_popularuser.adapter = comSearAllPopUserAdapter

        ///////////////////
        // 검색 후
        // 인기리뷰
        reviewAllPopularAdapter = ReviewAllPopularAdapter(this, popularReviewList)
        rv_act_com_sear_all_review.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_act_com_sear_all_review.adapter = reviewAllPopularAdapter

        rv_act_com_sear_cafe_popular.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_act_com_sear_cafe_popular.adapter = reviewAllPopularAdapter

        //최신리뷰
        val spanCount = 3 // 3 columns
        val spacing = 40     // 50px
        val includeEdge = false
        rv_act_com_sear_cafe_recent.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        reviewAllRecentAdapter = ReviewAllRecentAdapter(this, reviewListOrderByLatest)
        rv_act_com_sear_cafe_recent.layoutManager = GridLayoutManager(this, 3)
        rv_act_com_sear_cafe_recent.adapter = reviewAllRecentAdapter

        //사용자
        comSearUserAdapter = ComSearUserAdapter(this, searchUserList)
        rv_act_com_sear_all_user.layoutManager = LinearLayoutManager(this)
        rv_act_com_sear_all_user.adapter = comSearUserAdapter

    }
    */

    private fun setOnBtnClickListeners() {
        ib_act_com_sear_back.setOnClickListener {
            finish()
        }

        btn_act_com_sear_all.setOnClickListener {
            btn_act_com_sear_all.setTextColor(Color.parseColor("#e1b2a3"))
            btn_act_com_sear_cafe.setTextColor(Color.parseColor("#707070"))
            btn_act_com_sear_user.setTextColor(Color.parseColor("#707070"))

            view_all.setBackgroundColor(Color.parseColor("#e1b2a3"))
            view_cafe.setBackgroundColor(Color.parseColor("#ffffff"))
            view_location.setBackgroundColor(Color.parseColor("#ffffff"))

            ll_act_com_sear_all.visibility = View.VISIBLE
            ll_act_com_sear_cafe.visibility = View.GONE
            ll_act_com_sear_user.visibility = View.GONE


        }

        btn_act_com_sear_cafe.setOnClickListener {
            btn_act_com_sear_all.setTextColor(Color.parseColor("#707070"))
            btn_act_com_sear_cafe.setTextColor(Color.parseColor("#e1b2a3"))
            btn_act_com_sear_user.setTextColor(Color.parseColor("#707070"))

            view_all.setBackgroundColor(Color.parseColor("#ffffff"))
            view_cafe.setBackgroundColor(Color.parseColor("#e1b2a3"))
            view_location.setBackgroundColor(Color.parseColor("#ffffff"))

            ll_act_com_sear_all.visibility = View.GONE
            ll_act_com_sear_cafe.visibility = View.VISIBLE
            ll_act_com_sear_user.visibility = View.GONE
        }

        btn_act_com_sear_user.setOnClickListener {
            btn_act_com_sear_cafe.setTextColor(Color.parseColor("#707070"))
            btn_act_com_sear_cafe.setTextColor(Color.parseColor("#707070"))
            btn_act_com_sear_user.setTextColor(Color.parseColor("#e1b2a3"))

            view_all.setBackgroundColor(Color.parseColor("#ffffff"))
            view_cafe.setBackgroundColor(Color.parseColor("#ffffff"))
            view_location.setBackgroundColor(Color.parseColor("#e1b2a3"))

            ll_act_com_sear_all.visibility = View.GONE
            ll_act_com_sear_cafe.visibility = View.GONE
            ll_act_com_sear_user.visibility = View.VISIBLE
        }
    }




    private fun setNetwork() {
        getBestReviewCafe()

        getBestUser()
    }

    //통신 (이번주 리뷰 많은 카페)
    private fun getBestReviewCafe() {
        getBestCafeListResponse = networkService.getBestCafeList(User.token!!, 1)
        getBestCafeListResponse.enqueue(object : Callback<GetBestCafeListResponse> {
            override fun onFailure(call: Call<GetBestCafeListResponse>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }

            override fun onResponse(call: Call<GetBestCafeListResponse>, response: Response<GetBestCafeListResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == 200) {
                        val temp = response.body()!!.data
                        if (temp.size > 0) {
                            // 검색 전 화면 - 이번주 리뷰 많은 카페 RecyclerView 설정
                            rv_act_com_sear_all_reviewtop.layoutManager = LinearLayoutManager(
                                this@CommunitySearchActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            rv_act_com_sear_all_reviewtop.adapter =
                                    ComSearAllReviewTopAdapter(this@CommunitySearchActivity, temp)

                            rv_act_com_sear_cafe_reviewtop.layoutManager = LinearLayoutManager(
                                this@CommunitySearchActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            rv_act_com_sear_cafe_reviewtop.adapter =
                                    ComSearAllReviewTopAdapter(this@CommunitySearchActivity, temp)

                            rv_act_com_sear_user_reviewtop.layoutManager = LinearLayoutManager(
                                this@CommunitySearchActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            rv_act_com_sear_user_reviewtop.adapter =
                                    ComSearAllReviewTopAdapter(this@CommunitySearchActivity, temp)
                        } else if (response.body()!!.status == 204) {
                            toast("리뷰 많은 카페가 존재하지 않습니다!")
                        }
                    }
                }
            }
        })
    }


    // 통신 (이번주 인기 많은 사용자)
    private fun getBestUser() {
        getBestUserResponse = networkService.getBestUserList(User.token!!)
        getBestUserResponse.enqueue(object : Callback<GetBestUserResponse> {
            override fun onFailure(call: Call<GetBestUserResponse>, t: Throwable) {
                toast(t.message.toString())
            }

            override fun onResponse(call: Call<GetBestUserResponse>, response: Response<GetBestUserResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == 200) {
                        val temp = response.body()!!.data
                        if (temp.size > 0) {
                            // 이번주 인기 많은 사용자

                            rv_act_comm_sear_all_popularuser.layoutManager = LinearLayoutManager(this@CommunitySearchActivity, LinearLayoutManager.HORIZONTAL, false)
                            rv_act_comm_sear_all_popularuser.adapter = ComSearAllPopUserAdapter(this@CommunitySearchActivity, temp)

                            rv_act_comm_sear_cafe_popularuser.layoutManager = LinearLayoutManager(this@CommunitySearchActivity, LinearLayoutManager.HORIZONTAL, false)
                            rv_act_comm_sear_cafe_popularuser.adapter = ComSearAllPopUserAdapter(this@CommunitySearchActivity, temp)

                            rv_act_comm_sear_user_popularuser.layoutManager = LinearLayoutManager(this@CommunitySearchActivity, LinearLayoutManager.HORIZONTAL, false)
                            rv_act_comm_sear_user_popularuser.adapter = ComSearAllPopUserAdapter(this@CommunitySearchActivity, temp)
                        }
                    }
                }
            }
        })
    }


}