#!/usr/bin/env python3
"""
Generates every texture of the Balthorite Mod with Pillow (pip install pillow).

    python3 tools/generate_textures.py

Edit the palette / shapes below and re-run to restyle the mod.
Output goes to src/main/resources/assets/balthorite/textures/
"""
import os
import random
from PIL import Image

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..",
                    "src", "main", "resources", "assets", "balthorite", "textures")

# ---- Balthorite palette: dark blue, centred on the Lapis Lazuli colour ----
OUT = (0x0E, 0x1B, 0x48, 255)
DARK = (0x1B, 0x3A, 0x8C, 255)
MID = (0x2A, 0x55, 0xC4, 255)      # ~ lapis lazuli
LIGHT = (0x4B, 0x7B, 0xE8, 255)
HI = (0x9A, 0xBB, 0xFF, 255)


def save(img, *parts):
    path = os.path.join(ROOT, *parts)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)
    print("wrote", os.path.normpath(path))


def put(img, x, y, c):
    if 0 <= x < img.width and 0 <= y < img.height:
        img.putpixel((x, y), c)


def rect(img, x0, y0, x1, y1, c):
    """Filled rectangle, inclusive coordinates."""
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            put(img, x, y, c)


def frame(img, x0, y0, x1, y1, c):
    for x in range(x0, x1 + 1):
        put(img, x, y0, c)
        put(img, x, y1, c)
    for y in range(y0, y1 + 1):
        put(img, x0, y, c)
        put(img, x1, y, c)


def shade(c, d):
    return (max(0, min(255, c[0] + d)), max(0, min(255, c[1] + d)), max(0, min(255, c[2] + d)), c[3])


def outline(img, c):
    """1px outline drawn on transparent pixels that touch a filled pixel."""
    src = img.copy()
    for y in range(img.height):
        for x in range(img.width):
            if src.getpixel((x, y))[3] != 0:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < img.width and 0 <= ny < img.height and src.getpixel((nx, ny))[3] != 0:
                    img.putpixel((x, y), c)
                    break


# =====================================================================
# Ingot
# =====================================================================
def ingot_faces(ox, oy):
    """Returns {(x, y): 'top'|'front'|'side'} for an ingot seen from the top-front-right."""
    faces = {}
    for r in range(4):                       # top face (parallelogram leaning right)
        for x in range(3 - r, 13 - r):
            faces[(ox + x, oy + r)] = "top"
    for r in range(4, 8):                    # front face
        for x in range(0, 10):
            faces[(ox + x, oy + r)] = "front"
    for k in range(1, 4):                    # right face
        for r in range(4 - k, 8 - k):
            faces[(ox + 9 + k, oy + r)] = "side"
    return faces


def make_ingot():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    ox, oy = 1, 4
    faces = ingot_faces(ox, oy)
    for (x, y), kind in faces.items():
        ry = y - oy
        if kind == "top":
            c = HI if (ry == 0 or x == ox + 3 - ry) else LIGHT
        elif kind == "front":
            c = LIGHT if ry == 4 else MID if ry < 7 else DARK
        else:
            c = DARK if (x - ox) < 12 else shade(DARK, -12)
        put(img, x, y, c)
    # little sparkles
    put(img, ox + 5, oy + 1, HI)
    put(img, ox + 6, oy + 1, HI)
    put(img, ox + 2, oy + 5, HI)
    outline(img, OUT)
    save(img, "item", "balthorite_ingot.png")
    return img


# =====================================================================
# Upgrade template
# =====================================================================
def make_template():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    plate = (0x26, 0x2D, 0x4E, 255)
    edge = (0x4A, 0x5A, 0x98, 255)
    rect(img, 1, 1, 14, 14, plate)
    frame(img, 1, 1, 14, 14, edge)
    for corner in ((1, 1), (14, 1), (1, 14), (14, 14)):     # rounded corners
        put(img, corner[0], corner[1], (0, 0, 0, 0))
    # rivets
    for p in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, p[0], p[1], shade(edge, 25))
    # lapis crystal emblem
    for y in range(16):
        for x in range(16):
            d = abs(x - 7.5) + abs(y - 7.5)
            if d <= 1.5:
                put(img, x, y, HI)
            elif d <= 3.0:
                put(img, x, y, LIGHT)
            elif d <= 4.0:
                put(img, x, y, MID)
            elif d <= 5.0:
                put(img, x, y, DARK)
    outline(img, OUT)
    save(img, "item", "balthorite_upgrade_template.png")
    return img


