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
                
                // Eğer (0,64,0) boşsa 3x3 platformu ve çekirdek bloğu yerleştir
                if (world.isAir(corePos)) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos pos = new BlockPos(x, 64, z);
                            if (x == 0 && z == 0) {
                                world.setBlockState(pos, CoreBlockMod.CORE_BLOCK.getDefaultState());
                            } else {
                                world.setBlockState(pos, Blocks.OAK_PLANKS.getDefaultState());
                            }
                        }
                    }
                    world.setSpawnPos(new BlockPos(0, 65, 0), 0.0f);
                }
            }
        });
    }
}
