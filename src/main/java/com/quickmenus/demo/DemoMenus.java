package com.quickmenus.demo;

import com.quickmenus.QuickMenus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DemoMenus {

    public record Demo(String id, String description, String yaml) {}

    private static final Map<String, Demo> REGISTRY = buildRegistry();

    private static Map<String, Demo> buildRegistry() {
        Map<String, Demo> m = new LinkedHashMap<>();
        m.put("hub", new Demo("hub", "4-server portal navigator with PROXY actions", HUB));
        m.put("shop", new Demo("shop", "Multi-page item shop with QuickEco integration", SHOP));
        m.put("cosmetics", new Demo("cosmetics", "Particle trail picker with toggle actions", COSMETICS));
        m.put("warps", new Demo("warps", "Warp grid that runs /warp", WARPS));
        m.put("profile", new Demo("profile", "PAPI-powered stats dashboard", PROFILE));
        m.put("rules", new Demo("rules", "Multi-page server rules display", RULES));
        m.put("kits", new Demo("kits", "Kit picker with permission gates", KITS));
        m.put("vote", new Demo("vote", "Vote site portal with reward action", VOTE));
        return m;
    }

    private DemoMenus() {}

    public static Collection<Demo> all() { return REGISTRY.values(); }
    public static Demo get(String id) { return REGISTRY.get(id); }
    public static boolean exists(String id) { return REGISTRY.containsKey(id); }

    public static boolean install(QuickMenus plugin, String id) {
        Demo d = REGISTRY.get(id);
        if (d == null) return false;
        File menusDir = new File(plugin.getDataFolder(), "menus");
        menusDir.mkdirs();
        File target = new File(menusDir, id + ".yml");
        try {
            Files.writeString(target.toPath(), d.yaml());
            return true;
        } catch (IOException e) {
            plugin.getLogger().warning("Demo install failed for " + id + ": " + e.getMessage());
            return false;
        }
    }

    public static int installAll(QuickMenus plugin) {
        int n = 0;
        for (String id : REGISTRY.keySet()) {
            if (install(plugin, id)) n++;
        }
        return n;
    }

    private static final String HUB = """
            id: 'hub'
            slots: 27
            title: '&8>> &b&lHub Navigator &8<<'
            pages: 1
            refresh: { enabled: false, rate: 40 }
            items:
              filler:
                material: CYAN_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              survival:
                material: GRASS_BLOCK
                slot: 10
                glow: true
                display_name: '&a&lSurvival'
                lore:
                  - '&7Classic survival multiplayer.'
                  - '&7Build, mine, explore.'
                  - ''
                  - '&eClick to join'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[PROXY] survival'
              skyblock:
                material: SAND
                slot: 12
                glow: true
                display_name: '&b&lSkyblock'
                lore:
                  - '&7Island survival challenge.'
                  - '&7Build your sky empire.'
                  - ''
                  - '&eClick to join'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[PROXY] skyblock'
              creative:
                material: REDSTONE_BLOCK
                slot: 14
                glow: true
                display_name: '&c&lCreative'
                lore:
                  - '&7Unlimited blocks.'
                  - '&7Free creativity.'
                  - ''
                  - '&eClick to join'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[PROXY] creative'
              minigames:
                material: FIREWORK_ROCKET
                slot: 16
                glow: true
                display_name: '&e&lMinigames'
                lore:
                  - '&7Quick matches and chaos.'
                  - ''
                  - '&eClick to join'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[PROXY] minigames'
              close:
                material: BARRIER
                slot: 22
                display_name: '&cClose'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[CLOSE]'
            """;

    private static final String SHOP = """
            id: 'shop'
            slots: 54
            title: '&8&lShop &7- &ePage {page}&8/&7{max_page}'
            pages: 3
            refresh: { enabled: true, rate: 20 }
            items:
              filler:
                material: BLACK_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              prev:
                material: ARROW
                slot: 48
                page_nav: true
                display_name: '&ePrevious Page'
                actions: ['[PAGE_PREV]']
              next:
                material: ARROW
                slot: 50
                page_nav: true
                display_name: '&eNext Page'
                actions: ['[PAGE_NEXT]']
              page_indicator:
                material: PAPER
                slot: 49
                page_nav: true
                display_name: '&fPage &e{page} &7/ &e{max_page}'
                actions: []
              balance:
                material: GOLD_INGOT
                slot: 52
                page_nav: true
                glow: true
                display_name: '&6&lBalance: &e%quickmenus_balance_formatted%'
                lore: ['&7Tap to see balance']
                actions:
                  - '[MESSAGE] &7Balance: &e%quickmenus_balance_formatted%'
              close:
                material: BARRIER
                slot: 53
                page_nav: true
                display_name: '&cClose'
                actions: ['[SOUND] UI_BUTTON_CLICK', '[CLOSE]']
              # Page 1: Combat
              sword:
                material: DIAMOND_SWORD
                slot: 11
                page: 1
                glow: true
                item_flags: ['HIDE_ATTRIBUTES']
                display_name: '&b&lDiamond Sword'
                lore: ['&7High-tier melee.', '', '&eCost: &f$250', '&7Balance: &e%quickmenus_balance_formatted%', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 250'
                  - '[ECO_WITHDRAW] 250'
                  - '[MESSAGE] &aPurchased &bDiamond Sword&a.'
                  - '[CONSOLE] give %player_name% diamond_sword 1'
              bow:
                material: BOW
                slot: 13
                page: 1
                display_name: '&a&lPower Bow'
                lore: ['&7Ranged precision.', '', '&eCost: &f$150', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 150'
                  - '[ECO_WITHDRAW] 150'
                  - '[MESSAGE] &aPurchased &aPower Bow&a.'
                  - '[CONSOLE] give %player_name% bow 1'
              chestplate:
                material: DIAMOND_CHESTPLATE
                slot: 15
                page: 1
                glow: true
                item_flags: ['HIDE_ATTRIBUTES']
                display_name: '&b&lDiamond Chestplate'
                lore: ['&7Top defense.', '', '&eCost: &f$400', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 400'
                  - '[ECO_WITHDRAW] 400'
                  - '[MESSAGE] &aPurchased &bDiamond Chestplate&a.'
                  - '[CONSOLE] give %player_name% diamond_chestplate 1'
              # Page 2: Utility
              golden_apple:
                material: GOLDEN_APPLE
                slot: 11
                page: 2
                amount: 16
                display_name: '&6Golden Apples &7x16'
                lore: ['&7Clutch healing.', '', '&eCost: &f$200', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 200'
                  - '[ECO_WITHDRAW] 200'
                  - '[MESSAGE] &aPurchased &616x Golden Apple&a.'
                  - '[CONSOLE] give %player_name% golden_apple 16'
              ender_pearl:
                material: ENDER_PEARL
                slot: 13
                page: 2
                amount: 8
                display_name: '&5Ender Pearls &7x8'
                lore: ['&7Teleport with precision.', '', '&eCost: &f$300', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 300'
                  - '[ECO_WITHDRAW] 300'
                  - '[MESSAGE] &aPurchased &58x Ender Pearl&a.'
                  - '[CONSOLE] give %player_name% ender_pearl 8'
              xp:
                material: EXPERIENCE_BOTTLE
                slot: 15
                page: 2
                amount: 32
                display_name: '&aXP Bottles &7x32'
                lore: ['&7Instant experience.', '', '&eCost: &f$100', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 100'
                  - '[ECO_WITHDRAW] 100'
                  - '[MESSAGE] &aPurchased &a32x XP Bottle&a.'
                  - '[CONSOLE] give %player_name% experience_bottle 32'
              # Page 3: End-game
              elytra:
                material: ELYTRA
                slot: 11
                page: 3
                glow: true
                item_flags: ['HIDE_ATTRIBUTES']
                display_name: '&d&lElytra'
                lore: ['&7Soar through the skies.', '', '&eCost: &f$1000', '&7Requires &equickmenus.vip', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[PERMISSION_CHECK] quickmenus.vip'
                  - '[ECO_BALANCE_CHECK] 1000'
                  - '[ECO_WITHDRAW] 1000'
                  - '[MESSAGE] &aPurchased &dElytra&a.'
                  - '[CONSOLE] give %player_name% elytra 1'
              totem:
                material: TOTEM_OF_UNDYING
                slot: 13
                page: 3
                glow: true
                display_name: '&6&lTotem of Undying'
                lore: ['&7Cheat death.', '', '&eCost: &f$2000', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 2000'
                  - '[ECO_WITHDRAW] 2000'
                  - '[MESSAGE] &aPurchased &6Totem of Undying&a.'
                  - '[CONSOLE] give %player_name% totem_of_undying 1'
              netherite:
                material: NETHERITE_INGOT
                slot: 15
                page: 3
                glow: true
                display_name: '&8&lNetherite Ingot'
                lore: ['&7Endgame material.', '', '&eCost: &f$1500', '', '&aClick to purchase']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[ECO_BALANCE_CHECK] 1500'
                  - '[ECO_WITHDRAW] 1500'
                  - '[MESSAGE] &aPurchased &8Netherite Ingot&a.'
                  - '[CONSOLE] give %player_name% netherite_ingot 1'
            """;

    private static final String COSMETICS = """
            id: 'cosmetics'
            slots: 27
            title: '&8>> &d&lCosmetics &8<<'
            pages: 1
            refresh: { enabled: false, rate: 40 }
            items:
              filler:
                material: MAGENTA_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              flame:
                material: BLAZE_POWDER
                slot: 10
                glow: true
                display_name: '&6&lFlame Trail'
                lore: ['&7A trail of flames.', '', '&aClick to equip']
                actions:
                  - '[SOUND] BLOCK_FIRE_AMBIENT'
                  - '[MESSAGE] &aEquipped &6Flame Trail&a.'
                  - '[CONSOLE] particle flame %player_x% %player_y% %player_z% 1 0 1 0 30'
              hearts:
                material: POPPY
                slot: 11
                glow: true
                display_name: '&c&lHearts Trail'
                lore: ['&7Spread the love.', '', '&aClick to equip']
                actions:
                  - '[SOUND] ENTITY_EXPERIENCE_ORB_PICKUP'
                  - '[MESSAGE] &aEquipped &cHearts Trail&a.'
              snow:
                material: SNOWBALL
                slot: 12
                glow: true
                display_name: '&b&lSnow Trail'
                lore: ['&7Cool and crisp.', '', '&aClick to equip']
                actions:
                  - '[SOUND] BLOCK_SNOW_BREAK'
                  - '[MESSAGE] &aEquipped &bSnow Trail&a.'
              notes:
                material: NOTE_BLOCK
                slot: 13
                glow: true
                display_name: '&d&lMusic Trail'
                lore: ['&7Carry a melody.', '', '&aClick to equip']
                actions:
                  - '[SOUND] BLOCK_NOTE_BLOCK_HARP'
                  - '[MESSAGE] &aEquipped &dMusic Trail&a.'
              soul:
                material: SOUL_TORCH
                slot: 14
                glow: true
                display_name: '&3&lSoul Trail'
                lore: ['&7Wandering souls.', '', '&aClick to equip']
                actions:
                  - '[SOUND] PARTICLE_SOUL_ESCAPE'
                  - '[MESSAGE] &aEquipped &3Soul Trail&a.'
              dragon:
                material: DRAGON_BREATH
                slot: 15
                glow: true
                display_name: '&5&lDragon Trail'
                lore: ['&7Ferocious aura.', '&7Requires &equickmenus.vip', '', '&aClick to equip']
                actions:
                  - '[PERMISSION_CHECK] quickmenus.vip'
                  - '[SOUND] ENTITY_ENDER_DRAGON_GROWL'
                  - '[MESSAGE] &aEquipped &5Dragon Trail&a.'
              clear:
                material: BARRIER
                slot: 16
                display_name: '&7&lClear Trail'
                lore: ['&7Disable cosmetics.']
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[MESSAGE] &7Cleared cosmetic trail.'
              close:
                material: ARROW
                slot: 22
                display_name: '&7Close'
                actions: ['[CLOSE]']
            """;

    private static final String WARPS = """
            id: 'warps'
            slots: 27
            title: '&8>> &a&lWarps &8<<'
            pages: 1
            refresh: { enabled: false, rate: 40 }
            items:
              filler:
                material: LIGHT_GRAY_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              spawn:
                material: BEACON
                slot: 10
                glow: true
                display_name: '&e&lSpawn'
                lore: ['&7Return to spawn.']
                actions:
                  - '[SOUND] BLOCK_BEACON_ACTIVATE'
                  - '[PLAYER] spawn'
              market:
                material: EMERALD
                slot: 11
                glow: true
                display_name: '&a&lMarket'
                lore: ['&7Player shops.']
                actions: ['[SOUND] UI_BUTTON_CLICK', '[PLAYER] warp market']
              pvp:
                material: IRON_SWORD
                slot: 12
                glow: true
                item_flags: ['HIDE_ATTRIBUTES']
                display_name: '&c&lPvP Arena'
                lore: ['&7Combat zone.']
                actions: ['[SOUND] UI_BUTTON_CLICK', '[PLAYER] warp pvp']
              farm:
                material: WHEAT
                slot: 13
                glow: true
                display_name: '&6&lFarms'
                lore: ['&7Public farms area.']
                actions: ['[SOUND] UI_BUTTON_CLICK', '[PLAYER] warp farm']
              nether:
                material: NETHERRACK
                slot: 14
                glow: true
                display_name: '&4&lNether Hub'
                lore: ['&7Highway portals.']
                actions: ['[SOUND] BLOCK_PORTAL_AMBIENT', '[PLAYER] warp nether']
              end:
                material: END_STONE
                slot: 15
                glow: true
                display_name: '&5&lEnd Hub'
                lore: ['&7End portal hub.']
                actions: ['[SOUND] BLOCK_END_PORTAL_FRAME_FILL', '[PLAYER] warp end']
              wild:
                material: OAK_SAPLING
                slot: 16
                glow: true
                display_name: '&2&lWilderness'
                lore: ['&7Random teleport.']
                actions: ['[SOUND] BLOCK_GRASS_BREAK', '[PLAYER] rtp']
              close:
                material: BARRIER
                slot: 22
                display_name: '&cClose'
                actions: ['[CLOSE]']
            """;

    private static final String PROFILE = """
            id: 'profile'
            slots: 27
            title: '&8>> &b&lYour Profile &8<<'
            pages: 1
            refresh: { enabled: true, rate: 20 }
            items:
              filler:
                material: BLUE_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              head:
                material: PLAYER_HEAD
                slot: 4
                display_name: '&f%player_name%'
                lore:
                  - '&7uuid: &8%player_uuid%'
                  - '&7world: &f%player_world%'
              balance:
                material: GOLD_INGOT
                slot: 11
                glow: true
                display_name: '&6&lBalance'
                lore:
                  - '&7Current: &e%quickmenus_balance_formatted%'
                  - '&7Rank: &e#%quickeco_rank%'
              health:
                material: APPLE
                slot: 12
                display_name: '&c&lHealth'
                lore:
                  - '&7HP: &c%player_health% &7/ &c%player_max_health%'
                  - '&7Hunger: &6%player_food_level%&7/20'
              location:
                material: COMPASS
                slot: 13
                display_name: '&e&lLocation'
                lore:
                  - '&7x: &f%player_x%'
                  - '&7y: &f%player_y%'
                  - '&7z: &f%player_z%'
                  - '&7world: &f%player_world%'
              playtime:
                material: CLOCK
                slot: 14
                display_name: '&a&lPlaytime'
                lore: ['&7Total: &f%statistic_play_one_minute%']
              ping:
                material: FEATHER
                slot: 15
                display_name: '&b&lPing'
                lore: ['&7Current: &f%player_ping% ms']
              close:
                material: BARRIER
                slot: 22
                display_name: '&cClose'
                actions: ['[CLOSE]']
            """;

    private static final String RULES = """
            id: 'rules'
            slots: 54
            title: '&8>> &c&lServer Rules &8<< &7- Page &e{page}&7/&e{max_page}'
            pages: 2
            refresh: { enabled: false, rate: 40 }
            items:
              filler:
                material: RED_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              prev:
                material: ARROW
                slot: 48
                page_nav: true
                display_name: '&ePrevious'
                actions: ['[PAGE_PREV]']
              next:
                material: ARROW
                slot: 50
                page_nav: true
                display_name: '&eNext'
                actions: ['[PAGE_NEXT]']
              page_indicator:
                material: PAPER
                slot: 49
                page_nav: true
                display_name: '&fPage &e{page} &7/ &e{max_page}'
              close:
                material: BARRIER
                slot: 53
                page_nav: true
                display_name: '&cClose'
                actions: ['[CLOSE]']
              accept:
                material: LIME_WOOL
                slot: 45
                page_nav: true
                glow: true
                display_name: '&a&lI Agree'
                lore: ['&7Acknowledge the rules.']
                actions:
                  - '[SOUND] ENTITY_PLAYER_LEVELUP'
                  - '[MESSAGE] &aThanks for agreeing to the rules.'
                  - '[CLOSE]'
              # Page 1
              respect:
                material: WRITTEN_BOOK
                slot: 10
                page: 1
                display_name: '&c&l1. Respect'
                lore:
                  - '&7Treat everyone with respect.'
                  - '&7No harassment, hate speech,'
                  - '&7or personal attacks.'
              cheating:
                material: WRITTEN_BOOK
                slot: 12
                page: 1
                display_name: '&c&l2. No Cheating'
                lore:
                  - '&7No hacks, x-ray, or exploits.'
                  - '&7Auto-clickers are banned.'
              chat:
                material: WRITTEN_BOOK
                slot: 14
                page: 1
                display_name: '&c&l3. Clean Chat'
                lore:
                  - '&7Keep it family friendly.'
                  - '&7English only in global chat.'
              spam:
                material: WRITTEN_BOOK
                slot: 16
                page: 1
                display_name: '&c&l4. No Spam'
                lore:
                  - '&7Do not flood chat.'
                  - '&7No caps abuse.'
              # Page 2
              griefing:
                material: WRITTEN_BOOK
                slot: 10
                page: 2
                display_name: '&c&l5. No Griefing'
                lore:
                  - '&7Do not destroy other players'
                  - '&7builds or steal items.'
              advertising:
                material: WRITTEN_BOOK
                slot: 12
                page: 2
                display_name: '&c&l6. No Advertising'
                lore:
                  - '&7No other server links.'
                  - '&7No spam in DMs either.'
              staff:
                material: WRITTEN_BOOK
                slot: 14
                page: 2
                display_name: '&c&l7. Listen to Staff'
                lore:
                  - '&7Follow staff instructions.'
                  - '&7Appeals via Discord only.'
              report:
                material: WRITTEN_BOOK
                slot: 16
                page: 2
                display_name: '&c&l8. Report Issues'
                lore:
                  - '&7Use /report or Discord.'
                  - '&7Help keep the server safe.'
            """;

    private static final String KITS = """
            id: 'kits'
            slots: 27
            title: '&8>> &6&lKit Selector &8<<'
            pages: 1
            refresh: { enabled: false, rate: 40 }
            items:
              filler:
                material: ORANGE_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              starter:
                material: WOODEN_SWORD
                slot: 10
                glow: true
                item_flags: ['HIDE_ATTRIBUTES']
                display_name: '&f&lStarter Kit'
                lore: ['&7Basic gear for new players.', '', '&aFree']
                actions:
                  - '[SOUND] ITEM_ARMOR_EQUIP_LEATHER'
                  - '[CONSOLE] kit starter %player_name%'
                  - '[MESSAGE] &aReceived &fStarter Kit&a.'
              warrior:
                material: IRON_SWORD
                slot: 12
                glow: true
                item_flags: ['HIDE_ATTRIBUTES']
                display_name: '&e&lWarrior Kit'
                lore: ['&7Iron sword + iron armor.', '', '&7Requires &equickmenus.kit.warrior']
                actions:
                  - '[PERMISSION_CHECK] quickmenus.kit.warrior'
                  - '[SOUND] ITEM_ARMOR_EQUIP_IRON'
                  - '[CONSOLE] kit warrior %player_name%'
                  - '[MESSAGE] &aReceived &eWarrior Kit&a.'
              mage:
                material: BLAZE_ROD
                slot: 14
                glow: true
                display_name: '&5&lMage Kit'
                lore: ['&7Wand + potions.', '', '&7Requires &equickmenus.kit.mage']
                actions:
                  - '[PERMISSION_CHECK] quickmenus.kit.mage'
                  - '[SOUND] ENTITY_EVOKER_CAST_SPELL'
                  - '[CONSOLE] kit mage %player_name%'
                  - '[MESSAGE] &aReceived &5Mage Kit&a.'
              vip:
                material: DIAMOND_CHESTPLATE
                slot: 16
                glow: true
                item_flags: ['HIDE_ATTRIBUTES']
                display_name: '&b&lVIP Kit'
                lore: ['&7Diamond gear + extras.', '', '&7Requires &equickmenus.vip']
                actions:
                  - '[PERMISSION_CHECK] quickmenus.vip'
                  - '[SOUND] ENTITY_PLAYER_LEVELUP'
                  - '[CONSOLE] kit vip %player_name%'
                  - '[MESSAGE] &aReceived &bVIP Kit&a.'
              close:
                material: BARRIER
                slot: 22
                display_name: '&cClose'
                actions: ['[CLOSE]']
            """;

    private static final String VOTE = """
            id: 'vote'
            slots: 27
            title: '&8>> &a&lVote &8<<'
            pages: 1
            refresh: { enabled: false, rate: 40 }
            items:
              filler:
                material: LIME_STAINED_GLASS_PANE
                slot: -1
                display_name: ' '
              info:
                material: WRITABLE_BOOK
                slot: 4
                display_name: '&a&lWhy Vote?'
                lore:
                  - '&7Voting helps the server grow.'
                  - '&7You get rewards each vote.'
                  - ''
                  - '&7Click a site below to vote.'
              site1:
                material: NETHER_STAR
                slot: 11
                glow: true
                display_name: '&e&lVote Site 1'
                lore:
                  - '&7minecraft-server-list.com'
                  - '&7Reward: &f$100 + 1 crate key'
                  - ''
                  - '&aClick to open'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[MESSAGE] &7Open this link to vote:'
                  - '[MESSAGE] &fhttps://minecraft-server-list.com'
              site2:
                material: NETHER_STAR
                slot: 13
                glow: true
                display_name: '&e&lVote Site 2'
                lore:
                  - '&7minecraftservers.org'
                  - '&7Reward: &f$100 + 1 crate key'
                  - ''
                  - '&aClick to open'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[MESSAGE] &7Open this link to vote:'
                  - '[MESSAGE] &fhttps://minecraftservers.org'
              site3:
                material: NETHER_STAR
                slot: 15
                glow: true
                display_name: '&e&lVote Site 3'
                lore:
                  - '&7planetminecraft.com'
                  - '&7Reward: &f$100 + 1 crate key'
                  - ''
                  - '&aClick to open'
                actions:
                  - '[SOUND] UI_BUTTON_CLICK'
                  - '[MESSAGE] &7Open this link to vote:'
                  - '[MESSAGE] &fhttps://planetminecraft.com'
              streak:
                material: GOLD_BLOCK
                slot: 22
                glow: true
                display_name: '&6&lVote Streak'
                lore:
                  - '&7Your streak: &e0 days'
                  - '&75-day reward: &f$500'
                  - '&730-day reward: &fVIP for a month'
              close:
                material: BARRIER
                slot: 26
                display_name: '&cClose'
                actions: ['[CLOSE]']
            """;
}
