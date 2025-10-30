package com.example.vaadin.views;

import com.example.vaadin.model.GeneralAffairsDraft;
import com.example.vaadin.session.PortalUserSession;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

import java.time.LocalDateTime;

/**
 * 依頼内容を入力し、@VaadinSessionScope の {@link PortalUserSession} に保存する画面。
 */
@Route("general-affairs/draft")
@RouteAlias("general-affairs")
public class GeneralAffairsDraftView extends VerticalLayout {

    /**
     * PortalUserSession.sessionData へ保存する際のキー。
     * 旧UIで共有されていた static 変数に相当する「共有領域」をセッションごとに分割する意図を明示する。
     */
    public static final String SESSION_KEY = "generalAffairsDraft";

    /**
     * @VaadinSessionScope の PortalUserSession。
     * 同一ブラウザタブ＝同一 VaadinSession に紐づくため、画面を跨いでも値を再利用できる。
     */
    private final PortalUserSession userSession;

    private final TextField applicantIdField = new TextField("依頼者ID");
    private final TextField applicantNameField = new TextField("依頼者名");
    private final TextField departmentField = new TextField("所属部署");
    private final ComboBox<String> requestTypeField = new ComboBox<>("依頼カテゴリ");
    private final TextArea descriptionField = new TextArea("依頼内容の詳細");

    public GeneralAffairsDraftView(PortalUserSession userSession) {
        this.userSession = userSession;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addClassName("app-view");

        OperationsNavigationBar navigationBar = new OperationsNavigationBar();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);
        content.setWidthFull();
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);
        content.getStyle().set("gap", "16px");

        content.add(new H1("総務依頼の下書きフォーム"));
        content.add(new Paragraph("入力した依頼内容は @VaadinSessionScope の PortalUserSession に保存されます。"));

        configureFields();

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("600px");
        formLayout.addClassName("app-content-subsection");
        formLayout.add(applicantIdField, applicantNameField, departmentField, requestTypeField, descriptionField);

        Button saveButton = new Button("依頼内容を一時保存", event -> saveDraft());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button clearButton = new Button("保存内容をクリア", event -> clearDraft());
        clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(saveButton, clearButton);
        actions.setSpacing(true);
        actions.setPadding(false);
        actions.setMargin(false);
        actions.setWidthFull();
        actions.addClassName("app-content-subsection");

        RouterLink toSummary = new RouterLink("下書きの確認へ移動", GeneralAffairsDraftSummaryView.class);
        stylePrimaryLink(toSummary);

        Paragraph note = new Paragraph("→ セッションに保存された内容は下書き確認画面から参照できます。");
        note.getElement().getClassList().add("app-inline-note");

        content.add(formLayout, actions, note, toSummary);

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

    private void stylePrimaryLink(RouterLink link) {
        link.addClassName("app-primary-link");
    }

    private void configureFields() {
        requestTypeField.setItems("備品購入", "出張手配", "システム権限付与", "オフィスレイアウト変更");
        requestTypeField.setPlaceholder("選択してください");
        requestTypeField.setClearButtonVisible(true);

        descriptionField.setWidthFull();
        descriptionField.setMinHeight("150px");
        descriptionField.setPlaceholder("依頼の背景や補足事項を入力してください");

        // PortalUserSession から事前に保存されたドラフトを復元。
        // 別画面や F5 後も同じセッションであればここで値が初期表示される。
        GeneralAffairsDraft draft = userSession.getAttribute(SESSION_KEY, GeneralAffairsDraft.class);
        if (draft != null) {
            applicantIdField.setValue(draft.getApplicantId() == null ? "" : draft.getApplicantId());
            applicantNameField.setValue(draft.getApplicantName() == null ? "" : draft.getApplicantName());
            departmentField.setValue(draft.getDepartment() == null ? "" : draft.getDepartment());
            requestTypeField.setValue(draft.getRequestType());
            descriptionField.setValue(draft.getDescription() == null ? "" : draft.getDescription());
        }
    }

    private void saveDraft() {
        GeneralAffairsDraft draft = userSession.getAttribute(SESSION_KEY, GeneralAffairsDraft.class);
        if (draft == null) {
            draft = new GeneralAffairsDraft();
        }

        // 画面入力値をドラフトへ詰め替え。PortalUserSession 側で Serializable な DTO として保持する。
        draft.setApplicantId(applicantIdField.getValue());
        draft.setApplicantName(applicantNameField.getValue());
        draft.setDepartment(departmentField.getValue());
        draft.setRequestType(requestTypeField.getValue());
        draft.setDescription(descriptionField.getValue());
        draft.setUpdatedAt(LocalDateTime.now());

        // 従来クライアント実装で共有していた static 変数代わりに VaadinSession スコープへ格納し、別ビューでも取り出せるようにする。
        userSession.setAttribute(SESSION_KEY, draft);
        // ついでに PortalUserSession の簡易プロフィールも更新しておくと、他画面で共通的に利用できる。
        userSession.setUserId(draft.getApplicantId());
        userSession.setUserName(draft.getApplicantName());

        Notification notification = Notification.show("依頼内容を下書き保存しました", 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void clearDraft() {
        // セッション上のドラフトのみを削除。PortalUserSession インスタンス自体は Vaadin が管理しているので破棄不要。
        userSession.removeAttribute(SESSION_KEY);
        Notification notification = Notification.show("下書きを削除しました", 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);

        applicantIdField.clear();
        applicantNameField.clear();
        departmentField.clear();
        requestTypeField.clear();
        descriptionField.clear();
    }
}
