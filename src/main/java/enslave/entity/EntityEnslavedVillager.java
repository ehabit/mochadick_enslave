package enslave.entity;


import java.util.Random;

import lib.Utils;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import enslave.Enslave;
import enslave.entity.ai.AIHarvestCrops;
import enslave.entity.ai.AIHarvestLogs;
import enslave.entity.ai.AIOpenDoor;
import enslave.entity.ai.AIReturnHome;
import enslave.item.ItemWhip;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntityEnslavedVillager extends EntityWolf {
	
	public static final RegistryNamespaced itemRegistry = GameData.getItemRegistry();
	public Integer textureType;
	private static final Float modelHeight = 1.8F;
	private static final Float modelWidth = 0.6F;
	
	public static int slaveDelay = 5;
    public static int slaveIgnoreDelay = 10000;
    private int slaveStrength = 1;
    
	
	public int action = 0;
	public int leftArm = 0;
	public int rightArm = 0;
	public int healing = 0;
	

	private Item itemDrop = null;
	private int heldItemDamage;
	
	// revolt increases the longer a slave is held without supervision
	private int lashesTaken;
	private int hungerLevel;
	private int thirstLevel;
	private int revoltLevel;
	private boolean isRevolting;
	
	private int thoughtTick;
	
	private int lumberjackSkill;
	private int farmerSkill;
	private int gladiatorSkill;
	
	// Watchers need to sync client and server side for rendering purposes
	private final int TEXTURE_TYPE_WATCHER = 26;
	private final int REVOLT_LEVEL_WATCHER = 27;
	private final int HUNGER_WATCHER = 28;
//	private final int THIRST_WATCHER = 29;
	private final int LUMBERJACK_SKILL = 29;
	private final int FARMER_SKILL = 30;
	private final int GLADIATOR_SKILL = 31;
	
	
	
	public EntityEnslavedVillager(World world) {
		super(world);
		
		this.setSize(modelWidth, modelHeight);
		
		this.getNavigator().setAvoidsWater(true);
		this.FollowOwner();	
		
		this.dataWatcher.addObject(TEXTURE_TYPE_WATCHER, Integer.valueOf(0));
		this.dataWatcher.addObject(REVOLT_LEVEL_WATCHER, Integer.valueOf(0));
		this.dataWatcher.addObject(HUNGER_WATCHER, Integer.valueOf(0));
		this.dataWatcher.addObject(LUMBERJACK_SKILL, Integer.valueOf(0));
		this.dataWatcher.addObject(FARMER_SKILL, Integer.valueOf(0));
		this.dataWatcher.addObject(GLADIATOR_SKILL, Integer.valueOf(0));
		
		int randNum = this.rand.nextInt(2) + 1;
		this.textureType = randNum;
		this.textureType = this.getTextureType();
		
		this.lashesTaken = 0;
		this.lashesTaken = this.getLashesTaken();
		
		this.lumberjackSkill = 0;
		this.lumberjackSkill = this.getLumberjackSkill();
		
		this.farmerSkill = 0;
		this.farmerSkill = this.getFarmerSkill();
		
		this.gladiatorSkill = 0;
		this.gladiatorSkill = this.getGladiatorSkill();
		
		this.revoltLevel = 0;
		this.revoltLevel = this.getRevoltLevel();
		
		this.hungerLevel = 0;
		this.hungerLevel = this.getHungerLevel();
		
//		this.thirstLevel = 0;
		
		this.thoughtTick = 0;
		

		this.getSlaveStrength();
		this.setAIByHeldItem();
		
	}
	
	public int getTextureType() {
		return this.dataWatcher.getWatchableObjectInt(TEXTURE_TYPE_WATCHER);
	}
	
	public void setTextureType(int type) {
		this.textureType = type;
		this.dataWatcher.updateObject(TEXTURE_TYPE_WATCHER, Integer.valueOf(type));
	}	

	public static void mainRegistry(Class entityClass, String name) {
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		long seed = name.hashCode();
		Random rand = new Random(seed);
		int primaryColor = rand.nextInt() * 16777215;
		int secondaryColor = rand.nextInt() * 16777215;

		EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
		EntityRegistry.registerModEntity(entityClass, name, entityID, Enslave.instance, 64, 3, true);
		EntityList.entityEggs.put(Integer.valueOf(entityID), new EntityList.EntityEggInfo(entityID, primaryColor, secondaryColor));
	}
	
	public static void mainRegistry() {
		mainRegistry(EntityEnslavedVillager.class, "EnslavedVillager");
	}
	
	protected void clearAITasks() {
		tasks.taskEntries.clear();
		targetTasks.taskEntries.clear();
	}
	
	protected void setAIBase() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIOpenDoor(this, true));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(10, new EntityAILookIdle(this));
       
	}
	
	protected void setAILumberjack() {
		this.clearAITasks();
		this.setHomeArea((int) this.posX, (int) this.posY, (int) this.posZ, (int) this.getRange());
		this.tasks.addTask(1, new AIHarvestLogs(this));
		this.tasks.addTask(2, new AIReturnHome(this));
		this.setAIBase();
		this.aiSit.setSitting(false);	
	}
	
	protected void setAIFarmer() {
		this.clearAITasks();
		this.setHomeArea((int) this.posX, (int) this.posY, (int) this.posZ, (int) this.getRange());
		this.tasks.addTask(1, new AIHarvestCrops(this));
		this.tasks.addTask(2, new AIReturnHome(this));
		this.setAIBase();
		this.aiSit.setSitting(false);	
	}
	
	protected void setAIGladiator() {
		this.clearAITasks();
		this.setHomeArea((int) this.posX, (int) this.posY, (int) this.posZ, (int) this.getRange());
		
		// TODO Add Gladiator AI
		this.tasks.addTask(1, new EntityAILeapAtTarget(this, 0.4F));
	    this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0D, true));
	    this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityZombie.class, 200, false));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntitySkeleton.class, 200, false));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntitySpider.class, 200, false));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityCreeper.class, 200, false));	
		this.tasks.addTask(2, new AIReturnHome(this));
		this.setAIBase();
		this.aiSit.setSitting(false);	
	}
	
	protected void setAIRevolting() {
		this.clearAITasks();
		this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(1, new AIOpenDoor(this, true));
		this.tasks.addTask(1, new EntityAILeapAtTarget(this, 0.4F));
	    this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0D, true));
	    this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
	    this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 200, false));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityPlayer.class, 200, false));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.setPathToEntity((PathEntity)null);
        this.setTarget((Entity)this.getOwner());
        this.setAttackTarget((EntityLivingBase)this.getOwner());
        this.aiSit.setSitting(false);
	}
	
	protected void FollowOwner () {
		this.clearAITasks();
		this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.setAIBase();
	}
	
	@Override
	protected String getHurtSound() {
        return "mob.villager.hit";
    }
	
	@Override
	protected String getDeathSound() {
        return "mob.villager.death";
    }
	
	@Override
	protected String getLivingSound() {
        return "mob.villager.idle";
    }
	
	@Override
	public float getEyeHeight()  {
        return this.height * modelHeight;
    }
	
	protected void dropFewItems(boolean par1, int par2) {
		if (this.getHeldItem() != null) {
			this.dropHeldItem();
		}
		
		int random = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + par2);
		
		for (int k = 0; k < random; ++k) {
			if (k==2) {
				this.dropItem(Items.iron_ingot, 1);
			} else if (k == 1) {
				this.dropItem(Items.iron_ingot, 2);
			}
		}
		
	}
	
	public int getHeldItemDamage() {
		return this.heldItemDamage;
	}
	
	public void addToHeldItemDamage(int dmg) {
		this.heldItemDamage = this.heldItemDamage + dmg;
	}
	
	public void setHeldItemDamage(int dmg) {
		this.heldItemDamage = dmg;
	}
	
	public void setLumberjackSkill(int amount) {
		this.lumberjackSkill = amount;
		this.dataWatcher.updateObject(LUMBERJACK_SKILL, Integer.valueOf(amount));
	}
	
	public int getLumberjackSkill() {
		return this.dataWatcher.getWatchableObjectInt(LUMBERJACK_SKILL); 
	}
	
	public void setFarmerSkill(int amount) {
		this.farmerSkill = amount;
		this.dataWatcher.updateObject(FARMER_SKILL, Integer.valueOf(amount));
	}
	
	public int getFarmerSkill() {
		return this.dataWatcher.getWatchableObjectInt(FARMER_SKILL); 
	}
	
	public void setGladiatorSkill(int amount) {
		this.gladiatorSkill = amount;
		this.dataWatcher.updateObject(GLADIATOR_SKILL, Integer.valueOf(amount));
	}
	
	public int getGladiatorSkill() {
		return this.dataWatcher.getWatchableObjectInt(GLADIATOR_SKILL); 
	}
	
	public void setRevoltLevel(int amount) {
		this.revoltLevel = amount;
		this.dataWatcher.updateObject(REVOLT_LEVEL_WATCHER, Integer.valueOf(amount));
	}
	
	public int getRevoltLevel() {
		return this.dataWatcher.getWatchableObjectInt(REVOLT_LEVEL_WATCHER); 
	}
	
	public boolean shouldRevolt() {
		if (this.getRevoltLevel() >= 100) {
			return true;
		}
		return false;
	}

	
	public void revolt() {
		if (!this.isRevolting && this.shouldRevolt()) {
			this.isRevolting = true;
			this.setTamed(false);
			this.setAngry(true);
			this.setAIRevolting();
		}
	}
	
	
	public int getHungerLevel() {
		return this.dataWatcher.getWatchableObjectInt(HUNGER_WATCHER); 
	}
	
	public void setHungerLevel(int amount) {
		this.hungerLevel = amount;
		this.dataWatcher.updateObject(HUNGER_WATCHER, Integer.valueOf(amount));
	}
	
	public boolean isHungry() {
		if (this.getHungerLevel() >= 100) {
			return true;
		} 
		return false;
	}
	
