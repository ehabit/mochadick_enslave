package enslave.entity;

import java.util.Random;

import cpw.mods.fml.common.registry.EntityRegistry;
import enslave.Enslave;
import enslave.entity.ai.AIHarvestCrops;
import enslave.entity.ai.AIHarvestLogs;
import enslave.entity.ai.AIReturnHome;
import enslave.item.ItemWhip;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.world.World;


public class EntityEnslavedVillager extends EntityWolf {
	
	public Integer textureType;
	protected static Random itemRand = new Random();
	private static final Float modelHeight = 1.8F;
	private static final Float modelWidth = 0.6F;
	
	public static int slaveDelay = 5;
    public static int slaveIgnoreDelay = 10000;
    private int slaveStrength = 1;
	
    private float hunger;
    
    
	private Item heldItem;
	
	public EntityEnslavedVillager(World world) {
		super(world);
		this.setSize(modelWidth, modelHeight);
		
		this.getNavigator().setAvoidsWater(true);
		this.FollowOwner();

		int randNum = itemRand.nextInt(2) + 1;
		this.textureType = randNum;
		
		
		this.hunger = 100.0F;
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
        
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(5, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(10, new EntityAILookIdle(this));
       
	}
	
	protected void setAILumberjack() {
		this.clearAITasks();
		this.setHomeArea((int) this.posX, (int) this.posY, (int) this.posZ, 25);
		this.tasks.addTask(1, new AIHarvestLogs(this));
		this.tasks.addTask(2, new AIReturnHome(this));
		this.setAIBase();
		this.aiSit.setSitting(false);	
	}
	
	protected void setAIPicker() {
		this.clearAITasks();
		this.setHomeArea((int) this.posX, (int) this.posY, (int) this.posZ, 25);
		this.tasks.addTask(1, new AIHarvestCrops(this));
		this.tasks.addTask(2, new AIReturnHome(this));
		this.setAIBase();
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
		if (this.heldItem != null) {
			this.dropHeldItem();
		}
		
		int random = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + par2);
		
		for (int k = 0; k < random; ++k) {
			if (k==2) {
//				this.dropItem(Enslave.shackles, 1);
				this.dropItem(Items.iron_ingot, 1);
			} else if (k == 1) {
				this.dropItem(Items.iron_ingot, 2);
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
	
	@Override
	public void onUpdate() {
		 super.onUpdate();
		 
	}
	
	@Override
	public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();

        if (this.isTamed()) {
            if (itemstack != null) {
            	if (itemstack.getItem() instanceof ItemWhip) {
            		player.swingItem();
            		player.worldObj.playSoundAtEntity(player, "enslave:whip", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));        
                    
            		// players can steal slaves with a whip from current owner
            		// must be owner to leash slave and get them to follow you
                    this.setTarget((Entity)player);
                    if (!this.worldObj.isRemote) {
                        if (this.rand.nextInt(3) == 0) {
                        	this.func_152115_b(player.getUniqueID().toString());
                        	this.playTameEffect(true);
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
            		
            		this.setAIPicker();
            		
            	} else if (itemstack.getItem() instanceof ItemSword) {
            		// if player interacts with sword, give sword to slave
            		this.setHeldItem(itemstack.getItem());
            		if (!player.capabilities.isCreativeMode) {
                        --itemstack.stackSize;
                    }
            		
            	} else if (itemstack.getItem() instanceof ItemFood) {
            		// feed slave
                    ItemFood itemfood = (ItemFood)itemstack.getItem();

                    if (itemfood.isWolfsFavoriteMeat() && this.dataWatcher.getWatchableObjectFloat(18) < 20.0F) {
                        if (!player.capabilities.isCreativeMode) {
                            --itemstack.stackSize;
                        }

                        this.heal((float)itemfood.func_150905_g(itemstack));

                        if (itemstack.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                        }

                        return true;
                    }
                } 
            }  else if (itemstack == null) {
            	// if slave is holding an item and player interact with empty hand,
            	// then have slave drop their held item
            	if (this.heldItem != null) {
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
        	player.swingItem();
    		player.worldObj.playSoundAtEntity(player, "enslave:whip", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));        
            
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
	
//	Makes slave drop whatever their currently held item is
	public void dropHeldItem() {
		if (!this.worldObj.isRemote) {
			this.dropItem(this.heldItem, 1);
			this.heldItem = null;
		}
		
		if (this.worldObj.isRemote) {
			this.heldItem = null;
		}
	}
	
//	Returns an ItemStack of the slave's current held item
	public ItemStack getHeldItem() {
		if (heldItem == null) {
			return null;
		} else {
			ItemStack heldStack = new ItemStack(heldItem);
			return (ItemStack) heldStack;
		}
	}
	
//	Sets the slave's held item.  Automatically drops currently held item
	public void setHeldItem(Item item) {
		if (this.heldItem != null) {
			this.dropHeldItem();
		}
		this.heldItem = item;
	}
	
	public float getRange() {
	    float dmod = 16 + 10 * 4;
	    dmod += Math.max(dmod * 0.2F, 2.0F);
	    return dmod;
	}
	
	public int action = 0;
	public int leftArm = 0;
	public int rightArm = 0;
	public int healing = 0;
	  
	public int getActionTimer() {
		return 3 - Math.abs(this.action - 3);
	}
	  
	public void startActionTimer() {
		if (this.action == 0) {
			this.action = 6;
			this.worldObj.setEntityState(this, (byte)4);
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
		if (this.heldItem != null) {
			if (this.heldItem == Items.wooden_axe ||
				this.heldItem == Items.wooden_hoe ||
				this.heldItem == Items.wooden_sword ||
				this.heldItem == Items.wooden_pickaxe ||
				this.heldItem == Items.wooden_shovel) {
						   
				this.slaveStrength = 2;
				
			} else if (this.heldItem == Items.stone_axe ||
					   this.heldItem == Items.stone_hoe ||
					   this.heldItem == Items.stone_sword ||
					   this.heldItem == Items.stone_pickaxe ||
					   this.heldItem == Items.stone_shovel) {
				
				this.slaveStrength = 4;
				
			} else if (this.heldItem == Items.iron_axe ||
					   this.heldItem == Items.iron_hoe ||
					   this.heldItem == Items.iron_sword ||
					   this.heldItem == Items.iron_pickaxe ||
					   this.heldItem == Items.iron_shovel) {
				
				this.slaveStrength = 6;
				
			} else if (this.heldItem == Items.diamond_axe ||
					   this.heldItem == Items.diamond_hoe ||
					   this.heldItem == Items.diamond_sword ||
					   this.heldItem == Items.diamond_pickaxe ||
					   this.heldItem == Items.diamond_shovel) {
				
				this.slaveStrength = 10;				
			}
		} else {
			this.slaveStrength = 1;
		}
		return this.slaveStrength;
	}
	
	
	
}
