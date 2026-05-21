(function() {
  "use strict";
  var ENV = /* @__PURE__ */ ((ENV2) => (ENV2[ENV2.WEBGL_LEGACY = 0] = "WEBGL_LEGACY", ENV2[ENV2.WEBGL = 1] = "WEBGL", ENV2[ENV2.WEBGL2 = 2] = "WEBGL2", ENV2))(ENV || {}), RENDERER_TYPE = /* @__PURE__ */ ((RENDERER_TYPE2) => (RENDERER_TYPE2[RENDERER_TYPE2.UNKNOWN = 0] = "UNKNOWN", RENDERER_TYPE2[RENDERER_TYPE2.WEBGL = 1] = "WEBGL", RENDERER_TYPE2[RENDERER_TYPE2.CANVAS = 2] = "CANVAS", RENDERER_TYPE2))(RENDERER_TYPE || {}), BUFFER_BITS = /* @__PURE__ */ ((BUFFER_BITS2) => (BUFFER_BITS2[BUFFER_BITS2.COLOR = 16384] = "COLOR", BUFFER_BITS2[BUFFER_BITS2.DEPTH = 256] = "DEPTH", BUFFER_BITS2[BUFFER_BITS2.STENCIL = 1024] = "STENCIL", BUFFER_BITS2))(BUFFER_BITS || {}), BLEND_MODES = /* @__PURE__ */ ((BLEND_MODES2) => (BLEND_MODES2[BLEND_MODES2.NORMAL = 0] = "NORMAL", BLEND_MODES2[BLEND_MODES2.ADD = 1] = "ADD", BLEND_MODES2[BLEND_MODES2.MULTIPLY = 2] = "MULTIPLY", BLEND_MODES2[BLEND_MODES2.SCREEN = 3] = "SCREEN", BLEND_MODES2[BLEND_MODES2.OVERLAY = 4] = "OVERLAY", BLEND_MODES2[BLEND_MODES2.DARKEN = 5] = "DARKEN", BLEND_MODES2[BLEND_MODES2.LIGHTEN = 6] = "LIGHTEN", BLEND_MODES2[BLEND_MODES2.COLOR_DODGE = 7] = "COLOR_DODGE", BLEND_MODES2[BLEND_MODES2.COLOR_BURN = 8] = "COLOR_BURN", BLEND_MODES2[BLEND_MODES2.HARD_LIGHT = 9] = "HARD_LIGHT", BLEND_MODES2[BLEND_MODES2.SOFT_LIGHT = 10] = "SOFT_LIGHT", BLEND_MODES2[BLEND_MODES2.DIFFERENCE = 11] = "DIFFERENCE", BLEND_MODES2[BLEND_MODES2.EXCLUSION = 12] = "EXCLUSION", BLEND_MODES2[BLEND_MODES2.HUE = 13] = "HUE", BLEND_MODES2[BLEND_MODES2.SATURATION = 14] = "SATURATION", BLEND_MODES2[BLEND_MODES2.COLOR = 15] = "COLOR", BLEND_MODES2[BLEND_MODES2.LUMINOSITY = 16] = "LUMINOSITY", BLEND_MODES2[BLEND_MODES2.NORMAL_NPM = 17] = "NORMAL_NPM", BLEND_MODES2[BLEND_MODES2.ADD_NPM = 18] = "ADD_NPM", BLEND_MODES2[BLEND_MODES2.SCREEN_NPM = 19] = "SCREEN_NPM", BLEND_MODES2[BLEND_MODES2.NONE = 20] = "NONE", BLEND_MODES2[BLEND_MODES2.SRC_OVER = 0] = "SRC_OVER", BLEND_MODES2[BLEND_MODES2.SRC_IN = 21] = "SRC_IN", BLEND_MODES2[BLEND_MODES2.SRC_OUT = 22] = "SRC_OUT", BLEND_MODES2[BLEND_MODES2.SRC_ATOP = 23] = "SRC_ATOP", BLEND_MODES2[BLEND_MODES2.DST_OVER = 24] = "DST_OVER", BLEND_MODES2[BLEND_MODES2.DST_IN = 25] = "DST_IN", BLEND_MODES2[BLEND_MODES2.DST_OUT = 26] = "DST_OUT", BLEND_MODES2[BLEND_MODES2.DST_ATOP = 27] = "DST_ATOP", BLEND_MODES2[BLEND_MODES2.ERASE = 26] = "ERASE", BLEND_MODES2[BLEND_MODES2.SUBTRACT = 28] = "SUBTRACT", BLEND_MODES2[BLEND_MODES2.XOR = 29] = "XOR", BLEND_MODES2))(BLEND_MODES || {}), DRAW_MODES = /* @__PURE__ */ ((DRAW_MODES2) => (DRAW_MODES2[DRAW_MODES2.POINTS = 0] = "POINTS", DRAW_MODES2[DRAW_MODES2.LINES = 1] = "LINES", DRAW_MODES2[DRAW_MODES2.LINE_LOOP = 2] = "LINE_LOOP", DRAW_MODES2[DRAW_MODES2.LINE_STRIP = 3] = "LINE_STRIP", DRAW_MODES2[DRAW_MODES2.TRIANGLES = 4] = "TRIANGLES", DRAW_MODES2[DRAW_MODES2.TRIANGLE_STRIP = 5] = "TRIANGLE_STRIP", DRAW_MODES2[DRAW_MODES2.TRIANGLE_FAN = 6] = "TRIANGLE_FAN", DRAW_MODES2))(DRAW_MODES || {}), FORMATS = /* @__PURE__ */ ((FORMATS2) => (FORMATS2[FORMATS2.RGBA = 6408] = "RGBA", FORMATS2[FORMATS2.RGB = 6407] = "RGB", FORMATS2[FORMATS2.RG = 33319] = "RG", FORMATS2[FORMATS2.RED = 6403] = "RED", FORMATS2[FORMATS2.RGBA_INTEGER = 36249] = "RGBA_INTEGER", FORMATS2[FORMATS2.RGB_INTEGER = 36248] = "RGB_INTEGER", FORMATS2[FORMATS2.RG_INTEGER = 33320] = "RG_INTEGER", FORMATS2[FORMATS2.RED_INTEGER = 36244] = "RED_INTEGER", FORMATS2[FORMATS2.ALPHA = 6406] = "ALPHA", FORMATS2[FORMATS2.LUMINANCE = 6409] = "LUMINANCE", FORMATS2[FORMATS2.LUMINANCE_ALPHA = 6410] = "LUMINANCE_ALPHA", FORMATS2[FORMATS2.DEPTH_COMPONENT = 6402] = "DEPTH_COMPONENT", FORMATS2[FORMATS2.DEPTH_STENCIL = 34041] = "DEPTH_STENCIL", FORMATS2))(FORMATS || {}), TARGETS = /* @__PURE__ */ ((TARGETS2) => (TARGETS2[TARGETS2.TEXTURE_2D = 3553] = "TEXTURE_2D", TARGETS2[TARGETS2.TEXTURE_CUBE_MAP = 34067] = "TEXTURE_CUBE_MAP", TARGETS2[TARGETS2.TEXTURE_2D_ARRAY = 35866] = "TEXTURE_2D_ARRAY", TARGETS2[TARGETS2.TEXTURE_CUBE_MAP_POSITIVE_X = 34069] = "TEXTURE_CUBE_MAP_POSITIVE_X", TARGETS2[TARGETS2.TEXTURE_CUBE_MAP_NEGATIVE_X = 34070] = "TEXTURE_CUBE_MAP_NEGATIVE_X", TARGETS2[TARGETS2.TEXTURE_CUBE_MAP_POSITIVE_Y = 34071] = "TEXTURE_CUBE_MAP_POSITIVE_Y", TARGETS2[TARGETS2.TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072] = "TEXTURE_CUBE_MAP_NEGATIVE_Y", TARGETS2[TARGETS2.TEXTURE_CUBE_MAP_POSITIVE_Z = 34073] = "TEXTURE_CUBE_MAP_POSITIVE_Z", TARGETS2[TARGETS2.TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074] = "TEXTURE_CUBE_MAP_NEGATIVE_Z", TARGETS2))(TARGETS || {}), TYPES = /* @__PURE__ */ ((TYPES2) => (TYPES2[TYPES2.UNSIGNED_BYTE = 5121] = "UNSIGNED_BYTE", TYPES2[TYPES2.UNSIGNED_SHORT = 5123] = "UNSIGNED_SHORT", TYPES2[TYPES2.UNSIGNED_SHORT_5_6_5 = 33635] = "UNSIGNED_SHORT_5_6_5", TYPES2[TYPES2.UNSIGNED_SHORT_4_4_4_4 = 32819] = "UNSIGNED_SHORT_4_4_4_4", TYPES2[TYPES2.UNSIGNED_SHORT_5_5_5_1 = 32820] = "UNSIGNED_SHORT_5_5_5_1", TYPES2[TYPES2.UNSIGNED_INT = 5125] = "UNSIGNED_INT", TYPES2[TYPES2.UNSIGNED_INT_10F_11F_11F_REV = 35899] = "UNSIGNED_INT_10F_11F_11F_REV", TYPES2[TYPES2.UNSIGNED_INT_2_10_10_10_REV = 33640] = "UNSIGNED_INT_2_10_10_10_REV", TYPES2[TYPES2.UNSIGNED_INT_24_8 = 34042] = "UNSIGNED_INT_24_8", TYPES2[TYPES2.UNSIGNED_INT_5_9_9_9_REV = 35902] = "UNSIGNED_INT_5_9_9_9_REV", TYPES2[TYPES2.BYTE = 5120] = "BYTE", TYPES2[TYPES2.SHORT = 5122] = "SHORT", TYPES2[TYPES2.INT = 5124] = "INT", TYPES2[TYPES2.FLOAT = 5126] = "FLOAT", TYPES2[TYPES2.FLOAT_32_UNSIGNED_INT_24_8_REV = 36269] = "FLOAT_32_UNSIGNED_INT_24_8_REV", TYPES2[TYPES2.HALF_FLOAT = 36193] = "HALF_FLOAT", TYPES2))(TYPES || {}), SAMPLER_TYPES = /* @__PURE__ */ ((SAMPLER_TYPES2) => (SAMPLER_TYPES2[SAMPLER_TYPES2.FLOAT = 0] = "FLOAT", SAMPLER_TYPES2[SAMPLER_TYPES2.INT = 1] = "INT", SAMPLER_TYPES2[SAMPLER_TYPES2.UINT = 2] = "UINT", SAMPLER_TYPES2))(SAMPLER_TYPES || {}), SCALE_MODES = /* @__PURE__ */ ((SCALE_MODES2) => (SCALE_MODES2[SCALE_MODES2.NEAREST = 0] = "NEAREST", SCALE_MODES2[SCALE_MODES2.LINEAR = 1] = "LINEAR", SCALE_MODES2))(SCALE_MODES || {}), WRAP_MODES = /* @__PURE__ */ ((WRAP_MODES2) => (WRAP_MODES2[WRAP_MODES2.CLAMP = 33071] = "CLAMP", WRAP_MODES2[WRAP_MODES2.REPEAT = 10497] = "REPEAT", WRAP_MODES2[WRAP_MODES2.MIRRORED_REPEAT = 33648] = "MIRRORED_REPEAT", WRAP_MODES2))(WRAP_MODES || {}), MIPMAP_MODES = /* @__PURE__ */ ((MIPMAP_MODES2) => (MIPMAP_MODES2[MIPMAP_MODES2.OFF = 0] = "OFF", MIPMAP_MODES2[MIPMAP_MODES2.POW2 = 1] = "POW2", MIPMAP_MODES2[MIPMAP_MODES2.ON = 2] = "ON", MIPMAP_MODES2[MIPMAP_MODES2.ON_MANUAL = 3] = "ON_MANUAL", MIPMAP_MODES2))(MIPMAP_MODES || {}), ALPHA_MODES = /* @__PURE__ */ ((ALPHA_MODES2) => (ALPHA_MODES2[ALPHA_MODES2.NPM = 0] = "NPM", ALPHA_MODES2[ALPHA_MODES2.UNPACK = 1] = "UNPACK", ALPHA_MODES2[ALPHA_MODES2.PMA = 2] = "PMA", ALPHA_MODES2[ALPHA_MODES2.NO_PREMULTIPLIED_ALPHA = 0] = "NO_PREMULTIPLIED_ALPHA", ALPHA_MODES2[ALPHA_MODES2.PREMULTIPLY_ON_UPLOAD = 1] = "PREMULTIPLY_ON_UPLOAD", ALPHA_MODES2[ALPHA_MODES2.PREMULTIPLIED_ALPHA = 2] = "PREMULTIPLIED_ALPHA", ALPHA_MODES2))(ALPHA_MODES || {}), CLEAR_MODES = /* @__PURE__ */ ((CLEAR_MODES2) => (CLEAR_MODES2[CLEAR_MODES2.NO = 0] = "NO", CLEAR_MODES2[CLEAR_MODES2.YES = 1] = "YES", CLEAR_MODES2[CLEAR_MODES2.AUTO = 2] = "AUTO", CLEAR_MODES2[CLEAR_MODES2.BLEND = 0] = "BLEND", CLEAR_MODES2[CLEAR_MODES2.CLEAR = 1] = "CLEAR", CLEAR_MODES2[CLEAR_MODES2.BLIT = 2] = "BLIT", CLEAR_MODES2))(CLEAR_MODES || {}), GC_MODES = /* @__PURE__ */ ((GC_MODES2) => (GC_MODES2[GC_MODES2.AUTO = 0] = "AUTO", GC_MODES2[GC_MODES2.MANUAL = 1] = "MANUAL", GC_MODES2))(GC_MODES || {}), PRECISION = /* @__PURE__ */ ((PRECISION2) => (PRECISION2.LOW = "lowp", PRECISION2.MEDIUM = "mediump", PRECISION2.HIGH = "highp", PRECISION2))(PRECISION || {}), MASK_TYPES = /* @__PURE__ */ ((MASK_TYPES2) => (MASK_TYPES2[MASK_TYPES2.NONE = 0] = "NONE", MASK_TYPES2[MASK_TYPES2.SCISSOR = 1] = "SCISSOR", MASK_TYPES2[MASK_TYPES2.STENCIL = 2] = "STENCIL", MASK_TYPES2[MASK_TYPES2.SPRITE = 3] = "SPRITE", MASK_TYPES2[MASK_TYPES2.COLOR = 4] = "COLOR", MASK_TYPES2))(MASK_TYPES || {}), MSAA_QUALITY = /* @__PURE__ */ ((MSAA_QUALITY2) => (MSAA_QUALITY2[MSAA_QUALITY2.NONE = 0] = "NONE", MSAA_QUALITY2[MSAA_QUALITY2.LOW = 2] = "LOW", MSAA_QUALITY2[MSAA_QUALITY2.MEDIUM = 4] = "MEDIUM", MSAA_QUALITY2[MSAA_QUALITY2.HIGH = 8] = "HIGH", MSAA_QUALITY2))(MSAA_QUALITY || {}), BUFFER_TYPE = /* @__PURE__ */ ((BUFFER_TYPE2) => (BUFFER_TYPE2[BUFFER_TYPE2.ELEMENT_ARRAY_BUFFER = 34963] = "ELEMENT_ARRAY_BUFFER", BUFFER_TYPE2[BUFFER_TYPE2.ARRAY_BUFFER = 34962] = "ARRAY_BUFFER", BUFFER_TYPE2[BUFFER_TYPE2.UNIFORM_BUFFER = 35345] = "UNIFORM_BUFFER", BUFFER_TYPE2))(BUFFER_TYPE || {});
  const BrowserAdapter = {
    /**
     * Creates a canvas element of the given size.
     * This canvas is created using the browser's native canvas element.
     * @param width - width of the canvas
     * @param height - height of the canvas
     */
    createCanvas: (width, height) => {
      const canvas = document.createElement("canvas");
      return canvas.width = width, canvas.height = height, canvas;
    },
    getCanvasRenderingContext2D: () => CanvasRenderingContext2D,
    getWebGLRenderingContext: () => WebGLRenderingContext,
    getNavigator: () => navigator,
    getBaseUrl: () => document.baseURI ?? window.location.href,
    getFontFaceSet: () => document.fonts,
    fetch: (url2, options) => fetch(url2, options),
    parseXML: (xml) => new DOMParser().parseFromString(xml, "text/xml")
  };
  const settings = {
    /**
     * This adapter is used to call methods that are platform dependent.
     * For example `document.createElement` only runs on the web but fails in node environments.
     * This allows us to support more platforms by abstracting away specific implementations per platform.
     *
     * By default the adapter is set to work in the browser. However you can create your own
     * by implementing the `IAdapter` interface. See `IAdapter` for more information.
     * @name ADAPTER
     * @memberof PIXI.settings
     * @type {PIXI.IAdapter}
     * @default PIXI.BrowserAdapter
     */
    ADAPTER: BrowserAdapter,
    /**
     * Default resolution / device pixel ratio of the renderer.
     * @static
     * @name RESOLUTION
     * @memberof PIXI.settings
     * @type {number}
     * @default 1
     */
    RESOLUTION: 1,
    /**
     * Enables bitmap creation before image load. This feature is experimental.
     * @static
     * @name CREATE_IMAGE_BITMAP
     * @memberof PIXI.settings
     * @type {boolean}
     * @default false
     */
    CREATE_IMAGE_BITMAP: false,
    /**
     * If true PixiJS will Math.floor() x/y values when rendering, stopping pixel interpolation.
     * Advantages can include sharper image quality (like text) and faster rendering on canvas.
     * The main disadvantage is movement of objects may appear less smooth.
     * @static
     * @memberof PIXI.settings
     * @type {boolean}
     * @default false
     */
    ROUND_PIXELS: false
  };
  var appleIphone = /iPhone/i;
  var appleIpod = /iPod/i;
  var appleTablet = /iPad/i;
  var appleUniversal = /\biOS-universal(?:.+)Mac\b/i;
  var androidPhone = /\bAndroid(?:.+)Mobile\b/i;
  var androidTablet = /Android/i;
  var amazonPhone = /(?:SD4930UR|\bSilk(?:.+)Mobile\b)/i;
  var amazonTablet = /Silk/i;
  var windowsPhone = /Windows Phone/i;
  var windowsTablet = /\bWindows(?:.+)ARM\b/i;
  var otherBlackBerry = /BlackBerry/i;
  var otherBlackBerry10 = /BB10/i;
  var otherOpera = /Opera Mini/i;
  var otherChrome = /\b(CriOS|Chrome)(?:.+)Mobile/i;
  var otherFirefox = /Mobile(?:.+)Firefox\b/i;
  var isAppleTabletOnIos13 = function(navigator2) {
    return typeof navigator2 !== "undefined" && navigator2.platform === "MacIntel" && typeof navigator2.maxTouchPoints === "number" && navigator2.maxTouchPoints > 1 && typeof MSStream === "undefined";
  };
  function createMatch(userAgent) {
    return function(regex) {
      return regex.test(userAgent);
    };
  }
  function isMobile$1(param) {
    var nav = {
      userAgent: "",
      platform: "",
      maxTouchPoints: 0
    };
    if (!param && typeof navigator !== "undefined") {
      nav = {
        userAgent: navigator.userAgent,
        platform: navigator.platform,
        maxTouchPoints: navigator.maxTouchPoints || 0
      };
    } else if (typeof param === "string") {
      nav.userAgent = param;
    } else if (param && param.userAgent) {
      nav = {
        userAgent: param.userAgent,
        platform: param.platform,
        maxTouchPoints: param.maxTouchPoints || 0
      };
    }
    var userAgent = nav.userAgent;
    var tmp = userAgent.split("[FBAN");
    if (typeof tmp[1] !== "undefined") {
      userAgent = tmp[0];
    }
    tmp = userAgent.split("Twitter");
    if (typeof tmp[1] !== "undefined") {
      userAgent = tmp[0];
    }
    var match = createMatch(userAgent);
    var result = {
      apple: {
        phone: match(appleIphone) && !match(windowsPhone),
        ipod: match(appleIpod),
        tablet: !match(appleIphone) && (match(appleTablet) || isAppleTabletOnIos13(nav)) && !match(windowsPhone),
        universal: match(appleUniversal),
        device: (match(appleIphone) || match(appleIpod) || match(appleTablet) || match(appleUniversal) || isAppleTabletOnIos13(nav)) && !match(windowsPhone)
      },
      amazon: {
        phone: match(amazonPhone),
        tablet: !match(amazonPhone) && match(amazonTablet),
        device: match(amazonPhone) || match(amazonTablet)
      },
      android: {
        phone: !match(windowsPhone) && match(amazonPhone) || !match(windowsPhone) && match(androidPhone),
        tablet: !match(windowsPhone) && !match(amazonPhone) && !match(androidPhone) && (match(amazonTablet) || match(androidTablet)),
        device: !match(windowsPhone) && (match(amazonPhone) || match(amazonTablet) || match(androidPhone) || match(androidTablet)) || match(/\bokhttp\b/i)
      },
      windows: {
        phone: match(windowsPhone),
        tablet: match(windowsTablet),
        device: match(windowsPhone) || match(windowsTablet)
      },
      other: {
        blackberry: match(otherBlackBerry),
        blackberry10: match(otherBlackBerry10),
        opera: match(otherOpera),
        firefox: match(otherFirefox),
        chrome: match(otherChrome),
        device: match(otherBlackBerry) || match(otherBlackBerry10) || match(otherOpera) || match(otherFirefox) || match(otherChrome)
      },
      any: false,
      phone: false,
      tablet: false
    };
    result.any = result.apple.device || result.android.device || result.windows.device || result.other.device;
    result.phone = result.apple.phone || result.android.phone || result.windows.phone;
    result.tablet = result.apple.tablet || result.android.tablet || result.windows.tablet;
    return result;
  }
  const isMobileCall = isMobile$1.default ?? isMobile$1, isMobile = isMobileCall(globalThis.navigator);
  settings.RETINA_PREFIX = /@([0-9\.]+)x/;
  settings.FAIL_IF_MAJOR_PERFORMANCE_CAVEAT = false;
  function getDefaultExportFromCjs(x2) {
    return x2 && x2.__esModule && Object.prototype.hasOwnProperty.call(x2, "default") ? x2["default"] : x2;
  }
  function getAugmentedNamespace(n2) {
    if (Object.prototype.hasOwnProperty.call(n2, "__esModule")) return n2;
    var f2 = n2.default;
    if (typeof f2 == "function") {
      var a2 = function a22() {
        var isInstance = false;
        try {
          isInstance = this instanceof a22;
        } catch {
        }
        if (isInstance) {
          return Reflect.construct(f2, arguments, this.constructor);
        }
        return f2.apply(this, arguments);
      };
      a2.prototype = f2.prototype;
    } else a2 = {};
    Object.defineProperty(a2, "__esModule", { value: true });
    Object.keys(n2).forEach(function(k2) {
      var d2 = Object.getOwnPropertyDescriptor(n2, k2);
      Object.defineProperty(a2, k2, d2.get ? d2 : {
        enumerable: true,
        get: function() {
          return n2[k2];
        }
      });
    });
    return a2;
  }
  var eventemitter3 = { exports: {} };
  var hasRequiredEventemitter3;
  function requireEventemitter3() {
    if (hasRequiredEventemitter3) return eventemitter3.exports;
    hasRequiredEventemitter3 = 1;
    (function(module) {
      var has = Object.prototype.hasOwnProperty, prefix = "~";
      function Events() {
      }
      if (Object.create) {
        Events.prototype = /* @__PURE__ */ Object.create(null);
        if (!new Events().__proto__) prefix = false;
      }
      function EE(fn, context2, once) {
        this.fn = fn;
        this.context = context2;
        this.once = once || false;
      }
      function addListener(emitter, event, fn, context2, once) {
        if (typeof fn !== "function") {
          throw new TypeError("The listener must be a function");
        }
        var listener = new EE(fn, context2 || emitter, once), evt = prefix ? prefix + event : event;
        if (!emitter._events[evt]) emitter._events[evt] = listener, emitter._eventsCount++;
        else if (!emitter._events[evt].fn) emitter._events[evt].push(listener);
        else emitter._events[evt] = [emitter._events[evt], listener];
        return emitter;
      }
      function clearEvent(emitter, evt) {
        if (--emitter._eventsCount === 0) emitter._events = new Events();
        else delete emitter._events[evt];
      }
      function EventEmitter2() {
        this._events = new Events();
        this._eventsCount = 0;
      }
      EventEmitter2.prototype.eventNames = function eventNames() {
        var names = [], events, name;
        if (this._eventsCount === 0) return names;
        for (name in events = this._events) {
          if (has.call(events, name)) names.push(prefix ? name.slice(1) : name);
        }
        if (Object.getOwnPropertySymbols) {
          return names.concat(Object.getOwnPropertySymbols(events));
        }
        return names;
      };
      EventEmitter2.prototype.listeners = function listeners(event) {
        var evt = prefix ? prefix + event : event, handlers = this._events[evt];
        if (!handlers) return [];
        if (handlers.fn) return [handlers.fn];
        for (var i2 = 0, l2 = handlers.length, ee2 = new Array(l2); i2 < l2; i2++) {
          ee2[i2] = handlers[i2].fn;
        }
        return ee2;
      };
      EventEmitter2.prototype.listenerCount = function listenerCount(event) {
        var evt = prefix ? prefix + event : event, listeners = this._events[evt];
        if (!listeners) return 0;
        if (listeners.fn) return 1;
        return listeners.length;
      };
      EventEmitter2.prototype.emit = function emit(event, a1, a2, a3, a4, a5) {
        var evt = prefix ? prefix + event : event;
        if (!this._events[evt]) return false;
        var listeners = this._events[evt], len = arguments.length, args, i2;
        if (listeners.fn) {
          if (listeners.once) this.removeListener(event, listeners.fn, void 0, true);
          switch (len) {
            case 1:
              return listeners.fn.call(listeners.context), true;
            case 2:
              return listeners.fn.call(listeners.context, a1), true;
            case 3:
              return listeners.fn.call(listeners.context, a1, a2), true;
            case 4:
              return listeners.fn.call(listeners.context, a1, a2, a3), true;
            case 5:
              return listeners.fn.call(listeners.context, a1, a2, a3, a4), true;
            case 6:
              return listeners.fn.call(listeners.context, a1, a2, a3, a4, a5), true;
          }
          for (i2 = 1, args = new Array(len - 1); i2 < len; i2++) {
            args[i2 - 1] = arguments[i2];
          }
          listeners.fn.apply(listeners.context, args);
        } else {
          var length = listeners.length, j2;
          for (i2 = 0; i2 < length; i2++) {
            if (listeners[i2].once) this.removeListener(event, listeners[i2].fn, void 0, true);
            switch (len) {
              case 1:
                listeners[i2].fn.call(listeners[i2].context);
                break;
              case 2:
                listeners[i2].fn.call(listeners[i2].context, a1);
                break;
              case 3:
                listeners[i2].fn.call(listeners[i2].context, a1, a2);
                break;
              case 4:
                listeners[i2].fn.call(listeners[i2].context, a1, a2, a3);
                break;
              default:
                if (!args) for (j2 = 1, args = new Array(len - 1); j2 < len; j2++) {
                  args[j2 - 1] = arguments[j2];
                }
                listeners[i2].fn.apply(listeners[i2].context, args);
            }
          }
        }
        return true;
      };
      EventEmitter2.prototype.on = function on(event, fn, context2) {
        return addListener(this, event, fn, context2, false);
      };
      EventEmitter2.prototype.once = function once(event, fn, context2) {
        return addListener(this, event, fn, context2, true);
      };
      EventEmitter2.prototype.removeListener = function removeListener(event, fn, context2, once) {
        var evt = prefix ? prefix + event : event;
        if (!this._events[evt]) return this;
        if (!fn) {
          clearEvent(this, evt);
          return this;
        }
        var listeners = this._events[evt];
        if (listeners.fn) {
          if (listeners.fn === fn && (!once || listeners.once) && (!context2 || listeners.context === context2)) {
            clearEvent(this, evt);
          }
        } else {
          for (var i2 = 0, events = [], length = listeners.length; i2 < length; i2++) {
            if (listeners[i2].fn !== fn || once && !listeners[i2].once || context2 && listeners[i2].context !== context2) {
              events.push(listeners[i2]);
            }
          }
          if (events.length) this._events[evt] = events.length === 1 ? events[0] : events;
          else clearEvent(this, evt);
        }
        return this;
      };
      EventEmitter2.prototype.removeAllListeners = function removeAllListeners(event) {
        var evt;
        if (event) {
          evt = prefix ? prefix + event : event;
          if (this._events[evt]) clearEvent(this, evt);
        } else {
          this._events = new Events();
          this._eventsCount = 0;
        }
        return this;
      };
      EventEmitter2.prototype.off = EventEmitter2.prototype.removeListener;
      EventEmitter2.prototype.addListener = EventEmitter2.prototype.on;
      EventEmitter2.prefixed = prefix;
      EventEmitter2.EventEmitter = EventEmitter2;
      {
        module.exports = EventEmitter2;
      }
    })(eventemitter3);
    return eventemitter3.exports;
  }
  var eventemitter3Exports = requireEventemitter3();
  const EventEmitter = /* @__PURE__ */ getDefaultExportFromCjs(eventemitter3Exports);
  var earcut = { exports: {} };
  var hasRequiredEarcut;
  function requireEarcut() {
    if (hasRequiredEarcut) return earcut.exports;
    hasRequiredEarcut = 1;
    earcut.exports = earcut$1;
    earcut.exports.default = earcut$1;
    function earcut$1(data, holeIndices, dim) {
      dim = dim || 2;
      var hasHoles = holeIndices && holeIndices.length, outerLen = hasHoles ? holeIndices[0] * dim : data.length, outerNode = linkedList(data, 0, outerLen, dim, true), triangles = [];
      if (!outerNode || outerNode.next === outerNode.prev) return triangles;
      var minX, minY, maxX, maxY, x2, y2, invSize;
      if (hasHoles) outerNode = eliminateHoles(data, holeIndices, outerNode, dim);
      if (data.length > 80 * dim) {
        minX = maxX = data[0];
        minY = maxY = data[1];
        for (var i2 = dim; i2 < outerLen; i2 += dim) {
          x2 = data[i2];
          y2 = data[i2 + 1];
          if (x2 < minX) minX = x2;
          if (y2 < minY) minY = y2;
          if (x2 > maxX) maxX = x2;
          if (y2 > maxY) maxY = y2;
        }
        invSize = Math.max(maxX - minX, maxY - minY);
        invSize = invSize !== 0 ? 32767 / invSize : 0;
      }
      earcutLinked(outerNode, triangles, dim, minX, minY, invSize, 0);
      return triangles;
    }
    function linkedList(data, start, end, dim, clockwise) {
      var i2, last;
      if (clockwise === signedArea(data, start, end, dim) > 0) {
        for (i2 = start; i2 < end; i2 += dim) last = insertNode(i2, data[i2], data[i2 + 1], last);
      } else {
        for (i2 = end - dim; i2 >= start; i2 -= dim) last = insertNode(i2, data[i2], data[i2 + 1], last);
      }
      if (last && equals(last, last.next)) {
        removeNode(last);
        last = last.next;
      }
      return last;
    }
    function filterPoints(start, end) {
      if (!start) return start;
      if (!end) end = start;
      var p2 = start, again;
      do {
        again = false;
        if (!p2.steiner && (equals(p2, p2.next) || area(p2.prev, p2, p2.next) === 0)) {
          removeNode(p2);
          p2 = end = p2.prev;
          if (p2 === p2.next) break;
          again = true;
        } else {
          p2 = p2.next;
        }
      } while (again || p2 !== end);
      return end;
    }
    function earcutLinked(ear, triangles, dim, minX, minY, invSize, pass) {
      if (!ear) return;
      if (!pass && invSize) indexCurve(ear, minX, minY, invSize);
      var stop = ear, prev, next;
      while (ear.prev !== ear.next) {
        prev = ear.prev;
        next = ear.next;
        if (invSize ? isEarHashed(ear, minX, minY, invSize) : isEar(ear)) {
          triangles.push(prev.i / dim | 0);
          triangles.push(ear.i / dim | 0);
          triangles.push(next.i / dim | 0);
          removeNode(ear);
          ear = next.next;
          stop = next.next;
          continue;
        }
        ear = next;
        if (ear === stop) {
          if (!pass) {
            earcutLinked(filterPoints(ear), triangles, dim, minX, minY, invSize, 1);
          } else if (pass === 1) {
            ear = cureLocalIntersections(filterPoints(ear), triangles, dim);
            earcutLinked(ear, triangles, dim, minX, minY, invSize, 2);
          } else if (pass === 2) {
            splitEarcut(ear, triangles, dim, minX, minY, invSize);
          }
          break;
        }
      }
    }
    function isEar(ear) {
      var a2 = ear.prev, b2 = ear, c2 = ear.next;
      if (area(a2, b2, c2) >= 0) return false;
      var ax = a2.x, bx = b2.x, cx = c2.x, ay = a2.y, by = b2.y, cy = c2.y;
      var x0 = ax < bx ? ax < cx ? ax : cx : bx < cx ? bx : cx, y0 = ay < by ? ay < cy ? ay : cy : by < cy ? by : cy, x1 = ax > bx ? ax > cx ? ax : cx : bx > cx ? bx : cx, y1 = ay > by ? ay > cy ? ay : cy : by > cy ? by : cy;
      var p2 = c2.next;
      while (p2 !== a2) {
        if (p2.x >= x0 && p2.x <= x1 && p2.y >= y0 && p2.y <= y1 && pointInTriangle(ax, ay, bx, by, cx, cy, p2.x, p2.y) && area(p2.prev, p2, p2.next) >= 0) return false;
        p2 = p2.next;
      }
      return true;
    }
    function isEarHashed(ear, minX, minY, invSize) {
      var a2 = ear.prev, b2 = ear, c2 = ear.next;
      if (area(a2, b2, c2) >= 0) return false;
      var ax = a2.x, bx = b2.x, cx = c2.x, ay = a2.y, by = b2.y, cy = c2.y;
      var x0 = ax < bx ? ax < cx ? ax : cx : bx < cx ? bx : cx, y0 = ay < by ? ay < cy ? ay : cy : by < cy ? by : cy, x1 = ax > bx ? ax > cx ? ax : cx : bx > cx ? bx : cx, y1 = ay > by ? ay > cy ? ay : cy : by > cy ? by : cy;
      var minZ = zOrder(x0, y0, minX, minY, invSize), maxZ = zOrder(x1, y1, minX, minY, invSize);
      var p2 = ear.prevZ, n2 = ear.nextZ;
      while (p2 && p2.z >= minZ && n2 && n2.z <= maxZ) {
        if (p2.x >= x0 && p2.x <= x1 && p2.y >= y0 && p2.y <= y1 && p2 !== a2 && p2 !== c2 && pointInTriangle(ax, ay, bx, by, cx, cy, p2.x, p2.y) && area(p2.prev, p2, p2.next) >= 0) return false;
        p2 = p2.prevZ;
        if (n2.x >= x0 && n2.x <= x1 && n2.y >= y0 && n2.y <= y1 && n2 !== a2 && n2 !== c2 && pointInTriangle(ax, ay, bx, by, cx, cy, n2.x, n2.y) && area(n2.prev, n2, n2.next) >= 0) return false;
        n2 = n2.nextZ;
      }
      while (p2 && p2.z >= minZ) {
        if (p2.x >= x0 && p2.x <= x1 && p2.y >= y0 && p2.y <= y1 && p2 !== a2 && p2 !== c2 && pointInTriangle(ax, ay, bx, by, cx, cy, p2.x, p2.y) && area(p2.prev, p2, p2.next) >= 0) return false;
        p2 = p2.prevZ;
      }
      while (n2 && n2.z <= maxZ) {
        if (n2.x >= x0 && n2.x <= x1 && n2.y >= y0 && n2.y <= y1 && n2 !== a2 && n2 !== c2 && pointInTriangle(ax, ay, bx, by, cx, cy, n2.x, n2.y) && area(n2.prev, n2, n2.next) >= 0) return false;
        n2 = n2.nextZ;
      }
      return true;
    }
    function cureLocalIntersections(start, triangles, dim) {
      var p2 = start;
      do {
        var a2 = p2.prev, b2 = p2.next.next;
        if (!equals(a2, b2) && intersects(a2, p2, p2.next, b2) && locallyInside(a2, b2) && locallyInside(b2, a2)) {
          triangles.push(a2.i / dim | 0);
          triangles.push(p2.i / dim | 0);
          triangles.push(b2.i / dim | 0);
          removeNode(p2);
          removeNode(p2.next);
          p2 = start = b2;
        }
        p2 = p2.next;
      } while (p2 !== start);
      return filterPoints(p2);
    }
    function splitEarcut(start, triangles, dim, minX, minY, invSize) {
      var a2 = start;
      do {
        var b2 = a2.next.next;
        while (b2 !== a2.prev) {
          if (a2.i !== b2.i && isValidDiagonal(a2, b2)) {
            var c2 = splitPolygon(a2, b2);
            a2 = filterPoints(a2, a2.next);
            c2 = filterPoints(c2, c2.next);
            earcutLinked(a2, triangles, dim, minX, minY, invSize, 0);
            earcutLinked(c2, triangles, dim, minX, minY, invSize, 0);
            return;
          }
          b2 = b2.next;
        }
        a2 = a2.next;
      } while (a2 !== start);
    }
    function eliminateHoles(data, holeIndices, outerNode, dim) {
      var queue = [], i2, len, start, end, list;
      for (i2 = 0, len = holeIndices.length; i2 < len; i2++) {
        start = holeIndices[i2] * dim;
        end = i2 < len - 1 ? holeIndices[i2 + 1] * dim : data.length;
        list = linkedList(data, start, end, dim, false);
        if (list === list.next) list.steiner = true;
        queue.push(getLeftmost(list));
      }
      queue.sort(compareX);
      for (i2 = 0; i2 < queue.length; i2++) {
        outerNode = eliminateHole(queue[i2], outerNode);
      }
      return outerNode;
    }
    function compareX(a2, b2) {
      return a2.x - b2.x;
    }
    function eliminateHole(hole, outerNode) {
      var bridge = findHoleBridge(hole, outerNode);
      if (!bridge) {
        return outerNode;
      }
      var bridgeReverse = splitPolygon(bridge, hole);
      filterPoints(bridgeReverse, bridgeReverse.next);
      return filterPoints(bridge, bridge.next);
    }
    function findHoleBridge(hole, outerNode) {
      var p2 = outerNode, hx = hole.x, hy = hole.y, qx = -Infinity, m2;
      do {
        if (hy <= p2.y && hy >= p2.next.y && p2.next.y !== p2.y) {
          var x2 = p2.x + (hy - p2.y) * (p2.next.x - p2.x) / (p2.next.y - p2.y);
          if (x2 <= hx && x2 > qx) {
            qx = x2;
            m2 = p2.x < p2.next.x ? p2 : p2.next;
            if (x2 === hx) return m2;
          }
        }
        p2 = p2.next;
      } while (p2 !== outerNode);
      if (!m2) return null;
      var stop = m2, mx = m2.x, my = m2.y, tanMin = Infinity, tan;
      p2 = m2;
      do {
        if (hx >= p2.x && p2.x >= mx && hx !== p2.x && pointInTriangle(hy < my ? hx : qx, hy, mx, my, hy < my ? qx : hx, hy, p2.x, p2.y)) {
          tan = Math.abs(hy - p2.y) / (hx - p2.x);
          if (locallyInside(p2, hole) && (tan < tanMin || tan === tanMin && (p2.x > m2.x || p2.x === m2.x && sectorContainsSector(m2, p2)))) {
            m2 = p2;
            tanMin = tan;
          }
        }
        p2 = p2.next;
      } while (p2 !== stop);
      return m2;
    }
    function sectorContainsSector(m2, p2) {
      return area(m2.prev, m2, p2.prev) < 0 && area(p2.next, m2, m2.next) < 0;
    }
    function indexCurve(start, minX, minY, invSize) {
      var p2 = start;
      do {
        if (p2.z === 0) p2.z = zOrder(p2.x, p2.y, minX, minY, invSize);
        p2.prevZ = p2.prev;
        p2.nextZ = p2.next;
        p2 = p2.next;
      } while (p2 !== start);
      p2.prevZ.nextZ = null;
      p2.prevZ = null;
      sortLinked(p2);
    }
    function sortLinked(list) {
      var i2, p2, q2, e2, tail, numMerges, pSize, qSize, inSize = 1;
      do {
        p2 = list;
        list = null;
        tail = null;
        numMerges = 0;
        while (p2) {
          numMerges++;
          q2 = p2;
          pSize = 0;
          for (i2 = 0; i2 < inSize; i2++) {
            pSize++;
            q2 = q2.nextZ;
            if (!q2) break;
          }
          qSize = inSize;
          while (pSize > 0 || qSize > 0 && q2) {
            if (pSize !== 0 && (qSize === 0 || !q2 || p2.z <= q2.z)) {
              e2 = p2;
              p2 = p2.nextZ;
              pSize--;
            } else {
              e2 = q2;
              q2 = q2.nextZ;
              qSize--;
            }
            if (tail) tail.nextZ = e2;
            else list = e2;
            e2.prevZ = tail;
            tail = e2;
          }
          p2 = q2;
        }
        tail.nextZ = null;
        inSize *= 2;
      } while (numMerges > 1);
      return list;
    }
    function zOrder(x2, y2, minX, minY, invSize) {
      x2 = (x2 - minX) * invSize | 0;
      y2 = (y2 - minY) * invSize | 0;
      x2 = (x2 | x2 << 8) & 16711935;
      x2 = (x2 | x2 << 4) & 252645135;
      x2 = (x2 | x2 << 2) & 858993459;
      x2 = (x2 | x2 << 1) & 1431655765;
      y2 = (y2 | y2 << 8) & 16711935;
      y2 = (y2 | y2 << 4) & 252645135;
      y2 = (y2 | y2 << 2) & 858993459;
      y2 = (y2 | y2 << 1) & 1431655765;
      return x2 | y2 << 1;
    }
    function getLeftmost(start) {
      var p2 = start, leftmost = start;
      do {
        if (p2.x < leftmost.x || p2.x === leftmost.x && p2.y < leftmost.y) leftmost = p2;
        p2 = p2.next;
      } while (p2 !== start);
      return leftmost;
    }
    function pointInTriangle(ax, ay, bx, by, cx, cy, px, py) {
      return (cx - px) * (ay - py) >= (ax - px) * (cy - py) && (ax - px) * (by - py) >= (bx - px) * (ay - py) && (bx - px) * (cy - py) >= (cx - px) * (by - py);
    }
    function isValidDiagonal(a2, b2) {
      return a2.next.i !== b2.i && a2.prev.i !== b2.i && !intersectsPolygon(a2, b2) && // dones't intersect other edges
      (locallyInside(a2, b2) && locallyInside(b2, a2) && middleInside(a2, b2) && // locally visible
      (area(a2.prev, a2, b2.prev) || area(a2, b2.prev, b2)) || // does not create opposite-facing sectors
      equals(a2, b2) && area(a2.prev, a2, a2.next) > 0 && area(b2.prev, b2, b2.next) > 0);
    }
    function area(p2, q2, r2) {
      return (q2.y - p2.y) * (r2.x - q2.x) - (q2.x - p2.x) * (r2.y - q2.y);
    }
    function equals(p1, p2) {
      return p1.x === p2.x && p1.y === p2.y;
    }
    function intersects(p1, q1, p2, q2) {
      var o1 = sign2(area(p1, q1, p2));
      var o2 = sign2(area(p1, q1, q2));
      var o3 = sign2(area(p2, q2, p1));
      var o4 = sign2(area(p2, q2, q1));
      if (o1 !== o2 && o3 !== o4) return true;
      if (o1 === 0 && onSegment(p1, p2, q1)) return true;
      if (o2 === 0 && onSegment(p1, q2, q1)) return true;
      if (o3 === 0 && onSegment(p2, p1, q2)) return true;
      if (o4 === 0 && onSegment(p2, q1, q2)) return true;
      return false;
    }
    function onSegment(p2, q2, r2) {
      return q2.x <= Math.max(p2.x, r2.x) && q2.x >= Math.min(p2.x, r2.x) && q2.y <= Math.max(p2.y, r2.y) && q2.y >= Math.min(p2.y, r2.y);
    }
    function sign2(num) {
      return num > 0 ? 1 : num < 0 ? -1 : 0;
    }
    function intersectsPolygon(a2, b2) {
      var p2 = a2;
      do {
        if (p2.i !== a2.i && p2.next.i !== a2.i && p2.i !== b2.i && p2.next.i !== b2.i && intersects(p2, p2.next, a2, b2)) return true;
        p2 = p2.next;
      } while (p2 !== a2);
      return false;
    }
    function locallyInside(a2, b2) {
      return area(a2.prev, a2, a2.next) < 0 ? area(a2, b2, a2.next) >= 0 && area(a2, a2.prev, b2) >= 0 : area(a2, b2, a2.prev) < 0 || area(a2, a2.next, b2) < 0;
    }
    function middleInside(a2, b2) {
      var p2 = a2, inside = false, px = (a2.x + b2.x) / 2, py = (a2.y + b2.y) / 2;
      do {
        if (p2.y > py !== p2.next.y > py && p2.next.y !== p2.y && px < (p2.next.x - p2.x) * (py - p2.y) / (p2.next.y - p2.y) + p2.x)
          inside = !inside;
        p2 = p2.next;
      } while (p2 !== a2);
      return inside;
    }
    function splitPolygon(a2, b2) {
      var a22 = new Node(a2.i, a2.x, a2.y), b22 = new Node(b2.i, b2.x, b2.y), an = a2.next, bp = b2.prev;
      a2.next = b2;
      b2.prev = a2;
      a22.next = an;
      an.prev = a22;
      b22.next = a22;
      a22.prev = b22;
      bp.next = b22;
      b22.prev = bp;
      return b22;
    }
    function insertNode(i2, x2, y2, last) {
      var p2 = new Node(i2, x2, y2);
      if (!last) {
        p2.prev = p2;
        p2.next = p2;
      } else {
        p2.next = last.next;
        p2.prev = last;
        last.next.prev = p2;
        last.next = p2;
      }
      return p2;
    }
    function removeNode(p2) {
      p2.next.prev = p2.prev;
      p2.prev.next = p2.next;
      if (p2.prevZ) p2.prevZ.nextZ = p2.nextZ;
      if (p2.nextZ) p2.nextZ.prevZ = p2.prevZ;
    }
    function Node(i2, x2, y2) {
      this.i = i2;
      this.x = x2;
      this.y = y2;
      this.prev = null;
      this.next = null;
      this.z = 0;
      this.prevZ = null;
      this.nextZ = null;
      this.steiner = false;
    }
    earcut$1.deviation = function(data, holeIndices, dim, triangles) {
      var hasHoles = holeIndices && holeIndices.length;
      var outerLen = hasHoles ? holeIndices[0] * dim : data.length;
      var polygonArea = Math.abs(signedArea(data, 0, outerLen, dim));
      if (hasHoles) {
        for (var i2 = 0, len = holeIndices.length; i2 < len; i2++) {
          var start = holeIndices[i2] * dim;
          var end = i2 < len - 1 ? holeIndices[i2 + 1] * dim : data.length;
          polygonArea -= Math.abs(signedArea(data, start, end, dim));
        }
      }
      var trianglesArea = 0;
      for (i2 = 0; i2 < triangles.length; i2 += 3) {
        var a2 = triangles[i2] * dim;
        var b2 = triangles[i2 + 1] * dim;
        var c2 = triangles[i2 + 2] * dim;
        trianglesArea += Math.abs(
          (data[a2] - data[c2]) * (data[b2 + 1] - data[a2 + 1]) - (data[a2] - data[b2]) * (data[c2 + 1] - data[a2 + 1])
        );
      }
      return polygonArea === 0 && trianglesArea === 0 ? 0 : Math.abs((trianglesArea - polygonArea) / polygonArea);
    };
    function signedArea(data, start, end, dim) {
      var sum = 0;
      for (var i2 = start, j2 = end - dim; i2 < end; i2 += dim) {
        sum += (data[j2] - data[i2]) * (data[i2 + 1] + data[j2 + 1]);
        j2 = i2;
      }
      return sum;
    }
    earcut$1.flatten = function(data) {
      var dim = data[0][0].length, result = { vertices: [], holes: [], dimensions: dim }, holeIndex = 0;
      for (var i2 = 0; i2 < data.length; i2++) {
        for (var j2 = 0; j2 < data[i2].length; j2++) {
          for (var d2 = 0; d2 < dim; d2++) result.vertices.push(data[i2][j2][d2]);
        }
        if (i2 > 0) {
          holeIndex += data[i2 - 1].length;
          result.holes.push(holeIndex);
        }
      }
      return result;
    };
    return earcut.exports;
  }
  requireEarcut();
  var url = {};
  var punycode$1 = { exports: {} };
  var punycode = punycode$1.exports;
  var hasRequiredPunycode;
  function requirePunycode() {
    if (hasRequiredPunycode) return punycode$1.exports;
    hasRequiredPunycode = 1;
    (function(module, exports$1) {
      (function(root) {
        var freeExports = exports$1 && !exports$1.nodeType && exports$1;
        var freeModule = module && !module.nodeType && module;
        var freeGlobal = typeof globalThis == "object" && globalThis;
        if (freeGlobal.global === freeGlobal || freeGlobal.window === freeGlobal || freeGlobal.self === freeGlobal) {
          root = freeGlobal;
        }
        var punycode2, maxInt = 2147483647, base = 36, tMin = 1, tMax = 26, skew = 38, damp = 700, initialBias = 72, initialN = 128, delimiter = "-", regexPunycode = /^xn--/, regexNonASCII = /[^\x20-\x7E]/, regexSeparators = /[\x2E\u3002\uFF0E\uFF61]/g, errors = {
          "overflow": "Overflow: input needs wider integers to process",
          "not-basic": "Illegal input >= 0x80 (not a basic code point)",
          "invalid-input": "Invalid input"
        }, baseMinusTMin = base - tMin, floor2 = Math.floor, stringFromCharCode = String.fromCharCode, key;
        function error(type2) {
          throw new RangeError(errors[type2]);
        }
        function map2(array, fn) {
          var length = array.length;
          var result = [];
          while (length--) {
            result[length] = fn(array[length]);
          }
          return result;
        }
        function mapDomain(string, fn) {
          var parts = string.split("@");
          var result = "";
          if (parts.length > 1) {
            result = parts[0] + "@";
            string = parts[1];
          }
          string = string.replace(regexSeparators, ".");
          var labels = string.split(".");
          var encoded = map2(labels, fn).join(".");
          return result + encoded;
        }
        function ucs2decode(string) {
          var output = [], counter = 0, length = string.length, value, extra;
          while (counter < length) {
            value = string.charCodeAt(counter++);
            if (value >= 55296 && value <= 56319 && counter < length) {
              extra = string.charCodeAt(counter++);
              if ((extra & 64512) == 56320) {
                output.push(((value & 1023) << 10) + (extra & 1023) + 65536);
              } else {
                output.push(value);
                counter--;
              }
            } else {
              output.push(value);
            }
          }
          return output;
        }
        function ucs2encode(array) {
          return map2(array, function(value) {
            var output = "";
            if (value > 65535) {
              value -= 65536;
              output += stringFromCharCode(value >>> 10 & 1023 | 55296);
              value = 56320 | value & 1023;
            }
            output += stringFromCharCode(value);
            return output;
          }).join("");
        }
        function basicToDigit(codePoint) {
          if (codePoint - 48 < 10) {
            return codePoint - 22;
          }
          if (codePoint - 65 < 26) {
            return codePoint - 65;
          }
          if (codePoint - 97 < 26) {
            return codePoint - 97;
          }
          return base;
        }
        function digitToBasic(digit, flag) {
          return digit + 22 + 75 * (digit < 26) - ((flag != 0) << 5);
        }
        function adapt(delta, numPoints, firstTime) {
          var k2 = 0;
          delta = firstTime ? floor2(delta / damp) : delta >> 1;
          delta += floor2(delta / numPoints);
          for (; delta > baseMinusTMin * tMax >> 1; k2 += base) {
            delta = floor2(delta / baseMinusTMin);
          }
          return floor2(k2 + (baseMinusTMin + 1) * delta / (delta + skew));
        }
        function decode(input) {
          var output = [], inputLength = input.length, out, i2 = 0, n2 = initialN, bias = initialBias, basic, j2, index, oldi, w2, k2, digit, t2, baseMinusT;
          basic = input.lastIndexOf(delimiter);
          if (basic < 0) {
            basic = 0;
          }
          for (j2 = 0; j2 < basic; ++j2) {
            if (input.charCodeAt(j2) >= 128) {
              error("not-basic");
            }
            output.push(input.charCodeAt(j2));
          }
          for (index = basic > 0 ? basic + 1 : 0; index < inputLength; ) {
            for (oldi = i2, w2 = 1, k2 = base; ; k2 += base) {
              if (index >= inputLength) {
                error("invalid-input");
              }
              digit = basicToDigit(input.charCodeAt(index++));
              if (digit >= base || digit > floor2((maxInt - i2) / w2)) {
                error("overflow");
              }
              i2 += digit * w2;
              t2 = k2 <= bias ? tMin : k2 >= bias + tMax ? tMax : k2 - bias;
              if (digit < t2) {
                break;
              }
              baseMinusT = base - t2;
              if (w2 > floor2(maxInt / baseMinusT)) {
                error("overflow");
              }
              w2 *= baseMinusT;
            }
            out = output.length + 1;
            bias = adapt(i2 - oldi, out, oldi == 0);
            if (floor2(i2 / out) > maxInt - n2) {
              error("overflow");
            }
            n2 += floor2(i2 / out);
            i2 %= out;
            output.splice(i2++, 0, n2);
          }
          return ucs2encode(output);
        }
        function encode(input) {
          var n2, delta, handledCPCount, basicLength, bias, j2, m2, q2, k2, t2, currentValue, output = [], inputLength, handledCPCountPlusOne, baseMinusT, qMinusT;
          input = ucs2decode(input);
          inputLength = input.length;
          n2 = initialN;
          delta = 0;
          bias = initialBias;
          for (j2 = 0; j2 < inputLength; ++j2) {
            currentValue = input[j2];
            if (currentValue < 128) {
              output.push(stringFromCharCode(currentValue));
            }
          }
          handledCPCount = basicLength = output.length;
          if (basicLength) {
            output.push(delimiter);
          }
          while (handledCPCount < inputLength) {
            for (m2 = maxInt, j2 = 0; j2 < inputLength; ++j2) {
              currentValue = input[j2];
              if (currentValue >= n2 && currentValue < m2) {
                m2 = currentValue;
              }
            }
            handledCPCountPlusOne = handledCPCount + 1;
            if (m2 - n2 > floor2((maxInt - delta) / handledCPCountPlusOne)) {
              error("overflow");
            }
            delta += (m2 - n2) * handledCPCountPlusOne;
            n2 = m2;
            for (j2 = 0; j2 < inputLength; ++j2) {
              currentValue = input[j2];
              if (currentValue < n2 && ++delta > maxInt) {
                error("overflow");
              }
              if (currentValue == n2) {
                for (q2 = delta, k2 = base; ; k2 += base) {
                  t2 = k2 <= bias ? tMin : k2 >= bias + tMax ? tMax : k2 - bias;
                  if (q2 < t2) {
                    break;
                  }
                  qMinusT = q2 - t2;
                  baseMinusT = base - t2;
                  output.push(
                    stringFromCharCode(digitToBasic(t2 + qMinusT % baseMinusT, 0))
                  );
                  q2 = floor2(qMinusT / baseMinusT);
                }
                output.push(stringFromCharCode(digitToBasic(q2, 0)));
                bias = adapt(delta, handledCPCountPlusOne, handledCPCount == basicLength);
                delta = 0;
                ++handledCPCount;
              }
            }
            ++delta;
            ++n2;
          }
          return output.join("");
        }
        function toUnicode(input) {
          return mapDomain(input, function(string) {
            return regexPunycode.test(string) ? decode(string.slice(4).toLowerCase()) : string;
          });
        }
        function toASCII(input) {
          return mapDomain(input, function(string) {
            return regexNonASCII.test(string) ? "xn--" + encode(string) : string;
          });
        }
        punycode2 = {
          /**
           * A string representing the current Punycode.js version number.
           * @memberOf punycode
           * @type String
           */
          "version": "1.4.1",
          /**
           * An object of methods to convert from JavaScript's internal character
           * representation (UCS-2) to Unicode code points, and back.
           * @see <https://mathiasbynens.be/notes/javascript-encoding>
           * @memberOf punycode
           * @type Object
           */
          "ucs2": {
            "decode": ucs2decode,
            "encode": ucs2encode
          },
          "decode": decode,
          "encode": encode,
          "toASCII": toASCII,
          "toUnicode": toUnicode
        };
        if (freeExports && freeModule) {
          if (module.exports == freeExports) {
            freeModule.exports = punycode2;
          } else {
            for (key in punycode2) {
              punycode2.hasOwnProperty(key) && (freeExports[key] = punycode2[key]);
            }
          }
        } else {
          root.punycode = punycode2;
        }
      })(punycode);
    })(punycode$1, punycode$1.exports);
    return punycode$1.exports;
  }
  var type;
  var hasRequiredType;
  function requireType() {
    if (hasRequiredType) return type;
    hasRequiredType = 1;
    type = TypeError;
    return type;
  }
  const __viteBrowserExternal = {};
  const __viteBrowserExternal$1 = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
    __proto__: null,
    default: __viteBrowserExternal
  }, Symbol.toStringTag, { value: "Module" }));
  const require$$0 = /* @__PURE__ */ getAugmentedNamespace(__viteBrowserExternal$1);
  var objectInspect;
  var hasRequiredObjectInspect;
  function requireObjectInspect() {
    if (hasRequiredObjectInspect) return objectInspect;
    hasRequiredObjectInspect = 1;
    var hasMap = typeof Map === "function" && Map.prototype;
    var mapSizeDescriptor = Object.getOwnPropertyDescriptor && hasMap ? Object.getOwnPropertyDescriptor(Map.prototype, "size") : null;
    var mapSize2 = hasMap && mapSizeDescriptor && typeof mapSizeDescriptor.get === "function" ? mapSizeDescriptor.get : null;
    var mapForEach = hasMap && Map.prototype.forEach;
    var hasSet = typeof Set === "function" && Set.prototype;
    var setSizeDescriptor = Object.getOwnPropertyDescriptor && hasSet ? Object.getOwnPropertyDescriptor(Set.prototype, "size") : null;
    var setSize = hasSet && setSizeDescriptor && typeof setSizeDescriptor.get === "function" ? setSizeDescriptor.get : null;
    var setForEach = hasSet && Set.prototype.forEach;
    var hasWeakMap = typeof WeakMap === "function" && WeakMap.prototype;
    var weakMapHas = hasWeakMap ? WeakMap.prototype.has : null;
    var hasWeakSet = typeof WeakSet === "function" && WeakSet.prototype;
    var weakSetHas = hasWeakSet ? WeakSet.prototype.has : null;
    var hasWeakRef = typeof WeakRef === "function" && WeakRef.prototype;
    var weakRefDeref = hasWeakRef ? WeakRef.prototype.deref : null;
    var booleanValueOf = Boolean.prototype.valueOf;
    var objectToString = Object.prototype.toString;
    var functionToString = Function.prototype.toString;
    var $match = String.prototype.match;
    var $slice = String.prototype.slice;
    var $replace = String.prototype.replace;
    var $toUpperCase = String.prototype.toUpperCase;
    var $toLowerCase = String.prototype.toLowerCase;
    var $test = RegExp.prototype.test;
    var $concat = Array.prototype.concat;
    var $join = Array.prototype.join;
    var $arrSlice = Array.prototype.slice;
    var $floor = Math.floor;
    var bigIntValueOf = typeof BigInt === "function" ? BigInt.prototype.valueOf : null;
    var gOPS = Object.getOwnPropertySymbols;
    var symToString = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? Symbol.prototype.toString : null;
    var hasShammedSymbols = typeof Symbol === "function" && typeof Symbol.iterator === "object";
    var toStringTag = typeof Symbol === "function" && Symbol.toStringTag && (typeof Symbol.toStringTag === hasShammedSymbols ? "object" : "symbol") ? Symbol.toStringTag : null;
    var isEnumerable = Object.prototype.propertyIsEnumerable;
    var gPO = (typeof Reflect === "function" ? Reflect.getPrototypeOf : Object.getPrototypeOf) || ([].__proto__ === Array.prototype ? function(O2) {
      return O2.__proto__;
    } : null);
    function addNumericSeparator(num, str) {
      if (num === Infinity || num === -Infinity || num !== num || num && num > -1e3 && num < 1e3 || $test.call(/e/, str)) {
        return str;
      }
      var sepRegex = /[0-9](?=(?:[0-9]{3})+(?![0-9]))/g;
      if (typeof num === "number") {
        var int = num < 0 ? -$floor(-num) : $floor(num);
        if (int !== num) {
          var intStr = String(int);
          var dec = $slice.call(str, intStr.length + 1);
          return $replace.call(intStr, sepRegex, "$&_") + "." + $replace.call($replace.call(dec, /([0-9]{3})/g, "$&_"), /_$/, "");
        }
      }
      return $replace.call(str, sepRegex, "$&_");
    }
    var utilInspect = require$$0;
    var inspectCustom = utilInspect.custom;
    var inspectSymbol = isSymbol(inspectCustom) ? inspectCustom : null;
    var quotes = {
      __proto__: null,
      "double": '"',
      single: "'"
    };
    var quoteREs = {
      __proto__: null,
      "double": /(["\\])/g,
      single: /(['\\])/g
    };
    objectInspect = function inspect_(obj, options, depth, seen) {
      var opts = options || {};
      if (has(opts, "quoteStyle") && !has(quotes, opts.quoteStyle)) {
        throw new TypeError('option "quoteStyle" must be "single" or "double"');
      }
      if (has(opts, "maxStringLength") && (typeof opts.maxStringLength === "number" ? opts.maxStringLength < 0 && opts.maxStringLength !== Infinity : opts.maxStringLength !== null)) {
        throw new TypeError('option "maxStringLength", if provided, must be a positive integer, Infinity, or `null`');
      }
      var customInspect = has(opts, "customInspect") ? opts.customInspect : true;
      if (typeof customInspect !== "boolean" && customInspect !== "symbol") {
        throw new TypeError("option \"customInspect\", if provided, must be `true`, `false`, or `'symbol'`");
      }
      if (has(opts, "indent") && opts.indent !== null && opts.indent !== "	" && !(parseInt(opts.indent, 10) === opts.indent && opts.indent > 0)) {
        throw new TypeError('option "indent" must be "\\t", an integer > 0, or `null`');
      }
      if (has(opts, "numericSeparator") && typeof opts.numericSeparator !== "boolean") {
        throw new TypeError('option "numericSeparator", if provided, must be `true` or `false`');
      }
      var numericSeparator = opts.numericSeparator;
      if (typeof obj === "undefined") {
        return "undefined";
      }
      if (obj === null) {
        return "null";
      }
      if (typeof obj === "boolean") {
        return obj ? "true" : "false";
      }
      if (typeof obj === "string") {
        return inspectString(obj, opts);
      }
      if (typeof obj === "number") {
        if (obj === 0) {
          return Infinity / obj > 0 ? "0" : "-0";
        }
        var str = String(obj);
        return numericSeparator ? addNumericSeparator(obj, str) : str;
      }
      if (typeof obj === "bigint") {
        var bigIntStr = String(obj) + "n";
        return numericSeparator ? addNumericSeparator(obj, bigIntStr) : bigIntStr;
      }
      var maxDepth = typeof opts.depth === "undefined" ? 5 : opts.depth;
      if (typeof depth === "undefined") {
        depth = 0;
      }
      if (depth >= maxDepth && maxDepth > 0 && typeof obj === "object") {
        return isArray(obj) ? "[Array]" : "[Object]";
      }
      var indent = getIndent(opts, depth);
      if (typeof seen === "undefined") {
        seen = [];
      } else if (indexOf(seen, obj) >= 0) {
        return "[Circular]";
      }
      function inspect(value, from, noIndent) {
        if (from) {
          seen = $arrSlice.call(seen);
          seen.push(from);
        }
        if (noIndent) {
          var newOpts = {
            depth: opts.depth
          };
          if (has(opts, "quoteStyle")) {
            newOpts.quoteStyle = opts.quoteStyle;
          }
          return inspect_(value, newOpts, depth + 1, seen);
        }
        return inspect_(value, opts, depth + 1, seen);
      }
      if (typeof obj === "function" && !isRegExp(obj)) {
        var name = nameOf(obj);
        var keys = arrObjKeys(obj, inspect);
        return "[Function" + (name ? ": " + name : " (anonymous)") + "]" + (keys.length > 0 ? " { " + $join.call(keys, ", ") + " }" : "");
      }
      if (isSymbol(obj)) {
        var symString = hasShammedSymbols ? $replace.call(String(obj), /^(Symbol\(.*\))_[^)]*$/, "$1") : symToString.call(obj);
        return typeof obj === "object" && !hasShammedSymbols ? markBoxed(symString) : symString;
      }
      if (isElement(obj)) {
        var s2 = "<" + $toLowerCase.call(String(obj.nodeName));
        var attrs = obj.attributes || [];
        for (var i2 = 0; i2 < attrs.length; i2++) {
          s2 += " " + attrs[i2].name + "=" + wrapQuotes(quote(attrs[i2].value), "double", opts);
        }
        s2 += ">";
        if (obj.childNodes && obj.childNodes.length) {
          s2 += "...";
        }
        s2 += "</" + $toLowerCase.call(String(obj.nodeName)) + ">";
        return s2;
      }
      if (isArray(obj)) {
        if (obj.length === 0) {
          return "[]";
        }
        var xs2 = arrObjKeys(obj, inspect);
        if (indent && !singleLineValues(xs2)) {
          return "[" + indentedJoin(xs2, indent) + "]";
        }
        return "[ " + $join.call(xs2, ", ") + " ]";
      }
      if (isError(obj)) {
        var parts = arrObjKeys(obj, inspect);
        if (!("cause" in Error.prototype) && "cause" in obj && !isEnumerable.call(obj, "cause")) {
          return "{ [" + String(obj) + "] " + $join.call($concat.call("[cause]: " + inspect(obj.cause), parts), ", ") + " }";
        }
        if (parts.length === 0) {
          return "[" + String(obj) + "]";
        }
        return "{ [" + String(obj) + "] " + $join.call(parts, ", ") + " }";
      }
      if (typeof obj === "object" && customInspect) {
        if (inspectSymbol && typeof obj[inspectSymbol] === "function" && utilInspect) {
          return utilInspect(obj, { depth: maxDepth - depth });
        } else if (customInspect !== "symbol" && typeof obj.inspect === "function") {
          return obj.inspect();
        }
      }
      if (isMap(obj)) {
        var mapParts = [];
        if (mapForEach) {
          mapForEach.call(obj, function(value, key) {
            mapParts.push(inspect(key, obj, true) + " => " + inspect(value, obj));
          });
        }
        return collectionOf("Map", mapSize2.call(obj), mapParts, indent);
      }
      if (isSet(obj)) {
        var setParts = [];
        if (setForEach) {
          setForEach.call(obj, function(value) {
            setParts.push(inspect(value, obj));
          });
        }
        return collectionOf("Set", setSize.call(obj), setParts, indent);
      }
      if (isWeakMap(obj)) {
        return weakCollectionOf("WeakMap");
      }
      if (isWeakSet(obj)) {
        return weakCollectionOf("WeakSet");
      }
      if (isWeakRef(obj)) {
        return weakCollectionOf("WeakRef");
      }
      if (isNumber(obj)) {
        return markBoxed(inspect(Number(obj)));
      }
      if (isBigInt(obj)) {
        return markBoxed(inspect(bigIntValueOf.call(obj)));
      }
      if (isBoolean(obj)) {
        return markBoxed(booleanValueOf.call(obj));
      }
      if (isString(obj)) {
        return markBoxed(inspect(String(obj)));
      }
      if (typeof window !== "undefined" && obj === window) {
        return "{ [object Window] }";
      }
      if (typeof globalThis !== "undefined" && obj === globalThis || typeof globalThis !== "undefined" && obj === globalThis) {
        return "{ [object globalThis] }";
      }
      if (!isDate(obj) && !isRegExp(obj)) {
        var ys2 = arrObjKeys(obj, inspect);
        var isPlainObject = gPO ? gPO(obj) === Object.prototype : obj instanceof Object || obj.constructor === Object;
        var protoTag = obj instanceof Object ? "" : "null prototype";
        var stringTag = !isPlainObject && toStringTag && Object(obj) === obj && toStringTag in obj ? $slice.call(toStr(obj), 8, -1) : protoTag ? "Object" : "";
        var constructorTag = isPlainObject || typeof obj.constructor !== "function" ? "" : obj.constructor.name ? obj.constructor.name + " " : "";
        var tag = constructorTag + (stringTag || protoTag ? "[" + $join.call($concat.call([], stringTag || [], protoTag || []), ": ") + "] " : "");
        if (ys2.length === 0) {
          return tag + "{}";
        }
        if (indent) {
          return tag + "{" + indentedJoin(ys2, indent) + "}";
        }
        return tag + "{ " + $join.call(ys2, ", ") + " }";
      }
      return String(obj);
    };
    function wrapQuotes(s2, defaultStyle, opts) {
      var style = opts.quoteStyle || defaultStyle;
      var quoteChar = quotes[style];
      return quoteChar + s2 + quoteChar;
    }
    function quote(s2) {
      return $replace.call(String(s2), /"/g, "&quot;");
    }
    function canTrustToString(obj) {
      return !toStringTag || !(typeof obj === "object" && (toStringTag in obj || typeof obj[toStringTag] !== "undefined"));
    }
    function isArray(obj) {
      return toStr(obj) === "[object Array]" && canTrustToString(obj);
    }
    function isDate(obj) {
      return toStr(obj) === "[object Date]" && canTrustToString(obj);
    }
    function isRegExp(obj) {
      return toStr(obj) === "[object RegExp]" && canTrustToString(obj);
    }
    function isError(obj) {
      return toStr(obj) === "[object Error]" && canTrustToString(obj);
    }
    function isString(obj) {
      return toStr(obj) === "[object String]" && canTrustToString(obj);
    }
    function isNumber(obj) {
      return toStr(obj) === "[object Number]" && canTrustToString(obj);
    }
    function isBoolean(obj) {
      return toStr(obj) === "[object Boolean]" && canTrustToString(obj);
    }
    function isSymbol(obj) {
      if (hasShammedSymbols) {
        return obj && typeof obj === "object" && obj instanceof Symbol;
      }
      if (typeof obj === "symbol") {
        return true;
      }
      if (!obj || typeof obj !== "object" || !symToString) {
        return false;
      }
      try {
        symToString.call(obj);
        return true;
      } catch (e2) {
      }
      return false;
    }
    function isBigInt(obj) {
      if (!obj || typeof obj !== "object" || !bigIntValueOf) {
        return false;
      }
      try {
        bigIntValueOf.call(obj);
        return true;
      } catch (e2) {
      }
      return false;
    }
    var hasOwn = Object.prototype.hasOwnProperty || function(key) {
      return key in this;
    };
    function has(obj, key) {
      return hasOwn.call(obj, key);
    }
    function toStr(obj) {
      return objectToString.call(obj);
    }
    function nameOf(f2) {
      if (f2.name) {
        return f2.name;
      }
      var m2 = $match.call(functionToString.call(f2), /^function\s*([\w$]+)/);
      if (m2) {
        return m2[1];
      }
      return null;
    }
    function indexOf(xs2, x2) {
      if (xs2.indexOf) {
        return xs2.indexOf(x2);
      }
      for (var i2 = 0, l2 = xs2.length; i2 < l2; i2++) {
        if (xs2[i2] === x2) {
          return i2;
        }
      }
      return -1;
    }
    function isMap(x2) {
      if (!mapSize2 || !x2 || typeof x2 !== "object") {
        return false;
      }
      try {
        mapSize2.call(x2);
        try {
          setSize.call(x2);
        } catch (s2) {
          return true;
        }
        return x2 instanceof Map;
      } catch (e2) {
      }
      return false;
    }
    function isWeakMap(x2) {
      if (!weakMapHas || !x2 || typeof x2 !== "object") {
        return false;
      }
      try {
        weakMapHas.call(x2, weakMapHas);
        try {
          weakSetHas.call(x2, weakSetHas);
        } catch (s2) {
          return true;
        }
        return x2 instanceof WeakMap;
      } catch (e2) {
      }
      return false;
    }
    function isWeakRef(x2) {
      if (!weakRefDeref || !x2 || typeof x2 !== "object") {
        return false;
      }
      try {
        weakRefDeref.call(x2);
        return true;
      } catch (e2) {
      }
      return false;
    }
    function isSet(x2) {
      if (!setSize || !x2 || typeof x2 !== "object") {
        return false;
      }
      try {
        setSize.call(x2);
        try {
          mapSize2.call(x2);
        } catch (m2) {
          return true;
        }
        return x2 instanceof Set;
      } catch (e2) {
      }
      return false;
    }
    function isWeakSet(x2) {
      if (!weakSetHas || !x2 || typeof x2 !== "object") {
        return false;
      }
      try {
        weakSetHas.call(x2, weakSetHas);
        try {
          weakMapHas.call(x2, weakMapHas);
        } catch (s2) {
          return true;
        }
        return x2 instanceof WeakSet;
      } catch (e2) {
      }
      return false;
    }
    function isElement(x2) {
      if (!x2 || typeof x2 !== "object") {
        return false;
      }
      if (typeof HTMLElement !== "undefined" && x2 instanceof HTMLElement) {
        return true;
      }
      return typeof x2.nodeName === "string" && typeof x2.getAttribute === "function";
    }
    function inspectString(str, opts) {
      if (str.length > opts.maxStringLength) {
        var remaining = str.length - opts.maxStringLength;
        var trailer = "... " + remaining + " more character" + (remaining > 1 ? "s" : "");
        return inspectString($slice.call(str, 0, opts.maxStringLength), opts) + trailer;
      }
      var quoteRE = quoteREs[opts.quoteStyle || "single"];
      quoteRE.lastIndex = 0;
      var s2 = $replace.call($replace.call(str, quoteRE, "\\$1"), /[\x00-\x1f]/g, lowbyte);
      return wrapQuotes(s2, "single", opts);
    }
    function lowbyte(c2) {
      var n2 = c2.charCodeAt(0);
      var x2 = {
        8: "b",
        9: "t",
        10: "n",
        12: "f",
        13: "r"
      }[n2];
      if (x2) {
        return "\\" + x2;
      }
      return "\\x" + (n2 < 16 ? "0" : "") + $toUpperCase.call(n2.toString(16));
    }
    function markBoxed(str) {
      return "Object(" + str + ")";
    }
    function weakCollectionOf(type2) {
      return type2 + " { ? }";
    }
    function collectionOf(type2, size, entries, indent) {
      var joinedEntries = indent ? indentedJoin(entries, indent) : $join.call(entries, ", ");
      return type2 + " (" + size + ") {" + joinedEntries + "}";
    }
    function singleLineValues(xs2) {
      for (var i2 = 0; i2 < xs2.length; i2++) {
        if (indexOf(xs2[i2], "\n") >= 0) {
          return false;
        }
      }
      return true;
    }
    function getIndent(opts, depth) {
      var baseIndent;
      if (opts.indent === "	") {
        baseIndent = "	";
      } else if (typeof opts.indent === "number" && opts.indent > 0) {
        baseIndent = $join.call(Array(opts.indent + 1), " ");
      } else {
        return null;
      }
      return {
        base: baseIndent,
        prev: $join.call(Array(depth + 1), baseIndent)
      };
    }
    function indentedJoin(xs2, indent) {
      if (xs2.length === 0) {
        return "";
      }
      var lineJoiner = "\n" + indent.prev + indent.base;
      return lineJoiner + $join.call(xs2, "," + lineJoiner) + "\n" + indent.prev;
    }
    function arrObjKeys(obj, inspect) {
      var isArr = isArray(obj);
      var xs2 = [];
      if (isArr) {
        xs2.length = obj.length;
        for (var i2 = 0; i2 < obj.length; i2++) {
          xs2[i2] = has(obj, i2) ? inspect(obj[i2], obj) : "";
        }
      }
      var syms = typeof gOPS === "function" ? gOPS(obj) : [];
      var symMap;
      if (hasShammedSymbols) {
        symMap = {};
        for (var k2 = 0; k2 < syms.length; k2++) {
          symMap["$" + syms[k2]] = syms[k2];
        }
      }
      for (var key in obj) {
        if (!has(obj, key)) {
          continue;
        }
        if (isArr && String(Number(key)) === key && key < obj.length) {
          continue;
        }
        if (hasShammedSymbols && symMap["$" + key] instanceof Symbol) {
          continue;
        } else if ($test.call(/[^\w$]/, key)) {
          xs2.push(inspect(key, obj) + ": " + inspect(obj[key], obj));
        } else {
          xs2.push(key + ": " + inspect(obj[key], obj));
        }
      }
      if (typeof gOPS === "function") {
        for (var j2 = 0; j2 < syms.length; j2++) {
          if (isEnumerable.call(obj, syms[j2])) {
            xs2.push("[" + inspect(syms[j2]) + "]: " + inspect(obj[syms[j2]], obj));
          }
        }
      }
      return xs2;
    }
    return objectInspect;
  }
  var sideChannelList;
  var hasRequiredSideChannelList;
  function requireSideChannelList() {
    if (hasRequiredSideChannelList) return sideChannelList;
    hasRequiredSideChannelList = 1;
    var inspect = /* @__PURE__ */ requireObjectInspect();
    var $TypeError = /* @__PURE__ */ requireType();
    var listGetNode = function(list, key, isDelete) {
      var prev = list;
      var curr;
      for (; (curr = prev.next) != null; prev = curr) {
        if (curr.key === key) {
          prev.next = curr.next;
          if (!isDelete) {
            curr.next = /** @type {NonNullable<typeof list.next>} */
            list.next;
            list.next = curr;
          }
          return curr;
        }
      }
    };
    var listGet = function(objects, key) {
      if (!objects) {
        return void 0;
      }
      var node = listGetNode(objects, key);
      return node && node.value;
    };
    var listSet = function(objects, key, value) {
      var node = listGetNode(objects, key);
      if (node) {
        node.value = value;
      } else {
        objects.next = /** @type {import('./list.d.ts').ListNode<typeof value, typeof key>} */
        {
          // eslint-disable-line no-param-reassign, no-extra-parens
          key,
          next: objects.next,
          value
        };
      }
    };
    var listHas = function(objects, key) {
      if (!objects) {
        return false;
      }
      return !!listGetNode(objects, key);
    };
    var listDelete = function(objects, key) {
      if (objects) {
        return listGetNode(objects, key, true);
      }
    };
    sideChannelList = function getSideChannelList() {
      var $o;
      var channel = {
        assert: function(key) {
          if (!channel.has(key)) {
            throw new $TypeError("Side channel does not contain " + inspect(key));
          }
        },
        "delete": function(key) {
          var root = $o && $o.next;
          var deletedNode = listDelete($o, key);
          if (deletedNode && root && root === deletedNode) {
            $o = void 0;
          }
          return !!deletedNode;
        },
        get: function(key) {
          return listGet($o, key);
        },
        has: function(key) {
          return listHas($o, key);
        },
        set: function(key, value) {
          if (!$o) {
            $o = {
              next: void 0
            };
          }
          listSet(
            /** @type {NonNullable<typeof $o>} */
            $o,
            key,
            value
          );
        }
      };
      return channel;
    };
    return sideChannelList;
  }
  var esObjectAtoms;
  var hasRequiredEsObjectAtoms;
  function requireEsObjectAtoms() {
    if (hasRequiredEsObjectAtoms) return esObjectAtoms;
    hasRequiredEsObjectAtoms = 1;
    esObjectAtoms = Object;
    return esObjectAtoms;
  }
  var esErrors;
  var hasRequiredEsErrors;
  function requireEsErrors() {
    if (hasRequiredEsErrors) return esErrors;
    hasRequiredEsErrors = 1;
    esErrors = Error;
    return esErrors;
  }
  var _eval;
  var hasRequired_eval;
  function require_eval() {
    if (hasRequired_eval) return _eval;
    hasRequired_eval = 1;
    _eval = EvalError;
    return _eval;
  }
  var range;
  var hasRequiredRange;
  function requireRange() {
    if (hasRequiredRange) return range;
    hasRequiredRange = 1;
    range = RangeError;
    return range;
  }
  var ref;
  var hasRequiredRef;
  function requireRef() {
    if (hasRequiredRef) return ref;
    hasRequiredRef = 1;
    ref = ReferenceError;
    return ref;
  }
  var syntax;
  var hasRequiredSyntax;
  function requireSyntax() {
    if (hasRequiredSyntax) return syntax;
    hasRequiredSyntax = 1;
    syntax = SyntaxError;
    return syntax;
  }
  var uri;
  var hasRequiredUri;
  function requireUri() {
    if (hasRequiredUri) return uri;
    hasRequiredUri = 1;
    uri = URIError;
    return uri;
  }
  var abs;
  var hasRequiredAbs;
  function requireAbs() {
    if (hasRequiredAbs) return abs;
    hasRequiredAbs = 1;
    abs = Math.abs;
    return abs;
  }
  var floor;
  var hasRequiredFloor;
  function requireFloor() {
    if (hasRequiredFloor) return floor;
    hasRequiredFloor = 1;
    floor = Math.floor;
    return floor;
  }
  var max;
  var hasRequiredMax;
  function requireMax() {
    if (hasRequiredMax) return max;
    hasRequiredMax = 1;
    max = Math.max;
    return max;
  }
  var min;
  var hasRequiredMin;
  function requireMin() {
    if (hasRequiredMin) return min;
    hasRequiredMin = 1;
    min = Math.min;
    return min;
  }
  var pow;
  var hasRequiredPow;
  function requirePow() {
    if (hasRequiredPow) return pow;
    hasRequiredPow = 1;
    pow = Math.pow;
    return pow;
  }
  var round;
  var hasRequiredRound;
  function requireRound() {
    if (hasRequiredRound) return round;
    hasRequiredRound = 1;
    round = Math.round;
    return round;
  }
  var _isNaN;
  var hasRequired_isNaN;
  function require_isNaN() {
    if (hasRequired_isNaN) return _isNaN;
    hasRequired_isNaN = 1;
    _isNaN = Number.isNaN || function isNaN2(a2) {
      return a2 !== a2;
    };
    return _isNaN;
  }
  var sign$1;
  var hasRequiredSign;
  function requireSign() {
    if (hasRequiredSign) return sign$1;
    hasRequiredSign = 1;
    var $isNaN = /* @__PURE__ */ require_isNaN();
    sign$1 = function sign2(number) {
      if ($isNaN(number) || number === 0) {
        return number;
      }
      return number < 0 ? -1 : 1;
    };
    return sign$1;
  }
  var gOPD;
  var hasRequiredGOPD;
  function requireGOPD() {
    if (hasRequiredGOPD) return gOPD;
    hasRequiredGOPD = 1;
    gOPD = Object.getOwnPropertyDescriptor;
    return gOPD;
  }
  var gopd;
  var hasRequiredGopd;
  function requireGopd() {
    if (hasRequiredGopd) return gopd;
    hasRequiredGopd = 1;
    var $gOPD = /* @__PURE__ */ requireGOPD();
    if ($gOPD) {
      try {
        $gOPD([], "length");
      } catch (e2) {
        $gOPD = null;
      }
    }
    gopd = $gOPD;
    return gopd;
  }
  var esDefineProperty;
  var hasRequiredEsDefineProperty;
  function requireEsDefineProperty() {
    if (hasRequiredEsDefineProperty) return esDefineProperty;
    hasRequiredEsDefineProperty = 1;
    var $defineProperty = Object.defineProperty || false;
    if ($defineProperty) {
      try {
        $defineProperty({}, "a", { value: 1 });
      } catch (e2) {
        $defineProperty = false;
      }
    }
    esDefineProperty = $defineProperty;
    return esDefineProperty;
  }
  var shams;
  var hasRequiredShams;
  function requireShams() {
    if (hasRequiredShams) return shams;
    hasRequiredShams = 1;
    shams = function hasSymbols2() {
      if (typeof Symbol !== "function" || typeof Object.getOwnPropertySymbols !== "function") {
        return false;
      }
      if (typeof Symbol.iterator === "symbol") {
        return true;
      }
      var obj = {};
      var sym = /* @__PURE__ */ Symbol("test");
      var symObj = Object(sym);
      if (typeof sym === "string") {
        return false;
      }
      if (Object.prototype.toString.call(sym) !== "[object Symbol]") {
        return false;
      }
      if (Object.prototype.toString.call(symObj) !== "[object Symbol]") {
        return false;
      }
      var symVal = 42;
      obj[sym] = symVal;
      for (var _ in obj) {
        return false;
      }
      if (typeof Object.keys === "function" && Object.keys(obj).length !== 0) {
        return false;
      }
      if (typeof Object.getOwnPropertyNames === "function" && Object.getOwnPropertyNames(obj).length !== 0) {
        return false;
      }
      var syms = Object.getOwnPropertySymbols(obj);
      if (syms.length !== 1 || syms[0] !== sym) {
        return false;
      }
      if (!Object.prototype.propertyIsEnumerable.call(obj, sym)) {
        return false;
      }
      if (typeof Object.getOwnPropertyDescriptor === "function") {
        var descriptor = (
          /** @type {PropertyDescriptor} */
          Object.getOwnPropertyDescriptor(obj, sym)
        );
        if (descriptor.value !== symVal || descriptor.enumerable !== true) {
          return false;
        }
      }
      return true;
    };
    return shams;
  }
  var hasSymbols;
  var hasRequiredHasSymbols;
  function requireHasSymbols() {
    if (hasRequiredHasSymbols) return hasSymbols;
    hasRequiredHasSymbols = 1;
    var origSymbol = typeof Symbol !== "undefined" && Symbol;
    var hasSymbolSham = requireShams();
    hasSymbols = function hasNativeSymbols() {
      if (typeof origSymbol !== "function") {
        return false;
      }
      if (typeof Symbol !== "function") {
        return false;
      }
      if (typeof origSymbol("foo") !== "symbol") {
        return false;
      }
      if (typeof /* @__PURE__ */ Symbol("bar") !== "symbol") {
        return false;
      }
      return hasSymbolSham();
    };
    return hasSymbols;
  }
  var Reflect_getPrototypeOf;
  var hasRequiredReflect_getPrototypeOf;
  function requireReflect_getPrototypeOf() {
    if (hasRequiredReflect_getPrototypeOf) return Reflect_getPrototypeOf;
    hasRequiredReflect_getPrototypeOf = 1;
    Reflect_getPrototypeOf = typeof Reflect !== "undefined" && Reflect.getPrototypeOf || null;
    return Reflect_getPrototypeOf;
  }
  var Object_getPrototypeOf;
  var hasRequiredObject_getPrototypeOf;
  function requireObject_getPrototypeOf() {
    if (hasRequiredObject_getPrototypeOf) return Object_getPrototypeOf;
    hasRequiredObject_getPrototypeOf = 1;
    var $Object = /* @__PURE__ */ requireEsObjectAtoms();
    Object_getPrototypeOf = $Object.getPrototypeOf || null;
    return Object_getPrototypeOf;
  }
  var implementation;
  var hasRequiredImplementation;
  function requireImplementation() {
    if (hasRequiredImplementation) return implementation;
    hasRequiredImplementation = 1;
    var ERROR_MESSAGE = "Function.prototype.bind called on incompatible ";
    var toStr = Object.prototype.toString;
    var max2 = Math.max;
    var funcType = "[object Function]";
    var concatty = function concatty2(a2, b2) {
      var arr = [];
      for (var i2 = 0; i2 < a2.length; i2 += 1) {
        arr[i2] = a2[i2];
      }
      for (var j2 = 0; j2 < b2.length; j2 += 1) {
        arr[j2 + a2.length] = b2[j2];
      }
      return arr;
    };
    var slicy = function slicy2(arrLike, offset) {
      var arr = [];
      for (var i2 = offset, j2 = 0; i2 < arrLike.length; i2 += 1, j2 += 1) {
        arr[j2] = arrLike[i2];
      }
      return arr;
    };
    var joiny = function(arr, joiner) {
      var str = "";
      for (var i2 = 0; i2 < arr.length; i2 += 1) {
        str += arr[i2];
        if (i2 + 1 < arr.length) {
          str += joiner;
        }
      }
      return str;
    };
    implementation = function bind(that) {
      var target = this;
      if (typeof target !== "function" || toStr.apply(target) !== funcType) {
        throw new TypeError(ERROR_MESSAGE + target);
      }
      var args = slicy(arguments, 1);
      var bound;
      var binder = function() {
        if (this instanceof bound) {
          var result = target.apply(
            this,
            concatty(args, arguments)
          );
          if (Object(result) === result) {
            return result;
          }
          return this;
        }
        return target.apply(
          that,
          concatty(args, arguments)
        );
      };
      var boundLength = max2(0, target.length - args.length);
      var boundArgs = [];
      for (var i2 = 0; i2 < boundLength; i2++) {
        boundArgs[i2] = "$" + i2;
      }
      bound = Function("binder", "return function (" + joiny(boundArgs, ",") + "){ return binder.apply(this,arguments); }")(binder);
      if (target.prototype) {
        var Empty = function Empty2() {
        };
        Empty.prototype = target.prototype;
        bound.prototype = new Empty();
        Empty.prototype = null;
      }
      return bound;
    };
    return implementation;
  }
  var functionBind;
  var hasRequiredFunctionBind;
  function requireFunctionBind() {
    if (hasRequiredFunctionBind) return functionBind;
    hasRequiredFunctionBind = 1;
    var implementation2 = requireImplementation();
    functionBind = Function.prototype.bind || implementation2;
    return functionBind;
  }
  var functionCall;
  var hasRequiredFunctionCall;
  function requireFunctionCall() {
    if (hasRequiredFunctionCall) return functionCall;
    hasRequiredFunctionCall = 1;
    functionCall = Function.prototype.call;
    return functionCall;
  }
  var functionApply;
  var hasRequiredFunctionApply;
  function requireFunctionApply() {
    if (hasRequiredFunctionApply) return functionApply;
    hasRequiredFunctionApply = 1;
    functionApply = Function.prototype.apply;
    return functionApply;
  }
  var reflectApply;
  var hasRequiredReflectApply;
  function requireReflectApply() {
    if (hasRequiredReflectApply) return reflectApply;
    hasRequiredReflectApply = 1;
    reflectApply = typeof Reflect !== "undefined" && Reflect && Reflect.apply;
    return reflectApply;
  }
  var actualApply;
  var hasRequiredActualApply;
  function requireActualApply() {
    if (hasRequiredActualApply) return actualApply;
    hasRequiredActualApply = 1;
    var bind = requireFunctionBind();
    var $apply = requireFunctionApply();
    var $call = requireFunctionCall();
    var $reflectApply = requireReflectApply();
    actualApply = $reflectApply || bind.call($call, $apply);
    return actualApply;
  }
  var callBindApplyHelpers;
  var hasRequiredCallBindApplyHelpers;
  function requireCallBindApplyHelpers() {
    if (hasRequiredCallBindApplyHelpers) return callBindApplyHelpers;
    hasRequiredCallBindApplyHelpers = 1;
    var bind = requireFunctionBind();
    var $TypeError = /* @__PURE__ */ requireType();
    var $call = requireFunctionCall();
    var $actualApply = requireActualApply();
    callBindApplyHelpers = function callBindBasic(args) {
      if (args.length < 1 || typeof args[0] !== "function") {
        throw new $TypeError("a function is required");
      }
      return $actualApply(bind, $call, args);
    };
    return callBindApplyHelpers;
  }
  var get;
  var hasRequiredGet;
  function requireGet() {
    if (hasRequiredGet) return get;
    hasRequiredGet = 1;
    var callBind = requireCallBindApplyHelpers();
    var gOPD2 = /* @__PURE__ */ requireGopd();
    var hasProtoAccessor;
    try {
      hasProtoAccessor = /** @type {{ __proto__?: typeof Array.prototype }} */
      [].__proto__ === Array.prototype;
    } catch (e2) {
      if (!e2 || typeof e2 !== "object" || !("code" in e2) || e2.code !== "ERR_PROTO_ACCESS") {
        throw e2;
      }
    }
    var desc = !!hasProtoAccessor && gOPD2 && gOPD2(
      Object.prototype,
      /** @type {keyof typeof Object.prototype} */
      "__proto__"
    );
    var $Object = Object;
    var $getPrototypeOf = $Object.getPrototypeOf;
    get = desc && typeof desc.get === "function" ? callBind([desc.get]) : typeof $getPrototypeOf === "function" ? (
      /** @type {import('./get')} */
      function getDunder(value) {
        return $getPrototypeOf(value == null ? value : $Object(value));
      }
    ) : false;
    return get;
  }
  var getProto;
  var hasRequiredGetProto;
  function requireGetProto() {
    if (hasRequiredGetProto) return getProto;
    hasRequiredGetProto = 1;
    var reflectGetProto = requireReflect_getPrototypeOf();
    var originalGetProto = requireObject_getPrototypeOf();
    var getDunderProto = /* @__PURE__ */ requireGet();
    getProto = reflectGetProto ? function getProto2(O2) {
      return reflectGetProto(O2);
    } : originalGetProto ? function getProto2(O2) {
      if (!O2 || typeof O2 !== "object" && typeof O2 !== "function") {
        throw new TypeError("getProto: not an object");
      }
      return originalGetProto(O2);
    } : getDunderProto ? function getProto2(O2) {
      return getDunderProto(O2);
    } : null;
    return getProto;
  }
  var hasown;
  var hasRequiredHasown;
  function requireHasown() {
    if (hasRequiredHasown) return hasown;
    hasRequiredHasown = 1;
    var call = Function.prototype.call;
    var $hasOwn = Object.prototype.hasOwnProperty;
    var bind = requireFunctionBind();
    hasown = bind.call(call, $hasOwn);
    return hasown;
  }
  var getIntrinsic;
  var hasRequiredGetIntrinsic;
  function requireGetIntrinsic() {
    if (hasRequiredGetIntrinsic) return getIntrinsic;
    hasRequiredGetIntrinsic = 1;
    var undefined$1;
    var $Object = /* @__PURE__ */ requireEsObjectAtoms();
    var $Error = /* @__PURE__ */ requireEsErrors();
    var $EvalError = /* @__PURE__ */ require_eval();
    var $RangeError = /* @__PURE__ */ requireRange();
    var $ReferenceError = /* @__PURE__ */ requireRef();
    var $SyntaxError = /* @__PURE__ */ requireSyntax();
    var $TypeError = /* @__PURE__ */ requireType();
    var $URIError = /* @__PURE__ */ requireUri();
    var abs2 = /* @__PURE__ */ requireAbs();
    var floor2 = /* @__PURE__ */ requireFloor();
    var max2 = /* @__PURE__ */ requireMax();
    var min2 = /* @__PURE__ */ requireMin();
    var pow2 = /* @__PURE__ */ requirePow();
    var round2 = /* @__PURE__ */ requireRound();
    var sign2 = /* @__PURE__ */ requireSign();
    var $Function = Function;
    var getEvalledConstructor = function(expressionSyntax) {
      try {
        return $Function('"use strict"; return (' + expressionSyntax + ").constructor;")();
      } catch (e2) {
      }
    };
    var $gOPD = /* @__PURE__ */ requireGopd();
    var $defineProperty = /* @__PURE__ */ requireEsDefineProperty();
    var throwTypeError = function() {
      throw new $TypeError();
    };
    var ThrowTypeError = $gOPD ? (function() {
      try {
        arguments.callee;
        return throwTypeError;
      } catch (calleeThrows) {
        try {
          return $gOPD(arguments, "callee").get;
        } catch (gOPDthrows) {
          return throwTypeError;
        }
      }
    })() : throwTypeError;
    var hasSymbols2 = requireHasSymbols()();
    var getProto2 = requireGetProto();
    var $ObjectGPO = requireObject_getPrototypeOf();
    var $ReflectGPO = requireReflect_getPrototypeOf();
    var $apply = requireFunctionApply();
    var $call = requireFunctionCall();
    var needsEval = {};
    var TypedArray = typeof Uint8Array === "undefined" || !getProto2 ? undefined$1 : getProto2(Uint8Array);
    var INTRINSICS = {
      __proto__: null,
      "%AggregateError%": typeof AggregateError === "undefined" ? undefined$1 : AggregateError,
      "%Array%": Array,
      "%ArrayBuffer%": typeof ArrayBuffer === "undefined" ? undefined$1 : ArrayBuffer,
      "%ArrayIteratorPrototype%": hasSymbols2 && getProto2 ? getProto2([][Symbol.iterator]()) : undefined$1,
      "%AsyncFromSyncIteratorPrototype%": undefined$1,
      "%AsyncFunction%": needsEval,
      "%AsyncGenerator%": needsEval,
      "%AsyncGeneratorFunction%": needsEval,
      "%AsyncIteratorPrototype%": needsEval,
      "%Atomics%": typeof Atomics === "undefined" ? undefined$1 : Atomics,
      "%BigInt%": typeof BigInt === "undefined" ? undefined$1 : BigInt,
      "%BigInt64Array%": typeof BigInt64Array === "undefined" ? undefined$1 : BigInt64Array,
      "%BigUint64Array%": typeof BigUint64Array === "undefined" ? undefined$1 : BigUint64Array,
      "%Boolean%": Boolean,
      "%DataView%": typeof DataView === "undefined" ? undefined$1 : DataView,
      "%Date%": Date,
      "%decodeURI%": decodeURI,
      "%decodeURIComponent%": decodeURIComponent,
      "%encodeURI%": encodeURI,
      "%encodeURIComponent%": encodeURIComponent,
      "%Error%": $Error,
      "%eval%": eval,
      // eslint-disable-line no-eval
      "%EvalError%": $EvalError,
      "%Float16Array%": typeof Float16Array === "undefined" ? undefined$1 : Float16Array,
      "%Float32Array%": typeof Float32Array === "undefined" ? undefined$1 : Float32Array,
      "%Float64Array%": typeof Float64Array === "undefined" ? undefined$1 : Float64Array,
      "%FinalizationRegistry%": typeof FinalizationRegistry === "undefined" ? undefined$1 : FinalizationRegistry,
      "%Function%": $Function,
      "%GeneratorFunction%": needsEval,
      "%Int8Array%": typeof Int8Array === "undefined" ? undefined$1 : Int8Array,
      "%Int16Array%": typeof Int16Array === "undefined" ? undefined$1 : Int16Array,
      "%Int32Array%": typeof Int32Array === "undefined" ? undefined$1 : Int32Array,
      "%isFinite%": isFinite,
      "%isNaN%": isNaN,
      "%IteratorPrototype%": hasSymbols2 && getProto2 ? getProto2(getProto2([][Symbol.iterator]())) : undefined$1,
      "%JSON%": typeof JSON === "object" ? JSON : undefined$1,
      "%Map%": typeof Map === "undefined" ? undefined$1 : Map,
      "%MapIteratorPrototype%": typeof Map === "undefined" || !hasSymbols2 || !getProto2 ? undefined$1 : getProto2((/* @__PURE__ */ new Map())[Symbol.iterator]()),
      "%Math%": Math,
      "%Number%": Number,
      "%Object%": $Object,
      "%Object.getOwnPropertyDescriptor%": $gOPD,
      "%parseFloat%": parseFloat,
      "%parseInt%": parseInt,
      "%Promise%": typeof Promise === "undefined" ? undefined$1 : Promise,
      "%Proxy%": typeof Proxy === "undefined" ? undefined$1 : Proxy,
      "%RangeError%": $RangeError,
      "%ReferenceError%": $ReferenceError,
      "%Reflect%": typeof Reflect === "undefined" ? undefined$1 : Reflect,
      "%RegExp%": RegExp,
      "%Set%": typeof Set === "undefined" ? undefined$1 : Set,
      "%SetIteratorPrototype%": typeof Set === "undefined" || !hasSymbols2 || !getProto2 ? undefined$1 : getProto2((/* @__PURE__ */ new Set())[Symbol.iterator]()),
      "%SharedArrayBuffer%": typeof SharedArrayBuffer === "undefined" ? undefined$1 : SharedArrayBuffer,
      "%String%": String,
      "%StringIteratorPrototype%": hasSymbols2 && getProto2 ? getProto2(""[Symbol.iterator]()) : undefined$1,
      "%Symbol%": hasSymbols2 ? Symbol : undefined$1,
      "%SyntaxError%": $SyntaxError,
      "%ThrowTypeError%": ThrowTypeError,
      "%TypedArray%": TypedArray,
      "%TypeError%": $TypeError,
      "%Uint8Array%": typeof Uint8Array === "undefined" ? undefined$1 : Uint8Array,
      "%Uint8ClampedArray%": typeof Uint8ClampedArray === "undefined" ? undefined$1 : Uint8ClampedArray,
      "%Uint16Array%": typeof Uint16Array === "undefined" ? undefined$1 : Uint16Array,
      "%Uint32Array%": typeof Uint32Array === "undefined" ? undefined$1 : Uint32Array,
      "%URIError%": $URIError,
      "%WeakMap%": typeof WeakMap === "undefined" ? undefined$1 : WeakMap,
      "%WeakRef%": typeof WeakRef === "undefined" ? undefined$1 : WeakRef,
      "%WeakSet%": typeof WeakSet === "undefined" ? undefined$1 : WeakSet,
      "%Function.prototype.call%": $call,
      "%Function.prototype.apply%": $apply,
      "%Object.defineProperty%": $defineProperty,
      "%Object.getPrototypeOf%": $ObjectGPO,
      "%Math.abs%": abs2,
      "%Math.floor%": floor2,
      "%Math.max%": max2,
      "%Math.min%": min2,
      "%Math.pow%": pow2,
      "%Math.round%": round2,
      "%Math.sign%": sign2,
      "%Reflect.getPrototypeOf%": $ReflectGPO
    };
    if (getProto2) {
      try {
        null.error;
      } catch (e2) {
        var errorProto = getProto2(getProto2(e2));
        INTRINSICS["%Error.prototype%"] = errorProto;
      }
    }
    var doEval = function doEval2(name) {
      var value;
      if (name === "%AsyncFunction%") {
        value = getEvalledConstructor("async function () {}");
      } else if (name === "%GeneratorFunction%") {
        value = getEvalledConstructor("function* () {}");
      } else if (name === "%AsyncGeneratorFunction%") {
        value = getEvalledConstructor("async function* () {}");
      } else if (name === "%AsyncGenerator%") {
        var fn = doEval2("%AsyncGeneratorFunction%");
        if (fn) {
          value = fn.prototype;
        }
      } else if (name === "%AsyncIteratorPrototype%") {
        var gen = doEval2("%AsyncGenerator%");
        if (gen && getProto2) {
          value = getProto2(gen.prototype);
        }
      }
      INTRINSICS[name] = value;
      return value;
    };
    var LEGACY_ALIASES = {
      __proto__: null,
      "%ArrayBufferPrototype%": ["ArrayBuffer", "prototype"],
      "%ArrayPrototype%": ["Array", "prototype"],
      "%ArrayProto_entries%": ["Array", "prototype", "entries"],
      "%ArrayProto_forEach%": ["Array", "prototype", "forEach"],
      "%ArrayProto_keys%": ["Array", "prototype", "keys"],
      "%ArrayProto_values%": ["Array", "prototype", "values"],
      "%AsyncFunctionPrototype%": ["AsyncFunction", "prototype"],
      "%AsyncGenerator%": ["AsyncGeneratorFunction", "prototype"],
      "%AsyncGeneratorPrototype%": ["AsyncGeneratorFunction", "prototype", "prototype"],
      "%BooleanPrototype%": ["Boolean", "prototype"],
      "%DataViewPrototype%": ["DataView", "prototype"],
      "%DatePrototype%": ["Date", "prototype"],
      "%ErrorPrototype%": ["Error", "prototype"],
      "%EvalErrorPrototype%": ["EvalError", "prototype"],
      "%Float32ArrayPrototype%": ["Float32Array", "prototype"],
      "%Float64ArrayPrototype%": ["Float64Array", "prototype"],
      "%FunctionPrototype%": ["Function", "prototype"],
      "%Generator%": ["GeneratorFunction", "prototype"],
      "%GeneratorPrototype%": ["GeneratorFunction", "prototype", "prototype"],
      "%Int8ArrayPrototype%": ["Int8Array", "prototype"],
      "%Int16ArrayPrototype%": ["Int16Array", "prototype"],
      "%Int32ArrayPrototype%": ["Int32Array", "prototype"],
      "%JSONParse%": ["JSON", "parse"],
      "%JSONStringify%": ["JSON", "stringify"],
      "%MapPrototype%": ["Map", "prototype"],
      "%NumberPrototype%": ["Number", "prototype"],
      "%ObjectPrototype%": ["Object", "prototype"],
      "%ObjProto_toString%": ["Object", "prototype", "toString"],
      "%ObjProto_valueOf%": ["Object", "prototype", "valueOf"],
      "%PromisePrototype%": ["Promise", "prototype"],
      "%PromiseProto_then%": ["Promise", "prototype", "then"],
      "%Promise_all%": ["Promise", "all"],
      "%Promise_reject%": ["Promise", "reject"],
      "%Promise_resolve%": ["Promise", "resolve"],
      "%RangeErrorPrototype%": ["RangeError", "prototype"],
      "%ReferenceErrorPrototype%": ["ReferenceError", "prototype"],
      "%RegExpPrototype%": ["RegExp", "prototype"],
      "%SetPrototype%": ["Set", "prototype"],
      "%SharedArrayBufferPrototype%": ["SharedArrayBuffer", "prototype"],
      "%StringPrototype%": ["String", "prototype"],
      "%SymbolPrototype%": ["Symbol", "prototype"],
      "%SyntaxErrorPrototype%": ["SyntaxError", "prototype"],
      "%TypedArrayPrototype%": ["TypedArray", "prototype"],
      "%TypeErrorPrototype%": ["TypeError", "prototype"],
      "%Uint8ArrayPrototype%": ["Uint8Array", "prototype"],
      "%Uint8ClampedArrayPrototype%": ["Uint8ClampedArray", "prototype"],
      "%Uint16ArrayPrototype%": ["Uint16Array", "prototype"],
      "%Uint32ArrayPrototype%": ["Uint32Array", "prototype"],
      "%URIErrorPrototype%": ["URIError", "prototype"],
      "%WeakMapPrototype%": ["WeakMap", "prototype"],
      "%WeakSetPrototype%": ["WeakSet", "prototype"]
    };
    var bind = requireFunctionBind();
    var hasOwn = /* @__PURE__ */ requireHasown();
    var $concat = bind.call($call, Array.prototype.concat);
    var $spliceApply = bind.call($apply, Array.prototype.splice);
    var $replace = bind.call($call, String.prototype.replace);
    var $strSlice = bind.call($call, String.prototype.slice);
    var $exec = bind.call($call, RegExp.prototype.exec);
    var rePropName = /[^%.[\]]+|\[(?:(-?\d+(?:\.\d+)?)|(["'])((?:(?!\2)[^\\]|\\.)*?)\2)\]|(?=(?:\.|\[\])(?:\.|\[\]|%$))/g;
    var reEscapeChar = /\\(\\)?/g;
    var stringToPath = function stringToPath2(string) {
      var first = $strSlice(string, 0, 1);
      var last = $strSlice(string, -1);
      if (first === "%" && last !== "%") {
        throw new $SyntaxError("invalid intrinsic syntax, expected closing `%`");
      } else if (last === "%" && first !== "%") {
        throw new $SyntaxError("invalid intrinsic syntax, expected opening `%`");
      }
      var result = [];
      $replace(string, rePropName, function(match, number, quote, subString) {
        result[result.length] = quote ? $replace(subString, reEscapeChar, "$1") : number || match;
      });
      return result;
    };
    var getBaseIntrinsic = function getBaseIntrinsic2(name, allowMissing) {
      var intrinsicName = name;
      var alias;
      if (hasOwn(LEGACY_ALIASES, intrinsicName)) {
        alias = LEGACY_ALIASES[intrinsicName];
        intrinsicName = "%" + alias[0] + "%";
      }
      if (hasOwn(INTRINSICS, intrinsicName)) {
        var value = INTRINSICS[intrinsicName];
        if (value === needsEval) {
          value = doEval(intrinsicName);
        }
        if (typeof value === "undefined" && !allowMissing) {
          throw new $TypeError("intrinsic " + name + " exists, but is not available. Please file an issue!");
        }
        return {
          alias,
          name: intrinsicName,
          value
        };
      }
      throw new $SyntaxError("intrinsic " + name + " does not exist!");
    };
    getIntrinsic = function GetIntrinsic(name, allowMissing) {
      if (typeof name !== "string" || name.length === 0) {
        throw new $TypeError("intrinsic name must be a non-empty string");
      }
      if (arguments.length > 1 && typeof allowMissing !== "boolean") {
        throw new $TypeError('"allowMissing" argument must be a boolean');
      }
      if ($exec(/^%?[^%]*%?$/, name) === null) {
        throw new $SyntaxError("`%` may not be present anywhere but at the beginning and end of the intrinsic name");
      }
      var parts = stringToPath(name);
      var intrinsicBaseName = parts.length > 0 ? parts[0] : "";
      var intrinsic = getBaseIntrinsic("%" + intrinsicBaseName + "%", allowMissing);
      var intrinsicRealName = intrinsic.name;
      var value = intrinsic.value;
      var skipFurtherCaching = false;
      var alias = intrinsic.alias;
      if (alias) {
        intrinsicBaseName = alias[0];
        $spliceApply(parts, $concat([0, 1], alias));
      }
      for (var i2 = 1, isOwn = true; i2 < parts.length; i2 += 1) {
        var part = parts[i2];
        var first = $strSlice(part, 0, 1);
        var last = $strSlice(part, -1);
        if ((first === '"' || first === "'" || first === "`" || (last === '"' || last === "'" || last === "`")) && first !== last) {
          throw new $SyntaxError("property names with quotes must have matching quotes");
        }
        if (part === "constructor" || !isOwn) {
          skipFurtherCaching = true;
        }
        intrinsicBaseName += "." + part;
        intrinsicRealName = "%" + intrinsicBaseName + "%";
        if (hasOwn(INTRINSICS, intrinsicRealName)) {
          value = INTRINSICS[intrinsicRealName];
        } else if (value != null) {
          if (!(part in value)) {
            if (!allowMissing) {
              throw new $TypeError("base intrinsic for " + name + " exists, but the property is not available.");
            }
            return void undefined$1;
          }
          if ($gOPD && i2 + 1 >= parts.length) {
            var desc = $gOPD(value, part);
            isOwn = !!desc;
            if (isOwn && "get" in desc && !("originalValue" in desc.get)) {
              value = desc.get;
            } else {
              value = value[part];
            }
          } else {
            isOwn = hasOwn(value, part);
            value = value[part];
          }
          if (isOwn && !skipFurtherCaching) {
            INTRINSICS[intrinsicRealName] = value;
          }
        }
      }
      return value;
    };
    return getIntrinsic;
  }
  var callBound;
  var hasRequiredCallBound;
  function requireCallBound() {
    if (hasRequiredCallBound) return callBound;
    hasRequiredCallBound = 1;
    var GetIntrinsic = /* @__PURE__ */ requireGetIntrinsic();
    var callBindBasic = requireCallBindApplyHelpers();
    var $indexOf = callBindBasic([GetIntrinsic("%String.prototype.indexOf%")]);
    callBound = function callBoundIntrinsic(name, allowMissing) {
      var intrinsic = (
        /** @type {(this: unknown, ...args: unknown[]) => unknown} */
        GetIntrinsic(name, !!allowMissing)
      );
      if (typeof intrinsic === "function" && $indexOf(name, ".prototype.") > -1) {
        return callBindBasic(
          /** @type {const} */
          [intrinsic]
        );
      }
      return intrinsic;
    };
    return callBound;
  }
  var sideChannelMap;
  var hasRequiredSideChannelMap;
  function requireSideChannelMap() {
    if (hasRequiredSideChannelMap) return sideChannelMap;
    hasRequiredSideChannelMap = 1;
    var GetIntrinsic = /* @__PURE__ */ requireGetIntrinsic();
    var callBound2 = /* @__PURE__ */ requireCallBound();
    var inspect = /* @__PURE__ */ requireObjectInspect();
    var $TypeError = /* @__PURE__ */ requireType();
    var $Map = GetIntrinsic("%Map%", true);
    var $mapGet = callBound2("Map.prototype.get", true);
    var $mapSet = callBound2("Map.prototype.set", true);
    var $mapHas = callBound2("Map.prototype.has", true);
    var $mapDelete = callBound2("Map.prototype.delete", true);
    var $mapSize = callBound2("Map.prototype.size", true);
    sideChannelMap = !!$Map && /** @type {Exclude<import('.'), false>} */
    function getSideChannelMap() {
      var $m;
      var channel = {
        assert: function(key) {
          if (!channel.has(key)) {
            throw new $TypeError("Side channel does not contain " + inspect(key));
          }
        },
        "delete": function(key) {
          if ($m) {
            var result = $mapDelete($m, key);
            if ($mapSize($m) === 0) {
              $m = void 0;
            }
            return result;
          }
          return false;
        },
        get: function(key) {
          if ($m) {
            return $mapGet($m, key);
          }
        },
        has: function(key) {
          if ($m) {
            return $mapHas($m, key);
          }
          return false;
        },
        set: function(key, value) {
          if (!$m) {
            $m = new $Map();
          }
          $mapSet($m, key, value);
        }
      };
      return channel;
    };
    return sideChannelMap;
  }
  var sideChannelWeakmap;
  var hasRequiredSideChannelWeakmap;
  function requireSideChannelWeakmap() {
    if (hasRequiredSideChannelWeakmap) return sideChannelWeakmap;
    hasRequiredSideChannelWeakmap = 1;
    var GetIntrinsic = /* @__PURE__ */ requireGetIntrinsic();
    var callBound2 = /* @__PURE__ */ requireCallBound();
    var inspect = /* @__PURE__ */ requireObjectInspect();
    var getSideChannelMap = requireSideChannelMap();
    var $TypeError = /* @__PURE__ */ requireType();
    var $WeakMap = GetIntrinsic("%WeakMap%", true);
    var $weakMapGet = callBound2("WeakMap.prototype.get", true);
    var $weakMapSet = callBound2("WeakMap.prototype.set", true);
    var $weakMapHas = callBound2("WeakMap.prototype.has", true);
    var $weakMapDelete = callBound2("WeakMap.prototype.delete", true);
    sideChannelWeakmap = $WeakMap ? (
      /** @type {Exclude<import('.'), false>} */
      function getSideChannelWeakMap() {
        var $wm;
        var $m;
        var channel = {
          assert: function(key) {
            if (!channel.has(key)) {
              throw new $TypeError("Side channel does not contain " + inspect(key));
            }
          },
          "delete": function(key) {
            if ($WeakMap && key && (typeof key === "object" || typeof key === "function")) {
              if ($wm) {
                return $weakMapDelete($wm, key);
              }
            } else if (getSideChannelMap) {
              if ($m) {
                return $m["delete"](key);
              }
            }
            return false;
          },
          get: function(key) {
            if ($WeakMap && key && (typeof key === "object" || typeof key === "function")) {
              if ($wm) {
                return $weakMapGet($wm, key);
              }
            }
            return $m && $m.get(key);
          },
          has: function(key) {
            if ($WeakMap && key && (typeof key === "object" || typeof key === "function")) {
              if ($wm) {
                return $weakMapHas($wm, key);
              }
            }
            return !!$m && $m.has(key);
          },
          set: function(key, value) {
            if ($WeakMap && key && (typeof key === "object" || typeof key === "function")) {
              if (!$wm) {
                $wm = new $WeakMap();
              }
              $weakMapSet($wm, key, value);
            } else if (getSideChannelMap) {
              if (!$m) {
                $m = getSideChannelMap();
              }
              $m.set(key, value);
            }
          }
        };
        return channel;
      }
    ) : getSideChannelMap;
    return sideChannelWeakmap;
  }
  var sideChannel;
  var hasRequiredSideChannel;
  function requireSideChannel() {
    if (hasRequiredSideChannel) return sideChannel;
    hasRequiredSideChannel = 1;
    var $TypeError = /* @__PURE__ */ requireType();
    var inspect = /* @__PURE__ */ requireObjectInspect();
    var getSideChannelList = requireSideChannelList();
    var getSideChannelMap = requireSideChannelMap();
    var getSideChannelWeakMap = requireSideChannelWeakmap();
    var makeChannel = getSideChannelWeakMap || getSideChannelMap || getSideChannelList;
    sideChannel = function getSideChannel() {
      var $channelData;
      var channel = {
        assert: function(key) {
          if (!channel.has(key)) {
            throw new $TypeError("Side channel does not contain " + inspect(key));
          }
        },
        "delete": function(key) {
          return !!$channelData && $channelData["delete"](key);
        },
        get: function(key) {
          return $channelData && $channelData.get(key);
        },
        has: function(key) {
          return !!$channelData && $channelData.has(key);
        },
        set: function(key, value) {
          if (!$channelData) {
            $channelData = makeChannel();
          }
          $channelData.set(key, value);
        }
      };
      return channel;
    };
    return sideChannel;
  }
  var formats;
  var hasRequiredFormats;
  function requireFormats() {
    if (hasRequiredFormats) return formats;
    hasRequiredFormats = 1;
    var replace = String.prototype.replace;
    var percentTwenties = /%20/g;
    var Format = {
      RFC1738: "RFC1738",
      RFC3986: "RFC3986"
    };
    formats = {
      "default": Format.RFC3986,
      formatters: {
        RFC1738: function(value) {
          return replace.call(value, percentTwenties, "+");
        },
        RFC3986: function(value) {
          return String(value);
        }
      },
      RFC1738: Format.RFC1738,
      RFC3986: Format.RFC3986
    };
    return formats;
  }
  var utils;
  var hasRequiredUtils;
  function requireUtils() {
    if (hasRequiredUtils) return utils;
    hasRequiredUtils = 1;
    var formats2 = /* @__PURE__ */ requireFormats();
    var getSideChannel = requireSideChannel();
    var has = Object.prototype.hasOwnProperty;
    var isArray = Array.isArray;
    var overflowChannel = getSideChannel();
    var markOverflow = function markOverflow2(obj, maxIndex) {
      overflowChannel.set(obj, maxIndex);
      return obj;
    };
    var isOverflow = function isOverflow2(obj) {
      return overflowChannel.has(obj);
    };
    var getMaxIndex = function getMaxIndex2(obj) {
      return overflowChannel.get(obj);
    };
    var setMaxIndex = function setMaxIndex2(obj, maxIndex) {
      overflowChannel.set(obj, maxIndex);
    };
    var hexTable = (function() {
      var array = [];
      for (var i2 = 0; i2 < 256; ++i2) {
        array[array.length] = "%" + ((i2 < 16 ? "0" : "") + i2.toString(16)).toUpperCase();
      }
      return array;
    })();
    var compactQueue = function compactQueue2(queue) {
      while (queue.length > 1) {
        var item = queue.pop();
        var obj = item.obj[item.prop];
        if (isArray(obj)) {
          var compacted = [];
          for (var j2 = 0; j2 < obj.length; ++j2) {
            if (typeof obj[j2] !== "undefined") {
              compacted[compacted.length] = obj[j2];
            }
          }
          item.obj[item.prop] = compacted;
        }
      }
    };
    var arrayToObject = function arrayToObject2(source, options) {
      var obj = options && options.plainObjects ? { __proto__: null } : {};
      for (var i2 = 0; i2 < source.length; ++i2) {
        if (typeof source[i2] !== "undefined") {
          obj[i2] = source[i2];
        }
      }
      return obj;
    };
    var merge = function merge2(target, source, options) {
      if (!source) {
        return target;
      }
      if (typeof source !== "object" && typeof source !== "function") {
        if (isArray(target)) {
          var nextIndex = target.length;
          if (options && typeof options.arrayLimit === "number" && nextIndex > options.arrayLimit) {
            return markOverflow(arrayToObject(target.concat(source), options), nextIndex);
          }
          target[nextIndex] = source;
        } else if (target && typeof target === "object") {
          if (isOverflow(target)) {
            var newIndex = getMaxIndex(target) + 1;
            target[newIndex] = source;
            setMaxIndex(target, newIndex);
          } else if (options && options.strictMerge) {
            return [target, source];
          } else if (options && (options.plainObjects || options.allowPrototypes) || !has.call(Object.prototype, source)) {
            target[source] = true;
          }
        } else {
          return [target, source];
        }
        return target;
      }
      if (!target || typeof target !== "object") {
        if (isOverflow(source)) {
          var sourceKeys = Object.keys(source);
          var result = options && options.plainObjects ? { __proto__: null, 0: target } : { 0: target };
          for (var m2 = 0; m2 < sourceKeys.length; m2++) {
            var oldKey = parseInt(sourceKeys[m2], 10);
            result[oldKey + 1] = source[sourceKeys[m2]];
          }
          return markOverflow(result, getMaxIndex(source) + 1);
        }
        var combined = [target].concat(source);
        if (options && typeof options.arrayLimit === "number" && combined.length > options.arrayLimit) {
          return markOverflow(arrayToObject(combined, options), combined.length - 1);
        }
        return combined;
      }
      var mergeTarget = target;
      if (isArray(target) && !isArray(source)) {
        mergeTarget = arrayToObject(target, options);
      }
      if (isArray(target) && isArray(source)) {
        source.forEach(function(item, i2) {
          if (has.call(target, i2)) {
            var targetItem = target[i2];
            if (targetItem && typeof targetItem === "object" && item && typeof item === "object") {
              target[i2] = merge2(targetItem, item, options);
            } else {
              target[target.length] = item;
            }
          } else {
            target[i2] = item;
          }
        });
        return target;
      }
      return Object.keys(source).reduce(function(acc, key) {
        var value = source[key];
        if (has.call(acc, key)) {
          acc[key] = merge2(acc[key], value, options);
        } else {
          acc[key] = value;
        }
        if (isOverflow(source) && !isOverflow(acc)) {
          markOverflow(acc, getMaxIndex(source));
        }
        if (isOverflow(acc)) {
          var keyNum = parseInt(key, 10);
          if (String(keyNum) === key && keyNum >= 0 && keyNum > getMaxIndex(acc)) {
            setMaxIndex(acc, keyNum);
          }
        }
        return acc;
      }, mergeTarget);
    };
    var assign = function assignSingleSource(target, source) {
      return Object.keys(source).reduce(function(acc, key) {
        acc[key] = source[key];
        return acc;
      }, target);
    };
    var decode = function(str, defaultDecoder, charset) {
      var strWithoutPlus = str.replace(/\+/g, " ");
      if (charset === "iso-8859-1") {
        return strWithoutPlus.replace(/%[0-9a-f]{2}/gi, unescape);
      }
      try {
        return decodeURIComponent(strWithoutPlus);
      } catch (e2) {
        return strWithoutPlus;
      }
    };
    var limit = 1024;
    var encode = function encode2(str, defaultEncoder, charset, kind, format) {
      if (str.length === 0) {
        return str;
      }
      var string = str;
      if (typeof str === "symbol") {
        string = Symbol.prototype.toString.call(str);
      } else if (typeof str !== "string") {
        string = String(str);
      }
      if (charset === "iso-8859-1") {
        return escape(string).replace(/%u[0-9a-f]{4}/gi, function($0) {
          return "%26%23" + parseInt($0.slice(2), 16) + "%3B";
        });
      }
      var out = "";
      for (var j2 = 0; j2 < string.length; j2 += limit) {
        var segment = string.length >= limit ? string.slice(j2, j2 + limit) : string;
        var arr = [];
        for (var i2 = 0; i2 < segment.length; ++i2) {
          var c2 = segment.charCodeAt(i2);
          if (c2 === 45 || c2 === 46 || c2 === 95 || c2 === 126 || c2 >= 48 && c2 <= 57 || c2 >= 65 && c2 <= 90 || c2 >= 97 && c2 <= 122 || format === formats2.RFC1738 && (c2 === 40 || c2 === 41)) {
            arr[arr.length] = segment.charAt(i2);
            continue;
          }
          if (c2 < 128) {
            arr[arr.length] = hexTable[c2];
            continue;
          }
          if (c2 < 2048) {
            arr[arr.length] = hexTable[192 | c2 >> 6] + hexTable[128 | c2 & 63];
            continue;
          }
          if (c2 < 55296 || c2 >= 57344) {
            arr[arr.length] = hexTable[224 | c2 >> 12] + hexTable[128 | c2 >> 6 & 63] + hexTable[128 | c2 & 63];
            continue;
          }
          i2 += 1;
          c2 = 65536 + ((c2 & 1023) << 10 | segment.charCodeAt(i2) & 1023);
          arr[arr.length] = hexTable[240 | c2 >> 18] + hexTable[128 | c2 >> 12 & 63] + hexTable[128 | c2 >> 6 & 63] + hexTable[128 | c2 & 63];
        }
        out += arr.join("");
      }
      return out;
    };
    var compact = function compact2(value) {
      var queue = [{ obj: { o: value }, prop: "o" }];
      var refs = [];
      for (var i2 = 0; i2 < queue.length; ++i2) {
        var item = queue[i2];
        var obj = item.obj[item.prop];
        var keys = Object.keys(obj);
        for (var j2 = 0; j2 < keys.length; ++j2) {
          var key = keys[j2];
          var val = obj[key];
          if (typeof val === "object" && val !== null && refs.indexOf(val) === -1) {
            queue[queue.length] = { obj, prop: key };
            refs[refs.length] = val;
          }
        }
      }
      compactQueue(queue);
      return value;
    };
    var isRegExp = function isRegExp2(obj) {
      return Object.prototype.toString.call(obj) === "[object RegExp]";
    };
    var isBuffer = function isBuffer2(obj) {
      if (!obj || typeof obj !== "object") {
        return false;
      }
      return !!(obj.constructor && obj.constructor.isBuffer && obj.constructor.isBuffer(obj));
    };
    var combine = function combine2(a2, b2, arrayLimit, plainObjects) {
      if (isOverflow(a2)) {
        var newIndex = getMaxIndex(a2) + 1;
        a2[newIndex] = b2;
        setMaxIndex(a2, newIndex);
        return a2;
      }
      var result = [].concat(a2, b2);
      if (result.length > arrayLimit) {
        return markOverflow(arrayToObject(result, { plainObjects }), result.length - 1);
      }
      return result;
    };
    var maybeMap = function maybeMap2(val, fn) {
      if (isArray(val)) {
        var mapped = [];
        for (var i2 = 0; i2 < val.length; i2 += 1) {
          mapped[mapped.length] = fn(val[i2]);
        }
        return mapped;
      }
      return fn(val);
    };
    utils = {
      arrayToObject,
      assign,
      combine,
      compact,
      decode,
      encode,
      isBuffer,
      isOverflow,
      isRegExp,
      markOverflow,
      maybeMap,
      merge
    };
    return utils;
  }
  var stringify_1;
  var hasRequiredStringify;
  function requireStringify() {
    if (hasRequiredStringify) return stringify_1;
    hasRequiredStringify = 1;
    var getSideChannel = requireSideChannel();
    var utils2 = /* @__PURE__ */ requireUtils();
    var formats2 = /* @__PURE__ */ requireFormats();
    var has = Object.prototype.hasOwnProperty;
    var arrayPrefixGenerators = {
      brackets: function brackets(prefix) {
        return prefix + "[]";
      },
      comma: "comma",
      indices: function indices2(prefix, key) {
        return prefix + "[" + key + "]";
      },
      repeat: function repeat(prefix) {
        return prefix;
      }
    };
    var isArray = Array.isArray;
    var push = Array.prototype.push;
    var pushToArray = function(arr, valueOrArray) {
      push.apply(arr, isArray(valueOrArray) ? valueOrArray : [valueOrArray]);
    };
    var toISO = Date.prototype.toISOString;
    var defaultFormat = formats2["default"];
    var defaults = {
      addQueryPrefix: false,
      allowDots: false,
      allowEmptyArrays: false,
      arrayFormat: "indices",
      charset: "utf-8",
      charsetSentinel: false,
      commaRoundTrip: false,
      delimiter: "&",
      encode: true,
      encodeDotInKeys: false,
      encoder: utils2.encode,
      encodeValuesOnly: false,
      filter: void 0,
      format: defaultFormat,
      formatter: formats2.formatters[defaultFormat],
      // deprecated
      indices: false,
      serializeDate: function serializeDate(date) {
        return toISO.call(date);
      },
      skipNulls: false,
      strictNullHandling: false
    };
    var isNonNullishPrimitive = function isNonNullishPrimitive2(v2) {
      return typeof v2 === "string" || typeof v2 === "number" || typeof v2 === "boolean" || typeof v2 === "symbol" || typeof v2 === "bigint";
    };
    var sentinel = {};
    var stringify = function stringify2(object, prefix, generateArrayPrefix, commaRoundTrip, allowEmptyArrays, strictNullHandling, skipNulls, encodeDotInKeys, encoder, filter, sort, allowDots, serializeDate, format, formatter, encodeValuesOnly, charset, sideChannel2) {
      var obj = object;
      var tmpSc = sideChannel2;
      var step = 0;
      var findFlag = false;
      while ((tmpSc = tmpSc.get(sentinel)) !== void 0 && !findFlag) {
        var pos = tmpSc.get(object);
        step += 1;
        if (typeof pos !== "undefined") {
          if (pos === step) {
            throw new RangeError("Cyclic object value");
          } else {
            findFlag = true;
          }
        }
        if (typeof tmpSc.get(sentinel) === "undefined") {
          step = 0;
        }
      }
      if (typeof filter === "function") {
        obj = filter(prefix, obj);
      } else if (obj instanceof Date) {
        obj = serializeDate(obj);
      } else if (generateArrayPrefix === "comma" && isArray(obj)) {
        obj = utils2.maybeMap(obj, function(value2) {
          if (value2 instanceof Date) {
            return serializeDate(value2);
          }
          return value2;
        });
      }
      if (obj === null) {
        if (strictNullHandling) {
          return encoder && !encodeValuesOnly ? encoder(prefix, defaults.encoder, charset, "key", format) : prefix;
        }
        obj = "";
      }
      if (isNonNullishPrimitive(obj) || utils2.isBuffer(obj)) {
        if (encoder) {
          var keyValue = encodeValuesOnly ? prefix : encoder(prefix, defaults.encoder, charset, "key", format);
          return [formatter(keyValue) + "=" + formatter(encoder(obj, defaults.encoder, charset, "value", format))];
        }
        return [formatter(prefix) + "=" + formatter(String(obj))];
      }
      var values = [];
      if (typeof obj === "undefined") {
        return values;
      }
      var objKeys;
      if (generateArrayPrefix === "comma" && isArray(obj)) {
        if (encodeValuesOnly && encoder) {
          obj = utils2.maybeMap(obj, encoder);
        }
        objKeys = [{ value: obj.length > 0 ? obj.join(",") || null : void 0 }];
      } else if (isArray(filter)) {
        objKeys = filter;
      } else {
        var keys = Object.keys(obj);
        objKeys = sort ? keys.sort(sort) : keys;
      }
      var encodedPrefix = encodeDotInKeys ? String(prefix).replace(/\./g, "%2E") : String(prefix);
      var adjustedPrefix = commaRoundTrip && isArray(obj) && obj.length === 1 ? encodedPrefix + "[]" : encodedPrefix;
      if (allowEmptyArrays && isArray(obj) && obj.length === 0) {
        return adjustedPrefix + "[]";
      }
      for (var j2 = 0; j2 < objKeys.length; ++j2) {
        var key = objKeys[j2];
        var value = typeof key === "object" && key && typeof key.value !== "undefined" ? key.value : obj[key];
        if (skipNulls && value === null) {
          continue;
        }
        var encodedKey = allowDots && encodeDotInKeys ? String(key).replace(/\./g, "%2E") : String(key);
        var keyPrefix = isArray(obj) ? typeof generateArrayPrefix === "function" ? generateArrayPrefix(adjustedPrefix, encodedKey) : adjustedPrefix : adjustedPrefix + (allowDots ? "." + encodedKey : "[" + encodedKey + "]");
        sideChannel2.set(object, step);
        var valueSideChannel = getSideChannel();
        valueSideChannel.set(sentinel, sideChannel2);
        pushToArray(values, stringify2(
          value,
          keyPrefix,
          generateArrayPrefix,
          commaRoundTrip,
          allowEmptyArrays,
          strictNullHandling,
          skipNulls,
          encodeDotInKeys,
          generateArrayPrefix === "comma" && encodeValuesOnly && isArray(obj) ? null : encoder,
          filter,
          sort,
          allowDots,
          serializeDate,
          format,
          formatter,
          encodeValuesOnly,
          charset,
          valueSideChannel
        ));
      }
      return values;
    };
    var normalizeStringifyOptions = function normalizeStringifyOptions2(opts) {
      if (!opts) {
        return defaults;
      }
      if (typeof opts.allowEmptyArrays !== "undefined" && typeof opts.allowEmptyArrays !== "boolean") {
        throw new TypeError("`allowEmptyArrays` option can only be `true` or `false`, when provided");
      }
      if (typeof opts.encodeDotInKeys !== "undefined" && typeof opts.encodeDotInKeys !== "boolean") {
        throw new TypeError("`encodeDotInKeys` option can only be `true` or `false`, when provided");
      }
      if (opts.encoder !== null && typeof opts.encoder !== "undefined" && typeof opts.encoder !== "function") {
        throw new TypeError("Encoder has to be a function.");
      }
      var charset = opts.charset || defaults.charset;
      if (typeof opts.charset !== "undefined" && opts.charset !== "utf-8" && opts.charset !== "iso-8859-1") {
        throw new TypeError("The charset option must be either utf-8, iso-8859-1, or undefined");
      }
      var format = formats2["default"];
      if (typeof opts.format !== "undefined") {
        if (!has.call(formats2.formatters, opts.format)) {
          throw new TypeError("Unknown format option provided.");
        }
        format = opts.format;
      }
      var formatter = formats2.formatters[format];
      var filter = defaults.filter;
      if (typeof opts.filter === "function" || isArray(opts.filter)) {
        filter = opts.filter;
      }
      var arrayFormat;
      if (opts.arrayFormat in arrayPrefixGenerators) {
        arrayFormat = opts.arrayFormat;
      } else if ("indices" in opts) {
        arrayFormat = opts.indices ? "indices" : "repeat";
      } else {
        arrayFormat = defaults.arrayFormat;
      }
      if ("commaRoundTrip" in opts && typeof opts.commaRoundTrip !== "boolean") {
        throw new TypeError("`commaRoundTrip` must be a boolean, or absent");
      }
      var allowDots = typeof opts.allowDots === "undefined" ? opts.encodeDotInKeys === true ? true : defaults.allowDots : !!opts.allowDots;
      return {
        addQueryPrefix: typeof opts.addQueryPrefix === "boolean" ? opts.addQueryPrefix : defaults.addQueryPrefix,
        allowDots,
        allowEmptyArrays: typeof opts.allowEmptyArrays === "boolean" ? !!opts.allowEmptyArrays : defaults.allowEmptyArrays,
        arrayFormat,
        charset,
        charsetSentinel: typeof opts.charsetSentinel === "boolean" ? opts.charsetSentinel : defaults.charsetSentinel,
        commaRoundTrip: !!opts.commaRoundTrip,
        delimiter: typeof opts.delimiter === "undefined" ? defaults.delimiter : opts.delimiter,
        encode: typeof opts.encode === "boolean" ? opts.encode : defaults.encode,
        encodeDotInKeys: typeof opts.encodeDotInKeys === "boolean" ? opts.encodeDotInKeys : defaults.encodeDotInKeys,
        encoder: typeof opts.encoder === "function" ? opts.encoder : defaults.encoder,
        encodeValuesOnly: typeof opts.encodeValuesOnly === "boolean" ? opts.encodeValuesOnly : defaults.encodeValuesOnly,
        filter,
        format,
        formatter,
        serializeDate: typeof opts.serializeDate === "function" ? opts.serializeDate : defaults.serializeDate,
        skipNulls: typeof opts.skipNulls === "boolean" ? opts.skipNulls : defaults.skipNulls,
        sort: typeof opts.sort === "function" ? opts.sort : null,
        strictNullHandling: typeof opts.strictNullHandling === "boolean" ? opts.strictNullHandling : defaults.strictNullHandling
      };
    };
    stringify_1 = function(object, opts) {
      var obj = object;
      var options = normalizeStringifyOptions(opts);
      var objKeys;
      var filter;
      if (typeof options.filter === "function") {
        filter = options.filter;
        obj = filter("", obj);
      } else if (isArray(options.filter)) {
        filter = options.filter;
        objKeys = filter;
      }
      var keys = [];
      if (typeof obj !== "object" || obj === null) {
        return "";
      }
      var generateArrayPrefix = arrayPrefixGenerators[options.arrayFormat];
      var commaRoundTrip = generateArrayPrefix === "comma" && options.commaRoundTrip;
      if (!objKeys) {
        objKeys = Object.keys(obj);
      }
      if (options.sort) {
        objKeys.sort(options.sort);
      }
      var sideChannel2 = getSideChannel();
      for (var i2 = 0; i2 < objKeys.length; ++i2) {
        var key = objKeys[i2];
        var value = obj[key];
        if (options.skipNulls && value === null) {
          continue;
        }
        pushToArray(keys, stringify(
          value,
          key,
          generateArrayPrefix,
          commaRoundTrip,
          options.allowEmptyArrays,
          options.strictNullHandling,
          options.skipNulls,
          options.encodeDotInKeys,
          options.encode ? options.encoder : null,
          options.filter,
          options.sort,
          options.allowDots,
          options.serializeDate,
          options.format,
          options.formatter,
          options.encodeValuesOnly,
          options.charset,
          sideChannel2
        ));
      }
      var joined = keys.join(options.delimiter);
      var prefix = options.addQueryPrefix === true ? "?" : "";
      if (options.charsetSentinel) {
        if (options.charset === "iso-8859-1") {
          prefix += "utf8=%26%2310003%3B&";
        } else {
          prefix += "utf8=%E2%9C%93&";
        }
      }
      return joined.length > 0 ? prefix + joined : "";
    };
    return stringify_1;
  }
  var parse;
  var hasRequiredParse;
  function requireParse() {
    if (hasRequiredParse) return parse;
    hasRequiredParse = 1;
    var utils2 = /* @__PURE__ */ requireUtils();
    var has = Object.prototype.hasOwnProperty;
    var isArray = Array.isArray;
    var defaults = {
      allowDots: false,
      allowEmptyArrays: false,
      allowPrototypes: false,
      allowSparse: false,
      arrayLimit: 20,
      charset: "utf-8",
      charsetSentinel: false,
      comma: false,
      decodeDotInKeys: false,
      decoder: utils2.decode,
      delimiter: "&",
      depth: 5,
      duplicates: "combine",
      ignoreQueryPrefix: false,
      interpretNumericEntities: false,
      parameterLimit: 1e3,
      parseArrays: true,
      plainObjects: false,
      strictDepth: false,
      strictMerge: true,
      strictNullHandling: false,
      throwOnLimitExceeded: false
    };
    var interpretNumericEntities = function(str) {
      return str.replace(/&#(\d+);/g, function($0, numberStr) {
        return String.fromCharCode(parseInt(numberStr, 10));
      });
    };
    var parseArrayValue = function(val, options, currentArrayLength) {
      if (val && typeof val === "string" && options.comma && val.indexOf(",") > -1) {
        return val.split(",");
      }
      if (options.throwOnLimitExceeded && currentArrayLength >= options.arrayLimit) {
        throw new RangeError("Array limit exceeded. Only " + options.arrayLimit + " element" + (options.arrayLimit === 1 ? "" : "s") + " allowed in an array.");
      }
      return val;
    };
    var isoSentinel = "utf8=%26%2310003%3B";
    var charsetSentinel = "utf8=%E2%9C%93";
    var parseValues = function parseQueryStringValues(str, options) {
      var obj = { __proto__: null };
      var cleanStr = options.ignoreQueryPrefix ? str.replace(/^\?/, "") : str;
      cleanStr = cleanStr.replace(/%5B/gi, "[").replace(/%5D/gi, "]");
      var limit = options.parameterLimit === Infinity ? void 0 : options.parameterLimit;
      var parts = cleanStr.split(
        options.delimiter,
        options.throwOnLimitExceeded ? limit + 1 : limit
      );
      if (options.throwOnLimitExceeded && parts.length > limit) {
        throw new RangeError("Parameter limit exceeded. Only " + limit + " parameter" + (limit === 1 ? "" : "s") + " allowed.");
      }
      var skipIndex = -1;
      var i2;
      var charset = options.charset;
      if (options.charsetSentinel) {
        for (i2 = 0; i2 < parts.length; ++i2) {
          if (parts[i2].indexOf("utf8=") === 0) {
            if (parts[i2] === charsetSentinel) {
              charset = "utf-8";
            } else if (parts[i2] === isoSentinel) {
              charset = "iso-8859-1";
            }
            skipIndex = i2;
            i2 = parts.length;
          }
        }
      }
      for (i2 = 0; i2 < parts.length; ++i2) {
        if (i2 === skipIndex) {
          continue;
        }
        var part = parts[i2];
        var bracketEqualsPos = part.indexOf("]=");
        var pos = bracketEqualsPos === -1 ? part.indexOf("=") : bracketEqualsPos + 1;
        var key;
        var val;
        if (pos === -1) {
          key = options.decoder(part, defaults.decoder, charset, "key");
          val = options.strictNullHandling ? null : "";
        } else {
          key = options.decoder(part.slice(0, pos), defaults.decoder, charset, "key");
          if (key !== null) {
            val = utils2.maybeMap(
              parseArrayValue(
                part.slice(pos + 1),
                options,
                isArray(obj[key]) ? obj[key].length : 0
              ),
              function(encodedVal) {
                return options.decoder(encodedVal, defaults.decoder, charset, "value");
              }
            );
          }
        }
        if (val && options.interpretNumericEntities && charset === "iso-8859-1") {
          val = interpretNumericEntities(String(val));
        }
        if (part.indexOf("[]=") > -1) {
          val = isArray(val) ? [val] : val;
        }
        if (options.comma && isArray(val) && val.length > options.arrayLimit) {
          if (options.throwOnLimitExceeded) {
            throw new RangeError("Array limit exceeded. Only " + options.arrayLimit + " element" + (options.arrayLimit === 1 ? "" : "s") + " allowed in an array.");
          }
          val = utils2.combine([], val, options.arrayLimit, options.plainObjects);
        }
        if (key !== null) {
          var existing = has.call(obj, key);
          if (existing && (options.duplicates === "combine" || part.indexOf("[]=") > -1)) {
            obj[key] = utils2.combine(
              obj[key],
              val,
              options.arrayLimit,
              options.plainObjects
            );
          } else if (!existing || options.duplicates === "last") {
            obj[key] = val;
          }
        }
      }
      return obj;
    };
    var parseObject = function(chain, val, options, valuesParsed) {
      var currentArrayLength = 0;
      if (chain.length > 0 && chain[chain.length - 1] === "[]") {
        var parentKey = chain.slice(0, -1).join("");
        currentArrayLength = Array.isArray(val) && val[parentKey] ? val[parentKey].length : 0;
      }
      var leaf = valuesParsed ? val : parseArrayValue(val, options, currentArrayLength);
      for (var i2 = chain.length - 1; i2 >= 0; --i2) {
        var obj;
        var root = chain[i2];
        if (root === "[]" && options.parseArrays) {
          if (utils2.isOverflow(leaf)) {
            obj = leaf;
          } else {
            obj = options.allowEmptyArrays && (leaf === "" || options.strictNullHandling && leaf === null) ? [] : utils2.combine(
              [],
              leaf,
              options.arrayLimit,
              options.plainObjects
            );
          }
        } else {
          obj = options.plainObjects ? { __proto__: null } : {};
          var cleanRoot = root.charAt(0) === "[" && root.charAt(root.length - 1) === "]" ? root.slice(1, -1) : root;
          var decodedRoot = options.decodeDotInKeys ? cleanRoot.replace(/%2E/g, ".") : cleanRoot;
          var index = parseInt(decodedRoot, 10);
          var isValidArrayIndex = !isNaN(index) && root !== decodedRoot && String(index) === decodedRoot && index >= 0 && options.parseArrays;
          if (!options.parseArrays && decodedRoot === "") {
            obj = { 0: leaf };
          } else if (isValidArrayIndex && index < options.arrayLimit) {
            obj = [];
            obj[index] = leaf;
          } else if (isValidArrayIndex && options.throwOnLimitExceeded) {
            throw new RangeError("Array limit exceeded. Only " + options.arrayLimit + " element" + (options.arrayLimit === 1 ? "" : "s") + " allowed in an array.");
          } else if (isValidArrayIndex) {
            obj[index] = leaf;
            utils2.markOverflow(obj, index);
          } else if (decodedRoot !== "__proto__") {
            obj[decodedRoot] = leaf;
          }
        }
        leaf = obj;
      }
      return leaf;
    };
    var splitKeyIntoSegments = function splitKeyIntoSegments2(givenKey, options) {
      var key = options.allowDots ? givenKey.replace(/\.([^.[]+)/g, "[$1]") : givenKey;
      if (options.depth <= 0) {
        if (!options.plainObjects && has.call(Object.prototype, key)) {
          if (!options.allowPrototypes) {
            return;
          }
        }
        return [key];
      }
      var brackets = /(\[[^[\]]*])/;
      var child = /(\[[^[\]]*])/g;
      var segment = brackets.exec(key);
      var parent = segment ? key.slice(0, segment.index) : key;
      var keys = [];
      if (parent) {
        if (!options.plainObjects && has.call(Object.prototype, parent)) {
          if (!options.allowPrototypes) {
            return;
          }
        }
        keys[keys.length] = parent;
      }
      var i2 = 0;
      while ((segment = child.exec(key)) !== null && i2 < options.depth) {
        i2 += 1;
        var segmentContent = segment[1].slice(1, -1);
        if (!options.plainObjects && has.call(Object.prototype, segmentContent)) {
          if (!options.allowPrototypes) {
            return;
          }
        }
        keys[keys.length] = segment[1];
      }
      if (segment) {
        if (options.strictDepth === true) {
          throw new RangeError("Input depth exceeded depth option of " + options.depth + " and strictDepth is true");
        }
        keys[keys.length] = "[" + key.slice(segment.index) + "]";
      }
      return keys;
    };
    var parseKeys = function parseQueryStringKeys(givenKey, val, options, valuesParsed) {
      if (!givenKey) {
        return;
      }
      var keys = splitKeyIntoSegments(givenKey, options);
      if (!keys) {
        return;
      }
      return parseObject(keys, val, options, valuesParsed);
    };
    var normalizeParseOptions = function normalizeParseOptions2(opts) {
      if (!opts) {
        return defaults;
      }
      if (typeof opts.allowEmptyArrays !== "undefined" && typeof opts.allowEmptyArrays !== "boolean") {
        throw new TypeError("`allowEmptyArrays` option can only be `true` or `false`, when provided");
      }
      if (typeof opts.decodeDotInKeys !== "undefined" && typeof opts.decodeDotInKeys !== "boolean") {
        throw new TypeError("`decodeDotInKeys` option can only be `true` or `false`, when provided");
      }
      if (opts.decoder !== null && typeof opts.decoder !== "undefined" && typeof opts.decoder !== "function") {
        throw new TypeError("Decoder has to be a function.");
      }
      if (typeof opts.charset !== "undefined" && opts.charset !== "utf-8" && opts.charset !== "iso-8859-1") {
        throw new TypeError("The charset option must be either utf-8, iso-8859-1, or undefined");
      }
      if (typeof opts.throwOnLimitExceeded !== "undefined" && typeof opts.throwOnLimitExceeded !== "boolean") {
        throw new TypeError("`throwOnLimitExceeded` option must be a boolean");
      }
      var charset = typeof opts.charset === "undefined" ? defaults.charset : opts.charset;
      var duplicates = typeof opts.duplicates === "undefined" ? defaults.duplicates : opts.duplicates;
      if (duplicates !== "combine" && duplicates !== "first" && duplicates !== "last") {
        throw new TypeError("The duplicates option must be either combine, first, or last");
      }
      var allowDots = typeof opts.allowDots === "undefined" ? opts.decodeDotInKeys === true ? true : defaults.allowDots : !!opts.allowDots;
      return {
        allowDots,
        allowEmptyArrays: typeof opts.allowEmptyArrays === "boolean" ? !!opts.allowEmptyArrays : defaults.allowEmptyArrays,
        allowPrototypes: typeof opts.allowPrototypes === "boolean" ? opts.allowPrototypes : defaults.allowPrototypes,
        allowSparse: typeof opts.allowSparse === "boolean" ? opts.allowSparse : defaults.allowSparse,
        arrayLimit: typeof opts.arrayLimit === "number" ? opts.arrayLimit : defaults.arrayLimit,
        charset,
        charsetSentinel: typeof opts.charsetSentinel === "boolean" ? opts.charsetSentinel : defaults.charsetSentinel,
        comma: typeof opts.comma === "boolean" ? opts.comma : defaults.comma,
        decodeDotInKeys: typeof opts.decodeDotInKeys === "boolean" ? opts.decodeDotInKeys : defaults.decodeDotInKeys,
        decoder: typeof opts.decoder === "function" ? opts.decoder : defaults.decoder,
        delimiter: typeof opts.delimiter === "string" || utils2.isRegExp(opts.delimiter) ? opts.delimiter : defaults.delimiter,
        // eslint-disable-next-line no-implicit-coercion, no-extra-parens
        depth: typeof opts.depth === "number" || opts.depth === false ? +opts.depth : defaults.depth,
        duplicates,
        ignoreQueryPrefix: opts.ignoreQueryPrefix === true,
        interpretNumericEntities: typeof opts.interpretNumericEntities === "boolean" ? opts.interpretNumericEntities : defaults.interpretNumericEntities,
        parameterLimit: typeof opts.parameterLimit === "number" ? opts.parameterLimit : defaults.parameterLimit,
        parseArrays: opts.parseArrays !== false,
        plainObjects: typeof opts.plainObjects === "boolean" ? opts.plainObjects : defaults.plainObjects,
        strictDepth: typeof opts.strictDepth === "boolean" ? !!opts.strictDepth : defaults.strictDepth,
        strictMerge: typeof opts.strictMerge === "boolean" ? !!opts.strictMerge : defaults.strictMerge,
        strictNullHandling: typeof opts.strictNullHandling === "boolean" ? opts.strictNullHandling : defaults.strictNullHandling,
        throwOnLimitExceeded: typeof opts.throwOnLimitExceeded === "boolean" ? opts.throwOnLimitExceeded : false
      };
    };
    parse = function(str, opts) {
      var options = normalizeParseOptions(opts);
      if (str === "" || str === null || typeof str === "undefined") {
        return options.plainObjects ? { __proto__: null } : {};
      }
      var tempObj = typeof str === "string" ? parseValues(str, options) : str;
      var obj = options.plainObjects ? { __proto__: null } : {};
      var keys = Object.keys(tempObj);
      for (var i2 = 0; i2 < keys.length; ++i2) {
        var key = keys[i2];
        var newObj = parseKeys(key, tempObj[key], options, typeof str === "string");
        obj = utils2.merge(obj, newObj, options);
      }
      if (options.allowSparse === true) {
        return obj;
      }
      return utils2.compact(obj);
    };
    return parse;
  }
  var lib;
  var hasRequiredLib;
  function requireLib() {
    if (hasRequiredLib) return lib;
    hasRequiredLib = 1;
    var stringify = /* @__PURE__ */ requireStringify();
    var parse2 = /* @__PURE__ */ requireParse();
    var formats2 = /* @__PURE__ */ requireFormats();
    lib = {
      formats: formats2,
      parse: parse2,
      stringify
    };
    return lib;
  }
  var hasRequiredUrl;
  function requireUrl() {
    if (hasRequiredUrl) return url;
    hasRequiredUrl = 1;
    var punycode2 = requirePunycode();
    function Url() {
      this.protocol = null;
      this.slashes = null;
      this.auth = null;
      this.host = null;
      this.port = null;
      this.hostname = null;
      this.hash = null;
      this.search = null;
      this.query = null;
      this.pathname = null;
      this.path = null;
      this.href = null;
    }
    var protocolPattern = /^([a-z0-9.+-]+:)/i, portPattern = /:[0-9]*$/, simplePathPattern = /^(\/\/?(?!\/)[^?\s]*)(\?[^\s]*)?$/, delims = [
      "<",
      ">",
      '"',
      "`",
      " ",
      "\r",
      "\n",
      "	"
    ], unwise = [
      "{",
      "}",
      "|",
      "\\",
      "^",
      "`"
    ].concat(delims), autoEscape = ["'"].concat(unwise), nonHostChars = [
      "%",
      "/",
      "?",
      ";",
      "#"
    ].concat(autoEscape), hostEndingChars = [
      "/",
      "?",
      "#"
    ], hostnameMaxLen = 255, hostnamePartPattern = /^[+a-z0-9A-Z_-]{0,63}$/, hostnamePartStart = /^([+a-z0-9A-Z_-]{0,63})(.*)$/, unsafeProtocol = {
      javascript: true,
      "javascript:": true
    }, hostlessProtocol = {
      javascript: true,
      "javascript:": true
    }, slashedProtocol = {
      http: true,
      https: true,
      ftp: true,
      gopher: true,
      file: true,
      "http:": true,
      "https:": true,
      "ftp:": true,
      "gopher:": true,
      "file:": true
    }, querystring = /* @__PURE__ */ requireLib();
    function urlParse(url2, parseQueryString, slashesDenoteHost) {
      if (url2 && typeof url2 === "object" && url2 instanceof Url) {
        return url2;
      }
      var u2 = new Url();
      u2.parse(url2, parseQueryString, slashesDenoteHost);
      return u2;
    }
    Url.prototype.parse = function(url2, parseQueryString, slashesDenoteHost) {
      if (typeof url2 !== "string") {
        throw new TypeError("Parameter 'url' must be a string, not " + typeof url2);
      }
      var queryIndex = url2.indexOf("?"), splitter = queryIndex !== -1 && queryIndex < url2.indexOf("#") ? "?" : "#", uSplit = url2.split(splitter), slashRegex = /\\/g;
      uSplit[0] = uSplit[0].replace(slashRegex, "/");
      url2 = uSplit.join(splitter);
      var rest = url2;
      rest = rest.trim();
      if (!slashesDenoteHost && url2.split("#").length === 1) {
        var simplePath = simplePathPattern.exec(rest);
        if (simplePath) {
          this.path = rest;
          this.href = rest;
          this.pathname = simplePath[1];
          if (simplePath[2]) {
            this.search = simplePath[2];
            if (parseQueryString) {
              this.query = querystring.parse(this.search.substr(1));
            } else {
              this.query = this.search.substr(1);
            }
          } else if (parseQueryString) {
            this.search = "";
            this.query = {};
          }
          return this;
        }
      }
      var proto = protocolPattern.exec(rest);
      if (proto) {
        proto = proto[0];
        var lowerProto = proto.toLowerCase();
        this.protocol = lowerProto;
        rest = rest.substr(proto.length);
      }
      if (slashesDenoteHost || proto || rest.match(/^\/\/[^@/]+@[^@/]+/)) {
        var slashes = rest.substr(0, 2) === "//";
        if (slashes && !(proto && hostlessProtocol[proto])) {
          rest = rest.substr(2);
          this.slashes = true;
        }
      }
      if (!hostlessProtocol[proto] && (slashes || proto && !slashedProtocol[proto])) {
        var hostEnd = -1;
        for (var i2 = 0; i2 < hostEndingChars.length; i2++) {
          var hec = rest.indexOf(hostEndingChars[i2]);
          if (hec !== -1 && (hostEnd === -1 || hec < hostEnd)) {
            hostEnd = hec;
          }
        }
        var auth, atSign;
        if (hostEnd === -1) {
          atSign = rest.lastIndexOf("@");
        } else {
          atSign = rest.lastIndexOf("@", hostEnd);
        }
        if (atSign !== -1) {
          auth = rest.slice(0, atSign);
          rest = rest.slice(atSign + 1);
          this.auth = decodeURIComponent(auth);
        }
        hostEnd = -1;
        for (var i2 = 0; i2 < nonHostChars.length; i2++) {
          var hec = rest.indexOf(nonHostChars[i2]);
          if (hec !== -1 && (hostEnd === -1 || hec < hostEnd)) {
            hostEnd = hec;
          }
        }
        if (hostEnd === -1) {
          hostEnd = rest.length;
        }
        this.host = rest.slice(0, hostEnd);
        rest = rest.slice(hostEnd);
        this.parseHost();
        this.hostname = this.hostname || "";
        var ipv6Hostname = this.hostname[0] === "[" && this.hostname[this.hostname.length - 1] === "]";
        if (!ipv6Hostname) {
          var hostparts = this.hostname.split(/\./);
          for (var i2 = 0, l2 = hostparts.length; i2 < l2; i2++) {
            var part = hostparts[i2];
            if (!part) {
              continue;
            }
            if (!part.match(hostnamePartPattern)) {
              var newpart = "";
              for (var j2 = 0, k2 = part.length; j2 < k2; j2++) {
                if (part.charCodeAt(j2) > 127) {
                  newpart += "x";
                } else {
                  newpart += part[j2];
                }
              }
              if (!newpart.match(hostnamePartPattern)) {
                var validParts = hostparts.slice(0, i2);
                var notHost = hostparts.slice(i2 + 1);
                var bit = part.match(hostnamePartStart);
                if (bit) {
                  validParts.push(bit[1]);
                  notHost.unshift(bit[2]);
                }
                if (notHost.length) {
                  rest = "/" + notHost.join(".") + rest;
                }
                this.hostname = validParts.join(".");
                break;
              }
            }
          }
        }
        if (this.hostname.length > hostnameMaxLen) {
          this.hostname = "";
        } else {
          this.hostname = this.hostname.toLowerCase();
        }
        if (!ipv6Hostname) {
          this.hostname = punycode2.toASCII(this.hostname);
        }
        var p2 = this.port ? ":" + this.port : "";
        var h2 = this.hostname || "";
        this.host = h2 + p2;
        this.href += this.host;
        if (ipv6Hostname) {
          this.hostname = this.hostname.substr(1, this.hostname.length - 2);
          if (rest[0] !== "/") {
            rest = "/" + rest;
          }
        }
      }
      if (!unsafeProtocol[lowerProto]) {
        for (var i2 = 0, l2 = autoEscape.length; i2 < l2; i2++) {
          var ae2 = autoEscape[i2];
          if (rest.indexOf(ae2) === -1) {
            continue;
          }
          var esc = encodeURIComponent(ae2);
          if (esc === ae2) {
            esc = escape(ae2);
          }
          rest = rest.split(ae2).join(esc);
        }
      }
      var hash = rest.indexOf("#");
      if (hash !== -1) {
        this.hash = rest.substr(hash);
        rest = rest.slice(0, hash);
      }
      var qm = rest.indexOf("?");
      if (qm !== -1) {
        this.search = rest.substr(qm);
        this.query = rest.substr(qm + 1);
        if (parseQueryString) {
          this.query = querystring.parse(this.query);
        }
        rest = rest.slice(0, qm);
      } else if (parseQueryString) {
        this.search = "";
        this.query = {};
      }
      if (rest) {
        this.pathname = rest;
      }
      if (slashedProtocol[lowerProto] && this.hostname && !this.pathname) {
        this.pathname = "/";
      }
      if (this.pathname || this.search) {
        var p2 = this.pathname || "";
        var s2 = this.search || "";
        this.path = p2 + s2;
      }
      this.href = this.format();
      return this;
    };
    function urlFormat(obj) {
      if (typeof obj === "string") {
        obj = urlParse(obj);
      }
      if (!(obj instanceof Url)) {
        return Url.prototype.format.call(obj);
      }
      return obj.format();
    }
    Url.prototype.format = function() {
      var auth = this.auth || "";
      if (auth) {
        auth = encodeURIComponent(auth);
        auth = auth.replace(/%3A/i, ":");
        auth += "@";
      }
      var protocol = this.protocol || "", pathname = this.pathname || "", hash = this.hash || "", host = false, query = "";
      if (this.host) {
        host = auth + this.host;
      } else if (this.hostname) {
        host = auth + (this.hostname.indexOf(":") === -1 ? this.hostname : "[" + this.hostname + "]");
        if (this.port) {
          host += ":" + this.port;
        }
      }
      if (this.query && typeof this.query === "object" && Object.keys(this.query).length) {
        query = querystring.stringify(this.query, {
          arrayFormat: "repeat",
          addQueryPrefix: false
        });
      }
      var search = this.search || query && "?" + query || "";
      if (protocol && protocol.substr(-1) !== ":") {
        protocol += ":";
      }
      if (this.slashes || (!protocol || slashedProtocol[protocol]) && host !== false) {
        host = "//" + (host || "");
        if (pathname && pathname.charAt(0) !== "/") {
          pathname = "/" + pathname;
        }
      } else if (!host) {
        host = "";
      }
      if (hash && hash.charAt(0) !== "#") {
        hash = "#" + hash;
      }
      if (search && search.charAt(0) !== "?") {
        search = "?" + search;
      }
      pathname = pathname.replace(/[?#]/g, function(match) {
        return encodeURIComponent(match);
      });
      search = search.replace("#", "%23");
      return protocol + host + pathname + search + hash;
    };
    function urlResolve(source, relative) {
      return urlParse(source, false, true).resolve(relative);
    }
    Url.prototype.resolve = function(relative) {
      return this.resolveObject(urlParse(relative, false, true)).format();
    };
    function urlResolveObject(source, relative) {
      if (!source) {
        return relative;
      }
      return urlParse(source, false, true).resolveObject(relative);
    }
    Url.prototype.resolveObject = function(relative) {
      if (typeof relative === "string") {
        var rel = new Url();
        rel.parse(relative, false, true);
        relative = rel;
      }
      var result = new Url();
      var tkeys = Object.keys(this);
      for (var tk = 0; tk < tkeys.length; tk++) {
        var tkey = tkeys[tk];
        result[tkey] = this[tkey];
      }
      result.hash = relative.hash;
      if (relative.href === "") {
        result.href = result.format();
        return result;
      }
      if (relative.slashes && !relative.protocol) {
        var rkeys = Object.keys(relative);
        for (var rk = 0; rk < rkeys.length; rk++) {
          var rkey = rkeys[rk];
          if (rkey !== "protocol") {
            result[rkey] = relative[rkey];
          }
        }
        if (slashedProtocol[result.protocol] && result.hostname && !result.pathname) {
          result.pathname = "/";
          result.path = result.pathname;
        }
        result.href = result.format();
        return result;
      }
      if (relative.protocol && relative.protocol !== result.protocol) {
        if (!slashedProtocol[relative.protocol]) {
          var keys = Object.keys(relative);
          for (var v2 = 0; v2 < keys.length; v2++) {
            var k2 = keys[v2];
            result[k2] = relative[k2];
          }
          result.href = result.format();
          return result;
        }
        result.protocol = relative.protocol;
        if (!relative.host && !hostlessProtocol[relative.protocol]) {
          var relPath = (relative.pathname || "").split("/");
          while (relPath.length && !(relative.host = relPath.shift())) {
          }
          if (!relative.host) {
            relative.host = "";
          }
          if (!relative.hostname) {
            relative.hostname = "";
          }
          if (relPath[0] !== "") {
            relPath.unshift("");
          }
          if (relPath.length < 2) {
            relPath.unshift("");
          }
          result.pathname = relPath.join("/");
        } else {
          result.pathname = relative.pathname;
        }
        result.search = relative.search;
        result.query = relative.query;
        result.host = relative.host || "";
        result.auth = relative.auth;
        result.hostname = relative.hostname || relative.host;
        result.port = relative.port;
        if (result.pathname || result.search) {
          var p2 = result.pathname || "";
          var s2 = result.search || "";
          result.path = p2 + s2;
        }
        result.slashes = result.slashes || relative.slashes;
        result.href = result.format();
        return result;
      }
      var isSourceAbs = result.pathname && result.pathname.charAt(0) === "/", isRelAbs = relative.host || relative.pathname && relative.pathname.charAt(0) === "/", mustEndAbs = isRelAbs || isSourceAbs || result.host && relative.pathname, removeAllDots = mustEndAbs, srcPath = result.pathname && result.pathname.split("/") || [], relPath = relative.pathname && relative.pathname.split("/") || [], psychotic = result.protocol && !slashedProtocol[result.protocol];
      if (psychotic) {
        result.hostname = "";
        result.port = null;
        if (result.host) {
          if (srcPath[0] === "") {
            srcPath[0] = result.host;
          } else {
            srcPath.unshift(result.host);
          }
        }
        result.host = "";
        if (relative.protocol) {
          relative.hostname = null;
          relative.port = null;
          if (relative.host) {
            if (relPath[0] === "") {
              relPath[0] = relative.host;
            } else {
              relPath.unshift(relative.host);
            }
          }
          relative.host = null;
        }
        mustEndAbs = mustEndAbs && (relPath[0] === "" || srcPath[0] === "");
      }
      if (isRelAbs) {
        result.host = relative.host || relative.host === "" ? relative.host : result.host;
        result.hostname = relative.hostname || relative.hostname === "" ? relative.hostname : result.hostname;
        result.search = relative.search;
        result.query = relative.query;
        srcPath = relPath;
      } else if (relPath.length) {
        if (!srcPath) {
          srcPath = [];
        }
        srcPath.pop();
        srcPath = srcPath.concat(relPath);
        result.search = relative.search;
        result.query = relative.query;
      } else if (relative.search != null) {
        if (psychotic) {
          result.host = srcPath.shift();
          result.hostname = result.host;
          var authInHost = result.host && result.host.indexOf("@") > 0 ? result.host.split("@") : false;
          if (authInHost) {
            result.auth = authInHost.shift();
            result.hostname = authInHost.shift();
            result.host = result.hostname;
          }
        }
        result.search = relative.search;
        result.query = relative.query;
        if (result.pathname !== null || result.search !== null) {
          result.path = (result.pathname ? result.pathname : "") + (result.search ? result.search : "");
        }
        result.href = result.format();
        return result;
      }
      if (!srcPath.length) {
        result.pathname = null;
        if (result.search) {
          result.path = "/" + result.search;
        } else {
          result.path = null;
        }
        result.href = result.format();
        return result;
      }
      var last = srcPath.slice(-1)[0];
      var hasTrailingSlash = (result.host || relative.host || srcPath.length > 1) && (last === "." || last === "..") || last === "";
      var up = 0;
      for (var i2 = srcPath.length; i2 >= 0; i2--) {
        last = srcPath[i2];
        if (last === ".") {
          srcPath.splice(i2, 1);
        } else if (last === "..") {
          srcPath.splice(i2, 1);
          up++;
        } else if (up) {
          srcPath.splice(i2, 1);
          up--;
        }
      }
      if (!mustEndAbs && !removeAllDots) {
        for (; up--; up) {
          srcPath.unshift("..");
        }
      }
      if (mustEndAbs && srcPath[0] !== "" && (!srcPath[0] || srcPath[0].charAt(0) !== "/")) {
        srcPath.unshift("");
      }
      if (hasTrailingSlash && srcPath.join("/").substr(-1) !== "/") {
        srcPath.push("");
      }
      var isAbsolute = srcPath[0] === "" || srcPath[0] && srcPath[0].charAt(0) === "/";
      if (psychotic) {
        result.hostname = isAbsolute ? "" : srcPath.length ? srcPath.shift() : "";
        result.host = result.hostname;
        var authInHost = result.host && result.host.indexOf("@") > 0 ? result.host.split("@") : false;
        if (authInHost) {
          result.auth = authInHost.shift();
          result.hostname = authInHost.shift();
          result.host = result.hostname;
        }
      }
      mustEndAbs = mustEndAbs || result.host && srcPath.length;
      if (mustEndAbs && !isAbsolute) {
        srcPath.unshift("");
      }
      if (srcPath.length > 0) {
        result.pathname = srcPath.join("/");
      } else {
        result.pathname = null;
        result.path = null;
      }
      if (result.pathname !== null || result.search !== null) {
        result.path = (result.pathname ? result.pathname : "") + (result.search ? result.search : "");
      }
      result.auth = relative.auth || result.auth;
      result.slashes = result.slashes || relative.slashes;
      result.href = result.format();
      return result;
    };
    Url.prototype.parseHost = function() {
      var host = this.host;
      var port = portPattern.exec(host);
      if (port) {
        port = port[0];
        if (port !== ":") {
          this.port = port.substr(1);
        }
        host = host.substr(0, host.length - port.length);
      }
      if (host) {
        this.hostname = host;
      }
    };
    url.parse = urlParse;
    url.resolve = urlResolve;
    url.resolveObject = urlResolveObject;
    url.format = urlFormat;
    url.Url = Url;
    return url;
  }
  requireUrl();
  const warnings = {};
  function deprecation(version, message, ignoreDepth = 3) {
    if (warnings[message])
      return;
    let stack = new Error().stack;
    typeof stack > "u" ? console.warn("PixiJS Deprecation Warning: ", `${message}
Deprecated since v${version}`) : (stack = stack.split(`
`).splice(ignoreDepth).join(`
`), console.groupCollapsed ? (console.groupCollapsed(
      "%cPixiJS Deprecation Warning: %c%s",
      "color:#614108;background:#fffbe6",
      "font-weight:normal;color:#614108;background:#fffbe6",
      `${message}
Deprecated since v${version}`
    ), console.warn(stack), console.groupEnd()) : (console.warn("PixiJS Deprecation Warning: ", `${message}
Deprecated since v${version}`), console.warn(stack))), warnings[message] = true;
  }
  let supported;
  function isWebGLSupported() {
    return typeof supported > "u" && (supported = (function() {
      const contextOptions = {
        stencil: true,
        failIfMajorPerformanceCaveat: settings.FAIL_IF_MAJOR_PERFORMANCE_CAVEAT
      };
      try {
        if (!settings.ADAPTER.getWebGLRenderingContext())
          return false;
        const canvas = settings.ADAPTER.createCanvas();
        let gl = canvas.getContext("webgl", contextOptions) || canvas.getContext("experimental-webgl", contextOptions);
        const success = !!gl?.getContextAttributes()?.stencil;
        if (gl) {
          const loseContext = gl.getExtension("WEBGL_lose_context");
          loseContext && loseContext.loseContext();
        }
        return gl = null, success;
      } catch {
        return false;
      }
    })()), supported;
  }
  var r = { grad: 0.9, turn: 360, rad: 360 / (2 * Math.PI) }, t$1 = function(r2) {
    return "string" == typeof r2 ? r2.length > 0 : "number" == typeof r2;
  }, n$1 = function(r2, t2, n2) {
    return void 0 === t2 && (t2 = 0), void 0 === n2 && (n2 = Math.pow(10, t2)), Math.round(n2 * r2) / n2 + 0;
  }, e = function(r2, t2, n2) {
    return void 0 === t2 && (t2 = 0), void 0 === n2 && (n2 = 1), r2 > n2 ? n2 : r2 > t2 ? r2 : t2;
  }, u$1 = function(r2) {
    return (r2 = isFinite(r2) ? r2 % 360 : 0) > 0 ? r2 : r2 + 360;
  }, a = function(r2) {
    return { r: e(r2.r, 0, 255), g: e(r2.g, 0, 255), b: e(r2.b, 0, 255), a: e(r2.a) };
  }, o = function(r2) {
    return { r: n$1(r2.r), g: n$1(r2.g), b: n$1(r2.b), a: n$1(r2.a, 3) };
  }, i = /^#([0-9a-f]{3,8})$/i, s = function(r2) {
    var t2 = r2.toString(16);
    return t2.length < 2 ? "0" + t2 : t2;
  }, h = function(r2) {
    var t2 = r2.r, n2 = r2.g, e2 = r2.b, u2 = r2.a, a2 = Math.max(t2, n2, e2), o2 = a2 - Math.min(t2, n2, e2), i2 = o2 ? a2 === t2 ? (n2 - e2) / o2 : a2 === n2 ? 2 + (e2 - t2) / o2 : 4 + (t2 - n2) / o2 : 0;
    return { h: 60 * (i2 < 0 ? i2 + 6 : i2), s: a2 ? o2 / a2 * 100 : 0, v: a2 / 255 * 100, a: u2 };
  }, b = function(r2) {
    var t2 = r2.h, n2 = r2.s, e2 = r2.v, u2 = r2.a;
    t2 = t2 / 360 * 6, n2 /= 100, e2 /= 100;
    var a2 = Math.floor(t2), o2 = e2 * (1 - n2), i2 = e2 * (1 - (t2 - a2) * n2), s2 = e2 * (1 - (1 - t2 + a2) * n2), h2 = a2 % 6;
    return { r: 255 * [e2, i2, o2, o2, s2, e2][h2], g: 255 * [s2, e2, e2, i2, o2, o2][h2], b: 255 * [o2, o2, s2, e2, e2, i2][h2], a: u2 };
  }, g = function(r2) {
    return { h: u$1(r2.h), s: e(r2.s, 0, 100), l: e(r2.l, 0, 100), a: e(r2.a) };
  }, d$1 = function(r2) {
    return { h: n$1(r2.h), s: n$1(r2.s), l: n$1(r2.l), a: n$1(r2.a, 3) };
  }, f = function(r2) {
    return b((n2 = (t2 = r2).s, { h: t2.h, s: (n2 *= ((e2 = t2.l) < 50 ? e2 : 100 - e2) / 100) > 0 ? 2 * n2 / (e2 + n2) * 100 : 0, v: e2 + n2, a: t2.a }));
    var t2, n2, e2;
  }, c = function(r2) {
    return { h: (t2 = h(r2)).h, s: (u2 = (200 - (n2 = t2.s)) * (e2 = t2.v) / 100) > 0 && u2 < 200 ? n2 * e2 / 100 / (u2 <= 100 ? u2 : 200 - u2) * 100 : 0, l: u2 / 2, a: t2.a };
    var t2, n2, e2, u2;
  }, l = /^hsla?\(\s*([+-]?\d*\.?\d+)(deg|rad|grad|turn)?\s*,\s*([+-]?\d*\.?\d+)%\s*,\s*([+-]?\d*\.?\d+)%\s*(?:,\s*([+-]?\d*\.?\d+)(%)?\s*)?\)$/i, p = /^hsla?\(\s*([+-]?\d*\.?\d+)(deg|rad|grad|turn)?\s+([+-]?\d*\.?\d+)%\s+([+-]?\d*\.?\d+)%\s*(?:\/\s*([+-]?\d*\.?\d+)(%)?\s*)?\)$/i, v = /^rgba?\(\s*([+-]?\d*\.?\d+)(%)?\s*,\s*([+-]?\d*\.?\d+)(%)?\s*,\s*([+-]?\d*\.?\d+)(%)?\s*(?:,\s*([+-]?\d*\.?\d+)(%)?\s*)?\)$/i, m = /^rgba?\(\s*([+-]?\d*\.?\d+)(%)?\s+([+-]?\d*\.?\d+)(%)?\s+([+-]?\d*\.?\d+)(%)?\s*(?:\/\s*([+-]?\d*\.?\d+)(%)?\s*)?\)$/i, y = { string: [[function(r2) {
    var t2 = i.exec(r2);
    return t2 ? (r2 = t2[1]).length <= 4 ? { r: parseInt(r2[0] + r2[0], 16), g: parseInt(r2[1] + r2[1], 16), b: parseInt(r2[2] + r2[2], 16), a: 4 === r2.length ? n$1(parseInt(r2[3] + r2[3], 16) / 255, 2) : 1 } : 6 === r2.length || 8 === r2.length ? { r: parseInt(r2.substr(0, 2), 16), g: parseInt(r2.substr(2, 2), 16), b: parseInt(r2.substr(4, 2), 16), a: 8 === r2.length ? n$1(parseInt(r2.substr(6, 2), 16) / 255, 2) : 1 } : null : null;
  }, "hex"], [function(r2) {
    var t2 = v.exec(r2) || m.exec(r2);
    return t2 ? t2[2] !== t2[4] || t2[4] !== t2[6] ? null : a({ r: Number(t2[1]) / (t2[2] ? 100 / 255 : 1), g: Number(t2[3]) / (t2[4] ? 100 / 255 : 1), b: Number(t2[5]) / (t2[6] ? 100 / 255 : 1), a: void 0 === t2[7] ? 1 : Number(t2[7]) / (t2[8] ? 100 : 1) }) : null;
  }, "rgb"], [function(t2) {
    var n2 = l.exec(t2) || p.exec(t2);
    if (!n2) return null;
    var e2, u2, a2 = g({ h: (e2 = n2[1], u2 = n2[2], void 0 === u2 && (u2 = "deg"), Number(e2) * (r[u2] || 1)), s: Number(n2[3]), l: Number(n2[4]), a: void 0 === n2[5] ? 1 : Number(n2[5]) / (n2[6] ? 100 : 1) });
    return f(a2);
  }, "hsl"]], object: [[function(r2) {
    var n2 = r2.r, e2 = r2.g, u2 = r2.b, o2 = r2.a, i2 = void 0 === o2 ? 1 : o2;
    return t$1(n2) && t$1(e2) && t$1(u2) ? a({ r: Number(n2), g: Number(e2), b: Number(u2), a: Number(i2) }) : null;
  }, "rgb"], [function(r2) {
    var n2 = r2.h, e2 = r2.s, u2 = r2.l, a2 = r2.a, o2 = void 0 === a2 ? 1 : a2;
    if (!t$1(n2) || !t$1(e2) || !t$1(u2)) return null;
    var i2 = g({ h: Number(n2), s: Number(e2), l: Number(u2), a: Number(o2) });
    return f(i2);
  }, "hsl"], [function(r2) {
    var n2 = r2.h, a2 = r2.s, o2 = r2.v, i2 = r2.a, s2 = void 0 === i2 ? 1 : i2;
    if (!t$1(n2) || !t$1(a2) || !t$1(o2)) return null;
    var h2 = (function(r3) {
      return { h: u$1(r3.h), s: e(r3.s, 0, 100), v: e(r3.v, 0, 100), a: e(r3.a) };
    })({ h: Number(n2), s: Number(a2), v: Number(o2), a: Number(s2) });
    return b(h2);
  }, "hsv"]] }, N$1 = function(r2, t2) {
    for (var n2 = 0; n2 < t2.length; n2++) {
      var e2 = t2[n2][0](r2);
      if (e2) return [e2, t2[n2][1]];
    }
    return [null, void 0];
  }, x = function(r2) {
    return "string" == typeof r2 ? N$1(r2.trim(), y.string) : "object" == typeof r2 && null !== r2 ? N$1(r2, y.object) : [null, void 0];
  }, M = function(r2, t2) {
    var n2 = c(r2);
    return { h: n2.h, s: e(n2.s + 100 * t2, 0, 100), l: n2.l, a: n2.a };
  }, H$1 = function(r2) {
    return (299 * r2.r + 587 * r2.g + 114 * r2.b) / 1e3 / 255;
  }, $ = function(r2, t2) {
    var n2 = c(r2);
    return { h: n2.h, s: n2.s, l: e(n2.l + 100 * t2, 0, 100), a: n2.a };
  }, j = (function() {
    function r2(r3) {
      this.parsed = x(r3)[0], this.rgba = this.parsed || { r: 0, g: 0, b: 0, a: 1 };
    }
    return r2.prototype.isValid = function() {
      return null !== this.parsed;
    }, r2.prototype.brightness = function() {
      return n$1(H$1(this.rgba), 2);
    }, r2.prototype.isDark = function() {
      return H$1(this.rgba) < 0.5;
    }, r2.prototype.isLight = function() {
      return H$1(this.rgba) >= 0.5;
    }, r2.prototype.toHex = function() {
      return r3 = o(this.rgba), t2 = r3.r, e2 = r3.g, u2 = r3.b, i2 = (a2 = r3.a) < 1 ? s(n$1(255 * a2)) : "", "#" + s(t2) + s(e2) + s(u2) + i2;
      var r3, t2, e2, u2, a2, i2;
    }, r2.prototype.toRgb = function() {
      return o(this.rgba);
    }, r2.prototype.toRgbString = function() {
      return r3 = o(this.rgba), t2 = r3.r, n2 = r3.g, e2 = r3.b, (u2 = r3.a) < 1 ? "rgba(" + t2 + ", " + n2 + ", " + e2 + ", " + u2 + ")" : "rgb(" + t2 + ", " + n2 + ", " + e2 + ")";
      var r3, t2, n2, e2, u2;
    }, r2.prototype.toHsl = function() {
      return d$1(c(this.rgba));
    }, r2.prototype.toHslString = function() {
      return r3 = d$1(c(this.rgba)), t2 = r3.h, n2 = r3.s, e2 = r3.l, (u2 = r3.a) < 1 ? "hsla(" + t2 + ", " + n2 + "%, " + e2 + "%, " + u2 + ")" : "hsl(" + t2 + ", " + n2 + "%, " + e2 + "%)";
      var r3, t2, n2, e2, u2;
    }, r2.prototype.toHsv = function() {
      return r3 = h(this.rgba), { h: n$1(r3.h), s: n$1(r3.s), v: n$1(r3.v), a: n$1(r3.a, 3) };
      var r3;
    }, r2.prototype.invert = function() {
      return w({ r: 255 - (r3 = this.rgba).r, g: 255 - r3.g, b: 255 - r3.b, a: r3.a });
      var r3;
    }, r2.prototype.saturate = function(r3) {
      return void 0 === r3 && (r3 = 0.1), w(M(this.rgba, r3));
    }, r2.prototype.desaturate = function(r3) {
      return void 0 === r3 && (r3 = 0.1), w(M(this.rgba, -r3));
    }, r2.prototype.grayscale = function() {
      return w(M(this.rgba, -1));
    }, r2.prototype.lighten = function(r3) {
      return void 0 === r3 && (r3 = 0.1), w($(this.rgba, r3));
    }, r2.prototype.darken = function(r3) {
      return void 0 === r3 && (r3 = 0.1), w($(this.rgba, -r3));
    }, r2.prototype.rotate = function(r3) {
      return void 0 === r3 && (r3 = 15), this.hue(this.hue() + r3);
    }, r2.prototype.alpha = function(r3) {
      return "number" == typeof r3 ? w({ r: (t2 = this.rgba).r, g: t2.g, b: t2.b, a: r3 }) : n$1(this.rgba.a, 3);
      var t2;
    }, r2.prototype.hue = function(r3) {
      var t2 = c(this.rgba);
      return "number" == typeof r3 ? w({ h: r3, s: t2.s, l: t2.l, a: t2.a }) : n$1(t2.h);
    }, r2.prototype.isEqual = function(r3) {
      return this.toHex() === w(r3).toHex();
    }, r2;
  })(), w = function(r2) {
    return r2 instanceof j ? r2 : new j(r2);
  }, S$1 = [], k = function(r2) {
    r2.forEach(function(r3) {
      S$1.indexOf(r3) < 0 && (r3(j, y), S$1.push(r3));
    });
  };
  function namesPlugin(e2, f2) {
    var a2 = { white: "#ffffff", bisque: "#ffe4c4", blue: "#0000ff", cadetblue: "#5f9ea0", chartreuse: "#7fff00", chocolate: "#d2691e", coral: "#ff7f50", antiquewhite: "#faebd7", aqua: "#00ffff", azure: "#f0ffff", whitesmoke: "#f5f5f5", papayawhip: "#ffefd5", plum: "#dda0dd", blanchedalmond: "#ffebcd", black: "#000000", gold: "#ffd700", goldenrod: "#daa520", gainsboro: "#dcdcdc", cornsilk: "#fff8dc", cornflowerblue: "#6495ed", burlywood: "#deb887", aquamarine: "#7fffd4", beige: "#f5f5dc", crimson: "#dc143c", cyan: "#00ffff", darkblue: "#00008b", darkcyan: "#008b8b", darkgoldenrod: "#b8860b", darkkhaki: "#bdb76b", darkgray: "#a9a9a9", darkgreen: "#006400", darkgrey: "#a9a9a9", peachpuff: "#ffdab9", darkmagenta: "#8b008b", darkred: "#8b0000", darkorchid: "#9932cc", darkorange: "#ff8c00", darkslateblue: "#483d8b", gray: "#808080", darkslategray: "#2f4f4f", darkslategrey: "#2f4f4f", deeppink: "#ff1493", deepskyblue: "#00bfff", wheat: "#f5deb3", firebrick: "#b22222", floralwhite: "#fffaf0", ghostwhite: "#f8f8ff", darkviolet: "#9400d3", magenta: "#ff00ff", green: "#008000", dodgerblue: "#1e90ff", grey: "#808080", honeydew: "#f0fff0", hotpink: "#ff69b4", blueviolet: "#8a2be2", forestgreen: "#228b22", lawngreen: "#7cfc00", indianred: "#cd5c5c", indigo: "#4b0082", fuchsia: "#ff00ff", brown: "#a52a2a", maroon: "#800000", mediumblue: "#0000cd", lightcoral: "#f08080", darkturquoise: "#00ced1", lightcyan: "#e0ffff", ivory: "#fffff0", lightyellow: "#ffffe0", lightsalmon: "#ffa07a", lightseagreen: "#20b2aa", linen: "#faf0e6", mediumaquamarine: "#66cdaa", lemonchiffon: "#fffacd", lime: "#00ff00", khaki: "#f0e68c", mediumseagreen: "#3cb371", limegreen: "#32cd32", mediumspringgreen: "#00fa9a", lightskyblue: "#87cefa", lightblue: "#add8e6", midnightblue: "#191970", lightpink: "#ffb6c1", mistyrose: "#ffe4e1", moccasin: "#ffe4b5", mintcream: "#f5fffa", lightslategray: "#778899", lightslategrey: "#778899", navajowhite: "#ffdead", navy: "#000080", mediumvioletred: "#c71585", powderblue: "#b0e0e6", palegoldenrod: "#eee8aa", oldlace: "#fdf5e6", paleturquoise: "#afeeee", mediumturquoise: "#48d1cc", mediumorchid: "#ba55d3", rebeccapurple: "#663399", lightsteelblue: "#b0c4de", mediumslateblue: "#7b68ee", thistle: "#d8bfd8", tan: "#d2b48c", orchid: "#da70d6", mediumpurple: "#9370db", purple: "#800080", pink: "#ffc0cb", skyblue: "#87ceeb", springgreen: "#00ff7f", palegreen: "#98fb98", red: "#ff0000", yellow: "#ffff00", slateblue: "#6a5acd", lavenderblush: "#fff0f5", peru: "#cd853f", palevioletred: "#db7093", violet: "#ee82ee", teal: "#008080", slategray: "#708090", slategrey: "#708090", aliceblue: "#f0f8ff", darkseagreen: "#8fbc8f", darkolivegreen: "#556b2f", greenyellow: "#adff2f", seagreen: "#2e8b57", seashell: "#fff5ee", tomato: "#ff6347", silver: "#c0c0c0", sienna: "#a0522d", lavender: "#e6e6fa", lightgreen: "#90ee90", orange: "#ffa500", orangered: "#ff4500", steelblue: "#4682b4", royalblue: "#4169e1", turquoise: "#40e0d0", yellowgreen: "#9acd32", salmon: "#fa8072", saddlebrown: "#8b4513", sandybrown: "#f4a460", rosybrown: "#bc8f8f", darksalmon: "#e9967a", lightgoldenrodyellow: "#fafad2", snow: "#fffafa", lightgrey: "#d3d3d3", lightgray: "#d3d3d3", dimgray: "#696969", dimgrey: "#696969", olivedrab: "#6b8e23", olive: "#808000" }, r2 = {};
    for (var d2 in a2) r2[a2[d2]] = d2;
    var l2 = {};
    e2.prototype.toName = function(f3) {
      if (!(this.rgba.a || this.rgba.r || this.rgba.g || this.rgba.b)) return "transparent";
      var d3, i2, n2 = r2[this.toHex()];
      if (n2) return n2;
      if (null == f3 ? void 0 : f3.closest) {
        var o2 = this.toRgb(), t2 = 1 / 0, b2 = "black";
        if (!l2.length) for (var c2 in a2) l2[c2] = new e2(a2[c2]).toRgb();
        for (var g2 in a2) {
          var u2 = (d3 = o2, i2 = l2[g2], Math.pow(d3.r - i2.r, 2) + Math.pow(d3.g - i2.g, 2) + Math.pow(d3.b - i2.b, 2));
          u2 < t2 && (t2 = u2, b2 = g2);
        }
        return b2;
      }
    };
    f2.string.push([function(f3) {
      var r3 = f3.toLowerCase(), d3 = "transparent" === r3 ? "#0000" : a2[r3];
      return d3 ? new e2(d3).toRgb() : null;
    }, "name"]);
  }
  k([namesPlugin]);
  const _Color = class _Color2 {
    /**
     * @param {PIXI.ColorSource} value - Optional value to use, if not provided, white is used.
     */
    constructor(value = 16777215) {
      this._value = null, this._components = new Float32Array(4), this._components.fill(1), this._int = 16777215, this.value = value;
    }
    /** Get red component (0 - 1) */
    get red() {
      return this._components[0];
    }
    /** Get green component (0 - 1) */
    get green() {
      return this._components[1];
    }
    /** Get blue component (0 - 1) */
    get blue() {
      return this._components[2];
    }
    /** Get alpha component (0 - 1) */
    get alpha() {
      return this._components[3];
    }
    /**
     * Set the value, suitable for chaining
     * @param value
     * @see PIXI.Color.value
     */
    setValue(value) {
      return this.value = value, this;
    }
    /**
     * The current color source.
     *
     * When setting:
     * - Setting to an instance of `Color` will copy its color source and components.
     * - Otherwise, `Color` will try to normalize the color source and set the components.
     *   If the color source is invalid, an `Error` will be thrown and the `Color` will left unchanged.
     *
     * Note: The `null` in the setter's parameter type is added to match the TypeScript rule: return type of getter
     * must be assignable to its setter's parameter type. Setting `value` to `null` will throw an `Error`.
     *
     * When getting:
     * - A return value of `null` means the previous value was overridden (e.g., {@link PIXI.Color.multiply multiply},
     *   {@link PIXI.Color.premultiply premultiply} or {@link PIXI.Color.round round}).
     * - Otherwise, the color source used when setting is returned.
     * @type {PIXI.ColorSource}
     */
    set value(value) {
      if (value instanceof _Color2)
        this._value = this.cloneSource(value._value), this._int = value._int, this._components.set(value._components);
      else {
        if (value === null)
          throw new Error("Cannot set PIXI.Color#value to null");
        (this._value === null || !this.isSourceEqual(this._value, value)) && (this.normalize(value), this._value = this.cloneSource(value));
      }
    }
    get value() {
      return this._value;
    }
    /**
     * Copy a color source internally.
     * @param value - Color source
     */
    cloneSource(value) {
      return typeof value == "string" || typeof value == "number" || value instanceof Number || value === null ? value : Array.isArray(value) || ArrayBuffer.isView(value) ? value.slice(0) : typeof value == "object" && value !== null ? { ...value } : value;
    }
    /**
     * Equality check for color sources.
     * @param value1 - First color source
     * @param value2 - Second color source
     * @returns `true` if the color sources are equal, `false` otherwise.
     */
    isSourceEqual(value1, value2) {
      const type1 = typeof value1;
      if (type1 !== typeof value2)
        return false;
      if (type1 === "number" || type1 === "string" || value1 instanceof Number)
        return value1 === value2;
      if (Array.isArray(value1) && Array.isArray(value2) || ArrayBuffer.isView(value1) && ArrayBuffer.isView(value2))
        return value1.length !== value2.length ? false : value1.every((v2, i2) => v2 === value2[i2]);
      if (value1 !== null && value2 !== null) {
        const keys1 = Object.keys(value1), keys2 = Object.keys(value2);
        return keys1.length !== keys2.length ? false : keys1.every((key) => value1[key] === value2[key]);
      }
      return value1 === value2;
    }
    /**
     * Convert to a RGBA color object.
     * @example
     * import { Color } from 'pixi.js';
     * new Color('white').toRgb(); // returns { r: 1, g: 1, b: 1, a: 1 }
     */
    toRgba() {
      const [r2, g2, b2, a2] = this._components;
      return { r: r2, g: g2, b: b2, a: a2 };
    }
    /**
     * Convert to a RGB color object.
     * @example
     * import { Color } from 'pixi.js';
     * new Color('white').toRgb(); // returns { r: 1, g: 1, b: 1 }
     */
    toRgb() {
      const [r2, g2, b2] = this._components;
      return { r: r2, g: g2, b: b2 };
    }
    /** Convert to a CSS-style rgba string: `rgba(255,255,255,1.0)`. */
    toRgbaString() {
      const [r2, g2, b2] = this.toUint8RgbArray();
      return `rgba(${r2},${g2},${b2},${this.alpha})`;
    }
    toUint8RgbArray(out) {
      const [r2, g2, b2] = this._components;
      return out = out ?? [], out[0] = Math.round(r2 * 255), out[1] = Math.round(g2 * 255), out[2] = Math.round(b2 * 255), out;
    }
    toRgbArray(out) {
      out = out ?? [];
      const [r2, g2, b2] = this._components;
      return out[0] = r2, out[1] = g2, out[2] = b2, out;
    }
    /**
     * Convert to a hexadecimal number.
     * @example
     * import { Color } from 'pixi.js';
     * new Color('white').toNumber(); // returns 16777215
     */
    toNumber() {
      return this._int;
    }
    /**
     * Convert to a hexadecimal number in little endian format (e.g., BBGGRR).
     * @example
     * import { Color } from 'pixi.js';
     * new Color(0xffcc99).toLittleEndianNumber(); // returns 0x99ccff
     * @returns {number} - The color as a number in little endian format.
     */
    toLittleEndianNumber() {
      const value = this._int;
      return (value >> 16) + (value & 65280) + ((value & 255) << 16);
    }
    /**
     * Multiply with another color. This action is destructive, and will
     * override the previous `value` property to be `null`.
     * @param {PIXI.ColorSource} value - The color to multiply by.
     */
    multiply(value) {
      const [r2, g2, b2, a2] = _Color2.temp.setValue(value)._components;
      return this._components[0] *= r2, this._components[1] *= g2, this._components[2] *= b2, this._components[3] *= a2, this.refreshInt(), this._value = null, this;
    }
    /**
     * Converts color to a premultiplied alpha format. This action is destructive, and will
     * override the previous `value` property to be `null`.
     * @param alpha - The alpha to multiply by.
     * @param {boolean} [applyToRGB=true] - Whether to premultiply RGB channels.
     * @returns {PIXI.Color} - Itself.
     */
    premultiply(alpha, applyToRGB = true) {
      return applyToRGB && (this._components[0] *= alpha, this._components[1] *= alpha, this._components[2] *= alpha), this._components[3] = alpha, this.refreshInt(), this._value = null, this;
    }
    /**
     * Premultiplies alpha with current color.
     * @param {number} alpha - The alpha to multiply by.
     * @param {boolean} [applyToRGB=true] - Whether to premultiply RGB channels.
     * @returns {number} tint multiplied by alpha
     */
    toPremultiplied(alpha, applyToRGB = true) {
      if (alpha === 1)
        return (255 << 24) + this._int;
      if (alpha === 0)
        return applyToRGB ? 0 : this._int;
      let r2 = this._int >> 16 & 255, g2 = this._int >> 8 & 255, b2 = this._int & 255;
      return applyToRGB && (r2 = r2 * alpha + 0.5 | 0, g2 = g2 * alpha + 0.5 | 0, b2 = b2 * alpha + 0.5 | 0), (alpha * 255 << 24) + (r2 << 16) + (g2 << 8) + b2;
    }
    /**
     * Convert to a hexidecimal string.
     * @example
     * import { Color } from 'pixi.js';
     * new Color('white').toHex(); // returns "#ffffff"
     */
    toHex() {
      const hexString = this._int.toString(16);
      return `#${"000000".substring(0, 6 - hexString.length) + hexString}`;
    }
    /**
     * Convert to a hexidecimal string with alpha.
     * @example
     * import { Color } from 'pixi.js';
     * new Color('white').toHexa(); // returns "#ffffffff"
     */
    toHexa() {
      const alphaString = Math.round(this._components[3] * 255).toString(16);
      return this.toHex() + "00".substring(0, 2 - alphaString.length) + alphaString;
    }
    /**
     * Set alpha, suitable for chaining.
     * @param alpha
     */
    setAlpha(alpha) {
      return this._components[3] = this._clamp(alpha), this;
    }
    /**
     * Rounds the specified color according to the step. This action is destructive, and will
     * override the previous `value` property to be `null`. The alpha component is not rounded.
     * @param steps - Number of steps which will be used as a cap when rounding colors
     * @deprecated since 7.3.0
     */
    round(steps) {
      const [r2, g2, b2] = this._components;
      return this._components[0] = Math.round(r2 * steps) / steps, this._components[1] = Math.round(g2 * steps) / steps, this._components[2] = Math.round(b2 * steps) / steps, this.refreshInt(), this._value = null, this;
    }
    toArray(out) {
      out = out ?? [];
      const [r2, g2, b2, a2] = this._components;
      return out[0] = r2, out[1] = g2, out[2] = b2, out[3] = a2, out;
    }
    /**
     * Normalize the input value into rgba
     * @param value - Input value
     */
    normalize(value) {
      let r2, g2, b2, a2;
      if ((typeof value == "number" || value instanceof Number) && value >= 0 && value <= 16777215) {
        const int = value;
        r2 = (int >> 16 & 255) / 255, g2 = (int >> 8 & 255) / 255, b2 = (int & 255) / 255, a2 = 1;
      } else if ((Array.isArray(value) || value instanceof Float32Array) && value.length >= 3 && value.length <= 4)
        value = this._clamp(value), [r2, g2, b2, a2 = 1] = value;
      else if ((value instanceof Uint8Array || value instanceof Uint8ClampedArray) && value.length >= 3 && value.length <= 4)
        value = this._clamp(value, 0, 255), [r2, g2, b2, a2 = 255] = value, r2 /= 255, g2 /= 255, b2 /= 255, a2 /= 255;
      else if (typeof value == "string" || typeof value == "object") {
        if (typeof value == "string") {
          const match = _Color2.HEX_PATTERN.exec(value);
          match && (value = `#${match[2]}`);
        }
        const color = w(value);
        color.isValid() && ({ r: r2, g: g2, b: b2, a: a2 } = color.rgba, r2 /= 255, g2 /= 255, b2 /= 255);
      }
      if (r2 !== void 0)
        this._components[0] = r2, this._components[1] = g2, this._components[2] = b2, this._components[3] = a2, this.refreshInt();
      else
        throw new Error(`Unable to convert color ${value}`);
    }
    /** Refresh the internal color rgb number */
    refreshInt() {
      this._clamp(this._components);
      const [r2, g2, b2] = this._components;
      this._int = (r2 * 255 << 16) + (g2 * 255 << 8) + (b2 * 255 | 0);
    }
    /**
     * Clamps values to a range. Will override original values
     * @param value - Value(s) to clamp
     * @param min - Minimum value
     * @param max - Maximum value
     */
    _clamp(value, min2 = 0, max2 = 1) {
      return typeof value == "number" ? Math.min(Math.max(value, min2), max2) : (value.forEach((v2, i2) => {
        value[i2] = Math.min(Math.max(v2, min2), max2);
      }), value);
    }
  };
  _Color.shared = new _Color(), /**
  * Temporary Color object for static uses internally.
  * As to not conflict with Color.shared.
  * @ignore
  */
  _Color.temp = new _Color(), /** Pattern for hex strings */
  _Color.HEX_PATTERN = /^(#|0x)?(([a-f0-9]{3}){1,2}([a-f0-9]{2})?)$/i;
  let Color = _Color;
  function mapPremultipliedBlendModes() {
    const pm = [], npm = [];
    for (let i2 = 0; i2 < 32; i2++)
      pm[i2] = i2, npm[i2] = i2;
    pm[BLEND_MODES.NORMAL_NPM] = BLEND_MODES.NORMAL, pm[BLEND_MODES.ADD_NPM] = BLEND_MODES.ADD, pm[BLEND_MODES.SCREEN_NPM] = BLEND_MODES.SCREEN, npm[BLEND_MODES.NORMAL] = BLEND_MODES.NORMAL_NPM, npm[BLEND_MODES.ADD] = BLEND_MODES.ADD_NPM, npm[BLEND_MODES.SCREEN] = BLEND_MODES.SCREEN_NPM;
    const array = [];
    return array.push(npm), array.push(pm), array;
  }
  const premultiplyBlendMode = mapPremultipliedBlendModes();
  function getBufferType(array) {
    if (array.BYTES_PER_ELEMENT === 4)
      return array instanceof Float32Array ? "Float32Array" : array instanceof Uint32Array ? "Uint32Array" : "Int32Array";
    if (array.BYTES_PER_ELEMENT === 2) {
      if (array instanceof Uint16Array)
        return "Uint16Array";
    } else if (array.BYTES_PER_ELEMENT === 1 && array instanceof Uint8Array)
      return "Uint8Array";
    return null;
  }
  function nextPow2(v2) {
    return v2 += v2 === 0 ? 1 : 0, --v2, v2 |= v2 >>> 1, v2 |= v2 >>> 2, v2 |= v2 >>> 4, v2 |= v2 >>> 8, v2 |= v2 >>> 16, v2 + 1;
  }
  function isPow2(v2) {
    return !(v2 & v2 - 1) && !!v2;
  }
  function log2(v2) {
    let r2 = (v2 > 65535 ? 1 : 0) << 4;
    v2 >>>= r2;
    let shift = (v2 > 255 ? 1 : 0) << 3;
    return v2 >>>= shift, r2 |= shift, shift = (v2 > 15 ? 1 : 0) << 2, v2 >>>= shift, r2 |= shift, shift = (v2 > 3 ? 1 : 0) << 1, v2 >>>= shift, r2 |= shift, r2 | v2 >> 1;
  }
  function removeItems(arr, startIdx, removeCount) {
    const length = arr.length;
    let i2;
    if (startIdx >= length || removeCount === 0)
      return;
    removeCount = startIdx + removeCount > length ? length - startIdx : removeCount;
    const len = length - removeCount;
    for (i2 = startIdx; i2 < len; ++i2)
      arr[i2] = arr[i2 + removeCount];
    arr.length = len;
  }
  function sign(n2) {
    return n2 === 0 ? 0 : n2 < 0 ? -1 : 1;
  }
  let nextUid = 0;
  function uid() {
    return ++nextUid;
  }
  const ProgramCache = {}, TextureCache = /* @__PURE__ */ Object.create(null), BaseTextureCache = /* @__PURE__ */ Object.create(null);
  function determineCrossOrigin(url2, loc = globalThis.location) {
    if (url2.startsWith("data:"))
      return "";
    loc = loc || globalThis.location;
    const parsedUrl = new URL(url2, document.baseURI);
    return parsedUrl.hostname !== loc.hostname || parsedUrl.port !== loc.port || parsedUrl.protocol !== loc.protocol ? "anonymous" : "";
  }
  function getResolutionOfUrl(url2, defaultValue2 = 1) {
    const resolution = settings.RETINA_PREFIX?.exec(url2);
    return resolution ? parseFloat(resolution[1]) : defaultValue2;
  }
  var ExtensionType = /* @__PURE__ */ ((ExtensionType2) => (ExtensionType2.Renderer = "renderer", ExtensionType2.Application = "application", ExtensionType2.RendererSystem = "renderer-webgl-system", ExtensionType2.RendererPlugin = "renderer-webgl-plugin", ExtensionType2.CanvasRendererSystem = "renderer-canvas-system", ExtensionType2.CanvasRendererPlugin = "renderer-canvas-plugin", ExtensionType2.Asset = "asset", ExtensionType2.LoadParser = "load-parser", ExtensionType2.ResolveParser = "resolve-parser", ExtensionType2.CacheParser = "cache-parser", ExtensionType2.DetectionParser = "detection-parser", ExtensionType2))(ExtensionType || {});
  const normalizeExtension = (ext) => {
    if (typeof ext == "function" || typeof ext == "object" && ext.extension) {
      if (!ext.extension)
        throw new Error("Extension class must have an extension object");
      ext = { ...typeof ext.extension != "object" ? { type: ext.extension } : ext.extension, ref: ext };
    }
    if (typeof ext == "object")
      ext = { ...ext };
    else
      throw new Error("Invalid extension type");
    return typeof ext.type == "string" && (ext.type = [ext.type]), ext;
  }, normalizePriority = (ext, defaultPriority) => normalizeExtension(ext).priority ?? defaultPriority, extensions = {
    /** @ignore */
    _addHandlers: {},
    /** @ignore */
    _removeHandlers: {},
    /** @ignore */
    _queue: {},
    /**
     * Remove extensions from PixiJS.
     * @param extensions - Extensions to be removed.
     * @returns {PIXI.extensions} For chaining.
     */
    remove(...extensions2) {
      return extensions2.map(normalizeExtension).forEach((ext) => {
        ext.type.forEach((type2) => this._removeHandlers[type2]?.(ext));
      }), this;
    },
    /**
     * Register new extensions with PixiJS.
     * @param extensions - The spread of extensions to add to PixiJS.
     * @returns {PIXI.extensions} For chaining.
     */
    add(...extensions2) {
      return extensions2.map(normalizeExtension).forEach((ext) => {
        ext.type.forEach((type2) => {
          const handlers = this._addHandlers, queue = this._queue;
          handlers[type2] ? handlers[type2]?.(ext) : (queue[type2] = queue[type2] || [], queue[type2]?.push(ext));
        });
      }), this;
    },
    /**
     * Internal method to handle extensions by name.
     * @param type - The extension type.
     * @param onAdd  - Function for handling when extensions are added/registered passes {@link PIXI.ExtensionFormat}.
     * @param onRemove  - Function for handling when extensions are removed/unregistered passes {@link PIXI.ExtensionFormat}.
     * @returns {PIXI.extensions} For chaining.
     */
    handle(type2, onAdd, onRemove) {
      const addHandlers = this._addHandlers, removeHandlers = this._removeHandlers;
      if (addHandlers[type2] || removeHandlers[type2])
        throw new Error(`Extension type ${type2} already has a handler`);
      addHandlers[type2] = onAdd, removeHandlers[type2] = onRemove;
      const queue = this._queue;
      return queue[type2] && (queue[type2]?.forEach((ext) => onAdd(ext)), delete queue[type2]), this;
    },
    /**
     * Handle a type, but using a map by `name` property.
     * @param type - Type of extension to handle.
     * @param map - The object map of named extensions.
     * @returns {PIXI.extensions} For chaining.
     */
    handleByMap(type2, map2) {
      return this.handle(
        type2,
        (extension) => {
          extension.name && (map2[extension.name] = extension.ref);
        },
        (extension) => {
          extension.name && delete map2[extension.name];
        }
      );
    },
    /**
     * Handle a type, but using a list of extensions.
     * @param type - Type of extension to handle.
     * @param list - The list of extensions.
     * @param defaultPriority - The default priority to use if none is specified.
     * @returns {PIXI.extensions} For chaining.
     */
    handleByList(type2, list, defaultPriority = -1) {
      return this.handle(
        type2,
        (extension) => {
          list.includes(extension.ref) || (list.push(extension.ref), list.sort((a2, b2) => normalizePriority(b2, defaultPriority) - normalizePriority(a2, defaultPriority)));
        },
        (extension) => {
          const index = list.indexOf(extension.ref);
          index !== -1 && list.splice(index, 1);
        }
      );
    }
  };
  class ViewableBuffer {
    constructor(sizeOrBuffer) {
      typeof sizeOrBuffer == "number" ? this.rawBinaryData = new ArrayBuffer(sizeOrBuffer) : sizeOrBuffer instanceof Uint8Array ? this.rawBinaryData = sizeOrBuffer.buffer : this.rawBinaryData = sizeOrBuffer, this.uint32View = new Uint32Array(this.rawBinaryData), this.float32View = new Float32Array(this.rawBinaryData);
    }
    /** View on the raw binary data as a `Int8Array`. */
    get int8View() {
      return this._int8View || (this._int8View = new Int8Array(this.rawBinaryData)), this._int8View;
    }
    /** View on the raw binary data as a `Uint8Array`. */
    get uint8View() {
      return this._uint8View || (this._uint8View = new Uint8Array(this.rawBinaryData)), this._uint8View;
    }
    /**  View on the raw binary data as a `Int16Array`. */
    get int16View() {
      return this._int16View || (this._int16View = new Int16Array(this.rawBinaryData)), this._int16View;
    }
    /** View on the raw binary data as a `Uint16Array`. */
    get uint16View() {
      return this._uint16View || (this._uint16View = new Uint16Array(this.rawBinaryData)), this._uint16View;
    }
    /** View on the raw binary data as a `Int32Array`. */
    get int32View() {
      return this._int32View || (this._int32View = new Int32Array(this.rawBinaryData)), this._int32View;
    }
    /**
     * Returns the view of the given type.
     * @param type - One of `int8`, `uint8`, `int16`,
     *    `uint16`, `int32`, `uint32`, and `float32`.
     * @returns - typed array of given type
     */
    view(type2) {
      return this[`${type2}View`];
    }
    /** Destroys all buffer references. Do not use after calling this. */
    destroy() {
      this.rawBinaryData = null, this._int8View = null, this._uint8View = null, this._int16View = null, this._uint16View = null, this._int32View = null, this.uint32View = null, this.float32View = null;
    }
    static sizeOf(type2) {
      switch (type2) {
        case "int8":
        case "uint8":
          return 1;
        case "int16":
        case "uint16":
          return 2;
        case "int32":
        case "uint32":
        case "float32":
          return 4;
        default:
          throw new Error(`${type2} isn't a valid view type`);
      }
    }
  }
  const fragTemplate$1 = [
    "precision mediump float;",
    "void main(void){",
    "float test = 0.1;",
    "%forloop%",
    "gl_FragColor = vec4(0.0);",
    "}"
  ].join(`
`);
  function generateIfTestSrc(maxIfs) {
    let src = "";
    for (let i2 = 0; i2 < maxIfs; ++i2)
      i2 > 0 && (src += `
else `), i2 < maxIfs - 1 && (src += `if(test == ${i2}.0){}`);
    return src;
  }
  function checkMaxIfStatementsInShader(maxIfs, gl) {
    if (maxIfs === 0)
      throw new Error("Invalid value of `0` passed to `checkMaxIfStatementsInShader`");
    const shader = gl.createShader(gl.FRAGMENT_SHADER);
    for (; ; ) {
      const fragmentSrc = fragTemplate$1.replace(/%forloop%/gi, generateIfTestSrc(maxIfs));
      if (gl.shaderSource(shader, fragmentSrc), gl.compileShader(shader), !gl.getShaderParameter(shader, gl.COMPILE_STATUS))
        maxIfs = maxIfs / 2 | 0;
      else
        break;
    }
    return maxIfs;
  }
  const BLEND$1 = 0, OFFSET$1 = 1, CULLING$1 = 2, DEPTH_TEST$1 = 3, WINDING$1 = 4, DEPTH_MASK$1 = 5;
  class State {
    constructor() {
      this.data = 0, this.blendMode = BLEND_MODES.NORMAL, this.polygonOffset = 0, this.blend = true, this.depthMask = true;
    }
    /**
     * Activates blending of the computed fragment color values.
     * @default true
     */
    get blend() {
      return !!(this.data & 1 << BLEND$1);
    }
    set blend(value) {
      !!(this.data & 1 << BLEND$1) !== value && (this.data ^= 1 << BLEND$1);
    }
    /**
     * Activates adding an offset to depth values of polygon's fragments
     * @default false
     */
    get offsets() {
      return !!(this.data & 1 << OFFSET$1);
    }
    set offsets(value) {
      !!(this.data & 1 << OFFSET$1) !== value && (this.data ^= 1 << OFFSET$1);
    }
    /**
     * Activates culling of polygons.
     * @default false
     */
    get culling() {
      return !!(this.data & 1 << CULLING$1);
    }
    set culling(value) {
      !!(this.data & 1 << CULLING$1) !== value && (this.data ^= 1 << CULLING$1);
    }
    /**
     * Activates depth comparisons and updates to the depth buffer.
     * @default false
     */
    get depthTest() {
      return !!(this.data & 1 << DEPTH_TEST$1);
    }
    set depthTest(value) {
      !!(this.data & 1 << DEPTH_TEST$1) !== value && (this.data ^= 1 << DEPTH_TEST$1);
    }
    /**
     * Enables or disables writing to the depth buffer.
     * @default true
     */
    get depthMask() {
      return !!(this.data & 1 << DEPTH_MASK$1);
    }
    set depthMask(value) {
      !!(this.data & 1 << DEPTH_MASK$1) !== value && (this.data ^= 1 << DEPTH_MASK$1);
    }
    /**
     * Specifies whether or not front or back-facing polygons can be culled.
     * @default false
     */
    get clockwiseFrontFace() {
      return !!(this.data & 1 << WINDING$1);
    }
    set clockwiseFrontFace(value) {
      !!(this.data & 1 << WINDING$1) !== value && (this.data ^= 1 << WINDING$1);
    }
    /**
     * The blend mode to be applied when this state is set. Apply a value of `PIXI.BLEND_MODES.NORMAL` to reset the blend mode.
     * Setting this mode to anything other than NO_BLEND will automatically switch blending on.
     * @default PIXI.BLEND_MODES.NORMAL
     */
    get blendMode() {
      return this._blendMode;
    }
    set blendMode(value) {
      this.blend = value !== BLEND_MODES.NONE, this._blendMode = value;
    }
    /**
     * The polygon offset. Setting this property to anything other than 0 will automatically enable polygon offset fill.
     * @default 0
     */
    get polygonOffset() {
      return this._polygonOffset;
    }
    set polygonOffset(value) {
      this.offsets = !!value, this._polygonOffset = value;
    }
    static for2d() {
      const state = new State();
      return state.depthTest = false, state.blend = true, state;
    }
  }
  State.prototype.toString = function() {
    return `[@pixi/core:State blendMode=${this.blendMode} clockwiseFrontFace=${this.clockwiseFrontFace} culling=${this.culling} depthMask=${this.depthMask} polygonOffset=${this.polygonOffset}]`;
  };
  const INSTALLED = [];
  function autoDetectResource(source, options) {
    if (!source)
      return null;
    let extension = "";
    if (typeof source == "string") {
      const result = /\.(\w{3,4})(?:$|\?|#)/i.exec(source);
      result && (extension = result[1].toLowerCase());
    }
    for (let i2 = INSTALLED.length - 1; i2 >= 0; --i2) {
      const ResourcePlugin = INSTALLED[i2];
      if (ResourcePlugin.test && ResourcePlugin.test(source, extension))
        return new ResourcePlugin(source, options);
    }
    throw new Error("Unrecognized source type to auto-detect Resource");
  }
  class Runner {
    /**
     * @param {string} name - The function name that will be executed on the listeners added to this Runner.
     */
    constructor(name) {
      this.items = [], this._name = name, this._aliasCount = 0;
    }
    /* eslint-disable jsdoc/require-param, jsdoc/check-param-names */
    /**
     * Dispatch/Broadcast Runner to all listeners added to the queue.
     * @param {...any} params - (optional) parameters to pass to each listener
     */
    /*  eslint-enable jsdoc/require-param, jsdoc/check-param-names */
    emit(a0, a1, a2, a3, a4, a5, a6, a7) {
      if (arguments.length > 8)
        throw new Error("max arguments reached");
      const { name, items } = this;
      this._aliasCount++;
      for (let i2 = 0, len = items.length; i2 < len; i2++)
        items[i2][name](a0, a1, a2, a3, a4, a5, a6, a7);
      return items === this.items && this._aliasCount--, this;
    }
    ensureNonAliasedItems() {
      this._aliasCount > 0 && this.items.length > 1 && (this._aliasCount = 0, this.items = this.items.slice(0));
    }
    /**
     * Add a listener to the Runner
     *
     * Runners do not need to have scope or functions passed to them.
     * All that is required is to pass the listening object and ensure that it has contains a function that has the same name
     * as the name provided to the Runner when it was created.
     *
     * E.g. A listener passed to this Runner will require a 'complete' function.
     *
     * ```js
     * import { Runner } from '@pixi/runner';
     *
     * const complete = new Runner('complete');
     * ```
     *
     * The scope used will be the object itself.
     * @param {any} item - The object that will be listening.
     */
    add(item) {
      return item[this._name] && (this.ensureNonAliasedItems(), this.remove(item), this.items.push(item)), this;
    }
    /**
     * Remove a single listener from the dispatch queue.
     * @param {any} item - The listener that you would like to remove.
     */
    remove(item) {
      const index = this.items.indexOf(item);
      return index !== -1 && (this.ensureNonAliasedItems(), this.items.splice(index, 1)), this;
    }
    /**
     * Check to see if the listener is already in the Runner
     * @param {any} item - The listener that you would like to check.
     */
    contains(item) {
      return this.items.includes(item);
    }
    /** Remove all listeners from the Runner */
    removeAll() {
      return this.ensureNonAliasedItems(), this.items.length = 0, this;
    }
    /** Remove all references, don't use after this. */
    destroy() {
      this.removeAll(), this.items.length = 0, this._name = "";
    }
    /**
     * `true` if there are no this Runner contains no listeners
     * @readonly
     */
    get empty() {
      return this.items.length === 0;
    }
    /**
     * The name of the runner.
     * @type {string}
     */
    get name() {
      return this._name;
    }
  }
  Object.defineProperties(Runner.prototype, {
    /**
     * Alias for `emit`
     * @memberof PIXI.Runner#
     * @method dispatch
     * @see PIXI.Runner#emit
     */
    dispatch: { value: Runner.prototype.emit },
    /**
     * Alias for `emit`
     * @memberof PIXI.Runner#
     * @method run
     * @see PIXI.Runner#emit
     */
    run: { value: Runner.prototype.emit }
  });
  class Resource {
    /**
     * @param width - Width of the resource
     * @param height - Height of the resource
     */
    constructor(width = 0, height = 0) {
      this._width = width, this._height = height, this.destroyed = false, this.internal = false, this.onResize = new Runner("setRealSize"), this.onUpdate = new Runner("update"), this.onError = new Runner("onError");
    }
    /**
     * Bind to a parent BaseTexture
     * @param baseTexture - Parent texture
     */
    bind(baseTexture) {
      this.onResize.add(baseTexture), this.onUpdate.add(baseTexture), this.onError.add(baseTexture), (this._width || this._height) && this.onResize.emit(this._width, this._height);
    }
    /**
     * Unbind to a parent BaseTexture
     * @param baseTexture - Parent texture
     */
    unbind(baseTexture) {
      this.onResize.remove(baseTexture), this.onUpdate.remove(baseTexture), this.onError.remove(baseTexture);
    }
    /**
     * Trigger a resize event
     * @param width - X dimension
     * @param height - Y dimension
     */
    resize(width, height) {
      (width !== this._width || height !== this._height) && (this._width = width, this._height = height, this.onResize.emit(width, height));
    }
    /**
     * Has been validated
     * @readonly
     */
    get valid() {
      return !!this._width && !!this._height;
    }
    /** Has been updated trigger event. */
    update() {
      this.destroyed || this.onUpdate.emit();
    }
    /**
     * This can be overridden to start preloading a resource
     * or do any other prepare step.
     * @protected
     * @returns Handle the validate event
     */
    load() {
      return Promise.resolve(this);
    }
    /**
     * The width of the resource.
     * @readonly
     */
    get width() {
      return this._width;
    }
    /**
     * The height of the resource.
     * @readonly
     */
    get height() {
      return this._height;
    }
    /**
     * Set the style, optional to override
     * @param _renderer - yeah, renderer!
     * @param _baseTexture - the texture
     * @param _glTexture - texture instance for this webgl context
     * @returns - `true` is success
     */
    style(_renderer, _baseTexture, _glTexture) {
      return false;
    }
    /** Clean up anything, this happens when destroying is ready. */
    dispose() {
    }
    /**
     * Call when destroying resource, unbind any BaseTexture object
     * before calling this method, as reference counts are maintained
     * internally.
     */
    destroy() {
      this.destroyed || (this.destroyed = true, this.dispose(), this.onError.removeAll(), this.onError = null, this.onResize.removeAll(), this.onResize = null, this.onUpdate.removeAll(), this.onUpdate = null);
    }
    /**
     * Abstract, used to auto-detect resource type.
     * @param {*} _source - The source object
     * @param {string} _extension - The extension of source, if set
     */
    static test(_source, _extension) {
      return false;
    }
  }
  class BufferResource extends Resource {
    /**
     * @param source - Source buffer
     * @param options - Options
     * @param {number} options.width - Width of the texture
     * @param {number} options.height - Height of the texture
     * @param {1|2|4|8} [options.unpackAlignment=4] - The alignment of the pixel rows.
     */
    constructor(source, options) {
      const { width, height } = options || {};
      if (!width || !height)
        throw new Error("BufferResource width or height invalid");
      super(width, height), this.data = source, this.unpackAlignment = options.unpackAlignment ?? 4;
    }
    /**
     * Upload the texture to the GPU.
     * @param renderer - Upload to the renderer
     * @param baseTexture - Reference to parent texture
     * @param glTexture - glTexture
     * @returns - true is success
     */
    upload(renderer, baseTexture, glTexture) {
      const gl = renderer.gl;
      gl.pixelStorei(gl.UNPACK_ALIGNMENT, this.unpackAlignment), gl.pixelStorei(gl.UNPACK_PREMULTIPLY_ALPHA_WEBGL, baseTexture.alphaMode === ALPHA_MODES.UNPACK);
      const width = baseTexture.realWidth, height = baseTexture.realHeight;
      return glTexture.width === width && glTexture.height === height ? gl.texSubImage2D(
        baseTexture.target,
        0,
        0,
        0,
        width,
        height,
        baseTexture.format,
        glTexture.type,
        this.data
      ) : (glTexture.width = width, glTexture.height = height, gl.texImage2D(
        baseTexture.target,
        0,
        glTexture.internalFormat,
        width,
        height,
        0,
        baseTexture.format,
        glTexture.type,
        this.data
      )), true;
    }
    /** Destroy and don't use after this. */
    dispose() {
      this.data = null;
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @returns {boolean} `true` if buffer source
     */
    static test(source) {
      return source === null || source instanceof Int8Array || source instanceof Uint8Array || source instanceof Uint8ClampedArray || source instanceof Int16Array || source instanceof Uint16Array || source instanceof Int32Array || source instanceof Uint32Array || source instanceof Float32Array;
    }
  }
  const defaultBufferOptions = {
    scaleMode: SCALE_MODES.NEAREST,
    alphaMode: ALPHA_MODES.NPM
  }, _BaseTexture = class _BaseTexture2 extends EventEmitter {
    /**
     * @param {PIXI.Resource|PIXI.ImageSource|string} [resource=null] -
     *        The current resource to use, for things that aren't Resource objects, will be converted
     *        into a Resource.
     * @param options - Collection of options, default options inherited from {@link PIXI.BaseTexture.defaultOptions}.
     * @param {PIXI.MIPMAP_MODES} [options.mipmap] - If mipmapping is enabled for texture
     * @param {number} [options.anisotropicLevel] - Anisotropic filtering level of texture
     * @param {PIXI.WRAP_MODES} [options.wrapMode] - Wrap mode for textures
     * @param {PIXI.SCALE_MODES} [options.scaleMode] - Default scale mode, linear, nearest
     * @param {PIXI.FORMATS} [options.format] - GL format type
     * @param {PIXI.TYPES} [options.type] - GL data type
     * @param {PIXI.TARGETS} [options.target] - GL texture target
     * @param {PIXI.ALPHA_MODES} [options.alphaMode] - Pre multiply the image alpha
     * @param {number} [options.width=0] - Width of the texture
     * @param {number} [options.height=0] - Height of the texture
     * @param {number} [options.resolution=PIXI.settings.RESOLUTION] - Resolution of the base texture
     * @param {object} [options.resourceOptions] - Optional resource options,
     *        see {@link PIXI.autoDetectResource autoDetectResource}
     */
    constructor(resource = null, options = null) {
      super(), options = Object.assign({}, _BaseTexture2.defaultOptions, options);
      const {
        alphaMode,
        mipmap,
        anisotropicLevel,
        scaleMode,
        width,
        height,
        wrapMode,
        format,
        type: type2,
        target,
        resolution,
        resourceOptions
      } = options;
      resource && !(resource instanceof Resource) && (resource = autoDetectResource(resource, resourceOptions), resource.internal = true), this.resolution = resolution || settings.RESOLUTION, this.width = Math.round((width || 0) * this.resolution) / this.resolution, this.height = Math.round((height || 0) * this.resolution) / this.resolution, this._mipmap = mipmap, this.anisotropicLevel = anisotropicLevel, this._wrapMode = wrapMode, this._scaleMode = scaleMode, this.format = format, this.type = type2, this.target = target, this.alphaMode = alphaMode, this.uid = uid(), this.touched = 0, this.isPowerOfTwo = false, this._refreshPOT(), this._glTextures = {}, this.dirtyId = 0, this.dirtyStyleId = 0, this.cacheId = null, this.valid = width > 0 && height > 0, this.textureCacheIds = [], this.destroyed = false, this.resource = null, this._batchEnabled = 0, this._batchLocation = 0, this.parentTextureArray = null, this.setResource(resource);
    }
    /**
     * Pixel width of the source of this texture
     * @readonly
     */
    get realWidth() {
      return Math.round(this.width * this.resolution);
    }
    /**
     * Pixel height of the source of this texture
     * @readonly
     */
    get realHeight() {
      return Math.round(this.height * this.resolution);
    }
    /**
     * Mipmap mode of the texture, affects downscaled images
     * @default PIXI.MIPMAP_MODES.POW2
     */
    get mipmap() {
      return this._mipmap;
    }
    set mipmap(value) {
      this._mipmap !== value && (this._mipmap = value, this.dirtyStyleId++);
    }
    /**
     * The scale mode to apply when scaling this texture
     * @default PIXI.SCALE_MODES.LINEAR
     */
    get scaleMode() {
      return this._scaleMode;
    }
    set scaleMode(value) {
      this._scaleMode !== value && (this._scaleMode = value, this.dirtyStyleId++);
    }
    /**
     * How the texture wraps
     * @default PIXI.WRAP_MODES.CLAMP
     */
    get wrapMode() {
      return this._wrapMode;
    }
    set wrapMode(value) {
      this._wrapMode !== value && (this._wrapMode = value, this.dirtyStyleId++);
    }
    /**
     * Changes style options of BaseTexture
     * @param scaleMode - Pixi scalemode
     * @param mipmap - enable mipmaps
     * @returns - this
     */
    setStyle(scaleMode, mipmap) {
      let dirty;
      return scaleMode !== void 0 && scaleMode !== this.scaleMode && (this.scaleMode = scaleMode, dirty = true), mipmap !== void 0 && mipmap !== this.mipmap && (this.mipmap = mipmap, dirty = true), dirty && this.dirtyStyleId++, this;
    }
    /**
     * Changes w/h/resolution. Texture becomes valid if width and height are greater than zero.
     * @param desiredWidth - Desired visual width
     * @param desiredHeight - Desired visual height
     * @param resolution - Optionally set resolution
     * @returns - this
     */
    setSize(desiredWidth, desiredHeight, resolution) {
      return resolution = resolution || this.resolution, this.setRealSize(desiredWidth * resolution, desiredHeight * resolution, resolution);
    }
    /**
     * Sets real size of baseTexture, preserves current resolution.
     * @param realWidth - Full rendered width
     * @param realHeight - Full rendered height
     * @param resolution - Optionally set resolution
     * @returns - this
     */
    setRealSize(realWidth, realHeight, resolution) {
      return this.resolution = resolution || this.resolution, this.width = Math.round(realWidth) / this.resolution, this.height = Math.round(realHeight) / this.resolution, this._refreshPOT(), this.update(), this;
    }
    /**
     * Refresh check for isPowerOfTwo texture based on size
     * @private
     */
    _refreshPOT() {
      this.isPowerOfTwo = isPow2(this.realWidth) && isPow2(this.realHeight);
    }
    /**
     * Changes resolution
     * @param resolution - res
     * @returns - this
     */
    setResolution(resolution) {
      const oldResolution = this.resolution;
      return oldResolution === resolution ? this : (this.resolution = resolution, this.valid && (this.width = Math.round(this.width * oldResolution) / resolution, this.height = Math.round(this.height * oldResolution) / resolution, this.emit("update", this)), this._refreshPOT(), this);
    }
    /**
     * Sets the resource if it wasn't set. Throws error if resource already present
     * @param resource - that is managing this BaseTexture
     * @returns - this
     */
    setResource(resource) {
      if (this.resource === resource)
        return this;
      if (this.resource)
        throw new Error("Resource can be set only once");
      return resource.bind(this), this.resource = resource, this;
    }
    /** Invalidates the object. Texture becomes valid if width and height are greater than zero. */
    update() {
      this.valid ? (this.dirtyId++, this.dirtyStyleId++, this.emit("update", this)) : this.width > 0 && this.height > 0 && (this.valid = true, this.emit("loaded", this), this.emit("update", this));
    }
    /**
     * Handle errors with resources.
     * @private
     * @param event - Error event emitted.
     */
    onError(event) {
      this.emit("error", this, event);
    }
    /**
     * Destroys this base texture.
     * The method stops if resource doesn't want this texture to be destroyed.
     * Removes texture from all caches.
     * @fires PIXI.BaseTexture#destroyed
     */
    destroy() {
      this.resource && (this.resource.unbind(this), this.resource.internal && this.resource.destroy(), this.resource = null), this.cacheId && (delete BaseTextureCache[this.cacheId], delete TextureCache[this.cacheId], this.cacheId = null), this.valid = false, this.dispose(), _BaseTexture2.removeFromCache(this), this.textureCacheIds = null, this.destroyed = true, this.emit("destroyed", this), this.removeAllListeners();
    }
    /**
     * Frees the texture from WebGL memory without destroying this texture object.
     * This means you can still use the texture later which will upload it to GPU
     * memory again.
     * @fires PIXI.BaseTexture#dispose
     */
    dispose() {
      this.emit("dispose", this);
    }
    /** Utility function for BaseTexture|Texture cast. */
    castToBaseTexture() {
      return this;
    }
    /**
     * Helper function that creates a base texture based on the source you provide.
     * The source can be - image url, image element, canvas element. If the
     * source is an image url or an image element and not in the base texture
     * cache, it will be created and loaded.
     * @static
     * @param {PIXI.ImageSource|string|string[]} source - The
     *        source to create base texture from.
     * @param options - See {@link PIXI.BaseTexture}'s constructor for options.
     * @param {string} [options.pixiIdPrefix=pixiid] - If a source has no id, this is the prefix of the generated id
     * @param {boolean} [strict] - Enforce strict-mode, see {@link PIXI.settings.STRICT_TEXTURE_CACHE}.
     * @returns {PIXI.BaseTexture} The new base texture.
     */
    static from(source, options, strict = settings.STRICT_TEXTURE_CACHE) {
      const isFrame = typeof source == "string";
      let cacheId = null;
      if (isFrame)
        cacheId = source;
      else {
        if (!source._pixiId) {
          const prefix = options?.pixiIdPrefix || "pixiid";
          source._pixiId = `${prefix}_${uid()}`;
        }
        cacheId = source._pixiId;
      }
      let baseTexture = BaseTextureCache[cacheId];
      if (isFrame && strict && !baseTexture)
        throw new Error(`The cacheId "${cacheId}" does not exist in BaseTextureCache.`);
      return baseTexture || (baseTexture = new _BaseTexture2(source, options), baseTexture.cacheId = cacheId, _BaseTexture2.addToCache(baseTexture, cacheId)), baseTexture;
    }
    /**
     * Create a new Texture with a BufferResource from a typed array.
     * @param buffer - The optional array to use. If no data is provided, a new Float32Array is created.
     * @param width - Width of the resource
     * @param height - Height of the resource
     * @param options - See {@link PIXI.BaseTexture}'s constructor for options.
     *        Default properties are different from the constructor's defaults.
     * @param {PIXI.FORMATS} [options.format] - The format is not given, the type is inferred from the
     *        type of the buffer: `RGBA` if Float32Array, Int8Array, Uint8Array, or Uint8ClampedArray,
     *        otherwise `RGBA_INTEGER`.
     * @param {PIXI.TYPES} [options.type] - The type is not given, the type is inferred from the
     *        type of the buffer. Maps Float32Array to `FLOAT`, Int32Array to `INT`, Uint32Array to
     *        `UNSIGNED_INT`, Int16Array to `SHORT`, Uint16Array to `UNSIGNED_SHORT`, Int8Array to `BYTE`,
     *        Uint8Array/Uint8ClampedArray to `UNSIGNED_BYTE`.
     * @param {PIXI.ALPHA_MODES} [options.alphaMode=PIXI.ALPHA_MODES.NPM]
     * @param {PIXI.SCALE_MODES} [options.scaleMode=PIXI.SCALE_MODES.NEAREST]
     * @returns - The resulting new BaseTexture
     */
    static fromBuffer(buffer, width, height, options) {
      buffer = buffer || new Float32Array(width * height * 4);
      const resource = new BufferResource(buffer, { width, height, ...options?.resourceOptions });
      let format, type2;
      return buffer instanceof Float32Array ? (format = FORMATS.RGBA, type2 = TYPES.FLOAT) : buffer instanceof Int32Array ? (format = FORMATS.RGBA_INTEGER, type2 = TYPES.INT) : buffer instanceof Uint32Array ? (format = FORMATS.RGBA_INTEGER, type2 = TYPES.UNSIGNED_INT) : buffer instanceof Int16Array ? (format = FORMATS.RGBA_INTEGER, type2 = TYPES.SHORT) : buffer instanceof Uint16Array ? (format = FORMATS.RGBA_INTEGER, type2 = TYPES.UNSIGNED_SHORT) : buffer instanceof Int8Array ? (format = FORMATS.RGBA, type2 = TYPES.BYTE) : (format = FORMATS.RGBA, type2 = TYPES.UNSIGNED_BYTE), resource.internal = true, new _BaseTexture2(resource, Object.assign({}, defaultBufferOptions, { type: type2, format }, options));
    }
    /**
     * Adds a BaseTexture to the global BaseTextureCache. This cache is shared across the whole PIXI object.
     * @param {PIXI.BaseTexture} baseTexture - The BaseTexture to add to the cache.
     * @param {string} id - The id that the BaseTexture will be stored against.
     */
    static addToCache(baseTexture, id) {
      id && (baseTexture.textureCacheIds.includes(id) || baseTexture.textureCacheIds.push(id), BaseTextureCache[id] && BaseTextureCache[id] !== baseTexture && console.warn(`BaseTexture added to the cache with an id [${id}] that already had an entry`), BaseTextureCache[id] = baseTexture);
    }
    /**
     * Remove a BaseTexture from the global BaseTextureCache.
     * @param {string|PIXI.BaseTexture} baseTexture - id of a BaseTexture to be removed, or a BaseTexture instance itself.
     * @returns {PIXI.BaseTexture|null} The BaseTexture that was removed.
     */
    static removeFromCache(baseTexture) {
      if (typeof baseTexture == "string") {
        const baseTextureFromCache = BaseTextureCache[baseTexture];
        if (baseTextureFromCache) {
          const index = baseTextureFromCache.textureCacheIds.indexOf(baseTexture);
          return index > -1 && baseTextureFromCache.textureCacheIds.splice(index, 1), delete BaseTextureCache[baseTexture], baseTextureFromCache;
        }
      } else if (baseTexture?.textureCacheIds) {
        for (let i2 = 0; i2 < baseTexture.textureCacheIds.length; ++i2)
          delete BaseTextureCache[baseTexture.textureCacheIds[i2]];
        return baseTexture.textureCacheIds.length = 0, baseTexture;
      }
      return null;
    }
  };
  _BaseTexture.defaultOptions = {
    /**
     * If mipmapping is enabled for texture.
     * @type {PIXI.MIPMAP_MODES}
     * @default PIXI.MIPMAP_MODES.POW2
     */
    mipmap: MIPMAP_MODES.POW2,
    /** Anisotropic filtering level of texture */
    anisotropicLevel: 0,
    /**
     * Default scale mode, linear, nearest.
     * @type {PIXI.SCALE_MODES}
     * @default PIXI.SCALE_MODES.LINEAR
     */
    scaleMode: SCALE_MODES.LINEAR,
    /**
     * Wrap mode for textures.
     * @type {PIXI.WRAP_MODES}
     * @default PIXI.WRAP_MODES.CLAMP
     */
    wrapMode: WRAP_MODES.CLAMP,
    /**
     * Pre multiply the image alpha
     * @type {PIXI.ALPHA_MODES}
     * @default PIXI.ALPHA_MODES.UNPACK
     */
    alphaMode: ALPHA_MODES.UNPACK,
    /**
     * GL texture target
     * @type {PIXI.TARGETS}
     * @default PIXI.TARGETS.TEXTURE_2D
     */
    target: TARGETS.TEXTURE_2D,
    /**
     * GL format type
     * @type {PIXI.FORMATS}
     * @default PIXI.FORMATS.RGBA
     */
    format: FORMATS.RGBA,
    /**
     * GL data type
     * @type {PIXI.TYPES}
     * @default PIXI.TYPES.UNSIGNED_BYTE
     */
    type: TYPES.UNSIGNED_BYTE
  }, /** Global number of the texture batch, used by multi-texture renderers. */
  _BaseTexture._globalBatch = 0;
  let BaseTexture = _BaseTexture;
  class BatchDrawCall {
    constructor() {
      this.texArray = null, this.blend = 0, this.type = DRAW_MODES.TRIANGLES, this.start = 0, this.size = 0, this.data = null;
    }
  }
  let UID$4 = 0;
  class Buffer {
    /**
     * @param {PIXI.IArrayBuffer} data - the data to store in the buffer.
     * @param _static - `true` for static buffer
     * @param index - `true` for index buffer
     */
    constructor(data, _static = true, index = false) {
      this.data = data || new Float32Array(1), this._glBuffers = {}, this._updateID = 0, this.index = index, this.static = _static, this.id = UID$4++, this.disposeRunner = new Runner("disposeBuffer");
    }
    // TODO could explore flagging only a partial upload?
    /**
     * Flags this buffer as requiring an upload to the GPU.
     * @param {PIXI.IArrayBuffer|number[]} [data] - the data to update in the buffer.
     */
    update(data) {
      data instanceof Array && (data = new Float32Array(data)), this.data = data || this.data, this._updateID++;
    }
    /** Disposes WebGL resources that are connected to this geometry. */
    dispose() {
      this.disposeRunner.emit(this, false);
    }
    /** Destroys the buffer. */
    destroy() {
      this.dispose(), this.data = null;
    }
    /**
     * Flags whether this is an index buffer.
     *
     * Index buffers are of type `ELEMENT_ARRAY_BUFFER`. Note that setting this property to false will make
     * the buffer of type `ARRAY_BUFFER`.
     *
     * For backwards compatibility.
     */
    set index(value) {
      this.type = value ? BUFFER_TYPE.ELEMENT_ARRAY_BUFFER : BUFFER_TYPE.ARRAY_BUFFER;
    }
    get index() {
      return this.type === BUFFER_TYPE.ELEMENT_ARRAY_BUFFER;
    }
    /**
     * Helper function that creates a buffer based on an array or TypedArray
     * @param {ArrayBufferView | number[]} data - the TypedArray that the buffer will store. If this is a regular Array it will be converted to a Float32Array.
     * @returns - A new Buffer based on the data provided.
     */
    static from(data) {
      return data instanceof Array && (data = new Float32Array(data)), new Buffer(data);
    }
  }
  class Attribute {
    /**
     * @param buffer - the id of the buffer that this attribute will look for
     * @param size - the size of the attribute. If you have 2 floats per vertex (eg position x and y) this would be 2.
     * @param normalized - should the data be normalized.
     * @param {PIXI.TYPES} [type=PIXI.TYPES.FLOAT] - what type of number is the attribute. Check {@link PIXI.TYPES} to see the ones available
     * @param [stride=0] - How far apart, in bytes, the start of each value is. (used for interleaving data)
     * @param [start=0] - How far into the array to start reading values (used for interleaving data)
     * @param [instance=false] - Whether the geometry is instanced.
     * @param [divisor=1] - Divisor to use when doing instanced rendering
     */
    constructor(buffer, size = 0, normalized = false, type2 = TYPES.FLOAT, stride, start, instance, divisor = 1) {
      this.buffer = buffer, this.size = size, this.normalized = normalized, this.type = type2, this.stride = stride, this.start = start, this.instance = instance, this.divisor = divisor;
    }
    /** Destroys the Attribute. */
    destroy() {
      this.buffer = null;
    }
    /**
     * Helper function that creates an Attribute based on the information provided
     * @param buffer - the id of the buffer that this attribute will look for
     * @param [size=0] - the size of the attribute. If you have 2 floats per vertex (eg position x and y) this would be 2
     * @param [normalized=false] - should the data be normalized.
     * @param [type=PIXI.TYPES.FLOAT] - what type of number is the attribute. Check {@link PIXI.TYPES} to see the ones available
     * @param [stride=0] - How far apart, in bytes, the start of each value is. (used for interleaving data)
     * @returns - A new {@link PIXI.Attribute} based on the information provided
     */
    static from(buffer, size, normalized, type2, stride) {
      return new Attribute(buffer, size, normalized, type2, stride);
    }
  }
  const map$1 = {
    Float32Array,
    Uint32Array,
    Int32Array,
    Uint8Array
  };
  function interleaveTypedArrays(arrays, sizes) {
    let outSize = 0, stride = 0;
    const views = {};
    for (let i2 = 0; i2 < arrays.length; i2++)
      stride += sizes[i2], outSize += arrays[i2].length;
    const buffer = new ArrayBuffer(outSize * 4);
    let out = null, littleOffset = 0;
    for (let i2 = 0; i2 < arrays.length; i2++) {
      const size = sizes[i2], array = arrays[i2], type2 = getBufferType(array);
      views[type2] || (views[type2] = new map$1[type2](buffer)), out = views[type2];
      for (let j2 = 0; j2 < array.length; j2++) {
        const indexStart = (j2 / size | 0) * stride + littleOffset, index = j2 % size;
        out[indexStart + index] = array[j2];
      }
      littleOffset += size;
    }
    return new Float32Array(buffer);
  }
  const byteSizeMap$1 = { 5126: 4, 5123: 2, 5121: 1 };
  let UID$3 = 0;
  const map = {
    Float32Array,
    Uint32Array,
    Int32Array,
    Uint8Array,
    Uint16Array
  };
  class Geometry {
    /**
     * @param buffers - An array of buffers. optional.
     * @param attributes - Of the geometry, optional structure of the attributes layout
     */
    constructor(buffers = [], attributes = {}) {
      this.buffers = buffers, this.indexBuffer = null, this.attributes = attributes, this.glVertexArrayObjects = {}, this.id = UID$3++, this.instanced = false, this.instanceCount = 1, this.disposeRunner = new Runner("disposeGeometry"), this.refCount = 0;
    }
    /**
     *
     * Adds an attribute to the geometry
     * Note: `stride` and `start` should be `undefined` if you dont know them, not 0!
     * @param id - the name of the attribute (matching up to a shader)
     * @param {PIXI.Buffer|number[]} buffer - the buffer that holds the data of the attribute . You can also provide an Array and a buffer will be created from it.
     * @param size - the size of the attribute. If you have 2 floats per vertex (eg position x and y) this would be 2
     * @param normalized - should the data be normalized.
     * @param [type=PIXI.TYPES.FLOAT] - what type of number is the attribute. Check {@link PIXI.TYPES} to see the ones available
     * @param [stride=0] - How far apart, in bytes, the start of each value is. (used for interleaving data)
     * @param [start=0] - How far into the array to start reading values (used for interleaving data)
     * @param instance - Instancing flag
     * @returns - Returns self, useful for chaining.
     */
    addAttribute(id, buffer, size = 0, normalized = false, type2, stride, start, instance = false) {
      if (!buffer)
        throw new Error("You must pass a buffer when creating an attribute");
      buffer instanceof Buffer || (buffer instanceof Array && (buffer = new Float32Array(buffer)), buffer = new Buffer(buffer));
      const ids = id.split("|");
      if (ids.length > 1) {
        for (let i2 = 0; i2 < ids.length; i2++)
          this.addAttribute(ids[i2], buffer, size, normalized, type2);
        return this;
      }
      let bufferIndex = this.buffers.indexOf(buffer);
      return bufferIndex === -1 && (this.buffers.push(buffer), bufferIndex = this.buffers.length - 1), this.attributes[id] = new Attribute(bufferIndex, size, normalized, type2, stride, start, instance), this.instanced = this.instanced || instance, this;
    }
    /**
     * Returns the requested attribute.
     * @param id - The name of the attribute required
     * @returns - The attribute requested.
     */
    getAttribute(id) {
      return this.attributes[id];
    }
    /**
     * Returns the requested buffer.
     * @param id - The name of the buffer required.
     * @returns - The buffer requested.
     */
    getBuffer(id) {
      return this.buffers[this.getAttribute(id).buffer];
    }
    /**
     *
     * Adds an index buffer to the geometry
     * The index buffer contains integers, three for each triangle in the geometry, which reference the various attribute buffers (position, colour, UV coordinates, other UV coordinates, normal, …). There is only ONE index buffer.
     * @param {PIXI.Buffer|number[]} [buffer] - The buffer that holds the data of the index buffer. You can also provide an Array and a buffer will be created from it.
     * @returns - Returns self, useful for chaining.
     */
    addIndex(buffer) {
      return buffer instanceof Buffer || (buffer instanceof Array && (buffer = new Uint16Array(buffer)), buffer = new Buffer(buffer)), buffer.type = BUFFER_TYPE.ELEMENT_ARRAY_BUFFER, this.indexBuffer = buffer, this.buffers.includes(buffer) || this.buffers.push(buffer), this;
    }
    /**
     * Returns the index buffer
     * @returns - The index buffer.
     */
    getIndex() {
      return this.indexBuffer;
    }
    /**
     * This function modifies the structure so that all current attributes become interleaved into a single buffer
     * This can be useful if your model remains static as it offers a little performance boost
     * @returns - Returns self, useful for chaining.
     */
    interleave() {
      if (this.buffers.length === 1 || this.buffers.length === 2 && this.indexBuffer)
        return this;
      const arrays = [], sizes = [], interleavedBuffer = new Buffer();
      let i2;
      for (i2 in this.attributes) {
        const attribute = this.attributes[i2], buffer = this.buffers[attribute.buffer];
        arrays.push(buffer.data), sizes.push(attribute.size * byteSizeMap$1[attribute.type] / 4), attribute.buffer = 0;
      }
      for (interleavedBuffer.data = interleaveTypedArrays(arrays, sizes), i2 = 0; i2 < this.buffers.length; i2++)
        this.buffers[i2] !== this.indexBuffer && this.buffers[i2].destroy();
      return this.buffers = [interleavedBuffer], this.indexBuffer && this.buffers.push(this.indexBuffer), this;
    }
    /** Get the size of the geometries, in vertices. */
    getSize() {
      for (const i2 in this.attributes) {
        const attribute = this.attributes[i2];
        return this.buffers[attribute.buffer].data.length / (attribute.stride / 4 || attribute.size);
      }
      return 0;
    }
    /** Disposes WebGL resources that are connected to this geometry. */
    dispose() {
      this.disposeRunner.emit(this, false);
    }
    /** Destroys the geometry. */
    destroy() {
      this.dispose(), this.buffers = null, this.indexBuffer = null, this.attributes = null;
    }
    /**
     * Returns a clone of the geometry.
     * @returns - A new clone of this geometry.
     */
    clone() {
      const geometry = new Geometry();
      for (let i2 = 0; i2 < this.buffers.length; i2++)
        geometry.buffers[i2] = new Buffer(this.buffers[i2].data.slice(0));
      for (const i2 in this.attributes) {
        const attrib = this.attributes[i2];
        geometry.attributes[i2] = new Attribute(
          attrib.buffer,
          attrib.size,
          attrib.normalized,
          attrib.type,
          attrib.stride,
          attrib.start,
          attrib.instance
        );
      }
      return this.indexBuffer && (geometry.indexBuffer = geometry.buffers[this.buffers.indexOf(this.indexBuffer)], geometry.indexBuffer.type = BUFFER_TYPE.ELEMENT_ARRAY_BUFFER), geometry;
    }
    /**
     * Merges an array of geometries into a new single one.
     *
     * Geometry attribute styles must match for this operation to work.
     * @param geometries - array of geometries to merge
     * @returns - Shiny new geometry!
     */
    static merge(geometries) {
      const geometryOut = new Geometry(), arrays = [], sizes = [], offsets = [];
      let geometry;
      for (let i2 = 0; i2 < geometries.length; i2++) {
        geometry = geometries[i2];
        for (let j2 = 0; j2 < geometry.buffers.length; j2++)
          sizes[j2] = sizes[j2] || 0, sizes[j2] += geometry.buffers[j2].data.length, offsets[j2] = 0;
      }
      for (let i2 = 0; i2 < geometry.buffers.length; i2++)
        arrays[i2] = new map[getBufferType(geometry.buffers[i2].data)](sizes[i2]), geometryOut.buffers[i2] = new Buffer(arrays[i2]);
      for (let i2 = 0; i2 < geometries.length; i2++) {
        geometry = geometries[i2];
        for (let j2 = 0; j2 < geometry.buffers.length; j2++)
          arrays[j2].set(geometry.buffers[j2].data, offsets[j2]), offsets[j2] += geometry.buffers[j2].data.length;
      }
      if (geometryOut.attributes = geometry.attributes, geometry.indexBuffer) {
        geometryOut.indexBuffer = geometryOut.buffers[geometry.buffers.indexOf(geometry.indexBuffer)], geometryOut.indexBuffer.type = BUFFER_TYPE.ELEMENT_ARRAY_BUFFER;
        let offset = 0, stride = 0, offset2 = 0, bufferIndexToCount = 0;
        for (let i2 = 0; i2 < geometry.buffers.length; i2++)
          if (geometry.buffers[i2] !== geometry.indexBuffer) {
            bufferIndexToCount = i2;
            break;
          }
        for (const i2 in geometry.attributes) {
          const attribute = geometry.attributes[i2];
          (attribute.buffer | 0) === bufferIndexToCount && (stride += attribute.size * byteSizeMap$1[attribute.type] / 4);
        }
        for (let i2 = 0; i2 < geometries.length; i2++) {
          const indexBufferData = geometries[i2].indexBuffer.data;
          for (let j2 = 0; j2 < indexBufferData.length; j2++)
            geometryOut.indexBuffer.data[j2 + offset2] += offset;
          offset += geometries[i2].buffers[bufferIndexToCount].data.length / stride, offset2 += indexBufferData.length;
        }
      }
      return geometryOut;
    }
  }
  class BatchGeometry extends Geometry {
    /**
     * @param {boolean} [_static=false] - Optimization flag, where `false`
     *        is updated every frame, `true` doesn't change frame-to-frame.
     */
    constructor(_static = false) {
      super(), this._buffer = new Buffer(null, _static, false), this._indexBuffer = new Buffer(null, _static, true), this.addAttribute("aVertexPosition", this._buffer, 2, false, TYPES.FLOAT).addAttribute("aTextureCoord", this._buffer, 2, false, TYPES.FLOAT).addAttribute("aColor", this._buffer, 4, true, TYPES.UNSIGNED_BYTE).addAttribute("aTextureId", this._buffer, 1, true, TYPES.FLOAT).addIndex(this._indexBuffer);
    }
  }
  const PI_2 = Math.PI * 2, RAD_TO_DEG = 180 / Math.PI, DEG_TO_RAD = Math.PI / 180;
  var SHAPES = /* @__PURE__ */ ((SHAPES2) => (SHAPES2[SHAPES2.POLY = 0] = "POLY", SHAPES2[SHAPES2.RECT = 1] = "RECT", SHAPES2[SHAPES2.CIRC = 2] = "CIRC", SHAPES2[SHAPES2.ELIP = 3] = "ELIP", SHAPES2[SHAPES2.RREC = 4] = "RREC", SHAPES2))(SHAPES || {});
  class Point {
    /**
     * Creates a new `Point`
     * @param {number} [x=0] - position of the point on the x axis
     * @param {number} [y=0] - position of the point on the y axis
     */
    constructor(x2 = 0, y2 = 0) {
      this.x = 0, this.y = 0, this.x = x2, this.y = y2;
    }
    /**
     * Creates a clone of this point
     * @returns A clone of this point
     */
    clone() {
      return new Point(this.x, this.y);
    }
    /**
     * Copies `x` and `y` from the given point into this point
     * @param p - The point to copy from
     * @returns The point instance itself
     */
    copyFrom(p2) {
      return this.set(p2.x, p2.y), this;
    }
    /**
     * Copies this point's x and y into the given point (`p`).
     * @param p - The point to copy to. Can be any of type that is or extends `IPointData`
     * @returns The point (`p`) with values updated
     */
    copyTo(p2) {
      return p2.set(this.x, this.y), p2;
    }
    /**
     * Accepts another point (`p`) and returns `true` if the given point is equal to this point
     * @param p - The point to check
     * @returns Returns `true` if both `x` and `y` are equal
     */
    equals(p2) {
      return p2.x === this.x && p2.y === this.y;
    }
    /**
     * Sets the point to a new `x` and `y` position.
     * If `y` is omitted, both `x` and `y` will be set to `x`.
     * @param {number} [x=0] - position of the point on the `x` axis
     * @param {number} [y=x] - position of the point on the `y` axis
     * @returns The point instance itself
     */
    set(x2 = 0, y2 = x2) {
      return this.x = x2, this.y = y2, this;
    }
  }
  Point.prototype.toString = function() {
    return `[@pixi/math:Point x=${this.x} y=${this.y}]`;
  };
  const tempPoints$1 = [new Point(), new Point(), new Point(), new Point()];
  class Rectangle {
    /**
     * @param x - The X coordinate of the upper-left corner of the rectangle
     * @param y - The Y coordinate of the upper-left corner of the rectangle
     * @param width - The overall width of the rectangle
     * @param height - The overall height of the rectangle
     */
    constructor(x2 = 0, y2 = 0, width = 0, height = 0) {
      this.x = Number(x2), this.y = Number(y2), this.width = Number(width), this.height = Number(height), this.type = SHAPES.RECT;
    }
    /** Returns the left edge of the rectangle. */
    get left() {
      return this.x;
    }
    /** Returns the right edge of the rectangle. */
    get right() {
      return this.x + this.width;
    }
    /** Returns the top edge of the rectangle. */
    get top() {
      return this.y;
    }
    /** Returns the bottom edge of the rectangle. */
    get bottom() {
      return this.y + this.height;
    }
    /** A constant empty rectangle. */
    static get EMPTY() {
      return new Rectangle(0, 0, 0, 0);
    }
    /**
     * Creates a clone of this Rectangle
     * @returns a copy of the rectangle
     */
    clone() {
      return new Rectangle(this.x, this.y, this.width, this.height);
    }
    /**
     * Copies another rectangle to this one.
     * @param rectangle - The rectangle to copy from.
     * @returns Returns itself.
     */
    copyFrom(rectangle) {
      return this.x = rectangle.x, this.y = rectangle.y, this.width = rectangle.width, this.height = rectangle.height, this;
    }
    /**
     * Copies this rectangle to another one.
     * @param rectangle - The rectangle to copy to.
     * @returns Returns given parameter.
     */
    copyTo(rectangle) {
      return rectangle.x = this.x, rectangle.y = this.y, rectangle.width = this.width, rectangle.height = this.height, rectangle;
    }
    /**
     * Checks whether the x and y coordinates given are contained within this Rectangle
     * @param x - The X coordinate of the point to test
     * @param y - The Y coordinate of the point to test
     * @returns Whether the x/y coordinates are within this Rectangle
     */
    contains(x2, y2) {
      return this.width <= 0 || this.height <= 0 ? false : x2 >= this.x && x2 < this.x + this.width && y2 >= this.y && y2 < this.y + this.height;
    }
    /**
     * Determines whether the `other` Rectangle transformed by `transform` intersects with `this` Rectangle object.
     * Returns true only if the area of the intersection is >0, this means that Rectangles
     * sharing a side are not overlapping. Another side effect is that an arealess rectangle
     * (width or height equal to zero) can't intersect any other rectangle.
     * @param {Rectangle} other - The Rectangle to intersect with `this`.
     * @param {Matrix} transform - The transformation matrix of `other`.
     * @returns {boolean} A value of `true` if the transformed `other` Rectangle intersects with `this`; otherwise `false`.
     */
    intersects(other, transform) {
      if (!transform) {
        const x02 = this.x < other.x ? other.x : this.x;
        if ((this.right > other.right ? other.right : this.right) <= x02)
          return false;
        const y02 = this.y < other.y ? other.y : this.y;
        return (this.bottom > other.bottom ? other.bottom : this.bottom) > y02;
      }
      const x0 = this.left, x1 = this.right, y0 = this.top, y1 = this.bottom;
      if (x1 <= x0 || y1 <= y0)
        return false;
      const lt2 = tempPoints$1[0].set(other.left, other.top), lb = tempPoints$1[1].set(other.left, other.bottom), rt2 = tempPoints$1[2].set(other.right, other.top), rb = tempPoints$1[3].set(other.right, other.bottom);
      if (rt2.x <= lt2.x || lb.y <= lt2.y)
        return false;
      const s2 = Math.sign(transform.a * transform.d - transform.b * transform.c);
      if (s2 === 0 || (transform.apply(lt2, lt2), transform.apply(lb, lb), transform.apply(rt2, rt2), transform.apply(rb, rb), Math.max(lt2.x, lb.x, rt2.x, rb.x) <= x0 || Math.min(lt2.x, lb.x, rt2.x, rb.x) >= x1 || Math.max(lt2.y, lb.y, rt2.y, rb.y) <= y0 || Math.min(lt2.y, lb.y, rt2.y, rb.y) >= y1))
        return false;
      const nx = s2 * (lb.y - lt2.y), ny = s2 * (lt2.x - lb.x), n00 = nx * x0 + ny * y0, n10 = nx * x1 + ny * y0, n01 = nx * x0 + ny * y1, n11 = nx * x1 + ny * y1;
      if (Math.max(n00, n10, n01, n11) <= nx * lt2.x + ny * lt2.y || Math.min(n00, n10, n01, n11) >= nx * rb.x + ny * rb.y)
        return false;
      const mx = s2 * (lt2.y - rt2.y), my = s2 * (rt2.x - lt2.x), m00 = mx * x0 + my * y0, m10 = mx * x1 + my * y0, m01 = mx * x0 + my * y1, m11 = mx * x1 + my * y1;
      return !(Math.max(m00, m10, m01, m11) <= mx * lt2.x + my * lt2.y || Math.min(m00, m10, m01, m11) >= mx * rb.x + my * rb.y);
    }
    /**
     * Pads the rectangle making it grow in all directions.
     * If paddingY is omitted, both paddingX and paddingY will be set to paddingX.
     * @param paddingX - The horizontal padding amount.
     * @param paddingY - The vertical padding amount.
     * @returns Returns itself.
     */
    pad(paddingX = 0, paddingY = paddingX) {
      return this.x -= paddingX, this.y -= paddingY, this.width += paddingX * 2, this.height += paddingY * 2, this;
    }
    /**
     * Fits this rectangle around the passed one.
     * @param rectangle - The rectangle to fit.
     * @returns Returns itself.
     */
    fit(rectangle) {
      const x1 = Math.max(this.x, rectangle.x), x2 = Math.min(this.x + this.width, rectangle.x + rectangle.width), y1 = Math.max(this.y, rectangle.y), y2 = Math.min(this.y + this.height, rectangle.y + rectangle.height);
      return this.x = x1, this.width = Math.max(x2 - x1, 0), this.y = y1, this.height = Math.max(y2 - y1, 0), this;
    }
    /**
     * Enlarges rectangle that way its corners lie on grid
     * @param resolution - resolution
     * @param eps - precision
     * @returns Returns itself.
     */
    ceil(resolution = 1, eps = 1e-3) {
      const x2 = Math.ceil((this.x + this.width - eps) * resolution) / resolution, y2 = Math.ceil((this.y + this.height - eps) * resolution) / resolution;
      return this.x = Math.floor((this.x + eps) * resolution) / resolution, this.y = Math.floor((this.y + eps) * resolution) / resolution, this.width = x2 - this.x, this.height = y2 - this.y, this;
    }
    /**
     * Enlarges this rectangle to include the passed rectangle.
     * @param rectangle - The rectangle to include.
     * @returns Returns itself.
     */
    enlarge(rectangle) {
      const x1 = Math.min(this.x, rectangle.x), x2 = Math.max(this.x + this.width, rectangle.x + rectangle.width), y1 = Math.min(this.y, rectangle.y), y2 = Math.max(this.y + this.height, rectangle.y + rectangle.height);
      return this.x = x1, this.width = x2 - x1, this.y = y1, this.height = y2 - y1, this;
    }
  }
  Rectangle.prototype.toString = function() {
    return `[@pixi/math:Rectangle x=${this.x} y=${this.y} width=${this.width} height=${this.height}]`;
  };
  class Matrix {
    /**
     * @param a - x scale
     * @param b - y skew
     * @param c - x skew
     * @param d - y scale
     * @param tx - x translation
     * @param ty - y translation
     */
    constructor(a2 = 1, b2 = 0, c2 = 0, d2 = 1, tx = 0, ty = 0) {
      this.array = null, this.a = a2, this.b = b2, this.c = c2, this.d = d2, this.tx = tx, this.ty = ty;
    }
    /**
     * Creates a Matrix object based on the given array. The Element to Matrix mapping order is as follows:
     *
     * a = array[0]
     * b = array[1]
     * c = array[3]
     * d = array[4]
     * tx = array[2]
     * ty = array[5]
     * @param array - The array that the matrix will be populated from.
     */
    fromArray(array) {
      this.a = array[0], this.b = array[1], this.c = array[3], this.d = array[4], this.tx = array[2], this.ty = array[5];
    }
    /**
     * Sets the matrix properties.
     * @param a - Matrix component
     * @param b - Matrix component
     * @param c - Matrix component
     * @param d - Matrix component
     * @param tx - Matrix component
     * @param ty - Matrix component
     * @returns This matrix. Good for chaining method calls.
     */
    set(a2, b2, c2, d2, tx, ty) {
      return this.a = a2, this.b = b2, this.c = c2, this.d = d2, this.tx = tx, this.ty = ty, this;
    }
    /**
     * Creates an array from the current Matrix object.
     * @param transpose - Whether we need to transpose the matrix or not
     * @param [out=new Float32Array(9)] - If provided the array will be assigned to out
     * @returns The newly created array which contains the matrix
     */
    toArray(transpose, out) {
      this.array || (this.array = new Float32Array(9));
      const array = out || this.array;
      return transpose ? (array[0] = this.a, array[1] = this.b, array[2] = 0, array[3] = this.c, array[4] = this.d, array[5] = 0, array[6] = this.tx, array[7] = this.ty, array[8] = 1) : (array[0] = this.a, array[1] = this.c, array[2] = this.tx, array[3] = this.b, array[4] = this.d, array[5] = this.ty, array[6] = 0, array[7] = 0, array[8] = 1), array;
    }
    /**
     * Get a new position with the current transformation applied.
     * Can be used to go from a child's coordinate space to the world coordinate space. (e.g. rendering)
     * @param pos - The origin
     * @param {PIXI.Point} [newPos] - The point that the new position is assigned to (allowed to be same as input)
     * @returns {PIXI.Point} The new point, transformed through this matrix
     */
    apply(pos, newPos) {
      newPos = newPos || new Point();
      const x2 = pos.x, y2 = pos.y;
      return newPos.x = this.a * x2 + this.c * y2 + this.tx, newPos.y = this.b * x2 + this.d * y2 + this.ty, newPos;
    }
    /**
     * Get a new position with the inverse of the current transformation applied.
     * Can be used to go from the world coordinate space to a child's coordinate space. (e.g. input)
     * @param pos - The origin
     * @param {PIXI.Point} [newPos] - The point that the new position is assigned to (allowed to be same as input)
     * @returns {PIXI.Point} The new point, inverse-transformed through this matrix
     */
    applyInverse(pos, newPos) {
      newPos = newPos || new Point();
      const id = 1 / (this.a * this.d + this.c * -this.b), x2 = pos.x, y2 = pos.y;
      return newPos.x = this.d * id * x2 + -this.c * id * y2 + (this.ty * this.c - this.tx * this.d) * id, newPos.y = this.a * id * y2 + -this.b * id * x2 + (-this.ty * this.a + this.tx * this.b) * id, newPos;
    }
    /**
     * Translates the matrix on the x and y.
     * @param x - How much to translate x by
     * @param y - How much to translate y by
     * @returns This matrix. Good for chaining method calls.
     */
    translate(x2, y2) {
      return this.tx += x2, this.ty += y2, this;
    }
    /**
     * Applies a scale transformation to the matrix.
     * @param x - The amount to scale horizontally
     * @param y - The amount to scale vertically
     * @returns This matrix. Good for chaining method calls.
     */
    scale(x2, y2) {
      return this.a *= x2, this.d *= y2, this.c *= x2, this.b *= y2, this.tx *= x2, this.ty *= y2, this;
    }
    /**
     * Applies a rotation transformation to the matrix.
     * @param angle - The angle in radians.
     * @returns This matrix. Good for chaining method calls.
     */
    rotate(angle) {
      const cos = Math.cos(angle), sin = Math.sin(angle), a1 = this.a, c1 = this.c, tx1 = this.tx;
      return this.a = a1 * cos - this.b * sin, this.b = a1 * sin + this.b * cos, this.c = c1 * cos - this.d * sin, this.d = c1 * sin + this.d * cos, this.tx = tx1 * cos - this.ty * sin, this.ty = tx1 * sin + this.ty * cos, this;
    }
    /**
     * Appends the given Matrix to this Matrix.
     * @param matrix - The matrix to append.
     * @returns This matrix. Good for chaining method calls.
     */
    append(matrix) {
      const a1 = this.a, b1 = this.b, c1 = this.c, d1 = this.d;
      return this.a = matrix.a * a1 + matrix.b * c1, this.b = matrix.a * b1 + matrix.b * d1, this.c = matrix.c * a1 + matrix.d * c1, this.d = matrix.c * b1 + matrix.d * d1, this.tx = matrix.tx * a1 + matrix.ty * c1 + this.tx, this.ty = matrix.tx * b1 + matrix.ty * d1 + this.ty, this;
    }
    /**
     * Sets the matrix based on all the available properties
     * @param x - Position on the x axis
     * @param y - Position on the y axis
     * @param pivotX - Pivot on the x axis
     * @param pivotY - Pivot on the y axis
     * @param scaleX - Scale on the x axis
     * @param scaleY - Scale on the y axis
     * @param rotation - Rotation in radians
     * @param skewX - Skew on the x axis
     * @param skewY - Skew on the y axis
     * @returns This matrix. Good for chaining method calls.
     */
    setTransform(x2, y2, pivotX, pivotY, scaleX, scaleY, rotation, skewX, skewY) {
      return this.a = Math.cos(rotation + skewY) * scaleX, this.b = Math.sin(rotation + skewY) * scaleX, this.c = -Math.sin(rotation - skewX) * scaleY, this.d = Math.cos(rotation - skewX) * scaleY, this.tx = x2 - (pivotX * this.a + pivotY * this.c), this.ty = y2 - (pivotX * this.b + pivotY * this.d), this;
    }
    /**
     * Prepends the given Matrix to this Matrix.
     * @param matrix - The matrix to prepend
     * @returns This matrix. Good for chaining method calls.
     */
    prepend(matrix) {
      const tx1 = this.tx;
      if (matrix.a !== 1 || matrix.b !== 0 || matrix.c !== 0 || matrix.d !== 1) {
        const a1 = this.a, c1 = this.c;
        this.a = a1 * matrix.a + this.b * matrix.c, this.b = a1 * matrix.b + this.b * matrix.d, this.c = c1 * matrix.a + this.d * matrix.c, this.d = c1 * matrix.b + this.d * matrix.d;
      }
      return this.tx = tx1 * matrix.a + this.ty * matrix.c + matrix.tx, this.ty = tx1 * matrix.b + this.ty * matrix.d + matrix.ty, this;
    }
    /**
     * Decomposes the matrix (x, y, scaleX, scaleY, and rotation) and sets the properties on to a transform.
     * @param transform - The transform to apply the properties to.
     * @returns The transform with the newly applied properties
     */
    decompose(transform) {
      const a2 = this.a, b2 = this.b, c2 = this.c, d2 = this.d, pivot = transform.pivot, skewX = -Math.atan2(-c2, d2), skewY = Math.atan2(b2, a2), delta = Math.abs(skewX + skewY);
      return delta < 1e-5 || Math.abs(PI_2 - delta) < 1e-5 ? (transform.rotation = skewY, transform.skew.x = transform.skew.y = 0) : (transform.rotation = 0, transform.skew.x = skewX, transform.skew.y = skewY), transform.scale.x = Math.sqrt(a2 * a2 + b2 * b2), transform.scale.y = Math.sqrt(c2 * c2 + d2 * d2), transform.position.x = this.tx + (pivot.x * a2 + pivot.y * c2), transform.position.y = this.ty + (pivot.x * b2 + pivot.y * d2), transform;
    }
    /**
     * Inverts this matrix
     * @returns This matrix. Good for chaining method calls.
     */
    invert() {
      const a1 = this.a, b1 = this.b, c1 = this.c, d1 = this.d, tx1 = this.tx, n2 = a1 * d1 - b1 * c1;
      return this.a = d1 / n2, this.b = -b1 / n2, this.c = -c1 / n2, this.d = a1 / n2, this.tx = (c1 * this.ty - d1 * tx1) / n2, this.ty = -(a1 * this.ty - b1 * tx1) / n2, this;
    }
    /**
     * Resets this Matrix to an identity (default) matrix.
     * @returns This matrix. Good for chaining method calls.
     */
    identity() {
      return this.a = 1, this.b = 0, this.c = 0, this.d = 1, this.tx = 0, this.ty = 0, this;
    }
    /**
     * Creates a new Matrix object with the same values as this one.
     * @returns A copy of this matrix. Good for chaining method calls.
     */
    clone() {
      const matrix = new Matrix();
      return matrix.a = this.a, matrix.b = this.b, matrix.c = this.c, matrix.d = this.d, matrix.tx = this.tx, matrix.ty = this.ty, matrix;
    }
    /**
     * Changes the values of the given matrix to be the same as the ones in this matrix
     * @param matrix - The matrix to copy to.
     * @returns The matrix given in parameter with its values updated.
     */
    copyTo(matrix) {
      return matrix.a = this.a, matrix.b = this.b, matrix.c = this.c, matrix.d = this.d, matrix.tx = this.tx, matrix.ty = this.ty, matrix;
    }
    /**
     * Changes the values of the matrix to be the same as the ones in given matrix
     * @param {PIXI.Matrix} matrix - The matrix to copy from.
     * @returns {PIXI.Matrix} this
     */
    copyFrom(matrix) {
      return this.a = matrix.a, this.b = matrix.b, this.c = matrix.c, this.d = matrix.d, this.tx = matrix.tx, this.ty = matrix.ty, this;
    }
    /**
     * A default (identity) matrix
     * @readonly
     */
    static get IDENTITY() {
      return new Matrix();
    }
    /**
     * A temp matrix
     * @readonly
     */
    static get TEMP_MATRIX() {
      return new Matrix();
    }
  }
  Matrix.prototype.toString = function() {
    return `[@pixi/math:Matrix a=${this.a} b=${this.b} c=${this.c} d=${this.d} tx=${this.tx} ty=${this.ty}]`;
  };
  const ux = [1, 1, 0, -1, -1, -1, 0, 1, 1, 1, 0, -1, -1, -1, 0, 1], uy = [0, 1, 1, 1, 0, -1, -1, -1, 0, 1, 1, 1, 0, -1, -1, -1], vx = [0, -1, -1, -1, 0, 1, 1, 1, 0, 1, 1, 1, 0, -1, -1, -1], vy = [1, 1, 0, -1, -1, -1, 0, 1, -1, -1, 0, 1, 1, 1, 0, -1], rotationCayley = [], rotationMatrices = [], signum = Math.sign;
  function init() {
    for (let i2 = 0; i2 < 16; i2++) {
      const row = [];
      rotationCayley.push(row);
      for (let j2 = 0; j2 < 16; j2++) {
        const _ux = signum(ux[i2] * ux[j2] + vx[i2] * uy[j2]), _uy = signum(uy[i2] * ux[j2] + vy[i2] * uy[j2]), _vx = signum(ux[i2] * vx[j2] + vx[i2] * vy[j2]), _vy = signum(uy[i2] * vx[j2] + vy[i2] * vy[j2]);
        for (let k2 = 0; k2 < 16; k2++)
          if (ux[k2] === _ux && uy[k2] === _uy && vx[k2] === _vx && vy[k2] === _vy) {
            row.push(k2);
            break;
          }
      }
    }
    for (let i2 = 0; i2 < 16; i2++) {
      const mat = new Matrix();
      mat.set(ux[i2], uy[i2], vx[i2], vy[i2], 0, 0), rotationMatrices.push(mat);
    }
  }
  init();
  const groupD8 = {
    /**
     * | Rotation | Direction |
     * |----------|-----------|
     * | 0°       | East      |
     * @readonly
     */
    E: 0,
    /**
     * | Rotation | Direction |
     * |----------|-----------|
     * | 45°↻     | Southeast |
     * @readonly
     */
    SE: 1,
    /**
     * | Rotation | Direction |
     * |----------|-----------|
     * | 90°↻     | South     |
     * @readonly
     */
    S: 2,
    /**
     * | Rotation | Direction |
     * |----------|-----------|
     * | 135°↻    | Southwest |
     * @readonly
     */
    SW: 3,
    /**
     * | Rotation | Direction |
     * |----------|-----------|
     * | 180°     | West      |
     * @readonly
     */
    W: 4,
    /**
     * | Rotation    | Direction    |
     * |-------------|--------------|
     * | -135°/225°↻ | Northwest    |
     * @readonly
     */
    NW: 5,
    /**
     * | Rotation    | Direction    |
     * |-------------|--------------|
     * | -90°/270°↻  | North        |
     * @readonly
     */
    N: 6,
    /**
     * | Rotation    | Direction    |
     * |-------------|--------------|
     * | -45°/315°↻  | Northeast    |
     * @readonly
     */
    NE: 7,
    /**
     * Reflection about Y-axis.
     * @readonly
     */
    MIRROR_VERTICAL: 8,
    /**
     * Reflection about the main diagonal.
     * @readonly
     */
    MAIN_DIAGONAL: 10,
    /**
     * Reflection about X-axis.
     * @readonly
     */
    MIRROR_HORIZONTAL: 12,
    /**
     * Reflection about reverse diagonal.
     * @readonly
     */
    REVERSE_DIAGONAL: 14,
    /**
     * @param {PIXI.GD8Symmetry} ind - sprite rotation angle.
     * @returns {PIXI.GD8Symmetry} The X-component of the U-axis
     *    after rotating the axes.
     */
    uX: (ind) => ux[ind],
    /**
     * @param {PIXI.GD8Symmetry} ind - sprite rotation angle.
     * @returns {PIXI.GD8Symmetry} The Y-component of the U-axis
     *    after rotating the axes.
     */
    uY: (ind) => uy[ind],
    /**
     * @param {PIXI.GD8Symmetry} ind - sprite rotation angle.
     * @returns {PIXI.GD8Symmetry} The X-component of the V-axis
     *    after rotating the axes.
     */
    vX: (ind) => vx[ind],
    /**
     * @param {PIXI.GD8Symmetry} ind - sprite rotation angle.
     * @returns {PIXI.GD8Symmetry} The Y-component of the V-axis
     *    after rotating the axes.
     */
    vY: (ind) => vy[ind],
    /**
     * @param {PIXI.GD8Symmetry} rotation - symmetry whose opposite
     *   is needed. Only rotations have opposite symmetries while
     *   reflections don't.
     * @returns {PIXI.GD8Symmetry} The opposite symmetry of `rotation`
     */
    inv: (rotation) => rotation & 8 ? rotation & 15 : -rotation & 7,
    /**
     * Composes the two D8 operations.
     *
     * Taking `^` as reflection:
     *
     * |       | E=0 | S=2 | W=4 | N=6 | E^=8 | S^=10 | W^=12 | N^=14 |
     * |-------|-----|-----|-----|-----|------|-------|-------|-------|
     * | E=0   | E   | S   | W   | N   | E^   | S^    | W^    | N^    |
     * | S=2   | S   | W   | N   | E   | S^   | W^    | N^    | E^    |
     * | W=4   | W   | N   | E   | S   | W^   | N^    | E^    | S^    |
     * | N=6   | N   | E   | S   | W   | N^   | E^    | S^    | W^    |
     * | E^=8  | E^  | N^  | W^  | S^  | E    | N     | W     | S     |
     * | S^=10 | S^  | E^  | N^  | W^  | S    | E     | N     | W     |
     * | W^=12 | W^  | S^  | E^  | N^  | W    | S     | E     | N     |
     * | N^=14 | N^  | W^  | S^  | E^  | N    | W     | S     | E     |
     *
     * [This is a Cayley table]{@link https://en.wikipedia.org/wiki/Cayley_table}
     * @param {PIXI.GD8Symmetry} rotationSecond - Second operation, which
     *   is the row in the above cayley table.
     * @param {PIXI.GD8Symmetry} rotationFirst - First operation, which
     *   is the column in the above cayley table.
     * @returns {PIXI.GD8Symmetry} Composed operation
     */
    add: (rotationSecond, rotationFirst) => rotationCayley[rotationSecond][rotationFirst],
    /**
     * Reverse of `add`.
     * @param {PIXI.GD8Symmetry} rotationSecond - Second operation
     * @param {PIXI.GD8Symmetry} rotationFirst - First operation
     * @returns {PIXI.GD8Symmetry} Result
     */
    sub: (rotationSecond, rotationFirst) => rotationCayley[rotationSecond][groupD8.inv(rotationFirst)],
    /**
     * Adds 180 degrees to rotation, which is a commutative
     * operation.
     * @param {number} rotation - The number to rotate.
     * @returns {number} Rotated number
     */
    rotate180: (rotation) => rotation ^ 4,
    /**
     * Checks if the rotation angle is vertical, i.e. south
     * or north. It doesn't work for reflections.
     * @param {PIXI.GD8Symmetry} rotation - The number to check.
     * @returns {boolean} Whether or not the direction is vertical
     */
    isVertical: (rotation) => (rotation & 3) === 2,
    // rotation % 4 === 2
    /**
     * Approximates the vector `V(dx,dy)` into one of the
     * eight directions provided by `groupD8`.
     * @param {number} dx - X-component of the vector
     * @param {number} dy - Y-component of the vector
     * @returns {PIXI.GD8Symmetry} Approximation of the vector into
     *  one of the eight symmetries.
     */
    byDirection: (dx, dy) => Math.abs(dx) * 2 <= Math.abs(dy) ? dy >= 0 ? groupD8.S : groupD8.N : Math.abs(dy) * 2 <= Math.abs(dx) ? dx > 0 ? groupD8.E : groupD8.W : dy > 0 ? dx > 0 ? groupD8.SE : groupD8.SW : dx > 0 ? groupD8.NE : groupD8.NW,
    /**
     * Helps sprite to compensate texture packer rotation.
     * @param {PIXI.Matrix} matrix - sprite world matrix
     * @param {PIXI.GD8Symmetry} rotation - The rotation factor to use.
     * @param {number} tx - sprite anchoring
     * @param {number} ty - sprite anchoring
     */
    matrixAppendRotationInv: (matrix, rotation, tx = 0, ty = 0) => {
      const mat = rotationMatrices[groupD8.inv(rotation)];
      mat.tx = tx, mat.ty = ty, matrix.append(mat);
    }
  };
  class ObservablePoint {
    /**
     * Creates a new `ObservablePoint`
     * @param cb - callback function triggered when `x` and/or `y` are changed
     * @param scope - owner of callback
     * @param {number} [x=0] - position of the point on the x axis
     * @param {number} [y=0] - position of the point on the y axis
     */
    constructor(cb, scope, x2 = 0, y2 = 0) {
      this._x = x2, this._y = y2, this.cb = cb, this.scope = scope;
    }
    /**
     * Creates a clone of this point.
     * The callback and scope params can be overridden otherwise they will default
     * to the clone object's values.
     * @override
     * @param cb - The callback function triggered when `x` and/or `y` are changed
     * @param scope - The owner of the callback
     * @returns a copy of this observable point
     */
    clone(cb = this.cb, scope = this.scope) {
      return new ObservablePoint(cb, scope, this._x, this._y);
    }
    /**
     * Sets the point to a new `x` and `y` position.
     * If `y` is omitted, both `x` and `y` will be set to `x`.
     * @param {number} [x=0] - position of the point on the x axis
     * @param {number} [y=x] - position of the point on the y axis
     * @returns The observable point instance itself
     */
    set(x2 = 0, y2 = x2) {
      return (this._x !== x2 || this._y !== y2) && (this._x = x2, this._y = y2, this.cb.call(this.scope)), this;
    }
    /**
     * Copies x and y from the given point (`p`)
     * @param p - The point to copy from. Can be any of type that is or extends `IPointData`
     * @returns The observable point instance itself
     */
    copyFrom(p2) {
      return (this._x !== p2.x || this._y !== p2.y) && (this._x = p2.x, this._y = p2.y, this.cb.call(this.scope)), this;
    }
    /**
     * Copies this point's x and y into that of the given point (`p`)
     * @param p - The point to copy to. Can be any of type that is or extends `IPointData`
     * @returns The point (`p`) with values updated
     */
    copyTo(p2) {
      return p2.set(this._x, this._y), p2;
    }
    /**
     * Accepts another point (`p`) and returns `true` if the given point is equal to this point
     * @param p - The point to check
     * @returns Returns `true` if both `x` and `y` are equal
     */
    equals(p2) {
      return p2.x === this._x && p2.y === this._y;
    }
    /** Position of the observable point on the x axis. */
    get x() {
      return this._x;
    }
    set x(value) {
      this._x !== value && (this._x = value, this.cb.call(this.scope));
    }
    /** Position of the observable point on the y axis. */
    get y() {
      return this._y;
    }
    set y(value) {
      this._y !== value && (this._y = value, this.cb.call(this.scope));
    }
  }
  ObservablePoint.prototype.toString = function() {
    return `[@pixi/math:ObservablePoint x=${this.x} y=${this.y} scope=${this.scope}]`;
  };
  const _Transform = class {
    constructor() {
      this.worldTransform = new Matrix(), this.localTransform = new Matrix(), this.position = new ObservablePoint(this.onChange, this, 0, 0), this.scale = new ObservablePoint(this.onChange, this, 1, 1), this.pivot = new ObservablePoint(this.onChange, this, 0, 0), this.skew = new ObservablePoint(this.updateSkew, this, 0, 0), this._rotation = 0, this._cx = 1, this._sx = 0, this._cy = 0, this._sy = 1, this._localID = 0, this._currentLocalID = 0, this._worldID = 0, this._parentID = 0;
    }
    /** Called when a value changes. */
    onChange() {
      this._localID++;
    }
    /** Called when the skew or the rotation changes. */
    updateSkew() {
      this._cx = Math.cos(this._rotation + this.skew.y), this._sx = Math.sin(this._rotation + this.skew.y), this._cy = -Math.sin(this._rotation - this.skew.x), this._sy = Math.cos(this._rotation - this.skew.x), this._localID++;
    }
    /** Updates the local transformation matrix. */
    updateLocalTransform() {
      const lt2 = this.localTransform;
      this._localID !== this._currentLocalID && (lt2.a = this._cx * this.scale.x, lt2.b = this._sx * this.scale.x, lt2.c = this._cy * this.scale.y, lt2.d = this._sy * this.scale.y, lt2.tx = this.position.x - (this.pivot.x * lt2.a + this.pivot.y * lt2.c), lt2.ty = this.position.y - (this.pivot.x * lt2.b + this.pivot.y * lt2.d), this._currentLocalID = this._localID, this._parentID = -1);
    }
    /**
     * Updates the local and the world transformation matrices.
     * @param parentTransform - The parent transform
     */
    updateTransform(parentTransform) {
      const lt2 = this.localTransform;
      if (this._localID !== this._currentLocalID && (lt2.a = this._cx * this.scale.x, lt2.b = this._sx * this.scale.x, lt2.c = this._cy * this.scale.y, lt2.d = this._sy * this.scale.y, lt2.tx = this.position.x - (this.pivot.x * lt2.a + this.pivot.y * lt2.c), lt2.ty = this.position.y - (this.pivot.x * lt2.b + this.pivot.y * lt2.d), this._currentLocalID = this._localID, this._parentID = -1), this._parentID !== parentTransform._worldID) {
        const pt2 = parentTransform.worldTransform, wt2 = this.worldTransform;
        wt2.a = lt2.a * pt2.a + lt2.b * pt2.c, wt2.b = lt2.a * pt2.b + lt2.b * pt2.d, wt2.c = lt2.c * pt2.a + lt2.d * pt2.c, wt2.d = lt2.c * pt2.b + lt2.d * pt2.d, wt2.tx = lt2.tx * pt2.a + lt2.ty * pt2.c + pt2.tx, wt2.ty = lt2.tx * pt2.b + lt2.ty * pt2.d + pt2.ty, this._parentID = parentTransform._worldID, this._worldID++;
      }
    }
    /**
     * Decomposes a matrix and sets the transforms properties based on it.
     * @param matrix - The matrix to decompose
     */
    setFromMatrix(matrix) {
      matrix.decompose(this), this._localID++;
    }
    /** The rotation of the object in radians. */
    get rotation() {
      return this._rotation;
    }
    set rotation(value) {
      this._rotation !== value && (this._rotation = value, this.updateSkew());
    }
  };
  _Transform.IDENTITY = new _Transform();
  let Transform = _Transform;
  Transform.prototype.toString = function() {
    return `[@pixi/math:Transform position=(${this.position.x}, ${this.position.y}) rotation=${this.rotation} scale=(${this.scale.x}, ${this.scale.y}) skew=(${this.skew.x}, ${this.skew.y}) ]`;
  };
  var defaultFragment$2 = `varying vec2 vTextureCoord;

uniform sampler2D uSampler;

void main(void){
   gl_FragColor *= texture2D(uSampler, vTextureCoord);
}`;
  var defaultVertex$2 = `attribute vec2 aVertexPosition;
attribute vec2 aTextureCoord;

uniform mat3 projectionMatrix;

varying vec2 vTextureCoord;

void main(void){
   gl_Position = vec4((projectionMatrix * vec3(aVertexPosition, 1.0)).xy, 0.0, 1.0);
   vTextureCoord = aTextureCoord;
}
`;
  function compileShader(gl, type2, src) {
    const shader = gl.createShader(type2);
    return gl.shaderSource(shader, src), gl.compileShader(shader), shader;
  }
  function booleanArray(size) {
    const array = new Array(size);
    for (let i2 = 0; i2 < array.length; i2++)
      array[i2] = false;
    return array;
  }
  function defaultValue(type2, size) {
    switch (type2) {
      case "float":
        return 0;
      case "vec2":
        return new Float32Array(2 * size);
      case "vec3":
        return new Float32Array(3 * size);
      case "vec4":
        return new Float32Array(4 * size);
      case "int":
      case "uint":
      case "sampler2D":
      case "sampler2DArray":
        return 0;
      case "ivec2":
        return new Int32Array(2 * size);
      case "ivec3":
        return new Int32Array(3 * size);
      case "ivec4":
        return new Int32Array(4 * size);
      case "uvec2":
        return new Uint32Array(2 * size);
      case "uvec3":
        return new Uint32Array(3 * size);
      case "uvec4":
        return new Uint32Array(4 * size);
      case "bool":
        return false;
      case "bvec2":
        return booleanArray(2 * size);
      case "bvec3":
        return booleanArray(3 * size);
      case "bvec4":
        return booleanArray(4 * size);
      case "mat2":
        return new Float32Array([
          1,
          0,
          0,
          1
        ]);
      case "mat3":
        return new Float32Array([
          1,
          0,
          0,
          0,
          1,
          0,
          0,
          0,
          1
        ]);
      case "mat4":
        return new Float32Array([
          1,
          0,
          0,
          0,
          0,
          1,
          0,
          0,
          0,
          0,
          1,
          0,
          0,
          0,
          0,
          1
        ]);
    }
    return null;
  }
  const uniformParsers = [
    // a float cache layer
    {
      test: (data) => data.type === "float" && data.size === 1 && !data.isArray,
      code: (name) => `
            if(uv["${name}"] !== ud["${name}"].value)
            {
                ud["${name}"].value = uv["${name}"]
                gl.uniform1f(ud["${name}"].location, uv["${name}"])
            }
            `
    },
    // handling samplers
    {
      test: (data, uniform) => (
        // eslint-disable-next-line max-len,no-eq-null,eqeqeq
        (data.type === "sampler2D" || data.type === "samplerCube" || data.type === "sampler2DArray") && data.size === 1 && !data.isArray && (uniform == null || uniform.castToBaseTexture !== void 0)
      ),
      code: (name) => `t = syncData.textureCount++;

            renderer.texture.bind(uv["${name}"], t);

            if(ud["${name}"].value !== t)
            {
                ud["${name}"].value = t;
                gl.uniform1i(ud["${name}"].location, t);
; // eslint-disable-line max-len
            }`
    },
    // uploading pixi matrix object to mat3
    {
      test: (data, uniform) => data.type === "mat3" && data.size === 1 && !data.isArray && uniform.a !== void 0,
      code: (name) => (
        // TODO and some smart caching dirty ids here!
        `
            gl.uniformMatrix3fv(ud["${name}"].location, false, uv["${name}"].toArray(true));
            `
      ),
      codeUbo: (name) => `
                var ${name}_matrix = uv.${name}.toArray(true);

                data[offset] = ${name}_matrix[0];
                data[offset+1] = ${name}_matrix[1];
                data[offset+2] = ${name}_matrix[2];
        
                data[offset + 4] = ${name}_matrix[3];
                data[offset + 5] = ${name}_matrix[4];
                data[offset + 6] = ${name}_matrix[5];
        
                data[offset + 8] = ${name}_matrix[6];
                data[offset + 9] = ${name}_matrix[7];
                data[offset + 10] = ${name}_matrix[8];
            `
    },
    // uploading a pixi point as a vec2 with caching layer
    {
      test: (data, uniform) => data.type === "vec2" && data.size === 1 && !data.isArray && uniform.x !== void 0,
      code: (name) => `
                cv = ud["${name}"].value;
                v = uv["${name}"];

                if(cv[0] !== v.x || cv[1] !== v.y)
                {
                    cv[0] = v.x;
                    cv[1] = v.y;
                    gl.uniform2f(ud["${name}"].location, v.x, v.y);
                }`,
      codeUbo: (name) => `
                v = uv.${name};

                data[offset] = v.x;
                data[offset+1] = v.y;
            `
    },
    // caching layer for a vec2
    {
      test: (data) => data.type === "vec2" && data.size === 1 && !data.isArray,
      code: (name) => `
                cv = ud["${name}"].value;
                v = uv["${name}"];

                if(cv[0] !== v[0] || cv[1] !== v[1])
                {
                    cv[0] = v[0];
                    cv[1] = v[1];
                    gl.uniform2f(ud["${name}"].location, v[0], v[1]);
                }
            `
    },
    // upload a pixi rectangle as a vec4 with caching layer
    {
      test: (data, uniform) => data.type === "vec4" && data.size === 1 && !data.isArray && uniform.width !== void 0,
      code: (name) => `
                cv = ud["${name}"].value;
                v = uv["${name}"];

                if(cv[0] !== v.x || cv[1] !== v.y || cv[2] !== v.width || cv[3] !== v.height)
                {
                    cv[0] = v.x;
                    cv[1] = v.y;
                    cv[2] = v.width;
                    cv[3] = v.height;
                    gl.uniform4f(ud["${name}"].location, v.x, v.y, v.width, v.height)
                }`,
      codeUbo: (name) => `
                    v = uv.${name};

                    data[offset] = v.x;
                    data[offset+1] = v.y;
                    data[offset+2] = v.width;
                    data[offset+3] = v.height;
                `
    },
    // upload a pixi color as vec4 with caching layer
    {
      test: (data, uniform) => data.type === "vec4" && data.size === 1 && !data.isArray && uniform.red !== void 0,
      code: (name) => `
                cv = ud["${name}"].value;
                v = uv["${name}"];

                if(cv[0] !== v.red || cv[1] !== v.green || cv[2] !== v.blue || cv[3] !== v.alpha)
                {
                    cv[0] = v.red;
                    cv[1] = v.green;
                    cv[2] = v.blue;
                    cv[3] = v.alpha;
                    gl.uniform4f(ud["${name}"].location, v.red, v.green, v.blue, v.alpha)
                }`,
      codeUbo: (name) => `
                    v = uv.${name};

                    data[offset] = v.red;
                    data[offset+1] = v.green;
                    data[offset+2] = v.blue;
                    data[offset+3] = v.alpha;
                `
    },
    // upload a pixi color as a vec3 with caching layer
    {
      test: (data, uniform) => data.type === "vec3" && data.size === 1 && !data.isArray && uniform.red !== void 0,
      code: (name) => `
                cv = ud["${name}"].value;
                v = uv["${name}"];

                if(cv[0] !== v.red || cv[1] !== v.green || cv[2] !== v.blue || cv[3] !== v.a)
                {
                    cv[0] = v.red;
                    cv[1] = v.green;
                    cv[2] = v.blue;
    
                    gl.uniform3f(ud["${name}"].location, v.red, v.green, v.blue)
                }`,
      codeUbo: (name) => `
                    v = uv.${name};

                    data[offset] = v.red;
                    data[offset+1] = v.green;
                    data[offset+2] = v.blue;
                `
    },
    // a caching layer for vec4 uploading
    {
      test: (data) => data.type === "vec4" && data.size === 1 && !data.isArray,
      code: (name) => `
                cv = ud["${name}"].value;
                v = uv["${name}"];

                if(cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2] || cv[3] !== v[3])
                {
                    cv[0] = v[0];
                    cv[1] = v[1];
                    cv[2] = v[2];
                    cv[3] = v[3];

                    gl.uniform4f(ud["${name}"].location, v[0], v[1], v[2], v[3])
                }`
    }
  ];
  const GLSL_TO_SINGLE_SETTERS_CACHED = {
    float: `
    if (cv !== v)
    {
        cu.value = v;
        gl.uniform1f(location, v);
    }`,
    vec2: `
    if (cv[0] !== v[0] || cv[1] !== v[1])
    {
        cv[0] = v[0];
        cv[1] = v[1];

        gl.uniform2f(location, v[0], v[1])
    }`,
    vec3: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];

        gl.uniform3f(location, v[0], v[1], v[2])
    }`,
    vec4: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2] || cv[3] !== v[3])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];
        cv[3] = v[3];

        gl.uniform4f(location, v[0], v[1], v[2], v[3]);
    }`,
    int: `
    if (cv !== v)
    {
        cu.value = v;

        gl.uniform1i(location, v);
    }`,
    ivec2: `
    if (cv[0] !== v[0] || cv[1] !== v[1])
    {
        cv[0] = v[0];
        cv[1] = v[1];

        gl.uniform2i(location, v[0], v[1]);
    }`,
    ivec3: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];

        gl.uniform3i(location, v[0], v[1], v[2]);
    }`,
    ivec4: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2] || cv[3] !== v[3])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];
        cv[3] = v[3];

        gl.uniform4i(location, v[0], v[1], v[2], v[3]);
    }`,
    uint: `
    if (cv !== v)
    {
        cu.value = v;

        gl.uniform1ui(location, v);
    }`,
    uvec2: `
    if (cv[0] !== v[0] || cv[1] !== v[1])
    {
        cv[0] = v[0];
        cv[1] = v[1];

        gl.uniform2ui(location, v[0], v[1]);
    }`,
    uvec3: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];

        gl.uniform3ui(location, v[0], v[1], v[2]);
    }`,
    uvec4: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2] || cv[3] !== v[3])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];
        cv[3] = v[3];

        gl.uniform4ui(location, v[0], v[1], v[2], v[3]);
    }`,
    bool: `
    if (cv !== v)
    {
        cu.value = v;
        gl.uniform1i(location, v);
    }`,
    bvec2: `
    if (cv[0] != v[0] || cv[1] != v[1])
    {
        cv[0] = v[0];
        cv[1] = v[1];

        gl.uniform2i(location, v[0], v[1]);
    }`,
    bvec3: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];

        gl.uniform3i(location, v[0], v[1], v[2]);
    }`,
    bvec4: `
    if (cv[0] !== v[0] || cv[1] !== v[1] || cv[2] !== v[2] || cv[3] !== v[3])
    {
        cv[0] = v[0];
        cv[1] = v[1];
        cv[2] = v[2];
        cv[3] = v[3];

        gl.uniform4i(location, v[0], v[1], v[2], v[3]);
    }`,
    mat2: "gl.uniformMatrix2fv(location, false, v)",
    mat3: "gl.uniformMatrix3fv(location, false, v)",
    mat4: "gl.uniformMatrix4fv(location, false, v)",
    sampler2D: `
    if (cv !== v)
    {
        cu.value = v;

        gl.uniform1i(location, v);
    }`,
    samplerCube: `
    if (cv !== v)
    {
        cu.value = v;

        gl.uniform1i(location, v);
    }`,
    sampler2DArray: `
    if (cv !== v)
    {
        cu.value = v;

        gl.uniform1i(location, v);
    }`
  }, GLSL_TO_ARRAY_SETTERS = {
    float: "gl.uniform1fv(location, v)",
    vec2: "gl.uniform2fv(location, v)",
    vec3: "gl.uniform3fv(location, v)",
    vec4: "gl.uniform4fv(location, v)",
    mat4: "gl.uniformMatrix4fv(location, false, v)",
    mat3: "gl.uniformMatrix3fv(location, false, v)",
    mat2: "gl.uniformMatrix2fv(location, false, v)",
    int: "gl.uniform1iv(location, v)",
    ivec2: "gl.uniform2iv(location, v)",
    ivec3: "gl.uniform3iv(location, v)",
    ivec4: "gl.uniform4iv(location, v)",
    uint: "gl.uniform1uiv(location, v)",
    uvec2: "gl.uniform2uiv(location, v)",
    uvec3: "gl.uniform3uiv(location, v)",
    uvec4: "gl.uniform4uiv(location, v)",
    bool: "gl.uniform1iv(location, v)",
    bvec2: "gl.uniform2iv(location, v)",
    bvec3: "gl.uniform3iv(location, v)",
    bvec4: "gl.uniform4iv(location, v)",
    sampler2D: "gl.uniform1iv(location, v)",
    samplerCube: "gl.uniform1iv(location, v)",
    sampler2DArray: "gl.uniform1iv(location, v)"
  };
  function generateUniformsSync(group, uniformData) {
    const funcFragments = [`
        var v = null;
        var cv = null;
        var cu = null;
        var t = 0;
        var gl = renderer.gl;
    `];
    for (const i2 in group.uniforms) {
      const data = uniformData[i2];
      if (!data) {
        group.uniforms[i2]?.group === true && (group.uniforms[i2].ubo ? funcFragments.push(`
                        renderer.shader.syncUniformBufferGroup(uv.${i2}, '${i2}');
                    `) : funcFragments.push(`
                        renderer.shader.syncUniformGroup(uv.${i2}, syncData);
                    `));
        continue;
      }
      const uniform = group.uniforms[i2];
      let parsed = false;
      for (let j2 = 0; j2 < uniformParsers.length; j2++)
        if (uniformParsers[j2].test(data, uniform)) {
          funcFragments.push(uniformParsers[j2].code(i2, uniform)), parsed = true;
          break;
        }
      if (!parsed) {
        const template = (data.size === 1 && !data.isArray ? GLSL_TO_SINGLE_SETTERS_CACHED : GLSL_TO_ARRAY_SETTERS)[data.type].replace("location", `ud["${i2}"].location`);
        funcFragments.push(`
            cu = ud["${i2}"];
            cv = cu.value;
            v = uv["${i2}"];
            ${template};`);
      }
    }
    return new Function("ud", "uv", "renderer", "syncData", funcFragments.join(`
`));
  }
  const unknownContext = {};
  let context = unknownContext;
  function getTestContext() {
    if (context === unknownContext || context?.isContextLost()) {
      const canvas = settings.ADAPTER.createCanvas();
      let gl;
      settings.PREFER_ENV >= ENV.WEBGL2 && (gl = canvas.getContext("webgl2", {})), gl || (gl = canvas.getContext("webgl", {}) || canvas.getContext("experimental-webgl", {}), gl ? gl.getExtension("WEBGL_draw_buffers") : gl = null), context = gl;
    }
    return context;
  }
  let maxFragmentPrecision;
  function getMaxFragmentPrecision() {
    if (!maxFragmentPrecision) {
      maxFragmentPrecision = PRECISION.MEDIUM;
      const gl = getTestContext();
      if (gl && gl.getShaderPrecisionFormat) {
        const shaderFragment = gl.getShaderPrecisionFormat(gl.FRAGMENT_SHADER, gl.HIGH_FLOAT);
        shaderFragment && (maxFragmentPrecision = shaderFragment.precision ? PRECISION.HIGH : PRECISION.MEDIUM);
      }
    }
    return maxFragmentPrecision;
  }
  function logPrettyShaderError(gl, shader) {
    const shaderSrc = gl.getShaderSource(shader).split(`
`).map((line, index) => `${index}: ${line}`), shaderLog = gl.getShaderInfoLog(shader), splitShader = shaderLog.split(`
`), dedupe = {}, lineNumbers = splitShader.map((line) => parseFloat(line.replace(/^ERROR\: 0\:([\d]+)\:.*$/, "$1"))).filter((n2) => n2 && !dedupe[n2] ? (dedupe[n2] = true, true) : false), logArgs = [""];
    lineNumbers.forEach((number) => {
      shaderSrc[number - 1] = `%c${shaderSrc[number - 1]}%c`, logArgs.push("background: #FF0000; color:#FFFFFF; font-size: 10px", "font-size: 10px");
    });
    const fragmentSourceToLog = shaderSrc.join(`
`);
    logArgs[0] = fragmentSourceToLog, console.error(shaderLog), console.groupCollapsed("click to view full shader code"), console.warn(...logArgs), console.groupEnd();
  }
  function logProgramError(gl, program, vertexShader, fragmentShader) {
    gl.getProgramParameter(program, gl.LINK_STATUS) || (gl.getShaderParameter(vertexShader, gl.COMPILE_STATUS) || logPrettyShaderError(gl, vertexShader), gl.getShaderParameter(fragmentShader, gl.COMPILE_STATUS) || logPrettyShaderError(gl, fragmentShader), console.error("PixiJS Error: Could not initialize shader."), gl.getProgramInfoLog(program) !== "" && console.warn("PixiJS Warning: gl.getProgramInfoLog()", gl.getProgramInfoLog(program)));
  }
  const GLSL_TO_SIZE = {
    float: 1,
    vec2: 2,
    vec3: 3,
    vec4: 4,
    int: 1,
    ivec2: 2,
    ivec3: 3,
    ivec4: 4,
    uint: 1,
    uvec2: 2,
    uvec3: 3,
    uvec4: 4,
    bool: 1,
    bvec2: 2,
    bvec3: 3,
    bvec4: 4,
    mat2: 4,
    mat3: 9,
    mat4: 16,
    sampler2D: 1
  };
  function mapSize(type2) {
    return GLSL_TO_SIZE[type2];
  }
  let GL_TABLE = null;
  const GL_TO_GLSL_TYPES = {
    FLOAT: "float",
    FLOAT_VEC2: "vec2",
    FLOAT_VEC3: "vec3",
    FLOAT_VEC4: "vec4",
    INT: "int",
    INT_VEC2: "ivec2",
    INT_VEC3: "ivec3",
    INT_VEC4: "ivec4",
    UNSIGNED_INT: "uint",
    UNSIGNED_INT_VEC2: "uvec2",
    UNSIGNED_INT_VEC3: "uvec3",
    UNSIGNED_INT_VEC4: "uvec4",
    BOOL: "bool",
    BOOL_VEC2: "bvec2",
    BOOL_VEC3: "bvec3",
    BOOL_VEC4: "bvec4",
    FLOAT_MAT2: "mat2",
    FLOAT_MAT3: "mat3",
    FLOAT_MAT4: "mat4",
    SAMPLER_2D: "sampler2D",
    INT_SAMPLER_2D: "sampler2D",
    UNSIGNED_INT_SAMPLER_2D: "sampler2D",
    SAMPLER_CUBE: "samplerCube",
    INT_SAMPLER_CUBE: "samplerCube",
    UNSIGNED_INT_SAMPLER_CUBE: "samplerCube",
    SAMPLER_2D_ARRAY: "sampler2DArray",
    INT_SAMPLER_2D_ARRAY: "sampler2DArray",
    UNSIGNED_INT_SAMPLER_2D_ARRAY: "sampler2DArray"
  };
  function mapType(gl, type2) {
    if (!GL_TABLE) {
      const typeNames = Object.keys(GL_TO_GLSL_TYPES);
      GL_TABLE = {};
      for (let i2 = 0; i2 < typeNames.length; ++i2) {
        const tn = typeNames[i2];
        GL_TABLE[gl[tn]] = GL_TO_GLSL_TYPES[tn];
      }
    }
    return GL_TABLE[type2];
  }
  function setPrecision(src, requestedPrecision, maxSupportedPrecision) {
    if (src.substring(0, 9) !== "precision") {
      let precision = requestedPrecision;
      return requestedPrecision === PRECISION.HIGH && maxSupportedPrecision !== PRECISION.HIGH && (precision = PRECISION.MEDIUM), `precision ${precision} float;
${src}`;
    } else if (maxSupportedPrecision !== PRECISION.HIGH && src.substring(0, 15) === "precision highp")
      return src.replace("precision highp", "precision mediump");
    return src;
  }
  let unsafeEval;
  function unsafeEvalSupported() {
    if (typeof unsafeEval == "boolean")
      return unsafeEval;
    try {
      unsafeEval = new Function("param1", "param2", "param3", "return param1[param2] === param3;")({ a: "b" }, "a", "b") === true;
    } catch {
      unsafeEval = false;
    }
    return unsafeEval;
  }
  let UID$2 = 0;
  const nameCache = {}, _Program = class _Program2 {
    /**
     * @param vertexSrc - The source of the vertex shader.
     * @param fragmentSrc - The source of the fragment shader.
     * @param name - Name for shader
     * @param extra - Extra data for shader
     */
    constructor(vertexSrc, fragmentSrc, name = "pixi-shader", extra = {}) {
      this.extra = {}, this.id = UID$2++, this.vertexSrc = vertexSrc || _Program2.defaultVertexSrc, this.fragmentSrc = fragmentSrc || _Program2.defaultFragmentSrc, this.vertexSrc = this.vertexSrc.trim(), this.fragmentSrc = this.fragmentSrc.trim(), this.extra = extra, this.vertexSrc.substring(0, 8) !== "#version" && (name = name.replace(/\s+/g, "-"), nameCache[name] ? (nameCache[name]++, name += `-${nameCache[name]}`) : nameCache[name] = 1, this.vertexSrc = `#define SHADER_NAME ${name}
${this.vertexSrc}`, this.fragmentSrc = `#define SHADER_NAME ${name}
${this.fragmentSrc}`, this.vertexSrc = setPrecision(
        this.vertexSrc,
        _Program2.defaultVertexPrecision,
        PRECISION.HIGH
      ), this.fragmentSrc = setPrecision(
        this.fragmentSrc,
        _Program2.defaultFragmentPrecision,
        getMaxFragmentPrecision()
      )), this.glPrograms = {}, this.syncUniforms = null;
    }
    /**
     * The default vertex shader source.
     * @readonly
     */
    static get defaultVertexSrc() {
      return defaultVertex$2;
    }
    /**
     * The default fragment shader source.
     * @readonly
     */
    static get defaultFragmentSrc() {
      return defaultFragment$2;
    }
    /**
     * A short hand function to create a program based of a vertex and fragment shader.
     *
     * This method will also check to see if there is a cached program.
     * @param vertexSrc - The source of the vertex shader.
     * @param fragmentSrc - The source of the fragment shader.
     * @param name - Name for shader
     * @returns A shiny new PixiJS shader program!
     */
    static from(vertexSrc, fragmentSrc, name) {
      const key = vertexSrc + fragmentSrc;
      let program = ProgramCache[key];
      return program || (ProgramCache[key] = program = new _Program2(vertexSrc, fragmentSrc, name)), program;
    }
  };
  _Program.defaultVertexPrecision = PRECISION.HIGH, /**
  * Default specify float precision in fragment shader.
  * iOS is best set at highp due to https://github.com/pixijs/pixijs/issues/3742
  * @static
  * @type {PIXI.PRECISION}
  * @default PIXI.PRECISION.MEDIUM
  */
  _Program.defaultFragmentPrecision = isMobile.apple.device ? PRECISION.HIGH : PRECISION.MEDIUM;
  let Program = _Program;
  let UID$1 = 0;
  class UniformGroup {
    /**
     * @param {object | Buffer} [uniforms] - Custom uniforms to use to augment the built-in ones. Or a pixi buffer.
     * @param isStatic - Uniforms wont be changed after creation.
     * @param isUbo - If true, will treat this uniform group as a uniform buffer object.
     */
    constructor(uniforms, isStatic, isUbo) {
      this.group = true, this.syncUniforms = {}, this.dirtyId = 0, this.id = UID$1++, this.static = !!isStatic, this.ubo = !!isUbo, uniforms instanceof Buffer ? (this.buffer = uniforms, this.buffer.type = BUFFER_TYPE.UNIFORM_BUFFER, this.autoManage = false, this.ubo = true) : (this.uniforms = uniforms, this.ubo && (this.buffer = new Buffer(new Float32Array(1)), this.buffer.type = BUFFER_TYPE.UNIFORM_BUFFER, this.autoManage = true));
    }
    update() {
      this.dirtyId++, !this.autoManage && this.buffer && this.buffer.update();
    }
    add(name, uniforms, _static) {
      if (!this.ubo)
        this.uniforms[name] = new UniformGroup(uniforms, _static);
      else
        throw new Error("[UniformGroup] uniform groups in ubo mode cannot be modified, or have uniform groups nested in them");
    }
    static from(uniforms, _static, _ubo) {
      return new UniformGroup(uniforms, _static, _ubo);
    }
    /**
     * A short hand function for creating a static UBO UniformGroup.
     * @param uniforms - the ubo item
     * @param _static - should this be updated each time it is used? defaults to true here!
     */
    static uboFrom(uniforms, _static) {
      return new UniformGroup(uniforms, _static ?? true, true);
    }
  }
  class Shader {
    /**
     * @param program - The program the shader will use.
     * @param uniforms - Custom uniforms to use to augment the built-in ones.
     */
    constructor(program, uniforms) {
      this.uniformBindCount = 0, this.program = program, uniforms ? uniforms instanceof UniformGroup ? this.uniformGroup = uniforms : this.uniformGroup = new UniformGroup(uniforms) : this.uniformGroup = new UniformGroup({}), this.disposeRunner = new Runner("disposeShader");
    }
    // TODO move to shader system..
    checkUniformExists(name, group) {
      if (group.uniforms[name])
        return true;
      for (const i2 in group.uniforms) {
        const uniform = group.uniforms[i2];
        if (uniform.group === true && this.checkUniformExists(name, uniform))
          return true;
      }
      return false;
    }
    destroy() {
      this.uniformGroup = null, this.disposeRunner.emit(this), this.disposeRunner.destroy();
    }
    /**
     * Shader uniform values, shortcut for `uniformGroup.uniforms`.
     * @readonly
     */
    get uniforms() {
      return this.uniformGroup.uniforms;
    }
    /**
     * A short hand function to create a shader based of a vertex and fragment shader.
     * @param vertexSrc - The source of the vertex shader.
     * @param fragmentSrc - The source of the fragment shader.
     * @param uniforms - Custom uniforms to use to augment the built-in ones.
     * @returns A shiny new PixiJS shader!
     */
    static from(vertexSrc, fragmentSrc, uniforms) {
      const program = Program.from(vertexSrc, fragmentSrc);
      return new Shader(program, uniforms);
    }
  }
  class BatchShaderGenerator {
    /**
     * @param vertexSrc - Vertex shader
     * @param fragTemplate - Fragment shader template
     */
    constructor(vertexSrc, fragTemplate2) {
      if (this.vertexSrc = vertexSrc, this.fragTemplate = fragTemplate2, this.programCache = {}, this.defaultGroupCache = {}, !fragTemplate2.includes("%count%"))
        throw new Error('Fragment template must contain "%count%".');
      if (!fragTemplate2.includes("%forloop%"))
        throw new Error('Fragment template must contain "%forloop%".');
    }
    generateShader(maxTextures) {
      if (!this.programCache[maxTextures]) {
        const sampleValues = new Int32Array(maxTextures);
        for (let i2 = 0; i2 < maxTextures; i2++)
          sampleValues[i2] = i2;
        this.defaultGroupCache[maxTextures] = UniformGroup.from({ uSamplers: sampleValues }, true);
        let fragmentSrc = this.fragTemplate;
        fragmentSrc = fragmentSrc.replace(/%count%/gi, `${maxTextures}`), fragmentSrc = fragmentSrc.replace(/%forloop%/gi, this.generateSampleSrc(maxTextures)), this.programCache[maxTextures] = new Program(this.vertexSrc, fragmentSrc);
      }
      const uniforms = {
        tint: new Float32Array([1, 1, 1, 1]),
        translationMatrix: new Matrix(),
        default: this.defaultGroupCache[maxTextures]
      };
      return new Shader(this.programCache[maxTextures], uniforms);
    }
    generateSampleSrc(maxTextures) {
      let src = "";
      src += `
`, src += `
`;
      for (let i2 = 0; i2 < maxTextures; i2++)
        i2 > 0 && (src += `
else `), i2 < maxTextures - 1 && (src += `if(vTextureId < ${i2}.5)`), src += `
{`, src += `
	color = texture2D(uSamplers[${i2}], vTextureCoord);`, src += `
}`;
      return src += `
`, src += `
`, src;
    }
  }
  class BatchTextureArray {
    constructor() {
      this.elements = [], this.ids = [], this.count = 0;
    }
    clear() {
      for (let i2 = 0; i2 < this.count; i2++)
        this.elements[i2] = null;
      this.count = 0;
    }
  }
  function canUploadSameBuffer() {
    return !isMobile.apple.device;
  }
  function maxRecommendedTextures(max2) {
    let allowMax = true;
    const navigator2 = settings.ADAPTER.getNavigator();
    if (isMobile.tablet || isMobile.phone) {
      if (isMobile.apple.device) {
        const match = navigator2.userAgent.match(/OS (\d+)_(\d+)?/);
        match && parseInt(match[1], 10) < 11 && (allowMax = false);
      }
      if (isMobile.android.device) {
        const match = navigator2.userAgent.match(/Android\s([0-9.]*)/);
        match && parseInt(match[1], 10) < 7 && (allowMax = false);
      }
    }
    return allowMax ? max2 : 4;
  }
  class ObjectRenderer {
    /**
     * @param renderer - The renderer this manager works for.
     */
    constructor(renderer) {
      this.renderer = renderer;
    }
    /** Stub method that should be used to empty the current batch by rendering objects now. */
    flush() {
    }
    /** Generic destruction method that frees all resources. This should be called by subclasses. */
    destroy() {
      this.renderer = null;
    }
    /**
     * Stub method that initializes any state required before
     * rendering starts. It is different from the `prerender`
     * signal, which occurs every frame, in that it is called
     * whenever an object requests _this_ renderer specifically.
     */
    start() {
    }
    /** Stops the renderer. It should free up any state and become dormant. */
    stop() {
      this.flush();
    }
    /**
     * Keeps the object to render. It doesn't have to be
     * rendered immediately.
     * @param {PIXI.DisplayObject} _object - The object to render.
     */
    render(_object) {
    }
  }
  var defaultFragment$1 = `varying vec2 vTextureCoord;
varying vec4 vColor;
varying float vTextureId;
uniform sampler2D uSamplers[%count%];

void main(void){
    vec4 color;
    %forloop%
    gl_FragColor = color * vColor;
}
`;
  var defaultVertex$1 = `precision highp float;
attribute vec2 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec4 aColor;
attribute float aTextureId;

uniform mat3 projectionMatrix;
uniform mat3 translationMatrix;
uniform vec4 tint;

varying vec2 vTextureCoord;
varying vec4 vColor;
varying float vTextureId;

void main(void){
    gl_Position = vec4((projectionMatrix * translationMatrix * vec3(aVertexPosition, 1.0)).xy, 0.0, 1.0);

    vTextureCoord = aTextureCoord;
    vTextureId = aTextureId;
    vColor = aColor * tint;
}
`;
  const _BatchRenderer = class _BatchRenderer2 extends ObjectRenderer {
    /**
     * This will hook onto the renderer's `contextChange`
     * and `prerender` signals.
     * @param {PIXI.Renderer} renderer - The renderer this works for.
     */
    constructor(renderer) {
      super(renderer), this.setShaderGenerator(), this.geometryClass = BatchGeometry, this.vertexSize = 6, this.state = State.for2d(), this.size = _BatchRenderer2.defaultBatchSize * 4, this._vertexCount = 0, this._indexCount = 0, this._bufferedElements = [], this._bufferedTextures = [], this._bufferSize = 0, this._shader = null, this._packedGeometries = [], this._packedGeometryPoolSize = 2, this._flushId = 0, this._aBuffers = {}, this._iBuffers = {}, this.maxTextures = 1, this.renderer.on("prerender", this.onPrerender, this), renderer.runners.contextChange.add(this), this._dcIndex = 0, this._aIndex = 0, this._iIndex = 0, this._attributeBuffer = null, this._indexBuffer = null, this._tempBoundTextures = [];
    }
    /**
     * The maximum textures that this device supports.
     * @static
     * @default 32
     */
    static get defaultMaxTextures() {
      return this._defaultMaxTextures = this._defaultMaxTextures ?? maxRecommendedTextures(32), this._defaultMaxTextures;
    }
    static set defaultMaxTextures(value) {
      this._defaultMaxTextures = value;
    }
    /**
     * Can we upload the same buffer in a single frame?
     * @static
     */
    static get canUploadSameBuffer() {
      return this._canUploadSameBuffer = this._canUploadSameBuffer ?? canUploadSameBuffer(), this._canUploadSameBuffer;
    }
    static set canUploadSameBuffer(value) {
      this._canUploadSameBuffer = value;
    }
    /**
     * @see PIXI.BatchRenderer#maxTextures
     * @deprecated since 7.1.0
     * @readonly
     */
    get MAX_TEXTURES() {
      return deprecation("7.1.0", "BatchRenderer#MAX_TEXTURES renamed to BatchRenderer#maxTextures"), this.maxTextures;
    }
    /**
     * The default vertex shader source
     * @readonly
     */
    static get defaultVertexSrc() {
      return defaultVertex$1;
    }
    /**
     * The default fragment shader source
     * @readonly
     */
    static get defaultFragmentTemplate() {
      return defaultFragment$1;
    }
    /**
     * Set the shader generator.
     * @param {object} [options]
     * @param {string} [options.vertex=PIXI.BatchRenderer.defaultVertexSrc] - Vertex shader source
     * @param {string} [options.fragment=PIXI.BatchRenderer.defaultFragmentTemplate] - Fragment shader template
     */
    setShaderGenerator({
      vertex: vertex2 = _BatchRenderer2.defaultVertexSrc,
      fragment: fragment2 = _BatchRenderer2.defaultFragmentTemplate
    } = {}) {
      this.shaderGenerator = new BatchShaderGenerator(vertex2, fragment2);
    }
    /**
     * Handles the `contextChange` signal.
     *
     * It calculates `this.maxTextures` and allocating the packed-geometry object pool.
     */
    contextChange() {
      const gl = this.renderer.gl;
      settings.PREFER_ENV === ENV.WEBGL_LEGACY ? this.maxTextures = 1 : (this.maxTextures = Math.min(
        gl.getParameter(gl.MAX_TEXTURE_IMAGE_UNITS),
        _BatchRenderer2.defaultMaxTextures
      ), this.maxTextures = checkMaxIfStatementsInShader(
        this.maxTextures,
        gl
      )), this._shader = this.shaderGenerator.generateShader(this.maxTextures);
      for (let i2 = 0; i2 < this._packedGeometryPoolSize; i2++)
        this._packedGeometries[i2] = new this.geometryClass();
      this.initFlushBuffers();
    }
    /** Makes sure that static and dynamic flush pooled objects have correct dimensions. */
    initFlushBuffers() {
      const {
        _drawCallPool,
        _textureArrayPool
      } = _BatchRenderer2, MAX_SPRITES = this.size / 4, MAX_TA = Math.floor(MAX_SPRITES / this.maxTextures) + 1;
      for (; _drawCallPool.length < MAX_SPRITES; )
        _drawCallPool.push(new BatchDrawCall());
      for (; _textureArrayPool.length < MAX_TA; )
        _textureArrayPool.push(new BatchTextureArray());
      for (let i2 = 0; i2 < this.maxTextures; i2++)
        this._tempBoundTextures[i2] = null;
    }
    /** Handles the `prerender` signal. It ensures that flushes start from the first geometry object again. */
    onPrerender() {
      this._flushId = 0;
    }
    /**
     * Buffers the "batchable" object. It need not be rendered immediately.
     * @param {PIXI.DisplayObject} element - the element to render when
     *    using this renderer
     */
    render(element) {
      element._texture.valid && (this._vertexCount + element.vertexData.length / 2 > this.size && this.flush(), this._vertexCount += element.vertexData.length / 2, this._indexCount += element.indices.length, this._bufferedTextures[this._bufferSize] = element._texture.baseTexture, this._bufferedElements[this._bufferSize++] = element);
    }
    buildTexturesAndDrawCalls() {
      const {
        _bufferedTextures: textures,
        maxTextures
      } = this, textureArrays = _BatchRenderer2._textureArrayPool, batch = this.renderer.batch, boundTextures = this._tempBoundTextures, touch = this.renderer.textureGC.count;
      let TICK = ++BaseTexture._globalBatch, countTexArrays = 0, texArray = textureArrays[0], start = 0;
      batch.copyBoundTextures(boundTextures, maxTextures);
      for (let i2 = 0; i2 < this._bufferSize; ++i2) {
        const tex = textures[i2];
        textures[i2] = null, tex._batchEnabled !== TICK && (texArray.count >= maxTextures && (batch.boundArray(texArray, boundTextures, TICK, maxTextures), this.buildDrawCalls(texArray, start, i2), start = i2, texArray = textureArrays[++countTexArrays], ++TICK), tex._batchEnabled = TICK, tex.touched = touch, texArray.elements[texArray.count++] = tex);
      }
      texArray.count > 0 && (batch.boundArray(texArray, boundTextures, TICK, maxTextures), this.buildDrawCalls(texArray, start, this._bufferSize), ++countTexArrays, ++TICK);
      for (let i2 = 0; i2 < boundTextures.length; i2++)
        boundTextures[i2] = null;
      BaseTexture._globalBatch = TICK;
    }
    /**
     * Populating drawcalls for rendering
     * @param texArray
     * @param start
     * @param finish
     */
    buildDrawCalls(texArray, start, finish) {
      const {
        _bufferedElements: elements,
        _attributeBuffer,
        _indexBuffer,
        vertexSize
      } = this, drawCalls = _BatchRenderer2._drawCallPool;
      let dcIndex = this._dcIndex, aIndex = this._aIndex, iIndex = this._iIndex, drawCall = drawCalls[dcIndex];
      drawCall.start = this._iIndex, drawCall.texArray = texArray;
      for (let i2 = start; i2 < finish; ++i2) {
        const sprite = elements[i2], tex = sprite._texture.baseTexture, spriteBlendMode = premultiplyBlendMode[tex.alphaMode ? 1 : 0][sprite.blendMode];
        elements[i2] = null, start < i2 && drawCall.blend !== spriteBlendMode && (drawCall.size = iIndex - drawCall.start, start = i2, drawCall = drawCalls[++dcIndex], drawCall.texArray = texArray, drawCall.start = iIndex), this.packInterleavedGeometry(sprite, _attributeBuffer, _indexBuffer, aIndex, iIndex), aIndex += sprite.vertexData.length / 2 * vertexSize, iIndex += sprite.indices.length, drawCall.blend = spriteBlendMode;
      }
      start < finish && (drawCall.size = iIndex - drawCall.start, ++dcIndex), this._dcIndex = dcIndex, this._aIndex = aIndex, this._iIndex = iIndex;
    }
    /**
     * Bind textures for current rendering
     * @param texArray
     */
    bindAndClearTexArray(texArray) {
      const textureSystem = this.renderer.texture;
      for (let j2 = 0; j2 < texArray.count; j2++)
        textureSystem.bind(texArray.elements[j2], texArray.ids[j2]), texArray.elements[j2] = null;
      texArray.count = 0;
    }
    updateGeometry() {
      const {
        _packedGeometries: packedGeometries,
        _attributeBuffer: attributeBuffer,
        _indexBuffer: indexBuffer
      } = this;
      _BatchRenderer2.canUploadSameBuffer ? (packedGeometries[this._flushId]._buffer.update(attributeBuffer.rawBinaryData), packedGeometries[this._flushId]._indexBuffer.update(indexBuffer), this.renderer.geometry.updateBuffers()) : (this._packedGeometryPoolSize <= this._flushId && (this._packedGeometryPoolSize++, packedGeometries[this._flushId] = new this.geometryClass()), packedGeometries[this._flushId]._buffer.update(attributeBuffer.rawBinaryData), packedGeometries[this._flushId]._indexBuffer.update(indexBuffer), this.renderer.geometry.bind(packedGeometries[this._flushId]), this.renderer.geometry.updateBuffers(), this._flushId++);
    }
    drawBatches() {
      const dcCount = this._dcIndex, { gl, state: stateSystem } = this.renderer, drawCalls = _BatchRenderer2._drawCallPool;
      let curTexArray = null;
      for (let i2 = 0; i2 < dcCount; i2++) {
        const { texArray, type: type2, size, start, blend } = drawCalls[i2];
        curTexArray !== texArray && (curTexArray = texArray, this.bindAndClearTexArray(texArray)), this.state.blendMode = blend, stateSystem.set(this.state), gl.drawElements(type2, size, gl.UNSIGNED_SHORT, start * 2);
      }
    }
    /** Renders the content _now_ and empties the current batch. */
    flush() {
      this._vertexCount !== 0 && (this._attributeBuffer = this.getAttributeBuffer(this._vertexCount), this._indexBuffer = this.getIndexBuffer(this._indexCount), this._aIndex = 0, this._iIndex = 0, this._dcIndex = 0, this.buildTexturesAndDrawCalls(), this.updateGeometry(), this.drawBatches(), this._bufferSize = 0, this._vertexCount = 0, this._indexCount = 0);
    }
    /** Starts a new sprite batch. */
    start() {
      this.renderer.state.set(this.state), this.renderer.texture.ensureSamplerType(this.maxTextures), this.renderer.shader.bind(this._shader), _BatchRenderer2.canUploadSameBuffer && this.renderer.geometry.bind(this._packedGeometries[this._flushId]);
    }
    /** Stops and flushes the current batch. */
    stop() {
      this.flush();
    }
    /** Destroys this `BatchRenderer`. It cannot be used again. */
    destroy() {
      for (let i2 = 0; i2 < this._packedGeometryPoolSize; i2++)
        this._packedGeometries[i2] && this._packedGeometries[i2].destroy();
      this.renderer.off("prerender", this.onPrerender, this), this._aBuffers = null, this._iBuffers = null, this._packedGeometries = null, this._attributeBuffer = null, this._indexBuffer = null, this._shader && (this._shader.destroy(), this._shader = null), super.destroy();
    }
    /**
     * Fetches an attribute buffer from `this._aBuffers` that can hold atleast `size` floats.
     * @param size - minimum capacity required
     * @returns - buffer than can hold atleast `size` floats
     */
    getAttributeBuffer(size) {
      const roundedP2 = nextPow2(Math.ceil(size / 8)), roundedSizeIndex = log2(roundedP2), roundedSize = roundedP2 * 8;
      this._aBuffers.length <= roundedSizeIndex && (this._iBuffers.length = roundedSizeIndex + 1);
      let buffer = this._aBuffers[roundedSize];
      return buffer || (this._aBuffers[roundedSize] = buffer = new ViewableBuffer(roundedSize * this.vertexSize * 4)), buffer;
    }
    /**
     * Fetches an index buffer from `this._iBuffers` that can
     * have at least `size` capacity.
     * @param size - minimum required capacity
     * @returns - buffer that can fit `size` indices.
     */
    getIndexBuffer(size) {
      const roundedP2 = nextPow2(Math.ceil(size / 12)), roundedSizeIndex = log2(roundedP2), roundedSize = roundedP2 * 12;
      this._iBuffers.length <= roundedSizeIndex && (this._iBuffers.length = roundedSizeIndex + 1);
      let buffer = this._iBuffers[roundedSizeIndex];
      return buffer || (this._iBuffers[roundedSizeIndex] = buffer = new Uint16Array(roundedSize)), buffer;
    }
    /**
     * Takes the four batching parameters of `element`, interleaves
     * and pushes them into the batching attribute/index buffers given.
     *
     * It uses these properties: `vertexData` `uvs`, `textureId` and
     * `indicies`. It also uses the "tint" of the base-texture, if
     * present.
     * @param {PIXI.DisplayObject} element - element being rendered
     * @param attributeBuffer - attribute buffer.
     * @param indexBuffer - index buffer
     * @param aIndex - number of floats already in the attribute buffer
     * @param iIndex - number of indices already in `indexBuffer`
     */
    packInterleavedGeometry(element, attributeBuffer, indexBuffer, aIndex, iIndex) {
      const {
        uint32View,
        float32View
      } = attributeBuffer, packedVertices = aIndex / this.vertexSize, uvs = element.uvs, indicies = element.indices, vertexData = element.vertexData, textureId = element._texture.baseTexture._batchLocation, alpha = Math.min(element.worldAlpha, 1), argb = Color.shared.setValue(element._tintRGB).toPremultiplied(alpha, element._texture.baseTexture.alphaMode > 0);
      for (let i2 = 0; i2 < vertexData.length; i2 += 2)
        float32View[aIndex++] = vertexData[i2], float32View[aIndex++] = vertexData[i2 + 1], float32View[aIndex++] = uvs[i2], float32View[aIndex++] = uvs[i2 + 1], uint32View[aIndex++] = argb, float32View[aIndex++] = textureId;
      for (let i2 = 0; i2 < indicies.length; i2++)
        indexBuffer[iIndex++] = packedVertices + indicies[i2];
    }
  };
  _BatchRenderer.defaultBatchSize = 4096, /** @ignore */
  _BatchRenderer.extension = {
    name: "batch",
    type: ExtensionType.RendererPlugin
  }, /**
  * Pool of `BatchDrawCall` objects that `flush` used
  * to create "batches" of the objects being rendered.
  *
  * These are never re-allocated again.
  * Shared between all batch renderers because it can be only one "flush" working at the moment.
  * @member {PIXI.BatchDrawCall[]}
  */
  _BatchRenderer._drawCallPool = [], /**
  * Pool of `BatchDrawCall` objects that `flush` used
  * to create "batches" of the objects being rendered.
  *
  * These are never re-allocated again.
  * Shared between all batch renderers because it can be only one "flush" working at the moment.
  * @member {PIXI.BatchTextureArray[]}
  */
  _BatchRenderer._textureArrayPool = [];
  let BatchRenderer = _BatchRenderer;
  extensions.add(BatchRenderer);
  var defaultFragment = `varying vec2 vTextureCoord;

uniform sampler2D uSampler;

void main(void){
   gl_FragColor = texture2D(uSampler, vTextureCoord);
}
`;
  var defaultVertex = `attribute vec2 aVertexPosition;

uniform mat3 projectionMatrix;

varying vec2 vTextureCoord;

uniform vec4 inputSize;
uniform vec4 outputFrame;

vec4 filterVertexPosition( void )
{
    vec2 position = aVertexPosition * max(outputFrame.zw, vec2(0.)) + outputFrame.xy;

    return vec4((projectionMatrix * vec3(position, 1.0)).xy, 0.0, 1.0);
}

vec2 filterTextureCoord( void )
{
    return aVertexPosition * (outputFrame.zw * inputSize.zw);
}

void main(void)
{
    gl_Position = filterVertexPosition();
    vTextureCoord = filterTextureCoord();
}
`;
  const _Filter = class _Filter2 extends Shader {
    /**
     * @param vertexSrc - The source of the vertex shader.
     * @param fragmentSrc - The source of the fragment shader.
     * @param uniforms - Custom uniforms to use to augment the built-in ones.
     */
    constructor(vertexSrc, fragmentSrc, uniforms) {
      const program = Program.from(
        vertexSrc || _Filter2.defaultVertexSrc,
        fragmentSrc || _Filter2.defaultFragmentSrc
      );
      super(program, uniforms), this.padding = 0, this.resolution = _Filter2.defaultResolution, this.multisample = _Filter2.defaultMultisample, this.enabled = true, this.autoFit = true, this.state = new State();
    }
    /**
     * Applies the filter
     * @param {PIXI.FilterSystem} filterManager - The renderer to retrieve the filter from
     * @param {PIXI.RenderTexture} input - The input render target.
     * @param {PIXI.RenderTexture} output - The target to output to.
     * @param {PIXI.CLEAR_MODES} [clearMode] - Should the output be cleared before rendering to it.
     * @param {object} [_currentState] - It's current state of filter.
     *        There are some useful properties in the currentState :
     *        target, filters, sourceFrame, destinationFrame, renderTarget, resolution
     */
    apply(filterManager, input, output, clearMode, _currentState) {
      filterManager.applyFilter(this, input, output, clearMode);
    }
    /**
     * Sets the blend mode of the filter.
     * @default PIXI.BLEND_MODES.NORMAL
     */
    get blendMode() {
      return this.state.blendMode;
    }
    set blendMode(value) {
      this.state.blendMode = value;
    }
    /**
     * The resolution of the filter. Setting this to be lower will lower the quality but
     * increase the performance of the filter.
     * If set to `null` or `0`, the resolution of the current render target is used.
     * @default PIXI.Filter.defaultResolution
     */
    get resolution() {
      return this._resolution;
    }
    set resolution(value) {
      this._resolution = value;
    }
    /**
     * The default vertex shader source
     * @readonly
     */
    static get defaultVertexSrc() {
      return defaultVertex;
    }
    /**
     * The default fragment shader source
     * @readonly
     */
    static get defaultFragmentSrc() {
      return defaultFragment;
    }
  };
  _Filter.defaultResolution = 1, /**
  * Default filter samples for any filter.
  * @static
  * @type {PIXI.MSAA_QUALITY|null}
  * @default PIXI.MSAA_QUALITY.NONE
  */
  _Filter.defaultMultisample = MSAA_QUALITY.NONE;
  let Filter = _Filter;
  class BackgroundSystem {
    constructor() {
      this.clearBeforeRender = true, this._backgroundColor = new Color(0), this.alpha = 1;
    }
    /**
     * initiates the background system
     * @param {PIXI.IRendererOptions} options - the options for the background colors
     */
    init(options) {
      this.clearBeforeRender = options.clearBeforeRender;
      const { backgroundColor, background, backgroundAlpha } = options, color = background ?? backgroundColor;
      color !== void 0 && (this.color = color), this.alpha = backgroundAlpha;
    }
    /**
     * The background color to fill if not transparent.
     * @member {PIXI.ColorSource}
     */
    get color() {
      return this._backgroundColor.value;
    }
    set color(value) {
      this._backgroundColor.setValue(value);
    }
    /**
     * The background color alpha. Setting this to 0 will make the canvas transparent.
     * @member {number}
     */
    get alpha() {
      return this._backgroundColor.alpha;
    }
    set alpha(value) {
      this._backgroundColor.setAlpha(value);
    }
    /** The background color object. */
    get backgroundColor() {
      return this._backgroundColor;
    }
    destroy() {
    }
  }
  BackgroundSystem.defaultOptions = {
    /**
     * {@link PIXI.IRendererOptions.backgroundAlpha}
     * @default 1
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    backgroundAlpha: 1,
    /**
     * {@link PIXI.IRendererOptions.backgroundColor}
     * @default 0x000000
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    backgroundColor: 0,
    /**
     * {@link PIXI.IRendererOptions.clearBeforeRender}
     * @default true
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    clearBeforeRender: true
  }, /** @ignore */
  BackgroundSystem.extension = {
    type: [
      ExtensionType.RendererSystem,
      ExtensionType.CanvasRendererSystem
    ],
    name: "background"
  };
  extensions.add(BackgroundSystem);
  class BatchSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.emptyRenderer = new ObjectRenderer(renderer), this.currentRenderer = this.emptyRenderer;
    }
    /**
     * Changes the current renderer to the one given in parameter
     * @param objectRenderer - The object renderer to use.
     */
    setObjectRenderer(objectRenderer) {
      this.currentRenderer !== objectRenderer && (this.currentRenderer.stop(), this.currentRenderer = objectRenderer, this.currentRenderer.start());
    }
    /**
     * This should be called if you wish to do some custom rendering
     * It will basically render anything that may be batched up such as sprites
     */
    flush() {
      this.setObjectRenderer(this.emptyRenderer);
    }
    /** Reset the system to an empty renderer */
    reset() {
      this.setObjectRenderer(this.emptyRenderer);
    }
    /**
     * Handy function for batch renderers: copies bound textures in first maxTextures locations to array
     * sets actual _batchLocation for them
     * @param arr - arr copy destination
     * @param maxTextures - number of copied elements
     */
    copyBoundTextures(arr, maxTextures) {
      const { boundTextures } = this.renderer.texture;
      for (let i2 = maxTextures - 1; i2 >= 0; --i2)
        arr[i2] = boundTextures[i2] || null, arr[i2] && (arr[i2]._batchLocation = i2);
    }
    /**
     * Assigns batch locations to textures in array based on boundTextures state.
     * All textures in texArray should have `_batchEnabled = _batchId`,
     * and their count should be less than `maxTextures`.
     * @param texArray - textures to bound
     * @param boundTextures - current state of bound textures
     * @param batchId - marker for _batchEnabled param of textures in texArray
     * @param maxTextures - number of texture locations to manipulate
     */
    boundArray(texArray, boundTextures, batchId, maxTextures) {
      const { elements, ids, count } = texArray;
      let j2 = 0;
      for (let i2 = 0; i2 < count; i2++) {
        const tex = elements[i2], loc = tex._batchLocation;
        if (loc >= 0 && loc < maxTextures && boundTextures[loc] === tex) {
          ids[i2] = loc;
          continue;
        }
        for (; j2 < maxTextures; ) {
          const bound = boundTextures[j2];
          if (bound && bound._batchEnabled === batchId && bound._batchLocation === j2) {
            j2++;
            continue;
          }
          ids[i2] = j2, tex._batchLocation = j2, boundTextures[j2] = tex;
          break;
        }
      }
    }
    /**
     * @ignore
     */
    destroy() {
      this.renderer = null;
    }
  }
  BatchSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "batch"
  };
  extensions.add(BatchSystem);
  let CONTEXT_UID_COUNTER = 0;
  class ContextSystem {
    /** @param renderer - The renderer this System works for. */
    constructor(renderer) {
      this.renderer = renderer, this.webGLVersion = 1, this.extensions = {}, this.supports = {
        uint32Indices: false
      }, this.handleContextLost = this.handleContextLost.bind(this), this.handleContextRestored = this.handleContextRestored.bind(this);
    }
    /**
     * `true` if the context is lost
     * @readonly
     */
    get isLost() {
      return !this.gl || this.gl.isContextLost();
    }
    /**
     * Handles the context change event.
     * @param {WebGLRenderingContext} gl - New WebGL context.
     */
    contextChange(gl) {
      this.gl = gl, this.renderer.gl = gl, this.renderer.CONTEXT_UID = CONTEXT_UID_COUNTER++;
    }
    init(options) {
      if (options.context)
        this.initFromContext(options.context);
      else {
        const alpha = this.renderer.background.alpha < 1, premultipliedAlpha = options.premultipliedAlpha;
        this.preserveDrawingBuffer = options.preserveDrawingBuffer, this.useContextAlpha = options.useContextAlpha, this.powerPreference = options.powerPreference, this.initFromOptions({
          alpha,
          premultipliedAlpha,
          antialias: options.antialias,
          stencil: true,
          preserveDrawingBuffer: options.preserveDrawingBuffer,
          powerPreference: options.powerPreference
        });
      }
    }
    /**
     * Initializes the context.
     * @protected
     * @param {WebGLRenderingContext} gl - WebGL context
     */
    initFromContext(gl) {
      this.gl = gl, this.validateContext(gl), this.renderer.gl = gl, this.renderer.CONTEXT_UID = CONTEXT_UID_COUNTER++, this.renderer.runners.contextChange.emit(gl);
      const view = this.renderer.view;
      view.addEventListener !== void 0 && (view.addEventListener("webglcontextlost", this.handleContextLost, false), view.addEventListener("webglcontextrestored", this.handleContextRestored, false));
    }
    /**
     * Initialize from context options
     * @protected
     * @see https://developer.mozilla.org/en-US/docs/Web/API/HTMLCanvasElement/getContext
     * @param {object} options - context attributes
     */
    initFromOptions(options) {
      const gl = this.createContext(this.renderer.view, options);
      this.initFromContext(gl);
    }
    /**
     * Helper class to create a WebGL Context
     * @param canvas - the canvas element that we will get the context from
     * @param options - An options object that gets passed in to the canvas element containing the
     *    context attributes
     * @see https://developer.mozilla.org/en/docs/Web/API/HTMLCanvasElement/getContext
     * @returns {WebGLRenderingContext} the WebGL context
     */
    createContext(canvas, options) {
      let gl;
      if (settings.PREFER_ENV >= ENV.WEBGL2 && (gl = canvas.getContext("webgl2", options)), gl)
        this.webGLVersion = 2;
      else if (this.webGLVersion = 1, gl = canvas.getContext("webgl", options) || canvas.getContext("experimental-webgl", options), !gl)
        throw new Error("This browser does not support WebGL. Try using the canvas renderer");
      return this.gl = gl, this.getExtensions(), this.gl;
    }
    /** Auto-populate the {@link PIXI.ContextSystem.extensions extensions}. */
    getExtensions() {
      const { gl } = this, common = {
        loseContext: gl.getExtension("WEBGL_lose_context"),
        anisotropicFiltering: gl.getExtension("EXT_texture_filter_anisotropic"),
        floatTextureLinear: gl.getExtension("OES_texture_float_linear"),
        s3tc: gl.getExtension("WEBGL_compressed_texture_s3tc"),
        s3tc_sRGB: gl.getExtension("WEBGL_compressed_texture_s3tc_srgb"),
        // eslint-disable-line camelcase
        etc: gl.getExtension("WEBGL_compressed_texture_etc"),
        etc1: gl.getExtension("WEBGL_compressed_texture_etc1"),
        pvrtc: gl.getExtension("WEBGL_compressed_texture_pvrtc") || gl.getExtension("WEBKIT_WEBGL_compressed_texture_pvrtc"),
        atc: gl.getExtension("WEBGL_compressed_texture_atc"),
        astc: gl.getExtension("WEBGL_compressed_texture_astc"),
        bptc: gl.getExtension("EXT_texture_compression_bptc")
      };
      this.webGLVersion === 1 ? Object.assign(this.extensions, common, {
        drawBuffers: gl.getExtension("WEBGL_draw_buffers"),
        depthTexture: gl.getExtension("WEBGL_depth_texture"),
        vertexArrayObject: gl.getExtension("OES_vertex_array_object") || gl.getExtension("MOZ_OES_vertex_array_object") || gl.getExtension("WEBKIT_OES_vertex_array_object"),
        uint32ElementIndex: gl.getExtension("OES_element_index_uint"),
        // Floats and half-floats
        floatTexture: gl.getExtension("OES_texture_float"),
        floatTextureLinear: gl.getExtension("OES_texture_float_linear"),
        textureHalfFloat: gl.getExtension("OES_texture_half_float"),
        textureHalfFloatLinear: gl.getExtension("OES_texture_half_float_linear")
      }) : this.webGLVersion === 2 && Object.assign(this.extensions, common, {
        // Floats and half-floats
        colorBufferFloat: gl.getExtension("EXT_color_buffer_float")
      });
    }
    /**
     * Handles a lost webgl context
     * @param {WebGLContextEvent} event - The context lost event.
     */
    handleContextLost(event) {
      event.preventDefault(), setTimeout(() => {
        this.gl.isContextLost() && this.extensions.loseContext && this.extensions.loseContext.restoreContext();
      }, 0);
    }
    /** Handles a restored webgl context. */
    handleContextRestored() {
      this.renderer.runners.contextChange.emit(this.gl);
    }
    destroy() {
      const view = this.renderer.view;
      this.renderer = null, view.removeEventListener !== void 0 && (view.removeEventListener("webglcontextlost", this.handleContextLost), view.removeEventListener("webglcontextrestored", this.handleContextRestored)), this.gl.useProgram(null), this.extensions.loseContext && this.extensions.loseContext.loseContext();
    }
    /** Handle the post-render runner event. */
    postrender() {
      this.renderer.objectRenderer.renderingToScreen && this.gl.flush();
    }
    /**
     * Validate context.
     * @param {WebGLRenderingContext} gl - Render context.
     */
    validateContext(gl) {
      const attributes = gl.getContextAttributes(), isWebGl2 = "WebGL2RenderingContext" in globalThis && gl instanceof globalThis.WebGL2RenderingContext;
      isWebGl2 && (this.webGLVersion = 2), attributes && !attributes.stencil && console.warn("Provided WebGL context does not have a stencil buffer, masks may not render correctly");
      const hasuint32 = isWebGl2 || !!gl.getExtension("OES_element_index_uint");
      this.supports.uint32Indices = hasuint32, hasuint32 || console.warn("Provided WebGL context does not support 32 index buffer, complex graphics may not render correctly");
    }
  }
  ContextSystem.defaultOptions = {
    /**
     * {@link PIXI.IRendererOptions.context}
     * @default null
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    context: null,
    /**
     * {@link PIXI.IRendererOptions.antialias}
     * @default false
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    antialias: false,
    /**
     * {@link PIXI.IRendererOptions.premultipliedAlpha}
     * @default true
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    premultipliedAlpha: true,
    /**
     * {@link PIXI.IRendererOptions.preserveDrawingBuffer}
     * @default false
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    preserveDrawingBuffer: false,
    /**
     * {@link PIXI.IRendererOptions.powerPreference}
     * @default default
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    powerPreference: "default"
  }, /** @ignore */
  ContextSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "context"
  };
  extensions.add(ContextSystem);
  class Framebuffer {
    /**
     * @param width - Width of the frame buffer
     * @param height - Height of the frame buffer
     */
    constructor(width, height) {
      if (this.width = Math.round(width), this.height = Math.round(height), !this.width || !this.height)
        throw new Error("Framebuffer width or height is zero");
      this.stencil = false, this.depth = false, this.dirtyId = 0, this.dirtyFormat = 0, this.dirtySize = 0, this.depthTexture = null, this.colorTextures = [], this.glFramebuffers = {}, this.disposeRunner = new Runner("disposeFramebuffer"), this.multisample = MSAA_QUALITY.NONE;
    }
    /**
     * Reference to the colorTexture.
     * @readonly
     */
    get colorTexture() {
      return this.colorTextures[0];
    }
    /**
     * Add texture to the colorTexture array.
     * @param index - Index of the array to add the texture to
     * @param texture - Texture to add to the array
     */
    addColorTexture(index = 0, texture) {
      return this.colorTextures[index] = texture || new BaseTexture(null, {
        scaleMode: SCALE_MODES.NEAREST,
        resolution: 1,
        mipmap: MIPMAP_MODES.OFF,
        width: this.width,
        height: this.height
      }), this.dirtyId++, this.dirtyFormat++, this;
    }
    /**
     * Add a depth texture to the frame buffer.
     * @param texture - Texture to add.
     */
    addDepthTexture(texture) {
      return this.depthTexture = texture || new BaseTexture(null, {
        scaleMode: SCALE_MODES.NEAREST,
        resolution: 1,
        width: this.width,
        height: this.height,
        mipmap: MIPMAP_MODES.OFF,
        format: FORMATS.DEPTH_COMPONENT,
        type: TYPES.UNSIGNED_SHORT
      }), this.dirtyId++, this.dirtyFormat++, this;
    }
    /** Enable depth on the frame buffer. */
    enableDepth() {
      return this.depth = true, this.dirtyId++, this.dirtyFormat++, this;
    }
    /** Enable stencil on the frame buffer. */
    enableStencil() {
      return this.stencil = true, this.dirtyId++, this.dirtyFormat++, this;
    }
    /**
     * Resize the frame buffer
     * @param width - Width of the frame buffer to resize to
     * @param height - Height of the frame buffer to resize to
     */
    resize(width, height) {
      if (width = Math.round(width), height = Math.round(height), !width || !height)
        throw new Error("Framebuffer width and height must not be zero");
      if (!(width === this.width && height === this.height)) {
        this.width = width, this.height = height, this.dirtyId++, this.dirtySize++;
        for (let i2 = 0; i2 < this.colorTextures.length; i2++) {
          const texture = this.colorTextures[i2], resolution = texture.resolution;
          texture.setSize(width / resolution, height / resolution);
        }
        if (this.depthTexture) {
          const resolution = this.depthTexture.resolution;
          this.depthTexture.setSize(width / resolution, height / resolution);
        }
      }
    }
    /** Disposes WebGL resources that are connected to this geometry. */
    dispose() {
      this.disposeRunner.emit(this, false);
    }
    /** Destroys and removes the depth texture added to this framebuffer. */
    destroyDepthTexture() {
      this.depthTexture && (this.depthTexture.destroy(), this.depthTexture = null, ++this.dirtyId, ++this.dirtyFormat);
    }
  }
  class BaseRenderTexture extends BaseTexture {
    /**
     * @param options
     * @param {number} [options.width=100] - The width of the base render texture.
     * @param {number} [options.height=100] - The height of the base render texture.
     * @param {PIXI.SCALE_MODES} [options.scaleMode=PIXI.BaseTexture.defaultOptions.scaleMode] - See {@link PIXI.SCALE_MODES}
     *   for possible values.
     * @param {number} [options.resolution=PIXI.settings.RESOLUTION] - The resolution / device pixel ratio
     *   of the texture being generated.
     * @param {PIXI.MSAA_QUALITY} [options.multisample=PIXI.MSAA_QUALITY.NONE] - The number of samples of the frame buffer.
     */
    constructor(options = {}) {
      if (typeof options == "number") {
        const width = arguments[0], height = arguments[1], scaleMode = arguments[2], resolution = arguments[3];
        options = { width, height, scaleMode, resolution };
      }
      options.width = options.width ?? 100, options.height = options.height ?? 100, options.multisample ?? (options.multisample = MSAA_QUALITY.NONE), super(null, options), this.mipmap = MIPMAP_MODES.OFF, this.valid = true, this._clear = new Color([0, 0, 0, 0]), this.framebuffer = new Framebuffer(this.realWidth, this.realHeight).addColorTexture(0, this), this.framebuffer.multisample = options.multisample, this.maskStack = [], this.filterStack = [{}];
    }
    /** Color when clearning the texture. */
    set clearColor(value) {
      this._clear.setValue(value);
    }
    get clearColor() {
      return this._clear.value;
    }
    /**
     * Color object when clearning the texture.
     * @readonly
     * @since 7.2.0
     */
    get clear() {
      return this._clear;
    }
    /**
     * Shortcut to `this.framebuffer.multisample`.
     * @default PIXI.MSAA_QUALITY.NONE
     */
    get multisample() {
      return this.framebuffer.multisample;
    }
    set multisample(value) {
      this.framebuffer.multisample = value;
    }
    /**
     * Resizes the BaseRenderTexture.
     * @param desiredWidth - The desired width to resize to.
     * @param desiredHeight - The desired height to resize to.
     */
    resize(desiredWidth, desiredHeight) {
      this.framebuffer.resize(desiredWidth * this.resolution, desiredHeight * this.resolution), this.setRealSize(this.framebuffer.width, this.framebuffer.height);
    }
    /**
     * Frees the texture and framebuffer from WebGL memory without destroying this texture object.
     * This means you can still use the texture later which will upload it to GPU
     * memory again.
     * @fires PIXI.BaseTexture#dispose
     */
    dispose() {
      this.framebuffer.dispose(), super.dispose();
    }
    /** Destroys this texture. */
    destroy() {
      super.destroy(), this.framebuffer.destroyDepthTexture(), this.framebuffer = null;
    }
  }
  class BaseImageResource extends Resource {
    /**
     * @param {PIXI.ImageSourcee} source
     */
    constructor(source) {
      const sourceAny = source, width = sourceAny.naturalWidth || sourceAny.videoWidth || sourceAny.displayWidth || sourceAny.width, height = sourceAny.naturalHeight || sourceAny.videoHeight || sourceAny.displayHeight || sourceAny.height;
      super(width, height), this.source = source, this.noSubImage = false;
    }
    /**
     * Set cross origin based detecting the url and the crossorigin
     * @param element - Element to apply crossOrigin
     * @param url - URL to check
     * @param crossorigin - Cross origin value to use
     */
    static crossOrigin(element, url2, crossorigin) {
      crossorigin === void 0 && !url2.startsWith("data:") ? element.crossOrigin = determineCrossOrigin(url2) : crossorigin !== false && (element.crossOrigin = typeof crossorigin == "string" ? crossorigin : "anonymous");
    }
    /**
     * Upload the texture to the GPU.
     * @param renderer - Upload to the renderer
     * @param baseTexture - Reference to parent texture
     * @param glTexture
     * @param {PIXI.ImageSourcee} [source] - (optional)
     * @returns - true is success
     */
    upload(renderer, baseTexture, glTexture, source) {
      const gl = renderer.gl, width = baseTexture.realWidth, height = baseTexture.realHeight;
      if (source = source || this.source, typeof HTMLImageElement < "u" && source instanceof HTMLImageElement) {
        if (!source.complete || source.naturalWidth === 0)
          return false;
      } else if (typeof HTMLVideoElement < "u" && source instanceof HTMLVideoElement && source.readyState <= 1)
        return false;
      return gl.pixelStorei(gl.UNPACK_PREMULTIPLY_ALPHA_WEBGL, baseTexture.alphaMode === ALPHA_MODES.UNPACK), !this.noSubImage && baseTexture.target === gl.TEXTURE_2D && glTexture.width === width && glTexture.height === height ? gl.texSubImage2D(gl.TEXTURE_2D, 0, 0, 0, baseTexture.format, glTexture.type, source) : (glTexture.width = width, glTexture.height = height, gl.texImage2D(baseTexture.target, 0, glTexture.internalFormat, baseTexture.format, glTexture.type, source)), true;
    }
    /**
     * Checks if source width/height was changed, resize can cause extra baseTexture update.
     * Triggers one update in any case.
     */
    update() {
      if (this.destroyed)
        return;
      const source = this.source, width = source.naturalWidth || source.videoWidth || source.width, height = source.naturalHeight || source.videoHeight || source.height;
      this.resize(width, height), super.update();
    }
    /** Destroy this {@link PIXI.BaseImageResource} */
    dispose() {
      this.source = null;
    }
  }
  class ImageResource extends BaseImageResource {
    /**
     * @param source - image source or URL
     * @param options
     * @param {boolean} [options.autoLoad=true] - start loading process
     * @param {boolean} [options.createBitmap=PIXI.settings.CREATE_IMAGE_BITMAP] - whether its required to create
     *        a bitmap before upload
     * @param {boolean} [options.crossorigin=true] - Load image using cross origin
     * @param {PIXI.ALPHA_MODES} [options.alphaMode=PIXI.ALPHA_MODES.UNPACK] - Premultiply image alpha in bitmap
     */
    constructor(source, options) {
      if (options = options || {}, typeof source == "string") {
        const imageElement = new Image();
        BaseImageResource.crossOrigin(imageElement, source, options.crossorigin), imageElement.src = source, source = imageElement;
      }
      super(source), !source.complete && this._width && this._height && (this._width = 0, this._height = 0), this.url = source.src, this._process = null, this.preserveBitmap = false, this.createBitmap = (options.createBitmap ?? settings.CREATE_IMAGE_BITMAP) && !!globalThis.createImageBitmap, this.alphaMode = typeof options.alphaMode == "number" ? options.alphaMode : null, this.bitmap = null, this._load = null, options.autoLoad !== false && this.load();
    }
    /**
     * Returns a promise when image will be loaded and processed.
     * @param createBitmap - whether process image into bitmap
     */
    load(createBitmap) {
      return this._load ? this._load : (createBitmap !== void 0 && (this.createBitmap = createBitmap), this._load = new Promise((resolve, reject) => {
        const source = this.source;
        this.url = source.src;
        const completed = () => {
          this.destroyed || (source.onload = null, source.onerror = null, this.update(), this._load = null, this.createBitmap ? resolve(this.process()) : resolve(this));
        };
        source.complete && source.src ? completed() : (source.onload = completed, source.onerror = (event) => {
          reject(event), this.onError.emit(event);
        });
      }), this._load);
    }
    /**
     * Called when we need to convert image into BitmapImage.
     * Can be called multiple times, real promise is cached inside.
     * @returns - Cached promise to fill that bitmap
     */
    process() {
      const source = this.source;
      if (this._process !== null)
        return this._process;
      if (this.bitmap !== null || !globalThis.createImageBitmap)
        return Promise.resolve(this);
      const createImageBitmap2 = globalThis.createImageBitmap, cors = !source.crossOrigin || source.crossOrigin === "anonymous";
      return this._process = fetch(
        source.src,
        {
          mode: cors ? "cors" : "no-cors"
        }
      ).then((r2) => r2.blob()).then((blob) => createImageBitmap2(
        blob,
        0,
        0,
        source.width,
        source.height,
        {
          premultiplyAlpha: this.alphaMode === null || this.alphaMode === ALPHA_MODES.UNPACK ? "premultiply" : "none"
        }
      )).then((bitmap) => this.destroyed ? Promise.reject() : (this.bitmap = bitmap, this.update(), this._process = null, Promise.resolve(this))), this._process;
    }
    /**
     * Upload the image resource to GPU.
     * @param renderer - Renderer to upload to
     * @param baseTexture - BaseTexture for this resource
     * @param glTexture - GLTexture to use
     * @returns {boolean} true is success
     */
    upload(renderer, baseTexture, glTexture) {
      if (typeof this.alphaMode == "number" && (baseTexture.alphaMode = this.alphaMode), !this.createBitmap)
        return super.upload(renderer, baseTexture, glTexture);
      if (!this.bitmap && (this.process(), !this.bitmap))
        return false;
      if (super.upload(renderer, baseTexture, glTexture, this.bitmap), !this.preserveBitmap) {
        let flag = true;
        const glTextures = baseTexture._glTextures;
        for (const key in glTextures) {
          const otherTex = glTextures[key];
          if (otherTex !== glTexture && otherTex.dirtyId !== baseTexture.dirtyId) {
            flag = false;
            break;
          }
        }
        flag && (this.bitmap.close && this.bitmap.close(), this.bitmap = null);
      }
      return true;
    }
    /** Destroys this resource. */
    dispose() {
      this.source.onload = null, this.source.onerror = null, super.dispose(), this.bitmap && (this.bitmap.close(), this.bitmap = null), this._process = null, this._load = null;
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @returns {boolean} `true` if current environment support HTMLImageElement, and source is string or HTMLImageElement
     */
    static test(source) {
      return typeof HTMLImageElement < "u" && (typeof source == "string" || source instanceof HTMLImageElement);
    }
  }
  class TextureUvs {
    constructor() {
      this.x0 = 0, this.y0 = 0, this.x1 = 1, this.y1 = 0, this.x2 = 1, this.y2 = 1, this.x3 = 0, this.y3 = 1, this.uvsFloat32 = new Float32Array(8);
    }
    /**
     * Sets the texture Uvs based on the given frame information.
     * @protected
     * @param frame - The frame of the texture
     * @param baseFrame - The base frame of the texture
     * @param rotate - Rotation of frame, see {@link PIXI.groupD8}
     */
    set(frame, baseFrame, rotate) {
      const tw = baseFrame.width, th = baseFrame.height;
      if (rotate) {
        const w2 = frame.width / 2 / tw, h2 = frame.height / 2 / th, cX = frame.x / tw + w2, cY = frame.y / th + h2;
        rotate = groupD8.add(rotate, groupD8.NW), this.x0 = cX + w2 * groupD8.uX(rotate), this.y0 = cY + h2 * groupD8.uY(rotate), rotate = groupD8.add(rotate, 2), this.x1 = cX + w2 * groupD8.uX(rotate), this.y1 = cY + h2 * groupD8.uY(rotate), rotate = groupD8.add(rotate, 2), this.x2 = cX + w2 * groupD8.uX(rotate), this.y2 = cY + h2 * groupD8.uY(rotate), rotate = groupD8.add(rotate, 2), this.x3 = cX + w2 * groupD8.uX(rotate), this.y3 = cY + h2 * groupD8.uY(rotate);
      } else
        this.x0 = frame.x / tw, this.y0 = frame.y / th, this.x1 = (frame.x + frame.width) / tw, this.y1 = frame.y / th, this.x2 = (frame.x + frame.width) / tw, this.y2 = (frame.y + frame.height) / th, this.x3 = frame.x / tw, this.y3 = (frame.y + frame.height) / th;
      this.uvsFloat32[0] = this.x0, this.uvsFloat32[1] = this.y0, this.uvsFloat32[2] = this.x1, this.uvsFloat32[3] = this.y1, this.uvsFloat32[4] = this.x2, this.uvsFloat32[5] = this.y2, this.uvsFloat32[6] = this.x3, this.uvsFloat32[7] = this.y3;
    }
  }
  TextureUvs.prototype.toString = function() {
    return `[@pixi/core:TextureUvs x0=${this.x0} y0=${this.y0} x1=${this.x1} y1=${this.y1} x2=${this.x2} y2=${this.y2} x3=${this.x3} y3=${this.y3}]`;
  };
  const DEFAULT_UVS = new TextureUvs();
  function removeAllHandlers(tex) {
    tex.destroy = function() {
    }, tex.on = function() {
    }, tex.once = function() {
    }, tex.emit = function() {
    };
  }
  class Texture extends EventEmitter {
    /**
     * @param baseTexture - The base texture source to create the texture from
     * @param frame - The rectangle frame of the texture to show
     * @param orig - The area of original texture
     * @param trim - Trimmed rectangle of original texture
     * @param rotate - indicates how the texture was rotated by texture packer. See {@link PIXI.groupD8}
     * @param anchor - Default anchor point used for sprite placement / rotation
     * @param borders - Default borders used for 9-slice scaling. See {@link PIXI.NineSlicePlane}
     */
    constructor(baseTexture, frame, orig, trim, rotate, anchor, borders) {
      if (super(), this.noFrame = false, frame || (this.noFrame = true, frame = new Rectangle(0, 0, 1, 1)), baseTexture instanceof Texture && (baseTexture = baseTexture.baseTexture), this.baseTexture = baseTexture, this._frame = frame, this.trim = trim, this.valid = false, this.destroyed = false, this._uvs = DEFAULT_UVS, this.uvMatrix = null, this.orig = orig || frame, this._rotate = Number(rotate || 0), rotate === true)
        this._rotate = 2;
      else if (this._rotate % 2 !== 0)
        throw new Error("attempt to use diamond-shaped UVs. If you are sure, set rotation manually");
      this.defaultAnchor = anchor ? new Point(anchor.x, anchor.y) : new Point(0, 0), this.defaultBorders = borders, this._updateID = 0, this.textureCacheIds = [], baseTexture.valid ? this.noFrame ? baseTexture.valid && this.onBaseTextureUpdated(baseTexture) : this.frame = frame : baseTexture.once("loaded", this.onBaseTextureUpdated, this), this.noFrame && baseTexture.on("update", this.onBaseTextureUpdated, this);
    }
    /**
     * Updates this texture on the gpu.
     *
     * Calls the TextureResource update.
     *
     * If you adjusted `frame` manually, please call `updateUvs()` instead.
     */
    update() {
      this.baseTexture.resource && this.baseTexture.resource.update();
    }
    /**
     * Called when the base texture is updated
     * @protected
     * @param baseTexture - The base texture.
     */
    onBaseTextureUpdated(baseTexture) {
      if (this.noFrame) {
        if (!this.baseTexture.valid)
          return;
        this._frame.width = baseTexture.width, this._frame.height = baseTexture.height, this.valid = true, this.updateUvs();
      } else
        this.frame = this._frame;
      this.emit("update", this);
    }
    /**
     * Destroys this texture
     * @param [destroyBase=false] - Whether to destroy the base texture as well
     * @fires PIXI.Texture#destroyed
     */
    destroy(destroyBase) {
      if (this.baseTexture) {
        if (destroyBase) {
          const { resource } = this.baseTexture;
          resource?.url && TextureCache[resource.url] && Texture.removeFromCache(resource.url), this.baseTexture.destroy();
        }
        this.baseTexture.off("loaded", this.onBaseTextureUpdated, this), this.baseTexture.off("update", this.onBaseTextureUpdated, this), this.baseTexture = null;
      }
      this._frame = null, this._uvs = null, this.trim = null, this.orig = null, this.valid = false, Texture.removeFromCache(this), this.textureCacheIds = null, this.destroyed = true, this.emit("destroyed", this), this.removeAllListeners();
    }
    /**
     * Creates a new texture object that acts the same as this one.
     * @returns - The new texture
     */
    clone() {
      const clonedFrame = this._frame.clone(), clonedOrig = this._frame === this.orig ? clonedFrame : this.orig.clone(), clonedTexture = new Texture(
        this.baseTexture,
        !this.noFrame && clonedFrame,
        clonedOrig,
        this.trim?.clone(),
        this.rotate,
        this.defaultAnchor,
        this.defaultBorders
      );
      return this.noFrame && (clonedTexture._frame = clonedFrame), clonedTexture;
    }
    /**
     * Updates the internal WebGL UV cache. Use it after you change `frame` or `trim` of the texture.
     * Call it after changing the frame
     */
    updateUvs() {
      this._uvs === DEFAULT_UVS && (this._uvs = new TextureUvs()), this._uvs.set(this._frame, this.baseTexture, this.rotate), this._updateID++;
    }
    /**
     * Helper function that creates a new Texture based on the source you provide.
     * The source can be - frame id, image url, video url, canvas element, video element, base texture
     * @param {string|PIXI.BaseTexture|HTMLImageElement|HTMLVideoElement|ImageBitmap|PIXI.ICanvas} source -
     *        Source or array of sources to create texture from
     * @param options - See {@link PIXI.BaseTexture}'s constructor for options.
     * @param {string} [options.pixiIdPrefix=pixiid] - If a source has no id, this is the prefix of the generated id
     * @param {boolean} [strict] - Enforce strict-mode, see {@link PIXI.settings.STRICT_TEXTURE_CACHE}.
     * @returns {PIXI.Texture} The newly created texture
     */
    static from(source, options = {}, strict = settings.STRICT_TEXTURE_CACHE) {
      const isFrame = typeof source == "string";
      let cacheId = null;
      if (isFrame)
        cacheId = source;
      else if (source instanceof BaseTexture) {
        if (!source.cacheId) {
          const prefix = options?.pixiIdPrefix || "pixiid";
          source.cacheId = `${prefix}-${uid()}`, BaseTexture.addToCache(source, source.cacheId);
        }
        cacheId = source.cacheId;
      } else {
        if (!source._pixiId) {
          const prefix = options?.pixiIdPrefix || "pixiid";
          source._pixiId = `${prefix}_${uid()}`;
        }
        cacheId = source._pixiId;
      }
      let texture = TextureCache[cacheId];
      if (isFrame && strict && !texture)
        throw new Error(`The cacheId "${cacheId}" does not exist in TextureCache.`);
      return !texture && !(source instanceof BaseTexture) ? (options.resolution || (options.resolution = getResolutionOfUrl(source)), texture = new Texture(new BaseTexture(source, options)), texture.baseTexture.cacheId = cacheId, BaseTexture.addToCache(texture.baseTexture, cacheId), Texture.addToCache(texture, cacheId)) : !texture && source instanceof BaseTexture && (texture = new Texture(source), Texture.addToCache(texture, cacheId)), texture;
    }
    /**
     * Useful for loading textures via URLs. Use instead of `Texture.from` because
     * it does a better job of handling failed URLs more effectively. This also ignores
     * `PIXI.settings.STRICT_TEXTURE_CACHE`. Works for Videos, SVGs, Images.
     * @param url - The remote URL or array of URLs to load.
     * @param options - Optional options to include
     * @returns - A Promise that resolves to a Texture.
     */
    static fromURL(url2, options) {
      const resourceOptions = Object.assign({ autoLoad: false }, options?.resourceOptions), texture = Texture.from(url2, Object.assign({ resourceOptions }, options), false), resource = texture.baseTexture.resource;
      return texture.baseTexture.valid ? Promise.resolve(texture) : resource.load().then(() => Promise.resolve(texture));
    }
    /**
     * Create a new Texture with a BufferResource from a typed array.
     * @param buffer - The optional array to use. If no data is provided, a new Float32Array is created.
     * @param width - Width of the resource
     * @param height - Height of the resource
     * @param options - See {@link PIXI.BaseTexture}'s constructor for options.
     *        Default properties are different from the constructor's defaults.
     * @param {PIXI.FORMATS} [options.format] - The format is not given, the type is inferred from the
     *        type of the buffer: `RGBA` if Float32Array, Int8Array, Uint8Array, or Uint8ClampedArray,
     *        otherwise `RGBA_INTEGER`.
     * @param {PIXI.TYPES} [options.type] - The type is not given, the type is inferred from the
     *        type of the buffer. Maps Float32Array to `FLOAT`, Int32Array to `INT`, Uint32Array to
     *        `UNSIGNED_INT`, Int16Array to `SHORT`, Uint16Array to `UNSIGNED_SHORT`, Int8Array to `BYTE`,
     *        Uint8Array/Uint8ClampedArray to `UNSIGNED_BYTE`.
     * @param {PIXI.ALPHA_MODES} [options.alphaMode=PIXI.ALPHA_MODES.NPM]
     * @param {PIXI.SCALE_MODES} [options.scaleMode=PIXI.SCALE_MODES.NEAREST]
     * @returns - The resulting new BaseTexture
     */
    static fromBuffer(buffer, width, height, options) {
      return new Texture(BaseTexture.fromBuffer(buffer, width, height, options));
    }
    /**
     * Create a texture from a source and add to the cache.
     * @param {HTMLImageElement|HTMLVideoElement|ImageBitmap|PIXI.ICanvas|string} source - The input source.
     * @param imageUrl - File name of texture, for cache and resolving resolution.
     * @param name - Human readable name for the texture cache. If no name is
     *        specified, only `imageUrl` will be used as the cache ID.
     * @param options
     * @returns - Output texture
     */
    static fromLoader(source, imageUrl, name, options) {
      const baseTexture = new BaseTexture(source, Object.assign({
        scaleMode: BaseTexture.defaultOptions.scaleMode,
        resolution: getResolutionOfUrl(imageUrl)
      }, options)), { resource } = baseTexture;
      resource instanceof ImageResource && (resource.url = imageUrl);
      const texture = new Texture(baseTexture);
      return name || (name = imageUrl), BaseTexture.addToCache(texture.baseTexture, name), Texture.addToCache(texture, name), name !== imageUrl && (BaseTexture.addToCache(texture.baseTexture, imageUrl), Texture.addToCache(texture, imageUrl)), texture.baseTexture.valid ? Promise.resolve(texture) : new Promise((resolve) => {
        texture.baseTexture.once("loaded", () => resolve(texture));
      });
    }
    /**
     * Adds a Texture to the global TextureCache. This cache is shared across the whole PIXI object.
     * @param texture - The Texture to add to the cache.
     * @param id - The id that the Texture will be stored against.
     */
    static addToCache(texture, id) {
      id && (texture.textureCacheIds.includes(id) || texture.textureCacheIds.push(id), TextureCache[id] && TextureCache[id] !== texture && console.warn(`Texture added to the cache with an id [${id}] that already had an entry`), TextureCache[id] = texture);
    }
    /**
     * Remove a Texture from the global TextureCache.
     * @param texture - id of a Texture to be removed, or a Texture instance itself
     * @returns - The Texture that was removed
     */
    static removeFromCache(texture) {
      if (typeof texture == "string") {
        const textureFromCache = TextureCache[texture];
        if (textureFromCache) {
          const index = textureFromCache.textureCacheIds.indexOf(texture);
          return index > -1 && textureFromCache.textureCacheIds.splice(index, 1), delete TextureCache[texture], textureFromCache;
        }
      } else if (texture?.textureCacheIds) {
        for (let i2 = 0; i2 < texture.textureCacheIds.length; ++i2)
          TextureCache[texture.textureCacheIds[i2]] === texture && delete TextureCache[texture.textureCacheIds[i2]];
        return texture.textureCacheIds.length = 0, texture;
      }
      return null;
    }
    /**
     * Returns resolution of baseTexture
     * @readonly
     */
    get resolution() {
      return this.baseTexture.resolution;
    }
    /**
     * The frame specifies the region of the base texture that this texture uses.
     * Please call `updateUvs()` after you change coordinates of `frame` manually.
     */
    get frame() {
      return this._frame;
    }
    set frame(frame) {
      this._frame = frame, this.noFrame = false;
      const { x: x2, y: y2, width, height } = frame, xNotFit = x2 + width > this.baseTexture.width, yNotFit = y2 + height > this.baseTexture.height;
      if (xNotFit || yNotFit) {
        const relationship = xNotFit && yNotFit ? "and" : "or", errorX = `X: ${x2} + ${width} = ${x2 + width} > ${this.baseTexture.width}`, errorY = `Y: ${y2} + ${height} = ${y2 + height} > ${this.baseTexture.height}`;
        throw new Error(`Texture Error: frame does not fit inside the base Texture dimensions: ${errorX} ${relationship} ${errorY}`);
      }
      this.valid = width && height && this.baseTexture.valid, !this.trim && !this.rotate && (this.orig = frame), this.valid && this.updateUvs();
    }
    /**
     * Indicates whether the texture is rotated inside the atlas
     * set to 2 to compensate for texture packer rotation
     * set to 6 to compensate for spine packer rotation
     * can be used to rotate or mirror sprites
     * See {@link PIXI.groupD8} for explanation
     */
    get rotate() {
      return this._rotate;
    }
    set rotate(rotate) {
      this._rotate = rotate, this.valid && this.updateUvs();
    }
    /** The width of the Texture in pixels. */
    get width() {
      return this.orig.width;
    }
    /** The height of the Texture in pixels. */
    get height() {
      return this.orig.height;
    }
    /** Utility function for BaseTexture|Texture cast. */
    castToBaseTexture() {
      return this.baseTexture;
    }
    /** An empty texture, used often to not have to create multiple empty textures. Can not be destroyed. */
    static get EMPTY() {
      return Texture._EMPTY || (Texture._EMPTY = new Texture(new BaseTexture()), removeAllHandlers(Texture._EMPTY), removeAllHandlers(Texture._EMPTY.baseTexture)), Texture._EMPTY;
    }
    /** A white texture of 16x16 size, used for graphics and other things Can not be destroyed. */
    static get WHITE() {
      if (!Texture._WHITE) {
        const canvas = settings.ADAPTER.createCanvas(16, 16), context2 = canvas.getContext("2d");
        canvas.width = 16, canvas.height = 16, context2.fillStyle = "white", context2.fillRect(0, 0, 16, 16), Texture._WHITE = new Texture(BaseTexture.from(canvas)), removeAllHandlers(Texture._WHITE), removeAllHandlers(Texture._WHITE.baseTexture);
      }
      return Texture._WHITE;
    }
  }
  class RenderTexture extends Texture {
    /**
     * @param baseRenderTexture - The base texture object that this texture uses.
     * @param frame - The rectangle frame of the texture to show.
     */
    constructor(baseRenderTexture, frame) {
      super(baseRenderTexture, frame), this.valid = true, this.filterFrame = null, this.filterPoolKey = null, this.updateUvs();
    }
    /**
     * Shortcut to `this.baseTexture.framebuffer`, saves baseTexture cast.
     * @readonly
     */
    get framebuffer() {
      return this.baseTexture.framebuffer;
    }
    /**
     * Shortcut to `this.framebuffer.multisample`.
     * @default PIXI.MSAA_QUALITY.NONE
     */
    get multisample() {
      return this.framebuffer.multisample;
    }
    set multisample(value) {
      this.framebuffer.multisample = value;
    }
    /**
     * Resizes the RenderTexture.
     * @param desiredWidth - The desired width to resize to.
     * @param desiredHeight - The desired height to resize to.
     * @param resizeBaseTexture - Should the baseTexture.width and height values be resized as well?
     */
    resize(desiredWidth, desiredHeight, resizeBaseTexture = true) {
      const resolution = this.baseTexture.resolution, width = Math.round(desiredWidth * resolution) / resolution, height = Math.round(desiredHeight * resolution) / resolution;
      this.valid = width > 0 && height > 0, this._frame.width = this.orig.width = width, this._frame.height = this.orig.height = height, resizeBaseTexture && this.baseTexture.resize(width, height), this.updateUvs();
    }
    /**
     * Changes the resolution of baseTexture, but does not change framebuffer size.
     * @param resolution - The new resolution to apply to RenderTexture
     */
    setResolution(resolution) {
      const { baseTexture } = this;
      baseTexture.resolution !== resolution && (baseTexture.setResolution(resolution), this.resize(baseTexture.width, baseTexture.height, false));
    }
    /**
     * A short hand way of creating a render texture.
     * @param options - Options
     * @param {number} [options.width=100] - The width of the render texture
     * @param {number} [options.height=100] - The height of the render texture
     * @param {PIXI.SCALE_MODES} [options.scaleMode=PIXI.BaseTexture.defaultOptions.scaleMode] - See {@link PIXI.SCALE_MODES}
     *    for possible values
     * @param {number} [options.resolution=PIXI.settings.RESOLUTION] - The resolution / device pixel ratio of the texture
     *    being generated
     * @param {PIXI.MSAA_QUALITY} [options.multisample=PIXI.MSAA_QUALITY.NONE] - The number of samples of the frame buffer
     * @returns The new render texture
     */
    static create(options) {
      return new RenderTexture(new BaseRenderTexture(options));
    }
  }
  class RenderTexturePool {
    /**
     * @param textureOptions - options that will be passed to BaseRenderTexture constructor
     * @param {PIXI.SCALE_MODES} [textureOptions.scaleMode] - See {@link PIXI.SCALE_MODES} for possible values.
     */
    constructor(textureOptions) {
      this.texturePool = {}, this.textureOptions = textureOptions || {}, this.enableFullScreen = false, this._pixelsWidth = 0, this._pixelsHeight = 0;
    }
    /**
     * Creates texture with params that were specified in pool constructor.
     * @param realWidth - Width of texture in pixels.
     * @param realHeight - Height of texture in pixels.
     * @param multisample - Number of samples of the framebuffer.
     */
    createTexture(realWidth, realHeight, multisample = MSAA_QUALITY.NONE) {
      const baseRenderTexture = new BaseRenderTexture(Object.assign({
        width: realWidth,
        height: realHeight,
        resolution: 1,
        multisample
      }, this.textureOptions));
      return new RenderTexture(baseRenderTexture);
    }
    /**
     * Gets a Power-of-Two render texture or fullScreen texture
     * @param minWidth - The minimum width of the render texture.
     * @param minHeight - The minimum height of the render texture.
     * @param resolution - The resolution of the render texture.
     * @param multisample - Number of samples of the render texture.
     * @returns The new render texture.
     */
    getOptimalTexture(minWidth, minHeight, resolution = 1, multisample = MSAA_QUALITY.NONE) {
      let key;
      minWidth = Math.max(Math.ceil(minWidth * resolution - 1e-6), 1), minHeight = Math.max(Math.ceil(minHeight * resolution - 1e-6), 1), !this.enableFullScreen || minWidth !== this._pixelsWidth || minHeight !== this._pixelsHeight ? (minWidth = nextPow2(minWidth), minHeight = nextPow2(minHeight), key = ((minWidth & 65535) << 16 | minHeight & 65535) >>> 0, multisample > 1 && (key += multisample * 4294967296)) : key = multisample > 1 ? -multisample : -1, this.texturePool[key] || (this.texturePool[key] = []);
      let renderTexture = this.texturePool[key].pop();
      return renderTexture || (renderTexture = this.createTexture(minWidth, minHeight, multisample)), renderTexture.filterPoolKey = key, renderTexture.setResolution(resolution), renderTexture;
    }
    /**
     * Gets extra texture of the same size as input renderTexture
     *
     * `getFilterTexture(input, 0.5)` or `getFilterTexture(0.5, input)`
     * @param input - renderTexture from which size and resolution will be copied
     * @param resolution - override resolution of the renderTexture
     *  It overrides, it does not multiply
     * @param multisample - number of samples of the renderTexture
     */
    getFilterTexture(input, resolution, multisample) {
      const filterTexture = this.getOptimalTexture(
        input.width,
        input.height,
        resolution || input.resolution,
        multisample || MSAA_QUALITY.NONE
      );
      return filterTexture.filterFrame = input.filterFrame, filterTexture;
    }
    /**
     * Place a render texture back into the pool.
     * @param renderTexture - The renderTexture to free
     */
    returnTexture(renderTexture) {
      const key = renderTexture.filterPoolKey;
      renderTexture.filterFrame = null, this.texturePool[key].push(renderTexture);
    }
    /**
     * Alias for returnTexture, to be compliant with FilterSystem interface.
     * @param renderTexture - The renderTexture to free
     */
    returnFilterTexture(renderTexture) {
      this.returnTexture(renderTexture);
    }
    /**
     * Clears the pool.
     * @param destroyTextures - Destroy all stored textures.
     */
    clear(destroyTextures) {
      if (destroyTextures = destroyTextures !== false, destroyTextures)
        for (const i2 in this.texturePool) {
          const textures = this.texturePool[i2];
          if (textures)
            for (let j2 = 0; j2 < textures.length; j2++)
              textures[j2].destroy(true);
        }
      this.texturePool = {};
    }
    /**
     * If screen size was changed, drops all screen-sized textures,
     * sets new screen size, sets `enableFullScreen` to true
     *
     * Size is measured in pixels, `renderer.view` can be passed here, not `renderer.screen`
     * @param size - Initial size of screen.
     */
    setScreenSize(size) {
      if (!(size.width === this._pixelsWidth && size.height === this._pixelsHeight)) {
        this.enableFullScreen = size.width > 0 && size.height > 0;
        for (const i2 in this.texturePool) {
          if (!(Number(i2) < 0))
            continue;
          const textures = this.texturePool[i2];
          if (textures)
            for (let j2 = 0; j2 < textures.length; j2++)
              textures[j2].destroy(true);
          this.texturePool[i2] = [];
        }
        this._pixelsWidth = size.width, this._pixelsHeight = size.height;
      }
    }
  }
  RenderTexturePool.SCREEN_KEY = -1;
  class Quad extends Geometry {
    constructor() {
      super(), this.addAttribute("aVertexPosition", new Float32Array([
        0,
        0,
        1,
        0,
        1,
        1,
        0,
        1
      ])).addIndex([0, 1, 3, 2]);
    }
  }
  class QuadUv extends Geometry {
    constructor() {
      super(), this.vertices = new Float32Array([
        -1,
        -1,
        1,
        -1,
        1,
        1,
        -1,
        1
      ]), this.uvs = new Float32Array([
        0,
        0,
        1,
        0,
        1,
        1,
        0,
        1
      ]), this.vertexBuffer = new Buffer(this.vertices), this.uvBuffer = new Buffer(this.uvs), this.addAttribute("aVertexPosition", this.vertexBuffer).addAttribute("aTextureCoord", this.uvBuffer).addIndex([0, 1, 2, 0, 2, 3]);
    }
    /**
     * Maps two Rectangle to the quad.
     * @param targetTextureFrame - The first rectangle
     * @param destinationFrame - The second rectangle
     * @returns - Returns itself.
     */
    map(targetTextureFrame, destinationFrame) {
      let x2 = 0, y2 = 0;
      return this.uvs[0] = x2, this.uvs[1] = y2, this.uvs[2] = x2 + destinationFrame.width / targetTextureFrame.width, this.uvs[3] = y2, this.uvs[4] = x2 + destinationFrame.width / targetTextureFrame.width, this.uvs[5] = y2 + destinationFrame.height / targetTextureFrame.height, this.uvs[6] = x2, this.uvs[7] = y2 + destinationFrame.height / targetTextureFrame.height, x2 = destinationFrame.x, y2 = destinationFrame.y, this.vertices[0] = x2, this.vertices[1] = y2, this.vertices[2] = x2 + destinationFrame.width, this.vertices[3] = y2, this.vertices[4] = x2 + destinationFrame.width, this.vertices[5] = y2 + destinationFrame.height, this.vertices[6] = x2, this.vertices[7] = y2 + destinationFrame.height, this.invalidate(), this;
    }
    /**
     * Legacy upload method, just marks buffers dirty.
     * @returns - Returns itself.
     */
    invalidate() {
      return this.vertexBuffer._updateID++, this.uvBuffer._updateID++, this;
    }
  }
  class FilterState {
    constructor() {
      this.renderTexture = null, this.target = null, this.legacy = false, this.resolution = 1, this.multisample = MSAA_QUALITY.NONE, this.sourceFrame = new Rectangle(), this.destinationFrame = new Rectangle(), this.bindingSourceFrame = new Rectangle(), this.bindingDestinationFrame = new Rectangle(), this.filters = [], this.transform = null;
    }
    /** Clears the state */
    clear() {
      this.target = null, this.filters = null, this.renderTexture = null;
    }
  }
  const tempPoints = [new Point(), new Point(), new Point(), new Point()], tempMatrix$2 = new Matrix();
  class FilterSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.defaultFilterStack = [{}], this.texturePool = new RenderTexturePool(), this.statePool = [], this.quad = new Quad(), this.quadUv = new QuadUv(), this.tempRect = new Rectangle(), this.activeState = {}, this.globalUniforms = new UniformGroup({
        outputFrame: new Rectangle(),
        inputSize: new Float32Array(4),
        inputPixel: new Float32Array(4),
        inputClamp: new Float32Array(4),
        resolution: 1,
        // legacy variables
        filterArea: new Float32Array(4),
        filterClamp: new Float32Array(4)
      }, true), this.forceClear = false, this.useMaxPadding = false;
    }
    init() {
      this.texturePool.setScreenSize(this.renderer.view);
    }
    /**
     * Pushes a set of filters to be applied later to the system. This will redirect further rendering into an
     * input render-texture for the rest of the filtering pipeline.
     * @param {PIXI.DisplayObject} target - The target of the filter to render.
     * @param filters - The filters to apply.
     */
    push(target, filters) {
      const renderer = this.renderer, filterStack = this.defaultFilterStack, state = this.statePool.pop() || new FilterState(), renderTextureSystem = renderer.renderTexture;
      let currentResolution, currentMultisample;
      if (renderTextureSystem.current) {
        const renderTexture = renderTextureSystem.current;
        currentResolution = renderTexture.resolution, currentMultisample = renderTexture.multisample;
      } else
        currentResolution = renderer.resolution, currentMultisample = renderer.multisample;
      let resolution = filters[0].resolution || currentResolution, multisample = filters[0].multisample ?? currentMultisample, padding = filters[0].padding, autoFit = filters[0].autoFit, legacy = filters[0].legacy ?? true;
      for (let i2 = 1; i2 < filters.length; i2++) {
        const filter = filters[i2];
        resolution = Math.min(resolution, filter.resolution || currentResolution), multisample = Math.min(multisample, filter.multisample ?? currentMultisample), padding = this.useMaxPadding ? Math.max(padding, filter.padding) : padding + filter.padding, autoFit = autoFit && filter.autoFit, legacy = legacy || (filter.legacy ?? true);
      }
      filterStack.length === 1 && (this.defaultFilterStack[0].renderTexture = renderTextureSystem.current), filterStack.push(state), state.resolution = resolution, state.multisample = multisample, state.legacy = legacy, state.target = target, state.sourceFrame.copyFrom(target.filterArea || target.getBounds(true)), state.sourceFrame.pad(padding);
      const sourceFrameProjected = this.tempRect.copyFrom(renderTextureSystem.sourceFrame);
      renderer.projection.transform && this.transformAABB(
        tempMatrix$2.copyFrom(renderer.projection.transform).invert(),
        sourceFrameProjected
      ), autoFit ? (state.sourceFrame.fit(sourceFrameProjected), (state.sourceFrame.width <= 0 || state.sourceFrame.height <= 0) && (state.sourceFrame.width = 0, state.sourceFrame.height = 0)) : state.sourceFrame.intersects(sourceFrameProjected) || (state.sourceFrame.width = 0, state.sourceFrame.height = 0), this.roundFrame(
        state.sourceFrame,
        renderTextureSystem.current ? renderTextureSystem.current.resolution : renderer.resolution,
        renderTextureSystem.sourceFrame,
        renderTextureSystem.destinationFrame,
        renderer.projection.transform
      ), state.renderTexture = this.getOptimalFilterTexture(
        state.sourceFrame.width,
        state.sourceFrame.height,
        resolution,
        multisample
      ), state.filters = filters, state.destinationFrame.width = state.renderTexture.width, state.destinationFrame.height = state.renderTexture.height;
      const destinationFrame = this.tempRect;
      destinationFrame.x = 0, destinationFrame.y = 0, destinationFrame.width = state.sourceFrame.width, destinationFrame.height = state.sourceFrame.height, state.renderTexture.filterFrame = state.sourceFrame, state.bindingSourceFrame.copyFrom(renderTextureSystem.sourceFrame), state.bindingDestinationFrame.copyFrom(renderTextureSystem.destinationFrame), state.transform = renderer.projection.transform, renderer.projection.transform = null, renderTextureSystem.bind(state.renderTexture, state.sourceFrame, destinationFrame), renderer.framebuffer.clear(0, 0, 0, 0);
    }
    /** Pops off the filter and applies it. */
    pop() {
      const filterStack = this.defaultFilterStack, state = filterStack.pop(), filters = state.filters;
      this.activeState = state;
      const globalUniforms = this.globalUniforms.uniforms;
      globalUniforms.outputFrame = state.sourceFrame, globalUniforms.resolution = state.resolution;
      const inputSize = globalUniforms.inputSize, inputPixel = globalUniforms.inputPixel, inputClamp = globalUniforms.inputClamp;
      if (inputSize[0] = state.destinationFrame.width, inputSize[1] = state.destinationFrame.height, inputSize[2] = 1 / inputSize[0], inputSize[3] = 1 / inputSize[1], inputPixel[0] = Math.round(inputSize[0] * state.resolution), inputPixel[1] = Math.round(inputSize[1] * state.resolution), inputPixel[2] = 1 / inputPixel[0], inputPixel[3] = 1 / inputPixel[1], inputClamp[0] = 0.5 * inputPixel[2], inputClamp[1] = 0.5 * inputPixel[3], inputClamp[2] = state.sourceFrame.width * inputSize[2] - 0.5 * inputPixel[2], inputClamp[3] = state.sourceFrame.height * inputSize[3] - 0.5 * inputPixel[3], state.legacy) {
        const filterArea = globalUniforms.filterArea;
        filterArea[0] = state.destinationFrame.width, filterArea[1] = state.destinationFrame.height, filterArea[2] = state.sourceFrame.x, filterArea[3] = state.sourceFrame.y, globalUniforms.filterClamp = globalUniforms.inputClamp;
      }
      this.globalUniforms.update();
      const lastState = filterStack[filterStack.length - 1];
      if (this.renderer.framebuffer.blit(), filters.length === 1)
        filters[0].apply(this, state.renderTexture, lastState.renderTexture, CLEAR_MODES.BLEND, state), this.returnFilterTexture(state.renderTexture);
      else {
        let flip = state.renderTexture, flop = this.getOptimalFilterTexture(
          flip.width,
          flip.height,
          state.resolution
        );
        flop.filterFrame = flip.filterFrame;
        let i2 = 0;
        for (i2 = 0; i2 < filters.length - 1; ++i2) {
          i2 === 1 && state.multisample > 1 && (flop = this.getOptimalFilterTexture(
            flip.width,
            flip.height,
            state.resolution
          ), flop.filterFrame = flip.filterFrame), filters[i2].apply(this, flip, flop, CLEAR_MODES.CLEAR, state);
          const t2 = flip;
          flip = flop, flop = t2;
        }
        filters[i2].apply(this, flip, lastState.renderTexture, CLEAR_MODES.BLEND, state), i2 > 1 && state.multisample > 1 && this.returnFilterTexture(state.renderTexture), this.returnFilterTexture(flip), this.returnFilterTexture(flop);
      }
      state.clear(), this.statePool.push(state);
    }
    /**
     * Binds a renderTexture with corresponding `filterFrame`, clears it if mode corresponds.
     * @param filterTexture - renderTexture to bind, should belong to filter pool or filter stack
     * @param clearMode - clearMode, by default its CLEAR/YES. See {@link PIXI.CLEAR_MODES}
     */
    bindAndClear(filterTexture, clearMode = CLEAR_MODES.CLEAR) {
      const {
        renderTexture: renderTextureSystem,
        state: stateSystem
      } = this.renderer;
      if (filterTexture === this.defaultFilterStack[this.defaultFilterStack.length - 1].renderTexture ? this.renderer.projection.transform = this.activeState.transform : this.renderer.projection.transform = null, filterTexture?.filterFrame) {
        const destinationFrame = this.tempRect;
        destinationFrame.x = 0, destinationFrame.y = 0, destinationFrame.width = filterTexture.filterFrame.width, destinationFrame.height = filterTexture.filterFrame.height, renderTextureSystem.bind(filterTexture, filterTexture.filterFrame, destinationFrame);
      } else
        filterTexture !== this.defaultFilterStack[this.defaultFilterStack.length - 1].renderTexture ? renderTextureSystem.bind(filterTexture) : this.renderer.renderTexture.bind(
          filterTexture,
          this.activeState.bindingSourceFrame,
          this.activeState.bindingDestinationFrame
        );
      const autoClear = stateSystem.stateId & 1 || this.forceClear;
      (clearMode === CLEAR_MODES.CLEAR || clearMode === CLEAR_MODES.BLIT && autoClear) && this.renderer.framebuffer.clear(0, 0, 0, 0);
    }
    /**
     * Draws a filter using the default rendering process.
     *
     * This should be called only by {@link PIXI.Filter#apply}.
     * @param filter - The filter to draw.
     * @param input - The input render target.
     * @param output - The target to output to.
     * @param clearMode - Should the output be cleared before rendering to it
     */
    applyFilter(filter, input, output, clearMode) {
      const renderer = this.renderer;
      renderer.state.set(filter.state), this.bindAndClear(output, clearMode), filter.uniforms.uSampler = input, filter.uniforms.filterGlobals = this.globalUniforms, renderer.shader.bind(filter), filter.legacy = !!filter.program.attributeData.aTextureCoord, filter.legacy ? (this.quadUv.map(input._frame, input.filterFrame), renderer.geometry.bind(this.quadUv), renderer.geometry.draw(DRAW_MODES.TRIANGLES)) : (renderer.geometry.bind(this.quad), renderer.geometry.draw(DRAW_MODES.TRIANGLE_STRIP));
    }
    /**
     * Multiply _input normalized coordinates_ to this matrix to get _sprite texture normalized coordinates_.
     *
     * Use `outputMatrix * vTextureCoord` in the shader.
     * @param outputMatrix - The matrix to output to.
     * @param {PIXI.Sprite} sprite - The sprite to map to.
     * @returns The mapped matrix.
     */
    calculateSpriteMatrix(outputMatrix, sprite) {
      const { sourceFrame, destinationFrame } = this.activeState, { orig } = sprite._texture, mappedMatrix = outputMatrix.set(
        destinationFrame.width,
        0,
        0,
        destinationFrame.height,
        sourceFrame.x,
        sourceFrame.y
      ), worldTransform = sprite.worldTransform.copyTo(Matrix.TEMP_MATRIX);
      return worldTransform.invert(), mappedMatrix.prepend(worldTransform), mappedMatrix.scale(1 / orig.width, 1 / orig.height), mappedMatrix.translate(sprite.anchor.x, sprite.anchor.y), mappedMatrix;
    }
    /** Destroys this Filter System. */
    destroy() {
      this.renderer = null, this.texturePool.clear(false);
    }
    /**
     * Gets a Power-of-Two render texture or fullScreen texture
     * @param minWidth - The minimum width of the render texture in real pixels.
     * @param minHeight - The minimum height of the render texture in real pixels.
     * @param resolution - The resolution of the render texture.
     * @param multisample - Number of samples of the render texture.
     * @returns - The new render texture.
     */
    getOptimalFilterTexture(minWidth, minHeight, resolution = 1, multisample = MSAA_QUALITY.NONE) {
      return this.texturePool.getOptimalTexture(minWidth, minHeight, resolution, multisample);
    }
    /**
     * Gets extra render texture to use inside current filter
     * To be compliant with older filters, you can use params in any order
     * @param input - renderTexture from which size and resolution will be copied
     * @param resolution - override resolution of the renderTexture
     * @param multisample - number of samples of the renderTexture
     */
    getFilterTexture(input, resolution, multisample) {
      if (typeof input == "number") {
        const swap = input;
        input = resolution, resolution = swap;
      }
      input = input || this.activeState.renderTexture;
      const filterTexture = this.texturePool.getOptimalTexture(
        input.width,
        input.height,
        resolution || input.resolution,
        multisample || MSAA_QUALITY.NONE
      );
      return filterTexture.filterFrame = input.filterFrame, filterTexture;
    }
    /**
     * Frees a render texture back into the pool.
     * @param renderTexture - The renderTarget to free
     */
    returnFilterTexture(renderTexture) {
      this.texturePool.returnTexture(renderTexture);
    }
    /** Empties the texture pool. */
    emptyPool() {
      this.texturePool.clear(true);
    }
    /** Calls `texturePool.resize()`, affects fullScreen renderTextures. */
    resize() {
      this.texturePool.setScreenSize(this.renderer.view);
    }
    /**
     * @param matrix - first param
     * @param rect - second param
     */
    transformAABB(matrix, rect) {
      const lt2 = tempPoints[0], lb = tempPoints[1], rt2 = tempPoints[2], rb = tempPoints[3];
      lt2.set(rect.left, rect.top), lb.set(rect.left, rect.bottom), rt2.set(rect.right, rect.top), rb.set(rect.right, rect.bottom), matrix.apply(lt2, lt2), matrix.apply(lb, lb), matrix.apply(rt2, rt2), matrix.apply(rb, rb);
      const x0 = Math.min(lt2.x, lb.x, rt2.x, rb.x), y0 = Math.min(lt2.y, lb.y, rt2.y, rb.y), x1 = Math.max(lt2.x, lb.x, rt2.x, rb.x), y1 = Math.max(lt2.y, lb.y, rt2.y, rb.y);
      rect.x = x0, rect.y = y0, rect.width = x1 - x0, rect.height = y1 - y0;
    }
    roundFrame(frame, resolution, bindingSourceFrame, bindingDestinationFrame, transform) {
      if (!(frame.width <= 0 || frame.height <= 0 || bindingSourceFrame.width <= 0 || bindingSourceFrame.height <= 0)) {
        if (transform) {
          const { a: a2, b: b2, c: c2, d: d2 } = transform;
          if ((Math.abs(b2) > 1e-4 || Math.abs(c2) > 1e-4) && (Math.abs(a2) > 1e-4 || Math.abs(d2) > 1e-4))
            return;
        }
        transform = transform ? tempMatrix$2.copyFrom(transform) : tempMatrix$2.identity(), transform.translate(-bindingSourceFrame.x, -bindingSourceFrame.y).scale(
          bindingDestinationFrame.width / bindingSourceFrame.width,
          bindingDestinationFrame.height / bindingSourceFrame.height
        ).translate(bindingDestinationFrame.x, bindingDestinationFrame.y), this.transformAABB(transform, frame), frame.ceil(resolution), this.transformAABB(transform.invert(), frame);
      }
    }
  }
  FilterSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "filter"
  };
  extensions.add(FilterSystem);
  class GLFramebuffer {
    constructor(framebuffer) {
      this.framebuffer = framebuffer, this.stencil = null, this.dirtyId = -1, this.dirtyFormat = -1, this.dirtySize = -1, this.multisample = MSAA_QUALITY.NONE, this.msaaBuffer = null, this.blitFramebuffer = null, this.mipLevel = 0;
    }
  }
  const tempRectangle = new Rectangle();
  class FramebufferSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.managedFramebuffers = [], this.unknownFramebuffer = new Framebuffer(10, 10), this.msaaSamples = null;
    }
    /** Sets up the renderer context and necessary buffers. */
    contextChange() {
      this.disposeAll(true);
      const gl = this.gl = this.renderer.gl;
      if (this.CONTEXT_UID = this.renderer.CONTEXT_UID, this.current = this.unknownFramebuffer, this.viewport = new Rectangle(), this.hasMRT = true, this.writeDepthTexture = true, this.renderer.context.webGLVersion === 1) {
        let nativeDrawBuffersExtension = this.renderer.context.extensions.drawBuffers, nativeDepthTextureExtension = this.renderer.context.extensions.depthTexture;
        settings.PREFER_ENV === ENV.WEBGL_LEGACY && (nativeDrawBuffersExtension = null, nativeDepthTextureExtension = null), nativeDrawBuffersExtension ? gl.drawBuffers = (activeTextures) => nativeDrawBuffersExtension.drawBuffersWEBGL(activeTextures) : (this.hasMRT = false, gl.drawBuffers = () => {
        }), nativeDepthTextureExtension || (this.writeDepthTexture = false);
      } else
        this.msaaSamples = gl.getInternalformatParameter(gl.RENDERBUFFER, gl.RGBA8, gl.SAMPLES);
    }
    /**
     * Bind a framebuffer.
     * @param framebuffer
     * @param frame - frame, default is framebuffer size
     * @param mipLevel - optional mip level to set on the framebuffer - defaults to 0
     */
    bind(framebuffer, frame, mipLevel = 0) {
      const { gl } = this;
      if (framebuffer) {
        const fbo = framebuffer.glFramebuffers[this.CONTEXT_UID] || this.initFramebuffer(framebuffer);
        this.current !== framebuffer && (this.current = framebuffer, gl.bindFramebuffer(gl.FRAMEBUFFER, fbo.framebuffer)), fbo.mipLevel !== mipLevel && (framebuffer.dirtyId++, framebuffer.dirtyFormat++, fbo.mipLevel = mipLevel), fbo.dirtyId !== framebuffer.dirtyId && (fbo.dirtyId = framebuffer.dirtyId, fbo.dirtyFormat !== framebuffer.dirtyFormat ? (fbo.dirtyFormat = framebuffer.dirtyFormat, fbo.dirtySize = framebuffer.dirtySize, this.updateFramebuffer(framebuffer, mipLevel)) : fbo.dirtySize !== framebuffer.dirtySize && (fbo.dirtySize = framebuffer.dirtySize, this.resizeFramebuffer(framebuffer)));
        for (let i2 = 0; i2 < framebuffer.colorTextures.length; i2++) {
          const tex = framebuffer.colorTextures[i2];
          this.renderer.texture.unbind(tex.parentTextureArray || tex);
        }
        if (framebuffer.depthTexture && this.renderer.texture.unbind(framebuffer.depthTexture), frame) {
          const mipWidth = frame.width >> mipLevel, mipHeight = frame.height >> mipLevel, scale = mipWidth / frame.width;
          this.setViewport(
            frame.x * scale,
            frame.y * scale,
            mipWidth,
            mipHeight
          );
        } else {
          const mipWidth = framebuffer.width >> mipLevel, mipHeight = framebuffer.height >> mipLevel;
          this.setViewport(0, 0, mipWidth, mipHeight);
        }
      } else
        this.current && (this.current = null, gl.bindFramebuffer(gl.FRAMEBUFFER, null)), frame ? this.setViewport(frame.x, frame.y, frame.width, frame.height) : this.setViewport(0, 0, this.renderer.width, this.renderer.height);
    }
    /**
     * Set the WebGLRenderingContext's viewport.
     * @param x - X position of viewport
     * @param y - Y position of viewport
     * @param width - Width of viewport
     * @param height - Height of viewport
     */
    setViewport(x2, y2, width, height) {
      const v2 = this.viewport;
      x2 = Math.round(x2), y2 = Math.round(y2), width = Math.round(width), height = Math.round(height), (v2.width !== width || v2.height !== height || v2.x !== x2 || v2.y !== y2) && (v2.x = x2, v2.y = y2, v2.width = width, v2.height = height, this.gl.viewport(x2, y2, width, height));
    }
    /**
     * Get the size of the current width and height. Returns object with `width` and `height` values.
     * @readonly
     */
    get size() {
      return this.current ? { x: 0, y: 0, width: this.current.width, height: this.current.height } : { x: 0, y: 0, width: this.renderer.width, height: this.renderer.height };
    }
    /**
     * Clear the color of the context
     * @param r - Red value from 0 to 1
     * @param g - Green value from 0 to 1
     * @param b - Blue value from 0 to 1
     * @param a - Alpha value from 0 to 1
     * @param {PIXI.BUFFER_BITS} [mask=BUFFER_BITS.COLOR | BUFFER_BITS.DEPTH] - Bitwise OR of masks
     *  that indicate the buffers to be cleared, by default COLOR and DEPTH buffers.
     */
    clear(r2, g2, b2, a2, mask = BUFFER_BITS.COLOR | BUFFER_BITS.DEPTH) {
      const { gl } = this;
      gl.clearColor(r2, g2, b2, a2), gl.clear(mask);
    }
    /**
     * Initialize framebuffer for this context
     * @protected
     * @param framebuffer
     * @returns - created GLFramebuffer
     */
    initFramebuffer(framebuffer) {
      const { gl } = this, fbo = new GLFramebuffer(gl.createFramebuffer());
      return fbo.multisample = this.detectSamples(framebuffer.multisample), framebuffer.glFramebuffers[this.CONTEXT_UID] = fbo, this.managedFramebuffers.push(framebuffer), framebuffer.disposeRunner.add(this), fbo;
    }
    /**
     * Resize the framebuffer
     * @param framebuffer
     * @protected
     */
    resizeFramebuffer(framebuffer) {
      const { gl } = this, fbo = framebuffer.glFramebuffers[this.CONTEXT_UID];
      if (fbo.stencil) {
        gl.bindRenderbuffer(gl.RENDERBUFFER, fbo.stencil);
        let stencilFormat;
        this.renderer.context.webGLVersion === 1 ? stencilFormat = gl.DEPTH_STENCIL : framebuffer.depth && framebuffer.stencil ? stencilFormat = gl.DEPTH24_STENCIL8 : framebuffer.depth ? stencilFormat = gl.DEPTH_COMPONENT24 : stencilFormat = gl.STENCIL_INDEX8, fbo.msaaBuffer ? gl.renderbufferStorageMultisample(
          gl.RENDERBUFFER,
          fbo.multisample,
          stencilFormat,
          framebuffer.width,
          framebuffer.height
        ) : gl.renderbufferStorage(gl.RENDERBUFFER, stencilFormat, framebuffer.width, framebuffer.height);
      }
      const colorTextures = framebuffer.colorTextures;
      let count = colorTextures.length;
      gl.drawBuffers || (count = Math.min(count, 1));
      for (let i2 = 0; i2 < count; i2++) {
        const texture = colorTextures[i2], parentTexture = texture.parentTextureArray || texture;
        this.renderer.texture.bind(parentTexture, 0), i2 === 0 && fbo.msaaBuffer && (gl.bindRenderbuffer(gl.RENDERBUFFER, fbo.msaaBuffer), gl.renderbufferStorageMultisample(
          gl.RENDERBUFFER,
          fbo.multisample,
          parentTexture._glTextures[this.CONTEXT_UID].internalFormat,
          framebuffer.width,
          framebuffer.height
        ));
      }
      framebuffer.depthTexture && this.writeDepthTexture && this.renderer.texture.bind(framebuffer.depthTexture, 0);
    }
    /**
     * Update the framebuffer
     * @param framebuffer
     * @param mipLevel
     * @protected
     */
    updateFramebuffer(framebuffer, mipLevel) {
      const { gl } = this, fbo = framebuffer.glFramebuffers[this.CONTEXT_UID], colorTextures = framebuffer.colorTextures;
      let count = colorTextures.length;
      gl.drawBuffers || (count = Math.min(count, 1)), fbo.multisample > 1 && this.canMultisampleFramebuffer(framebuffer) ? fbo.msaaBuffer = fbo.msaaBuffer || gl.createRenderbuffer() : fbo.msaaBuffer && (gl.deleteRenderbuffer(fbo.msaaBuffer), fbo.msaaBuffer = null, fbo.blitFramebuffer && (fbo.blitFramebuffer.dispose(), fbo.blitFramebuffer = null));
      const activeTextures = [];
      for (let i2 = 0; i2 < count; i2++) {
        const texture = colorTextures[i2], parentTexture = texture.parentTextureArray || texture;
        this.renderer.texture.bind(parentTexture, 0), i2 === 0 && fbo.msaaBuffer ? (gl.bindRenderbuffer(gl.RENDERBUFFER, fbo.msaaBuffer), gl.renderbufferStorageMultisample(
          gl.RENDERBUFFER,
          fbo.multisample,
          parentTexture._glTextures[this.CONTEXT_UID].internalFormat,
          framebuffer.width,
          framebuffer.height
        ), gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.RENDERBUFFER, fbo.msaaBuffer)) : (gl.framebufferTexture2D(
          gl.FRAMEBUFFER,
          gl.COLOR_ATTACHMENT0 + i2,
          texture.target,
          parentTexture._glTextures[this.CONTEXT_UID].texture,
          mipLevel
        ), activeTextures.push(gl.COLOR_ATTACHMENT0 + i2));
      }
      if (activeTextures.length > 1 && gl.drawBuffers(activeTextures), framebuffer.depthTexture && this.writeDepthTexture) {
        const depthTexture = framebuffer.depthTexture;
        this.renderer.texture.bind(depthTexture, 0), gl.framebufferTexture2D(
          gl.FRAMEBUFFER,
          gl.DEPTH_ATTACHMENT,
          gl.TEXTURE_2D,
          depthTexture._glTextures[this.CONTEXT_UID].texture,
          mipLevel
        );
      }
      if ((framebuffer.stencil || framebuffer.depth) && !(framebuffer.depthTexture && this.writeDepthTexture)) {
        fbo.stencil = fbo.stencil || gl.createRenderbuffer();
        let stencilAttachment, stencilFormat;
        this.renderer.context.webGLVersion === 1 ? (stencilAttachment = gl.DEPTH_STENCIL_ATTACHMENT, stencilFormat = gl.DEPTH_STENCIL) : framebuffer.depth && framebuffer.stencil ? (stencilAttachment = gl.DEPTH_STENCIL_ATTACHMENT, stencilFormat = gl.DEPTH24_STENCIL8) : framebuffer.depth ? (stencilAttachment = gl.DEPTH_ATTACHMENT, stencilFormat = gl.DEPTH_COMPONENT24) : (stencilAttachment = gl.STENCIL_ATTACHMENT, stencilFormat = gl.STENCIL_INDEX8), gl.bindRenderbuffer(gl.RENDERBUFFER, fbo.stencil), fbo.msaaBuffer ? gl.renderbufferStorageMultisample(
          gl.RENDERBUFFER,
          fbo.multisample,
          stencilFormat,
          framebuffer.width,
          framebuffer.height
        ) : gl.renderbufferStorage(gl.RENDERBUFFER, stencilFormat, framebuffer.width, framebuffer.height), gl.framebufferRenderbuffer(gl.FRAMEBUFFER, stencilAttachment, gl.RENDERBUFFER, fbo.stencil);
      } else
        fbo.stencil && (gl.deleteRenderbuffer(fbo.stencil), fbo.stencil = null);
    }
    /**
     * Returns true if the frame buffer can be multisampled.
     * @param framebuffer
     */
    canMultisampleFramebuffer(framebuffer) {
      return this.renderer.context.webGLVersion !== 1 && framebuffer.colorTextures.length <= 1 && !framebuffer.depthTexture;
    }
    /**
     * Detects number of samples that is not more than a param but as close to it as possible
     * @param samples - number of samples
     * @returns - recommended number of samples
     */
    detectSamples(samples) {
      const { msaaSamples } = this;
      let res = MSAA_QUALITY.NONE;
      if (samples <= 1 || msaaSamples === null)
        return res;
      for (let i2 = 0; i2 < msaaSamples.length; i2++)
        if (msaaSamples[i2] <= samples) {
          res = msaaSamples[i2];
          break;
        }
      return res === 1 && (res = MSAA_QUALITY.NONE), res;
    }
    /**
     * Only works with WebGL2
     *
     * blits framebuffer to another of the same or bigger size
     * after that target framebuffer is bound
     *
     * Fails with WebGL warning if blits multisample framebuffer to different size
     * @param framebuffer - by default it blits "into itself", from renderBuffer to texture.
     * @param sourcePixels - source rectangle in pixels
     * @param destPixels - dest rectangle in pixels, assumed to be the same as sourcePixels
     */
    blit(framebuffer, sourcePixels, destPixels) {
      const { current, renderer, gl, CONTEXT_UID } = this;
      if (renderer.context.webGLVersion !== 2 || !current)
        return;
      const fbo = current.glFramebuffers[CONTEXT_UID];
      if (!fbo)
        return;
      if (!framebuffer) {
        if (!fbo.msaaBuffer)
          return;
        const colorTexture = current.colorTextures[0];
        if (!colorTexture)
          return;
        fbo.blitFramebuffer || (fbo.blitFramebuffer = new Framebuffer(current.width, current.height), fbo.blitFramebuffer.addColorTexture(0, colorTexture)), framebuffer = fbo.blitFramebuffer, framebuffer.colorTextures[0] !== colorTexture && (framebuffer.colorTextures[0] = colorTexture, framebuffer.dirtyId++, framebuffer.dirtyFormat++), (framebuffer.width !== current.width || framebuffer.height !== current.height) && (framebuffer.width = current.width, framebuffer.height = current.height, framebuffer.dirtyId++, framebuffer.dirtySize++);
      }
      sourcePixels || (sourcePixels = tempRectangle, sourcePixels.width = current.width, sourcePixels.height = current.height), destPixels || (destPixels = sourcePixels);
      const sameSize = sourcePixels.width === destPixels.width && sourcePixels.height === destPixels.height;
      this.bind(framebuffer), gl.bindFramebuffer(gl.READ_FRAMEBUFFER, fbo.framebuffer), gl.blitFramebuffer(
        sourcePixels.left,
        sourcePixels.top,
        sourcePixels.right,
        sourcePixels.bottom,
        destPixels.left,
        destPixels.top,
        destPixels.right,
        destPixels.bottom,
        gl.COLOR_BUFFER_BIT,
        sameSize ? gl.NEAREST : gl.LINEAR
      ), gl.bindFramebuffer(gl.READ_FRAMEBUFFER, framebuffer.glFramebuffers[this.CONTEXT_UID].framebuffer);
    }
    /**
     * Disposes framebuffer.
     * @param framebuffer - framebuffer that has to be disposed of
     * @param contextLost - If context was lost, we suppress all delete function calls
     */
    disposeFramebuffer(framebuffer, contextLost) {
      const fbo = framebuffer.glFramebuffers[this.CONTEXT_UID], gl = this.gl;
      if (!fbo)
        return;
      delete framebuffer.glFramebuffers[this.CONTEXT_UID];
      const index = this.managedFramebuffers.indexOf(framebuffer);
      index >= 0 && this.managedFramebuffers.splice(index, 1), framebuffer.disposeRunner.remove(this), contextLost || (gl.deleteFramebuffer(fbo.framebuffer), fbo.msaaBuffer && gl.deleteRenderbuffer(fbo.msaaBuffer), fbo.stencil && gl.deleteRenderbuffer(fbo.stencil)), fbo.blitFramebuffer && this.disposeFramebuffer(fbo.blitFramebuffer, contextLost);
    }
    /**
     * Disposes all framebuffers, but not textures bound to them.
     * @param [contextLost=false] - If context was lost, we suppress all delete function calls
     */
    disposeAll(contextLost) {
      const list = this.managedFramebuffers;
      this.managedFramebuffers = [];
      for (let i2 = 0; i2 < list.length; i2++)
        this.disposeFramebuffer(list[i2], contextLost);
    }
    /**
     * Forcing creation of stencil buffer for current framebuffer, if it wasn't done before.
     * Used by MaskSystem, when its time to use stencil mask for Graphics element.
     *
     * Its an alternative for public lazy `framebuffer.enableStencil`, in case we need stencil without rebind.
     * @private
     */
    forceStencil() {
      const framebuffer = this.current;
      if (!framebuffer)
        return;
      const fbo = framebuffer.glFramebuffers[this.CONTEXT_UID];
      if (!fbo || fbo.stencil && framebuffer.stencil)
        return;
      framebuffer.stencil = true;
      const w2 = framebuffer.width, h2 = framebuffer.height, gl = this.gl, stencil = fbo.stencil = gl.createRenderbuffer();
      gl.bindRenderbuffer(gl.RENDERBUFFER, stencil);
      let stencilAttachment, stencilFormat;
      this.renderer.context.webGLVersion === 1 ? (stencilAttachment = gl.DEPTH_STENCIL_ATTACHMENT, stencilFormat = gl.DEPTH_STENCIL) : framebuffer.depth ? (stencilAttachment = gl.DEPTH_STENCIL_ATTACHMENT, stencilFormat = gl.DEPTH24_STENCIL8) : (stencilAttachment = gl.STENCIL_ATTACHMENT, stencilFormat = gl.STENCIL_INDEX8), fbo.msaaBuffer ? gl.renderbufferStorageMultisample(gl.RENDERBUFFER, fbo.multisample, stencilFormat, w2, h2) : gl.renderbufferStorage(gl.RENDERBUFFER, stencilFormat, w2, h2), gl.framebufferRenderbuffer(gl.FRAMEBUFFER, stencilAttachment, gl.RENDERBUFFER, stencil);
    }
    /** Resets framebuffer stored state, binds screen framebuffer. Should be called before renderTexture reset(). */
    reset() {
      this.current = this.unknownFramebuffer, this.viewport = new Rectangle();
    }
    destroy() {
      this.renderer = null;
    }
  }
  FramebufferSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "framebuffer"
  };
  extensions.add(FramebufferSystem);
  const byteSizeMap = { 5126: 4, 5123: 2, 5121: 1 };
  class GeometrySystem {
    /** @param renderer - The renderer this System works for. */
    constructor(renderer) {
      this.renderer = renderer, this._activeGeometry = null, this._activeVao = null, this.hasVao = true, this.hasInstance = true, this.canUseUInt32ElementIndex = false, this.managedGeometries = {};
    }
    /** Sets up the renderer context and necessary buffers. */
    contextChange() {
      this.disposeAll(true);
      const gl = this.gl = this.renderer.gl, context2 = this.renderer.context;
      if (this.CONTEXT_UID = this.renderer.CONTEXT_UID, context2.webGLVersion !== 2) {
        let nativeVaoExtension = this.renderer.context.extensions.vertexArrayObject;
        settings.PREFER_ENV === ENV.WEBGL_LEGACY && (nativeVaoExtension = null), nativeVaoExtension ? (gl.createVertexArray = () => nativeVaoExtension.createVertexArrayOES(), gl.bindVertexArray = (vao) => nativeVaoExtension.bindVertexArrayOES(vao), gl.deleteVertexArray = (vao) => nativeVaoExtension.deleteVertexArrayOES(vao)) : (this.hasVao = false, gl.createVertexArray = () => null, gl.bindVertexArray = () => null, gl.deleteVertexArray = () => null);
      }
      if (context2.webGLVersion !== 2) {
        const instanceExt = gl.getExtension("ANGLE_instanced_arrays");
        instanceExt ? (gl.vertexAttribDivisor = (a2, b2) => instanceExt.vertexAttribDivisorANGLE(a2, b2), gl.drawElementsInstanced = (a2, b2, c2, d2, e2) => instanceExt.drawElementsInstancedANGLE(a2, b2, c2, d2, e2), gl.drawArraysInstanced = (a2, b2, c2, d2) => instanceExt.drawArraysInstancedANGLE(a2, b2, c2, d2)) : this.hasInstance = false;
      }
      this.canUseUInt32ElementIndex = context2.webGLVersion === 2 || !!context2.extensions.uint32ElementIndex;
    }
    /**
     * Binds geometry so that is can be drawn. Creating a Vao if required
     * @param geometry - Instance of geometry to bind.
     * @param shader - Instance of shader to use vao for.
     */
    bind(geometry, shader) {
      shader = shader || this.renderer.shader.shader;
      const { gl } = this;
      let vaos = geometry.glVertexArrayObjects[this.CONTEXT_UID], incRefCount = false;
      vaos || (this.managedGeometries[geometry.id] = geometry, geometry.disposeRunner.add(this), geometry.glVertexArrayObjects[this.CONTEXT_UID] = vaos = {}, incRefCount = true);
      const vao = vaos[shader.program.id] || this.initGeometryVao(geometry, shader, incRefCount);
      this._activeGeometry = geometry, this._activeVao !== vao && (this._activeVao = vao, this.hasVao ? gl.bindVertexArray(vao) : this.activateVao(geometry, shader.program)), this.updateBuffers();
    }
    /** Reset and unbind any active VAO and geometry. */
    reset() {
      this.unbind();
    }
    /** Update buffers of the currently bound geometry. */
    updateBuffers() {
      const geometry = this._activeGeometry, bufferSystem = this.renderer.buffer;
      for (let i2 = 0; i2 < geometry.buffers.length; i2++) {
        const buffer = geometry.buffers[i2];
        bufferSystem.update(buffer);
      }
    }
    /**
     * Check compatibility between a geometry and a program
     * @param geometry - Geometry instance.
     * @param program - Program instance.
     */
    checkCompatibility(geometry, program) {
      const geometryAttributes = geometry.attributes, shaderAttributes = program.attributeData;
      for (const j2 in shaderAttributes)
        if (!geometryAttributes[j2])
          throw new Error(`shader and geometry incompatible, geometry missing the "${j2}" attribute`);
    }
    /**
     * Takes a geometry and program and generates a unique signature for them.
     * @param geometry - To get signature from.
     * @param program - To test geometry against.
     * @returns - Unique signature of the geometry and program
     */
    getSignature(geometry, program) {
      const attribs = geometry.attributes, shaderAttributes = program.attributeData, strings = ["g", geometry.id];
      for (const i2 in attribs)
        shaderAttributes[i2] && strings.push(i2, shaderAttributes[i2].location);
      return strings.join("-");
    }
    /**
     * Creates or gets Vao with the same structure as the geometry and stores it on the geometry.
     * If vao is created, it is bound automatically. We use a shader to infer what and how to set up the
     * attribute locations.
     * @param geometry - Instance of geometry to to generate Vao for.
     * @param shader - Instance of the shader.
     * @param incRefCount - Increment refCount of all geometry buffers.
     */
    initGeometryVao(geometry, shader, incRefCount = true) {
      const gl = this.gl, CONTEXT_UID = this.CONTEXT_UID, bufferSystem = this.renderer.buffer, program = shader.program;
      program.glPrograms[CONTEXT_UID] || this.renderer.shader.generateProgram(shader), this.checkCompatibility(geometry, program);
      const signature = this.getSignature(geometry, program), vaoObjectHash = geometry.glVertexArrayObjects[this.CONTEXT_UID];
      let vao = vaoObjectHash[signature];
      if (vao)
        return vaoObjectHash[program.id] = vao, vao;
      const buffers = geometry.buffers, attributes = geometry.attributes, tempStride = {}, tempStart = {};
      for (const j2 in buffers)
        tempStride[j2] = 0, tempStart[j2] = 0;
      for (const j2 in attributes)
        !attributes[j2].size && program.attributeData[j2] ? attributes[j2].size = program.attributeData[j2].size : attributes[j2].size || console.warn(`PIXI Geometry attribute '${j2}' size cannot be determined (likely the bound shader does not have the attribute)`), tempStride[attributes[j2].buffer] += attributes[j2].size * byteSizeMap[attributes[j2].type];
      for (const j2 in attributes) {
        const attribute = attributes[j2], attribSize = attribute.size;
        attribute.stride === void 0 && (tempStride[attribute.buffer] === attribSize * byteSizeMap[attribute.type] ? attribute.stride = 0 : attribute.stride = tempStride[attribute.buffer]), attribute.start === void 0 && (attribute.start = tempStart[attribute.buffer], tempStart[attribute.buffer] += attribSize * byteSizeMap[attribute.type]);
      }
      vao = gl.createVertexArray(), gl.bindVertexArray(vao);
      for (let i2 = 0; i2 < buffers.length; i2++) {
        const buffer = buffers[i2];
        bufferSystem.bind(buffer), incRefCount && buffer._glBuffers[CONTEXT_UID].refCount++;
      }
      return this.activateVao(geometry, program), vaoObjectHash[program.id] = vao, vaoObjectHash[signature] = vao, gl.bindVertexArray(null), bufferSystem.unbind(BUFFER_TYPE.ARRAY_BUFFER), vao;
    }
    /**
     * Disposes geometry.
     * @param geometry - Geometry with buffers. Only VAO will be disposed
     * @param [contextLost=false] - If context was lost, we suppress deleteVertexArray
     */
    disposeGeometry(geometry, contextLost) {
      if (!this.managedGeometries[geometry.id])
        return;
      delete this.managedGeometries[geometry.id];
      const vaos = geometry.glVertexArrayObjects[this.CONTEXT_UID], gl = this.gl, buffers = geometry.buffers, bufferSystem = this.renderer?.buffer;
      if (geometry.disposeRunner.remove(this), !!vaos) {
        if (bufferSystem)
          for (let i2 = 0; i2 < buffers.length; i2++) {
            const buf = buffers[i2]._glBuffers[this.CONTEXT_UID];
            buf && (buf.refCount--, buf.refCount === 0 && !contextLost && bufferSystem.dispose(buffers[i2], contextLost));
          }
        if (!contextLost) {
          for (const vaoId in vaos)
            if (vaoId[0] === "g") {
              const vao = vaos[vaoId];
              this._activeVao === vao && this.unbind(), gl.deleteVertexArray(vao);
            }
        }
        delete geometry.glVertexArrayObjects[this.CONTEXT_UID];
      }
    }
    /**
     * Dispose all WebGL resources of all managed geometries.
     * @param [contextLost=false] - If context was lost, we suppress `gl.delete` calls
     */
    disposeAll(contextLost) {
      const all = Object.keys(this.managedGeometries);
      for (let i2 = 0; i2 < all.length; i2++)
        this.disposeGeometry(this.managedGeometries[all[i2]], contextLost);
    }
    /**
     * Activate vertex array object.
     * @param geometry - Geometry instance.
     * @param program - Shader program instance.
     */
    activateVao(geometry, program) {
      const gl = this.gl, CONTEXT_UID = this.CONTEXT_UID, bufferSystem = this.renderer.buffer, buffers = geometry.buffers, attributes = geometry.attributes;
      geometry.indexBuffer && bufferSystem.bind(geometry.indexBuffer);
      let lastBuffer = null;
      for (const j2 in attributes) {
        const attribute = attributes[j2], buffer = buffers[attribute.buffer], glBuffer = buffer._glBuffers[CONTEXT_UID];
        if (program.attributeData[j2]) {
          lastBuffer !== glBuffer && (bufferSystem.bind(buffer), lastBuffer = glBuffer);
          const location = program.attributeData[j2].location;
          if (gl.enableVertexAttribArray(location), gl.vertexAttribPointer(
            location,
            attribute.size,
            attribute.type || gl.FLOAT,
            attribute.normalized,
            attribute.stride,
            attribute.start
          ), attribute.instance)
            if (this.hasInstance)
              gl.vertexAttribDivisor(location, attribute.divisor);
            else
              throw new Error("geometry error, GPU Instancing is not supported on this device");
        }
      }
    }
    /**
     * Draws the currently bound geometry.
     * @param type - The type primitive to render.
     * @param size - The number of elements to be rendered. If not specified, all vertices after the
     *  starting vertex will be drawn.
     * @param start - The starting vertex in the geometry to start drawing from. If not specified,
     *  drawing will start from the first vertex.
     * @param instanceCount - The number of instances of the set of elements to execute. If not specified,
     *  all instances will be drawn.
     */
    draw(type2, size, start, instanceCount) {
      const { gl } = this, geometry = this._activeGeometry;
      if (geometry.indexBuffer) {
        const byteSize = geometry.indexBuffer.data.BYTES_PER_ELEMENT, glType = byteSize === 2 ? gl.UNSIGNED_SHORT : gl.UNSIGNED_INT;
        byteSize === 2 || byteSize === 4 && this.canUseUInt32ElementIndex ? geometry.instanced ? gl.drawElementsInstanced(type2, size || geometry.indexBuffer.data.length, glType, (start || 0) * byteSize, instanceCount || 1) : gl.drawElements(type2, size || geometry.indexBuffer.data.length, glType, (start || 0) * byteSize) : console.warn("unsupported index buffer type: uint32");
      } else
        geometry.instanced ? gl.drawArraysInstanced(type2, start, size || geometry.getSize(), instanceCount || 1) : gl.drawArrays(type2, start, size || geometry.getSize());
      return this;
    }
    /** Unbind/reset everything. */
    unbind() {
      this.gl.bindVertexArray(null), this._activeVao = null, this._activeGeometry = null;
    }
    destroy() {
      this.renderer = null;
    }
  }
  GeometrySystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "geometry"
  };
  extensions.add(GeometrySystem);
  const tempMat = new Matrix();
  class TextureMatrix {
    /**
     * @param texture - observed texture
     * @param clampMargin - Changes frame clamping, 0.5 by default. Use -0.5 for extra border.
     */
    constructor(texture, clampMargin) {
      this._texture = texture, this.mapCoord = new Matrix(), this.uClampFrame = new Float32Array(4), this.uClampOffset = new Float32Array(2), this._textureID = -1, this._updateID = 0, this.clampOffset = 0, this.clampMargin = typeof clampMargin > "u" ? 0.5 : clampMargin, this.isSimple = false;
    }
    /** Texture property. */
    get texture() {
      return this._texture;
    }
    set texture(value) {
      this._texture = value, this._textureID = -1;
    }
    /**
     * Multiplies uvs array to transform
     * @param uvs - mesh uvs
     * @param [out=uvs] - output
     * @returns - output
     */
    multiplyUvs(uvs, out) {
      out === void 0 && (out = uvs);
      const mat = this.mapCoord;
      for (let i2 = 0; i2 < uvs.length; i2 += 2) {
        const x2 = uvs[i2], y2 = uvs[i2 + 1];
        out[i2] = x2 * mat.a + y2 * mat.c + mat.tx, out[i2 + 1] = x2 * mat.b + y2 * mat.d + mat.ty;
      }
      return out;
    }
    /**
     * Updates matrices if texture was changed.
     * @param [forceUpdate=false] - if true, matrices will be updated any case
     * @returns - Whether or not it was updated
     */
    update(forceUpdate) {
      const tex = this._texture;
      if (!tex || !tex.valid || !forceUpdate && this._textureID === tex._updateID)
        return false;
      this._textureID = tex._updateID, this._updateID++;
      const uvs = tex._uvs;
      this.mapCoord.set(uvs.x1 - uvs.x0, uvs.y1 - uvs.y0, uvs.x3 - uvs.x0, uvs.y3 - uvs.y0, uvs.x0, uvs.y0);
      const orig = tex.orig, trim = tex.trim;
      trim && (tempMat.set(
        orig.width / trim.width,
        0,
        0,
        orig.height / trim.height,
        -trim.x / trim.width,
        -trim.y / trim.height
      ), this.mapCoord.append(tempMat));
      const texBase = tex.baseTexture, frame = this.uClampFrame, margin = this.clampMargin / texBase.resolution, offset = this.clampOffset;
      return frame[0] = (tex._frame.x + margin + offset) / texBase.width, frame[1] = (tex._frame.y + margin + offset) / texBase.height, frame[2] = (tex._frame.x + tex._frame.width - margin + offset) / texBase.width, frame[3] = (tex._frame.y + tex._frame.height - margin + offset) / texBase.height, this.uClampOffset[0] = offset / texBase.realWidth, this.uClampOffset[1] = offset / texBase.realHeight, this.isSimple = tex._frame.width === texBase.width && tex._frame.height === texBase.height && tex.rotate === 0, true;
    }
  }
  var fragment$1 = `varying vec2 vMaskCoord;
varying vec2 vTextureCoord;

uniform sampler2D uSampler;
uniform sampler2D mask;
uniform float alpha;
uniform float npmAlpha;
uniform vec4 maskClamp;

void main(void)
{
    float clip = step(3.5,
        step(maskClamp.x, vMaskCoord.x) +
        step(maskClamp.y, vMaskCoord.y) +
        step(vMaskCoord.x, maskClamp.z) +
        step(vMaskCoord.y, maskClamp.w));

    vec4 original = texture2D(uSampler, vTextureCoord);
    vec4 masky = texture2D(mask, vMaskCoord);
    float alphaMul = 1.0 - npmAlpha * (1.0 - masky.a);

    original *= (alphaMul * masky.r * alpha * clip);

    gl_FragColor = original;
}
`;
  var vertex = `attribute vec2 aVertexPosition;
attribute vec2 aTextureCoord;

uniform mat3 projectionMatrix;
uniform mat3 otherMatrix;

varying vec2 vMaskCoord;
varying vec2 vTextureCoord;

void main(void)
{
    gl_Position = vec4((projectionMatrix * vec3(aVertexPosition, 1.0)).xy, 0.0, 1.0);

    vTextureCoord = aTextureCoord;
    vMaskCoord = ( otherMatrix * vec3( aTextureCoord, 1.0)  ).xy;
}
`;
  class SpriteMaskFilter extends Filter {
    /** @ignore */
    constructor(vertexSrc, fragmentSrc, uniforms) {
      let sprite = null;
      typeof vertexSrc != "string" && fragmentSrc === void 0 && uniforms === void 0 && (sprite = vertexSrc, vertexSrc = void 0, fragmentSrc = void 0, uniforms = void 0), super(vertexSrc || vertex, fragmentSrc || fragment$1, uniforms), this.maskSprite = sprite, this.maskMatrix = new Matrix();
    }
    /**
     * Sprite mask
     * @type {PIXI.DisplayObject}
     */
    get maskSprite() {
      return this._maskSprite;
    }
    set maskSprite(value) {
      this._maskSprite = value, this._maskSprite && (this._maskSprite.renderable = false);
    }
    /**
     * Applies the filter
     * @param filterManager - The renderer to retrieve the filter from
     * @param input - The input render target.
     * @param output - The target to output to.
     * @param clearMode - Should the output be cleared before rendering to it.
     */
    apply(filterManager, input, output, clearMode) {
      const maskSprite = this._maskSprite, tex = maskSprite._texture;
      tex.valid && (tex.uvMatrix || (tex.uvMatrix = new TextureMatrix(tex, 0)), tex.uvMatrix.update(), this.uniforms.npmAlpha = tex.baseTexture.alphaMode ? 0 : 1, this.uniforms.mask = tex, this.uniforms.otherMatrix = filterManager.calculateSpriteMatrix(this.maskMatrix, maskSprite).prepend(tex.uvMatrix.mapCoord), this.uniforms.alpha = maskSprite.worldAlpha, this.uniforms.maskClamp = tex.uvMatrix.uClampFrame, filterManager.applyFilter(this, input, output, clearMode));
    }
  }
  class MaskData {
    /**
     * Create MaskData
     * @param {PIXI.DisplayObject} [maskObject=null] - object that describes the mask
     */
    constructor(maskObject = null) {
      this.type = MASK_TYPES.NONE, this.autoDetect = true, this.maskObject = maskObject || null, this.pooled = false, this.isMaskData = true, this.resolution = null, this.multisample = Filter.defaultMultisample, this.enabled = true, this.colorMask = 15, this._filters = null, this._stencilCounter = 0, this._scissorCounter = 0, this._scissorRect = null, this._scissorRectLocal = null, this._colorMask = 15, this._target = null;
    }
    /**
     * The sprite mask filter.
     * If set to `null`, the default sprite mask filter is used.
     * @default null
     */
    get filter() {
      return this._filters ? this._filters[0] : null;
    }
    set filter(value) {
      value ? this._filters ? this._filters[0] = value : this._filters = [value] : this._filters = null;
    }
    /** Resets the mask data after popMask(). */
    reset() {
      this.pooled && (this.maskObject = null, this.type = MASK_TYPES.NONE, this.autoDetect = true), this._target = null, this._scissorRectLocal = null;
    }
    /**
     * Copies counters from maskData above, called from pushMask().
     * @param maskAbove
     */
    copyCountersOrReset(maskAbove) {
      maskAbove ? (this._stencilCounter = maskAbove._stencilCounter, this._scissorCounter = maskAbove._scissorCounter, this._scissorRect = maskAbove._scissorRect) : (this._stencilCounter = 0, this._scissorCounter = 0, this._scissorRect = null);
    }
  }
  class MaskSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.enableScissor = true, this.alphaMaskPool = [], this.maskDataPool = [], this.maskStack = [], this.alphaMaskIndex = 0;
    }
    /**
     * Changes the mask stack that is used by this System.
     * @param maskStack - The mask stack
     */
    setMaskStack(maskStack) {
      this.maskStack = maskStack, this.renderer.scissor.setMaskStack(maskStack), this.renderer.stencil.setMaskStack(maskStack);
    }
    /**
     * Enables the mask and appends it to the current mask stack.
     *
     * NOTE: The batch renderer should be flushed beforehand to prevent pending renders from being masked.
     * @param {PIXI.DisplayObject} target - Display Object to push the mask to
     * @param {PIXI.MaskData|PIXI.Sprite|PIXI.Graphics|PIXI.DisplayObject} maskDataOrTarget - The masking data.
     */
    push(target, maskDataOrTarget) {
      let maskData = maskDataOrTarget;
      if (!maskData.isMaskData) {
        const d2 = this.maskDataPool.pop() || new MaskData();
        d2.pooled = true, d2.maskObject = maskDataOrTarget, maskData = d2;
      }
      const maskAbove = this.maskStack.length !== 0 ? this.maskStack[this.maskStack.length - 1] : null;
      if (maskData.copyCountersOrReset(maskAbove), maskData._colorMask = maskAbove ? maskAbove._colorMask : 15, maskData.autoDetect && this.detect(maskData), maskData._target = target, maskData.type !== MASK_TYPES.SPRITE && this.maskStack.push(maskData), maskData.enabled)
        switch (maskData.type) {
          case MASK_TYPES.SCISSOR:
            this.renderer.scissor.push(maskData);
            break;
          case MASK_TYPES.STENCIL:
            this.renderer.stencil.push(maskData);
            break;
          case MASK_TYPES.SPRITE:
            maskData.copyCountersOrReset(null), this.pushSpriteMask(maskData);
            break;
          case MASK_TYPES.COLOR:
            this.pushColorMask(maskData);
            break;
        }
      maskData.type === MASK_TYPES.SPRITE && this.maskStack.push(maskData);
    }
    /**
     * Removes the last mask from the mask stack and doesn't return it.
     *
     * NOTE: The batch renderer should be flushed beforehand to render the masked contents before the mask is removed.
     * @param {PIXI.IMaskTarget} target - Display Object to pop the mask from
     */
    pop(target) {
      const maskData = this.maskStack.pop();
      if (!(!maskData || maskData._target !== target)) {
        if (maskData.enabled)
          switch (maskData.type) {
            case MASK_TYPES.SCISSOR:
              this.renderer.scissor.pop(maskData);
              break;
            case MASK_TYPES.STENCIL:
              this.renderer.stencil.pop(maskData.maskObject);
              break;
            case MASK_TYPES.SPRITE:
              this.popSpriteMask(maskData);
              break;
            case MASK_TYPES.COLOR:
              this.popColorMask(maskData);
              break;
          }
        if (maskData.reset(), maskData.pooled && this.maskDataPool.push(maskData), this.maskStack.length !== 0) {
          const maskCurrent = this.maskStack[this.maskStack.length - 1];
          maskCurrent.type === MASK_TYPES.SPRITE && maskCurrent._filters && (maskCurrent._filters[0].maskSprite = maskCurrent.maskObject);
        }
      }
    }
    /**
     * Sets type of MaskData based on its maskObject.
     * @param maskData
     */
    detect(maskData) {
      const maskObject = maskData.maskObject;
      maskObject ? maskObject.isSprite ? maskData.type = MASK_TYPES.SPRITE : this.enableScissor && this.renderer.scissor.testScissor(maskData) ? maskData.type = MASK_TYPES.SCISSOR : maskData.type = MASK_TYPES.STENCIL : maskData.type = MASK_TYPES.COLOR;
    }
    /**
     * Applies the Mask and adds it to the current filter stack.
     * @param maskData - Sprite to be used as the mask.
     */
    pushSpriteMask(maskData) {
      const { maskObject } = maskData, target = maskData._target;
      let alphaMaskFilter = maskData._filters;
      alphaMaskFilter || (alphaMaskFilter = this.alphaMaskPool[this.alphaMaskIndex], alphaMaskFilter || (alphaMaskFilter = this.alphaMaskPool[this.alphaMaskIndex] = [new SpriteMaskFilter()])), alphaMaskFilter[0].resolution = maskData.resolution, alphaMaskFilter[0].multisample = maskData.multisample, alphaMaskFilter[0].maskSprite = maskObject;
      const stashFilterArea = target.filterArea;
      target.filterArea = maskObject.getBounds(true), this.renderer.filter.push(target, alphaMaskFilter), target.filterArea = stashFilterArea, maskData._filters || this.alphaMaskIndex++;
    }
    /**
     * Removes the last filter from the filter stack and doesn't return it.
     * @param maskData - Sprite to be used as the mask.
     */
    popSpriteMask(maskData) {
      this.renderer.filter.pop(), maskData._filters ? maskData._filters[0].maskSprite = null : (this.alphaMaskIndex--, this.alphaMaskPool[this.alphaMaskIndex][0].maskSprite = null);
    }
    /**
     * Pushes the color mask.
     * @param maskData - The mask data
     */
    pushColorMask(maskData) {
      const currColorMask = maskData._colorMask, nextColorMask = maskData._colorMask = currColorMask & maskData.colorMask;
      nextColorMask !== currColorMask && this.renderer.gl.colorMask(
        (nextColorMask & 1) !== 0,
        (nextColorMask & 2) !== 0,
        (nextColorMask & 4) !== 0,
        (nextColorMask & 8) !== 0
      );
    }
    /**
     * Pops the color mask.
     * @param maskData - The mask data
     */
    popColorMask(maskData) {
      const currColorMask = maskData._colorMask, nextColorMask = this.maskStack.length > 0 ? this.maskStack[this.maskStack.length - 1]._colorMask : 15;
      nextColorMask !== currColorMask && this.renderer.gl.colorMask(
        (nextColorMask & 1) !== 0,
        (nextColorMask & 2) !== 0,
        (nextColorMask & 4) !== 0,
        (nextColorMask & 8) !== 0
      );
    }
    destroy() {
      this.renderer = null;
    }
  }
  MaskSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "mask"
  };
  extensions.add(MaskSystem);
  class AbstractMaskSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.maskStack = [], this.glConst = 0;
    }
    /** Gets count of masks of certain type. */
    getStackLength() {
      return this.maskStack.length;
    }
    /**
     * Changes the mask stack that is used by this System.
     * @param {PIXI.MaskData[]} maskStack - The mask stack
     */
    setMaskStack(maskStack) {
      const { gl } = this.renderer, curStackLen = this.getStackLength();
      this.maskStack = maskStack;
      const newStackLen = this.getStackLength();
      newStackLen !== curStackLen && (newStackLen === 0 ? gl.disable(this.glConst) : (gl.enable(this.glConst), this._useCurrent()));
    }
    /**
     * Setup renderer to use the current mask data.
     * @private
     */
    _useCurrent() {
    }
    /** Destroys the mask stack. */
    destroy() {
      this.renderer = null, this.maskStack = null;
    }
  }
  const tempMatrix$1 = new Matrix(), rectPool = [], _ScissorSystem = class _ScissorSystem2 extends AbstractMaskSystem {
    /**
     * @param {PIXI.Renderer} renderer - The renderer this System works for.
     */
    constructor(renderer) {
      super(renderer), this.glConst = settings.ADAPTER.getWebGLRenderingContext().SCISSOR_TEST;
    }
    getStackLength() {
      const maskData = this.maskStack[this.maskStack.length - 1];
      return maskData ? maskData._scissorCounter : 0;
    }
    /**
     * evaluates _boundsTransformed, _scissorRect for MaskData
     * @param maskData
     */
    calcScissorRect(maskData) {
      if (maskData._scissorRectLocal)
        return;
      const prevData = maskData._scissorRect, { maskObject } = maskData, { renderer } = this, renderTextureSystem = renderer.renderTexture, rect = maskObject.getBounds(true, rectPool.pop() ?? new Rectangle());
      this.roundFrameToPixels(
        rect,
        renderTextureSystem.current ? renderTextureSystem.current.resolution : renderer.resolution,
        renderTextureSystem.sourceFrame,
        renderTextureSystem.destinationFrame,
        renderer.projection.transform
      ), prevData && rect.fit(prevData), maskData._scissorRectLocal = rect;
    }
    static isMatrixRotated(matrix) {
      if (!matrix)
        return false;
      const { a: a2, b: b2, c: c2, d: d2 } = matrix;
      return (Math.abs(b2) > 1e-4 || Math.abs(c2) > 1e-4) && (Math.abs(a2) > 1e-4 || Math.abs(d2) > 1e-4);
    }
    /**
     * Test, whether the object can be scissor mask with current renderer projection.
     * Calls "calcScissorRect()" if its true.
     * @param maskData - mask data
     * @returns whether Whether the object can be scissor mask
     */
    testScissor(maskData) {
      const { maskObject } = maskData;
      if (!maskObject.isFastRect || !maskObject.isFastRect() || _ScissorSystem2.isMatrixRotated(maskObject.worldTransform) || _ScissorSystem2.isMatrixRotated(this.renderer.projection.transform))
        return false;
      this.calcScissorRect(maskData);
      const rect = maskData._scissorRectLocal;
      return rect.width > 0 && rect.height > 0;
    }
    roundFrameToPixels(frame, resolution, bindingSourceFrame, bindingDestinationFrame, transform) {
      _ScissorSystem2.isMatrixRotated(transform) || (transform = transform ? tempMatrix$1.copyFrom(transform) : tempMatrix$1.identity(), transform.translate(-bindingSourceFrame.x, -bindingSourceFrame.y).scale(
        bindingDestinationFrame.width / bindingSourceFrame.width,
        bindingDestinationFrame.height / bindingSourceFrame.height
      ).translate(bindingDestinationFrame.x, bindingDestinationFrame.y), this.renderer.filter.transformAABB(transform, frame), frame.fit(bindingDestinationFrame), frame.x = Math.round(frame.x * resolution), frame.y = Math.round(frame.y * resolution), frame.width = Math.round(frame.width * resolution), frame.height = Math.round(frame.height * resolution));
    }
    /**
     * Applies the Mask and adds it to the current stencil stack.
     * @author alvin
     * @param maskData - The mask data.
     */
    push(maskData) {
      maskData._scissorRectLocal || this.calcScissorRect(maskData);
      const { gl } = this.renderer;
      maskData._scissorRect || gl.enable(gl.SCISSOR_TEST), maskData._scissorCounter++, maskData._scissorRect = maskData._scissorRectLocal, this._useCurrent();
    }
    /**
     * This should be called after a mask is popped off the mask stack. It will rebind the scissor box to be latest with the
     * last mask in the stack.
     *
     * This can also be called when you directly modify the scissor box and want to restore PixiJS state.
     * @param maskData - The mask data.
     */
    pop(maskData) {
      const { gl } = this.renderer;
      maskData && rectPool.push(maskData._scissorRectLocal), this.getStackLength() > 0 ? this._useCurrent() : gl.disable(gl.SCISSOR_TEST);
    }
    /**
     * Setup renderer to use the current scissor data.
     * @private
     */
    _useCurrent() {
      const rect = this.maskStack[this.maskStack.length - 1]._scissorRect;
      let y2;
      this.renderer.renderTexture.current ? y2 = rect.y : y2 = this.renderer.height - rect.height - rect.y, this.renderer.gl.scissor(rect.x, y2, rect.width, rect.height);
    }
  };
  _ScissorSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "scissor"
  };
  let ScissorSystem = _ScissorSystem;
  extensions.add(ScissorSystem);
  class StencilSystem extends AbstractMaskSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      super(renderer), this.glConst = settings.ADAPTER.getWebGLRenderingContext().STENCIL_TEST;
    }
    getStackLength() {
      const maskData = this.maskStack[this.maskStack.length - 1];
      return maskData ? maskData._stencilCounter : 0;
    }
    /**
     * Applies the Mask and adds it to the current stencil stack.
     * @param maskData - The mask data
     */
    push(maskData) {
      const maskObject = maskData.maskObject, { gl } = this.renderer, prevMaskCount = maskData._stencilCounter;
      prevMaskCount === 0 && (this.renderer.framebuffer.forceStencil(), gl.clearStencil(0), gl.clear(gl.STENCIL_BUFFER_BIT), gl.enable(gl.STENCIL_TEST)), maskData._stencilCounter++;
      const colorMask = maskData._colorMask;
      colorMask !== 0 && (maskData._colorMask = 0, gl.colorMask(false, false, false, false)), gl.stencilFunc(gl.EQUAL, prevMaskCount, 4294967295), gl.stencilOp(gl.KEEP, gl.KEEP, gl.INCR), maskObject.renderable = true, maskObject.render(this.renderer), this.renderer.batch.flush(), maskObject.renderable = false, colorMask !== 0 && (maskData._colorMask = colorMask, gl.colorMask(
        (colorMask & 1) !== 0,
        (colorMask & 2) !== 0,
        (colorMask & 4) !== 0,
        (colorMask & 8) !== 0
      )), this._useCurrent();
    }
    /**
     * Pops stencil mask. MaskData is already removed from stack
     * @param {PIXI.DisplayObject} maskObject - object of popped mask data
     */
    pop(maskObject) {
      const gl = this.renderer.gl;
      if (this.getStackLength() === 0)
        gl.disable(gl.STENCIL_TEST);
      else {
        const maskData = this.maskStack.length !== 0 ? this.maskStack[this.maskStack.length - 1] : null, colorMask = maskData ? maskData._colorMask : 15;
        colorMask !== 0 && (maskData._colorMask = 0, gl.colorMask(false, false, false, false)), gl.stencilOp(gl.KEEP, gl.KEEP, gl.DECR), maskObject.renderable = true, maskObject.render(this.renderer), this.renderer.batch.flush(), maskObject.renderable = false, colorMask !== 0 && (maskData._colorMask = colorMask, gl.colorMask(
          (colorMask & 1) !== 0,
          (colorMask & 2) !== 0,
          (colorMask & 4) !== 0,
          (colorMask & 8) !== 0
        )), this._useCurrent();
      }
    }
    /**
     * Setup renderer to use the current stencil data.
     * @private
     */
    _useCurrent() {
      const gl = this.renderer.gl;
      gl.stencilFunc(gl.EQUAL, this.getStackLength(), 4294967295), gl.stencilOp(gl.KEEP, gl.KEEP, gl.KEEP);
    }
  }
  StencilSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "stencil"
  };
  extensions.add(StencilSystem);
  class PluginSystem {
    constructor(renderer) {
      this.renderer = renderer, this.plugins = {}, Object.defineProperties(this.plugins, {
        extract: {
          enumerable: false,
          get() {
            return deprecation("7.0.0", "renderer.plugins.extract has moved to renderer.extract"), renderer.extract;
          }
        },
        prepare: {
          enumerable: false,
          get() {
            return deprecation("7.0.0", "renderer.plugins.prepare has moved to renderer.prepare"), renderer.prepare;
          }
        },
        interaction: {
          enumerable: false,
          get() {
            return deprecation("7.0.0", "renderer.plugins.interaction has been deprecated, use renderer.events"), renderer.events;
          }
        }
      });
    }
    /**
     * Initialize the plugins.
     * @protected
     */
    init() {
      const staticMap = this.rendererPlugins;
      for (const o2 in staticMap)
        this.plugins[o2] = new staticMap[o2](this.renderer);
    }
    destroy() {
      for (const o2 in this.plugins)
        this.plugins[o2].destroy(), this.plugins[o2] = null;
    }
  }
  PluginSystem.extension = {
    type: [
      ExtensionType.RendererSystem,
      ExtensionType.CanvasRendererSystem
    ],
    name: "_plugin"
  };
  extensions.add(PluginSystem);
  class ProjectionSystem {
    /** @param renderer - The renderer this System works for. */
    constructor(renderer) {
      this.renderer = renderer, this.destinationFrame = null, this.sourceFrame = null, this.defaultFrame = null, this.projectionMatrix = new Matrix(), this.transform = null;
    }
    /**
     * Updates the projection-matrix based on the sourceFrame → destinationFrame mapping provided.
     *
     * NOTE: It is expected you call `renderer.framebuffer.setViewport(destinationFrame)` after this. This is because
     * the framebuffer viewport converts shader vertex output in normalized device coordinates to window coordinates.
     *
     * NOTE-2: {@link PIXI.RenderTextureSystem#bind} updates the projection-matrix when you bind a render-texture.
     * It is expected
     * that you dirty the current bindings when calling this manually.
     * @param destinationFrame - The rectangle in the render-target to render the contents into. If rendering to the canvas,
     *  the origin is on the top-left; if rendering to a render-texture, the origin is on the bottom-left.
     * @param sourceFrame - The rectangle in world space that contains the contents being rendered.
     * @param resolution - The resolution of the render-target, which is the ratio of
     *  world-space (or CSS) pixels to physical pixels.
     * @param root - Whether the render-target is the screen. This is required because rendering to textures
     *  is y-flipped (i.e. upside down relative to the screen).
     */
    update(destinationFrame, sourceFrame, resolution, root) {
      this.destinationFrame = destinationFrame || this.destinationFrame || this.defaultFrame, this.sourceFrame = sourceFrame || this.sourceFrame || destinationFrame, this.calculateProjection(this.destinationFrame, this.sourceFrame, resolution, root), this.transform && this.projectionMatrix.append(this.transform);
      const renderer = this.renderer;
      renderer.globalUniforms.uniforms.projectionMatrix = this.projectionMatrix, renderer.globalUniforms.update(), renderer.shader.shader && renderer.shader.syncUniformGroup(renderer.shader.shader.uniforms.globals);
    }
    /**
     * Calculates the `projectionMatrix` to map points inside `sourceFrame` to inside `destinationFrame`.
     * @param _destinationFrame - The destination frame in the render-target.
     * @param sourceFrame - The source frame in world space.
     * @param _resolution - The render-target's resolution, i.e. ratio of CSS to physical pixels.
     * @param root - Whether rendering into the screen. Otherwise, if rendering to a framebuffer, the projection
     *  is y-flipped.
     */
    calculateProjection(_destinationFrame, sourceFrame, _resolution, root) {
      const pm = this.projectionMatrix, sign2 = root ? -1 : 1;
      pm.identity(), pm.a = 1 / sourceFrame.width * 2, pm.d = sign2 * (1 / sourceFrame.height * 2), pm.tx = -1 - sourceFrame.x * pm.a, pm.ty = -sign2 - sourceFrame.y * pm.d;
    }
    /**
     * Sets the transform of the active render target to the given matrix.
     * @param _matrix - The transformation matrix
     */
    setTransform(_matrix) {
    }
    destroy() {
      this.renderer = null;
    }
  }
  ProjectionSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "projection"
  };
  extensions.add(ProjectionSystem);
  const tempTransform = new Transform(), tempRect$1 = new Rectangle();
  class GenerateTextureSystem {
    constructor(renderer) {
      this.renderer = renderer, this._tempMatrix = new Matrix();
    }
    /**
     * A Useful function that returns a texture of the display object that can then be used to create sprites
     * This can be quite useful if your displayObject is complicated and needs to be reused multiple times.
     * @param displayObject - The displayObject the object will be generated from.
     * @param {IGenerateTextureOptions} options - Generate texture options.
     * @param {PIXI.Rectangle} options.region - The region of the displayObject, that shall be rendered,
     *        if no region is specified, defaults to the local bounds of the displayObject.
     * @param {number} [options.resolution] - If not given, the renderer's resolution is used.
     * @param {PIXI.MSAA_QUALITY} [options.multisample] - If not given, the renderer's multisample is used.
     * @returns a shiny new texture of the display object passed in
     */
    generateTexture(displayObject, options) {
      const { region: manualRegion, ...textureOptions } = options || {}, region = manualRegion?.copyTo(tempRect$1) || displayObject.getLocalBounds(tempRect$1, true), resolution = textureOptions.resolution || this.renderer.resolution;
      region.width = Math.max(region.width, 1 / resolution), region.height = Math.max(region.height, 1 / resolution), textureOptions.width = region.width, textureOptions.height = region.height, textureOptions.resolution = resolution, textureOptions.multisample ?? (textureOptions.multisample = this.renderer.multisample);
      const renderTexture = RenderTexture.create(textureOptions);
      this._tempMatrix.tx = -region.x, this._tempMatrix.ty = -region.y;
      const transform = displayObject.transform;
      return displayObject.transform = tempTransform, this.renderer.render(displayObject, {
        renderTexture,
        transform: this._tempMatrix,
        skipUpdateTransform: !!displayObject.parent,
        blit: true
      }), displayObject.transform = transform, renderTexture;
    }
    destroy() {
    }
  }
  GenerateTextureSystem.extension = {
    type: [
      ExtensionType.RendererSystem,
      ExtensionType.CanvasRendererSystem
    ],
    name: "textureGenerator"
  };
  extensions.add(GenerateTextureSystem);
  const tempRect = new Rectangle(), tempRect2 = new Rectangle();
  class RenderTextureSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.defaultMaskStack = [], this.current = null, this.sourceFrame = new Rectangle(), this.destinationFrame = new Rectangle(), this.viewportFrame = new Rectangle();
    }
    contextChange() {
      const attributes = this.renderer?.gl.getContextAttributes();
      this._rendererPremultipliedAlpha = !!(attributes && attributes.alpha && attributes.premultipliedAlpha);
    }
    /**
     * Bind the current render texture.
     * @param renderTexture - RenderTexture to bind, by default its `null` - the screen.
     * @param sourceFrame - Part of world that is mapped to the renderTexture.
     * @param destinationFrame - Part of renderTexture, by default it has the same size as sourceFrame.
     */
    bind(renderTexture = null, sourceFrame, destinationFrame) {
      const renderer = this.renderer;
      this.current = renderTexture;
      let baseTexture, framebuffer, resolution;
      renderTexture ? (baseTexture = renderTexture.baseTexture, resolution = baseTexture.resolution, sourceFrame || (tempRect.width = renderTexture.frame.width, tempRect.height = renderTexture.frame.height, sourceFrame = tempRect), destinationFrame || (tempRect2.x = renderTexture.frame.x, tempRect2.y = renderTexture.frame.y, tempRect2.width = sourceFrame.width, tempRect2.height = sourceFrame.height, destinationFrame = tempRect2), framebuffer = baseTexture.framebuffer) : (resolution = renderer.resolution, sourceFrame || (tempRect.width = renderer._view.screen.width, tempRect.height = renderer._view.screen.height, sourceFrame = tempRect), destinationFrame || (destinationFrame = tempRect, destinationFrame.width = sourceFrame.width, destinationFrame.height = sourceFrame.height));
      const viewportFrame = this.viewportFrame;
      viewportFrame.x = destinationFrame.x * resolution, viewportFrame.y = destinationFrame.y * resolution, viewportFrame.width = destinationFrame.width * resolution, viewportFrame.height = destinationFrame.height * resolution, renderTexture || (viewportFrame.y = renderer.view.height - (viewportFrame.y + viewportFrame.height)), viewportFrame.ceil(), this.renderer.framebuffer.bind(framebuffer, viewportFrame), this.renderer.projection.update(destinationFrame, sourceFrame, resolution, !framebuffer), renderTexture ? this.renderer.mask.setMaskStack(baseTexture.maskStack) : this.renderer.mask.setMaskStack(this.defaultMaskStack), this.sourceFrame.copyFrom(sourceFrame), this.destinationFrame.copyFrom(destinationFrame);
    }
    /**
     * Erases the render texture and fills the drawing area with a colour.
     * @param clearColor - The color as rgba, default to use the renderer backgroundColor
     * @param [mask=BUFFER_BITS.COLOR | BUFFER_BITS.DEPTH] - Bitwise OR of masks
     *  that indicate the buffers to be cleared, by default COLOR and DEPTH buffers.
     */
    clear(clearColor, mask) {
      const fallbackColor = this.current ? this.current.baseTexture.clear : this.renderer.background.backgroundColor, color = Color.shared.setValue(clearColor || fallbackColor);
      (this.current && this.current.baseTexture.alphaMode > 0 || !this.current && this._rendererPremultipliedAlpha) && color.premultiply(color.alpha);
      const destinationFrame = this.destinationFrame, baseFrame = this.current ? this.current.baseTexture : this.renderer._view.screen, clearMask = destinationFrame.width !== baseFrame.width || destinationFrame.height !== baseFrame.height;
      if (clearMask) {
        let { x: x2, y: y2, width, height } = this.viewportFrame;
        x2 = Math.round(x2), y2 = Math.round(y2), width = Math.round(width), height = Math.round(height), this.renderer.gl.enable(this.renderer.gl.SCISSOR_TEST), this.renderer.gl.scissor(x2, y2, width, height);
      }
      this.renderer.framebuffer.clear(color.red, color.green, color.blue, color.alpha, mask), clearMask && this.renderer.scissor.pop();
    }
    resize() {
      this.bind(null);
    }
    /** Resets render-texture state. */
    reset() {
      this.bind(null);
    }
    destroy() {
      this.renderer = null;
    }
  }
  RenderTextureSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "renderTexture"
  };
  extensions.add(RenderTextureSystem);
  class GLProgram {
    /**
     * Makes a new Pixi program.
     * @param program - webgl program
     * @param uniformData - uniforms
     */
    constructor(program, uniformData) {
      this.program = program, this.uniformData = uniformData, this.uniformGroups = {}, this.uniformDirtyGroups = {}, this.uniformBufferBindings = {};
    }
    /** Destroys this program. */
    destroy() {
      this.uniformData = null, this.uniformGroups = null, this.uniformDirtyGroups = null, this.uniformBufferBindings = null, this.program = null;
    }
  }
  function getAttributeData(program, gl) {
    const attributes = {}, totalAttributes = gl.getProgramParameter(program, gl.ACTIVE_ATTRIBUTES);
    for (let i2 = 0; i2 < totalAttributes; i2++) {
      const attribData = gl.getActiveAttrib(program, i2);
      if (attribData.name.startsWith("gl_"))
        continue;
      const type2 = mapType(gl, attribData.type), data = {
        type: type2,
        name: attribData.name,
        size: mapSize(type2),
        location: gl.getAttribLocation(program, attribData.name)
      };
      attributes[attribData.name] = data;
    }
    return attributes;
  }
  function getUniformData(program, gl) {
    const uniforms = {}, totalUniforms = gl.getProgramParameter(program, gl.ACTIVE_UNIFORMS);
    for (let i2 = 0; i2 < totalUniforms; i2++) {
      const uniformData = gl.getActiveUniform(program, i2), name = uniformData.name.replace(/\[.*?\]$/, ""), isArray = !!uniformData.name.match(/\[.*?\]$/), type2 = mapType(gl, uniformData.type);
      uniforms[name] = {
        name,
        index: i2,
        type: type2,
        size: uniformData.size,
        isArray,
        value: defaultValue(type2, uniformData.size)
      };
    }
    return uniforms;
  }
  function generateProgram(gl, program) {
    const glVertShader = compileShader(gl, gl.VERTEX_SHADER, program.vertexSrc), glFragShader = compileShader(gl, gl.FRAGMENT_SHADER, program.fragmentSrc), webGLProgram = gl.createProgram();
    gl.attachShader(webGLProgram, glVertShader), gl.attachShader(webGLProgram, glFragShader);
    const transformFeedbackVaryings = program.extra?.transformFeedbackVaryings;
    if (transformFeedbackVaryings && (typeof gl.transformFeedbackVaryings != "function" ? console.warn("TransformFeedback is not supported but TransformFeedbackVaryings are given.") : gl.transformFeedbackVaryings(
      webGLProgram,
      transformFeedbackVaryings.names,
      transformFeedbackVaryings.bufferMode === "separate" ? gl.SEPARATE_ATTRIBS : gl.INTERLEAVED_ATTRIBS
    )), gl.linkProgram(webGLProgram), gl.getProgramParameter(webGLProgram, gl.LINK_STATUS) || logProgramError(gl, webGLProgram, glVertShader, glFragShader), program.attributeData = getAttributeData(webGLProgram, gl), program.uniformData = getUniformData(webGLProgram, gl), !/^[ \t]*#[ \t]*version[ \t]+300[ \t]+es[ \t]*$/m.test(program.vertexSrc)) {
      const keys = Object.keys(program.attributeData);
      keys.sort((a2, b2) => a2 > b2 ? 1 : -1);
      for (let i2 = 0; i2 < keys.length; i2++)
        program.attributeData[keys[i2]].location = i2, gl.bindAttribLocation(webGLProgram, i2, keys[i2]);
      gl.linkProgram(webGLProgram);
    }
    gl.deleteShader(glVertShader), gl.deleteShader(glFragShader);
    const uniformData = {};
    for (const i2 in program.uniformData) {
      const data = program.uniformData[i2];
      uniformData[i2] = {
        location: gl.getUniformLocation(webGLProgram, i2),
        value: defaultValue(data.type, data.size)
      };
    }
    return new GLProgram(webGLProgram, uniformData);
  }
  function uboUpdate(_ud, _uv, _renderer, _syncData, buffer) {
    _renderer.buffer.update(buffer);
  }
  const UBO_TO_SINGLE_SETTERS = {
    float: `
        data[offset] = v;
    `,
    vec2: `
        data[offset] = v[0];
        data[offset+1] = v[1];
    `,
    vec3: `
        data[offset] = v[0];
        data[offset+1] = v[1];
        data[offset+2] = v[2];

    `,
    vec4: `
        data[offset] = v[0];
        data[offset+1] = v[1];
        data[offset+2] = v[2];
        data[offset+3] = v[3];
    `,
    mat2: `
        data[offset] = v[0];
        data[offset+1] = v[1];

        data[offset+4] = v[2];
        data[offset+5] = v[3];
    `,
    mat3: `
        data[offset] = v[0];
        data[offset+1] = v[1];
        data[offset+2] = v[2];

        data[offset + 4] = v[3];
        data[offset + 5] = v[4];
        data[offset + 6] = v[5];

        data[offset + 8] = v[6];
        data[offset + 9] = v[7];
        data[offset + 10] = v[8];
    `,
    mat4: `
        for(var i = 0; i < 16; i++)
        {
            data[offset + i] = v[i];
        }
    `
  }, GLSL_TO_STD40_SIZE = {
    float: 4,
    vec2: 8,
    vec3: 12,
    vec4: 16,
    int: 4,
    ivec2: 8,
    ivec3: 12,
    ivec4: 16,
    uint: 4,
    uvec2: 8,
    uvec3: 12,
    uvec4: 16,
    bool: 4,
    bvec2: 8,
    bvec3: 12,
    bvec4: 16,
    mat2: 16 * 2,
    mat3: 16 * 3,
    mat4: 16 * 4
  };
  function createUBOElements(uniformData) {
    const uboElements = uniformData.map((data) => ({
      data,
      offset: 0,
      dataLen: 0,
      dirty: 0
    }));
    let size = 0, chunkSize = 0, offset = 0;
    for (let i2 = 0; i2 < uboElements.length; i2++) {
      const uboElement = uboElements[i2];
      if (size = GLSL_TO_STD40_SIZE[uboElement.data.type], uboElement.data.size > 1 && (size = Math.max(size, 16) * uboElement.data.size), uboElement.dataLen = size, chunkSize % size !== 0 && chunkSize < 16) {
        const lineUpValue = chunkSize % size % 16;
        chunkSize += lineUpValue, offset += lineUpValue;
      }
      chunkSize + size > 16 ? (offset = Math.ceil(offset / 16) * 16, uboElement.offset = offset, offset += size, chunkSize = size) : (uboElement.offset = offset, chunkSize += size, offset += size);
    }
    return offset = Math.ceil(offset / 16) * 16, { uboElements, size: offset };
  }
  function getUBOData(uniforms, uniformData) {
    const usedUniformDatas = [];
    for (const i2 in uniforms)
      uniformData[i2] && usedUniformDatas.push(uniformData[i2]);
    return usedUniformDatas.sort((a2, b2) => a2.index - b2.index), usedUniformDatas;
  }
  function generateUniformBufferSync(group, uniformData) {
    if (!group.autoManage)
      return { size: 0, syncFunc: uboUpdate };
    const usedUniformDatas = getUBOData(group.uniforms, uniformData), { uboElements, size } = createUBOElements(usedUniformDatas), funcFragments = [`
    var v = null;
    var v2 = null;
    var cv = null;
    var t = 0;
    var gl = renderer.gl
    var index = 0;
    var data = buffer.data;
    `];
    for (let i2 = 0; i2 < uboElements.length; i2++) {
      const uboElement = uboElements[i2], uniform = group.uniforms[uboElement.data.name], name = uboElement.data.name;
      let parsed = false;
      for (let j2 = 0; j2 < uniformParsers.length; j2++) {
        const uniformParser = uniformParsers[j2];
        if (uniformParser.codeUbo && uniformParser.test(uboElement.data, uniform)) {
          funcFragments.push(
            `offset = ${uboElement.offset / 4};`,
            uniformParsers[j2].codeUbo(uboElement.data.name, uniform)
          ), parsed = true;
          break;
        }
      }
      if (!parsed)
        if (uboElement.data.size > 1) {
          const size2 = mapSize(uboElement.data.type), rowSize = Math.max(GLSL_TO_STD40_SIZE[uboElement.data.type] / 16, 1), elementSize = size2 / rowSize, remainder = (4 - elementSize % 4) % 4;
          funcFragments.push(`
                cv = ud.${name}.value;
                v = uv.${name};
                offset = ${uboElement.offset / 4};

                t = 0;

                for(var i=0; i < ${uboElement.data.size * rowSize}; i++)
                {
                    for(var j = 0; j < ${elementSize}; j++)
                    {
                        data[offset++] = v[t++];
                    }
                    offset += ${remainder};
                }

                `);
        } else {
          const template = UBO_TO_SINGLE_SETTERS[uboElement.data.type];
          funcFragments.push(`
                cv = ud.${name}.value;
                v = uv.${name};
                offset = ${uboElement.offset / 4};
                ${template};
                `);
        }
    }
    return funcFragments.push(`
       renderer.buffer.update(buffer);
    `), {
      size,
      // eslint-disable-next-line no-new-func
      syncFunc: new Function(
        "ud",
        "uv",
        "renderer",
        "syncData",
        "buffer",
        funcFragments.join(`
`)
      )
    };
  }
  let UID = 0;
  const defaultSyncData = { textureCount: 0, uboCount: 0 };
  class ShaderSystem {
    /** @param renderer - The renderer this System works for. */
    constructor(renderer) {
      this.destroyed = false, this.renderer = renderer, this.systemCheck(), this.gl = null, this.shader = null, this.program = null, this.cache = {}, this._uboCache = {}, this.id = UID++;
    }
    /**
     * Overrideable function by `@pixi/unsafe-eval` to silence
     * throwing an error if platform doesn't support unsafe-evals.
     * @private
     */
    systemCheck() {
      if (!unsafeEvalSupported())
        throw new Error("Current environment does not allow unsafe-eval, please use @pixi/unsafe-eval module to enable support.");
    }
    contextChange(gl) {
      this.gl = gl, this.reset();
    }
    /**
     * Changes the current shader to the one given in parameter.
     * @param shader - the new shader
     * @param dontSync - false if the shader should automatically sync its uniforms.
     * @returns the glProgram that belongs to the shader.
     */
    bind(shader, dontSync) {
      shader.disposeRunner.add(this), shader.uniforms.globals = this.renderer.globalUniforms;
      const program = shader.program, glProgram = program.glPrograms[this.renderer.CONTEXT_UID] || this.generateProgram(shader);
      return this.shader = shader, this.program !== program && (this.program = program, this.gl.useProgram(glProgram.program)), dontSync || (defaultSyncData.textureCount = 0, defaultSyncData.uboCount = 0, this.syncUniformGroup(shader.uniformGroup, defaultSyncData)), glProgram;
    }
    /**
     * Uploads the uniforms values to the currently bound shader.
     * @param uniforms - the uniforms values that be applied to the current shader
     */
    setUniforms(uniforms) {
      const shader = this.shader.program, glProgram = shader.glPrograms[this.renderer.CONTEXT_UID];
      shader.syncUniforms(glProgram.uniformData, uniforms, this.renderer);
    }
    /* eslint-disable @typescript-eslint/explicit-module-boundary-types */
    /**
     * Syncs uniforms on the group
     * @param group - the uniform group to sync
     * @param syncData - this is data that is passed to the sync function and any nested sync functions
     */
    syncUniformGroup(group, syncData) {
      const glProgram = this.getGlProgram();
      (!group.static || group.dirtyId !== glProgram.uniformDirtyGroups[group.id]) && (glProgram.uniformDirtyGroups[group.id] = group.dirtyId, this.syncUniforms(group, glProgram, syncData));
    }
    /**
     * Overrideable by the @pixi/unsafe-eval package to use static syncUniforms instead.
     * @param group
     * @param glProgram
     * @param syncData
     */
    syncUniforms(group, glProgram, syncData) {
      (group.syncUniforms[this.shader.program.id] || this.createSyncGroups(group))(glProgram.uniformData, group.uniforms, this.renderer, syncData);
    }
    createSyncGroups(group) {
      const id = this.getSignature(group, this.shader.program.uniformData, "u");
      return this.cache[id] || (this.cache[id] = generateUniformsSync(group, this.shader.program.uniformData)), group.syncUniforms[this.shader.program.id] = this.cache[id], group.syncUniforms[this.shader.program.id];
    }
    /**
     * Syncs uniform buffers
     * @param group - the uniform buffer group to sync
     * @param name - the name of the uniform buffer
     */
    syncUniformBufferGroup(group, name) {
      const glProgram = this.getGlProgram();
      if (!group.static || group.dirtyId !== 0 || !glProgram.uniformGroups[group.id]) {
        group.dirtyId = 0;
        const syncFunc = glProgram.uniformGroups[group.id] || this.createSyncBufferGroup(group, glProgram, name);
        group.buffer.update(), syncFunc(
          glProgram.uniformData,
          group.uniforms,
          this.renderer,
          defaultSyncData,
          group.buffer
        );
      }
      this.renderer.buffer.bindBufferBase(group.buffer, glProgram.uniformBufferBindings[name]);
    }
    /**
     * Will create a function that uploads a uniform buffer using the STD140 standard.
     * The upload function will then be cached for future calls
     * If a group is manually managed, then a simple upload function is generated
     * @param group - the uniform buffer group to sync
     * @param glProgram - the gl program to attach the uniform bindings to
     * @param name - the name of the uniform buffer (must exist on the shader)
     */
    createSyncBufferGroup(group, glProgram, name) {
      const { gl } = this.renderer;
      this.renderer.buffer.bind(group.buffer);
      const uniformBlockIndex = this.gl.getUniformBlockIndex(glProgram.program, name);
      glProgram.uniformBufferBindings[name] = this.shader.uniformBindCount, gl.uniformBlockBinding(glProgram.program, uniformBlockIndex, this.shader.uniformBindCount), this.shader.uniformBindCount++;
      const id = this.getSignature(group, this.shader.program.uniformData, "ubo");
      let uboData = this._uboCache[id];
      if (uboData || (uboData = this._uboCache[id] = generateUniformBufferSync(group, this.shader.program.uniformData)), group.autoManage) {
        const data = new Float32Array(uboData.size / 4);
        group.buffer.update(data);
      }
      return glProgram.uniformGroups[group.id] = uboData.syncFunc, glProgram.uniformGroups[group.id];
    }
    /**
     * Takes a uniform group and data and generates a unique signature for them.
     * @param group - The uniform group to get signature of
     * @param group.uniforms
     * @param uniformData - Uniform information generated by the shader
     * @param preFix
     * @returns Unique signature of the uniform group
     */
    getSignature(group, uniformData, preFix) {
      const uniforms = group.uniforms, strings = [`${preFix}-`];
      for (const i2 in uniforms)
        strings.push(i2), uniformData[i2] && strings.push(uniformData[i2].type);
      return strings.join("-");
    }
    /**
     * Returns the underlying GLShade rof the currently bound shader.
     *
     * This can be handy for when you to have a little more control over the setting of your uniforms.
     * @returns The glProgram for the currently bound Shader for this context
     */
    getGlProgram() {
      return this.shader ? this.shader.program.glPrograms[this.renderer.CONTEXT_UID] : null;
    }
    /**
     * Generates a glProgram version of the Shader provided.
     * @param shader - The shader that the glProgram will be based on.
     * @returns A shiny new glProgram!
     */
    generateProgram(shader) {
      const gl = this.gl, program = shader.program, glProgram = generateProgram(gl, program);
      return program.glPrograms[this.renderer.CONTEXT_UID] = glProgram, glProgram;
    }
    /** Resets ShaderSystem state, does not affect WebGL state. */
    reset() {
      this.program = null, this.shader = null;
    }
    /**
     * Disposes shader.
     * If disposing one equals with current shader, set current as null.
     * @param shader - Shader object
     */
    disposeShader(shader) {
      this.shader === shader && (this.shader = null);
    }
    /** Destroys this System and removes all its textures. */
    destroy() {
      this.renderer = null, this.destroyed = true;
    }
  }
  ShaderSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "shader"
  };
  extensions.add(ShaderSystem);
  class StartupSystem {
    constructor(renderer) {
      this.renderer = renderer;
    }
    /**
     * It all starts here! This initiates every system, passing in the options for any system by name.
     * @param options - the config for the renderer and all its systems
     */
    run(options) {
      const { renderer } = this;
      renderer.runners.init.emit(renderer.options), options.hello && console.log(`PixiJS 7.4.3 - ${renderer.rendererLogId} - https://pixijs.com`), renderer.resize(renderer.screen.width, renderer.screen.height);
    }
    destroy() {
    }
  }
  StartupSystem.defaultOptions = {
    /**
     * {@link PIXI.IRendererOptions.hello}
     * @default false
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    hello: false
  }, /** @ignore */
  StartupSystem.extension = {
    type: [
      ExtensionType.RendererSystem,
      ExtensionType.CanvasRendererSystem
    ],
    name: "startup"
  };
  extensions.add(StartupSystem);
  function mapWebGLBlendModesToPixi(gl, array = []) {
    return array[BLEND_MODES.NORMAL] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.ADD] = [gl.ONE, gl.ONE], array[BLEND_MODES.MULTIPLY] = [gl.DST_COLOR, gl.ONE_MINUS_SRC_ALPHA, gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.SCREEN] = [gl.ONE, gl.ONE_MINUS_SRC_COLOR, gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.OVERLAY] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.DARKEN] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.LIGHTEN] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.COLOR_DODGE] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.COLOR_BURN] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.HARD_LIGHT] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.SOFT_LIGHT] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.DIFFERENCE] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.EXCLUSION] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.HUE] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.SATURATION] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.COLOR] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.LUMINOSITY] = [gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.NONE] = [0, 0], array[BLEND_MODES.NORMAL_NPM] = [gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA, gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.ADD_NPM] = [gl.SRC_ALPHA, gl.ONE, gl.ONE, gl.ONE], array[BLEND_MODES.SCREEN_NPM] = [gl.SRC_ALPHA, gl.ONE_MINUS_SRC_COLOR, gl.ONE, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.SRC_IN] = [gl.DST_ALPHA, gl.ZERO], array[BLEND_MODES.SRC_OUT] = [gl.ONE_MINUS_DST_ALPHA, gl.ZERO], array[BLEND_MODES.SRC_ATOP] = [gl.DST_ALPHA, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.DST_OVER] = [gl.ONE_MINUS_DST_ALPHA, gl.ONE], array[BLEND_MODES.DST_IN] = [gl.ZERO, gl.SRC_ALPHA], array[BLEND_MODES.DST_OUT] = [gl.ZERO, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.DST_ATOP] = [gl.ONE_MINUS_DST_ALPHA, gl.SRC_ALPHA], array[BLEND_MODES.XOR] = [gl.ONE_MINUS_DST_ALPHA, gl.ONE_MINUS_SRC_ALPHA], array[BLEND_MODES.SUBTRACT] = [gl.ONE, gl.ONE, gl.ONE, gl.ONE, gl.FUNC_REVERSE_SUBTRACT, gl.FUNC_ADD], array;
  }
  const BLEND = 0, OFFSET = 1, CULLING = 2, DEPTH_TEST = 3, WINDING = 4, DEPTH_MASK = 5, _StateSystem = class _StateSystem2 {
    constructor() {
      this.gl = null, this.stateId = 0, this.polygonOffset = 0, this.blendMode = BLEND_MODES.NONE, this._blendEq = false, this.map = [], this.map[BLEND] = this.setBlend, this.map[OFFSET] = this.setOffset, this.map[CULLING] = this.setCullFace, this.map[DEPTH_TEST] = this.setDepthTest, this.map[WINDING] = this.setFrontFace, this.map[DEPTH_MASK] = this.setDepthMask, this.checks = [], this.defaultState = new State(), this.defaultState.blend = true;
    }
    contextChange(gl) {
      this.gl = gl, this.blendModes = mapWebGLBlendModesToPixi(gl), this.set(this.defaultState), this.reset();
    }
    /**
     * Sets the current state
     * @param {*} state - The state to set.
     */
    set(state) {
      if (state = state || this.defaultState, this.stateId !== state.data) {
        let diff = this.stateId ^ state.data, i2 = 0;
        for (; diff; )
          diff & 1 && this.map[i2].call(this, !!(state.data & 1 << i2)), diff = diff >> 1, i2++;
        this.stateId = state.data;
      }
      for (let i2 = 0; i2 < this.checks.length; i2++)
        this.checks[i2](this, state);
    }
    /**
     * Sets the state, when previous state is unknown.
     * @param {*} state - The state to set
     */
    forceState(state) {
      state = state || this.defaultState;
      for (let i2 = 0; i2 < this.map.length; i2++)
        this.map[i2].call(this, !!(state.data & 1 << i2));
      for (let i2 = 0; i2 < this.checks.length; i2++)
        this.checks[i2](this, state);
      this.stateId = state.data;
    }
    /**
     * Sets whether to enable or disable blending.
     * @param value - Turn on or off WebGl blending.
     */
    setBlend(value) {
      this.updateCheck(_StateSystem2.checkBlendMode, value), this.gl[value ? "enable" : "disable"](this.gl.BLEND);
    }
    /**
     * Sets whether to enable or disable polygon offset fill.
     * @param value - Turn on or off webgl polygon offset testing.
     */
    setOffset(value) {
      this.updateCheck(_StateSystem2.checkPolygonOffset, value), this.gl[value ? "enable" : "disable"](this.gl.POLYGON_OFFSET_FILL);
    }
    /**
     * Sets whether to enable or disable depth test.
     * @param value - Turn on or off webgl depth testing.
     */
    setDepthTest(value) {
      this.gl[value ? "enable" : "disable"](this.gl.DEPTH_TEST);
    }
    /**
     * Sets whether to enable or disable depth mask.
     * @param value - Turn on or off webgl depth mask.
     */
    setDepthMask(value) {
      this.gl.depthMask(value);
    }
    /**
     * Sets whether to enable or disable cull face.
     * @param {boolean} value - Turn on or off webgl cull face.
     */
    setCullFace(value) {
      this.gl[value ? "enable" : "disable"](this.gl.CULL_FACE);
    }
    /**
     * Sets the gl front face.
     * @param {boolean} value - true is clockwise and false is counter-clockwise
     */
    setFrontFace(value) {
      this.gl.frontFace(this.gl[value ? "CW" : "CCW"]);
    }
    /**
     * Sets the blend mode.
     * @param {number} value - The blend mode to set to.
     */
    setBlendMode(value) {
      if (value === this.blendMode)
        return;
      this.blendMode = value;
      const mode = this.blendModes[value], gl = this.gl;
      mode.length === 2 ? gl.blendFunc(mode[0], mode[1]) : gl.blendFuncSeparate(mode[0], mode[1], mode[2], mode[3]), mode.length === 6 ? (this._blendEq = true, gl.blendEquationSeparate(mode[4], mode[5])) : this._blendEq && (this._blendEq = false, gl.blendEquationSeparate(gl.FUNC_ADD, gl.FUNC_ADD));
    }
    /**
     * Sets the polygon offset.
     * @param {number} value - the polygon offset
     * @param {number} scale - the polygon offset scale
     */
    setPolygonOffset(value, scale) {
      this.gl.polygonOffset(value, scale);
    }
    // used
    /** Resets all the logic and disables the VAOs. */
    reset() {
      this.gl.pixelStorei(this.gl.UNPACK_FLIP_Y_WEBGL, false), this.forceState(this.defaultState), this._blendEq = true, this.blendMode = -1, this.setBlendMode(0);
    }
    /**
     * Checks to see which updates should be checked based on which settings have been activated.
     *
     * For example, if blend is enabled then we should check the blend modes each time the state is changed
     * or if polygon fill is activated then we need to check if the polygon offset changes.
     * The idea is that we only check what we have too.
     * @param func - the checking function to add or remove
     * @param value - should the check function be added or removed.
     */
    updateCheck(func, value) {
      const index = this.checks.indexOf(func);
      value && index === -1 ? this.checks.push(func) : !value && index !== -1 && this.checks.splice(index, 1);
    }
    /**
     * A private little wrapper function that we call to check the blend mode.
     * @param system - the System to perform the state check on
     * @param state - the state that the blendMode will pulled from
     */
    static checkBlendMode(system, state) {
      system.setBlendMode(state.blendMode);
    }
    /**
     * A private little wrapper function that we call to check the polygon offset.
     * @param system - the System to perform the state check on
     * @param state - the state that the blendMode will pulled from
     */
    static checkPolygonOffset(system, state) {
      system.setPolygonOffset(1, state.polygonOffset);
    }
    /**
     * @ignore
     */
    destroy() {
      this.gl = null;
    }
  };
  _StateSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "state"
  };
  let StateSystem = _StateSystem;
  extensions.add(StateSystem);
  class SystemManager extends EventEmitter {
    constructor() {
      super(...arguments), this.runners = {}, this._systemsHash = {};
    }
    /**
     * Set up a system with a collection of SystemClasses and runners.
     * Systems are attached dynamically to this class when added.
     * @param config - the config for the system manager
     */
    setup(config) {
      this.addRunners(...config.runners);
      const priority = (config.priority ?? []).filter((key) => config.systems[key]), orderByPriority = [
        ...priority,
        ...Object.keys(config.systems).filter((key) => !priority.includes(key))
      ];
      for (const i2 of orderByPriority)
        this.addSystem(config.systems[i2], i2);
    }
    /**
     * Create a bunch of runners based of a collection of ids
     * @param runnerIds - the runner ids to add
     */
    addRunners(...runnerIds) {
      runnerIds.forEach((runnerId) => {
        this.runners[runnerId] = new Runner(runnerId);
      });
    }
    /**
     * Add a new system to the renderer.
     * @param ClassRef - Class reference
     * @param name - Property name for system, if not specified
     *        will use a static `name` property on the class itself. This
     *        name will be assigned as s property on the Renderer so make
     *        sure it doesn't collide with properties on Renderer.
     * @returns Return instance of renderer
     */
    addSystem(ClassRef, name) {
      const system = new ClassRef(this);
      if (this[name])
        throw new Error(`Whoops! The name "${name}" is already in use`);
      this[name] = system, this._systemsHash[name] = system;
      for (const i2 in this.runners)
        this.runners[i2].add(system);
      return this;
    }
    /**
     * A function that will run a runner and call the runners function but pass in different options
     * to each system based on there name.
     *
     * E.g. If you have two systems added called `systemA` and `systemB` you could call do the following:
     *
     * ```js
     * system.emitWithCustomOptions(init, {
     *     systemA: {...optionsForA},
     *     systemB: {...optionsForB},
     * });
     * ```
     *
     * `init` would be called on system A passing `optionsForA` and on system B passing `optionsForB`.
     * @param runner - the runner to target
     * @param options - key value options for each system
     */
    emitWithCustomOptions(runner, options) {
      const systemHashKeys = Object.keys(this._systemsHash);
      runner.items.forEach((system) => {
        const systemName = systemHashKeys.find((systemId) => this._systemsHash[systemId] === system);
        system[runner.name](options[systemName]);
      });
    }
    /** destroy the all runners and systems. Its apps job to */
    destroy() {
      Object.values(this.runners).forEach((runner) => {
        runner.destroy();
      }), this._systemsHash = {};
    }
    // TODO implement!
    // removeSystem(ClassRef: ISystemConstructor, name: string): void
    // {
    // }
  }
  const _TextureGCSystem = class _TextureGCSystem2 {
    /** @param renderer - The renderer this System works for. */
    constructor(renderer) {
      this.renderer = renderer, this.count = 0, this.checkCount = 0, this.maxIdle = _TextureGCSystem2.defaultMaxIdle, this.checkCountMax = _TextureGCSystem2.defaultCheckCountMax, this.mode = _TextureGCSystem2.defaultMode;
    }
    /**
     * Checks to see when the last time a texture was used.
     * If the texture has not been used for a specified amount of time, it will be removed from the GPU.
     */
    postrender() {
      this.renderer.objectRenderer.renderingToScreen && (this.count++, this.mode !== GC_MODES.MANUAL && (this.checkCount++, this.checkCount > this.checkCountMax && (this.checkCount = 0, this.run())));
    }
    /**
     * Checks to see when the last time a texture was used.
     * If the texture has not been used for a specified amount of time, it will be removed from the GPU.
     */
    run() {
      const tm = this.renderer.texture, managedTextures = tm.managedTextures;
      let wasRemoved = false;
      for (let i2 = 0; i2 < managedTextures.length; i2++) {
        const texture = managedTextures[i2];
        texture.resource && this.count - texture.touched > this.maxIdle && (tm.destroyTexture(texture, true), managedTextures[i2] = null, wasRemoved = true);
      }
      if (wasRemoved) {
        let j2 = 0;
        for (let i2 = 0; i2 < managedTextures.length; i2++)
          managedTextures[i2] !== null && (managedTextures[j2++] = managedTextures[i2]);
        managedTextures.length = j2;
      }
    }
    /**
     * Removes all the textures within the specified displayObject and its children from the GPU.
     * @param {PIXI.DisplayObject} displayObject - the displayObject to remove the textures from.
     */
    unload(displayObject) {
      const tm = this.renderer.texture, texture = displayObject._texture;
      texture && !texture.framebuffer && tm.destroyTexture(texture);
      for (let i2 = displayObject.children.length - 1; i2 >= 0; i2--)
        this.unload(displayObject.children[i2]);
    }
    destroy() {
      this.renderer = null;
    }
  };
  _TextureGCSystem.defaultMode = GC_MODES.AUTO, /**
  * Default maximum idle frames before a texture is destroyed by garbage collection.
  * @static
  * @default 3600
  * @see PIXI.TextureGCSystem#maxIdle
  */
  _TextureGCSystem.defaultMaxIdle = 60 * 60, /**
  * Default frames between two garbage collections.
  * @static
  * @default 600
  * @see PIXI.TextureGCSystem#checkCountMax
  */
  _TextureGCSystem.defaultCheckCountMax = 60 * 10, /** @ignore */
  _TextureGCSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "textureGC"
  };
  let TextureGCSystem = _TextureGCSystem;
  extensions.add(TextureGCSystem);
  class GLTexture {
    constructor(texture) {
      this.texture = texture, this.width = -1, this.height = -1, this.dirtyId = -1, this.dirtyStyleId = -1, this.mipmap = false, this.wrapMode = 33071, this.type = TYPES.UNSIGNED_BYTE, this.internalFormat = FORMATS.RGBA, this.samplerType = 0;
    }
  }
  function mapInternalFormatToSamplerType(gl) {
    let table;
    return "WebGL2RenderingContext" in globalThis && gl instanceof globalThis.WebGL2RenderingContext ? table = {
      [gl.RGB]: SAMPLER_TYPES.FLOAT,
      [gl.RGBA]: SAMPLER_TYPES.FLOAT,
      [gl.ALPHA]: SAMPLER_TYPES.FLOAT,
      [gl.LUMINANCE]: SAMPLER_TYPES.FLOAT,
      [gl.LUMINANCE_ALPHA]: SAMPLER_TYPES.FLOAT,
      [gl.R8]: SAMPLER_TYPES.FLOAT,
      [gl.R8_SNORM]: SAMPLER_TYPES.FLOAT,
      [gl.RG8]: SAMPLER_TYPES.FLOAT,
      [gl.RG8_SNORM]: SAMPLER_TYPES.FLOAT,
      [gl.RGB8]: SAMPLER_TYPES.FLOAT,
      [gl.RGB8_SNORM]: SAMPLER_TYPES.FLOAT,
      [gl.RGB565]: SAMPLER_TYPES.FLOAT,
      [gl.RGBA4]: SAMPLER_TYPES.FLOAT,
      [gl.RGB5_A1]: SAMPLER_TYPES.FLOAT,
      [gl.RGBA8]: SAMPLER_TYPES.FLOAT,
      [gl.RGBA8_SNORM]: SAMPLER_TYPES.FLOAT,
      [gl.RGB10_A2]: SAMPLER_TYPES.FLOAT,
      [gl.RGB10_A2UI]: SAMPLER_TYPES.FLOAT,
      [gl.SRGB8]: SAMPLER_TYPES.FLOAT,
      [gl.SRGB8_ALPHA8]: SAMPLER_TYPES.FLOAT,
      [gl.R16F]: SAMPLER_TYPES.FLOAT,
      [gl.RG16F]: SAMPLER_TYPES.FLOAT,
      [gl.RGB16F]: SAMPLER_TYPES.FLOAT,
      [gl.RGBA16F]: SAMPLER_TYPES.FLOAT,
      [gl.R32F]: SAMPLER_TYPES.FLOAT,
      [gl.RG32F]: SAMPLER_TYPES.FLOAT,
      [gl.RGB32F]: SAMPLER_TYPES.FLOAT,
      [gl.RGBA32F]: SAMPLER_TYPES.FLOAT,
      [gl.R11F_G11F_B10F]: SAMPLER_TYPES.FLOAT,
      [gl.RGB9_E5]: SAMPLER_TYPES.FLOAT,
      [gl.R8I]: SAMPLER_TYPES.INT,
      [gl.R8UI]: SAMPLER_TYPES.UINT,
      [gl.R16I]: SAMPLER_TYPES.INT,
      [gl.R16UI]: SAMPLER_TYPES.UINT,
      [gl.R32I]: SAMPLER_TYPES.INT,
      [gl.R32UI]: SAMPLER_TYPES.UINT,
      [gl.RG8I]: SAMPLER_TYPES.INT,
      [gl.RG8UI]: SAMPLER_TYPES.UINT,
      [gl.RG16I]: SAMPLER_TYPES.INT,
      [gl.RG16UI]: SAMPLER_TYPES.UINT,
      [gl.RG32I]: SAMPLER_TYPES.INT,
      [gl.RG32UI]: SAMPLER_TYPES.UINT,
      [gl.RGB8I]: SAMPLER_TYPES.INT,
      [gl.RGB8UI]: SAMPLER_TYPES.UINT,
      [gl.RGB16I]: SAMPLER_TYPES.INT,
      [gl.RGB16UI]: SAMPLER_TYPES.UINT,
      [gl.RGB32I]: SAMPLER_TYPES.INT,
      [gl.RGB32UI]: SAMPLER_TYPES.UINT,
      [gl.RGBA8I]: SAMPLER_TYPES.INT,
      [gl.RGBA8UI]: SAMPLER_TYPES.UINT,
      [gl.RGBA16I]: SAMPLER_TYPES.INT,
      [gl.RGBA16UI]: SAMPLER_TYPES.UINT,
      [gl.RGBA32I]: SAMPLER_TYPES.INT,
      [gl.RGBA32UI]: SAMPLER_TYPES.UINT,
      [gl.DEPTH_COMPONENT16]: SAMPLER_TYPES.FLOAT,
      [gl.DEPTH_COMPONENT24]: SAMPLER_TYPES.FLOAT,
      [gl.DEPTH_COMPONENT32F]: SAMPLER_TYPES.FLOAT,
      [gl.DEPTH_STENCIL]: SAMPLER_TYPES.FLOAT,
      [gl.DEPTH24_STENCIL8]: SAMPLER_TYPES.FLOAT,
      [gl.DEPTH32F_STENCIL8]: SAMPLER_TYPES.FLOAT
    } : table = {
      [gl.RGB]: SAMPLER_TYPES.FLOAT,
      [gl.RGBA]: SAMPLER_TYPES.FLOAT,
      [gl.ALPHA]: SAMPLER_TYPES.FLOAT,
      [gl.LUMINANCE]: SAMPLER_TYPES.FLOAT,
      [gl.LUMINANCE_ALPHA]: SAMPLER_TYPES.FLOAT,
      [gl.DEPTH_STENCIL]: SAMPLER_TYPES.FLOAT
    }, table;
  }
  function mapTypeAndFormatToInternalFormat(gl) {
    let table;
    return "WebGL2RenderingContext" in globalThis && gl instanceof globalThis.WebGL2RenderingContext ? table = {
      [TYPES.UNSIGNED_BYTE]: {
        [FORMATS.RGBA]: gl.RGBA8,
        [FORMATS.RGB]: gl.RGB8,
        [FORMATS.RG]: gl.RG8,
        [FORMATS.RED]: gl.R8,
        [FORMATS.RGBA_INTEGER]: gl.RGBA8UI,
        [FORMATS.RGB_INTEGER]: gl.RGB8UI,
        [FORMATS.RG_INTEGER]: gl.RG8UI,
        [FORMATS.RED_INTEGER]: gl.R8UI,
        [FORMATS.ALPHA]: gl.ALPHA,
        [FORMATS.LUMINANCE]: gl.LUMINANCE,
        [FORMATS.LUMINANCE_ALPHA]: gl.LUMINANCE_ALPHA
      },
      [TYPES.BYTE]: {
        [FORMATS.RGBA]: gl.RGBA8_SNORM,
        [FORMATS.RGB]: gl.RGB8_SNORM,
        [FORMATS.RG]: gl.RG8_SNORM,
        [FORMATS.RED]: gl.R8_SNORM,
        [FORMATS.RGBA_INTEGER]: gl.RGBA8I,
        [FORMATS.RGB_INTEGER]: gl.RGB8I,
        [FORMATS.RG_INTEGER]: gl.RG8I,
        [FORMATS.RED_INTEGER]: gl.R8I
      },
      [TYPES.UNSIGNED_SHORT]: {
        [FORMATS.RGBA_INTEGER]: gl.RGBA16UI,
        [FORMATS.RGB_INTEGER]: gl.RGB16UI,
        [FORMATS.RG_INTEGER]: gl.RG16UI,
        [FORMATS.RED_INTEGER]: gl.R16UI,
        [FORMATS.DEPTH_COMPONENT]: gl.DEPTH_COMPONENT16
      },
      [TYPES.SHORT]: {
        [FORMATS.RGBA_INTEGER]: gl.RGBA16I,
        [FORMATS.RGB_INTEGER]: gl.RGB16I,
        [FORMATS.RG_INTEGER]: gl.RG16I,
        [FORMATS.RED_INTEGER]: gl.R16I
      },
      [TYPES.UNSIGNED_INT]: {
        [FORMATS.RGBA_INTEGER]: gl.RGBA32UI,
        [FORMATS.RGB_INTEGER]: gl.RGB32UI,
        [FORMATS.RG_INTEGER]: gl.RG32UI,
        [FORMATS.RED_INTEGER]: gl.R32UI,
        [FORMATS.DEPTH_COMPONENT]: gl.DEPTH_COMPONENT24
      },
      [TYPES.INT]: {
        [FORMATS.RGBA_INTEGER]: gl.RGBA32I,
        [FORMATS.RGB_INTEGER]: gl.RGB32I,
        [FORMATS.RG_INTEGER]: gl.RG32I,
        [FORMATS.RED_INTEGER]: gl.R32I
      },
      [TYPES.FLOAT]: {
        [FORMATS.RGBA]: gl.RGBA32F,
        [FORMATS.RGB]: gl.RGB32F,
        [FORMATS.RG]: gl.RG32F,
        [FORMATS.RED]: gl.R32F,
        [FORMATS.DEPTH_COMPONENT]: gl.DEPTH_COMPONENT32F
      },
      [TYPES.HALF_FLOAT]: {
        [FORMATS.RGBA]: gl.RGBA16F,
        [FORMATS.RGB]: gl.RGB16F,
        [FORMATS.RG]: gl.RG16F,
        [FORMATS.RED]: gl.R16F
      },
      [TYPES.UNSIGNED_SHORT_5_6_5]: {
        [FORMATS.RGB]: gl.RGB565
      },
      [TYPES.UNSIGNED_SHORT_4_4_4_4]: {
        [FORMATS.RGBA]: gl.RGBA4
      },
      [TYPES.UNSIGNED_SHORT_5_5_5_1]: {
        [FORMATS.RGBA]: gl.RGB5_A1
      },
      [TYPES.UNSIGNED_INT_2_10_10_10_REV]: {
        [FORMATS.RGBA]: gl.RGB10_A2,
        [FORMATS.RGBA_INTEGER]: gl.RGB10_A2UI
      },
      [TYPES.UNSIGNED_INT_10F_11F_11F_REV]: {
        [FORMATS.RGB]: gl.R11F_G11F_B10F
      },
      [TYPES.UNSIGNED_INT_5_9_9_9_REV]: {
        [FORMATS.RGB]: gl.RGB9_E5
      },
      [TYPES.UNSIGNED_INT_24_8]: {
        [FORMATS.DEPTH_STENCIL]: gl.DEPTH24_STENCIL8
      },
      [TYPES.FLOAT_32_UNSIGNED_INT_24_8_REV]: {
        [FORMATS.DEPTH_STENCIL]: gl.DEPTH32F_STENCIL8
      }
    } : table = {
      [TYPES.UNSIGNED_BYTE]: {
        [FORMATS.RGBA]: gl.RGBA,
        [FORMATS.RGB]: gl.RGB,
        [FORMATS.ALPHA]: gl.ALPHA,
        [FORMATS.LUMINANCE]: gl.LUMINANCE,
        [FORMATS.LUMINANCE_ALPHA]: gl.LUMINANCE_ALPHA
      },
      [TYPES.UNSIGNED_SHORT_5_6_5]: {
        [FORMATS.RGB]: gl.RGB
      },
      [TYPES.UNSIGNED_SHORT_4_4_4_4]: {
        [FORMATS.RGBA]: gl.RGBA
      },
      [TYPES.UNSIGNED_SHORT_5_5_5_1]: {
        [FORMATS.RGBA]: gl.RGBA
      }
    }, table;
  }
  class TextureSystem {
    /**
     * @param renderer - The renderer this system works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.boundTextures = [], this.currentLocation = -1, this.managedTextures = [], this._unknownBoundTextures = false, this.unknownTexture = new BaseTexture(), this.hasIntegerTextures = false;
    }
    /** Sets up the renderer context and necessary buffers. */
    contextChange() {
      const gl = this.gl = this.renderer.gl;
      this.CONTEXT_UID = this.renderer.CONTEXT_UID, this.webGLVersion = this.renderer.context.webGLVersion, this.internalFormats = mapTypeAndFormatToInternalFormat(gl), this.samplerTypes = mapInternalFormatToSamplerType(gl);
      const maxTextures = gl.getParameter(gl.MAX_TEXTURE_IMAGE_UNITS);
      this.boundTextures.length = maxTextures;
      for (let i2 = 0; i2 < maxTextures; i2++)
        this.boundTextures[i2] = null;
      this.emptyTextures = {};
      const emptyTexture2D = new GLTexture(gl.createTexture());
      gl.bindTexture(gl.TEXTURE_2D, emptyTexture2D.texture), gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, 1, 1, 0, gl.RGBA, gl.UNSIGNED_BYTE, new Uint8Array(4)), this.emptyTextures[gl.TEXTURE_2D] = emptyTexture2D, this.emptyTextures[gl.TEXTURE_CUBE_MAP] = new GLTexture(gl.createTexture()), gl.bindTexture(gl.TEXTURE_CUBE_MAP, this.emptyTextures[gl.TEXTURE_CUBE_MAP].texture);
      for (let i2 = 0; i2 < 6; i2++)
        gl.texImage2D(gl.TEXTURE_CUBE_MAP_POSITIVE_X + i2, 0, gl.RGBA, 1, 1, 0, gl.RGBA, gl.UNSIGNED_BYTE, null);
      gl.texParameteri(gl.TEXTURE_CUBE_MAP, gl.TEXTURE_MAG_FILTER, gl.LINEAR), gl.texParameteri(gl.TEXTURE_CUBE_MAP, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
      for (let i2 = 0; i2 < this.boundTextures.length; i2++)
        this.bind(null, i2);
    }
    /**
     * Bind a texture to a specific location
     *
     * If you want to unbind something, please use `unbind(texture)` instead of `bind(null, textureLocation)`
     * @param texture - Texture to bind
     * @param [location=0] - Location to bind at
     */
    bind(texture, location = 0) {
      const { gl } = this;
      if (texture = texture?.castToBaseTexture(), texture?.valid && !texture.parentTextureArray) {
        texture.touched = this.renderer.textureGC.count;
        const glTexture = texture._glTextures[this.CONTEXT_UID] || this.initTexture(texture);
        this.boundTextures[location] !== texture && (this.currentLocation !== location && (this.currentLocation = location, gl.activeTexture(gl.TEXTURE0 + location)), gl.bindTexture(texture.target, glTexture.texture)), glTexture.dirtyId !== texture.dirtyId ? (this.currentLocation !== location && (this.currentLocation = location, gl.activeTexture(gl.TEXTURE0 + location)), this.updateTexture(texture)) : glTexture.dirtyStyleId !== texture.dirtyStyleId && this.updateTextureStyle(texture), this.boundTextures[location] = texture;
      } else
        this.currentLocation !== location && (this.currentLocation = location, gl.activeTexture(gl.TEXTURE0 + location)), gl.bindTexture(gl.TEXTURE_2D, this.emptyTextures[gl.TEXTURE_2D].texture), this.boundTextures[location] = null;
    }
    /** Resets texture location and bound textures Actual `bind(null, i)` calls will be performed at next `unbind()` call */
    reset() {
      this._unknownBoundTextures = true, this.hasIntegerTextures = false, this.currentLocation = -1;
      for (let i2 = 0; i2 < this.boundTextures.length; i2++)
        this.boundTextures[i2] = this.unknownTexture;
    }
    /**
     * Unbind a texture.
     * @param texture - Texture to bind
     */
    unbind(texture) {
      const { gl, boundTextures } = this;
      if (this._unknownBoundTextures) {
        this._unknownBoundTextures = false;
        for (let i2 = 0; i2 < boundTextures.length; i2++)
          boundTextures[i2] === this.unknownTexture && this.bind(null, i2);
      }
      for (let i2 = 0; i2 < boundTextures.length; i2++)
        boundTextures[i2] === texture && (this.currentLocation !== i2 && (gl.activeTexture(gl.TEXTURE0 + i2), this.currentLocation = i2), gl.bindTexture(texture.target, this.emptyTextures[texture.target].texture), boundTextures[i2] = null);
    }
    /**
     * Ensures that current boundTextures all have FLOAT sampler type,
     * see {@link PIXI.SAMPLER_TYPES} for explanation.
     * @param maxTextures - number of locations to check
     */
    ensureSamplerType(maxTextures) {
      const { boundTextures, hasIntegerTextures, CONTEXT_UID } = this;
      if (hasIntegerTextures)
        for (let i2 = maxTextures - 1; i2 >= 0; --i2) {
          const tex = boundTextures[i2];
          tex && tex._glTextures[CONTEXT_UID].samplerType !== SAMPLER_TYPES.FLOAT && this.renderer.texture.unbind(tex);
        }
    }
    /**
     * Initialize a texture
     * @private
     * @param texture - Texture to initialize
     */
    initTexture(texture) {
      const glTexture = new GLTexture(this.gl.createTexture());
      return glTexture.dirtyId = -1, texture._glTextures[this.CONTEXT_UID] = glTexture, this.managedTextures.push(texture), texture.on("dispose", this.destroyTexture, this), glTexture;
    }
    initTextureType(texture, glTexture) {
      glTexture.internalFormat = this.internalFormats[texture.type]?.[texture.format] ?? texture.format, glTexture.samplerType = this.samplerTypes[glTexture.internalFormat] ?? SAMPLER_TYPES.FLOAT, this.webGLVersion === 2 && texture.type === TYPES.HALF_FLOAT ? glTexture.type = this.gl.HALF_FLOAT : glTexture.type = texture.type;
    }
    /**
     * Update a texture
     * @private
     * @param {PIXI.BaseTexture} texture - Texture to initialize
     */
    updateTexture(texture) {
      const glTexture = texture._glTextures[this.CONTEXT_UID];
      if (!glTexture)
        return;
      const renderer = this.renderer;
      if (this.initTextureType(texture, glTexture), texture.resource?.upload(renderer, texture, glTexture))
        glTexture.samplerType !== SAMPLER_TYPES.FLOAT && (this.hasIntegerTextures = true);
      else {
        const width = texture.realWidth, height = texture.realHeight, gl = renderer.gl;
        (glTexture.width !== width || glTexture.height !== height || glTexture.dirtyId < 0) && (glTexture.width = width, glTexture.height = height, gl.texImage2D(
          texture.target,
          0,
          glTexture.internalFormat,
          width,
          height,
          0,
          texture.format,
          glTexture.type,
          null
        ));
      }
      texture.dirtyStyleId !== glTexture.dirtyStyleId && this.updateTextureStyle(texture), glTexture.dirtyId = texture.dirtyId;
    }
    /**
     * Deletes the texture from WebGL
     * @private
     * @param texture - the texture to destroy
     * @param [skipRemove=false] - Whether to skip removing the texture from the TextureManager.
     */
    destroyTexture(texture, skipRemove) {
      const { gl } = this;
      if (texture = texture.castToBaseTexture(), texture._glTextures[this.CONTEXT_UID] && (this.unbind(texture), gl.deleteTexture(texture._glTextures[this.CONTEXT_UID].texture), texture.off("dispose", this.destroyTexture, this), delete texture._glTextures[this.CONTEXT_UID], !skipRemove)) {
        const i2 = this.managedTextures.indexOf(texture);
        i2 !== -1 && removeItems(this.managedTextures, i2, 1);
      }
    }
    /**
     * Update texture style such as mipmap flag
     * @private
     * @param {PIXI.BaseTexture} texture - Texture to update
     */
    updateTextureStyle(texture) {
      const glTexture = texture._glTextures[this.CONTEXT_UID];
      glTexture && ((texture.mipmap === MIPMAP_MODES.POW2 || this.webGLVersion !== 2) && !texture.isPowerOfTwo ? glTexture.mipmap = false : glTexture.mipmap = texture.mipmap >= 1, this.webGLVersion !== 2 && !texture.isPowerOfTwo ? glTexture.wrapMode = WRAP_MODES.CLAMP : glTexture.wrapMode = texture.wrapMode, texture.resource?.style(this.renderer, texture, glTexture) || this.setStyle(texture, glTexture), glTexture.dirtyStyleId = texture.dirtyStyleId);
    }
    /**
     * Set style for texture
     * @private
     * @param texture - Texture to update
     * @param glTexture
     */
    setStyle(texture, glTexture) {
      const gl = this.gl;
      if (glTexture.mipmap && texture.mipmap !== MIPMAP_MODES.ON_MANUAL && gl.generateMipmap(texture.target), gl.texParameteri(texture.target, gl.TEXTURE_WRAP_S, glTexture.wrapMode), gl.texParameteri(texture.target, gl.TEXTURE_WRAP_T, glTexture.wrapMode), glTexture.mipmap) {
        gl.texParameteri(texture.target, gl.TEXTURE_MIN_FILTER, texture.scaleMode === SCALE_MODES.LINEAR ? gl.LINEAR_MIPMAP_LINEAR : gl.NEAREST_MIPMAP_NEAREST);
        const anisotropicExt = this.renderer.context.extensions.anisotropicFiltering;
        if (anisotropicExt && texture.anisotropicLevel > 0 && texture.scaleMode === SCALE_MODES.LINEAR) {
          const level = Math.min(texture.anisotropicLevel, gl.getParameter(anisotropicExt.MAX_TEXTURE_MAX_ANISOTROPY_EXT));
          gl.texParameterf(texture.target, anisotropicExt.TEXTURE_MAX_ANISOTROPY_EXT, level);
        }
      } else
        gl.texParameteri(texture.target, gl.TEXTURE_MIN_FILTER, texture.scaleMode === SCALE_MODES.LINEAR ? gl.LINEAR : gl.NEAREST);
      gl.texParameteri(texture.target, gl.TEXTURE_MAG_FILTER, texture.scaleMode === SCALE_MODES.LINEAR ? gl.LINEAR : gl.NEAREST);
    }
    destroy() {
      this.renderer = null;
    }
  }
  TextureSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "texture"
  };
  extensions.add(TextureSystem);
  class TransformFeedbackSystem {
    /**
     * @param renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer;
    }
    /** Sets up the renderer context and necessary buffers. */
    contextChange() {
      this.gl = this.renderer.gl, this.CONTEXT_UID = this.renderer.CONTEXT_UID;
    }
    /**
     * Bind TransformFeedback and buffers
     * @param transformFeedback - TransformFeedback to bind
     */
    bind(transformFeedback) {
      const { gl, CONTEXT_UID } = this, glTransformFeedback = transformFeedback._glTransformFeedbacks[CONTEXT_UID] || this.createGLTransformFeedback(transformFeedback);
      gl.bindTransformFeedback(gl.TRANSFORM_FEEDBACK, glTransformFeedback);
    }
    /** Unbind TransformFeedback */
    unbind() {
      const { gl } = this;
      gl.bindTransformFeedback(gl.TRANSFORM_FEEDBACK, null);
    }
    /**
     * Begin TransformFeedback
     * @param drawMode - DrawMode for TransformFeedback
     * @param shader - A Shader used by TransformFeedback. Current bound shader will be used if not provided.
     */
    beginTransformFeedback(drawMode, shader) {
      const { gl, renderer } = this;
      shader && renderer.shader.bind(shader), gl.beginTransformFeedback(drawMode);
    }
    /** End TransformFeedback */
    endTransformFeedback() {
      const { gl } = this;
      gl.endTransformFeedback();
    }
    /**
     * Create TransformFeedback and bind buffers
     * @param tf - TransformFeedback
     * @returns WebGLTransformFeedback
     */
    createGLTransformFeedback(tf) {
      const { gl, renderer, CONTEXT_UID } = this, glTransformFeedback = gl.createTransformFeedback();
      tf._glTransformFeedbacks[CONTEXT_UID] = glTransformFeedback, gl.bindTransformFeedback(gl.TRANSFORM_FEEDBACK, glTransformFeedback);
      for (let i2 = 0; i2 < tf.buffers.length; i2++) {
        const buffer = tf.buffers[i2];
        buffer && (renderer.buffer.update(buffer), buffer._glBuffers[CONTEXT_UID].refCount++, gl.bindBufferBase(gl.TRANSFORM_FEEDBACK_BUFFER, i2, buffer._glBuffers[CONTEXT_UID].buffer || null));
      }
      return gl.bindTransformFeedback(gl.TRANSFORM_FEEDBACK, null), tf.disposeRunner.add(this), glTransformFeedback;
    }
    /**
     * Disposes TransfromFeedback
     * @param {PIXI.TransformFeedback} tf - TransformFeedback
     * @param {boolean} [contextLost=false] - If context was lost, we suppress delete TransformFeedback
     */
    disposeTransformFeedback(tf, contextLost) {
      const glTF = tf._glTransformFeedbacks[this.CONTEXT_UID], gl = this.gl;
      tf.disposeRunner.remove(this);
      const bufferSystem = this.renderer.buffer;
      if (bufferSystem)
        for (let i2 = 0; i2 < tf.buffers.length; i2++) {
          const buffer = tf.buffers[i2];
          if (!buffer)
            continue;
          const buf = buffer._glBuffers[this.CONTEXT_UID];
          buf && (buf.refCount--, buf.refCount === 0 && !contextLost && bufferSystem.dispose(buffer, contextLost));
        }
      glTF && (contextLost || gl.deleteTransformFeedback(glTF), delete tf._glTransformFeedbacks[this.CONTEXT_UID]);
    }
    destroy() {
      this.renderer = null;
    }
  }
  TransformFeedbackSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "transformFeedback"
  };
  extensions.add(TransformFeedbackSystem);
  class ViewSystem {
    constructor(renderer) {
      this.renderer = renderer;
    }
    /**
     * initiates the view system
     * @param {PIXI.ViewOptions} options - the options for the view
     */
    init(options) {
      this.screen = new Rectangle(0, 0, options.width, options.height), this.element = options.view || settings.ADAPTER.createCanvas(), this.resolution = options.resolution || settings.RESOLUTION, this.autoDensity = !!options.autoDensity;
    }
    /**
     * Resizes the screen and canvas to the specified dimensions.
     * @param desiredScreenWidth - The new width of the screen.
     * @param desiredScreenHeight - The new height of the screen.
     */
    resizeView(desiredScreenWidth, desiredScreenHeight) {
      this.element.width = Math.round(desiredScreenWidth * this.resolution), this.element.height = Math.round(desiredScreenHeight * this.resolution);
      const screenWidth = this.element.width / this.resolution, screenHeight = this.element.height / this.resolution;
      this.screen.width = screenWidth, this.screen.height = screenHeight, this.autoDensity && (this.element.style.width = `${screenWidth}px`, this.element.style.height = `${screenHeight}px`), this.renderer.emit("resize", screenWidth, screenHeight), this.renderer.runners.resize.emit(this.screen.width, this.screen.height);
    }
    /**
     * Destroys this System and optionally removes the canvas from the dom.
     * @param {boolean} [removeView=false] - Whether to remove the canvas from the DOM.
     */
    destroy(removeView) {
      removeView && this.element.parentNode?.removeChild(this.element), this.renderer = null, this.element = null, this.screen = null;
    }
  }
  ViewSystem.defaultOptions = {
    /**
     * {@link PIXI.IRendererOptions.width}
     * @default 800
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    width: 800,
    /**
     * {@link PIXI.IRendererOptions.height}
     * @default 600
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    height: 600,
    /**
     * {@link PIXI.IRendererOptions.resolution}
     * @type {number}
     * @default PIXI.settings.RESOLUTION
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    resolution: void 0,
    /**
     * {@link PIXI.IRendererOptions.autoDensity}
     * @default false
     * @memberof PIXI.settings.RENDER_OPTIONS
     */
    autoDensity: false
  }, /** @ignore */
  ViewSystem.extension = {
    type: [
      ExtensionType.RendererSystem,
      ExtensionType.CanvasRendererSystem
    ],
    name: "_view"
  };
  extensions.add(ViewSystem);
  settings.PREFER_ENV = ENV.WEBGL2;
  settings.STRICT_TEXTURE_CACHE = false;
  settings.RENDER_OPTIONS = {
    ...ContextSystem.defaultOptions,
    ...BackgroundSystem.defaultOptions,
    ...ViewSystem.defaultOptions,
    ...StartupSystem.defaultOptions
  };
  Object.defineProperties(settings, {
    /**
     * @static
     * @name WRAP_MODE
     * @memberof PIXI.settings
     * @type {PIXI.WRAP_MODES}
     * @deprecated since 7.1.0
     * @see PIXI.BaseTexture.defaultOptions.wrapMode
     */
    WRAP_MODE: {
      get() {
        return BaseTexture.defaultOptions.wrapMode;
      },
      set(value) {
        deprecation("7.1.0", "settings.WRAP_MODE is deprecated, use BaseTexture.defaultOptions.wrapMode"), BaseTexture.defaultOptions.wrapMode = value;
      }
    },
    /**
     * @static
     * @name SCALE_MODE
     * @memberof PIXI.settings
     * @type {PIXI.SCALE_MODES}
     * @deprecated since 7.1.0
     * @see PIXI.BaseTexture.defaultOptions.scaleMode
     */
    SCALE_MODE: {
      get() {
        return BaseTexture.defaultOptions.scaleMode;
      },
      set(value) {
        deprecation("7.1.0", "settings.SCALE_MODE is deprecated, use BaseTexture.defaultOptions.scaleMode"), BaseTexture.defaultOptions.scaleMode = value;
      }
    },
    /**
     * @static
     * @name MIPMAP_TEXTURES
     * @memberof PIXI.settings
     * @type {PIXI.MIPMAP_MODES}
     * @deprecated since 7.1.0
     * @see PIXI.BaseTexture.defaultOptions.mipmap
     */
    MIPMAP_TEXTURES: {
      get() {
        return BaseTexture.defaultOptions.mipmap;
      },
      set(value) {
        deprecation("7.1.0", "settings.MIPMAP_TEXTURES is deprecated, use BaseTexture.defaultOptions.mipmap"), BaseTexture.defaultOptions.mipmap = value;
      }
      // MIPMAP_MODES.POW2,
    },
    /**
     * @static
     * @name ANISOTROPIC_LEVEL
     * @memberof PIXI.settings
     * @type {number}
     * @deprecated since 7.1.0
     * @see PIXI.BaseTexture.defaultOptions.anisotropicLevel
     */
    ANISOTROPIC_LEVEL: {
      get() {
        return BaseTexture.defaultOptions.anisotropicLevel;
      },
      set(value) {
        deprecation(
          "7.1.0",
          "settings.ANISOTROPIC_LEVEL is deprecated, use BaseTexture.defaultOptions.anisotropicLevel"
        ), BaseTexture.defaultOptions.anisotropicLevel = value;
      }
    },
    /**
     * Default filter resolution.
     * @static
     * @name FILTER_RESOLUTION
     * @memberof PIXI.settings
     * @deprecated since 7.1.0
     * @type {number|null}
     * @see PIXI.Filter.defaultResolution
     */
    FILTER_RESOLUTION: {
      get() {
        return deprecation("7.1.0", "settings.FILTER_RESOLUTION is deprecated, use Filter.defaultResolution"), Filter.defaultResolution;
      },
      set(value) {
        Filter.defaultResolution = value;
      }
    },
    /**
     * Default filter samples.
     * @static
     * @name FILTER_MULTISAMPLE
     * @memberof PIXI.settings
     * @deprecated since 7.1.0
     * @type {PIXI.MSAA_QUALITY}
     * @see PIXI.Filter.defaultMultisample
     */
    FILTER_MULTISAMPLE: {
      get() {
        return deprecation("7.1.0", "settings.FILTER_MULTISAMPLE is deprecated, use Filter.defaultMultisample"), Filter.defaultMultisample;
      },
      set(value) {
        Filter.defaultMultisample = value;
      }
    },
    /**
     * The maximum textures that this device supports.
     * @static
     * @name SPRITE_MAX_TEXTURES
     * @memberof PIXI.settings
     * @deprecated since 7.1.0
     * @see PIXI.BatchRenderer.defaultMaxTextures
     * @type {number}
     */
    SPRITE_MAX_TEXTURES: {
      get() {
        return BatchRenderer.defaultMaxTextures;
      },
      set(value) {
        deprecation("7.1.0", "settings.SPRITE_MAX_TEXTURES is deprecated, use BatchRenderer.defaultMaxTextures"), BatchRenderer.defaultMaxTextures = value;
      }
    },
    /**
     * The default sprite batch size.
     *
     * The default aims to balance desktop and mobile devices.
     * @static
     * @name SPRITE_BATCH_SIZE
     * @memberof PIXI.settings
     * @see PIXI.BatchRenderer.defaultBatchSize
     * @deprecated since 7.1.0
     * @type {number}
     */
    SPRITE_BATCH_SIZE: {
      get() {
        return BatchRenderer.defaultBatchSize;
      },
      set(value) {
        deprecation("7.1.0", "settings.SPRITE_BATCH_SIZE is deprecated, use BatchRenderer.defaultBatchSize"), BatchRenderer.defaultBatchSize = value;
      }
    },
    /**
     * Can we upload the same buffer in a single frame?
     * @static
     * @name CAN_UPLOAD_SAME_BUFFER
     * @memberof PIXI.settings
     * @see PIXI.BatchRenderer.canUploadSameBuffer
     * @deprecated since 7.1.0
     * @type {boolean}
     */
    CAN_UPLOAD_SAME_BUFFER: {
      get() {
        return BatchRenderer.canUploadSameBuffer;
      },
      set(value) {
        deprecation("7.1.0", "settings.CAN_UPLOAD_SAME_BUFFER is deprecated, use BatchRenderer.canUploadSameBuffer"), BatchRenderer.canUploadSameBuffer = value;
      }
    },
    /**
     * Default Garbage Collection mode.
     * @static
     * @name GC_MODE
     * @memberof PIXI.settings
     * @type {PIXI.GC_MODES}
     * @deprecated since 7.1.0
     * @see PIXI.TextureGCSystem.defaultMode
     */
    GC_MODE: {
      get() {
        return TextureGCSystem.defaultMode;
      },
      set(value) {
        deprecation("7.1.0", "settings.GC_MODE is deprecated, use TextureGCSystem.defaultMode"), TextureGCSystem.defaultMode = value;
      }
    },
    /**
     * Default Garbage Collection max idle.
     * @static
     * @name GC_MAX_IDLE
     * @memberof PIXI.settings
     * @type {number}
     * @deprecated since 7.1.0
     * @see PIXI.TextureGCSystem.defaultMaxIdle
     */
    GC_MAX_IDLE: {
      get() {
        return TextureGCSystem.defaultMaxIdle;
      },
      set(value) {
        deprecation("7.1.0", "settings.GC_MAX_IDLE is deprecated, use TextureGCSystem.defaultMaxIdle"), TextureGCSystem.defaultMaxIdle = value;
      }
    },
    /**
     * Default Garbage Collection maximum check count.
     * @static
     * @name GC_MAX_CHECK_COUNT
     * @memberof PIXI.settings
     * @type {number}
     * @deprecated since 7.1.0
     * @see PIXI.TextureGCSystem.defaultCheckCountMax
     */
    GC_MAX_CHECK_COUNT: {
      get() {
        return TextureGCSystem.defaultCheckCountMax;
      },
      set(value) {
        deprecation("7.1.0", "settings.GC_MAX_CHECK_COUNT is deprecated, use TextureGCSystem.defaultCheckCountMax"), TextureGCSystem.defaultCheckCountMax = value;
      }
    },
    /**
     * Default specify float precision in vertex shader.
     * @static
     * @name PRECISION_VERTEX
     * @memberof PIXI.settings
     * @type {PIXI.PRECISION}
     * @deprecated since 7.1.0
     * @see PIXI.Program.defaultVertexPrecision
     */
    PRECISION_VERTEX: {
      get() {
        return Program.defaultVertexPrecision;
      },
      set(value) {
        deprecation("7.1.0", "settings.PRECISION_VERTEX is deprecated, use Program.defaultVertexPrecision"), Program.defaultVertexPrecision = value;
      }
    },
    /**
     * Default specify float precision in fragment shader.
     * @static
     * @name PRECISION_FRAGMENT
     * @memberof PIXI.settings
     * @type {PIXI.PRECISION}
     * @deprecated since 7.1.0
     * @see PIXI.Program.defaultFragmentPrecision
     */
    PRECISION_FRAGMENT: {
      get() {
        return Program.defaultFragmentPrecision;
      },
      set(value) {
        deprecation("7.1.0", "settings.PRECISION_FRAGMENT is deprecated, use Program.defaultFragmentPrecision"), Program.defaultFragmentPrecision = value;
      }
    }
  });
  var UPDATE_PRIORITY = /* @__PURE__ */ ((UPDATE_PRIORITY2) => (UPDATE_PRIORITY2[UPDATE_PRIORITY2.INTERACTION = 50] = "INTERACTION", UPDATE_PRIORITY2[UPDATE_PRIORITY2.HIGH = 25] = "HIGH", UPDATE_PRIORITY2[UPDATE_PRIORITY2.NORMAL = 0] = "NORMAL", UPDATE_PRIORITY2[UPDATE_PRIORITY2.LOW = -25] = "LOW", UPDATE_PRIORITY2[UPDATE_PRIORITY2.UTILITY = -50] = "UTILITY", UPDATE_PRIORITY2))(UPDATE_PRIORITY || {});
  class TickerListener {
    /**
     * Constructor
     * @private
     * @param fn - The listener function to be added for one update
     * @param context - The listener context
     * @param priority - The priority for emitting
     * @param once - If the handler should fire once
     */
    constructor(fn, context2 = null, priority = 0, once = false) {
      this.next = null, this.previous = null, this._destroyed = false, this.fn = fn, this.context = context2, this.priority = priority, this.once = once;
    }
    /**
     * Simple compare function to figure out if a function and context match.
     * @private
     * @param fn - The listener function to be added for one update
     * @param context - The listener context
     * @returns `true` if the listener match the arguments
     */
    match(fn, context2 = null) {
      return this.fn === fn && this.context === context2;
    }
    /**
     * Emit by calling the current function.
     * @private
     * @param deltaTime - time since the last emit.
     * @returns Next ticker
     */
    emit(deltaTime) {
      this.fn && (this.context ? this.fn.call(this.context, deltaTime) : this.fn(deltaTime));
      const redirect = this.next;
      return this.once && this.destroy(true), this._destroyed && (this.next = null), redirect;
    }
    /**
     * Connect to the list.
     * @private
     * @param previous - Input node, previous listener
     */
    connect(previous) {
      this.previous = previous, previous.next && (previous.next.previous = this), this.next = previous.next, previous.next = this;
    }
    /**
     * Destroy and don't use after this.
     * @private
     * @param hard - `true` to remove the `next` reference, this
     *        is considered a hard destroy. Soft destroy maintains the next reference.
     * @returns The listener to redirect while emitting or removing.
     */
    destroy(hard = false) {
      this._destroyed = true, this.fn = null, this.context = null, this.previous && (this.previous.next = this.next), this.next && (this.next.previous = this.previous);
      const redirect = this.next;
      return this.next = hard ? null : redirect, this.previous = null, redirect;
    }
  }
  const _Ticker = class _Ticker2 {
    constructor() {
      this.autoStart = false, this.deltaTime = 1, this.lastTime = -1, this.speed = 1, this.started = false, this._requestId = null, this._maxElapsedMS = 100, this._minElapsedMS = 0, this._protected = false, this._lastFrame = -1, this._head = new TickerListener(null, null, 1 / 0), this.deltaMS = 1 / _Ticker2.targetFPMS, this.elapsedMS = 1 / _Ticker2.targetFPMS, this._tick = (time) => {
        this._requestId = null, this.started && (this.update(time), this.started && this._requestId === null && this._head.next && (this._requestId = requestAnimationFrame(this._tick)));
      };
    }
    /**
     * Conditionally requests a new animation frame.
     * If a frame has not already been requested, and if the internal
     * emitter has listeners, a new frame is requested.
     * @private
     */
    _requestIfNeeded() {
      this._requestId === null && this._head.next && (this.lastTime = performance.now(), this._lastFrame = this.lastTime, this._requestId = requestAnimationFrame(this._tick));
    }
    /**
     * Conditionally cancels a pending animation frame.
     * @private
     */
    _cancelIfNeeded() {
      this._requestId !== null && (cancelAnimationFrame(this._requestId), this._requestId = null);
    }
    /**
     * Conditionally requests a new animation frame.
     * If the ticker has been started it checks if a frame has not already
     * been requested, and if the internal emitter has listeners. If these
     * conditions are met, a new frame is requested. If the ticker has not
     * been started, but autoStart is `true`, then the ticker starts now,
     * and continues with the previous conditions to request a new frame.
     * @private
     */
    _startIfPossible() {
      this.started ? this._requestIfNeeded() : this.autoStart && this.start();
    }
    /**
     * Register a handler for tick events. Calls continuously unless
     * it is removed or the ticker is stopped.
     * @param fn - The listener function to be added for updates
     * @param context - The listener context
     * @param {number} [priority=PIXI.UPDATE_PRIORITY.NORMAL] - The priority for emitting
     * @returns This instance of a ticker
     */
    add(fn, context2, priority = UPDATE_PRIORITY.NORMAL) {
      return this._addListener(new TickerListener(fn, context2, priority));
    }
    /**
     * Add a handler for the tick event which is only execute once.
     * @param fn - The listener function to be added for one update
     * @param context - The listener context
     * @param {number} [priority=PIXI.UPDATE_PRIORITY.NORMAL] - The priority for emitting
     * @returns This instance of a ticker
     */
    addOnce(fn, context2, priority = UPDATE_PRIORITY.NORMAL) {
      return this._addListener(new TickerListener(fn, context2, priority, true));
    }
    /**
     * Internally adds the event handler so that it can be sorted by priority.
     * Priority allows certain handler (user, AnimatedSprite, Interaction) to be run
     * before the rendering.
     * @private
     * @param listener - Current listener being added.
     * @returns This instance of a ticker
     */
    _addListener(listener) {
      let current = this._head.next, previous = this._head;
      if (!current)
        listener.connect(previous);
      else {
        for (; current; ) {
          if (listener.priority > current.priority) {
            listener.connect(previous);
            break;
          }
          previous = current, current = current.next;
        }
        listener.previous || listener.connect(previous);
      }
      return this._startIfPossible(), this;
    }
    /**
     * Removes any handlers matching the function and context parameters.
     * If no handlers are left after removing, then it cancels the animation frame.
     * @param fn - The listener function to be removed
     * @param context - The listener context to be removed
     * @returns This instance of a ticker
     */
    remove(fn, context2) {
      let listener = this._head.next;
      for (; listener; )
        listener.match(fn, context2) ? listener = listener.destroy() : listener = listener.next;
      return this._head.next || this._cancelIfNeeded(), this;
    }
    /**
     * The number of listeners on this ticker, calculated by walking through linked list
     * @readonly
     * @member {number}
     */
    get count() {
      if (!this._head)
        return 0;
      let count = 0, current = this._head;
      for (; current = current.next; )
        count++;
      return count;
    }
    /** Starts the ticker. If the ticker has listeners a new animation frame is requested at this point. */
    start() {
      this.started || (this.started = true, this._requestIfNeeded());
    }
    /** Stops the ticker. If the ticker has requested an animation frame it is canceled at this point. */
    stop() {
      this.started && (this.started = false, this._cancelIfNeeded());
    }
    /** Destroy the ticker and don't use after this. Calling this method removes all references to internal events. */
    destroy() {
      if (!this._protected) {
        this.stop();
        let listener = this._head.next;
        for (; listener; )
          listener = listener.destroy(true);
        this._head.destroy(), this._head = null;
      }
    }
    /**
     * Triggers an update. An update entails setting the
     * current {@link PIXI.Ticker#elapsedMS},
     * the current {@link PIXI.Ticker#deltaTime},
     * invoking all listeners with current deltaTime,
     * and then finally setting {@link PIXI.Ticker#lastTime}
     * with the value of currentTime that was provided.
     * This method will be called automatically by animation
     * frame callbacks if the ticker instance has been started
     * and listeners are added.
     * @param {number} [currentTime=performance.now()] - the current time of execution
     */
    update(currentTime2 = performance.now()) {
      let elapsedMS;
      if (currentTime2 > this.lastTime) {
        if (elapsedMS = this.elapsedMS = currentTime2 - this.lastTime, elapsedMS > this._maxElapsedMS && (elapsedMS = this._maxElapsedMS), elapsedMS *= this.speed, this._minElapsedMS) {
          const delta = currentTime2 - this._lastFrame | 0;
          if (delta < this._minElapsedMS)
            return;
          this._lastFrame = currentTime2 - delta % this._minElapsedMS;
        }
        this.deltaMS = elapsedMS, this.deltaTime = this.deltaMS * _Ticker2.targetFPMS;
        const head = this._head;
        let listener = head.next;
        for (; listener; )
          listener = listener.emit(this.deltaTime);
        head.next || this._cancelIfNeeded();
      } else
        this.deltaTime = this.deltaMS = this.elapsedMS = 0;
      this.lastTime = currentTime2;
    }
    /**
     * The frames per second at which this ticker is running.
     * The default is approximately 60 in most modern browsers.
     * **Note:** This does not factor in the value of
     * {@link PIXI.Ticker#speed}, which is specific
     * to scaling {@link PIXI.Ticker#deltaTime}.
     * @member {number}
     * @readonly
     */
    get FPS() {
      return 1e3 / this.elapsedMS;
    }
    /**
     * Manages the maximum amount of milliseconds allowed to
     * elapse between invoking {@link PIXI.Ticker#update}.
     * This value is used to cap {@link PIXI.Ticker#deltaTime},
     * but does not effect the measured value of {@link PIXI.Ticker#FPS}.
     * When setting this property it is clamped to a value between
     * `0` and `Ticker.targetFPMS * 1000`.
     * @member {number}
     * @default 10
     */
    get minFPS() {
      return 1e3 / this._maxElapsedMS;
    }
    set minFPS(fps) {
      const minFPS = Math.min(this.maxFPS, fps), minFPMS = Math.min(Math.max(0, minFPS) / 1e3, _Ticker2.targetFPMS);
      this._maxElapsedMS = 1 / minFPMS;
    }
    /**
     * Manages the minimum amount of milliseconds required to
     * elapse between invoking {@link PIXI.Ticker#update}.
     * This will effect the measured value of {@link PIXI.Ticker#FPS}.
     * If it is set to `0`, then there is no limit; PixiJS will render as many frames as it can.
     * Otherwise it will be at least `minFPS`
     * @member {number}
     * @default 0
     */
    get maxFPS() {
      return this._minElapsedMS ? Math.round(1e3 / this._minElapsedMS) : 0;
    }
    set maxFPS(fps) {
      if (fps === 0)
        this._minElapsedMS = 0;
      else {
        const maxFPS = Math.max(this.minFPS, fps);
        this._minElapsedMS = 1 / (maxFPS / 1e3);
      }
    }
    /**
     * The shared ticker instance used by {@link PIXI.AnimatedSprite} and by
     * {@link PIXI.VideoResource} to update animation frames / video textures.
     *
     * It may also be used by {@link PIXI.Application} if created with the `sharedTicker` option property set to true.
     *
     * The property {@link PIXI.Ticker#autoStart} is set to `true` for this instance.
     * Please follow the examples for usage, including how to opt-out of auto-starting the shared ticker.
     * @example
     * import { Ticker } from 'pixi.js';
     *
     * const ticker = Ticker.shared;
     * // Set this to prevent starting this ticker when listeners are added.
     * // By default this is true only for the PIXI.Ticker.shared instance.
     * ticker.autoStart = false;
     *
     * // FYI, call this to ensure the ticker is stopped. It should be stopped
     * // if you have not attempted to render anything yet.
     * ticker.stop();
     *
     * // Call this when you are ready for a running shared ticker.
     * ticker.start();
     * @example
     * import { autoDetectRenderer, Container } from 'pixi.js';
     *
     * // You may use the shared ticker to render...
     * const renderer = autoDetectRenderer();
     * const stage = new Container();
     * document.body.appendChild(renderer.view);
     * ticker.add((time) => renderer.render(stage));
     *
     * // Or you can just update it manually.
     * ticker.autoStart = false;
     * ticker.stop();
     * const animate = (time) => {
     *     ticker.update(time);
     *     renderer.render(stage);
     *     requestAnimationFrame(animate);
     * };
     * animate(performance.now());
     * @member {PIXI.Ticker}
     * @static
     */
    static get shared() {
      if (!_Ticker2._shared) {
        const shared = _Ticker2._shared = new _Ticker2();
        shared.autoStart = true, shared._protected = true;
      }
      return _Ticker2._shared;
    }
    /**
     * The system ticker instance used by {@link PIXI.BasePrepare} for core timing
     * functionality that shouldn't usually need to be paused, unlike the `shared`
     * ticker which drives visual animations and rendering which may want to be paused.
     *
     * The property {@link PIXI.Ticker#autoStart} is set to `true` for this instance.
     * @member {PIXI.Ticker}
     * @static
     */
    static get system() {
      if (!_Ticker2._system) {
        const system = _Ticker2._system = new _Ticker2();
        system.autoStart = true, system._protected = true;
      }
      return _Ticker2._system;
    }
  };
  _Ticker.targetFPMS = 0.06;
  let Ticker = _Ticker;
  Object.defineProperties(settings, {
    /**
     * Target frames per millisecond.
     * @static
     * @name TARGET_FPMS
     * @memberof PIXI.settings
     * @type {number}
     * @deprecated since 7.1.0
     * @see PIXI.Ticker.targetFPMS
     */
    TARGET_FPMS: {
      get() {
        return Ticker.targetFPMS;
      },
      set(value) {
        deprecation("7.1.0", "settings.TARGET_FPMS is deprecated, use Ticker.targetFPMS"), Ticker.targetFPMS = value;
      }
    }
  });
  class TickerPlugin {
    /**
     * Initialize the plugin with scope of application instance
     * @static
     * @private
     * @param {object} [options] - See application options
     */
    static init(options) {
      options = Object.assign({
        autoStart: true,
        sharedTicker: false
      }, options), Object.defineProperty(
        this,
        "ticker",
        {
          set(ticker) {
            this._ticker && this._ticker.remove(this.render, this), this._ticker = ticker, ticker && ticker.add(this.render, this, UPDATE_PRIORITY.LOW);
          },
          get() {
            return this._ticker;
          }
        }
      ), this.stop = () => {
        this._ticker.stop();
      }, this.start = () => {
        this._ticker.start();
      }, this._ticker = null, this.ticker = options.sharedTicker ? Ticker.shared : new Ticker(), options.autoStart && this.start();
    }
    /**
     * Clean up the ticker, scoped to application.
     * @static
     * @private
     */
    static destroy() {
      if (this._ticker) {
        const oldTicker = this._ticker;
        this.ticker = null, oldTicker.destroy();
      }
    }
  }
  TickerPlugin.extension = ExtensionType.Application;
  extensions.add(TickerPlugin);
  const renderers = [];
  extensions.handleByList(ExtensionType.Renderer, renderers);
  function autoDetectRenderer(options) {
    for (const RendererType of renderers)
      if (RendererType.test(options))
        return new RendererType(options);
    throw new Error("Unable to auto-detect a suitable renderer.");
  }
  var $defaultFilterVertex = `attribute vec2 aVertexPosition;

uniform mat3 projectionMatrix;

varying vec2 vTextureCoord;

uniform vec4 inputSize;
uniform vec4 outputFrame;

vec4 filterVertexPosition( void )
{
    vec2 position = aVertexPosition * max(outputFrame.zw, vec2(0.)) + outputFrame.xy;

    return vec4((projectionMatrix * vec3(position, 1.0)).xy, 0.0, 1.0);
}

vec2 filterTextureCoord( void )
{
    return aVertexPosition * (outputFrame.zw * inputSize.zw);
}

void main(void)
{
    gl_Position = filterVertexPosition();
    vTextureCoord = filterTextureCoord();
}
`;
  const defaultFilterVertex = $defaultFilterVertex;
  class MultisampleSystem {
    constructor(renderer) {
      this.renderer = renderer;
    }
    contextChange(gl) {
      let samples;
      if (this.renderer.context.webGLVersion === 1) {
        const framebuffer = gl.getParameter(gl.FRAMEBUFFER_BINDING);
        gl.bindFramebuffer(gl.FRAMEBUFFER, null), samples = gl.getParameter(gl.SAMPLES), gl.bindFramebuffer(gl.FRAMEBUFFER, framebuffer);
      } else {
        const framebuffer = gl.getParameter(gl.DRAW_FRAMEBUFFER_BINDING);
        gl.bindFramebuffer(gl.DRAW_FRAMEBUFFER, null), samples = gl.getParameter(gl.SAMPLES), gl.bindFramebuffer(gl.DRAW_FRAMEBUFFER, framebuffer);
      }
      samples >= MSAA_QUALITY.HIGH ? this.multisample = MSAA_QUALITY.HIGH : samples >= MSAA_QUALITY.MEDIUM ? this.multisample = MSAA_QUALITY.MEDIUM : samples >= MSAA_QUALITY.LOW ? this.multisample = MSAA_QUALITY.LOW : this.multisample = MSAA_QUALITY.NONE;
    }
    destroy() {
    }
  }
  MultisampleSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "_multisample"
  };
  extensions.add(MultisampleSystem);
  class GLBuffer {
    constructor(buffer) {
      this.buffer = buffer || null, this.updateID = -1, this.byteLength = -1, this.refCount = 0;
    }
  }
  class BufferSystem {
    /**
     * @param {PIXI.Renderer} renderer - The renderer this System works for.
     */
    constructor(renderer) {
      this.renderer = renderer, this.managedBuffers = {}, this.boundBufferBases = {};
    }
    /**
     * @ignore
     */
    destroy() {
      this.renderer = null;
    }
    /** Sets up the renderer context and necessary buffers. */
    contextChange() {
      this.disposeAll(true), this.gl = this.renderer.gl, this.CONTEXT_UID = this.renderer.CONTEXT_UID;
    }
    /**
     * This binds specified buffer. On first run, it will create the webGL buffers for the context too
     * @param buffer - the buffer to bind to the renderer
     */
    bind(buffer) {
      const { gl, CONTEXT_UID } = this, glBuffer = buffer._glBuffers[CONTEXT_UID] || this.createGLBuffer(buffer);
      gl.bindBuffer(buffer.type, glBuffer.buffer);
    }
    unbind(type2) {
      const { gl } = this;
      gl.bindBuffer(type2, null);
    }
    /**
     * Binds an uniform buffer to at the given index.
     *
     * A cache is used so a buffer will not be bound again if already bound.
     * @param buffer - the buffer to bind
     * @param index - the base index to bind it to.
     */
    bindBufferBase(buffer, index) {
      const { gl, CONTEXT_UID } = this;
      if (this.boundBufferBases[index] !== buffer) {
        const glBuffer = buffer._glBuffers[CONTEXT_UID] || this.createGLBuffer(buffer);
        this.boundBufferBases[index] = buffer, gl.bindBufferBase(gl.UNIFORM_BUFFER, index, glBuffer.buffer);
      }
    }
    /**
     * Binds a buffer whilst also binding its range.
     * This will make the buffer start from the offset supplied rather than 0 when it is read.
     * @param buffer - the buffer to bind
     * @param index - the base index to bind at, defaults to 0
     * @param offset - the offset to bind at (this is blocks of 256). 0 = 0, 1 = 256, 2 = 512 etc
     */
    bindBufferRange(buffer, index, offset) {
      const { gl, CONTEXT_UID } = this;
      offset = offset || 0;
      const glBuffer = buffer._glBuffers[CONTEXT_UID] || this.createGLBuffer(buffer);
      gl.bindBufferRange(gl.UNIFORM_BUFFER, index || 0, glBuffer.buffer, offset * 256, 256);
    }
    /**
     * Will ensure the data in the buffer is uploaded to the GPU.
     * @param {PIXI.Buffer} buffer - the buffer to update
     */
    update(buffer) {
      const { gl, CONTEXT_UID } = this, glBuffer = buffer._glBuffers[CONTEXT_UID] || this.createGLBuffer(buffer);
      if (buffer._updateID !== glBuffer.updateID)
        if (glBuffer.updateID = buffer._updateID, gl.bindBuffer(buffer.type, glBuffer.buffer), glBuffer.byteLength >= buffer.data.byteLength)
          gl.bufferSubData(buffer.type, 0, buffer.data);
        else {
          const drawType = buffer.static ? gl.STATIC_DRAW : gl.DYNAMIC_DRAW;
          glBuffer.byteLength = buffer.data.byteLength, gl.bufferData(buffer.type, buffer.data, drawType);
        }
    }
    /**
     * Disposes buffer
     * @param {PIXI.Buffer} buffer - buffer with data
     * @param {boolean} [contextLost=false] - If context was lost, we suppress deleteVertexArray
     */
    dispose(buffer, contextLost) {
      if (!this.managedBuffers[buffer.id])
        return;
      delete this.managedBuffers[buffer.id];
      const glBuffer = buffer._glBuffers[this.CONTEXT_UID], gl = this.gl;
      buffer.disposeRunner.remove(this), glBuffer && (contextLost || gl.deleteBuffer(glBuffer.buffer), delete buffer._glBuffers[this.CONTEXT_UID]);
    }
    /**
     * dispose all WebGL resources of all managed buffers
     * @param {boolean} [contextLost=false] - If context was lost, we suppress `gl.delete` calls
     */
    disposeAll(contextLost) {
      const all = Object.keys(this.managedBuffers);
      for (let i2 = 0; i2 < all.length; i2++)
        this.dispose(this.managedBuffers[all[i2]], contextLost);
    }
    /**
     * creates and attaches a GLBuffer object tied to the current context.
     * @param buffer
     * @protected
     */
    createGLBuffer(buffer) {
      const { CONTEXT_UID, gl } = this;
      return buffer._glBuffers[CONTEXT_UID] = new GLBuffer(gl.createBuffer()), this.managedBuffers[buffer.id] = buffer, buffer.disposeRunner.add(this), buffer._glBuffers[CONTEXT_UID];
    }
  }
  BufferSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "buffer"
  };
  extensions.add(BufferSystem);
  class ObjectRendererSystem {
    // renderers scene graph!
    constructor(renderer) {
      this.renderer = renderer;
    }
    /**
     * Renders the object to its WebGL view.
     * @param displayObject - The object to be rendered.
     * @param options - the options to be passed to the renderer
     */
    render(displayObject, options) {
      const renderer = this.renderer;
      let renderTexture, clear, transform, skipUpdateTransform;
      if (options && (renderTexture = options.renderTexture, clear = options.clear, transform = options.transform, skipUpdateTransform = options.skipUpdateTransform), this.renderingToScreen = !renderTexture, renderer.runners.prerender.emit(), renderer.emit("prerender"), renderer.projection.transform = transform, !renderer.context.isLost) {
        if (renderTexture || (this.lastObjectRendered = displayObject), !skipUpdateTransform) {
          const cacheParent = displayObject.enableTempParent();
          displayObject.updateTransform(), displayObject.disableTempParent(cacheParent);
        }
        renderer.renderTexture.bind(renderTexture), renderer.batch.currentRenderer.start(), (clear ?? renderer.background.clearBeforeRender) && renderer.renderTexture.clear(), displayObject.render(renderer), renderer.batch.currentRenderer.flush(), renderTexture && (options.blit && renderer.framebuffer.blit(), renderTexture.baseTexture.update()), renderer.runners.postrender.emit(), renderer.projection.transform = null, renderer.emit("postrender");
      }
    }
    destroy() {
      this.renderer = null, this.lastObjectRendered = null;
    }
  }
  ObjectRendererSystem.extension = {
    type: ExtensionType.RendererSystem,
    name: "objectRenderer"
  };
  extensions.add(ObjectRendererSystem);
  const _Renderer = class _Renderer2 extends SystemManager {
    /**
     * @param {PIXI.IRendererOptions} [options] - See {@link PIXI.settings.RENDER_OPTIONS} for defaults.
     */
    constructor(options) {
      super(), this.type = RENDERER_TYPE.WEBGL, options = Object.assign({}, settings.RENDER_OPTIONS, options), this.gl = null, this.CONTEXT_UID = 0, this.globalUniforms = new UniformGroup({
        projectionMatrix: new Matrix()
      }, true);
      const systemConfig = {
        runners: [
          "init",
          "destroy",
          "contextChange",
          "resolutionChange",
          "reset",
          "update",
          "postrender",
          "prerender",
          "resize"
        ],
        systems: _Renderer2.__systems,
        priority: [
          "_view",
          "textureGenerator",
          "background",
          "_plugin",
          "startup",
          // low level WebGL systems
          "context",
          "state",
          "texture",
          "buffer",
          "geometry",
          "framebuffer",
          "transformFeedback",
          // high level pixi specific rendering
          "mask",
          "scissor",
          "stencil",
          "projection",
          "textureGC",
          "filter",
          "renderTexture",
          "batch",
          "objectRenderer",
          "_multisample"
        ]
      };
      this.setup(systemConfig), "useContextAlpha" in options && (deprecation("7.0.0", "options.useContextAlpha is deprecated, use options.premultipliedAlpha and options.backgroundAlpha instead"), options.premultipliedAlpha = options.useContextAlpha && options.useContextAlpha !== "notMultiplied", options.backgroundAlpha = options.useContextAlpha === false ? 1 : options.backgroundAlpha), this._plugin.rendererPlugins = _Renderer2.__plugins, this.options = options, this.startup.run(this.options);
    }
    /**
     * Create renderer if WebGL is available. Overrideable
     * by the **@pixi/canvas-renderer** package to allow fallback.
     * throws error if WebGL is not available.
     * @param options
     * @private
     */
    static test(options) {
      return options?.forceCanvas ? false : isWebGLSupported();
    }
    /**
     * Renders the object to its WebGL view.
     * @param displayObject - The object to be rendered.
     * @param {object} [options] - Object to use for render options.
     * @param {PIXI.RenderTexture} [options.renderTexture] - The render texture to render to.
     * @param {boolean} [options.clear=true] - Should the canvas be cleared before the new render.
     * @param {PIXI.Matrix} [options.transform] - A transform to apply to the render texture before rendering.
     * @param {boolean} [options.skipUpdateTransform=false] - Should we skip the update transform pass?
     */
    render(displayObject, options) {
      this.objectRenderer.render(displayObject, options);
    }
    /**
     * Resizes the WebGL view to the specified width and height.
     * @param desiredScreenWidth - The desired width of the screen.
     * @param desiredScreenHeight - The desired height of the screen.
     */
    resize(desiredScreenWidth, desiredScreenHeight) {
      this._view.resizeView(desiredScreenWidth, desiredScreenHeight);
    }
    /**
     * Resets the WebGL state so you can render things however you fancy!
     * @returns Returns itself.
     */
    reset() {
      return this.runners.reset.emit(), this;
    }
    /** Clear the frame buffer. */
    clear() {
      this.renderTexture.bind(), this.renderTexture.clear();
    }
    /**
     * Removes everything from the renderer (event listeners, spritebatch, etc...)
     * @param [removeView=false] - Removes the Canvas element from the DOM.
     *  See: https://github.com/pixijs/pixijs/issues/2233
     */
    destroy(removeView = false) {
      this.runners.destroy.items.reverse(), this.emitWithCustomOptions(this.runners.destroy, {
        _view: removeView
      }), super.destroy();
    }
    /** Collection of plugins */
    get plugins() {
      return this._plugin.plugins;
    }
    /** The number of msaa samples of the canvas. */
    get multisample() {
      return this._multisample.multisample;
    }
    /**
     * Same as view.width, actual number of pixels in the canvas by horizontal.
     * @member {number}
     * @readonly
     * @default 800
     */
    get width() {
      return this._view.element.width;
    }
    /**
     * Same as view.height, actual number of pixels in the canvas by vertical.
     * @default 600
     */
    get height() {
      return this._view.element.height;
    }
    /** The resolution / device pixel ratio of the renderer. */
    get resolution() {
      return this._view.resolution;
    }
    set resolution(value) {
      this._view.resolution = value, this.runners.resolutionChange.emit(value);
    }
    /** Whether CSS dimensions of canvas view should be resized to screen dimensions automatically. */
    get autoDensity() {
      return this._view.autoDensity;
    }
    /** The canvas element that everything is drawn to.*/
    get view() {
      return this._view.element;
    }
    /**
     * Measurements of the screen. (0, 0, screenWidth, screenHeight).
     *
     * Its safe to use as filterArea or hitArea for the whole stage.
     * @member {PIXI.Rectangle}
     */
    get screen() {
      return this._view.screen;
    }
    /** the last object rendered by the renderer. Useful for other plugins like interaction managers */
    get lastObjectRendered() {
      return this.objectRenderer.lastObjectRendered;
    }
    /** Flag if we are rendering to the screen vs renderTexture */
    get renderingToScreen() {
      return this.objectRenderer.renderingToScreen;
    }
    /** When logging Pixi to the console, this is the name we will show */
    get rendererLogId() {
      return `WebGL ${this.context.webGLVersion}`;
    }
    /**
     * This sets weather the screen is totally cleared between each frame withthe background color and alpha
     * @deprecated since 7.0.0
     */
    get clearBeforeRender() {
      return deprecation("7.0.0", "renderer.clearBeforeRender has been deprecated, please use renderer.background.clearBeforeRender instead."), this.background.clearBeforeRender;
    }
    /**
     * Pass-thru setting for the canvas' context `alpha` property. This is typically
     * not something you need to fiddle with. If you want transparency, use `backgroundAlpha`.
     * @deprecated since 7.0.0
     * @member {boolean}
     */
    get useContextAlpha() {
      return deprecation("7.0.0", "renderer.useContextAlpha has been deprecated, please use renderer.context.premultipliedAlpha instead."), this.context.useContextAlpha;
    }
    /**
     * readonly drawing buffer preservation
     * we can only know this if Pixi created the context
     * @deprecated since 7.0.0
     */
    get preserveDrawingBuffer() {
      return deprecation("7.0.0", "renderer.preserveDrawingBuffer has been deprecated, we cannot truly know this unless pixi created the context"), this.context.preserveDrawingBuffer;
    }
    /**
     * The background color to fill if not transparent
     * @member {number}
     * @deprecated since 7.0.0
     */
    get backgroundColor() {
      return deprecation("7.0.0", "renderer.backgroundColor has been deprecated, use renderer.background.color instead."), this.background.color;
    }
    set backgroundColor(value) {
      deprecation("7.0.0", "renderer.backgroundColor has been deprecated, use renderer.background.color instead."), this.background.color = value;
    }
    /**
     * The background color alpha. Setting this to 0 will make the canvas transparent.
     * @member {number}
     * @deprecated since 7.0.0
     */
    get backgroundAlpha() {
      return deprecation("7.0.0", "renderer.backgroundAlpha has been deprecated, use renderer.background.alpha instead."), this.background.alpha;
    }
    /**
     * @deprecated since 7.0.0
     */
    set backgroundAlpha(value) {
      deprecation("7.0.0", "renderer.backgroundAlpha has been deprecated, use renderer.background.alpha instead."), this.background.alpha = value;
    }
    /**
     * @deprecated since 7.0.0
     */
    get powerPreference() {
      return deprecation("7.0.0", "renderer.powerPreference has been deprecated, we can only know this if pixi creates the context"), this.context.powerPreference;
    }
    /**
     * Useful function that returns a texture of the display object that can then be used to create sprites
     * This can be quite useful if your displayObject is complicated and needs to be reused multiple times.
     * @param displayObject - The displayObject the object will be generated from.
     * @param {IGenerateTextureOptions} options - Generate texture options.
     * @param {PIXI.Rectangle} options.region - The region of the displayObject, that shall be rendered,
     *        if no region is specified, defaults to the local bounds of the displayObject.
     * @param {number} [options.resolution] - If not given, the renderer's resolution is used.
     * @param {PIXI.MSAA_QUALITY} [options.multisample] - If not given, the renderer's multisample is used.
     * @returns A texture of the graphics object.
     */
    generateTexture(displayObject, options) {
      return this.textureGenerator.generateTexture(displayObject, options);
    }
  };
  _Renderer.extension = {
    type: ExtensionType.Renderer,
    priority: 1
  }, /**
  * Collection of installed plugins. These are included by default in PIXI, but can be excluded
  * by creating a custom build. Consult the README for more information about creating custom
  * builds and excluding plugins.
  * @private
  */
  _Renderer.__plugins = {}, /**
  * The collection of installed systems.
  * @private
  */
  _Renderer.__systems = {};
  let Renderer = _Renderer;
  extensions.handleByMap(ExtensionType.RendererPlugin, Renderer.__plugins);
  extensions.handleByMap(ExtensionType.RendererSystem, Renderer.__systems);
  extensions.add(Renderer);
  class AbstractMultiResource extends Resource {
    /**
     * @param length
     * @param options - Options to for Resource constructor
     * @param {number} [options.width] - Width of the resource
     * @param {number} [options.height] - Height of the resource
     */
    constructor(length, options) {
      const { width, height } = options || {};
      super(width, height), this.items = [], this.itemDirtyIds = [];
      for (let i2 = 0; i2 < length; i2++) {
        const partTexture = new BaseTexture();
        this.items.push(partTexture), this.itemDirtyIds.push(-2);
      }
      this.length = length, this._load = null, this.baseTexture = null;
    }
    /**
     * Used from ArrayResource and CubeResource constructors.
     * @param resources - Can be resources, image elements, canvas, etc. ,
     *  length should be same as constructor length
     * @param options - Detect options for resources
     */
    initFromArray(resources, options) {
      for (let i2 = 0; i2 < this.length; i2++)
        resources[i2] && (resources[i2].castToBaseTexture ? this.addBaseTextureAt(resources[i2].castToBaseTexture(), i2) : resources[i2] instanceof Resource ? this.addResourceAt(resources[i2], i2) : this.addResourceAt(autoDetectResource(resources[i2], options), i2));
    }
    /** Destroy this BaseImageResource. */
    dispose() {
      for (let i2 = 0, len = this.length; i2 < len; i2++)
        this.items[i2].destroy();
      this.items = null, this.itemDirtyIds = null, this._load = null;
    }
    /**
     * Set a resource by ID
     * @param resource
     * @param index - Zero-based index of resource to set
     * @returns - Instance for chaining
     */
    addResourceAt(resource, index) {
      if (!this.items[index])
        throw new Error(`Index ${index} is out of bounds`);
      return resource.valid && !this.valid && this.resize(resource.width, resource.height), this.items[index].setResource(resource), this;
    }
    /**
     * Set the parent base texture.
     * @param baseTexture
     */
    bind(baseTexture) {
      if (this.baseTexture !== null)
        throw new Error("Only one base texture per TextureArray is allowed");
      super.bind(baseTexture);
      for (let i2 = 0; i2 < this.length; i2++)
        this.items[i2].parentTextureArray = baseTexture, this.items[i2].on("update", baseTexture.update, baseTexture);
    }
    /**
     * Unset the parent base texture.
     * @param baseTexture
     */
    unbind(baseTexture) {
      super.unbind(baseTexture);
      for (let i2 = 0; i2 < this.length; i2++)
        this.items[i2].parentTextureArray = null, this.items[i2].off("update", baseTexture.update, baseTexture);
    }
    /**
     * Load all the resources simultaneously
     * @returns - When load is resolved
     */
    load() {
      if (this._load)
        return this._load;
      const promises = this.items.map((item) => item.resource).filter((item) => item).map((item) => item.load());
      return this._load = Promise.all(promises).then(
        () => {
          const { realWidth, realHeight } = this.items[0];
          return this.resize(realWidth, realHeight), this.update(), Promise.resolve(this);
        }
      ), this._load;
    }
  }
  class ArrayResource extends AbstractMultiResource {
    /**
     * @param source - Number of items in array or the collection
     *        of image URLs to use. Can also be resources, image elements, canvas, etc.
     * @param options - Options to apply to {@link PIXI.autoDetectResource}
     * @param {number} [options.width] - Width of the resource
     * @param {number} [options.height] - Height of the resource
     */
    constructor(source, options) {
      const { width, height } = options || {};
      let urls, length;
      Array.isArray(source) ? (urls = source, length = source.length) : length = source, super(length, { width, height }), urls && this.initFromArray(urls, options);
    }
    /**
     * Set a baseTexture by ID,
     * ArrayResource just takes resource from it, nothing more
     * @param baseTexture
     * @param index - Zero-based index of resource to set
     * @returns - Instance for chaining
     */
    addBaseTextureAt(baseTexture, index) {
      if (baseTexture.resource)
        this.addResourceAt(baseTexture.resource, index);
      else
        throw new Error("ArrayResource does not support RenderTexture");
      return this;
    }
    /**
     * Add binding
     * @param baseTexture
     */
    bind(baseTexture) {
      super.bind(baseTexture), baseTexture.target = TARGETS.TEXTURE_2D_ARRAY;
    }
    /**
     * Upload the resources to the GPU.
     * @param renderer
     * @param texture
     * @param glTexture
     * @returns - whether texture was uploaded
     */
    upload(renderer, texture, glTexture) {
      const { length, itemDirtyIds, items } = this, { gl } = renderer;
      glTexture.dirtyId < 0 && gl.texImage3D(
        gl.TEXTURE_2D_ARRAY,
        0,
        glTexture.internalFormat,
        this._width,
        this._height,
        length,
        0,
        texture.format,
        glTexture.type,
        null
      );
      for (let i2 = 0; i2 < length; i2++) {
        const item = items[i2];
        itemDirtyIds[i2] < item.dirtyId && (itemDirtyIds[i2] = item.dirtyId, item.valid && gl.texSubImage3D(
          gl.TEXTURE_2D_ARRAY,
          0,
          0,
          // xoffset
          0,
          // yoffset
          i2,
          // zoffset
          item.resource.width,
          item.resource.height,
          1,
          texture.format,
          glTexture.type,
          item.resource.source
        ));
      }
      return true;
    }
  }
  class CanvasResource extends BaseImageResource {
    /**
     * @param source - Canvas element to use
     */
    // eslint-disable-next-line @typescript-eslint/no-useless-constructor
    constructor(source) {
      super(source);
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @returns {boolean} `true` if source is HTMLCanvasElement or OffscreenCanvas
     */
    static test(source) {
      const { OffscreenCanvas: OffscreenCanvas2 } = globalThis;
      return OffscreenCanvas2 && source instanceof OffscreenCanvas2 ? true : globalThis.HTMLCanvasElement && source instanceof HTMLCanvasElement;
    }
  }
  const _CubeResource = class _CubeResource2 extends AbstractMultiResource {
    /**
     * @param {Array<string|PIXI.Resource>} [source] - Collection of URLs or resources
     *        to use as the sides of the cube.
     * @param options - ImageResource options
     * @param {number} [options.width] - Width of resource
     * @param {number} [options.height] - Height of resource
     * @param {number} [options.autoLoad=true] - Whether to auto-load resources
     * @param {number} [options.linkBaseTexture=true] - In case BaseTextures are supplied,
     *   whether to copy them or use
     */
    constructor(source, options) {
      const { width, height, autoLoad, linkBaseTexture } = options || {};
      if (source && source.length !== _CubeResource2.SIDES)
        throw new Error(`Invalid length. Got ${source.length}, expected 6`);
      super(6, { width, height });
      for (let i2 = 0; i2 < _CubeResource2.SIDES; i2++)
        this.items[i2].target = TARGETS.TEXTURE_CUBE_MAP_POSITIVE_X + i2;
      this.linkBaseTexture = linkBaseTexture !== false, source && this.initFromArray(source, options), autoLoad !== false && this.load();
    }
    /**
     * Add binding.
     * @param baseTexture - parent base texture
     */
    bind(baseTexture) {
      super.bind(baseTexture), baseTexture.target = TARGETS.TEXTURE_CUBE_MAP;
    }
    addBaseTextureAt(baseTexture, index, linkBaseTexture) {
      if (linkBaseTexture === void 0 && (linkBaseTexture = this.linkBaseTexture), !this.items[index])
        throw new Error(`Index ${index} is out of bounds`);
      if (!this.linkBaseTexture || baseTexture.parentTextureArray || Object.keys(baseTexture._glTextures).length > 0)
        if (baseTexture.resource)
          this.addResourceAt(baseTexture.resource, index);
        else
          throw new Error("CubeResource does not support copying of renderTexture.");
      else
        baseTexture.target = TARGETS.TEXTURE_CUBE_MAP_POSITIVE_X + index, baseTexture.parentTextureArray = this.baseTexture, this.items[index] = baseTexture;
      return baseTexture.valid && !this.valid && this.resize(baseTexture.realWidth, baseTexture.realHeight), this.items[index] = baseTexture, this;
    }
    /**
     * Upload the resource
     * @param renderer
     * @param _baseTexture
     * @param glTexture
     * @returns {boolean} true is success
     */
    upload(renderer, _baseTexture, glTexture) {
      const dirty = this.itemDirtyIds;
      for (let i2 = 0; i2 < _CubeResource2.SIDES; i2++) {
        const side = this.items[i2];
        (dirty[i2] < side.dirtyId || glTexture.dirtyId < _baseTexture.dirtyId) && (side.valid && side.resource ? (side.resource.upload(renderer, side, glTexture), dirty[i2] = side.dirtyId) : dirty[i2] < -1 && (renderer.gl.texImage2D(
          side.target,
          0,
          glTexture.internalFormat,
          _baseTexture.realWidth,
          _baseTexture.realHeight,
          0,
          _baseTexture.format,
          glTexture.type,
          null
        ), dirty[i2] = -1));
      }
      return true;
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @returns {boolean} `true` if source is an array of 6 elements
     */
    static test(source) {
      return Array.isArray(source) && source.length === _CubeResource2.SIDES;
    }
  };
  _CubeResource.SIDES = 6;
  let CubeResource = _CubeResource;
  class ImageBitmapResource extends BaseImageResource {
    /**
     * @param source - ImageBitmap or URL to use.
     * @param options - Options to use.
     */
    constructor(source, options) {
      options = options || {};
      let baseSource, url2, ownsImageBitmap;
      typeof source == "string" ? (baseSource = ImageBitmapResource.EMPTY, url2 = source, ownsImageBitmap = true) : (baseSource = source, url2 = null, ownsImageBitmap = false), super(baseSource), this.url = url2, this.crossOrigin = options.crossOrigin ?? true, this.alphaMode = typeof options.alphaMode == "number" ? options.alphaMode : null, this.ownsImageBitmap = options.ownsImageBitmap ?? ownsImageBitmap, this._load = null, options.autoLoad !== false && this.load();
    }
    load() {
      return this._load ? this._load : (this._load = new Promise(async (resolve, reject) => {
        if (this.url === null) {
          resolve(this);
          return;
        }
        try {
          const response = await settings.ADAPTER.fetch(this.url, {
            mode: this.crossOrigin ? "cors" : "no-cors"
          });
          if (this.destroyed)
            return;
          const imageBlob = await response.blob();
          if (this.destroyed)
            return;
          const imageBitmap = await createImageBitmap(imageBlob, {
            premultiplyAlpha: this.alphaMode === null || this.alphaMode === ALPHA_MODES.UNPACK ? "premultiply" : "none"
          });
          if (this.destroyed) {
            imageBitmap.close();
            return;
          }
          this.source = imageBitmap, this.update(), resolve(this);
        } catch (e2) {
          if (this.destroyed)
            return;
          reject(e2), this.onError.emit(e2);
        }
      }), this._load);
    }
    /**
     * Upload the image bitmap resource to GPU.
     * @param renderer - Renderer to upload to
     * @param baseTexture - BaseTexture for this resource
     * @param glTexture - GLTexture to use
     * @returns {boolean} true is success
     */
    upload(renderer, baseTexture, glTexture) {
      return this.source instanceof ImageBitmap ? (typeof this.alphaMode == "number" && (baseTexture.alphaMode = this.alphaMode), super.upload(renderer, baseTexture, glTexture)) : (this.load(), false);
    }
    /** Destroys this resource. */
    dispose() {
      this.ownsImageBitmap && this.source instanceof ImageBitmap && this.source.close(), super.dispose(), this._load = null;
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @returns {boolean} `true` if current environment support ImageBitmap, and source is string or ImageBitmap
     */
    static test(source) {
      return !!globalThis.createImageBitmap && typeof ImageBitmap < "u" && (typeof source == "string" || source instanceof ImageBitmap);
    }
    /**
     * ImageBitmap cannot be created synchronously, so a empty placeholder canvas is needed when loading from URLs.
     * Only for internal usage.
     * @returns The cached placeholder canvas.
     */
    static get EMPTY() {
      return ImageBitmapResource._EMPTY = ImageBitmapResource._EMPTY ?? settings.ADAPTER.createCanvas(0, 0), ImageBitmapResource._EMPTY;
    }
  }
  const _SVGResource = class _SVGResource2 extends BaseImageResource {
    /**
     * @param sourceBase64 - Base64 encoded SVG element or URL for SVG file.
     * @param {object} [options] - Options to use
     * @param {number} [options.scale=1] - Scale to apply to SVG. Overridden by...
     * @param {number} [options.width] - Rasterize SVG this wide. Aspect ratio preserved if height not specified.
     * @param {number} [options.height] - Rasterize SVG this high. Aspect ratio preserved if width not specified.
     * @param {boolean} [options.autoLoad=true] - Start loading right away.
     */
    constructor(sourceBase64, options) {
      options = options || {}, super(settings.ADAPTER.createCanvas()), this._width = 0, this._height = 0, this.svg = sourceBase64, this.scale = options.scale || 1, this._overrideWidth = options.width, this._overrideHeight = options.height, this._resolve = null, this._crossorigin = options.crossorigin, this._load = null, options.autoLoad !== false && this.load();
    }
    load() {
      return this._load ? this._load : (this._load = new Promise((resolve) => {
        if (this._resolve = () => {
          this.update(), resolve(this);
        }, _SVGResource2.SVG_XML.test(this.svg.trim())) {
          if (!btoa)
            throw new Error("Your browser doesn't support base64 conversions.");
          this.svg = `data:image/svg+xml;base64,${btoa(unescape(encodeURIComponent(this.svg)))}`;
        }
        this._loadSvg();
      }), this._load);
    }
    /** Loads an SVG image from `imageUrl` or `data URL`. */
    _loadSvg() {
      const tempImage = new Image();
      BaseImageResource.crossOrigin(tempImage, this.svg, this._crossorigin), tempImage.src = this.svg, tempImage.onerror = (event) => {
        this._resolve && (tempImage.onerror = null, this.onError.emit(event));
      }, tempImage.onload = () => {
        if (!this._resolve)
          return;
        const svgWidth = tempImage.width, svgHeight = tempImage.height;
        if (!svgWidth || !svgHeight)
          throw new Error("The SVG image must have width and height defined (in pixels), canvas API needs them.");
        let width = svgWidth * this.scale, height = svgHeight * this.scale;
        (this._overrideWidth || this._overrideHeight) && (width = this._overrideWidth || this._overrideHeight / svgHeight * svgWidth, height = this._overrideHeight || this._overrideWidth / svgWidth * svgHeight), width = Math.round(width), height = Math.round(height);
        const canvas = this.source;
        canvas.width = width, canvas.height = height, canvas._pixiId = `canvas_${uid()}`, canvas.getContext("2d").drawImage(tempImage, 0, 0, svgWidth, svgHeight, 0, 0, width, height), this._resolve(), this._resolve = null;
      };
    }
    /**
     * Get size from an svg string using a regular expression.
     * @param svgString - a serialized svg element
     * @returns - image extension
     */
    static getSize(svgString) {
      const sizeMatch = _SVGResource2.SVG_SIZE.exec(svgString), size = {};
      return sizeMatch && (size[sizeMatch[1]] = Math.round(parseFloat(sizeMatch[3])), size[sizeMatch[5]] = Math.round(parseFloat(sizeMatch[7]))), size;
    }
    /** Destroys this texture. */
    dispose() {
      super.dispose(), this._resolve = null, this._crossorigin = null;
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @param {string} extension - The extension of source, if set
     * @returns {boolean} - If the source is a SVG source or data file
     */
    static test(source, extension) {
      return extension === "svg" || typeof source == "string" && source.startsWith("data:image/svg+xml") || typeof source == "string" && _SVGResource2.SVG_XML.test(source);
    }
    // eslint-disable-line max-len
  };
  _SVGResource.SVG_XML = /^(<\?xml[^?]+\?>)?\s*(<!--[^(-->)]*-->)?\s*\<svg/m, /**
  * Regular expression for SVG size.
  * @example &lt;svg width="100" height="100"&gt;&lt;/svg&gt;
  * @readonly
  */
  _SVGResource.SVG_SIZE = /<svg[^>]*(?:\s(width|height)=('|")(\d*(?:\.\d+)?)(?:px)?('|"))[^>]*(?:\s(width|height)=('|")(\d*(?:\.\d+)?)(?:px)?('|"))[^>]*>/i;
  let SVGResource = _SVGResource;
  class VideoFrameResource extends BaseImageResource {
    /**
     * @param source - Image element to use
     */
    // eslint-disable-next-line @typescript-eslint/no-useless-constructor
    constructor(source) {
      super(source);
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @returns {boolean} `true` if source is an VideoFrame
     */
    static test(source) {
      return !!globalThis.VideoFrame && source instanceof globalThis.VideoFrame;
    }
  }
  const _VideoResource = class _VideoResource2 extends BaseImageResource {
    /**
     * @param {HTMLVideoElement|object|string|Array<string|object>} source - Video element to use.
     * @param {object} [options] - Options to use
     * @param {boolean} [options.autoLoad=true] - Start loading the video immediately
     * @param {boolean} [options.autoPlay=true] - Start playing video immediately
     * @param {number} [options.updateFPS=0] - How many times a second to update the texture from the video.
     * If 0, `requestVideoFrameCallback` is used to update the texture.
     * If `requestVideoFrameCallback` is not available, the texture is updated every render.
     * @param {boolean} [options.crossorigin=true] - Load image using cross origin
     * @param {boolean} [options.loop=false] - Loops the video
     * @param {boolean} [options.muted=false] - Mutes the video audio, useful for autoplay
     * @param {boolean} [options.playsinline=true] - Prevents opening the video on mobile devices
     */
    constructor(source, options) {
      if (options = options || {}, !(source instanceof HTMLVideoElement)) {
        const videoElement = document.createElement("video");
        options.autoLoad !== false && videoElement.setAttribute("preload", "auto"), options.playsinline !== false && (videoElement.setAttribute("webkit-playsinline", ""), videoElement.setAttribute("playsinline", "")), options.muted === true && (videoElement.setAttribute("muted", ""), videoElement.muted = true), options.loop === true && videoElement.setAttribute("loop", ""), options.autoPlay !== false && videoElement.setAttribute("autoplay", ""), typeof source == "string" && (source = [source]);
        const firstSrc = source[0].src || source[0];
        BaseImageResource.crossOrigin(videoElement, firstSrc, options.crossorigin);
        for (let i2 = 0; i2 < source.length; ++i2) {
          const sourceElement = document.createElement("source");
          let { src, mime } = source[i2];
          if (src = src || source[i2], src.startsWith("data:"))
            mime = src.slice(5, src.indexOf(";"));
          else if (!src.startsWith("blob:")) {
            const baseSrc = src.split("?").shift().toLowerCase(), ext = baseSrc.slice(baseSrc.lastIndexOf(".") + 1);
            mime = mime || _VideoResource2.MIME_TYPES[ext] || `video/${ext}`;
          }
          sourceElement.src = src, mime && (sourceElement.type = mime), videoElement.appendChild(sourceElement);
        }
        source = videoElement;
      }
      super(source), this.noSubImage = true, this._autoUpdate = true, this._isConnectedToTicker = false, this._updateFPS = options.updateFPS || 0, this._msToNextUpdate = 0, this.autoPlay = options.autoPlay !== false, this._videoFrameRequestCallback = this._videoFrameRequestCallback.bind(this), this._videoFrameRequestCallbackHandle = null, this._load = null, this._resolve = null, this._reject = null, this._onCanPlay = this._onCanPlay.bind(this), this._onError = this._onError.bind(this), this._onPlayStart = this._onPlayStart.bind(this), this._onPlayStop = this._onPlayStop.bind(this), this._onSeeked = this._onSeeked.bind(this), options.autoLoad !== false && this.load();
    }
    /**
     * Trigger updating of the texture.
     * @param _deltaTime - time delta since last tick
     */
    update(_deltaTime = 0) {
      if (!this.destroyed) {
        if (this._updateFPS) {
          const elapsedMS = Ticker.shared.elapsedMS * this.source.playbackRate;
          this._msToNextUpdate = Math.floor(this._msToNextUpdate - elapsedMS);
        }
        (!this._updateFPS || this._msToNextUpdate <= 0) && (super.update(
          /* deltaTime*/
        ), this._msToNextUpdate = this._updateFPS ? Math.floor(1e3 / this._updateFPS) : 0);
      }
    }
    _videoFrameRequestCallback() {
      this.update(), this.destroyed ? this._videoFrameRequestCallbackHandle = null : this._videoFrameRequestCallbackHandle = this.source.requestVideoFrameCallback(
        this._videoFrameRequestCallback
      );
    }
    /**
     * Start preloading the video resource.
     * @returns {Promise<void>} Handle the validate event
     */
    load() {
      if (this._load)
        return this._load;
      const source = this.source;
      return (source.readyState === source.HAVE_ENOUGH_DATA || source.readyState === source.HAVE_FUTURE_DATA) && source.width && source.height && (source.complete = true), source.addEventListener("play", this._onPlayStart), source.addEventListener("pause", this._onPlayStop), source.addEventListener("seeked", this._onSeeked), this._isSourceReady() ? this._onCanPlay() : (source.addEventListener("canplay", this._onCanPlay), source.addEventListener("canplaythrough", this._onCanPlay), source.addEventListener("error", this._onError, true)), this._load = new Promise((resolve, reject) => {
        this.valid ? resolve(this) : (this._resolve = resolve, this._reject = reject, source.load());
      }), this._load;
    }
    /**
     * Handle video error events.
     * @param event
     */
    _onError(event) {
      this.source.removeEventListener("error", this._onError, true), this.onError.emit(event), this._reject && (this._reject(event), this._reject = null, this._resolve = null);
    }
    /**
     * Returns true if the underlying source is playing.
     * @returns - True if playing.
     */
    _isSourcePlaying() {
      const source = this.source;
      return !source.paused && !source.ended;
    }
    /**
     * Returns true if the underlying source is ready for playing.
     * @returns - True if ready.
     */
    _isSourceReady() {
      return this.source.readyState > 2;
    }
    /** Runs the update loop when the video is ready to play. */
    _onPlayStart() {
      this.valid || this._onCanPlay(), this._configureAutoUpdate();
    }
    /** Fired when a pause event is triggered, stops the update loop. */
    _onPlayStop() {
      this._configureAutoUpdate();
    }
    /** Fired when the video is completed seeking to the current playback position. */
    _onSeeked() {
      this._autoUpdate && !this._isSourcePlaying() && (this._msToNextUpdate = 0, this.update(), this._msToNextUpdate = 0);
    }
    /** Fired when the video is loaded and ready to play. */
    _onCanPlay() {
      const source = this.source;
      source.removeEventListener("canplay", this._onCanPlay), source.removeEventListener("canplaythrough", this._onCanPlay);
      const valid = this.valid;
      this._msToNextUpdate = 0, this.update(), this._msToNextUpdate = 0, !valid && this._resolve && (this._resolve(this), this._resolve = null, this._reject = null), this._isSourcePlaying() ? this._onPlayStart() : this.autoPlay && source.play();
    }
    /** Destroys this texture. */
    dispose() {
      this._configureAutoUpdate();
      const source = this.source;
      source && (source.removeEventListener("play", this._onPlayStart), source.removeEventListener("pause", this._onPlayStop), source.removeEventListener("seeked", this._onSeeked), source.removeEventListener("canplay", this._onCanPlay), source.removeEventListener("canplaythrough", this._onCanPlay), source.removeEventListener("error", this._onError, true), source.pause(), source.src = "", source.load()), super.dispose();
    }
    /** Should the base texture automatically update itself, set to true by default. */
    get autoUpdate() {
      return this._autoUpdate;
    }
    set autoUpdate(value) {
      value !== this._autoUpdate && (this._autoUpdate = value, this._configureAutoUpdate());
    }
    /**
     * How many times a second to update the texture from the video. If 0, `requestVideoFrameCallback` is used to
     * update the texture. If `requestVideoFrameCallback` is not available, the texture is updated every render.
     * A lower fps can help performance, as updating the texture at 60fps on a 30ps video may not be efficient.
     */
    get updateFPS() {
      return this._updateFPS;
    }
    set updateFPS(value) {
      value !== this._updateFPS && (this._updateFPS = value, this._configureAutoUpdate());
    }
    _configureAutoUpdate() {
      this._autoUpdate && this._isSourcePlaying() ? !this._updateFPS && this.source.requestVideoFrameCallback ? (this._isConnectedToTicker && (Ticker.shared.remove(this.update, this), this._isConnectedToTicker = false, this._msToNextUpdate = 0), this._videoFrameRequestCallbackHandle === null && (this._videoFrameRequestCallbackHandle = this.source.requestVideoFrameCallback(
        this._videoFrameRequestCallback
      ))) : (this._videoFrameRequestCallbackHandle !== null && (this.source.cancelVideoFrameCallback(this._videoFrameRequestCallbackHandle), this._videoFrameRequestCallbackHandle = null), this._isConnectedToTicker || (Ticker.shared.add(this.update, this), this._isConnectedToTicker = true, this._msToNextUpdate = 0)) : (this._videoFrameRequestCallbackHandle !== null && (this.source.cancelVideoFrameCallback(this._videoFrameRequestCallbackHandle), this._videoFrameRequestCallbackHandle = null), this._isConnectedToTicker && (Ticker.shared.remove(this.update, this), this._isConnectedToTicker = false, this._msToNextUpdate = 0));
    }
    /**
     * Used to auto-detect the type of resource.
     * @param {*} source - The source object
     * @param {string} extension - The extension of source, if set
     * @returns {boolean} `true` if video source
     */
    static test(source, extension) {
      return globalThis.HTMLVideoElement && source instanceof HTMLVideoElement || _VideoResource2.TYPES.includes(extension);
    }
  };
  _VideoResource.TYPES = ["mp4", "m4v", "webm", "ogg", "ogv", "h264", "avi", "mov"], /**
  * Map of video MIME types that can't be directly derived from file extensions.
  * @readonly
  */
  _VideoResource.MIME_TYPES = {
    ogv: "video/ogg",
    mov: "video/quicktime",
    m4v: "video/mp4"
  };
  let VideoResource = _VideoResource;
  INSTALLED.push(
    ImageBitmapResource,
    ImageResource,
    CanvasResource,
    VideoResource,
    VideoFrameResource,
    SVGResource,
    BufferResource,
    CubeResource,
    ArrayResource
  );
  class Bounds {
    constructor() {
      this.minX = 1 / 0, this.minY = 1 / 0, this.maxX = -1 / 0, this.maxY = -1 / 0, this.rect = null, this.updateID = -1;
    }
    /**
     * Checks if bounds are empty.
     * @returns - True if empty.
     */
    isEmpty() {
      return this.minX > this.maxX || this.minY > this.maxY;
    }
    /** Clears the bounds and resets. */
    clear() {
      this.minX = 1 / 0, this.minY = 1 / 0, this.maxX = -1 / 0, this.maxY = -1 / 0;
    }
    /**
     * Can return Rectangle.EMPTY constant, either construct new rectangle, either use your rectangle
     * It is not guaranteed that it will return tempRect
     * @param rect - Temporary object will be used if AABB is not empty
     * @returns - A rectangle of the bounds
     */
    getRectangle(rect) {
      return this.minX > this.maxX || this.minY > this.maxY ? Rectangle.EMPTY : (rect = rect || new Rectangle(0, 0, 1, 1), rect.x = this.minX, rect.y = this.minY, rect.width = this.maxX - this.minX, rect.height = this.maxY - this.minY, rect);
    }
    /**
     * This function should be inlined when its possible.
     * @param point - The point to add.
     */
    addPoint(point) {
      this.minX = Math.min(this.minX, point.x), this.maxX = Math.max(this.maxX, point.x), this.minY = Math.min(this.minY, point.y), this.maxY = Math.max(this.maxY, point.y);
    }
    /**
     * Adds a point, after transformed. This should be inlined when its possible.
     * @param matrix
     * @param point
     */
    addPointMatrix(matrix, point) {
      const { a: a2, b: b2, c: c2, d: d2, tx, ty } = matrix, x2 = a2 * point.x + c2 * point.y + tx, y2 = b2 * point.x + d2 * point.y + ty;
      this.minX = Math.min(this.minX, x2), this.maxX = Math.max(this.maxX, x2), this.minY = Math.min(this.minY, y2), this.maxY = Math.max(this.maxY, y2);
    }
    /**
     * Adds a quad, not transformed
     * @param vertices - The verts to add.
     */
    addQuad(vertices) {
      let minX = this.minX, minY = this.minY, maxX = this.maxX, maxY = this.maxY, x2 = vertices[0], y2 = vertices[1];
      minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, x2 = vertices[2], y2 = vertices[3], minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, x2 = vertices[4], y2 = vertices[5], minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, x2 = vertices[6], y2 = vertices[7], minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, this.minX = minX, this.minY = minY, this.maxX = maxX, this.maxY = maxY;
    }
    /**
     * Adds sprite frame, transformed.
     * @param transform - transform to apply
     * @param x0 - left X of frame
     * @param y0 - top Y of frame
     * @param x1 - right X of frame
     * @param y1 - bottom Y of frame
     */
    addFrame(transform, x0, y0, x1, y1) {
      this.addFrameMatrix(transform.worldTransform, x0, y0, x1, y1);
    }
    /**
     * Adds sprite frame, multiplied by matrix
     * @param matrix - matrix to apply
     * @param x0 - left X of frame
     * @param y0 - top Y of frame
     * @param x1 - right X of frame
     * @param y1 - bottom Y of frame
     */
    addFrameMatrix(matrix, x0, y0, x1, y1) {
      const a2 = matrix.a, b2 = matrix.b, c2 = matrix.c, d2 = matrix.d, tx = matrix.tx, ty = matrix.ty;
      let minX = this.minX, minY = this.minY, maxX = this.maxX, maxY = this.maxY, x2 = a2 * x0 + c2 * y0 + tx, y2 = b2 * x0 + d2 * y0 + ty;
      minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, x2 = a2 * x1 + c2 * y0 + tx, y2 = b2 * x1 + d2 * y0 + ty, minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, x2 = a2 * x0 + c2 * y1 + tx, y2 = b2 * x0 + d2 * y1 + ty, minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, x2 = a2 * x1 + c2 * y1 + tx, y2 = b2 * x1 + d2 * y1 + ty, minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY, this.minX = minX, this.minY = minY, this.maxX = maxX, this.maxY = maxY;
    }
    /**
     * Adds screen vertices from array
     * @param vertexData - calculated vertices
     * @param beginOffset - begin offset
     * @param endOffset - end offset, excluded
     */
    addVertexData(vertexData, beginOffset, endOffset) {
      let minX = this.minX, minY = this.minY, maxX = this.maxX, maxY = this.maxY;
      for (let i2 = beginOffset; i2 < endOffset; i2 += 2) {
        const x2 = vertexData[i2], y2 = vertexData[i2 + 1];
        minX = x2 < minX ? x2 : minX, minY = y2 < minY ? y2 : minY, maxX = x2 > maxX ? x2 : maxX, maxY = y2 > maxY ? y2 : maxY;
      }
      this.minX = minX, this.minY = minY, this.maxX = maxX, this.maxY = maxY;
    }
    /**
     * Add an array of mesh vertices
     * @param transform - mesh transform
     * @param vertices - mesh coordinates in array
     * @param beginOffset - begin offset
     * @param endOffset - end offset, excluded
     */
    addVertices(transform, vertices, beginOffset, endOffset) {
      this.addVerticesMatrix(transform.worldTransform, vertices, beginOffset, endOffset);
    }
    /**
     * Add an array of mesh vertices.
     * @param matrix - mesh matrix
     * @param vertices - mesh coordinates in array
     * @param beginOffset - begin offset
     * @param endOffset - end offset, excluded
     * @param padX - x padding
     * @param padY - y padding
     */
    addVerticesMatrix(matrix, vertices, beginOffset, endOffset, padX = 0, padY = padX) {
      const a2 = matrix.a, b2 = matrix.b, c2 = matrix.c, d2 = matrix.d, tx = matrix.tx, ty = matrix.ty;
      let minX = this.minX, minY = this.minY, maxX = this.maxX, maxY = this.maxY;
      for (let i2 = beginOffset; i2 < endOffset; i2 += 2) {
        const rawX = vertices[i2], rawY = vertices[i2 + 1], x2 = a2 * rawX + c2 * rawY + tx, y2 = d2 * rawY + b2 * rawX + ty;
        minX = Math.min(minX, x2 - padX), maxX = Math.max(maxX, x2 + padX), minY = Math.min(minY, y2 - padY), maxY = Math.max(maxY, y2 + padY);
      }
      this.minX = minX, this.minY = minY, this.maxX = maxX, this.maxY = maxY;
    }
    /**
     * Adds other {@link PIXI.Bounds}.
     * @param bounds - The Bounds to be added
     */
    addBounds(bounds) {
      const minX = this.minX, minY = this.minY, maxX = this.maxX, maxY = this.maxY;
      this.minX = bounds.minX < minX ? bounds.minX : minX, this.minY = bounds.minY < minY ? bounds.minY : minY, this.maxX = bounds.maxX > maxX ? bounds.maxX : maxX, this.maxY = bounds.maxY > maxY ? bounds.maxY : maxY;
    }
    /**
     * Adds other Bounds, masked with Bounds.
     * @param bounds - The Bounds to be added.
     * @param mask - TODO
     */
    addBoundsMask(bounds, mask) {
      const _minX = bounds.minX > mask.minX ? bounds.minX : mask.minX, _minY = bounds.minY > mask.minY ? bounds.minY : mask.minY, _maxX = bounds.maxX < mask.maxX ? bounds.maxX : mask.maxX, _maxY = bounds.maxY < mask.maxY ? bounds.maxY : mask.maxY;
      if (_minX <= _maxX && _minY <= _maxY) {
        const minX = this.minX, minY = this.minY, maxX = this.maxX, maxY = this.maxY;
        this.minX = _minX < minX ? _minX : minX, this.minY = _minY < minY ? _minY : minY, this.maxX = _maxX > maxX ? _maxX : maxX, this.maxY = _maxY > maxY ? _maxY : maxY;
      }
    }
    /**
     * Adds other Bounds, multiplied by matrix. Bounds shouldn't be empty.
     * @param bounds - other bounds
     * @param matrix - multiplicator
     */
    addBoundsMatrix(bounds, matrix) {
      this.addFrameMatrix(matrix, bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);
    }
    /**
     * Adds other Bounds, masked with Rectangle.
     * @param bounds - TODO
     * @param area - TODO
     */
    addBoundsArea(bounds, area) {
      const _minX = bounds.minX > area.x ? bounds.minX : area.x, _minY = bounds.minY > area.y ? bounds.minY : area.y, _maxX = bounds.maxX < area.x + area.width ? bounds.maxX : area.x + area.width, _maxY = bounds.maxY < area.y + area.height ? bounds.maxY : area.y + area.height;
      if (_minX <= _maxX && _minY <= _maxY) {
        const minX = this.minX, minY = this.minY, maxX = this.maxX, maxY = this.maxY;
        this.minX = _minX < minX ? _minX : minX, this.minY = _minY < minY ? _minY : minY, this.maxX = _maxX > maxX ? _maxX : maxX, this.maxY = _maxY > maxY ? _maxY : maxY;
      }
    }
    /**
     * Pads bounds object, making it grow in all directions.
     * If paddingY is omitted, both paddingX and paddingY will be set to paddingX.
     * @param paddingX - The horizontal padding amount.
     * @param paddingY - The vertical padding amount.
     */
    pad(paddingX = 0, paddingY = paddingX) {
      this.isEmpty() || (this.minX -= paddingX, this.maxX += paddingX, this.minY -= paddingY, this.maxY += paddingY);
    }
    /**
     * Adds padded frame. (x0, y0) should be strictly less than (x1, y1)
     * @param x0 - left X of frame
     * @param y0 - top Y of frame
     * @param x1 - right X of frame
     * @param y1 - bottom Y of frame
     * @param padX - padding X
     * @param padY - padding Y
     */
    addFramePad(x0, y0, x1, y1, padX, padY) {
      x0 -= padX, y0 -= padY, x1 += padX, y1 += padY, this.minX = this.minX < x0 ? this.minX : x0, this.maxX = this.maxX > x1 ? this.maxX : x1, this.minY = this.minY < y0 ? this.minY : y0, this.maxY = this.maxY > y1 ? this.maxY : y1;
    }
  }
  class DisplayObject extends EventEmitter {
    constructor() {
      super(), this.tempDisplayObjectParent = null, this.transform = new Transform(), this.alpha = 1, this.visible = true, this.renderable = true, this.cullable = false, this.cullArea = null, this.parent = null, this.worldAlpha = 1, this._lastSortedIndex = 0, this._zIndex = 0, this.filterArea = null, this.filters = null, this._enabledFilters = null, this._bounds = new Bounds(), this._localBounds = null, this._boundsID = 0, this._boundsRect = null, this._localBoundsRect = null, this._mask = null, this._maskRefCount = 0, this._destroyed = false, this.isSprite = false, this.isMask = false;
    }
    /**
     * Mixes all enumerable properties and methods from a source object to DisplayObject.
     * @param source - The source of properties and methods to mix in.
     */
    static mixin(source) {
      const keys = Object.keys(source);
      for (let i2 = 0; i2 < keys.length; ++i2) {
        const propertyName = keys[i2];
        Object.defineProperty(
          DisplayObject.prototype,
          propertyName,
          Object.getOwnPropertyDescriptor(source, propertyName)
        );
      }
    }
    /**
     * Fired when this DisplayObject is added to a Container.
     * @instance
     * @event added
     * @param {PIXI.Container} container - The container added to.
     */
    /**
     * Fired when this DisplayObject is removed from a Container.
     * @instance
     * @event removed
     * @param {PIXI.Container} container - The container removed from.
     */
    /**
     * Fired when this DisplayObject is destroyed. This event is emitted once
     * destroy is finished.
     * @instance
     * @event destroyed
     */
    /** Readonly flag for destroyed display objects. */
    get destroyed() {
      return this._destroyed;
    }
    /** Recursively updates transform of all objects from the root to this one internal function for toLocal() */
    _recursivePostUpdateTransform() {
      this.parent ? (this.parent._recursivePostUpdateTransform(), this.transform.updateTransform(this.parent.transform)) : this.transform.updateTransform(this._tempDisplayObjectParent.transform);
    }
    /** Updates the object transform for rendering. TODO - Optimization pass! */
    updateTransform() {
      this._boundsID++, this.transform.updateTransform(this.parent.transform), this.worldAlpha = this.alpha * this.parent.worldAlpha;
    }
    /**
     * Calculates and returns the (world) bounds of the display object as a [Rectangle]{@link PIXI.Rectangle}.
     *
     * This method is expensive on containers with a large subtree (like the stage). This is because the bounds
     * of a container depend on its children's bounds, which recursively causes all bounds in the subtree to
     * be recalculated. The upside, however, is that calling `getBounds` once on a container will indeed update
     * the bounds of all children (the whole subtree, in fact). This side effect should be exploited by using
     * `displayObject._bounds.getRectangle()` when traversing through all the bounds in a scene graph. Otherwise,
     * calling `getBounds` on each object in a subtree will cause the total cost to increase quadratically as
     * its height increases.
     *
     * The transforms of all objects in a container's **subtree** and of all **ancestors** are updated.
     * The world bounds of all display objects in a container's **subtree** will also be recalculated.
     *
     * The `_bounds` object stores the last calculation of the bounds. You can use to entirely skip bounds
     * calculation if needed.
     *
     * ```js
     * const lastCalculatedBounds = displayObject._bounds.getRectangle(optionalRect);
     * ```
     *
     * Do know that usage of `getLocalBounds` can corrupt the `_bounds` of children (the whole subtree, actually). This
     * is a known issue that has not been solved. See [getLocalBounds]{@link PIXI.DisplayObject#getLocalBounds} for more
     * details.
     *
     * `getBounds` should be called with `skipUpdate` equal to `true` in a render() call. This is because the transforms
     * are guaranteed to be update-to-date. In fact, recalculating inside a render() call may cause corruption in certain
     * cases.
     * @param skipUpdate - Setting to `true` will stop the transforms of the scene graph from
     *  being updated. This means the calculation returned MAY be out of date BUT will give you a
     *  nice performance boost.
     * @param rect - Optional rectangle to store the result of the bounds calculation.
     * @returns - The minimum axis-aligned rectangle in world space that fits around this object.
     */
    getBounds(skipUpdate, rect) {
      return skipUpdate || (this.parent ? (this._recursivePostUpdateTransform(), this.updateTransform()) : (this.parent = this._tempDisplayObjectParent, this.updateTransform(), this.parent = null)), this._bounds.updateID !== this._boundsID && (this.calculateBounds(), this._bounds.updateID = this._boundsID), rect || (this._boundsRect || (this._boundsRect = new Rectangle()), rect = this._boundsRect), this._bounds.getRectangle(rect);
    }
    /**
     * Retrieves the local bounds of the displayObject as a rectangle object.
     * @param rect - Optional rectangle to store the result of the bounds calculation.
     * @returns - The rectangular bounding area.
     */
    getLocalBounds(rect) {
      rect || (this._localBoundsRect || (this._localBoundsRect = new Rectangle()), rect = this._localBoundsRect), this._localBounds || (this._localBounds = new Bounds());
      const transformRef = this.transform, parentRef = this.parent;
      this.parent = null, this._tempDisplayObjectParent.worldAlpha = parentRef?.worldAlpha ?? 1, this.transform = this._tempDisplayObjectParent.transform;
      const worldBounds = this._bounds, worldBoundsID = this._boundsID;
      this._bounds = this._localBounds;
      const bounds = this.getBounds(false, rect);
      return this.parent = parentRef, this.transform = transformRef, this._bounds = worldBounds, this._bounds.updateID += this._boundsID - worldBoundsID, bounds;
    }
    /**
     * Calculates the global position of the display object.
     * @param position - The world origin to calculate from.
     * @param point - A Point object in which to store the value, optional
     *  (otherwise will create a new Point).
     * @param skipUpdate - Should we skip the update transform.
     * @returns - A point object representing the position of this object.
     */
    toGlobal(position, point, skipUpdate = false) {
      return skipUpdate || (this._recursivePostUpdateTransform(), this.parent ? this.displayObjectUpdateTransform() : (this.parent = this._tempDisplayObjectParent, this.displayObjectUpdateTransform(), this.parent = null)), this.worldTransform.apply(position, point);
    }
    /**
     * Calculates the local position of the display object relative to another point.
     * @param position - The world origin to calculate from.
     * @param from - The DisplayObject to calculate the global position from.
     * @param point - A Point object in which to store the value, optional
     *  (otherwise will create a new Point).
     * @param skipUpdate - Should we skip the update transform
     * @returns - A point object representing the position of this object
     */
    toLocal(position, from, point, skipUpdate) {
      return from && (position = from.toGlobal(position, point, skipUpdate)), skipUpdate || (this._recursivePostUpdateTransform(), this.parent ? this.displayObjectUpdateTransform() : (this.parent = this._tempDisplayObjectParent, this.displayObjectUpdateTransform(), this.parent = null)), this.worldTransform.applyInverse(position, point);
    }
    /**
     * Set the parent Container of this DisplayObject.
     * @param container - The Container to add this DisplayObject to.
     * @returns - The Container that this DisplayObject was added to.
     */
    setParent(container) {
      if (!container || !container.addChild)
        throw new Error("setParent: Argument must be a Container");
      return container.addChild(this), container;
    }
    /** Remove the DisplayObject from its parent Container. If the DisplayObject has no parent, do nothing. */
    removeFromParent() {
      this.parent?.removeChild(this);
    }
    /**
     * Convenience function to set the position, scale, skew and pivot at once.
     * @param x - The X position
     * @param y - The Y position
     * @param scaleX - The X scale value
     * @param scaleY - The Y scale value
     * @param rotation - The rotation
     * @param skewX - The X skew value
     * @param skewY - The Y skew value
     * @param pivotX - The X pivot value
     * @param pivotY - The Y pivot value
     * @returns - The DisplayObject instance
     */
    setTransform(x2 = 0, y2 = 0, scaleX = 1, scaleY = 1, rotation = 0, skewX = 0, skewY = 0, pivotX = 0, pivotY = 0) {
      return this.position.x = x2, this.position.y = y2, this.scale.x = scaleX || 1, this.scale.y = scaleY || 1, this.rotation = rotation, this.skew.x = skewX, this.skew.y = skewY, this.pivot.x = pivotX, this.pivot.y = pivotY, this;
    }
    /**
     * Base destroy method for generic display objects. This will automatically
     * remove the display object from its parent Container as well as remove
     * all current event listeners and internal references. Do not use a DisplayObject
     * after calling `destroy()`.
     * @param _options
     */
    destroy(_options) {
      this.removeFromParent(), this._destroyed = true, this.transform = null, this.parent = null, this._bounds = null, this.mask = null, this.cullArea = null, this.filters = null, this.filterArea = null, this.hitArea = null, this.eventMode = "auto", this.interactiveChildren = false, this.emit("destroyed"), this.removeAllListeners();
    }
    /**
     * @protected
     * @member {PIXI.Container}
     */
    get _tempDisplayObjectParent() {
      return this.tempDisplayObjectParent === null && (this.tempDisplayObjectParent = new TemporaryDisplayObject()), this.tempDisplayObjectParent;
    }
    /**
     * Used in Renderer, cacheAsBitmap and other places where you call an `updateTransform` on root.
     *
     * ```js
     * const cacheParent = elem.enableTempParent();
     * elem.updateTransform();
     * elem.disableTempParent(cacheParent);
     * ```
     * @returns - Current parent
     */
    enableTempParent() {
      const myParent = this.parent;
      return this.parent = this._tempDisplayObjectParent, myParent;
    }
    /**
     * Pair method for `enableTempParent`
     * @param cacheParent - Actual parent of element
     */
    disableTempParent(cacheParent) {
      this.parent = cacheParent;
    }
    /**
     * The position of the displayObject on the x axis relative to the local coordinates of the parent.
     * An alias to position.x
     */
    get x() {
      return this.position.x;
    }
    set x(value) {
      this.transform.position.x = value;
    }
    /**
     * The position of the displayObject on the y axis relative to the local coordinates of the parent.
     * An alias to position.y
     */
    get y() {
      return this.position.y;
    }
    set y(value) {
      this.transform.position.y = value;
    }
    /**
     * Current transform of the object based on world (parent) factors.
     * @readonly
     */
    get worldTransform() {
      return this.transform.worldTransform;
    }
    /**
     * Current transform of the object based on local factors: position, scale, other stuff.
     * @readonly
     */
    get localTransform() {
      return this.transform.localTransform;
    }
    /**
     * The coordinate of the object relative to the local coordinates of the parent.
     * @since 4.0.0
     */
    get position() {
      return this.transform.position;
    }
    set position(value) {
      this.transform.position.copyFrom(value);
    }
    /**
     * The scale factors of this object along the local coordinate axes.
     *
     * The default scale is (1, 1).
     * @since 4.0.0
     */
    get scale() {
      return this.transform.scale;
    }
    set scale(value) {
      this.transform.scale.copyFrom(value);
    }
    /**
     * The center of rotation, scaling, and skewing for this display object in its local space. The `position`
     * is the projection of `pivot` in the parent's local space.
     *
     * By default, the pivot is the origin (0, 0).
     * @since 4.0.0
     */
    get pivot() {
      return this.transform.pivot;
    }
    set pivot(value) {
      this.transform.pivot.copyFrom(value);
    }
    /**
     * The skew factor for the object in radians.
     * @since 4.0.0
     */
    get skew() {
      return this.transform.skew;
    }
    set skew(value) {
      this.transform.skew.copyFrom(value);
    }
    /**
     * The rotation of the object in radians.
     * 'rotation' and 'angle' have the same effect on a display object; rotation is in radians, angle is in degrees.
     */
    get rotation() {
      return this.transform.rotation;
    }
    set rotation(value) {
      this.transform.rotation = value;
    }
    /**
     * The angle of the object in degrees.
     * 'rotation' and 'angle' have the same effect on a display object; rotation is in radians, angle is in degrees.
     */
    get angle() {
      return this.transform.rotation * RAD_TO_DEG;
    }
    set angle(value) {
      this.transform.rotation = value * DEG_TO_RAD;
    }
    /**
     * The zIndex of the displayObject.
     *
     * If a container has the sortableChildren property set to true, children will be automatically
     * sorted by zIndex value; a higher value will mean it will be moved towards the end of the array,
     * and thus rendered on top of other display objects within the same container.
     * @see PIXI.Container#sortableChildren
     */
    get zIndex() {
      return this._zIndex;
    }
    set zIndex(value) {
      this._zIndex !== value && (this._zIndex = value, this.parent && (this.parent.sortDirty = true));
    }
    /**
     * Indicates if the object is globally visible.
     * @readonly
     */
    get worldVisible() {
      let item = this;
      do {
        if (!item.visible)
          return false;
        item = item.parent;
      } while (item);
      return true;
    }
    /**
     * Sets a mask for the displayObject. A mask is an object that limits the visibility of an
     * object to the shape of the mask applied to it. In PixiJS a regular mask must be a
     * {@link PIXI.Graphics} or a {@link PIXI.Sprite} object. This allows for much faster masking in canvas as it
     * utilities shape clipping. Furthermore, a mask of an object must be in the subtree of its parent.
     * Otherwise, `getLocalBounds` may calculate incorrect bounds, which makes the container's width and height wrong.
     * To remove a mask, set this property to `null`.
     *
     * For sprite mask both alpha and red channel are used. Black mask is the same as transparent mask.
     * @example
     * import { Graphics, Sprite } from 'pixi.js';
     *
     * const graphics = new Graphics();
     * graphics.beginFill(0xFF3300);
     * graphics.drawRect(50, 250, 100, 100);
     * graphics.endFill();
     *
     * const sprite = new Sprite(texture);
     * sprite.mask = graphics;
     * @todo At the moment, CanvasRenderer doesn't support Sprite as mask.
     */
    get mask() {
      return this._mask;
    }
    set mask(value) {
      if (this._mask !== value) {
        if (this._mask) {
          const maskObject = this._mask.isMaskData ? this._mask.maskObject : this._mask;
          maskObject && (maskObject._maskRefCount--, maskObject._maskRefCount === 0 && (maskObject.renderable = true, maskObject.isMask = false));
        }
        if (this._mask = value, this._mask) {
          const maskObject = this._mask.isMaskData ? this._mask.maskObject : this._mask;
          maskObject && (maskObject._maskRefCount === 0 && (maskObject.renderable = false, maskObject.isMask = true), maskObject._maskRefCount++);
        }
      }
    }
  }
  class TemporaryDisplayObject extends DisplayObject {
    constructor() {
      super(...arguments), this.sortDirty = null;
    }
  }
  DisplayObject.prototype.displayObjectUpdateTransform = DisplayObject.prototype.updateTransform;
  const tempMatrix = new Matrix();
  function sortChildren(a2, b2) {
    return a2.zIndex === b2.zIndex ? a2._lastSortedIndex - b2._lastSortedIndex : a2.zIndex - b2.zIndex;
  }
  const _Container = class _Container2 extends DisplayObject {
    constructor() {
      super(), this.children = [], this.sortableChildren = _Container2.defaultSortableChildren, this.sortDirty = false;
    }
    /**
     * Overridable method that can be used by Container subclasses whenever the children array is modified.
     * @param _length
     */
    onChildrenChange(_length) {
    }
    /**
     * Adds one or more children to the container.
     *
     * Multiple items can be added like so: `myContainer.addChild(thingOne, thingTwo, thingThree)`
     * @param {...PIXI.DisplayObject} children - The DisplayObject(s) to add to the container
     * @returns {PIXI.DisplayObject} - The first child that was added.
     */
    addChild(...children) {
      if (children.length > 1)
        for (let i2 = 0; i2 < children.length; i2++)
          this.addChild(children[i2]);
      else {
        const child = children[0];
        child.parent && child.parent.removeChild(child), child.parent = this, this.sortDirty = true, child.transform._parentID = -1, this.children.push(child), this._boundsID++, this.onChildrenChange(this.children.length - 1), this.emit("childAdded", child, this, this.children.length - 1), child.emit("added", this);
      }
      return children[0];
    }
    /**
     * Adds a child to the container at a specified index. If the index is out of bounds an error will be thrown.
     * If the child is already in this container, it will be moved to the specified index.
     * @param {PIXI.DisplayObject} child - The child to add.
     * @param {number} index - The absolute index where the child will be positioned at the end of the operation.
     * @returns {PIXI.DisplayObject} The child that was added.
     */
    addChildAt(child, index) {
      if (index < 0 || index > this.children.length)
        throw new Error(`${child}addChildAt: The index ${index} supplied is out of bounds ${this.children.length}`);
      return child.parent && child.parent.removeChild(child), child.parent = this, this.sortDirty = true, child.transform._parentID = -1, this.children.splice(index, 0, child), this._boundsID++, this.onChildrenChange(index), child.emit("added", this), this.emit("childAdded", child, this, index), child;
    }
    /**
     * Swaps the position of 2 Display Objects within this container.
     * @param child - First display object to swap
     * @param child2 - Second display object to swap
     */
    swapChildren(child, child2) {
      if (child === child2)
        return;
      const index1 = this.getChildIndex(child), index2 = this.getChildIndex(child2);
      this.children[index1] = child2, this.children[index2] = child, this.onChildrenChange(index1 < index2 ? index1 : index2);
    }
    /**
     * Returns the index position of a child DisplayObject instance
     * @param child - The DisplayObject instance to identify
     * @returns - The index position of the child display object to identify
     */
    getChildIndex(child) {
      const index = this.children.indexOf(child);
      if (index === -1)
        throw new Error("The supplied DisplayObject must be a child of the caller");
      return index;
    }
    /**
     * Changes the position of an existing child in the display object container
     * @param child - The child DisplayObject instance for which you want to change the index number
     * @param index - The resulting index number for the child display object
     */
    setChildIndex(child, index) {
      if (index < 0 || index >= this.children.length)
        throw new Error(`The index ${index} supplied is out of bounds ${this.children.length}`);
      const currentIndex = this.getChildIndex(child);
      removeItems(this.children, currentIndex, 1), this.children.splice(index, 0, child), this.onChildrenChange(index);
    }
    /**
     * Returns the child at the specified index
     * @param index - The index to get the child at
     * @returns - The child at the given index, if any.
     */
    getChildAt(index) {
      if (index < 0 || index >= this.children.length)
        throw new Error(`getChildAt: Index (${index}) does not exist.`);
      return this.children[index];
    }
    /**
     * Removes one or more children from the container.
     * @param {...PIXI.DisplayObject} children - The DisplayObject(s) to remove
     * @returns {PIXI.DisplayObject} The first child that was removed.
     */
    removeChild(...children) {
      if (children.length > 1)
        for (let i2 = 0; i2 < children.length; i2++)
          this.removeChild(children[i2]);
      else {
        const child = children[0], index = this.children.indexOf(child);
        if (index === -1)
          return null;
        child.parent = null, child.transform._parentID = -1, removeItems(this.children, index, 1), this._boundsID++, this.onChildrenChange(index), child.emit("removed", this), this.emit("childRemoved", child, this, index);
      }
      return children[0];
    }
    /**
     * Removes a child from the specified index position.
     * @param index - The index to get the child from
     * @returns The child that was removed.
     */
    removeChildAt(index) {
      const child = this.getChildAt(index);
      return child.parent = null, child.transform._parentID = -1, removeItems(this.children, index, 1), this._boundsID++, this.onChildrenChange(index), child.emit("removed", this), this.emit("childRemoved", child, this, index), child;
    }
    /**
     * Removes all children from this container that are within the begin and end indexes.
     * @param beginIndex - The beginning position.
     * @param endIndex - The ending position. Default value is size of the container.
     * @returns - List of removed children
     */
    removeChildren(beginIndex = 0, endIndex = this.children.length) {
      const begin = beginIndex, end = endIndex, range2 = end - begin;
      let removed;
      if (range2 > 0 && range2 <= end) {
        removed = this.children.splice(begin, range2);
        for (let i2 = 0; i2 < removed.length; ++i2)
          removed[i2].parent = null, removed[i2].transform && (removed[i2].transform._parentID = -1);
        this._boundsID++, this.onChildrenChange(beginIndex);
        for (let i2 = 0; i2 < removed.length; ++i2)
          removed[i2].emit("removed", this), this.emit("childRemoved", removed[i2], this, i2);
        return removed;
      } else if (range2 === 0 && this.children.length === 0)
        return [];
      throw new RangeError("removeChildren: numeric values are outside the acceptable range.");
    }
    /** Sorts children by zIndex. Previous order is maintained for 2 children with the same zIndex. */
    sortChildren() {
      let sortRequired = false;
      for (let i2 = 0, j2 = this.children.length; i2 < j2; ++i2) {
        const child = this.children[i2];
        child._lastSortedIndex = i2, !sortRequired && child.zIndex !== 0 && (sortRequired = true);
      }
      sortRequired && this.children.length > 1 && this.children.sort(sortChildren), this.sortDirty = false;
    }
    /** Updates the transform on all children of this container for rendering. */
    updateTransform() {
      this.sortableChildren && this.sortDirty && this.sortChildren(), this._boundsID++, this.transform.updateTransform(this.parent.transform), this.worldAlpha = this.alpha * this.parent.worldAlpha;
      for (let i2 = 0, j2 = this.children.length; i2 < j2; ++i2) {
        const child = this.children[i2];
        child.visible && child.updateTransform();
      }
    }
    /**
     * Recalculates the bounds of the container.
     *
     * This implementation will automatically fit the children's bounds into the calculation. Each child's bounds
     * is limited to its mask's bounds or filterArea, if any is applied.
     */
    calculateBounds() {
      this._bounds.clear(), this._calculateBounds();
      for (let i2 = 0; i2 < this.children.length; i2++) {
        const child = this.children[i2];
        if (!(!child.visible || !child.renderable))
          if (child.calculateBounds(), child._mask) {
            const maskObject = child._mask.isMaskData ? child._mask.maskObject : child._mask;
            maskObject ? (maskObject.calculateBounds(), this._bounds.addBoundsMask(child._bounds, maskObject._bounds)) : this._bounds.addBounds(child._bounds);
          } else
            child.filterArea ? this._bounds.addBoundsArea(child._bounds, child.filterArea) : this._bounds.addBounds(child._bounds);
      }
      this._bounds.updateID = this._boundsID;
    }
    /**
     * Retrieves the local bounds of the displayObject as a rectangle object.
     *
     * Calling `getLocalBounds` may invalidate the `_bounds` of the whole subtree below. If using it inside a render()
     * call, it is advised to call `getBounds()` immediately after to recalculate the world bounds of the subtree.
     * @param rect - Optional rectangle to store the result of the bounds calculation.
     * @param skipChildrenUpdate - Setting to `true` will stop re-calculation of children transforms,
     *  it was default behaviour of pixi 4.0-5.2 and caused many problems to users.
     * @returns - The rectangular bounding area.
     */
    getLocalBounds(rect, skipChildrenUpdate = false) {
      const result = super.getLocalBounds(rect);
      if (!skipChildrenUpdate)
        for (let i2 = 0, j2 = this.children.length; i2 < j2; ++i2) {
          const child = this.children[i2];
          child.visible && child.updateTransform();
        }
      return result;
    }
    /**
     * Recalculates the content bounds of this object. This should be overriden to
     * calculate the bounds of this specific object (not including children).
     * @protected
     */
    _calculateBounds() {
    }
    /**
     * Renders this object and its children with culling.
     * @protected
     * @param {PIXI.Renderer} renderer - The renderer
     */
    _renderWithCulling(renderer) {
      const sourceFrame = renderer.renderTexture.sourceFrame;
      if (!(sourceFrame.width > 0 && sourceFrame.height > 0))
        return;
      let bounds, transform;
      this.cullArea ? (bounds = this.cullArea, transform = this.worldTransform) : this._render !== _Container2.prototype._render && (bounds = this.getBounds(true));
      const projectionTransform = renderer.projection.transform;
      if (projectionTransform && (transform ? (transform = tempMatrix.copyFrom(transform), transform.prepend(projectionTransform)) : transform = projectionTransform), bounds && sourceFrame.intersects(bounds, transform))
        this._render(renderer);
      else if (this.cullArea)
        return;
      for (let i2 = 0, j2 = this.children.length; i2 < j2; ++i2) {
        const child = this.children[i2], childCullable = child.cullable;
        child.cullable = childCullable || !this.cullArea, child.render(renderer), child.cullable = childCullable;
      }
    }
    /**
     * Renders the object using the WebGL renderer.
     *
     * The [_render]{@link PIXI.Container#_render} method is be overriden for rendering the contents of the
     * container itself. This `render` method will invoke it, and also invoke the `render` methods of all
     * children afterward.
     *
     * If `renderable` or `visible` is false or if `worldAlpha` is not positive or if `cullable` is true and
     * the bounds of this object are out of frame, this implementation will entirely skip rendering.
     * See {@link PIXI.DisplayObject} for choosing between `renderable` or `visible`. Generally,
     * setting alpha to zero is not recommended for purely skipping rendering.
     *
     * When your scene becomes large (especially when it is larger than can be viewed in a single screen), it is
     * advised to employ **culling** to automatically skip rendering objects outside of the current screen.
     * See [cullable]{@link PIXI.DisplayObject#cullable} and [cullArea]{@link PIXI.DisplayObject#cullArea}.
     * Other culling methods might be better suited for a large number static objects; see
     * [@pixi-essentials/cull]{@link https://www.npmjs.com/package/@pixi-essentials/cull} and
     * [pixi-cull]{@link https://www.npmjs.com/package/pixi-cull}.
     *
     * The [renderAdvanced]{@link PIXI.Container#renderAdvanced} method is internally used when when masking or
     * filtering is applied on a container. This does, however, break batching and can affect performance when
     * masking and filtering is applied extensively throughout the scene graph.
     * @param renderer - The renderer
     */
    render(renderer) {
      if (!(!this.visible || this.worldAlpha <= 0 || !this.renderable))
        if (this._mask || this.filters?.length)
          this.renderAdvanced(renderer);
        else if (this.cullable)
          this._renderWithCulling(renderer);
        else {
          this._render(renderer);
          for (let i2 = 0, j2 = this.children.length; i2 < j2; ++i2)
            this.children[i2].render(renderer);
        }
    }
    /**
     * Render the object using the WebGL renderer and advanced features.
     * @param renderer - The renderer
     */
    renderAdvanced(renderer) {
      const filters = this.filters, mask = this._mask;
      if (filters) {
        this._enabledFilters || (this._enabledFilters = []), this._enabledFilters.length = 0;
        for (let i2 = 0; i2 < filters.length; i2++)
          filters[i2].enabled && this._enabledFilters.push(filters[i2]);
      }
      const flush = filters && this._enabledFilters?.length || mask && (!mask.isMaskData || mask.enabled && (mask.autoDetect || mask.type !== MASK_TYPES.NONE));
      if (flush && renderer.batch.flush(), filters && this._enabledFilters?.length && renderer.filter.push(this, this._enabledFilters), mask && renderer.mask.push(this, this._mask), this.cullable)
        this._renderWithCulling(renderer);
      else {
        this._render(renderer);
        for (let i2 = 0, j2 = this.children.length; i2 < j2; ++i2)
          this.children[i2].render(renderer);
      }
      flush && renderer.batch.flush(), mask && renderer.mask.pop(this), filters && this._enabledFilters?.length && renderer.filter.pop();
    }
    /**
     * To be overridden by the subclasses.
     * @param _renderer - The renderer
     */
    _render(_renderer) {
    }
    /**
     * Removes all internal references and listeners as well as removes children from the display list.
     * Do not use a Container after calling `destroy`.
     * @param options - Options parameter. A boolean will act as if all options
     *  have been set to that value
     * @param {boolean} [options.children=false] - if set to true, all the children will have their destroy
     *  method called as well. 'options' will be passed on to those calls.
     * @param {boolean} [options.texture=false] - Only used for child Sprites if options.children is set to true
     *  Should it destroy the texture of the child sprite
     * @param {boolean} [options.baseTexture=false] - Only used for child Sprites if options.children is set to true
     *  Should it destroy the base texture of the child sprite
     */
    destroy(options) {
      super.destroy(), this.sortDirty = false;
      const destroyChildren = typeof options == "boolean" ? options : options?.children, oldChildren = this.removeChildren(0, this.children.length);
      if (destroyChildren)
        for (let i2 = 0; i2 < oldChildren.length; ++i2)
          oldChildren[i2].destroy(options);
    }
    /** The width of the Container, setting this will actually modify the scale to achieve the value set. */
    get width() {
      return this.scale.x * this.getLocalBounds().width;
    }
    set width(value) {
      const width = this.getLocalBounds().width;
      width !== 0 ? this.scale.x = value / width : this.scale.x = 1, this._width = value;
    }
    /** The height of the Container, setting this will actually modify the scale to achieve the value set. */
    get height() {
      return this.scale.y * this.getLocalBounds().height;
    }
    set height(value) {
      const height = this.getLocalBounds().height;
      height !== 0 ? this.scale.y = value / height : this.scale.y = 1, this._height = value;
    }
  };
  _Container.defaultSortableChildren = false;
  let Container = _Container;
  Container.prototype.containerUpdateTransform = Container.prototype.updateTransform;
  Object.defineProperties(settings, {
    /**
     * Sets the default value for the container property 'sortableChildren'.
     * @static
     * @name SORTABLE_CHILDREN
     * @memberof PIXI.settings
     * @deprecated since 7.1.0
     * @type {boolean}
     * @see PIXI.Container.defaultSortableChildren
     */
    SORTABLE_CHILDREN: {
      get() {
        return Container.defaultSortableChildren;
      },
      set(value) {
        deprecation("7.1.0", "settings.SORTABLE_CHILDREN is deprecated, use Container.defaultSortableChildren"), Container.defaultSortableChildren = value;
      }
    }
  });
  const _Application = class _Application2 {
    /**
     * @param options - The optional application and renderer parameters.
     */
    constructor(options) {
      this.stage = new Container(), options = Object.assign({
        forceCanvas: false
      }, options), this.renderer = autoDetectRenderer(options), _Application2._plugins.forEach((plugin) => {
        plugin.init.call(this, options);
      });
    }
    /** Render the current stage. */
    render() {
      this.renderer.render(this.stage);
    }
    /**
     * Reference to the renderer's canvas element.
     * @member {PIXI.ICanvas}
     * @readonly
     */
    get view() {
      return this.renderer?.view;
    }
    /**
     * Reference to the renderer's screen rectangle. Its safe to use as `filterArea` or `hitArea` for the whole screen.
     * @member {PIXI.Rectangle}
     * @readonly
     */
    get screen() {
      return this.renderer?.screen;
    }
    /**
     * Destroy and don't use after this.
     * @param {boolean} [removeView=false] - Automatically remove canvas from DOM.
     * @param {object|boolean} [stageOptions] - Options parameter. A boolean will act as if all options
     *  have been set to that value
     * @param {boolean} [stageOptions.children=false] - if set to true, all the children will have their destroy
     *  method called as well. 'stageOptions' will be passed on to those calls.
     * @param {boolean} [stageOptions.texture=false] - Only used for child Sprites if stageOptions.children is set
     *  to true. Should it destroy the texture of the child sprite
     * @param {boolean} [stageOptions.baseTexture=false] - Only used for child Sprites if stageOptions.children is set
     *  to true. Should it destroy the base texture of the child sprite
     */
    destroy(removeView, stageOptions) {
      const plugins = _Application2._plugins.slice(0);
      plugins.reverse(), plugins.forEach((plugin) => {
        plugin.destroy.call(this);
      }), this.stage.destroy(stageOptions), this.stage = null, this.renderer.destroy(removeView), this.renderer = null;
    }
  };
  _Application._plugins = [];
  let Application = _Application;
  extensions.handleByList(ExtensionType.Application, Application._plugins);
  class ResizePlugin {
    /**
     * Initialize the plugin with scope of application instance
     * @static
     * @private
     * @param {object} [options] - See application options
     */
    static init(options) {
      Object.defineProperty(
        this,
        "resizeTo",
        /**
         * The HTML element or window to automatically resize the
         * renderer's view element to match width and height.
         * @member {Window|HTMLElement}
         * @name resizeTo
         * @memberof PIXI.Application#
         */
        {
          set(dom) {
            globalThis.removeEventListener("resize", this.queueResize), this._resizeTo = dom, dom && (globalThis.addEventListener("resize", this.queueResize), this.resize());
          },
          get() {
            return this._resizeTo;
          }
        }
      ), this.queueResize = () => {
        this._resizeTo && (this.cancelResize(), this._resizeId = requestAnimationFrame(() => this.resize()));
      }, this.cancelResize = () => {
        this._resizeId && (cancelAnimationFrame(this._resizeId), this._resizeId = null);
      }, this.resize = () => {
        if (!this._resizeTo)
          return;
        this.cancelResize();
        let width, height;
        if (this._resizeTo === globalThis.window)
          width = globalThis.innerWidth, height = globalThis.innerHeight;
        else {
          const { clientWidth, clientHeight } = this._resizeTo;
          width = clientWidth, height = clientHeight;
        }
        this.renderer.resize(width, height), this.render();
      }, this._resizeId = null, this._resizeTo = null, this.resizeTo = options.resizeTo || null;
    }
    /**
     * Clean up the ticker, scoped to application
     * @static
     * @private
     */
    static destroy() {
      globalThis.removeEventListener("resize", this.queueResize), this.cancelResize(), this.cancelResize = null, this.queueResize = null, this.resizeTo = null, this.resize = null;
    }
  }
  ResizePlugin.extension = ExtensionType.Application;
  extensions.add(ResizePlugin);
  const GAUSSIAN_VALUES = {
    5: [0.153388, 0.221461, 0.250301],
    7: [0.071303, 0.131514, 0.189879, 0.214607],
    9: [0.028532, 0.067234, 0.124009, 0.179044, 0.20236],
    11: [93e-4, 0.028002, 0.065984, 0.121703, 0.175713, 0.198596],
    13: [2406e-6, 9255e-6, 0.027867, 0.065666, 0.121117, 0.174868, 0.197641],
    15: [489e-6, 2403e-6, 9246e-6, 0.02784, 0.065602, 0.120999, 0.174697, 0.197448]
  }, fragTemplate = [
    "varying vec2 vBlurTexCoords[%size%];",
    "uniform sampler2D uSampler;",
    "void main(void)",
    "{",
    "    gl_FragColor = vec4(0.0);",
    "    %blur%",
    "}"
  ].join(`
`);
  function generateBlurFragSource(kernelSize) {
    const kernel = GAUSSIAN_VALUES[kernelSize], halfLength = kernel.length;
    let fragSource = fragTemplate, blurLoop = "";
    const template = "gl_FragColor += texture2D(uSampler, vBlurTexCoords[%index%]) * %value%;";
    let value;
    for (let i2 = 0; i2 < kernelSize; i2++) {
      let blur = template.replace("%index%", i2.toString());
      value = i2, i2 >= halfLength && (value = kernelSize - i2 - 1), blur = blur.replace("%value%", kernel[value].toString()), blurLoop += blur, blurLoop += `
`;
    }
    return fragSource = fragSource.replace("%blur%", blurLoop), fragSource = fragSource.replace("%size%", kernelSize.toString()), fragSource;
  }
  const vertTemplate = `
    attribute vec2 aVertexPosition;

    uniform mat3 projectionMatrix;

    uniform float strength;

    varying vec2 vBlurTexCoords[%size%];

    uniform vec4 inputSize;
    uniform vec4 outputFrame;

    vec4 filterVertexPosition( void )
    {
        vec2 position = aVertexPosition * max(outputFrame.zw, vec2(0.)) + outputFrame.xy;

        return vec4((projectionMatrix * vec3(position, 1.0)).xy, 0.0, 1.0);
    }

    vec2 filterTextureCoord( void )
    {
        return aVertexPosition * (outputFrame.zw * inputSize.zw);
    }

    void main(void)
    {
        gl_Position = filterVertexPosition();

        vec2 textureCoord = filterTextureCoord();
        %blur%
    }`;
  function generateBlurVertSource(kernelSize, x2) {
    const halfLength = Math.ceil(kernelSize / 2);
    let vertSource = vertTemplate, blurLoop = "", template;
    x2 ? template = "vBlurTexCoords[%index%] =  textureCoord + vec2(%sampleIndex% * strength, 0.0);" : template = "vBlurTexCoords[%index%] =  textureCoord + vec2(0.0, %sampleIndex% * strength);";
    for (let i2 = 0; i2 < kernelSize; i2++) {
      let blur = template.replace("%index%", i2.toString());
      blur = blur.replace("%sampleIndex%", `${i2 - (halfLength - 1)}.0`), blurLoop += blur, blurLoop += `
`;
    }
    return vertSource = vertSource.replace("%blur%", blurLoop), vertSource = vertSource.replace("%size%", kernelSize.toString()), vertSource;
  }
  class BlurFilterPass extends Filter {
    /**
     * @param horizontal - Do pass along the x-axis (`true`) or y-axis (`false`).
     * @param strength - The strength of the blur filter.
     * @param quality - The quality of the blur filter.
     * @param {number|null} [resolution=PIXI.Filter.defaultResolution] - The resolution of the blur filter.
     * @param kernelSize - The kernelSize of the blur filter.Options: 5, 7, 9, 11, 13, 15.
     */
    constructor(horizontal, strength = 8, quality = 4, resolution = Filter.defaultResolution, kernelSize = 5) {
      const vertSrc = generateBlurVertSource(kernelSize, horizontal), fragSrc = generateBlurFragSource(kernelSize);
      super(
        // vertex shader
        vertSrc,
        // fragment shader
        fragSrc
      ), this.horizontal = horizontal, this.resolution = resolution, this._quality = 0, this.quality = quality, this.blur = strength;
    }
    /**
     * Applies the filter.
     * @param filterManager - The manager.
     * @param input - The input target.
     * @param output - The output target.
     * @param clearMode - How to clear
     */
    apply(filterManager, input, output, clearMode) {
      if (output ? this.horizontal ? this.uniforms.strength = 1 / output.width * (output.width / input.width) : this.uniforms.strength = 1 / output.height * (output.height / input.height) : this.horizontal ? this.uniforms.strength = 1 / filterManager.renderer.width * (filterManager.renderer.width / input.width) : this.uniforms.strength = 1 / filterManager.renderer.height * (filterManager.renderer.height / input.height), this.uniforms.strength *= this.strength, this.uniforms.strength /= this.passes, this.passes === 1)
        filterManager.applyFilter(this, input, output, clearMode);
      else {
        const renderTarget = filterManager.getFilterTexture(), renderer = filterManager.renderer;
        let flip = input, flop = renderTarget;
        this.state.blend = false, filterManager.applyFilter(this, flip, flop, CLEAR_MODES.CLEAR);
        for (let i2 = 1; i2 < this.passes - 1; i2++) {
          filterManager.bindAndClear(flip, CLEAR_MODES.BLIT), this.uniforms.uSampler = flop;
          const temp = flop;
          flop = flip, flip = temp, renderer.shader.bind(this), renderer.geometry.draw(5);
        }
        this.state.blend = true, filterManager.applyFilter(this, flop, output, clearMode), filterManager.returnFilterTexture(renderTarget);
      }
    }
    /**
     * Sets the strength of both the blur.
     * @default 16
     */
    get blur() {
      return this.strength;
    }
    set blur(value) {
      this.padding = 1 + Math.abs(value) * 2, this.strength = value;
    }
    /**
     * Sets the quality of the blur by modifying the number of passes. More passes means higher
     * quality bluring but the lower the performance.
     * @default 4
     */
    get quality() {
      return this._quality;
    }
    set quality(value) {
      this._quality = value, this.passes = value;
    }
  }
  class BlurFilter extends Filter {
    /**
     * @param strength - The strength of the blur filter.
     * @param quality - The quality of the blur filter.
     * @param {number|null} [resolution=PIXI.Filter.defaultResolution] - The resolution of the blur filter.
     * @param kernelSize - The kernelSize of the blur filter.Options: 5, 7, 9, 11, 13, 15.
     */
    constructor(strength = 8, quality = 4, resolution = Filter.defaultResolution, kernelSize = 5) {
      super(), this._repeatEdgePixels = false, this.blurXFilter = new BlurFilterPass(true, strength, quality, resolution, kernelSize), this.blurYFilter = new BlurFilterPass(false, strength, quality, resolution, kernelSize), this.resolution = resolution, this.quality = quality, this.blur = strength, this.repeatEdgePixels = false;
    }
    /**
     * Applies the filter.
     * @param filterManager - The manager.
     * @param input - The input target.
     * @param output - The output target.
     * @param clearMode - How to clear
     */
    apply(filterManager, input, output, clearMode) {
      const xStrength = Math.abs(this.blurXFilter.strength), yStrength = Math.abs(this.blurYFilter.strength);
      if (xStrength && yStrength) {
        const renderTarget = filterManager.getFilterTexture();
        this.blurXFilter.apply(filterManager, input, renderTarget, CLEAR_MODES.CLEAR), this.blurYFilter.apply(filterManager, renderTarget, output, clearMode), filterManager.returnFilterTexture(renderTarget);
      } else
        yStrength ? this.blurYFilter.apply(filterManager, input, output, clearMode) : this.blurXFilter.apply(filterManager, input, output, clearMode);
    }
    updatePadding() {
      this._repeatEdgePixels ? this.padding = 0 : this.padding = Math.max(Math.abs(this.blurXFilter.strength), Math.abs(this.blurYFilter.strength)) * 2;
    }
    /**
     * Sets the strength of both the blurX and blurY properties simultaneously
     * @default 2
     */
    get blur() {
      return this.blurXFilter.blur;
    }
    set blur(value) {
      this.blurXFilter.blur = this.blurYFilter.blur = value, this.updatePadding();
    }
    /**
     * Sets the number of passes for blur. More passes means higher quality bluring.
     * @default 1
     */
    get quality() {
      return this.blurXFilter.quality;
    }
    set quality(value) {
      this.blurXFilter.quality = this.blurYFilter.quality = value;
    }
    /**
     * Sets the strength of the blurX property
     * @default 2
     */
    get blurX() {
      return this.blurXFilter.blur;
    }
    set blurX(value) {
      this.blurXFilter.blur = value, this.updatePadding();
    }
    /**
     * Sets the strength of the blurY property
     * @default 2
     */
    get blurY() {
      return this.blurYFilter.blur;
    }
    set blurY(value) {
      this.blurYFilter.blur = value, this.updatePadding();
    }
    /**
     * Sets the blendmode of the filter
     * @default PIXI.BLEND_MODES.NORMAL
     */
    get blendMode() {
      return this.blurYFilter.blendMode;
    }
    set blendMode(value) {
      this.blurYFilter.blendMode = value;
    }
    /**
     * If set to true the edge of the target will be clamped
     * @default false
     */
    get repeatEdgePixels() {
      return this._repeatEdgePixels;
    }
    set repeatEdgePixels(value) {
      this._repeatEdgePixels = value, this.updatePadding();
    }
  }
  var d = `attribute vec2 aVertexPosition;
attribute vec2 aTextureCoord;

uniform mat3 projectionMatrix;

varying vec2 vTextureCoord;

void main(void)
{
    gl_Position = vec4((projectionMatrix * vec3(aVertexPosition, 1.0)).xy, 0.0, 1.0);
    vTextureCoord = aTextureCoord;
}`, u = `uniform float radius;
uniform float strength;
uniform vec2 center;
uniform sampler2D uSampler;
varying vec2 vTextureCoord;

uniform vec4 filterArea;
uniform vec4 filterClamp;
uniform vec2 dimensions;

void main()
{
    vec2 coord = vTextureCoord * filterArea.xy;
    coord -= center * dimensions.xy;
    float distance = length(coord);
    if (distance < radius) {
        float percent = distance / radius;
        if (strength > 0.0) {
            coord *= mix(1.0, smoothstep(0.0, radius / distance, percent), strength * 0.75);
        } else {
            coord *= mix(1.0, pow(percent, 1.0 + strength * 0.75) * radius / distance, 1.0 - percent);
        }
    }
    coord += center * dimensions.xy;
    coord /= filterArea.xy;
    vec2 clampedCoord = clamp(coord, filterClamp.xy, filterClamp.zw);
    vec4 color = texture2D(uSampler, clampedCoord);
    if (coord != clampedCoord) {
        color *= max(0.0, 1.0 - length(coord - clampedCoord));
    }

    gl_FragColor = color;
}
`;
  const n = class extends Filter {
    constructor(e2) {
      super(d, u), this.uniforms.dimensions = new Float32Array(2), Object.assign(this, n.defaults, e2);
    }
    apply(e2, r2, o2, i2) {
      const { width: s2, height: a2 } = r2.filterFrame;
      this.uniforms.dimensions[0] = s2, this.uniforms.dimensions[1] = a2, e2.applyFilter(this, r2, o2, i2);
    }
    get radius() {
      return this.uniforms.radius;
    }
    set radius(e2) {
      this.uniforms.radius = e2;
    }
    get strength() {
      return this.uniforms.strength;
    }
    set strength(e2) {
      this.uniforms.strength = e2;
    }
    get center() {
      return this.uniforms.center;
    }
    set center(e2) {
      this.uniforms.center = e2;
    }
  };
  let t = n;
  t.defaults = { center: [0.5, 0.5], radius: 100, strength: 1 };
  var fragment = `varying vec2 vTextureCoord;
uniform sampler2D uSampler;
uniform float m[20];
uniform float uAlpha;

void main(void)
{
    vec4 c = texture2D(uSampler, vTextureCoord);

    if (uAlpha == 0.0) {
        gl_FragColor = c;
        return;
    }

    // Un-premultiply alpha before applying the color matrix. See issue #3539.
    if (c.a > 0.0) {
      c.rgb /= c.a;
    }

    vec4 result;

    result.r = (m[0] * c.r);
        result.r += (m[1] * c.g);
        result.r += (m[2] * c.b);
        result.r += (m[3] * c.a);
        result.r += m[4];

    result.g = (m[5] * c.r);
        result.g += (m[6] * c.g);
        result.g += (m[7] * c.b);
        result.g += (m[8] * c.a);
        result.g += m[9];

    result.b = (m[10] * c.r);
       result.b += (m[11] * c.g);
       result.b += (m[12] * c.b);
       result.b += (m[13] * c.a);
       result.b += m[14];

    result.a = (m[15] * c.r);
       result.a += (m[16] * c.g);
       result.a += (m[17] * c.b);
       result.a += (m[18] * c.a);
       result.a += m[19];

    vec3 rgb = mix(c.rgb, result.rgb, uAlpha);

    // Premultiply alpha again.
    rgb *= result.a;

    gl_FragColor = vec4(rgb, result.a);
}
`;
  class ColorMatrixFilter extends Filter {
    constructor() {
      const uniforms = {
        m: new Float32Array([
          1,
          0,
          0,
          0,
          0,
          0,
          1,
          0,
          0,
          0,
          0,
          0,
          1,
          0,
          0,
          0,
          0,
          0,
          1,
          0
        ]),
        uAlpha: 1
      };
      super(defaultFilterVertex, fragment, uniforms), this.alpha = 1;
    }
    /**
     * Transforms current matrix and set the new one
     * @param {number[]} matrix - 5x4 matrix
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    _loadMatrix(matrix, multiply = false) {
      let newMatrix = matrix;
      multiply && (this._multiply(newMatrix, this.uniforms.m, matrix), newMatrix = this._colorMatrix(newMatrix)), this.uniforms.m = newMatrix;
    }
    /**
     * Multiplies two mat5's
     * @private
     * @param out - 5x4 matrix the receiving matrix
     * @param a - 5x4 matrix the first operand
     * @param b - 5x4 matrix the second operand
     * @returns {number[]} 5x4 matrix
     */
    _multiply(out, a2, b2) {
      return out[0] = a2[0] * b2[0] + a2[1] * b2[5] + a2[2] * b2[10] + a2[3] * b2[15], out[1] = a2[0] * b2[1] + a2[1] * b2[6] + a2[2] * b2[11] + a2[3] * b2[16], out[2] = a2[0] * b2[2] + a2[1] * b2[7] + a2[2] * b2[12] + a2[3] * b2[17], out[3] = a2[0] * b2[3] + a2[1] * b2[8] + a2[2] * b2[13] + a2[3] * b2[18], out[4] = a2[0] * b2[4] + a2[1] * b2[9] + a2[2] * b2[14] + a2[3] * b2[19] + a2[4], out[5] = a2[5] * b2[0] + a2[6] * b2[5] + a2[7] * b2[10] + a2[8] * b2[15], out[6] = a2[5] * b2[1] + a2[6] * b2[6] + a2[7] * b2[11] + a2[8] * b2[16], out[7] = a2[5] * b2[2] + a2[6] * b2[7] + a2[7] * b2[12] + a2[8] * b2[17], out[8] = a2[5] * b2[3] + a2[6] * b2[8] + a2[7] * b2[13] + a2[8] * b2[18], out[9] = a2[5] * b2[4] + a2[6] * b2[9] + a2[7] * b2[14] + a2[8] * b2[19] + a2[9], out[10] = a2[10] * b2[0] + a2[11] * b2[5] + a2[12] * b2[10] + a2[13] * b2[15], out[11] = a2[10] * b2[1] + a2[11] * b2[6] + a2[12] * b2[11] + a2[13] * b2[16], out[12] = a2[10] * b2[2] + a2[11] * b2[7] + a2[12] * b2[12] + a2[13] * b2[17], out[13] = a2[10] * b2[3] + a2[11] * b2[8] + a2[12] * b2[13] + a2[13] * b2[18], out[14] = a2[10] * b2[4] + a2[11] * b2[9] + a2[12] * b2[14] + a2[13] * b2[19] + a2[14], out[15] = a2[15] * b2[0] + a2[16] * b2[5] + a2[17] * b2[10] + a2[18] * b2[15], out[16] = a2[15] * b2[1] + a2[16] * b2[6] + a2[17] * b2[11] + a2[18] * b2[16], out[17] = a2[15] * b2[2] + a2[16] * b2[7] + a2[17] * b2[12] + a2[18] * b2[17], out[18] = a2[15] * b2[3] + a2[16] * b2[8] + a2[17] * b2[13] + a2[18] * b2[18], out[19] = a2[15] * b2[4] + a2[16] * b2[9] + a2[17] * b2[14] + a2[18] * b2[19] + a2[19], out;
    }
    /**
     * Create a Float32 Array and normalize the offset component to 0-1
     * @param {number[]} matrix - 5x4 matrix
     * @returns {number[]} 5x4 matrix with all values between 0-1
     */
    _colorMatrix(matrix) {
      const m2 = new Float32Array(matrix);
      return m2[4] /= 255, m2[9] /= 255, m2[14] /= 255, m2[19] /= 255, m2;
    }
    /**
     * Adjusts brightness
     * @param b - value of the brigthness (0-1, where 0 is black)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    brightness(b2, multiply) {
      const matrix = [
        b2,
        0,
        0,
        0,
        0,
        0,
        b2,
        0,
        0,
        0,
        0,
        0,
        b2,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Sets each channel on the diagonal of the color matrix.
     * This can be used to achieve a tinting effect on Containers similar to the tint field of some
     * display objects like Sprite, Text, Graphics, and Mesh.
     * @param color - Color of the tint. This is a hex value.
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    tint(color, multiply) {
      const [r2, g2, b2] = Color.shared.setValue(color).toArray(), matrix = [
        r2,
        0,
        0,
        0,
        0,
        0,
        g2,
        0,
        0,
        0,
        0,
        0,
        b2,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Set the matrices in grey scales
     * @param scale - value of the grey (0-1, where 0 is black)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    greyscale(scale, multiply) {
      const matrix = [
        scale,
        scale,
        scale,
        0,
        0,
        scale,
        scale,
        scale,
        0,
        0,
        scale,
        scale,
        scale,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Set the black and white matrice.
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    blackAndWhite(multiply) {
      const matrix = [
        0.3,
        0.6,
        0.1,
        0,
        0,
        0.3,
        0.6,
        0.1,
        0,
        0,
        0.3,
        0.6,
        0.1,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Set the hue property of the color
     * @param rotation - in degrees
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    hue(rotation, multiply) {
      rotation = (rotation || 0) / 180 * Math.PI;
      const cosR = Math.cos(rotation), sinR = Math.sin(rotation), sqrt = Math.sqrt, w2 = 1 / 3, sqrW = sqrt(w2), a00 = cosR + (1 - cosR) * w2, a01 = w2 * (1 - cosR) - sqrW * sinR, a02 = w2 * (1 - cosR) + sqrW * sinR, a10 = w2 * (1 - cosR) + sqrW * sinR, a11 = cosR + w2 * (1 - cosR), a12 = w2 * (1 - cosR) - sqrW * sinR, a20 = w2 * (1 - cosR) - sqrW * sinR, a21 = w2 * (1 - cosR) + sqrW * sinR, a22 = cosR + w2 * (1 - cosR), matrix = [
        a00,
        a01,
        a02,
        0,
        0,
        a10,
        a11,
        a12,
        0,
        0,
        a20,
        a21,
        a22,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Set the contrast matrix, increase the separation between dark and bright
     * Increase contrast : shadows darker and highlights brighter
     * Decrease contrast : bring the shadows up and the highlights down
     * @param amount - value of the contrast (0-1)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    contrast(amount, multiply) {
      const v2 = (amount || 0) + 1, o2 = -0.5 * (v2 - 1), matrix = [
        v2,
        0,
        0,
        0,
        o2,
        0,
        v2,
        0,
        0,
        o2,
        0,
        0,
        v2,
        0,
        o2,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Set the saturation matrix, increase the separation between colors
     * Increase saturation : increase contrast, brightness, and sharpness
     * @param amount - The saturation amount (0-1)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    saturate(amount = 0, multiply) {
      const x2 = amount * 2 / 3 + 1, y2 = (x2 - 1) * -0.5, matrix = [
        x2,
        y2,
        y2,
        0,
        0,
        y2,
        x2,
        y2,
        0,
        0,
        y2,
        y2,
        x2,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /** Desaturate image (remove color) Call the saturate function */
    desaturate() {
      this.saturate(-1);
    }
    /**
     * Negative image (inverse of classic rgb matrix)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    negative(multiply) {
      const matrix = [
        -1,
        0,
        0,
        1,
        0,
        0,
        -1,
        0,
        1,
        0,
        0,
        0,
        -1,
        1,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Sepia image
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    sepia(multiply) {
      const matrix = [
        0.393,
        0.7689999,
        0.18899999,
        0,
        0,
        0.349,
        0.6859999,
        0.16799999,
        0,
        0,
        0.272,
        0.5339999,
        0.13099999,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Color motion picture process invented in 1916 (thanks Dominic Szablewski)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    technicolor(multiply) {
      const matrix = [
        1.9125277891456083,
        -0.8545344976951645,
        -0.09155508482755585,
        0,
        11.793603434377337,
        -0.3087833385928097,
        1.7658908555458428,
        -0.10601743074722245,
        0,
        -70.35205161461398,
        -0.231103377548616,
        -0.7501899197440212,
        1.847597816108189,
        0,
        30.950940869491138,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Polaroid filter
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    polaroid(multiply) {
      const matrix = [
        1.438,
        -0.062,
        -0.062,
        0,
        0,
        -0.122,
        1.378,
        -0.122,
        0,
        0,
        -0.016,
        -0.016,
        1.483,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Filter who transforms : Red -> Blue and Blue -> Red
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    toBGR(multiply) {
      const matrix = [
        0,
        0,
        1,
        0,
        0,
        0,
        1,
        0,
        0,
        0,
        1,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Color reversal film introduced by Eastman Kodak in 1935. (thanks Dominic Szablewski)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    kodachrome(multiply) {
      const matrix = [
        1.1285582396593525,
        -0.3967382283601348,
        -0.03992559172921793,
        0,
        63.72958762196502,
        -0.16404339962244616,
        1.0835251566291304,
        -0.05498805115633132,
        0,
        24.732407896706203,
        -0.16786010706155763,
        -0.5603416277695248,
        1.6014850761964943,
        0,
        35.62982807460946,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Brown delicious browni filter (thanks Dominic Szablewski)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    browni(multiply) {
      const matrix = [
        0.5997023498159715,
        0.34553243048391263,
        -0.2708298674538042,
        0,
        47.43192855600873,
        -0.037703249837783157,
        0.8609577587992641,
        0.15059552388459913,
        0,
        -36.96841498319127,
        0.24113635128153335,
        -0.07441037908422492,
        0.44972182064877153,
        0,
        -7.562075277591283,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Vintage filter (thanks Dominic Szablewski)
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    vintage(multiply) {
      const matrix = [
        0.6279345635605994,
        0.3202183420819367,
        -0.03965408211312453,
        0,
        9.651285835294123,
        0.02578397704808868,
        0.6441188644374771,
        0.03259127616149294,
        0,
        7.462829176470591,
        0.0466055556782719,
        -0.0851232987247891,
        0.5241648018700465,
        0,
        5.159190588235296,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * We don't know exactly what it does, kind of gradient map, but funny to play with!
     * @param desaturation - Tone values.
     * @param toned - Tone values.
     * @param lightColor - Tone values, example: `0xFFE580`
     * @param darkColor - Tone values, example: `0xFFE580`
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    colorTone(desaturation, toned, lightColor, darkColor, multiply) {
      desaturation = desaturation || 0.2, toned = toned || 0.15, lightColor = lightColor || 16770432, darkColor = darkColor || 3375104;
      const temp = Color.shared, [lR, lG, lB] = temp.setValue(lightColor).toArray(), [dR, dG, dB] = temp.setValue(darkColor).toArray(), matrix = [
        0.3,
        0.59,
        0.11,
        0,
        0,
        lR,
        lG,
        lB,
        desaturation,
        0,
        dR,
        dG,
        dB,
        toned,
        0,
        lR - dR,
        lG - dG,
        lB - dB,
        0,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Night effect
     * @param intensity - The intensity of the night effect.
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    night(intensity, multiply) {
      intensity = intensity || 0.1;
      const matrix = [
        intensity * -2,
        -intensity,
        0,
        0,
        0,
        -intensity,
        0,
        intensity,
        0,
        0,
        0,
        intensity,
        intensity * 2,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * Predator effect
     *
     * Erase the current matrix by setting a new indepent one
     * @param amount - how much the predator feels his future victim
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    predator(amount, multiply) {
      const matrix = [
        // row 1
        11.224130630493164 * amount,
        -4.794486999511719 * amount,
        -2.8746118545532227 * amount,
        0 * amount,
        0.40342438220977783 * amount,
        // row 2
        -3.6330697536468506 * amount,
        9.193157196044922 * amount,
        -2.951810836791992 * amount,
        0 * amount,
        -1.316135048866272 * amount,
        // row 3
        -3.2184197902679443 * amount,
        -4.2375030517578125 * amount,
        7.476448059082031 * amount,
        0 * amount,
        0.8044459223747253 * amount,
        // row 4
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /**
     * LSD effect
     *
     * Multiply the current matrix
     * @param multiply - if true, current matrix and matrix are multiplied. If false,
     *  just set the current matrix with @param matrix
     */
    lsd(multiply) {
      const matrix = [
        2,
        -0.4,
        0.5,
        0,
        0,
        -0.5,
        2,
        -0.4,
        0,
        0,
        -0.4,
        -0.5,
        3,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, multiply);
    }
    /** Erase the current matrix by setting the default one. */
    reset() {
      const matrix = [
        1,
        0,
        0,
        0,
        0,
        0,
        1,
        0,
        0,
        0,
        0,
        0,
        1,
        0,
        0,
        0,
        0,
        0,
        1,
        0
      ];
      this._loadMatrix(matrix, false);
    }
    /**
     * The matrix of the color matrix filter
     * @member {number[]}
     * @default [1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0]
     */
    get matrix() {
      return this.uniforms.m;
    }
    set matrix(value) {
      this.uniforms.m = value;
    }
    /**
     * The opacity value to use when mixing the original and resultant colors.
     *
     * When the value is 0, the original color is used without modification.
     * When the value is 1, the result color is used.
     * When in the range (0, 1) the color is interpolated between the original and result by this amount.
     * @default 1
     */
    get alpha() {
      return this.uniforms.uAlpha;
    }
    set alpha(value) {
      this.uniforms.uAlpha = value;
    }
  }
  ColorMatrixFilter.prototype.grayscale = ColorMatrixFilter.prototype.greyscale;
  const tempPoint = new Point(), indices = new Uint16Array([0, 1, 2, 0, 2, 3]);
  class Sprite extends Container {
    /** @param texture - The texture for this sprite. */
    constructor(texture) {
      super(), this._anchor = new ObservablePoint(
        this._onAnchorUpdate,
        this,
        texture ? texture.defaultAnchor.x : 0,
        texture ? texture.defaultAnchor.y : 0
      ), this._texture = null, this._width = 0, this._height = 0, this._tintColor = new Color(16777215), this._tintRGB = null, this.tint = 16777215, this.blendMode = BLEND_MODES.NORMAL, this._cachedTint = 16777215, this.uvs = null, this.texture = texture || Texture.EMPTY, this.vertexData = new Float32Array(8), this.vertexTrimmedData = null, this._transformID = -1, this._textureID = -1, this._transformTrimmedID = -1, this._textureTrimmedID = -1, this.indices = indices, this.pluginName = "batch", this.isSprite = true, this._roundPixels = settings.ROUND_PIXELS;
    }
    /** When the texture is updated, this event will fire to update the scale and frame. */
    _onTextureUpdate() {
      this._textureID = -1, this._textureTrimmedID = -1, this._cachedTint = 16777215, this._width && (this.scale.x = sign(this.scale.x) * this._width / this._texture.orig.width), this._height && (this.scale.y = sign(this.scale.y) * this._height / this._texture.orig.height);
    }
    /** Called when the anchor position updates. */
    _onAnchorUpdate() {
      this._transformID = -1, this._transformTrimmedID = -1;
    }
    /** Calculates worldTransform * vertices, store it in vertexData. */
    calculateVertices() {
      const texture = this._texture;
      if (this._transformID === this.transform._worldID && this._textureID === texture._updateID)
        return;
      this._textureID !== texture._updateID && (this.uvs = this._texture._uvs.uvsFloat32), this._transformID = this.transform._worldID, this._textureID = texture._updateID;
      const wt2 = this.transform.worldTransform, a2 = wt2.a, b2 = wt2.b, c2 = wt2.c, d2 = wt2.d, tx = wt2.tx, ty = wt2.ty, vertexData = this.vertexData, trim = texture.trim, orig = texture.orig, anchor = this._anchor;
      let w0 = 0, w1 = 0, h0 = 0, h1 = 0;
      if (trim ? (w1 = trim.x - anchor._x * orig.width, w0 = w1 + trim.width, h1 = trim.y - anchor._y * orig.height, h0 = h1 + trim.height) : (w1 = -anchor._x * orig.width, w0 = w1 + orig.width, h1 = -anchor._y * orig.height, h0 = h1 + orig.height), vertexData[0] = a2 * w1 + c2 * h1 + tx, vertexData[1] = d2 * h1 + b2 * w1 + ty, vertexData[2] = a2 * w0 + c2 * h1 + tx, vertexData[3] = d2 * h1 + b2 * w0 + ty, vertexData[4] = a2 * w0 + c2 * h0 + tx, vertexData[5] = d2 * h0 + b2 * w0 + ty, vertexData[6] = a2 * w1 + c2 * h0 + tx, vertexData[7] = d2 * h0 + b2 * w1 + ty, this._roundPixels) {
        const resolution = settings.RESOLUTION;
        for (let i2 = 0; i2 < vertexData.length; ++i2)
          vertexData[i2] = Math.round(vertexData[i2] * resolution) / resolution;
      }
    }
    /**
     * Calculates worldTransform * vertices for a non texture with a trim. store it in vertexTrimmedData.
     *
     * This is used to ensure that the true width and height of a trimmed texture is respected.
     */
    calculateTrimmedVertices() {
      if (!this.vertexTrimmedData)
        this.vertexTrimmedData = new Float32Array(8);
      else if (this._transformTrimmedID === this.transform._worldID && this._textureTrimmedID === this._texture._updateID)
        return;
      this._transformTrimmedID = this.transform._worldID, this._textureTrimmedID = this._texture._updateID;
      const texture = this._texture, vertexData = this.vertexTrimmedData, orig = texture.orig, anchor = this._anchor, wt2 = this.transform.worldTransform, a2 = wt2.a, b2 = wt2.b, c2 = wt2.c, d2 = wt2.d, tx = wt2.tx, ty = wt2.ty, w1 = -anchor._x * orig.width, w0 = w1 + orig.width, h1 = -anchor._y * orig.height, h0 = h1 + orig.height;
      if (vertexData[0] = a2 * w1 + c2 * h1 + tx, vertexData[1] = d2 * h1 + b2 * w1 + ty, vertexData[2] = a2 * w0 + c2 * h1 + tx, vertexData[3] = d2 * h1 + b2 * w0 + ty, vertexData[4] = a2 * w0 + c2 * h0 + tx, vertexData[5] = d2 * h0 + b2 * w0 + ty, vertexData[6] = a2 * w1 + c2 * h0 + tx, vertexData[7] = d2 * h0 + b2 * w1 + ty, this._roundPixels) {
        const resolution = settings.RESOLUTION;
        for (let i2 = 0; i2 < vertexData.length; ++i2)
          vertexData[i2] = Math.round(vertexData[i2] * resolution) / resolution;
      }
    }
    /**
     *
     * Renders the object using the WebGL renderer
     * @param renderer - The webgl renderer to use.
     */
    _render(renderer) {
      this.calculateVertices(), renderer.batch.setObjectRenderer(renderer.plugins[this.pluginName]), renderer.plugins[this.pluginName].render(this);
    }
    /** Updates the bounds of the sprite. */
    _calculateBounds() {
      const trim = this._texture.trim, orig = this._texture.orig;
      !trim || trim.width === orig.width && trim.height === orig.height ? (this.calculateVertices(), this._bounds.addQuad(this.vertexData)) : (this.calculateTrimmedVertices(), this._bounds.addQuad(this.vertexTrimmedData));
    }
    /**
     * Gets the local bounds of the sprite object.
     * @param rect - Optional output rectangle.
     * @returns The bounds.
     */
    getLocalBounds(rect) {
      return this.children.length === 0 ? (this._localBounds || (this._localBounds = new Bounds()), this._localBounds.minX = this._texture.orig.width * -this._anchor._x, this._localBounds.minY = this._texture.orig.height * -this._anchor._y, this._localBounds.maxX = this._texture.orig.width * (1 - this._anchor._x), this._localBounds.maxY = this._texture.orig.height * (1 - this._anchor._y), rect || (this._localBoundsRect || (this._localBoundsRect = new Rectangle()), rect = this._localBoundsRect), this._localBounds.getRectangle(rect)) : super.getLocalBounds.call(this, rect);
    }
    /**
     * Tests if a point is inside this sprite
     * @param point - the point to test
     * @returns The result of the test
     */
    containsPoint(point) {
      this.worldTransform.applyInverse(point, tempPoint);
      const width = this._texture.orig.width, height = this._texture.orig.height, x1 = -width * this.anchor.x;
      let y1 = 0;
      return tempPoint.x >= x1 && tempPoint.x < x1 + width && (y1 = -height * this.anchor.y, tempPoint.y >= y1 && tempPoint.y < y1 + height);
    }
    /**
     * Destroys this sprite and optionally its texture and children.
     * @param options - Options parameter. A boolean will act as if all options
     *  have been set to that value
     * @param [options.children=false] - if set to true, all the children will have their destroy
     *      method called as well. 'options' will be passed on to those calls.
     * @param [options.texture=false] - Should it destroy the current texture of the sprite as well
     * @param [options.baseTexture=false] - Should it destroy the base texture of the sprite as well
     */
    destroy(options) {
      if (super.destroy(options), this._texture.off("update", this._onTextureUpdate, this), this._anchor = null, typeof options == "boolean" ? options : options?.texture) {
        const destroyBaseTexture = typeof options == "boolean" ? options : options?.baseTexture;
        this._texture.destroy(!!destroyBaseTexture);
      }
      this._texture = null;
    }
    // some helper functions..
    /**
     * Helper function that creates a new sprite based on the source you provide.
     * The source can be - frame id, image url, video url, canvas element, video element, base texture
     * @param {string|PIXI.Texture|HTMLImageElement|HTMLVideoElement|ImageBitmap|PIXI.ICanvas} source
     *     - Source to create texture from
     * @param {object} [options] - See {@link PIXI.BaseTexture}'s constructor for options.
     * @returns The newly created sprite
     */
    static from(source, options) {
      const texture = source instanceof Texture ? source : Texture.from(source, options);
      return new Sprite(texture);
    }
    /**
     * If true PixiJS will Math.floor() x/y values when rendering, stopping pixel interpolation.
     *
     * Advantages can include sharper image quality (like text) and faster rendering on canvas.
     * The main disadvantage is movement of objects may appear less smooth.
     *
     * To set the global default, change {@link PIXI.settings.ROUND_PIXELS}.
     * @default false
     */
    set roundPixels(value) {
      this._roundPixels !== value && (this._transformID = -1, this._transformTrimmedID = -1), this._roundPixels = value;
    }
    get roundPixels() {
      return this._roundPixels;
    }
    /** The width of the sprite, setting this will actually modify the scale to achieve the value set. */
    get width() {
      return Math.abs(this.scale.x) * this._texture.orig.width;
    }
    set width(value) {
      const s2 = sign(this.scale.x) || 1;
      this.scale.x = s2 * value / this._texture.orig.width, this._width = value;
    }
    /** The height of the sprite, setting this will actually modify the scale to achieve the value set. */
    get height() {
      return Math.abs(this.scale.y) * this._texture.orig.height;
    }
    set height(value) {
      const s2 = sign(this.scale.y) || 1;
      this.scale.y = s2 * value / this._texture.orig.height, this._height = value;
    }
    /**
     * The anchor sets the origin point of the sprite. The default value is taken from the {@link PIXI.Texture|Texture}
     * and passed to the constructor.
     *
     * The default is `(0,0)`, this means the sprite's origin is the top left.
     *
     * Setting the anchor to `(0.5,0.5)` means the sprite's origin is centered.
     *
     * Setting the anchor to `(1,1)` would mean the sprite's origin point will be the bottom right corner.
     *
     * If you pass only single parameter, it will set both x and y to the same value as shown in the example below.
     * @example
     * import { Sprite } from 'pixi.js';
     *
     * const sprite = new Sprite(Texture.WHITE);
     * sprite.anchor.set(0.5); // This will set the origin to center. (0.5) is same as (0.5, 0.5).
     */
    get anchor() {
      return this._anchor;
    }
    set anchor(value) {
      this._anchor.copyFrom(value);
    }
    /**
     * The tint applied to the sprite. This is a hex value.
     *
     * A value of 0xFFFFFF will remove any tint effect.
     * @default 0xFFFFFF
     */
    get tint() {
      return this._tintColor.value;
    }
    set tint(value) {
      this._tintColor.setValue(value), this._tintRGB = this._tintColor.toLittleEndianNumber();
    }
    /**
     * Get the tint as a RGB integer.
     * @ignore
     */
    get tintValue() {
      return this._tintColor.toNumber();
    }
    /** The texture that the sprite is using. */
    get texture() {
      return this._texture;
    }
    set texture(value) {
      this._texture !== value && (this._texture && this._texture.off("update", this._onTextureUpdate, this), this._texture = value || Texture.EMPTY, this._cachedTint = 16777215, this._textureID = -1, this._textureTrimmedID = -1, value && (value.baseTexture.valid ? this._onTextureUpdate() : value.once("update", this._onTextureUpdate, this)));
    }
  }
  class Le {
  }
  class te extends Le {
    constructor(t2) {
      super(), this.canvas = t2, this.observer = new ResizeObserver(() => {
        const e2 = Math.max(
          1,
          t2.clientWidth * window.devicePixelRatio * this.currerntRenderScale
        ), s2 = Math.max(
          1,
          t2.clientHeight * window.devicePixelRatio * this.currerntRenderScale
        );
        this.onResize(e2, s2);
      }), this.observer.observe(t2);
    }
    observer;
    flowSpeed = 4;
    currerntRenderScale = 0.75;
    setRenderScale(t2) {
      this.currerntRenderScale = t2, this.onResize(
        this.canvas.clientWidth * window.devicePixelRatio * this.currerntRenderScale,
        this.canvas.clientHeight * window.devicePixelRatio * this.currerntRenderScale
      );
    }
    /**
     * 当画板元素大小发生变化时此函数会被调用
     * 可以在此处重设和渲染器相关的尺寸设置
     * 考虑到初始化的时候元素不一定在文档中或出于某些特殊样式状态，尺寸长宽有可能会为 0，请注意进行特判处理
     * @param width 画板元素实际的物理像素宽度，有可能为 0
     * @param height 画板元素实际的物理像素高度，有可能为 0
     */
    onResize(t2, e2) {
      this.canvas.width = t2, this.canvas.height = e2;
    }
    /**
     * 修改背景的流动速度，数字越大越快，默认为 4
     * @param speed 背景的流动速度，默认为 4
     */
    setFlowSpeed(t2) {
      this.flowSpeed = t2;
    }
    dispose() {
      this.observer.disconnect(), this.canvas.remove();
    }
    getElement() {
      return this.canvas;
    }
  }
  const O = 1e-6, Bt = new Float32Array([
    1,
    0,
    0,
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    1
  ]);
  class I extends Float32Array {
    /**
     * The number of bytes in a {@link Mat4}.
     */
    static BYTE_LENGTH = 16 * Float32Array.BYTES_PER_ELEMENT;
    /**
     * Create a {@link Mat4}.
     */
    constructor(...t2) {
      switch (t2.length) {
        case 16:
          super(t2);
          break;
        case 2:
          super(t2[0], t2[1], 16);
          break;
        case 1:
          const e2 = t2[0];
          typeof e2 == "number" ? super([
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2,
            e2
          ]) : super(e2, 0, 16);
          break;
        default:
          super(Bt);
          break;
      }
    }
    //============
    // Attributes
    //============
    /**
     * A string representation of `this`
     * Equivalent to `Mat4.str(this);`
     */
    get str() {
      return I.str(this);
    }
    //===================
    // Instance methods
    //===================
    /**
     * Copy the values from another {@link Mat4} into `this`.
     *
     * @param a the source vector
     * @returns `this`
     */
    copy(t2) {
      return this.set(t2), this;
    }
    /**
     * Set `this` to the identity matrix
     * Equivalent to Mat4.identity(this)
     *
     * @returns `this`
     */
    identity() {
      return this.set(Bt), this;
    }
    /**
     * Multiplies this {@link Mat4} against another one
     * Equivalent to `Mat4.multiply(this, this, b);`
     *
     * @param out - The receiving Matrix
     * @param a - The first operand
     * @param b - The second operand
     * @returns `this`
     */
    multiply(t2) {
      return I.multiply(this, this, t2);
    }
    /**
     * Alias for {@link Mat4.multiply}
     */
    mul(t2) {
      return this;
    }
    /**
     * Transpose this {@link Mat4}
     * Equivalent to `Mat4.transpose(this, this);`
     *
     * @returns `this`
     */
    transpose() {
      return I.transpose(this, this);
    }
    /**
     * Inverts this {@link Mat4}
     * Equivalent to `Mat4.invert(this, this);`
     *
     * @returns `this`
     */
    invert() {
      return I.invert(this, this);
    }
    /**
     * Translate this {@link Mat4} by the given vector
     * Equivalent to `Mat4.translate(this, this, v);`
     *
     * @param v - The {@link Vec3} to translate by
     * @returns `this`
     */
    translate(t2) {
      return I.translate(this, this, t2);
    }
    /**
     * Rotates this {@link Mat4} by the given angle around the given axis
     * Equivalent to `Mat4.rotate(this, this, rad, axis);`
     *
     * @param rad - the angle to rotate the matrix by
     * @param axis - the axis to rotate around
     * @returns `out`
     */
    rotate(t2, e2) {
      return I.rotate(this, this, t2, e2);
    }
    /**
     * Scales this {@link Mat4} by the dimensions in the given vec3 not using vectorization
     * Equivalent to `Mat4.scale(this, this, v);`
     *
     * @param v - The {@link Vec3} to scale the matrix by
     * @returns `this`
     */
    scale(t2) {
      return I.scale(this, this, t2);
    }
    /**
     * Rotates this {@link Mat4} by the given angle around the X axis
     * Equivalent to `Mat4.rotateX(this, this, rad);`
     *
     * @param rad - the angle to rotate the matrix by
     * @returns `this`
     */
    rotateX(t2) {
      return I.rotateX(this, this, t2);
    }
    /**
     * Rotates this {@link Mat4} by the given angle around the Y axis
     * Equivalent to `Mat4.rotateY(this, this, rad);`
     *
     * @param rad - the angle to rotate the matrix by
     * @returns `this`
     */
    rotateY(t2) {
      return I.rotateY(this, this, t2);
    }
    /**
     * Rotates this {@link Mat4} by the given angle around the Z axis
     * Equivalent to `Mat4.rotateZ(this, this, rad);`
     *
     * @param rad - the angle to rotate the matrix by
     * @returns `this`
     */
    rotateZ(t2) {
      return I.rotateZ(this, this, t2);
    }
    /**
     * Generates a perspective projection matrix with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [-1, 1],
     * which matches WebGL/OpenGL's clip volume.
     * Passing null/undefined/no value for far will generate infinite projection matrix.
     * Equivalent to `Mat4.perspectiveNO(this, fovy, aspect, near, far);`
     *
     * @param fovy - Vertical field of view in radians
     * @param aspect - Aspect ratio. typically viewport width/height
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum, can be null or Infinity
     * @returns `this`
     */
    perspectiveNO(t2, e2, s2, i2) {
      return I.perspectiveNO(this, t2, e2, s2, i2);
    }
    /**
     * Generates a perspective projection matrix suitable for WebGPU with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [0, 1],
     * which matches WebGPU/Vulkan/DirectX/Metal's clip volume.
     * Passing null/undefined/no value for far will generate infinite projection matrix.
     * Equivalent to `Mat4.perspectiveZO(this, fovy, aspect, near, far);`
     *
     * @param fovy - Vertical field of view in radians
     * @param aspect - Aspect ratio. typically viewport width/height
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum, can be null or Infinity
     * @returns `this`
     */
    perspectiveZO(t2, e2, s2, i2) {
      return I.perspectiveZO(this, t2, e2, s2, i2);
    }
    /**
     * Generates a orthogonal projection matrix with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [-1, 1],
     * which matches WebGL/OpenGL's clip volume.
     * Equivalent to `Mat4.orthoNO(this, left, right, bottom, top, near, far);`
     *
     * @param left - Left bound of the frustum
     * @param right - Right bound of the frustum
     * @param bottom - Bottom bound of the frustum
     * @param top - Top bound of the frustum
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum
     * @returns `this`
     */
    orthoNO(t2, e2, s2, i2, n2, r2) {
      return I.orthoNO(this, t2, e2, s2, i2, n2, r2);
    }
    /**
     * Generates a orthogonal projection matrix with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [0, 1],
     * which matches WebGPU/Vulkan/DirectX/Metal's clip volume.
     * Equivalent to `Mat4.orthoZO(this, left, right, bottom, top, near, far);`
     *
     * @param left - Left bound of the frustum
     * @param right - Right bound of the frustum
     * @param bottom - Bottom bound of the frustum
     * @param top - Top bound of the frustum
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum
     * @returns `this`
     */
    orthoZO(t2, e2, s2, i2, n2, r2) {
      return I.orthoZO(this, t2, e2, s2, i2, n2, r2);
    }
    //================
    // Static methods
    //================
    /**
     * Creates a new, identity {@link Mat4}
     * @category Static
     *
     * @returns A new {@link Mat4}
     */
    static create() {
      return new I();
    }
    /**
     * Creates a new {@link Mat4} initialized with values from an existing matrix
     * @category Static
     *
     * @param a - Matrix to clone
     * @returns A new {@link Mat4}
     */
    static clone(t2) {
      return new I(t2);
    }
    /**
     * Copy the values from one {@link Mat4} to another
     * @category Static
     *
     * @param out - The receiving Matrix
     * @param a - Matrix to copy
     * @returns `out`
     */
    static copy(t2, e2) {
      return t2[0] = e2[0], t2[1] = e2[1], t2[2] = e2[2], t2[3] = e2[3], t2[4] = e2[4], t2[5] = e2[5], t2[6] = e2[6], t2[7] = e2[7], t2[8] = e2[8], t2[9] = e2[9], t2[10] = e2[10], t2[11] = e2[11], t2[12] = e2[12], t2[13] = e2[13], t2[14] = e2[14], t2[15] = e2[15], t2;
    }
    /**
     * Create a new mat4 with the given values
     * @category Static
     *
     * @param values - Matrix components
     * @returns A new {@link Mat4}
     */
    static fromValues(...t2) {
      return new I(...t2);
    }
    /**
     * Set the components of a mat4 to the given values
     * @category Static
     *
     * @param out - The receiving matrix
     * @param values - Matrix components
     * @returns `out`
     */
    static set(t2, ...e2) {
      return t2[0] = e2[0], t2[1] = e2[1], t2[2] = e2[2], t2[3] = e2[3], t2[4] = e2[4], t2[5] = e2[5], t2[6] = e2[6], t2[7] = e2[7], t2[8] = e2[8], t2[9] = e2[9], t2[10] = e2[10], t2[11] = e2[11], t2[12] = e2[12], t2[13] = e2[13], t2[14] = e2[14], t2[15] = e2[15], t2;
    }
    /**
     * Set a {@link Mat4} to the identity matrix
     * @category Static
     *
     * @param out - The receiving Matrix
     * @returns `out`
     */
    static identity(t2) {
      return t2[0] = 1, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = 1, t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[10] = 1, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Transpose the values of a {@link Mat4}
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the source matrix
     * @returns `out`
     */
    static transpose(t2, e2) {
      if (t2 === e2) {
        const s2 = e2[1], i2 = e2[2], n2 = e2[3], r2 = e2[6], a2 = e2[7], o2 = e2[11];
        t2[1] = e2[4], t2[2] = e2[8], t2[3] = e2[12], t2[4] = s2, t2[6] = e2[9], t2[7] = e2[13], t2[8] = i2, t2[9] = r2, t2[11] = e2[14], t2[12] = n2, t2[13] = a2, t2[14] = o2;
      } else
        t2[0] = e2[0], t2[1] = e2[4], t2[2] = e2[8], t2[3] = e2[12], t2[4] = e2[1], t2[5] = e2[5], t2[6] = e2[9], t2[7] = e2[13], t2[8] = e2[2], t2[9] = e2[6], t2[10] = e2[10], t2[11] = e2[14], t2[12] = e2[3], t2[13] = e2[7], t2[14] = e2[11], t2[15] = e2[15];
      return t2;
    }
    /**
     * Inverts a {@link Mat4}
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the source matrix
     * @returns `out` or `null` if the matrix is not invertable
     */
    static invert(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2], r2 = e2[3], a2 = e2[4], o2 = e2[5], l2 = e2[6], c2 = e2[7], h2 = e2[8], m2 = e2[9], u2 = e2[10], f2 = e2[11], g2 = e2[12], p2 = e2[13], y2 = e2[14], L = e2[15], x2 = s2 * o2 - i2 * a2, M2 = s2 * l2 - n2 * a2, b2 = s2 * c2 - r2 * a2, w2 = i2 * l2 - n2 * o2, T = i2 * c2 - r2 * o2, v2 = n2 * c2 - r2 * l2, P = h2 * p2 - m2 * g2, z = h2 * y2 - u2 * g2, E = h2 * L - f2 * g2, F = m2 * y2 - u2 * p2, _ = m2 * L - f2 * p2, R = u2 * L - f2 * y2;
      let k2 = x2 * R - M2 * _ + b2 * F + w2 * E - T * z + v2 * P;
      return k2 ? (k2 = 1 / k2, t2[0] = (o2 * R - l2 * _ + c2 * F) * k2, t2[1] = (n2 * _ - i2 * R - r2 * F) * k2, t2[2] = (p2 * v2 - y2 * T + L * w2) * k2, t2[3] = (u2 * T - m2 * v2 - f2 * w2) * k2, t2[4] = (l2 * E - a2 * R - c2 * z) * k2, t2[5] = (s2 * R - n2 * E + r2 * z) * k2, t2[6] = (y2 * b2 - g2 * v2 - L * M2) * k2, t2[7] = (h2 * v2 - u2 * b2 + f2 * M2) * k2, t2[8] = (a2 * _ - o2 * E + c2 * P) * k2, t2[9] = (i2 * E - s2 * _ - r2 * P) * k2, t2[10] = (g2 * T - p2 * b2 + L * x2) * k2, t2[11] = (m2 * b2 - h2 * T - f2 * x2) * k2, t2[12] = (o2 * z - a2 * F - l2 * P) * k2, t2[13] = (s2 * F - i2 * z + n2 * P) * k2, t2[14] = (p2 * M2 - g2 * w2 - y2 * x2) * k2, t2[15] = (h2 * w2 - m2 * M2 + u2 * x2) * k2, t2) : null;
    }
    /**
     * Calculates the adjugate of a {@link Mat4}
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the source matrix
     * @returns `out`
     */
    static adjoint(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2], r2 = e2[3], a2 = e2[4], o2 = e2[5], l2 = e2[6], c2 = e2[7], h2 = e2[8], m2 = e2[9], u2 = e2[10], f2 = e2[11], g2 = e2[12], p2 = e2[13], y2 = e2[14], L = e2[15], x2 = s2 * o2 - i2 * a2, M2 = s2 * l2 - n2 * a2, b2 = s2 * c2 - r2 * a2, w2 = i2 * l2 - n2 * o2, T = i2 * c2 - r2 * o2, v2 = n2 * c2 - r2 * l2, P = h2 * p2 - m2 * g2, z = h2 * y2 - u2 * g2, E = h2 * L - f2 * g2, F = m2 * y2 - u2 * p2, _ = m2 * L - f2 * p2, R = u2 * L - f2 * y2;
      return t2[0] = o2 * R - l2 * _ + c2 * F, t2[1] = n2 * _ - i2 * R - r2 * F, t2[2] = p2 * v2 - y2 * T + L * w2, t2[3] = u2 * T - m2 * v2 - f2 * w2, t2[4] = l2 * E - a2 * R - c2 * z, t2[5] = s2 * R - n2 * E + r2 * z, t2[6] = y2 * b2 - g2 * v2 - L * M2, t2[7] = h2 * v2 - u2 * b2 + f2 * M2, t2[8] = a2 * _ - o2 * E + c2 * P, t2[9] = i2 * E - s2 * _ - r2 * P, t2[10] = g2 * T - p2 * b2 + L * x2, t2[11] = m2 * b2 - h2 * T - f2 * x2, t2[12] = o2 * z - a2 * F - l2 * P, t2[13] = s2 * F - i2 * z + n2 * P, t2[14] = p2 * M2 - g2 * w2 - y2 * x2, t2[15] = h2 * w2 - m2 * M2 + u2 * x2, t2;
    }
    /**
     * Calculates the determinant of a {@link Mat4}
     * @category Static
     *
     * @param a - the source matrix
     * @returns determinant of a
     */
    static determinant(t2) {
      const e2 = t2[0], s2 = t2[1], i2 = t2[2], n2 = t2[3], r2 = t2[4], a2 = t2[5], o2 = t2[6], l2 = t2[7], c2 = t2[8], h2 = t2[9], m2 = t2[10], u2 = t2[11], f2 = t2[12], g2 = t2[13], p2 = t2[14], y2 = t2[15], L = e2 * a2 - s2 * r2, x2 = e2 * o2 - i2 * r2, M2 = s2 * o2 - i2 * a2, b2 = c2 * g2 - h2 * f2, w2 = c2 * p2 - m2 * f2, T = h2 * p2 - m2 * g2, v2 = e2 * T - s2 * w2 + i2 * b2, P = r2 * T - a2 * w2 + o2 * b2, z = c2 * M2 - h2 * x2 + m2 * L, E = f2 * M2 - g2 * x2 + p2 * L;
      return l2 * v2 - n2 * P + y2 * z - u2 * E;
    }
    /**
     * Multiplies two {@link Mat4}s
     * @category Static
     *
     * @param out - The receiving Matrix
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static multiply(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1], r2 = e2[2], a2 = e2[3], o2 = e2[4], l2 = e2[5], c2 = e2[6], h2 = e2[7], m2 = e2[8], u2 = e2[9], f2 = e2[10], g2 = e2[11], p2 = e2[12], y2 = e2[13], L = e2[14], x2 = e2[15];
      let M2 = s2[0], b2 = s2[1], w2 = s2[2], T = s2[3];
      return t2[0] = M2 * i2 + b2 * o2 + w2 * m2 + T * p2, t2[1] = M2 * n2 + b2 * l2 + w2 * u2 + T * y2, t2[2] = M2 * r2 + b2 * c2 + w2 * f2 + T * L, t2[3] = M2 * a2 + b2 * h2 + w2 * g2 + T * x2, M2 = s2[4], b2 = s2[5], w2 = s2[6], T = s2[7], t2[4] = M2 * i2 + b2 * o2 + w2 * m2 + T * p2, t2[5] = M2 * n2 + b2 * l2 + w2 * u2 + T * y2, t2[6] = M2 * r2 + b2 * c2 + w2 * f2 + T * L, t2[7] = M2 * a2 + b2 * h2 + w2 * g2 + T * x2, M2 = s2[8], b2 = s2[9], w2 = s2[10], T = s2[11], t2[8] = M2 * i2 + b2 * o2 + w2 * m2 + T * p2, t2[9] = M2 * n2 + b2 * l2 + w2 * u2 + T * y2, t2[10] = M2 * r2 + b2 * c2 + w2 * f2 + T * L, t2[11] = M2 * a2 + b2 * h2 + w2 * g2 + T * x2, M2 = s2[12], b2 = s2[13], w2 = s2[14], T = s2[15], t2[12] = M2 * i2 + b2 * o2 + w2 * m2 + T * p2, t2[13] = M2 * n2 + b2 * l2 + w2 * u2 + T * y2, t2[14] = M2 * r2 + b2 * c2 + w2 * f2 + T * L, t2[15] = M2 * a2 + b2 * h2 + w2 * g2 + T * x2, t2;
    }
    /**
     * Alias for {@link Mat4.multiply}
     * @category Static
     */
    static mul(t2, e2, s2) {
      return t2;
    }
    /**
     * Translate a {@link Mat4} by the given vector
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the matrix to translate
     * @param v - vector to translate by
     * @returns `out`
     */
    static translate(t2, e2, s2) {
      const i2 = s2[0], n2 = s2[1], r2 = s2[2];
      if (e2 === t2)
        t2[12] = e2[0] * i2 + e2[4] * n2 + e2[8] * r2 + e2[12], t2[13] = e2[1] * i2 + e2[5] * n2 + e2[9] * r2 + e2[13], t2[14] = e2[2] * i2 + e2[6] * n2 + e2[10] * r2 + e2[14], t2[15] = e2[3] * i2 + e2[7] * n2 + e2[11] * r2 + e2[15];
      else {
        const a2 = e2[0], o2 = e2[1], l2 = e2[2], c2 = e2[3], h2 = e2[4], m2 = e2[5], u2 = e2[6], f2 = e2[7], g2 = e2[8], p2 = e2[9], y2 = e2[10], L = e2[11];
        t2[0] = a2, t2[1] = o2, t2[2] = l2, t2[3] = c2, t2[4] = h2, t2[5] = m2, t2[6] = u2, t2[7] = f2, t2[8] = g2, t2[9] = p2, t2[10] = y2, t2[11] = L, t2[12] = a2 * i2 + h2 * n2 + g2 * r2 + e2[12], t2[13] = o2 * i2 + m2 * n2 + p2 * r2 + e2[13], t2[14] = l2 * i2 + u2 * n2 + y2 * r2 + e2[14], t2[15] = c2 * i2 + f2 * n2 + L * r2 + e2[15];
      }
      return t2;
    }
    /**
     * Scales the {@link Mat4} by the dimensions in the given {@link Vec3} not using vectorization
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the matrix to scale
     * @param v - the {@link Vec3} to scale the matrix by
     * @returns `out`
     **/
    static scale(t2, e2, s2) {
      const i2 = s2[0], n2 = s2[1], r2 = s2[2];
      return t2[0] = e2[0] * i2, t2[1] = e2[1] * i2, t2[2] = e2[2] * i2, t2[3] = e2[3] * i2, t2[4] = e2[4] * n2, t2[5] = e2[5] * n2, t2[6] = e2[6] * n2, t2[7] = e2[7] * n2, t2[8] = e2[8] * r2, t2[9] = e2[9] * r2, t2[10] = e2[10] * r2, t2[11] = e2[11] * r2, t2[12] = e2[12], t2[13] = e2[13], t2[14] = e2[14], t2[15] = e2[15], t2;
    }
    /**
     * Rotates a {@link Mat4} by the given angle around the given axis
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the matrix to rotate
     * @param rad - the angle to rotate the matrix by
     * @param axis - the axis to rotate around
     * @returns `out` or `null` if axis has a length of 0
     */
    static rotate(t2, e2, s2, i2) {
      let n2 = i2[0], r2 = i2[1], a2 = i2[2], o2 = Math.sqrt(n2 * n2 + r2 * r2 + a2 * a2);
      if (o2 < O)
        return null;
      o2 = 1 / o2, n2 *= o2, r2 *= o2, a2 *= o2;
      const l2 = Math.sin(s2), c2 = Math.cos(s2), h2 = 1 - c2, m2 = e2[0], u2 = e2[1], f2 = e2[2], g2 = e2[3], p2 = e2[4], y2 = e2[5], L = e2[6], x2 = e2[7], M2 = e2[8], b2 = e2[9], w2 = e2[10], T = e2[11], v2 = n2 * n2 * h2 + c2, P = r2 * n2 * h2 + a2 * l2, z = a2 * n2 * h2 - r2 * l2, E = n2 * r2 * h2 - a2 * l2, F = r2 * r2 * h2 + c2, _ = a2 * r2 * h2 + n2 * l2, R = n2 * a2 * h2 + r2 * l2, k2 = r2 * a2 * h2 - n2 * l2, B = a2 * a2 * h2 + c2;
      return t2[0] = m2 * v2 + p2 * P + M2 * z, t2[1] = u2 * v2 + y2 * P + b2 * z, t2[2] = f2 * v2 + L * P + w2 * z, t2[3] = g2 * v2 + x2 * P + T * z, t2[4] = m2 * E + p2 * F + M2 * _, t2[5] = u2 * E + y2 * F + b2 * _, t2[6] = f2 * E + L * F + w2 * _, t2[7] = g2 * E + x2 * F + T * _, t2[8] = m2 * R + p2 * k2 + M2 * B, t2[9] = u2 * R + y2 * k2 + b2 * B, t2[10] = f2 * R + L * k2 + w2 * B, t2[11] = g2 * R + x2 * k2 + T * B, e2 !== t2 && (t2[12] = e2[12], t2[13] = e2[13], t2[14] = e2[14], t2[15] = e2[15]), t2;
    }
    /**
     * Rotates a matrix by the given angle around the X axis
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the matrix to rotate
     * @param rad - the angle to rotate the matrix by
     * @returns `out`
     */
    static rotateX(t2, e2, s2) {
      let i2 = Math.sin(s2), n2 = Math.cos(s2), r2 = e2[4], a2 = e2[5], o2 = e2[6], l2 = e2[7], c2 = e2[8], h2 = e2[9], m2 = e2[10], u2 = e2[11];
      return e2 !== t2 && (t2[0] = e2[0], t2[1] = e2[1], t2[2] = e2[2], t2[3] = e2[3], t2[12] = e2[12], t2[13] = e2[13], t2[14] = e2[14], t2[15] = e2[15]), t2[4] = r2 * n2 + c2 * i2, t2[5] = a2 * n2 + h2 * i2, t2[6] = o2 * n2 + m2 * i2, t2[7] = l2 * n2 + u2 * i2, t2[8] = c2 * n2 - r2 * i2, t2[9] = h2 * n2 - a2 * i2, t2[10] = m2 * n2 - o2 * i2, t2[11] = u2 * n2 - l2 * i2, t2;
    }
    /**
     * Rotates a matrix by the given angle around the Y axis
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the matrix to rotate
     * @param rad - the angle to rotate the matrix by
     * @returns `out`
     */
    static rotateY(t2, e2, s2) {
      let i2 = Math.sin(s2), n2 = Math.cos(s2), r2 = e2[0], a2 = e2[1], o2 = e2[2], l2 = e2[3], c2 = e2[8], h2 = e2[9], m2 = e2[10], u2 = e2[11];
      return e2 !== t2 && (t2[4] = e2[4], t2[5] = e2[5], t2[6] = e2[6], t2[7] = e2[7], t2[12] = e2[12], t2[13] = e2[13], t2[14] = e2[14], t2[15] = e2[15]), t2[0] = r2 * n2 - c2 * i2, t2[1] = a2 * n2 - h2 * i2, t2[2] = o2 * n2 - m2 * i2, t2[3] = l2 * n2 - u2 * i2, t2[8] = r2 * i2 + c2 * n2, t2[9] = a2 * i2 + h2 * n2, t2[10] = o2 * i2 + m2 * n2, t2[11] = l2 * i2 + u2 * n2, t2;
    }
    /**
     * Rotates a matrix by the given angle around the Z axis
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the matrix to rotate
     * @param rad - the angle to rotate the matrix by
     * @returns `out`
     */
    static rotateZ(t2, e2, s2) {
      let i2 = Math.sin(s2), n2 = Math.cos(s2), r2 = e2[0], a2 = e2[1], o2 = e2[2], l2 = e2[3], c2 = e2[4], h2 = e2[5], m2 = e2[6], u2 = e2[7];
      return e2 !== t2 && (t2[8] = e2[8], t2[9] = e2[9], t2[10] = e2[10], t2[11] = e2[11], t2[12] = e2[12], t2[13] = e2[13], t2[14] = e2[14], t2[15] = e2[15]), t2[0] = r2 * n2 + c2 * i2, t2[1] = a2 * n2 + h2 * i2, t2[2] = o2 * n2 + m2 * i2, t2[3] = l2 * n2 + u2 * i2, t2[4] = c2 * n2 - r2 * i2, t2[5] = h2 * n2 - a2 * i2, t2[6] = m2 * n2 - o2 * i2, t2[7] = u2 * n2 - l2 * i2, t2;
    }
    /**
     * Creates a {@link Mat4} from a vector translation
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.translate(dest, dest, vec);
     * @category Static
     *
     * @param out - {@link Mat4} receiving operation result
     * @param v - Translation vector
     * @returns `out`
     */
    static fromTranslation(t2, e2) {
      return t2[0] = 1, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = 1, t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[10] = 1, t2[11] = 0, t2[12] = e2[0], t2[13] = e2[1], t2[14] = e2[2], t2[15] = 1, t2;
    }
    /**
     * Creates a {@link Mat4} from a vector scaling
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.scale(dest, dest, vec);
     * @category Static
     *
     * @param out - {@link Mat4} receiving operation result
     * @param v - Scaling vector
     * @returns `out`
     */
    static fromScaling(t2, e2) {
      return t2[0] = e2[0], t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = e2[1], t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[10] = e2[2], t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Creates a {@link Mat4} from a given angle around a given axis
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.rotate(dest, dest, rad, axis);
     * @category Static
     *
     * @param out - {@link Mat4} receiving operation result
     * @param rad - the angle to rotate the matrix by
     * @param axis - the axis to rotate around
     * @returns `out` or `null` if `axis` has a length of 0
     */
    static fromRotation(t2, e2, s2) {
      let i2 = s2[0], n2 = s2[1], r2 = s2[2], a2 = Math.sqrt(i2 * i2 + n2 * n2 + r2 * r2);
      if (a2 < O)
        return null;
      a2 = 1 / a2, i2 *= a2, n2 *= a2, r2 *= a2;
      const o2 = Math.sin(e2), l2 = Math.cos(e2), c2 = 1 - l2;
      return t2[0] = i2 * i2 * c2 + l2, t2[1] = n2 * i2 * c2 + r2 * o2, t2[2] = r2 * i2 * c2 - n2 * o2, t2[3] = 0, t2[4] = i2 * n2 * c2 - r2 * o2, t2[5] = n2 * n2 * c2 + l2, t2[6] = r2 * n2 * c2 + i2 * o2, t2[7] = 0, t2[8] = i2 * r2 * c2 + n2 * o2, t2[9] = n2 * r2 * c2 - i2 * o2, t2[10] = r2 * r2 * c2 + l2, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Creates a matrix from the given angle around the X axis
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.rotateX(dest, dest, rad);
     * @category Static
     *
     * @param out - mat4 receiving operation result
     * @param rad - the angle to rotate the matrix by
     * @returns `out`
     */
    static fromXRotation(t2, e2) {
      let s2 = Math.sin(e2), i2 = Math.cos(e2);
      return t2[0] = 1, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = i2, t2[6] = s2, t2[7] = 0, t2[8] = 0, t2[9] = -s2, t2[10] = i2, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Creates a matrix from the given angle around the Y axis
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.rotateY(dest, dest, rad);
     * @category Static
     *
     * @param out - mat4 receiving operation result
     * @param rad - the angle to rotate the matrix by
     * @returns `out`
     */
    static fromYRotation(t2, e2) {
      let s2 = Math.sin(e2), i2 = Math.cos(e2);
      return t2[0] = i2, t2[1] = 0, t2[2] = -s2, t2[3] = 0, t2[4] = 0, t2[5] = 1, t2[6] = 0, t2[7] = 0, t2[8] = s2, t2[9] = 0, t2[10] = i2, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Creates a matrix from the given angle around the Z axis
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.rotateZ(dest, dest, rad);
     * @category Static
     *
     * @param out - mat4 receiving operation result
     * @param rad - the angle to rotate the matrix by
     * @returns `out`
     */
    static fromZRotation(t2, e2) {
      const s2 = Math.sin(e2), i2 = Math.cos(e2);
      return t2[0] = i2, t2[1] = s2, t2[2] = 0, t2[3] = 0, t2[4] = -s2, t2[5] = i2, t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[10] = 1, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Creates a matrix from a quaternion rotation and vector translation
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.translate(dest, vec);
     *     let quatMat = mat4.create();
     *     quat4.toMat4(quat, quatMat);
     *     mat4.multiply(dest, quatMat);
     * @category Static
     *
     * @param out - mat4 receiving operation result
     * @param q - Rotation quaternion
     * @param v - Translation vector
     * @returns `out`
     */
    static fromRotationTranslation(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1], r2 = e2[2], a2 = e2[3], o2 = i2 + i2, l2 = n2 + n2, c2 = r2 + r2, h2 = i2 * o2, m2 = i2 * l2, u2 = i2 * c2, f2 = n2 * l2, g2 = n2 * c2, p2 = r2 * c2, y2 = a2 * o2, L = a2 * l2, x2 = a2 * c2;
      return t2[0] = 1 - (f2 + p2), t2[1] = m2 + x2, t2[2] = u2 - L, t2[3] = 0, t2[4] = m2 - x2, t2[5] = 1 - (h2 + p2), t2[6] = g2 + y2, t2[7] = 0, t2[8] = u2 + L, t2[9] = g2 - y2, t2[10] = 1 - (h2 + f2), t2[11] = 0, t2[12] = s2[0], t2[13] = s2[1], t2[14] = s2[2], t2[15] = 1, t2;
    }
    /**
     * Sets a {@link Mat4} from a {@link Quat2}.
     * @category Static
     *
     * @param out - Matrix
     * @param a - Dual Quaternion
     * @returns `out`
     */
    static fromQuat2(t2, e2) {
      const s2 = -e2[0], i2 = -e2[1], n2 = -e2[2], r2 = e2[3], a2 = e2[4], o2 = e2[5], l2 = e2[6], c2 = e2[7];
      let h2 = s2 * s2 + i2 * i2 + n2 * n2 + r2 * r2;
      return h2 > 0 ? (V[0] = (a2 * r2 + c2 * s2 + o2 * n2 - l2 * i2) * 2 / h2, V[1] = (o2 * r2 + c2 * i2 + l2 * s2 - a2 * n2) * 2 / h2, V[2] = (l2 * r2 + c2 * n2 + a2 * i2 - o2 * s2) * 2 / h2) : (V[0] = (a2 * r2 + c2 * s2 + o2 * n2 - l2 * i2) * 2, V[1] = (o2 * r2 + c2 * i2 + l2 * s2 - a2 * n2) * 2, V[2] = (l2 * r2 + c2 * n2 + a2 * i2 - o2 * s2) * 2), I.fromRotationTranslation(t2, e2, V), t2;
    }
    /**
     * Calculates a {@link Mat4} normal matrix (transpose inverse) from a {@link Mat4}
     * @category Static
     *
     * @param out - Matrix receiving operation result
     * @param a - Mat4 to derive the normal matrix from
     * @returns `out` or `null` if the matrix is not invertable
     */
    static normalFromMat4(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2], r2 = e2[3], a2 = e2[4], o2 = e2[5], l2 = e2[6], c2 = e2[7], h2 = e2[8], m2 = e2[9], u2 = e2[10], f2 = e2[11], g2 = e2[12], p2 = e2[13], y2 = e2[14], L = e2[15], x2 = s2 * o2 - i2 * a2, M2 = s2 * l2 - n2 * a2, b2 = s2 * c2 - r2 * a2, w2 = i2 * l2 - n2 * o2, T = i2 * c2 - r2 * o2, v2 = n2 * c2 - r2 * l2, P = h2 * p2 - m2 * g2, z = h2 * y2 - u2 * g2, E = h2 * L - f2 * g2, F = m2 * y2 - u2 * p2, _ = m2 * L - f2 * p2, R = u2 * L - f2 * y2;
      let k2 = x2 * R - M2 * _ + b2 * F + w2 * E - T * z + v2 * P;
      return k2 ? (k2 = 1 / k2, t2[0] = (o2 * R - l2 * _ + c2 * F) * k2, t2[1] = (l2 * E - a2 * R - c2 * z) * k2, t2[2] = (a2 * _ - o2 * E + c2 * P) * k2, t2[3] = 0, t2[4] = (n2 * _ - i2 * R - r2 * F) * k2, t2[5] = (s2 * R - n2 * E + r2 * z) * k2, t2[6] = (i2 * E - s2 * _ - r2 * P) * k2, t2[7] = 0, t2[8] = (p2 * v2 - y2 * T + L * w2) * k2, t2[9] = (y2 * b2 - g2 * v2 - L * M2) * k2, t2[10] = (g2 * T - p2 * b2 + L * x2) * k2, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2) : null;
    }
    /**
     * Calculates a {@link Mat4} normal matrix (transpose inverse) from a {@link Mat4}
     * This version omits the calculation of the constant factor (1/determinant), so
     * any normals transformed with it will need to be renormalized.
     * From https://stackoverflow.com/a/27616419/25968
     * @category Static
     *
     * @param out - Matrix receiving operation result
     * @param a - Mat4 to derive the normal matrix from
     * @returns `out`
     */
    static normalFromMat4Fast(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2], r2 = e2[4], a2 = e2[5], o2 = e2[6], l2 = e2[8], c2 = e2[9], h2 = e2[10];
      return t2[0] = a2 * h2 - h2 * c2, t2[1] = o2 * l2 - l2 * h2, t2[2] = r2 * c2 - c2 * l2, t2[3] = 0, t2[4] = c2 * n2 - h2 * i2, t2[5] = h2 * s2 - l2 * n2, t2[6] = l2 * i2 - c2 * s2, t2[7] = 0, t2[8] = i2 * o2 - n2 * a2, t2[9] = n2 * r2 - s2 * o2, t2[10] = s2 * a2 - i2 * r2, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Returns the translation vector component of a transformation
     * matrix. If a matrix is built with fromRotationTranslation,
     * the returned vector will be the same as the translation vector
     * originally supplied.
     * @category Static
     *
     * @param  {vec3} out Vector to receive translation component
     * @param  {ReadonlyMat4} mat Matrix to be decomposed (input)
     * @return {vec3} out
     */
    static getTranslation(t2, e2) {
      return t2[0] = e2[12], t2[1] = e2[13], t2[2] = e2[14], t2;
    }
    /**
     * Returns the scaling factor component of a transformation
     * matrix. If a matrix is built with fromRotationTranslationScale
     * with a normalized Quaternion parameter, the returned vector will be
     * the same as the scaling vector
     * originally supplied.
     * @category Static
     *
     * @param  {vec3} out Vector to receive scaling factor component
     * @param  {ReadonlyMat4} mat Matrix to be decomposed (input)
     * @return {vec3} out
     */
    static getScaling(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2], r2 = e2[4], a2 = e2[5], o2 = e2[6], l2 = e2[8], c2 = e2[9], h2 = e2[10];
      return t2[0] = Math.sqrt(s2 * s2 + i2 * i2 + n2 * n2), t2[1] = Math.sqrt(r2 * r2 + a2 * a2 + o2 * o2), t2[2] = Math.sqrt(l2 * l2 + c2 * c2 + h2 * h2), t2;
    }
    /**
     * Returns a quaternion representing the rotational component
     * of a transformation matrix. If a matrix is built with
     * fromRotationTranslation, the returned quaternion will be the
     * same as the quaternion originally supplied.
     * @category Static
     *
     * @param out - Quaternion to receive the rotation component
     * @param mat - Matrix to be decomposed (input)
     * @return `out`
     */
    static getRotation(t2, e2) {
      I.getScaling(V, e2);
      const s2 = 1 / V[0], i2 = 1 / V[1], n2 = 1 / V[2], r2 = e2[0] * s2, a2 = e2[1] * i2, o2 = e2[2] * n2, l2 = e2[4] * s2, c2 = e2[5] * i2, h2 = e2[6] * n2, m2 = e2[8] * s2, u2 = e2[9] * i2, f2 = e2[10] * n2, g2 = r2 + c2 + f2;
      let p2 = 0;
      return g2 > 0 ? (p2 = Math.sqrt(g2 + 1) * 2, t2[3] = 0.25 * p2, t2[0] = (h2 - u2) / p2, t2[1] = (m2 - o2) / p2, t2[2] = (a2 - l2) / p2) : r2 > c2 && r2 > f2 ? (p2 = Math.sqrt(1 + r2 - c2 - f2) * 2, t2[3] = (h2 - u2) / p2, t2[0] = 0.25 * p2, t2[1] = (a2 + l2) / p2, t2[2] = (m2 + o2) / p2) : c2 > f2 ? (p2 = Math.sqrt(1 + c2 - r2 - f2) * 2, t2[3] = (m2 - o2) / p2, t2[0] = (a2 + l2) / p2, t2[1] = 0.25 * p2, t2[2] = (h2 + u2) / p2) : (p2 = Math.sqrt(1 + f2 - r2 - c2) * 2, t2[3] = (a2 - l2) / p2, t2[0] = (m2 + o2) / p2, t2[1] = (h2 + u2) / p2, t2[2] = 0.25 * p2), t2;
    }
    /**
     * Decomposes a transformation matrix into its rotation, translation
     * and scale components. Returns only the rotation component
     * @category Static
     *
     * @param out_r - Quaternion to receive the rotation component
     * @param out_t - Vector to receive the translation vector
     * @param out_s - Vector to receive the scaling factor
     * @param mat - Matrix to be decomposed (input)
     * @returns `out_r`
     */
    static decompose(t2, e2, s2, i2) {
      e2[0] = i2[12], e2[1] = i2[13], e2[2] = i2[14];
      const n2 = i2[0], r2 = i2[1], a2 = i2[2], o2 = i2[4], l2 = i2[5], c2 = i2[6], h2 = i2[8], m2 = i2[9], u2 = i2[10];
      s2[0] = Math.sqrt(n2 * n2 + r2 * r2 + a2 * a2), s2[1] = Math.sqrt(o2 * o2 + l2 * l2 + c2 * c2), s2[2] = Math.sqrt(h2 * h2 + m2 * m2 + u2 * u2);
      const f2 = 1 / s2[0], g2 = 1 / s2[1], p2 = 1 / s2[2], y2 = n2 * f2, L = r2 * g2, x2 = a2 * p2, M2 = o2 * f2, b2 = l2 * g2, w2 = c2 * p2, T = h2 * f2, v2 = m2 * g2, P = u2 * p2, z = y2 + b2 + P;
      let E = 0;
      return z > 0 ? (E = Math.sqrt(z + 1) * 2, t2[3] = 0.25 * E, t2[0] = (w2 - v2) / E, t2[1] = (T - x2) / E, t2[2] = (L - M2) / E) : y2 > b2 && y2 > P ? (E = Math.sqrt(1 + y2 - b2 - P) * 2, t2[3] = (w2 - v2) / E, t2[0] = 0.25 * E, t2[1] = (L + M2) / E, t2[2] = (T + x2) / E) : b2 > P ? (E = Math.sqrt(1 + b2 - y2 - P) * 2, t2[3] = (T - x2) / E, t2[0] = (L + M2) / E, t2[1] = 0.25 * E, t2[2] = (w2 + v2) / E) : (E = Math.sqrt(1 + P - y2 - b2) * 2, t2[3] = (L - M2) / E, t2[0] = (T + x2) / E, t2[1] = (w2 + v2) / E, t2[2] = 0.25 * E), t2;
    }
    /**
     * Creates a matrix from a quaternion rotation, vector translation and vector scale
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.translate(dest, vec);
     *     let quatMat = mat4.create();
     *     quat4.toMat4(quat, quatMat);
     *     mat4.multiply(dest, quatMat);
     *     mat4.scale(dest, scale);
     * @category Static
     *
     * @param out - mat4 receiving operation result
     * @param q - Rotation quaternion
     * @param v - Translation vector
     * @param s - Scaling vector
     * @returns `out`
     */
    static fromRotationTranslationScale(t2, e2, s2, i2) {
      const n2 = e2[0], r2 = e2[1], a2 = e2[2], o2 = e2[3], l2 = n2 + n2, c2 = r2 + r2, h2 = a2 + a2, m2 = n2 * l2, u2 = n2 * c2, f2 = n2 * h2, g2 = r2 * c2, p2 = r2 * h2, y2 = a2 * h2, L = o2 * l2, x2 = o2 * c2, M2 = o2 * h2, b2 = i2[0], w2 = i2[1], T = i2[2];
      return t2[0] = (1 - (g2 + y2)) * b2, t2[1] = (u2 + M2) * b2, t2[2] = (f2 - x2) * b2, t2[3] = 0, t2[4] = (u2 - M2) * w2, t2[5] = (1 - (m2 + y2)) * w2, t2[6] = (p2 + L) * w2, t2[7] = 0, t2[8] = (f2 + x2) * T, t2[9] = (p2 - L) * T, t2[10] = (1 - (m2 + g2)) * T, t2[11] = 0, t2[12] = s2[0], t2[13] = s2[1], t2[14] = s2[2], t2[15] = 1, t2;
    }
    /**
     * Creates a matrix from a quaternion rotation, vector translation and vector scale, rotating and scaling around the given origin
     * This is equivalent to (but much faster than):
     *
     *     mat4.identity(dest);
     *     mat4.translate(dest, vec);
     *     mat4.translate(dest, origin);
     *     let quatMat = mat4.create();
     *     quat4.toMat4(quat, quatMat);
     *     mat4.multiply(dest, quatMat);
     *     mat4.scale(dest, scale)
     *     mat4.translate(dest, negativeOrigin);
     * @category Static
     *
     * @param out - mat4 receiving operation result
     * @param q - Rotation quaternion
     * @param v - Translation vector
     * @param s - Scaling vector
     * @param o - The origin vector around which to scale and rotate
     * @returns `out`
     */
    static fromRotationTranslationScaleOrigin(t2, e2, s2, i2, n2) {
      const r2 = e2[0], a2 = e2[1], o2 = e2[2], l2 = e2[3], c2 = r2 + r2, h2 = a2 + a2, m2 = o2 + o2, u2 = r2 * c2, f2 = r2 * h2, g2 = r2 * m2, p2 = a2 * h2, y2 = a2 * m2, L = o2 * m2, x2 = l2 * c2, M2 = l2 * h2, b2 = l2 * m2, w2 = i2[0], T = i2[1], v2 = i2[2], P = n2[0], z = n2[1], E = n2[2], F = (1 - (p2 + L)) * w2, _ = (f2 + b2) * w2, R = (g2 - M2) * w2, k2 = (f2 - b2) * T, B = (1 - (u2 + L)) * T, $2 = (y2 + x2) * T, U = (g2 + M2) * v2, Y = (y2 - x2) * v2, j2 = (1 - (u2 + p2)) * v2;
      return t2[0] = F, t2[1] = _, t2[2] = R, t2[3] = 0, t2[4] = k2, t2[5] = B, t2[6] = $2, t2[7] = 0, t2[8] = U, t2[9] = Y, t2[10] = j2, t2[11] = 0, t2[12] = s2[0] + P - (F * P + k2 * z + U * E), t2[13] = s2[1] + z - (_ * P + B * z + Y * E), t2[14] = s2[2] + E - (R * P + $2 * z + j2 * E), t2[15] = 1, t2;
    }
    /**
     * Calculates a 4x4 matrix from the given quaternion
     * @category Static
     *
     * @param out - mat4 receiving operation result
     * @param q - Quaternion to create matrix from
     * @returns `out`
     */
    static fromQuat(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2], r2 = e2[3], a2 = s2 + s2, o2 = i2 + i2, l2 = n2 + n2, c2 = s2 * a2, h2 = i2 * a2, m2 = i2 * o2, u2 = n2 * a2, f2 = n2 * o2, g2 = n2 * l2, p2 = r2 * a2, y2 = r2 * o2, L = r2 * l2;
      return t2[0] = 1 - m2 - g2, t2[1] = h2 + L, t2[2] = u2 - y2, t2[3] = 0, t2[4] = h2 - L, t2[5] = 1 - c2 - g2, t2[6] = f2 + p2, t2[7] = 0, t2[8] = u2 + y2, t2[9] = f2 - p2, t2[10] = 1 - c2 - m2, t2[11] = 0, t2[12] = 0, t2[13] = 0, t2[14] = 0, t2[15] = 1, t2;
    }
    /**
     * Generates a frustum matrix with the given bounds
     * The near/far clip planes correspond to a normalized device coordinate Z range of [-1, 1],
     * which matches WebGL/OpenGL's clip volume.
     * Passing null/undefined/no value for far will generate infinite projection matrix.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param left - Left bound of the frustum
     * @param right - Right bound of the frustum
     * @param bottom - Bottom bound of the frustum
     * @param top - Top bound of the frustum
     * @param near - Near bound of the frustum
     * @param far -  Far bound of the frustum, can be null or Infinity
     * @returns `out`
     */
    static frustumNO(t2, e2, s2, i2, n2, r2, a2 = 1 / 0) {
      const o2 = 1 / (s2 - e2), l2 = 1 / (n2 - i2);
      if (t2[0] = r2 * 2 * o2, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = r2 * 2 * l2, t2[6] = 0, t2[7] = 0, t2[8] = (s2 + e2) * o2, t2[9] = (n2 + i2) * l2, t2[11] = -1, t2[12] = 0, t2[13] = 0, t2[15] = 0, a2 != null && a2 !== 1 / 0) {
        const c2 = 1 / (r2 - a2);
        t2[10] = (a2 + r2) * c2, t2[14] = 2 * a2 * r2 * c2;
      } else
        t2[10] = -1, t2[14] = -2 * r2;
      return t2;
    }
    /**
     * Alias for {@link Mat4.frustumNO}
     * @category Static
     * @deprecated Use {@link Mat4.frustumNO} or {@link Mat4.frustumZO} explicitly
     */
    static frustum(t2, e2, s2, i2, n2, r2, a2 = 1 / 0) {
      return t2;
    }
    /**
     * Generates a frustum matrix with the given bounds
     * The near/far clip planes correspond to a normalized device coordinate Z range of [0, 1],
     * which matches WebGPU/Vulkan/DirectX/Metal's clip volume.
     * Passing null/undefined/no value for far will generate infinite projection matrix.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param left - Left bound of the frustum
     * @param right - Right bound of the frustum
     * @param bottom - Bottom bound of the frustum
     * @param top - Top bound of the frustum
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum, can be null or Infinity
     * @returns `out`
     */
    static frustumZO(t2, e2, s2, i2, n2, r2, a2 = 1 / 0) {
      const o2 = 1 / (s2 - e2), l2 = 1 / (n2 - i2);
      if (t2[0] = r2 * 2 * o2, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = r2 * 2 * l2, t2[6] = 0, t2[7] = 0, t2[8] = (s2 + e2) * o2, t2[9] = (n2 + i2) * l2, t2[11] = -1, t2[12] = 0, t2[13] = 0, t2[15] = 0, a2 != null && a2 !== 1 / 0) {
        const c2 = 1 / (r2 - a2);
        t2[10] = a2 * c2, t2[14] = a2 * r2 * c2;
      } else
        t2[10] = -1, t2[14] = -r2;
      return t2;
    }
    /**
     * Generates a perspective projection matrix with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [-1, 1],
     * which matches WebGL/OpenGL's clip volume.
     * Passing null/undefined/no value for far will generate infinite projection matrix.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param fovy - Vertical field of view in radians
     * @param aspect - Aspect ratio. typically viewport width/height
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum, can be null or Infinity
     * @returns `out`
     */
    static perspectiveNO(t2, e2, s2, i2, n2 = 1 / 0) {
      const r2 = 1 / Math.tan(e2 / 2);
      if (t2[0] = r2 / s2, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = r2, t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[11] = -1, t2[12] = 0, t2[13] = 0, t2[15] = 0, n2 != null && n2 !== 1 / 0) {
        const a2 = 1 / (i2 - n2);
        t2[10] = (n2 + i2) * a2, t2[14] = 2 * n2 * i2 * a2;
      } else
        t2[10] = -1, t2[14] = -2 * i2;
      return t2;
    }
    /**
     * Alias for {@link Mat4.perspectiveNO}
     * @category Static
     * @deprecated Use {@link Mat4.perspectiveNO} or {@link Mat4.perspectiveZO} explicitly
     */
    static perspective(t2, e2, s2, i2, n2 = 1 / 0) {
      return t2;
    }
    /**
     * Generates a perspective projection matrix suitable for WebGPU with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [0, 1],
     * which matches WebGPU/Vulkan/DirectX/Metal's clip volume.
     * Passing null/undefined/no value for far will generate infinite projection matrix.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param fovy - Vertical field of view in radians
     * @param aspect - Aspect ratio. typically viewport width/height
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum, can be null or Infinity
     * @returns `out`
     */
    static perspectiveZO(t2, e2, s2, i2, n2 = 1 / 0) {
      const r2 = 1 / Math.tan(e2 / 2);
      if (t2[0] = r2 / s2, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = r2, t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[11] = -1, t2[12] = 0, t2[13] = 0, t2[15] = 0, n2 != null && n2 !== 1 / 0) {
        const a2 = 1 / (i2 - n2);
        t2[10] = n2 * a2, t2[14] = n2 * i2 * a2;
      } else
        t2[10] = -1, t2[14] = -i2;
      return t2;
    }
    /**
     * Generates a perspective projection matrix with the given field of view.
     * This is primarily useful for generating projection matrices to be used
     * with the still experiemental WebVR API.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param fov - Object containing the following values: upDegrees, downDegrees, leftDegrees, rightDegrees
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum
     * @returns `out`
     * @deprecated
     */
    static perspectiveFromFieldOfView(t2, e2, s2, i2) {
      const n2 = Math.tan(e2.upDegrees * Math.PI / 180), r2 = Math.tan(e2.downDegrees * Math.PI / 180), a2 = Math.tan(e2.leftDegrees * Math.PI / 180), o2 = Math.tan(e2.rightDegrees * Math.PI / 180), l2 = 2 / (a2 + o2), c2 = 2 / (n2 + r2);
      return t2[0] = l2, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = c2, t2[6] = 0, t2[7] = 0, t2[8] = -((a2 - o2) * l2 * 0.5), t2[9] = (n2 - r2) * c2 * 0.5, t2[10] = i2 / (s2 - i2), t2[11] = -1, t2[12] = 0, t2[13] = 0, t2[14] = i2 * s2 / (s2 - i2), t2[15] = 0, t2;
    }
    /**
     * Generates a orthogonal projection matrix with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [-1, 1],
     * which matches WebGL/OpenGL's clip volume.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param left - Left bound of the frustum
     * @param right - Right bound of the frustum
     * @param bottom - Bottom bound of the frustum
     * @param top - Top bound of the frustum
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum
     * @returns `out`
     */
    static orthoNO(t2, e2, s2, i2, n2, r2, a2) {
      const o2 = 1 / (e2 - s2), l2 = 1 / (i2 - n2), c2 = 1 / (r2 - a2);
      return t2[0] = -2 * o2, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = -2 * l2, t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[10] = 2 * c2, t2[11] = 0, t2[12] = (e2 + s2) * o2, t2[13] = (n2 + i2) * l2, t2[14] = (a2 + r2) * c2, t2[15] = 1, t2;
    }
    /**
     * Alias for {@link Mat4.orthoNO}
     * @category Static
     * @deprecated Use {@link Mat4.orthoNO} or {@link Mat4.orthoZO} explicitly
     */
    static ortho(t2, e2, s2, i2, n2, r2, a2) {
      return t2;
    }
    /**
     * Generates a orthogonal projection matrix with the given bounds.
     * The near/far clip planes correspond to a normalized device coordinate Z range of [0, 1],
     * which matches WebGPU/Vulkan/DirectX/Metal's clip volume.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param left - Left bound of the frustum
     * @param right - Right bound of the frustum
     * @param bottom - Bottom bound of the frustum
     * @param top - Top bound of the frustum
     * @param near - Near bound of the frustum
     * @param far - Far bound of the frustum
     * @returns `out`
     */
    static orthoZO(t2, e2, s2, i2, n2, r2, a2) {
      const o2 = 1 / (e2 - s2), l2 = 1 / (i2 - n2), c2 = 1 / (r2 - a2);
      return t2[0] = -2 * o2, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2[4] = 0, t2[5] = -2 * l2, t2[6] = 0, t2[7] = 0, t2[8] = 0, t2[9] = 0, t2[10] = c2, t2[11] = 0, t2[12] = (e2 + s2) * o2, t2[13] = (n2 + i2) * l2, t2[14] = r2 * c2, t2[15] = 1, t2;
    }
    /**
     * Generates a look-at matrix with the given eye position, focal point, and up axis.
     * If you want a matrix that actually makes an object look at another object, you should use targetTo instead.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param eye - Position of the viewer
     * @param center - Point the viewer is looking at
     * @param up - vec3 pointing up
     * @returns `out`
     */
    static lookAt(t2, e2, s2, i2) {
      const n2 = e2[0], r2 = e2[1], a2 = e2[2], o2 = i2[0], l2 = i2[1], c2 = i2[2], h2 = s2[0], m2 = s2[1], u2 = s2[2];
      if (Math.abs(n2 - h2) < O && Math.abs(r2 - m2) < O && Math.abs(a2 - u2) < O)
        return I.identity(t2);
      let f2 = n2 - h2, g2 = r2 - m2, p2 = a2 - u2, y2 = 1 / Math.sqrt(f2 * f2 + g2 * g2 + p2 * p2);
      f2 *= y2, g2 *= y2, p2 *= y2;
      let L = l2 * p2 - c2 * g2, x2 = c2 * f2 - o2 * p2, M2 = o2 * g2 - l2 * f2;
      y2 = Math.sqrt(L * L + x2 * x2 + M2 * M2), y2 ? (y2 = 1 / y2, L *= y2, x2 *= y2, M2 *= y2) : (L = 0, x2 = 0, M2 = 0);
      let b2 = g2 * M2 - p2 * x2, w2 = p2 * L - f2 * M2, T = f2 * x2 - g2 * L;
      return y2 = Math.sqrt(b2 * b2 + w2 * w2 + T * T), y2 ? (y2 = 1 / y2, b2 *= y2, w2 *= y2, T *= y2) : (b2 = 0, w2 = 0, T = 0), t2[0] = L, t2[1] = b2, t2[2] = f2, t2[3] = 0, t2[4] = x2, t2[5] = w2, t2[6] = g2, t2[7] = 0, t2[8] = M2, t2[9] = T, t2[10] = p2, t2[11] = 0, t2[12] = -(L * n2 + x2 * r2 + M2 * a2), t2[13] = -(b2 * n2 + w2 * r2 + T * a2), t2[14] = -(f2 * n2 + g2 * r2 + p2 * a2), t2[15] = 1, t2;
    }
    /**
     * Generates a matrix that makes something look at something else.
     * @category Static
     *
     * @param out - mat4 frustum matrix will be written into
     * @param eye - Position of the viewer
     * @param target - Point the viewer is looking at
     * @param up - vec3 pointing up
     * @returns `out`
     */
    static targetTo(t2, e2, s2, i2) {
      const n2 = e2[0], r2 = e2[1], a2 = e2[2], o2 = i2[0], l2 = i2[1], c2 = i2[2];
      let h2 = n2 - s2[0], m2 = r2 - s2[1], u2 = a2 - s2[2], f2 = h2 * h2 + m2 * m2 + u2 * u2;
      f2 > 0 && (f2 = 1 / Math.sqrt(f2), h2 *= f2, m2 *= f2, u2 *= f2);
      let g2 = l2 * u2 - c2 * m2, p2 = c2 * h2 - o2 * u2, y2 = o2 * m2 - l2 * h2;
      return f2 = g2 * g2 + p2 * p2 + y2 * y2, f2 > 0 && (f2 = 1 / Math.sqrt(f2), g2 *= f2, p2 *= f2, y2 *= f2), t2[0] = g2, t2[1] = p2, t2[2] = y2, t2[3] = 0, t2[4] = m2 * y2 - u2 * p2, t2[5] = u2 * g2 - h2 * y2, t2[6] = h2 * p2 - m2 * g2, t2[7] = 0, t2[8] = h2, t2[9] = m2, t2[10] = u2, t2[11] = 0, t2[12] = n2, t2[13] = r2, t2[14] = a2, t2[15] = 1, t2;
    }
    /**
     * Returns Frobenius norm of a {@link Mat4}
     * @category Static
     *
     * @param a - the matrix to calculate Frobenius norm of
     * @returns Frobenius norm
     */
    static frob(t2) {
      return Math.sqrt(t2[0] * t2[0] + t2[1] * t2[1] + t2[2] * t2[2] + t2[3] * t2[3] + t2[4] * t2[4] + t2[5] * t2[5] + t2[6] * t2[6] + t2[7] * t2[7] + t2[8] * t2[8] + t2[9] * t2[9] + t2[10] * t2[10] + t2[11] * t2[11] + t2[12] * t2[12] + t2[13] * t2[13] + t2[14] * t2[14] + t2[15] * t2[15]);
    }
    /**
     * Adds two {@link Mat4}'s
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static add(t2, e2, s2) {
      return t2[0] = e2[0] + s2[0], t2[1] = e2[1] + s2[1], t2[2] = e2[2] + s2[2], t2[3] = e2[3] + s2[3], t2[4] = e2[4] + s2[4], t2[5] = e2[5] + s2[5], t2[6] = e2[6] + s2[6], t2[7] = e2[7] + s2[7], t2[8] = e2[8] + s2[8], t2[9] = e2[9] + s2[9], t2[10] = e2[10] + s2[10], t2[11] = e2[11] + s2[11], t2[12] = e2[12] + s2[12], t2[13] = e2[13] + s2[13], t2[14] = e2[14] + s2[14], t2[15] = e2[15] + s2[15], t2;
    }
    /**
     * Subtracts matrix b from matrix a
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static subtract(t2, e2, s2) {
      return t2[0] = e2[0] - s2[0], t2[1] = e2[1] - s2[1], t2[2] = e2[2] - s2[2], t2[3] = e2[3] - s2[3], t2[4] = e2[4] - s2[4], t2[5] = e2[5] - s2[5], t2[6] = e2[6] - s2[6], t2[7] = e2[7] - s2[7], t2[8] = e2[8] - s2[8], t2[9] = e2[9] - s2[9], t2[10] = e2[10] - s2[10], t2[11] = e2[11] - s2[11], t2[12] = e2[12] - s2[12], t2[13] = e2[13] - s2[13], t2[14] = e2[14] - s2[14], t2[15] = e2[15] - s2[15], t2;
    }
    /**
     * Alias for {@link Mat4.subtract}
     * @category Static
     */
    static sub(t2, e2, s2) {
      return t2;
    }
    /**
     * Multiply each element of the matrix by a scalar.
     * @category Static
     *
     * @param out - the receiving matrix
     * @param a - the matrix to scale
     * @param b - amount to scale the matrix's elements by
     * @returns `out`
     */
    static multiplyScalar(t2, e2, s2) {
      return t2[0] = e2[0] * s2, t2[1] = e2[1] * s2, t2[2] = e2[2] * s2, t2[3] = e2[3] * s2, t2[4] = e2[4] * s2, t2[5] = e2[5] * s2, t2[6] = e2[6] * s2, t2[7] = e2[7] * s2, t2[8] = e2[8] * s2, t2[9] = e2[9] * s2, t2[10] = e2[10] * s2, t2[11] = e2[11] * s2, t2[12] = e2[12] * s2, t2[13] = e2[13] * s2, t2[14] = e2[14] * s2, t2[15] = e2[15] * s2, t2;
    }
    /**
     * Adds two mat4's after multiplying each element of the second operand by a scalar value.
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param scale - the amount to scale b's elements by before adding
     * @returns `out`
     */
    static multiplyScalarAndAdd(t2, e2, s2, i2) {
      return t2[0] = e2[0] + s2[0] * i2, t2[1] = e2[1] + s2[1] * i2, t2[2] = e2[2] + s2[2] * i2, t2[3] = e2[3] + s2[3] * i2, t2[4] = e2[4] + s2[4] * i2, t2[5] = e2[5] + s2[5] * i2, t2[6] = e2[6] + s2[6] * i2, t2[7] = e2[7] + s2[7] * i2, t2[8] = e2[8] + s2[8] * i2, t2[9] = e2[9] + s2[9] * i2, t2[10] = e2[10] + s2[10] * i2, t2[11] = e2[11] + s2[11] * i2, t2[12] = e2[12] + s2[12] * i2, t2[13] = e2[13] + s2[13] * i2, t2[14] = e2[14] + s2[14] * i2, t2[15] = e2[15] + s2[15] * i2, t2;
    }
    /**
     * Returns whether or not two {@link Mat4}s have exactly the same elements in the same position (when compared with ===)
     * @category Static
     *
     * @param a - The first matrix.
     * @param b - The second matrix.
     * @returns True if the matrices are equal, false otherwise.
     */
    static exactEquals(t2, e2) {
      return t2[0] === e2[0] && t2[1] === e2[1] && t2[2] === e2[2] && t2[3] === e2[3] && t2[4] === e2[4] && t2[5] === e2[5] && t2[6] === e2[6] && t2[7] === e2[7] && t2[8] === e2[8] && t2[9] === e2[9] && t2[10] === e2[10] && t2[11] === e2[11] && t2[12] === e2[12] && t2[13] === e2[13] && t2[14] === e2[14] && t2[15] === e2[15];
    }
    /**
     * Returns whether or not two {@link Mat4}s have approximately the same elements in the same position.
     * @category Static
     *
     * @param a - The first matrix.
     * @param b - The second matrix.
     * @returns True if the matrices are equal, false otherwise.
     */
    static equals(t2, e2) {
      const s2 = t2[0], i2 = t2[1], n2 = t2[2], r2 = t2[3], a2 = t2[4], o2 = t2[5], l2 = t2[6], c2 = t2[7], h2 = t2[8], m2 = t2[9], u2 = t2[10], f2 = t2[11], g2 = t2[12], p2 = t2[13], y2 = t2[14], L = t2[15], x2 = e2[0], M2 = e2[1], b2 = e2[2], w2 = e2[3], T = e2[4], v2 = e2[5], P = e2[6], z = e2[7], E = e2[8], F = e2[9], _ = e2[10], R = e2[11], k2 = e2[12], B = e2[13], $2 = e2[14], U = e2[15];
      return Math.abs(s2 - x2) <= O * Math.max(1, Math.abs(s2), Math.abs(x2)) && Math.abs(i2 - M2) <= O * Math.max(1, Math.abs(i2), Math.abs(M2)) && Math.abs(n2 - b2) <= O * Math.max(1, Math.abs(n2), Math.abs(b2)) && Math.abs(r2 - w2) <= O * Math.max(1, Math.abs(r2), Math.abs(w2)) && Math.abs(a2 - T) <= O * Math.max(1, Math.abs(a2), Math.abs(T)) && Math.abs(o2 - v2) <= O * Math.max(1, Math.abs(o2), Math.abs(v2)) && Math.abs(l2 - P) <= O * Math.max(1, Math.abs(l2), Math.abs(P)) && Math.abs(c2 - z) <= O * Math.max(1, Math.abs(c2), Math.abs(z)) && Math.abs(h2 - E) <= O * Math.max(1, Math.abs(h2), Math.abs(E)) && Math.abs(m2 - F) <= O * Math.max(1, Math.abs(m2), Math.abs(F)) && Math.abs(u2 - _) <= O * Math.max(1, Math.abs(u2), Math.abs(_)) && Math.abs(f2 - R) <= O * Math.max(1, Math.abs(f2), Math.abs(R)) && Math.abs(g2 - k2) <= O * Math.max(1, Math.abs(g2), Math.abs(k2)) && Math.abs(p2 - B) <= O * Math.max(1, Math.abs(p2), Math.abs(B)) && Math.abs(y2 - $2) <= O * Math.max(1, Math.abs(y2), Math.abs($2)) && Math.abs(L - U) <= O * Math.max(1, Math.abs(L), Math.abs(U));
    }
    /**
     * Returns a string representation of a {@link Mat4}
     * @category Static
     *
     * @param a - matrix to represent as a string
     * @returns string representation of the matrix
     */
    static str(t2) {
      return `Mat4(${t2.join(", ")})`;
    }
  }
  const V = new Float32Array(3);
  I.prototype.mul = I.prototype.multiply;
  I.sub = I.subtract;
  I.mul = I.multiply;
  I.frustum = I.frustumNO;
  I.perspective = I.perspectiveNO;
  I.ortho = I.orthoNO;
  class C extends Float32Array {
    /**
    * The number of bytes in a {@link Vec3}.
    */
    static BYTE_LENGTH = 3 * Float32Array.BYTES_PER_ELEMENT;
    /**
    * Create a {@link Vec3}.
    */
    constructor(...t2) {
      switch (t2.length) {
        case 3:
          super(t2);
          break;
        case 2:
          super(t2[0], t2[1], 3);
          break;
        case 1: {
          const e2 = t2[0];
          typeof e2 == "number" ? super([e2, e2, e2]) : super(e2, 0, 3);
          break;
        }
        default:
          super(3);
          break;
      }
    }
    //============
    // Attributes
    //============
    // Getters and setters to make component access read better.
    // These are likely to be a little bit slower than direct array access.
    /**
     * The x component of the vector. Equivalent to `this[0];`
     * @category Vector components
     */
    get x() {
      return this[0];
    }
    set x(t2) {
      this[0] = t2;
    }
    /**
     * The y component of the vector. Equivalent to `this[1];`
     * @category Vector components
     */
    get y() {
      return this[1];
    }
    set y(t2) {
      this[1] = t2;
    }
    /**
     * The z component of the vector. Equivalent to `this[2];`
     * @category Vector components
     */
    get z() {
      return this[2];
    }
    set z(t2) {
      this[2] = t2;
    }
    // Alternate set of getters and setters in case this is being used to define
    // a color.
    /**
     * The r component of the vector. Equivalent to `this[0];`
     * @category Color components
     */
    get r() {
      return this[0];
    }
    set r(t2) {
      this[0] = t2;
    }
    /**
     * The g component of the vector. Equivalent to `this[1];`
     * @category Color components
     */
    get g() {
      return this[1];
    }
    set g(t2) {
      this[1] = t2;
    }
    /**
     * The b component of the vector. Equivalent to `this[2];`
     * @category Color components
     */
    get b() {
      return this[2];
    }
    set b(t2) {
      this[2] = t2;
    }
    /**
     * The magnitude (length) of this.
     * Equivalent to `Vec3.magnitude(this);`
     *
     * Magnitude is used because the `length` attribute is already defined by
     * TypedArrays to mean the number of elements in the array.
     */
    get magnitude() {
      const t2 = this[0], e2 = this[1], s2 = this[2];
      return Math.sqrt(t2 * t2 + e2 * e2 + s2 * s2);
    }
    /**
     * Alias for {@link Vec3.magnitude}
     */
    get mag() {
      return this.magnitude;
    }
    /**
     * The squared magnitude (length) of `this`.
     * Equivalent to `Vec3.squaredMagnitude(this);`
     */
    get squaredMagnitude() {
      const t2 = this[0], e2 = this[1], s2 = this[2];
      return t2 * t2 + e2 * e2 + s2 * s2;
    }
    /**
     * Alias for {@link Vec3.squaredMagnitude}
     */
    get sqrMag() {
      return this.squaredMagnitude;
    }
    /**
     * A string representation of `this`
     * Equivalent to `Vec3.str(this);`
     */
    get str() {
      return C.str(this);
    }
    //===================
    // Instances methods
    //===================
    /**
     * Copy the values from another {@link Vec3} into `this`.
     *
     * @param a the source vector
     * @returns `this`
     */
    copy(t2) {
      return this.set(t2), this;
    }
    /**
     * Adds a {@link Vec3} to `this`.
     * Equivalent to `Vec3.add(this, this, b);`
     *
     * @param b - The vector to add to `this`
     * @returns `this`
     */
    add(t2) {
      return this[0] += t2[0], this[1] += t2[1], this[2] += t2[2], this;
    }
    /**
     * Subtracts a {@link Vec3} from `this`.
     * Equivalent to `Vec3.subtract(this, this, b);`
     *
     * @param b - The vector to subtract from `this`
     * @returns `this`
     */
    subtract(t2) {
      return this[0] -= t2[0], this[1] -= t2[1], this[2] -= t2[2], this;
    }
    /**
     * Alias for {@link Vec3.subtract}
     */
    sub(t2) {
      return this;
    }
    /**
     * Multiplies `this` by a {@link Vec3}.
     * Equivalent to `Vec3.multiply(this, this, b);`
     *
     * @param b - The vector to multiply `this` by
     * @returns `this`
     */
    multiply(t2) {
      return this[0] *= t2[0], this[1] *= t2[1], this[2] *= t2[2], this;
    }
    /**
     * Alias for {@link Vec3.multiply}
     */
    mul(t2) {
      return this;
    }
    /**
     * Divides `this` by a {@link Vec3}.
     * Equivalent to `Vec3.divide(this, this, b);`
     *
     * @param b - The vector to divide `this` by
     * @returns `this`
     */
    divide(t2) {
      return this[0] /= t2[0], this[1] /= t2[1], this[2] /= t2[2], this;
    }
    /**
     * Alias for {@link Vec3.divide}
     */
    div(t2) {
      return this;
    }
    /**
     * Scales `this` by a scalar number.
     * Equivalent to `Vec3.scale(this, this, b);`
     *
     * @param b - Amount to scale `this` by
     * @returns `this`
     */
    scale(t2) {
      return this[0] *= t2, this[1] *= t2, this[2] *= t2, this;
    }
    /**
     * Calculates `this` scaled by a scalar value then adds the result to `this`.
     * Equivalent to `Vec3.scaleAndAdd(this, this, b, scale);`
     *
     * @param b - The vector to add to `this`
     * @param scale - The amount to scale `b` by before adding
     * @returns `this`
     */
    scaleAndAdd(t2, e2) {
      return this[0] += t2[0] * e2, this[1] += t2[1] * e2, this[2] += t2[2] * e2, this;
    }
    /**
     * Calculates the euclidian distance between another {@link Vec3} and `this`.
     * Equivalent to `Vec3.distance(this, b);`
     *
     * @param b - The vector to calculate the distance to
     * @returns Distance between `this` and `b`
     */
    distance(t2) {
      return C.distance(this, t2);
    }
    /**
     * Alias for {@link Vec3.distance}
     */
    dist(t2) {
      return 0;
    }
    /**
     * Calculates the squared euclidian distance between another {@link Vec3} and `this`.
     * Equivalent to `Vec3.squaredDistance(this, b);`
     *
     * @param b The vector to calculate the squared distance to
     * @returns Squared distance between `this` and `b`
     */
    squaredDistance(t2) {
      return C.squaredDistance(this, t2);
    }
    /**
     * Alias for {@link Vec3.squaredDistance}
     */
    sqrDist(t2) {
      return 0;
    }
    /**
     * Negates the components of `this`.
     * Equivalent to `Vec3.negate(this, this);`
     *
     * @returns `this`
     */
    negate() {
      return this[0] *= -1, this[1] *= -1, this[2] *= -1, this;
    }
    /**
     * Inverts the components of `this`.
     * Equivalent to `Vec3.inverse(this, this);`
     *
     * @returns `this`
     */
    invert() {
      return this[0] = 1 / this[0], this[1] = 1 / this[1], this[2] = 1 / this[2], this;
    }
    /**
     * Sets each component of `this` to it's absolute value.
     * Equivalent to `Vec3.abs(this, this);`
     *
     * @returns `this`
     */
    abs() {
      return this[0] = Math.abs(this[0]), this[1] = Math.abs(this[1]), this[2] = Math.abs(this[2]), this;
    }
    /**
     * Calculates the dot product of this and another {@link Vec3}.
     * Equivalent to `Vec3.dot(this, b);`
     *
     * @param b - The second operand
     * @returns Dot product of `this` and `b`
     */
    dot(t2) {
      return this[0] * t2[0] + this[1] * t2[1] + this[2] * t2[2];
    }
    /**
     * Normalize `this`.
     * Equivalent to `Vec3.normalize(this, this);`
     *
     * @returns `this`
     */
    normalize() {
      return C.normalize(this, this);
    }
    //================
    // Static methods
    //================
    /**
     * Creates a new, empty vec3
     * @category Static
     *
     * @returns a new 3D vector
     */
    static create() {
      return new C();
    }
    /**
     * Creates a new vec3 initialized with values from an existing vector
     * @category Static
     *
     * @param a - vector to clone
     * @returns a new 3D vector
     */
    static clone(t2) {
      return new C(t2);
    }
    /**
     * Calculates the magnitude (length) of a {@link Vec3}
     * @category Static
     *
     * @param a - Vector to calculate magnitude of
     * @returns Magnitude of a
     */
    static magnitude(t2) {
      let e2 = t2[0], s2 = t2[1], i2 = t2[2];
      return Math.sqrt(e2 * e2 + s2 * s2 + i2 * i2);
    }
    /**
     * Alias for {@link Vec3.magnitude}
     * @category Static
     */
    static mag(t2) {
      return 0;
    }
    /**
     * Alias for {@link Vec3.magnitude}
     * @category Static
     * @deprecated Use {@link Vec3.magnitude} to avoid conflicts with builtin `length` methods/attribs
     *
     * @param a - vector to calculate length of
     * @returns length of a
     */
    // @ts-ignore: Length conflicts with Function.length
    static length(t2) {
      return 0;
    }
    /**
     * Alias for {@link Vec3.magnitude}
     * @category Static
     * @deprecated Use {@link Vec3.mag}
     */
    static len(t2) {
      return 0;
    }
    /**
     * Creates a new vec3 initialized with the given values
     * @category Static
     *
     * @param x - X component
     * @param y - Y component
     * @param z - Z component
     * @returns a new 3D vector
     */
    static fromValues(t2, e2, s2) {
      return new C(t2, e2, s2);
    }
    /**
     * Copy the values from one vec3 to another
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the source vector
     * @returns `out`
     */
    static copy(t2, e2) {
      return t2[0] = e2[0], t2[1] = e2[1], t2[2] = e2[2], t2;
    }
    /**
     * Set the components of a vec3 to the given values
     * @category Static
     *
     * @param out - the receiving vector
     * @param x - X component
     * @param y - Y component
     * @param z - Z component
     * @returns `out`
     */
    static set(t2, e2, s2, i2) {
      return t2[0] = e2, t2[1] = s2, t2[2] = i2, t2;
    }
    /**
     * Adds two {@link Vec3}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static add(t2, e2, s2) {
      return t2[0] = e2[0] + s2[0], t2[1] = e2[1] + s2[1], t2[2] = e2[2] + s2[2], t2;
    }
    /**
     * Subtracts vector b from vector a
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static subtract(t2, e2, s2) {
      return t2[0] = e2[0] - s2[0], t2[1] = e2[1] - s2[1], t2[2] = e2[2] - s2[2], t2;
    }
    /**
     * Alias for {@link Vec3.subtract}
     * @category Static
     */
    static sub(t2, e2, s2) {
      return [0, 0, 0];
    }
    /**
     * Multiplies two vec3's
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static multiply(t2, e2, s2) {
      return t2[0] = e2[0] * s2[0], t2[1] = e2[1] * s2[1], t2[2] = e2[2] * s2[2], t2;
    }
    /**
     * Alias for {@link Vec3.multiply}
     * @category Static
     */
    static mul(t2, e2, s2) {
      return [0, 0, 0];
    }
    /**
     * Divides two vec3's
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static divide(t2, e2, s2) {
      return t2[0] = e2[0] / s2[0], t2[1] = e2[1] / s2[1], t2[2] = e2[2] / s2[2], t2;
    }
    /**
     * Alias for {@link Vec3.divide}
     * @category Static
     */
    static div(t2, e2, s2) {
      return [0, 0, 0];
    }
    /**
     * Math.ceil the components of a vec3
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to ceil
     * @returns `out`
     */
    static ceil(t2, e2) {
      return t2[0] = Math.ceil(e2[0]), t2[1] = Math.ceil(e2[1]), t2[2] = Math.ceil(e2[2]), t2;
    }
    /**
     * Math.floor the components of a vec3
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to floor
     * @returns `out`
     */
    static floor(t2, e2) {
      return t2[0] = Math.floor(e2[0]), t2[1] = Math.floor(e2[1]), t2[2] = Math.floor(e2[2]), t2;
    }
    /**
     * Returns the minimum of two vec3's
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static min(t2, e2, s2) {
      return t2[0] = Math.min(e2[0], s2[0]), t2[1] = Math.min(e2[1], s2[1]), t2[2] = Math.min(e2[2], s2[2]), t2;
    }
    /**
     * Returns the maximum of two vec3's
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static max(t2, e2, s2) {
      return t2[0] = Math.max(e2[0], s2[0]), t2[1] = Math.max(e2[1], s2[1]), t2[2] = Math.max(e2[2], s2[2]), t2;
    }
    /**
     * symmetric round the components of a vec3
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to round
     * @returns `out`
     */
    /*static round(out: Vec3Like, a: Readonly<Vec3Like>): Vec3Like {
      out[0] = glMatrix.round(a[0]);
      out[1] = glMatrix.round(a[1]);
      out[2] = glMatrix.round(a[2]);
      return out;
    }*/
    /**
     * Scales a vec3 by a scalar number
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the vector to scale
     * @param scale - amount to scale the vector by
     * @returns `out`
     */
    static scale(t2, e2, s2) {
      return t2[0] = e2[0] * s2, t2[1] = e2[1] * s2, t2[2] = e2[2] * s2, t2;
    }
    /**
     * Adds two vec3's after scaling the second operand by a scalar value
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param scale - the amount to scale b by before adding
     * @returns `out`
     */
    static scaleAndAdd(t2, e2, s2, i2) {
      return t2[0] = e2[0] + s2[0] * i2, t2[1] = e2[1] + s2[1] * i2, t2[2] = e2[2] + s2[2] * i2, t2;
    }
    /**
     * Calculates the euclidian distance between two vec3's
     * @category Static
     *
     * @param a - the first operand
     * @param b - the second operand
     * @returns distance between a and b
     */
    static distance(t2, e2) {
      const s2 = e2[0] - t2[0], i2 = e2[1] - t2[1], n2 = e2[2] - t2[2];
      return Math.sqrt(s2 * s2 + i2 * i2 + n2 * n2);
    }
    /**
     * Alias for {@link Vec3.distance}
     */
    static dist(t2, e2) {
      return 0;
    }
    /**
     * Calculates the squared euclidian distance between two vec3's
     * @category Static
     *
     * @param a - the first operand
     * @param b - the second operand
     * @returns squared distance between a and b
     */
    static squaredDistance(t2, e2) {
      const s2 = e2[0] - t2[0], i2 = e2[1] - t2[1], n2 = e2[2] - t2[2];
      return s2 * s2 + i2 * i2 + n2 * n2;
    }
    /**
     * Alias for {@link Vec3.squaredDistance}
     */
    static sqrDist(t2, e2) {
      return 0;
    }
    /**
     * Calculates the squared length of a vec3
     * @category Static
     *
     * @param a - vector to calculate squared length of
     * @returns squared length of a
     */
    static squaredLength(t2) {
      const e2 = t2[0], s2 = t2[1], i2 = t2[2];
      return e2 * e2 + s2 * s2 + i2 * i2;
    }
    /**
     * Alias for {@link Vec3.squaredLength}
     */
    static sqrLen(t2, e2) {
      return 0;
    }
    /**
     * Negates the components of a vec3
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to negate
     * @returns `out`
     */
    static negate(t2, e2) {
      return t2[0] = -e2[0], t2[1] = -e2[1], t2[2] = -e2[2], t2;
    }
    /**
     * Returns the inverse of the components of a vec3
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to invert
     * @returns `out`
     */
    static inverse(t2, e2) {
      return t2[0] = 1 / e2[0], t2[1] = 1 / e2[1], t2[2] = 1 / e2[2], t2;
    }
    /**
     * Returns the absolute value of the components of a {@link Vec3}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to compute the absolute values of
     * @returns `out`
     */
    static abs(t2, e2) {
      return t2[0] = Math.abs(e2[0]), t2[1] = Math.abs(e2[1]), t2[2] = Math.abs(e2[2]), t2;
    }
    /**
     * Normalize a vec3
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to normalize
     * @returns `out`
     */
    static normalize(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2];
      let r2 = s2 * s2 + i2 * i2 + n2 * n2;
      return r2 > 0 && (r2 = 1 / Math.sqrt(r2)), t2[0] = e2[0] * r2, t2[1] = e2[1] * r2, t2[2] = e2[2] * r2, t2;
    }
    /**
     * Calculates the dot product of two vec3's
     * @category Static
     *
     * @param a - the first operand
     * @param b - the second operand
     * @returns dot product of a and b
     */
    static dot(t2, e2) {
      return t2[0] * e2[0] + t2[1] * e2[1] + t2[2] * e2[2];
    }
    /**
     * Computes the cross product of two vec3's
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static cross(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1], r2 = e2[2], a2 = s2[0], o2 = s2[1], l2 = s2[2];
      return t2[0] = n2 * l2 - r2 * o2, t2[1] = r2 * a2 - i2 * l2, t2[2] = i2 * o2 - n2 * a2, t2;
    }
    /**
     * Performs a linear interpolation between two vec3's
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param t - interpolation amount, in the range [0-1], between the two inputs
     * @returns `out`
     */
    static lerp(t2, e2, s2, i2) {
      const n2 = e2[0], r2 = e2[1], a2 = e2[2];
      return t2[0] = n2 + i2 * (s2[0] - n2), t2[1] = r2 + i2 * (s2[1] - r2), t2[2] = a2 + i2 * (s2[2] - a2), t2;
    }
    /**
     * Performs a spherical linear interpolation between two vec3's
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param t - interpolation amount, in the range [0-1], between the two inputs
     * @returns `out`
     */
    static slerp(t2, e2, s2, i2) {
      const n2 = Math.acos(Math.min(Math.max(C.dot(e2, s2), -1), 1)), r2 = Math.sin(n2), a2 = Math.sin((1 - i2) * n2) / r2, o2 = Math.sin(i2 * n2) / r2;
      return t2[0] = a2 * e2[0] + o2 * s2[0], t2[1] = a2 * e2[1] + o2 * s2[1], t2[2] = a2 * e2[2] + o2 * s2[2], t2;
    }
    /**
     * Performs a hermite interpolation with two control points
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param c - the third operand
     * @param d - the fourth operand
     * @param t - interpolation amount, in the range [0-1], between the two inputs
     * @returns `out`
     */
    static hermite(t2, e2, s2, i2, n2, r2) {
      const a2 = r2 * r2, o2 = a2 * (2 * r2 - 3) + 1, l2 = a2 * (r2 - 2) + r2, c2 = a2 * (r2 - 1), h2 = a2 * (3 - 2 * r2);
      return t2[0] = e2[0] * o2 + s2[0] * l2 + i2[0] * c2 + n2[0] * h2, t2[1] = e2[1] * o2 + s2[1] * l2 + i2[1] * c2 + n2[1] * h2, t2[2] = e2[2] * o2 + s2[2] * l2 + i2[2] * c2 + n2[2] * h2, t2;
    }
    /**
     * Performs a bezier interpolation with two control points
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param c - the third operand
     * @param d - the fourth operand
     * @param t - interpolation amount, in the range [0-1], between the two inputs
     * @returns `out`
     */
    static bezier(t2, e2, s2, i2, n2, r2) {
      const a2 = 1 - r2, o2 = a2 * a2, l2 = r2 * r2, c2 = o2 * a2, h2 = 3 * r2 * o2, m2 = 3 * l2 * a2, u2 = l2 * r2;
      return t2[0] = e2[0] * c2 + s2[0] * h2 + i2[0] * m2 + n2[0] * u2, t2[1] = e2[1] * c2 + s2[1] * h2 + i2[1] * m2 + n2[1] * u2, t2[2] = e2[2] * c2 + s2[2] * h2 + i2[2] * m2 + n2[2] * u2, t2;
    }
    /**
     * Generates a random vector with the given scale
     * @category Static
     *
     * @param out - the receiving vector
     * @param {Number} [scale] Length of the resulting vector. If omitted, a unit vector will be returned
     * @returns `out`
     */
    /*static random(out: Vec3Like, scale) {
        scale = scale === undefined ? 1.0 : scale;
    
        let r = glMatrix.RANDOM() * 2.0 * Math.PI;
        let z = glMatrix.RANDOM() * 2.0 - 1.0;
        let zScale = Math.sqrt(1.0 - z * z) * scale;
    
        out[0] = Math.cos(r) * zScale;
        out[1] = Math.sin(r) * zScale;
        out[2] = z * scale;
        return out;
      }*/
    /**
     * Transforms the vec3 with a mat4.
     * 4th vector component is implicitly '1'
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the vector to transform
     * @param m - matrix to transform with
     * @returns `out`
     */
    static transformMat4(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1], r2 = e2[2], a2 = s2[3] * i2 + s2[7] * n2 + s2[11] * r2 + s2[15] || 1;
      return t2[0] = (s2[0] * i2 + s2[4] * n2 + s2[8] * r2 + s2[12]) / a2, t2[1] = (s2[1] * i2 + s2[5] * n2 + s2[9] * r2 + s2[13]) / a2, t2[2] = (s2[2] * i2 + s2[6] * n2 + s2[10] * r2 + s2[14]) / a2, t2;
    }
    /**
     * Transforms the vec3 with a mat3.
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the vector to transform
     * @param m - the 3x3 matrix to transform with
     * @returns `out`
     */
    static transformMat3(t2, e2, s2) {
      let i2 = e2[0], n2 = e2[1], r2 = e2[2];
      return t2[0] = i2 * s2[0] + n2 * s2[3] + r2 * s2[6], t2[1] = i2 * s2[1] + n2 * s2[4] + r2 * s2[7], t2[2] = i2 * s2[2] + n2 * s2[5] + r2 * s2[8], t2;
    }
    /**
     * Transforms the vec3 with a quat
     * Can also be used for dual quaternions. (Multiply it with the real part)
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the vector to transform
     * @param q - quaternion to transform with
     * @returns `out`
     */
    static transformQuat(t2, e2, s2) {
      const i2 = s2[0], n2 = s2[1], r2 = s2[2], a2 = s2[3] * 2, o2 = e2[0], l2 = e2[1], c2 = e2[2], h2 = n2 * c2 - r2 * l2, m2 = r2 * o2 - i2 * c2, u2 = i2 * l2 - n2 * o2, f2 = (n2 * u2 - r2 * m2) * 2, g2 = (r2 * h2 - i2 * u2) * 2, p2 = (i2 * m2 - n2 * h2) * 2;
      return t2[0] = o2 + h2 * a2 + f2, t2[1] = l2 + m2 * a2 + g2, t2[2] = c2 + u2 * a2 + p2, t2;
    }
    /**
     * Rotate a 3D vector around the x-axis
     * @param out - The receiving vec3
     * @param a - The vec3 point to rotate
     * @param b - The origin of the rotation
     * @param rad - The angle of rotation in radians
     * @returns `out`
     */
    static rotateX(t2, e2, s2, i2) {
      const n2 = s2[1], r2 = s2[2], a2 = e2[1] - n2, o2 = e2[2] - r2;
      return t2[0] = e2[0], t2[1] = a2 * Math.cos(i2) - o2 * Math.sin(i2) + n2, t2[2] = a2 * Math.sin(i2) + o2 * Math.cos(i2) + r2, t2;
    }
    /**
     * Rotate a 3D vector around the y-axis
     * @param out - The receiving vec3
     * @param a - The vec3 point to rotate
     * @param b - The origin of the rotation
     * @param rad - The angle of rotation in radians
     * @returns `out`
     */
    static rotateY(t2, e2, s2, i2) {
      const n2 = s2[0], r2 = s2[2], a2 = e2[0] - n2, o2 = e2[2] - r2;
      return t2[0] = o2 * Math.sin(i2) + a2 * Math.cos(i2) + n2, t2[1] = e2[1], t2[2] = o2 * Math.cos(i2) - a2 * Math.sin(i2) + r2, t2;
    }
    /**
     * Rotate a 3D vector around the z-axis
     * @param out - The receiving vec3
     * @param a - The vec3 point to rotate
     * @param b - The origin of the rotation
     * @param rad - The angle of rotation in radians
     * @returns `out`
     */
    static rotateZ(t2, e2, s2, i2) {
      const n2 = s2[0], r2 = s2[1], a2 = e2[0] - n2, o2 = e2[1] - r2;
      return t2[0] = a2 * Math.cos(i2) - o2 * Math.sin(i2) + n2, t2[1] = a2 * Math.sin(i2) + o2 * Math.cos(i2) + r2, t2[2] = s2[2], t2;
    }
    /**
     * Get the angle between two 3D vectors
     * @param a - The first operand
     * @param b - The second operand
     * @returns The angle in radians
     */
    static angle(t2, e2) {
      const s2 = t2[0], i2 = t2[1], n2 = t2[2], r2 = e2[0], a2 = e2[1], o2 = e2[2], l2 = Math.sqrt((s2 * s2 + i2 * i2 + n2 * n2) * (r2 * r2 + a2 * a2 + o2 * o2)), c2 = l2 && C.dot(t2, e2) / l2;
      return Math.acos(Math.min(Math.max(c2, -1), 1));
    }
    /**
     * Set the components of a vec3 to zero
     * @category Static
     *
     * @param out - the receiving vector
     * @returns `out`
     */
    static zero(t2) {
      return t2[0] = 0, t2[1] = 0, t2[2] = 0, t2;
    }
    /**
     * Returns a string representation of a vector
     * @category Static
     *
     * @param a - vector to represent as a string
     * @returns string representation of the vector
     */
    static str(t2) {
      return `Vec3(${t2.join(", ")})`;
    }
    /**
     * Returns whether or not the vectors have exactly the same elements in the same position (when compared with ===)
     * @category Static
     *
     * @param a - The first vector.
     * @param b - The second vector.
     * @returns True if the vectors are equal, false otherwise.
     */
    static exactEquals(t2, e2) {
      return t2[0] === e2[0] && t2[1] === e2[1] && t2[2] === e2[2];
    }
    /**
     * Returns whether or not the vectors have approximately the same elements in the same position.
     * @category Static
     *
     * @param a - The first vector.
     * @param b - The second vector.
     * @returns True if the vectors are equal, false otherwise.
     */
    static equals(t2, e2) {
      const s2 = t2[0], i2 = t2[1], n2 = t2[2], r2 = e2[0], a2 = e2[1], o2 = e2[2];
      return Math.abs(s2 - r2) <= O * Math.max(1, Math.abs(s2), Math.abs(r2)) && Math.abs(i2 - a2) <= O * Math.max(1, Math.abs(i2), Math.abs(a2)) && Math.abs(n2 - o2) <= O * Math.max(1, Math.abs(n2), Math.abs(o2));
    }
  }
  C.prototype.sub = C.prototype.subtract;
  C.prototype.mul = C.prototype.multiply;
  C.prototype.div = C.prototype.divide;
  C.prototype.dist = C.prototype.distance;
  C.prototype.sqrDist = C.prototype.squaredDistance;
  C.sub = C.subtract;
  C.mul = C.multiply;
  C.div = C.divide;
  C.dist = C.distance;
  C.sqrDist = C.squaredDistance;
  C.sqrLen = C.squaredLength;
  C.mag = C.magnitude;
  C.length = C.magnitude;
  C.len = C.magnitude;
  class D extends Float32Array {
    /**
     * The number of bytes in a {@link Vec4}.
     */
    static BYTE_LENGTH = 4 * Float32Array.BYTES_PER_ELEMENT;
    /**
     * Create a {@link Vec4}.
     */
    constructor(...t2) {
      switch (t2.length) {
        case 4:
          super(t2);
          break;
        case 2:
          super(t2[0], t2[1], 4);
          break;
        case 1: {
          const e2 = t2[0];
          typeof e2 == "number" ? super([e2, e2, e2, e2]) : super(e2, 0, 4);
          break;
        }
        default:
          super(4);
          break;
      }
    }
    //============
    // Attributes
    //============
    // Getters and setters to make component access read better.
    // These are likely to be a little bit slower than direct array access.
    /**
     * The x component of the vector. Equivalent to `this[0];`
     * @category Vector components
     */
    get x() {
      return this[0];
    }
    set x(t2) {
      this[0] = t2;
    }
    /**
     * The y component of the vector. Equivalent to `this[1];`
     * @category Vector components
     */
    get y() {
      return this[1];
    }
    set y(t2) {
      this[1] = t2;
    }
    /**
     * The z component of the vector. Equivalent to `this[2];`
     * @category Vector components
     */
    get z() {
      return this[2];
    }
    set z(t2) {
      this[2] = t2;
    }
    /**
     * The w component of the vector. Equivalent to `this[3];`
     * @category Vector components
     */
    get w() {
      return this[3];
    }
    set w(t2) {
      this[3] = t2;
    }
    // Alternate set of getters and setters in case this is being used to define
    // a color.
    /**
     * The r component of the vector. Equivalent to `this[0];`
     * @category Color components
     */
    get r() {
      return this[0];
    }
    set r(t2) {
      this[0] = t2;
    }
    /**
     * The g component of the vector. Equivalent to `this[1];`
     * @category Color components
     */
    get g() {
      return this[1];
    }
    set g(t2) {
      this[1] = t2;
    }
    /**
     * The b component of the vector. Equivalent to `this[2];`
     * @category Color components
     */
    get b() {
      return this[2];
    }
    set b(t2) {
      this[2] = t2;
    }
    /**
     * The a component of the vector. Equivalent to `this[3];`
     * @category Color components
     */
    get a() {
      return this[3];
    }
    set a(t2) {
      this[3] = t2;
    }
    /**
     * The magnitude (length) of this.
     * Equivalent to `Vec4.magnitude(this);`
     *
     * Magnitude is used because the `length` attribute is already defined by
     * TypedArrays to mean the number of elements in the array.
     */
    get magnitude() {
      const t2 = this[0], e2 = this[1], s2 = this[2], i2 = this[3];
      return Math.sqrt(t2 * t2 + e2 * e2 + s2 * s2 + i2 * i2);
    }
    /**
     * Alias for {@link Vec4.magnitude}
     */
    get mag() {
      return this.magnitude;
    }
    /**
     * A string representation of `this`
     * Equivalent to `Vec4.str(this);`
     */
    get str() {
      return D.str(this);
    }
    //===================
    // Instances methods
    //===================
    /**
     * Copy the values from another {@link Vec4} into `this`.
     *
     * @param a the source vector
     * @returns `this`
     */
    copy(t2) {
      return super.set(t2), this;
    }
    /**
     * Adds a {@link Vec4} to `this`.
     * Equivalent to `Vec4.add(this, this, b);`
     *
     * @param b - The vector to add to `this`
     * @returns `this`
     */
    add(t2) {
      return this[0] += t2[0], this[1] += t2[1], this[2] += t2[2], this[3] += t2[3], this;
    }
    /**
     * Subtracts a {@link Vec4} from `this`.
     * Equivalent to `Vec4.subtract(this, this, b);`
     *
     * @param b - The vector to subtract from `this`
     * @returns `this`
     */
    subtract(t2) {
      return this[0] -= t2[0], this[1] -= t2[1], this[2] -= t2[2], this[3] -= t2[3], this;
    }
    /**
     * Alias for {@link Vec4.subtract}
     */
    sub(t2) {
      return this;
    }
    /**
     * Multiplies `this` by a {@link Vec4}.
     * Equivalent to `Vec4.multiply(this, this, b);`
     *
     * @param b - The vector to multiply `this` by
     * @returns `this`
     */
    multiply(t2) {
      return this[0] *= t2[0], this[1] *= t2[1], this[2] *= t2[2], this[3] *= t2[3], this;
    }
    /**
     * Alias for {@link Vec4.multiply}
     */
    mul(t2) {
      return this;
    }
    /**
     * Divides `this` by a {@link Vec4}.
     * Equivalent to `Vec4.divide(this, this, b);`
     *
     * @param b - The vector to divide `this` by
     * @returns `this`
     */
    divide(t2) {
      return this[0] /= t2[0], this[1] /= t2[1], this[2] /= t2[2], this[3] /= t2[3], this;
    }
    /**
     * Alias for {@link Vec4.divide}
     */
    div(t2) {
      return this;
    }
    /**
     * Scales `this` by a scalar number.
     * Equivalent to `Vec4.scale(this, this, b);`
     *
     * @param b - Amount to scale `this` by
     * @returns `this`
     */
    scale(t2) {
      return this[0] *= t2, this[1] *= t2, this[2] *= t2, this[3] *= t2, this;
    }
    /**
     * Calculates `this` scaled by a scalar value then adds the result to `this`.
     * Equivalent to `Vec4.scaleAndAdd(this, this, b, scale);`
     *
     * @param b - The vector to add to `this`
     * @param scale - The amount to scale `b` by before adding
     * @returns `this`
     */
    scaleAndAdd(t2, e2) {
      return this[0] += t2[0] * e2, this[1] += t2[1] * e2, this[2] += t2[2] * e2, this[3] += t2[3] * e2, this;
    }
    /**
     * Calculates the euclidian distance between another {@link Vec4} and `this`.
     * Equivalent to `Vec4.distance(this, b);`
     *
     * @param b - The vector to calculate the distance to
     * @returns Distance between `this` and `b`
     */
    distance(t2) {
      return D.distance(this, t2);
    }
    /**
     * Alias for {@link Vec4.distance}
     */
    dist(t2) {
      return 0;
    }
    /**
     * Calculates the squared euclidian distance between another {@link Vec4} and `this`.
     * Equivalent to `Vec4.squaredDistance(this, b);`
     *
     * @param b The vector to calculate the squared distance to
     * @returns Squared distance between `this` and `b`
     */
    squaredDistance(t2) {
      return D.squaredDistance(this, t2);
    }
    /**
     * Alias for {@link Vec4.squaredDistance}
     */
    sqrDist(t2) {
      return 0;
    }
    /**
     * Negates the components of `this`.
     * Equivalent to `Vec4.negate(this, this);`
     *
     * @returns `this`
     */
    negate() {
      return this[0] *= -1, this[1] *= -1, this[2] *= -1, this[3] *= -1, this;
    }
    /**
     * Inverts the components of `this`.
     * Equivalent to `Vec4.inverse(this, this);`
     *
     * @returns `this`
     */
    invert() {
      return this[0] = 1 / this[0], this[1] = 1 / this[1], this[2] = 1 / this[2], this[3] = 1 / this[3], this;
    }
    /**
     * Sets each component of `this` to it's absolute value.
     * Equivalent to `Vec4.abs(this, this);`
     *
     * @returns `this`
     */
    abs() {
      return this[0] = Math.abs(this[0]), this[1] = Math.abs(this[1]), this[2] = Math.abs(this[2]), this[3] = Math.abs(this[3]), this;
    }
    /**
     * Calculates the dot product of this and another {@link Vec4}.
     * Equivalent to `Vec4.dot(this, b);`
     *
     * @param b - The second operand
     * @returns Dot product of `this` and `b`
     */
    dot(t2) {
      return this[0] * t2[0] + this[1] * t2[1] + this[2] * t2[2] + this[3] * t2[3];
    }
    /**
     * Normalize `this`.
     * Equivalent to `Vec4.normalize(this, this);`
     *
     * @returns `this`
     */
    normalize() {
      return D.normalize(this, this);
    }
    //===================
    // Static methods
    //===================
    /**
     * Creates a new, empty {@link Vec4}
     * @category Static
     *
     * @returns a new 4D vector
     */
    static create() {
      return new D();
    }
    /**
     * Creates a new {@link Vec4} initialized with values from an existing vector
     * @category Static
     *
     * @param a - vector to clone
     * @returns a new 4D vector
     */
    static clone(t2) {
      return new D(t2);
    }
    /**
     * Creates a new {@link Vec4} initialized with the given values
     * @category Static
     *
     * @param x - X component
     * @param y - Y component
     * @param z - Z component
     * @param w - W component
     * @returns a new 4D vector
     */
    static fromValues(t2, e2, s2, i2) {
      return new D(t2, e2, s2, i2);
    }
    /**
     * Copy the values from one {@link Vec4} to another
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the source vector
     * @returns `out`
     */
    static copy(t2, e2) {
      return t2[0] = e2[0], t2[1] = e2[1], t2[2] = e2[2], t2[3] = e2[3], t2;
    }
    /**
     * Set the components of a {@link Vec4} to the given values
     * @category Static
     *
     * @param out - the receiving vector
     * @param x - X component
     * @param y - Y component
     * @param z - Z component
     * @param w - W component
     * @returns `out`
     */
    static set(t2, e2, s2, i2, n2) {
      return t2[0] = e2, t2[1] = s2, t2[2] = i2, t2[3] = n2, t2;
    }
    /**
     * Adds two {@link Vec4}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static add(t2, e2, s2) {
      return t2[0] = e2[0] + s2[0], t2[1] = e2[1] + s2[1], t2[2] = e2[2] + s2[2], t2[3] = e2[3] + s2[3], t2;
    }
    /**
     * Subtracts vector b from vector a
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static subtract(t2, e2, s2) {
      return t2[0] = e2[0] - s2[0], t2[1] = e2[1] - s2[1], t2[2] = e2[2] - s2[2], t2[3] = e2[3] - s2[3], t2;
    }
    /**
     * Alias for {@link Vec4.subtract}
     * @category Static
     */
    static sub(t2, e2, s2) {
      return t2;
    }
    /**
     * Multiplies two {@link Vec4}'s
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static multiply(t2, e2, s2) {
      return t2[0] = e2[0] * s2[0], t2[1] = e2[1] * s2[1], t2[2] = e2[2] * s2[2], t2[3] = e2[3] * s2[3], t2;
    }
    /**
     * Alias for {@link Vec4.multiply}
     * @category Static
     */
    static mul(t2, e2, s2) {
      return t2;
    }
    /**
     * Divides two {@link Vec4}'s
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static divide(t2, e2, s2) {
      return t2[0] = e2[0] / s2[0], t2[1] = e2[1] / s2[1], t2[2] = e2[2] / s2[2], t2[3] = e2[3] / s2[3], t2;
    }
    /**
     * Alias for {@link Vec4.divide}
     * @category Static
     */
    static div(t2, e2, s2) {
      return t2;
    }
    /**
     * Math.ceil the components of a {@link Vec4}
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to ceil
     * @returns `out`
     */
    static ceil(t2, e2) {
      return t2[0] = Math.ceil(e2[0]), t2[1] = Math.ceil(e2[1]), t2[2] = Math.ceil(e2[2]), t2[3] = Math.ceil(e2[3]), t2;
    }
    /**
     * Math.floor the components of a {@link Vec4}
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to floor
     * @returns `out`
     */
    static floor(t2, e2) {
      return t2[0] = Math.floor(e2[0]), t2[1] = Math.floor(e2[1]), t2[2] = Math.floor(e2[2]), t2[3] = Math.floor(e2[3]), t2;
    }
    /**
     * Returns the minimum of two {@link Vec4}'s
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static min(t2, e2, s2) {
      return t2[0] = Math.min(e2[0], s2[0]), t2[1] = Math.min(e2[1], s2[1]), t2[2] = Math.min(e2[2], s2[2]), t2[3] = Math.min(e2[3], s2[3]), t2;
    }
    /**
     * Returns the maximum of two {@link Vec4}'s
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @returns `out`
     */
    static max(t2, e2, s2) {
      return t2[0] = Math.max(e2[0], s2[0]), t2[1] = Math.max(e2[1], s2[1]), t2[2] = Math.max(e2[2], s2[2]), t2[3] = Math.max(e2[3], s2[3]), t2;
    }
    /**
     * Math.round the components of a {@link Vec4}
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to round
     * @returns `out`
     */
    static round(t2, e2) {
      return t2[0] = Math.round(e2[0]), t2[1] = Math.round(e2[1]), t2[2] = Math.round(e2[2]), t2[3] = Math.round(e2[3]), t2;
    }
    /**
     * Scales a {@link Vec4} by a scalar number
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the vector to scale
     * @param scale - amount to scale the vector by
     * @returns `out`
     */
    static scale(t2, e2, s2) {
      return t2[0] = e2[0] * s2, t2[1] = e2[1] * s2, t2[2] = e2[2] * s2, t2[3] = e2[3] * s2, t2;
    }
    /**
     * Adds two {@link Vec4}'s after scaling the second operand by a scalar value
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param scale - the amount to scale b by before adding
     * @returns `out`
     */
    static scaleAndAdd(t2, e2, s2, i2) {
      return t2[0] = e2[0] + s2[0] * i2, t2[1] = e2[1] + s2[1] * i2, t2[2] = e2[2] + s2[2] * i2, t2[3] = e2[3] + s2[3] * i2, t2;
    }
    /**
     * Calculates the euclidian distance between two {@link Vec4}'s
     * @category Static
     *
     * @param a - the first operand
     * @param b - the second operand
     * @returns distance between a and b
     */
    static distance(t2, e2) {
      const s2 = e2[0] - t2[0], i2 = e2[1] - t2[1], n2 = e2[2] - t2[2], r2 = e2[3] - t2[3];
      return Math.hypot(s2, i2, n2, r2);
    }
    /**
     * Alias for {@link Vec4.distance}
     * @category Static
     */
    static dist(t2, e2) {
      return 0;
    }
    /**
     * Calculates the squared euclidian distance between two {@link Vec4}'s
     * @category Static
     *
     * @param a - the first operand
     * @param b - the second operand
     * @returns squared distance between a and b
     */
    static squaredDistance(t2, e2) {
      const s2 = e2[0] - t2[0], i2 = e2[1] - t2[1], n2 = e2[2] - t2[2], r2 = e2[3] - t2[3];
      return s2 * s2 + i2 * i2 + n2 * n2 + r2 * r2;
    }
    /**
     * Alias for {@link Vec4.squaredDistance}
     * @category Static
     */
    static sqrDist(t2, e2) {
      return 0;
    }
    /**
     * Calculates the magnitude (length) of a {@link Vec4}
     * @category Static
     *
     * @param a - vector to calculate length of
     * @returns length of `a`
     */
    static magnitude(t2) {
      const e2 = t2[0], s2 = t2[1], i2 = t2[2], n2 = t2[3];
      return Math.sqrt(e2 * e2 + s2 * s2 + i2 * i2 + n2 * n2);
    }
    /**
     * Alias for {@link Vec4.magnitude}
     * @category Static
     */
    static mag(t2) {
      return 0;
    }
    /**
     * Alias for {@link Vec4.magnitude}
     * @category Static
     * @deprecated Use {@link Vec4.magnitude} to avoid conflicts with builtin `length` methods/attribs
     */
    // @ts-ignore: Length conflicts with Function.length
    static length(t2) {
      return 0;
    }
    /**
     * Alias for {@link Vec4.magnitude}
     * @category Static
     * @deprecated Use {@link Vec4.mag}
     */
    static len(t2) {
      return 0;
    }
    /**
     * Calculates the squared length of a {@link Vec4}
     * @category Static
     *
     * @param a - vector to calculate squared length of
     * @returns squared length of a
     */
    static squaredLength(t2) {
      const e2 = t2[0], s2 = t2[1], i2 = t2[2], n2 = t2[3];
      return e2 * e2 + s2 * s2 + i2 * i2 + n2 * n2;
    }
    /**
     * Alias for {@link Vec4.squaredLength}
     * @category Static
     */
    static sqrLen(t2) {
      return 0;
    }
    /**
     * Negates the components of a {@link Vec4}
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to negate
     * @returns `out`
     */
    static negate(t2, e2) {
      return t2[0] = -e2[0], t2[1] = -e2[1], t2[2] = -e2[2], t2[3] = -e2[3], t2;
    }
    /**
     * Returns the inverse of the components of a {@link Vec4}
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to invert
     * @returns `out`
     */
    static inverse(t2, e2) {
      return t2[0] = 1 / e2[0], t2[1] = 1 / e2[1], t2[2] = 1 / e2[2], t2[3] = 1 / e2[3], t2;
    }
    /**
     * Returns the absolute value of the components of a {@link Vec4}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to compute the absolute values of
     * @returns `out`
     */
    static abs(t2, e2) {
      return t2[0] = Math.abs(e2[0]), t2[1] = Math.abs(e2[1]), t2[2] = Math.abs(e2[2]), t2[3] = Math.abs(e2[3]), t2;
    }
    /**
     * Normalize a {@link Vec4}
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - vector to normalize
     * @returns `out`
     */
    static normalize(t2, e2) {
      const s2 = e2[0], i2 = e2[1], n2 = e2[2], r2 = e2[3];
      let a2 = s2 * s2 + i2 * i2 + n2 * n2 + r2 * r2;
      return a2 > 0 && (a2 = 1 / Math.sqrt(a2)), t2[0] = s2 * a2, t2[1] = i2 * a2, t2[2] = n2 * a2, t2[3] = r2 * a2, t2;
    }
    /**
     * Calculates the dot product of two {@link Vec4}'s
     * @category Static
     *
     * @param a - the first operand
     * @param b - the second operand
     * @returns dot product of a and b
     */
    static dot(t2, e2) {
      return t2[0] * e2[0] + t2[1] * e2[1] + t2[2] * e2[2] + t2[3] * e2[3];
    }
    /**
     * Returns the cross-product of three vectors in a 4-dimensional space
     * @category Static
     *
     * @param out the receiving vector
     * @param u - the first vector
     * @param v - the second vector
     * @param w - the third vector
     * @returns result
     */
    static cross(t2, e2, s2, i2) {
      const n2 = s2[0] * i2[1] - s2[1] * i2[0], r2 = s2[0] * i2[2] - s2[2] * i2[0], a2 = s2[0] * i2[3] - s2[3] * i2[0], o2 = s2[1] * i2[2] - s2[2] * i2[1], l2 = s2[1] * i2[3] - s2[3] * i2[1], c2 = s2[2] * i2[3] - s2[3] * i2[2], h2 = e2[0], m2 = e2[1], u2 = e2[2], f2 = e2[3];
      return t2[0] = m2 * c2 - u2 * l2 + f2 * o2, t2[1] = -(h2 * c2) + u2 * a2 - f2 * r2, t2[2] = h2 * l2 - m2 * a2 + f2 * n2, t2[3] = -(h2 * o2) + m2 * r2 - u2 * n2, t2;
    }
    /**
     * Performs a linear interpolation between two {@link Vec4}'s
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the first operand
     * @param b - the second operand
     * @param t - interpolation amount, in the range [0-1], between the two inputs
     * @returns `out`
     */
    static lerp(t2, e2, s2, i2) {
      const n2 = e2[0], r2 = e2[1], a2 = e2[2], o2 = e2[3];
      return t2[0] = n2 + i2 * (s2[0] - n2), t2[1] = r2 + i2 * (s2[1] - r2), t2[2] = a2 + i2 * (s2[2] - a2), t2[3] = o2 + i2 * (s2[3] - o2), t2;
    }
    /**
     * Generates a random vector with the given scale
     * @category Static
     *
     * @param out - the receiving vector
     * @param [scale] - Length of the resulting vector. If ommitted, a unit vector will be returned
     * @returns `out`
     */
    /*static random(out: Vec4Like, scale): Vec4Like {
        scale = scale || 1.0;
    
        // Marsaglia, George. Choosing a Point from the Surface of a
        // Sphere. Ann. Math. Statist. 43 (1972), no. 2, 645--646.
        // http://projecteuclid.org/euclid.aoms/1177692644;
        var v1, v2, v3, v4;
        var s1, s2;
        do {
          v1 = glMatrix.RANDOM() * 2 - 1;
          v2 = glMatrix.RANDOM() * 2 - 1;
          s1 = v1 * v1 + v2 * v2;
        } while (s1 >= 1);
        do {
          v3 = glMatrix.RANDOM() * 2 - 1;
          v4 = glMatrix.RANDOM() * 2 - 1;
          s2 = v3 * v3 + v4 * v4;
        } while (s2 >= 1);
    
        var d = Math.sqrt((1 - s1) / s2);
        out[0] = scale * v1;
        out[1] = scale * v2;
        out[2] = scale * v3 * d;
        out[3] = scale * v4 * d;
        return out;
      }*/
    /**
     * Transforms the {@link Vec4} with a {@link Mat4}.
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the vector to transform
     * @param m - matrix to transform with
     * @returns `out`
     */
    static transformMat4(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1], r2 = e2[2], a2 = e2[3];
      return t2[0] = s2[0] * i2 + s2[4] * n2 + s2[8] * r2 + s2[12] * a2, t2[1] = s2[1] * i2 + s2[5] * n2 + s2[9] * r2 + s2[13] * a2, t2[2] = s2[2] * i2 + s2[6] * n2 + s2[10] * r2 + s2[14] * a2, t2[3] = s2[3] * i2 + s2[7] * n2 + s2[11] * r2 + s2[15] * a2, t2;
    }
    /**
     * Transforms the {@link Vec4} with a {@link Quat}
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - the vector to transform
     * @param q - quaternion to transform with
     * @returns `out`
     */
    static transformQuat(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1], r2 = e2[2], a2 = s2[0], o2 = s2[1], l2 = s2[2], c2 = s2[3], h2 = c2 * i2 + o2 * r2 - l2 * n2, m2 = c2 * n2 + l2 * i2 - a2 * r2, u2 = c2 * r2 + a2 * n2 - o2 * i2, f2 = -a2 * i2 - o2 * n2 - l2 * r2;
      return t2[0] = h2 * c2 + f2 * -a2 + m2 * -l2 - u2 * -o2, t2[1] = m2 * c2 + f2 * -o2 + u2 * -a2 - h2 * -l2, t2[2] = u2 * c2 + f2 * -l2 + h2 * -o2 - m2 * -a2, t2[3] = e2[3], t2;
    }
    /**
     * Set the components of a {@link Vec4} to zero
     * @category Static
     *
     * @param out - the receiving vector
     * @returns `out`
     */
    static zero(t2) {
      return t2[0] = 0, t2[1] = 0, t2[2] = 0, t2[3] = 0, t2;
    }
    /**
     * Returns a string representation of a {@link Vec4}
     * @category Static
     *
     * @param a - vector to represent as a string
     * @returns string representation of the vector
     */
    static str(t2) {
      return `Vec4(${t2.join(", ")})`;
    }
    /**
     * Returns whether or not the vectors have exactly the same elements in the same position (when compared with ===)
     * @category Static
     *
     * @param a - The first vector.
     * @param b - The second vector.
     * @returns True if the vectors are equal, false otherwise.
     */
    static exactEquals(t2, e2) {
      return t2[0] === e2[0] && t2[1] === e2[1] && t2[2] === e2[2] && t2[3] === e2[3];
    }
    /**
     * Returns whether or not the vectors have approximately the same elements in the same position.
     * @category Static
     *
     * @param a - The first vector.
     * @param b - The second vector.
     * @returns True if the vectors are equal, false otherwise.
     */
    static equals(t2, e2) {
      const s2 = t2[0], i2 = t2[1], n2 = t2[2], r2 = t2[3], a2 = e2[0], o2 = e2[1], l2 = e2[2], c2 = e2[3];
      return Math.abs(s2 - a2) <= O * Math.max(1, Math.abs(s2), Math.abs(a2)) && Math.abs(i2 - o2) <= O * Math.max(1, Math.abs(i2), Math.abs(o2)) && Math.abs(n2 - l2) <= O * Math.max(1, Math.abs(n2), Math.abs(l2)) && Math.abs(r2 - c2) <= O * Math.max(1, Math.abs(r2), Math.abs(c2));
    }
  }
  D.prototype.sub = D.prototype.subtract;
  D.prototype.mul = D.prototype.multiply;
  D.prototype.div = D.prototype.divide;
  D.prototype.dist = D.prototype.distance;
  D.prototype.sqrDist = D.prototype.squaredDistance;
  D.sub = D.subtract;
  D.mul = D.multiply;
  D.div = D.divide;
  D.dist = D.distance;
  D.sqrDist = D.squaredDistance;
  D.sqrLen = D.squaredLength;
  D.mag = D.magnitude;
  D.length = D.magnitude;
  D.len = D.magnitude;
  class A extends Float32Array {
    /**
     * The number of bytes in a {@link Vec2}.
     */
    static BYTE_LENGTH = 2 * Float32Array.BYTES_PER_ELEMENT;
    /**
     * Create a {@link Vec2}.
     */
    constructor(...t2) {
      switch (t2.length) {
        case 2: {
          const e2 = t2[0];
          typeof e2 == "number" ? super([e2, t2[1]]) : super(e2, t2[1], 2);
          break;
        }
        case 1: {
          const e2 = t2[0];
          typeof e2 == "number" ? super([e2, e2]) : super(e2, 0, 2);
          break;
        }
        default:
          super(2);
          break;
      }
    }
    //============
    // Attributes
    //============
    // Getters and setters to make component access read better.
    // These are likely to be a little bit slower than direct array access.
    /**
     * The x component of the vector. Equivalent to `this[0];`
     * @category Vector components
     */
    get x() {
      return this[0];
    }
    set x(t2) {
      this[0] = t2;
    }
    /**
     * The y component of the vector. Equivalent to `this[1];`
     * @category Vector components
     */
    get y() {
      return this[1];
    }
    set y(t2) {
      this[1] = t2;
    }
    // Alternate set of getters and setters in case this is being used to define
    // a color.
    /**
     * The r component of the vector. Equivalent to `this[0];`
     * @category Color components
     */
    get r() {
      return this[0];
    }
    set r(t2) {
      this[0] = t2;
    }
    /**
     * The g component of the vector. Equivalent to `this[1];`
     * @category Color components
     */
    get g() {
      return this[1];
    }
    set g(t2) {
      this[1] = t2;
    }
    /**
     * The magnitude (length) of this.
     * Equivalent to `Vec2.magnitude(this);`
     *
     * Magnitude is used because the `length` attribute is already defined by
     * TypedArrays to mean the number of elements in the array.
     */
    get magnitude() {
      return Math.hypot(this[0], this[1]);
    }
    /**
     * Alias for {@link Vec2.magnitude}
     */
    get mag() {
      return this.magnitude;
    }
    /**
     * The squared magnitude (length) of `this`.
     * Equivalent to `Vec2.squaredMagnitude(this);`
     */
    get squaredMagnitude() {
      const t2 = this[0], e2 = this[1];
      return t2 * t2 + e2 * e2;
    }
    /**
     * Alias for {@link Vec2.squaredMagnitude}
     */
    get sqrMag() {
      return this.squaredMagnitude;
    }
    /**
     * A string representation of `this`
     * Equivalent to `Vec2.str(this);`
     */
    get str() {
      return A.str(this);
    }
    //===================
    // Instances methods
    //===================
    /**
     * Copy the values from another {@link Vec2} into `this`.
     *
     * @param a the source vector
     * @returns `this`
     */
    copy(t2) {
      return this.set(t2), this;
    }
    // Instead of zero(), use a.fill(0) for instances;
    /**
     * Adds a {@link Vec2} to `this`.
     * Equivalent to `Vec2.add(this, this, b);`
     *
     * @param b - The vector to add to `this`
     * @returns `this`
     */
    add(t2) {
      return this[0] += t2[0], this[1] += t2[1], this;
    }
    /**
     * Subtracts a {@link Vec2} from `this`.
     * Equivalent to `Vec2.subtract(this, this, b);`
     *
     * @param b - The vector to subtract from `this`
     * @returns `this`
     */
    subtract(t2) {
      return this[0] -= t2[0], this[1] -= t2[1], this;
    }
    /**
     * Alias for {@link Vec2.subtract}
     */
    sub(t2) {
      return this;
    }
    /**
     * Multiplies `this` by a {@link Vec2}.
     * Equivalent to `Vec2.multiply(this, this, b);`
     *
     * @param b - The vector to multiply `this` by
     * @returns `this`
     */
    multiply(t2) {
      return this[0] *= t2[0], this[1] *= t2[1], this;
    }
    /**
     * Alias for {@link Vec2.multiply}
     */
    mul(t2) {
      return this;
    }
    /**
     * Divides `this` by a {@link Vec2}.
     * Equivalent to `Vec2.divide(this, this, b);`
     *
     * @param b - The vector to divide `this` by
     * @returns {Vec2} `this`
     */
    divide(t2) {
      return this[0] /= t2[0], this[1] /= t2[1], this;
    }
    /**
     * Alias for {@link Vec2.divide}
     */
    div(t2) {
      return this;
    }
    /**
     * Scales `this` by a scalar number.
     * Equivalent to `Vec2.scale(this, this, b);`
     *
     * @param b - Amount to scale `this` by
     * @returns `this`
     */
    scale(t2) {
      return this[0] *= t2, this[1] *= t2, this;
    }
    /**
     * Calculates `this` scaled by a scalar value then adds the result to `this`.
     * Equivalent to `Vec2.scaleAndAdd(this, this, b, scale);`
     *
     * @param b - The vector to add to `this`
     * @param scale - The amount to scale `b` by before adding
     * @returns `this`
     */
    scaleAndAdd(t2, e2) {
      return this[0] += t2[0] * e2, this[1] += t2[1] * e2, this;
    }
    /**
     * Calculates the euclidian distance between another {@link Vec2} and `this`.
     * Equivalent to `Vec2.distance(this, b);`
     *
     * @param b - The vector to calculate the distance to
     * @returns Distance between `this` and `b`
     */
    distance(t2) {
      return A.distance(this, t2);
    }
    /**
     * Alias for {@link Vec2.distance}
     */
    dist(t2) {
      return 0;
    }
    /**
     * Calculates the squared euclidian distance between another {@link Vec2} and `this`.
     * Equivalent to `Vec2.squaredDistance(this, b);`
     *
     * @param b The vector to calculate the squared distance to
     * @returns Squared distance between `this` and `b`
     */
    squaredDistance(t2) {
      return A.squaredDistance(this, t2);
    }
    /**
     * Alias for {@link Vec2.squaredDistance}
     */
    sqrDist(t2) {
      return 0;
    }
    /**
     * Negates the components of `this`.
     * Equivalent to `Vec2.negate(this, this);`
     *
     * @returns `this`
     */
    negate() {
      return this[0] *= -1, this[1] *= -1, this;
    }
    /**
     * Inverts the components of `this`.
     * Equivalent to `Vec2.inverse(this, this);`
     *
     * @returns `this`
     */
    invert() {
      return this[0] = 1 / this[0], this[1] = 1 / this[1], this;
    }
    /**
     * Sets each component of `this` to it's absolute value.
     * Equivalent to `Vec2.abs(this, this);`
     *
     * @returns `this`
     */
    abs() {
      return this[0] = Math.abs(this[0]), this[1] = Math.abs(this[1]), this;
    }
    /**
     * Calculates the dot product of this and another {@link Vec2}.
     * Equivalent to `Vec2.dot(this, b);`
     *
     * @param b - The second operand
     * @returns Dot product of `this` and `b`
     */
    dot(t2) {
      return this[0] * t2[0] + this[1] * t2[1];
    }
    /**
     * Normalize `this`.
     * Equivalent to `Vec2.normalize(this, this);`
     *
     * @returns `this`
     */
    normalize() {
      return A.normalize(this, this);
    }
    //================
    // Static methods
    //================
    /**
     * Creates a new, empty {@link Vec2}
     * @category Static
     *
     * @returns A new 2D vector
     */
    static create() {
      return new A();
    }
    /**
     * Creates a new {@link Vec2} initialized with values from an existing vector
     * @category Static
     *
     * @param a - Vector to clone
     * @returns A new 2D vector
     */
    static clone(t2) {
      return new A(t2);
    }
    /**
     * Creates a new {@link Vec2} initialized with the given values
     * @category Static
     *
     * @param x - X component
     * @param y - Y component
     * @returns A new 2D vector
     */
    static fromValues(t2, e2) {
      return new A(t2, e2);
    }
    /**
     * Copy the values from one {@link Vec2} to another
     * @category Static
     *
     * @param out - the receiving vector
     * @param a - The source vector
     * @returns `out`
     */
    static copy(t2, e2) {
      return t2[0] = e2[0], t2[1] = e2[1], t2;
    }
    /**
     * Set the components of a {@link Vec2} to the given values
     * @category Static
     *
     * @param out - The receiving vector
     * @param x - X component
     * @param y - Y component
     * @returns `out`
     */
    static set(t2, e2, s2) {
      return t2[0] = e2, t2[1] = s2, t2;
    }
    /**
     * Adds two {@link Vec2}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static add(t2, e2, s2) {
      return t2[0] = e2[0] + s2[0], t2[1] = e2[1] + s2[1], t2;
    }
    /**
     * Subtracts vector b from vector a
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static subtract(t2, e2, s2) {
      return t2[0] = e2[0] - s2[0], t2[1] = e2[1] - s2[1], t2;
    }
    /**
     * Alias for {@link Vec2.subtract}
     * @category Static
     */
    static sub(t2, e2, s2) {
      return [0, 0];
    }
    /**
     * Multiplies two {@link Vec2}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static multiply(t2, e2, s2) {
      return t2[0] = e2[0] * s2[0], t2[1] = e2[1] * s2[1], t2;
    }
    /**
     * Alias for {@link Vec2.multiply}
     * @category Static
     */
    static mul(t2, e2, s2) {
      return [0, 0];
    }
    /**
     * Divides two {@link Vec2}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static divide(t2, e2, s2) {
      return t2[0] = e2[0] / s2[0], t2[1] = e2[1] / s2[1], t2;
    }
    /**
     * Alias for {@link Vec2.divide}
     * @category Static
     */
    static div(t2, e2, s2) {
      return [0, 0];
    }
    /**
     * Math.ceil the components of a {@link Vec2}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to ceil
     * @returns `out`
     */
    static ceil(t2, e2) {
      return t2[0] = Math.ceil(e2[0]), t2[1] = Math.ceil(e2[1]), t2;
    }
    /**
     * Math.floor the components of a {@link Vec2}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to floor
     * @returns `out`
     */
    static floor(t2, e2) {
      return t2[0] = Math.floor(e2[0]), t2[1] = Math.floor(e2[1]), t2;
    }
    /**
     * Returns the minimum of two {@link Vec2}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static min(t2, e2, s2) {
      return t2[0] = Math.min(e2[0], s2[0]), t2[1] = Math.min(e2[1], s2[1]), t2;
    }
    /**
     * Returns the maximum of two {@link Vec2}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static max(t2, e2, s2) {
      return t2[0] = Math.max(e2[0], s2[0]), t2[1] = Math.max(e2[1], s2[1]), t2;
    }
    /**
     * Math.round the components of a {@link Vec2}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to round
     * @returns `out`
     */
    static round(t2, e2) {
      return t2[0] = Math.round(e2[0]), t2[1] = Math.round(e2[1]), t2;
    }
    /**
     * Scales a {@link Vec2} by a scalar number
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The vector to scale
     * @param b - Amount to scale the vector by
     * @returns `out`
     */
    static scale(t2, e2, s2) {
      return t2[0] = e2[0] * s2, t2[1] = e2[1] * s2, t2;
    }
    /**
     * Adds two Vec2's after scaling the second operand by a scalar value
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @param scale - The amount to scale b by before adding
     * @returns `out`
     */
    static scaleAndAdd(t2, e2, s2, i2) {
      return t2[0] = e2[0] + s2[0] * i2, t2[1] = e2[1] + s2[1] * i2, t2;
    }
    /**
     * Calculates the euclidian distance between two {@link Vec2}s
     * @category Static
     *
     * @param a - The first operand
     * @param b - The second operand
     * @returns distance between `a` and `b`
     */
    static distance(t2, e2) {
      return Math.hypot(e2[0] - t2[0], e2[1] - t2[1]);
    }
    /**
     * Alias for {@link Vec2.distance}
     * @category Static
     */
    static dist(t2, e2) {
      return 0;
    }
    /**
     * Calculates the squared euclidian distance between two {@link Vec2}s
     * @category Static
     *
     * @param a - The first operand
     * @param b - The second operand
     * @returns Squared distance between `a` and `b`
     */
    static squaredDistance(t2, e2) {
      const s2 = e2[0] - t2[0], i2 = e2[1] - t2[1];
      return s2 * s2 + i2 * i2;
    }
    /**
     * Alias for {@link Vec2.distance}
     * @category Static
     */
    static sqrDist(t2, e2) {
      return 0;
    }
    /**
     * Calculates the magnitude (length) of a {@link Vec2}
     * @category Static
     *
     * @param a - Vector to calculate magnitude of
     * @returns Magnitude of a
     */
    static magnitude(t2) {
      let e2 = t2[0], s2 = t2[1];
      return Math.sqrt(e2 * e2 + s2 * s2);
    }
    /**
     * Alias for {@link Vec2.magnitude}
     * @category Static
     */
    static mag(t2) {
      return 0;
    }
    /**
     * Alias for {@link Vec2.magnitude}
     * @category Static
     * @deprecated Use {@link Vec2.magnitude} to avoid conflicts with builtin `length` methods/attribs
     *
     * @param a - vector to calculate length of
     * @returns length of a
     */
    // @ts-ignore: Length conflicts with Function.length
    static length(t2) {
      return 0;
    }
    /**
     * Alias for {@link Vec2.magnitude}
     * @category Static
     * @deprecated Use {@link Vec2.mag}
     */
    static len(t2) {
      return 0;
    }
    /**
     * Calculates the squared length of a {@link Vec2}
     * @category Static
     *
     * @param a - Vector to calculate squared length of
     * @returns Squared length of a
     */
    static squaredLength(t2) {
      const e2 = t2[0], s2 = t2[1];
      return e2 * e2 + s2 * s2;
    }
    /**
     * Alias for {@link Vec2.squaredLength}
     */
    static sqrLen(t2, e2) {
      return 0;
    }
    /**
     * Negates the components of a {@link Vec2}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to negate
     * @returns `out`
     */
    static negate(t2, e2) {
      return t2[0] = -e2[0], t2[1] = -e2[1], t2;
    }
    /**
     * Returns the inverse of the components of a {@link Vec2}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to invert
     * @returns `out`
     */
    static inverse(t2, e2) {
      return t2[0] = 1 / e2[0], t2[1] = 1 / e2[1], t2;
    }
    /**
     * Returns the absolute value of the components of a {@link Vec2}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to compute the absolute values of
     * @returns `out`
     */
    static abs(t2, e2) {
      return t2[0] = Math.abs(e2[0]), t2[1] = Math.abs(e2[1]), t2;
    }
    /**
     * Normalize a {@link Vec2}
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - Vector to normalize
     * @returns `out`
     */
    static normalize(t2, e2) {
      const s2 = e2[0], i2 = e2[1];
      let n2 = s2 * s2 + i2 * i2;
      return n2 > 0 && (n2 = 1 / Math.sqrt(n2)), t2[0] = e2[0] * n2, t2[1] = e2[1] * n2, t2;
    }
    /**
     * Calculates the dot product of two {@link Vec2}s
     * @category Static
     *
     * @param a - The first operand
     * @param b - The second operand
     * @returns Dot product of `a` and `b`
     */
    static dot(t2, e2) {
      return t2[0] * e2[0] + t2[1] * e2[1];
    }
    /**
     * Computes the cross product of two {@link Vec2}s
     * Note that the cross product must by definition produce a 3D vector.
     * For this reason there is also not instance equivalent for this function.
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @returns `out`
     */
    static cross(t2, e2, s2) {
      const i2 = e2[0] * s2[1] - e2[1] * s2[0];
      return t2[0] = t2[1] = 0, t2[2] = i2, t2;
    }
    /**
     * Performs a linear interpolation between two {@link Vec2}s
     * @category Static
     *
     * @param out - The receiving vector
     * @param a - The first operand
     * @param b - The second operand
     * @param t - Interpolation amount, in the range [0-1], between the two inputs
     * @returns `out`
     */
    static lerp(t2, e2, s2, i2) {
      const n2 = e2[0], r2 = e2[1];
      return t2[0] = n2 + i2 * (s2[0] - n2), t2[1] = r2 + i2 * (s2[1] - r2), t2;
    }
    /**
     * Transforms the {@link Vec2} with a {@link Mat2}
     *
     * @param out - The receiving vector
     * @param a - The vector to transform
     * @param m - Matrix to transform with
     * @returns `out`
     */
    static transformMat2(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1];
      return t2[0] = s2[0] * i2 + s2[2] * n2, t2[1] = s2[1] * i2 + s2[3] * n2, t2;
    }
    /**
     * Transforms the {@link Vec2} with a {@link Mat2d}
     *
     * @param out - The receiving vector
     * @param a - The vector to transform
     * @param m - Matrix to transform with
     * @returns `out`
     */
    static transformMat2d(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1];
      return t2[0] = s2[0] * i2 + s2[2] * n2 + s2[4], t2[1] = s2[1] * i2 + s2[3] * n2 + s2[5], t2;
    }
    /**
     * Transforms the {@link Vec2} with a {@link Mat3}
     * 3rd vector component is implicitly '1'
     *
     * @param out - The receiving vector
     * @param a - The vector to transform
     * @param m - Matrix to transform with
     * @returns `out`
     */
    static transformMat3(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1];
      return t2[0] = s2[0] * i2 + s2[3] * n2 + s2[6], t2[1] = s2[1] * i2 + s2[4] * n2 + s2[7], t2;
    }
    /**
     * Transforms the {@link Vec2} with a {@link Mat4}
     * 3rd vector component is implicitly '0'
     * 4th vector component is implicitly '1'
     *
     * @param out - The receiving vector
     * @param a - The vector to transform
     * @param m - Matrix to transform with
     * @returns `out`
     */
    static transformMat4(t2, e2, s2) {
      const i2 = e2[0], n2 = e2[1];
      return t2[0] = s2[0] * i2 + s2[4] * n2 + s2[12], t2[1] = s2[1] * i2 + s2[5] * n2 + s2[13], t2;
    }
    /**
     * Rotate a 2D vector
     * @category Static
     *
     * @param out - The receiving {@link Vec2}
     * @param a - The {@link Vec2} point to rotate
     * @param b - The origin of the rotation
     * @param rad - The angle of rotation in radians
     * @returns `out`
     */
    static rotate(t2, e2, s2, i2) {
      const n2 = e2[0] - s2[0], r2 = e2[1] - s2[1], a2 = Math.sin(i2), o2 = Math.cos(i2);
      return t2[0] = n2 * o2 - r2 * a2 + s2[0], t2[1] = n2 * a2 + r2 * o2 + s2[1], t2;
    }
    /**
     * Get the angle between two 2D vectors
     * @category Static
     *
     * @param a - The first operand
     * @param b - The second operand
     * @returns The angle in radians
     */
    static angle(t2, e2) {
      const s2 = t2[0], i2 = t2[1], n2 = e2[0], r2 = e2[1], a2 = Math.sqrt(s2 * s2 + i2 * i2) * Math.sqrt(n2 * n2 + r2 * r2), o2 = a2 && (s2 * n2 + i2 * r2) / a2;
      return Math.acos(Math.min(Math.max(o2, -1), 1));
    }
    /**
     * Set the components of a {@link Vec2} to zero
     * @category Static
     *
     * @param out - The receiving vector
     * @returns `out`
     */
    static zero(t2) {
      return t2[0] = 0, t2[1] = 0, t2;
    }
    /**
     * Returns whether or not the vectors have exactly the same elements in the same position (when compared with ===)
     * @category Static
     *
     * @param a - The first vector.
     * @param b - The second vector.
     * @returns `true` if the vectors components are ===, `false` otherwise.
     */
    static exactEquals(t2, e2) {
      return t2[0] === e2[0] && t2[1] === e2[1];
    }
    /**
     * Returns whether or not the vectors have approximately the same elements in the same position.
     * @category Static
     *
     * @param a - The first vector.
     * @param b - The second vector.
     * @returns `true` if the vectors are approximately equal, `false` otherwise.
     */
    static equals(t2, e2) {
      const s2 = t2[0], i2 = t2[1], n2 = e2[0], r2 = e2[1];
      return Math.abs(s2 - n2) <= O * Math.max(1, Math.abs(s2), Math.abs(n2)) && Math.abs(i2 - r2) <= O * Math.max(1, Math.abs(i2), Math.abs(r2));
    }
    /**
     * Returns a string representation of a vector
     * @category Static
     *
     * @param a - Vector to represent as a string
     * @returns String representation of the vector
     */
    static str(t2) {
      return `Vec2(${t2.join(", ")})`;
    }
  }
  A.prototype.sub = A.prototype.subtract;
  A.prototype.mul = A.prototype.multiply;
  A.prototype.div = A.prototype.divide;
  A.prototype.dist = A.prototype.distance;
  A.prototype.sqrDist = A.prototype.squaredDistance;
  A.sub = A.subtract;
  A.mul = A.multiply;
  A.div = A.divide;
  A.dist = A.distance;
  A.sqrDist = A.squaredDistance;
  A.sqrLen = A.squaredLength;
  A.mag = A.magnitude;
  A.length = A.magnitude;
  A.len = A.magnitude;
  function Me(d2) {
    return new Promise((t2, e2) => {
      const s2 = document.createElement("img");
      s2.onload = () => t2(s2), s2.onerror = e2, s2.src = d2, s2.crossOrigin = "anonymous", s2.loading = "eager";
    });
  }
  function be(d2) {
    return new Promise((t2, e2) => {
      const s2 = document.createElement("video");
      let i2 = false, n2 = false, r2 = false;
      s2.addEventListener(
        "playing",
        () => {
          i2 = true, a2();
        },
        true
      ), s2.addEventListener(
        "timeupdate",
        () => {
          n2 = true, a2();
        },
        true
      ), s2.addEventListener(
        "error",
        (o2) => {
          r2 = true, e2(o2);
        },
        true
      );
      function a2() {
        i2 && n2 && !r2 && t2(s2);
      }
      s2.src = d2, s2.playsInline = true, s2.crossOrigin = "anonymous", s2.autoplay = true, s2.loop = true, s2.muted = true, s2.play();
    });
  }
  function ee(d2, t2 = false) {
    return t2 ? be(d2) : Me(d2);
  }
  function se(d2) {
    return new Promise((t2, e2) => {
      (d2 instanceof HTMLImageElement ? d2.complete : d2.readyState >= 3) ? t2(d2) : (d2.onload = () => t2(d2), d2.onerror = e2);
    });
  }
  function xe(d2, t2, e2) {
    const s2 = d2.data, i2 = d2.width, n2 = d2.height;
    let r2, a2, o2, l2, c2, h2, m2, u2, f2, g2, p2, y2, L;
    const x2 = i2 - 1, M2 = n2 - 1, b2 = t2 + 1, w2 = t2 + b2, T = t2 + 1, v2 = t2 + T, P = 1 / (w2 * v2), z = [], E = [], F = [], _ = [], R = [], k2 = [];
    for (; e2-- > 0; ) {
      for (L = y2 = 0, h2 = 0; h2 < n2; h2++) {
        for (r2 = s2[L] * b2, a2 = s2[L + 1] * b2, o2 = s2[L + 2] * b2, l2 = s2[L + 3] * b2, m2 = 1; m2 <= t2; m2++)
          u2 = L + ((m2 > x2 ? x2 : m2) << 2), r2 += s2[u2++], a2 += s2[u2++], o2 += s2[u2++], l2 += s2[u2];
        for (c2 = 0; c2 < i2; c2++)
          z[y2] = r2, E[y2] = a2, F[y2] = o2, _[y2] = l2, h2 === 0 && (R[c2] = Math.min(c2 + b2, x2) << 2, k2[c2] = Math.max(c2 - t2, 0) << 2), f2 = L + R[c2], g2 = L + k2[c2], r2 += s2[f2++] - s2[g2++], a2 += s2[f2++] - s2[g2++], o2 += s2[f2++] - s2[g2++], l2 += s2[f2] - s2[g2], y2++;
        L += i2 << 2;
      }
      for (c2 = 0; c2 < i2; c2++) {
        for (p2 = c2, r2 = z[p2] * T, a2 = E[p2] * T, o2 = F[p2] * T, l2 = _[p2] * T, m2 = 1; m2 <= t2; m2++)
          p2 += m2 > M2 ? 0 : i2, r2 += z[p2], a2 += E[p2], o2 += F[p2], l2 += _[p2];
        for (y2 = c2 << 2, h2 = 0; h2 < n2; h2++)
          s2[y2] = r2 * P + 0.5 | 0, s2[y2 + 1] = a2 * P + 0.5 | 0, s2[y2 + 2] = o2 * P + 0.5 | 0, s2[y2 + 3] = l2 * P + 0.5 | 0, c2 === 0 && (R[h2] = Math.min(h2 + T, M2) * i2, k2[h2] = Math.max(h2 - t2, 0) * i2), f2 = c2 + R[h2], g2 = c2 + k2[h2], r2 += z[f2] - z[g2], a2 += E[f2] - E[g2], o2 += F[f2] - F[g2], l2 += _[f2] - _[g2], y2 += i2 << 2;
      }
    }
  }
  function Te(d2, t2) {
    const e2 = d2.data;
    for (let s2 = 0; s2 < e2.length; s2 += 4) {
      const i2 = e2[s2], n2 = e2[s2 + 1], r2 = e2[s2 + 2], a2 = e2[s2 + 3], o2 = i2 * 0.3 + n2 * 0.59 + r2 * 0.11;
      e2[s2] = o2 * (1 - t2) + i2 * t2, e2[s2 + 1] = o2 * (1 - t2) + n2 * t2, e2[s2 + 2] = o2 * (1 - t2) + r2 * t2, e2[s2 + 3] = a2;
    }
  }
  function we(d2, t2) {
    const e2 = d2.data;
    for (let s2 = 0; s2 < e2.length; s2 += 4) {
      const i2 = e2[s2], n2 = e2[s2 + 1], r2 = e2[s2 + 2], a2 = e2[s2 + 3];
      e2[s2] = i2 * t2, e2[s2 + 1] = n2 * t2, e2[s2 + 2] = r2 * t2, e2[s2 + 3] = a2;
    }
  }
  function $t(d2, t2) {
    const e2 = d2.data;
    for (let s2 = 0; s2 < e2.length; s2 += 4) {
      const i2 = e2[s2], n2 = e2[s2 + 1], r2 = e2[s2 + 2], a2 = e2[s2 + 3];
      e2[s2] = (i2 - 128) * t2 + 128, e2[s2 + 1] = (n2 - 128) * t2 + 128, e2[s2 + 2] = (r2 - 128) * t2 + 128, e2[s2 + 3] = a2;
    }
  }
  const S = (d2, t2, e2, s2, i2 = 0, n2 = 0, r2 = 1, a2 = 1) => Object.freeze({ cx: d2, cy: t2, x: e2, y: s2, ur: i2, vr: n2, up: r2, vp: a2 }), ct = (d2, t2, e2) => Object.freeze({ width: d2, height: t2, conf: e2 }), qt = [
    // TODO: 竖屏推荐
    ct(5, 5, [
      S(0, 0, -1, -1, 0, 0, 1, 1),
      S(1, 0, -0.5, -1, 0, 0, 1, 1),
      S(2, 0, 0, -1, 0, 0, 1, 1),
      S(3, 0, 0.5, -1, 0, 0, 1, 1),
      S(4, 0, 1, -1, 0, 0, 1, 1),
      S(0, 1, -1, -0.5, 0, 0, 1, 1),
      S(1, 1, -0.5, -0.5, 0, 0, 1, 1),
      S(2, 1, -0.0052029684413368305, -0.6131420587090777, 0, 0, 1, 1),
      S(3, 1, 0.5884227308309977, -0.3990805107556692, 0, 0, 1, 1),
      S(4, 1, 1, -0.5, 0, 0, 1, 1),
      S(0, 2, -1, 0, 0, 0, 1, 1),
      S(1, 2, -0.4210024670505933, -0.11895058380429502, 0, 0, 1, 1),
      S(2, 2, -0.1019613423315412, -0.023812118047224606, 0, -47, 0.629, 0.849),
      S(3, 2, 0.40275125660925437, -0.06345314544600389, 0, 0, 1, 1),
      S(4, 2, 1, 0, 0, 0, 1, 1),
      S(0, 3, -1, 0.5, 0, 0, 1, 1),
      S(1, 3, 0.06801958477287173, 0.5205913248960121, -31, -45, 1, 1),
      S(2, 3, 0.21446469120128908, 0.29331610114301043, 6, -56, 0.566, 1.321),
      S(3, 3, 0.5, 0.5, 0, 0, 1, 1),
      S(4, 3, 1, 0.5, 0, 0, 1, 1),
      S(0, 4, -1, 1, 0, 0, 1, 1),
      S(1, 4, -0.31378372841550195, 1, 0, 0, 1, 1),
      S(2, 4, 0.26153633255328046, 1, 0, 0, 1, 1),
      S(3, 4, 0.5, 1, 0, 0, 1, 1),
      S(4, 4, 1, 1, 0, 0, 1, 1)
    ]),
    // TODO: 横屏推荐
    ct(4, 4, [
      S(0, 0, -1, -1, 0, 0, 1, 1),
      S(1, 0, -0.33333333333333337, -1, 0, 0, 1, 1),
      S(2, 0, 0.33333333333333326, -1, 0, 0, 1, 1),
      S(3, 0, 1, -1, 0, 0, 1, 1),
      S(0, 1, -1, -0.04495399932657351, 0, 0, 1, 1),
      S(1, 1, -0.24056117520129328, -0.22465999020104, 0, 0, 1, 1),
      S(2, 1, 0.334758885767489, -0.00531297192779423, 0, 0, 1, 1),
      S(3, 1, 0.9989920470678106, -0.3382976020775408, 8, 0, 0.566, 1.792),
      S(0, 2, -1, 0.33333333333333326, 0, 0, 1, 1),
      S(1, 2, -0.3425497314639411, -27501607956947893e-21, 0, 0, 1, 1),
      S(2, 2, 0.3321437945812673, 0.1981776353859399, 0, 0, 1, 1),
      S(3, 2, 1, 0.0766118180296832, 0, 0, 1, 1),
      S(0, 3, -1, 1, 0, 0, 1, 1),
      S(1, 3, -0.33333333333333337, 1, 0, 0, 1, 1),
      S(2, 3, 0.33333333333333326, 1, 0, 0, 1, 1),
      S(3, 3, 1, 1, 0, 0, 1, 1)
    ]),
    ct(4, 4, [
      S(0, 0, -1, -1, 0, 0, 1, 2.075),
      S(1, 0, -0.33333333333333337, -1, 0, 0, 1, 1),
      S(2, 0, 0.33333333333333326, -1, 0, 0, 1, 1),
      S(3, 0, 1, -1, 0, 0, 1, 1),
      S(0, 1, -1, -0.4545779491139603, 0, 0, 1, 1),
      S(1, 1, -0.33333333333333337, -0.33333333333333337, 0, 0, 1, 1),
      S(2, 1, 0.0889403142626457, -0.6025711180694033, -32, 45, 1, 1),
      S(3, 1, 1, -0.33333333333333337, 0, 0, 1, 1),
      S(0, 2, -1, -0.07402408608567845, 1, 0, 1, 0.094),
      S(1, 2, -0.2719422694359541, 0.09775369930903222, 25, -18, 1.321, 0),
      S(2, 2, 0.19877414408395877, 0.4307383294587789, 48, -40, 0.755, 0.975),
      S(3, 2, 1, 0.33333333333333326, -37, 0, 1, 1),
      S(0, 3, -1, 1, 0, 0, 1, 1),
      S(1, 3, -0.33333333333333337, 1, 0, 0, 1, 1),
      S(2, 3, 0.5125850864305672, 1, -20, -18, 0, 1.604),
      S(3, 3, 1, 1, 0, 0, 1, 1)
    ]),
    ct(5, 5, [
      S(0, 0, -1, -1, 0, 0, 1, 1),
      S(1, 0, -0.4501953125, -1, 0, 55, 1, 2.075),
      S(2, 0, 0.1953125, -1, 0, 0, 1, 1),
      S(3, 0, 0.4580078125, -1, 0, -25, 1, 1),
      S(4, 0, 1, -1, 0, 0, 1, 1),
      S(0, 1, -1, -0.2514475377525607, -16, 0, 2.327, 0.943),
      S(1, 1, -0.55859375, -0.6609325945787148, 47, 0, 2.358, 0.377),
      S(2, 1, 0.232421875, -0.5244375756366635, -66, -25, 1.855, 1.164),
      S(3, 1, 0.685546875, -0.3753706470552125, 0, 0, 1, 1),
      S(4, 1, 1, -0.6699125300354287, 0, 0, 1, 1),
      S(0, 2, -1, 0.035910396862284255, 0, 0, 1, 1),
      S(1, 2, -0.4921875, 0.005378616309457018, 90, 23, 1, 1.981),
      S(2, 2, 0.021484375, -0.1365043639066228, 0, 42, 1, 1),
      S(3, 2, 0.4765625, 0.05925822904974043, -30, 0, 1.95, 0.44),
      S(4, 2, 1, 0.251428847823418, 0, 0, 1, 1),
      S(0, 3, -1, 0.6968336464764276, -68, 0, 1, 0.786),
      S(1, 3, -0.6904296875, 0.5890744209958608, -68, 0, 1, 1),
      S(2, 3, 0.1845703125, 0.3879238667654693, 61, 0, 1, 1),
      S(3, 3, 0.60546875, 0.4633553246018661, -47, -59, 0.849, 1.73),
      S(4, 3, 1, 0.6214021886400309, -33, 0, 0.377, 1.604),
      S(0, 4, -1, 1, 0, 0, 1, 1),
      S(1, 4, -0.5, 1, 0, -73, 1, 1),
      S(2, 4, -0.3271484375, 1, 0, -24, 0.314, 2.704),
      S(3, 4, 0.5, 1, 0, 0, 1, 1),
      S(4, 4, 1, 1, 0, 0, 1, 1)
    ])
  ], H = (d2, t2) => Math.random() * (t2 - d2) + d2;
  function Se(d2, t2, e2) {
    return Math.min(Math.max(d2, t2), e2);
  }
  function Ee(d2, t2, e2) {
    const s2 = Se((e2 - d2) / (t2 - d2), 0, 1);
    return s2 * s2 * (3 - 2 * s2);
  }
  function ve(d2, t2, e2, s2 = 2, i2 = 0.5, n2 = 0.1) {
    let r2 = [], a2 = i2;
    for (let c2 = 0; c2 < e2; c2++) {
      r2[c2] = [];
      for (let h2 = 0; h2 < t2; h2++)
        r2[c2][h2] = d2[c2 * t2 + h2];
    }
    const o2 = [
      [1, 2, 1],
      [2, 4, 2],
      [1, 2, 1]
    ], l2 = 16;
    for (let c2 = 0; c2 < s2; c2++) {
      const h2 = [];
      for (let m2 = 0; m2 < e2; m2++) {
        h2[m2] = [];
        for (let u2 = 0; u2 < t2; u2++) {
          if (u2 === 0 || u2 === t2 - 1 || m2 === 0 || m2 === e2 - 1) {
            h2[m2][u2] = r2[m2][u2];
            continue;
          }
          let f2 = 0, g2 = 0, p2 = 0, y2 = 0, L = 0, x2 = 0;
          for (let $2 = -1; $2 <= 1; $2++)
            for (let U = -1; U <= 1; U++) {
              const Y = o2[$2 + 1][U + 1], j2 = r2[m2 + $2][u2 + U];
              f2 += j2.x * Y, g2 += j2.y * Y, p2 += j2.ur * Y, y2 += j2.vr * Y, L += j2.up * Y, x2 += j2.vp * Y;
            }
          const M2 = f2 / l2, b2 = g2 / l2, w2 = p2 / l2, T = y2 / l2, v2 = L / l2, P = x2 / l2, z = r2[m2][u2], E = z.x * (1 - a2) + M2 * a2, F = z.y * (1 - a2) + b2 * a2, _ = z.ur * (1 - a2) + w2 * a2, R = z.vr * (1 - a2) + T * a2, k2 = z.up * (1 - a2) + v2 * a2, B = z.vp * (1 - a2) + P * a2;
          h2[m2][u2] = S(u2, m2, E, F, _, R, k2, B);
        }
      }
      r2 = h2, a2 = Math.min(1, Math.max(a2 + n2, 0));
    }
    for (let c2 = 0; c2 < e2; c2++)
      for (let h2 = 0; h2 < t2; h2++)
        d2[c2 * t2 + h2] = r2[c2][h2];
  }
  function pt(d2, t2) {
    return Pe(Math.sin(d2 * 12.9898 + t2 * 78.233) * 43758.5453);
  }
  function Pe(d2) {
    return d2 - Math.floor(d2);
  }
  function ze(d2, t2) {
    const e2 = Math.floor(d2), s2 = Math.floor(t2), i2 = e2 + 1, n2 = s2 + 1, r2 = d2 - e2, a2 = t2 - s2, o2 = r2 * r2 * (3 - 2 * r2), l2 = a2 * a2 * (3 - 2 * a2), c2 = pt(e2, s2), h2 = pt(i2, s2), m2 = pt(e2, n2), u2 = pt(i2, n2), f2 = c2 * (1 - o2) + h2 * o2, g2 = m2 * (1 - o2) + u2 * o2;
    return f2 * (1 - l2) + g2 * l2;
  }
  function Ie(d2, t2, e2, s2 = 1e-3) {
    const i2 = d2(t2 + s2, e2), n2 = d2(t2 - s2, e2), r2 = d2(t2, e2 + s2), a2 = d2(t2, e2 - s2), o2 = (i2 - n2) / (2 * s2), l2 = (r2 - a2) / (2 * s2), c2 = Math.sqrt(o2 * o2 + l2 * l2) || 1;
    return [o2 / c2, l2 / c2];
  }
  function ke(d2, t2, e2 = H(0.4, 0.6), s2 = H(0.3, 0.6), i2 = 0.8, n2 = Math.floor(H(3, 5)), r2 = H(0.2, 0.3), a2 = H(-0.1, -0.05)) {
    const o2 = d2, l2 = t2, c2 = [], h2 = 2 / (o2 - 1), m2 = 2 / (l2 - 1);
    for (let u2 = 0; u2 < l2; u2++)
      for (let f2 = 0; f2 < o2; f2++) {
        const g2 = f2 / (o2 - 1) * 2 - 1, p2 = u2 / (l2 - 1) * 2 - 1, y2 = f2 === 0 || f2 === o2 - 1 || u2 === 0 || u2 === l2 - 1, L = y2 ? 0 : H(-e2 * h2, e2 * h2), x2 = y2 ? 0 : H(-e2 * m2, e2 * m2);
        let M2 = g2 + L, b2 = p2 + x2;
        const w2 = y2 ? 0 : H(-60, 60), T = y2 ? 0 : H(-60, 60), v2 = y2 ? 1 : H(0.8, 1.2), P = y2 ? 1 : H(0.8, 1.2);
        if (!y2) {
          const z = (g2 + 1) / 2, E = (p2 + 1) / 2, [F, _] = Ie(ze, z, E, 1e-3);
          let R = F * s2, k2 = _ * s2;
          const B = Math.min(z, 1 - z, E, 1 - E), $2 = Ee(0, 1, B);
          R *= $2, k2 *= $2, M2 = M2 * (1 - i2) + (M2 + R) * i2, b2 = b2 * (1 - i2) + (b2 + k2) * i2;
        }
        c2.push(S(f2, u2, M2, b2, w2, T, v2, P));
      }
    return ve(c2, o2, l2, n2, r2, a2), ct(o2, l2, c2);
  }
  const De = `precision highp float;\r
\r
varying vec3 v_color;\r
varying vec2 v_uv;\r
uniform sampler2D u_texture;\r
uniform float u_time;\r
uniform float u_volume;\r
uniform float u_alpha;\r
\r
// 预计算常量\r
const float INV_255 = 1.0 / 255.0;\r
const float HALF_INV_255 = 0.5 / 255.0;\r
const float GRADIENT_NOISE_A = 52.9829189;\r
const vec2 GRADIENT_NOISE_B = vec2(0.06711056, 0.00583715);\r
\r
/* Gradient noise from Jorge Jimenez's presentation: */\r
/* http://www.iryoku.com/next-generation-post-processing-in-call-of-duty-advanced-warfare */\r
float gradientNoise(in vec2 uv) {\r
    return fract(GRADIENT_NOISE_A * fract(dot(uv, GRADIENT_NOISE_B)));\r
}\r
\r
// 优化的旋转函数，避免重复计算sin/cos\r
vec2 rot(vec2 v, float angle) {\r
    float s = sin(angle);\r
    float c = cos(angle);\r
    return vec2(c * v.x - s * v.y, s * v.x + c * v.y);\r
}\r
\r
void main() {\r
    // 合并计算以减少指令数\r
    float volumeEffect = u_volume * 2.0;\r
    float timeVolume = u_time + u_volume;\r
    \r
    float dither = INV_255 * gradientNoise(gl_FragCoord.xy) - HALF_INV_255;\r
    vec2 centeredUV = v_uv - vec2(0.2);\r
    vec2 rotatedUV = rot(centeredUV, timeVolume * 2.0);\r
    vec2 finalUV = rotatedUV * max(0.001, 1.0 - volumeEffect) + vec2(0.5);\r
    \r
    vec4 result = texture2D(u_texture, finalUV);\r
    \r
    float alphaVolumeFactor = u_alpha * max(0.5, 1.0 - u_volume * 0.5);\r
    result.rgb *= v_color * alphaVolumeFactor;\r
    result.a *= alphaVolumeFactor;\r
    \r
    result.rgb += vec3(dither);\r
    \r
    float dist = distance(v_uv, vec2(0.5));\r
    float vignette = smoothstep(0.8, 0.3, dist);\r
    float mask = 0.6 + vignette * 0.4;\r
    result.rgb *= mask;\r
    \r
    gl_FragColor = result;\r
}\r
`, Fe = `precision highp float;

attribute vec2 a_pos;
attribute vec3 a_color;
attribute vec2 a_uv;
varying vec3 v_color;
varying vec2 v_uv;

uniform float u_aspect;

void main() {
    v_color = a_color;
    v_uv = a_uv;
    vec2 pos = a_pos;
    if (u_aspect > 1.0) {
        pos.y *= u_aspect;
    } else {
        pos.x /= u_aspect;
    }
    gl_Position = vec4(pos, 0.0, 1.0);
}
`;
  class Ae {
    constructor(t2, e2, s2, i2 = "unknown") {
      this.label = i2, this.gl = t2, this.vertexShader = this.createShader(t2.VERTEX_SHADER, e2), this.fragmentShader = this.createShader(
        t2.FRAGMENT_SHADER,
        s2
      ), this.program = this.createProgram();
      const n2 = t2.getProgramParameter(this.program, t2.ACTIVE_ATTRIBUTES), r2 = {};
      for (let a2 = 0; a2 < n2; a2++) {
        const o2 = t2.getActiveAttrib(this.program, a2);
        if (!o2) continue;
        const l2 = t2.getAttribLocation(this.program, o2.name);
        l2 !== -1 && (r2[o2.name] = l2);
      }
      this.attrs = r2;
    }
    gl;
    program;
    vertexShader;
    fragmentShader;
    attrs;
    createShader(t2, e2) {
      const s2 = this.gl, i2 = s2.createShader(t2);
      if (!i2) throw new Error("Failed to create shader");
      if (s2.shaderSource(i2, e2), s2.compileShader(i2), !s2.getShaderParameter(i2, s2.COMPILE_STATUS))
        throw new Error(
          `Failed to compile shader for type ${t2} "${this.label}": ${s2.getShaderInfoLog(i2)}`
        );
      return i2;
    }
    createProgram() {
      const t2 = this.gl, e2 = t2.createProgram();
      if (!e2) throw new Error("Failed to create program");
      if (t2.attachShader(e2, this.vertexShader), t2.attachShader(e2, this.fragmentShader), t2.linkProgram(e2), t2.validateProgram(e2), !t2.getProgramParameter(e2, t2.LINK_STATUS)) {
        const s2 = t2.getProgramInfoLog(e2);
        throw t2.deleteProgram(e2), new Error(`Failed to link program "${this.label}": ${s2}`);
      }
      return e2;
    }
    use() {
      this.gl.useProgram(this.program);
    }
    notFoundUniforms = /* @__PURE__ */ new Set();
    warnUniformNotFound(t2) {
      this.notFoundUniforms.has(t2) || (this.notFoundUniforms.add(t2), console.warn(
        `Failed to get uniform location for program "${this.label}": ${t2}`
      ));
    }
    setUniform1f(t2, e2) {
      const s2 = this.gl, i2 = s2.getUniformLocation(this.program, t2);
      i2 ? s2.uniform1f(i2, e2) : this.warnUniformNotFound(t2);
    }
    setUniform2f(t2, e2, s2) {
      const i2 = this.gl, n2 = i2.getUniformLocation(this.program, t2);
      n2 ? i2.uniform2f(n2, e2, s2) : this.warnUniformNotFound(t2);
    }
    setUniform1i(t2, e2) {
      const s2 = this.gl, i2 = s2.getUniformLocation(this.program, t2);
      i2 ? s2.uniform1i(i2, e2) : this.warnUniformNotFound(t2);
    }
    dispose() {
      const t2 = this.gl;
      t2.deleteShader(this.vertexShader), t2.deleteShader(this.fragmentShader), t2.deleteProgram(this.program);
    }
  }
  class _e {
    constructor(t2, e2, s2, i2) {
      this.gl = t2, this.attrPos = e2, this.attrColor = s2, this.attrUV = i2;
      const n2 = t2.createBuffer();
      if (!n2) throw new Error("Failed to create vertex buffer");
      this.vertexBuffer = n2;
      const r2 = t2.createBuffer();
      if (!r2) throw new Error("Failed to create index buffer");
      this.indexBuffer = r2, this.bind(), this.vertexData = new Float32Array(0), this.indexData = new Uint16Array(0), this.resize(2, 2), this.update();
    }
    vertexWidth = 0;
    vertexHeight = 0;
    vertexBuffer;
    indexBuffer;
    vertexData;
    indexData;
    vertexIndexLength = 0;
    // 调试用途，开启线框模式
    wireFrame = false;
    setWireFrame(t2) {
      this.wireFrame = t2, this.resize(this.vertexWidth, this.vertexHeight);
    }
    setVertexPos(t2, e2, s2, i2) {
      const n2 = (t2 + e2 * this.vertexWidth) * 7;
      if (n2 >= this.vertexData.length - 1) {
        console.warn("Vertex position out of range", n2, this.vertexData.length);
        return;
      }
      this.vertexData[n2] = s2, this.vertexData[n2 + 1] = i2;
    }
    setVertexColor(t2, e2, s2, i2, n2) {
      const r2 = (t2 + e2 * this.vertexWidth) * 7 + 2;
      if (r2 >= this.vertexData.length - 2) {
        console.warn("Vertex color out of range", r2, this.vertexData.length);
        return;
      }
      this.vertexData[r2] = s2, this.vertexData[r2 + 1] = i2, this.vertexData[r2 + 2] = n2;
    }
    setVertexUV(t2, e2, s2, i2) {
      const n2 = (t2 + e2 * this.vertexWidth) * 7 + 5;
      if (n2 >= this.vertexData.length - 1) {
        console.warn("Vertex UV out of range", n2, this.vertexData.length);
        return;
      }
      this.vertexData[n2] = s2, this.vertexData[n2 + 1] = i2;
    }
    // 批量设置顶点数据的优化方法
    setVertexData(t2, e2, s2, i2, n2, r2, a2, o2, l2) {
      const c2 = (t2 + e2 * this.vertexWidth) * 7;
      if (c2 >= this.vertexData.length - 6) {
        console.warn("Vertex data out of range", c2, this.vertexData.length);
        return;
      }
      const h2 = this.vertexData;
      h2[c2] = s2, h2[c2 + 1] = i2, h2[c2 + 2] = n2, h2[c2 + 3] = r2, h2[c2 + 4] = a2, h2[c2 + 5] = o2, h2[c2 + 6] = l2;
    }
    getVertexIndexLength() {
      return this.vertexIndexLength;
    }
    draw() {
      const t2 = this.gl;
      this.wireFrame ? t2.drawElements(t2.LINES, this.vertexIndexLength, t2.UNSIGNED_SHORT, 0) : t2.drawElements(
        t2.TRIANGLES,
        this.vertexIndexLength,
        t2.UNSIGNED_SHORT,
        0
      );
    }
    resize(t2, e2) {
      this.vertexWidth = t2, this.vertexHeight = e2, this.vertexIndexLength = t2 * e2 * 6, this.wireFrame && (this.vertexIndexLength = t2 * e2 * 10);
      const s2 = new Float32Array(
        t2 * e2 * 7
      ), i2 = new Uint16Array(this.vertexIndexLength);
      this.vertexData = s2, this.indexData = i2;
      for (let r2 = 0; r2 < e2; r2++)
        for (let a2 = 0; a2 < t2; a2++) {
          const o2 = a2 / (t2 - 1) * 2 - 1, l2 = r2 / (e2 - 1) * 2 - 1;
          this.setVertexPos(a2, r2, o2 || 0, l2 || 0), this.setVertexColor(a2, r2, 1, 1, 1), this.setVertexUV(a2, r2, a2 / (t2 - 1), r2 / (e2 - 1));
        }
      for (let r2 = 0; r2 < e2 - 1; r2++)
        for (let a2 = 0; a2 < t2 - 1; a2++)
          if (this.wireFrame) {
            const o2 = (r2 * t2 + a2) * 10;
            i2[o2] = r2 * t2 + a2, i2[o2 + 1] = r2 * t2 + a2 + 1, i2[o2 + 2] = r2 * t2 + a2 + 1, i2[o2 + 3] = (r2 + 1) * t2 + a2, i2[o2 + 4] = (r2 + 1) * t2 + a2, i2[o2 + 5] = (r2 + 1) * t2 + a2 + 1, i2[o2 + 6] = (r2 + 1) * t2 + a2 + 1, i2[o2 + 7] = r2 * t2 + a2 + 1, i2[o2 + 8] = r2 * t2 + a2, i2[o2 + 9] = (r2 + 1) * t2 + a2;
          } else {
            const o2 = (r2 * t2 + a2) * 6;
            i2[o2] = r2 * t2 + a2, i2[o2 + 1] = r2 * t2 + a2 + 1, i2[o2 + 2] = (r2 + 1) * t2 + a2, i2[o2 + 3] = r2 * t2 + a2 + 1, i2[o2 + 4] = (r2 + 1) * t2 + a2 + 1, i2[o2 + 5] = (r2 + 1) * t2 + a2;
          }
      const n2 = this.gl;
      n2.bindBuffer(n2.ELEMENT_ARRAY_BUFFER, this.indexBuffer), n2.bufferData(n2.ELEMENT_ARRAY_BUFFER, this.indexData, n2.STATIC_DRAW);
    }
    bind() {
      const t2 = this.gl;
      t2.bindBuffer(t2.ARRAY_BUFFER, this.vertexBuffer), t2.bindBuffer(t2.ELEMENT_ARRAY_BUFFER, this.indexBuffer), this.attrPos !== void 0 && (t2.vertexAttribPointer(this.attrPos, 2, t2.FLOAT, false, 28, 0), t2.enableVertexAttribArray(this.attrPos)), this.attrColor !== void 0 && (t2.vertexAttribPointer(this.attrColor, 3, t2.FLOAT, false, 28, 8), t2.enableVertexAttribArray(this.attrColor)), this.attrUV !== void 0 && (t2.vertexAttribPointer(this.attrUV, 2, t2.FLOAT, false, 28, 20), t2.enableVertexAttribArray(this.attrUV));
    }
    update() {
      const t2 = this.gl;
      t2.bindBuffer(t2.ARRAY_BUFFER, this.vertexBuffer), t2.bufferData(t2.ARRAY_BUFFER, this.vertexData, t2.DYNAMIC_DRAW);
    }
    dispose() {
      this.gl.deleteBuffer(this.vertexBuffer), this.gl.deleteBuffer(this.indexBuffer);
    }
  }
  class Ce {
    color = C.fromValues(1, 1, 1);
    location = A.fromValues(0, 0);
    uTangent = A.fromValues(0, 0);
    vTangent = A.fromValues(0, 0);
    _uRot = 0;
    _vRot = 0;
    _uScale = 1;
    _vScale = 1;
    constructor() {
      Object.seal(this);
    }
    get uRot() {
      return this._uRot;
    }
    get vRot() {
      return this._vRot;
    }
    set uRot(t2) {
      this._uRot = t2, this.updateUTangent();
    }
    set vRot(t2) {
      this._vRot = t2, this.updateVTangent();
    }
    get uScale() {
      return this._uScale;
    }
    get vScale() {
      return this._vScale;
    }
    set uScale(t2) {
      this._uScale = t2, this.updateUTangent();
    }
    set vScale(t2) {
      this._vScale = t2, this.updateVTangent();
    }
    updateUTangent() {
      this.uTangent[0] = Math.cos(this._uRot) * this._uScale, this.uTangent[1] = Math.sin(this._uRot) * this._uScale;
    }
    updateVTangent() {
      this.vTangent[0] = -Math.sin(this._vRot) * this._vScale, this.vTangent[1] = Math.cos(this._vRot) * this._vScale;
    }
  }
  const at = I.fromValues(2, -2, 1, 1, -3, 3, -2, -1, 0, 0, 1, 0, 1, 0, 0, 0), lt = I.clone(at).transpose(), Z = D.create(), ft = D.create(), K = D.create(), tt = I.create(), et = I.create();
  function Re(d2, t2, e2, s2, i2 = A.create()) {
    Z[0] = d2 ** 3, Z[1] = d2 ** 2, Z[2] = d2, Z[3] = 1, ft.copy(Z), K[0] = t2 ** 3, K[1] = t2 ** 2, K[2] = t2, K[3] = 1, tt.copy(e2).transpose(), I.mul(tt, tt, at), I.mul(tt, lt, tt), D.transformMat4(Z, Z, tt);
    const n2 = K.dot(Z);
    et.copy(s2).transpose(), I.mul(et, et, at), I.mul(et, lt, et), D.transformMat4(ft, ft, et);
    const r2 = K.dot(ft);
    return i2.x = n2, i2.y = r2, i2;
  }
  function Vt(d2, t2, e2, s2, i2, n2 = I.create()) {
    const r2 = (l2) => l2.location[i2], a2 = (l2) => l2.uTangent[i2], o2 = (l2) => l2.vTangent[i2];
    return n2[0] = r2(d2), n2[1] = r2(t2), n2[2] = o2(d2), n2[3] = o2(t2), n2[4] = r2(e2), n2[5] = r2(s2), n2[6] = o2(e2), n2[7] = o2(s2), n2[8] = a2(d2), n2[9] = a2(t2), n2[10] = 0, n2[11] = 0, n2[12] = a2(e2), n2[13] = a2(s2), n2[14] = 0, n2[15] = 0, n2;
  }
  function vt(d2, t2, e2, s2, i2, n2 = I.create()) {
    const r2 = (a2) => a2.color[i2];
    return n2.fill(0), n2[0] = r2(d2), n2[1] = r2(t2), n2[4] = r2(e2), n2[5] = r2(s2), n2;
  }
  const G = D.create(), ut = D.create(), gt = D.create(), J = D.create(), st = I.create(), it = I.create(), nt = I.create(), yt = C.create();
  function Oe(d2, t2, e2, s2, i2) {
    return G[0] = d2 ** 3, G[1] = d2 ** 2, G[2] = d2, G[3] = 1, ut.copy(G), gt.copy(G), J[0] = t2 ** 3, J[1] = t2 ** 2, J[2] = t2, J[3] = 1, st.copy(e2).transpose(), I.mul(st, st, at), I.mul(st, lt, st), D.transformMat4(G, G, st), yt.r = J.dot(G), it.copy(s2).transpose(), I.mul(it, it, at), I.mul(it, lt, it), D.transformMat4(ut, ut, it), yt.g = J.dot(ut), nt.copy(i2).transpose(), I.mul(nt, nt, at), I.mul(nt, lt, nt), D.transformMat4(gt, gt, nt), yt.b = J.dot(gt), yt;
  }
  class We {
    _width = 0;
    _height = 0;
    _data = [];
    constructor(t2, e2) {
      this.resize(t2, e2), Object.seal(this);
    }
    resize(t2, e2) {
      this._width = t2, this._height = e2, this._data = new Array(t2 * e2).fill(0);
    }
    set(t2, e2, s2) {
      this._data[t2 + e2 * this._width] = s2;
    }
    get(t2, e2) {
      return this._data[t2 + e2 * this._width];
    }
    get width() {
      return this._width;
    }
    get height() {
      return this._height;
    }
  }
  class Ne extends _e {
    /**
     * 细分级别，越大曲线越平滑，但是性能消耗也越大
     */
    _subDivisions = 10;
    _controlPoints = new We(3, 3);
    constructor(t2, e2, s2, i2) {
      super(t2, e2, s2, i2), this.resizeControlPoints(3, 3), Object.seal(this);
    }
    setWireFrame(t2) {
      super.setWireFrame(t2), this.updateMesh();
    }
    /**
     * 以当前的控制点矩阵大小和细分级别为参考重新设置细分级别，此操作不会重设控制点数据
     * @param subDivisions 细分级别
     */
    resetSubdivition(t2) {
      this._subDivisions = t2, super.resize(
        (this._controlPoints.width - 1) * t2,
        (this._controlPoints.height - 1) * t2
      );
    }
    /**
     * 重设控制点矩阵尺寸，将会重置所有控制点的颜色和坐标数据
     * 请在调用此方法后重新设置颜色和坐标，并调用 updateMesh 方法更新网格
     * @param width 控制点宽度数量，必须大于等于 2
     * @param height 控制点高度数量，必须大于等于 2
     */
    resizeControlPoints(t2, e2) {
      if (!(t2 >= 2 && e2 >= 2))
        throw new Error("Control points must be larger than 3x3 or equal");
      this._controlPoints.resize(t2, e2);
      for (let s2 = 0; s2 < e2; s2++)
        for (let i2 = 0; i2 < t2; i2++) {
          const n2 = new Ce();
          n2.location.x = i2 / (t2 - 1) * 2 - 1, n2.location.y = s2 / (e2 - 1) * 2 - 1, n2.uTangent.x = 2 / (t2 - 1), n2.vTangent.y = 2 / (e2 - 1), this._controlPoints.set(i2, s2, n2);
        }
      this.resetSubdivition(this._subDivisions);
    }
    /**
     * 获取指定位置的控制点，然后可以设置颜色和坐标属性
     * 留意颜色属性和坐标属性的值范围均参考 WebGL 的定义
     * 即颜色各个组件取值 [0-1]，坐标取值 [-1, 1]
     * 点的位置以画面左下角为原点 (0,0)
     * @param x 需要获取的控制点的 x 坐标
     * @param y 需要获取的控制点的 y 坐标
     * @returns 控制点对象
     */
    getControlPoint(t2, e2) {
      return this._controlPoints.get(t2, e2);
    }
    tmpV2 = A.create();
    // 预分配重复使用的矩阵，避免频繁创建
    tempX = I.create();
    tempY = I.create();
    tempR = I.create();
    tempG = I.create();
    tempB = I.create();
    /**
     * 更新最终呈现的网格数据，此方法应在所有控制点或细分参数的操作完成后调用
     */
    updateMesh() {
      const t2 = this._subDivisions - 1, e2 = t2 * (this._controlPoints.height - 1), s2 = t2 * (this._controlPoints.width - 1), i2 = this._controlPoints.width, n2 = this._controlPoints.height, r2 = this._subDivisions, a2 = 1 / t2, o2 = 1 / s2, l2 = 1 / e2;
      for (let c2 = 0; c2 < i2 - 1; c2++)
        for (let h2 = 0; h2 < n2 - 1; h2++) {
          const m2 = this._controlPoints.get(c2, h2), u2 = this._controlPoints.get(c2, h2 + 1), f2 = this._controlPoints.get(c2 + 1, h2), g2 = this._controlPoints.get(c2 + 1, h2 + 1);
          Vt(m2, u2, f2, g2, "x", this.tempX), Vt(m2, u2, f2, g2, "y", this.tempY), vt(m2, u2, f2, g2, "r", this.tempR), vt(m2, u2, f2, g2, "g", this.tempG), vt(m2, u2, f2, g2, "b", this.tempB);
          const p2 = c2 / (i2 - 1), y2 = h2 / (n2 - 1), L = h2 * r2, x2 = c2 * r2;
          for (let M2 = 0; M2 < r2; M2++) {
            const b2 = M2 * a2, w2 = L + M2;
            for (let T = 0; T < r2; T++) {
              const v2 = T * a2, P = x2 + T, [z, E] = Re(
                b2,
                v2,
                this.tempX,
                this.tempY,
                this.tmpV2
              ), [F, _, R] = Oe(
                b2,
                v2,
                this.tempR,
                this.tempG,
                this.tempB
              ), k2 = p2 + T * o2, B = 1 - y2 - M2 * l2;
              this.setVertexData(w2, P, z, E, F, _, R, k2, B);
            }
          }
        }
      this.update();
    }
  }
  class Ht {
    constructor(t2, e2) {
      this.gl = t2;
      const s2 = t2.createTexture();
      if (!s2) throw new Error("Failed to create texture");
      this.tex = s2, t2.activeTexture(t2.TEXTURE0), t2.bindTexture(t2.TEXTURE_2D, s2), t2.texImage2D(
        t2.TEXTURE_2D,
        0,
        t2.RGBA,
        t2.RGBA,
        t2.UNSIGNED_BYTE,
        e2
      ), t2.texParameteri(t2.TEXTURE_2D, t2.TEXTURE_MIN_FILTER, t2.LINEAR), t2.texParameteri(t2.TEXTURE_2D, t2.TEXTURE_MAG_FILTER, t2.LINEAR), t2.texParameteri(t2.TEXTURE_2D, t2.TEXTURE_WRAP_S, t2.MIRRORED_REPEAT), t2.texParameteri(t2.TEXTURE_2D, t2.TEXTURE_WRAP_T, t2.MIRRORED_REPEAT);
    }
    tex;
    bind() {
      this.gl.bindTexture(this.gl.TEXTURE_2D, this.tex);
    }
    dispose() {
      this.gl.deleteTexture(this.tex);
    }
  }
  function Be(d2, t2) {
    if ("OffscreenCanvas" in window) return new OffscreenCanvas(d2, t2);
    const e2 = document.createElement("canvas");
    return e2.width = d2, e2.height = t2, e2;
  }
  class si extends te {
    gl;
    lastFrameTime = 0;
    frameTime = 0;
    currentImageData;
    lastTickTime = 0;
    smoothedVolume = 0;
    volume = 0;
    tickHandle = 0;
    maxFPS = 60;
    paused = false;
    staticMode = false;
    mainProgram;
    manualControl = false;
    reduceImageSizeCanvas = Be(
      32,
      32
    );
    targetSize = A.fromValues(0, 0);
    currentSize = A.fromValues(0, 0);
    isNoCover = true;
    meshStates = [];
    _disposed = false;
    // 性能监控
    frameCount = 0;
    lastFPSUpdate = 0;
    currentFPS = 0;
    enablePerformanceMonitoring = false;
    setManualControl(t2) {
      this.manualControl = t2;
    }
    setWireFrame(t2) {
      for (const e2 of this.meshStates)
        e2.mesh.setWireFrame(t2);
    }
    getControlPoint(t2, e2) {
      return this.meshStates[this.meshStates.length - 1]?.mesh?.getControlPoint(
        t2,
        e2
      );
    }
    resizeControlPoints(t2, e2) {
      return this.meshStates[this.meshStates.length - 1]?.mesh?.resizeControlPoints(t2, e2);
    }
    resetSubdivition(t2) {
      return this.meshStates[this.meshStates.length - 1]?.mesh?.resetSubdivition(
        t2
      );
    }
    onTick(t2) {
      if (this.tickHandle = 0, this.paused || this._disposed) return;
      this.updatePerformanceStats(t2), Number.isNaN(this.lastFrameTime) && (this.lastFrameTime = t2);
      const e2 = t2 - this.lastTickTime, s2 = t2 - this.lastFrameTime;
      if (this.lastFrameTime = t2, e2 < 1e3 / this.maxFPS) {
        this.requestTick();
        return;
      }
      this.frameTime += s2 * this.flowSpeed, this.onRedraw(this.frameTime, s2) && this.staticMode ? this.staticMode && (this.lastFrameTime = Number.NaN) : this.requestTick(), this.lastTickTime = t2;
    }
    checkIfResize() {
      const [t2, e2] = [this.targetSize.x, this.targetSize.y], [s2, i2] = [this.currentSize.x, this.currentSize.y];
      if (t2 !== s2 || e2 !== i2) {
        super.onResize(t2, e2);
        const n2 = this.gl;
        n2.bindFramebuffer(n2.FRAMEBUFFER, null), n2.viewport(0, 0, t2, e2), this.currentSize.x = t2, this.currentSize.y = e2;
      }
    }
    onRedraw(t2, e2) {
      const s2 = this.meshStates[this.meshStates.length - 1];
      let i2 = false;
      const n2 = e2 / 500;
      if (s2)
        if (s2.mesh.bind(), this.manualControl && s2.mesh.updateMesh(), this.isNoCover) {
          let o2 = false;
          for (let l2 = this.meshStates.length - 1; l2 >= 0; l2--) {
            const c2 = this.meshStates[l2];
            c2.alpha = Math.max(0, c2.alpha - n2), c2.alpha > 0 ? o2 = true : (c2.mesh.dispose(), c2.texture.dispose(), this.meshStates.splice(l2, 1));
          }
          i2 = !o2;
        } else {
          if (s2.alpha = Math.min(
            1,
            s2.alpha + n2
          ), s2.alpha >= 1) {
            const o2 = this.meshStates.splice(0, this.meshStates.length - 1);
            for (const l2 of o2)
              l2.mesh.dispose(), l2.texture.dispose();
          }
          i2 = this.meshStates.length === 1 && s2.alpha >= 1;
        }
      const r2 = this.gl;
      r2.bindFramebuffer(r2.FRAMEBUFFER, null), r2.blendFunc(r2.SRC_ALPHA, r2.ONE_MINUS_SRC_ALPHA), r2.clear(r2.COLOR_BUFFER_BIT), this.checkIfResize();
      const a2 = Math.min(1, e2 / 100);
      this.smoothedVolume += (this.volume - this.smoothedVolume) * a2, this.mainProgram.use(), r2.activeTexture(r2.TEXTURE0), this.mainProgram.setUniform1f("u_time", t2 / 1e4), this.mainProgram.setUniform1f(
        "u_aspect",
        this.manualControl ? 1 : this.canvas.width / this.canvas.height
      ), this.mainProgram.setUniform1i("u_texture", 0), this.mainProgram.setUniform1f("u_volume", this.volume);
      for (const o2 of this.meshStates)
        this.mainProgram.setUniform1f("u_alpha", o2.alpha), o2.texture.bind(), o2.mesh.bind(), o2.mesh.draw();
      return r2.flush(), i2;
    }
    onTickBinded = this.onTick.bind(this);
    requestTick() {
      this._disposed || this.tickHandle === 0 && (this.tickHandle = requestAnimationFrame(this.onTickBinded));
    }
    supportTextureFloat = true;
    constructor(t2) {
      super(t2);
      const e2 = t2.getContext("webgl");
      if (!e2) throw new Error("WebGL not supported");
      e2.getExtension("EXT_color_buffer_float") || console.warn("EXT_color_buffer_float not supported"), e2.getExtension("EXT_float_blend") || (console.warn("EXT_float_blend not supported"), this.supportTextureFloat = false), e2.getExtension("OES_texture_float_linear") || console.warn("OES_texture_float_linear not supported"), e2.getExtension("OES_texture_float") || (this.supportTextureFloat = false, console.warn("OES_texture_float not supported")), this.gl = e2, e2.enable(e2.BLEND), e2.enable(e2.DEPTH_TEST), e2.depthFunc(e2.ALWAYS), this.mainProgram = new Ae(
        e2,
        Fe,
        De,
        "main-program-mg"
      ), this.requestTick();
    }
    onResize(t2, e2) {
      this.targetSize.x = Math.ceil(t2), this.targetSize.y = Math.ceil(e2), this.requestTick();
    }
    setStaticMode(t2) {
      this.staticMode = t2, this.lastFrameTime = performance.now(), this.requestTick();
    }
    setFPS(t2) {
      this.maxFPS = t2;
    }
    pause() {
      this.tickHandle && (cancelAnimationFrame(this.tickHandle), this.tickHandle = 0), this.paused = true;
    }
    resume() {
      this.paused = false, this.requestTick();
    }
    async setAlbum(t2, e2) {
      if (t2 === void 0 || typeof t2 == "string" && t2.trim().length === 0) {
        this.isNoCover = true;
        return;
      }
      let s2 = null, i2 = 5;
      for (; !s2 && i2 > 0; )
        try {
          typeof t2 == "string" ? s2 = await ee(t2, e2) : s2 = await se(t2);
        } catch (c2) {
          console.warn(
            `failed on loading album resource, retrying (${i2})`,
            {
              albumSource: t2,
              error: c2
            }
          ), i2--;
        }
      if (!s2) {
        console.error("Failed to load album resource", t2), this.isNoCover = true;
        return;
      }
      this.isNoCover = false;
      const n2 = this.reduceImageSizeCanvas, r2 = n2.getContext("2d", {
        willReadFrequently: true
      });
      if (!r2) throw new Error("Failed to create canvas context");
      r2.clearRect(0, 0, n2.width, n2.height);
      const a2 = s2 instanceof HTMLVideoElement ? s2.videoWidth : s2.naturalWidth, o2 = s2 instanceof HTMLVideoElement ? s2.videoHeight : s2.naturalHeight;
      if (a2 * o2 === 0) throw new Error("Invalid image size");
      r2.drawImage(s2, 0, 0, a2, o2, 0, 0, n2.width, n2.height);
      const l2 = r2.getImageData(0, 0, n2.width, n2.height);
      if ($t(l2, 0.4), Te(l2, 3), $t(l2, 1.7), we(l2, 0.75), xe(l2, 2, 4), this.manualControl && this.meshStates.length > 0)
        this.meshStates[0].texture.dispose(), this.meshStates[0].texture = new Ht(this.gl, l2);
      else {
        const c2 = new Ne(
          this.gl,
          this.mainProgram.attrs.a_pos,
          this.mainProgram.attrs.a_color,
          this.mainProgram.attrs.a_uv
        );
        c2.resetSubdivition(15);
        const h2 = Math.random() > 0.8 ? ke(6, 6) : qt[Math.floor(Math.random() * qt.length)];
        c2.resizeControlPoints(h2.width, h2.height);
        const m2 = 2 / (h2.width - 1), u2 = 2 / (h2.height - 1);
        for (const p2 of h2.conf) {
          const y2 = c2.getControlPoint(p2.cx, p2.cy);
          y2.location.x = p2.x, y2.location.y = p2.y, y2.uRot = p2.ur * Math.PI / 180, y2.vRot = p2.vr * Math.PI / 180, y2.uScale = m2 * p2.up, y2.vScale = u2 * p2.vp;
        }
        c2.updateMesh(), this.currentImageData = l2;
        const f2 = new Ht(this.gl, l2), g2 = {
          mesh: c2,
          texture: f2,
          alpha: 0
        };
        this.meshStates.push(g2);
      }
      this.requestTick();
    }
    setLowFreqVolume(t2) {
      this.volume = t2 / 10;
    }
    setHasLyric(t2) {
    }
    dispose() {
      super.dispose(), this.tickHandle && (cancelAnimationFrame(this.tickHandle), this.tickHandle = 0), this._disposed = true, this.mainProgram.dispose();
      for (const t2 of this.meshStates)
        t2.mesh.dispose(), t2.texture.dispose();
    }
    enablePerformanceMonitor(t2) {
      this.enablePerformanceMonitoring = t2, t2 && (this.frameCount = 0, this.lastFPSUpdate = performance.now());
    }
    getCurrentFPS() {
      return this.currentFPS;
    }
    updatePerformanceStats(t2) {
      this.enablePerformanceMonitoring && (this.frameCount++, t2 - this.lastFPSUpdate > 1e3 && (this.currentFPS = this.frameCount, this.frameCount = 0, this.lastFPSUpdate = t2));
    }
  }
  class $e extends Container {
    time = 0;
  }
  class ii extends te {
    constructor(t2) {
      super(t2), this.canvas = t2, this.app = new Application({
        view: t2,
        resizeTo: this.canvas,
        powerPreference: "low-power",
        backgroundAlpha: 1
      }), this.rebuildFilters(), this.app.ticker.maxFPS = 30, this.app.ticker.add(this.onTick), this.app.ticker.start();
    }
    app;
    curContainer;
    staticMode = false;
    lastContainer = /* @__PURE__ */ new Set();
    onTick = (t2) => {
      for (const e2 of this.lastContainer)
        e2.alpha = Math.max(0, e2.alpha - t2 / 60), e2.alpha <= 0 && (this.app.stage.removeChild(e2), this.lastContainer.delete(e2), e2.destroy(true));
      if (this.curContainer) {
        this.curContainer.alpha = Math.min(
          1,
          this.curContainer.alpha + t2 / 60
        );
        const [e2, s2, i2, n2] = this.curContainer.children, r2 = Math.max(this.app.screen.width, this.app.screen.height);
        e2.position.set(this.app.screen.width / 2, this.app.screen.height / 2), s2.position.set(
          this.app.screen.width / 2.5,
          this.app.screen.height / 2.5
        ), i2.position.set(this.app.screen.width / 2, this.app.screen.height / 2), n2.position.set(this.app.screen.width / 2, this.app.screen.height / 2), e2.width = r2 * Math.sqrt(2), e2.height = e2.width, s2.width = r2 * 0.8, s2.height = s2.width, i2.width = r2 * 0.5, i2.height = i2.width, n2.width = r2 * 0.25, n2.height = n2.width, this.curContainer.time += t2 * this.flowSpeed, e2.rotation += t2 / 1e3 * this.flowSpeed, s2.rotation -= t2 / 500 * this.flowSpeed, i2.rotation += t2 / 1e3 * this.flowSpeed, n2.rotation -= t2 / 750 * this.flowSpeed, i2.x = this.app.screen.width / 2 + this.app.screen.width / 4 * Math.cos(this.curContainer.time / 1e3 * 0.75), i2.y = this.app.screen.height / 2 + this.app.screen.width / 4 * Math.cos(this.curContainer.time / 1e3 * 0.75), n2.x = this.app.screen.width / 2 + this.app.screen.width / 4 * 0.1 + Math.cos(this.curContainer.time * 6e-3 * 0.75), n2.y = this.app.screen.height / 2 + this.app.screen.width / 4 * 0.1 + Math.cos(this.curContainer.time * 6e-3 * 0.75), this.curContainer.alpha >= 1 && this.lastContainer.size === 0 && this.staticMode && this.app.ticker.stop();
      }
    };
    onResize(t2, e2) {
      super.onResize(t2, e2), this.app.resize(), this.rebuildFilters();
    }
    setRenderScale(t2) {
      super.setRenderScale(t2), this.rebuildFilters();
    }
    rebuildFilters() {
      const t$12 = Math.min(this.canvas.width, this.canvas.height), e2 = Math.max(this.canvas.width, this.canvas.height), s2 = new ColorMatrixFilter();
      s2.saturate(1.2, false);
      const i2 = new ColorMatrixFilter();
      i2.brightness(0.6, false);
      const n2 = new ColorMatrixFilter();
      n2.contrast(0.3, true);
      for (const r2 of this.app.stage.filters ?? [])
        r2.destroy();
      this.app.stage.filters = [], this.app.stage.filters.push(new BlurFilter(5, 1)), this.app.stage.filters.push(new BlurFilter(10, 1)), this.app.stage.filters.push(new BlurFilter(20, 2)), this.app.stage.filters.push(new BlurFilter(40, 2)), this.app.stage.filters.push(new BlurFilter(80, 2)), t$12 > 768 && this.app.stage.filters.push(new BlurFilter(160, 4)), t$12 > 768 * 2 && this.app.stage.filters.push(new BlurFilter(320, 4)), this.app.stage.filters.push(s2, i2, n2), this.app.stage.filters.push(new BlurFilter(5, 1)), Math.random() > 0.5 ? (this.app.stage.filters.push(
        new t({
          radius: (e2 + t$12) / 2,
          strength: 1,
          center: [0.25, 1]
        })
      ), this.app.stage.filters.push(
        new t({
          radius: (e2 + t$12) / 2,
          strength: 1,
          center: [0.75, 0]
        })
      )) : (this.app.stage.filters.push(
        new t({
          radius: (e2 + t$12) / 2,
          strength: 1,
          center: [0.75, 1]
        })
      ), this.app.stage.filters.push(
        new t({
          radius: (e2 + t$12) / 2,
          strength: 1,
          center: [0.25, 0]
        })
      ));
    }
    setStaticMode(t2 = false) {
      this.staticMode = t2, this.app.ticker.start();
    }
    setFPS(t2) {
      this.app.ticker.maxFPS = t2;
    }
    pause() {
      this.app.ticker.stop(), this.app.render();
    }
    resume() {
      this.app.ticker.start();
    }
    setLowFreqVolume(t2) {
    }
    setHasLyric(t2) {
    }
    async setAlbum(t2, e2) {
      if (!t2 || typeof t2 == "string" && t2.trim().length === 0)
        return;
      let s2 = null, i2 = 5, n2 = null;
      for (; !n2?.baseTexture?.resource?.valid && i2 > 0; )
        try {
          typeof t2 == "string" ? s2 = await ee(t2, e2) : s2 = await se(t2), n2 = Texture.from(s2, {
            resourceOptions: {
              autoLoad: false
            }
          }), await n2.baseTexture.resource.load();
        } catch (h2) {
          console.warn(
            `failed on loading album image, retrying (${i2})`,
            t2,
            h2
          ), n2 = null, i2--;
        }
      if (!n2) return;
      const r2 = new $e(), a2 = new Sprite(n2), o2 = new Sprite(n2), l2 = new Sprite(n2), c2 = new Sprite(n2);
      a2.anchor.set(0.5, 0.5), o2.anchor.set(0.5, 0.5), l2.anchor.set(0.5, 0.5), c2.anchor.set(0.5, 0.5), a2.rotation = Math.random() * Math.PI * 2, o2.rotation = Math.random() * Math.PI * 2, l2.rotation = Math.random() * Math.PI * 2, c2.rotation = Math.random() * Math.PI * 2, r2.addChild(a2, o2, l2, c2), this.curContainer && this.lastContainer.add(this.curContainer), this.curContainer = r2, this.app.stage.addChild(r2), this.curContainer.alpha = 0, this.app.ticker.start();
    }
    dispose() {
      super.dispose(), this.app.ticker.remove(this.onTick), this.app.destroy(true);
    }
    getElement() {
      return this.canvas;
    }
  }
  class ie {
    element;
    renderer;
    constructor(t2, e2) {
      this.renderer = t2, this.element = e2, e2.style.pointerEvents = "none", e2.style.zIndex = "-1", e2.style.contain = "strict";
    }
    static new(t2) {
      const e2 = document.createElement("canvas");
      return new ie(new t2(e2), e2);
    }
    setRenderScale(t2) {
      this.renderer.setRenderScale(t2);
    }
    setFlowSpeed(t2) {
      this.renderer.setFlowSpeed(t2);
    }
    setStaticMode(t2) {
      this.renderer.setStaticMode(t2);
    }
    setFPS(t2) {
      this.renderer.setFPS(t2);
    }
    pause() {
      this.renderer.pause();
    }
    resume() {
      this.renderer.resume();
    }
    setLowFreqVolume(t2) {
      this.renderer.setLowFreqVolume(t2);
    }
    setHasLyric(t2) {
      this.renderer.setHasLyric(t2);
    }
    setAlbum(t2, e2) {
      return this.renderer.setAlbum(t2, e2);
    }
    getElement() {
      return this.element;
    }
    dispose() {
      this.renderer.dispose(), this.element.remove();
    }
  }
  const qe = "_lyricLine_ut4sn_6", Ve = "_lyricBgLine_ut4sn_50", He = "_active_ut4sn_62", Ue = "_hasDuetLine_ut4sn_78", Ye = "_lyricDuetLine_ut4sn_79", je = "_lyricMainLine_ut4sn_98", Ge = "_romanWord_ut4sn_107", Xe = "_emphasizeWrapper_ut4sn_113", Ze = "_emphasize_ut4sn_113", Je = "_lyricSubLine_ut4sn_136", Qe = "_disableSpring_ut4sn_143", Ke = "_interludeDots_ut4sn_151", ts = "_enabled_ut4sn_163", es = "_tmpDisableTransition_ut4sn_189", W = {
    lyricLine: qe,
    lyricBgLine: Ve,
    active: He,
    hasDuetLine: Ue,
    lyricDuetLine: Ye,
    lyricMainLine: je,
    romanWord: Ge,
    emphasizeWrapper: Xe,
    emphasize: Ze,
    lyricSubLine: Je,
    disableSpring: Qe,
    interludeDots: Ke,
    enabled: ts,
    tmpDisableTransition: es
  }, ne = -1, wt = 0, ht = 1, xt = 2, Ft = 3, At = 4, _t = 5, Ct = 6, re = 7, ae = 8, Ut = typeof self == "object" ? self : globalThis, ss = (d2, t2) => {
    const e2 = (i2, n2) => (d2.set(n2, i2), i2), s2 = (i2) => {
      if (d2.has(i2))
        return d2.get(i2);
      const [n2, r2] = t2[i2];
      switch (n2) {
        case wt:
        case ne:
          return e2(r2, i2);
        case ht: {
          const a2 = e2([], i2);
          for (const o2 of r2)
            a2.push(s2(o2));
          return a2;
        }
        case xt: {
          const a2 = e2({}, i2);
          for (const [o2, l2] of r2)
            a2[s2(o2)] = s2(l2);
          return a2;
        }
        case Ft:
          return e2(new Date(r2), i2);
        case At: {
          const { source: a2, flags: o2 } = r2;
          return e2(new RegExp(a2, o2), i2);
        }
        case _t: {
          const a2 = e2(/* @__PURE__ */ new Map(), i2);
          for (const [o2, l2] of r2)
            a2.set(s2(o2), s2(l2));
          return a2;
        }
        case Ct: {
          const a2 = e2(/* @__PURE__ */ new Set(), i2);
          for (const o2 of r2)
            a2.add(s2(o2));
          return a2;
        }
        case re: {
          const { name: a2, message: o2 } = r2;
          return e2(new Ut[a2](o2), i2);
        }
        case ae:
          return e2(BigInt(r2), i2);
        case "BigInt":
          return e2(Object(BigInt(r2)), i2);
        case "ArrayBuffer":
          return e2(new Uint8Array(r2).buffer, r2);
        case "DataView": {
          const { buffer: a2 } = new Uint8Array(r2);
          return e2(new DataView(a2), r2);
        }
      }
      return e2(new Ut[n2](r2), i2);
    };
    return s2;
  }, Yt = (d2) => ss(/* @__PURE__ */ new Map(), d2)(0), rt = "", { toString: is } = {}, { keys: ns } = Object, ot = (d2) => {
    const t2 = typeof d2;
    if (t2 !== "object" || !d2)
      return [wt, t2];
    const e2 = is.call(d2).slice(8, -1);
    switch (e2) {
      case "Array":
        return [ht, rt];
      case "Object":
        return [xt, rt];
      case "Date":
        return [Ft, rt];
      case "RegExp":
        return [At, rt];
      case "Map":
        return [_t, rt];
      case "Set":
        return [Ct, rt];
      case "DataView":
        return [ht, e2];
    }
    return e2.includes("Array") ? [ht, e2] : e2.includes("Error") ? [re, e2] : [xt, e2];
  }, Lt = ([d2, t2]) => d2 === wt && (t2 === "function" || t2 === "symbol"), rs = (d2, t2, e2, s2) => {
    const i2 = (r2, a2) => {
      const o2 = s2.push(r2) - 1;
      return e2.set(a2, o2), o2;
    }, n2 = (r2) => {
      if (e2.has(r2))
        return e2.get(r2);
      let [a2, o2] = ot(r2);
      switch (a2) {
        case wt: {
          let c2 = r2;
          switch (o2) {
            case "bigint":
              a2 = ae, c2 = r2.toString();
              break;
            case "function":
            case "symbol":
              if (d2)
                throw new TypeError("unable to serialize " + o2);
              c2 = null;
              break;
            case "undefined":
              return i2([ne], r2);
          }
          return i2([a2, c2], r2);
        }
        case ht: {
          if (o2) {
            let m2 = r2;
            return o2 === "DataView" ? m2 = new Uint8Array(r2.buffer) : o2 === "ArrayBuffer" && (m2 = new Uint8Array(r2)), i2([o2, [...m2]], r2);
          }
          const c2 = [], h2 = i2([a2, c2], r2);
          for (const m2 of r2)
            c2.push(n2(m2));
          return h2;
        }
        case xt: {
          if (o2)
            switch (o2) {
              case "BigInt":
                return i2([o2, r2.toString()], r2);
              case "Boolean":
              case "Number":
              case "String":
                return i2([o2, r2.valueOf()], r2);
            }
          if (t2 && "toJSON" in r2)
            return n2(r2.toJSON());
          const c2 = [], h2 = i2([a2, c2], r2);
          for (const m2 of ns(r2))
            (d2 || !Lt(ot(r2[m2]))) && c2.push([n2(m2), n2(r2[m2])]);
          return h2;
        }
        case Ft:
          return i2([a2, r2.toISOString()], r2);
        case At: {
          const { source: c2, flags: h2 } = r2;
          return i2([a2, { source: c2, flags: h2 }], r2);
        }
        case _t: {
          const c2 = [], h2 = i2([a2, c2], r2);
          for (const [m2, u2] of r2)
            (d2 || !(Lt(ot(m2)) || Lt(ot(u2)))) && c2.push([n2(m2), n2(u2)]);
          return h2;
        }
        case Ct: {
          const c2 = [], h2 = i2([a2, c2], r2);
          for (const m2 of r2)
            (d2 || !Lt(ot(m2))) && c2.push(n2(m2));
          return h2;
        }
      }
      const { message: l2 } = r2;
      return i2([a2, { name: o2, message: l2 }], r2);
    };
    return n2;
  }, jt = (d2, { json: t2, lossy: e2 } = {}) => {
    const s2 = [];
    return rs(!(t2 || e2), !!t2, /* @__PURE__ */ new Map(), s2)(d2), s2;
  }, Gt = typeof structuredClone == "function" ? (
    /* c8 ignore start */
    ((d2, t2) => t2 && ("json" in t2 || "lossy" in t2) ? Yt(jt(d2, t2)) : structuredClone(d2))
  ) : (d2, t2) => Yt(jt(d2, t2)), as = (d2, t2) => d2.size === t2.size && [...d2].every((e2) => t2.has(e2)), os = (d2) => /^[\p{Unified_Ideograph}\u0800-\u9FFC]+$/u.test(d2);
  function cs(d2) {
    return (e2) => (d2(e2 + 1e-3) - d2(e2 - 1e-3)) / (2 * 1e-3);
  }
  function Xt(d2) {
    return cs(d2);
  }
  class Tt {
    currentPosition = 0;
    targetPosition = 0;
    currentTime = 0;
    params = {};
    currentSolver;
    getV;
    getV2;
    queueParams;
    queuePosition;
    constructor(t2 = 0) {
      this.targetPosition = t2, this.currentPosition = this.targetPosition, this.currentSolver = () => this.targetPosition, this.getV = () => 0, this.getV2 = () => 0;
    }
    resetSolver() {
      const t2 = this.getV(this.currentTime);
      this.currentTime = 0, this.currentSolver = ls(
        this.currentPosition,
        t2,
        this.targetPosition,
        0,
        this.params
      ), this.getV = Xt(this.currentSolver), this.getV2 = Xt(this.getV);
    }
    arrived() {
      return Math.abs(this.targetPosition - this.currentPosition) < 0.01 && this.getV(this.currentTime) < 0.01 && this.getV2(this.currentTime) < 0.01 && this.queueParams === void 0 && this.queuePosition === void 0;
    }
    setPosition(t2) {
      this.targetPosition = t2, this.currentPosition = t2, this.currentSolver = () => this.targetPosition, this.getV = () => 0, this.getV2 = () => 0;
    }
    update(t2 = 0) {
      this.currentTime += t2, this.currentPosition = this.currentSolver(this.currentTime), this.queueParams && (this.queueParams.time -= t2, this.queueParams.time <= 0 && this.updateParams({
        ...this.queueParams
      })), this.queuePosition && (this.queuePosition.time -= t2, this.queuePosition.time <= 0 && this.setTargetPosition(this.queuePosition.position)), this.arrived() && this.setPosition(this.targetPosition);
    }
    updateParams(t2, e2 = 0) {
      e2 > 0 ? this.queueParams = {
        ...this.queuePosition ?? {},
        ...t2,
        time: e2
      } : (this.queuePosition = void 0, this.params = {
        ...this.params,
        ...t2
      }, this.resetSolver());
    }
    setTargetPosition(t2, e2 = 0) {
      e2 > 0 ? this.queuePosition = {
        ...this.queuePosition ?? {},
        position: t2,
        time: e2
      } : (this.queuePosition = void 0, this.targetPosition = t2, this.resetSolver());
    }
    getCurrentPosition() {
      return this.currentPosition;
    }
  }
  function ls(d2, t2, e2, s2 = 0, i2) {
    const n2 = i2?.soft ?? false, r2 = i2?.stiffness ?? 100, a2 = i2?.damping ?? 10, o2 = i2?.mass ?? 1, l2 = e2 - d2;
    if (n2 || 1 <= a2 / (2 * Math.sqrt(r2 * o2))) {
      const f2 = -Math.sqrt(r2 / o2), g2 = -f2 * l2 - t2;
      return (p2) => (p2 -= s2, p2 < 0 ? d2 : e2 - (l2 + p2 * g2) * Math.E ** (p2 * f2));
    }
    const c2 = Math.sqrt(4 * o2 * r2 - a2 ** 2), h2 = (a2 * l2 - 2 * o2 * t2) / c2, m2 = 0.5 * c2 / o2, u2 = -(0.5 * a2) / o2;
    return (f2) => (f2 -= s2, f2 < 0 ? d2 : e2 - (Math.cos(f2 * m2) * l2 + Math.sin(f2 * m2) * h2) * Math.E ** (f2 * u2));
  }
  const It = [], kt = [];
  let Dt = false;
  function hs() {
    let d2 = kt.shift();
    for (; d2; ) {
      try {
        d2.resolve(d2.task());
      } catch (t2) {
        d2.reject(t2);
      }
      d2 = kt.shift();
    }
    for (d2 = It.shift(); d2; ) {
      try {
        d2.resolve(d2.task());
      } catch (t2) {
        d2.reject(t2);
      }
      d2 = It.shift();
    }
    Dt = false;
  }
  function oe() {
    Dt || (Dt = true, requestAnimationFrame(hs));
  }
  function ce(d2) {
    const t2 = {
      task: d2,
      resolve: () => {
      },
      reject: () => {
      }
    }, e2 = new Promise((s2, i2) => {
      t2.resolve = s2, t2.reject = i2;
    });
    return It.push(t2), oe(), e2;
  }
  function Zt(d2) {
    const t2 = {
      task: d2,
      resolve: () => {
      },
      reject: () => {
      }
    }, e2 = new Promise((s2, i2) => {
      t2.resolve = s2, t2.reject = i2;
    });
    return kt.push(t2), oe(), e2;
  }
  class ds {
    constructor(t2) {
      this.lyricPlayer = t2, this.element.setAttribute("class", W.lyricLine), this.rebuildStyle();
    }
    element = document.createElement("div");
    left = 0;
    top = 0;
    delay = 0;
    // 由 LyricPlayer 来设置
    lineSize = [0, 0];
    lineTransforms = {
      posX: new Tt(0),
      posY: new Tt(0)
    };
    async measureSize() {
      return await ce(() => [
        this.element.clientWidth,
        this.element.clientHeight
      ]);
    }
    lastStyle = "";
    show() {
      this.rebuildStyle();
    }
    hide() {
      this.rebuildStyle();
    }
    rebuildStyle() {
      let t2 = `transform:translate(${this.lineTransforms.posX.getCurrentPosition().toFixed(2)}px,${this.lineTransforms.posY.getCurrentPosition().toFixed(2)}px);`;
      !this.lyricPlayer.getEnableSpring() && this.isInSight && (t2 += `transition-delay:${this.delay}ms;`), t2 !== this.lastStyle && (this.lastStyle = t2, this.element.setAttribute("style", t2));
    }
    getElement() {
      return this.element;
    }
    setTransform(t2 = this.left, e2 = this.top, s2 = false, i2 = 0) {
      this.left = t2, this.top = e2, this.delay = i2 * 1e3 | 0, s2 || !this.lyricPlayer.getEnableSpring() ? (s2 && this.element.classList.add(W.tmpDisableTransition), this.lineTransforms.posX.setPosition(t2), this.lineTransforms.posY.setPosition(e2), this.lyricPlayer.getEnableSpring() ? this.rebuildStyle() : this.show(), s2 && requestAnimationFrame(() => {
        this.element.classList.remove(W.tmpDisableTransition);
      })) : (this.lineTransforms.posX.setTargetPosition(t2, i2), this.lineTransforms.posY.setTargetPosition(e2, i2));
    }
    update(t2 = 0) {
      this.lyricPlayer.getEnableSpring() && (this.lineTransforms.posX.update(t2), this.lineTransforms.posY.update(t2), this.isInSight ? this.show() : this.hide());
    }
    get isInSight() {
      const t2 = this.lineTransforms.posX.getCurrentPosition(), e2 = this.lineTransforms.posY.getCurrentPosition(), s2 = t2 + this.lineSize[0], i2 = e2 + this.lineSize[1], n2 = this.lyricPlayer.size[0], r2 = this.lyricPlayer.size[1];
      return !(t2 > n2 || e2 > r2 || s2 < 0 || i2 < 0);
    }
    dispose() {
      this.element.remove();
    }
  }
  function ms(d2) {
    const e2 = 2.5949095;
    return d2 < 0.5 ? (2 * d2) ** 2 * ((e2 + 1) * 2 * d2 - e2) / 2 : ((2 * d2 - 2) ** 2 * ((e2 + 1) * (d2 * 2 - 2) + e2) + 2) / 2;
  }
  function ps(d2) {
    return d2 === 1 ? 1 : 1 - 2 ** (-10 * d2);
  }
  const Q = (d2, t2, e2) => Math.max(d2, Math.min(t2, e2));
  class fs {
    element = document.createElement("div");
    dot0 = document.createElement("span");
    dot1 = document.createElement("span");
    dot2 = document.createElement("span");
    left = 0;
    top = 0;
    playing = true;
    lastStyle = "";
    currentInterlude;
    currentTime = 0;
    targetBreatheDuration = 1500;
    constructor() {
      this.element.className = W.interludeDots, this.element.appendChild(this.dot0), this.element.appendChild(this.dot1), this.element.appendChild(this.dot2);
    }
    getElement() {
      return this.element;
    }
    setTransform(t2 = this.left, e2 = this.top) {
      this.left = t2, this.top = e2, this.update();
    }
    setInterlude(t2) {
      this.currentInterlude = t2, this.currentTime = t2?.[0] ?? 0, t2 ? this.element.classList.add(W.enabled) : this.element.classList.remove(W.enabled);
    }
    pause() {
      this.playing = false, this.element.classList.remove(W.playing);
    }
    resume() {
      this.playing = true, this.element.classList.add(W.playing);
    }
    update(t2 = 0) {
      if (!this.playing) return;
      this.currentTime += t2;
      let e2 = "";
      if (e2 += `transform:translate(${this.left.toFixed(
        2
      )}px, ${this.top.toFixed(2)}px)`, this.currentInterlude) {
        const s2 = this.currentInterlude[1] - this.currentInterlude[0], i2 = this.currentTime - this.currentInterlude[0];
        if (i2 <= s2) {
          const n2 = s2 / Math.ceil(s2 / this.targetBreatheDuration);
          let r2 = 1, a2 = 1;
          r2 *= Math.sin(1.5 * Math.PI - i2 / n2 * 2) / 20 + 1, i2 < 2e3 && (r2 *= ps(i2 / 2e3)), i2 < 500 ? a2 = 0 : i2 < 1e3 && (a2 *= (i2 - 500) / 500), s2 - i2 < 750 && (r2 *= 1 - ms(
            (750 - (s2 - i2)) / 750 / 2
          )), s2 - i2 < 375 && (a2 *= Q(
            0,
            (s2 - i2) / 375,
            1
          ));
          const o2 = Math.max(0, s2 - 750);
          r2 = Math.max(0, r2) * 0.7, e2 += ` scale(${r2})`;
          const l2 = Q(
            0.25,
            i2 * 3 / o2 * 0.75,
            1
          ), c2 = Q(
            0.25,
            (i2 - o2 / 3) * 3 / o2 * 0.75,
            1
          ), h2 = Q(
            0.25,
            (i2 - o2 / 3 * 2) * 3 / o2 * 0.75,
            1
          );
          this.dot0.style.opacity = `${Q(
            0,
            Math.max(0, a2 * l2),
            1
          )}`, this.dot1.style.opacity = `${Q(
            0,
            Math.max(0, a2 * c2),
            1
          )}`, this.dot2.style.opacity = `${Q(
            0,
            Math.max(0, a2 * h2),
            1
          )}`;
        } else
          e2 += " scale(0)", this.dot0.style.opacity = "0", this.dot1.style.opacity = "0", this.dot2.style.opacity = "0";
        e2 += ";", this.lastStyle !== e2 && (this.element.setAttribute("style", e2), this.lastStyle = e2);
      }
    }
    dispose() {
      this.element.remove();
    }
  }
  class Rt extends EventTarget {
    element = document.createElement("div");
    currentTime = 0;
    /** @internal */
    lyricLinesSize = /* @__PURE__ */ new WeakMap();
    /** @internal */
    lyricLineElementMap = /* @__PURE__ */ new WeakMap();
    currentLyricLines = [];
    // protected currentLyricLineObjects: LyricLineBase[] = [];
    processedLines = [];
    lyricLinesIndexes = /* @__PURE__ */ new WeakMap();
    hotLines = /* @__PURE__ */ new Set();
    bufferedLines = /* @__PURE__ */ new Set();
    isNonDynamic = false;
    hasDuetLine = false;
    scrollToIndex = 0;
    disableSpring = false;
    interludeDotsSize = [0, 0];
    interludeDots = new fs();
    bottomLine = new ds(this);
    enableBlur = true;
    enableScale = true;
    hidePassedLines = false;
    scrollBoundary = [0, 0];
    currentLyricLineObjects = [];
    isSeeking = false;
    lastCurrentTime = 0;
    alignAnchor = "center";
    alignPosition = 0.35;
    scrollOffset = 0;
    size = [0, 0];
    allowScroll = true;
    isPageVisible = true;
    initialLayoutFinished = false;
    /**
     * 视图额外预渲染（overscan）距离，单位：像素。
     * 用于决定在视口之外多少距离内也认为是“可见”，以便提前创建/保留行元素。
     */
    overscanPx = 300;
    posXSpringParams = {
      mass: 1,
      damping: 10,
      stiffness: 100
    };
    posYSpringParams = {
      mass: 0.9,
      damping: 15,
      stiffness: 90
    };
    scaleSpringParams = {
      mass: 2,
      damping: 25,
      stiffness: 100
    };
    scaleForBGSpringParams = {
      mass: 1,
      damping: 20,
      stiffness: 50
    };
    onPageShow = () => {
      this.isPageVisible = true, this.setCurrentTime(this.currentTime, true);
    };
    onPageHide = () => {
      this.isPageVisible = false;
    };
    scrolledHandler = 0;
    isScrolled = false;
    /** @internal */
    resizeObserver = new ResizeObserver(((t2) => {
      let e2 = false, s2 = false;
      for (const i2 of t2)
        if (i2.target === this.element) {
          const n2 = i2.contentRect;
          this.size[0] = n2.width, this.size[1] = n2.height, s2 = true;
        } else if (i2.target === this.interludeDots.getElement())
          this.interludeDotsSize[0] = i2.target.clientWidth, this.interludeDotsSize[1] = i2.target.clientHeight, e2 = true;
        else if (i2.target === this.bottomLine.getElement()) {
          const n2 = [
            i2.target.clientWidth,
            i2.target.clientHeight
          ], r2 = this.bottomLine.lineSize;
          (n2[0] !== r2[0] || n2[1] !== r2[1]) && (this.bottomLine.lineSize = n2, e2 = true);
        } else {
          const n2 = this.lyricLineElementMap.get(i2.target);
          if (n2) {
            const r2 = [
              i2.target.clientWidth,
              i2.target.clientHeight
            ], a2 = this.lyricLinesSize.get(
              n2
            ) ?? [0, 0];
            (r2[0] !== a2[0] || r2[1] !== a2[1]) && (this.lyricLinesSize.set(n2, r2), n2.onLineSizeChange(r2), e2 = true);
          }
        }
      e2 && this.calcLayout(true), s2 && this.onResize();
    }));
    wordFadeWidth = 0.5;
    targetAlignIndex = 0;
    constructor() {
      super(), this.resizeObserver.observe(this.element), this.resizeObserver.observe(this.interludeDots.getElement()), this.element.classList.add(W.lyricPlayer), this.element.appendChild(this.interludeDots.getElement()), this.element.appendChild(this.bottomLine.getElement()), this.interludeDots.setTransform(0, 200), window.addEventListener("pageshow", this.onPageShow), window.addEventListener("pagehide", this.onPageHide);
      let t2 = 0, e2 = "none", s2 = 0, i2 = 0, n2 = 0, r2 = /* @__PURE__ */ Symbol("amll-scroll"), a2 = 0, o2 = 0;
      this.element.addEventListener("touchstart", (l2) => {
        this.beginScrollHandler() && (l2.preventDefault(), t2 = this.scrollOffset, s2 = l2.touches[0].screenY, a2 = s2, i2 = Date.now(), n2 = 0);
      }), this.element.addEventListener("touchmove", (l2) => {
        if (this.beginScrollHandler()) {
          l2.preventDefault();
          const c2 = l2.touches[0].screenY, h2 = c2 - s2, m2 = c2 - a2, u2 = m2 > 0 ? "down" : m2 < 0 ? "up" : "none";
          e2 !== u2 ? (e2 = u2, t2 = this.scrollOffset, s2 = c2, i2 = Date.now()) : this.scrollOffset = t2 - h2, a2 = c2, o2 = Date.now(), this.limitScrollOffset(), this.calcLayout(true);
        }
      }), this.element.addEventListener("touchend", (l2) => {
        if (this.beginScrollHandler()) {
          l2.preventDefault(), s2 = 0;
          const c2 = Date.now();
          if (c2 - o2 > 100) return this.endScrollHandler();
          const h2 = c2 - i2;
          n2 = (this.scrollOffset - t2) / h2 * 1e3;
          let m2 = 0;
          const u2 = /* @__PURE__ */ Symbol("amll-scroll");
          r2 = u2;
          const f2 = (g2) => {
            m2 ||= g2, r2 === u2 && this.beginScrollHandler() && (this.scrollOffset += n2 * (g2 - m2) / 1e3, n2 *= 0.99, this.limitScrollOffset(), this.calcLayout(true), Math.abs(n2) > 1 && !this.scrollBoundary.includes(this.scrollOffset) && requestAnimationFrame(f2), this.endScrollHandler(), m2 = g2);
          };
          requestAnimationFrame(f2), this.endScrollHandler();
        }
      }), this.element.addEventListener("wheel", (l2) => {
        this.beginScrollHandler() && (l2.deltaMode === l2.DOM_DELTA_PIXEL ? (this.scrollOffset += l2.deltaY, this.limitScrollOffset(), this.calcLayout(true)) : (this.scrollOffset += l2.deltaY * 50, this.limitScrollOffset(), this.calcLayout(false)), this.endScrollHandler());
      });
    }
    beginScrollHandler() {
      const t2 = this.allowScroll;
      return t2 && (this.isScrolled = true, clearTimeout(this.scrolledHandler), this.scrolledHandler = setTimeout(() => {
        this.isScrolled = false, this.scrollOffset = 0;
      }, 5e3)), t2;
    }
    endScrollHandler() {
    }
    limitScrollOffset() {
      this.scrollOffset = Math.max(
        Math.min(this.scrollBoundary[1], this.scrollOffset),
        this.scrollBoundary[0]
      );
    }
    /**
     * 设置文字动画的渐变宽度，单位以歌词行的主文字字体大小的倍数为单位，默认为 0.5，即一个全角字符的一半宽度
     *
     * 如果要模拟 Apple Music for Android 的效果，可以设置为 1
     *
     * 如果要模拟 Apple Music for iPad 的效果，可以设置为 0.5
     *
     * 如果想要近乎禁用渐变效果，可以设置成非常接近 0 的小数（例如 `0.0001` ），但是**不可以为 0**
     *
     * @param value 需要设置的渐变宽度，单位以歌词行的主文字字体大小的倍数为单位，默认为 0.5
     */
    setWordFadeWidth(t2 = 0.5) {
      this.wordFadeWidth = Math.max(1e-4, t2);
    }
    /**
     * 是否启用歌词行缩放效果，默认启用
     *
     * 如果启用，非选中的歌词行会轻微缩小以凸显当前播放歌词行效果
     *
     * 此效果对性能影响微乎其微，推荐启用
     * @param enable 是否启用歌词行缩放效果
     */
    setEnableScale(t2 = true) {
      this.enableScale = t2, this.calcLayout();
    }
    /**
     * 获取当前是否启用了歌词行缩放效果
     * @returns 是否启用歌词行缩放效果
     */
    getEnableScale() {
      return this.enableScale;
    }
    /**
     * 获取当前文字动画的渐变宽度，单位以歌词行的主文字字体大小的倍数为单位
     * @returns 当前文字动画的渐变宽度，单位以歌词行的主文字字体大小的倍数为单位
     */
    getWordFadeWidth() {
      return this.wordFadeWidth;
    }
    setIsSeeking(t2) {
      this.isSeeking = t2;
    }
    /**
     * 设置是否隐藏已经播放过的歌词行，默认不隐藏
     * @param hide 是否隐藏已经播放过的歌词行，默认不隐藏
     */
    setHidePassedLines(t2) {
      this.hidePassedLines = t2, this.calcLayout();
    }
    /**
     * 设置是否启用歌词行的模糊效果
     * @param enable 是否启用
     */
    setEnableBlur(t2) {
      this.enableBlur !== t2 && (this.enableBlur = t2, this.calcLayout());
    }
    /**
     * 设置目标歌词行的对齐方式，默认为 `center`
     *
     * - 设置成 `top` 的话将会向目标歌词行的顶部对齐
     * - 设置成 `bottom` 的话将会向目标歌词行的底部对齐
     * - 设置成 `center` 的话将会向目标歌词行的垂直中心对齐
     * @param alignAnchor 歌词行对齐方式，详情见函数说明
     */
    setAlignAnchor(t2) {
      this.alignAnchor = t2;
    }
    /**
     * 设置默认的歌词行对齐位置，相对于整个歌词播放组件的大小位置，默认为 `0.5`
     * @param alignPosition 一个 `[0.0-1.0]` 之间的任意数字，代表组件高度由上到下的比例位置
     */
    setAlignPosition(t2) {
      this.alignPosition = t2;
    }
    /**
     * 设置 overscan（视图上下额外缓冲渲染区）距离，单位：像素。
     * @param px 像素值，默认 300
     */
    setOverscanPx(t2) {
      this.overscanPx = Math.max(0, t2 | 0);
    }
    /** 获取当前 overscan 像素距离 */
    getOverscanPx() {
      return this.overscanPx;
    }
    /**
     * 设置是否使用物理弹簧算法实现歌词动画效果，默认启用
     *
     * 如果启用，则会通过弹簧算法实时处理歌词位置，但是需要性能足够强劲的电脑方可流畅运行
     *
     * 如果不启用，则会回退到基于 `transition` 的过渡效果，对低性能的机器比较友好，但是效果会比较单一
     */
    setEnableSpring(t2 = true) {
      this.disableSpring = !t2, t2 ? this.element.classList.remove(W.disableSpring) : this.element.classList.add(W.disableSpring), this.calcLayout(true);
    }
    /**
     * 获取当前是否启用了物理弹簧
     * @returns 是否启用物理弹簧
     */
    getEnableSpring() {
      return !this.disableSpring;
    }
    /**
     * 获取当前播放时间里是否处于间奏区间
     * 如果是则会返回单位为毫秒的始末时间
     * 否则返回 undefined
     *
     * 这个只允许内部调用
     * @returns [开始时间,结束时间,大概处于的歌词行ID,下一句是否为对唱歌词] 或 undefined 如果不处于间奏区间
     */
    getCurrentInterlude() {
      if (this.bufferedLines.size > 0) return;
      const t2 = this.currentTime + 20, e2 = this.scrollToIndex;
      if (e2 === 0) {
        if (this.processedLines[0]?.startTime) {
          if (this.processedLines[0].startTime > t2)
            return [
              t2,
              Math.max(t2, this.processedLines[0].startTime - 250),
              -2,
              this.processedLines[0].isDuet
            ];
          if (this.processedLines[1].startTime > t2 && this.processedLines[0].endTime < t2)
            return [
              Math.max(this.processedLines[0].endTime, t2),
              this.processedLines[1].startTime,
              0,
              this.processedLines[1].isDuet
            ];
        }
      } else if (this.processedLines[e2]?.endTime && this.processedLines[e2 + 1]?.startTime) {
        if (this.processedLines[e2 + 1].startTime > t2 && this.processedLines[e2].endTime < t2)
          return [
            Math.max(this.processedLines[e2].endTime, t2),
            this.processedLines[e2 + 1].startTime,
            e2,
            this.processedLines[e2 + 1].isDuet
          ];
        if (this.processedLines[e2 + 2]?.startTime && this.processedLines[e2 + 2].startTime > t2 && this.processedLines[e2 + 1].endTime < t2)
          return [
            Math.max(this.processedLines[e2 + 1].endTime, t2),
            this.processedLines[e2 + 2].startTime,
            e2 + 1,
            this.processedLines[e2 + 2].isDuet
          ];
      }
    }
    /**
     * 设置当前播放歌词，要注意传入后这个数组内的信息不得修改，否则会发生错误
     * @param lines 歌词数组
     * @param initialTime 初始时间，默认为 0
     */
    setLyricLines(t2, e2 = 0) {
      this.initialLayoutFinished = true;
      for (const s2 of t2)
        for (const i2 of s2.words)
          i2.word = i2.word.replace(/\s+/g, " ");
      this.lastCurrentTime = e2, this.currentTime = e2, this.currentLyricLines = Gt(t2), this.processedLines = Gt(t2), this.isNonDynamic = true;
      for (const s2 of this.processedLines)
        if (s2.words.length > 1) {
          this.isNonDynamic = false;
          break;
        }
      this.hasDuetLine = this.processedLines.some((s2) => s2.isDuet);
      for (let s2 = this.processedLines.length - 1; s2 >= 0; s2--) {
        const i2 = this.processedLines[s2];
        if (i2.isBG) continue;
        const n2 = this.processedLines[s2 - 1];
        n2 ? i2.startTime = Math.max(
          Math.min(n2.endTime, i2.startTime),
          i2.startTime - 1e3
        ) : i2.startTime = Math.max(0, i2.startTime - 1e3);
      }
      for (let s2 = this.processedLines.length - 1; s2 >= 0; s2--) {
        const i2 = this.processedLines[s2];
        if (i2.isBG) continue;
        const n2 = this.processedLines[s2 + 1];
        if (n2?.isBG) {
          const r2 = Math.min(
            ...n2.words.filter((c2) => c2.word.trim().length > 0).map((c2) => c2.startTime),
            i2.startTime
          ), a2 = Math.max(
            ...n2.words.filter((c2) => c2.word.trim().length > 0).map((c2) => c2.endTime),
            i2.endTime
          ), o2 = Math.min(r2, i2.startTime), l2 = Math.max(a2, i2.endTime);
          n2.startTime = o2, n2.endTime = l2;
        }
      }
      for (const s2 of this.currentLyricLineObjects)
        s2.dispose();
      this.interludeDots.setInterlude(void 0), this.hotLines.clear(), this.bufferedLines.clear(), this.setCurrentTime(0, true);
    }
    /**
     * 设置当前播放进度，单位为毫秒且**必须是整数**，此时将会更新内部的歌词进度信息
     * 内部会根据调用间隔和播放进度自动决定如何滚动和显示歌词，所以这个的调用频率越快越准确越好
     *
     * 调用完成后，可以每帧调用 `update` 函数来执行歌词动画效果
     * @param time 当前播放进度，单位为毫秒
     */
    setCurrentTime(t2, e2 = false) {
      if (this.currentTime = t2, !this.initialLayoutFinished && !e2) return;
      const s2 = /* @__PURE__ */ new Set(), i2 = /* @__PURE__ */ new Set(), n2 = /* @__PURE__ */ new Set();
      for (const r2 of this.hotLines) {
        const a2 = this.processedLines[r2];
        if (a2) {
          if (a2.isBG) continue;
          const o2 = this.processedLines[r2 + 1];
          if (o2?.isBG) {
            const l2 = this.processedLines[r2 + 2], c2 = Math.min(a2.startTime, o2?.startTime), h2 = Math.min(
              Math.max(a2.endTime, l2?.startTime ?? Number.MAX_VALUE),
              Math.max(a2.endTime, o2?.endTime)
            );
            (c2 > t2 || h2 <= t2) && (this.hotLines.delete(r2), s2.add(r2), this.hotLines.delete(r2 + 1), s2.add(r2 + 1), e2 && (this.currentLyricLineObjects[r2]?.disable(), this.currentLyricLineObjects[r2 + 1]?.disable()));
          } else (a2.startTime > t2 || a2.endTime <= t2) && (this.hotLines.delete(r2), s2.add(r2), e2 && this.currentLyricLineObjects[r2]?.disable());
        } else
          this.hotLines.delete(r2), s2.add(r2), e2 && this.currentLyricLineObjects[r2]?.disable();
      }
      this.currentLyricLineObjects.forEach((r2, a2, o2) => {
        const l2 = r2.getLine();
        !l2.isBG && l2.startTime <= t2 && l2.endTime > t2 && (this.hotLines.has(a2) || (this.hotLines.add(a2), n2.add(a2), e2 && r2.enable(), o2[a2 + 1]?.getLine()?.isBG && (this.hotLines.add(a2 + 1), n2.add(a2 + 1), e2 && o2[a2 + 1].enable())));
      });
      for (const r2 of this.bufferedLines)
        this.hotLines.has(r2) || (i2.add(r2), e2 && this.currentLyricLineObjects[r2]?.disable());
      if (e2) {
        this.bufferedLines.size > 0 ? this.scrollToIndex = Math.min(...this.bufferedLines) : this.scrollToIndex = this.processedLines.findIndex(
          (r2) => r2.startTime >= t2
        ), this.bufferedLines.clear();
        for (const r2 of this.hotLines)
          this.bufferedLines.add(r2);
        this.calcLayout();
      } else if (i2.size > 0 || n2.size > 0)
        if (i2.size === 0 && n2.size > 0) {
          for (const r2 of n2)
            this.bufferedLines.add(r2), this.currentLyricLineObjects[r2]?.enable();
          this.scrollToIndex = Math.min(...this.bufferedLines), this.calcLayout();
        } else if (n2.size === 0 && i2.size > 0) {
          if (as(i2, this.bufferedLines)) {
            for (const r2 of this.bufferedLines)
              this.hotLines.has(r2) || (this.bufferedLines.delete(r2), this.currentLyricLineObjects[r2]?.disable());
            this.calcLayout();
          }
        } else {
          for (const r2 of n2)
            this.bufferedLines.add(r2), this.currentLyricLineObjects[r2]?.enable();
          for (const r2 of i2)
            this.bufferedLines.delete(r2), this.currentLyricLineObjects[r2]?.disable();
          this.bufferedLines.size > 0 && (this.scrollToIndex = Math.min(...this.bufferedLines)), this.calcLayout();
        }
      this.lastCurrentTime = t2;
    }
    /**
     * 重新布局定位歌词行的位置，调用完成后再逐帧调用 `update`
     * 函数即可让歌词通过动画移动到目标位置。
     *
     * 函数有一个 `force` 参数，用于指定是否强制修改布局，也就是不经过动画直接调整元素位置和大小。
     *
     * 此函数还有一个 `reflow` 参数，用于指定是否需要重新计算布局
     *
     * 因为计算布局必定会导致浏览器重排布局，所以会大幅度影响流畅度和性能，故请只在以下情况下将其​设置为 true：
     *
     * 1. 歌词页面大小发生改变时（这个组件会自行处理）
     * 2. 加载了新的歌词时（不论前后歌词是否完全一样）
     * 3. 用户自行跳转了歌曲播放位置（不论距离远近）
     *
     * @param force 是否不经过动画直接修改布局定位
     * @param reflow 是否进行重新布局（重新计算每行歌词大小）
     */
    async calcLayout(t2 = false) {
      const e2 = this.getCurrentInterlude();
      let s2 = -this.scrollOffset, i2 = this.scrollToIndex, n2 = 0;
      e2 ? (n2 = e2[1] - e2[0], n2 >= 4e3 && this.currentLyricLineObjects[e2[2] + 1] && (i2 = e2[2] + 1)) : this.interludeDots.setInterlude(void 0);
      const r2 = this.size[1] / 5, a2 = this.currentLyricLineObjects.slice(0, i2).reduce(
        (u2, f2) => u2 + (f2.getLine().isBG && this.isPlaying ? 0 : this.lyricLinesSize.get(f2)?.[1] ?? r2),
        0
      );
      this.scrollBoundary[0] = -a2, s2 -= a2, s2 += this.size[1] * this.alignPosition;
      const o2 = this.currentLyricLineObjects[i2];
      if (this.targetAlignIndex = i2, o2) {
        const u2 = this.lyricLinesSize.get(o2)?.[1] ?? r2;
        switch (this.alignAnchor) {
          case "bottom":
            s2 -= u2;
            break;
          case "center":
            s2 -= u2 / 2;
            break;
        }
      }
      const l2 = Math.max(...this.bufferedLines);
      let c2 = 0, h2 = t2 ? 0 : 0.05, m2 = false;
      this.currentLyricLineObjects.forEach((u2, f2) => {
        const g2 = this.bufferedLines.has(f2), p2 = g2 || f2 >= this.scrollToIndex && f2 < l2, y2 = u2.getLine();
        !m2 && n2 >= 4e3 && (f2 === this.scrollToIndex && e2?.[2] === -2 || f2 === this.scrollToIndex + 1) && (m2 = true, this.interludeDots.setTransform(0, s2), e2 && this.interludeDots.setInterlude([e2[0], e2[1]]), s2 += this.interludeDotsSize[1]);
        let L;
        this.hidePassedLines ? f2 < (e2 ? e2[2] + 1 : this.scrollToIndex) && this.isPlaying ? L = 1e-5 : g2 ? L = 0.85 : L = this.isNonDynamic ? 0.2 : 1 : g2 ? L = 0.85 : L = this.isNonDynamic ? 0.2 : 1;
        let x2 = 0;
        this.enableBlur && (p2 ? x2 = 0 : (x2 = 1, f2 < this.scrollToIndex ? x2 += Math.abs(this.scrollToIndex - f2) + 1 : x2 += Math.abs(
          f2 - Math.max(this.scrollToIndex, l2)
        )));
        const M2 = this.enableScale ? 97 : 100;
        let b2 = 100;
        !p2 && this.isPlaying && (y2.isBG ? b2 = 75 : b2 = M2), u2.setTransform(
          s2,
          b2,
          L,
          window.innerWidth <= 1024 ? x2 * 0.8 : x2,
          false,
          c2
        ), y2.isBG && (p2 || !this.isPlaying) ? s2 += this.lyricLinesSize.get(u2)?.[1] ?? r2 : y2.isBG || (s2 += this.lyricLinesSize.get(u2)?.[1] ?? r2), s2 >= 0 && !this.isSeeking && (y2.isBG || (c2 += h2), f2 >= this.scrollToIndex && (h2 /= 1.05));
      }), this.scrollBoundary[1] = s2 + this.scrollOffset - this.size[1] / 2, this.bottomLine.setTransform(0, s2, false, c2);
    }
    /**
     * 设置所有歌词行在横坐标上的弹簧属性，包括重量、弹力和阻力。
     *
     * @param params 需要设置的弹簧属性，提供的属性将会覆盖原来的属性，未提供的属性将会保持原样
     * @deprecated 考虑到横向弹簧效果并不常见，所以这个函数将会在未来的版本中移除
     */
    setLinePosXSpringParams(t2 = {}) {
    }
    /**
     * 设置所有歌词行在​纵坐标上的弹簧属性，包括重量、弹力和阻力。
     *
     * @param params 需要设置的弹簧属性，提供的属性将会覆盖原来的属性，未提供的属性将会保持原样
     */
    setLinePosYSpringParams(t2 = {}) {
      this.posYSpringParams = {
        ...this.posYSpringParams,
        ...t2
      }, this.bottomLine.lineTransforms.posY.updateParams(this.posYSpringParams);
      for (const e2 of this.currentLyricLineObjects)
        e2.lineTransforms.posY.updateParams(this.posYSpringParams);
    }
    /**
     * 设置所有歌词行在​缩放大小上的弹簧属性，包括重量、弹力和阻力。
     *
     * @param params 需要设置的弹簧属性，提供的属性将会覆盖原来的属性，未提供的属性将会保持原样
     */
    setLineScaleSpringParams(t2 = {}) {
      this.scaleSpringParams = {
        ...this.scaleSpringParams,
        ...t2
      }, this.scaleForBGSpringParams = {
        ...this.scaleForBGSpringParams,
        ...t2
      };
      for (const e2 of this.currentLyricLineObjects)
        e2.getLine().isBG ? e2.lineTransforms.scale.updateParams(this.scaleForBGSpringParams) : e2.lineTransforms.scale.updateParams(this.scaleSpringParams);
    }
    isPlaying = true;
    /**
     * 暂停部分效果演出，目前会暂停播放间奏点的动画，且将背景歌词显示出来
     */
    pause() {
      this.interludeDots.pause(), this.isPlaying && (this.isPlaying = false, this.calcLayout());
    }
    /**
     * 恢复部分效果演出，目前会恢复播放间奏点的动画
     */
    resume() {
      this.interludeDots.resume(), this.isPlaying || (this.isPlaying = true, this.calcLayout());
    }
    /**
     * 更新动画，这个函数应该被逐帧调用或者在以下情况下调用一次：
     *
     * 1. 刚刚调用完设置歌词函数的时候
     * @param delta 距离上一次被调用到现在的时长，单位为毫秒（可为浮点数）
     */
    update(t2 = 0) {
      this.bottomLine.update(t2 / 1e3), this.interludeDots.update(t2 / 1e3);
    }
    onResize() {
    }
    /**
     * 获取一个特殊的底栏元素，默认是空白的，可以往内部添加任意元素
     *
     * 这个元素始终在歌词的底部，可以用于显示歌曲创作者等信息
     *
     * 但是请勿删除该元素，只能在内部存放元素
     *
     * @returns 一个元素，可以往内部添加任意元素
     */
    getBottomLineElement() {
      return this.bottomLine.getElement();
    }
    /**
     * 重置用户滚动状态
     *
     * 请在用户完成滚动点击跳转歌词时调用本事件再调用 `calcLayout` 以正确滚动到目标位置
     */
    resetScroll() {
      this.isScrolled = false, this.scrollOffset = 0, clearTimeout(this.scrolledHandler), this.scrolledHandler = 0;
    }
    /**
     * 获取当前歌词数组
     *
     * 一般和最后调用 `setLyricLines` 给予的参数一样
     * @returns 当前歌词数组
     */
    getLyricLines() {
      return this.currentLyricLines;
    }
    /**
     * 获取当前歌词的播放位置
     *
     * 一般和最后调用 `setCurrentTime` 给予的参数一样
     * @returns 当前播放位置
     */
    getCurrentTime() {
      return this.currentTime;
    }
    getElement() {
      return this.element;
    }
    dispose() {
      this.element.remove(), window.removeEventListener("pageshow", this.onPageShow), window.removeEventListener("pagehide", this.onPageHide);
    }
  }
  class q extends EventTarget {
    top = 0;
    scale = 1;
    blur = 0;
    opacity = 1;
    delay = 0;
    lineTransforms = {
      posY: new Tt(0),
      scale: new Tt(100)
    };
    onLineSizeChange(t2) {
    }
    setTransform(t2 = this.top, e2 = this.scale, s2 = this.opacity, i2 = this.blur, n2 = false, r2 = 0) {
      this.top = t2, this.scale = e2, this.opacity = s2, this.blur = i2, this.delay = r2;
    }
    /**
     * 判定歌词是否可以应用强调辉光效果
     *
     * 果子在对辉光效果的解释是一种强调（emphasized）效果
     *
     * 条件是一个单词时长大于等于 1s 且长度小于等于 7
     *
     * @param word 单词
     * @returns 是否可以应用强调辉光效果
     */
    static shouldEmphasize(t2) {
      return os(t2.word) ? t2.endTime - t2.startTime >= 1e3 : t2.endTime - t2.startTime >= 1e3 && t2.word.trim().length <= 7 && t2.word.trim().length > 1;
    }
    dispose() {
    }
  }
  function us(d2) {
    return d2 && d2.__esModule && Object.prototype.hasOwnProperty.call(d2, "default") ? d2.default : d2;
  }
  var Pt, Jt;
  function gs() {
    if (Jt) return Pt;
    Jt = 1;
    var d2 = 4, t2 = 1e-3, e2 = 1e-7, s2 = 10, i2 = 11, n2 = 1 / (i2 - 1), r2 = typeof Float32Array == "function";
    function a2(g2, p2) {
      return 1 - 3 * p2 + 3 * g2;
    }
    function o2(g2, p2) {
      return 3 * p2 - 6 * g2;
    }
    function l2(g2) {
      return 3 * g2;
    }
    function c2(g2, p2, y2) {
      return ((a2(p2, y2) * g2 + o2(p2, y2)) * g2 + l2(p2)) * g2;
    }
    function h2(g2, p2, y2) {
      return 3 * a2(p2, y2) * g2 * g2 + 2 * o2(p2, y2) * g2 + l2(p2);
    }
    function m2(g2, p2, y2, L, x2) {
      var M2, b2, w2 = 0;
      do
        b2 = p2 + (y2 - p2) / 2, M2 = c2(b2, L, x2) - g2, M2 > 0 ? y2 = b2 : p2 = b2;
      while (Math.abs(M2) > e2 && ++w2 < s2);
      return b2;
    }
    function u2(g2, p2, y2, L) {
      for (var x2 = 0; x2 < d2; ++x2) {
        var M2 = h2(p2, y2, L);
        if (M2 === 0)
          return p2;
        var b2 = c2(p2, y2, L) - g2;
        p2 -= b2 / M2;
      }
      return p2;
    }
    function f2(g2) {
      return g2;
    }
    return Pt = function(p2, y2, L, x2) {
      if (!(0 <= p2 && p2 <= 1 && 0 <= L && L <= 1))
        throw new Error("bezier x values must be in [0, 1] range");
      if (p2 === y2 && L === x2)
        return f2;
      for (var M2 = r2 ? new Float32Array(i2) : new Array(i2), b2 = 0; b2 < i2; ++b2)
        M2[b2] = c2(b2 * n2, p2, L);
      function w2(T) {
        for (var v2 = 0, P = 1, z = i2 - 1; P !== z && M2[P] <= T; ++P)
          v2 += n2;
        --P;
        var E = (T - M2[P]) / (M2[P + 1] - M2[P]), F = v2 + E * n2, _ = h2(F, p2, L);
        return _ >= t2 ? u2(T, F, p2, L) : _ === 0 ? F : m2(T, v2, v2 + n2, p2, L);
      }
      return function(v2) {
        return v2 === 0 ? 0 : v2 === 1 ? 1 : c2(w2(v2), y2, x2);
      };
    }, Pt;
  }
  var ys = gs();
  const St = /* @__PURE__ */ us(ys), Ls = /^[\p{Unified_Ideograph}\u0800-\u9FFC]+$/u;
  function Ot(d2) {
    const t2 = [];
    for (const n2 of d2) {
      const r2 = n2.word.replace(/\s/g, "").length, a2 = n2.word.split(" ").filter((o2) => o2.trim().length > 0);
      if (a2.length > 1) {
        n2.word.startsWith(" ") && t2.push({
          word: " ",
          romanWord: "",
          startTime: 0,
          endTime: 0,
          obscene: false
        });
        let o2 = 0;
        for (const l2 of a2) {
          const c2 = {
            word: l2,
            romanWord: "",
            obscene: n2.obscene,
            startTime: n2.startTime + o2 / r2 * (n2.endTime - n2.startTime),
            endTime: n2.startTime + (o2 + l2.length) / r2 * (n2.endTime - n2.startTime)
          };
          t2.push(c2), t2.push({
            word: " ",
            romanWord: "",
            startTime: 0,
            endTime: 0,
            obscene: false
          }), o2 += l2.length;
        }
        n2.word.endsWith(" ") || t2.pop();
      } else
        t2.push({
          ...n2
        });
    }
    let e2 = [], s2 = [];
    const i2 = [];
    for (const n2 of t2) {
      const r2 = n2.word;
      e2.push(r2), s2.push(n2), r2.length > 0 && r2.trim().length === 0 ? (e2.pop(), s2.pop(), s2.length === 1 ? i2.push(s2[0]) : s2.length > 1 && i2.push(s2), i2.push(n2), e2 = [], s2 = []) : (!/^\s*[^\s]*\s*$/.test(e2.join("")) || Ls.test(r2)) && (e2.pop(), s2.pop(), s2.length === 1 ? i2.push(s2[0]) : s2.length > 1 && i2.push(s2), e2 = [r2], s2 = [n2]);
    }
    return s2.length === 1 ? i2.push(s2[0]) : i2.push(s2), i2;
  }
  function le() {
    return [
      1,
      0,
      0,
      0,
      0,
      1,
      0,
      0,
      0,
      0,
      1,
      0,
      0,
      0,
      0,
      1
    ];
  }
  function he(d2, t2 = 1, e2 = { x: 0, y: 0 }) {
    const [s2, i2] = [e2.x, e2.y];
    return [
      d2[0] * t2,
      d2[1] * t2,
      d2[2] * t2,
      d2[3],
      d2[4] * t2,
      d2[5] * t2,
      d2[6] * t2,
      d2[7],
      d2[8] * t2,
      d2[9] * t2,
      d2[10] * t2,
      d2[11],
      d2[12] - s2 * t2 + s2,
      d2[13] - i2 * t2 + i2,
      d2[14],
      d2[15]
    ];
  }
  function de(d2, t2 = 4) {
    const e2 = (s2, i2) => s2.toFixed(t2);
    return `matrix3d(${d2.map(e2).join(", ")})`;
  }
  const Mt = 32, me = (d2, t2) => (e2) => Math.min(1, Math.max(0, (e2 - d2) / (t2 - d2))), Wt = 0.5, Ms = me(0, Wt), bs = me(Wt, 1), xs = St(0.2, 0.4, 0.58, 1), Ts = St(0.3, 0, 0.58, 1), ws = (d2) => (t2) => t2 < d2 ? xs(Ms(t2)) : 1 - Ts(bs(t2));
  function Qt(d2, t2 = 0, e2 = "rgba(0,0,0,var(--bright-mask-alpha, 1.0))", s2 = "rgba(0,0,0,var(--dark-mask-alpha, 1.0))") {
    const i2 = 2 + d2 + t2, n2 = d2 / i2, r2 = (1 - n2) / 2;
    return [
      `linear-gradient(to right,${e2} ${r2 * 100}%,${s2} ${(r2 + n2) * 100}%)`,
      i2
    ];
  }
  let Ss = class extends MouseEvent {
    constructor(t2, e2) {
      super(e2.type, e2), this.line = t2;
    }
  };
  function Es(d2) {
    const t2 = d2.match(/matrix\(([^)]+)\)/);
    if (t2) {
      const e2 = t2[1].split(", "), s2 = Number.parseFloat(e2[0]), i2 = Number.parseFloat(e2[3]);
      return (s2 + i2) / 2;
    }
    return 1;
  }
  let vs = class extends q {
    constructor(t2, e2 = {
      words: [],
      translatedLyric: "",
      romanLyric: "",
      startTime: 0,
      endTime: 0,
      isBG: false,
      isDuet: false
    }) {
      super(), this.lyricPlayer = t2, this.lyricLine = e2, this._prevParentEl = t2.getElement(), t2.resizeObserver.observe(this.element), this.element.setAttribute("class", W.lyricLine), this.lyricLine.isBG && this.element.classList.add(W.lyricBgLine), this.lyricLine.isDuet && this.element.classList.add(W.lyricDuetLine), this.lineTransforms.posY.setPosition(window.innerHeight * 2), this.element.appendChild(document.createElement("div")), this.element.appendChild(document.createElement("div")), this.element.appendChild(document.createElement("div"));
      const s2 = this.element.children[0], i2 = this.element.children[1], n2 = this.element.children[2];
      s2.setAttribute("class", W.lyricMainLine), i2.setAttribute("class", W.lyricSubLine), n2.setAttribute("class", W.lyricSubLine), this.rebuildStyle();
    }
    element = document.createElement("div");
    splittedWords = [];
    // 标记是否已经构建了行内的实际 DOM（单词与动画等）
    built = false;
    // 由 LyricPlayer 来设置
    lineSize = [0, 0];
    listenersMap = /* @__PURE__ */ new Map();
    onMouseEvent = (t2) => {
      const e2 = new Ss(this, t2);
      for (const s2 of this.listenersMap.get(t2.type) ?? [])
        s2.call(this, e2);
      if (!this.dispatchEvent(e2) || e2.defaultPrevented)
        return t2.preventDefault(), t2.stopPropagation(), t2.stopImmediatePropagation(), false;
    };
    addMouseEventListener(t2, e2, s2) {
      if (e2) {
        const i2 = this.listenersMap.get(t2) ?? /* @__PURE__ */ new Set();
        i2.size === 0 && this.element.addEventListener(t2, this.onMouseEvent, s2), i2.add(e2), this.listenersMap.set(t2, i2);
      }
    }
    removeMouseEventListener(t2, e2, s2) {
      if (e2) {
        const i2 = this.listenersMap.get(t2);
        i2 && (i2.delete(e2), i2.size === 0 && this.element.removeEventListener(t2, this.onMouseEvent, s2));
      }
    }
    areWordsOnSameLine(t2, e2) {
      if (t2?.mainElement && e2?.mainElement) {
        const s2 = t2.mainElement, i2 = e2.mainElement, n2 = s2.getBoundingClientRect(), r2 = i2.getBoundingClientRect();
        return Math.abs(n2.top - r2.top) < 10;
      }
      return true;
    }
    isEnabled = false;
    async enable(t2 = this.lyricLine.startTime) {
      this.isEnabled = true, this.element.classList.add(W.active);
      const e2 = this.element.children[0];
      for (const s2 of this.splittedWords) {
        for (const i2 of s2.elementAnimations)
          i2.currentTime = 0, i2.playbackRate = 1, i2.play();
        for (const i2 of s2.maskAnimations)
          i2.currentTime = Math.min(
            this.totalDuration,
            Math.max(0, t2 - this.lyricLine.startTime)
          ), i2.playbackRate = 1, i2.play();
      }
      e2.classList.add(W.active);
    }
    disable() {
      this.isEnabled = false, this.element.classList.remove(W.active);
      const t2 = this.element.children[0];
      for (const e2 of this.splittedWords)
        for (const s2 of e2.elementAnimations)
          (s2.id === "float-word" || s2.id.includes("emphasize-word-float-only")) && (s2.playbackRate = -1, s2.play());
      t2.classList.remove(W.active);
    }
    lastWord;
    async resume() {
      if (this.isEnabled)
        for (const t2 of this.splittedWords) {
          for (const e2 of t2.elementAnimations)
            (!this.lastWord || this.splittedWords.indexOf(this.lastWord) < this.splittedWords.indexOf(t2)) && e2.play();
          for (const e2 of t2.maskAnimations)
            (!this.lastWord || this.splittedWords.indexOf(this.lastWord) < this.splittedWords.indexOf(t2)) && e2.play();
        }
    }
    async pause() {
      if (this.isEnabled)
        for (const t2 of this.splittedWords) {
          for (const e2 of t2.elementAnimations)
            e2.pause();
          for (const e2 of t2.maskAnimations)
            e2.pause();
        }
    }
    setMaskAnimationState(t2 = 0) {
      const e2 = t2 - this.lyricLine.startTime;
      for (const s2 of this.splittedWords)
        for (const i2 of s2.maskAnimations)
          i2.currentTime = Math.min(this.totalDuration, Math.max(0, e2)), i2.playbackRate = 1, e2 >= 0 && e2 < this.totalDuration ? i2.play() : i2.pause();
    }
    getLine() {
      return this.lyricLine;
    }
    _hide = true;
    _prevParentEl;
    lastStyle = "";
    show() {
      this._hide = false, this.element.parentElement || (this._prevParentEl.appendChild(this.element), this.lyricPlayer.resizeObserver.observe(this.element)), this.built || (this.rebuildElement(), this.built = true, this.updateMaskImageSync()), this.rebuildStyle();
    }
    hide() {
      this._hide = true, this.element.parentElement && (this._prevParentEl.removeChild(this.element), this.lyricPlayer.resizeObserver.unobserve(this.element)), this.built && (this.disposeElements(), this.built = false);
    }
    rebuildStyle() {
      let t2 = "";
      t2 += `transform:translateY(${this.lineTransforms.posY.getCurrentPosition().toFixed(
        1
      )}px) scale(${(this.lineTransforms.scale.getCurrentPosition() / 100).toFixed(4)});`, !this.lyricPlayer.getEnableSpring() && this.isInSight && (t2 += `transition-delay:${this.delay}ms;`), t2 += `filter:blur(${Math.min(32, this.blur)}px);`, t2 !== this.lastStyle && (this.lastStyle = t2, this.element.setAttribute("style", t2));
    }
    rebuildElement() {
      this.disposeElements();
      const t2 = this.element.children[0], e2 = this.element.children[1], s2 = this.element.children[2];
      if (this.lyricPlayer._getIsNonDynamic()) {
        t2.innerText = this.lyricLine.words.map((n2) => n2.word).join(""), this.setSubLinesText(e2, s2);
        return;
      }
      const i2 = Ot(this.lyricLine.words);
      t2.innerHTML = "";
      for (const n2 of i2)
        if (Array.isArray(n2)) {
          if (n2.length === 0) continue;
          this.buildChunkGroup(n2, t2);
        } else n2.word.trim().length === 0 ? t2.appendChild(document.createTextNode(" ")) : this.buildSingleWord(n2, t2);
      this.setSubLinesText(e2, s2);
    }
    /** 设置翻译与音译行文本 */
    setSubLinesText(t2, e2) {
      t2.innerText = this.lyricLine.translatedLyric, e2.innerText = this.lyricLine.romanLyric;
    }
    /** 处理一组连写（无空格）单词，包含强调效果 */
    buildChunkGroup(t2, e2) {
      const s2 = t2.reduce(
        (a2, o2) => (a2.endTime = Math.max(a2.endTime, o2.endTime), a2.startTime = Math.min(a2.startTime, o2.startTime), a2.word += o2.word, a2),
        {
          word: "",
          romanWord: "",
          startTime: Number.POSITIVE_INFINITY,
          endTime: Number.NEGATIVE_INFINITY,
          wordType: "normal",
          obscene: false
        }
      ), i2 = t2.map((a2) => q.shouldEmphasize(a2)).reduce((a2, o2) => a2 || o2, q.shouldEmphasize(s2)), n2 = document.createElement("span");
      n2.classList.add(W.emphasizeWrapper);
      const r2 = [];
      for (const a2 of t2) {
        const o2 = document.createElement("span");
        if (i2) {
          o2.classList.add(W.emphasize);
          const l2 = [];
          for (const h2 of a2.word.trim()) {
            const m2 = document.createElement("span");
            m2.innerText = h2, l2.push(m2), r2.push(m2), o2.appendChild(m2);
          }
          const c2 = {
            ...a2,
            mainElement: o2,
            subElements: l2,
            elementAnimations: [this.initFloatAnimation(a2, o2)],
            maskAnimations: [],
            width: 0,
            height: 0,
            padding: 0,
            shouldEmphasize: i2
          };
          this.splittedWords.push(c2);
        } else {
          if (a2.romanWord && a2.romanWord.trim().length > 0) {
            const l2 = document.createElement("div"), c2 = document.createElement("div");
            l2.innerText = a2.word, c2.innerText = a2.romanWord, c2.classList.add(W.romanWord), o2.appendChild(l2), o2.appendChild(c2);
          } else
            o2.innerText = a2.word;
          this.splittedWords.push({
            ...a2,
            mainElement: o2,
            subElements: [],
            elementAnimations: [this.initFloatAnimation(a2, o2)],
            maskAnimations: [],
            width: 0,
            height: 0,
            padding: 0,
            shouldEmphasize: i2
          });
        }
        n2.appendChild(o2);
      }
      i2 && this.splittedWords[this.splittedWords.length - 1].elementAnimations.push(
        ...this.initEmphasizeAnimation(
          s2,
          r2,
          s2.endTime - s2.startTime,
          s2.startTime - this.lyricLine.startTime
        )
      ), s2.word.trimStart() !== s2.word && e2.appendChild(document.createTextNode(" ")), e2.appendChild(n2), s2.word.trimEnd() !== s2.word && q.shouldEmphasize(s2) && e2.appendChild(document.createTextNode(" "));
    }
    /** 渲染单个词（含强调与音译处理） */
    buildSingleWord(t2, e2) {
      const s2 = q.shouldEmphasize(t2), i2 = document.createElement("span"), n2 = {
        ...t2,
        mainElement: i2,
        subElements: [],
        elementAnimations: [this.initFloatAnimation(t2, i2)],
        maskAnimations: [],
        width: 0,
        height: 0,
        padding: 0,
        shouldEmphasize: s2
      };
      if (s2) {
        i2.classList.add(W.emphasize);
        const r2 = [];
        for (const o2 of t2.word.trim()) {
          const l2 = document.createElement("span");
          l2.innerText = o2, r2.push(l2), i2.appendChild(l2);
        }
        if (t2.romanWord && t2.romanWord.trim().length > 0) {
          const o2 = document.createElement("div");
          o2.innerText = t2.romanWord, o2.classList.add(W.romanWord), i2.appendChild(o2);
        }
        n2.subElements = r2;
        const a2 = Math.abs(n2.endTime - n2.startTime);
        n2.elementAnimations.push(
          ...this.initEmphasizeAnimation(
            t2,
            r2,
            a2,
            n2.startTime - this.lyricLine.startTime
          )
        );
      } else if (t2.romanWord && t2.romanWord.trim().length > 0) {
        const r2 = document.createElement("div"), a2 = document.createElement("div");
        r2.innerText = t2.word, a2.innerText = t2.romanWord, a2.classList.add(W.romanWord), i2.appendChild(r2), i2.appendChild(a2);
      } else
        i2.innerText = t2.word.trim();
      t2.word.trimStart() !== t2.word && e2.appendChild(document.createTextNode(" ")), e2.appendChild(i2), t2.word.trimEnd() !== t2.word && e2.appendChild(document.createTextNode(" ")), this.splittedWords.push(n2);
    }
    initFloatAnimation(t2, e2) {
      const s2 = t2.startTime - this.lyricLine.startTime, i2 = Math.max(1e3, t2.endTime - t2.startTime);
      let n2 = 0.05;
      this.lyricLine.isBG && (n2 *= 2);
      const r2 = e2.animate(
        [
          {
            transform: "translateY(0px)"
          },
          {
            transform: `translateY(${-n2}em)`
          }
        ],
        {
          duration: Number.isFinite(i2) ? i2 : 0,
          delay: Number.isFinite(s2) ? s2 : 0,
          id: "float-word",
          composite: "add",
          fill: "both",
          easing: "ease-out"
        }
      );
      return r2.pause(), r2;
    }
    // 按照原 Apple Music 参考，强调效果只应用缩放、轻微左右位移和辉光效果，原主要的悬浮位移效果不变
    // 为了避免产生锯齿抖动感，使用 matrix3d 来实现缩放和位移
    initEmphasizeAnimation(t2, e2, s2, i2) {
      const n2 = Math.max(0, i2);
      let r2 = Math.max(1e3, s2), a2 = [], o2 = r2 / 2e3;
      o2 = o2 > 1 ? Math.sqrt(o2) : o2 ** 3;
      let l2 = r2 / 3e3;
      l2 = l2 > 1 ? Math.sqrt(l2) : l2 ** 3, o2 *= 0.6, l2 *= 0.5, this.lyricLine.words.length > 0 && t2.word.includes(
        this.lyricLine.words[this.lyricLine.words.length - 1].word
      ) && (o2 *= 1.6, l2 *= 1.5, r2 *= 1.2), o2 = Math.min(1.2, o2), l2 = Math.min(0.8, l2);
      const c2 = Number.isFinite(r2) ? r2 : 0, h2 = ws(Wt);
      return a2 = e2.flatMap((m2, u2, f2) => {
        const g2 = n2 + r2 / 2.5 / f2.length * u2, p2 = [], y2 = new Array(Mt).fill(0).map((b2, w2) => {
          const T = (w2 + 1) / Mt, v2 = h2(T), P = h2(T) * l2, z = he(le(), 1 + v2 * 0.1 * o2), E = -v2 * 0.03 * o2 * (f2.length / 2 - u2), F = -v2 * 0.025 * o2;
          return {
            offset: T,
            transform: `${de(
              z,
              4
            )} translate(${E}em, ${F}em)`,
            textShadow: `0 0 ${Math.min(
              0.3,
              l2 * 0.3
            )}em rgba(255, 255, 255, ${P})`
          };
        }), L = m2.animate(y2, {
          duration: c2,
          delay: Number.isFinite(g2) ? g2 : 0,
          id: `emphasize-word-${m2.innerText}-${u2}`,
          iterations: 1,
          composite: "replace",
          fill: "both"
        });
        L.onfinish = () => {
          L.pause();
        }, L.pause(), p2.push(L);
        const x2 = new Array(Mt).fill(0).map((b2, w2) => {
          const T = (w2 + 1) / Mt;
          let v2 = Math.sin(T * Math.PI);
          return this.lyricLine.isBG && (v2 *= 2), {
            offset: T,
            transform: `translateY(${-v2 * 0.05}em)`
          };
        }), M2 = m2.animate(x2, {
          duration: c2 * 1.4,
          delay: Number.isFinite(g2) ? g2 - 400 : 0,
          id: "emphasize-word-float",
          iterations: 1,
          composite: "add",
          fill: "both"
        });
        return M2.onfinish = () => {
          M2.pause();
        }, M2.pause(), p2.push(M2), p2;
      }), a2;
    }
    get totalDuration() {
      return this.lyricLine.endTime - this.lyricLine.startTime;
    }
    onLineSizeChange(t2) {
      this.updateMaskImageSync();
    }
    updateMaskImageSync() {
      for (const t2 of this.splittedWords) {
        const e2 = t2.mainElement;
        e2 ? (t2.padding = Number.parseFloat(getComputedStyle(e2).paddingLeft), t2.width = e2.clientWidth - t2.padding * 2, t2.height = e2.clientHeight - t2.padding * 2) : (t2.width = 0, t2.height = 0, t2.padding = 0);
      }
      this.lyricPlayer.supportMaskImage ? this.generateWebAnimationBasedMaskImage() : this.generateCalcBasedMaskImage(), this.isEnabled && this.enable(this.lyricPlayer.getCurrentTime());
    }
    generateCalcBasedMaskImage() {
      for (const t2 of this.splittedWords) {
        const e2 = t2.mainElement;
        if (e2) {
          t2.width = e2.clientWidth, t2.height = e2.clientHeight;
          const s2 = t2.height * this.lyricPlayer.getWordFadeWidth(), [i2, n2] = Qt(
            s2 / t2.width
          ), r2 = `${n2 * 100}% 100%`;
          this.lyricPlayer.supportMaskImage ? (e2.style.maskImage = i2, e2.style.maskRepeat = "no-repeat", e2.style.maskOrigin = "left", e2.style.maskSize = r2) : (e2.style.webkitMaskImage = i2, e2.style.webkitMaskRepeat = "no-repeat", e2.style.webkitMaskOrigin = "left", e2.style.webkitMaskSize = r2);
          const a2 = t2.width + s2, o2 = `clamp(${-a2}px,calc(${-a2}px + (var(--amll-player-time) - ${t2.startTime})*${a2 / Math.abs(t2.endTime - t2.startTime)}px),0px) 0px, left top`;
          e2.style.maskPosition = o2, e2.style.webkitMaskPosition = o2;
        }
      }
    }
    generateWebAnimationBasedMaskImage() {
      const t2 = Math.max(
        this.splittedWords.reduce((e2, s2) => Math.max(s2.endTime, e2), 0),
        this.lyricLine.endTime
      ) - this.lyricLine.startTime;
      this.splittedWords.forEach((e2, s2) => {
        const i2 = e2.mainElement;
        if (i2) {
          const n2 = e2.height * this.lyricPlayer.getWordFadeWidth(), [r2, a2] = Qt(
            n2 / (e2.width + e2.padding * 2)
          ), o2 = `${a2 * 100}% 100%`;
          this.lyricPlayer.supportMaskImage ? (i2.style.maskImage = r2, i2.style.maskRepeat = "no-repeat", i2.style.maskOrigin = "left", i2.style.maskSize = o2) : (i2.style.webkitMaskImage = r2, i2.style.webkitMaskRepeat = "no-repeat", i2.style.webkitMaskOrigin = "left", i2.style.webkitMaskSize = o2);
          const l2 = this.splittedWords.slice(0, s2).reduce((x2, M2) => x2 + M2.width, 0) + (this.splittedWords[0] ? n2 : 0), c2 = -(e2.width + e2.padding * 2 + n2), h2 = (x2) => Math.max(c2, Math.min(0, x2));
          let m2 = -l2 - e2.width - e2.padding - n2, u2 = 0;
          const f2 = [];
          let g2 = m2, p2 = 0;
          const y2 = () => {
            const x2 = m2 - g2, M2 = Math.max(0, Math.min(1, u2)), b2 = M2 - p2, w2 = Math.abs(b2 / x2);
            if (m2 > c2 && g2 < c2) {
              const P = Math.abs(g2 - c2) * w2, z = `${h2(g2)}px 0`, E = {
                offset: p2 + P,
                maskPosition: z
              };
              f2.push(E);
            }
            if (m2 > 0 && g2 < 0) {
              const P = Math.abs(g2) * w2, z = `${h2(m2)}px 0`, E = {
                offset: p2 + P,
                maskPosition: z
              };
              f2.push(E);
            }
            const T = `${h2(m2)}px 0`, v2 = {
              offset: M2,
              maskPosition: T
            };
            f2.push(v2), g2 = m2, p2 = M2;
          };
          y2();
          let L = 0;
          this.splittedWords.forEach((x2, M2) => {
            {
              const b2 = x2.startTime - this.lyricLine.startTime, w2 = b2 - L;
              u2 += w2 / t2, w2 > 0 && y2(), L = b2;
            }
            {
              const b2 = x2.endTime - x2.startTime;
              u2 += b2 / t2, m2 += x2.width, M2 === 0 && (m2 += n2 * 1.5), M2 === this.splittedWords.length - 1 && (m2 += n2 * 0.5), b2 > 0 && y2(), L += b2;
            }
          });
          for (const x2 of e2.maskAnimations)
            x2.cancel();
          try {
            const x2 = i2.animate(f2, {
              duration: t2 || 1,
              id: `fade-word-${e2.word}-${s2}`,
              fill: "both"
            });
            x2.pause(), e2.maskAnimations = [x2];
          } catch (x2) {
            console.warn("应用渐变动画发生错误", f2, t2, x2);
          }
        }
      });
    }
    getElement() {
      return this.element;
    }
    setTransform(t2 = this.top, e2 = this.scale, s2 = 1, i2 = 0, n2 = false, r2 = 0) {
      super.setTransform(t2, e2, s2, i2, n2, r2);
      const a2 = this.isInSight, o2 = this.lyricPlayer.getEnableSpring();
      this.top = t2, this.scale = e2, this.delay = r2 * 1e3 | 0;
      const l2 = this.element.children[0];
      if (l2.style.opacity = `${s2}`, n2 || !o2)
        if (this.blur = Math.min(32, i2), this.lineTransforms.posY.setPosition(t2), this.lineTransforms.scale.setPosition(e2), o2)
          this.rebuildStyle();
        else {
          const c2 = this.isInSight;
          a2 || c2 ? this.show() : this.hide();
        }
      else if (this.lineTransforms.posY.setTargetPosition(t2, r2), this.lineTransforms.scale.setTargetPosition(e2), this.blur !== Math.min(32, i2)) {
        this.blur = Math.min(32, i2);
        const c2 = i2.toFixed(3);
        this.element.style.filter = `blur(${c2}px)`;
      }
    }
    update(t2 = 0) {
      if (this.lyricPlayer.getEnableSpring())
        if (this.lineTransforms.posY.update(t2), this.lineTransforms.scale.update(t2), this.isInSight ? this.show() : this.hide(), this.lyricPlayer.getEnableSpring())
          this.element.style.setProperty(
            "--bright-mask-alpha",
            `${Math.max(
              0,
              Math.min(
                1,
                this.lineTransforms.scale.getCurrentPosition() / 100 - 0.97
              ) / 0.03
            ) * 0.8 + 0.2}`
          ), this.element.style.setProperty(
            "--dark-mask-alpha",
            `${Math.max(
              0,
              Math.min(
                1,
                this.lineTransforms.scale.getCurrentPosition() / 100 - 0.97
              ) / 0.03
            ) * 0.2 + 0.2}`
          );
        else {
          const s2 = window.getComputedStyle(this.element).transform, i2 = Es(s2);
          this.element.style.setProperty(
            "--bright-mask-alpha",
            `${Math.max(0, Math.min(1, (i2 - 0.97) / 0.03)) * 0.8 + 0.2}`
          ), this.element.style.setProperty(
            "--dark-mask-alpha",
            `${Math.max(0, Math.min(1, (i2 - 0.97) / 0.03)) * 0.2 + 0.2}`
          );
        }
    }
    _getDebugTargetPos() {
      return `[位移: ${this.top}; 缩放: ${this.scale}; 延时: ${this.delay}]`;
    }
    get isInSight() {
      const t2 = this.lineTransforms.posY.getCurrentPosition(), e2 = this.lyricPlayer.lyricLinesSize.get(this)?.[1] ?? 0, s2 = t2 + e2, i2 = this.lyricPlayer.size[1], n2 = this.lyricPlayer.getOverscanPx();
      return !(t2 > i2 + e2 + n2 || s2 < -e2 - n2);
    }
    disposeElements() {
      for (const i2 of this.splittedWords) {
        for (const n2 of i2.elementAnimations)
          n2.cancel();
        for (const n2 of i2.maskAnimations)
          n2.cancel();
        for (const n2 of i2.subElements)
          n2.remove(), n2.parentNode?.removeChild(n2);
        i2.elementAnimations = [], i2.maskAnimations = [], i2.subElements = [], i2.mainElement?.parentNode && i2.mainElement.parentNode.removeChild(i2.mainElement);
      }
      this.splittedWords = [];
      const t2 = this.element.children[0], e2 = this.element.children[1], s2 = this.element.children[2];
      t2 && (t2.innerHTML = ""), e2 && (e2.innerHTML = ""), s2 && (s2.innerHTML = "");
    }
    dispose() {
      this.disposeElements(), this.lyricPlayer.resizeObserver.unobserve(this.element), this.element.remove();
    }
  };
  class pe extends MouseEvent {
    constructor(t2, e2, s2) {
      super(`line-${s2.type}`, s2), this.lineIndex = t2, this.line = e2;
    }
  }
  class ai extends Rt {
    currentLyricLineObjects = [];
    onResize() {
      const t2 = getComputedStyle(this.element);
      this._baseFontSize = Number.parseFloat(t2.fontSize), this.rebuildStyle();
    }
    supportPlusLighter = CSS.supports("mix-blend-mode", "plus-lighter");
    supportMaskImage = CSS.supports("mask-image", "none");
    innerSize = [0, 0];
    onLineClickedHandler = (t2) => {
      const e2 = new pe(
        this.lyricLinesIndexes.get(t2.line) ?? -1,
        t2.line,
        t2
      );
      this.dispatchEvent(e2) || (t2.preventDefault(), t2.stopPropagation(), t2.stopImmediatePropagation());
    };
    /**
     * 是否为非逐词歌词
     * @internal
     */
    _getIsNonDynamic() {
      return this.isNonDynamic;
    }
    _baseFontSize = Number.parseFloat(
      getComputedStyle(this.element).fontSize
    );
    get baseFontSize() {
      return this._baseFontSize;
    }
    constructor() {
      super(), this.onResize(), this.element.classList.add("amll-lyric-player", "dom"), this.disableSpring && this.element.classList.add(W.disableSpring);
    }
    rebuildStyle() {
    }
    setWordFadeWidth(t2 = 0.5) {
      super.setWordFadeWidth(t2);
      for (const e2 of this.currentLyricLineObjects)
        e2.updateMaskImageSync();
    }
    /**
     * 设置当前播放歌词，要注意传入后这个数组内的信息不得修改，否则会发生错误
     * @param lines 歌词数组
     * @param initialTime 初始时间，默认为 0
     */
    setLyricLines(t2, e2 = 0) {
      super.setLyricLines(t2, e2), this.hasDuetLine ? this.element.classList.add(W.hasDuetLine) : this.element.classList.remove(W.hasDuetLine), this.supportMaskImage || this.element.style.setProperty("--amll-player-time", `${e2}`);
      for (const s2 of this.currentLyricLineObjects)
        s2.removeMouseEventListener("click", this.onLineClickedHandler), s2.removeMouseEventListener("contextmenu", this.onLineClickedHandler), s2.dispose();
      this.currentLyricLineObjects = this.processedLines.map((s2, i2) => {
        const n2 = new vs(this, s2);
        return n2.addMouseEventListener("click", this.onLineClickedHandler), n2.addMouseEventListener("contextmenu", this.onLineClickedHandler), this.lyricLinesIndexes.set(n2, i2), this.lyricLineElementMap.set(n2.getElement(), n2), n2;
      }), this.setLinePosXSpringParams({}), this.setLinePosYSpringParams({}), this.setLineScaleSpringParams({}), this.calcLayout(true), this.update(0);
    }
    pause() {
      super.pause(), this.element.classList.remove("playing"), this.interludeDots.pause();
      for (const t2 of this.currentLyricLineObjects)
        t2.pause();
    }
    resume() {
      super.resume(), this.element.classList.add("playing"), this.interludeDots.resume();
      for (const t2 of this.currentLyricLineObjects)
        t2.resume();
    }
    update(t2 = 0) {
      if (!this.initialLayoutFinished || (super.update(t2), this.supportMaskImage || this.element.style.setProperty(
        "--amll-player-time",
        `${this.currentTime}`
      ), !this.isPageVisible)) return;
      const e2 = t2 / 1e3;
      this.interludeDots.update(t2), this.bottomLine.update(e2);
      for (const s2 of this.currentLyricLineObjects)
        s2.update(e2);
    }
    dispose() {
      super.dispose(), this.element.remove();
      for (const t2 of this.currentLyricLineObjects)
        t2.dispose();
      this.bottomLine.dispose(), this.interludeDots.dispose();
    }
  }
  const Ps = /^\s+/, zs = /^[\p{L}0-9!"#$%&’()*+,-./:;<=>?@\[\]^_`\{|\}~]+/iu;
  function* Is(d2, t2, e2, s2 = 0) {
    let i2 = s2, n2 = 0, r2 = 0, a2 = 0, o2 = false, l2 = null;
    const c2 = d2.measureText(" ");
    for (; a2 < t2.length; ) {
      const h2 = t2.substring(a2);
      if (l2 = h2.match(Ps), l2) {
        o2 = true, r2 = a2, a2 += l2[0].length;
        continue;
      }
      if (l2 = h2.match(zs), l2) {
        a2 += l2[0].length;
        const m2 = l2[0], u2 = d2.measureText(m2);
        i2 + u2.width > e2.maxWidth && (i2 = 0, n2++, o2 = false), o2 && (o2 = false, yield {
          text: " ",
          index: r2,
          lineIndex: n2,
          width: 0,
          height: e2.fontSize,
          x: i2
        }, i2 += c2.width);
        let f2 = "", g2 = null;
        for (const p2 of m2) {
          const y2 = d2.measureText(`${f2}${p2}`), L = d2.measureText(p2);
          f2 !== "" && (i2 = i2 + y2.width - L.width), i2 + L.width > e2.maxWidth && (i2 = 0, n2++), yield {
            text: p2,
            index: a2,
            lineIndex: n2,
            width: L.width,
            height: e2.fontSize,
            x: i2
          }, f2 = p2, g2 = L;
        }
        g2 && (i2 += g2.width);
        continue;
      }
      {
        o2 && (o2 = false, yield {
          text: " ",
          index: r2,
          lineIndex: n2,
          width: 0,
          height: e2.fontSize,
          x: i2
        }, i2 += c2.width);
        const m2 = d2.measureText(t2[a2]);
        i2 + m2.width > e2.maxWidth && (i2 = 0, n2++), yield {
          text: t2[a2],
          index: a2,
          lineIndex: n2,
          width: m2.width,
          height: e2.fontSize,
          x: i2
        }, i2 += m2.width;
      }
      a2++;
    }
    return { x: i2, lineIndex: n2 };
  }
  function* zt(d2, t2, e2, s2 = 0) {
    let i2 = {
      text: "",
      index: 0,
      lineIndex: 0,
      width: 0,
      height: 0,
      x: 0
    };
    for (const n2 of Is(d2, t2, e2, s2))
      n2.lineIndex !== i2.lineIndex ? (i2.text.length && (yield i2), i2 = {
        ...n2
      }) : (i2.text += n2.text, i2.width = n2.x + n2.width);
    i2.text.length && (yield i2);
  }
  class ks extends q {
    constructor(t2, e2 = {
      words: [],
      translatedLyric: "",
      romanLyric: "",
      startTime: 0,
      endTime: 0,
      isBG: false,
      isDuet: false
    }) {
      super(), this.player = t2, this.line = e2, this.relayout();
    }
    getLine() {
      return this.line;
    }
    lineSize = [0, 0];
    measureSize() {
      const t2 = Math.max(
        0,
        ...this.layoutWords.flat().map((n2) => n2.lineIndex + 1)
      ), e2 = Math.max(
        0,
        ...this.translatedLayoutWords.map((n2) => n2.lineIndex + 1)
      ), s2 = Math.max(
        0,
        ...this.romanLayoutWords.map((n2) => n2.lineIndex + 1)
      ), i2 = this.player.baseFontSize;
      return this.lineSize = [
        this.player.size[0],
        (t2 + e2 + s2) * i2 + this.player.size[1] * 0.04
      ], [...this.lineSize];
    }
    layoutWords = [];
    translatedLayoutWords = [];
    romanLayoutWords = [];
    lineCanvas = document.createElement("canvas");
    /** @internal */
    relayout() {
      const t2 = {
        fontSize: this.player.baseFontSize,
        maxWidth: this.player.size[0] - 50,
        lineHeight: this.player.baseFontSize
      }, e2 = this.player.ctx;
      this.player.setFontSize(1);
      for (const n2 of Ot(this.line.words))
        Array.isArray(n2) && n2.length;
      this.layoutWords = [
        [...zt(e2, this.line.words.map((n2) => n2.word).join(""), t2)]
      ], this.player.setFontSize(0.5), this.translatedLayoutWords = [
        ...zt(e2, this.line.translatedLyric, t2)
      ], this.romanLayoutWords = [...zt(e2, this.line.romanLyric, t2)], this.measureSize(), this.lineCanvas.width = this.player.ctx.canvas.width, this.lineCanvas.height = this.lineSize[1] * devicePixelRatio;
      const s2 = this.lineCanvas.getContext("2d");
      s2.globalAlpha = 1, this.player.setFontSize(1), s2.font = e2.font, s2.scale(devicePixelRatio, devicePixelRatio), s2.fillStyle = "white", s2.textBaseline = "top", s2.textAlign = "left", s2.font = `${this.player.baseFontSize}px ${this.player.baseFontFamily}`;
      let i2 = 0;
      for (const n2 of this.layoutWords)
        for (const r2 of n2)
          s2.fillText(
            r2.text,
            r2.x,
            r2.lineIndex * this.player.baseFontSize * this.player.baseLineHeight
          ), i2 = r2.lineIndex;
      s2.translate(0, (i2 + 1) * this.player.baseFontSize), this.player.setFontSize(0.5), s2.font = e2.font, s2.globalAlpha = 0.5, i2 = 0;
      for (const n2 of this.translatedLayoutWords)
        s2.fillText(
          n2.text,
          n2.x,
          n2.lineIndex * this.player.baseFontSize * this.player.baseLineHeight
        ), i2 = n2.lineIndex;
      s2.translate(0, (i2 + 1) * this.player.baseFontSize);
      for (const n2 of this.romanLayoutWords)
        s2.fillText(
          n2.text,
          n2.x,
          n2.lineIndex * this.player.baseFontSize * this.player.baseLineHeight
        );
    }
    enabled = false;
    enable() {
      this.enabled = true;
    }
    disable() {
      this.enabled = false;
    }
    resume() {
    }
    pause() {
    }
    setTransform(t2 = this.top, e2 = this.scale, s2 = this.opacity, i2 = this.blur, n2 = false, r2 = this.delay) {
      const a2 = Math.min(32, i2);
      this.blur = a2, this.opacity = s2, n2 ? (this.lineTransforms.posY.setPosition(t2), this.lineTransforms.scale.setPosition(e2)) : (this.lineTransforms.posY.setTargetPosition(t2, r2), this.lineTransforms.scale.setTargetPosition(e2));
    }
    get isInSight() {
      const t2 = this.lineTransforms.posY.getCurrentPosition(), e2 = this.lineSize[0], s2 = t2 + this.lineSize[1], i2 = this.player.size[1];
      return !(t2 > i2 || e2 < 0 || s2 < 0);
    }
    update(t2) {
      if (this.lineTransforms.posY.update(t2), this.lineTransforms.scale.update(t2), !this.isInSight) return;
      const e2 = this.player.ctx;
      e2.save(), e2.fillStyle = "white", e2.filter = `blur(${this.blur}px)`, e2.textRendering = "geometricPrecision", e2.globalAlpha = this.opacity, e2.translate(0, this.lineTransforms.posY.getCurrentPosition()), e2.scale(1 / devicePixelRatio, 1 / devicePixelRatio), this.lineCanvas.width * this.lineCanvas.height > 0 && e2.drawImage(this.lineCanvas, 0, 0), e2.restore();
    }
  }
  class oi extends Rt {
    canvasElement = document.createElement("canvas");
    currentLyricLineObjects = [];
    /** @internal */
    ctx = this.canvasElement.getContext("2d");
    /** @internal */
    baseLineHeight = 1;
    /** @internal */
    baseFontSize = 30;
    /** @internal */
    baseFontFamily = "sans-serif";
    constructor() {
      super(), this.element.classList.add("amll-lyric-player", "dom"), this.canvasElement.style.width = "100%", this.canvasElement.style.height = "100%", this.canvasElement.style.display = "block", this.canvasElement.style.position = "absolute", this.onResize(), this.update(), this.element.addEventListener("mousemove", (t2) => {
        t2.preventDefault();
      }), this.element.addEventListener("click", (t2) => {
        t2.preventDefault();
      }), this.element.addEventListener("contextmenu", (t2) => {
        t2.preventDefault();
      }), this.element.appendChild(this.canvasElement), this.element.appendChild(this.interludeDots.getElement()), this.element.appendChild(this.bottomLine.getElement());
    }
    setLyricLines(t2, e2) {
      super.setLyricLines(t2, e2), this.currentLyricLineObjects = this.processedLines.map(
        (s2) => new ks(this, s2)
      ), this.setLinePosYSpringParams({}), this.setLineScaleSpringParams({}), this.calcLayout(true);
    }
    onResize() {
      const t2 = getComputedStyle(this.element);
      this.baseFontSize = Number.parseFloat(t2.fontSize) || 30, this.baseFontFamily = t2.fontFamily;
      const e2 = this.canvasElement.clientWidth, s2 = this.canvasElement.clientHeight;
      this.size[0] = e2 - this.baseFontSize * 2, this.size[1] = s2, this.canvasElement.width = e2 * devicePixelRatio, this.canvasElement.height = s2 * devicePixelRatio;
      for (const i2 of this.currentLyricLineObjects)
        i2.relayout();
      console.log("CanvasLyricPlayer.onResize", this.size), this.calcLayout(true);
    }
    /**
     * @internal
     * @param size
     */
    setFontSize(t2) {
      this.ctx.font = `${this.baseFontSize * t2}px ${this.baseFontFamily}`;
    }
    update(t2 = 0) {
      super.update(t2);
      const e2 = this.ctx, s2 = this.size[0], i2 = this.size[1];
      e2.resetTransform(), e2.scale(devicePixelRatio, devicePixelRatio), e2.clearRect(0, 0, this.canvasElement.width, this.canvasElement.height), e2.fillStyle = "currentColor", e2.font = `${this.baseFontSize}px ${this.baseFontFamily}`, e2.textRendering = "optimizeSpeed", e2.textAlign = "left", e2.save(), e2.translate(this.baseFontSize, 0);
      for (const n2 of this.currentLyricLineObjects)
        n2.update(t2 / 1e3);
      e2.restore(), e2.font = `15px ${this.baseFontFamily}`, e2.fillStyle = "#FFFFFF55", e2.textAlign = "right", e2.fillText("CanvasLyricPlayer 播放器", s2 - 16, i2 - 16);
    }
  }
  function Ds(d2, t2 = 20) {
    let e2 = 0;
    return (...i2) => {
      clearTimeout(e2), e2 = setTimeout(() => d2(...i2), t2);
    };
  }
  const Fs = "_lyricLine_1jop6_6", As = "_lyricBgLine_1jop6_36", _s = "_active_1jop6_49", Cs = "_hasDuetLine_1jop6_65", Rs = "_lyricDuetLine_1jop6_66", Os = "_lyricMainLine_1jop6_80", Ws = "_lyricSubLine_1jop6_90", Ns = "_tmpDisableTransition_1jop6_134", N = {
    lyricLine: Fs,
    lyricBgLine: As,
    active: _s,
    hasDuetLine: Cs,
    lyricDuetLine: Rs,
    lyricMainLine: Os,
    lyricSubLine: Ws,
    tmpDisableTransition: Ns
  };
  function Bs(d2) {
    const t2 = [];
    function e2() {
      const s2 = t2[0];
      s2 && d2(...s2.args).then((i2) => {
        s2.resolve(i2);
      }).catch((i2) => {
        s2.reject(i2);
      }).finally(() => {
        t2.shift(), t2.length > 0 && e2();
      });
    }
    return ((...s2) => new Promise((i2, n2) => {
      t2.push({ resolve: i2, reject: n2, args: s2 }), t2.length === 1 && e2();
    }));
  }
  const bt = 32, fe = (d2, t2) => (e2) => Math.min(1, Math.max(0, (e2 - d2) / (t2 - d2))), Nt = 0.5, $s = fe(0, Nt), qs = fe(Nt, 1), Vs = St(0.2, 0.4, 0.58, 1), Hs = St(0.3, 0, 0.58, 1), Us = (d2) => (t2) => t2 < d2 ? Vs($s(t2)) : 1 - Hs(qs(t2));
  function Kt(d2, t2 = 0, e2 = "rgba(0,0,0,var(--bright-mask-alpha, 1.0))", s2 = "rgba(0,0,0,var(--dark-mask-alpha, 1.0))") {
    const i2 = 2 + d2 + t2, n2 = d2 / i2, r2 = (1 - n2) / 2;
    return [
      `linear-gradient(to right,${e2} ${r2 * 100}%,${s2} ${(r2 + n2) * 100}%)`,
      i2
    ];
  }
  class Ys extends MouseEvent {
    constructor(t2, e2) {
      super(e2.type, e2), this.line = t2;
    }
  }
  function js(d2) {
    const t2 = d2.match(/matrix\(([^)]+)\)/);
    if (t2) {
      const e2 = t2[1].split(", "), s2 = Number.parseFloat(e2[0]), i2 = Number.parseFloat(e2[3]);
      return (s2 + i2) / 2;
    }
    return 1;
  }
  class Gs extends q {
    constructor(t2, e2 = {
      words: [],
      translatedLyric: "",
      romanLyric: "",
      startTime: 0,
      endTime: 0,
      isBG: false,
      isDuet: false
    }) {
      super(), this.lyricPlayer = t2, this.lyricLine = e2, this._prevParentEl = t2.getElement(), this.element.setAttribute("class", N.lyricLine), this.lyricLine.isBG && this.element.classList.add(N.lyricBgLine), this.lyricLine.isDuet && this.element.classList.add(N.lyricDuetLine), this.element.appendChild(document.createElement("div")), this.element.appendChild(document.createElement("div")), this.element.appendChild(document.createElement("div"));
      const s2 = this.element.children[0], i2 = this.element.children[1], n2 = this.element.children[2];
      s2.setAttribute("class", N.lyricMainLine), i2.setAttribute("class", N.lyricSubLine), n2.setAttribute("class", N.lyricSubLine), this.rebuildElement(), this.rebuildStyle(), this.markMaskImageDirty("Initial construction");
    }
    element = document.createElement("div");
    splittedWords = [];
    // 由 LyricPlayer 来设置
    lineSize = [0, 0];
    listenersMap = /* @__PURE__ */ new Map();
    onMouseEvent = (t2) => {
      const e2 = new Ys(this, t2);
      for (const s2 of this.listenersMap.get(t2.type) ?? [])
        s2.call(this, e2);
      if (!this.dispatchEvent(e2) || e2.defaultPrevented)
        return t2.preventDefault(), t2.stopPropagation(), t2.stopImmediatePropagation(), false;
    };
    addMouseEventListener(t2, e2, s2) {
      if (e2) {
        const i2 = this.listenersMap.get(t2) ?? /* @__PURE__ */ new Set();
        i2.size === 0 && this.element.addEventListener(t2, this.onMouseEvent, s2), i2.add(e2), this.listenersMap.set(t2, i2);
      }
    }
    removeMouseEventListener(t2, e2, s2) {
      if (e2) {
        const i2 = this.listenersMap.get(t2);
        i2 && (i2.delete(e2), i2.size === 0 && this.element.removeEventListener(t2, this.onMouseEvent, s2));
      }
    }
    areWordsOnSameLine(t2, e2) {
      if (t2?.mainElement && e2?.mainElement) {
        const s2 = t2.mainElement, i2 = e2.mainElement, n2 = s2.getBoundingClientRect(), r2 = i2.getBoundingClientRect();
        return Math.abs(n2.top - r2.top) < 10;
      }
      return true;
    }
    isEnabled = false;
    async enable(t2 = this.lyricLine.startTime) {
      this.isEnabled = true, this.element.classList.add(N.active), await this.waitMaskImageUpdated();
      const e2 = this.element.children[0];
      for (const s2 of this.splittedWords) {
        for (const i2 of s2.elementAnimations)
          i2.currentTime = 0, i2.playbackRate = 1, i2.play();
        for (const i2 of s2.maskAnimations)
          i2.currentTime = Math.min(
            this.totalDuration,
            Math.max(0, t2 - this.lyricLine.startTime)
          ), i2.playbackRate = 1, i2.play();
      }
      e2.classList.add(N.active);
    }
    disable() {
      this.isEnabled = false, this.element.classList.remove(N.active);
      const t2 = this.element.children[0];
      for (const e2 of this.splittedWords)
        for (const s2 of e2.elementAnimations)
          (s2.id === "float-word" || s2.id.includes("emphasize-word-float-only")) && (s2.playbackRate = -1, s2.play());
      t2.classList.remove(N.active);
    }
    lastWord;
    async resume() {
      if (await this.waitMaskImageUpdated(), !!this.isEnabled)
        for (const t2 of this.splittedWords) {
          for (const e2 of t2.elementAnimations)
            (!this.lastWord || this.splittedWords.indexOf(this.lastWord) < this.splittedWords.indexOf(t2)) && e2.play();
          for (const e2 of t2.maskAnimations)
            (!this.lastWord || this.splittedWords.indexOf(this.lastWord) < this.splittedWords.indexOf(t2)) && e2.play();
        }
    }
    async pause() {
      if (await this.waitMaskImageUpdated(), !!this.isEnabled)
        for (const t2 of this.splittedWords) {
          for (const e2 of t2.elementAnimations)
            e2.pause();
          for (const e2 of t2.maskAnimations)
            e2.pause();
        }
    }
    setMaskAnimationState(t2 = 0) {
      const e2 = t2 - this.lyricLine.startTime;
      for (const s2 of this.splittedWords)
        for (const i2 of s2.maskAnimations)
          i2.currentTime = Math.min(this.totalDuration, Math.max(0, e2)), i2.playbackRate = 1, e2 >= 0 && e2 < this.totalDuration ? i2.play() : i2.pause();
    }
    measureLockMark = false;
    measureLock = Bs(
      async (t2) => {
        this.measureLockMark || (this.measureLockMark = true, await t2(), this.measureLockMark = false);
      }
    );
    getLine() {
      return this.lyricLine;
    }
    _prevParentEl;
    lastStyle = "";
    show() {
      this.rebuildStyle();
    }
    hide() {
    }
    rebuildStyle() {
    }
    rebuildElement() {
      this.disposeElements();
      const t2 = this.element.children[0], e2 = this.element.children[1], s2 = this.element.children[2];
      if (this.lyricPlayer._getIsNonDynamic()) {
        t2.innerText = this.lyricLine.words.map((n2) => n2.word).join(""), e2.innerText = this.lyricLine.translatedLyric, s2.innerText = this.lyricLine.romanLyric;
        return;
      }
      const i2 = Ot(this.lyricLine.words);
      t2.innerHTML = "";
      for (const n2 of i2)
        if (Array.isArray(n2)) {
          if (n2.length === 0) continue;
          const r2 = n2.reduce(
            (c2, h2) => (c2.endTime = Math.max(c2.endTime, h2.endTime), c2.startTime = Math.min(c2.startTime, h2.startTime), c2.word += h2.word, c2),
            {
              word: "",
              romanWord: "",
              startTime: Number.POSITIVE_INFINITY,
              endTime: Number.NEGATIVE_INFINITY,
              wordType: "normal",
              obscene: false
            }
          ), a2 = n2.map((c2) => q.shouldEmphasize(c2)).reduce((c2, h2) => c2 || h2, q.shouldEmphasize(r2)), o2 = document.createElement("span");
          o2.classList.add(N.emphasizeWrapper);
          const l2 = [];
          for (const c2 of n2) {
            const h2 = document.createElement("span");
            if (a2) {
              h2.classList.add(N.emphasize);
              const m2 = [];
              for (const f2 of c2.word.trim()) {
                const g2 = document.createElement("span");
                g2.innerText = f2, m2.push(g2), l2.push(g2), h2.appendChild(g2);
              }
              const u2 = {
                ...c2,
                mainElement: h2,
                subElements: m2,
                // elementAnimations: [this.initFloatAnimation(word, mainWordEl)],
                elementAnimations: [],
                // this.initFloatAnimation(word, mainWordEl)
                maskAnimations: [],
                width: 0,
                height: 0,
                padding: 0,
                shouldEmphasize: a2
              };
              this.splittedWords.push(u2);
            } else
              h2.innerText = c2.word, this.splittedWords.push({
                ...c2,
                mainElement: h2,
                subElements: [],
                // elementAnimations: [this.initFloatAnimation(word, mainWordEl)],
                elementAnimations: [],
                // this.initFloatAnimation(word, mainWordEl)
                maskAnimations: [],
                width: 0,
                height: 0,
                padding: 0,
                shouldEmphasize: a2
              });
            o2.appendChild(h2);
          }
          a2 && this.splittedWords[this.splittedWords.length - 1].elementAnimations.push(
            ...this.initEmphasizeAnimation(
              r2,
              l2,
              r2.endTime - r2.startTime,
              r2.startTime - this.lyricLine.startTime
            )
          ), r2.word.trimStart() !== r2.word && t2.appendChild(document.createTextNode(" ")), t2.appendChild(o2), r2.word.trimEnd() !== r2.word && q.shouldEmphasize(r2) && t2.appendChild(document.createTextNode(" "));
        } else if (n2.word.trim().length === 0)
          t2.appendChild(document.createTextNode(" "));
        else {
          const r2 = q.shouldEmphasize(n2), a2 = document.createElement("span"), o2 = {
            ...n2,
            mainElement: a2,
            subElements: [],
            // elementAnimations: [this.initFloatAnimation(chunk, mainWordEl)],
            elementAnimations: [],
            // this.initFloatAnimation(chunk, mainWordEl)
            maskAnimations: [],
            width: 0,
            height: 0,
            padding: 0,
            shouldEmphasize: r2
          };
          if (q.shouldEmphasize(n2)) {
            a2.classList.add(N.emphasize);
            const l2 = [];
            for (const h2 of n2.word.trim()) {
              const m2 = document.createElement("span");
              m2.innerText = h2, l2.push(m2), a2.appendChild(m2);
            }
            o2.subElements = l2;
            const c2 = Math.abs(o2.endTime - o2.startTime);
            o2.elementAnimations.push(
              ...this.initEmphasizeAnimation(
                n2,
                l2,
                c2,
                o2.startTime - this.lyricLine.startTime
              )
            );
          } else
            a2.innerText = n2.word.trim();
          n2.word.trimStart() !== n2.word && t2.appendChild(document.createTextNode(" ")), t2.appendChild(a2), n2.word.trimEnd() !== n2.word && t2.appendChild(document.createTextNode(" ")), this.splittedWords.push(o2);
        }
      e2.innerText = this.lyricLine.translatedLyric, s2.innerText = this.lyricLine.romanLyric;
    }
    initFloatAnimation(t2, e2) {
      const s2 = t2.startTime - this.lyricLine.startTime, i2 = Math.max(1e3, t2.endTime - t2.startTime);
      let n2 = 0.05;
      this.lyricLine.isBG && (n2 *= 2);
      const r2 = e2.animate(
        [
          {
            transform: "translateY(0px)"
          },
          {
            transform: `translateY(${-n2}em)`
          }
        ],
        {
          duration: Number.isFinite(i2) ? i2 : 0,
          delay: Number.isFinite(s2) ? s2 : 0,
          id: "float-word",
          composite: "add",
          fill: "both",
          easing: "ease-out"
        }
      );
      return r2.pause(), r2;
    }
    // 按照原 Apple Music 参考，强调效果只应用缩放、轻微左右位移和辉光效果，原主要的悬浮位移效果不变
    // 为了避免产生锯齿抖动感，使用 matrix3d 来实现缩放和位移
    initEmphasizeAnimation(t2, e2, s2, i2) {
      const n2 = Math.max(0, i2);
      let r2 = Math.max(1e3, s2), a2 = [], o2 = r2 / 2e3;
      o2 = o2 > 1 ? Math.sqrt(o2) : o2 ** 3;
      let l2 = r2 / 3e3;
      l2 = l2 > 1 ? Math.sqrt(l2) : l2 ** 3, o2 *= 0.6, l2 *= 0.5, this.lyricLine.words.length > 0 && t2.word.includes(
        this.lyricLine.words[this.lyricLine.words.length - 1].word
      ) && (o2 *= 1.6, l2 *= 1.5, r2 *= 1.2), o2 = Math.min(1.2, o2), l2 = Math.min(0.8, l2);
      const c2 = Number.isFinite(r2) ? r2 : 0, h2 = Us(Nt);
      return a2 = e2.flatMap((m2, u2, f2) => {
        const g2 = n2 + r2 / 2.5 / f2.length * u2, p2 = [], y2 = new Array(bt).fill(0).map((b2, w2) => {
          const T = (w2 + 1) / bt, v2 = h2(T), P = h2(T) * l2, z = he(le(), 1 + v2 * 0.1 * o2), E = -v2 * 0.03 * o2 * (f2.length / 2 - u2), F = -v2 * 0.025 * o2;
          return {
            offset: T,
            transform: `${de(
              z,
              4
            )} translate(${E}em, ${F}em)`,
            textShadow: `0 0 ${Math.min(
              0.3,
              l2 * 0.3
            )}em rgba(255, 255, 255, ${P})`
          };
        }), L = m2.animate(y2, {
          duration: c2,
          delay: Number.isFinite(g2) ? g2 : 0,
          id: `emphasize-word-${m2.innerText}-${u2}`,
          iterations: 1,
          composite: "replace",
          fill: "both"
        });
        L.onfinish = () => {
          L.pause();
        }, L.pause(), p2.push(L);
        const x2 = new Array(bt).fill(0).map((b2, w2) => {
          const T = (w2 + 1) / bt;
          let v2 = Math.sin(T * Math.PI);
          return this.lyricLine.isBG && (v2 *= 2), {
            offset: T,
            transform: `translateY(${-v2 * 0.05}em)`
          };
        }), M2 = m2.animate(x2, {
          duration: c2 * 1.4,
          delay: Number.isFinite(g2) ? g2 - 400 : 0,
          id: "emphasize-word-float",
          iterations: 1,
          composite: "add",
          fill: "both"
        });
        return M2.onfinish = () => {
          M2.pause();
        }, M2.pause(), p2.push(M2), p2;
      }), a2;
    }
    get totalDuration() {
      return this.lyricLine.endTime - this.lyricLine.startTime;
    }
    maskImageDirty = false;
    markImageDirtyPromiseResolve = /* @__PURE__ */ new Set();
    markImageDirtyPromise = new Promise((t2) => {
      this.markImageDirtyPromiseResolve.add(t2);
    });
    markMaskImageDirty(t2 = "") {
      this.maskImageDirty = true, this.element.classList.contains(N.dirty) || this.element.classList.add(N.dirty);
      const e2 = Promise.all([
        this.markImageDirtyPromise,
        new Promise((s2) => {
          this.markImageDirtyPromiseResolve.add(s2);
        })
      ]).then(() => {
      });
      return this.markImageDirtyPromise = e2, e2;
    }
    waitMaskImageUpdated() {
      return this.markImageDirtyPromise;
    }
    async updateMaskImage() {
      if (this.element.checkVisibility({
        contentVisibilityAuto: true
      })) {
        this.maskImageDirty = false, await this.measureLock(async () => {
          await Promise.all(
            this.splittedWords.map(async (t2) => {
              const e2 = t2.mainElement;
              e2 ? await ce(() => {
                t2.padding = Number.parseFloat(
                  getComputedStyle(e2).paddingLeft
                ), t2.width = e2.clientWidth - t2.padding * 2, t2.height = e2.clientHeight - t2.padding * 2;
              }) : (t2.width = 0, t2.height = 0, t2.padding = 0), t2.width * t2.height === 0 && console.warn("Word size is zero");
            })
          ), await Zt(() => {
            this.lyricPlayer.supportMaskImage ? this.generateWebAnimationBasedMaskImage() : this.generateCalcBasedMaskImage();
          });
        });
        for (const t2 of this.markImageDirtyPromiseResolve)
          t2(), this.markImageDirtyPromiseResolve.delete(t2);
        await Zt(() => {
          this.element.classList.remove(N.dirty);
        });
      }
    }
    generateCalcBasedMaskImage() {
      for (const t2 of this.splittedWords) {
        const e2 = t2.mainElement;
        if (e2) {
          t2.width = e2.clientWidth, t2.height = e2.clientHeight;
          const s2 = t2.height * this.lyricPlayer.getWordFadeWidth(), [i2, n2] = Kt(
            s2 / t2.width
          ), r2 = `${n2 * 100}% 100%`;
          this.lyricPlayer.supportMaskImage ? (e2.style.maskImage = i2, e2.style.maskRepeat = "no-repeat", e2.style.maskOrigin = "left", e2.style.maskSize = r2) : (e2.style.webkitMaskImage = i2, e2.style.webkitMaskRepeat = "no-repeat", e2.style.webkitMaskOrigin = "left", e2.style.webkitMaskSize = r2);
          const a2 = t2.width + s2, o2 = `clamp(${-a2}px,calc(${-a2}px + (var(--amll-player-time) - ${t2.startTime})*${a2 / Math.abs(t2.endTime - t2.startTime)}px),0px) 0px, left top`;
          e2.style.maskPosition = o2, e2.style.webkitMaskPosition = o2;
        }
      }
    }
    generateWebAnimationBasedMaskImage() {
      const t2 = Math.max(
        this.splittedWords.reduce((e2, s2) => Math.max(s2.endTime, e2), 0),
        this.lyricLine.endTime
      ) - this.lyricLine.startTime;
      this.splittedWords.forEach((e2, s2) => {
        const i2 = e2.mainElement;
        if (i2) {
          const n2 = e2.height * this.lyricPlayer.getWordFadeWidth(), [r2, a2] = Kt(
            n2 / (e2.width + e2.padding * 2)
          ), o2 = `${a2 * 100}% 100%`;
          this.lyricPlayer.supportMaskImage ? (i2.style.maskImage = r2, i2.style.maskRepeat = "no-repeat", i2.style.maskOrigin = "left", i2.style.maskSize = o2) : (i2.style.webkitMaskImage = r2, i2.style.webkitMaskRepeat = "no-repeat", i2.style.webkitMaskOrigin = "left", i2.style.webkitMaskSize = o2);
          const l2 = this.splittedWords.slice(0, s2).reduce((x2, M2) => x2 + M2.width, 0) + (this.splittedWords[0] ? n2 : 0), c2 = -(e2.width + e2.padding * 2 + n2), h2 = (x2) => Math.max(c2, Math.min(0, x2));
          let m2 = -l2 - e2.width - e2.padding - n2, u2 = 0;
          const f2 = [];
          let g2 = m2, p2 = 0;
          const y2 = () => {
            const x2 = m2 - g2, M2 = Math.max(0, Math.min(1, u2)), b2 = M2 - p2, w2 = Math.abs(b2 / x2);
            if (m2 > c2 && g2 < c2) {
              const P = Math.abs(g2 - c2) * w2, z = `${h2(g2)}px 0`, E = {
                offset: p2 + P,
                maskPosition: z
              };
              f2.push(E);
            }
            if (m2 > 0 && g2 < 0) {
              const P = Math.abs(g2) * w2, z = `${h2(m2)}px 0`, E = {
                offset: p2 + P,
                maskPosition: z
              };
              f2.push(E);
            }
            const T = `${h2(m2)}px 0`, v2 = {
              offset: M2,
              maskPosition: T
            };
            f2.push(v2), g2 = m2, p2 = M2;
          };
          y2();
          let L = 0;
          this.splittedWords.forEach((x2, M2) => {
            {
              const b2 = x2.startTime - this.lyricLine.startTime, w2 = b2 - L;
              u2 += w2 / t2, w2 > 0 && y2(), L = b2;
            }
            {
              const b2 = x2.endTime - x2.startTime;
              u2 += b2 / t2, m2 += x2.width, M2 === 0 && (m2 += n2 * 1.5), M2 === this.splittedWords.length - 1 && (m2 += n2 * 0.5), b2 > 0 && y2(), L += b2;
            }
          });
          for (const x2 of e2.maskAnimations)
            x2.cancel();
          try {
            const x2 = i2.animate(f2, {
              duration: t2 || 1,
              id: `fade-word-${e2.word}-${s2}`,
              fill: "both"
            });
            x2.pause(), e2.maskAnimations = [x2];
          } catch (x2) {
            console.warn("应用渐变动画发生错误", f2, t2, x2);
          }
        }
      });
    }
    getElement() {
      return this.element;
    }
    setTransform(t2 = this.top, e2 = this.scale, s2 = 1, i2 = 0, n2 = false, r2 = 0) {
      super.setTransform(t2, e2, s2, i2, n2, r2);
      const a2 = this.isInSight, o2 = this.lyricPlayer.getEnableSpring();
      this.top = t2, this.scale = e2, this.delay = r2 * 1e3 | 0;
      const l2 = this.element.children[0];
      if (l2.style.opacity = `${s2}`, n2 || !o2) {
        if (n2 && this.element.classList.add(N.tmpDisableTransition), this.lineTransforms.posY.setPosition(t2), this.lineTransforms.scale.setPosition(e2), o2)
          this.rebuildStyle();
        else {
          const c2 = this.isInSight;
          a2 || c2 ? this.show() : this.hide();
        }
        n2 && requestAnimationFrame(() => {
          this.element.classList.remove(N.tmpDisableTransition);
        });
      } else
        this.lineTransforms.posY.setTargetPosition(t2, r2), this.lineTransforms.scale.setTargetPosition(e2);
    }
    update(t2 = 0) {
      if (this.lyricPlayer.getEnableSpring())
        if (this.lineTransforms.posY.update(t2), this.lineTransforms.scale.update(t2), this.isInSight ? (this.show(), this.maskImageDirty && this.updateMaskImage()) : this.hide(), this.lyricPlayer.getEnableSpring())
          this.element.style.setProperty(
            "--bright-mask-alpha",
            `${Math.max(
              0,
              Math.min(
                1,
                this.lineTransforms.scale.getCurrentPosition() / 100 - 0.97
              ) / 0.03
            ) * 0.8 + 0.2}`
          ), this.element.style.setProperty(
            "--dark-mask-alpha",
            `${Math.max(
              0,
              Math.min(
                1,
                this.lineTransforms.scale.getCurrentPosition() / 100 - 0.97
              ) / 0.03
            ) * 0.2 + 0.2}`
          );
        else {
          const s2 = window.getComputedStyle(this.element).transform, i2 = js(s2);
          this.element.style.setProperty(
            "--bright-mask-alpha",
            `${Math.max(0, Math.min(1, (i2 - 0.97) / 0.03)) * 0.8 + 0.2}`
          ), this.element.style.setProperty(
            "--dark-mask-alpha",
            `${Math.max(0, Math.min(1, (i2 - 0.97) / 0.03)) * 0.2 + 0.2}`
          );
        }
    }
    _getDebugTargetPos() {
      return `[位移: ${this.top}; 缩放: ${this.scale}; 延时: ${this.delay}]`;
    }
    get isInSight() {
      const t2 = this.lineTransforms.posY.getCurrentPosition(), e2 = this.lineSize[1], s2 = t2 + e2, i2 = this.lyricPlayer.size[1];
      return !(t2 > i2 + e2 || s2 < -e2);
    }
    disposeElements() {
      for (const t2 of this.splittedWords) {
        for (const e2 of t2.elementAnimations)
          e2.cancel();
        for (const e2 of t2.maskAnimations)
          e2.cancel();
        for (const e2 of t2.subElements)
          e2.remove(), e2.parentNode?.removeChild(e2);
        t2.elementAnimations = [], t2.maskAnimations = [], t2.subElements = [], t2.mainElement.remove(), t2.mainElement.parentNode?.removeChild(t2.mainElement);
      }
      this.splittedWords = [];
    }
    dispose() {
      this.disposeElements(), this.element.remove();
    }
  }
  class ci extends Rt {
    currentLyricLineObjects = [];
    debounceCalcLayout = Ds(
      () => this.calcLayout(true).then(
        () => this.currentLyricLineObjects.map(async (t2, e2) => {
          t2.markMaskImageDirty("DomLyricPlayer onResize"), await t2.waitMaskImageUpdated(), this.hotLines.has(e2) && (t2.enable(this.currentTime), t2.resume());
        })
      ),
      1e3
    );
    onResize() {
      const t2 = getComputedStyle(this.element);
      this._baseFontSize = Number.parseFloat(t2.fontSize);
      const e2 = this.element.clientWidth - Number.parseFloat(t2.paddingLeft) - Number.parseFloat(t2.paddingRight), s2 = this.element.clientHeight - Number.parseFloat(t2.paddingTop) - Number.parseFloat(t2.paddingBottom);
      this.innerSize[0] = e2, this.innerSize[1] = s2, this.rebuildStyle(), this.debounceCalcLayout();
    }
    supportPlusLighter = CSS.supports("mix-blend-mode", "plus-lighter");
    supportMaskImage = CSS.supports("mask-image", "none");
    innerSize = [0, 0];
    onLineClickedHandler = (t2) => {
      const e2 = new pe(
        this.lyricLinesIndexes.get(t2.line) ?? -1,
        t2.line,
        t2
      );
      this.dispatchEvent(e2) || (t2.preventDefault(), t2.stopPropagation(), t2.stopImmediatePropagation());
    };
    /**
     * 是否为非逐词歌词
     * @internal
     */
    _getIsNonDynamic() {
      return this.isNonDynamic;
    }
    _baseFontSize = Number.parseFloat(
      getComputedStyle(this.element).fontSize
    );
    get baseFontSize() {
      return this._baseFontSize;
    }
    constructor() {
      super(), this.onResize(), this.element.classList.add("amll-lyric-player", "dom-slim"), this.disableSpring && this.element.classList.add(N.disableSpring);
    }
    rebuildStyle() {
      const t2 = this.innerSize[0], e2 = this.innerSize[1];
      this.element.style.setProperty("--amll-lp-width", `${t2.toFixed(4)}px`), this.element.style.setProperty(
        "--amll-lp-height",
        `${e2.toFixed(4)}px`
      );
    }
    setWordFadeWidth(t2 = 0.5) {
      super.setWordFadeWidth(t2);
      for (const e2 of this.currentLyricLineObjects)
        e2.markMaskImageDirty("DomLyricPlayer setWordFadeWidth");
    }
    /**
     * 设置当前播放歌词，要注意传入后这个数组内的信息不得修改，否则会发生错误
     * @param lines 歌词数组
     * @param initialTime 初始时间，默认为 0
     */
    setLyricLines(t2, e2 = 0) {
      super.setLyricLines(t2, e2), this.hasDuetLine ? this.element.classList.add(N.hasDuetLine) : this.element.classList.remove(N.hasDuetLine);
      for (const s2 of this.currentLyricLineObjects)
        s2.removeMouseEventListener("click", this.onLineClickedHandler), s2.removeMouseEventListener("contextmenu", this.onLineClickedHandler), s2.dispose();
      this.currentLyricLineObjects = this.processedLines.map((s2, i2) => {
        const n2 = new Gs(this, s2);
        return n2.addMouseEventListener("click", this.onLineClickedHandler), n2.addMouseEventListener("contextmenu", this.onLineClickedHandler), this.element.appendChild(n2.getElement()), this.lyricLinesIndexes.set(n2, i2), n2.markMaskImageDirty("DomLyricPlayer setLyricLines"), n2;
      }), this.setLinePosXSpringParams({}), this.setLinePosYSpringParams({}), this.setLineScaleSpringParams({}), this.calcLayout(true).then(() => {
        this.initialLayoutFinished = true;
      });
    }
    pause() {
      super.pause(), this.interludeDots.pause();
      for (const t2 of this.currentLyricLineObjects)
        t2.pause();
    }
    resume() {
      super.resume(), this.interludeDots.resume();
      for (const t2 of this.currentLyricLineObjects)
        t2.resume();
    }
    update(t2 = 0) {
      if (!this.initialLayoutFinished || (super.update(t2), !this.isPageVisible)) return;
      const e2 = t2 / 1e3;
      this.interludeDots.update(t2), this.bottomLine.update(e2);
      for (const s2 of this.currentLyricLineObjects)
        s2.update(e2);
    }
    async calcLayout(t2) {
      await super.calcLayout(t2);
      const e2 = this.currentLyricLineObjects[this.targetAlignIndex], s2 = e2.getElement(), i2 = s2.checkVisibility({
        contentVisibilityAuto: true
      }), n2 = this.element.getBoundingClientRect().top;
      i2 || s2.scrollIntoView({
        block: "center",
        behavior: "instant"
      });
      const r2 = s2.clientHeight;
      let o2 = s2.getBoundingClientRect().top - n2 - this.size[1] * this.alignPosition;
      if (e2)
        switch (this.alignAnchor) {
          case "bottom":
            o2 += r2;
            break;
          case "center":
            o2 += r2 / 2;
            break;
        }
      this.element.scrollBy({
        top: o2,
        behavior: "smooth"
      });
    }
    dispose() {
      super.dispose(), this.element.remove();
      for (const t2 of this.currentLyricLineObjects)
        t2.dispose();
      this.bottomLine.dispose(), this.interludeDots.dispose();
    }
  }
  const AMLLCore = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
    __proto__: null,
    AbstractBaseRenderer: Le,
    BackgroundRender: ie,
    BaseRenderer: te,
    CanvasLyricPlayer: oi,
    DomLyricPlayer: ai,
    DomSlimLyricPlayer: ci,
    LyricLineMouseEvent: pe,
    LyricPlayer: ai,
    LyricPlayerBase: Rt,
    MeshGradientRenderer: si,
    PixiRenderer: ii
  }, Symbol.toStringTag, { value: "Module" }));
  window.AMLLCore = AMLLCore;
  let playerInstance = null;
  let backgroundRender = null;
  let lastAlbumArt = "";
  let albumArtRetryCount = 0;
  const MAX_ALBUM_ART_RETRIES = 3;
  let pendingLyricOptions = {};
  function applyAMLLPatch() {
    logToAndroid("Applying AMLL patch for generateFadeGradient", "info");
    const style = document.createElement("style");
    style.textContent = `
    /* 确保 mask-image 相关 CSS 变量始终有安全默认值 */
    :root {
      --bright-mask-alpha: 1.0;
      --dark-mask-alpha: 0.2;
    }
  `;
    document.head.appendChild(style);
    logToAndroid("AMLL patch applied successfully", "debug");
  }
  function logToAndroid(message, level = "debug") {
    if (window.Android?.log) {
      try {
        window.Android.log(message, level);
      } catch (e2) {
        console.log(`[ANDROID] ${message}`);
      }
    } else {
      console.log(`[${level.toUpperCase()}] ${message}`);
    }
  }
  function normalizeLyricLines(lines) {
    if (!Array.isArray(lines)) return [];
    return lines.map((line) => {
      const words = line.words?.map((w2) => ({
        word: String(w2.word ?? ""),
        startTime: Number(w2.startTime ?? line.startTime ?? 0),
        endTime: Number(w2.endTime ?? line.endTime ?? line.startTime ?? 0)
      })) || [];
      if (words.length === 0 && line.text) {
        words.push({
          word: line.text,
          startTime: Number(line.startTime ?? 0),
          endTime: Number(line.endTime ?? line.startTime ?? 0)
        });
      }
      return {
        words,
        translatedLyric: String(line.translatedLyric ?? ""),
        romanLyric: String(line.romanLyric ?? ""),
        startTime: Number(line.startTime ?? 0),
        endTime: Number(line.endTime ?? 0),
        isBG: !!line.isBG,
        isDuet: !!line.isDuet
      };
    });
  }
  function attachElementToRoot(root, el, zIndex) {
    el.style.position = el.style.position || "absolute";
    el.style.inset = el.style.inset || "0";
    el.style.width = el.style.width || "100%";
    el.style.height = el.style.height || "100%";
    el.style.pointerEvents = el.style.pointerEvents || "none";
    el.style.zIndex = el.style.zIndex || zIndex;
    if (el.parentElement !== root) {
      root.appendChild(el);
    }
  }
  function createBackgroundRenderer(core, root) {
    const Background = core.BackgroundRender;
    if (!Background?.new) {
      logToAndroid("BackgroundRender factory not found on core", "debug");
      return null;
    }
    const rendererCandidates = [
      core.MeshGradientRenderer,
      core.PixiRenderer
    ].filter(Boolean);
    for (const RendererCtor of rendererCandidates) {
      try {
        const instance = Background.new(RendererCtor);
        const element = instance.getElement();
        attachElementToRoot(root, element, "-1");
        logToAndroid(`Created BackgroundRender with ${RendererCtor?.name || "renderer"}`, "info");
        return instance;
      } catch (e2) {
        logToAndroid(`BackgroundRender init failed with ${RendererCtor?.name || "renderer"}: ${e2.message}`, "warn");
      }
    }
    return null;
  }
  const demoAlbumArt = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJyZ2JhKDAsMCwwLDAuMSkiLz48L3N2Zz4=";
  let currentTime = 0;
  let lyricLines = [];
  let albumUri = demoAlbumArt;
  let isPlaying = true;
  let hasPlaybackState = false;
  function attachCoreInstances(container) {
    try {
      const Core = AMLLCore || window.AMLLCore;
      if (!Core) {
        logToAndroid("AMLL core module not found at runtime", "warn");
        return;
      }
      const DomLyricPlayer = Core.DomLyricPlayer || Core.DOMLyricPlayer || Core.DomLyricPlayerClass || Core.LyricPlayer;
      if (DomLyricPlayer) {
        try {
          playerInstance = new DomLyricPlayer({ container, album: albumUri });
          logToAndroid("Created DomLyricPlayer from core", "info");
        } catch (e2) {
          logToAndroid(`Failed to instantiate DomLyricPlayer: ${e2.message}`, "error");
        }
      } else {
        logToAndroid("DomLyricPlayer constructor not found on core", "debug");
      }
      backgroundRender = createBackgroundRenderer(Core, container);
      window.__amll = window.__amll || {};
      window.__amll.player = playerInstance;
      window.__amll.backgroundRender = backgroundRender;
    } catch (error) {
      logToAndroid(`attachCoreInstances error: ${error.message}`, "error");
    }
  }
  function renderFallbackLyrics(root, lines) {
    try {
      if (!root) return;
      let container = document.getElementById("amll-debug-lyrics");
      if (!container) {
        container = document.createElement("div");
        container.id = "amll-debug-lyrics";
        container.style.pointerEvents = "none";
        container.style.position = "absolute";
        container.style.left = "0";
        container.style.top = "0";
        container.style.width = "100%";
        container.style.zIndex = "9999";
        root.appendChild(container);
      }
      container.innerHTML = "";
      lines.forEach((line, idx) => {
        const el = document.createElement("div");
        el.className = "amll-debug-line";
        const text = line.words && line.words.length > 0 ? line.words.map((w2) => w2.word).join(" ") : line.translatedLyric || line.romanLyric || "";
        el.textContent = text || `[line ${idx}]`;
        el.setAttribute("data-start", String(line.startTime));
        el.setAttribute("data-end", String(line.endTime));
        container.appendChild(el);
      });
    } catch (err) {
      logToAndroid(`renderFallbackLyrics error: ${err.message}`, "error");
    }
  }
  function forceRebuildLyricsDom(reason = "manual refresh") {
    try {
      const p2 = window.__amll && window.__amll.player || playerInstance;
      if (!p2) return false;
      const lineObjects = Array.isArray(p2.currentLyricLineObjects) ? p2.currentLyricLineObjects : [];
      if (lineObjects.length === 0) {
        if (typeof p2.setLyricLines === "function" && lyricLines.length > 0) {
          p2.setLyricLines(lyricLines, currentTime);
          if (typeof p2.update === "function") {
            p2.update(currentTime);
          }
          logToAndroid(`forceRebuildLyricsDom: rebuilt via setLyricLines (${reason})`, "debug");
          return true;
        }
        return false;
      }
      let rebuiltCount = 0;
      for (const line of lineObjects) {
        if (!line) continue;
        if (typeof line.rebuildElement === "function") {
          line.rebuildElement();
          rebuiltCount++;
        }
        if (typeof line.markMaskImageDirty === "function") {
          line.markMaskImageDirty(reason);
        } else if (typeof line.updateMaskImageSync === "function") {
          line.updateMaskImageSync();
        } else if (typeof line.rebuildStyle === "function") {
          line.rebuildStyle();
        }
      }
      if (typeof p2.update === "function") {
        p2.update(currentTime);
      }
      logToAndroid(`forceRebuildLyricsDom: rebuilt=${rebuiltCount} (${reason})`, "debug");
      return rebuiltCount > 0;
    } catch (err) {
      logToAndroid(`forceRebuildLyricsDom error: ${err.message}`, "error");
      return false;
    }
  }
  function initAMLL() {
    try {
      document.documentElement.style.background = "transparent";
      document.body.style.background = "transparent";
      applyAMLLPatch();
      const root = document.getElementById("app") || document.createElement("div");
      if (!document.getElementById("app")) {
        root.id = "app";
        document.body.appendChild(root);
      }
      root.style.position = "relative";
      root.style.width = "100%";
      root.style.height = "100vh";
      attachCoreInstances(root);
      try {
        const p2 = window.__amll && window.__amll.player || playerInstance;
        const el = p2?.element || p2?.rootElement || null;
        if (el && el instanceof HTMLElement) {
          if (el.parentElement !== root) {
            try {
              el.style.position = el.style.position || "relative";
              el.style.width = el.style.width || "100%";
              el.style.height = el.style.height || "100%";
              root.appendChild(el);
              logToAndroid("Appended player.element to #app as fallback", "debug");
            } catch (e2) {
              logToAndroid(`Failed to append player.element fallback: ${e2.message}`, "error");
            }
          }
        }
      } catch (e2) {
        logToAndroid(`player append fallback error: ${e2.message}`, "error");
      }
      if (lyricLines.length > 0) {
        requestAnimationFrame(() => {
          forceRebuildLyricsDom("initAMLL");
        });
      }
      logToAndroid("AMLL core WebView initialized", "info");
    } catch (error) {
      logToAndroid(`Initialization error: ${error.message}`, "error");
    }
  }
  if (document.readyState === "loading") {
    window.addEventListener("DOMContentLoaded", initAMLL);
  } else {
    setTimeout(initAMLL, 0);
  }
  window.updateLyrics = function(payload) {
    try {
      const rawLines = Array.isArray(payload?.lines) ? payload.lines : [];
      const normalized = normalizeLyricLines(rawLines);
      lyricLines = normalized;
      logToAndroid(`updateLyrics: ${normalized.length} lines`, "debug");
      if (playerInstance) {
        if (playerInstance.setLyricLines) {
          playerInstance.setLyricLines(normalized);
        } else if (playerInstance.setLyrics) {
          playerInstance.setLyrics(normalized);
        } else if (playerInstance.updateLyrics) {
          playerInstance.updateLyrics(normalized);
        } else {
          logToAndroid("playerInstance does not expose setLyricLines/setLyrics/updateLyrics", "warn");
          const root = document.getElementById("app");
          renderFallbackLyrics(root, normalized);
        }
        requestAnimationFrame(() => {
          forceRebuildLyricsDom("updateLyrics");
        });
      } else {
        const root = document.getElementById("app");
        renderFallbackLyrics(root, normalized);
      }
    } catch (e2) {
      logToAndroid(`updateLyrics error: ${e2.message}`, "error");
    }
  };
  window.updateTime = function(timeMs) {
    try {
      const t2 = Number(timeMs);
      if (hasPlaybackState && !isPlaying) {
        logToAndroid(`updateTime skipped while paused: ${Math.trunc(t2)}`, "debug");
        return;
      }
      currentTime = t2;
      if (playerInstance) {
        if (playerInstance.setCurrentTime) {
          playerInstance.setCurrentTime(Math.trunc(t2), false);
        } else if (playerInstance.seek) {
          playerInstance.seek(Math.trunc(t2));
        } else {
          logToAndroid("playerInstance has no setCurrentTime/seek", "debug");
        }
        if (typeof playerInstance.update === "function") {
          playerInstance.update(Math.trunc(t2));
        }
      }
    } catch (e2) {
      logToAndroid(`updateTime error: ${e2.message}`, "error");
    }
  };
  window.updateAlbumArt = async function(uri2) {
    try {
      const isValidUri = uri2 && typeof uri2 === "string" && uri2.trim().length > 0;
      if (!isValidUri) {
        logToAndroid("updateAlbumArt: invalid uri, using placeholder", "warn");
        albumUri = demoAlbumArt;
        lastAlbumArt = "";
        return;
      }
      let finalUri = uri2;
      if (uri2.startsWith("file:")) {
        try {
          const response = await fetch(uri2);
          if (!response.ok) throw new Error(`HTTP ${response.status}`);
          const blob = await response.blob();
          const reader = new FileReader();
          finalUri = await new Promise((resolve, reject) => {
            reader.onload = () => resolve(reader.result);
            reader.onerror = () => reject(reader.error);
            reader.readAsDataURL(blob);
          });
        } catch (err) {
          logToAndroid(`Failed to load file URI: ${err.message}`, "error");
          albumUri = demoAlbumArt;
          lastAlbumArt = "";
          return;
        }
      }
      if (lastAlbumArt === uri2) {
        logToAndroid("Album art unchanged, skipping", "debug");
        return;
      }
      albumUri = finalUri || demoAlbumArt;
      lastAlbumArt = uri2;
      albumArtRetryCount = 0;
      if (backgroundRender && backgroundRender.setAlbum) {
        try {
          await backgroundRender.setAlbum(albumUri);
          logToAndroid("BackgroundRender setAlbum success", "debug");
        } catch (err) {
          logToAndroid(`BackgroundRender.setAlbum error: ${err.message}`, "error");
          albumArtRetryCount++;
          if (albumArtRetryCount < MAX_ALBUM_ART_RETRIES) {
            setTimeout(() => window.updateAlbumArt?.(uri2), 500 * albumArtRetryCount);
          }
        }
      }
    } catch (e2) {
      logToAndroid(`updateAlbumArt error: ${e2.message}`, "error");
    }
  };
  window.setPaused = function(paused) {
    isPlaying = !paused;
    hasPlaybackState = true;
    logToAndroid(`Playback ${paused ? "paused" : "resumed"}`, "debug");
    try {
      if (!playerInstance) return;
      if (paused && typeof playerInstance.pause === "function") {
        playerInstance.pause();
      } else if (!paused && typeof playerInstance.resume === "function") {
        playerInstance.resume();
      } else if (!paused && typeof playerInstance.play === "function") {
        playerInstance.play();
      }
      if (!paused && typeof playerInstance.update === "function") {
        playerInstance.update(Math.trunc(currentTime));
      }
    } catch (e2) {
      logToAndroid(`setPaused error: ${e2.message}`, "error");
    }
  };
  window.configureLyricMotion = function(options) {
    logToAndroid(`configureLyricMotion: ${JSON.stringify(options)}`, "debug");
    pendingLyricOptions = { ...pendingLyricOptions, ...options };
    try {
      if (!playerInstance) return;
      const lp = playerInstance;
      if (options.enableSpring !== void 0 && lp.setLinePosYSpringParams) {
        lp.setLinePosYSpringParams(options.enableSpring ? { mass: 0.9, damping: 15, stiffness: 90 } : { mass: 1, damping: 30, stiffness: 50 });
      }
      if (options.enableScale !== void 0 && lp.setLineScaleSpringParams) {
        lp.setLineScaleSpringParams(options.enableScale ? { mass: 2, damping: 25, stiffness: 100 } : { mass: 1, damping: 30, stiffness: 50 });
      }
      if (options.enableBlur !== void 0 && lp.setEnableBlur) lp.setEnableBlur(options.enableBlur);
      if (options.hidePassedLines !== void 0 && lp.setHidePassedLines) lp.setHidePassedLines(options.hidePassedLines);
      if (options.wordFadeWidth !== void 0 && lp.setWordFadeWidth) lp.setWordFadeWidth(options.wordFadeWidth);
    } catch (e2) {
      logToAndroid(`configureLyricMotion error: ${e2.message}`, "error");
    }
  };
  window.configureBackgroundEffect = function(options) {
    logToAndroid(`configureBackgroundEffect: ${JSON.stringify(options)}`, "debug");
    try {
      if (!backgroundRender) return;
      if (options.flowSpeed !== void 0 && backgroundRender.setFlowSpeed) backgroundRender.setFlowSpeed(options.flowSpeed);
      if (options.renderScale !== void 0 && backgroundRender.setRenderScale) backgroundRender.setRenderScale(options.renderScale);
      if (options.lowFreqVolume !== void 0 && backgroundRender.setLowFreqVolume) backgroundRender.setLowFreqVolume(options.lowFreqVolume);
      if (options.fps !== void 0 && backgroundRender.setFPS) backgroundRender.setFPS(options.fps);
      if (options.staticMode !== void 0 && backgroundRender.setStaticMode) backgroundRender.setStaticMode(options.staticMode);
    } catch (e2) {
      logToAndroid(`configureBackgroundEffect error: ${e2.message}`, "error");
    }
  };
  window.setRenderMode = function(mode) {
    logToAndroid(`setRenderMode: ${mode}`, "debug");
  };
  window.setLyricPlayerImplementation = function(implementation2) {
    logToAndroid(`setLyricPlayerImplementation: ${implementation2}`, "debug");
  };
  window.setLyricSizePreset = function(preset) {
    logToAndroid(`setLyricSizePreset: ${preset}`, "debug");
    document.documentElement.style.setProperty("--amll-lp-font-size-preset", preset);
  };
  window.setEnableTranslationLine = function(enabled) {
    document.documentElement.style.setProperty("--amll-show-translation", enabled ? "1" : "0");
  };
  window.setEnableRomanLine = function(enabled) {
    document.documentElement.style.setProperty("--amll-show-roman", enabled ? "1" : "0");
  };
  window.setEnableSwapTransRomanLine = function(enabled) {
    document.documentElement.style.setProperty("--amll-swap-trans-roman", enabled ? "1" : "0");
  };
  window.setAdvanceLyricDynamicLyricTime = function(enabled) {
    document.documentElement.style.setProperty("--amll-advance-dynamic-time", enabled ? "1" : "0");
    pendingLyricOptions = { ...pendingLyricOptions, advanceDynamicTime: enabled };
  };
  window.rebuildLyricsDom = function(reason = "manual refresh") {
    return forceRebuildLyricsDom(reason);
  };
})();
//# sourceMappingURL=amll.bundle.js.map
