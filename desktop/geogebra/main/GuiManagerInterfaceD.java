package geogebra.main;

import geogebra.common.gui.GuiManagerInterface;

import java.awt.Component;
import java.io.File;

public interface GuiManagerInterfaceD extends GuiManagerInterface {

	void setToolBarDefinition(String allTools);

	String getToolbarDefinition();

	File showSaveDialog(String fileExtension, File currentFile, String string,
			boolean b, boolean c);

	void removeFromToolbarDefinition(int i);

	boolean loadFile(File currentFile, boolean b);

	void updateMenuBarLayout();

	Component getMenuBar();

	boolean saveAs();

	void initMenubar();

	void setFocusedPanel(int viewSpreadsheet, boolean updatePropertiesView);

	File getDataFile();

}