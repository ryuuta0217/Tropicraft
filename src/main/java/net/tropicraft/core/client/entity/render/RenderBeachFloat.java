package net.tropicraft.core.client.entity.render;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tropicraft.ColorHelper;
import net.tropicraft.core.client.TropicraftRenderUtils;
import net.tropicraft.core.client.entity.model.ModelBeachFloat;
import net.tropicraft.core.common.entity.placeable.EntityBeachFloat;

public class RenderBeachFloat extends Render<EntityBeachFloat> {

	protected ModelBase modelFloat;
	FloatBuffer color;
	float red = 0.0F, green = 0.0F, blue = 0.0F, alpha = 1.0F;
	
	public RenderBeachFloat() {
		super(Minecraft.getMinecraft().getRenderManager());
		shadowSize = .5F;
		modelFloat = new ModelBeachFloat();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public void doRender(EntityBeachFloat entity, double x, double y, double z, float yaw, float partialTicks) {
		GL11.glPushMatrix();

		red = ColorHelper.getRed(entity.getColor());
		green = ColorHelper.getGreen(entity.getColor());
		blue = ColorHelper.getBlue(entity.getColor());

		GL11.glTranslatef((float) x, (float) y + 1.325f, (float) z);
		GL11.glRotatef(yaw + (180 - yaw)*2, 0.0F, 1.0F, 0.0F);

		// Draw arms of chair
		Minecraft.getMinecraft().renderEngine.bindTexture(TropicraftRenderUtils.getTextureEntity("float_layer"));
		GL11.glScalef(-1F, -1F, 1.0F);
		modelFloat.render(entity, 0.0F, 1.0F, 0.1F, 0.0F, 0.0F, 0.0625F);

		// Draw the colored part of the chair
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Change the color mode to blending
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
		color = BufferUtils.createFloatBuffer(4).put(new float[]{red, green, blue, alpha});
		color.position(0);
		// Color it
		GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, color);
		Minecraft.getMinecraft().renderEngine.bindTexture(TropicraftRenderUtils.getTextureEntity("float_color_layer"));
		modelFloat.render(entity, 0.0F, 1.0F, 0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glDisable(GL11.GL_BLEND);
		// Change the color mode back to modulation
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

		// loldisco
		/*if (red <= 1.0F)
			red += 0.005F;
		else
			if (green <= 1.0F)
				green += 0.005F;
			else
				if (blue <= 1.0F)
					blue += 0.005F;
				else {
					red = green = blue = 0.0F;
				}
		 */		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBeachFloat entity) {
		// NOOP
		return null;
	}
	
	private static float rotationPitch, prevRotationPitch;
	
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre event) {
	    EntityPlayer p = event.getEntityPlayer();
	    Entity riding = p.getRidingEntity();
	    if (riding instanceof EntityBeachFloat) {
	        EntityBeachFloat floaty = (EntityBeachFloat) riding;
	        GlStateManager.pushMatrix();
	        GlStateManager.rotate(-floaty.rotationYaw - 90, 0, 1, 0);
	        GlStateManager.translate(0, 0.6, 0.94);
	        GlStateManager.rotate(-90, 1, 0, 0);

	        // Cancel out player camera rotation
	        float f = p.renderYawOffset - p.prevRenderYawOffset;
	        while (f < -180) f += 360;
	        while (f >= 180) f -= 360;
	        f = p.prevRenderYawOffset + (event.getPartialRenderTick() * f);
	        
	        GlStateManager.rotate(f, 0, 1, 0);
	        
	        // Lock in head
	        p.rotationYawHead = p.renderYawOffset;
	        p.prevRotationYawHead = p.prevRenderYawOffset;
	        rotationPitch = p.rotationPitch;
	        prevRotationPitch = p.prevRotationPitch;
	        p.rotationPitch = 10f;
	        p.prevRotationPitch = 10f;
	    }
	}

	@SubscribeEvent
	public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
	    EntityPlayer p = event.getEntityPlayer();
	    if (p.getRidingEntity() instanceof EntityBeachFloat) {
	        GlStateManager.popMatrix();
	        p.rotationPitch = rotationPitch;
	        p.prevRotationPitch = prevRotationPitch;
	        p.getRidingEntity().rotationYaw += 0.25f;
	    }
	}
}
