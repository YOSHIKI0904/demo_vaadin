package com.example.vaadin.views;

import com.example.vaadin.model.EmployeeProfile;
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
 * 社員管理ツールを題材に各種 UI コンポーネントの使い方を示すビュー。
 * レガシー UI からの移行時に想定される入力項目を例に解説する。
 */
@Route("")  // ルートパス（http://localhost:8080/）でアクセス
public class EmployeeToolkitView extends VerticalLayout {


    // ? これ最初に宣言したほうがいいのはなぜ？
    // クラス内の複数メソッド（イベントハンドラ含む）から参照するためフィールド化が妥当。
    // final によりビュー生成時に一度だけ初期化され再代入されないことも明示でき、ローカル変数にした場合に生じる参照の受け渡しも不要になる。

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
    private Grid<EmployeeProfile> dataGrid;
    private Button saveButton;
    private Button clearButton;
    private RangeInput rangeInput;

    // データ保持用
    private ListDataProvider<EmployeeProfile> dataProvider;

    public EmployeeToolkitView() {
        // データの初期化
        dataProvider = new ListDataProvider<>(createSampleData());

        // レイアウト設定
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
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        content.getStyle().set("gap", "16px");

        // タイトル
        content.add(new H1("社員管理ツールの UI コンポーネント集"));
        content.add(new H2("人事業務で使う入力パターンを Vaadin で再現"));

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
     * テキストフィールドセクション（JTextField の置き換え例）
     */
    private VerticalLayout createTextFieldSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("1. 基本情報入力 (TextField)"));

        // 名前入力
        nameField = new TextField("氏名");
        nameField.setPlaceholder("山田 太郎");
        nameField.setHelperText("戸籍上の氏名を入力してください");
        nameField.setWidth("300px");

        // メール入力
        emailField = new TextField("社用メールアドレス");
        emailField.setPlaceholder("taro.yamada@company.jp");
        emailField.setHelperText("社内ドメインのメールアドレスを入力してください");
        emailField.setWidth("300px");

        // バリデーション例
        nameField.addValueChangeListener(event -> {
            if (event.getValue().length() < 2) {
                nameField.setInvalid(true);
                nameField.setErrorMessage("氏名は2文字以上で入力してください");
            } else {
                nameField.setInvalid(false);
            }
        });

