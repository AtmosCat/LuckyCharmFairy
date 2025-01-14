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
import android.widget.EditText
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
import com.luckycharmfairy.data.model.sports
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
import com.luckycharmfairy.utils.FragmentUtils
import com.luckycharmfairy.utils.SpinnerUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar
import kotlin.random.Random

class AddMyMatchOneFragment : Fragment() {

    lateinit var binding: FragmentAddMyMatchOneBinding

    private var currentUser: User = User()
    private var selectedSport = ""
    private var selectedSportTeams = listOf<Team>()
    private var selectedSportTeamNames = listOf<String>()
    private var selectedYear = CalendarDay.from(Calendar.getInstance()).year.toString()
    private var selectedMonth =
        String.format("%02d", CalendarDay.from(Calendar.getInstance()).month + 1)
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
    private lateinit var weatherButtonList: List<ImageView>
    private lateinit var weatherButtonBackgroundList: List<View>

    private var feelingList = listOf(
        "happy",
        "lovely",
        "soso",
        "sad",
        "angry"
    )

    private lateinit var feelingButtonList: List<ImageView>
    private lateinit var feelingButtonBackgroundList: List<View>

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

        weatherButtonList = listOf(
            binding.btnSunny,
            binding.btnSunnyCloudy,
            binding.btnCloudy,
            binding.btnRainy,
            binding.btnSnowy
        )

        weatherButtonBackgroundList = listOf(
            binding.viewSunnyBackground, binding.viewSunnyCloudyBackground,
            binding.viewCloudyBackground, binding.viewRainyBackground, binding.viewSnowyBackground
        )

        feelingButtonList = listOf(
            binding.btnHappy,
            binding.btnLovely,
            binding.btnSoso,
            binding.btnSad,
            binding.btnAngry
        )

        feelingButtonBackgroundList = listOf(
            binding.viewHappyBackground, binding.viewLovelyBackground, binding.viewSosoBackground,
            binding.viewSadBackground, binding.viewAngryBackground
        )

        binding.recyclerviewTeams.adapter = teamSelectionAdapter
        binding.recyclerviewTeams.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = userViewModel.getCurrentUser()!!

        /* 초기 세팅 */
        setSpinnerAdapters()

        /* 유저 액션 설정 */
        clickSportSpinner()

        clickCloseButton()

        clickAddSportButton()

        clickDatePicker()

        clickTimePicker()

        clickWeatherFeelingIcon("weather", weatherButtonList, weatherList, weatherButtonBackgroundList)

        clickWeatherFeelingIcon("feeling", feelingButtonList, feelingList, feelingButtonBackgroundList)

        clickMyTeamSpinner()

        clickHomeTeamButton()

        clickAwayTeamButton()

        clickResultSpinner()

        clickNextButton()

