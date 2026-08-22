package net.veinosef.coreblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class CoreBlockMod implements ModInitializer {
    public static final String MOD_ID = "coreblock";
    private static final Random RANDOM = new Random();

    public static int currentPhase = 1;
    public static int currentQuest = 1;
    public static int questProgress = 0;

    @Override
    public void onInitialize() {
        CoreBlockWorldHandler.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            updateGlobalScoreboard(handler.getPlayer());
        });

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient() && pos.equals(new BlockPos(0, 64, 0))) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    handleQuestProgress(world, serverPlayer, state, pos);
                }

                BlockState nextBlock = getNextBlockState(currentPhase);
                world.setBlockState(pos, nextBlock);
            }
        });
    }

    private static void handleQuestProgress(net.minecraft.world.World world, ServerPlayerEntity player, BlockState brokenState, BlockPos pos) {
        boolean progressMade = false;

        if (currentPhase == 1) {
            if (currentQuest == 1 && (brokenState.isOf(Blocks.DIRT) || brokenState.isOf(Blocks.GRASS_BLOCK))) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 40) completeQuest(world, player, pos, "40 Toprak Toplandı!", 2);
            } else if (currentQuest == 2 && (brokenState.isOf(Blocks.OAK_LOG) || brokenState.isOf(Blocks.BIRCH_LOG))) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 25) advancePhase(world, player, pos, 2, "Doğa Çağı Tamamlandı!");
            }
        } else if (currentPhase == 2) {
            if (currentQuest == 1 && (brokenState.isOf(Blocks.STONE) || brokenState.isOf(Blocks.DEEPSLATE))) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 60) completeQuest(world, player, pos, "60 Taş Kırıldı!", 2);
            } else if (currentQuest == 2 && brokenState.isOf(Blocks.IRON_ORE)) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 15) completeQuest(world, player, pos, "15 Demir Bulundu!", 3);
            } else if (currentQuest == 3 && brokenState.isOf(Blocks.DIAMOND_ORE)) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 3) advancePhase(world, player, pos, 3, "Madenler Çağı Tamamlandı!");
            }
        } else if (currentPhase == 3) {
            if (currentQuest == 1 && brokenState.isOf(Blocks.NETHERRACK)) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 80) completeQuest(world, player, pos, "80 Nether Taşı Kırıldı!", 2);
            } else if (currentQuest == 2 && brokenState.isOf(Blocks.NETHER_QUARTZ_ORE)) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 20) completeQuest(world, player, pos, "20 Kuvars Toplandı!", 3);
            } else if (currentQuest == 3 && brokenState.isOf(Blocks.ANCIENT_DEBRIS)) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 1) advancePhase(world, player, pos, 4, "Nether Çağı Tamamlandı!");
            }
        } else if (currentPhase == 4) {
            if (currentQuest == 1 && brokenState.isOf(Blocks.END_STONE)) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 100) completeQuest(world, player, pos, "100 End Taşı Kırıldı!", 2);
            } else if (currentQuest == 2 && brokenState.isOf(Blocks.OBSIDIAN)) {
                questProgress++;
                progressMade = true;
                if (questProgress >= 14) advancePhase(world, player, pos, 5, "CoreBlock Nihai Çekirdeğe Ulaştı!");
            }
        } else {
            questProgress++;
            if (questProgress >= 100) {
                questProgress = 0;
                broadcastMessage(player, "§d§l[KOZMİK DÖNGÜ] §e100 blok tamamlandı!");
                world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            progressMade = true;
        }

        if (progressMade) {
            updateGlobalScoreboard(player);
        }
    }

    private static void completeQuest(net.minecraft.world.World world, ServerPlayerEntity player, BlockPos pos, String msg, int nextQuest) {
        currentQuest = nextQuest;
        questProgress = 0;
        broadcastMessage(player, "§a§l[GÖREV TAMAM] §e" + msg);
        world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        updateGlobalScoreboard(player);
    }

    private static void advancePhase(net.minecraft.world.World world, ServerPlayerEntity player, BlockPos pos, int nextPhase, String msg) {
        currentPhase = nextPhase;
        currentQuest = 1;
        questProgress = 0;
        broadcastMessage(player, "§6§l[YENİ ÇAĞ] §e" + msg);
        world.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        updateGlobalScoreboard(player);
    }

    private static void updateGlobalScoreboard(ServerPlayerEntity player) {
        if (player.getServer() == null) return;

        Scoreboard scoreboard = player.getServer().getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective("coreblock_sb");

        if (objective != null) {
            scoreboard.removeObjective(objective);
        }

        objective = scoreboard.addObjective(
                "coreblock_sb",
                ScoreboardCriterion.DUMMY,
                Text.literal("§6§l✦ CORE BLOCK ✦"),
                ScoreboardCriterion.RenderType.INTEGER,
                true,
                null
        );
        scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);

        String phaseName = getPhaseName(currentPhase);
        String questName = getQuestTargetName();
        int target = getQuestTargetAmount();

        scoreboard.getOrCreateScore(ScoreHolderDummy.fromName("§7──────────────"), objective).setScore(6);
        scoreboard.getOrCreateScore(ScoreHolderDummy.fromName("§eÇağ: §f" + phaseName + " §7(" + currentPhase + "/5)"), objective).setScore(5);
        scoreboard.getOrCreateScore(ScoreHolderDummy.fromName("§eGörev: §f" + questName), objective).setScore(4);
        scoreboard.getOrCreateScore(ScoreHolderDummy.fromName("§eİlerleme: §b" + questProgress + " §7/ §a" + target), objective).setScore(3);
        scoreboard.getOrCreateScore(ScoreHolderDummy.fromName("§8──────────────"), objective).setScore(2);
        scoreboard.getOrCreateScore(ScoreHolderDummy.fromName("§dveinosef.coreblock"), objective).setScore(1);
    }

    private static String getPhaseName(int phase) {
        return switch (phase) {
            case 1 -> "Doğa";
            case 2 -> "Madenler";
            case 3 -> "Nether";
            case 4 -> "End";
            default -> "Kozmik";
        };
    }

    private static String getQuestTargetName() {
        return switch (currentPhase) {
            case 1 -> (currentQuest == 1) ? "Toprak Kır" : "Odun Kır";
            case 2 -> switch (currentQuest) {
                case 1 -> "Taş Kır";
                case 2 -> "Demir Bul";
                default -> "Elmas Bul";
            };
            case 3 -> switch (currentQuest) {
                case 1 -> "Nether Taşı";
                case 2 -> "Kuvars Bul";
                default -> "Antik Kalıntı";
            };
            case 4 -> (currentQuest == 1) ? "End Taşı" : "Obsidyen Kır";
            default -> "Sonsuz Döngü";
        };
    }

    private static int getQuestTargetAmount() {
        return switch (currentPhase) {
            case 1 -> (currentQuest == 1) ? 40 : 25;
            case 2 -> switch (currentQuest) {
                case 1 -> 60;
                case 2 -> 15;
                default -> 3;
            };
            case 3 -> switch (currentQuest) {
                case 1 -> 80;
                case 2 -> 20;
                default -> 1;
            };
            case 4 -> (currentQuest == 1) ? 100 : 14;
            default -> 100;
        };
    }

    private static void broadcastMessage(ServerPlayerEntity player, String message) {
        if (player.getServer() != null) {
            for (ServerPlayerEntity p : player.getServer().getPlayerManager().getPlayerList()) {
                p.sendMessage(Text.literal(message), false);
            }
        }
    }

    private static BlockState getNextBlockState(int phase) {
        int chance = RANDOM.nextInt(100);

        return switch (phase) {
            case 1 -> {
                if (chance < 45) yield Blocks.DIRT.getDefaultState();
                if (chance < 75) yield Blocks.OAK_LOG.getDefaultState();
                if (chance < 88) yield Blocks.GRASS_BLOCK.getDefaultState();
                if (chance < 96) yield Blocks.BIRCH_LOG.getDefaultState();
                yield Blocks.CLAY.getDefaultState();
            }
            case 2 -> {
                if (chance < 35) yield Blocks.STONE.getDefaultState();
                if (chance < 55) yield Blocks.DEEPSLATE.getDefaultState();
                if (chance < 75) yield Blocks.COAL_ORE.getDefaultState();
                if (chance < 88) yield Blocks.IRON_ORE.getDefaultState();
                if (chance < 95) yield Blocks.COPPER_ORE.getDefaultState();
                if (chance < 98) yield Blocks.GOLD_ORE.getDefaultState();
                yield Blocks.DIAMOND_ORE.getDefaultState();
            }
            case 3 -> {
                if (chance < 45) yield Blocks.NETHERRACK.getDefaultState();
                if (chance < 65) yield Blocks.SOUL_SAND.getDefaultState();
                if (chance < 80) yield Blocks.BASALT.getDefaultState();
                if (chance < 92) yield Blocks.NETHER_QUARTZ_ORE.getDefaultState();
                if (chance < 97) yield Blocks.GLOWSTONE.getDefaultState();
                yield Blocks.ANCIENT_DEBRIS.getDefaultState();
            }
            case 4 -> {
                if (chance < 55) yield Blocks.END_STONE.getDefaultState();
                if (chance < 75) yield Blocks.OBSIDIAN.getDefaultState();
                if (chance < 90) yield Blocks.END_STONE_BRICKS.getDefaultState();
                yield Blocks.PURPUR_BLOCK.getDefaultState();
            }
            default -> {
                if (chance < 25) yield Blocks.STONE.getDefaultState();
                if (chance < 45) yield Blocks.DEEPSLATE.getDefaultState();
                if (chance < 60) yield Blocks.OAK_LOG.getDefaultState();
                if (chance < 75) yield Blocks.NETHERRACK.getDefaultState();
                if (chance < 85) yield Blocks.END_STONE.getDefaultState();
                if (chance < 93) yield Blocks.IRON_ORE.getDefaultState();
                if (chance < 97) yield Blocks.DIAMOND_ORE.getDefaultState();
                yield Blocks.ANCIENT_DEBRIS.getDefaultState();
            }
        };
    }

    private static class ScoreHolderDummy implements net.minecraft.scoreboard.ScoreHolder {
        private final String name;

        private ScoreHolderDummy(String name) {
            this.name = name;
        }

        public static ScoreHolderDummy fromName(String name) {
            return new ScoreHolderDummy(name);
        }

        @Override
        public String getNameForScoreboard() {
            return this.name;
        }

        @Override
        public Text getDisplayName() {
            return Text.literal(this.name);
        }
    }
}
