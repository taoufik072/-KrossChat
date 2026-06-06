#!/usr/bin/env python3
"""Generate schema.svg — run once, then commit the output."""

TW, RH, HH, P = 240, 26, 38, 10

TABLES = [
    {'id': 'chat',        'name': 'ChatEntity',                'is_view': False, 'x': 60,  'y': 80,
     'cols': [
         {'n': 'chatId',                   't': 'String',  'pk': True,  'fk': False},
         {'n': 'lastActivityAt',           't': 'Long',    'pk': False, 'fk': False},
     ]},
    {'id': 'participant', 'name': 'ChatParticipantEntity',     'is_view': False, 'x': 820, 'y': 80,
     'cols': [
         {'n': 'userId',                   't': 'String',  'pk': True,  'fk': False},
         {'n': 'username',                 't': 'String',  'pk': False, 'fk': False},
         {'n': 'profilePictureUrl',        't': 'String?', 'pk': False, 'fk': False},
     ]},
    {'id': 'crossref',    'name': 'ChatParticipantCrossRef',   'is_view': False, 'x': 440, 'y': 310,
     'cols': [
         {'n': 'chatId',                   't': 'String',  'pk': True,  'fk': True},
         {'n': 'userId',                   't': 'String',  'pk': True,  'fk': True},
         {'n': 'isActive',                 't': 'Boolean', 'pk': False, 'fk': False},
     ]},
    {'id': 'message',     'name': 'ChatMessageEntity',         'is_view': False, 'x': 60,  'y': 510,
     'cols': [
         {'n': 'messageId',                't': 'String',  'pk': True,  'fk': False},
         {'n': 'chatId',                   't': 'String',  'pk': False, 'fk': True},
         {'n': 'senderId',                 't': 'String',  'pk': False, 'fk': True},
         {'n': 'content',                  't': 'String',  'pk': False, 'fk': False},
         {'n': 'timestamp',                't': 'Long',    'pk': False, 'fk': False},
         {'n': 'deliveryStatus',           't': 'String',  'pk': False, 'fk': False},
         {'n': 'deliveryStatusTimestamp',  't': 'Long',    'pk': False, 'fk': False},
     ]},
    {'id': 'lastmsg',     'name': 'last_message_view_per_chat','is_view': True,  'x': 820, 'y': 510,
     'cols': [
         {'n': 'messageId',  't': 'String', 'pk': False, 'fk': False},
         {'n': 'chatId',     't': 'String', 'pk': False, 'fk': False},
         {'n': 'senderId',   't': 'String', 'pk': False, 'fk': False},
         {'n': 'content',    't': 'String', 'pk': False, 'fk': False},
         {'n': 'timestamp',  't': 'Long',   'pk': False, 'fk': False},
     ]},
]

# (fromId, fromEdge, toId, toEdge, fromCard, toCard, isView)
RELS = [
    ('chat',        'right',  'crossref', 'left',   '1', 'N',    False),
    ('participant', 'left',   'crossref', 'right',  '1', 'N',    False),
    ('chat',        'bottom', 'message',  'top',    '1', 'N',    False),
    ('participant', 'bottom', 'message',  'right',  '1', 'N',    False),
    ('chat',        'right',  'lastmsg',  'left',   '1', '0..1', True),
]

# ── helpers ──────────────────────────────────────────────────────────────────

def th(t):
    return HH + len(t['cols']) * RH

