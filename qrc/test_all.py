#!/usr/bin/env python3
"""测试 lyric_download.fcg 是否有翻译, 以及不同歌曲 + crypt:0"""
import urllib.request, json, sys, base64, re

# 用 songID 测试 lyric_download.fcg
song_id = 107192078  # 第一首歌的数字ID

# ---- 测试1: lyric_download.fcg ----
print("=" * 60)
print(f"1. lyric_download.fcg (songID={song_id})")
print("=" * 60)
url = "https://c.y.qq.com/qqmusic/fcgi-bin/lyric_download.fcg"
data = urllib.parse.urlencode({
    'version': '15',
    'miniversion': '82',
    'lrctype': '4',
    'musicid': song_id,
}).encode()
req = urllib.request.Request(url, data=data, headers={
    'Referer': 'https://y.qq.com/',
    'User-Agent': 'Mozilla/5.0',
})
resp = urllib.request.urlopen(req, timeout=30)
body = resp.read().decode('utf-8').strip()
# Strip comments
if body.startswith('<!--'):
    body = body[4:]
if body.endswith('-->'):
    body = body[:-3]
body = body.strip()

# Extract CDATA blocks
for tag in ['content', 'contentts', 'contentroma']:
    m = re.search(f'<{tag}\\b[^>]*><!\\[CDATA\\[(.*?)]]></{tag}>', body, re.DOTALL)
    if m:
        val = m.group(1)
        print(f"  <{tag}>: {len(val)} 字符, 前50={val[:50]}")
    else:
        print(f"  <{tag}>: ❌ 不存在")

# ---- 测试2: crypt:0 试试 ----
print("\n" + "=" * 60)
print("2. musicu.fcg with crypt:0 (明文)")
print("=" * 60)
for mid, name in [("003OUlho2HcRHC", "告白气球"), ("0039MnYb0qxYhV", "青花瓷"), ("004Z8Ihr0JIu5d", "七里香")]:
    url = 'https://u.y.qq.com/cgi-bin/musicu.fcg?data=' + urllib.parse.quote(json.dumps({
        'comm': {'ct': 19, 'cv': 1859},
        'req_1': {
            'module': 'music.musichallSong.PlayLyricInfo',
            'method': 'GetPlayLyricInfo',
            'param': {
                'songMID': mid,
                'songID': 0,
                'crypt': 0,
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
    t = d.get('trans', 'MISSING')
    r = d.get('roma', 'MISSING')
    ly = d.get('lyric', 'MISSING')
    print(f"  {name:10s} | lyric={repr(ly[:60]) if isinstance(ly,str) else ly} | trans={repr(t[:60]) if isinstance(t,str) and t else t} | roma={repr(r[:60]) if isinstance(r,str) and r else r}")

# ---- 测试3: musicu.fcg with lrc trans instead of qrc trans ----
print("\n" + "=" * 60)
print("3. musicu.fcg with lrc trans (crypt:1)")
print("=" * 60)
for mid, name in [("003OUlho2HcRHC", "告白气球"), ("0039MnYb0qxYhV", "青花瓷")]:
    url = 'https://u.y.qq.com/cgi-bin/musicu.fcg?data=' + urllib.parse.quote(json.dumps({
        'comm': {'ct': 19, 'cv': 1859},
        'req_1': {
            'module': 'music.musichallSong.PlayLyricInfo',
            'method': 'GetPlayLyricInfo',
            'param': {
                'songMID': mid,
                'songID': 0,
                'crypt': 1,
                'qrc': 0,  # 不要 QRC
                'lrc_t': 0,
                'trans': 1, 'trans_t': 0,
                'roma': 1, 'roma_t': 0,
            }
        }
    }))
    req = urllib.request.Request(url, headers={'Referer': 'https://y.qq.com/', 'User-Agent': 'Mozilla/5.0'})
    resp = urllib.request.urlopen(req, timeout=30)
    data = json.loads(resp.read())
    d = data.get('req_1', {}).get('data', {})
    t = d.get('trans', 'MISSING')
    r = d.get('roma', 'MISSING')
    ly = d.get('lyric', 'MISSING')
    # 显示所有非空字符串字段
    present = [(k, len(v) if isinstance(v, str) else 0) for k, v in d.items() if isinstance(v, str) and v and v != '0']
    print(f"  {name:10s} | lyric={repr(ly[:60]) if isinstance(ly,str) and ly else ly} | trans={repr(t[:60]) if isinstance(t,str) and t else t} | roma={repr(r[:60]) if isinstance(r,str) and r else r} | nonempty={present}")
