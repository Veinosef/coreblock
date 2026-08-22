package net.veinosef.coreblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class CoreBlock extends Block {
    private static final Random RANDOM = new Random();
    public static int currentPhase = 1;

    public CoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.sendMessage(Text.translatable("message.coreblock.current_phase", Text.translatable("phase.coreblock." + currentPhase)), true);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            ItemStack drop = getRandomDrop(currentPhase);
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, drop);
            world.spawnEntity(itemEntity);
            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return super.onBreak(world, pos, state, player);
    }

    private static ItemStack getRandomDrop(int phase) {
        int chance = RANDOM.nextInt(100);

        return switch (phase) {
            case 1 -> {
                if (chance < 50) yield new ItemStack(Items.OAK_LOG);
                if (chance < 80) yield new ItemStack(Items.DIRT);
                if (chance < 95) yield new ItemStack(Items.OAK_SAPLING);
                yield new ItemStack(Items.APPLE);
            }
            case 2 -> {
                if (chance < 40) yield new ItemStack(Items.COBBLESTONE);
                if (chance < 65) yield new ItemStack(Items.COAL);
                if (chance < 85) yield new ItemStack(Items.RAW_IRON);
                if (chance < 95) yield new ItemStack(Items.RAW_COPPER);
                if (chance < 98) yield new ItemStack(Items.RAW_GOLD);
                yield new ItemStack(Items.DIAMOND);
            }
            case 3 -> {
                if (chance < 45) yield new ItemStack(Items.NETHERRACK);
                if (chance < 65) yield new ItemStack(Items.SOUL_SAND);
                if (chance < 80) yield new ItemStack(Items.QUARTZ);
                if (chance < 90) yield new ItemStack(Items.GLOWSTONE_DUST);
                if (chance < 96) yield new ItemStack(Items.BLAZE_ROD);
                if (chance < 99) yield new ItemStack(Items.DIAMOND);
                yield new ItemStack(Items.ANCIENT_DEBRIS);
            }
            case 4 -> {
                if (chance < 50) yield new ItemStack(Items.END_STONE);
                if (chance < 75) yield new ItemStack(Items.OBSIDIAN);
                if (chance < 90) yield new ItemStack(Items.ENDER_PEARL);
                if (chance < 97) yield new ItemStack(Items.CHORUS_FRUIT);
                yield new ItemStack(Items.ENDER_EYE);
            }
            default -> {
                if (chance < 20) yield new ItemStack(Items.DIAMOND_BLOCK);
                if (chance < 30) yield new ItemStack(Items.NETHERITE_INGOT);
                if (chance < 60) yield new ItemStack(Items.EMERALD_BLOCK);
                yield new ItemStack(Items.EXPERIENCE_BOTTLE, 4);
            }
        };
    }
}
