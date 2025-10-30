package com.example.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * ボタンから開くドラッガブルなポップアップウィンドウのデモビュー。
 */
@Route("draggable-window")
@PageTitle("ドラッガブルウィンドウ")
public class DraggableWindowDemoView extends VerticalLayout {

    private final Dialog floatingDialog = new Dialog();

    public DraggableWindowDemoView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addClassName("app-view");

        configureDialog();

        SampleNavigationBar navigationBar = new SampleNavigationBar();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);
        content.setWidthFull();
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        content.getStyle().set("gap", "18px");

        content.add(new H1("可動ウィンドウのサンプル"));
        content.add(new H2("ボタンから開き、マウスで自由に移動できる設定画面"));

        Paragraph intro = new Paragraph(
            "下のボタンを押すと設定ウィンドウが表示されます。ウィンドウはダイアログとして表示され、"
                + "ヘッダー部分をドラッグすることで任意の位置に移動できます。"
        );
        intro.getStyle().set("margin", "0");
        content.add(intro);

        Button openButton = new Button("設定ウィンドウを表示", event -> floatingDialog.open());
        openButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        content.add(openButton);

        Paragraph hint = new Paragraph("ウィンドウを閉じるにはヘッダー右上の×ボタン、または下部のボタンを利用してください。");
        hint.addClassName("app-inline-note");
        content.add(hint);

        Div contentWrapper = new Div(content);
        contentWrapper.addClassName("app-content");

        Div frame = new Div(navigationBar, contentWrapper);
        frame.addClassName("app-frame");

        Div shell = new Div(frame);
        shell.addClassName("app-shell");
        shell.setSizeFull();

        add(shell, floatingDialog);
    }

    private void configureDialog() {
        floatingDialog.setModal(false);
        // setDraggable(true) を指定すると、Vaadin Dialog のヘッダー部分をドラッグして位置を変更できる
        floatingDialog.setDraggable(true);
        floatingDialog.setResizable(false);
        floatingDialog.setCloseOnEsc(false);
        floatingDialog.setCloseOnOutsideClick(false);
        floatingDialog.setWidth("520px");

        floatingDialog.getElement().getStyle()
            .set("--vaadin-dialog-header-padding", "14px 18px")
            .set("--vaadin-dialog-content-padding", "0 18px 18px")
            .set("--vaadin-dialog-footer-padding", "0 18px 18px")
            .set("--vaadin-dialog-overlay-box-shadow", "0 18px 36px rgba(15,23,42,0.16)")
            .set("--vaadin-dialog-overlay-background-color", "transparent");

        floatingDialog.getHeader().add(createDialogHeader());

        floatingDialog.add(createDialogBody());

        floatingDialog.getFooter().add(createDialogFooter());
    }

    private HorizontalLayout createDialogHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setSpacing(false);
        header.setPadding(false);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
            .set("background", "rgba(15,23,42,0.06)")
            .set("border-radius", "9px")
            .set("padding", "10px 14px")
            .set("gap", "16px")
            // ヘッダーをドラッグハンドルとして明示するためにカーソルを移動アイコンに変更
            .set("cursor", "move");

        Span title = new Span("表示設定");
        title.getStyle()
            .set("font-weight", "700")
            .set("font-size", "16px")
            .set("letter-spacing", "0.02em")
            .set("color", "var(--app-panel-text, #17212a)");

        Span hint = new Span("ヘッダーをドラッグして移動");
        hint.getStyle()
            .set("font-size", "13px")
            .set("color", "rgba(15,23,42,0.7)");

        Button closeButton = new Button("×", event -> floatingDialog.close());
        closeButton.getElement().setAttribute("aria-label", "閉じる");
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getStyle()
            .set("margin-left", "auto")
            .set("font-size", "16px")
            .set("font-weight", "700")
            .set("color", "rgba(15,23,42,0.65)");

        header.add(title, hint, closeButton);
        return header;
    }

    private Div createDialogBody() {
        Div body = new Div();
        body.getStyle()
            .set("background", "var(--app-panel-color, #ffffff)")
            .set("border-radius", "12px")
            .set("padding", "24px")
            .set("box-shadow", "inset 0 0 0 1px rgba(15,23,42,0.08)");

        Paragraph message = new Paragraph("こちらはサンプルのポップアップメッセージです。画面上の任意の位置にドラッグして配置できます。");
        message.getStyle()
            .set("margin", "0")
            .set("font-size", "15px")
            .set("line-height", "1.7")
            .set("color", "rgba(15,23,42,0.85)");

        body.add(message);
        return body;
    }

    private HorizontalLayout createDialogFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setSpacing(true);
        footer.setPadding(false);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button close = new Button("キャンセル", event -> floatingDialog.close());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button apply = new Button("設定を適用", event -> {
            floatingDialog.close();
            Notification.show("設定を保存しました", 2500, Notification.Position.BOTTOM_CENTER);
        });
        apply.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        footer.add(close, apply);
        return footer;
    }
}
