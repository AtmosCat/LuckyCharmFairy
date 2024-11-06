package com.luckycharmfairy.presentation.mymatches.editmatch

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.model.Team
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.baseballTeamNames
import com.luckycharmfairy.data.model.baseballTeams
import com.luckycharmfairy.data.model.menBasketballTeamNames
import com.luckycharmfairy.data.model.menBasketballTeams
import com.luckycharmfairy.data.model.menFootballTeamNames
import com.luckycharmfairy.data.model.menFootballTeams
import com.luckycharmfairy.data.model.menVolleyballTeamNames
import com.luckycharmfairy.data.model.menVolleyballTeams
import com.luckycharmfairy.data.model.womenVolleyballTeamNames
import com.luckycharmfairy.data.model.womenVolleyballTeams
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentEditMyMatchOneBinding
import com.luckycharmfairy.presentation.mymatches.addmatches.AddMyMatchTwoFragment
import com.luckycharmfairy.presentation.mymatches.addmatches.TeamSelectionAdapter
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar
import kotlin.random.Random

private const val ARG_PARAM1 = "param1"
class EditMyMatchOneFragment : Fragment() {
    private var param1: String? = null

    private var _binding: FragmentEditMyMatchOneBinding? = null
    private val binding get() = _binding!!

    private var clickedMatch = Match()

    private var currentUser: User = User()
    private var selectedSport = ""
    private var selectedSportTeams = listOf<Team>()
    private var selectedSportTeamNames = listOf<String>()
    private var selectedYear = ""
    private var selectedMonth = ""
    private var selectedDate = ""
    private var selectedDay = ""
    private var selectedTime = ""
    private var selectedLocation = ""
    private var selectedWeather = ""
    private var selectedFeeling = ""
    private var selectedMyteam = ""
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

    private lateinit var weatherButton1: ImageView
    private lateinit var weatherButton2: ImageView
    private lateinit var weatherButton3: ImageView
    private lateinit var weatherButton4: ImageView
    private lateinit var weatherButton5: ImageView
    private lateinit var weatherButtonList : List<ImageView>

    private lateinit var weatherButton1Background: View
    private lateinit var weatherButton2Background: View
    private lateinit var weatherButton3Background: View
    private lateinit var weatherButton4Background: View
    private lateinit var weatherButton5Background: View
    private lateinit var weatherButtonBackgroundList : List<View>

    private var feelingList = listOf(
        "happy",
        "lovely",
        "soso",
        "sad",
        "angry"
    )

    private lateinit var feelingButton1: ImageView
    private lateinit var feelingButton2: ImageView
    private lateinit var feelingButton3: ImageView
    private lateinit var feelingButton4: ImageView
    private lateinit var feelingButton5: ImageView
    private lateinit var feelingButtonList : List<ImageView>

    private lateinit var feelingButton1Background: View
    private lateinit var feelingButton2Background: View
    private lateinit var feelingButton3Background: View
    private lateinit var feelingButton4Background: View
    private lateinit var feelingButton5Background: View
    private lateinit var feelingButtonBackgroundList : List<View>

    private val teamSelectionAdapter by lazy { TeamSelectionAdapter() }

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    companion object {
        fun newInstance(param1: String) =
            EditMyMatchOneFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditMyMatchOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = userViewModel.currentUser.value!!
        clickedMatch = userViewModel.selectedDayMatches.value!!.find { it.id == param1 }!!

        selectedSport = clickedMatch.sport
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
            else -> {
                selectedSportTeamNames = listOf("직접 입력")
            }
        }
        selectedYear = clickedMatch.year
        selectedMonth = clickedMatch.month
        selectedDate = clickedMatch.date
        selectedDay = clickedMatch.day
        selectedTime = clickedMatch.time
        selectedLocation = clickedMatch.location
        selectedWeather = clickedMatch.weather
        selectedFeeling = clickedMatch.feeling
        selectedMyteam = clickedMatch.myteam
        selectedHomeTeamName = clickedMatch.home.name
        selectedAwayTeamName = clickedMatch.away.name
        selectedHomeTeam = clickedMatch.home
        selectedAwayTeam = clickedMatch.away
        selectedHomescore = clickedMatch.homescore
        selectedAwayscore = clickedMatch.awayscore
        selectedResult = clickedMatch.result
        selectedMvp = clickedMatch.mvp


