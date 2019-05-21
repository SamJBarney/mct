package mask_of_loki.coretweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mask_of_loki.coretweaks.CTTradeOffers;
import mask_of_loki.coretweaks.config.MainConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends AbstractTraderEntity implements VillagerDataContainer {
	
	private int mct_level;

	public VillagerEntityMixin(EntityType<? extends AbstractTraderEntity> entityType_1, World world_1) {
		super(entityType_1, world_1);
		// TODO Auto-generated constructor stub
	}

	@Inject(method = "fillRecipes", at = @At("HEAD"), cancellable = true)
	protected void onFillRecipes(CallbackInfo ci) {
		if (MainConfig.tweakTrades()) {
			VillagerData data = this.getVillagerData();
			TradeOffers.Factory[] trades = CTTradeOffers.villagerTrades(data.getProfession(), data.getLevel());
			if (trades != null) {
				TraderOfferList offers = this.getOffers();
				this.fillRecipesFromPool(offers, trades, 2);
			}
			ci.cancel();
		}
	}

}
