// シンプルな路線図描画モジュール。Javaから渡されたデータをそのままSVGに変換する。

type NodeType = "station" | "crossing";

interface MapNode {
  id: string;
  name: string;
  type: NodeType;
  x: number;
  y: number;
}

interface MapLink {
  from: string;
  to: string;
}

interface MapData {
  width: number;
  height: number;
  nodes: MapNode[];
  links: MapLink[];
}

interface CheckboxItem {
  id: string;
  label: string;
  x: number;
  y: number;
}

interface StoredMap {
  data: MapData;
}

const STYLE_ID = "railway-map-style";
const mapRegistry = new Map<string, StoredMap>();

const RailwayMap = {
  render(containerId: string, data: MapData) {
    const container = document.getElementById(containerId);
    if (!container) {
      return;
    }

    ensureStyle();
    mapRegistry.set(containerId, { data });

    const nodeById = new Map<string, MapNode>();
    data.nodes.forEach((node) => {
      nodeById.set(node.id, node);
    });

    container.innerHTML = "";

    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("viewBox", `0 0 ${data.width} ${data.height}`);
    svg.setAttribute("width", "100%");
    svg.setAttribute("height", "100%");

    const linesGroup = document.createElementNS("http://www.w3.org/2000/svg", "g");
    const nodesGroup = document.createElementNS("http://www.w3.org/2000/svg", "g");

    for (const link of data.links) {
      const from = nodeById.get(link.from);
      const to = nodeById.get(link.to);
      if (!from || !to) {
        continue;
      }

      const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
      line.setAttribute("x1", String(from.x));
      line.setAttribute("y1", String(from.y));
      line.setAttribute("x2", String(to.x));
      line.setAttribute("y2", String(to.y));
      line.dataset.from = from.id;
      line.dataset.to = to.id;
      line.classList.add("railway-line");
      line.tabIndex = 0;
      line.setAttribute("role", "button");

      line.addEventListener("click", () => markSingleLine(container, line));
      line.addEventListener("keydown", (event) => {
        if (event.key === "Enter" || event.key === " ") {
          event.preventDefault();
          markSingleLine(container, line);
        }
      });

      linesGroup.appendChild(line);
    }

    for (const node of data.nodes) {
      const group = document.createElementNS("http://www.w3.org/2000/svg", "g");
      group.dataset.nodeId = node.id;
      group.classList.add("railway-node");
      group.tabIndex = 0;
      group.setAttribute("role", "button");

      group.addEventListener("click", () => emitNodeSelection(container, node));
      group.addEventListener("keydown", (event) => {
        if (event.key === "Enter" || event.key === " ") {
          event.preventDefault();
          emitNodeSelection(container, node);
        }
      });

      if (node.type === "station") {
        const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circle.setAttribute("cx", String(node.x));
        circle.setAttribute("cy", String(node.y));
        circle.setAttribute("r", "18");
        circle.classList.add("railway-node-icon", "station");
        group.appendChild(circle);
      } else {
        const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
        rect.setAttribute("x", String(node.x - 16));
        rect.setAttribute("y", String(node.y - 16));
        rect.setAttribute("width", "32");
        rect.setAttribute("height", "32");
        rect.setAttribute("rx", "6");
        rect.classList.add("railway-node-icon", "crossing");
        group.appendChild(rect);

        const lineOne = document.createElementNS("http://www.w3.org/2000/svg", "line");
        lineOne.setAttribute("x1", String(node.x - 12));
        lineOne.setAttribute("y1", String(node.y - 12));
        lineOne.setAttribute("x2", String(node.x + 12));
        lineOne.setAttribute("y2", String(node.y + 12));
        lineOne.classList.add("railway-crossing-line");
        group.appendChild(lineOne);

        const lineTwo = document.createElementNS("http://www.w3.org/2000/svg", "line");
        lineTwo.setAttribute("x1", String(node.x + 12));
        lineTwo.setAttribute("y1", String(node.y - 12));
        lineTwo.setAttribute("x2", String(node.x - 12));
        lineTwo.setAttribute("y2", String(node.y + 12));
        lineTwo.classList.add("railway-crossing-line");
        group.appendChild(lineTwo);
      }

      const label = document.createElementNS("http://www.w3.org/2000/svg", "text");
      label.textContent = node.name;
      label.setAttribute("x", String(node.x));
      label.setAttribute("y", String(node.y + 36));
      label.classList.add("railway-node-label");

      nodesGroup.appendChild(group);
      nodesGroup.appendChild(label);
    }

    svg.appendChild(linesGroup);
    svg.appendChild(nodesGroup);
    container.appendChild(svg);
  },

  highlightNode(containerId: string, nodeId: string) {
    const container = document.getElementById(containerId);
    if (!container) {
      return;
    }
    const highlightedNodes = container.querySelectorAll<SVGGElement>("g.railway-node.configured");
    highlightedNodes.forEach((entry) => entry.classList.remove("configured"));
    const node = container.querySelector<SVGGElement>(`g.railway-node[data-node-id="${nodeId}"]`);
    if (node) {
      node.classList.add("configured");
    }
  },

  highlightSection(containerId: string, startId: string, endId: string) {
    const container = document.getElementById(containerId);
    const stored = mapRegistry.get(containerId);
    if (!container || !stored) {
      return;
    }

    const lines = Array.from(container.querySelectorAll<SVGLineElement>("line.railway-line"));
    lines.forEach((line) => line.classList.remove("highlighted"));

    const path = findPath(stored.data, startId, endId);
    if (!path) {
      return;
    }

    for (let i = 0; i < path.length - 1; i += 1) {
      const from = path[i];
      const to = path[i + 1];
      const line =
        container.querySelector<SVGLineElement>(`line.railway-line[data-from="${from}"][data-to="${to}"]`) ??
        container.querySelector<SVGLineElement>(`line.railway-line[data-from="${to}"][data-to="${from}"]`);
      if (line) {
        line.classList.add("highlighted");
      }
    }
  },

  renderCheckboxDemo(containerId: string, items: CheckboxItem[]) {
    const container = document.getElementById(containerId);
    if (!container) {
      return;
    }

    ensureStyle();

    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("viewBox", "0 0 800 240");
    svg.setAttribute("width", "100%");
    svg.setAttribute("height", "100%");
    svg.classList.add("railway-checkbox-demo");

    container.querySelector("svg")?.remove();
    container.appendChild(svg);

    for (const item of items) {
      const group = document.createElementNS("http://www.w3.org/2000/svg", "g");
      group.setAttribute("transform", `translate(${item.x}, ${item.y})`);
      group.classList.add("railway-checkbox");
      group.tabIndex = 0;
      group.setAttribute("role", "checkbox");
      group.setAttribute("aria-checked", "false");

      const box = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      box.setAttribute("x", "-18");
      box.setAttribute("y", "-18");
      box.setAttribute("width", "36");
      box.setAttribute("height", "36");
      box.setAttribute("rx", "8");
      box.classList.add("checkbox-box");

      const mark = document.createElementNS("http://www.w3.org/2000/svg", "polyline");
      mark.setAttribute("points", "-8,0 -2,8 12,-10");
      mark.classList.add("checkbox-mark");

      const label = document.createElementNS("http://www.w3.org/2000/svg", "text");
      label.textContent = item.label;
      label.setAttribute("x", "28");
      label.setAttribute("y", "6");
      label.classList.add("checkbox-label");

      const toggle = () => {
        const checked = group.getAttribute("aria-checked") === "true";
        group.setAttribute("aria-checked", String(!checked));
        group.classList.toggle("checked", !checked);
        container.dispatchEvent(
          new CustomEvent("checkbox-selection-changed", {
            detail: { id: item.id, label: item.label, checked: !checked },
            bubbles: true,
            composed: true,
          })
        );
      };

      group.addEventListener("click", toggle);
      group.addEventListener("keydown", (event) => {
        if (event.key === "Enter" || event.key === " ") {
          event.preventDefault();
          toggle();
        }
      });

      group.append(box, mark, label);
      svg.appendChild(group);
    }
  },
};

