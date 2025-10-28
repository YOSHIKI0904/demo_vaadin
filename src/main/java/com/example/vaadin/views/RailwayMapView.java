package com.example.vaadin.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路線図と補助デモをまとめた単純なビュー。
 * Java側で座標データを保持し、JavaScriptにそのまま渡す構成。
 */
@Route("railway-map")
@PageTitle("路線図ビュー")
@JsModule("./src/railway-map.ts")
public class RailwayMapView extends VerticalLayout {

    private static final String MAP_CONTAINER_ID = "railway-map-container";
    private static final String CHECKBOX_CONTAINER_ID = "railway-checkbox-container";

    private static final List<RailwayNode> NODES = List.of(
        new RailwayNode("st-01", "北口駅", NodeType.STATION, 80, 80),
        new RailwayNode("st-02", "中央駅", NodeType.STATION, 280, 140),
        new RailwayNode("cr-01", "第一踏切", NodeType.CROSSING, 430, 200),
        new RailwayNode("st-03", "南浜駅", NodeType.STATION, 580, 260),
        new RailwayNode("st-04", "臨港駅", NodeType.STATION, 720, 320)
    );

    private static final List<RailwayLink> LINKS = List.of(
        new RailwayLink("st-01", "st-02"),
        new RailwayLink("st-02", "cr-01"),
        new RailwayLink("cr-01", "st-03"),
        new RailwayLink("st-03", "st-04")
    );

    private static final JsonObject RAILWAY_DATA = buildRailwayData();
    private static final JsonArray CHECKBOX_ITEMS = buildCheckboxItems();

    private final Div mapContainer = new Div();
    private final Div checkboxContainer = new Div();
    private final Map<String, String> stationNotes = new ConcurrentHashMap<>();

    private final Dialog stationDialog = new Dialog();
    private final Dialog sectionDialog = new Dialog();

    private final Span stationName = new Span();
    private final TextArea noteField = new TextArea("運行メモ");
    private final Select<RailwayNode> startSelect = new Select<>();
    private final Select<RailwayNode> endSelect = new Select<>();

    private String activeStationId;

    public RailwayMapView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        addClassName("app-view");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setMargin(false);
        content.setWidthFull();
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        content.getStyle().set("gap", "16px");

        content.add(new H1("SVG路線図デモ"));
        content.add(new H2("駅クリックで詳細設定、線クリックでハイライト"));

        Div description = new Div(
            new Span("駅・踏切の座標と線路の接続はJavaで定義し、"),
            new Span("描画はJavaScript（railway-map.ts）が担当します。"),
            new Span("ダイアログや通知はすべてVaadin側で扱うシンプルな構成です。")
        );
        description.addClassName("app-content-subsection");
        description.getStyle().set("display", "flex").set("flexDirection", "column").set("gap", "4px");

        setupMapContainer();
        setupCheckboxContainer();
        configureStationDialog();
        configureSectionDialog();

        Button sectionButton = new Button("区間をハイライト", event -> sectionDialog.open());
        sectionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout controls = new HorizontalLayout(sectionButton);
        controls.addClassName("app-content-subsection");
        controls.setAlignItems(FlexComponent.Alignment.CENTER);

        content.add(description, controls, mapContainer, checkboxContainer);

        Div contentWrapper = new Div(content);
        contentWrapper.addClassName("app-content");

        Div frame = new Div(new SampleNavigationBar(), contentWrapper);
        frame.addClassName("app-frame");

        Div shell = new Div(frame);
        shell.addClassName("app-shell");
        shell.setSizeFull();

        add(shell, stationDialog, sectionDialog);

        mapContainer.addAttachListener(event -> event.getUI().getPage()
            .executeJs("window.RailwayMap.render($0, $1)", MAP_CONTAINER_ID, RAILWAY_DATA));

