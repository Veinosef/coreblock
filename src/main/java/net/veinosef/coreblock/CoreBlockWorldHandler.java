package net.veinosef.coreblock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CoreBlockWorldHandler {
    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == ServerWorld.OVERWORLD) {
                BlockPos corePos = new BlockPos(0, 64, 0);

                // 3x3 Başlangıç Toprak Platformu ve Ortada İlk Blok
                if (world.isAir(corePos)) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos pos = new BlockPos(x, 64, z);
                            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                        }
                    }
                    world.setSpawnPos(new BlockPos(0, 65, 0), 0.0f);
                }
            }
        });
    }
}
