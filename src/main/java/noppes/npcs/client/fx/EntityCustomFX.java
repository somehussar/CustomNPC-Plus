package noppes.npcs.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

public class EntityCustomFX extends EntityFX {
    private Entity entity;
    private final ResourceLocation location;
    private static final ResourceLocation resource = new ResourceLocation("textures/particle/particles.png");
    private boolean move = true;
    private float startX = 0, startY = 0, startZ = 0;

    public float scale1 = 1.0F;
    public float scale2 = 1.0F;
    public float scaleRate = 1.0F;
    public int scaleRateStart = 0;

    public float alpha1 = 1.0F;
    public float alpha2 = 0;
    public float alphaRate = 0.0F;
    public int alphaRateStart = 0;

	public EntityCustomFX(Entity entity, String directory, int HEXColor, double x, double y, double z,
                          double motionX, double motionY, double motionZ, float gravity,
                          float scale1, float scale2, float scaleRate, int scaleRateStart,
                          float alpha1, float alpha2, float alphaRate, int alphaRateStart,
                          int age) {
		super(entity.worldObj, x, y, z, motionX, motionY, motionZ);

        this.scale1 = scale1;
        this.scale2 = scale2;
        this.scaleRate = Math.abs(scaleRate);
        this.scaleRateStart = scaleRateStart;
        particleScale = scale1;
        if(scale1 > scale2)
            this.scaleRate *= -1;

        this.alpha1 = alpha1;
        this.alpha2 = alpha2;
        this.alphaRate = Math.abs(alphaRate);
        this.alphaRateStart = alphaRateStart;
        particleAlpha = alpha1;
        if(alpha1 > alpha2)
            this.alphaRate *= -1;

        particleGravity = gravity/0.04F;
        particleMaxAge = age;

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

		this.entity = entity;
        noClip = true;

        particleRed = (HEXColor >> 16 & 255) / 255f;
        particleGreen = (HEXColor >> 8  & 255) / 255f;
        particleBlue = (HEXColor & 255) / 255f;
        
        if(Math.floor(Math.random()*3) == 1){
        	move = false;
            this.startX = (float) entity.posX;
            this.startY = (float) entity.posY;
            this.startZ = (float) entity.posZ;
        }

        location = new ResourceLocation(directory);
	}

    /*
        par2 = partial tick time
        par3 = ActiveRenderInfo.rotationX;
        par4 = ActiveRenderInfo.rotationZ;
        par5 = ActiveRenderInfo.rotationYZ;
        par6 = ActiveRenderInfo.rotationXY;
        par7 = ActiveRenderInfo.rotationXZ;
     */

