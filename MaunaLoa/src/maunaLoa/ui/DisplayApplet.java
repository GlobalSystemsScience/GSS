package maunaLoa.ui;

import java.io.IOException;

import javax.swing.JApplet;

public final class DisplayApplet extends JApplet {

	private static final long serialVersionUID = -8035274372819749563L;

	public void init() {
		try {
			getContentPane().add(Display.createFullDisplay());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
