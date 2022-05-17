package com.xysss.keeplearning.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.databinding.FragmentPlaceBinding
import com.xysss.keeplearning.ui.activity.MainActivity
import com.xysss.keeplearning.ui.activity.WeatherActivity
import com.xysss.keeplearning.ui.adapter.PlaceAdapter
import com.xysss.keeplearning.viewmodel.PlaceViewModel

/**
 * 作者 : xys
 * 时间 : 2022-05-16 11:21
 * 描述 : 描述
 */
class PlaceFragment : BaseFragment<PlaceViewModel, FragmentPlaceBinding>() {
    private lateinit var adapter: PlaceAdapter


    @SuppressLint("NotifyDataSetChanged")
    override fun initView(savedInstanceState: Bundle?) {
        if (activity is MainActivity && mViewModel.isPlaceSaved()) {
            val place = mViewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            //activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        mViewBinding.recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, mViewModel.placeList)
        mViewBinding.recyclerView.adapter = adapter
        mViewBinding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                mViewModel.searchPlaces(content)
            } else {
                mViewBinding.recyclerView.visibility = View.GONE
                mViewBinding.bgImageView.visibility = View.VISIBLE
                mViewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        mViewModel.placeLiveData.observe(this){ result->
            val places = result.getOrNull()
            if (places != null) {
                mViewBinding.recyclerView.visibility = View.VISIBLE
                mViewBinding.bgImageView.visibility = View.GONE
                mViewModel.placeList.clear()
                mViewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}