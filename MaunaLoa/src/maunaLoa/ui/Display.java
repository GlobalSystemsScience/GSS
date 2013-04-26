package maunaLoa.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import maunaLoa.data.*;

public final class Display extends JPanel implements ChangeListener {

	private static final long serialVersionUID = -4924721517684160151L;

	SpinnerDateModel startModel, endModel;

	JSpinner startDate, endDate;

	SpinnerNumberModel lower, upper;

	JSpinner bottomCO2, topCO2;

	JCheckBox[] siteBoxes;

	JCheckBox topAuto = new JCheckBox("Auto"), bottomAuto = new JCheckBox(
			"Auto");

	CO2Graph c2g;

	public Display(CO2Graph c2g, Date minDate, Date maxDate, double minCO2,
			double maxCO2) throws IOException {
		this.c2g = c2g;
		setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		JPanel panel2;
		Calendar c = Calendar.getInstance();
		c.setTime(minDate);
		c.add(Calendar.MONTH, -1);
		Date monthDownDate = c.getTime();
		Date dummyDate = c.getTime();
		startModel = new SpinnerDateModel(dummyDate, monthDownDate, maxDate,
				Calendar.MONTH);
		endModel = new SpinnerDateModel(dummyDate, monthDownDate, maxDate,
				Calendar.MONTH);
		startDate = new JSpinner(startModel);
		((JSpinner.DateEditor) startDate.getEditor()).getFormat().applyPattern(
				"MMM yyyy");
		startDate.setValue(minDate);
		endDate = new JSpinner(endModel);
		((JSpinner.DateEditor) endDate.getEditor()).getFormat().applyPattern(
				"MMM yyyy");
		endDate.setValue(maxDate);
		panel2 = new JPanel();
		panel2.setBackground(Color.WHITE);
		panel2.add(new JLabel("Start"));
		panel2.add(startDate);
		panel.add(panel2);
		panel2 = new JPanel();
		panel2.setBackground(Color.WHITE);
		panel2.add(new JLabel("End"));
		panel2.add(endDate);
		panel.add(panel2);
		add(panel, BorderLayout.SOUTH);
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		GraphDataSource[] sources = c2g.getData();
		panel.setLayout(new GridLayout(sources.length, 1));
		siteBoxes = new JCheckBox[sources.length];
		for (int i = 0; i < sources.length; i++) {
			siteBoxes[i] = new JCheckBox(sources[i].name);
			siteBoxes[i].setBackground(Color.WHITE);
			siteBoxes[i].setForeground(sources[i].color);
			siteBoxes[i].setToolTipText(sources[i].fullName);
			panel.add(siteBoxes[i]);
		}
		for (int i = 0; i < sources.length; i++) {
			if (c2g.isVisible(i))
				siteBoxes[i].setSelected(true);
		}
		add(panel, BorderLayout.EAST);
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		lower = new SpinnerNumberModel(minCO2, minCO2, maxCO2, 1D);
		upper = new SpinnerNumberModel(maxCO2, minCO2, maxCO2, 1D);
		bottomCO2 = new JSpinner(lower);
		JFormattedTextField jftf;
		jftf = ((JSpinner.NumberEditor) bottomCO2.getEditor()).getTextField();
		jftf.setColumns(5);
		jftf.setHorizontalAlignment(JTextField.LEFT);
		topCO2 = new JSpinner(upper);
		jftf = ((JSpinner.NumberEditor) topCO2.getEditor()).getTextField();
		jftf.setColumns(5);
		jftf.setHorizontalAlignment(JTextField.LEFT);
		JPanel panel4 = new JPanel();
		panel4.setBackground(Color.WHITE);
		panel.setLayout(new GridLayout(2, 1));
		panel2 = new JPanel();
		panel2.setBackground(Color.WHITE);
		JPanel panel3 = new JPanel();
		panel3.setBackground(Color.WHITE);
		panel3.setLayout(new GridLayout(3, 1));
		panel3.add(new JLabel("Maximum"));
		topCO2.setEnabled(false);
		panel3.add(topCO2);
		topAuto.setSelected(true);
		panel3.add(topAuto);
		panel2.add(panel3);
		panel.add(panel2);
		panel2 = new JPanel();
		panel2.setBackground(Color.WHITE);
		panel3 = new JPanel();
		panel3.setBackground(Color.WHITE);
		panel3.setLayout(new GridLayout(3, 1));
		panel3.add(new JLabel("Minimum"));
		bottomCO2.setEnabled(false);
		panel3.add(bottomCO2);
		bottomAuto.setSelected(true);
		bottomAuto.setBackground(Color.WHITE);
		panel3.add(bottomAuto);
		panel2.add(panel3);
		panel.add(panel2);
		panel4.add(panel);
		add(panel4, BorderLayout.WEST);
		bottomCO2.setValue(c2g.minCO2);
		topCO2.setValue(c2g.maxCO2);
		add(c2g, BorderLayout.CENTER);
		startDate.addChangeListener(this);
		endDate.addChangeListener(this);
		bottomCO2.addChangeListener(this);
		topCO2.addChangeListener(this);
		topAuto.addChangeListener(this);
		topAuto.setBackground(Color.WHITE);
		bottomAuto.addChangeListener(this);
		for (JCheckBox box : siteBoxes) {
			box.addChangeListener(this);
		}
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == startDate) {
			c2g.setStartDate((Date) startDate.getValue());
			if (bottomAuto.isSelected())
				bottomCO2.setValue(c2g.minCO2);
			if (topAuto.isSelected())
				topCO2.setValue(c2g.maxCO2);
			endModel.setStart((Date) startDate.getValue());
		} else if (ce.getSource() == endDate) {
			c2g.setEndDate((Date) endDate.getValue());
			if (bottomAuto.isSelected())
				bottomCO2.setValue(c2g.minCO2);
			if (topAuto.isSelected())
				topCO2.setValue(c2g.maxCO2);
			startModel.setEnd((Date) endDate.getValue());
		} else if (ce.getSource() == bottomCO2) {
			if (bottomAuto.isSelected())
				c2g.setMinCO2(-99.99);
			else
				c2g.setMinCO2((Double) bottomCO2.getValue());
			upper.setMinimum((Double) bottomCO2.getValue());
		} else if (ce.getSource() == topCO2) {
			if (topAuto.isSelected())
				c2g.setMaxCO2(-99.99);
			else
				c2g.setMaxCO2((Double) topCO2.getValue());
			lower.setMaximum((Double) topCO2.getValue());
		} else if (ce.getSource() == bottomAuto) {
			if (bottomAuto.isSelected()) {
				bottomCO2.setEnabled(false);
				c2g.setMinCO2(-99.99);
				bottomCO2.setValue(c2g.minCO2);
			} else {
				bottomCO2.setEnabled(true);
				c2g.setMinCO2((Double) bottomCO2.getValue());
			}
		} else if (ce.getSource() == topAuto) {
			if (topAuto.isSelected()) {
				topCO2.setEnabled(false);
				c2g.setMaxCO2(-99.99);
				topCO2.setValue(c2g.maxCO2);
			} else {
				topCO2.setEnabled(true);
				c2g.setMaxCO2((Double) topCO2.getValue());
			}
		} else {
			for (int i = 0; i < siteBoxes.length; i++) {
				if (ce.getSource() == siteBoxes[i]) {
					c2g.setVisible(i, siteBoxes[i].isSelected());
					if (bottomAuto.isSelected())
						bottomCO2.setValue(c2g.minCO2);
					if (topAuto.isSelected())
						topCO2.setValue(c2g.maxCO2);
					break;
				}
			}
		}
		c2g.repaint();
	}

	public static void main(String[] args) throws IOException {
		JFrame jf = new JFrame("CO2 Graphing Tool");
		jf.setSize(800, 600);

		jf.getContentPane().add(createFullDisplay());
		jf.setVisible(true);
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public static Display createFullDisplay() throws IOException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 15);
		c.set(Calendar.MONTH, Calendar.DECEMBER);
		c.add(Calendar.YEAR, -1);
		c.set(Calendar.HOUR_OF_DAY, 24);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date last = c.getTime();
		c.set(Calendar.YEAR, 1958);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		Date first = c.getTime();
		DataFetcher df = new DataFetcher();
		df.addSource("Mauna Loa",
				"Mauna Loa, Hawaii, U.S.A. 19°32' N, 155°35' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/maunaloa.co2",
				Color.BLACK);
		df.addSource("Barrow", "Barrow, Alaska, U.S.A. 71°19' N, 156°36' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/barrsio.co2",
				new Color(128, 255, 128));
		df
				.addSource(
						"American Samoa",
						"American Samoa (Cape Matatula), U.S. Territory 14°15' S, 170°34' W",
						"http://cdiac.esd.ornl.gov/ftp/trends/co2/samsio.co2",
						Color.ORANGE);
		df.addSource("South Pole", "South Pole, Antarctica 89°59' S, 24°48' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/sposio.co2",
				Color.BLUE);
		df.addSource("Alert",
				"Alert, Northwest Territories, Canada 82°28' N, 62°30' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/altsio.co2",
				Color.CYAN);
		df.addSource("Cape Kumukahi",
				"Cape Kumukahi, Hawaii, U.S.A. 19°31' N, 154°49' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/kumsio.co2",
				Color.RED);
		df.addSource("Christmas Island",
				"Christmas Island, Kiribati 2°00' N, 157°18' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/chr.dat",
				Color.ORANGE.darker().darker());
		df.addSource("Baring Head",
				"Baring Head, New Zealand 41°24' S, 174°54' E",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/nzd.dat",
				Color.MAGENTA);
		df.addSource("Kermadec Islands",
				"Kermadec Islands, Raoul Island 29°2' S, 177°9' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/ker.dat", Color.GREEN
						.darker());
		df.addSource("La Jolla Pier",
				"La Jolla Pier, California, U.S.A. 32°9' N, 117°3' W",
				"http://cdiac.esd.ornl.gov/ftp/trends/co2/ljo.dat",
				Color.YELLOW.darker());
		CO2Graph c2g = new CO2Graph(df, first, last, new int[] { 3 });
		return new Display(c2g, first, last, 0D, 500D);
	}
}
