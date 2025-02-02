package com.luckycharmfairy.presentation.mymatches.mysports

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMySportsBinding

class MySportsFragment : Fragment() {

    lateinit var binding : FragmentMySportsBinding

    private var currentUser = User()
    private var currentUserMySports = mutableListOf<String>()

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private val mySportsAdapter by lazy { MySportsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySportsBinding.inflate(inflater, container, false)

        binding.recyclerviewMySports.adapter = mySportsAdapter
        binding.recyclerviewMySports.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        currentUserMySports = userViewModel.currentUser.value!!.mysports
        mySportsAdapter.submitList(currentUserMySports)
        userViewModel.currentUser.observe(viewLifecycleOwner) { data ->
            currentUser = data!!
            currentUserMySports = currentUser.mysports
            mySportsAdapter.submitList(currentUserMySports)
            mySportsAdapter.updateData()
        }

        val addSportDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_my_sport, null)
        val etNewSport = addSportDialogView.findViewById<EditText>(R.id.et_new_sport)

        binding.btnAddMySport.setOnClickListener{
            if (addSportDialogView.parent != null) {
                (addSportDialogView.parent as ViewGroup).removeView(addSportDialogView)
            }
            etNewSport.setText("")
            AlertDialog.Builder(requireContext())
                .setView(addSportDialogView)
                .setPositiveButton("확인") { dialog, _ ->
                    val newSport = etNewSport.text.toString()
                    currentUserMySports += newSport
                    userViewModel.editMySport(currentUserMySports)
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "종목이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    mySportsAdapter.submitList(currentUserMySports)
                }
                .setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        mySportsAdapter.itemClick = object : MySportsAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle("삭제")
                    .setMessage("종목을 삭제할까요?")
                    .setPositiveButton("확인") { dialog, _ ->
                        val sportToDelete = currentUserMySports[position]
                        currentUserMySports -= sportToDelete
                        userViewModel.editMySport(currentUserMySports)
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "종목이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        mySportsAdapter.submitList(currentUserMySports)
                    }
                    .setNegativeButton("취소") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

    }
}