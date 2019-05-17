package mask_of_loki.coretweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import mask_of_loki.coretweaks.config.MainConfig;
import net.minecraft.village.VillagerData;

@Mixin(VillagerData.class)
public class VillagerDataMixin {
	@Shadow
	private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 50, 100, 150};
//	private static final int[] MCT_LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000};
//
//
//	public static int getLowerLevelExperience(int lvl) {
//		boolean canLevel = canLevelUp(lvl);
//		if (MainConfig.tweakTrades() && MainConfig.qualityTrades()) {
//			return canLevel? MCT_LEVEL_BASE_EXPERIENCE[lvl - 1] : 0;
//		}
//		return canLevel ? LEVEL_BASE_EXPERIENCE[lvl - 1] : 0;
//	}
//
//	public static int getUpperLevelExperience(int lvl) {
//		boolean canLevel = canLevelUp(lvl);
//		if (MainConfig.tweakTrades() && MainConfig.qualityTrades()) {
//			return canLevel? MCT_LEVEL_BASE_EXPERIENCE[lvl] : 0;
//		}
//		return canLevel ? LEVEL_BASE_EXPERIENCE[lvl] : 0;
//	}
//
//	public static boolean canLevelUp(int lvl) {
//		if (MainConfig.tweakTrades() && MainConfig.qualityTrades()) {
//			return lvl > 0 && lvl < MCT_LEVEL_BASE_EXPERIENCE.length;
//		}
//		return lvl > 0 && lvl < LEVEL_BASE_EXPERIENCE.length;
//	}
}