function emitNodeSelection(container: HTMLElement, node: MapNode) {
  container.dispatchEvent(
    new CustomEvent("map-node-selected", {
      detail: { id: node.id, type: node.type, name: node.name },
      bubbles: true,
      composed: true,
    })
  );
}

function markSingleLine(container: HTMLElement, line: SVGLineElement) {
  const lines = Array.from(container.querySelectorAll<SVGLineElement>("line.railway-line"));
  lines.forEach((entry) => entry.classList.remove("selected"));
  line.classList.add("selected");
}

function findPath(data: MapData, startId: string, endId: string): string[] | null {
  if (startId === endId) {
    return [startId];
  }

  const adjacent = new Map<string, Set<string>>();
  for (const node of data.nodes) {
    adjacent.set(node.id, new Set());
  }
  for (const link of data.links) {
    adjacent.get(link.from)?.add(link.to);
    adjacent.get(link.to)?.add(link.from);
  }

  const queue: string[] = [startId];
  const visited = new Set(queue);
  const previous = new Map<string, string>();

  while (queue.length > 0) {
    const current = queue.shift()!;
    if (current === endId) {
      break;
    }
    for (const next of adjacent.get(current) ?? []) {
      if (!visited.has(next)) {
        visited.add(next);
        previous.set(next, current);
        queue.push(next);
      }
    }
  }

  if (!visited.has(endId)) {
    return null;
  }

  const path = [endId];
  let current = endId;
  while (current !== startId) {
    const prev = previous.get(current);
    if (!prev) {
      return null;
    }
    path.unshift(prev);
    current = prev;
  }

  return path;
}

