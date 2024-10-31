package com.luckycharmfairy.presentation.mymatches

import android.graphics.Rect
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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.luckycharmfairy.R
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.EventDecorator
import com.luckycharmfairy.presentation.mymatches.addmatches.ViewPagerAdapter
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar


class MyMatchesFragment : Fragment() {

    private var _binding: FragmentMyMatchesBinding? = null
    private val binding get() = _binding!!

    private var currentUserEmail: String = ""

    private var selectedSport = ""
    private var selectedYear = CalendarDay.from(Calendar.getInstance()).year.toString()
    private var selectedMonth = CalendarDay.from(Calendar.getInstance()).month.toString()
    private var selectedDate = CalendarDay.from(Calendar.getInstance()).date.toString()
    private var selectedDay = CalendarDay.from(Calendar.getInstance()).day.toString()

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

        currentUserEmail = userViewModel.currentUser.value!!.email

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
                userViewModel.getSelectedDateMatches(currentUserEmail, selectedSport, selectedYear, selectedMonth, selectedDate)
                userViewModel.selectedDayMatches.observe(viewLifecycleOwner) { data ->
                    myMatchesAdapter.submitList(data)
                }
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
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.calendar_item_spacing)
        val recyclerView = binding.calendarMonthlyMatches.getChildAt(0) as RecyclerView
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(spacingInPixels))

        binding.calendarMonthlyMatches.setSelectedDate(CalendarDay.from(Calendar.getInstance()))
        binding.calendarMonthlyMatches.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            if (selected) {
                selectedYear = date.year.toString()
                selectedMonth = String.format("%02d", date.month + 1)
                selectedDate = String.format("%02d", date.date)
                selectedDay = when (date.day) {
                    1 -> "일"
                    2 -> "월"
                    3 -> "화"
                    4 -> "수"
                    5 -> "목"
                    6 -> "금"
                    7 -> "토"
                    else -> ""
                }
            }

            // 직관한 날짜 작은 점 표시
            val eventDays = mutableListOf<CalendarDay>()
            userViewModel.getSelectedMonthMatchdays(currentUserEmail, selectedSport, selectedYear, selectedMonth)
            val selectedMonthMatchdays = userViewModel.selectedMonthMatchdays.value
            selectedMonthMatchdays!!.forEach {
                eventDays.add(CalendarDay.from(selectedYear.toInt(), selectedMonth.toInt(), it.toInt()))
            }
            binding.tvMonthlyMatches.text = "${selectedYear}년 $${selectedMonth}월에 ${selectedSport} 경기를 ${selectedMonthMatchdays.size}일 직관했어요!"
            val eventDecorator = EventDecorator(eventDays)
            binding.calendarMonthlyMatches.addDecorators(eventDecorator)

            userViewModel.getSelectedDateMatches(currentUserEmail, selectedSport, selectedYear, selectedMonth, selectedDate)
            userViewModel.selectedDayMatches.observe(viewLifecycleOwner) { data ->
                myMatchesAdapter.submitList(data)
            }

        })

        val viewPager: ViewPager2 = binding.viewpagerAddMyMatch
        viewPager.visibility = View.GONE
        val viewPagerAdapter = ViewPagerAdapter(requireActivity())
        viewPager.adapter = viewPagerAdapter
        binding.btnAddMatchRecord.setOnClickListener{
            viewPager.currentItem = 0
            viewPager.visibility = View.VISIBLE
        }





    }
    class VerticalSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = spacing // 아래쪽 간격 설정
        }
    }
}