        checkboxContainer.addAttachListener(event -> event.getUI().getPage()
            .executeJs("window.RailwayMap.renderCheckboxDemo($0, $1)", CHECKBOX_CONTAINER_ID, CHECKBOX_ITEMS));
    }

    private void setupMapContainer() {
        mapContainer.setId(MAP_CONTAINER_ID);
        mapContainer.getStyle()
            .set("width", "100%")
            .set("height", "480px")
            .set("border-radius", "16px")
            .set("background", "var(--lumo-base-color)")
            .set("box-shadow", "0 6px 18px rgba(15, 23, 42, 0.1)")
            .set("overflow", "hidden")
            .set("position", "relative");

        mapContainer.getElement()
            .addEventListener("map-node-selected", event -> handleNodeSelection(
                event.getEventData().getString("event.detail.id"),
                event.getEventData().getString("event.detail.type"),
                event.getEventData().getString("event.detail.name")
            ))
            .addEventData("event.detail.id")
            .addEventData("event.detail.type")
            .addEventData("event.detail.name");
    }

    private void setupCheckboxContainer() {
        checkboxContainer.setId(CHECKBOX_CONTAINER_ID);
        checkboxContainer.addClassName("app-content-subsection");
        checkboxContainer.getStyle()
            .set("width", "100%")
            .set("height", "220px")
            .set("border-radius", "16px")
            .set("background", "var(--lumo-base-color)")
            .set("box-shadow", "0 6px 18px rgba(15, 23, 42, 0.1)")
            .set("overflow", "hidden")
            .set("position", "relative");

        Span title = new Span("SVG内チェックボックスサンプル");
        title.getStyle()
            .set("position", "absolute")
            .set("top", "16px")
            .set("left", "20px")
            .set("font-weight", "600")
            .set("color", "var(--lumo-secondary-text-color)");
        checkboxContainer.add(title);

        checkboxContainer.getElement()
            .addEventListener("checkbox-selection-changed", event -> {
                String label = event.getEventData().getString("event.detail.label");
                boolean checked = event.getEventData().getBoolean("event.detail.checked");
                Notification.show(label + " を" + (checked ? "選択" : "解除") + "しました。", 2500, Notification.Position.MIDDLE);
            })
            .addEventData("event.detail.label")
            .addEventData("event.detail.checked");
    }

    private void configureStationDialog() {
        stationDialog.setHeaderTitle("駅の追加設定");
        stationDialog.setModal(true);

        stationName.getStyle().set("font-weight", "600");

        noteField.setWidthFull();
        noteField.setMaxLength(200);
        noteField.setPlaceholder("例: 工事中のため片側運行");
        noteField.setHeight("160px");

        FormLayout form = new FormLayout();
        form.addFormItem(stationName, "駅名");
        form.addFormItem(noteField, "メモ");

        Button saveButton = new Button("保存", event -> saveStationSettings());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("閉じる", event -> stationDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        stationDialog.add(form);
        stationDialog.getFooter().add(cancelButton, saveButton);
    }

    private void configureSectionDialog() {
        sectionDialog.setHeaderTitle("区間ハイライト設定");
        sectionDialog.setModal(true);

        startSelect.setLabel("開始地点");
        startSelect.setItems(NODES);
        startSelect.setItemLabelGenerator(RailwayNode::name);
        startSelect.setValue(NODES.get(0));

        endSelect.setLabel("終了地点");
        endSelect.setItems(NODES);
        endSelect.setItemLabelGenerator(RailwayNode::name);
        endSelect.setValue(NODES.get(NODES.size() - 1));

        FormLayout form = new FormLayout(startSelect, endSelect);

        Button highlightButton = new Button("ハイライト", event -> applySectionHighlight());
        highlightButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button closeButton = new Button("閉じる", event -> sectionDialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        sectionDialog.add(form);
        sectionDialog.getFooter().add(closeButton, highlightButton);
    }

    private void handleNodeSelection(String id, String type, String name) {
        if ("station".equals(type)) {
            openStationDialog(id, name);
        } else {
            Notification.show(name + " を選択しました。", 2000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private void openStationDialog(String stationId, String displayName) {
        activeStationId = stationId;
        stationName.setText(displayName);

        String note = stationNotes.get(stationId);
        if (note == null) {
            noteField.clear();
        } else {
            noteField.setValue(note);
        }

        stationDialog.open();
    }

    private void saveStationSettings() {
        if (activeStationId == null) {
            stationDialog.close();
            return;
        }

        stationNotes.put(activeStationId, noteField.getValue());

        stationDialog.close();

        stationDialog.getUI().ifPresent(ui -> ui.getPage()
            .executeJs("window.RailwayMap.highlightNode($0, $1)", MAP_CONTAINER_ID, activeStationId));
    }

    private void applySectionHighlight() {
        RailwayNode start = startSelect.getValue();
        RailwayNode end = endSelect.getValue();
        if (start == null || end == null) {
            Notification.show("開始地点と終了地点を選択してください。", 2500, Notification.Position.BOTTOM_CENTER);
            return;
        }
        if (start.id().equals(end.id())) {
            Notification.show("同じ地点は選べません。", 2500, Notification.Position.BOTTOM_CENTER);
            return;
        }

        sectionDialog.getUI().ifPresent(ui -> ui.getPage()
            .executeJs("window.RailwayMap.highlightSection($0, $1, $2)", MAP_CONTAINER_ID, start.id(), end.id()));

        sectionDialog.close();
    }

    private static JsonObject buildRailwayData() {
        JsonObject root = Json.createObject();
        root.put("width", 840);
        root.put("height", 380);

        JsonArray nodesArray = Json.createArray();
        for (int i = 0; i < NODES.size(); i++) {
            RailwayNode node = NODES.get(i);
            JsonObject obj = Json.createObject();
            obj.put("id", node.id());
            obj.put("name", node.name());
            obj.put("type", node.type().serialized);
            obj.put("x", node.x());
            obj.put("y", node.y());
            nodesArray.set(i, obj);
        }
        root.put("nodes", nodesArray);

        JsonArray linksArray = Json.createArray();
        for (int i = 0; i < LINKS.size(); i++) {
            RailwayLink link = LINKS.get(i);
            JsonObject obj = Json.createObject();
            obj.put("from", link.from());
            obj.put("to", link.to());
            linksArray.set(i, obj);
        }
        root.put("links", linksArray);

        return root;
    }

    private static JsonArray buildCheckboxItems() {
        JsonArray array = Json.createArray();
        setCheckbox(array, 0, "chk-security", "保守点検完了", 220, 140);
        setCheckbox(array, 1, "chk-snow", "除雪パトロール", 480, 140);
        return array;
    }

    private static void setCheckbox(JsonArray array, int index, String id, String label, int x, int y) {
        JsonObject json = Json.createObject();
        json.put("id", id);
        json.put("label", label);
        json.put("x", x);
        json.put("y", y);
        array.set(index, json);
    }

    private enum NodeType {
        STATION("station"),
        CROSSING("crossing");

        private final String serialized;

        NodeType(String serialized) {
            this.serialized = serialized;
        }
    }

    private record RailwayNode(String id, String name, NodeType type, int x, int y) implements Serializable {}

    private record RailwayLink(String from, String to) implements Serializable {}
}
