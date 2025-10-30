package com.example.vaadin.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主要なデモ画面間を移動するための簡易ナビゲーションバー。
 */
public class SampleNavigationBar extends HorizontalLayout {

    public SampleNavigationBar() {
        setWidthFull();
        setSpacing(true);
        setPadding(false);
        setAlignItems(FlexComponent.Alignment.CENTER);

        // ナビゲーションバーのスタイル設定（インライン）
        getStyle()
            .set("background", "var(--app-panel-color)")
            .set("border-radius", "12px")
            .set("padding", "8px 14px")
            .set("box-shadow", "inset 0 -3px 0 rgba(15, 23, 42, 0.05)")
            .set("flex-shrink", "0");

        Span title = new Span("サンプル一覧:");
        title.getStyle()
            .set("font-weight", "700")
            .set("letter-spacing", "0.08em")
            .set("color", "var(--app-panel-text)");

        add(title);
        addLinks();
    }

    private void addLinks() {
        Map<String, Class<? extends Component>> links = new LinkedHashMap<>();
        links.put("UIコンポーネント", ComponentsView.class);
        links.put("カレンダー操作", ButtonCalendarView.class);
        links.put("申請フォーム", ApplicationFormView.class);
        links.put("シンプル申請", SimpleApplicationView.class);
        links.put("路線図ビュー", RailwayMapView.class);
        links.put("SVGポップアップ", SvgPopupDemoView.class);
        links.put("ドラッガブルウィンドウ", DraggableWindowDemoView.class);

        links.forEach((text, target) -> {
            RouterLink link = new RouterLink(text, target);

            // リンクのスタイル設定（インライン）
            link.getStyle()
                .set("color", "var(--app-panel-text)")
                .set("font-weight", "600")
                .set("text-decoration", "none")
                .set("padding", "10px 18px")
                .set("border-radius", "12px")
                .set("border", "1px solid rgba(15, 23, 42, 0.08)")
                .set("background", "rgba(15, 23, 42, 0.04)")
                .set("transition", "background-color 0.2s ease, border-color 0.2s ease, transform 0.2s ease");

            add(link);
        });
    }
}
