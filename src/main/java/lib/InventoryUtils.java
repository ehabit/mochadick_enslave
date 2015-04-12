package lib;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
//import thaumcraft.api.ThaumcraftApiHelper;
//import thaumcraft.api.aspects.Aspect;
//import thaumcraft.common.items.wands.ItemWandCasting;
//import thaumcraft.common.tiles.TileArcaneWorkbench;
//import thaumcraft.common.tiles.TileResearchTable;

public class InventoryUtils
{
  public static ItemStack placeItemStackIntoInventory(ItemStack stack, IInventory inventory, int side, boolean doit)
  {
    ItemStack itemstack = stack.copy();
    ItemStack itemstack1 = insertStack(inventory, itemstack, side, doit);

    if ((itemstack1 == null) || (itemstack1.stackSize == 0))
    {
      if (doit) inventory.markDirty();
      return null;
    }
    stack = itemstack1;
    return stack.copy();
  }
  
  public static boolean areItemsEqual(ItemStack s1, ItemStack s2)
  {
    if ((s1.isItemStackDamageable()) && (s2.isItemStackDamageable()))
    {
      return s1.getItem() == s2.getItem();
    }
    return (s1.getItem() == s2.getItem()) && (s1.getItemDamage() == s2.getItemDamage());
  }
  
