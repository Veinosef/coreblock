package net.veinosef.coreblock;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreBlockMod implements ModInitializer {
    public static final String MOD_ID = "coreblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Block CORE_BLOCK = new CoreBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).strength(-1.0F, 3600000.0F).dropsNothing());

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "core_block"), CORE_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "core_block"), new BlockItem(CORE_BLOCK, new Item.Settings()));

        CoreBlockWorldHandler.register();

        LOGGER.info("CoreBlock 1.21 Basariyla Yuklendi!");
    }
}