//	public int getThirstLevel() {
//		return this.thirstLevel;
//	}
	
//	public void setThirstLevel(int amount) {
//		this.thirstLevel = amount;
//	}

	public void increaseSkillBasedOnHeldItem(int amount) {
		if (this.getHeldItem() != null) {
			if (this.getHeldItem().getItem() instanceof ItemAxe) {
				this.setLumberjackSkill(this.getLumberjackSkill() + amount);
			} else if (this.getHeldItem().getItem() instanceof ItemHoe) {
				this.setFarmerSkill(this.getFarmerSkill() + amount);
			} else if (this.getHeldItem().getItem() instanceof ItemSword) {
				this.setGladiatorSkill(this.getGladiatorSkill() + amount);
			}
		}
	}
	
	public static boolean spawnEnslavedVillager(World var0, double var2, double var4, double var6) {
        Object var8;

        var8 = new EntityEnslavedVillager(var0);

        if (var8 != null) {
            ((EntityEnslavedVillager)var8).setLocationAndAngles(var2, var4, var6, var0.rand.nextFloat() * 360.0F, 0.0F);
            var0.spawnEntityInWorld((EntityEnslavedVillager)var8);
        }
        return var8 != null;
    }
	
	public boolean isSupervised() {
		// Get Owner
		EntityPlayer owner = (EntityPlayer) this.getOwner();
				 
		if (owner != null) {
			if(this.canEntityBeSeen(owner)) {
//				Enslave.log.info("Owner is watching!");
				return true;
			} else {
//				Enslave.log.info("Owner is gone, time to dream of freedom!");
				return false;
			}
		}
		return false;
	}
	
	@Override
	protected Entity findPlayerToAttack() {
        EntityPlayer entityplayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        return entityplayer != null && this.canEntityBeSeen(entityplayer) ? entityplayer : null;
    }
	
	@Override
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (super.attackEntityFrom(p_70097_1_, p_70097_2_))
        {
            Entity entity = p_70097_1_.getEntity();

            if (this.riddenByEntity != entity && this.ridingEntity != entity)
            {
                if (entity != this)
                {
                    this.entityToAttack = entity;
                }

                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3D);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity p_70652_1_) {
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int i = 0;

        if (p_70652_1_ instanceof EntityLivingBase)
        {
            f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)p_70652_1_);
            i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)p_70652_1_);
        }

        boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag)
        {
            if (i > 0)
            {
                p_70652_1_.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0)
            {
                p_70652_1_.setFire(j * 4);
            }

            if (p_70652_1_ instanceof EntityLivingBase)
            {
                EnchantmentHelper.func_151384_a((EntityLivingBase)p_70652_1_, this);
            }

            EnchantmentHelper.func_151385_b(this, p_70652_1_);
        }

        return flag;
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
	@Override
    protected void attackEntity(Entity p_70785_1_, float p_70785_2_) {
        if (this.attackTime <= 0 && p_70785_2_ < 2.0F && p_70785_1_.boundingBox.maxY > this.boundingBox.minY && p_70785_1_.boundingBox.minY < this.boundingBox.maxY)
        {
            this.attackTime = 20;
            this.attackEntityAsMob(p_70785_1_);
        }
    }
	
	@Override
	public void onUpdate() {
		 super.onUpdate();
		 
		 if (this.thoughtTick >= 100) {
			 if (!this.isSupervised()) {
				 // slave is not supervised
				 this.setRevoltLevel(this.getRevoltLevel() + 3);
				 if (!this.worldObj.isRemote) {
					 Enslave.log.info("RevoltLevel: " + this.getRevoltLevel());
				 }
			 }
			 
			 if (this.isHungry()) {
				 this.setRevoltLevel(this.getRevoltLevel() + 5);
			 }
			 
			 
			 if (this.getRevoltLevel() >= 100) {
				 // past revolt threshold
				 this.revolt();
			 }
			 
			 // reset thought tick
			 this.thoughtTick = 0;
		 } else {
			 this.thoughtTick++;
		 } 
		 
		 
	}
	
	public void lashSlave(EntityPlayer player) {
		this.lashesTaken++;
		this.attackEntityFrom(DamageSource.generic, 1);
		this.setRevoltLevel(this.getRevoltLevel() + 1);
		
		player.swingItem();
		player.worldObj.playSoundAtEntity(player, "enslave:whip", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));        
        
		if (!this.worldObj.isRemote) {
			Enslave.log.info("Slave has been lashed " + this.lashesTaken + " times.");
		}
	}
	
	public int getLashesTaken() {
		return this.lashesTaken;
	}
	
	public void setLashesTaken(int lashes) {
		this.lashesTaken = lashes;
	}
	
	@Override
	public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();

        if (this.isTamed()) {
            if (itemstack != null) {
            	if (itemstack.getItem() instanceof ItemWhip) {
            		this.lashSlave(player);
            		
            		// players can steal slaves with a whip from current owner
            		// must be owner to leash slave and get them to follow you
                    this.setTarget((Entity)player);
                    if (!this.worldObj.isRemote) {
                        if (this.rand.nextInt(3) == 0) {
                        	this.func_152115_b(player.getUniqueID().toString());
                        	this.playTameEffect(true);
                        	this.worldObj.setEntityState(this, (byte)7);
                        }
                    }
            		
            	} else if (itemstack.getItem() instanceof ItemAxe) {
            		// if player interacts with axe, give axe to slave
            		this.setHeldItem(itemstack.getItem());
            		if (!player.capabilities.isCreativeMode) {
                        --itemstack.stackSize;
                    }
            				
            		setAILumberjack();
            		
            		
            	} else if (itemstack.getItem() instanceof ItemHoe) {
            		// if player interacts with hoe, give hoe to slave
            		this.setHeldItem(itemstack.getItem());
            		if (!player.capabilities.isCreativeMode) {
                        --itemstack.stackSize;
                    }
            		
            		this.setAIFarmer();
            		
            	} else if (itemstack.getItem() instanceof ItemSword) {
            		// if player interacts with sword, give sword to slave
            		this.setHeldItem(itemstack.getItem());
            		if (!player.capabilities.isCreativeMode) {
                        --itemstack.stackSize;
                    }
            		this.setAIGladiator();
            		
            	} else if (itemstack.getItem() instanceof ItemFood) {
            		// feed slave
                    ItemFood itemfood = (ItemFood)itemstack.getItem();

                    if (this.dataWatcher.getWatchableObjectFloat(18) < 20.0F) {
                        if (!player.capabilities.isCreativeMode) {
                            --itemstack.stackSize;
                        }

                        this.heal((float)itemfood.func_150905_g(itemstack));
                        
                        this.setHungerLevel(this.getHungerLevel() - itemfood.func_150905_g(itemstack));
                        this.setRevoltLevel(this.getRevoltLevel() - itemfood.func_150905_g(itemstack));
                        
                        
                        if (itemstack.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                        }

                        return true;
                    } else if (itemstack.getItem() instanceof ItemArmor) {
                    	// equip armor, drop current armor in slot if equipped
                    }
                } 
            }  else if (itemstack == null) {
            	// if slave is holding an item and player interact with empty hand,
            	// then have slave drop their held item
            	if (this.getEquipmentInSlot(0) != null) {
            		this.dropHeldItem();
            	}
            	
            	this.FollowOwner();
            	return true;
            } 

            if (this.func_152114_e(player) && !this.worldObj.isRemote && !this.isBreedingItem(itemstack)) {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity((PathEntity)null);
                this.setTarget((Entity)null);
                this.setAttackTarget((EntityLivingBase)null);
            }
        } else if (itemstack != null && itemstack.getItem() instanceof ItemWhip) {
        	this.lashSlave(player);
        	
            if (!this.worldObj.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.setPathToEntity((PathEntity)null);
                    this.setAttackTarget((EntityLivingBase)null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.func_152115_b(player.getUniqueID().toString());
                    this.playTameEffect(true);
                    this.worldObj.setEntityState(this, (byte)7);
                } else {
                    this.playTameEffect(false);
                    this.worldObj.setEntityState(this, (byte)6);
                }
            }
            
            
            return true;
        }

        return super.interact(player);
    }
	
	@Override
	public float getAIMoveSpeed() {
		return 0.6F;
	}
	
	public ItemStack getItemStackSlot(int slot) {
		return (ItemStack) this.getEquipmentInSlot(slot);
	}
	
	public void dropItemSlot(int slot) {
		this.dropItem(this.getItemStackSlot(slot).getItem(), 1);
	}
	
	
	
