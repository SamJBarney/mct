package mask_of_loki.coretweaks.mixin;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.Vec3d;

@Mixin(EntitySelector.class)
public class EntitySelectorMixin {
	NumberRange.IntRange mct_lightLevel;

	public void mct_setLightLevel(NumberRange.IntRange value) {
		this.mct_lightLevel = value;
	}
	
	@Inject(method = "getEntities(Lnet/minecraft/util/math/Vec3d;Ljava/util/List;)Ljava/util/List;", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void filterLightLevel(Vec3d _ignore, List<? extends Entity> list_1, CallbackInfoReturnable ci) {
		if (this.mct_lightLevel != null && !this.mct_lightLevel.isDummy()) {
			list_1.removeIf(entity -> {
				int lvl = entity.getEntityWorld().getLightLevel(entity.getBlockPos());
				return !this.mct_lightLevel.test(lvl);
			});
		}
	}
}