# =====================================================================
# Alloy Forge block faces
# =====================================================================
def make_block_textures():
    rnd = random.Random(1337)
    metal = (0x2E, 0x34, 0x55, 255)
    rim_hi = (0x4A, 0x55, 0x8C, 255)
    rim_lo = (0x16, 0x19, 0x30, 255)

    def noisy(base, amt=5):
        return shade(base, rnd.randint(-amt, amt))

    # ---- side: riveted plating with a glowing vent ----
    side = Image.new("RGBA", (16, 16), (0, 0, 0, 255))
    for y in range(16):
        for x in range(16):
            put(side, x, y, noisy(metal))
    rect(side, 0, 0, 15, 1, rim_hi)          # top rim
    rect(side, 0, 14, 15, 15, rim_lo)        # bottom rim
    for x in range(16):
        put(side, x, 1, shade(rim_hi, -18))
        put(side, x, 14, shade(rim_lo, 12))
    frame(side, 0, 0, 15, 15, rim_lo)
    for p in ((2, 3), (13, 3), (2, 12), (13, 12)):          # rivets
        put(side, p[0], p[1], (0x8E, 0x9C, 0xD6, 255))
        put(side, p[0] + 1 if p[0] < 8 else p[0] - 1, p[1], rim_lo)
    # recessed vent + blue glow
    rect(side, 3, 5, 12, 10, (0x0B, 0x0E, 0x20, 255))
    frame(side, 3, 5, 12, 10, rim_lo)
    for x in range(4, 12):
        put(side, x, 6, shade(DARK, 0))
        put(side, x, 9, shade(DARK, 0))
        put(side, x, 7, LIGHT)
        put(side, x, 8, MID)
    for x in range(5, 11):
        put(side, x, 7, HI if x in (6, 9) else LIGHT)
    save(side, "block", "alloy_forge_side.png")

    # ---- top: dark rim around a molten-balthorite crucible ----
    top = Image.new("RGBA", (16, 16), (0, 0, 0, 255))
    for y in range(16):
        for x in range(16):
            put(top, x, y, noisy(metal))
    frame(top, 0, 0, 15, 15, rim_lo)
    frame(top, 1, 1, 14, 14, rim_hi)
    frame(top, 2, 2, 13, 13, rim_lo)
    for y in range(3, 13):
        for x in range(3, 13):
            d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if d <= 2.2:
                c = HI
            elif d <= 3.4:
                c = LIGHT
            elif d <= 4.6:
                c = MID
            else:
                c = (0x0F, 0x14, 0x30, 255)
            put(top, x, y, noisy(c, 4) if d > 4.6 else c)
    # a few glowing sparks
    for p in ((4, 4), (11, 5), (5, 11), (10, 10)):
        put(top, p[0], p[1], DARK)
    put(top, 7, 7, (0xDD, 0xEA, 0xFF, 255))
    save(top, "block", "alloy_forge_top.png")

    # ---- bottom ----
    bottom = Image.new("RGBA", (16, 16), (0, 0, 0, 255))
    for y in range(16):
        for x in range(16):
            put(bottom, x, y, noisy(shade(metal, -18), 4))
    frame(bottom, 0, 0, 15, 15, rim_lo)
    frame(bottom, 1, 1, 14, 14, shade(rim_lo, 8))
    save(bottom, "block", "alloy_forge_bottom.png")
    return side, top, bottom


