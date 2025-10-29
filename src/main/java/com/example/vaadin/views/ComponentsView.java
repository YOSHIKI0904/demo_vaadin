package com.example.vaadin.views;

import com.example.vaadin.model.SampleData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.RangeInput;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 各種UI部品のサンプルView
 * Swing/AWTからVaadinへの移行例を示す
 */
@Route("")  // ルートパス（http://localhost:8080/）でアクセス
public class ComponentsView extends VerticalLayout {


    // todo これ最初に宣言したほうがいいのはなぜ？
    // UI部品
    private TextField nameField;
    private TextField emailField;
    private TextArea descriptionArea;
    private IntegerField ageSpinner;
    private ComboBox<String> editableComboBox;
    private Select<String> nonEditableSelect;
    private RadioButtonGroup<String> radioGroup;
    private Checkbox agreeCheckbox;
    private Checkbox notifyCheckbox;
    private Grid<SampleData> dataGrid;
    private Button saveButton;
    private Button clearButton;
    private RangeInput rangeInput;

    // データ保持用
    private ListDataProvider<SampleData> dataProvider;

    public ComponentsView() {
        // データの初期化
        dataProvider = new ListDataProvider<>(createSampleData());

        // レイアウト設定
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

        // タイトル
        content.add(new H1("Vaadin 24 コンポーネントサンプル"));
        content.add(new H2("Swing/AWT からの移行例"));

        // 各セクションを追加
        content.add(createTextFieldSection());
        content.add(createTextAreaSection());
        content.add(createNumberSpinnerSection());
        content.add(createSelectSection());
        content.add(createRadioButtonSection());
        content.add(createCheckboxSection());
        content.add(createButtonSection());
        content.add(createGridSection());
        content.add(createRangeInput());

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

    /**
     * テキストフィールドセクション（Swing: JTextField）
     */
    private VerticalLayout createTextFieldSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("1. テキストボックス (TextField)"));

        // 名前入力
        nameField = new TextField("名前");
        nameField.setPlaceholder("山田太郎");
        nameField.setHelperText("フルネームを入力してください");
        nameField.setWidth("300px");

        // メール入力
        emailField = new TextField("メールアドレス");
        emailField.setPlaceholder("example@example.com");
        emailField.setHelperText("有効なメールアドレスを入力してください");
        emailField.setWidth("300px");

        // バリデーション例
        nameField.addValueChangeListener(event -> {
            if (event.getValue().length() < 2) {
                nameField.setInvalid(true);
                nameField.setErrorMessage("名前は2文字以上で入力してください");
            } else {
                nameField.setInvalid(false);
            }
        });

