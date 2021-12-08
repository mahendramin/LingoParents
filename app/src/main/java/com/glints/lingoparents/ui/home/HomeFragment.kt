package com.glints.lingoparents.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.glints.lingoparents.R
import com.glints.lingoparents.data.model.LiveEventSliderItem
import com.glints.lingoparents.data.model.response.AllEventItem
import com.glints.lingoparents.data.model.response.DataItem
import com.glints.lingoparents.data.model.response.RecentInsightItem
import com.glints.lingoparents.databinding.FragmentHomeBinding
import com.glints.lingoparents.ui.home.adapter.ChildrenAdapter
import com.glints.lingoparents.ui.home.adapter.InsightSliderAdapter
import com.glints.lingoparents.ui.home.adapter.LiveEventSliderAdapter
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import com.opensooq.pluto.listeners.OnSlideChangeListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking


class HomeFragment : Fragment(R.layout.fragment_home), ChildrenAdapter.OnItemClickCallback {
    var parentIdValue = -1
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: HomeViewModel

    private lateinit var liveEventSliderAdapter: LiveEventSliderAdapter
    private lateinit var childrenAdapter: ChildrenAdapter
    private lateinit var insightSliderAdapter: InsightSliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater)
        tokenPreferences = TokenPreferences.getInstance(requireContext().dataStore)
        viewModel =
            ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this, arguments))[
                    HomeViewModel::class.java
            ]

        val recentInsightPlaceholder = mutableListOf(
            RecentInsightItem(
                "title",
                "content",
                "cover",
                1,


                ),
        )
        insightSliderAdapter = InsightSliderAdapter(
            recentInsightPlaceholder,
            object : OnItemClickListener<RecentInsightItem> {
                override fun onItemClicked(item: RecentInsightItem?, position: Int) {

                }
            }
        )
        val allEventPlaceholder = mutableListOf(
            AllEventItem("date", "title", "cover", "speaker photo", 50000, -1)
        )
        liveEventSliderAdapter = LiveEventSliderAdapter(
            allEventPlaceholder,
            object : OnItemClickListener<AllEventItem> {
                override fun onItemClicked(item: AllEventItem?, position: Int) {

                }
            }
        )

        binding.sliderLiveEvent.apply {
            create(liveEventSliderAdapter, lifecycle = lifecycle)
            setOnSlideChangeListener(object : OnSlideChangeListener {
                override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

                }
            })
        }

        viewModel.getAccessParentId().observe(viewLifecycleOwner) { parentId ->
            parentIdValue = parentId
        }


        //ini original
        viewModel.getAccessToken().observe(viewLifecycleOwner) { accessToken ->
            viewModel.getRecentInsight(HomeViewModel.INSIGHT_TYPE)
            viewModel.getAllEvent(HomeViewModel.EVENT_TYPE)
//            viewModel.getStudentList("he", 4)
            viewModel.getStudentList(HomeViewModel.STUDENTLIST_TYPE, parentIdValue)
        }


        binding.apply {
            rvChildren.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                childrenAdapter = ChildrenAdapter(this@HomeFragment)
                adapter = childrenAdapter
                isVerticalScrollBarEnabled = false
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.studentList.collect { event ->
                when (event) {
                    is HomeViewModel.StudentList.Loading -> {
                        showLoading(HomeViewModel.STUDENTLIST_TYPE, true)
                        showEmptyData(HomeViewModel.STUDENTLIST_TYPE, false)
                    }
                    is HomeViewModel.StudentList.Success -> {
                        Log.d("studn", event.list.toString())
                        childrenAdapter.submitList(event.list)
                        showLoading(HomeViewModel.STUDENTLIST_TYPE, false)
                    }
                    is HomeViewModel.StudentList.Error -> {
                        showLoading(HomeViewModel.STUDENTLIST_TYPE, false)
                        showEmptyData(HomeViewModel.STUDENTLIST_TYPE, true)
                    }
//                    is AllCoursesViewModel.AllCoursesEvent.NavigateToDetailLiveEventFragment -> {
//                        val action =
//                            LiveEventListFragmentDirections.actionLiveEventListFragmentToLiveEventDetailFragment(event.id)
//                        Log.d("IDEvent", event.id.toString())
//                        findNavController().navigate(action)
//                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.allEvent.collect { event ->
                when (event) {
                    is HomeViewModel.AllEvent.Loading -> {
                        showLoading(HomeViewModel.EVENT_TYPE, true)
                        showEmptyData(HomeViewModel.EVENT_TYPE, false)
                    }
                    is HomeViewModel.AllEvent.Success -> {
                        Toast.makeText(context, "INI BISA NIHHHHHH", Toast.LENGTH_SHORT).show()
                        showLoading(HomeViewModel.EVENT_TYPE, false)
                        liveEventSliderAdapter.submitList(event.list)
                        liveEventSliderAdapter = LiveEventSliderAdapter(
                            event.list,
                            object : OnItemClickListener<AllEventItem> {
                                override fun onItemClicked(item: AllEventItem?, position: Int) {

                                }
                            }
                        )
                        binding.sliderLiveEvent.apply {
                            create(liveEventSliderAdapter, lifecycle = lifecycle)
                            setOnSlideChangeListener(object : OnSlideChangeListener {
                                override fun onSlideChange(
                                    adapter: PlutoAdapter<*, *>,
                                    position: Int
                                ) {

                                }
                            })
                        }
                    }
                    is HomeViewModel.AllEvent.Error -> {
                        showLoading(HomeViewModel.EVENT_TYPE, false)
                        showEmptyData(HomeViewModel.EVENT_TYPE, true)
                        //showEmptyStudentList(false)
                    }

                }

            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recentInsight.collect { event ->
                when (event) {
                    is HomeViewModel.RecentInsight.Loading -> {
                        showLoading(HomeViewModel.INSIGHT_TYPE, true)
                        showEmptyData(HomeViewModel.INSIGHT_TYPE, false)
                    }
                    is HomeViewModel.RecentInsight.Success -> {
                        Log.d("dfdf", event.list.toString())
                        //Toast.makeText(context, "INI BISA NIHHHHHH", Toast.LENGTH_SHORT).show()
                        //childrenAdapter.submitList(event.list)
                        insightSliderAdapter.submitList(event.list)
                        insightSliderAdapter = InsightSliderAdapter(
                            event.list,
                            object : OnItemClickListener<RecentInsightItem> {
                                override fun onItemClicked(
                                    item: RecentInsightItem?,
                                    position: Int
                                ) {

                                }
                            }
                        )
                        binding.sliderInsight.apply {
                            create(insightSliderAdapter, lifecycle = lifecycle)
                            setOnSlideChangeListener(object : OnSlideChangeListener {
                                override fun onSlideChange(
                                    adapter: PlutoAdapter<*, *>,
                                    position: Int
                                ) {

                                }
                            })
                        }
                        showLoading(HomeViewModel.INSIGHT_TYPE, false)
                        //showEmptyStudentList(false)
                    }
                    is HomeViewModel.RecentInsight.Error -> {
                        //showEmptyStudentList(false)
                        showLoading(HomeViewModel.INSIGHT_TYPE, false)
                        showEmptyData(HomeViewModel.INSIGHT_TYPE, true)
                    }

                }

            }

        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showLoading(context: String, bool: Boolean) {
        binding.apply {
            if (context == HomeViewModel.INSIGHT_TYPE) {
                if (bool) {
                    sliderInsight.visibility = View.GONE
                    shimmerLayoutInsight.visibility = View.VISIBLE
                } else {
                    sliderInsight.visibility = View.VISIBLE
                    shimmerLayoutInsight.visibility = View.GONE
                }
            } else if (context == HomeViewModel.EVENT_TYPE) {
                if (bool) {
                    sliderLiveEvent.visibility = View.GONE
                    shimmerLayoutEvent.visibility = View.VISIBLE
                } else {
                    sliderLiveEvent.visibility = View.VISIBLE
                    shimmerLayoutEvent.visibility = View.GONE
                }
            } else if (context == HomeViewModel.STUDENTLIST_TYPE) {
                if (bool) {
                    rvChildren.visibility = View.GONE
                    shimmerLayoutChildren.visibility = View.VISIBLE
                } else {
                    rvChildren.visibility = View.VISIBLE
                    shimmerLayoutChildren.visibility = View.GONE
                }
            }

        }
    }

    private fun showEmptyData(context: String, bool: Boolean) {
        binding.apply {
            if (context == HomeViewModel.INSIGHT_TYPE) {
                if (bool) {
                    sliderInsight.visibility = View.GONE
                    ivNoRecentInsight.visibility = View.VISIBLE
                    tvNoRecentInsight.visibility = View.VISIBLE
                } else {
                    sliderInsight.visibility = View.VISIBLE
                    ivNoRecentInsight.visibility = View.GONE
                    tvNoRecentInsight.visibility = View.GONE
                }
            } else if (context == HomeViewModel.EVENT_TYPE) {
                if (bool) {
                    sliderLiveEvent.visibility = View.GONE
                    ivNoLiveEvent.visibility = View.VISIBLE
                    tvNoLiveEvent.visibility = View.VISIBLE
                } else {
                    sliderLiveEvent.visibility = View.VISIBLE
                    ivNoLiveEvent.visibility = View.GONE
                    tvNoLiveEvent.visibility = View.GONE
                }
            } else if (context == HomeViewModel.STUDENTLIST_TYPE) {
                if (bool) {
                    rvChildren.visibility = View.GONE
                    ivNoStudentList.visibility = View.VISIBLE
                    tvNoStudentList.visibility = View.VISIBLE
                } else {
                    rvChildren.visibility = View.VISIBLE
                    ivNoStudentList.visibility = View.GONE
                    tvNoStudentList.visibility = View.GONE
                }
            }

        }
    }

    override fun onItemClicked(children: DataItem) {
        Toast.makeText(context, children.studentId.toString(), Toast.LENGTH_SHORT).show()
    }
}