# =====================================================================
# GUI (256x256 sheet; see AlloyForgeScreen for the layout)
# =====================================================================
def make_gui():
    rnd = random.Random(99)
    g = Image.new("RGBA", (256, 256), (0, 0, 0, 0))

    BG = (0x23, 0x28, 0x42, 255)
    EDGE = (0x08, 0x0A, 0x18, 255)
    BEVEL_HI = (0x46, 0x54, 0x8E, 255)
    BEVEL_LO = (0x14, 0x17, 0x2C, 255)
    WELL = (0x13, 0x17, 0x2E, 255)
    SLOT_FILL = (0x0D, 0x10, 0x23, 255)
    SLOT_DARK = (0x07, 0x09, 0x15, 255)
    SLOT_LIGHT = (0x3C, 0x49, 0x7C, 255)

    # --- main panel ---
    for y in range(166):
        for x in range(176):
            put(g, x, y, shade(BG, rnd.randint(-2, 2)))
    frame(g, 0, 0, 175, 165, EDGE)
    for x in range(1, 175):
        put(g, x, 1, BEVEL_HI)
        put(g, x, 164, BEVEL_LO)
    for y in range(1, 165):
        put(g, 1, y, BEVEL_HI)
        put(g, 174, y, BEVEL_LO)
    for p in ((0, 0), (175, 0), (0, 165), (175, 165)):       # rounded corners
        put(g, p[0], p[1], (0, 0, 0, 0))

    # --- machine well (y 17..70) ---
    rect(g, 5, 17, 170, 70, WELL)
    frame(g, 5, 17, 170, 70, DARK)
    for x in range(6, 170):
        put(g, x, 18, shade(WELL, -6))
    for x in range(6, 171):
        put(g, x, 70, MID)
    for y in range(18, 71):
        put(g, 170, y, MID)
    for p in ((8, 20), (166, 20), (8, 66), (166, 66)):       # rivets
        rect(g, p[0], p[1], p[0] + 1, p[1] + 1, BEVEL_HI)

    # faint lapis glow strip along the top of the well
    for x in range(8, 168):
        put(g, x, 19, shade(DARK, -20))

    # decorative frieze of lapis diamonds across the top of the well
    for i, cx in enumerate(range(16, 164, 12)):
        centre = (cx == 88)
        for dy in range(-3, 4):
            w = 3 - abs(dy)
            for dx in range(-w, w + 1):
                inner = (abs(dx) + abs(dy)) <= 1
                if centre:
                    c = HI if (abs(dx) + abs(dy)) == 0 else LIGHT if inner else MID
                else:
                    c = shade(DARK, 14) if inner else shade(DARK, -14)
                put(g, cx + dx, 29 + dy, c)
    for x in range(10, 167):                     # thin rail the diamonds sit on
        if (x - 16) % 12 not in (0, 1, 2, 3, 9, 10, 11):
            put(g, x, 29, shade(DARK, -26))

    def slot(x, y):
        """Sunken 18x18 slot background whose item sits at (x+1, y+1)."""
        rect(g, x, y, x + 17, y + 17, SLOT_FILL)
        for i in range(18):
            put(g, x + i, y, SLOT_DARK)
            put(g, x, y + i, SLOT_DARK)
            put(g, x + i, y + 17, SLOT_LIGHT)
            put(g, x + 17, y + i, SLOT_LIGHT)

    # input tray: 3 adjacent slots at item positions x = 8, 26, 44; y = 48
    for ix in (8, 26, 44):
        slot(ix - 1, 47)

    # result socket (24x24) around the 18x18 slot
    rect(g, 94, 44, 117, 67, (0x10, 0x1A, 0x40, 255))
    frame(g, 94, 44, 117, 67, MID)
    frame(g, 95, 45, 116, 66, DARK)
    slot(97, 47)

    # idle arrow (dim)
    def arrow(img, ox, oy, body, head_edge=None):
        for y in range(5, 10):
            for x in range(0, 14):
                put(img, ox + x, oy + y, body)
        for c in range(13, 22):
            h = round((21 - c) / 8 * 7)
            for y in range(7 - h, 7 + h + 1):
                put(img, ox + c, oy + y, body)

    arrow(g, 66, 49, (0x2B, 0x35, 0x66, 255))

    # player inventory + hotbar
    for r in range(3):
        for c in range(9):
            slot(7 + c * 18, 83 + r * 18)
    for c in range(9):
        slot(7 + c * 18, 141)

    # --- sprites at the right of the sheet ---
    ghost = (0x4E, 0x62, 0xA8, 150)

    # (176,0) template ghost
    gt = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    rect(gt, 1, 1, 14, 14, (0x4E, 0x62, 0xA8, 70))
    frame(gt, 1, 1, 14, 14, ghost)
    for y in range(16):
        for x in range(16):
            if abs(x - 7.5) + abs(y - 7.5) <= 3.5:
                put(gt, x, y, ghost)
    g.paste(gt, (176, 0))

    # (192,0) ingot ghost
    gi = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for (x, y) in ingot_faces(1, 4):
        put(gi, x, y, ghost)
    g.paste(gi, (192, 0))

    # (208,0) echo shard ghost
    shard = [
        "..........X.....",
        ".........XXX....",
        "........XXXXX...",
        ".......XXXXXX...",
        "......XXXXXX....",
        ".....XXXXXX.....",
        "....XXXXXX......",
        "...XXXXXX.......",
        "..XXXXXX........",
        ".XXXXX..........",
        ".XXX............",
    ]
    gs = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, row in enumerate(shard):
        for x, ch in enumerate(row):
            if ch == "X":
                put(gs, x, y + 2, ghost)
    g.paste(gs, (208, 0))

    # (176,16) glowing arrow, 22x15
    ga = Image.new("RGBA", (22, 15), (0, 0, 0, 0))
    arrow(ga, 0, 0, LIGHT)
    for x in range(0, 14):                      # bright core + soft edge on the shaft
        put(ga, x, 7, HI)
        put(ga, x, 5, MID)
        put(ga, x, 9, MID)
    for c in range(13, 21):
        put(ga, c, 7, HI)
    g.paste(ga, (176, 16))

    save(g, "gui", "container", "alloy_forge.png")
    return g


def preview(images, name, scale=8):
    """Optional contact sheet for eyeballing the result."""
    w = sum(i.width * scale + 8 for i in images)
    h = max(i.height * scale for i in images) + 8
    sheet = Image.new("RGBA", (w, h), (70, 70, 70, 255))
    x = 4
    for im in images:
        sheet.paste(im.resize((im.width * scale, im.height * scale), Image.NEAREST), (x, 4), im.resize((im.width * scale, im.height * scale), Image.NEAREST))
        x += im.width * scale + 8
    sheet.save(name)


if __name__ == "__main__":
    ingot = make_ingot()
    tmpl = make_template()
    side, top, bottom = make_block_textures()
    gui = make_gui()
    if os.environ.get("BALTHORITE_PREVIEW"):
        preview([ingot, tmpl, side, top, bottom], os.environ["BALTHORITE_PREVIEW"])
