/*
 * Copyright 2016 Carmen Alvarez
 * <p/>
 * This file is part of Scrum Chatter.
 * <p/>
 * Scrum Chatter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * Scrum Chatter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Scrum Chatter. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.rmen.android.scrumchatter.chart;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.List;

import ca.rmen.android.scrumchatter.R;
import ca.rmen.android.scrumchatter.provider.MeetingMemberCursorWrapper;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * The meeting speaking-time chart displays the speaking time for each
 * member, in one meeting.  Each column in the chart corresponds to the speaking time for one member,
 * in that meeting.
 */
final class MeetingSpeakingTimeColumnChart {
    private MeetingSpeakingTimeColumnChart() {
        // prevent instantiation
    }

    public static void populateMeeting(Context context, ColumnChartView chart, @NonNull Cursor cursor) {
        List<AxisValue> xAxisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();

        MeetingMemberCursorWrapper cursorWrapper = new MeetingMemberCursorWrapper(cursor);
        int maxLabelLength = 0;
        while (cursorWrapper.moveToNext()) {
            List<SubcolumnValue> subcolumnValues = new ArrayList<>();
            Column column = new Column(subcolumnValues);

            Long memberId = cursorWrapper.getMemberId();
            String memberName = cursorWrapper.getMemberName();
            float durationInMinutes = (float) cursorWrapper.getDuration() / 60;
            String durationLabel = DateUtils.formatElapsedTime(cursorWrapper.getDuration());

            SubcolumnValue subcolumnValue = new SubcolumnValue();
            subcolumnValue.setValue(durationInMinutes);
            subcolumnValue.setLabel(durationLabel);
            int color = ChartUtils.getMemberColor(context, memberId);
            subcolumnValue.setColor(color);
            subcolumnValues.add(subcolumnValue);

            column.setHasLabels(true);
            column.setValues(subcolumnValues);
            columns.add(column);

            AxisValue xAxisValue = new AxisValue(xAxisValues.size());
            xAxisValue.setLabel(memberName);
            xAxisValues.add(xAxisValue);
            if (memberName.length() > maxLabelLength) maxLabelLength = memberName.length();
        }

        cursor.moveToPosition(-1);

        Axis xAxis = new Axis(xAxisValues);
        xAxis.setAutoGenerated(false);
        //xAxis.setMaxLabelChars(maxLabelLength);
        xAxis.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.chart_text, null));
        xAxis.setHasTiltedLabels(true);

        ColumnChartData data = new ColumnChartData();
        data.setAxisXBottom(xAxis);
        data.setColumns(columns);
        chart.setInteractive(true);
        chart.setColumnChartData(data);
        chart.setZoomEnabled(true);
        chart.setZoomType(ZoomType.HORIZONTAL);
    }

}