        binding.btnClose.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        val spinnerSports = mutableListOf("야구","남자축구","남자농구","남자배구","여자배구")
        binding.btnAddSports.setOnClickListener{
            // MY종목 관리 프래그먼트 이동
        }
        val spinnerSportsAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerSports)
        spinnerSportsAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerSports.adapter = spinnerSportsAdapter
        val spinnerSportsIndex = spinnerSports.indexOf(selectedSport)
        if (spinnerSportsIndex != -1) {
            binding.spinnerSports.setSelection(spinnerSportsIndex)
        } else {
            binding.spinnerSports.setSelection(0)
        }
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
                    else -> {
                        selectedSportTeamNames = listOf(selectedHomeTeamName, selectedAwayTeamName, "직접입력")
                        selectedSportTeams = listOf(selectedHomeTeam, selectedAwayTeam)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = ""
            }
        }

        binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDate}일 (${selectedDay})")

        binding.calendarMatchdaySelector.setSelectedDate(CalendarDay.from(selectedYear.toInt(), selectedMonth.toInt(), selectedDate.toInt())) // 기본 오늘 설정
        binding.btnDate.setOnClickListener{
            binding.calendarMatchdaySelector.visibility = View.VISIBLE
            binding.calendarMatchdaySelector.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
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
                    binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDate}일 (${selectedDay})")
                    binding.calendarMatchdaySelector.visibility = View.GONE
                }
            })
        }

        binding.btnTime.setOnClickListener{
            showTimePickerDialog()
        }

        weatherButton1 = binding.btnSunny
        weatherButton2 = binding.btnSunnyCloudy
        weatherButton3 = binding.btnCloudy
        weatherButton4 = binding.btnRainy
        weatherButton5 = binding.btnSnowy

        weatherButtonList = listOf(weatherButton1, weatherButton2, weatherButton3, weatherButton4, weatherButton5)

        weatherButton1Background = binding.viewSunnyBackground
        weatherButton2Background = binding.viewSunnyCloudyBackground
        weatherButton3Background = binding.viewCloudyBackground
        weatherButton4Background = binding.viewRainyBackground
        weatherButton5Background = binding.viewSnowyBackground
        weatherButtonBackgroundList = listOf(weatherButton1Background, weatherButton2Background,
            weatherButton3Background, weatherButton4Background, weatherButton5Background)

        weatherButtonList.forEach{ weatherClicker(it, weatherButtonList, weatherButtonBackgroundList) }

        feelingButton1 = binding.btnHappy
        feelingButton2 = binding.btnLovely
        feelingButton3 = binding.btnSoso
        feelingButton4 = binding.btnSad
        feelingButton5 = binding.btnAngry

        feelingButtonList = listOf(feelingButton1, feelingButton2, feelingButton3, feelingButton4, feelingButton5)

        feelingButton1Background = binding.viewHappyBackground
        feelingButton2Background = binding.viewLovelyBackground
        feelingButton3Background = binding.viewSosoBackground
        feelingButton4Background = binding.viewSadBackground
        feelingButton5Background = binding.viewAngryBackground
        feelingButtonBackgroundList = listOf(feelingButton1Background, feelingButton2Background, feelingButton3Background,
            feelingButton4Background, feelingButton5Background)

        feelingButtonList.forEach{ feelingClicker(it, feelingButtonList, feelingButtonBackgroundList) }

        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀", "없음")
        val spinnerMyteamAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerHomeAway)
        spinnerMyteamAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        val spinnerMyteamIndex = spinnerHomeAway.indexOf(selectedMyteam)
        binding.spinnerMyteam.setSelection(spinnerMyteamIndex)
        binding.spinnerMyteam.adapter = spinnerMyteamAdapter
        binding.spinnerMyteam.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMyteam = spinnerHomeAway[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedMyteam = spinnerHomeAway[0]
            }
        }

        binding.recyclerviewTeams.adapter = teamSelectionAdapter
        binding.recyclerviewTeams.layoutManager = LinearLayoutManager(requireContext())
        if (selectedHomeTeamName == "직접 입력") {
            binding.btnHometeam.visibility = View.GONE
            binding.etHomeTeam.visibility = View.VISIBLE
        } else {
            binding.btnHometeam.visibility = View.VISIBLE
            binding.btnHometeam.setText(selectedHomeTeamName)
            binding.etHomeTeam.visibility = View.GONE
            binding.etHomeTeam.setText("")
        }
        binding.etHomeTeam.setText(selectedHomeTeamName)
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
        binding.etAwayTeam.setText(selectedAwayTeamName)
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

        binding.etHomeScore.setText(selectedHomescore)
        binding.etAwayScore.setText(selectedAwayscore)
        binding.etMvp.setText(selectedMvp)

        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타팀 직관")
        val spinnerResultAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerResult)
        val spinnerResultIndex = spinnerResult.indexOf(selectedResult)
        binding.spinnerResult.setSelection(spinnerResultIndex)
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

            if (binding.etMvp.text.isNullOrEmpty()) {
                selectedMvp = ""
            } else {
                selectedMvp = binding.etMvp.text.toString()
            }
            val temporaryMatchData = Match(
                id = clickedMatch.id,
                writerEmail = currentUser.email,
                year = selectedYear,
                month = selectedMonth,
                date = selectedDate,
                day = selectedDay,
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
        }, hour, minute, true) // true는 24시간 형식
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

}