package com.luckycharmfairy.presentation.mymatches.editmatch

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.model.Team
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.model.sports
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentEditMyMatchOneBinding
import com.luckycharmfairy.presentation.mymatches.addmatches.TeamSelectionAdapter
import com.luckycharmfairy.presentation.mymatches.mysports.MySportsFragment
import com.luckycharmfairy.utils.DateTimeUtils
import com.luckycharmfairy.utils.FragmentUtils
import com.luckycharmfairy.utils.SpinnerUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.Calendar

private const val ARG_PARAM1 = "param1"

class EditMyMatchOneFragment : Fragment() {
    private var param1: String? = null

    lateinit var binding: FragmentEditMyMatchOneBinding

    private lateinit var clickedMatch: Match

    private lateinit var currentUser: User

    private var edittedMatch = Match()

    private var selectedSportTeams = listOf<Team>()
    private var selectedSportTeamNames = listOf<String>()

    private var selectedHomeOrAway = ""

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
        binding = FragmentEditMyMatchOneBinding.inflate(inflater, container, false)

        binding.recyclerviewTeams.adapter = teamSelectionAdapter
        binding.recyclerviewTeams.layoutManager = LinearLayoutManager(requireContext())

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = userViewModel.getCurrentUser()!!
        clickedMatch = currentUser.matches.find { it.id == param1 } ?: Match()
        edittedMatch = clickedMatch.copy()

        /* 초기 세팅 */
        setSpinnerAdapters()

        /* UI 렌더링 */
        renderUI()

        /* 유저 액션 설정 */
        clickSportSpinner()

        clickCloseButton()

        clickAddSportButton()

        clickDatePicker()

        clickTimePicker()

        clickWeatherFeelingIcon(
            "weather",
            weatherButtonList,
            weatherList,
            weatherButtonBackgroundList
        )

        clickWeatherFeelingIcon(
            "feeling",
            feelingButtonList,
            feelingList,
            feelingButtonBackgroundList
        )

        clickMyTeamSpinner()

        clickHomeTeamButton()

        clickAwayTeamButton()

        clickResultSpinner()

