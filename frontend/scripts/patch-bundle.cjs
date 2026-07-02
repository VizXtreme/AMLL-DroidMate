/**
 * 构建后脚本：修补 amll.bundle.js
 *
 * 优化：缓存 clientHeight，消除每帧强制布局重算（layout thrashing）。
 * renderStyles 和 isInSight 中读取了 bgWrapper.clientHeight /
 * element.clientHeight，但此时已有待处理的样式写入（transform/opacity/filter），
 * 导致浏览器每帧同步 layout 一次。
 *
 * 修补方式：缓存 clientHeight，仅在 DOM 内容变更时（show/rebuildAllLines）
 * 失效后重新读取。
 */
const fs = require("fs");
const path = require("path");

const bundlePath = path.resolve(__dirname, "..", "dist", "amll.bundle.js");

if (!fs.existsSync(bundlePath)) {
  console.warn("⚠️  amll.bundle.js 不存在，跳过修补");
  process.exit(0);
}

let code = fs.readFileSync(bundlePath, "utf8");
let modified = false;

const patches = [
  // ── 1. LyricLineGroupBase 构造函数 → 添加缓存字段 ──
  {
    from: '__publicField(this, "isBgFirst", false);',
    to: [
      '__publicField(this, "isBgFirst", false);',
      '    __publicField(this, "_cachedBgH");',
      '    __publicField(this, "_cachedElH");',
    ].join("\n"),
  },

  // ── 2. isInSight → 缓存 element.clientHeight ──
  {
    from:
      'if (h2 === void 0 || h2 === 0) h2 = this.element.clientHeight || 0;',
    to: [
      "if (h2 === void 0 || h2 === 0) {",
      "  if (this._cachedElH === void 0)",
      "    this._cachedElH = this.element.clientHeight || 0;",
      "  h2 = this._cachedElH || 0;",
      "}",
    ].join("\n"),
  },

  // ── 3. renderStyles → bgWrapper.clientHeight 缓存 ──
  {
    from:
      "const currentMarginTop = -(this.bgWrapper.clientHeight || 0) * (1 - activeProgress);",
    to: [
      "if (this._cachedBgH === void 0)",
      "  this._cachedBgH = this.bgWrapper.clientHeight || 0;",
      "const cmt = -(this._cachedBgH || 0) * (1 - activeProgress);",
    ].join("\n"),
  },

  // ── 4. show(): DOM 挂载后失效缓存 ──
  {
    from: [
      "this.mainLine.show();",
      "    (_a2 = this.bgLine) == null ? void 0 : _a2.show();",
    ].join("\n"),
    to: [
      "this.mainLine.show();",
      "    (_a2 = this.bgLine) == null ? void 0 : _a2.show();",
      "    this._cachedBgH = this._cachedElH = void 0;",
    ].join("\n"),
  },

  // ── 5. rebuildAllLines(): 歌词行重建后失效缓存 ──
  {
    from: [
      "this.mainLine.rebuildElement();",
      "    (_a2 = this.bgLine) == null ? void 0 : _a2.rebuildElement();",
    ].join("\n"),
    to: [
      "this.mainLine.rebuildElement();",
      "    (_a2 = this.bgLine) == null ? void 0 : _a2.rebuildElement();",
      "    this._cachedBgH = this._cachedElH = void 0;",
    ].join("\n"),
  },
];

for (const patch of patches) {
  if (code.includes(patch.from)) {
    code = code.replace(patch.from, patch.to);
    console.log(`  ✅ ${patch.from.slice(0, 60).replace(/\n/g, "↵")}…`);
    modified = true;
  } else {
    console.warn(`  ⚠️  未找到匹配，检查库版本:`);
    console.warn(`      ${patch.from.slice(0, 80).replace(/\n/g, "↵")}`);
  }
}

if (modified) {
  fs.writeFileSync(bundlePath, code, "utf8");
  console.log(`\n✅ amll.bundle.js 修补完成`);
} else {
  console.log(`\n⚠️  未做任何修改`);
}
