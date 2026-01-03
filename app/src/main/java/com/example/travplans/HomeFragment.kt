package com.example.travplans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travplans.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var itineraryDays: MutableList<ItineraryDay>
    private lateinit var adapter: ItineraryAdapter
    private lateinit var repository: ItineraryRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itineraryDao = AppDatabase.getDatabase(requireContext().applicationContext).itineraryDao()
        repository = ItineraryRepository(itineraryDao)

        itineraryDays = mutableListOf()
        adapter = ItineraryAdapter(itineraryDays) { itineraryDay, position ->
            // Handle item click
        }
        binding.itineraryRecyclerView.adapter = adapter
        binding.itineraryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            itineraryDays.addAll(repository.getAll())
            adapter.notifyDataSetChanged()
        }

        binding.addItineraryFab.setOnClickListener {
            // Handle fab click
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}