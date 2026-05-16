<p align="center">
  <img src="https://raw.githubusercontent.com/oslcJS/.github/main/assets/VAULT.gif" width="160">
</p>

<h1 align="center">QuickMenus</h1>

<p align="center">
Free, open-source GUI menu plugin with multi-page support, proxy connect, demo menus, and PlaceholderAPI.
</p>

---

<p align="center">
  <span style="font-family: 'IBM Plex Mono', monospace; font-style: italic; border: 1px solid #1c1c1c; padding: 6px 10px; color: #555; background: #000;">
    status <span style="color:#222;">/</span>
    <span style="color:#fff; font-weight:600;">stable</span>
    <span style="color:#222;"> / </span>
    <span style="color:#fff; font-weight:600;">v2.0</span>
  </span>
</p>

<div align="center">
  <table>
    <tr>
      <td><img src="https://raw.githubusercontent.com/oslcJS/.github/main/assets/logo_03.png" width="72"></td>
      <td><strong>QuickPlugins</strong><br>Small, fast Minecraft plugins built for modern Paper, Spigot, and Purpur servers.</td>
    </tr>
  </table>
</div>

---

## overview

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/14.gif)

QuickMenus turns flat command lists into clean inventory GUIs. Define menus in YAML, multi-page paginates them automatically, chainable actions handle clicks (send to proxy, run command, open another menu, withdraw money, gate by permission). Eight polished demo menus ship in the jar so you can `/qm demo install all` and see what a finished server looks like in two seconds.

---

## features

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/2.gif)

- YAML-defined menus, hot-reloaded with `/qm reload`
- multi-page menus with `{page}` / `{max_page}` placeholders
- live auto-refresh (per-menu rate in ticks) for placeholder-driven items
- 12 chainable actions: CLOSE, MESSAGE, SOUND, COMMAND, PLAYER, CONSOLE, PROXY, PAGE_NEXT/PREV/GO, PERMISSION_CHECK, TOGGLE_VANISH, ECO_WITHDRAW/DEPOSIT/BALANCE_CHECK
- hotbar items locked to specific slots (compass, hide players, etc.)
- BungeeCord / Velocity proxy connect on the standard `BungeeCord` channel
- QuickEco bridge for purchases and balance gates
- PlaceholderAPI expansion (`%quickmenus_balance%`, `%quickmenus_menu_count%`, `%quickmenus_has_menu_<id>%`)
- 8 polished demo menus installable with one command
- runtime settings panel: `/qm settings [key] [value]`
- consistent command verbs aligned with the rest of the QuickPlugins suite

---

## compatibility

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/3.gif)

- Paper
- Spigot
- Purpur
- 1.20.4 - 1.21.8
- Paper 26.1.1 - 26.1.2

---

## installs

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/4.gif)

Drop the jar into `/plugins` and restart. Then:

```text
/qm demo install all       install all 8 demo menus
/qm reload                 reload (auto-runs after install)
/menu hub                  open the hub demo
```

Edit any of the installed YAML files in `plugins/QuickMenus/menus/` and `/qm reload` to apply.

---

## commands

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/5.gif)

### admin (/qm)

```text
/qm reload                    reload all menus and config
/qm list                      list loaded menus
/qm info <id>                 show menu details
/qm preview <id>              open menu as admin (bypass perms)
/qm settings [key] [value]    view or change settings
/qm demo list                 list available demo menus
/qm demo install <id|all>     install demo menu(s)
```

### player

```text
/menu <id> [player]           open a menu (admin can open for others)
```

---

## demo menus

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/2.gif)

8 polished menus ship with the plugin. Each is a self-contained YAML in `plugins/QuickMenus/menus/` after install.

| Demo | What it shows |
|------|---------------|
| **hub**       | 4-server portal navigator using `[PROXY]` actions |
| **shop**      | 3-page item shop using QuickEco for purchases and balance gates |
| **cosmetics** | Particle trail picker with click toggles and permission gating |
| **warps**     | 7-button warp grid that fires `/warp <name>` |
| **profile**   | PAPI-powered stat dashboard (balance, health, location, playtime, ping) |
| **rules**     | 2-page server rules with an "I Agree" close button |
| **kits**      | Kit picker gated by per-kit permissions |
| **vote**      | Vote-site portal with reward hooks |

```text
/qm demo install all              install everything
/qm demo install shop             install just the shop
/qm demo list                     show what's available
```

After installing, `/menu hub` (or any id) opens it. To customize, edit the
file in `plugins/QuickMenus/menus/<id>.yml` and `/qm reload`.

---

## actions

```text
[CLOSE]                         close the inventory
[MESSAGE] <text>                send chat to the clicking player
[SOUND] <sound>                 play a sound at the player
[COMMAND] <cmd>                 run as player
[PLAYER] <cmd>                  alias for COMMAND
[CONSOLE] <cmd>                 run as console
[PROXY] <server>                BungeeCord / Velocity connect
[PAGE_NEXT] / [PAGE_PREV]       navigate paged menus
[PAGE_GO] <n>                   jump to page
[PERMISSION_CHECK] <node>       gate subsequent actions
[TOGGLE_VANISH]                 toggle player hide
[ECO_WITHDRAW] <amount>         take money via QuickEco
[ECO_DEPOSIT] <amount>          give money via QuickEco
[ECO_BALANCE_CHECK] <amount>    gate by minimum balance
```

