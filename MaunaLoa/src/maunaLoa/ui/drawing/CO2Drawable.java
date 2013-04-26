package maunaLoa.ui.drawing;

import java.awt.Rectangle;

import javax.swing.JComponent;

import maunaLoa.data.CO2Graph;

public final class CO2Drawable implements Drawable {

	CO2Graph c2g;

	CO2Drawable(CO2Graph c2g) {
		this.c2g = c2g;
	}

	public JComponent getComponent() {
		return c2g;
	}

	public Rectangle getDrawRectangle() {
		return c2g.getGraphBounds();
	}
}
