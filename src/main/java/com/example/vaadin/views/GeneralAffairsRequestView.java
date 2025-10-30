package com.example.vaadin.views;

import com.example.vaadin.model.GeneralAffairsRequest;
import com.example.vaadin.services.GeneralAffairsRequestService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * 総務部門への依頼を受け付けるフォームビュー。
 * <p>
 * Bean Validation による入力チェックと簡易的な受付履歴の表示機能を兼ね備えている。
 */
@Route("general-affairs/requests")
@PageTitle("総務依頼の受付")
public class GeneralAffairsRequestView extends VerticalLayout {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    private final GeneralAffairsRequestService requestService;

    private final BeanValidationBinder<GeneralAffairsRequest> binder =
        new BeanValidationBinder<>(GeneralAffairsRequest.class);

    private final TextField applicantId = new TextField("依頼者ID");
    private final TextField applicantName = new TextField("依頼者名");
    private final EmailField contactEmail = new EmailField("連絡先メールアドレス");
    private final TextField department = new TextField("所属部署");
    private final ComboBox<String> requestType = new ComboBox<>("依頼カテゴリ");
    private final DatePicker desiredDate = new DatePicker("希望実施日");
    private final TextArea description = new TextArea("依頼内容詳細");

    private final Button submitButton = new Button("依頼を送信");
    private final Button resetButton = new Button("入力をリセット");

    private final Grid<GeneralAffairsRequestService.SubmissionRecord> historyGrid =
        new Grid<>(GeneralAffairsRequestService.SubmissionRecord.class, false);
    private final Paragraph emptyHistoryMessage = new Paragraph("まだ受付履歴はありません。");

