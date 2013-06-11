package com.mes51.minecraft.mods.javelin.renderer;

import com.mes51.minecraft.mods.javelin.entities.EntityJavelinMissile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Package: com.mes51.minecraft.mods.javelin.renderer
 * Date: 2013/06/04
 * Time: 22:36
 */
@SideOnly(Side.CLIENT)
public class RenderJavelinMissile extends Render {
    public void renderArrow(EntityJavelinMissile par1EntityArrow, double par2, double par4, double par6, float par8, float par9)
    {
        this.loadTexture("/mods/com.mes51.minecraft.mods.javelin.items/textures/item/arrows.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(par1EntityArrow.prevRotationYaw + (par1EntityArrow.rotationYaw - par1EntityArrow.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(par1EntityArrow.prevRotationPitch + (par1EntityArrow.rotationPitch - par1EntityArrow.prevRotationPitch) * par9, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (float)(0 + b0 * 10) / 32.0F;
        float f5 = (float)(5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (float)(5 + b0 * 10) / 32.0F;
        float f9 = (float)(10 + b0 * 10) / 32.0F;
        float f10 = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f9);
        tessellator.draw();

        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double)f2, (double)f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double)f3, (double)f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double)f3, (double)f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double)f2, (double)f5);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1) {
        this.renderArrow((EntityJavelinMissile)entity, d0, d1, d2, f, f1);
    }
}
