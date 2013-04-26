package maunaLoa.ui.drawing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

import maunaLoa.data.CO2Graph;
import maunaLoa.data.DataFetcher;

public final class ToolSelector extends JPanel implements ActionListener {
	private static final long serialVersionUID = -8124729862812679379L;

	private final JButton pen;

	private final JButton lineTool;

	private final JButton eraser;

	private final JPanel buttonPanel = new JPanel();

	private final DrawOn dO;

	private int buttons = 3;

	public ToolSelector(Drawable draw, Color c) {
		buttonPanel.setLayout(new GridLayout(buttons, 1));
		ImageIcon penPic = new ImageIcon(getClass().getClassLoader()
				.getResource("pen.png"));
		pen = new JButton("Pencil", penPic);
		pen.setToolTipText("Click and drag on the graph");
		buttonPanel.add(pen);
		ImageIcon linePic = new ImageIcon(getClass().getClassLoader()
				.getResource("lineTool.png"));
		lineTool = new JButton("Point plotter", linePic);
		lineTool
				.setToolTipText("Click repeatedly to plot points.  Double click to end the path");
		buttonPanel.add(lineTool);
		ImageIcon eraserPic = new ImageIcon(getClass().getClassLoader()
				.getResource("eraser.png"));
		eraser = new JButton("Erase all", eraserPic);
		eraser.setToolTipText("Clear everything and start over");
		buttonPanel.add(eraser);
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.EAST);
		dO = new DrawOn(draw, c);
		add(dO, BorderLayout.CENTER);
		pen.addActionListener(this);
		lineTool.addActionListener(this);
		eraser.addActionListener(this);
	}

	public static void main(String[] args) throws IOException {
		final JFrame jf = new JFrame();
		jf.getContentPane().add(createFullProject(null));
		jf.setSize(800, 600);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static JPanel createFullProject(final DrawApplet da)
			throws IOException {
		final JPanel contain = new JPanel();
		contain.setLayout(new GridLayout(1, 1));
		DataFetcher df = new DataFetcher();
		df.addSource("Mauna Loa", "Mauna Loa, Hawaii, U.S.A. 19°32' N, 155°35' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/maunaloa.co2",
				Color.BLACK);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DAY_OF_MONTH, 15);
		c.set(Calendar.HOUR, 24);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
                
                /*
                 * The original implementation was a dynamic time frame that was
                 * 10 years ago until the present time, but this ran out of data.
                 */
                /******            BEGIN CHANGED BLOCK                 ********/
                c.set(Calendar.YEAR, 1998); 
		//c.add(Calendar.YEAR, -10);
		Date start = c.getTime();
                c.set(Calendar.YEAR, 2008);
		//c.add(Calendar.YEAR, 10);
                /******            END CHANGED BLOCK                    *******/
                
		Date end = c.getTime();
		final CO2Graph c2g = new CO2Graph(
				"Mauna Loa Carbon Dioxide Concentration", df, start, end,
				new int[] { 0 });
		c.add(Calendar.YEAR, -3); //this sets the number of years for the user to draw
		c2g.setInvisiblePoint(c.getTime());
		final ToolSelector ts = new ToolSelector(new CO2Drawable(c2g),
				Color.RED);
		contain.add(ts);
		final JButton compare = new JButton("Finished");
		compare.setToolTipText("I'm done plotting points.  How close was I?");
		compare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				c2g.setInvisiblePoint(null);
				c2g.repaint();
				ts.removeButton(compare);
				ts.setEnabled(false);
				contain.updateUI();
				if (da != null)
					da.finished();
			}
		});
		ts.addButton(compare);
		return contain;
	}

	public void removeButton(JButton b) {
		buttonPanel.remove(b);
		((GridLayout) buttonPanel.getLayout()).setRows(--buttons);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == pen)
			dO.setDrawType(DrawOn.PEN_DRAW);
		else if (ae.getSource() == lineTool)
			dO.setDrawType(DrawOn.LINE_DRAW);
		else if (ae.getSource() == eraser)
			dO.eraseAll();
	}

	public void addButton(JButton b) {
		((GridLayout) buttonPanel.getLayout()).setRows(++buttons);
		buttonPanel.add(b);
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		for (Component button : buttonPanel.getComponents())
			button.setEnabled(b);
		dO.setEnabled(b);
	}
}