        layout.add(nameField, emailField);
        return layout;
    }

    /**
     * テキストエリアセクション（JTextArea の置き換え例）
     */
    private VerticalLayout createTextAreaSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("2. キャリアメモ (TextArea)"));

        // 説明入力
        descriptionArea = new TextArea("キャリアメモ");
        descriptionArea.setPlaceholder("担当業務や目標、配慮事項などを入力してください...");
        descriptionArea.setHelperText("複数行でメモを残せます");
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
     * 数値スピナーセクション（JSpinner の置き換え例）
     */
    private VerticalLayout createNumberSpinnerSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("3. 勤続年数 (IntegerField)"));

        // 年齢入力
        ageSpinner = new IntegerField("勤続年数（年）");
        ageSpinner.setValue(5);
        ageSpinner.setMin(0);
        ageSpinner.setMax(50);
        ageSpinner.setStep(1);
        ageSpinner.setWidth("200px");
        ageSpinner.setHelperText("0〜50年の範囲で入力してください");

        // 値の変更イベント
        ageSpinner.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue() < 0 || event.getValue() > 50) {
                    ageSpinner.setInvalid(true);
                    ageSpinner.setErrorMessage("勤続年数は0〜50年の範囲で入力してください");
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
     * JComboBox の置き換え例
     */
    private VerticalLayout createSelectSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("4. 所属・雇用区分 (Select)"));

        // テキスト入力可能なコンボボックス（JComboBox with editable=true に対応）
        editableComboBox = new ComboBox<>("所属拠点（入力可能）");
        editableComboBox.setItems("東京本社", "名古屋支社", "大阪オフィス", "福岡サービスセンター", "札幌カスタマー拠点");
        editableComboBox.setAllowCustomValue(true);
        editableComboBox.setWidth("300px");
        editableComboBox.setHelperText("リストにない拠点名も直接入力できます");

        // カスタム値の処理
        editableComboBox.addCustomValueSetListener(event -> {
            editableComboBox.setValue(event.getDetail());
            Notification.show("新しい拠点を登録しました: " + event.getDetail());
        });

        // テキスト入力不可のセレクト（JComboBox with editable=false に対応）
        nonEditableSelect = new Select<>();
        nonEditableSelect.setLabel("雇用区分（選択のみ）");
        nonEditableSelect.setItems("正社員", "契約社員", "派遣社員");
        nonEditableSelect.setValue("正社員");
        nonEditableSelect.setWidth("300px");
        nonEditableSelect.setHelperText("定義済みの雇用区分から選択してください");

        layout.add(editableComboBox, nonEditableSelect);
        return layout;
    }

    /**
     * ラジオボタンセクション（JRadioButton + ButtonGroup の置き換え例）
     */
    private VerticalLayout createRadioButtonSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("5. 勤務形態 (RadioButtonGroup)"));

        radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel("勤務形態");
        radioGroup.setItems("出社中心", "ハイブリッド", "フルリモート");
        radioGroup.setValue("ハイブリッド");

        // 選択変更イベント
        radioGroup.addValueChangeListener(event -> {
            Notification.show("勤務形態: " + event.getValue());
        });

        layout.add(radioGroup);
        return layout;
    }

    /**
     * チェックボックスセクション（JCheckBox の置き換え例）
     */
    private VerticalLayout createCheckboxSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("6. ポリシー確認 (Checkbox)"));

        agreeCheckbox = new Checkbox("個人情報保護方針に同意する");
        agreeCheckbox.setValue(false);

        notifyCheckbox = new Checkbox("評価更新時にメール通知を受け取る");
        notifyCheckbox.setValue(true);

        // チェック変更イベント
        agreeCheckbox.addValueChangeListener(event -> {
            saveButton.setEnabled(event.getValue());
        });

        layout.add(agreeCheckbox, notifyCheckbox);
        return layout;
    }

    /**
     * ボタンセクション（JButton の置き換え例）
     */
    private HorizontalLayout createButtonSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.add(new H2("7. 登録アクション (Button)"));

        // 保存ボタン
        saveButton = new Button("プロフィールを保存");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);  // 初期状態では無効（利用規約未同意）

        saveButton.addClickListener(event -> {
            // データの保存処理
            String name = nameField.getValue();
            String email = emailField.getValue();
            // 参考: office と workStyle は入力制御などに使用可能
            // String office = editableComboBox.getValue();
            String status = nonEditableSelect.getValue();
            // String workStyle = radioGroup.getValue();

            long nextId = dataProvider.getItems().size() + 1001L;
            // 新しいデータを追加
            EmployeeProfile newData = new EmployeeProfile(
                    nextId,
                    name,
                    email,
                    status,
                    ageSpinner.getValue()
            );
            dataProvider.getItems().add(newData);
            dataProvider.refreshAll();

            // 成功メッセージ
            Notification notification = Notification.show(
                    "社員プロフィールを保存しました: " + name,
                    3000,
                    Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        // クリアボタン
        clearButton = new Button("入力をリセット");
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        clearButton.addClickListener(event -> {
            nameField.clear();
            emailField.clear();
            descriptionArea.clear();
            ageSpinner.setValue(5);
            editableComboBox.clear();
            nonEditableSelect.setValue("正社員");
            radioGroup.setValue("ハイブリッド");
            agreeCheckbox.setValue(false);
            notifyCheckbox.setValue(true);

            Notification.show("入力内容をリセットしました");
        });

        layout.add(saveButton, clearButton);
        return layout;
    }

    /**
     * グリッドセクション（JTable の置き換え例）
     */
    private VerticalLayout createGridSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("8. 社員一覧 (Grid)"));

        // Gridの作成（JTable 相当）
        dataGrid = new Grid<>(EmployeeProfile.class, false);
        dataGrid.setHeight("220px");

        // カラムの定義
        dataGrid.addColumn(EmployeeProfile::getEmployeeNumber).setHeader("社員番号").setWidth("100px");
        dataGrid.addColumn(EmployeeProfile::getFullName).setHeader("氏名").setWidth("160px");
        dataGrid.addColumn(EmployeeProfile::getEmail).setHeader("メール").setWidth("220px");
        dataGrid.addColumn(EmployeeProfile::getEmploymentStatus).setHeader("雇用区分").setWidth("140px");
        dataGrid.addColumn(EmployeeProfile::getYearsOfService).setHeader("勤続年数").setWidth("120px");

        // データの設定
        dataGrid.setItems(dataProvider);

        // 行選択イベント
        dataGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(data -> {
                Notification.show("選択: " + data.getFullName());
            });
        });

        layout.add(dataGrid);
        return layout;
    }

    /**
     * サンプルデータの作成
     */
    private List<EmployeeProfile> createSampleData() {
        List<EmployeeProfile> list = new ArrayList<>();
        list.add(new EmployeeProfile(1001L, "山田 太郎", "taro.yamada@company.jp", "正社員", 7));
        list.add(new EmployeeProfile(1002L, "鈴木 花子", "hanako.suzuki@company.jp", "正社員", 3));
        list.add(new EmployeeProfile(1003L, "田中 一郎", "ichiro.tanaka@company.jp", "契約社員", 2));
        list.add(new EmployeeProfile(1004L, "佐藤 美咲", "misaki.sato@company.jp", "派遣社員", 4));
        return list;
    }

    /**
     * グリッドセクション（JTable の置き換え例）
     */
    private VerticalLayout createRangeInput() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("app-content-subsection");
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidthFull();
        layout.add(new H2("9. 評価スコア調整 (RangeInput)"));

        rangeInput = new RangeInput();
        rangeInput.setMin(0);
        rangeInput.setMax(100);
        rangeInput.setStep(1.0);
        rangeInput.setValue(50.0);

        // 数値を表示するためのコンポーネントを作成
        Span valueDisplay = new Span("評価スコア: " + rangeInput.getValue());
        
        // スライダーの値が変更されたときのリスナーを設定
        rangeInput.addValueChangeListener(event -> {
            // イベントから新しい値を取得
            Double newValue = event.getValue();
            
            // 表示用のテキストを更新
            valueDisplay.setText("評価スコア: " + newValue);
        });

        layout.add(rangeInput, valueDisplay);
        return layout;
    }
}