  public static boolean containsMatch(boolean strict, ItemStack[] inputs, ItemStack[] targets)
  {
    for (ItemStack input : inputs)
    {
      for (ItemStack target : targets)
      {
        if (itemMatches(target, input, strict))
        {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem, ItemStack recipeItem) {
    if ((recipeItem == null) || (slotItem == null)) return false;
    if ((recipeItem.stackTagCompound != null) && (slotItem.stackTagCompound == null)) return false;
    if (recipeItem.stackTagCompound == null) return true;

    Iterator iterator = recipeItem.stackTagCompound.func_150296_c().iterator();
    while (iterator.hasNext()) {
      String s = (String)iterator.next();
      if (slotItem.stackTagCompound.hasKey(s)) {
        if (!slotItem.stackTagCompound.getTag(s).toString().equals(recipeItem.stackTagCompound.getTag(s).toString()))
        {
          return false;
        }
      }
      else return false;

    }

    return true;
  }
  
  

  public static boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
  {
    if (((input == null) && (target != null)) || ((input != null) && (target == null)))
    {
      return false;
    }
    return (target.getItem() == input.getItem()) && (((target.getItemDamage() == 32767) && (!strict)) || (target.getItemDamage() == input.getItemDamage()));
  }

  public static ItemStack insertStack(IInventory inventory, ItemStack stack1, int side, boolean doit)
  {
    if (((inventory instanceof ISidedInventory)) && (side > -1))
    {
      ISidedInventory isidedinventory = (ISidedInventory)inventory;
      int[] aint = isidedinventory.getAccessibleSlotsFromSide(side);

      if (aint != null) {
        for (int j = 0; (j < aint.length) && (stack1 != null) && (stack1.stackSize > 0); j++)
        {
          if ((inventory.getStackInSlot(aint[j]) != null) && (inventory.getStackInSlot(aint[j]).isItemEqual(stack1)))
            stack1 = attemptInsertion(inventory, stack1, aint[j], side, doit);
          if ((stack1 == null) || (stack1.stackSize == 0))
            break;
        }
      }
      if ((aint != null) && (stack1 != null) && (stack1.stackSize > 0))
        for (int j = 0; (j < aint.length) && (stack1 != null) && (stack1.stackSize > 0); j++)
        {
          stack1 = attemptInsertion(inventory, stack1, aint[j], side, doit);
          if ((stack1 == null) || (stack1.stackSize == 0))
            break;
        }
    }
    else {
      int k = inventory.getSizeInventory();

      for (int l = 0; (l < k) && (stack1 != null) && (stack1.stackSize > 0); l++)
      {
        if ((inventory.getStackInSlot(l) != null) && (inventory.getStackInSlot(l).isItemEqual(stack1)))
          stack1 = attemptInsertion(inventory, stack1, l, side, doit);
        if ((stack1 == null) || (stack1.stackSize == 0))
          break;
      }
      if ((stack1 != null) && (stack1.stackSize > 0))
      {
        TileEntityChest dc = null;
        if ((inventory instanceof TileEntity)) {
          dc = getDoubleChest((TileEntity)inventory);
          if (dc != null) {
            int k2 = dc.getSizeInventory();

            for (int l = 0; (l < k2) && (stack1 != null) && (stack1.stackSize > 0); l++)
            {
              if ((dc.getStackInSlot(l) != null) && (dc.getStackInSlot(l).isItemEqual(stack1)))
                stack1 = attemptInsertion(dc, stack1, l, side, doit);
              if ((stack1 == null) || (stack1.stackSize == 0))
                break;
            }
          }
        }
        if ((stack1 != null) && (stack1.stackSize > 0)) {
          for (int l = 0; (l < k) && (stack1 != null) && (stack1.stackSize > 0); l++)
          {
            stack1 = attemptInsertion(inventory, stack1, l, side, doit);
            if ((stack1 == null) || (stack1.stackSize == 0))
              break;
          }
          if ((stack1 != null) && (stack1.stackSize > 0) && (dc != null)) {
            int k2 = dc.getSizeInventory();
            for (int l = 0; (l < k2) && (stack1 != null) && (stack1.stackSize > 0); l++)
            {
              if ((dc.getStackInSlot(l) != null) && (dc.getStackInSlot(l).isItemEqual(stack1)))
                stack1 = attemptInsertion(dc, stack1, l, side, doit);
              if ((stack1 == null) || (stack1.stackSize == 0))
                break;
            }
          }
        }
      }
    }
    if ((stack1 != null) && (stack1.stackSize == 0))
    {
      stack1 = null;
    }

    return stack1;
  }

  private static ItemStack attemptInsertion(IInventory inventory, ItemStack stack, int slot, int side, boolean doit)
  {
    ItemStack slotStack = inventory.getStackInSlot(slot);
    if (canInsertItemToInventory(inventory, stack, slot, side))
    {
      boolean flag = false;

      if (slotStack == null)
      {
        if (inventory.getInventoryStackLimit() < stack.stackSize) {
          ItemStack in = stack.splitStack(inventory.getInventoryStackLimit());
          if (doit) inventory.setInventorySlotContents(slot, in); 
        }
        else
        {
          if (doit) inventory.setInventorySlotContents(slot, stack);
          stack = null;
        }
        flag = true;
      }
      else if (areItemStacksEqualStrict(slotStack, stack))
      {
        int k = Math.min(inventory.getInventoryStackLimit() - slotStack.stackSize, stack.getMaxStackSize() - slotStack.stackSize);

        int l = Math.min(stack.stackSize, k);
        stack.stackSize -= l;
        if (doit) slotStack.stackSize += l;
        flag = l > 0;
      }

      if ((flag) && (doit))
      {
        if ((inventory instanceof TileEntityHopper))
        {
          ((TileEntityHopper)inventory).func_145896_c(8);
          inventory.markDirty();
        }

        inventory.markDirty();
      }

    }

    return stack;
  }

  public static ItemStack getFirstItemInInventory(IInventory inventory, int size, int side, boolean doit) {
    ItemStack stack1 = null;

    if (((inventory instanceof ISidedInventory)) && (side > -1))
    {
      ISidedInventory isidedinventory = (ISidedInventory)inventory;
      int[] aint = isidedinventory.getAccessibleSlotsFromSide(side);

      for (int j = 0; j < aint.length; j++)
      {
        if ((stack1 == null) && (inventory.getStackInSlot(aint[j]) != null)) {
          stack1 = inventory.getStackInSlot(aint[j]).copy();
          stack1.stackSize = size;
        }
        if (stack1 != null) stack1 = attemptExtraction(inventory, stack1, aint[j], side, false, false, false, doit);
        if (stack1 != null)
          break;
      }
    }
    else {
      int k = inventory.getSizeInventory();

      for (int l = 0; l < k; l++)
      {
        if ((stack1 == null) && (inventory.getStackInSlot(l) != null)) {
          stack1 = inventory.getStackInSlot(l).copy();
          stack1.stackSize = size;
        }
        if (stack1 != null) stack1 = attemptExtraction(inventory, stack1, l, side, false, false, false, doit);
        if (stack1 != null)
          break;
      }
    }
    if ((stack1 == null) || (stack1.stackSize == 0))
    {
      if (doit) inventory.markDirty();
      return null;
    }

    return stack1.copy();
  }

  public static boolean inventoryContains(IInventory inventory, ItemStack stack, int side, boolean useOre, boolean ignoreDamage, boolean ignoreNBT)
  {
    return extractStack(inventory, stack, side, useOre, ignoreDamage, ignoreNBT, false) != null;
  }

  public static ItemStack extractStack(IInventory inventory, ItemStack stack1, int side, boolean useOre, boolean ignoreDamage, boolean ignoreNBT, boolean doit)
  {
    ItemStack outStack = null;
    if (((inventory instanceof ISidedInventory)) && (side > -1))
    {
      ISidedInventory isidedinventory = (ISidedInventory)inventory;
      int[] aint = isidedinventory.getAccessibleSlotsFromSide(side);

      for (int j = 0; (j < aint.length) && (stack1 != null) && (stack1.stackSize > 0) && (outStack == null); j++)
      {
        outStack = attemptExtraction(inventory, stack1, aint[j], side, useOre, ignoreDamage, ignoreNBT, doit);
      }
    }
    else
    {
      int k = inventory.getSizeInventory();

      for (int l = 0; (l < k) && (stack1 != null) && (stack1.stackSize > 0) && (outStack == null); l++)
      {
        outStack = attemptExtraction(inventory, stack1, l, side, useOre, ignoreDamage, ignoreNBT, doit);
      }
    }

    if ((outStack == null) || (outStack.stackSize == 0))
    {
      return null;
    }

    return outStack.copy();
  }

  public static ItemStack attemptExtraction(IInventory inventory, ItemStack stack, int slot, int side, boolean useOre, boolean ignoreDamage, boolean ignoreNBT, boolean doit)
  {
    ItemStack slotStack = inventory.getStackInSlot(slot);
    ItemStack outStack = stack.copy();

    if (canExtractItemFromInventory(inventory, slotStack, slot, side))
    {
      boolean flag = false;

      if (areItemStacksEqual(slotStack, stack, useOre, ignoreDamage, ignoreNBT))
      {
        outStack = slotStack.copy();
        outStack.stackSize = stack.stackSize;
        int k = stack.stackSize - slotStack.stackSize;

        if (k >= 0) {
          outStack.stackSize -= k;
          if (doit) {
            slotStack = null;
            inventory.setInventorySlotContents(slot, null);
          }
        }
        else if (doit) {
          slotStack.stackSize -= outStack.stackSize;
          inventory.setInventorySlotContents(slot, slotStack);
        }

        flag = true;
      }
      else
      {
        return null;
      }

      if ((flag) && (doit))
      {
        inventory.markDirty();
      }
    } else {
      return null;
    }

    return outStack;
  }

  public static boolean canInsertItemToInventory(IInventory inventory, ItemStack stack1, int par2, int par3)
  {
    return (stack1 != null) && (inventory.isItemValidForSlot(par2, stack1)) && ((!(inventory instanceof ISidedInventory)) || (((ISidedInventory)inventory).canInsertItem(par2, stack1, par3)));
  }

  public static boolean canExtractItemFromInventory(IInventory inventory, ItemStack stack1, int par2, int par3)
  {
    return (stack1 != null) && ((!(inventory instanceof ISidedInventory)) || (((ISidedInventory)inventory).canExtractItem(par2, stack1, par3)));
  }

  public static boolean compareMultipleItems(ItemStack c1, ItemStack[] c2)
  {
    if ((c1 == null) || (c1.stackSize <= 0)) return false;
    for (ItemStack is : c2) {
      if ((is != null) && (c1.isItemEqual(is)) && (ItemStack.areItemStackTagsEqual(c1, is))) return true;
    }
    return false;
  }

  public static boolean areItemStacksEqualStrict(ItemStack stack0, ItemStack stack1) {
    return areItemStacksEqual(stack0, stack1, false, false, false);
  }

  public static boolean areItemStacksEqualForCrafting(ItemStack stack0, ItemStack stack1, boolean useOre, boolean ignoreDamage, boolean ignoreNBT)
  {
    if ((stack0 == null) && (stack1 != null)) return false;
    if ((stack0 != null) && (stack1 == null)) return false;
    if ((stack0 == null) && (stack1 == null)) return true;

    if (useOre) {
      int od = OreDictionary.getOreID(stack0);
      if (od != -1) {
        ItemStack[] ores = (ItemStack[])OreDictionary.getOres(Integer.valueOf(od)).toArray(new ItemStack[0]);
        if (containsMatch(false, new ItemStack[] { stack1 }, ores)) {
          return true;
        }
      }
    }
    boolean t1 = true;
    if (!ignoreNBT) {
      t1 = areItemStackTagsEqualForCrafting(stack0, stack1);
    }

    boolean t2 = stack0.getItemDamage() != stack1.getItemDamage();
    if ((ignoreDamage) && (stack0.isItemStackDamageable()) && (stack1.isItemStackDamageable())) {
      t2 = false;
    }

    if ((t2) && (ignoreDamage) && ((stack0.getItemDamage() == 32767) || (stack1.getItemDamage() == 32767)))
    {
      t2 = false;
    }

    return stack0.stackSize > stack0.getMaxStackSize() ? false : t2 ? false : stack0.getItem() != stack1.getItem() ? false : t1;
  }

  public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, boolean useOre, boolean ignoreDamage, boolean ignoreNBT)
  {
    if ((stack0 == null) && (stack1 != null)) return false;
    if ((stack0 != null) && (stack1 == null)) return false;
    if ((stack0 == null) && (stack1 == null)) return true;

    if (useOre) {
      int od = OreDictionary.getOreID(stack0);
      if (od != -1) {
        ItemStack[] ores = (ItemStack[])OreDictionary.getOres(Integer.valueOf(od)).toArray(new ItemStack[0]);
        if (containsMatch(false, new ItemStack[] { stack1 }, ores)) {
          return true;
        }
      }
    }
    boolean t1 = true;
    if (!ignoreNBT) {
      t1 = ItemStack.areItemStackTagsEqual(stack0, stack1);
    }

    boolean t2 = stack0.getItemDamage() != stack1.getItemDamage();
    if ((ignoreDamage) && (stack0.isItemStackDamageable()) && (stack1.isItemStackDamageable())) {
      t2 = false;
    }

    if ((t2) && (ignoreDamage) && ((stack0.getItemDamage() == 32767) || (stack1.getItemDamage() == 32767)))
    {
      t2 = false;
    }

    return t2 ? false : stack0.getItem() != stack1.getItem() ? false : t1;
  }

  public static boolean consumeInventoryItem(EntityPlayer player, Item item, int md)
  {
    for (int var2 = 0; var2 < player.inventory.mainInventory.length; var2++)
    {
      if ((player.inventory.mainInventory[var2] != null) && (player.inventory.mainInventory[var2].getItem() == item) && (player.inventory.mainInventory[var2].getItemDamage() == md))
      {
        if (--player.inventory.mainInventory[var2].stackSize <= 0)
        {
          player.inventory.mainInventory[var2] = null;
        }
        return true;
      }
    }

    return false;
  }

  public static void dropItems(World world, int x, int y, int z) {
    Random rand = new Random();
    int md = world.getBlockMetadata(x, y, z);
    TileEntity tileEntity = world.getTileEntity(x, y, z);
    if (!(tileEntity instanceof IInventory)) {
      return;
    }
    IInventory inventory = (IInventory)tileEntity;

    for (int i = 0; i < inventory.getSizeInventory(); i++)
      if (true) {
        ItemStack item = inventory.getStackInSlot(i);

        if ((item != null) && (item.stackSize > 0)) {
          float rx = rand.nextFloat() * 0.8F + 0.1F;
          float ry = rand.nextFloat() * 0.8F + 0.1F;
          float rz = rand.nextFloat() * 0.8F + 0.1F;

          EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

          if (item.hasTagCompound()) {
            entityItem.getEntityItem().setTagCompound((NBTTagCompound)item.getTagCompound().copy());
          }

          float factor = 0.05F;
          entityItem.motionX = (rand.nextGaussian() * factor);
          entityItem.motionY = (rand.nextGaussian() * factor + 0.2000000029802322D);
          entityItem.motionZ = (rand.nextGaussian() * factor);
          world.spawnEntityInWorld(entityItem);
          inventory.setInventorySlotContents(i, null);
        }
      }
  }

  public static void dropItemsAtEntity(World world, int x, int y, int z, Entity entity) {
    Random rand = new Random();
    int md = world.getBlockMetadata(x, y, z);
    TileEntity tileEntity = world.getTileEntity(x, y, z);
    if (!(tileEntity instanceof IInventory)) {
      return;
    }
    IInventory inventory = (IInventory)tileEntity;

    for (int i = 0; i < inventory.getSizeInventory(); i++)
      if (true) {
        ItemStack item = inventory.getStackInSlot(i);

        if ((item != null) && (item.stackSize > 0))
        {
          EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY + entity.getEyeHeight() / 2.0F, entity.posZ, item.copy());

          world.spawnEntityInWorld(entityItem);
          inventory.setInventorySlotContents(i, null);
        }
      }
  }

 

  public static int isPlayerCarrying(EntityPlayer player, ItemStack stack)
  {
    for (int var2 = 0; var2 < player.inventory.mainInventory.length; var2++)
    {
      if ((player.inventory.mainInventory[var2] != null) && (player.inventory.mainInventory[var2].isItemEqual(stack)))
      {
        return var2;
      }
    }

    return -1;
  }

  public static ItemStack damageItem(int par1, ItemStack stack, World worldObj) {
    if (stack.isItemStackDamageable())
    {
      if (par1 > 0)
      {
        int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
        int var4 = 0;

        for (int var5 = 0; (var3 > 0) && (var5 < par1); var5++)
        {
          if (EnchantmentDurability.negateDamage(stack, var3, worldObj.rand))
          {
            var4++;
          }
        }

        par1 -= var4;

        if (par1 <= 0)
        {
          return stack;
        }
      }

      stack.setItemDamage(stack.getItemDamage() + par1);

      if (stack.getItemDamage() > stack.getMaxDamage())
      {
        stack.stackSize -= 1;
        if (stack.stackSize < 0)
        {
          stack.stackSize = 0;
        }

        stack.setItemDamage(0);
      }
    }

    return stack;
  }

  public static void dropItemsWithChance(World world, int x, int y, int z, float chance, int fortune, ArrayList<ItemStack> items) {
    for (ItemStack item : items)
    {
      if ((world.rand.nextFloat() <= chance) && (item.stackSize > 0))
      {
        if ((!world.isRemote) && (world.getGameRules().getGameRuleBooleanValue("doTileDrops")))
        {
          float var6 = 0.7F;
          double var7 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
          double var9 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
          double var11 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
          EntityItem var13 = new EntityItem(world, x + var7, y + var9, z + var11, item);
          var13.delayBeforeCanPickup = 10;
          world.spawnEntityInWorld(var13);
        }
      }
    }
  }

  public static TileEntityChest getDoubleChest(TileEntity tile) {
    if ((tile != null) && ((tile instanceof TileEntityChest))) {
      if (((TileEntityChest)tile).adjacentChestXNeg != null) return ((TileEntityChest)tile).adjacentChestXNeg;
      if (((TileEntityChest)tile).adjacentChestXPos != null) return ((TileEntityChest)tile).adjacentChestXPos;
      if (((TileEntityChest)tile).adjacentChestZNeg != null) return ((TileEntityChest)tile).adjacentChestZNeg;
      if (((TileEntityChest)tile).adjacentChestZPos != null) return ((TileEntityChest)tile).adjacentChestZPos;
    }
    return null;
  }

  public static ItemStack cycleItemStack(Object input) {
    ItemStack it = null;
    if ((input instanceof ItemStack)) {
      it = (ItemStack)input;
      if ((it.getItemDamage() == 32767) && (it.getItem().getHasSubtypes())) {
        List q = new ArrayList();
        it.getItem().getSubItems(it.getItem(), it.getItem().getCreativeTab(), q);
        if ((q != null) && (q.size() > 0)) {
          int md = (int)(System.currentTimeMillis() / 1000L % q.size());
          ItemStack it2 = new ItemStack(it.getItem(), 1, md);
          it2.setTagCompound(it.getTagCompound());
          it = it2;
        }
      }
      else if ((it.getItemDamage() == 32767) && (it.isItemStackDamageable())) {
        int md = (int)(System.currentTimeMillis() / 10L % it.getMaxDamage());
        ItemStack it2 = new ItemStack(it.getItem(), 1, md);
        it2.setTagCompound(it.getTagCompound());
        it = it2;
      }

    }
    else if ((input instanceof ArrayList)) {
      ArrayList q = (ArrayList)input;
      if ((q != null) && (q.size() > 0)) {
        int idx = (int)(System.currentTimeMillis() / 1000L % q.size());
        it = cycleItemStack(q.get(idx));
      }
    }
    else if ((input instanceof String)) {
      ArrayList q = OreDictionary.getOres((String)input);
      if ((q != null) && (q.size() > 0)) {
        int idx = (int)(System.currentTimeMillis() / 1000L % q.size());
        it = cycleItemStack(q.get(idx));
      }
    }

    return it;
  }
}