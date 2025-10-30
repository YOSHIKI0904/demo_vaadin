package com.example.vaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * ボタンから開くドラッガブルなポップアップウィンドウのデモビュー。
 */
@Route("draggable-window")
@PageTitle("ドラッガブルウィンドウ")
public class DraggableWindowDemoView extends VerticalLayout {

    private final Dialog floatingDialog = new Dialog();
    private final TextField titleField = new TextField("タイトル");
    private final ComboBox<String> categoryField = new ComboBox<>("カテゴリ");
    private final Checkbox pinToScreen = new Checkbox("画面上にピン留めする");

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

        Paragraph hint = new Paragraph("ウィンドウを閉じるにはヘッダー右上の×ボタン、もしくはEscキーを使用してください。");
        hint.addClassName("app-inline-note");
        content.add(hint);

        Div navWrapper = new Div(navigationBar);
        Div contentWrapper = new Div(content);
        contentWrapper.addClassName("app-content");

        Div frame = new Div(navWrapper, contentWrapper);
        frame.addClassName("app-frame");

        Div shell = new Div(frame);
        shell.addClassName("app-shell");
        shell.setSizeFull();

        add(shell, floatingDialog);
    }

    private void configureDialog() {
        floatingDialog.setModal(false);
        floatingDialog.setDraggable(true);
        floatingDialog.setResizable(false);
        floatingDialog.setCloseOnEsc(true);
        floatingDialog.setCloseOnOutsideClick(false);
        floatingDialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        floatingDialog.setWidth("520px");

        Div dialogSurface = new Div();
        dialogSurface.addClassName("floating-dialog");
        dialogSurface.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "12px")
            .set("padding", "16px")
            .set("background", "linear-gradient(180deg, #ffffff 0%, #f5f7fa 100%)")
            .set("border-radius", "12px")
            .set("box-shadow", "0 18px 36px rgba(15,23,42,0.16)")
            .set("border", "1px solid rgba(15,23,42,0.12)");

        dialogSurface.add(createDialogHeader(), createDialogBody(), createDialogFooter());

        floatingDialog.removeAll();
        floatingDialog.add(dialogSurface);
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
            .set("gap", "16px");

        Span title = new Span("表示設定");
        title.getStyle()
            .set("font-weight", "700")
            .set("font-size", "16px")
            .set("letter-spacing", "0.02em")
            .set("color", "var(--app-panel-text, #17212a)");

        Span hint = new Span("枠をドラッグすると移動できます");
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
            .set("padding", "18px")
            .set("box-shadow", "inset 0 0 0 1px rgba(15,23,42,0.08)")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "18px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0px", 1),
            new FormLayout.ResponsiveStep("420px", 2)
        );

        titleField.setPlaceholder("画面タイトルを入力");
        titleField.setWidthFull();

        categoryField.setItems("全体表示", "部分表示", "作業指示", "ダッシュボード");
        categoryField.setPlaceholder("表示カテゴリーを選択");
        categoryField.setWidthFull();

        Checkbox showToolbar = new Checkbox("ツールバーを表示する");
        showToolbar.setValue(true);
        Checkbox showLegend = new Checkbox("凡例を下部に表示");
        showLegend.setValue(true);

        formLayout.add(titleField, categoryField, showToolbar, showLegend);

        TextArea remarks = new TextArea("メモ");
        remarks.setPlaceholder("必要な補足を入力してください");
        remarks.setWidthFull();
        remarks.setMaxLength(200);
        remarks.setClearButtonVisible(true);

        body.add(formLayout, remarks, pinToScreen);
        return body;
    }

    private HorizontalLayout createDialogFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setSpacing(true);
        footer.setPadding(false);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

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

