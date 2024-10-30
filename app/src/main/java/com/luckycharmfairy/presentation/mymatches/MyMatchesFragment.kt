package com.luckycharmfairy.presentation.mymatches

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.luckycharmfairy.R
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.EventDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.text.SimpleDateFormat
import java.util.Calendar


class MyMatchesFragment : Fragment() {

    private var _binding: FragmentMyMatchesBinding? = null
    private val binding get() = _binding!!

    private var selectedSport = ""
    private var selectedYear = ""
    private var selectedMonth = ""
    private var selectedDate = ""

    private var selectedMonthMatchdays: Int = -1


    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private val myMatchesAdapter by lazy { MyMatchesAdapter(userViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerviewMatchRecords.adapter = myMatchesAdapter
        binding.recyclerviewMatchRecords.layoutManager = LinearLayoutManager(requireContext())

        val spinnerItems = listOf("전체 종목", "야구", "축구", "농구", "여자배구", "남자배구")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerItems)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerSport.adapter = spinnerAdapter

        binding.spinnerSport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSport = spinnerItems[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = "전체 종목"
            }
        }

        binding.btnMatchRecord.setOnClickListener{

        }

        binding.btnMatchReport.setOnClickListener{

        }

//        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale("ko", "KR"))
//        val today = dateFormat.format(Calendar.getInstance())
//        selectedDate = today
        binding.calendarMonthlyMatches.setSelectedDate(CalendarDay.from(Calendar.getInstance())) // 오늘 날짜 선택
        binding.calendarMonthlyMatches.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            if (selected) {
                selectedYear = date.year.toString()
                selectedMonth = date.month.toString()
                selectedDate = date.date.toString()
            }
        })

        // 직관한 날짜 작은 점 표시
        val eventDays = mutableListOf<CalendarDay>()
        userViewModel.getSelectedMonthMatchdays(userViewModel.currentUser.value?.email!!, selectedYear, selectedMonth)
        val selectedMonthMatchdays = userViewModel.selectedMonthMatchdays.value
        selectedMonthMatchdays!!.forEach {
            eventDays.add(CalendarDay.from(selectedYear.toInt(), selectedMonth.toInt(), it.toInt()))
        }
        binding.tvMonthlyMatches.text = "${selectedYear}년 $${selectedMonth}월에 ${selectedSport} 경기를 ${selectedMonthMatchdays.size}일 직관했어요!"

        val eventDecorator = EventDecorator(eventDays)
        binding.calendarMonthlyMatches.addDecorators(eventDecorator)





    }
}
