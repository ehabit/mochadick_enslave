package enslave.client.render;


import java.util.Random;

import enslave.entity.EntityEnslavedVillager;
import enslave.Enslave;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderEnslavedVillager extends RenderBiped {

	private ModelZombieVillager zombieVillagerModel;
	private ModelBiped field_82434_o;
	private int field_82431_q = 1;
	protected ModelBiped field_82436_m;
    protected ModelBiped field_82433_n;
	
	private static final ResourceLocation enslavedVillagerTexture = new ResourceLocation(Enslave.MODID, "textures/entities/EnslavedVillager.png");
	private static final ResourceLocation enslavedVillagerTexture2 = new ResourceLocation(Enslave.MODID, "textures/entities/EnslavedVillager2.png");

	
	public RenderEnslavedVillager() {
		super(new ModelZombie(), 0.5F, 1.0F);
		this.field_82434_o = this.modelBipedMain;
		this.zombieVillagerModel = new ModelZombieVillager();
		
	}
	
	 protected void func_82421_b() {
	        this.field_82423_g = new ModelZombie(1.0F, true);
	        this.field_82425_h = new ModelZombie(0.5F, true);
	        this.field_82436_m = new ModelZombieVillager(1.0F, 0.0F, true);
	        this.field_82433_n = new ModelZombieVillager(0.5F, 0.0F, true);
	 }
	
	public void doRender(EntityEnslavedVillager p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        this.func_82427_a(p_76986_1_);
        super.doRender((EntityLiving)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
	
	private void func_82427_a(EntityEnslavedVillager p_82427_1_) {

            if (this.field_82431_q != this.zombieVillagerModel.func_82897_a())
            {
                this.zombieVillagerModel = new ModelZombieVillager();
                this.field_82431_q = this.zombieVillagerModel.func_82897_a();
                this.field_82436_m = new ModelZombieVillager(1.0F, 0.0F, true);
                this.field_82433_n = new ModelZombieVillager(0.5F, 0.0F, true);
            }

            this.mainModel = this.zombieVillagerModel;
            this.field_82423_g = this.field_82436_m;
            this.field_82425_h = this.field_82433_n;
        
        this.modelBipedMain = (ModelBiped)this.mainModel;
    }
	
	@Override
	public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        this.doRender((EntityEnslavedVillager)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
	
	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		this.doRender((EntityEnslavedVillager)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
	
	protected int shouldRenderPass(EntityEnslavedVillager p_77032_1_, int p_77032_2_, float p_77032_3_) {
		this.func_82427_a(p_77032_1_);
	    return super.shouldRenderPass((EntityLiving)p_77032_1_, p_77032_2_, p_77032_3_);
	}

    
	@Override
    protected int shouldRenderPass(EntityLiving p_77032_1_, int p_77032_2_, float p_77032_3_) {
        return this.shouldRenderPass((EntityEnslavedVillager)p_77032_1_, p_77032_2_, p_77032_3_);
    }

    
	@Override
    protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_) {
        return this.shouldRenderPass((EntityEnslavedVillager)p_77032_1_, p_77032_2_, p_77032_3_);
    }

	
	protected ResourceLocation getEntityTexture(Entity entity) {
		if (((EntityEnslavedVillager) entity).textureType == 1) {
			return enslavedVillagerTexture;
		} else {
			return enslavedVillagerTexture2;
		}
    }
}
