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
    private static final double TEXTURE_SIZE = 16.0;
    private static final float SIZE = 0.05625F;

    // サイズ等は矢と同じ
    // see: net.minecraft.client.renderer.entity.RenderArrow
    public void renderArrow(EntityJavelinMissile missile, double x, double y, double z)
    {
        this.loadTexture("/mods/com.mes51.minecraft.mods.javelin.items/textures/item/arrows.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(missile.rotationYaw - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(missile.rotationPitch, 0.0F, 0.0F, 1.0F);

        Tessellator tessellator = Tessellator.instance;
        double missileNozzleTexTop = 5.0 / TEXTURE_SIZE;
        double missileNozzleTexBottom = 10.0 / TEXTURE_SIZE;
        double missileNozzleTexLeft = 0.0;
        double missileNozzleTexRight = 5.0 / TEXTURE_SIZE;
        double missileSideTexTop = 0.0;
        double missileSideTexBottom = 5.0 / TEXTURE_SIZE;
        double missileSideTexLeft = 0.0;
        double missileSideTexRight = 16.0 / TEXTURE_SIZE;

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(SIZE, SIZE, SIZE);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(SIZE, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, missileNozzleTexLeft,  missileNozzleTexTop);
        tessellator.addVertexWithUV(-7.0D, -2.0D,  2.0D, missileNozzleTexRight, missileNozzleTexTop);
        tessellator.addVertexWithUV(-7.0D,  2.0D,  2.0D, missileNozzleTexRight, missileNozzleTexBottom);
        tessellator.addVertexWithUV(-7.0D,  2.0D, -2.0D, missileNozzleTexLeft,  missileNozzleTexBottom);
        tessellator.draw();

        for (int i = 0; i < 2; i++)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, SIZE);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, missileSideTexLeft,  missileSideTexTop);
            tessellator.addVertexWithUV( 8.0D, -2.0D, 0.0D, missileSideTexRight, missileSideTexTop);
            tessellator.addVertexWithUV (8.0D,  2.0D, 0.0D, missileSideTexRight, missileSideTexBottom);
            tessellator.addVertexWithUV(-8.0D,  2.0D, 0.0D, missileSideTexLeft,  missileSideTexBottom);
            tessellator.draw();
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1) {
        this.renderArrow((EntityJavelinMissile)entity, d0, d1, d2);
    }
}
