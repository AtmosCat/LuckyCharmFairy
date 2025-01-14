package com.luckycharmfairy.presentation.mymatches.addmatches

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.replace
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.model.Team
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.baseballLocations
import com.luckycharmfairy.data.model.baseballTeamNames
import com.luckycharmfairy.data.model.baseballTeams
import com.luckycharmfairy.data.model.menBasketballLocations
import com.luckycharmfairy.data.model.menBasketballTeamNames
import com.luckycharmfairy.data.model.menBasketballTeams
import com.luckycharmfairy.data.model.menFootballLocations
import com.luckycharmfairy.data.model.menFootballTeamNames
import com.luckycharmfairy.data.model.menFootballTeams
import com.luckycharmfairy.data.model.menVolleyballLocations
import com.luckycharmfairy.data.model.menVolleyballTeamNames
import com.luckycharmfairy.data.model.menVolleyballTeams
import com.luckycharmfairy.data.model.womenBasketballTeamNames
import com.luckycharmfairy.data.model.womenBasketballTeams
import com.luckycharmfairy.data.model.womenVolleyballLocations
import com.luckycharmfairy.data.model.womenVolleyballTeamNames
import com.luckycharmfairy.data.model.womenVolleyballTeams
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentAddMyMatchOneBinding
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMyMatchesBinding
import com.luckycharmfairy.presentation.mymatches.MyMatchesFragment
import com.luckycharmfairy.presentation.mymatches.mysports.MySportsFragment
import com.luckycharmfairy.utils.DateTimeUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar
import kotlin.random.Random

class AddMyMatchOneFragment : Fragment() {

    lateinit var binding : FragmentAddMyMatchOneBinding

    private var currentUser: User = User()
    private var selectedSport = ""
    private var selectedSportTeams = listOf<Team>()
    private var selectedSportTeamNames = listOf<String>()
    private var selectedYear = CalendarDay.from(Calendar.getInstance()).year.toString()
    private var selectedMonth = String.format("%02d", CalendarDay.from(Calendar.getInstance()).month + 1)
    private var selectedDay = CalendarDay.from(Calendar.getInstance()).day.toString()
    private var selectedDayOfWeek = DateTimeUtils.dayOfWeekFormatter(Calendar.DAY_OF_WEEK)
    private var selectedTime = "00:00"
    private var selectedLocation = ""
    private var selectedWeather = ""
    private var selectedFeeling = ""
    private var selectedHomeOrAway = ""
    private var selectedMyteam = Team()
    private var selectedHomeTeamName = ""
    private var selectedAwayTeamName = ""
    private var selectedHomeTeam = Team()
    private var selectedAwayTeam = Team()
    private var selectedHomescore = -1
    private var selectedAwayscore = -1
    private var selectedResult = ""
    private var selectedMvp = ""

    private var weatherList = listOf(
        "sunny",
        "sunny_cloudy",
        "cloudy",
        "rainy",
        "snowy"
    )
    private lateinit var weatherButtonList : List<ImageView>
    private lateinit var weatherButtonBackgroundList : List<View>

    private var feelingList = listOf(
        "happy",
        "lovely",
        "soso",
        "sad",
        "angry"
    )

    private lateinit var feelingButtonList : List<ImageView>
    private lateinit var feelingButtonBackgroundList : List<View>

    private val teamSelectionAdapter by lazy { TeamSelectionAdapter() }

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMyMatchOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = userViewModel.getCurrentUser()!!

