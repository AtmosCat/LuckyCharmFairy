import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.presentation.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMatchReportBinding
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.data.model.Team
import com.luckycharmfairy.presentation.mymatches.MyMatchesFragment
import com.luckycharmfairy.presentation.mymatches.matchreport.WinningStreakAdapter
import com.luckycharmfairy.presentation.mypage.MyPageFragment
import com.luckycharmfairy.utils.FragmentUtils
import com.luckycharmfairy.utils.SpinnerUtils
import java.text.DecimalFormat
import java.time.LocalDate

class MatchReportFragment : Fragment() {

    lateinit var binding: FragmentMatchReportBinding

    private var currentUser = User()

    private var selectedSport = ""
    private var selectedSportMyTeamNames = mutableListOf<String>()
    private var selectedMyteamName = ""
    private var selectedMyteam = Team()
    private var selectedYear = ""

    private var spinnerMyteams = mutableListOf<Team>()
    private var spinnerMyteamNames = mutableListOf("응원 팀 전체")

    private lateinit var spinnerMyTeamAdapter: ArrayAdapter<String>

    private var winCount = 0
    private var loseCount = 0
    private var tieCount = 0
    private var cancelCount = 0
    private var noResultCount = 0

    private var homeWinCount = 0
    private var homeLoseCount = 0
    private var homeTieCount = 0

    private var awayWinCount = 0
    private var awayLoseCount = 0
    private var awayTieCount = 0

    private var winningStreakMatches = mutableListOf<Match>()
    private var winningRatesByDay = mutableListOf<Double>()
    private var winningRatesByOpposites = listOf(listOf<String>())

    private val userViewModel: UserViewModel by activityViewModels {
        viewModelFactory { initializer { UserViewModel(requireActivity().application) } }
    }

    private val winningStreakAdapter by lazy { WinningStreakAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMatchRecord.setOnClickListener {
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@MatchReportFragment,
                MyMatchesFragment(),
                "MyMatchesFragment"
            )
        }

        SpinnerUtils.setSpinnerAdapter(binding.spinnerMyTeam, requireContext(), spinnerMyteamNames)

