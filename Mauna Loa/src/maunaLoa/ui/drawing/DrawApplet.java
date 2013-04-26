package maunaLoa.ui.drawing;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public final class DrawApplet extends JApplet implements ActionListener {

	private static final long serialVersionUID = 1L;

	JPanel inPanel = new JPanel();

	public void init() {
		try {
			inPanel.setLayout(new BorderLayout());
			inPanel.add(ToolSelector.createFullProject(this),
					BorderLayout.CENTER);
			add(inPanel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void finished() {
		final DrawApplet upper = this;
		new Thread() {
			public synchronized void run() {
				try {
					wait(2000);
				} catch (InterruptedException e) {
				}
				JButton cont = new JButton("Now explore data from other sites!");
				Font f = cont.getFont().deriveFont(Font.ITALIC, 35F);
				Map<TextAttribute, Integer> underlineMap = new HashMap<TextAttribute, Integer>();
				underlineMap.put(TextAttribute.UNDERLINE,
						TextAttribute.UNDERLINE_ON);
				f = f.deriveFont(underlineMap);
				cont.setFont(f);
				cont.setForeground(Color.BLUE);
				Dimension size = cont.getPreferredSize();
				size.width += 10;
				cont.setPreferredSize(size);
				JPanel jp = new JPanel();
				jp.setAlignmentX(Component.CENTER_ALIGNMENT);
				jp.add(cont);
				inPanel.add(jp, BorderLayout.SOUTH);
				cont.addActionListener(upper);
				cont.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				cont.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				inPanel.updateUI();
			}
		}.start();
	}

	public void actionPerformed(ActionEvent arg0) {
		String base = getDocumentBase().toExternalForm();
		try {
			URL newBase = new URL(base.substring(0, base.lastIndexOf('/'))
					+ "/explore.html");
			getAppletContext().showDocument(newBase);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