//	Makes slave drop whatever their currently held item is
	public void dropHeldItem() {
		if (!this.worldObj.isRemote) {
			ItemStack drop = new ItemStack(this.getHeldItem().getItem(), 2, (this.getHeldItemDamage()));
			this.dropItem(drop.getItem(), 1);
		}
		this.setCurrentItemOrArmor(0, (ItemStack) null);
	}
	

	public float getRange() {
	    float dmod = 16 + 10 * 4;
	    dmod += Math.max(dmod * 0.2F, 2.0F);
	    return dmod;
	}
	  
	public int getActionTimer() {
		return 3 - Math.abs(this.action - 3);
	}
	  
	public void startActionTimer() {
		if (this.action == 0) {
			this.action = 6;
			this.worldObj.setEntityState(this, (byte)4);
			
		    // damage slave's tool
//		    this.damageHeldItem(5);
			this.increaseSkillBasedOnHeldItem(1);
		}
	}
	  
	public void startLeftArmTimer() {
	    if (this.leftArm == 0) {
	      this.leftArm = 5;
	      this.worldObj.setEntityState(this, (byte)6);
	    }
	}
	  
	public void startRightArmTimer() {
	    if (this.rightArm == 0) {
	      this.rightArm = 5;
	      this.worldObj.setEntityState(this, (byte)8);
	    }
	}
	  
	public int getSlaveStrength() {
		// slave stength (mining speed) will be based on the 
		// material of their given tool
		
		
		if (this.getEquipmentInSlot(0) != null) {
			Item heldItem = this.getEquipmentInSlot(0).getItem();
			if (heldItem == Items.wooden_axe ||
			    heldItem == Items.wooden_hoe ||
				heldItem == Items.wooden_sword ||
				heldItem == Items.wooden_pickaxe ||
				heldItem == Items.wooden_shovel) {
						   
				this.slaveStrength = 2;
				
			} else if (heldItem == Items.stone_axe ||
					   heldItem == Items.stone_hoe ||
					   heldItem == Items.stone_sword ||
					   heldItem == Items.stone_pickaxe ||
					   heldItem == Items.stone_shovel) {
				
				this.slaveStrength = 4;
				
			} else if (heldItem == Items.iron_axe ||
					   heldItem == Items.iron_hoe ||
					   heldItem == Items.iron_sword ||
					   heldItem == Items.iron_pickaxe ||
					   heldItem == Items.iron_shovel) {
				
				this.slaveStrength = 6;
				
			} else if (heldItem == Items.diamond_axe ||
					   heldItem == Items.diamond_hoe ||
					   heldItem == Items.diamond_sword ||
					   heldItem == Items.diamond_pickaxe ||
					   heldItem == Items.diamond_shovel) {				
				this.slaveStrength = 10;				
			}
			
			
			// skill bonus
			if (heldItem instanceof ItemAxe) {
				this.slaveStrength = this.slaveStrength + (this.getLumberjackSkill()/100);
			} else if (heldItem instanceof ItemHoe) {
				this.slaveStrength = this.slaveStrength + (this.getFarmerSkill()/100);
			} else if (heldItem instanceof ItemSword) {
				this.slaveStrength = this.slaveStrength + (this.getGladiatorSkill()/100);
			}
			
		} else { 
			this.slaveStrength = 1;
			
			// grant slave a general experience bonus based on their skills
			this.slaveStrength = this.slaveStrength + (((this.getGladiatorSkill()/100) + (this.getFarmerSkill()/100) + (this.getLumberjackSkill()/100))/10);
		}
		return this.slaveStrength;
	}
	
