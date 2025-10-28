package com.example.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * カレンダー表示とボタン操作パネルのサンプルビュー。
 */
@Route("calendar-buttons")
@PageTitle("カレンダーとボタン操作サンプル")
public class ButtonCalendarView extends VerticalLayout {

    private static final List<String> WEEKDAY_LABELS = List.of("月", "火", "水", "木", "金", "土", "日");

    public ButtonCalendarView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addClassName("app-view");

        SampleNavigationBar navigationBar = new SampleNavigationBar();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);
        content.setWidthFull();
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        content.getStyle().set("gap", "16px");

        content.add(new H1("カレンダーとボタン操作サンプル"));

        Div borderedLayout = new Div();
        // カレンダー全体ラッパーのスタイル（インライン）
        borderedLayout.getStyle()
            .set("border-radius", "16px")
            .set("background", "rgba(15, 23, 42, 0.04)")
            .set("padding", "28px")
            .set("box-sizing", "border-box")
            .set("box-shadow", "inset 0 -3px 0 rgba(15, 23, 42, 0.08)");

        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.setSizeFull();
        layoutRow.setSpacing(true);
        // カレンダーアクションコンテンツのスタイル（インライン）
        layoutRow.getStyle()
            .set("align-items", "flex-start")
            .set("gap", "32px");

        // 現在の年月を取得
        YearMonth currentMonth = YearMonth.now();
        YearMonth nextMonth = currentMonth.plusMonths(1);

        // カレンダーのタイトルを生成
        String currentTitle = String.format("%d年%d月", currentMonth.getYear(), currentMonth.getMonthValue());
        String nextTitle = String.format("%d年%d月", nextMonth.getYear(), nextMonth.getMonthValue());

        VerticalLayout calendar1 = createCalendar(currentTitle, currentMonth);
        VerticalLayout calendar2 = createCalendar(nextTitle, nextMonth);

        layoutRow.add(calendar1, calendar2);
        layoutRow.setFlexGrow(1, calendar1);
        layoutRow.setFlexGrow(1, calendar2);

        borderedLayout.add(layoutRow);
        content.add(borderedLayout);

        Div navWrapper = new Div(navigationBar);

        Div contentWrapper = new Div(content);
        contentWrapper.addClassName("app-content");

        Div frame = new Div(navWrapper, contentWrapper);
        frame.addClassName("app-frame");

        Div shell = new Div(frame);
        shell.addClassName("app-shell");
        shell.setSizeFull();

        add(shell);
    }

    private VerticalLayout createCalendar(String title, YearMonth month) {
        VerticalLayout calendarLayout = new VerticalLayout();
        calendarLayout.setPadding(false);
        calendarLayout.setSpacing(false);
        calendarLayout.setAlignItems(Alignment.STRETCH);
        // カレンダーカードのスタイル（インライン）
        calendarLayout.getStyle()
            .set("background", "rgba(255, 255, 255, 0.9)")
            .set("border-radius", "14px")
            .set("padding", "18px")
            .set("box-shadow", "0 10px 18px rgba(0, 0, 0, 0.08)");

        H3 heading = new H3(title);
        // カレンダーヘッディングのスタイル（インライン）
        heading.getStyle()
            .set("margin", "0 0 12px")
            .set("color", "var(--app-panel-text)");

        Div grid = new Div();
        // カレンダーグリッドのスタイル（インライン）
        grid.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(7, minmax(0, 1fr))")
            .set("gap", "10px")
            .set("margin-top", "12px");

        WEEKDAY_LABELS.forEach(label -> {
            Span weekday = new Span(label);
            // 曜日ラベルのスタイル（インライン）
            weekday.getStyle()
                .set("text-align", "center")
                .set("font-weight", "700")
                .set("padding", "8px 0")
                .set("color", "var(--app-panel-text)");
            grid.add(weekday);
        });

        LocalDate firstDay = month.atDay(1);
        int startOffset = dayOffset(firstDay.getDayOfWeek());
        int daysInMonth = month.lengthOfMonth();

        for (int i = 0; i < startOffset; i++) {
            Button blank = new Button("");
            blank.setEnabled(false);
            // 日付ボタンのスタイル（インライン）
            blank.getStyle()
                .set("width", "100%")
                .set("min-height", "44px");
            grid.add(blank);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            Button dayButton = new Button(String.valueOf(day));
            dayButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            // 日付ボタンのスタイル（インライン）
            dayButton.getStyle()
                .set("width", "100%")
                .set("min-height", "44px");

            // クリックでON/OFF切り替え
            dayButton.addClickListener(event -> {
                if (dayButton.getThemeNames().contains("primary")) {
                    // ON状態の場合、OFF（通常状態）に戻す
                    dayButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    dayButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                } else {
                    // OFF状態の場合、ON状態にする
                    dayButton.removeThemeVariants(ButtonVariant.LUMO_TERTIARY);
                    dayButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                }
            });

            grid.add(dayButton);
        }

        calendarLayout.add(heading, grid);
        return calendarLayout;
    }

    private int dayOffset(DayOfWeek dayOfWeek) {
        int value = dayOfWeek.getValue(); // Monday = 1 ... Sunday = 7
        return value - 1;
    }
}
