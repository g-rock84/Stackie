package com.github.lunatrius.stackie.lib;

import com.google.common.base.Throwables;

import java.io.InputStream;
import java.util.Properties;

public class Reference {
	static {
		Properties prop = new Properties();

		try {
			InputStream stream = Reference.class.getClassLoader().getResourceAsStream("version.properties");
			prop.load(stream);
			stream.close();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		VERSION = prop.getProperty("version.mod");
		FORGE = prop.getProperty("version.forge");
		MINECRAFT = prop.getProperty("version.minecraft");
	}

	public static final String MODID = "Stackie";
	public static final String NAME = "Stackie";
	public static final String VERSION;
	public static final String FORGE;
	public static final String MINECRAFT;
	public static final String GUI_FACTORY = "com.github.lunatrius.stackie.client.gui.GuiFactory";
}
