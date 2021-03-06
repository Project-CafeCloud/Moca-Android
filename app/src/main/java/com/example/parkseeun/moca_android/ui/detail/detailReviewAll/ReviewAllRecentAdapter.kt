package com.example.parkseeun.moca_android.ui.detail.detailReviewAll

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.parkseeun.moca_android.R
import com.example.parkseeun.moca_android.model.get.ReviewData
import com.example.parkseeun.moca_android.ui.community.review_comment.ReviewCommentActivity
import com.example.parkseeun.moca_android.ui.community.review_detail.ReviewDetailActivity
import kotlinx.android.synthetic.main.category_picks_rv_item.view.*
import org.jetbrains.anko.startActivity

class ReviewAllRecentAdapter(val ctx : Context, var dataList: ArrayList<ReviewData>) : RecyclerView.Adapter<ReviewAllRecentAdapter.Holder>(){
    override fun onCreateViewHolder(container: ViewGroup, p1: Int): ReviewAllRecentAdapter.Holder {
        val view : View = LayoutInflater.from(ctx).inflate(R.layout.rv_act_review_all_recent_item, container, false)

        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ReviewAllRecentAdapter.Holder, position: Int) {
        val width = getScreenWidth()
        holder.photo.layoutParams.width = (width-dpToPx(18.toFloat())).toInt()

        Glide.with(ctx).load(dataList[position].review_img_url).into(holder.photo)
        holder.photo.setOnClickListener {
            val review_id = dataList[position].review_id
            val intent = Intent(ctx, ReviewDetailActivity::class.java)
            intent.putExtra("review_id", review_id)
            ctx.startActivity(intent)
        }

    }

    inner class Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val photo : ImageView = itemView.findViewById(R.id.iv_rv_act_review_all_recent_image) as ImageView
    }

    private fun dpToPx(dp:Float):Float{
        return (dp * ctx.resources.displayMetrics.density)
    }
    private fun getScreenWidth():Int{
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }
}