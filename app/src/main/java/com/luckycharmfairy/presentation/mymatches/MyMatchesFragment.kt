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
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.mymatches.matchdetail.MatchDetailFragment
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.EventDecorator
import com.luckycharmfairy.presentation.mymatches.addmatches.AddMyMatchOneFragment
import com.luckycharmfairy.presentation.mypage.MyPageFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
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

        currentUser = userViewModel.currentUser.value!!
        currentUserEmail = currentUser.email

//        userViewModel.currentUser.observe(viewLifecycleOwner) { data ->
//            if (data != null) {
//                currentUser = data
//            }
//            if (data != null) {
//                currentUserEmail = data.email
//            }
//        }

        binding.recyclerviewMatchRecords.adapter = myMatchesAdapter
        binding.recyclerviewMatchRecords.layoutManager = LinearLayoutManager(requireContext())

        val spinnerItems = currentUser.mysports
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
                addCalendarDot()
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

        addCalendarDot()

        binding.calendarMonthlyMatches.setSelectedDate(CalendarDay.from(Calendar.getInstance()))
//        userViewModel.getSelectedDateMatches(currentUserEmail, selectedSport, selectedYear, selectedMonth, selectedDate)
//        userViewModel.selectedDayMatches.observe(viewLifecycleOwner) { data ->
//            myMatchesAdapter.submitList(data)
//            if (data.size == 0) binding.tvNoticeNoMatches.visibility = View.VISIBLE
//            else binding.tvNoticeNoMatches.visibility = View.GONE
//            todayMatches = data
//        }
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
                    if (data.size == 0) binding.tvNoticeNoMatches.visibility = View.VISIBLE
                    else binding.tvNoticeNoMatches.visibility = View.GONE
                    todayMatches = data
                }
            }
        })

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

        val myPageFragment = requireActivity().supportFragmentManager.findFragmentByTag("MyPageFragment")
        binding.btnTabMypage.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@MyMatchesFragment)
                if (myPageFragment == null) {
                    add(R.id.main_frame, MyPageFragment(), "MyPageFragment")
                } else {
                    show(myPageFragment)
                }
                addToBackStack(null)
                commit()
            }
        }

        val matchReportFragment = requireActivity().supportFragmentManager.findFragmentByTag("MatchReportFragment")
        binding.btnMatchReport.setOnClickListener{
            if (currentUser.matches.size < 10) {
                Toast.makeText(requireContext(), "10경기 이상 직관해야 통계를 보실 수 있어요!",Toast.LENGTH_SHORT).show()
            } else {
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    hide(this@MyMatchesFragment)
                    if (matchReportFragment == null) {
                        add(R.id.main_frame, MatchReportFragment(), "MatchReportFragment")
                    } else {
                        show(matchReportFragment)
                    }
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

    private fun addCalendarDot() {
        userViewModel.getSelectedMonthMatchdays(currentUserEmail, selectedSport, selectedYear, selectedMonth)
        userViewModel.selectedMonthMatchdays.observe(viewLifecycleOwner) { data ->
            val selectedMonthMatchdays = data

            val eventDays = mutableListOf<CalendarDay>()
            selectedMonthMatchdays?.forEach {
                eventDays += (CalendarDay.from(selectedYear.toInt(), selectedMonth.toInt()-1, it.toInt()))
            }
            val matchdaysCount = selectedMonthMatchdays?.size
            binding.tvMonthlyMatches.text = "이번 달에 ${selectedSport} 경기를 ${matchdaysCount}일 직관했어요!"
            val eventDecorator = EventDecorator(eventDays)
            if (eventDays.size != 0) {
                binding.calendarMonthlyMatches.removeDecorators()
                binding.calendarMonthlyMatches.addDecorators(eventDecorator)
            } else {
                binding.calendarMonthlyMatches.removeDecorators()
            }
        }
    }

    class VerticalSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = spacing // 아래쪽 간격 설정
        }
    }
}
