package com.example.mytask

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytask.databinding.Fragment1Binding
import kotlinx.coroutines.launch

class Fragment1 : Fragment() {

    private lateinit var binding: Fragment1Binding
    private val viewModel by activityViewModels<MainViewModel>()
    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(SpacingItemDecorator(64, 128, 16))

        val adapter = RecyclerViewAdapter({ item ->
            when (item) {
                is RecyclerViewItem.Card -> {
                    val bundle = Bundle().apply {
                        putSerializable("item", item)
                    }
                    findNavController().navigate(R.id.action_fragment1_to_fragment2, bundle)
                }

                else -> {
                    // do nothing for other types
                }
            }
        }, requireContext())


        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { items ->
                    adapter.submitList(items)
                }
            }
        }

        binding.refreshButton.setOnClickListener {
            showLoadingDialog()
            setRecyclerViewEnabled(false)

            viewModel.refreshItems { updatedItems ->
                hideLoadingDialog()
                setRecyclerViewEnabled(true)
                adapter.submitList(updatedItems)
            }
        }
    }

    private fun setRecyclerViewEnabled(enabled: Boolean) {
        binding.recyclerView.isEnabled = enabled
        binding.recyclerView.alpha = if (enabled) 1f else 0.5f

        for (i in 0 until binding.recyclerView.childCount) {
            binding.recyclerView.getChildAt(i).isEnabled = enabled
        }
    }

    private fun showLoadingDialog() {
        loadingDialog = Dialog(requireContext())
        loadingDialog?.setContentView(R.layout.dialog_loading)
        loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
}