def ep(t, edge):
    h = th(t)
    if edge == 'top':    return (t['x'] + TW // 2, t['y'])
    if edge == 'bottom': return (t['x'] + TW // 2, t['y'] + h)
    if edge == 'left':   return (t['x'],            t['y'] + h // 2)
    if edge == 'right':  return (t['x'] + TW,       t['y'] + h // 2)

def by_id(tid):
    return next(t for t in TABLES if t['id'] == tid)

W = max(t['x'] + TW for t in TABLES) + 80
H = max(t['y'] + th(t) for t in TABLES) + 60

out = []
e = out.append

# ── SVG open + defs ───────────────────────────────────────────────────────────

e(f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}">')
e('<defs>')
for color, mid in [('#4a5568', 'aFk'), ('#28a566', 'aVw')]:
    e(f'  <marker id="{mid}" markerWidth="8" markerHeight="8" refX="7" refY="3" orient="auto">'
      f'<path d="M0,0 L0,6 L8,3 z" fill="{color}"/></marker>')
for t in TABLES:
    e(f'  <clipPath id="c{t["id"]}">'
      f'<rect x="{t["x"]}" y="{t["y"]}" width="{TW}" height="{th(t)}" rx="8"/>'
      f'</clipPath>')
e('</defs>')

# ── background ────────────────────────────────────────────────────────────────

e(f'<rect width="{W}" height="{H}" fill="#161b22"/>')

# ── relationships ─────────────────────────────────────────────────────────────

for fid, fe, tid, te, fc, tc, is_view in RELS:
    ft, tt = by_id(fid), by_id(tid)
    fx, fy = ep(ft, fe)
    tx, ty = ep(tt, te)
    col    = '#28a566' if is_view else '#4a5568'
    marker = 'aVw'     if is_view else 'aFk'
    dash   = ' stroke-dasharray="6,3"' if is_view else ''

    if fe == 'right' and te == 'left':
        if is_view:
            d = f'M{fx},{fy} L760,{fy} L760,{ty} L{tx},{ty}'
        else:
            mx = (fx + tx) // 2
            d = f'M{fx},{fy} L{mx},{fy} L{mx},{ty} L{tx},{ty}'
    elif fe == 'left' and te == 'right':
        mx = (fx + tx) // 2
        d = f'M{fx},{fy} L{mx},{fy} L{mx},{ty} L{tx},{ty}'
    elif fe == 'bottom' and te == 'top':
        d = f'M{fx},{fy} L{tx},{ty}'
    elif fe == 'bottom' and te == 'right':
        d = f'M{fx},{fy} L{fx},460 L{tx+12},460 L{tx+12},{ty} L{tx},{ty}'
    else:
        d = f'M{fx},{fy} L{tx},{ty}'

    e(f'<path d="{d}" stroke="{col}" stroke-width="1.5" fill="none"'
      f'{dash} marker-end="url(#{marker})"/>')

    off = {'top':(0,-8),'bottom':(0,14),'left':(-12,4),'right':(12,4)}
    fox, foy = off[fe]; tox, toy = off[te]
    for x, y, card in [(fx+fox, fy+foy, fc), (tx+tox, ty+toy, tc)]:
        e(f'<text x="{x}" y="{y}" fill="{col}" font-size="11" font-family="monospace"'
          f' font-weight="700" text-anchor="middle">{card}</text>')

# ── tables ────────────────────────────────────────────────────────────────────

for t in TABLES:
    h   = th(t)
    cid = f'c{t["id"]}'

    # shadow
    e(f'<rect x="{t["x"]+3}" y="{t["y"]+4}" width="{TW}" height="{h}"'
      f' rx="8" fill="#000000" fill-opacity="0.4"/>')
    # body
    e(f'<rect x="{t["x"]}" y="{t["y"]}" width="{TW}" height="{h}"'
      f' rx="8" fill="#1a1f2e" stroke="#30363d" stroke-width="1"/>')
    # header bg
    hc = '#0d2e1e' if t['is_view'] else '#0d2644'
    e(f'<rect x="{t["x"]}" y="{t["y"]}" width="{TW}" height="{HH}"'
      f' fill="{hc}" clip-path="url(#{cid})"/>')
    # header text
    e(f'<text x="{t["x"]+TW//2}" y="{t["y"]+HH//2+5}" fill="#e6edf3"'
      f' font-size="13" font-family="system-ui, sans-serif"'
      f' font-weight="600" text-anchor="middle">{t["name"]}</text>')
    # VIEW badge
    if t['is_view']:
        bx = t['x'] + TW - 42; by = t['y'] + (HH - 14) // 2
        e(f'<rect x="{bx}" y="{by}" width="34" height="14" rx="3" fill="#0f4a35"/>')
        e(f'<text x="{bx+17}" y="{by+11}" fill="#3fb980" font-size="8"'
          f' font-family="monospace" font-weight="700" text-anchor="middle">VIEW</text>')
    # header divider
    e(f'<line x1="{t["x"]}" y1="{t["y"]+HH}" x2="{t["x"]+TW}" y2="{t["y"]+HH}"'
      f' stroke="#30363d" stroke-width="1"/>')

    # columns
    for i, col in enumerate(t['cols']):
        ry = t['y'] + HH + i * RH
        if col['pk'] or (col['pk'] and col['fk']):
            rf = '#211d00'
        elif col['fk']:
            rf = '#001428'
        else:
            rf = '#1a1f2e' if i % 2 == 0 else '#1e2433'

        clip = f' clip-path="url(#{cid})"' if i == len(t['cols']) - 1 else ''
        e(f'<rect x="{t["x"]+1}" y="{ry}" width="{TW-2}" height="{RH}" fill="{rf}"{clip}/>')
        if i > 0:
            e(f'<line x1="{t["x"]+1}" y1="{ry}" x2="{t["x"]+TW-1}" y2="{ry}"'
              f' stroke="#30363d" stroke-width="0.5" opacity="0.5"/>')

        badge = bbg = bfg = None; bw = 22
        if col['pk'] and col['fk']:   badge, bbg, bfg, bw = 'PK·FK','#3a2a00','#e3b341', 38
        elif col['pk']:               badge, bbg, bfg      = 'PK',   '#5a4200','#e3b341'
        elif col['fk']:               badge, bbg, bfg      = 'FK',   '#0d3a6a','#79c0ff'

        ox = 0
        if badge:
            bx2 = t['x'] + P; by2 = ry + (RH - 14) // 2
            e(f'<rect x="{bx2}" y="{by2}" width="{bw}" height="14" rx="3" fill="{bbg}"/>')
            e(f'<text x="{bx2+bw//2}" y="{by2+10}" fill="{bfg}" font-size="8"'
              f' font-family="monospace" font-weight="700" text-anchor="middle">{badge}</text>')
            ox = bw + 6

        ty3 = ry + RH // 2 + 4
        nf  = '#e3b341' if col['pk'] else '#58a6ff' if col['fk'] else '#cdd9e5'
        fw  = '600' if col['pk'] else '400'
        e(f'<text x="{t["x"]+P+ox}" y="{ty3}" fill="{nf}" font-size="11.5"'
          f' font-family="system-ui, monospace, sans-serif" font-weight="{fw}">{col["n"]}</text>')
        e(f'<text x="{t["x"]+TW-P}" y="{ty3}" fill="#6e7681" font-size="10"'
          f' font-family="monospace" text-anchor="end">{col["t"]}</text>')

    # outer border
    bc = '#1a5e3c' if t['is_view'] else '#3d4e63'
    e(f'<rect x="{t["x"]}" y="{t["y"]}" width="{TW}" height="{h}"'
      f' rx="8" fill="none" stroke="{bc}" stroke-width="1"/>')

# ── legend ────────────────────────────────────────────────────────────────────

lx, ly = 20, H - 26
items = [('PK','#5a4200','#e3b341',22), ('FK','#0d3a6a','#79c0ff',22), ('PK·FK','#3a2a00','#e3b341',38)]
cx = lx
for label, bbg, bfg, bw in items:
    e(f'<rect x="{cx}" y="{ly-10}" width="{bw}" height="14" rx="3" fill="{bbg}"/>')
    e(f'<text x="{cx+bw//2}" y="{ly+1}" fill="{bfg}" font-size="8"'
      f' font-family="monospace" font-weight="700" text-anchor="middle">{label}</text>')
    cx += bw + 6
e(f'<text x="{cx+4}" y="{ly+1}" fill="#6e7681" font-size="10" font-family="system-ui, sans-serif">'
  f'solid = FK relation · dashed = DB view</text>')

e('</svg>')

# ── write file ────────────────────────────────────────────────────────────────

import os
out_path = os.path.join(os.path.dirname(__file__), 'schema.svg')
with open(out_path, 'w', encoding='utf-8') as f:
    f.write('\n'.join(out))
print(f'Written: {out_path}')
