package com.example.workoutapp.news

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workoutapp.R
import com.example.workoutapp.databinding.FragmentNewsBinding
import com.example.workoutapp.network.NetworkNews
import com.example.workoutapp.network.NetworkService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment(), NewsAdapter.NewsClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mCurrentLocation: Location? = null
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    lateinit var newsAdapter: NewsAdapter

    private val viewModel by lazy{
        val viewModelFactory = NewsViewModelFactory(requireActivity().application)
        ViewModelProvider(
                this, viewModelFactory
        ).get(NewsViewModel::class.java)
    }

    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        callApi()

        newsAdapter = NewsAdapter(this)
        binding.recyclerViewNews.adapter = newsAdapter
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(context)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroy() {
        mCompositeDisposable?.clear()
        super.onDestroy()

        _binding = null
    }


    fun callApi(){
        mCompositeDisposable?.add(NetworkService.newsService.getNews("id", "sports", NetworkService.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ items ->
                    newsAdapter.submitList(items.articles)
                },{}))
    }

    override fun onClick(item: NetworkNews) {
        findNavController().navigate(NewsFragmentDirections.actionNewsPageToWebViewPage(item))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment NewsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            NewsFragment()
    }
}