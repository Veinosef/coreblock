package net.veinosef.coreblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class CoreBlockMod implements ModInitializer {
    public static final String MOD_ID = "coreblock";
    private static final Random RANDOM = new Random();

    public static int currentPhase = 1;
    public static int phaseProgress = 0;

    @Override
    public void onInitialize() {
        CoreBlockWorldHandler.register();

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient() && pos.equals(new BlockPos(0, 64, 0))) {
                phaseProgress++;
                checkPhaseCompletion(world, player, pos);

                // Kırılan bloğun yerine o evreye ait rastgele yeni blok koy
                BlockState nextBlock = getNextBlockState(currentPhase);
                world.setBlockState(pos, nextBlock);
            }
        });
    }

    private static void checkPhaseCompletion(net.minecraft.world.World world, net.minecraft.entity.player.PlayerEntity player, BlockPos pos) {
        int target = getRequiredBlocksForPhase(currentPhase);

        if (currentPhase < 5) {
            if (phaseProgress >= target) {
                currentPhase++;
                phaseProgress = 0;
                player.sendMessage(Text.literal("§a§l[TEBRİKLER!] §eGörevi tamamladın! Yeni Evre: §6" + getPhaseName(currentPhase)), false);
                world.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            } else {
                player.sendMessage(Text.literal("§6Evre " + currentPhase + ": §e" + getPhaseName(currentPhase) + " §7(§b" + phaseProgress + "§7/§a" + target + "§7)"), true);
            }
        } else {
            // Sonsuz Evre Mantığı
            if (phaseProgress >= 100) {
                phaseProgress = 0;
                player.sendMessage(Text.literal("§d§l[SONSUZLUK ÖDÜLÜ!] §e100 blokluk dev görevi tamamladın!"), false);
                world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            } else {
                player.sendMessage(Text.literal("§dKozmik Sonsuzluk: §b" + phaseProgress + "§7/§a100"), true);
            }
        }
    }

    private static int getRequiredBlocksForPhase(int phase) {
        return switch (phase) {
            case 1 -> 40;  // 40 Doğa bloğu
            case 2 -> 60;  // 60 Maden bloğu
            case 3 -> 80;  // 80 Nether bloğu
            case 4 -> 100; // 100 End bloğu
            default -> 100;
        };
    }

    private static String getPhaseName(int phase) {
        return switch (phase) {
            case 1 -> "Doğa & Başlangıç";
            case 2 -> "Yeraltı Madenleri";
            case 3 -> "Nether Boyutu";
            case 4 -> "End Boyutu";
            default -> "Kozmik Sonsuzluk";
        };
    }

    private static BlockState getNextBlockState(int phase) {
        int chance = RANDOM.nextInt(100);

        return switch (phase) {
            case 1 -> {
                // 1. Evre: Doğa Havuzu
                if (chance < 35) yield Blocks.DIRT.getDefaultState();
                if (chance < 65) yield Blocks.OAK_LOG.getDefaultState();
                if (chance < 80) yield Blocks.GRASS_BLOCK.getDefaultState();
                if (chance < 90) yield Blocks.BIRCH_LOG.getDefaultState();
                if (chance < 96) yield Blocks.CLAY.getDefaultState();
                yield Blocks.GRAVEL.getDefaultState();
            }
            case 2 -> {
                // 2. Evre: Maden Havuzu
                if (chance < 30) yield Blocks.STONE.getDefaultState();
                if (chance < 50) yield Blocks.DEEPSLATE.getDefaultState();
                if (chance < 70) yield Blocks.COAL_ORE.getDefaultState();
                if (chance < 85) yield Blocks.IRON_ORE.getDefaultState();
                if (chance < 93) yield Blocks.COPPER_ORE.getDefaultState();
                if (chance < 98) yield Blocks.GOLD_ORE.getDefaultState();
                yield Blocks.DIAMOND_ORE.getDefaultState();
            }
            case 3 -> {
                // 3. Evre: Nether Havuzu
                if (chance < 35) yield Blocks.NETHERRACK.getDefaultState();
                if (chance < 55) yield Blocks.SOUL_SAND.getDefaultState();
                if (chance < 70) yield Blocks.BASALT.getDefaultState();
                if (chance < 82) yield Blocks.NETHER_QUARTZ_ORE.getDefaultState();
                if (chance < 92) yield Blocks.GLOWSTONE.getDefaultState();
                if (chance < 97) yield Blocks.NETHER_GOLD_ORE.getDefaultState();
                yield Blocks.ANCIENT_DEBRIS.getDefaultState();
            }
            case 4 -> {
                // 4. Evre: End Havuzu
                if (chance < 45) yield Blocks.END_STONE.getDefaultState();
                if (chance < 70) yield Blocks.OBSIDIAN.getDefaultState();
                if (chance < 85) yield Blocks.END_STONE_BRICKS.getDefaultState();
                if (chance < 95) yield Blocks.PURPUR_BLOCK.getDefaultState();
                yield Blocks.CRYING_OBSIDIAN.getDefaultState();
            }
            default -> {
                // 5. Evre: Sonsuzluk & Değerli Bloklar Havuzu
                if (chance < 30) yield Blocks.DIAMOND_BLOCK.getDefaultState();
                if (chance < 55) yield Blocks.EMERALD_BLOCK.getDefaultState();
                if (chance < 75) yield Blocks.GOLD_BLOCK.getDefaultState();
                if (chance < 90) yield Blocks.IRON_BLOCK.getDefaultState();
                yield Blocks.NETHERITE_BLOCK.getDefaultState();
            }
        };
    }
}
