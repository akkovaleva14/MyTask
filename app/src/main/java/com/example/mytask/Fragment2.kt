package com.example.mytask

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mytask.databinding.Fragment2Binding
import coil.load

class Fragment2 : Fragment() {

    private lateinit var binding: Fragment2Binding
    private var originalDescription: String? = null
    private var currentItem: RecyclerViewItem.Card? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Извлекаем данные из аргументов, используя Serializable
        currentItem = arguments?.getSerializable("item") as? RecyclerViewItem.Card
        originalDescription = currentItem?.description

        currentItem?.let {
            binding.cardImageView.load(it.imageUrl) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.error)
            }
            binding.descriptionEditText.setText(it.description)
        }

        binding.saveButton.setOnClickListener {
            saveChanges()
        }

        binding.backButton.setOnClickListener {
            checkForUnsavedChanges()
        }
    }

    private fun saveChanges() {
        currentItem?.description = binding.descriptionEditText.text.toString()
        findNavController().navigateUp()
    }

    private fun checkForUnsavedChanges() {
        val currentDescription = binding.descriptionEditText.text.toString()
        if (currentDescription != originalDescription) {
            showUnsavedChangesDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showUnsavedChangesDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Выйти без изменений?")
            .setPositiveButton("Да") { _, _ -> findNavController().navigateUp() }
            .setNegativeButton("Нет", null)
            .show()
    }
}
