package com.willfp.hypixelenchanting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
  public static Main instance;
  
  public static int scheduleSyncDelayedTask(Runnable runnable, int delay) {
    return Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)instance, runnable, delay);
  }
  
  ItemStack NOENCHANTMENT = new ItemStack(Material.INK_SACK, 1, (short)8);
  
  public void onLoad() {
    instance = this;
  }
  
  HashMap<Player, HypixelEnchants> playerHypixelEnchantsMap = new HashMap<>();
  
  public void onEnable() {
    instance = this;
    getServer().getPluginManager().registerEvents(this, (Plugin)this);
    ItemMeta meta = this.NOENCHANTMENT.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("noenchant")));
    this.NOENCHANTMENT.setItemMeta(meta);
    saveDefaultConfig();
  }
  
  public void onDisable() {
    instance = null;
  }
  
  public static Main getInstance() {
    return instance;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("updateitemlore")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("onlyplayers")));
      } else {
        Player player = (Player)sender;
        if (player.hasPermission("hypixelenchanting.update")) {
          player.setItemInHand(HypixelEnchantment.addEnchantmentLore(player.getItemInHand()));
        } else {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("nopermission")));
        } 
      } 
      return true;
    } 
    if (cmd.getName().equalsIgnoreCase("hereload")) {
      if (!(sender instanceof Player)) {
        reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("reloadedconfig")));
      } else {
        Player player = (Player)sender;
        if (player.hasPermission("hypixelenchanting.reload")) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("reloadedconfig")));
          reloadConfig();
        } else {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("nopermission")));
        } 
      } 
      return true;
    } 
    return false;
  }
  
  @EventHandler
  public void function(final InventoryClickEvent event) {
    final Player player = (Player)event.getWhoClicked();
    if (player.getOpenInventory().getTopInventory() == null)
      return; 
    if (!player.getOpenInventory().getTopInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', getConfig().getString("inventory-name"))))
      return; 
    if (event.getClickedInventory() == null)
      return; 
    if (event.getCursor() == null && event.getClickedInventory().getType() == InventoryType.PLAYER)
      return; 
    if (event.getCurrentItem() == null)
      return; 
    if (event.getSlot() == 49 && event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', getConfig().getString("inventory-name"))))
      player.closeInventory(); 
    if (event.getSlot() != 13 && event.getSlot() != 29 && event.getSlot() != 31 && event.getSlot() != 33 && 
      event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', getConfig().getString("inventory-name"))))
      event.setCancelled(true); 
    final HypixelEnchantment hypixelEnchantment = this.playerHypixelEnchantmentMap.get(player);
    if (event.getSlot() != 29 && event.getSlot() != 31 && event.getSlot() != 33) {
      if (event.getClickedInventory().getType() == InventoryType.PLAYER && event.getCursor() == null)
        return; 
      if (event.getClickedInventory().getType() == InventoryType.PLAYER && player.getInventory().getItem(event.getSlot()) == null)
        return; 
      Bukkit.getServer().getScheduler().runTaskLater((Plugin)this, new Runnable() {
            public void run() {
              if (event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("inventory-name"))) && event.getSlot() != 13)
                return; 
              hypixelEnchantment.setItem(player);
            }
          }1L);
    } else if (event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', getConfig().getString("inventory-name")))) {
      hypixelEnchantment.addEnchantment(player, event);
    } 
  }
  
  @EventHandler
  public void combineXPBook(InventoryClickEvent event) {
    Player player = (Player)event.getWhoClicked();
    if (event.getClickedInventory() == null)
      return; 
    if (event.getClickedInventory().getType() != InventoryType.PLAYER)
      return; 
    if (event.getCursor().getType() != Material.ENCHANTED_BOOK)
      return; 
    if (player.getInventory().getItem(event.getSlot()) == null)
      return; 
    if (!HypixelEnchantment.enchantabilityMap.containsKey(new ItemStack(player.getInventory().getItem(event.getSlot()).getType(), 1)))
      return; 
    EnchantmentStorageMeta meta = (EnchantmentStorageMeta)event.getCursor().getItemMeta();
    Map<Enchantment, Integer> enchants = meta.getStoredEnchants();
    ArrayList<Enchantment> appliedEnchants = new ArrayList<>();
    ItemMeta meta2 = player.getInventory().getItem(event.getSlot()).getItemMeta();
    label41: for (Enchantment e : enchants.keySet()) {
      if (e.canEnchantItem(player.getInventory().getItem(event.getSlot()))) {
        if (meta2.hasEnchant(e)) {
          if (((Integer)enchants.get(e)).intValue() >= meta2.getEnchantLevel(e)) {
            if (meta.getEnchantLevel(e) == ((Integer)enchants.get(e)).intValue()) {
              player.getInventory().getItem(event.getSlot()).addEnchantment(e, ((Integer)enchants.get(e)).intValue());
            } else {
              if (((Integer)enchants.get(e)).intValue() + 1 > e.getMaxLevel())
                continue; 
              player.getInventory().getItem(event.getSlot()).addEnchantment(e, ((Integer)enchants.get(e)).intValue() + 1);
            } 
            appliedEnchants.add(e);
          } 
          continue;
        } 
        for (Enchantment e2 : meta2.getEnchants().keySet()) {
          if (e.conflictsWith(e2))
            continue label41; 
        } 
        player.getInventory().getItem(event.getSlot()).addUnsafeEnchantment(e, ((Integer)enchants.get(e)).intValue());
        appliedEnchants.add(e);
      } 
    } 
    if (appliedEnchants.isEmpty())
      return; 
    player.getInventory().setItem(event.getSlot(), HypixelEnchantment.addEnchantmentLore(player.getInventory().getItem(event.getSlot())));
    event.setCancelled(true);
    player.setItemOnCursor(null);
  }
  
  @EventHandler
  public void onEnchantmentClick(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (event.getClickedBlock() == null)
      return; 
    if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
      return; 
    if (!event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE))
      return; 
    event.setCancelled(true);
    ItemStack YELLOW_DISPLAY_PANE = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)4);
    ItemMeta meta = YELLOW_DISPLAY_PANE.getItemMeta();
    meta.setDisplayName(");
    YELLOW_DISPLAY_PANE.setItemMeta(meta);
    ItemStack BLACK_DISPLAY_PANE = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15);
    meta = BLACK_DISPLAY_PANE.getItemMeta();
    meta.setDisplayName(");
    BLACK_DISPLAY_PANE.setItemMeta(meta);
    ItemStack RED_DISPLAY_PANE = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);
    meta = RED_DISPLAY_PANE.getItemMeta();
    meta.setDisplayName(");
    RED_DISPLAY_PANE.setItemMeta(meta);
    ItemStack BARRIER_DISPLAY = new ItemStack(Material.BARRIER, 1);
    meta = BARRIER_DISPLAY.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("exit")));
    BARRIER_DISPLAY.setItemMeta(meta);
    Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', getConfig().getString("inventory-name")));
    ItemStack[] items = { 
        YELLOW_DISPLAY_PANE, BLACK_DISPLAY_PANE, RED_DISPLAY_PANE, RED_DISPLAY_PANE, RED_DISPLAY_PANE, RED_DISPLAY_PANE, RED_DISPLAY_PANE, BLACK_DISPLAY_PANE, YELLOW_DISPLAY_PANE, BLACK_DISPLAY_PANE, 
        BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, RED_DISPLAY_PANE, new ItemStack(Material.AIR, 1), RED_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, 
        BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, this.NOENCHANTMENT, 
        BLACK_DISPLAY_PANE, this.NOENCHANTMENT, BLACK_DISPLAY_PANE, this.NOENCHANTMENT, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, 
        BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, YELLOW_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BARRIER_DISPLAY, 
        BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, BLACK_DISPLAY_PANE, YELLOW_DISPLAY_PANE };
    inv.setContents(items);
    player.setVelocity(new Vector(0, 0, 0));
    player.openInventory(inv);
    this.playerHypixelEnchantmentMap.put(player, new HypixelEnchantment());
  }
  
  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Player player = (Player)event.getPlayer();
    if (event.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', getConfig().getString("inventory-name")))) {
      if (event.getInventory().getItem(13) == null)
        return; 
      if (player.getInventory().firstEmpty() == -1) {
        player.getLocation().getWorld().dropItemNaturally(player.getLocation(), event.getInventory().getItem(13));
      } else {
        player.getInventory().addItem(new ItemStack[] { event.getInventory().getItem(13) });
      } 
      this.playerHypixelEnchantmentMap.remove(player);
    } 
  }
  
  public static double randInt(double min, double max) {
    double x = (int)(Math.random() * (max - min + 1.0D)) + min;
    return x;
  }
  
  public static double randFloat(double min, double max) {
    double x = Math.random() * (max - min + 1.0D) + min;
    return x;
  }
  
  public static String getNumeral(int Int) {
    LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<>();
    roman_numerals.put("M", Integer.valueOf(1000));
    roman_numerals.put("CM", Integer.valueOf(900));
    roman_numerals.put("D", Integer.valueOf(500));
    roman_numerals.put("CD", Integer.valueOf(400));
    roman_numerals.put("C", Integer.valueOf(100));
    roman_numerals.put("XC", Integer.valueOf(90));
    roman_numerals.put("L", Integer.valueOf(50));
    roman_numerals.put("XL", Integer.valueOf(40));
    roman_numerals.put("X", Integer.valueOf(10));
    roman_numerals.put("IX", Integer.valueOf(9));
    roman_numerals.put("V", Integer.valueOf(5));
    roman_numerals.put("IV", Integer.valueOf(4));
    roman_numerals.put("I", Integer.valueOf(1));
    String res = "";
    for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
      int matches = Int / ((Integer)entry.getValue()).intValue();
      res = res + repeat(entry.getKey(), matches);
      Int %= ((Integer)entry.getValue()).intValue();
    } 
    return res;
  }
  
  public static String repeat(String s, int n) {
    if (s == null)
      return null; 
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++)
      sb.append(s); 
    return sb.toString();
  }
}