        layout.add(nameField, emailField);
        return layout;
    }

    /**
     * テキストエリアセクション（Swing: JTextArea）
     */
    private VerticalLayout createTextAreaSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("2. テキストエリア (TextArea)"));

        // 説明入力
        descriptionArea = new TextArea("説明");
        descriptionArea.setPlaceholder("詳細な説明を入力してください...");
        descriptionArea.setHelperText("複数行のテキストを入力できます");
        descriptionArea.setWidth("600px");
        descriptionArea.setHeight("120px");
        descriptionArea.setMaxLength(500);
        descriptionArea.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.EAGER);

        // 文字数カウンター
        descriptionArea.addValueChangeListener(event -> {
            int length = event.getValue().length();
            descriptionArea.setHelperText("文字数: " + length + " / 500");
        });

        layout.add(descriptionArea);
        return layout;
    }

    /**
     * 数値スピナーセクション（Swing: JSpinner）
     */
    private VerticalLayout createNumberSpinnerSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("3. 数値スピナー (IntegerField)"));

        // 年齢入力
        ageSpinner = new IntegerField("年齢");
        ageSpinner.setValue(30);
        ageSpinner.setMin(0);
        ageSpinner.setMax(150);
        ageSpinner.setStep(1);
        ageSpinner.setWidth("200px");
        ageSpinner.setHelperText("0〜150の範囲で入力してください");

        // 値の変更イベント
        ageSpinner.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue() < 0 || event.getValue() > 150) {
                    ageSpinner.setInvalid(true);
                    ageSpinner.setErrorMessage("年齢は0〜150の範囲で入力してください");
                } else {
                    ageSpinner.setInvalid(false);
                }
            }
        });

        layout.add(ageSpinner);
        return layout;
    }

    /**
     * セレクトボックスセクション
     * Swing: JComboBox
     */
    private VerticalLayout createSelectSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("4. セレクトボックス"));

        // テキスト入力可能なコンボボックス（Swing: JComboBox with editable=true）
        editableComboBox = new ComboBox<>("都道府県（入力可能）");
        editableComboBox.setItems("東京都", "神奈川県", "大阪府", "愛知県", "北海道");
        editableComboBox.setAllowCustomValue(true);
        editableComboBox.setWidth("300px");
        editableComboBox.setHelperText("リストから選択、または直接入力できます");

        // カスタム値の処理
        editableComboBox.addCustomValueSetListener(event -> {
            editableComboBox.setValue(event.getDetail());
            Notification.show("カスタム値が入力されました: " + event.getDetail());
        });

        // テキスト入力不可のセレクト（Swing: JComboBox with editable=false）
        nonEditableSelect = new Select<>();
        nonEditableSelect.setLabel("ステータス（選択のみ）");
        nonEditableSelect.setItems("有効", "無効", "保留中");
        nonEditableSelect.setValue("有効");
        nonEditableSelect.setWidth("300px");
        nonEditableSelect.setHelperText("リストから選択のみ可能です");

        layout.add(editableComboBox, nonEditableSelect);
        return layout;
    }

    /**
     * ラジオボタンセクション（Swing: JRadioButton + ButtonGroup）
     */
    private VerticalLayout createRadioButtonSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("5. ラジオボタン (RadioButtonGroup)"));

        radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel("性別");
        radioGroup.setItems("男性", "女性", "その他");
        radioGroup.setValue("男性");

        // 選択変更イベント
        radioGroup.addValueChangeListener(event -> {
            Notification.show("選択: " + event.getValue());
        });

        layout.add(radioGroup);
        return layout;
    }

    /**
     * チェックボックスセクション（Swing: JCheckBox）
     */
    private VerticalLayout createCheckboxSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("6. チェックボックス (Checkbox)"));

        agreeCheckbox = new Checkbox("利用規約に同意する");
        agreeCheckbox.setValue(false);

        notifyCheckbox = new Checkbox("通知を受け取る");
        notifyCheckbox.setValue(true);

        // チェック変更イベント
        agreeCheckbox.addValueChangeListener(event -> {
            saveButton.setEnabled(event.getValue());
        });

        layout.add(agreeCheckbox, notifyCheckbox);
        return layout;
    }

    /**
     * ボタンセクション（Swing: JButton）
     */
    private HorizontalLayout createButtonSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.add(new H2("7. ボタン (Button)"));

        // 保存ボタン
        saveButton = new Button("保存");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);  // 初期状態では無効（利用規約未同意）

        saveButton.addClickListener(event -> {
            // データの保存処理
            String name = nameField.getValue();
            String email = emailField.getValue();
            // 参考: prefecture と gender は入力制御などに使用可能
            // String prefecture = editableComboBox.getValue();
            String status = nonEditableSelect.getValue();
            // String gender = radioGroup.getValue();

            long nextId = dataProvider.getItems().size() + 1L;
            // 新しいデータを追加
            SampleData newData = new SampleData(
                    nextId,
                    name,
                    email,
                    status,
                    30
            );
            dataProvider.getItems().add(newData);
            dataProvider.refreshAll();

            // 成功メッセージ
            Notification notification = Notification.show(
                    "データを保存しました: " + name,
                    3000,
                    Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        // クリアボタン
        clearButton = new Button("クリア");
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        clearButton.addClickListener(event -> {
            nameField.clear();
            emailField.clear();
            descriptionArea.clear();
            ageSpinner.setValue(30);
            editableComboBox.clear();
            nonEditableSelect.setValue("有効");
            radioGroup.setValue("男性");
            agreeCheckbox.setValue(false);
            notifyCheckbox.setValue(true);

            Notification.show("フォームをクリアしました");
        });

        layout.add(saveButton, clearButton);
        return layout;
    }

    /**
     * グリッドセクション（Swing: JTable）
     */
    private VerticalLayout createGridSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("8. テーブル (Grid)"));

        // Gridの作成（Swing: JTable）
        dataGrid = new Grid<>(SampleData.class, false);
        dataGrid.setHeight("220px");

        // カラムの定義
        dataGrid.addColumn(SampleData::getId).setHeader("ID").setWidth("80px");
        dataGrid.addColumn(SampleData::getName).setHeader("名前").setWidth("150px");
        dataGrid.addColumn(SampleData::getEmail).setHeader("メール").setWidth("200px");
        dataGrid.addColumn(SampleData::getStatus).setHeader("ステータス").setWidth("120px");
        dataGrid.addColumn(SampleData::getAge).setHeader("年齢").setWidth("80px");

        // データの設定
        dataGrid.setItems(dataProvider);

        // 行選択イベント
        dataGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(data -> {
                Notification.show("選択: " + data.getName());
            });
        });

        layout.add(dataGrid);
        return layout;
    }

    /**
     * サンプルデータの作成
     */
    private List<SampleData> createSampleData() {
        List<SampleData> list = new ArrayList<>();
        list.add(new SampleData(1L, "山田太郎", "yamada@example.com", "有効", 30));
        list.add(new SampleData(2L, "鈴木花子", "suzuki@example.com", "有効", 25));
        list.add(new SampleData(3L, "田中一郎", "tanaka@example.com", "無効", 35));
        list.add(new SampleData(4L, "佐藤美咲", "sato@example.com", "保留中", 28));
        return list;
    }

    /**
     * グリッドセクション（Swing: JTable）
     */
    private VerticalLayout createRangeInput() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("9.範囲指定"));

        rangeInput = new RangeInput();
        rangeInput.setMin(0);
        rangeInput.setMax(100);
        rangeInput.setStep(1.0);
        rangeInput.setValue(50.0);

        // 数値を表示するためのコンポーネントを作成
        Span valueDisplay = new Span("スライダーの値: " + rangeInput.getValue());
        
        // スライダーの値が変更されたときのリスナーを設定
        rangeInput.addValueChangeListener(event -> {
            // イベントから新しい値を取得
            Double newValue = event.getValue();
            
            // 表示用のテキストを更新
            valueDisplay.setText("スライダーの値: " + newValue);
        });

        layout.add(rangeInput, valueDisplay);
        return layout;
    }
}
