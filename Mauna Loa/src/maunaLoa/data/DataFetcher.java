package maunaLoa.data;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public final class DataFetcher {

	private final Calendar cal = Calendar.getInstance();

	private final ArrayList<GraphDataSource> data = new ArrayList<GraphDataSource>();

	public DataFetcher() {
		cal.set(Calendar.DAY_OF_MONTH, 15);
		cal.set(Calendar.HOUR_OF_DAY, 24);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	public void addSource(String n, String f, String s, Color c) throws IOException {
		GraphDataSource source = new GraphDataSource(n, f, s, c);
		data.add(source);
	}

	public GraphDataSource[] getDataArray() {
		return data.toArray(new GraphDataSource[data.size()]);
	}
}