        // UI 렌더링
        binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDay}일 (${selectedDayOfWeek})")

    }

    private fun setSpinnerAdapters() {
        val spinnerSports = currentUser.mysports.drop(1)
        SpinnerUtils.setSpinnerAdapter(binding.spinnerSports, requireContext(), spinnerSports)

        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀", "없음")
        SpinnerUtils.setSpinnerAdapter(binding.spinnerMyteam, requireContext(), spinnerHomeAway)

        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타팀 직관")
        SpinnerUtils.setSpinnerAdapter(binding.spinnerResult, requireContext(), spinnerResult)
    }

    private fun clickSportSpinner() {
        val spinnerSports = currentUser.mysports.drop(1)
        binding.spinnerSports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSport = spinnerSports[position]
                if (selectedSport != "직접 입력") {
                    selectedSportTeamNames =
                        sports.find { it.name == selectedSport }?.teamNames ?: mutableListOf()
                    selectedSportTeams =
                        sports.find { it.name == selectedSport }?.teams ?: mutableListOf()
                } else {
                    selectedSportTeamNames = listOf("직접 입력")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSport = ""
            }
        }
    }

    private fun clickCloseButton() {
        binding.btnClose.setOnClickListener {
            userViewModel.initializeTemporaryImageUrls()
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@AddMyMatchOneFragment,
                MyMatchesFragment(),
                "MyMatchesFragment"
            )
        }
    }

    private fun clickAddSportButton(){
        binding.btnAddSports.setOnClickListener {
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@AddMyMatchOneFragment,
                MySportsFragment(),
                "MySportsFragment"
            )
        }
    }

    private fun clickDatePicker(){
        binding.calendarMatchdaySelector.setSelectedDate(CalendarDay.from(Calendar.getInstance())) // 기본 오늘 설정
        binding.btnDate.setOnClickListener {
            binding.calendarMatchdaySelector.visibility = View.VISIBLE
            binding.calendarMatchdaySelector.setOnDateChangedListener( { widget, date, selected ->
                if (selected) {
                    selectedYear = date.year.toString()
                    selectedMonth = String.format("%02d", date.month + 1)
                    selectedDay = date.day.toString()
                    val calendar = Calendar.getInstance().apply {
                        set(date.year, date.month, date.day)
                    }
                    selectedDayOfWeek = DateTimeUtils.dayOfWeekFormatter(calendar.get(Calendar.DAY_OF_WEEK))
                    binding.btnDate.setText("${selectedYear}년 ${selectedMonth}월 ${selectedDay}일 (${selectedDayOfWeek})")
                    binding.calendarMatchdaySelector.visibility = View.GONE
                }
            })
        }
    }

    private fun clickTimePicker() {
        binding.btnTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog =
                TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                    val selectedPickerTime =
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.btnTime.setText(selectedPickerTime)
                    selectedTime = selectedPickerTime
                }, hour, minute, false) // true는 24시간 형식, false는 12시간 형식
            timePickerDialog.show()
        }
    }

    private fun clickWeatherFeelingIcon(
        category: String,
        iconList: List<ImageView>,
        iconNameList: List<String>,
        iconBackgroundList: List<View>
    ) {
        iconList.forEach { icon ->
            icon.setOnClickListener {
                val index = iconList.indexOf(icon)
                iconBackgroundList.forEach { bg ->
                    bg.setBackgroundColor(
                        ContextCompat.getColor(
                            bg.context,
                            R.color.white
                        )
                    )
                }
                iconBackgroundList[index].setBackgroundColor(
                    ContextCompat.getColor(
                        it.context,
                        R.color.main_medium_gray
                    )
                )
                if (category == "weather") {
                    selectedWeather = iconNameList[index]
                } else if (category == "feeling") {
                    selectedFeeling = iconNameList[index]
                }
            }
        }
    }

    private fun clickMyTeamSpinner(){
        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀", "없음")
        binding.spinnerMyteam.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedHomeOrAway = spinnerHomeAway[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedHomeOrAway = spinnerHomeAway[0]
            }
        }
    }

    private fun setupTeamButton(
        button: Button,
        editText: EditText,
        isHomeTeam: Boolean
    ) {
        button.setOnClickListener {
            binding.recyclerviewTeams.visibility = View.VISIBLE
            teamSelectionAdapter.submitList(selectedSportTeamNames)
            teamSelectionAdapter.itemClick = object : TeamSelectionAdapter.ItemClick {
                override fun onClick(view: View, position: Int) {
                    val selectedTeamName = selectedSportTeamNames[position]
                    binding.recyclerviewTeams.visibility = View.GONE

                    if (selectedTeamName == "직접 입력") {
                        button.visibility = View.GONE
                        editText.visibility = View.VISIBLE
                    } else {
                        if (isHomeTeam) {
                            selectedHomeTeamName = selectedTeamName
                        } else {
                            selectedAwayTeamName = selectedTeamName
                        }
                        button.visibility = View.VISIBLE
                        button.text = selectedTeamName
                        editText.visibility = View.GONE
                        editText.setText("")
                    }
                }
            }
        }
    }

    private fun clickHomeTeamButton() {
        setupTeamButton(
            button = binding.btnHometeam,
            editText = binding.etHomeTeam,
            isHomeTeam = true
        )
    }

    private fun clickAwayTeamButton() {
        setupTeamButton(
            button = binding.btnAwayteam,
            editText = binding.etAwayTeam,
            isHomeTeam = false
        )
    }

    private fun clickResultSpinner(){
        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타팀 직관")
        binding.spinnerResult.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedResult = spinnerResult[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedResult = spinnerResult[0]
            }
        }
    }

    private fun clickNextButton() {
        binding.btnNext.setOnClickListener {
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
                    teamcolor = "#999999"
                )
            } else {
                selectedHomeTeam = selectedSportTeams.find { it.name == selectedHomeTeamName } ?: Team()
            }

            if (selectedAwayTeamName == "직접 입력") {
                selectedAwayTeam = Team(
                    name = binding.etAwayTeam.text.toString(),
                    shortname = binding.etAwayTeam.text.toString(),
                    sport = selectedSport,
                    teamcolor = "#999999"
                )
            } else {
                selectedAwayTeam = selectedSportTeams.find { it.name == selectedAwayTeamName } ?: Team()
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
                id = "${currentUser.email}-${selectedYear}${selectedMonth}${selectedDay}-${
                    Random.nextInt(
                        100000,
                        1000000
                    )
                }",
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}