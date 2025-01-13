package com.luckycharmfairy.presentation.mymatches

import MatchReportFragment
import android.content.ContentValues.TAG
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.mymatches.matchdetail.MatchDetailFragment
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMatchReportBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.EventDecorator
import com.luckycharmfairy.presentation.UiState
import com.luckycharmfairy.presentation.mymatches.addmatches.AddMyMatchOneFragment
import com.luckycharmfairy.presentation.mypage.MyPageFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class MyMatchesFragment : Fragment() {

    private var _binding: FragmentMyMatchesBinding? = null
    private val binding get() = _binding!!
    private var currentUser = User()
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

        initViewModel()

        binding.recyclerviewMatchRecords.adapter = myMatchesAdapter
        binding.recyclerviewMatchRecords.layoutManager = LinearLayoutManager(requireContext())

        val spinnerItems = currentUser.mysports
        spinnerItems.add(0, "전체 종목")
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
                val selectedDateMatchesMatches = getSelectedDateMatches(selectedSport, selectedYear, selectedMonth, selectedDate)
                myMatchesAdapter.submitList(selectedDateMatchesMatches)
                todayMatches = selectedDateMatchesMatches
                addCalendarDot()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = spinnerItems[0]
            }
        }

//        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale("ko", "KR"))
//        val today = dateFormat.format(Calendar.getInstance())
//        selectedDate = today
//        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.calendar_item_spacing)
//        val recyclerView = binding.calendarMonthlyMatches.getChildAt(0) as RecyclerView
//        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(spacingInPixels))

        addCalendarDot()

        binding.calendarMonthlyMatches.setSelectedDate(CalendarDay.from(Calendar.getInstance()))

        binding.calendarMonthlyMatches.setOnDateChangedListener( { widget, date, selected ->
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
                val selectedDateMatches = getSelectedDateMatches(selectedSport, selectedYear, selectedMonth, selectedDate)
                myMatchesAdapter.submitList(selectedDateMatches)
                if (selectedDateMatches.size == 0) binding.tvNoticeNoMatches.visibility = View.VISIBLE
                else binding.tvNoticeNoMatches.visibility = View.GONE
                todayMatches = selectedDateMatches
            }
        })

        binding.calendarMonthlyMatches.setOnMonthChangedListener { widget, date ->
            val calendar = Calendar.getInstance()
            calendar.time = date.date
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            selectedYear = year.toString()
            selectedMonth = month.toString()
            addCalendarDot()
        }

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

        binding.btnTabMypage.setOnClickListener{
            moveFragment(MyPageFragment(), "MyPageFragment")
        }

        binding.btnMatchReport.setOnClickListener{
            if (currentUser.matches.size < 10) {
                Toast.makeText(requireContext(), "10경기 이상 직관해야 통계를 보실 수 있어요!",Toast.LENGTH_SHORT).show()
            } else {
                moveFragment(MatchReportFragment(),"MatchReportFragment")
            }
        }

        binding.btnAddMatchRecord.setOnClickListener{
            moveFragment(AddMyMatchOneFragment(), "AddMyMatchOneFragment")
        }

    }

    private fun addCalendarDot() {
        val selectedMonthMatchDays = getSelectedMonthMatchdays(selectedSport, selectedYear, selectedMonth)

        val eventDays = mutableListOf<CalendarDay>()
        selectedMonthMatchDays.forEach {
            eventDays += (CalendarDay.from(selectedYear.toInt(), selectedMonth.toInt()-1, it.toInt()))
        }
        val matchdaysCount = selectedMonthMatchDays?.size
        binding.tvMonthlyMatches.text = "${selectedMonth}월에 ${selectedSport} 경기를 ${matchdaysCount}일 직관했어요!"
        val eventDecorator = EventDecorator(eventDays)
        if (eventDays.size != 0) {
            binding.calendarMonthlyMatches.removeDecorators()
            binding.calendarMonthlyMatches.addDecorators(eventDecorator)
        } else {
            binding.calendarMonthlyMatches.removeDecorators()
        }
    }

    class VerticalSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = spacing // 아래쪽 간격 설정
        }
    }

    private fun getSelectedMonthMatchdays (
        selectedSport: String, selectedYear: String, selectedMonth: String) : MutableList<String> {
        val selectedMonthMatches = if (selectedSport != "전체 종목") {
            currentUser.matches.filter {
                it.sport == selectedSport &&
                        it.year == selectedYear &&
                        it.month == selectedMonth
            }.toMutableList()
        } else {
            currentUser.matches.filter {
                it.year == selectedYear &&
                        it.month == selectedMonth
            }.toMutableList()
        }
        val matchdays = mutableListOf<String>()
        selectedMonthMatches.forEach {
            if (it.date !in matchdays) {
                matchdays.add(it.date)
            }
        }
        return matchdays
    }

    fun getSelectedDateMatches(selectedSport: String, selectedYear: String, selectedMonth: String, selectedDate: String): MutableList<Match> {
        val selectedDateMatches = if (selectedSport != "전체 종목") {
            currentUser.matches.filter {
                it.sport == selectedSport && it.year == selectedYear && it.month == selectedMonth && it.date == selectedDate
            }.toMutableList()
        } else {
            currentUser.matches.filter {
                it.year == selectedYear && it.month == selectedMonth && it.date == selectedDate
            }.toMutableList()
        }
        return selectedDateMatches
    }


    private fun initViewModel() {
        userViewModel.setCurrentUser("dd@gmail.com")
        userViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.bgProgressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.bgProgressBar.visibility = View.GONE
                    currentUser = uiState.data as User
                    currentUserEmail = currentUser.email
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun moveFragment(fragment: Fragment, fragmentTag: String) {
        val fragmentToMove = requireActivity().supportFragmentManager.findFragmentByTag(fragmentTag)
        requireActivity().supportFragmentManager.beginTransaction().apply {
            hide(this@MyMatchesFragment)
            if (fragmentToMove == null) {
                add(R.id.main_frame, fragment, fragmentTag)
            } else {
                show(fragmentToMove)
            }
            addToBackStack(null)
            commit()
        }
    }
}