        val myMatchesFragment = requireActivity().supportFragmentManager.findFragmentByTag("MyMatchesFragment")
        val addMyMatchTwoFragment = requireActivity().supportFragmentManager.findFragmentByTag("AddMyMatchTwoFragment")
        binding.btnClose.setOnClickListener{
            userViewModel.initializeTemporaryImageUrls()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@AddMyMatchOneFragment)
                if (addMyMatchTwoFragment != null) {
                    hide(addMyMatchTwoFragment)
                }
                if (myMatchesFragment != null) {
                    show(myMatchesFragment)
                } else {
                    add(R.id.main_frame, MyMatchesFragment(), "MyMatchesFragment")
                }
                addToBackStack(null)
                commit()
            }
        }

        val spinnerSports = currentUser.mysports.drop(1)
        binding.btnAddSports.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().apply {
                hide(this@AddMyMatchOneFragment)
                add(R.id.main_frame, MySportsFragment())
                addToBackStack(null)
                commit()
            }
        }
        val spinnerSportsAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerSports)
        spinnerSportsAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerSports.adapter = spinnerSportsAdapter

        binding.spinnerSports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSport = spinnerSports[position]
                when (selectedSport) {
                    "야구" -> {
                        selectedSportTeamNames = baseballTeamNames
                        selectedSportTeams = baseballTeams
                    }
                    "남자축구" -> {
                        selectedSportTeamNames = menFootballTeamNames
                        selectedSportTeams = menFootballTeams
                    }
                    "남자농구" -> {
                        selectedSportTeamNames = menBasketballTeamNames
                        selectedSportTeams = menBasketballTeams
                    }
                    "남자배구" -> {
                        selectedSportTeamNames = menVolleyballTeamNames
                        selectedSportTeams = menVolleyballTeams
                    }
                    "여자배구" -> {
                        selectedSportTeamNames = womenVolleyballTeamNames
                        selectedSportTeams = womenVolleyballTeams
                    }
                    "여자농구" -> {
                        selectedSportTeamNames = womenBasketballTeamNames
                        selectedSportTeams = womenBasketballTeams
                    }
                    else -> selectedSportTeamNames = listOf("직접 입력")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = ""
            }
        }

        binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDay}일 (${selectedDayOfWeek})")

        binding.calendarMatchdaySelector.setSelectedDate(CalendarDay.from(Calendar.getInstance())) // 기본 오늘 설정
        binding.btnDate.setOnClickListener{
            binding.calendarMatchdaySelector.visibility = View.VISIBLE
            binding.calendarMatchdaySelector.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
                if (selected) {
                    selectedYear = date.year.toString()
                    selectedMonth = String.format("%02d", date.month + 1)
                    selectedDay = date.day.toString()
                    val calendar = Calendar.getInstance().apply {
                        set(date.year, date.month, date.day) // 선택한 날짜로 설정
                    }
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                    // 요일을 문자열로 변환
                    selectedDayOfWeek = when (dayOfWeek) {
                        Calendar.SUNDAY -> "일"
                        Calendar.MONDAY -> "월"
                        Calendar.TUESDAY -> "화"
                        Calendar.WEDNESDAY -> "수"
                        Calendar.THURSDAY -> "목"
                        Calendar.FRIDAY -> "금"
                        Calendar.SATURDAY -> "토"
                        else -> ""
                    }
                    binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDay}일 (${selectedDayOfWeek})")
                    binding.calendarMatchdaySelector.visibility = View.GONE
                }
            })
        }

        binding.btnTime.setOnClickListener{
            showTimePickerDialog()
        }

        weatherButtonList = listOf(binding.btnSunny, binding.btnSunnyCloudy, binding.btnCloudy, binding.btnRainy, binding.btnSnowy)
        weatherButtonBackgroundList = listOf(binding.viewSunnyBackground, binding.viewSunnyCloudyBackground,
                binding.viewCloudyBackground, binding.viewRainyBackground, binding.viewSnowyBackground)
        weatherButtonList.forEach{ weatherClicker(it, weatherButtonList, weatherButtonBackgroundList) }

        feelingButtonList = listOf(binding.btnHappy, binding.btnLovely, binding.btnSoso, binding.btnSad, binding.btnAngry)
        feelingButtonBackgroundList = listOf(binding.viewHappyBackground, binding.viewLovelyBackground, binding.viewSosoBackground,
                binding.viewSadBackground, binding.viewAngryBackground)
        feelingButtonList.forEach{ feelingClicker(it, feelingButtonList, feelingButtonBackgroundList) }

        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀", "없음")
        val spinnerMyteamAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerHomeAway)
        spinnerMyteamAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerMyteam.adapter = spinnerMyteamAdapter
        binding.spinnerMyteam.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedHomeOrAway = spinnerHomeAway[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedHomeOrAway = spinnerHomeAway[0]
            }
        }

        binding.recyclerviewTeams.adapter = teamSelectionAdapter
        binding.recyclerviewTeams.layoutManager = LinearLayoutManager(requireContext())

        binding.btnHometeam.setOnClickListener{
            binding.recyclerviewTeams.visibility = View.VISIBLE
            teamSelectionAdapter.submitList(selectedSportTeamNames)
            teamSelectionAdapter.itemClick = object : TeamSelectionAdapter.ItemClick {
                override fun onClick(view: View, position: Int) {
                    selectedHomeTeamName = selectedSportTeamNames[position]
                    binding.recyclerviewTeams.visibility = View.GONE
                    if (selectedHomeTeamName == "직접 입력") {
                        binding.btnHometeam.visibility = View.GONE
                        binding.etHomeTeam.visibility = View.VISIBLE
                    } else {
                        binding.btnHometeam.visibility = View.VISIBLE
                        binding.btnHometeam.setText(selectedHomeTeamName)
                        binding.etHomeTeam.visibility = View.GONE
                        binding.etHomeTeam.setText("")
                    }
                }
            }
        }
        binding.btnAwayteam.setOnClickListener{
            binding.recyclerviewTeams.visibility = View.VISIBLE
            teamSelectionAdapter.submitList(selectedSportTeamNames)
            teamSelectionAdapter.itemClick = object : TeamSelectionAdapter.ItemClick {
                override fun onClick(view: View, position: Int) {
                    selectedAwayTeamName = selectedSportTeamNames[position]
                    binding.recyclerviewTeams.visibility = View.GONE
                    if (selectedAwayTeamName == "직접 입력") {
                        binding.btnAwayteam.visibility = View.GONE
                        binding.etAwayTeam.visibility = View.VISIBLE
                    } else {
                        selectedAwayTeamName = selectedSportTeamNames[position]
                        binding.btnAwayteam.visibility = View.VISIBLE
                        binding.btnAwayteam.setText(selectedAwayTeamName)
                        binding.etAwayTeam.visibility = View.GONE
                        binding.etAwayTeam.setText("")
                    }
                }
            }
        }

        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타팀 직관")
        val spinnerResultAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerResult)
        spinnerResultAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerResult.adapter = spinnerResultAdapter
        binding.spinnerResult.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedResult = spinnerResult[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedResult = spinnerResult[0]
            }
        }

        binding.btnNext.setOnClickListener{
            selectedLocation = binding.etLocation.text.toString()

            if (binding.etHomeScore.text.isNullOrEmpty()) {
                selectedHomescore = 0
            } else {
                selectedHomescore = binding.etHomeScore.text.toString().toInt()
            }

            if (binding.etAwayScore.text.isNullOrEmpty()) {
                selectedAwayscore = 0
            } else {
                selectedAwayscore = binding.etAwayScore.text.toString().toInt()
            }

            if (selectedHomeTeamName == "직접 입력") {
                selectedHomeTeam = Team(
                    name = binding.etHomeTeam.text.toString(),
                    shortname = binding.etHomeTeam.text.toString(),
                    sport = selectedSport,
                    teamcolor = "#999999" )
            } else {
                selectedHomeTeam = selectedSportTeams.find { it.name == selectedHomeTeamName }!!
            }

            if (selectedAwayTeamName == "직접 입력") {
                selectedAwayTeam = Team(
                    name = binding.etAwayTeam.text.toString(),
                    shortname = binding.etAwayTeam.text.toString(),
                    sport = selectedSport,
                    teamcolor = "#999999" )
            } else {
                selectedAwayTeam = selectedSportTeams.find { it.name == selectedAwayTeamName }!!
            }

            if (selectedHomeOrAway == "홈 팀") {
                selectedMyteam = selectedHomeTeam
            } else if (selectedHomeOrAway == "어웨이 팀") {
                selectedMyteam = selectedAwayTeam
            }

            if (binding.etMvp.text.isNullOrEmpty()) {
                selectedMvp = ""
            } else {
                selectedMvp = binding.etMvp.text.toString()
            }
            val temporaryMatchData = Match(
            id = "${currentUser.email}-${selectedYear}${selectedMonth}${selectedDay}-${Random.nextInt(100000, 1000000)}",
            writerEmail = currentUser.email,
            year = selectedYear,
            month = selectedMonth,
            date = selectedDay,
            day = selectedDayOfWeek,
            time = selectedTime,
            location = selectedLocation,
            weather = selectedWeather,
            feeling = selectedFeeling,
            sport = selectedSport,
            home = selectedHomeTeam,
            away = selectedAwayTeam,
            homescore = selectedHomescore,
            awayscore = selectedAwayscore,
            result = selectedResult,
            myteam = selectedMyteam,
            mvp = selectedMvp,
            photos = mutableListOf(),
            content = ""
            )
            userViewModel.saveTemporaryMatchData(temporaryMatchData)
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_out_right
                )
                hide(this@AddMyMatchOneFragment)
                add(R.id.main_frame, AddMyMatchTwoFragment(), "AddMyMatchTwoFragment")
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val selectedPickerTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.btnTime.setText(selectedPickerTime)
            selectedTime = selectedPickerTime
        }, hour, minute, false) // true는 24시간 형식, false는 12시간 형식
        timePickerDialog.show()
    }

    private fun weatherClicker(image: ImageView, imageList: List<ImageView>, backgroundList: List<View>) {
        image.setOnClickListener{
            val index = imageList.indexOf(image)
            backgroundList.forEach { it.setBackgroundColor(ContextCompat.getColor(it.context, R.color.white))}
            backgroundList[index].setBackgroundColor(ContextCompat.getColor(it.context, R.color.main_medium_gray))
            selectedWeather = weatherList[index]
        }
    }

    private fun feelingClicker(image: ImageView, imageList: List<ImageView>, backgroundList: List<View>) {
        image.setOnClickListener{
            val index = imageList.indexOf(image)
            backgroundList.forEach { it.setBackgroundColor(ContextCompat.getColor(it.context, R.color.white))}
            backgroundList[index].setBackgroundColor(ContextCompat.getColor(it.context, R.color.main_medium_gray))
            selectedFeeling = feelingList[index]
        }
    }


    private fun moveFragment(fragment: Fragment, fragmentTag: String) {
        val fragmentToMove = requireActivity().supportFragmentManager.findFragmentByTag(fragmentTag)
        requireActivity().supportFragmentManager.beginTransaction().apply {
            hide(AddMyMatchOneFragment())
            if (fragmentToMove == null) {
                add(R.id.main_frame, fragment, fragmentTag)
            } else {
                show(fragmentToMove)
            }
            addToBackStack(null)
            commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}