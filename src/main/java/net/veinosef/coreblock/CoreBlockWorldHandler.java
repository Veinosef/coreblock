package net.veinosef.coreblock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class CoreBlockWorldHandler {
    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == ServerWorld.OVERWORLD) {
                BlockPos corePos = new BlockPos(0, 64, 0);
                
                // 3x3 Toprak platform ve ortada CoreBlock
                if (world.isAir(corePos)) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos pos = new BlockPos(x, 64, z);
                            if (x == 0 && z == 0) {
                                world.setBlockState(pos, CoreBlockMod.CORE_BLOCK.getDefaultState());
                            } else {
                                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                            }
                        }
                    }
                    world.setSpawnPos(new BlockPos(0, 65, 0), 0.0f);
                    spawnHologram(world, corePos);
                }
            }
        });
    }

    public static void spawnHologram(ServerWorld world, BlockPos pos) {
        // Eski hologram varsa temizle
        List<DisplayEntity.TextDisplayEntity> existing = world.getEntitiesByClass(
                DisplayEntity.TextDisplayEntity.class,
                new Box(pos.up()).expand(1.0),
                e -> true
        );
        for (DisplayEntity.TextDisplayEntity e : existing) {
            e.discard();
        }

        DisplayEntity.TextDisplayEntity display = EntityType.TEXT_DISPLAY.create(world);
        if (display != null) {
            display.setPosition(pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5);
            display.setText(Text.literal("§6§lCORE BLOCK\n§eEvre 1: Ahşap & Doğa Çağı"));
            display.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
            display.setViewRange(0.6F);
            world.spawnEntity(display);
        }
    }
}
