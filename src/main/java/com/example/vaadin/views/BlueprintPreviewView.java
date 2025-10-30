package com.example.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * 設備図面（SVG）をプレビューし拡大率を調整するダイアログのデモビュー。
 */
@Route("blueprints/preview")
@PageTitle("設備図面プレビュー")
public class BlueprintPreviewView extends VerticalLayout {

    private static final int BASE_WIDTH = 640;
    private static final int BASE_HEIGHT = 360;
    private static final String BASE_WIDTH_PX = BASE_WIDTH + "px";
    private static final String BASE_HEIGHT_PX = BASE_HEIGHT + "px";

    private final Dialog previewDialog = new Dialog();
    private final Span zoomValue = new Span();
    private final Div svgCanvas = new Div();
    private Input zoomSlider;

    public BlueprintPreviewView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addClassName("app-view");

        configureDialog();

        // デモ用ナビゲーションと各種レイアウト要素を組み合わせて画面の枠組みを作成
        OperationsNavigationBar navigationBar = new OperationsNavigationBar();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);
        content.setWidthFull();
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        content.getStyle().set("gap", "16px");

        content.add(new H1("設備図面プレビュー"));
        content.add(new H2("拡大率に応じてスクロールが変化するダイアログ構成"));

        Paragraph description = new Paragraph(
            "設備改修プロジェクトで利用する SVG 図面を Vaadin Dialog 上で確認する想定です。"
        );
        content.add(description);

        Button openButton = new Button("図面プレビューを開く", event -> previewDialog.open());
        content.add(openButton);

        Div navWrapper = new Div(navigationBar);

        Div contentWrapper = new Div(content);
        contentWrapper.addClassName("app-content");

        Div frame = new Div(navWrapper, contentWrapper);
        frame.addClassName("app-frame");

        Div shell = new Div(frame);
        shell.addClassName("app-shell");
        shell.setSizeFull();

        add(shell, previewDialog);
    }

    private void configureDialog() {
        previewDialog.setModal(true);
        previewDialog.setDraggable(true);
        previewDialog.setResizable(false);
        previewDialog.setCloseOnEsc(true);
        previewDialog.setCloseOnOutsideClick(false);
        previewDialog.setWidth("780px");
        previewDialog.setMaxWidth("90vw");
        previewDialog.getElement().getStyle().set("--vaadin-dialog-header-padding", "0");
        previewDialog.getElement().getStyle().set("--vaadin-dialog-footer-padding", "12px 16px");

        Div dialogFrame = new Div();
        dialogFrame.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "12px")
            .set("background", "#d9d9d9")
            .set("padding", "16px")
            .set("border-radius", "10px")
            .set("box-shadow", "inset 0 0 0 1px rgba(0,0,0,0.08)");

        dialogFrame.add(createHeaderBar(), createPreviewSection(), createControlSection());

        previewDialog.add(dialogFrame);

        updateZoom(100);
    }

    private Div createHeaderBar() {
        Div headerBar = new Div();
        headerBar.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "12px")
            .set("padding", "12px 16px")
            .set("background", "#c6c6c6")
            .set("font-weight", "600")
            .set("font-size", "16px");

        Span title = new Span("図面: A-102 配電盤配置");
        zoomValue.getStyle().set("font-weight", "600");
        headerBar.add(title, zoomValue);
        return headerBar;
    }

    private Div createPreviewSection() {
        Div previewOuter = new Div();
        previewOuter.getStyle()
            .set("background", "#c6c6c6")
            .set("padding", "8px 8px 12px")
            .set("border-radius", "6px");

        // スクロール領域を想定した外枠と、その内部に拡大縮小対象のキャンバスを保持
        Div previewContainer = new Div();
        previewContainer.getStyle()
            .set("background", "#7ec2ff")
            .set("padding", "24px")
            .set("overflow", "auto")
            .set("border-radius", "4px")
            .set("box-shadow", "inset 0 0 0 2px rgba(255,255,255,0.4)")
            .set("box-sizing", "border-box");
        previewContainer.setWidthFull();
        previewContainer.setHeight(BASE_HEIGHT_PX);
        previewContainer.getStyle().set("max-height", BASE_HEIGHT_PX);

        svgCanvas.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("gap", "8px")
            .set("background", "linear-gradient(135deg, #cfe8ff, #e8f3ff)")
            .set("border", "2px dashed rgba(15, 23, 42, 0.35)")
            .set("border-radius", "12px")
            .set("color", "#0f172a")
            .set("font-weight", "600")
            .set("text-align", "center")
            .set("padding", "32px")
            .set("box-sizing", "border-box")
            .set("min-width", BASE_WIDTH_PX)
            .set("min-height", BASE_HEIGHT_PX);

        Span svgLabel = new Span("ELECTRICAL PLAN");
        svgLabel.getStyle().set("font-size", "24px");

        Paragraph note = new Paragraph("拡大率を上げると細部まで確認でき、外周はスクロールで追従します。");
        note.getStyle().set("margin", "0");

        svgCanvas.add(svgLabel, note);

        previewContainer.add(svgCanvas);
        previewOuter.add(previewContainer);

        return previewOuter;
    }

    private Div createControlSection() {
        Div controlSection = new Div();
        controlSection.getStyle()
            .set("background", "#6fd26f")
            .set("padding", "16px")
            .set("border-radius", "6px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "12px")
            .set("color", "#0f172a")
            .set("font-weight", "600");

        zoomSlider = new Input();
        zoomSlider.getElement().setAttribute("type", "range");
        zoomSlider.setValue("100");
        zoomSlider.getElement().setAttribute("min", "50");
        zoomSlider.getElement().setAttribute("max", "200");
        zoomSlider.getElement().setProperty("step", "10");
        zoomSlider.setWidthFull();
        // 数値の正当性を確認してから拡大率を反映
        zoomSlider.addValueChangeListener(event -> {
            String value = event.getValue();
            if (value != null && !value.isEmpty()) {
                try {
                    updateZoom(Double.parseDouble(value));
                } catch (NumberFormatException ignored) {
                    // 無効値は無視
                }
            }
        });

        controlSection.add(zoomSlider);
        return controlSection;
    }

    private void updateZoom(double zoom) {
        // SVGキャンバスの実際の表示サイズとラベルを拡大率に合わせて更新
        int percent = (int) Math.round(zoom);
        zoomValue.setText("拡大率：" + percent + " %");

        double scaledWidth = BASE_WIDTH * (zoom / 100.0);
        double scaledHeight = BASE_HEIGHT * (zoom / 100.0);
        svgCanvas.setWidth(scaledWidth + "px");
        svgCanvas.setHeight(scaledHeight + "px");
    }
}