        clickNextButton()
    }

    private fun renderUI() {

        if (edittedMatch.sport != "직접 입력") {
            selectedSportTeamNames =
                sports.find { it.name == edittedMatch.sport }?.teamNames ?: mutableListOf()
            selectedSportTeams =
                sports.find { it.name == edittedMatch.sport }?.teams ?: mutableListOf()
        } else {
            selectedSportTeamNames = listOf("직접 입력")
        }

        val spinnerSportsIndex = currentUser.mysports.indexOf(edittedMatch.sport)
        if (spinnerSportsIndex != -1) {
            binding.spinnerSports.setSelection(spinnerSportsIndex)
        } else {
            binding.spinnerSports.setSelection(0)
        }

        binding.btnDate.setText("${edittedMatch.year}년 ${edittedMatch.month}월 ${edittedMatch.date}일 (${edittedMatch.day})")

        binding.calendarMatchdaySelector.setSelectedDate(
            CalendarDay.from(
                edittedMatch.year.toInt(),
                edittedMatch.month.toInt(),
                edittedMatch.date.toInt()
            )
        )

        binding.btnTime.setText(edittedMatch.time)
        binding.etLocation.setText(edittedMatch.location)

        val weatherIndex = weatherList.indexOf(edittedMatch.weather)
        weatherButtonBackgroundList[weatherIndex].setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_medium_gray
            )
        )
        val feelingIndex = feelingList.indexOf(edittedMatch.feeling)
        feelingButtonBackgroundList[feelingIndex].setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_medium_gray
            )
        )
        if (edittedMatch.myteam == edittedMatch.home) {
            selectedHomeOrAway == "홈 팀"
        } else if (edittedMatch.myteam == edittedMatch.away) {
            selectedHomeOrAway == "어웨이 팀"
        }

        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀", "없음")
        val spinnerMyteamIndex = spinnerHomeAway.indexOf(selectedHomeOrAway)
        binding.spinnerMyteam.setSelection(spinnerMyteamIndex)

        if (edittedMatch.home.name == "직접 입력") {
            binding.btnHometeam.visibility = View.GONE
            binding.etHomeTeam.visibility = View.VISIBLE
        } else {
            binding.btnHometeam.visibility = View.VISIBLE
            binding.btnHometeam.setText(edittedMatch.home.name)
            binding.etHomeTeam.visibility = View.GONE
            binding.etHomeTeam.setText("")
        }
        binding.etHomeTeam.setText(edittedMatch.home.name)

        if (edittedMatch.away.name == "직접 입력") {
            binding.btnAwayteam.visibility = View.GONE
            binding.etAwayTeam.visibility = View.VISIBLE
        } else {
            binding.btnAwayteam.visibility = View.VISIBLE
            binding.btnAwayteam.setText(edittedMatch.away.name)
            binding.etAwayTeam.visibility = View.GONE
            binding.etAwayTeam.setText("")
        }
        binding.etAwayTeam.setText(edittedMatch.away.name)

        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타팀 직관")
        val spinnerResultIndex = spinnerResult.indexOf(edittedMatch.result)
        binding.spinnerResult.setSelection(spinnerResultIndex)

        binding.etHomeScore.setText(edittedMatch.homescore.toString())
        binding.etAwayScore.setText(edittedMatch.awayscore.toString())
        binding.etMvp.setText(edittedMatch.mvp)
    }

    private fun setSpinnerAdapters() {
        SpinnerUtils.setSpinnerAdapter(
            binding.spinnerSports,
            requireContext(),
            currentUser.mysports.drop(0)
        )

        val spinnerHomeAway = listOf("홈 팀", "어웨이 팀", "없음")
        SpinnerUtils.setSpinnerAdapter(binding.spinnerMyteam, requireContext(), spinnerHomeAway)

        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타팀 직관")
        SpinnerUtils.setSpinnerAdapter(binding.spinnerResult, requireContext(), spinnerResult)
    }

    private fun clickSportSpinner() {
        binding.spinnerSports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                edittedMatch.sport = currentUser.mysports[position]
                if (edittedMatch.sport != "직접 입력") {
                    selectedSportTeamNames =
                        sports.find { it.name == edittedMatch.sport }?.teamNames ?: mutableListOf()
                    selectedSportTeams =
                        sports.find { it.name == edittedMatch.sport }?.teams ?: mutableListOf()
                } else {
                    selectedSportTeamNames = listOf("직접 입력")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                edittedMatch.sport = ""
            }
        }
    }

    private fun clickCloseButton() {
        binding.btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun clickDatePicker() {
        binding.btnDate.setOnClickListener {
            binding.calendarMatchdaySelector.visibility = View.VISIBLE
            binding.calendarMatchdaySelector.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
                if (selected) {
                    edittedMatch.year = date.year.toString()
                    edittedMatch.month = String.format("%02d", date.month + 1)
                    edittedMatch.date = date.day.toString()
                    val calendar = Calendar.getInstance().apply {
                        set(date.year, date.month, date.day) // 선택한 날짜로 설정
                    }

                    edittedMatch.day =
                        DateTimeUtils.dayOfWeekFormatter(calendar.get(Calendar.DAY_OF_WEEK))
                    binding.btnDate.setText("${edittedMatch.year}년 ${edittedMatch.month}월 ${edittedMatch.date}일 (${edittedMatch.day})")
                    binding.calendarMatchdaySelector.visibility = View.GONE
                }
            })
        }
    }

    private fun clickAddSportButton() {
        binding.btnAddSports.setOnClickListener {
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@EditMyMatchOneFragment,
                MySportsFragment(),
                "MySportsFragment"
            )
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
                    edittedMatch.weather = iconNameList[index]
                } else if (category == "feeling") {
                    edittedMatch.feeling = iconNameList[index]
                }
            }
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
                    edittedMatch.time = selectedPickerTime
                }, hour, minute, true) // true는 24시간 형식
            timePickerDialog.show()
        }
    }

    private fun clickMyTeamSpinner() {
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
                selectedHomeOrAway = "없음"
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
                            edittedMatch.home.name = selectedTeamName
                        } else {
                            edittedMatch.away.name = selectedTeamName
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

    private fun clickResultSpinner() {
        val spinnerResult = listOf("승리", "패배", "무승부", "경기 취소", "타팀 직관")
        binding.spinnerResult.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                edittedMatch.result = spinnerResult[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                edittedMatch.result = spinnerResult[0]
            }
        }
    }

    private fun clickNextButton() {
        binding.btnNext.setOnClickListener {
            edittedMatch.location = binding.etLocation.text.toString()

            if (binding.etHomeScore.text.isNullOrEmpty()) {
                edittedMatch.homescore = 0
            } else {
                edittedMatch.homescore = binding.etHomeScore.text.toString().toInt()
            }

            if (binding.etAwayScore.text.isNullOrEmpty()) {
                edittedMatch.awayscore = 0
            } else {
                edittedMatch.awayscore = binding.etAwayScore.text.toString().toInt()
            }

            if (edittedMatch.home.name == "직접 입력") {
                edittedMatch.home = Team(
                    name = binding.etHomeTeam.text.toString(),
                    shortname = binding.etHomeTeam.text.toString(),
                    sport = edittedMatch.sport,
                    teamcolor = "#999999"
                )
            } else {
                edittedMatch.home = selectedSportTeams.find { it.name == edittedMatch.home.name }!!
            }

            if (edittedMatch.away.name == "직접 입력") {
                edittedMatch.away = Team(
                    name = binding.etAwayTeam.text.toString(),
                    shortname = binding.etAwayTeam.text.toString(),
                    sport = edittedMatch.sport,
                    teamcolor = "#999999"
                )
            } else {
                edittedMatch.away = selectedSportTeams.find { it.name == edittedMatch.away.name }!!
            }

            if (selectedHomeOrAway == "홈 팀") {
                edittedMatch.myteam = edittedMatch.home
            } else if (selectedHomeOrAway == "어웨이 팀") {
                edittedMatch.myteam = edittedMatch.away
            }

            if (binding.etMvp.text.isNullOrEmpty()) {
                edittedMatch.mvp = ""
            } else {
                edittedMatch.mvp = binding.etMvp.text.toString()
            }

            userViewModel.saveTemporaryMatchData(edittedMatch)
            val editMyMatchTwoFragment = EditMyMatchTwoFragment.newInstance(clickedMatch.id)
            requireActivity().supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_out_right
                )
                hide(this@EditMyMatchOneFragment)
                add(R.id.main_frame, editMyMatchTwoFragment)
                addToBackStack(null)
                commit()
            }
        }
    }
}