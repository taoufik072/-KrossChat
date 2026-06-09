#!/usr/bin/env python3
"""Generate schema.svg — run once, then commit the output."""

TW, RH, HH, P = 300, 24, 38, 10
DRH = 20   # DAO method row height
DAH = 26   # DAO section divider height

TABLES = [
    {
        'id': 'chat', 'name': 'ChatEntity', 'is_view': False, 'x': 60, 'y': 80,
        'cols': [
            {'n': 'chatId',         't': 'String', 'pk': True,  'fk': False},
            {'n': 'lastActivityAt', 't': 'Long',   'pk': False, 'fk': False},
        ],
        'dao': 'ChatDao',
        'dao_methods': [
            'upsertChat(chat)',
            'upsertChats(chats)',
            'deleteChatById(chatId)',
            'deleteAllChats()',
            'deleteChatsByIds(ids)',
            'getAllChatIds()',
            'getChatCount()',
            'getChatById(chatId)',
            'getChatsWithParticipants()',
            'getActiveParticipants(chatId)',
            'getChatInfoById(chatId)',
            'upsertChatWithParticipants()',
            'upsertChatsWithParticipants()',
        ],
    },
    {
        'id': 'participant', 'name': 'ParticipantEntity', 'is_view': False, 'x': 780, 'y': 80,
        'cols': [
            {'n': 'userId',            't': 'String',  'pk': True,  'fk': False},
            {'n': 'username',          't': 'String',  'pk': False, 'fk': False},
            {'n': 'profilePictureUrl', 't': 'String?', 'pk': False, 'fk': False},
        ],
        'dao': 'ParticipantDao',
        'dao_methods': [
            'upsertParticipant(participant)',
            'upsertParticipants(participants)',
            'getAllParticipants()',
        ],
    },
    {
        'id': 'join', 'name': 'ChatParticipantJoin', 'is_view': False, 'x': 420, 'y': 80,
        'cols': [
            {'n': 'chatId',   't': 'String',  'pk': True,  'fk': True},
            {'n': 'userId',   't': 'String',  'pk': True,  'fk': True},
            {'n': 'isActive', 't': 'Boolean', 'pk': False, 'fk': False},
        ],
        'dao': 'ChatParticipantsJoinDao',
        'dao_methods': [
            'upsertCrossRefs(crossRefs)',
            'getActiveParticipantIdsByChat(chatId)',
            'getAllParticipantIdsByChat(chatId)',
            'markParticipantsAsInactive(chatId, ids)',
            'reactivateParticipants(chatId, ids)',
            'syncChatParticipants(chatId, participants)',
        ],
    },
    {
        'id': 'message', 'name': 'MessageEntity', 'is_view': False, 'x': 60, 'y': 520,
        'cols': [
            {'n': 'messageId',               't': 'String', 'pk': True,  'fk': False},
            {'n': 'chatId',                  't': 'String', 'pk': False, 'fk': True},
            {'n': 'senderId',                't': 'String', 'pk': False, 'fk': True},
            {'n': 'content',                 't': 'String', 'pk': False, 'fk': False},
            {'n': 'timestamp',               't': 'Long',   'pk': False, 'fk': False},
            {'n': 'deliveryStatus',          't': 'String', 'pk': False, 'fk': False},
            {'n': 'deliveryStatusTimestamp', 't': 'Long',   'pk': False, 'fk': False},
        ],
        'dao': 'MessageDao',
        'dao_methods': [
            'upsertMessage(message)',
            'upsertMessages(messages)',
            'deleteMessageById(messageId)',
            'deleteMessageById(messageIds)',
            'getMessagesByChatId(chatId)',
            'getMessageById(messageId)',
        ],
    },
    {
        'id': 'lastmsg', 'name': 'last_message_view_per_chat', 'is_view': True, 'x': 780, 'y': 330,
        'cols': [
            {'n': 'messageId',      't': 'String', 'pk': False, 'fk': False},
            {'n': 'chatId',         't': 'String', 'pk': False, 'fk': False},
            {'n': 'senderId',       't': 'String', 'pk': False, 'fk': False},
            {'n': 'content',        't': 'String', 'pk': False, 'fk': False},
            {'n': 'timestamp',      't': 'Long',   'pk': False, 'fk': False},
            {'n': 'deliveryStatus', 't': 'String', 'pk': False, 'fk': False},
        ],
        'dao': None,
        'dao_methods': [],
    },
]

# (fromId, fromEdge, toId, toEdge, fromCard, toCard, isView, customPath, fromLabel, toLabel)
RELS = [
    ('chat',        'right',  'join',    'left',  '1', 'N',    False, None,                                                                           None,       None),
    ('participant', 'left',   'join',    'right', '1', 'N',    False, None,                                                                           None,       None),
    ('chat',        'bottom', 'message', 'top',   '1', 'N',    False, None,                                                                           None,       None),
    ('participant', 'bottom', 'message', 'right', '1', 'N',    False, 'M930,276 L930,308 L760,308 L760,500 L400,500 L400,696 L360,696',               (930, 290), (372, 700)),
    ('chat',        'right',  'lastmsg', 'left',  '1', '0..1', True,  'M360,266 L390,266 L390,380 L760,380 L760,421 L780,421',                        (372, 258), (768, 425)),
]

# ── helpers ───────────────────────────────────────────────────────────────────

def th(t):
    col_h = HH + len(t['cols']) * RH
    if t.get('dao_methods'):
        return col_h + DAH + len(t['dao_methods']) * DRH
    return col_h

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

