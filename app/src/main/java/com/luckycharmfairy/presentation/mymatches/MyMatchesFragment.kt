package com.luckycharmfairy.presentation.mymatches

import MatchReportFragment
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.mymatches.matchdetail.MatchDetailFragment
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.EventDecorator
import com.luckycharmfairy.presentation.UiState
import com.luckycharmfairy.presentation.mymatches.addmatches.AddMyMatchOneFragment
import com.luckycharmfairy.presentation.mypage.MyPageFragment
import com.luckycharmfairy.utils.DateTimeUtils
import com.luckycharmfairy.utils.SpinnerUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar


class MyMatchesFragment : Fragment() {

    private var _binding: FragmentMyMatchesBinding? = null
    private val binding get() = _binding!!

    private var currentUser = User()

    private var selectedSport = "전체 종목"
    private var selectedYear = CalendarDay.from(Calendar.getInstance()).year.toString()
    private var selectedMonth = String.format("%02d", CalendarDay.from(Calendar.getInstance()).month + 1)
    private var selectedDay = CalendarDay.from(Calendar.getInstance()).day.toString()
    private lateinit var selectedDayOfWeek: String
    private var selectedDayMatches = mutableListOf<Match>()

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

        /** 유저 정보 불러와서 렌더링  **/
        initViewModel()

        /* 초기 세팅 */
        setMySportsSpinner()
        setCalendar()

        /* 유저 액션 설정 */
        setCalendarClickListener()
        setMySportsSelectedListener()
        setMyMatchesClickListener()
        setFragmentMovers()
    }

    private fun setCalendar() {
        addCalendarDot()
        binding.calendarMonthlyMatches.setSelectedDate(CalendarDay.from(Calendar.getInstance()))
    }

    private fun setCalendarClickListener() {
        // 월 변경
        binding.calendarMonthlyMatches.setOnMonthChangedListener { widget, date ->
            val calendar = Calendar.getInstance()
            calendar.time = date.date
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            selectedYear = year.toString()
            selectedMonth = month.toString()
            addCalendarDot()
        }

        // 일 변경
        binding.calendarMonthlyMatches.setOnDateChangedListener( { widget, date, selected ->
            if (selected) {
                selectedYear = date.year.toString()
                selectedMonth = String.format("%02d", date.month + 1)
                selectedDay = date.day.toString()
                val calendar = Calendar.getInstance().apply {
                    set(date.year, date.month, date.day) // 선택한 날짜로 설정
                }
                selectedDayOfWeek = DateTimeUtils.dayOfWeekFormatter(calendar.get(Calendar.DAY_OF_WEEK))

                selectedDayMatches = getSelectedDayMatches(selectedSport, selectedYear, selectedMonth, selectedDay)
                myMatchesAdapter.submitList(selectedDayMatches)
                if (selectedDayMatches.size == 0) binding.tvNoticeNoMatches.visibility = View.VISIBLE
                else binding.tvNoticeNoMatches.visibility = View.GONE
            }
        })
    }

    private fun setMySportsSpinner() {
        binding.recyclerviewMatchRecords.adapter = myMatchesAdapter
        binding.recyclerviewMatchRecords.layoutManager = LinearLayoutManager(requireContext())
        val spinnerMySportsItems = currentUser.mysports
        SpinnerUtils.setSpinnerAdapter(binding.spinnerSport, requireActivity(), spinnerMySportsItems)
    }

    private fun setMySportsSelectedListener() {
        val spinnerMySportsItems = currentUser.mysports
        spinnerMySportsItems.add(0, "전체 종목")
        binding.spinnerSport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSport = spinnerMySportsItems[position]
                selectedDayMatches = getSelectedDayMatches(selectedSport, selectedYear, selectedMonth, selectedDay)
                myMatchesAdapter.submitList(selectedDayMatches)
                addCalendarDot()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = spinnerMySportsItems[0]
            }
        }
    }

    private fun addCalendarDot() {
        val selectedMonthMatchDays = getSelectedMonthMatchDays(selectedSport, selectedYear, selectedMonth)

        val eventDays = mutableListOf<CalendarDay>()
        selectedMonthMatchDays.forEach {
            eventDays += (CalendarDay.from(selectedYear.toInt(), selectedMonth.toInt()-1, it.toInt()))
        }
        val matchDaysCount = selectedMonthMatchDays?.size
        binding.tvMonthlyMatches.text = "${selectedMonth}월에 ${selectedSport} 경기를 ${matchDaysCount}일 직관했어요!"
        val eventDecorator = EventDecorator(eventDays)
        if (eventDays.size != 0) {
            binding.calendarMonthlyMatches.removeDecorators()
            binding.calendarMonthlyMatches.addDecorators(eventDecorator)
        } else {
            binding.calendarMonthlyMatches.removeDecorators()
        }
    }

    private fun setMyMatchesClickListener() {
        myMatchesAdapter.itemClick = object : MyMatchesAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val clickedMatch = selectedDayMatches[position]
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
    }

    private fun getSelectedMonthMatchDays (
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

    fun getSelectedDayMatches(selectedSport: String, selectedYear: String, selectedMonth: String, selectedDate: String): MutableList<Match> {
        val matches = if (selectedSport != "전체 종목") {
            currentUser.matches.filter {
                it.sport == selectedSport && it.year == selectedYear && it.month == selectedMonth && it.date == selectedDate
            }.toMutableList()
        } else {
            currentUser.matches.filter {
                it.year == selectedYear && it.month == selectedMonth && it.date == selectedDate
            }.toMutableList()
        }
        selectedDayMatches = matches
        return matches
    }

    private fun setFragmentMovers() {
        // 직관 리포트
        binding.btnMatchReport.setOnClickListener{
            if (currentUser.matches.size < 10) {
                Toast.makeText(requireContext(), "10경기 이상 직관해야 통계를 보실 수 있어요!",Toast.LENGTH_SHORT).show()
            } else {
                moveFragment(MatchReportFragment(),"MatchReportFragment")
            }
        }

        // 직관 기록 추가
        binding.btnAddMatchRecord.setOnClickListener{
            moveFragment(AddMyMatchOneFragment(), "AddMyMatchOneFragment")
        }

        // 마이페이지
        binding.btnTabMypage.setOnClickListener{
            moveFragment(MyPageFragment(), "MyPageFragment")
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

    private fun initViewModel() {
        userViewModel.setCurrentUser("dd@gmail.com")
        userViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.bgProgressBar.visibility = View.VISIBLE
                    binding.tvProgressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.bgProgressBar.visibility = View.GONE
                    binding.tvProgressBar.visibility = View.GONE
                    currentUser = uiState.data as User
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