//	Returns an ItemStack of the slave's current held item
	public ItemStack getHeldItem() {
		return (ItemStack) this.getEquipmentInSlot(0);
	}
	
	
//	Sets the slave's held item.  Automatically drops currently held item
	public void setHeldItem(Item item) {
		if (this.getHeldItem() != null) {
			this.dropHeldItem();
		}
		this.setCurrentItemOrArmor(0, new ItemStack(item));
	}

	
	public void setHeldItemByType(int type) {
		Item newHeldItem = (Item)itemRegistry.getObjectById(type);
		ItemStack heldStack = new ItemStack(newHeldItem);
		this.setCurrentItemOrArmor(0, heldStack);
	}
	
	public void setAIByHeldItem() {
		if (this.getHeldItem() != null) {
			if (this.getHeldItem().getItem() instanceof ItemAxe) {
				this.setAILumberjack();
			} else if (this.getHeldItem().getItem() instanceof ItemHoe) {
				this.setAIFarmer();
			} else if (this.getHeldItem().getItem() instanceof ItemSword) {
				this.setAIGladiator();
			}
		} else {
			this.setAIBase();
		}
	}
	
	public void damageHeldItem(int amount) {
		ItemStack heldStack = this.getHeldItem();
		heldStack.setItemDamage(heldStack.getItemDamage() + amount);
		Enslave.log.info("Held item damaged " + heldStack.getItemDamage());
		if (heldStack.getItemDamage() >= heldStack.getMaxDamage()) {
			this.destroyHeldItem();
		}
	}
	
	public void destroyHeldItem() {
		this.setHeldItemByType(0);
	}
	

	@Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
//        compound.setInteger("TextureType", this.getTextureType());
    }

	@Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
//        this.setTextureType(compound.getInteger("TextureType"));
		
    }
	
}
