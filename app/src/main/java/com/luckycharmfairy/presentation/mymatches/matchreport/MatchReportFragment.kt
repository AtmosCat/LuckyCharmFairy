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

        val matchesBarchart = binding.barchartMatches

        userViewModel.getMatchResultStat()
        userViewModel.matchResultCount.observe(viewLifecycleOwner) { data ->
            winCount = data[0]
            loseCount = data[1]
            tieCount = data[2]
            cancelCount = data[3]
            noResultCount = data[4]

            val entries = ArrayList<BarEntry>()
            entries.add(BarEntry(0f, winCount.toFloat()))
            entries.add(BarEntry(1f, loseCount.toFloat()))
            entries.add(BarEntry(2f, tieCount.toFloat()))
            entries.add(BarEntry(3f, cancelCount.toFloat()))
            entries.add(BarEntry(4f, noResultCount.toFloat()))

            // BarDataSet 생성
            val dataSet = BarDataSet(entries, "직관 횟수") // 레이블
            dataSet.color = ContextCompat.getColor(requireContext(), R.color.main_mint) // 색상 지정

            // BarData 객체 생성
            val barData = BarData(dataSet)

            // HorizontalBarChart에 데이터 설정
            matchesBarchart.data = barData

            // 바 너비/높이 설정
            val barWidth = 0.3f
            matchesBarchart.barData.barWidth = barWidth

            // 바 데이터에 값 레이블 표시
            dataSet.setDrawValues(true) // 각 바 위에 레이블 표시
            dataSet.valueTextSize = 10f  // 값 텍스트 크기 설정
            dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.main_dark_gray))  // 값 텍스트 색상 설정

            // 색상 배열 설정
            val barColors = ArrayList<Int>()
            barColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
            barColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
            barColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
            barColors.add(ContextCompat.getColor(requireContext(), R.color.main_medium_gray))
            barColors.add(ContextCompat.getColor(requireContext(), R.color.main_mint))
            dataSet.colors = barColors  // 색상 배열을 데이터셋에 적용

            // 불필요한 그리드 라인, 축 레이블 숨기기
            matchesBarchart.setDrawGridBackground(false) // 배경 그리드 비활성화
            matchesBarchart.setDrawBorders(false) // 차트의 테두리 비활성화

            // 범례를 비활성화
            matchesBarchart.legend.isEnabled = false

            // X축설정
            val xAxis = matchesBarchart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM  // X축이 바닥에 위치
            xAxis.setDrawGridLines(false)  // 그리드 라인 숨기기
            xAxis.granularity = 1f  // 각 바가 1개의 항목에 대응하도록 설정
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "승리"
                        1 -> "패배"
                        2 -> "무승부"
                        3 -> "경기 취소"
                        4 -> "타팀 직관"
                        else -> ""
                    }
                }
            }

            // y축 설정
            val yAxis = matchesBarchart.axisLeft
            yAxis.isEnabled = true  // Y축 활성화
            yAxis.setDrawLabels(true)  // Y축 값 표시 활성화
//        yAxis.setDrawGridLines(true)  // 그리드 라인 표시 (선택 사항)

            // Y축 값 포맷팅 (선택 사항)
//        yAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return when (value.toInt()) {
//                    0 -> "Item 1"
//                    1 -> "Item 2"
//                    2 -> "Item 3"
//                    3 -> "Item 4"
//                    4 -> "Item 5"
//                    else -> ""
//                }
//            }
//        }

            val leftAxis = matchesBarchart.axisLeft
            leftAxis.isEnabled = false // Y축 라벨 비활성화
            leftAxis.setDrawGridLines(false) // Y축 그리드 라인 숨기기
            leftAxis.setDrawAxisLine(false) // Y축 선 숨기기

            val rightAxis = matchesBarchart.axisRight
            rightAxis.isEnabled = false // 오른쪽 Y축 비활성화

            // 차트 스타일 설정
            matchesBarchart.description.isEnabled = false  // 기본 설명 비활성화
            matchesBarchart.setFitBars(true)  // 바가 차트에 맞게 조정되도록 설정

            // 차트 애니메이션
            matchesBarchart.animateY(1000)

            // 차트 업데이트
            matchesBarchart.invalidate()
        }


    }
}
