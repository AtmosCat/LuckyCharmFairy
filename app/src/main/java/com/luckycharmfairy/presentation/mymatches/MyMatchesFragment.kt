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
import com.luckycharmfairy.presentation.mymatches.matchdetail.MatchDetailFragment
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.EventDecorator
import com.luckycharmfairy.presentation.mymatches.addmatches.AddMyMatchOneFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar


class MyMatchesFragment : Fragment() {

    private var _binding: FragmentMyMatchesBinding? = null
    private val binding get() = _binding!!

    private var currentUserEmail: String = ""

    private var selectedSport = "전체 종목"
    private var selectedYear = CalendarDay.from(Calendar.getInstance()).year.toString()
    private var selectedMonth = String.format("%02d", CalendarDay.from(Calendar.getInstance()).month + 1)
    private var selectedDate = CalendarDay.from(Calendar.getInstance()).day.toString()
    private var selectedDay = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        0 -> "일"
        1 -> "월"
        2 -> "화"
        3 -> "수"
        4 -> "목"
        5 -> "금"
        6 -> "토"
        else -> ""
    }
    private var selectedMonthMatchdays: Int = -1
    private var todayMatches = mutableListOf<com.luckycharmfairy.data.model.Match>()


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

        val spinnerItems = listOf("전체 종목", "야구", "남자축구", "남자농구", "여자배구", "남자배구")
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
                    todayMatches = data
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = spinnerItems[0]
            }
        }

        binding.btnMatchRecord.setOnClickListener{

        }

        binding.btnMatchReport.setOnClickListener{

        }

//        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale("ko", "KR"))
//        val today = dateFormat.format(Calendar.getInstance())
//        selectedDate = today
//        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.calendar_item_spacing)
//        val recyclerView = binding.calendarMonthlyMatches.getChildAt(0) as RecyclerView
//        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(spacingInPixels))

        // 직관한 날짜 작은 점 표시
        val eventDays = mutableListOf<CalendarDay>()
        userViewModel.getSelectedMonthMatchdays(currentUserEmail, selectedSport, selectedYear, selectedMonth)
        userViewModel.selectedMonthMatchdays.observe(viewLifecycleOwner) { data ->
            val selectedMonthMatchdays = data

            if (selectedMonthMatchdays != null && selectedMonthMatchdays.size != 0) {
                selectedMonthMatchdays.forEach {
                    eventDays += (CalendarDay.from(selectedYear.toInt(), selectedMonth.toInt()-1, it.toInt()))
                }
            }
            var matchdaysCount = selectedMonthMatchdays?.size
            if (matchdaysCount == null) matchdaysCount == 0
            binding.tvMonthlyMatches.text = "이번 달에 ${selectedSport} 경기를 ${matchdaysCount}일 직관했어요!"
            val eventDecorator = EventDecorator(eventDays)
            binding.calendarMonthlyMatches.addDecorators(eventDecorator)
        }

        binding.calendarMonthlyMatches.setSelectedDate(CalendarDay.from(Calendar.getInstance()))
        binding.calendarMonthlyMatches.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            if (selected) {
                selectedYear = date.year.toString()
                selectedMonth = String.format("%02d", date.month + 1)
                selectedDate = date.day.toString()
                val calendar = Calendar.getInstance().apply {
                    set(date.year, date.month, date.day) // 선택한 날짜로 설정
                }
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                // 요일을 문자열로 변환
                selectedDay = when (dayOfWeek) {
                    Calendar.SUNDAY -> "일"
                    Calendar.MONDAY -> "월"
                    Calendar.TUESDAY -> "화"
                    Calendar.WEDNESDAY -> "수"
                    Calendar.THURSDAY -> "목"
                    Calendar.FRIDAY -> "금"
                    Calendar.SATURDAY -> "토"
                    else -> ""
                }
                userViewModel.getSelectedDateMatches(currentUserEmail, selectedSport, selectedYear, selectedMonth, selectedDate)
                userViewModel.selectedDayMatches.observe(viewLifecycleOwner) { data ->
                    myMatchesAdapter.submitList(data)
                    todayMatches = data
                }
            }
        })

        val matchDetailFragment = requireActivity().supportFragmentManager.findFragmentByTag("MatchDetailFragment")
        myMatchesAdapter.itemClick = object : MyMatchesAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val clickedMatch = todayMatches[position]
                val dataToSend = clickedMatch.id
                val matchDetail = MatchDetailFragment.newInstance(dataToSend)
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    hide(this@MyMatchesFragment)
                    add(R.id.main_frame, matchDetail)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        binding.btnAddMatchRecord.setOnClickListener{
            val addMyMatchOneFragment = requireActivity().supportFragmentManager.findFragmentByTag("AddMyMatchOneFragment")
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@MyMatchesFragment)
                add(R.id.main_frame, AddMyMatchOneFragment())
                addToBackStack(null)
                commit()
            }
        }

    }
    class VerticalSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = spacing // 아래쪽 간격 설정
        }
    }
}
