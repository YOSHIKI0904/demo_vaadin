# Vaadin 24 移行サンプルプロジェクト

このプロジェクトは、Java アプレット（Swing/AWT）で実装されていた業務画面を Vaadin 24 + Spring Boot で再現するためのサンプルです。サンプル機能を **UI コンポーネント、ナビゲーション、カレンダー操作、セッション連携フォーム** の4テーマに絞り、移行時の実装イメージを把握できるようにしました。

## 目次

- [概要](#概要)
- [プロジェクト構成](#プロジェクト構成)
- [セットアップ](#セットアップ)
- [実行方法](#実行方法)
- [機能一覧](#機能一覧)
- [主要な移行ポイント](#主要な移行ポイント)
- [注意事項](#注意事項)
- [トラブルシューティング](#トラブルシューティング)

## 概要

IE サポート終了に伴い、Applets/Swing 画面を HTML5 ベースの Web アプリに移行する際の指針を示します。全画面に同一のナビゲーションバーを配置し、シンプルな画面遷移で各サンプルを確認できます。

### 技術スタック

- **Vaadin 24.3.0** – サーバーサイド UI フレームワーク
- **Spring Boot 3.2.0** – DI / 起動管理
- **Java 17**
- **Maven**

## プロジェクト構成

```
Vaadin/
├── pom.xml
├── README.md
├── SWING_TO_VAADIN_MAPPING.md
└── src/main/java/com/example/vaadin
    ├── Application.java
    ├── model
    │   ├── ApplicationDraft.java
    │   └── SampleData.java
    ├── session
    │   └── UserSession.java
    └── views
        ├── ApplicationFormView.java
        ├── ApplicationSummaryView.java
        ├── ButtonCalendarView.java
        ├── ComponentsView.java
        └── SampleNavigationBar.java
```

## セットアップ

### 必要な環境

- Java 17 以上
- Maven 3.6 以上

### 依存関係のインストール

```bash
mvn clean install
```

## 実行方法

### アプリケーションの起動

```bash
mvn spring-boot:run
```

または

```bash
mvn clean package
java -jar target/vaadin-migration-example-1.0-SNAPSHOT.jar
```

### サンプル画面

- **UI コンポーネント**: http://localhost:8080/
- **カレンダー操作サンプル**: http://localhost:8080/calendar-buttons
- **申請フォーム**: http://localhost:8080/application/form
- **申請確認**: http://localhost:8080/application/summary

（各画面上部のナビゲーションバーからも遷移可能です。）

## 機能一覧

### 1. UI コンポーネントサンプル（`ComponentsView`）

Swing/AWT のコンポーネントを Vaadin へ置き換える例をまとめたトップ画面です。フォーム制御、通知、Grid を使った一覧など、移行時に頻出する UI 部品を確認できます。

| Swing/AWT                  | Vaadin               | 補足                           |
|---------------------------|----------------------|--------------------------------|
| `JTextField`              | `TextField`          | プレースホルダー・バリデーション |
| `JComboBox (editable)`    | `ComboBox`           | カスタム値入力                 |
| `JComboBox (非編集)`      | `Select`             | 固定リスト選択                 |
| `JRadioButton+ButtonGroup`| `RadioButtonGroup`   | 値変更イベント                 |
| `JCheckBox`               | `Checkbox`           | 状態に応じたボタン制御         |
| `JButton`                 | `Button`             | クリックイベント               |
| `JTable`                  | `Grid<SampleData>`   | 行選択・DataProvider           |

### 2. ナビゲーションサンプル（`SampleNavigationBar`）

全画面で共通利用しているシンプルなナビゲーションバーです。`RouterLink` を `HorizontalLayout` に並べ、現在のビューを変えずに簡易なメニューバーを構築する方法を示しています。アプレット時代のメニューバーやタブを Vaadin に移行する際の最低限の雰囲気を再現します。

### 3. カレンダー & ボタン操作サンプル（`ButtonCalendarView`）

左上に 2 つのボタンベースカレンダー、右側に操作ボタン群をグリッド表示した固定レイアウト例です。`Button` コンポーネントと CSS グリッドを組み合わせ、日付選択や操作パネルを柔軟に構築するパターンを示します。

### 4. 入力フォーム & セッション跨ぎ表示（`ApplicationFormView` → `ApplicationSummaryView`）

申請フォームで入力した内容を `@VaadinSessionScope` な `UserSession` に保存し、別画面で復元して確認します。アプレットの `static` 変数を使った状態管理を Vaadin のセッションに置き換えるためのミニサンプルです。

フロー:

1. `ApplicationFormView` で項目を入力し「一時保存」
2. `UserSession` に `ApplicationDraft` を保持
3. `ApplicationSummaryView` で保存済み値・セッションIDを表示

## 主要な移行ポイント

### 1. アーキテクチャ差分

| 項目 | Java アプレット | Vaadin 24 |
|------|-----------------|-----------|
| 実行場所 | クライアント JVM | サーバー（ブラウザはHTML表示） |
| 通信 | ほぼ無し | HTTP/WebSocket |
| セッション | `static` 変数で共有 | `VaadinSession` / `@VaadinSessionScope` |

### 2. コンポーネント置き換え

`SWING_TO_VAADIN_MAPPING.md` に詳細な対応表を掲載しています。Swing ライブラリ内の代表的な UI はほぼ Vaadin の標準コンポーネントで置き換え可能です。

### 3. イベント処理

匿名クラスで `ActionListener` を実装していたコードは、ラムダ式で `addClickListener` や `addValueChangeListener` を設定する形に移行します。サーバーラウンドトリップが発生する点に注意してください。

### 4. セッション共有

`UserSession` のような `@VaadinSessionScope` Bean に状態を集約すると、同一ブラウザタブ間でデータを共有しつつ、ユーザーごとに完全分離できます。非同期処理から UI 更新を行う際は `UI.access()` を使用してください。

## 注意事項

- 本サンプルはディスプレイ解像度 1920×1080（OS・ブラウザとも表示倍率 100%）での表示を前提としており、レスポンシブ対応は行っていません。
- すべての UI 更新はサーバーラウンドトリップになるため、入力検証や長時間処理ではユーザー通知（`Notification` 等）を活用してください。
- セッションに保存するオブジェクトは `Serializable` を実装し、肥大化を避けるために必要最小限のデータだけを保持します。

## トラブルシューティング

### ポート 8080 が使用中の場合

`src/main/resources/application.properties` に以下を追加してポートを変更できます。

```properties
server.port=8081
```

### 依存関係の不整合が発生する場合

```bash
mvn clean install -U
```

でローカルキャッシュを更新してください。

## 参考資料

- [Vaadin 公式ドキュメント](https://vaadin.com/docs/latest/)
- [Spring Boot 公式ドキュメント](https://spring.io/projects/spring-boot)

## ライセンス

このサンプルプロジェクトは MIT ライセンスのもとで公開されています。

## サポート

質問や問題がある場合は Issue を作成してください。
