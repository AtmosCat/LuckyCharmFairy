import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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
            allMatchesPiechart.setEntryLabelTextSize(12f)

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
            allMatchesPiechart.setCenterText("${pieCenterText}%")
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


//        val homeMatchesPiechart = binding.piechartHomeMatches
//        val awayMatchesPiechart = binding.piechartAwayMatches


    }

}