    public GeneralAffairsRequestView(GeneralAffairsRequestService requestService) {
        this.requestService = requestService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addClassName("app-view");

        configureFormFields();
        configureBinder();
        configureHistoryGrid();
        configureActions();
        refreshHistory();
        resetForm();

        OperationsNavigationBar navigationBar = new OperationsNavigationBar();

        VerticalLayout content = createContentLayout();

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

    private VerticalLayout createContentLayout() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);
        content.setWidthFull();
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);
        content.getStyle().set("gap", "16px");

        content.add(buildHeaderSection());
        content.add(buildFormSection());
        content.add(buildHistorySection());

        return content;
    }

    private Component buildHeaderSection() {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.setMargin(false);
        header.setWidthFull();
        header.addClassName("app-content-subsection");
        header.getStyle().set("gap", "8px");

        header.add(new H1("総務依頼の受付"));
        header.add(new Paragraph("備品調達や出張手配など、総務部門に寄せられる代表的な依頼をまとめて受け付けます。"
            + "必須項目のバリデーションと受付履歴の参照方法を示す実装例です。"));

        return header;
    }

    private Component buildFormSection() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(false);
        container.setSpacing(false);
        container.setMargin(false);
        container.setWidthFull();
        container.addClassName("app-content-subsection");
        container.getStyle().set("gap", "16px");

        H2 title = new H2("依頼内容の入力");
        title.getStyle().set("margin", "0");

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.setMaxWidth("720px");
        formLayout.add(applicantId, applicantName, contactEmail, department, requestType, desiredDate, description);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("640px", 2)
        );
        formLayout.setColspan(contactEmail, 2);
        formLayout.setColspan(description, 2);

        HorizontalLayout actions = new HorizontalLayout(submitButton, resetButton);
        actions.setSpacing(true);
        actions.setPadding(false);
        actions.setMargin(false);
        actions.setWidthFull();
        actions.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        container.add(title, formLayout, actions);
        return container;
    }

    private Component buildHistorySection() {
        VerticalLayout historySection = new VerticalLayout();
        historySection.setPadding(false);
        historySection.setSpacing(false);
        historySection.setMargin(false);
        historySection.setWidthFull();
        historySection.addClassName("app-content-subsection");
        historySection.getStyle().set("gap", "12px");

        H2 title = new H2("直近の受付履歴");
        title.getStyle().set("margin", "0");

        emptyHistoryMessage.getStyle()
            .set("color", "var(--lumo-secondary-text-color)")
            .set("margin", "0");

        historySection.add(title, emptyHistoryMessage, historyGrid);
        return historySection;
    }

    private void configureFormFields() {
        applicantId.setWidthFull();
        applicantId.setMaxLength(32);
        applicantId.setRequiredIndicatorVisible(true);
        applicantId.setPlaceholder("例: EMP-00123");
        applicantId.setAutoselect(true);
        applicantId.setClearButtonVisible(true);
        applicantName.setWidthFull();
        applicantName.setMaxLength(64);
        applicantName.setRequiredIndicatorVisible(true);
        applicantName.setPlaceholder("例: 山田花子");
        applicantName.setClearButtonVisible(true);

        contactEmail.setWidthFull();
        contactEmail.setMaxLength(128);
        contactEmail.setRequiredIndicatorVisible(true);
        contactEmail.setClearButtonVisible(true);
        contactEmail.setPlaceholder("example@example.com");
        contactEmail.setErrorMessage("正しいメールアドレス形式で入力してください");

        department.setWidthFull();
        department.setMaxLength(64);
        department.setPlaceholder("任意入力");
        department.setClearButtonVisible(true);

        requestType.setWidthFull();
        requestType.setRequiredIndicatorVisible(true);
        requestType.setItems("備品購入", "出張手配", "システム権限付与", "オフィスレイアウト変更");
        requestType.setPlaceholder("選択してください");
        requestType.setClearButtonVisible(true);

        desiredDate.setWidthFull();
        desiredDate.setRequiredIndicatorVisible(true);
        desiredDate.setLocale(Locale.JAPAN);
        desiredDate.setMin(LocalDate.now());
        desiredDate.setPlaceholder("日付を選択");
        desiredDate.setClearButtonVisible(true);

        description.setWidthFull();
        description.setMinHeight("160px");
        description.setMaxLength(500);
        description.setValueChangeMode(ValueChangeMode.EAGER);
        description.setPlaceholder("依頼の背景や特記事項を記載してください（500文字以内）");
        description.addValueChangeListener(event -> {
            String value = event.getValue();
            updateDescriptionHelper(value == null ? 0 : value.length());
        });
    }

    private void configureBinder() {
        binder.bindInstanceFields(this);
    }

    private void configureHistoryGrid() {
        historyGrid.setWidthFull();
        historyGrid.setAllRowsVisible(true);
        historyGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
        historyGrid.addColumn(log -> TIMESTAMP_FORMATTER.format(log.submittedAt()))
            .setHeader("受付日時")
            .setAutoWidth(true)
            .setFlexGrow(0);
        historyGrid.addColumn(log -> log.request().getApplicantId())
            .setHeader("依頼者ID")
            .setAutoWidth(true)
            .setFlexGrow(0);
        historyGrid.addColumn(log -> log.request().getApplicantName())
            .setHeader("依頼者名")
            .setAutoWidth(true);
        historyGrid.addColumn(log -> log.request().getRequestType())
            .setHeader("依頼カテゴリ")
            .setAutoWidth(true)
            .setFlexGrow(0);
        historyGrid.addColumn(log -> formatDesiredDate(log.request()))
            .setHeader("希望日")
            .setAutoWidth(true)
            .setFlexGrow(0);
        historyGrid.addColumn(log -> truncateDescription(log.request().getDescription()))
            .setHeader("内容概要")
            .setAutoWidth(true);
        historyGrid.getColumns().forEach(column -> column.setSortable(false));
    }

    private void configureActions() {
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(event -> handleSubmit());
        submitButton.setDisableOnClick(true);

        resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetButton.addClickListener(event -> {
            resetForm();
            Notification notification = Notification.show("入力内容をリセットしました", 2500, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            applicantId.focus();
        });
    }

    private void handleSubmit() {
        try {
            GeneralAffairsRequest request = new GeneralAffairsRequest();
            binder.writeBean(request);
            requestService.submit(request);
            refreshHistory();
            resetForm();
            Notification notification = Notification.show("依頼内容を総務部門に連携しました", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            applicantId.focus();
        } catch (ValidationException ex) {
            submitButton.setEnabled(true);
            Notification notification = Notification.show("入力内容を確認してください", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshHistory() {
        List<GeneralAffairsRequestService.SubmissionRecord> latest = requestService.findLatest();
        historyGrid.setItems(latest);
        boolean hasHistory = !latest.isEmpty();
        historyGrid.setVisible(hasHistory);
        emptyHistoryMessage.setVisible(!hasHistory);
    }

    private void resetForm() {
        applicantId.clear();
        applicantName.clear();
        contactEmail.clear();
        department.clear();
        requestType.clear();
        desiredDate.clear();
        description.clear();

        clearValidation(applicantId, applicantName, contactEmail, department, requestType, desiredDate, description);
        updateDescriptionHelper(0);
        submitButton.setEnabled(true);
    }

    @SafeVarargs
    private void clearValidation(HasValidation... fields) {
        for (HasValidation field : fields) {
            field.setInvalid(false);
            field.setErrorMessage(null);
        }
    }

    private void updateDescriptionHelper(int currentLength) {
        description.setHelperText(currentLength + " / 500 文字");
    }

    private String formatDesiredDate(GeneralAffairsRequest request) {
        LocalDate date = request.getDesiredDate();
        return date == null ? "-" : date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private String truncateDescription(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        String trimmed = value.trim();
        if (trimmed.length() <= 30) {
            return trimmed;
        }
        return trimmed.substring(0, 30) + "…";
    }
}
