import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class HypixelEnchantment {
  HashMap<Enchantment, Integer> enchantmentWeights = new HashMap<Enchantment, Integer>() {
    
    };
  
  static HashMap<ItemStack, Integer> enchantabilityMap = new HashMap<ItemStack, Integer>() {
    
    };
  
  HashMap<Enchantment, Integer> availableEnchants = new HashMap<>();
  
  ArrayList<Enchantment> availableEnchantsSet;
  
  int topEnchantLevel = 0;
  
  int midEnchantLevel;
  
  Enchantment appliedEnchant = null;
  
  int medEnchLevel = 0;
  
  int w;
  
  ItemStack item;
  
  int botEnchXP = 0;
  
  int medEnchXP = 0;
  
  int topEnchXP = 0;
  
  int totalWeight;
  
  public static ItemStack addEnchantmentLore(ItemStack item) {
    if (item.getType() == Material.ENCHANTED_BOOK) {
      List<String> lore;
      EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
      if (meta.getLore() != null) {
        lore = meta.getLore();
      } else {
        lore = new ArrayList<>();
      } 
      meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
      meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
      meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
      for (Enchantment e2 : Enchantment.values()) {
        lore.removeIf(line -> line.startsWith(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getConfigurationSection(e2.getName()).getString("name"))));
        List<String> description2 = Main.getInstance().getConfig().getConfigurationSection(e2.getName()).getStringList("description");
        List<String> description = new ArrayList<>();
        for (String descLine : description2) {
          descLine = "+ descLine;
          descLine = ChatColor.translateAlternateColorCodes('&', descLine);
          String finalDescLine = descLine;
          lore.remove(descLine);
          lore.removeIf(line -> line.startsWith(finalDescLine));
          lore.removeIf(line -> line.equals(finalDescLine));
          description.add(descLine);
        } 
        description.add("");
        lore.removeAll(description);
      } 
      for (Enchantment e : Enchantment.values()) {
        if (meta.hasStoredEnchant(e)) {
          List<String> loreChunk = new ArrayList<>();
          loreChunk.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getConfigurationSection(e.getName()).getString("name")) + " " + Main.getNumeral(meta.getStoredEnchantLevel(e)));
          for (String desc : Main.getInstance().getConfig().getConfigurationSection(e.getName()).getStringList("description"))
            loreChunk.add("+ ChatColor.translateAlternateColorCodes('&', desc)); 
          loreChunk.add("");
          lore.addAll(0, loreChunk);
        } 
      } 
      meta.setLore(lore);
      item.setItemMeta((ItemMeta)meta);
    } else {
      List<String> lore;
      ItemMeta meta = item.getItemMeta();
      if (meta.getLore() != null) {
        lore = meta.getLore();
      } else {
        lore = new ArrayList<>();
      } 
      meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
      meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
      for (Enchantment e2 : Enchantment.values()) {
        lore.removeIf(line -> line.startsWith(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getConfigurationSection(e2.getName()).getString("name"))));
        List<String> description = new ArrayList<>();
        List<String> description2 = Main.getInstance().getConfig().getConfigurationSection(e2.getName()).getStringList("description");
        for (String descLine : description2) {
          descLine = "+ descLine;
          descLine = ChatColor.translateAlternateColorCodes('&', descLine);
          String finalDescLine = descLine;
          lore.remove(descLine);
          lore.removeIf(line -> line.startsWith(finalDescLine));
          lore.removeIf(line -> line.equals(finalDescLine));
          description.add(descLine);
        } 
        description.add("");
        lore.removeAll(description);
      } 
      for (Enchantment e : Enchantment.values()) {
        if (meta.hasEnchant(e)) {
          List<String> loreChunk = new ArrayList<>();
          loreChunk.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getConfigurationSection(e.getName()).getString("name")) + " " + Main.getNumeral(meta.getEnchantLevel(e)));
          for (String desc : Main.getInstance().getConfig().getConfigurationSection(e.getName()).getStringList("description"))
            loreChunk.add("+ ChatColor.translateAlternateColorCodes('&', desc)); 
          loreChunk.add("");
          lore.addAll(0, loreChunk);
        } 
      } 
      meta.setLore(lore);
      item.setItemMeta(meta);
    } 
    return item;
  }
  
  public void setItem(Player player) {
    Inventory inv = player.getOpenInventory().getTopInventory();
    if (inv.getItem(13) == null)
      inv.setItem(13, new ItemStack(Material.AIR)); 
    ItemStack NOENCHANTMENT = new ItemStack(Material.INK_SACK, 1, (short)8);
    ItemMeta meta = NOENCHANTMENT.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("noenchant")));
    NOENCHANTMENT.setItemMeta(meta);
    if (inv.getItem(13) == null) {
      inv.setItem(29, NOENCHANTMENT);
      inv.setItem(31, NOENCHANTMENT);
      inv.setItem(33, NOENCHANTMENT);
      return;
    } 
    if (inv.getItem(13).getItemMeta().hasEnchants()) {
      inv.setItem(29, NOENCHANTMENT);
      inv.setItem(31, NOENCHANTMENT);
      inv.setItem(33, NOENCHANTMENT);
      return;
    } 
    if (enchantabilityMap.containsKey(new ItemStack(inv.getItem(13).getType(), inv.getItem(13).getAmount()))) {
      this.availableEnchants = new HashMap<>();
      this.item = inv.getItem(13);
      int bookshelvesInRange = 15;
      int baseEnchantLevel = (int)(Main.randInt(1.0D, 8.0D) + Math.floor((bookshelvesInRange / 2)) + Main.randInt(0.0D, bookshelvesInRange));
      int botEnchantLevelBase = Integer.max(baseEnchantLevel / 3, 1);
      int midEnchantLevelBase = baseEnchantLevel * 2 / 3 + 1;
      int topEnchantLevelBase = Integer.max(baseEnchantLevel, bookshelvesInRange * 2);
      if (!enchantabilityMap.containsKey(new ItemStack(this.item.getType())))
        return; 
      int enchantability = ((Integer)enchantabilityMap.get(new ItemStack(this.item.getType()))).intValue();
      this.topEnchantLevel = (int)Math.round((topEnchantLevelBase + Main.randInt(0.0D, (enchantability / 4)) + Main.randInt(0.0D, (enchantability / 4)) + 1.0D) * Main.randFloat(0.85D, 1.15D));
      this.midEnchantLevel = (int)Math.round((midEnchantLevelBase + Main.randInt(0.0D, (enchantability / 4)) + Main.randInt(0.0D, (enchantability / 4)) + 1.0D) * Main.randFloat(0.85D, 1.15D));
      if (this.item.getType() == Material.BOOK) {
        this.availableEnchants = this.enchantmentWeights;
      } else {
        for (Map.Entry<Enchantment, Integer> entry : this.enchantmentWeights.entrySet()) {
          Enchantment key = entry.getKey();
          Integer value = entry.getValue();
          if (key.canEnchantItem(this.item))
            this.availableEnchants.put(key, value); 
        } 
      } 
      if (this.availableEnchants.isEmpty())
        return; 
      this.totalWeight = 0;
      for (Integer weight : this.availableEnchants.values())
        this.totalWeight += weight.intValue(); 
      this.appliedEnchant = null;
      Set<Enchantment> availableEnchantsSet2 = this.availableEnchants.keySet();
      this.availableEnchantsSet = new ArrayList<>();
      this.availableEnchantsSet.addAll(availableEnchantsSet2);
      Collections.shuffle(this.availableEnchantsSet);
      this.w = (int)Main.randInt(0.0D, (this.totalWeight / 2));
      for (Enchantment possibleEnchant : this.availableEnchantsSet) {
        this.w -= ((Integer)this.enchantmentWeights.get(possibleEnchant)).intValue();
        if (this.w < 0) {
          this.appliedEnchant = possibleEnchant;
          break;
        } 
      } 
      assert this.appliedEnchant != null;
      this.medEnchLevel = (int)((int)Math.floor(this.appliedEnchant.getMaxLevel() / 2.0D) + Math.round(Main.randFloat(0.0D, 1.7D)));
      if (this.medEnchLevel > this.appliedEnchant.getMaxLevel())
        this.medEnchLevel = this.appliedEnchant.getMaxLevel(); 
      this.botEnchXP = (int)Main.randInt(10.0D, 15.0D);
      ItemStack xpb1 = new ItemStack(Material.EXP_BOTTLE);
      xpb1.setAmount(this.botEnchXP);
      ItemMeta xpb1m = xpb1.getItemMeta();
      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("at-least")));
      assert false;
      lore.add("* " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getConfigurationSection(this.appliedEnchant.getName()).getString("name")) + " " + Main.getNumeral(this.appliedEnchant.getStartLevel())));
      lore.add("");
      if (player.getLevel() >= this.botEnchXP) {
        lore.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("click-to-enchant")));
      } else {
        lore.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("not-enough-levels")));
      } 
      xpb1m.setLore(lore);
      xpb1m.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("enchant-item")) + ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("enchxpcolor")) + this.botEnchXP + ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("levels")));
      xpb1.setItemMeta(xpb1m);
      inv.setItem(29, xpb1);
      this.medEnchXP = (int)Main.randInt(15.0D, 30.0D);
      ItemStack xpb2 = new ItemStack(Material.EXP_BOTTLE);
      xpb2.setAmount(this.medEnchXP);
      ItemMeta xpb2m = xpb2.getItemMeta();
      List<String> lore2 = new ArrayList<>();
      lore2.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("at-least")));
      lore2.add("* " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getConfigurationSection(this.appliedEnchant.getName()).getString("name")) + " " + Main.getNumeral(this.medEnchLevel)));
      lore2.add("");
      if (player.getLevel() >= this.medEnchXP) {
        lore2.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("click-to-enchant")));
      } else {
        lore2.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("not-enough-levels")));
      } 
      xpb2m.setLore(lore2);
      xpb2m.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("enchant-item")) + ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("enchxpcolor")) + this.medEnchXP + ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("levels")));
      xpb2.setItemMeta(xpb2m);
      inv.setItem(31, xpb2);
      this.topEnchXP = 45;
      ItemStack xpb3 = new ItemStack(Material.EXP_BOTTLE);
      xpb3.setAmount(this.topEnchXP);
      ItemMeta xpb3m = xpb3.getItemMeta();
      List<String> lore3 = new ArrayList<>();
      lore3.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("at-least")));
      lore3.add("* " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getConfigurationSection(this.appliedEnchant.getName()).getString("name")) + " " + Main.getNumeral(this.appliedEnchant.getMaxLevel())));
      lore3.add("");
      if (player.getLevel() >= this.topEnchXP) {
        lore3.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("click-to-enchant")));
      } else {
        lore3.add(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("not-enough-levels")));
      } 
      xpb3m.setLore(lore3);
      xpb3m.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("enchant-item")) + ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("enchxpcolor")) + this.topEnchXP + ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("levels")));
      xpb3.setItemMeta(xpb3m);
      inv.setItem(33, xpb3);
    } else {
      inv.setItem(29, NOENCHANTMENT);
      inv.setItem(31, NOENCHANTMENT);
      inv.setItem(33, NOENCHANTMENT);
    } 
  }
  
  public void addEnchantment(Player player, InventoryClickEvent event) {
    ItemStack NOENCHANTMENT = new ItemStack(Material.INK_SACK, 1, (short)8);
    ItemMeta meta = NOENCHANTMENT.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("noenchant")));
    NOENCHANTMENT.setItemMeta(meta);
    Inventory inv = player.getOpenInventory().getTopInventory();
    if (event.getClickedInventory().getType().toString().equalsIgnoreCase("player"))
      return; 
    event.setCancelled(true);
    if (event.getCurrentItem().getType() == Material.INK_SACK)
      return; 
    this.item = inv.getItem(13);
    int probability = 0;
    switch (event.getSlot()) {
      case 29:
        if (player.getLevel() >= this.botEnchXP) {
          player.setLevel(player.getLevel() - this.botEnchXP);
          player.playSound(player.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("enchanted-sound")), 1.0F, 1.0F);
        } else {
          player.playSound(player.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("not-enough-levels-sound")), 1000.0F, 1.0F);
          return;
        } 
        if (this.item.getType() == Material.BOOK)
          this.item.setType(Material.ENCHANTED_BOOK); 
        if (this.item.getType() == Material.ENCHANTED_BOOK) {
          EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)this.item.getItemMeta();
          enchantmentStorageMeta.addStoredEnchant(this.appliedEnchant, this.appliedEnchant.getStartLevel(), false);
          this.item.setItemMeta((ItemMeta)enchantmentStorageMeta);
        } else {
          this.item.addEnchantment(this.appliedEnchant, this.appliedEnchant.getStartLevel());
        } 
        this.item = addEnchantmentLore(this.item);
        inv.setItem(13, this.item);
        return;
      case 31:
        if (player.getLevel() >= this.medEnchXP) {
          player.setLevel(player.getLevel() - this.medEnchXP);
          player.playSound(player.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("enchanted-sound")), 1.0F, 1.0F);
        } else {
          player.playSound(player.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("not-enough-levels-sound")), 1000.0F, 1.0F);
          return;
        } 
        if (this.item.getType() == Material.BOOK)
          this.item.setType(Material.ENCHANTED_BOOK); 
        if (this.item.getType() == Material.ENCHANTED_BOOK) {
          EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)this.item.getItemMeta();
          enchantmentStorageMeta.addStoredEnchant(this.appliedEnchant, this.medEnchLevel, false);
          this.item.setItemMeta((ItemMeta)enchantmentStorageMeta);
        } else {
          this.item.addEnchantment(this.appliedEnchant, this.medEnchLevel);
        } 
        this.item = addEnchantmentLore(this.item);
        inv.setItem(13, this.item);
        return;
      case 33:
        if (player.getLevel() >= this.topEnchXP) {
          player.setLevel(player.getLevel() - this.topEnchXP);
          player.playSound(player.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("enchanted-sound")), 1.0F, 1.0F);
        } else {
          player.playSound(player.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("not-enough-levels-sound")), 1000.0F, 1.0F);
          return;
        } 
        if (this.item.getType() == Material.BOOK)
          this.item.setType(Material.ENCHANTED_BOOK); 
        if (this.item.getType() == Material.ENCHANTED_BOOK) {
          EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)this.item.getItemMeta();
          enchantmentStorageMeta.addStoredEnchant(this.appliedEnchant, this.appliedEnchant.getMaxLevel(), false);
          this.item.setItemMeta((ItemMeta)enchantmentStorageMeta);
        } else {
          this.item.addEnchantment(this.appliedEnchant, this.appliedEnchant.getMaxLevel());
        } 
        probability = this.topEnchantLevel + 50;
        break;
    } 
    inv.setItem(29, NOENCHANTMENT);
    inv.setItem(31, NOENCHANTMENT);
    inv.setItem(33, NOENCHANTMENT);
    this.availableEnchants.remove(this.appliedEnchant);
    Enchantment appliedEnchant1 = this.appliedEnchant;
    Enchantment appliedEnchant2 = this.appliedEnchant;
    Set<Enchantment> availableEnchantsSet2 = this.availableEnchants.keySet();
    this.availableEnchantsSet = new ArrayList<>();
    this.availableEnchantsSet.addAll(availableEnchantsSet2);
    if (Main.randFloat(0.0D, 1.0D) < ((probability + 1) / 50)) {
      this.totalWeight = 0;
      for (Integer weight : this.availableEnchants.values())
        this.totalWeight += weight.intValue(); 
      this.w = (int)Main.randInt(0.0D, (this.totalWeight / 2));
      for (Enchantment possibleEnchant : this.availableEnchantsSet) {
        this.w -= ((Integer)this.enchantmentWeights.get(possibleEnchant)).intValue();
        if (this.w >= 0 || 
          possibleEnchant.conflictsWith(appliedEnchant1))
          continue; 
        appliedEnchant2 = possibleEnchant;
        this.appliedEnchant = possibleEnchant;
      } 
      if (this.item.getType() == Material.ENCHANTED_BOOK) {
        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)this.item.getItemMeta();
        enchantmentStorageMeta.addStoredEnchant(this.appliedEnchant, this.appliedEnchant.getMaxLevel(), false);
        this.item.setItemMeta((ItemMeta)enchantmentStorageMeta);
      } else {
        this.item.addEnchantment(this.appliedEnchant, this.appliedEnchant.getMaxLevel());
      } 
      this.availableEnchants.remove(this.appliedEnchant);
    } else {
      this.item = addEnchantmentLore(this.item);
      inv.setItem(13, this.item);
      return;
    } 
    availableEnchantsSet2 = this.availableEnchants.keySet();
    this.availableEnchantsSet = new ArrayList<>();
    this.availableEnchantsSet.addAll(availableEnchantsSet2);
    if (Main.randFloat(0.0D, 1.0D) < ((probability + 1) / 50)) {
      this.totalWeight = 0;
      for (Integer weight : this.availableEnchants.values())
        this.totalWeight += weight.intValue(); 
      this.w = (int)Main.randInt(0.0D, (this.totalWeight / 2));
      for (Enchantment possibleEnchant : this.availableEnchantsSet) {
        this.w -= ((Integer)this.enchantmentWeights.get(possibleEnchant)).intValue();
        if (this.w >= 0 || 
          possibleEnchant.conflictsWith(appliedEnchant1) || possibleEnchant.conflictsWith(appliedEnchant2))
          continue; 
        this.appliedEnchant = possibleEnchant;
      } 
      if (this.item.getType() == Material.ENCHANTED_BOOK) {
        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)this.item.getItemMeta();
        enchantmentStorageMeta.addStoredEnchant(this.appliedEnchant, this.appliedEnchant.getMaxLevel(), false);
        this.item.setItemMeta((ItemMeta)enchantmentStorageMeta);
      } else {
        this.item.addEnchantment(this.appliedEnchant, this.appliedEnchant.getMaxLevel());
      } 
    } 
    this.item = addEnchantmentLore(this.item);
    inv.setItem(13, this.item);
  }
}
