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
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.baseballLocations
import com.luckycharmfairy.data.model.baseballTeams
import com.luckycharmfairy.data.model.menBasketballLocations
import com.luckycharmfairy.data.model.menBasketballTeams
import com.luckycharmfairy.data.model.menFootballLocations
import com.luckycharmfairy.data.model.menFootballTeams
import com.luckycharmfairy.data.model.menVolleyballLocations
import com.luckycharmfairy.data.model.menVolleyballTeams
import com.luckycharmfairy.data.model.womenVolleyballLocations
import com.luckycharmfairy.data.model.womenVolleyballTeams
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentAddMyMatchOneBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar
import kotlin.random.Random

class AddMyMatchOneFragment : Fragment() {

    private var _binding: FragmentAddMyMatchOneBinding? = null
    private val binding get() = _binding!!

    private var currentUser: User = User()
    private var selectedSport = ""
    private var selectedSportTeams = listOf<String>()
    private var spinnerLocations = listOf<String>()
    private var locationsMap = mapOf(
        "야구" to baseballLocations,
        "남자축구" to menFootballLocations,
        "남자농구" to menBasketballLocations,
        "남자배구" to menVolleyballLocations,
        "여자농구" to womenVolleyballLocations
    )
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
    private var selectedTime = "00:00"
    private var selectedLocation = ""
    private var selectedWeather = ""
    private var selectedFeeling = ""
    private var selectedMyteam = ""
    private var selectedHomeTeam = ""
    private var selectedAwayTeam = ""
    private var selectedHomescore = -1
    private var selectedAwayscore = -1
    private var selectedResult = ""
    private var selectedMvp = ""

    private var weatherList = listOf(
        "R.drawable.weather_sunny",
        "R.drawable.weather_sunny_cloudy",
        "R.drawable.weather_cloudy",
        "R.drawable.weather_rainy",
        "R.drawable.weather_snowy"
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
        "R.drawable.feeling_happy",
        "R.drawable.feeling_lovely",
        "R.drawable.feeling_soso",
        "R.drawable.feeling_sad",
        "R.drawable.feeling_angry"
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMyMatchOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = userViewModel.currentUser.value!!

        binding.btnClose.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

//        val spinnerSports = currentUser.mysports
        val spinnerSports = mutableListOf("야구","남자축구","남자농구","남자배구","여자배구")
//        if (spinnerSports.isEmpty()) {
//            spinnerSports.add("종목을 추가해주세요")
//        }
        binding.btnAddSports.setOnClickListener{
            // MY종목 관리 프래그먼트 이동
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
                        selectedSportTeams = baseballTeams
                        spinnerLocations = baseballLocations
                        val spinnerLocationAdapter =
                            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, locationsMap[selectedSport] ?: emptyList())
                        spinnerLocationAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
                        binding.spinnerLocation.adapter = spinnerLocationAdapter
                    }
                    "남자축구" -> {
                        selectedSportTeams = menFootballTeams
                        spinnerLocations = menFootballLocations
                        val spinnerLocationAdapter =
                            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, locationsMap[selectedSport] ?: emptyList())
                        spinnerLocationAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
                        binding.spinnerLocation.adapter = spinnerLocationAdapter
                    }
                    "남자농구" -> {
                        selectedSportTeams = menBasketballTeams
                        spinnerLocations = menBasketballLocations
                        val spinnerLocationAdapter =
                            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, locationsMap[selectedSport] ?: emptyList())
                        spinnerLocationAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
                        binding.spinnerLocation.adapter = spinnerLocationAdapter
                    }
                    "남자배구" -> {
                        selectedSportTeams = menVolleyballTeams
                        spinnerLocations = menVolleyballLocations
                        val spinnerLocationAdapter =
                            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, locationsMap[selectedSport] ?: emptyList())
                        spinnerLocationAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
                        binding.spinnerLocation.adapter = spinnerLocationAdapter
                    }
                    "여자배구" -> {
                        selectedSportTeams = womenVolleyballTeams
                        spinnerLocations = womenVolleyballLocations
                        val spinnerLocationAdapter =
                            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, locationsMap[selectedSport] ?: emptyList())
                        spinnerLocationAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
                        binding.spinnerLocation.adapter = spinnerLocationAdapter
                    }
                    else -> selectedSportTeams = listOf("직접 입력")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = ""
            }
        }

        binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDate}일 (${selectedDay})")

        binding.calendarMatchdaySelector.setSelectedDate(CalendarDay.from(Calendar.getInstance())) // 기본 오늘 설정
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

