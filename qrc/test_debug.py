#!/usr/bin/env python3
"""调试：打印 QQ Music API 返回的所有字段"""
import urllib.request, json, sys

mid = sys.argv[1] if len(sys.argv) > 1 else "003OUlho2HcRHC"

url = 'https://u.y.qq.com/cgi-bin/musicu.fcg?data=' + urllib.parse.quote(json.dumps({
    'comm': {'ct': 19, 'cv': 1859},
    'req_1': {
        'module': 'music.musichallSong.PlayLyricInfo',
        'method': 'GetPlayLyricInfo',
        'param': {
            'songMID': mid,
            'songID': 0,
            'crypt': 1,
            'lrc_t': 0,
            'qrc': 1,
            'qrc_t': 0,
            'trans': 1,   # 启用翻译
            'trans_t': 0,
            'roma': 1,    # 启用音译
            'roma_t': 0,
        }
    }
}))

req = urllib.request.Request(url, headers={'Referer': 'https://y.qq.com/', 'User-Agent': 'Mozilla/5.0'})
resp = urllib.request.urlopen(req, timeout=30)
data = json.loads(resp.read())

d = data.get('req_1', {}).get('data', {})
print(f"歌曲 MID: {mid}")
print(f"\n返回的所有字段 ({len(d)} 个):")
for k, v in d.items():
    if isinstance(v, str):
        print(f"  {k}: {len(v)} 字符, 前80={v[:80]}")
    else:
        print(f"  {k}: {repr(v)}")
