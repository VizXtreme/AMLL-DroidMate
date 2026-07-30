#!/usr/bin/env python3
"""
测试 QQ Music API: 添加 trans: 1 和 roma: 1 后能否获取翻译和音译
"""
import urllib.request, json, sys

def test_with_trans(song_mid="003OUlho2HcRHC"):
    url = 'https://u.y.qq.com/cgi-bin/musicu.fcg?data=' + urllib.parse.quote(json.dumps({
        'comm': {'ct': 19, 'cv': 1859},
        'req_1': {
            'module': 'music.musichallSong.PlayLyricInfo',
            'method': 'GetPlayLyricInfo',
            'param': {
                'songMID': song_mid,
                'songID': 0,
                'crypt': 1,
                'qrc': 1,       # QRC 逐字歌词
                'qrc_t': 0,
                'trans': 1,     # 🔵 关键改动：启用翻译
                'trans_t': 0,   # 翻译格式（0=原文格式）
                'roma': 1,      # 🔵 关键改动：启用音译/罗马音
                'roma_t': 0,
            }
        }
    }))
    req = urllib.request.Request(url, headers={'Referer': 'https://y.qq.com/', 'User-Agent': 'Mozilla/5.0'})
    resp = urllib.request.urlopen(req, timeout=30)
    data = json.loads(resp.read())
    req1 = data.get('req_1', {}).get('data', {})

    print("=" * 60)
    print(f"歌曲 MID: {song_mid}")
    print("=" * 60)

    # 检查返回的字段
    for key in ['lyric', 'trans', 'roma', 'qrc', 'qrc_t', 'crypt']:
        val = req1.get(key)
        if val:
            print(f"  {key}: 存在, 长度={len(str(val))}, 预览={str(val)[:60]}")
        else:
            print(f"  {key}: 缺失/空")

    # 如果有 trans，尝试解码
    trans_raw = req1.get('trans')
    if trans_raw and len(str(trans_raw)) > 5:
        print("\n--- trans (翻译) ---")
        try:
            decoded = urllib.parse.unquote(trans_raw) if '%' in str(trans_raw) else trans_raw
            # 尝试 Base64 解码
            import base64
            try:
                decoded_bytes = base64.b64decode(decoded)
                decoded_text = decoded_bytes.decode('utf-8')
                print(f"  Base64 解码成功: {len(decoded_text)} 字符")
                for line in decoded_text.split('\n')[:10]:
                    if line.strip():
                        print(f"    {line}")
            except:
                print(f"  Base64 解码失败，原始内容: {str(trans_raw)[:100]}")
        except Exception as e:
            print(f"  解码失败: {e}")
    else:
        print("\n⚠️  没有返回翻译内容!")

    # 如果有 roma，尝试解码
    roma_raw = req1.get('roma')
    if roma_raw and len(str(roma_raw)) > 5:
        print("\n--- roma (音译) ---")
        try:
            import base64
            decoded_bytes = base64.b64decode(roma_raw)
            decoded_text = decoded_bytes.decode('utf-8')
            print(f"  Base64 解码成功: {len(decoded_text)} 字符")
            for line in decoded_text.split('\n')[:10]:
                if line.strip():
                    print(f"    {line}")
        except Exception as e:
            print(f"  解码失败: {e}")
    else:
        print("\n⚠️  没有返回音译内容!")

if __name__ == '__main__':
    # 参数 1: songMID
    mid = sys.argv[1] if len(sys.argv) > 1 else "003OUlho2HcRHC"
    test_with_trans(mid)