        userViewModel.getSpinnerStatsInAllMatches()
        userViewModel.sportsInAllMatches.observe(viewLifecycleOwner) { data ->
            val spinnerSports = mutableListOf("종목 전체")
            spinnerSports += data
            SpinnerUtils.setSpinnerAdapter(binding.spinnerSport, requireContext(), spinnerSports)
            binding.spinnerSport.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedSport = spinnerSports[position]
                        userViewModel.getFilteredMatches(
                            selectedSport,
                            "응원 팀 전체",
                            selectedYear
                        )
                        if (position != 0) {
                            selectedSportMyTeamNames = mutableListOf("응원 팀 전체")
                            val selectedSportMyTeams =
                                spinnerMyteams.filter { it.sport == selectedSport }.toMutableList()
                            selectedSportMyTeams.forEach { selectedSportMyTeamNames.add(it.name) }
                        } else {
                            selectedSportMyTeamNames = spinnerMyteamNames
                        }
                        SpinnerUtils.setSpinnerAdapter(
                            binding.spinnerMyTeam,
                            requireContext(),
                            selectedSportMyTeamNames
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedSport = spinnerSports[0]
                        userViewModel.getFilteredMatches(
                            selectedSport,
                            selectedMyteamName,
                            selectedYear
                        )
                    }
                }
        }

        userViewModel.myteamsInAllMatches.observe(viewLifecycleOwner) { data ->
            spinnerMyteams = data
            spinnerMyteamNames = mutableListOf("응원 팀 전체")
            data.forEach { spinnerMyteamNames.add(it.name) }
            SpinnerUtils.setSpinnerAdapter(binding.spinnerMyTeam, requireContext(), spinnerMyteamNames)
            binding.spinnerMyTeam.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedMyteamName = selectedSportMyTeamNames[position]
                        userViewModel.getFilteredMatches(
                            selectedSport,
                            selectedMyteamName,
                            selectedYear
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedMyteamName = spinnerMyteamNames[0]
                        userViewModel.getFilteredMatches(
                            selectedSport,
                            selectedMyteamName,
                            selectedYear
                        )
                    }
                }
        }

        userViewModel.yearsInAllMatches.observe(viewLifecycleOwner) { data ->
            val spinnerYears = mutableListOf("기간 전체")
            spinnerYears += data
            SpinnerUtils.setSpinnerAdapter(binding.spinnerPeriod, requireContext(), spinnerYears)

            binding.spinnerPeriod.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedYear = spinnerYears[position]
                        userViewModel.getFilteredMatches(
                            selectedSport,
                            selectedMyteamName,
                            selectedYear
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedYear = spinnerYears[0]
                        userViewModel.getFilteredMatches(
                            selectedSport,
                            selectedMyteamName,
                            selectedYear
                        )
                    }
                }
        }

        userViewModel.getFilteredMatches("종목 전체", "응원 팀 전체", "기간 전체")

        userViewModel.filteredMatches.observe(viewLifecycleOwner) {
            // Barchart 부분
            val matchesBarchart = binding.barchartMatches
            userViewModel.getMatchResultStat()
            userViewModel.matchResultCount.observe(viewLifecycleOwner) { data ->
                winCount = data[0]
                loseCount = data[1]
                tieCount = data[2]
                cancelCount = data[3]
                noResultCount = data[4]

                val entries = ArrayList<BarEntry>()
                entries.add(BarEntry(4f, winCount.toFloat()))
                entries.add(BarEntry(3f, loseCount.toFloat()))
                entries.add(BarEntry(2f, tieCount.toFloat()))
                entries.add(BarEntry(1f, cancelCount.toFloat()))
                entries.add(BarEntry(0f, noResultCount.toFloat()))

                // y축 반전
                //            entries.sortBy { it.x }

                // BarDataSet 생성
                val barDataSet = BarDataSet(entries, "직관 횟수") // 레이블
                barDataSet.color =
                    ContextCompat.getColor(requireContext(), R.color.main_mint) // 색상 지정

                // Y값 데이터 레이블 형식 지정
                barDataSet.setValueFormatter(object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}회"
                    }
                })

                // BarData 객체 생성
                val barData = BarData(barDataSet)

                // HorizontalBarChart에 데이터 설정
                matchesBarchart.data = barData

                // 바 너비/높이 설정
                val barWidth = 0.6f
                matchesBarchart.barData.barWidth = barWidth

                // 바 데이터에 값 레이블 표시
                barDataSet.setDrawValues(true) // 각 바 위에 레이블 표시
                barDataSet.valueTextSize = 10f  // 값 텍스트 크기 설정
                barDataSet.setValueTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_dark_gray
                    )
                )  // 값 텍스트 색상 설정

                // 색상 배열 설정
                val barColors = ArrayList<Int>()
                barColors.add(ContextCompat.getColor(requireContext(), R.color.main_mint))
                barColors.add(ContextCompat.getColor(requireContext(), R.color.calendar_events))
                barColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
                barColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
                barColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
                barDataSet.colors = barColors  // 색상 배열을 데이터셋에 적용

                // 불필요한 그리드 라인, 축 레이블 숨기기
                matchesBarchart.setDrawGridBackground(false) // 배경 그리드 비활성화
                matchesBarchart.setDrawBorders(false) // 차트의 테두리 비활성화

                // 좌측 여백 추가
                matchesBarchart.setExtraLeftOffset(25f) // 좌측 여백 추가

                // 범례를 비활성화
                matchesBarchart.legend.isEnabled = false

                // X축 설정
                val xAxis = matchesBarchart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM  // X축이 바닥에 위치
                xAxis.setDrawGridLines(false)  // 그리드 라인 숨기기
                xAxis.granularity = 1f  // 각 바가 1개의 항목에 대응하도록 설정
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            4 -> "승리"
                            3 -> "패배"
                            2 -> "무승부"
                            1 -> "경기 취소"
                            0 -> "타팀 직관"
                            else -> ""
                        }
                    }
                }

                // Y축 값 포맷팅 (선택 사항)
                val leftAxis = matchesBarchart.axisLeft
                leftAxis.isEnabled = false // Y축 라벨 비활성화
                leftAxis.setDrawGridLines(false) // Y축 그리드 라인 숨기기
                leftAxis.setDrawAxisLine(false) // Y축 선 숨기기
                //            leftAxis.isInverted = true

                val rightAxis = matchesBarchart.axisRight
                rightAxis.isEnabled = false // 오른쪽 Y축 비활성화

                // 차트 스타일 설정
                matchesBarchart.description.isEnabled = false  // 기본 설명 비활성화
                matchesBarchart.setFitBars(true)  // 바가 차트에 맞게 조정되도록 설정

                // 차트 애니메이션
                matchesBarchart.animateY(1000)

                // 차트 업데이트
                matchesBarchart.invalidate()

                // Piechart - 종합 승률 부분
                val allMatchesPiechart = binding.piechartAllMatches
                val allMatchesEntries = ArrayList<PieEntry>()
                allMatchesEntries.add(PieEntry(winCount.toFloat(), "승리"))
                allMatchesEntries.add(PieEntry(loseCount.toFloat(), "패배"))
                allMatchesEntries.add(PieEntry(tieCount.toFloat(), "무승부"))

                // PieDataSet 생성: 데이터와 색상을 설정
                val pieDataSet = PieDataSet(allMatchesEntries, "")
                pieDataSet.valueTextSize = 14f

                val pieColors = ArrayList<Int>()
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.main_mint))
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.calendar_events))
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
                pieDataSet.colors = pieColors  // 색상 배열을 데이터셋에 적용

                // PieData 생성
                val data = PieData(pieDataSet)

                // PieChart에 데이터 세팅
                allMatchesPiechart.data = data
                allMatchesPiechart.invalidate() // 차트 갱신

                // PieChart의 일부 스타일 설정
                allMatchesPiechart.setUsePercentValues(true)  // 퍼센트 값 표시
                allMatchesPiechart.setDrawEntryLabels(false)  // 내부 텍스트 레이블 비활성화
                //            allMatchesPiechart.setEntryLabelTextSize(16f)

                // 퍼센트 값을 00% 형식으로 포맷하기 위해 ValueFormatter 사용
                pieDataSet.valueFormatter = object : ValueFormatter() {
                    private val percentFormat = DecimalFormat("00%")  // 퍼센트 형식을 00%로 설정
                    override fun getFormattedValue(value: Float): String {
                        return percentFormat.format(value / 100f)  // 100으로 나누어서 00% 형식으로 출력
                    }
                }

                allMatchesPiechart.setDrawHoleEnabled(true) // 홀을 활성화
                allMatchesPiechart.holeRadius = 50f // 홀 크기 설정
                allMatchesPiechart.setHoleColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                ) // 홀 배경색 설정

                val pieCenterText = (winCount.toDouble()) / (winCount + loseCount + tieCount) * 100
                val decimalFormat = DecimalFormat("00%")
                val formattedCenterText = decimalFormat.format(pieCenterText / 100)
                allMatchesPiechart.setCenterText("${formattedCenterText}")
                allMatchesPiechart.setCenterTextSize(20f)  // 중심 텍스트 크기

                allMatchesPiechart.animateY(1000) // Y축 애니메이션

                // Description Label 제거
                allMatchesPiechart.description.isEnabled = false  // Description Label 비활성화

                // Legend 설정 (차트 외부에 레이블 표시) - 범례
                val legend = allMatchesPiechart.legend
                legend.isEnabled = true  // Legend 활성화
                legend.verticalAlignment =
                    com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment =
                    com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
                legend.orientation =
                    com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)  // 내부에 그리지 않도록 설정
                legend.textSize = 10f  // 텍스트 크기 설정
                legend.textColor =
                    ContextCompat.getColor(requireContext(), R.color.main_dark_gray)  // 텍스트 색상 설정

                // 차트의 오른쪽에만 여백을 추가하여 Legend를 배치 -> 차트의 이동 방지
                //            allMatchesPiechart.setExtraOffsets(0f, 0f, 150f, 0f)
            }

            // Piechart - 홈 / 어웨이 승률 부분
            userViewModel.getHomeAwayMatchStat()

            userViewModel.homeMatchResultCount.observe(viewLifecycleOwner) { _data ->
                homeWinCount = _data[0]
                homeLoseCount = _data[1]
                homeTieCount = _data[2]

                val homeMatchesPiechart = binding.piechartHomeMatches

                val homeMatchesEntries = ArrayList<PieEntry>()
                homeMatchesEntries.add(PieEntry(homeWinCount.toFloat(), "승리"))
                homeMatchesEntries.add(PieEntry(homeLoseCount.toFloat(), "패배"))
                homeMatchesEntries.add(PieEntry(homeTieCount.toFloat(), "무승부"))

                // PieDataSet 생성: 데이터와 색상을 설정
                val pieDataSet = PieDataSet(homeMatchesEntries, "")
                pieDataSet.valueTextSize = 12f

                val pieColors = ArrayList<Int>()
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.main_mint))
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.calendar_events))
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
                pieDataSet.colors = pieColors  // 색상 배열을 데이터셋에 적용

                // PieData 생성
                val data = PieData(pieDataSet)

                // PieChart에 데이터 세팅
                homeMatchesPiechart.data = data
                homeMatchesPiechart.invalidate() // 차트 갱신

                // PieChart의 일부 스타일 설정
                homeMatchesPiechart.setUsePercentValues(true)  // 퍼센트 값 표시
                homeMatchesPiechart.setDrawEntryLabels(false)  // 내부 텍스트 레이블 비활성화
                //            homeMatchesPiechart.setEntryLabelTextSize(14f)

                // 퍼센트 값을 00% 형식으로 포맷하기 위해 ValueFormatter 사용
                pieDataSet.valueFormatter = object : ValueFormatter() {
                    private val percentFormat = DecimalFormat("00%")  // 퍼센트 형식을 00%로 설정
                    override fun getFormattedValue(value: Float): String {
                        return percentFormat.format(value / 100f)  // 100으로 나누어서 00% 형식으로 출력
                    }
                }

                homeMatchesPiechart.setDrawHoleEnabled(true) // 홀을 활성화
                homeMatchesPiechart.holeRadius = 50f // 홀 크기 설정
                homeMatchesPiechart.setHoleColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                ) // 홀 배경색 설정

                val pieCenterText =
                    (homeWinCount.toDouble()) / (homeWinCount + homeLoseCount + homeTieCount) * 100
                val decimalFormat = DecimalFormat("00%")
                val formattedCenterText = decimalFormat.format(pieCenterText / 100)
                if (homeWinCount + homeLoseCount + homeTieCount == 0) {
                    homeMatchesPiechart.setCenterText("경기 없음")
                } else {
                    homeMatchesPiechart.setCenterText("${formattedCenterText}")
                }
                homeMatchesPiechart.setCenterTextSize(16f)  // 중심 텍스트 크기

                homeMatchesPiechart.animateY(1000) // Y축 애니메이션

                // Description Label 제거
                homeMatchesPiechart.description.isEnabled = false  // Description Label 비활성화

                // Legend 설정 (차트 외부에 레이블 표시) - 범례
                val legend = homeMatchesPiechart.legend
                legend.isEnabled = false  // Legend 활성화
            }
            // 어웨이 파이차트
            userViewModel.awayMatchResultCount.observe(viewLifecycleOwner) { data ->
                awayWinCount = data[0]
                awayLoseCount = data[1]
                awayTieCount = data[2]

                val awayMatchesPiechart = binding.piechartAwayMatches

                val awayMatchesEntries = ArrayList<PieEntry>()
                awayMatchesEntries.add(PieEntry(awayWinCount.toFloat(), "승리"))
                awayMatchesEntries.add(PieEntry(awayLoseCount.toFloat(), "패배"))
                awayMatchesEntries.add(PieEntry(awayTieCount.toFloat(), "무승부"))

                // PieDataSet 생성: 데이터와 색상을 설정
                val pieDataSet = PieDataSet(awayMatchesEntries, "")
                pieDataSet.valueTextSize = 12f

                val pieColors = ArrayList<Int>()
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.main_mint))
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.calendar_events))
                pieColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
                pieDataSet.colors = pieColors  // 색상 배열을 데이터셋에 적용

                // PieData 생성
                val data = PieData(pieDataSet)

                // PieChart에 데이터 세팅
                awayMatchesPiechart.data = data
                awayMatchesPiechart.invalidate() // 차트 갱신

                // PieChart의 일부 스타일 설정
                awayMatchesPiechart.setUsePercentValues(true)  // 퍼센트 값 표시
                awayMatchesPiechart.setDrawEntryLabels(false)  // 내부 텍스트 레이블 비활성화
                //            awayMatchesPiechart.setEntryLabelTextSize(14f)

                // 퍼센트 값을 00% 형식으로 포맷하기 위해 ValueFormatter 사용
                pieDataSet.valueFormatter = object : ValueFormatter() {
                    private val percentFormat = DecimalFormat("00%")  // 퍼센트 형식을 00%로 설정
                    override fun getFormattedValue(value: Float): String {
                        return percentFormat.format(value / 100f)  // 100으로 나누어서 00% 형식으로 출력
                    }
                }

                awayMatchesPiechart.setDrawHoleEnabled(true) // 홀을 활성화
                awayMatchesPiechart.holeRadius = 50f // 홀 크기 설정
                awayMatchesPiechart.setHoleColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                ) // 홀 배경색 설정

                val pieCenterText =
                    (awayWinCount.toDouble()) / (awayWinCount + awayLoseCount + awayTieCount) * 100
                val decimalFormat = DecimalFormat("00%")
                val formattedCenterText = decimalFormat.format(pieCenterText / 100)
                if (awayWinCount + awayLoseCount + awayTieCount == 0) {
                    awayMatchesPiechart.setCenterText("경기 없음")
                } else {
                    awayMatchesPiechart.setCenterText("${formattedCenterText}")
                }
                awayMatchesPiechart.setCenterTextSize(16f)  // 중심 텍스트 크기

                awayMatchesPiechart.animateY(1000) // Y축 애니메이션

                // Description Label 제거
                awayMatchesPiechart.description.isEnabled = false  // Description Label 비활성화

                // Legend 설정 (차트 외부에 레이블 표시) - 범례
                val legend = awayMatchesPiechart.legend
                legend.isEnabled = false  // Legend 활성화
            }

            binding.recyclerviewWinningStreak.adapter = winningStreakAdapter
            binding.recyclerviewWinningStreak.layoutManager = LinearLayoutManager(requireContext())

            // 최다 연승 기록 파트
            userViewModel.getWinningStreakData()
            userViewModel.winningStreakMatches.observe(viewLifecycleOwner) { data ->
                winningStreakMatches = data
                winningStreakAdapter.submitList(winningStreakMatches)
                if (winningStreakMatches.size > 0) {
                    binding.tvWinningStreak.text =
                        "내가 직관을 간 날,\n내 응원팀은 최다 ${winningStreakMatches.size}연승을 기록했어요!"
                } else {
                    binding.tvWinningStreak.text =
                        "아직 내 응원팀의 승리가 없어요🥲"
                }
            }

            // 요일별 승률 파트
            userViewModel.getWinningRatesByDay()
            userViewModel.winningRatesByDay.observe(viewLifecycleOwner) { data ->
                winningRatesByDay = data
                val decimalFormat = DecimalFormat("00%")
                val mondayWinningRate = decimalFormat.format(winningRatesByDay[0])
                val tuesdayWinningRate = decimalFormat.format(winningRatesByDay[1])
                val wednesdayWinningRate = decimalFormat.format(winningRatesByDay[2])
                val thursdayWinningRate = decimalFormat.format(winningRatesByDay[3])
                val fridayWinningRate = decimalFormat.format(winningRatesByDay[4])
                val saturdayWinningRate = decimalFormat.format(winningRatesByDay[5])
                val sundayWinningRate = decimalFormat.format(winningRatesByDay[6])

                val winningRatesByDay = listOf(
                    mondayWinningRate,
                    tuesdayWinningRate,
                    wednesdayWinningRate,
                    thursdayWinningRate,
                    fridayWinningRate,
                    saturdayWinningRate,
                    sundayWinningRate,
                )
                val days = mutableListOf("월", "화", "수", "목", "금", "토", "일")
                val max = winningRatesByDay
                    .map { it.replace("%", "").toInt() }  // '%' 제거하고 정수로 변환
                    .maxOrNull().toString() + "%"
                val indexes = mutableListOf<Int>()
                val winningRatesByDayCopy = winningRatesByDay.toMutableList()
                winningRatesByDayCopy.forEach {
                    if (it == max) {
                        val index = winningRatesByDayCopy.indexOf(it)
                        indexes.add(index)
                        winningRatesByDayCopy[index] = "00%"
                    }
                }
                val highestWinningRateDays = mutableListOf<String>()
                indexes.forEach {
                    highestWinningRateDays.add(days[it])
                }

                if (max == "0%") {
                    binding.tvWinByDay.text =
                        "아직 내 응원팀의 승리가 없어요🥲"
                } else {
                    binding.tvWinByDay.text =
                        "${highestWinningRateDays.joinToString(", ")}요일에 직관을 갔을 때\n승률이 가장 높았어요!"
                }

                binding.tvMondayWinRate.text = mondayWinningRate
                binding.tvTuesdayWinRate.text = tuesdayWinningRate
                binding.tvWednesdayWinRate.text = wednesdayWinningRate
                binding.tvThursdayWinRate.text = thursdayWinningRate
                binding.tvFridayWinRate.text = fridayWinningRate
                binding.tvSaturdayWinRate.text = saturdayWinningRate
                binding.tvSundayWinRate.text = sundayWinningRate

                val tvWinningRateList = listOf(
                    binding.tvMondayWinRate,
                    binding.tvTuesdayWinRate,
                    binding.tvWednesdayWinRate,
                    binding.tvThursdayWinRate,
                    binding.tvFridayWinRate,
                    binding.tvSaturdayWinRate,
                    binding.tvSundayWinRate
                )

                val monday = binding.tvMonday
                val tuesday = binding.tvTuesday
                val wednesday = binding.tvWednesday
                val thursday = binding.tvThursday
                val friday = binding.tvFriday
                val saturday = binding.tvSaturday
                val sunday = binding.tvSunday

                val tvDayList = listOf(
                    monday,
                    tuesday,
                    wednesday,
                    thursday,
                    friday,
                    saturday,
                    sunday
                )

                val allIndexes = mutableListOf(0, 1, 2, 3, 4, 5, 6)
                allIndexes.forEach {
                    val colorStateList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_gray
                        )
                    )
                    tvDayList[it].backgroundTintList = colorStateList
                    tvWinningRateList[it].setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_gray
                        )
                    )
                }

                indexes.forEach {
                    val colorStateList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_mint
                        )
                    )
                    tvDayList[it].backgroundTintList = colorStateList
                    tvWinningRateList[it].setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_mint
                        )
                    )
                }
            }

            // 최근 월별 승률 부분
            userViewModel.getMonthlyWinningRates()
            userViewModel.lastAndThisYearWinningRatesByMonth.observe(viewLifecycleOwner) { data ->

                val recent12MonthesWinningRates = mutableListOf<Float>()
                val thisMonth = LocalDate.now().monthValue

                for (i in thisMonth..thisMonth + 11) {
                    recent12MonthesWinningRates.add(data[i] * 100)
                }

                val recent12Monthes = mutableListOf(
                    thisMonth - 11,
                    thisMonth - 10,
                    thisMonth - 9,
                    thisMonth - 8,
                    thisMonth - 7,
                    thisMonth - 6,
                    thisMonth - 5,
                    thisMonth - 4,
                    thisMonth - 3,
                    thisMonth - 2,
                    thisMonth - 1,
                    thisMonth
                )

                for (i in 0..11) {
                    if (recent12Monthes[i] <= 0) {
                        recent12Monthes[i] += 12
                    }
                }

                // LineChart 뷰 가져오기
                val lineChart: LineChart = binding.linechartMatches

                // 꺾은선 그래프에 표시할 데이터 생성
                val entries = mutableListOf<Entry>()
                entries.add(Entry(0f, recent12MonthesWinningRates[0].toFloat()))
                entries.add(Entry(1f, recent12MonthesWinningRates[1].toFloat()))
                entries.add(Entry(2f, recent12MonthesWinningRates[2].toFloat()))
                entries.add(Entry(3f, recent12MonthesWinningRates[3].toFloat()))
                entries.add(Entry(4f, recent12MonthesWinningRates[4].toFloat()))
                entries.add(Entry(5f, recent12MonthesWinningRates[5].toFloat()))
                entries.add(Entry(6f, recent12MonthesWinningRates[6].toFloat()))
                entries.add(Entry(7f, recent12MonthesWinningRates[7].toFloat()))
                entries.add(Entry(8f, recent12MonthesWinningRates[8].toFloat()))
                entries.add(Entry(9f, recent12MonthesWinningRates[9].toFloat()))
                entries.add(Entry(10f, recent12MonthesWinningRates[10].toFloat()))
                entries.add(Entry(11f, recent12MonthesWinningRates[11].toFloat()))

                // LineDataSet 생성 (데이터 세트를 설정)
                val dataSet = LineDataSet(entries, "")

                // 데이터 세트 색상, 라인 스타일 등 설정
                dataSet.color = ContextCompat.getColor(requireContext(), R.color.main_mint)
                dataSet.setCircleColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_mint
                    )
                )  // 원 색상
                dataSet.lineWidth = 2f  // 선 두께
                dataSet.circleRadius = 5f  // 원 크기
                dataSet.setDrawCircleHole(true)  // 원의 구멍 여부 설정
                dataSet.setDrawValues(true) // 각 점에 값 표시할지 여부 설정
                dataSet.valueTextSize = 12f  // 텍스트 크기를 12f로 설정

                // 각 점에 표시되는 값의 형식 지정 (소수점 없이 정수로 표시)
                dataSet.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString() + "%"  // 소수점 없이 정수로 표시
                    }
                }

                // LineData 생성
                val lineData = LineData(dataSet)

                // LineChart에 데이터 적용
                lineChart.data = lineData

                val xAxis = lineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)  // 그리드 라인 숨기기
                xAxis.setGranularity(1f)  // 값 간격을 1로 설정 (x축에 모든 값 표시)
                xAxis.setGranularityEnabled(true)

                // 표시값 설정
                xAxis.valueFormatter = object : ValueFormatter() {
                    private val months = arrayOf(
                        "${recent12Monthes[0]}월",
                        "${recent12Monthes[1]}월",
                        "${recent12Monthes[2]}월",
                        "${recent12Monthes[3]}월",
                        "${recent12Monthes[4]}월",
                        "${recent12Monthes[5]}월",
                        "${recent12Monthes[6]}월",
                        "${recent12Monthes[7]}월",
                        "${recent12Monthes[8]}월",
                        "${recent12Monthes[9]}월",
                        "${recent12Monthes[10]}월",
                        "${recent12Monthes[11]}월"
                    )

                    override fun getFormattedValue(value: Float): String {
                        return months[value.toInt()]  // X축 값에 해당하는 월 이름 반환
                    }
                }

                val yAxis = lineChart.axisLeft
                yAxis.setDrawLabels(true)  // Y축 라벨 표시
                lineChart.axisRight.isEnabled = false  // 오른쪽 Y축 비활성화
                yAxis.setDrawGridLines(false)
                yAxis.axisMinimum = 0f  // Y축 최소값 0으로 설정
                yAxis.axisMaximum = 100f  // Y축 최대값 100으로 설정
                yAxis.granularity = 10f  // Y축 간격을 10으로 설정
                lineChart.animateXY(2000, 2000)  // X, Y축 애니메이션 시간 2초

                // 범례 비활성화
                lineChart.legend.isEnabled = false  // 범례를 숨김

                // Description Label 제거
                lineChart.description.isEnabled = false  // Description Label 비활성화

                // 그래프 스타일 설정
                lineChart.invalidate()  // 데이터를 변경한 후 그래프 다시 그리기
            }

            // 상대 팀별 승률

            val tvWinningRatesByOppositesList = listOf(
                binding.tvFirstMostWonOpposite,
                binding.tvSecondMostWonOpposite,
                binding.tvThirdMostWonOpposite,
                binding.tvFourthMostWonOpposite,
                binding.tvFifthMostWonOpposite
            )

            userViewModel.getWinningRatesByOpposites()
            userViewModel.winningRatesByOpposites.observe(viewLifecycleOwner) { data ->
                winningRatesByOpposites = data.sortedByDescending { it[2].toFloat() }
                var iterateSize = 0
                if (winningRatesByOpposites.size >= 5) {
                    iterateSize = 5
                } else {
                    iterateSize = winningRatesByOpposites.size
                }
                binding.tvFirstMostWonOpposite.text = ""
                binding.tvSecondMostWonOpposite.text = ""
                binding.tvThirdMostWonOpposite.text = ""
                binding.tvFourthMostWonOpposite.text = ""
                binding.tvFifthMostWonOpposite.text = ""
                for (i in 0..iterateSize - 1) {
                    tvWinningRatesByOppositesList[i].text =
                        "vs. ${winningRatesByOpposites[i][1]} - 승률 ${winningRatesByOpposites[i][2].toFloat() * 100}% (${winningRatesByOpposites[i][3]}전 ${winningRatesByOpposites[i][4]}승 ${winningRatesByOpposites[i][5]}무 ${winningRatesByOpposites[i][6]}패)"
                }
                if (winningRatesByOpposites[0][2] == "0.0") {
                    binding.tvWinningRateAgainstOpposites.text =
                        "아직 내 응원팀의 승리가 없어요🥲"
                } else {
                    binding.tvWinningRateAgainstOpposites.text =
                        "내가 응원한 팀은,\n ${winningRatesByOpposites[0][0]} \n을 상대로 가장 강했어요!"
                }

                val spannableString = SpannableString(tvWinningRatesByOppositesList[0].text)
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    tvWinningRatesByOppositesList[0].text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_mint
                        )
                    ),
                    0,
                    tvWinningRatesByOppositesList[0].text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvWinningRatesByOppositesList[0].text = spannableString
            }
        }

        binding.btnTabMypage.setOnClickListener {
            FragmentUtils.hideAndShowFragment(
                requireActivity().supportFragmentManager,
                this@MatchReportFragment,
                MyPageFragment(),
                "MyPageFragment"
            )
        }

    }

}