//        when (selectedSport) {
//            "야구" -> spinnerLocations = baseballLocations
//            "남자축구" -> spinnerLocations = menFootballLocations
//            "남자농구" -> spinnerLocations = menBasketballLocations
//            "남자배구" -> spinnerLocations = menVolleyballLocations
//            "여자배구" -> spinnerLocations = womenVolleyballLocations
//            else -> spinnerLocations = listOf("직접 입력")
//        }
//        val spinnerLocationAdapter =
//            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, locationsMap[selectedSport] ?: emptyList())
//        spinnerLocationAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
//        binding.spinnerLocation.adapter = spinnerLocationAdapter
        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = spinnerLocations[position]
                if (selectedLocation == "직접 입력") {
                    binding.etLocation.visibility = View.VISIBLE
                } else {
                    binding.etLocation.visibility = View.GONE
                    binding.etLocation.setText("")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLocation = spinnerLocations[0]
            }
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
        feelingButton4Background = binding.viewSnowyBackground
        feelingButton5Background = binding.viewAngryBackground
        feelingButtonBackgroundList = listOf(feelingButton1Background, feelingButton2Background, feelingButton3Background,
                feelingButton4Background, feelingButton5Background)

        feelingButtonList.forEach{ feelingClicker(it, feelingButtonList, feelingButtonBackgroundList) }

        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀", "없음")
        val spinnerMyteamAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerHomeAway)
        spinnerMyteamAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
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

        binding.btnHometeam.setOnClickListener{
            binding.recyclerviewTeams.visibility = View.VISIBLE
            teamSelectionAdapter.submitList(selectedSportTeams)
            teamSelectionAdapter.itemClick = object : TeamSelectionAdapter.ItemClick {
                override fun onClick(view: View, position: Int) {
                    binding.recyclerviewTeams.visibility = View.GONE
                    if (selectedHomeTeam == "직접 입력") {
                        binding.btnHometeam.visibility = View.GONE
                        binding.etHomeTeam.visibility = View.VISIBLE
                    } else {
                        selectedHomeTeam = selectedSportTeams[position]
                        binding.btnHometeam.visibility = View.VISIBLE
                        binding.btnHometeam.setText(selectedHomeTeam)
                        binding.etHomeTeam.visibility = View.GONE
                        binding.etHomeTeam.setText("")
                    }
                }
            }
        }
        binding.btnAwayteam.setOnClickListener{
            binding.recyclerviewTeams.visibility = View.VISIBLE
            teamSelectionAdapter.submitList(selectedSportTeams)
            teamSelectionAdapter.itemClick = object : TeamSelectionAdapter.ItemClick {
                override fun onClick(view: View, position: Int) {
                    binding.recyclerviewTeams.visibility = View.GONE
                    if (selectedAwayTeam == "직접 입력") {
                        binding.btnAwayteam.visibility = View.GONE
                        binding.etAwayTeam.visibility = View.VISIBLE
                    } else {
                        selectedAwayTeam = selectedSportTeams[position]
                        binding.btnAwayteam.visibility = View.VISIBLE
                        binding.btnAwayteam.setText(selectedAwayTeam)
                        binding.etAwayTeam.visibility = View.GONE
                        binding.etAwayTeam.setText("")
                    }
                }
            }
        }

        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타 팀 직관")
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

        val spinnerMvp = listOf("류현진", "문동주", "김서현", "노시환", "페라자", "직접 입력")
        val spinnerMvpAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerMvp)
        spinnerMvpAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerMvp.adapter = spinnerMvpAdapter
        binding.spinnerMvp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMvp = spinnerMvp[position]
                if (selectedMvp == "직접 입력") {
                    binding.etMvp.visibility = View.VISIBLE
                } else {
                    binding.etMvp.visibility = View.GONE
                    binding.etMvp.setText("")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedResult = spinnerResult[0]
            }
        }

        binding.btnNext.setOnClickListener{
            selectedHomescore = binding.etHomeScore.text.toString().toInt()
            selectedAwayscore = binding.etAwayScore.text.toString().toInt()
            if (selectedLocation == "직접 입력") {
                selectedLocation = binding.etLocation.text.toString()
            }
            if (selectedHomeTeam == "직접 입력") {
                selectedHomeTeam = binding.etHomeTeam.text.toString()
            }
            if (selectedAwayTeam == "직접 입력") {
                selectedAwayTeam = binding.etAwayTeam.text.toString()
            }
            if (selectedMvp == "직접 입력") {
                selectedMvp = binding.etMvp.text.toString()
            }
            val temporaryMatchData = Match(
            id = "${currentUser.email}-${selectedYear}${selectedMonth}${selectedDate}-${Random.nextInt(100000, 1000000)}",
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
                add(R.id.main_frame, AddMyMatchTwoFragment(), "AddMatchTwoFragment")
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