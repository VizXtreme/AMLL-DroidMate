#!/usr/bin/env python3
"""测试确实有翻译的歌曲"""
import urllib.request, json, base64, zlib

# 几首可能有中文翻译的日语/韩语歌
songs = [
    # (songMID, songID, 名称)
    ("004ad3Nr3HmN3L", 28181871, "宇多田光-First Love"),    # 日语
    ("003RMFKt1e32JG", 4991546, "米津玄師-Lemon"),          # 日语
    ("0025SzLt37Tv5m", 13870942, "邓紫棋-光年之外"),        # 中文歌(可能有英文翻译)
    ("001Rs8eF06acLx", 20785678, "周深-大鱼"),              # 中文
    ("003OUlho2HcRHC", 107192078, "周杰伦-告白气球"),        # 原测试(确认无翻译)
]

for mid, sid, name in songs:
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

    t = d.get('trans', '')
    r = d.get('roma', '')
    ly = d.get('lyric', '')

    # 尝试解密 trans 如果存在
    trans_decoded = ''
    if t and len(t) > 3 and t != '0':
        try:
            raw = bytes.fromhex(t)
            from tripledes import tripledes_key_setup, tripledes_crypt, DECRYPT
            key = b"!@#)(*$%123ZXC!@!@#)(NHL"
            sched = tripledes_key_setup(key, DECRYPT)
            chunks = [tripledes_crypt(bytearray(raw[i:i+8]), sched) for i in range(0, len(raw), 8)]
            dec = b"".join(chunks)
            trans_decoded = zlib.decompress(dec).decode('utf-8')[:100]
        except:
            try:
                trans_decoded = base64.b64decode(t).decode('utf-8')[:100]
            except:
                trans_decoded = f"<无法解码:{t[:40]}>"

    t_len = len(t) if isinstance(t, str) else 0
    r_len = len(r) if isinstance(r, str) else 0
    print(f"{name:25s} | trans_len={t_len:5d} | roma_len={r_len:5d} | trans_preview={trans_decoded[:80]}")
