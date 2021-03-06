package enslave.entity.ai;
//Modified AI originally from Azanor's Thaumcraft

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import enslave.entity.EntityEnslavedVillager;;

public class AIReturnHome extends EntityAIBase {
  private EntityEnslavedVillager theSlave;
  private double movePosX;
  private double movePosY;
  private double movePosZ;
  private int pathingDelay = 0;
  private int pathingDelayInc = 5;

  int count = 0;
  int prevX = 0;
  int prevY = 0;
  int prevZ = 0;

  public AIReturnHome(EntityEnslavedVillager par1EntityCreature) {
    this.theSlave = par1EntityCreature;
    setMutexBits(3);
  }

  public boolean shouldExecute() {
    ChunkCoordinates home = this.theSlave.getHomePosition();
    if (this.pathingDelay > 0) this.pathingDelay -= 1;
    if ((this.pathingDelay > 0) || (this.theSlave.getDistanceSq(home.posX + 0.5F, home.posY + 0.5F, home.posZ + 0.5F) < 3.0D)) {
      return false;
    }

    this.movePosX = (home.posX + 0.5D);
    this.movePosY = (home.posY + 0.5D);
    this.movePosZ = (home.posZ + 0.5D);
    return true;
  }

  public boolean continueExecuting() {
    ChunkCoordinates home = this.theSlave.getHomePosition();
    return (this.pathingDelay <= 0) && (this.count > 0) && (!this.theSlave.getNavigator().noPath()) && (this.theSlave.getDistanceSq(home.posX + 0.5F, home.posY + 0.5F, home.posZ + 0.5F) >= 3.0D);
  }

  public void resetTask() {
  }

  public void updateTask() {
    this.count -= 1;
    if ((this.count == 0) && (this.prevX == MathHelper.floor_double(this.theSlave.posX)) && (this.prevY == MathHelper.floor_double(this.theSlave.posY)) && (this.prevZ == MathHelper.floor_double(this.theSlave.posZ))) {
      Vec3 var2 = RandomPositionGenerator.findRandomTarget(this.theSlave, 2, 1);

      if (var2 != null) {
        this.count = 20;
        boolean path = this.theSlave.getNavigator().tryMoveToXYZ(var2.xCoord + 0.5D, var2.yCoord + 0.5D, var2.zCoord + 0.5D, this.theSlave.getAIMoveSpeed());
        if (!path) {
          this.pathingDelay = this.pathingDelayInc;
          if (this.pathingDelayInc < 50) this.pathingDelayInc += 5; 
        }
        else { this.pathingDelayInc = 5; }

      }

    }

    super.updateTask();
  }

  public void startExecuting() {
    this.count = 20;
    this.prevX = MathHelper.floor_double(this.theSlave.posX);
    this.prevY = MathHelper.floor_double(this.theSlave.posY);
    this.prevZ = MathHelper.floor_double(this.theSlave.posZ);
    boolean path = this.theSlave.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.theSlave.getAIMoveSpeed());
    if (!path) {
      this.pathingDelay = this.pathingDelayInc;
      if (this.pathingDelayInc < 50) this.pathingDelayInc += 5; 
    }
    else { this.pathingDelayInc = 5; }

  }
}