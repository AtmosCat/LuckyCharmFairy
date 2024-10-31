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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.luckycharmfairy.R
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
import com.luckycharmfairy.databinding.FragmentAddMyMatchOneBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar

class AddMyMatchOneFragment : Fragment() {

    private var _binding: FragmentAddMyMatchOneBinding? = null
    private val binding get() = _binding!!

    private var currentUser: User = User()
    private var selectedSport = ""
    private var selectedSportTeams = listOf<String>()
    private var selectedYear = CalendarDay.from(Calendar.getInstance()).year.toString()
    private var selectedMonth = CalendarDay.from(Calendar.getInstance()).month.toString()
    private var selectedDate = CalendarDay.from(Calendar.getInstance()).date.toString()
    private var selectedDay = CalendarDay.from(Calendar.getInstance()).day.toString()
    private var selectedTime = "00:00"
    private var selectedLocation = ""
    private var selectedWeather = ""
    private var selectedFeeling = ""
    private var selectedMyteam = ""
    private var selectedHomeTeam = ""
    private var selectedAwayTeam = ""

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

        val spinnerSports = currentUser.mysports
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
                    "야구" -> selectedSportTeams = baseballTeams
                    "남자축구" -> selectedSportTeams = menFootballTeams
                    "남자농구" -> selectedSportTeams = menBasketballTeams
                    "남자배구" -> selectedSportTeams = menVolleyballTeams
                    "여자배구" -> selectedSportTeams = womenVolleyballTeams
                    else -> selectedSportTeams = listOf("직접 입력")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = ""
            }
        }

        binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDate}일 (${selectedDay})")

        binding.btnDate.setOnClickListener{
            binding.calendarMatchdaySelector.visibility = View.VISIBLE
            binding.calendarMatchdaySelector.setSelectedDate(CalendarDay.from(Calendar.getInstance())) // 기본 오늘 설정
            binding.calendarMatchdaySelector.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
                if (selected) {
                    selectedYear = date.year.toString()
                    selectedMonth = date.month.toString()
                    selectedDate = date.date.toString()
                    selectedDay = date.day.toString()
                    binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDate}일 (${selectedDay})")
                    binding.calendarMatchdaySelector.visibility = View.GONE
                }
            })
        }

        binding.btnTime.setOnClickListener{
            showTimePickerDialog()
        }

        var spinnerLocations = listOf<String>()
        when (selectedSport) {
            "야구" -> spinnerLocations = baseballLocations
            "남자축구" -> spinnerLocations = menFootballLocations
            "남자농구" -> spinnerLocations = menBasketballLocations
            "남자배구" -> spinnerLocations = menVolleyballLocations
            "여자배구" -> spinnerLocations = womenVolleyballLocations
            else -> spinnerLocations = listOf("직접 입력")
        }
        val spinnerLocationAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerLocations)
        spinnerLocationAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerLocation.adapter = spinnerLocationAdapter
        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = spinnerLocations[position]
                if (selectedLocation == "직접 입력") {
                    binding.etLocation.visibility = View.VISIBLE
                } else {
                    binding.etLocation.visibility = View.GONE
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
        weatherButtonBackgroundList.forEach{ it.visibility = View.GONE }

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
        feelingButtonBackgroundList.forEach{ it.visibility = View.GONE }

        feelingButtonList.forEach{ feelingClicker(it, feelingButtonList, feelingButtonBackgroundList) }

        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀")
        val spinnerMyteamAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_layout_custom, spinnerHomeAway)
        spinnerMyteamAdapter.setDropDownViewResource(R.layout.spinner_list_layout_custom)
        binding.spinnerLocation.adapter = spinnerLocationAdapter
        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    binding.btnHometeam.setText(selectedHomeTeam)
                    if (selectedHomeTeam == "직접 입력") {
                        binding.btnHometeam.visibility = View.GONE
                        binding.etHomeTeam.visibility = View.VISIBLE
                        // 창닫기
                    } else {
                        selectedHomeTeam = selectedSportTeams[position]
                        binding.btnHometeam.visibility = View.VISIBLE
                        binding.etHomeTeam.visibility = View.GONE
                        // 창닫기
                    }
                }
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
            backgroundList.forEach { it.visibility = View.GONE}
            backgroundList[index].visibility = View.VISIBLE
            selectedWeather = weatherList[index]
        }
    }

    private fun feelingClicker(image: ImageView, imageList: List<ImageView>, backgroundList: List<View>) {
        image.setOnClickListener{
            val index = imageList.indexOf(image)
            backgroundList.forEach { it.visibility = View.GONE}
            backgroundList[index].visibility = View.VISIBLE
            selectedFeeling = feelingList[index]
        }
    }

}