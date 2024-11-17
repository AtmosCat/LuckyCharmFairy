import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.luckycharmfairy.data.model.User
import com.luckycharmfairy.data.viewmodel.UserViewModel
import com.luckycharmfairy.luckycharmfairy.R
import com.luckycharmfairy.luckycharmfairy.databinding.FragmentMatchReportBinding
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.luckycharmfairy.data.model.Match
import com.luckycharmfairy.presentation.mymatches.matchreport.WinningStreakAdapter
import java.text.DecimalFormat

class MatchReportFragment : Fragment() {

    private var _binding: FragmentMatchReportBinding? = null
    private val binding get() = _binding!!
    private var currentUser = User()

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
    private var winningMatchesByDay = mutableListOf<Int>()

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
        _binding = FragmentMatchReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            barDataSet.color = ContextCompat.getColor(requireContext(), R.color.main_mint) // 색상 지정

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
            barDataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.main_dark_gray))  // 값 텍스트 색상 설정

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
            val pieDataSet = PieDataSet(allMatchesEntries,"")
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
            allMatchesPiechart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.white)) // 홀 배경색 설정

            val pieCenterText = (winCount.toDouble())/(winCount + loseCount + tieCount) * 100
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
            legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)  // 내부에 그리지 않도록 설정
            legend.textSize = 10f  // 텍스트 크기 설정
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.main_dark_gray)  // 텍스트 색상 설정

            // 차트의 오른쪽에만 여백을 추가하여 Legend를 배치 -> 차트의 이동 방지
//            allMatchesPiechart.setExtraOffsets(0f, 0f, 150f, 0f)
        }

        // Piechart - 홈 / 어웨이 승률 부분
        userViewModel.getHomeAwayMatchStat()

        userViewModel.homeMatchResultCount.observe(viewLifecycleOwner) { data ->
            homeWinCount = data[0]
            homeLoseCount = data[1]
            homeTieCount = data[2]

            val homeMatchesPiechart = binding.piechartHomeMatches

            val homeMatchesEntries = ArrayList<PieEntry>()
            homeMatchesEntries.add(PieEntry(homeWinCount.toFloat(), "승리"))
            homeMatchesEntries.add(PieEntry(homeLoseCount.toFloat(), "패배"))
            homeMatchesEntries.add(PieEntry(homeTieCount.toFloat(), "무승부"))

            // PieDataSet 생성: 데이터와 색상을 설정
            val pieDataSet = PieDataSet(homeMatchesEntries,"")
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
            homeMatchesPiechart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.white)) // 홀 배경색 설정

            val pieCenterText = (homeWinCount.toDouble())/(homeWinCount + homeLoseCount + homeTieCount) * 100
            val decimalFormat = DecimalFormat("00%")
            val formattedCenterText = decimalFormat.format(pieCenterText / 100)
            homeMatchesPiechart.setCenterText("${formattedCenterText}")
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
            val pieDataSet = PieDataSet(awayMatchesEntries,"")
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
            awayMatchesPiechart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.white)) // 홀 배경색 설정

            val pieCenterText = (awayWinCount.toDouble())/(awayWinCount + awayLoseCount + awayTieCount) * 100
            val decimalFormat = DecimalFormat("00%")
            val formattedCenterText = decimalFormat.format(pieCenterText / 100)
            awayMatchesPiechart.setCenterText("${formattedCenterText}")
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
            binding.tvWinningStreak.text = "내가 직관을 간 날,\n내 응원팀은 최다 ${winningStreakMatches.size}연승을 기록했어요!"
        }

        // 요일별 승률 파트
        userViewModel.getWinningMatchesByDay()
        userViewModel.winningMatchesByDay.observe(viewLifecycleOwner) { data ->
            winningMatchesByDay = data
            val decimalFormat = DecimalFormat("00%")
            val mondayWinningRate = decimalFormat.format(winningMatchesByDay[0].toDouble() / winningMatchesByDay.sum())
            val tuesdayWinningRate = decimalFormat.format(winningMatchesByDay[1].toDouble() / winningMatchesByDay.sum())
            val wednesdayWinningRate = decimalFormat.format(winningMatchesByDay[2].toDouble() / winningMatchesByDay.sum())
            val thursdayWinningRate = decimalFormat.format(winningMatchesByDay[3].toDouble() / winningMatchesByDay.sum())
            val fridayWinningRate = decimalFormat.format(winningMatchesByDay[4].toDouble() / winningMatchesByDay.sum())
            val saturdayWinningRate = decimalFormat.format(winningMatchesByDay[5].toDouble() / winningMatchesByDay.sum())
            val sundayWinningRate = decimalFormat.format(winningMatchesByDay[6].toDouble() / winningMatchesByDay.sum())

            val winningRatesByDay = listOf(
                mondayWinningRate,
                tuesdayWinningRate,
                wednesdayWinningRate,
                thursdayWinningRate,
                fridayWinningRate,
                saturdayWinningRate,
                sundayWinningRate,
                )
            val days = mutableListOf("월","화","수","목","금","토","일")
            val max = winningRatesByDay.maxOrNull()
            val indexes = mutableListOf<Int>()
            winningRatesByDay.forEach {
                if (it == max) {
                    indexes.add(winningRatesByDay.indexOf(it))
                }
            }
            val highestWinningRateDays = mutableListOf<String>()
            indexes.forEach {
                highestWinningRateDays.add(days[it])
            }

            binding.tvWinByDay.text = "${highestWinningRateDays.joinToString(", ")}요일에 직관 승률이 가장 높았어요!"

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

            indexes.forEach {
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_mint))
                tvDayList[it].backgroundTintList = colorStateList
                tvWinningRateList[it].setTextColor(ContextCompat.getColor(requireContext(), R.color.main_mint))
            }
        }


    }

}
