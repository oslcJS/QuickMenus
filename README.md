<p align="center">
  <img src="https://raw.githubusercontent.com/oslcJS/.github/main/assets/VAULT.gif" width="160">
</p>

<h1 align="center">QuickMenus</h1>

<p align="center">
Free, open-source GUI menu plugin with multi-page menus, animations, proxy connect actions, hotbar items, and PlaceholderAPI support.
</p>

---

<p align="center">
  <span style="font-family: 'IBM Plex Mono', monospace; font-style: italic; border: 1px solid #1c1c1c; padding: 6px 10px; color: #555; background: #000;">
    status <span style="color:#222;">/</span>
    <span style="color:#fff; font-weight:600;">stable</span>
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

QuickMenus builds configurable inventory menus for hubs, survival servers, proxies, and utility workflows. It supports menu files, paged layouts, click actions, hotbar shortcuts, PlaceholderAPI placeholders, and optional QuickEco checks.

---

## features

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/2.gif)

- configurable inventory menus
- multi-page menu support
- hotbar item shortcuts
- sound and command actions
- proxy connect actions
- PlaceholderAPI support
- optional QuickEco bridge
- player visibility toggle action

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

Drop the jar into `/plugins` and restart the server. Edit menu files under `plugins/QuickMenus/menus/`, then run `/qm reload`.

---

## commands

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/5.gif)

```text
/quickmenus
/qm help
/menu <id> [player]
/menus
```

---

## license

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/6.gif)

MIT

---

## plugins

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/7.gif)

Supports PlaceholderAPI and QuickEco as optional runtime hooks.

---

## philosophy

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/8.gif)

QuickMenus keeps GUI configuration simple enough for fast edits while still supporting real server navigation workflows.

---

## backend

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/9.gif)

Built on Bukkit/Paper Inventory APIs with YAML menu loading, action parsing, hotbar listeners, and compatibility hooks for PlaceholderAPI and QuickEco.

---

## roadmap

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/10.gif)

- add more built-in action types
- expand menu condition options
- improve editor-friendly examples

---

## permissions

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/11.gif)

```text
quickmenus.*
quickmenus.admin
quickmenus.use
quickmenus.open.others
```

---

## configuration

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/12.gif)

```yaml
settings:
  close-sound: UI_BUTTON_CLICK
  open-sound: BLOCK_CHEST_OPEN
hotbar-items:
  enabled: true
```

---

## api

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/13.gif)

```java
QuickMenus plugin = QuickMenus.getInstance();
```

---

## stats

![](https://raw.githubusercontent.com/oslcJS/.github/main/assets/15.gif)

```text
performance   / lightweight
memory        / menu-cache
design        / GUI actions
```
