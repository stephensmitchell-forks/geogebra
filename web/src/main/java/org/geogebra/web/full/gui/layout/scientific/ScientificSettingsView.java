package org.geogebra.web.full.gui.layout.scientific;

import java.util.Arrays;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Csilla
 * 
 *         Settings view of scientific calculator
 *
 */
public class ScientificSettingsView extends MyHeaderPanel implements FastClickHandler {

	private final AppW app;
	private HeaderView headerView;
	private boolean isOpen;
	private Localization localization;

	/**
	 * Build and style settings view for sci calc
	 * 
	 * @param app
	 *            application
	 */
	public ScientificSettingsView(AppW app) {
		this.addStyleName("scientificSettingsView");
		this.app = app;
		isOpen = true;
		localization = app.getLocalization();
		createHeader();
		createContent();
	}
	
	private void createHeader() {
		headerView = new HeaderView(app);
		headerView.setCaption(localization.getMenu("Settings"));
		StandardButton backButton = headerView.getBackButton();
		backButton.addFastClickHandler(this);
		
		setHeaderWidget(headerView);
		resizeHeader();
	}

	private void createContent() {
		ScrollPanel algebraScrollPanel = new ScrollPanel();
		algebraScrollPanel.addStyleName("settingsPanelScientificNoHeader");
		ComponentDropDown dropDown = new ComponentDropDown(app);
		dropDown.setElements(Arrays.asList("Hello", "World"));
		dropDown.setTitleText("Choose");
		dropDown.setSelected(0);
		algebraScrollPanel.add(dropDown);
		setContentWidget(algebraScrollPanel);
	}

	@Override
	public void onClick(Widget source) {
		if (source == headerView.getBackButton()) {
			close();
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}

	/**
	 * @return true if settings view is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            true if open settings, false otherwise
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	@Override
	public void resizeTo(int width, int height) {
		resizeHeader();
	}
	
	@Override
	public void onResize() {
		super.onResize();
		resizeHeader();
	}
	
	private void resizeHeader() {
		boolean smallScreen = app.isSmallScreen();
		headerView.resizeTo(smallScreen);
	}
}