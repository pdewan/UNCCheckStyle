package unc.tools.checkstyle;

import java.lang.reflect.Field;

public class UNCCheckStyleUtil {
	public static Field getDeclaredField (Class aClass, String aFieldName) throws NoSuchFieldException{
		Field result;
		try {
			result = aClass.getDeclaredField(aFieldName);
		} catch (NoSuchFieldException e) {
			String aModifiedFieldName = "m" +
					Character.toUpperCase(aFieldName.charAt(0)) +
					aFieldName.substring(1);
			
			result = aClass.getDeclaredField(aModifiedFieldName);
			
			
		}
		return result;
	}

}
