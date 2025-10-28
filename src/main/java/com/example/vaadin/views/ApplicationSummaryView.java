package com.example.vaadin.views;

import com.example.vaadin.model.ApplicationDraft;
import com.example.vaadin.session.UserSession;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * セッションに保存された申請内容を別画面で確認するビュー。
 */
@Route("application/summary")
public class ApplicationSummaryView extends VerticalLayout {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final UserSession userSession;

    public ApplicationSummaryView(UserSession userSession) {
        this.userSession = userSession;

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
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);
        content.getStyle().set("gap", "16px");

        content.add(new H1("申請内容の確認（セッション共有デモ）"));
        content.add(new Paragraph("同一ユーザー（=同じブラウザセッション）であれば、申請フォームで保存した内容がそのまま表示されます。"));
        content.add(createSessionInfoSection());
        content.add(createDraftSection());

        RouterLink backLink = new RouterLink("申請フォームに戻る", ApplicationFormView.class);
        stylePrimaryLink(backLink);
        content.add(backLink);

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

    private VerticalLayout createSessionInfoSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.addClassName("app-content-subsection");
        layout.add(new H2("セッション情報"));

        String sessionId = Optional.ofNullable(VaadinSession.getCurrent())
                .map(session -> session.getSession().getId())
                .orElse("取得できませんでした");

        Paragraph userId = new Paragraph("UserSession.userId = " + Optional.ofNullable(userSession.getUserId()).orElse("(未設定)"));
        Paragraph userName = new Paragraph("UserSession.userName = " + Optional.ofNullable(userSession.getUserName()).orElse("(未設定)"));
        Paragraph httpSession = new Paragraph("HttpSession ID = " + sessionId);

        layout.add(userId, userName, httpSession);
        return layout;
    }

    private VerticalLayout createDraftSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.addClassName("app-content-subsection");
        layout.add(new H2("申請内容"));

        ApplicationDraft draft = userSession.getAttribute(ApplicationFormView.SESSION_KEY, ApplicationDraft.class);
        if (draft == null) {
            layout.add(new Paragraph("セッションに申請内容が保存されていません。"));
            Button notifyButton = new Button("セッションが分かれているか確認する", event -> showSessionWarning());
            notifyButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            layout.add(notifyButton);
            return layout;
        }

        layout.add(new Paragraph("申請者ID: " + Optional.ofNullable(draft.getApplicantId()).orElse("(未入力)")));
        layout.add(new Paragraph("申請者名: " + Optional.ofNullable(draft.getApplicantName()).orElse("(未入力)")));
        layout.add(new Paragraph("所属部署: " + Optional.ofNullable(draft.getDepartment()).orElse("(未入力)")));
        layout.add(new Paragraph("申請区分: " + Optional.ofNullable(draft.getRequestType()).orElse("(未選択)")));
        layout.add(new Paragraph("最終更新: " + (draft.getUpdatedAt() == null ? "(未保存)" : FORMATTER.format(draft.getUpdatedAt()))));

        Html description = new Html("<div><strong>申請内容詳細:</strong><br>" +
                Optional.ofNullable(draft.getDescription()).orElse("(未入力)").replace("\n", "<br>") +
                "</div>");
        layout.add(description);
        return layout;
    }

    private void showSessionWarning() {
        Notification notification = Notification.show(
                "別ブラウザやシークレットウィンドウではセッションが分離され、ここにデータが表示されません。",
                4000,
                Notification.Position.TOP_CENTER
        );
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }

    private void stylePrimaryLink(RouterLink link) {
        link.addClassName("app-primary-link");
    }
}