Actions run in order. A failed `PERMISSION_CHECK` or `ECO_BALANCE_CHECK` stops
the chain, so safe-by-default purchases look like:

```yaml
actions:
  - '[PERMISSION_CHECK] shop.vip'
  - '[ECO_BALANCE_CHECK] 1000'
  - '[ECO_WITHDRAW] 1000'
  - '[CONSOLE] give %player_name% elytra 1'
  - '[MESSAGE] &aPurchased Elytra.'
```

---

## settings

```text
/qm settings                              view current values
/qm settings debug true                   verbose feedback
/qm settings hotbar-items.enabled false   disable join hotbar
/qm settings open-sound BLOCK_CHEST_OPEN  change open SFX
/qm settings close-sound UI_BUTTON_CLICK  change close SFX
```

Persisted to `config.yml` immediately.

---

## placeholders

```text
%quickmenus_balance%             QuickEco balance (raw)
%quickmenus_balance_formatted%   formatted with currency symbol
%quickmenus_menu_count%          number of loaded menus
%quickmenus_has_menu_<id>%       "true" / "false"
```

PAPI placeholders from other plugins work inside menu titles, item names,
and lore -- e.g. `%player_name%`, `%player_world%`, `%statistic_play_one_minute%`.

---

## permissions

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/11.gif)

```text
quickmenus.admin             parent (op default)
  qm.reload
  qm.preview
  qm.settings
  qm.demo
quickmenus.use               default true -- /menu <id>
quickmenus.open.others       open menus for other players
```

Each menu can declare its own `permission:` in YAML to gate `/menu <id>`.

---

## configuration

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/12.gif)

```yaml
settings:
  open-sound: BLOCK_CHEST_OPEN
  close-sound: UI_BUTTON_CLICK
  debug: false

hotbar-items:
  enabled: true
  sound: UI_BUTTON_CLICK
  items:
    menu:
      slot: 0
      material: BOOK
      display_name: '&b&lMenu'
      glow: true
      actions:
        - '[COMMAND] menu default'
    server_selector:
      slot: 4
      material: COMPASS
      display_name: '&5&lServer Selector'
      glow: true
      actions:
        - '[COMMAND] menu serverselector'
    hide_players:
      slot: 8
      material: LIME_DYE
      display_name: '&a&lHide Players'
      actions:
        - '[TOGGLE_VANISH]'
```

---

## menu yaml shape

```yaml
id: 'shop'
slots: 54
title: '&8&lShop &7- &ePage {page}'
pages: 3
permission: 'shop.use'        # optional
refresh:
  enabled: true
  rate: 20                    # ticks
items:
  filler:
    material: BLACK_STAINED_GLASS_PANE
    slot: -1                  # -1 fills all empty slots
    display_name: ' '
  prev:
    material: ARROW
    slot: 48
    page_nav: true            # appears on every page
    display_name: '&ePrev'
    actions: ['[PAGE_PREV]']
  sword:
    material: DIAMOND_SWORD
    slot: 13
    page: 1                   # only on page 1
    glow: true
    item_flags: ['HIDE_ATTRIBUTES']
    display_name: '&b&lDiamond Sword'
    lore:
      - '&7Cost: &e$250'
    actions:
      - '[ECO_BALANCE_CHECK] 250'
      - '[ECO_WITHDRAW] 250'
      - '[CONSOLE] give %player_name% diamond_sword 1'
```

---

## license

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/6.gif)

MIT

---

## plugins

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/7.gif)

Soft-depends on PlaceholderAPI and QuickEco. Registers an outgoing
`BungeeCord` plugin channel for proxy connect (works with BungeeCord and
Velocity). No required dependencies.

---

## philosophy

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/8.gif)

GUIs first, commands second. Ship a default that looks like a finished
server. Edit YAML to customize; no recompile needed. Consistent verbs across
the suite (`create`, `remove`, `edit`, `list`, `preview`, `settings`). All
runtime toggles live in `/qm settings`, not deep in the config.

---

## backend

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/9.gif)

Each open menu is a per-player session with its own page state and optional
refresh task. Actions are parsed once at load and executed in order at click
time. Hotbar items get re-applied on join and respawn; the hide-players
toggle uses Bukkit's `hidePlayer` for true server-side invisibility.

---

## roadmap

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/10.gif)

- in-game GUI editor (`/qm edit <id>`)
- per-item conditions (e.g. show only if perm + balance)
- per-page background materials
- action: `[OPEN] <id>` shortcut for nested menus
- per-server bStats integration

---

## stats

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/15.gif)

```text
performance   / per-player sessions + scheduled refresh
memory        / lightweight in-memory state
design        / YAML-driven, GUI-first
storage       / flat YAML
integrations  / Vault, PlaceholderAPI, QuickEco, BungeeCord/Velocity
```