function ensureStyle() {
  if (document.getElementById(STYLE_ID)) {
    return;
  }

  const style = document.createElement("style");
  style.id = STYLE_ID;
  style.textContent = `
    .railway-line {
      stroke: #1e40af;
      stroke-width: 6;
      stroke-linecap: round;
    }

    .railway-line.highlighted {
      stroke: #22c55e;
      stroke-width: 8;
    }

    .railway-line.selected {
      stroke: #fb923c;
      stroke-width: 8;
    }

    .railway-node {
      cursor: pointer;
    }

    .railway-node-icon.station {
      fill: #0f172a;
      stroke: #e2e8f0;
      stroke-width: 2;
    }

    .railway-node-icon.crossing {
      fill: #f97316;
      stroke: #fff;
      stroke-width: 2;
    }

    .railway-crossing-line {
      stroke: #fff;
      stroke-width: 4;
    }

    .railway-node.configured .railway-node-icon {
      fill: #d946ef;
      stroke: #fdf2f8;
    }

    .railway-node-label {
      font-family: "Inter", "Noto Sans JP", sans-serif;
      font-size: 16px;
      fill: #1f2937;
      text-anchor: middle;
      pointer-events: none;
    }

    .railway-checkbox-demo {
      background: #f8fafc;
    }

    .railway-checkbox {
      cursor: pointer;
    }

    .checkbox-box {
      fill: #fff;
      stroke: #1d4ed8;
      stroke-width: 2;
    }

    .checkbox-mark {
      fill: none;
      stroke: #1d4ed8;
      stroke-width: 3;
      stroke-linecap: round;
      stroke-linejoin: round;
      opacity: 0;
    }

    .checkbox-label {
      font-family: "Inter", "Noto Sans JP", sans-serif;
      font-size: 16px;
      fill: #111827;
    }

    .railway-checkbox.checked .checkbox-box {
      fill: #1d4ed8;
      stroke: #1d4ed8;
    }

    .railway-checkbox.checked .checkbox-mark {
      opacity: 1;
      stroke: #fff;
    }
  `;
  document.head.appendChild(style);
}

(window as any).RailwayMap = RailwayMap;

export {};
