package mask_of_loki.coretweaks.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.command.EntitySelectorReader;
import net.minecraft.util.NumberRange;
import net.minecraft.util.NumberRange.IntRange;

public class EntitySelectorReaderHelper {
	private static Method _setLightLevel;
	private static Method _getLightLevel;

	public static void init() {
		try {
			_setLightLevel = EntitySelectorReader.class.getMethod("mct_setLightLevel", NumberRange.IntRange.class);
			_getLightLevel = EntitySelectorReader.class.getMethod("mct_getLightLevel");
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setLightLevel(EntitySelectorReader reader, NumberRange.IntRange lvl) {
		if (_setLightLevel != null) {
			try {
				_setLightLevel.invoke(reader, lvl);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static NumberRange.IntRange getLightLevel(EntitySelectorReader reader) {
		if (_getLightLevel != null) {
			try {
				return (IntRange) _getLightLevel.invoke(reader);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return NumberRange.IntRange.ANY;
			}
		} else {
			return NumberRange.IntRange.ANY;
		}
	}
}
