package maunaLoa.data;

import java.awt.Color;
import java.util.*;

public final class GraphDataSource {

	public final String name, fullName, site;

	public final Color color;

	private final Calendar cal = Calendar.getInstance();

	private ArrayList<Datum> data;

	protected GraphDataSource(String n, String f, String s, Color c) {
		name = n;
		fullName = f;
		site = s;
		color = c;
		cal.set(Calendar.DAY_OF_MONTH, 15);
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	public Datum get(int i) {
		if (data == null) {
			generateData();
		}
		if (i >= data.size())
			return null;
		return data.get(i);
	}

	private void generateData() {
		Scanner scan;
		data = new ArrayList<Datum>();
		String fileName = site.substring(site.lastIndexOf('/') + 1);
		scan = new Scanner(getClass().getClassLoader().getResourceAsStream(
				"siteData/" + fileName));
		while (scan.hasNextLine())
			parseLine(scan.nextLine().trim());
	}

	public int size() {
		if (data == null)
			generateData();
		return data.size();
	}

	private void add(Date time, double d) {
		data.add(new Datum(time, d));
	}

	private void parseLine(String s) {
		if (!(s.startsWith("19") || s.startsWith("20"))) {
			return;
		}
		String[] values = s.split("\t");
		cal.set(Calendar.YEAR, Integer.parseInt(values[0].trim()));
		String val;
		double d;
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		for (int i = 1; i <= 12; i++) {
			val = values[i].trim();
			if (val.length() == 0)
				if (data.size() == 0) {
					cal.add(Calendar.MONTH, 1);
					continue;
				} else
					d = -99.99;
			else
				d = Double.parseDouble(val);
			add(cal.getTime(), d);
			cal.add(Calendar.MONTH, 1);
		}
	}
}