off = {'top': (0, -8), 'bottom': (0, 14), 'left': (-12, 4), 'right': (12, 4)}

for fid, fe, tid, te, fc, tc, is_view, custom_path, fl, tl in RELS:
    ft, tt = by_id(fid), by_id(tid)
    fx, fy = ep(ft, fe)
    tx, ty = ep(tt, te)
    col    = '#28a566' if is_view else '#4a5568'
    marker = 'aVw'     if is_view else 'aFk'
    dash   = ' stroke-dasharray="6,3"' if is_view else ''

    if custom_path:
        d = custom_path
        flx, fly = fl
        tlx, tly = tl
    else:
        if fe == 'right' and te == 'left':
            mx = (fx + tx) // 2
            d = f'M{fx},{fy} L{mx},{fy} L{mx},{ty} L{tx},{ty}'
        elif fe == 'left' and te == 'right':
            mx = (fx + tx) // 2
            d = f'M{fx},{fy} L{mx},{fy} L{mx},{ty} L{tx},{ty}'
        elif fe == 'bottom' and te == 'top':
            d = f'M{fx},{fy} L{tx},{ty}'
        else:
            d = f'M{fx},{fy} L{tx},{ty}'
        fox, foy = off[fe]
        tox, toy = off[te]
        flx, fly = fx + fox, fy + foy
        tlx, tly = tx + tox, ty + toy

    e(f'<path d="{d}" stroke="{col}" stroke-width="1.5" fill="none"'
      f'{dash} marker-end="url(#{marker})"/>')

    for x, y, card in [(flx, fly, fc), (tlx, tly, tc)]:
        e(f'<text x="{x}" y="{y}" fill="{col}" font-size="11" font-family="monospace"'
          f' font-weight="700" text-anchor="middle">{card}</text>')

# ── tables ────────────────────────────────────────────────────────────────────

for t in TABLES:
    h             = th(t)
    cid           = f'c{t["id"]}'
    col_section_h = HH + len(t['cols']) * RH

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
    has_dao = bool(t.get('dao_methods'))
    for i, col in enumerate(t['cols']):
        ry = t['y'] + HH + i * RH
        if col['pk']:
            rf = '#211d00'
        elif col['fk']:
            rf = '#001428'
        else:
            rf = '#1a1f2e' if i % 2 == 0 else '#1e2433'

        is_last_col = (i == len(t['cols']) - 1) and not has_dao
        clip = f' clip-path="url(#{cid})"' if is_last_col else ''
        e(f'<rect x="{t["x"]+1}" y="{ry}" width="{TW-2}" height="{RH}" fill="{rf}"{clip}/>')
        if i > 0:
            e(f'<line x1="{t["x"]+1}" y1="{ry}" x2="{t["x"]+TW-1}" y2="{ry}"'
              f' stroke="#30363d" stroke-width="0.5" opacity="0.5"/>')

        badge = bbg = bfg = None; bw = 22
        if col['pk'] and col['fk']:   badge, bbg, bfg, bw = 'PK·FK', '#3a2a00', '#e3b341', 38
        elif col['pk']:               badge, bbg, bfg      = 'PK',    '#5a4200', '#e3b341'
        elif col['fk']:               badge, bbg, bfg      = 'FK',    '#0d3a6a', '#79c0ff'

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

    # DAO section
    if has_dao:
        dao_y = t['y'] + col_section_h
        # divider bg
        e(f'<rect x="{t["x"]+1}" y="{dao_y}" width="{TW-2}" height="{DAH}"'
          f' fill="#0a1a1a"/>')
        e(f'<line x1="{t["x"]}" y1="{dao_y}" x2="{t["x"]+TW}" y2="{dao_y}"'
          f' stroke="#1e4040" stroke-width="1"/>')
        # DAO label
        e(f'<text x="{t["x"]+P}" y="{dao_y+DAH//2+4}" fill="#3fb980" font-size="9.5"'
          f' font-family="monospace" font-weight="700">DAO: {t["dao"]}</text>')
        # method rows
        for j, method in enumerate(t['dao_methods']):
            my  = dao_y + DAH + j * DRH
            mrf = '#111820' if j % 2 == 0 else '#131c28'
            is_last_row = (j == len(t['dao_methods']) - 1)
            clip = f' clip-path="url(#{cid})"' if is_last_row else ''
            e(f'<rect x="{t["x"]+1}" y="{my}" width="{TW-2}" height="{DRH}" fill="{mrf}"{clip}/>')
            if j > 0:
                e(f'<line x1="{t["x"]+1}" y1="{my}" x2="{t["x"]+TW-1}" y2="{my}"'
                  f' stroke="#1e2433" stroke-width="0.4" opacity="0.4"/>')
            mty = my + DRH // 2 + 4
            e(f'<text x="{t["x"]+P}" y="{mty}" fill="#8b949e" font-size="9.5"'
              f' font-family="monospace">{method}</text>')

    # outer border
    bc = '#1a5e3c' if t['is_view'] else '#3d4e63'
    e(f'<rect x="{t["x"]}" y="{t["y"]}" width="{TW}" height="{h}"'
      f' rx="8" fill="none" stroke="{bc}" stroke-width="1"/>')

# ── legend ────────────────────────────────────────────────────────────────────

lx, ly = 20, H - 26
items = [('PK', '#5a4200', '#e3b341', 22), ('FK', '#0d3a6a', '#79c0ff', 22), ('PK·FK', '#3a2a00', '#e3b341', 38)]
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
print(f'Written: {out_path}  ({W}×{H})')
