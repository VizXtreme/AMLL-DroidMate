#!/usr/bin/env python3
"""测试多种参数组合看 translation 在哪些条件下返回"""
import urllib.request, json, sys

mid = sys.argv[1] if len(sys.argv) > 1 else "003OUlho2HcRHC"
sid = int(sys.argv[2]) if len(sys.argv) > 2 else 107192078

tests = [
    # (名称, param_dict)
    ("MID + qrc:1 + trans:1", {
        'songMID': mid, 'songID': 0,
        'crypt': 1,
        'qrc': 1, 'qrc_t': 0,
        'trans': 1, 'trans_t': 0,
        'roma': 1, 'roma_t': 0,
    }),
    ("ID  + qrc:1 + trans:1 (w/o songMID)", {
        'songID': sid, 'songMID': '',
        'crypt': 1,
        'qrc': 1, 'qrc_t': 0,
        'trans': 1, 'trans_t': 0,
        'roma': 1, 'roma_t': 0,
    }),
    ("ID  + lrc:1 + trans:1 (no qrc)", {
        'songID': sid, 'songMID': '',
        'crypt': 1,
        'lrc_t': 0,
        'trans': 1, 'trans_t': 0,
        'roma': 1, 'roma_t': 0,
    }),
    ("MID + qrc:1 + lrc_t:1 + trans:1", {
        'songMID': mid, 'songID': 0,
        'crypt': 1,
        'qrc': 1, 'qrc_t': 0,
        'lrc_t': 1,
        'trans': 1, 'trans_t': 0,
        'roma': 1, 'roma_t': 0,
    }),
    ("MID + qrc:1 + trans:1 + trans_t:1", {
        'songMID': mid, 'songID': 0,
        'crypt': 1,
        'qrc': 1, 'qrc_t': 0,
        'trans': 1, 'trans_t': 1,
        'roma': 1, 'roma_t': 1,
    }),
]

for name, params in tests:
    url = 'https://u.y.qq.com/cgi-bin/musicu.fcg?data=' + urllib.parse.quote(json.dumps({
        'comm': {'ct': 19, 'cv': 1859},
        'req_1': {
            'module': 'music.musichallSong.PlayLyricInfo',
            'method': 'GetPlayLyricInfo',
            'param': params,
        }
    }))
    req = urllib.request.Request(url, headers={'Referer': 'https://y.qq.com/', 'User-Agent': 'Mozilla/5.0'})
    resp = urllib.request.urlopen(req, timeout=30)
    data = json.loads(resp.read())
    d = data.get('req_1', {}).get('data', {})

    t = d.get('trans', 'MISSING')
    r = d.get('roma', 'MISSING')
    l = d.get('lyric', 'MISSING')

    # 检查 trans/roma 是否有实际内容
    t_status = f"len={len(t)}" if isinstance(t, str) and len(t) > 3 else repr(t)
    r_status = f"len={len(r)}" if isinstance(r, str) and len(r) > 3 else repr(r)
    l_status = f"len={len(l)}" if isinstance(l, str) and len(l) > 3 else repr(l)

    # 打印返回的所有非空字段
    present = [k for k, v in d.items() if isinstance(v, str) and len(v) > 3]

    print(f"[{name:40s}] lyric={l_status:15s} | trans={t_status:15s} | roma={r_status:15s} | fields={present}")