	@Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
		if(move){
			startX = (float)(entity.prevPosX + (entity.posX - entity.prevPosX) * (double)par2);
			startY = (float)(entity.prevPosY + (entity.posY - entity.prevPosY) * (double)par2);
			startZ = (float)(entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)par2);
		}
        Tessellator tessellator = Tessellator.instance;
        tessellator.draw();

        float scale = this.scaleRate / (float)particleMaxAge;
        if((this.scaleRate < 0 && particleScale+scale < this.scale2) || (this.scaleRate > 0 && particleScale+scale > this.scale2))
            particleScale = this.scale2;
        else if(particleAge >= this.scaleRateStart)
            particleScale += scale;

        float alpha = this.alphaRate / (float)particleMaxAge;
        if((this.alphaRate < 0 && particleAlpha+alpha < this.alpha2) || (this.alphaRate > 0 && particleAlpha+alpha > this.alpha2))
            particleAlpha = this.alpha2;
        else if(particleAge >= this.alphaRateStart)
            particleAlpha += alpha;

        ClientProxy.bindTexture(location);

        float f = 0.0f;
        float f1 = 1.0f;
        float f2 = 0.0f;
        float f3 = 1.0f;
        float f4 = 0.1F * particleScale;
        float f5 = (float)(((prevPosX + (posX - prevPosX) * (double)par2) - interpPosX) + startX);
        float f6 = (float)(((prevPosY + (posY - prevPosY) * (double)par2) - interpPosY) + startY);
        float f7 = (float)(((prevPosZ + (posZ - prevPosZ) * (double)par2) - interpPosZ) + startZ);

        tessellator.startDrawingQuads();
        tessellator.setBrightness(240);
        par1Tessellator.setColorOpaque_F(1, 1, 1);
        par1Tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
        par1Tessellator.addVertexWithUV(f5 - par3 * f4 - par6 * f4, f6 - par4 * f4, f7 - par5 * f4 - par7 * f4, f1, f3);
        par1Tessellator.addVertexWithUV((f5 - par3 * f4) + par6 * f4, f6 + par4 * f4, (f7 - par5 * f4) + par7 * f4, f1, f2);
        par1Tessellator.addVertexWithUV(f5 + par3 * f4 + par6 * f4, f6 + par4 * f4, f7 + par5 * f4 + par7 * f4, f, f2);
        par1Tessellator.addVertexWithUV((f5 + par3 * f4) - par6 * f4, f6 - par4 * f4, (f7 + par5 * f4) - par7 * f4, f, f3);

        /*
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;

        double angle = Minecraft.getMinecraft().theWorld.getWorldTime()%360;
        double angleRad = (angle/180)*Math.PI;

        double diffX = player.posX - this.startX;
        double diffY = player.posY - player.getYOffset() - this.startY;
        double diffZ = player.posZ - this.startZ;

        double yaw = (player.rotationYaw/180)*Math.PI;
        double pitch = (player.rotationPitch/180)*Math.PI;

        double angleX = Math.sin(yaw)*Math.cos(pitch);
        if(Math.abs(angleX) < 0.0001){
            angleX = 0.0D;
        }
        double angleY = Math.sin(pitch);
        if(Math.abs(angleY) < 0.0001){
            angleY = 0.0D;
        }
        double angleZ = -Math.cos(yaw)*Math.cos(pitch);
        if(Math.abs(angleZ) < 0.0001){
            angleZ = 0.0D;
        }

        double angleXtranslateX = 0.0D;
        double angleXtranslateY = 0.0D;//(-diffY + Math.cos(angleRad)*diffY) + (-Math.sin(angleRad)*diffZ);
        double angleXtranslateZ = 0.0D;//(Math.sin(angleRad)*diffY) + (-diffZ + Math.cos(angleRad)*diffZ);

        double angleYtranslateX = 0.0D;//(-diffX + Math.cos(angleRad)*diffX) + (Math.sin(angleRad)*diffZ);
        double angleYtranslateY = 0.0D;
        double angleYtranslateZ = 0.0D;//(-Math.sin(angleRad)*diffX) + (-diffZ + Math.cos(angleRad)*diffZ);

        double angleZtranslateX = 0.0D;//(-diffX + Math.cos(angleRad)*diffX) + (-Math.sin(angleRad)*diffY);
        double angleZtranslateY = 0.0D;//(Math.sin(angleRad)*diffX) + (-diffY + Math.cos(angleRad)*diffY);
        double angleZtranslateZ = 0.0D;

        double translateX = angleXtranslateX + angleYtranslateX + angleZtranslateX;
        double translateY = angleXtranslateY + angleYtranslateY + angleZtranslateY;
        double translateZ = angleXtranslateZ + angleYtranslateZ + angleZtranslateZ;

        //distance in the X & Z plane
        double distXZ = Math.sqrt(Math.pow(diffX,2) + Math.pow(diffZ,2));
        //distance in the X & Y plane
        double distXY = Math.sqrt(Math.pow(diffX,2) + Math.pow(diffY,2));
        //distance in the Y & Z plane
        double distYZ = Math.sqrt(Math.pow(diffY,2) + Math.pow(diffZ,2));

        double signDiffX = Math.signum(diffX);
        double signDiffY = Math.signum(diffY);
        double signDiffZ = Math.signum(diffZ);

        double angleXtoNorm = Math.sin(Math.acos(diffX/distXZ));
        double angleZtoNorm = Math.sin(Math.acos(diffZ/distXZ));

        double angleDiffX = (Math.abs(Math.cos(yaw))-angleXtoNorm);
        double angleDiffZ = (Math.abs(Math.cos(yaw))-angleZtoNorm);

        double translateX = -Math.abs(angleDiffX*angleZ*distXZ)*signDiffX;
        double translateY = 0;
        double translateZ = -Math.abs(angleDiffX*angleX*distXZ)*signDiffZ + Math.cos(angleRad)*Math.abs(angleDiffX*angleX*distXZ)*signDiffZ;

        if(minecraft.currentScreen == null) {
            System.out.println();
            //System.out.printf("diffX: %f, diffY: %f, diffZ: %f\n", diffX, diffY, diffZ);
            //System.out.printf("angleX: %f, angleY: %f, angleZ: %f\n", angleX, angleY, angleZ);
            //System.out.printf("angleXtoNorm: %f, angleZtoNorm: %f\n", angleXtoNorm, angleZtoNorm);
            System.out.printf("Math.sin(yaw): %f\n", Math.sin(yaw));
            System.out.printf("Math.cos(yaw): %f\n", Math.cos(yaw));
            //System.out.printf("Math.sin(pitch): %f\n", Math.sin(pitch));
            //System.out.printf("Math.cos(pitch): %f\n", Math.cos(pitch));
            System.out.printf("angleDiffX: %f\n", angleDiffX);
            System.out.printf("angleDiffZ: %f\n", angleDiffZ);
            System.out.printf("translateX: %f\n", Math.cos(angleRad)*Math.abs(angleDiffX*angleZ*distXZ)*signDiffX);
            System.out.printf("translateZ: %f\n", Math.cos(angleRad)*Math.abs(angleDiffX*angleX*distXZ)*signDiffZ);
        }
        */

        GL11.glPushMatrix();
            GL11.glColor4f(1, 1, 1, 1.0F);

            //GL11.glTranslated(translateX,translateY,translateZ);
            //GL11.glRotated(angle, angleX, angleY, angleZ);

            tessellator.draw();
            ClientProxy.bindTexture(resource);
            tessellator.startDrawingQuads();
        GL11.glPopMatrix();
    }
    
    public int getFXLayer(){
    	return 0;
    }
}