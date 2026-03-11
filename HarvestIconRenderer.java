package com.harvestindicator;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a floating "!" icon above mature crops using WorldRenderEvents.
 * The icon billboards toward the camera and bobs up and down slightly.
 */
public class HarvestIconRenderer {

    // Texture path: assets/harvestindicator/textures/icons/ready.png
    private static final Identifier ICON_TEXTURE =
            Identifier.of(HarvestIndicatorMod.MOD_ID, "textures/icons/ready.png");

    // How far (in blocks) from the player to scan for crops
    private static final int SCAN_RADIUS = 12;

    // Icon visual size in world units
    private static final float ICON_SIZE = 0.35f;

    public static void register() {
        WorldRenderEvents.LAST.register(HarvestIconRenderer::onWorldRender);
    }

    private static void onWorldRender(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null || client.player == null) return;

        Vec3d cameraPos = ctx.camera().getPos();
        BlockPos playerPos = client.player.getBlockPos();
        MatrixStack matrices = ctx.matrixStack();

        // Collect all mature crop positions nearby
        List<BlockPos> maturePositions = new ArrayList<>();
        for (BlockPos pos : BlockPos.iterate(
                playerPos.add(-SCAN_RADIUS, -SCAN_RADIUS, -SCAN_RADIUS),
                playerPos.add(SCAN_RADIUS, SCAN_RADIUS, SCAN_RADIUS))) {
            if (CropReadinessChecker.isReadyToHarvest(world, pos)) {
                maturePositions.add(pos.toImmutable());
            }
        }

        if (maturePositions.isEmpty()) return;

        // Setup render state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, ICON_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(
                VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_TEXTURE_COLOR);

        // Compute bob offset based on world time
        long time = world.getTime();
        float bob = (float) Math.sin(time * 0.1f) * 0.08f;

        for (BlockPos pos : maturePositions) {
            double yOffset = CropReadinessChecker.getIconYOffset(world, pos);

            // World position of the icon center
            double wx = pos.getX() + 0.5 - cameraPos.x;
            double wy = pos.getY() + yOffset + bob - cameraPos.y;
            double wz = pos.getZ() + 0.5 - cameraPos.z;

            matrices.push();
            matrices.translate(wx, wy, wz);

            // Billboard: rotate toward camera
            float yaw = ctx.camera().getYaw();
            float pitch = ctx.camera().getPitch();
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y
                    .rotationDegrees(-yaw));
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X
                    .rotationDegrees(pitch));

            Matrix4f matrix = matrices.peek().getPositionMatrix();

            float h = ICON_SIZE / 2f;
            // Draw quad (full texture = the "!" icon)
            buffer.vertex(matrix, -h,  h, 0).texture(0, 0).color(255, 255, 255, 220);
            buffer.vertex(matrix, -h, -h, 0).texture(0, 1).color(255, 255, 255, 220);
            buffer.vertex(matrix,  h, -h, 0).texture(1, 1).color(255, 255, 255, 220);
            buffer.vertex(matrix,  h,  h, 0).texture(1, 0).color(255, 255, 255, 220);

            matrices.pop();
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        // Restore render state
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}
