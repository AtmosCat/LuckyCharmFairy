package com.luckycharmfairy.presentation

import android.graphics.Color
import com.luckycharmfairy.luckycharmfairy.R
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.HashSet;


public class EventDecorator(dates: List<CalendarDay>) : DayViewDecorator {

    private val dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(7f, Color.parseColor("#FF6F61"))) // 점 추가
    }
}
