#!/usr/bin/env python3
"""深度调试 米津玄師-Lemon 的翻译解密"""
import urllib.request, json, base64, zlib, sys
sys.path.insert(0, '.')
from test_qrc import tripledes_key_setup, tripledes_crypt, DECRYPT, _QRC_3DES_KEY

mid, sid, name = "003RMFKt1e32JG", 4991546, "米津玄師-Lemon"

url = 'https://u.y.qq.com/cgi-bin/musicu.fcg?data=' + urllib.parse.quote(json.dumps({
    'comm': {'ct': 19, 'cv': 1859},
    'req_1': {
        'module': 'music.musichallSong.PlayLyricInfo',
        'method': 'GetPlayLyricInfo',
        'param': {
            'songMID': mid,
            'songID': sid,
            'crypt': 1,
            'qrc': 1, 'qrc_t': 0,
            'trans': 1, 'trans_t': 0,
            'roma': 1, 'roma_t': 0,
        }
    }
}))

req = urllib.request.Request(url, headers={'Referer': 'https://y.qq.com/', 'User-Agent': 'Mozilla/5.0'})
resp = urllib.request.urlopen(req, timeout=30)
data = json.loads(resp.read())
d = data.get('req_1', {}).get('data', {})

trans_hex = d.get('trans', '')
lyric_hex = d.get('lyric', '')

print(f"=== {name} ===")
print(f"lyric hex 长度: {len(lyric_hex)} chars = {len(lyric_hex)//2} bytes")
print(f"trans hex 长度: {len(trans_hex)} chars = {len(trans_hex)//2} bytes")
print()

# 先解密 lyric 看看主歌词
print("--- 主歌词 (lyric) ---")
lyric_bytes = bytes.fromhex(lyric_hex)
print(f"块数: {len(lyric_bytes)} bytes, 8字节对齐: {len(lyric_bytes) % 8 == 0}")
sched = tripledes_key_setup(_QRC_3DES_KEY, DECRYPT)
chunks = [tripledes_crypt(bytearray(lyric_bytes[i:i+8]), sched) for i in range(0, len(lyric_bytes), 8)]
dec = b"".join(chunks)
try:
    main_text = zlib.decompress(dec).decode('utf-8')
    print(f"✅ 主歌词解密成功: {len(main_text)} 字符")
    print(f"预览: {main_text[:300]}")
except Exception as e:
    print(f"❌ 主歌词解压失败: {e}")
    print(f"首50字节: {dec[:50].hex()}")

print()

# 尝试解密 trans
print("--- 翻译 (trans) ---")
trans_bytes = bytes.fromhex(trans_hex)
print(f"块数: {len(trans_bytes)} bytes, 8字节对齐: {len(trans_bytes) % 8 == 0}")

# 尝试1: 标准的 3DES + Zlib
try:
    chunks = [tripledes_crypt(bytearray(trans_bytes[i:i+8]), sched) for i in range(0, len(trans_bytes), 8)]
    dec = b"".join(chunks)
    print(f"3DES 后: {len(dec)} bytes, 首字节=0x{dec[0]:02X}")

    if dec[0] == 0x78:
        trans_text = zlib.decompress(dec).decode('utf-8')
        print(f"✅ 3DES+zlib 解密成功: {len(trans_text)} 字符")
        print(f"翻译内容: {trans_text[:500]}")
    else:
        print(f"首字节不是 zlib 头 (0x78)，尝试 Base64...")
        # 可能不是 zlib，试试 base64
        try:
            trans_text = base64.b64decode(dec).decode('utf-8')
            print(f"✅ 3DES+Base64 解密成功: {trans_text[:300]}")
        except:
            print(f"Base64 也不对，尝试 raw deflate...")
            try:
                trans_text = zlib.decompress(dec, -zlib.MAX_WBITS).decode('utf-8')
                print(f"✅ 3DES+raw deflate 解密成功: {trans_text[:300]}")
            except Exception as e2:
                print(f"❌ 所有解密尝试失败: {e2}")
except Exception as e:
    print(f"❌ 3DES 失败: {e}")

# 尝试2: 直接 base64
print("\n尝试直接 Base64...")
try:
    trans_text = base64.b64decode(trans_hex).decode('utf-8')
    print(f"✅ Base64 (不经过 3DES) 成功: {trans_text[:200]}")
except Exception as e:
    print(f"❌ 直接 Base64 失败: {e}")

# 尝试3: 看长度是否 8 的倍数
print(f"\ntrans bytes 长度 = {len(trans_bytes)}")
print(f"是否是 8 的倍数: {len(trans_bytes) % 8 == 0}")
