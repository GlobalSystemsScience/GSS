package maunaLoa.data;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DateFormatSymbols;
import java.util.*;

import javax.swing.JComponent;

public final class CO2Graph extends JComponent {

	private static final long serialVersionUID = -974376492230990504L;

	private static final int PREFERRED_X_INTERVALS = 10;

	private static final int[] POSSIBLE_X_INTERVAL = { 1, 2, 3, 4, 6, 12, 24,
			60, 120, 240, 600, 1200 };

	private int xInterval;

	private static final int PREFERRED_Y_INTERVALS = 10;

	private static final int[] POSSIBLE_INTERVAL = { 1, 2, 5, 10, 20, 50, 100,
			200, 500 };

	private int yInterval;

	private int yStart;

	private GraphDataSource[] data;

	private boolean[] visible;

	private LinkedList<Integer> visibleStack;

	private Calendar startDate = Calendar.getInstance(), endDate = Calendar
			.getInstance();

	private Date invisibleDate;

	private boolean hasInvisible = false;

	private int monthWidth;

	private int[] startIndex;

	public double minCO2, maxCO2;

	private boolean relativeMin = true, relativeMax = true;

	private double co2Span;

	private final String title;

	private static final String[] months = new DateFormatSymbols()
			.getShortMonths();

	private double[] matrix = { 0, 1, -1, 0 };

	public CO2Graph(DataFetcher df, Date startDate, Date endDate, int[] visible) {
		this("Carbon Dioxide Concentration", df, startDate, endDate, visible);
	}

	public CO2Graph(String title, DataFetcher df, Date startDate, Date endDate,
			int[] visible) {
		this.title = title;
		this.data = df.getDataArray();
		this.startDate.setTime(startDate);
		this.endDate.setTime(endDate);
		this.visible = new boolean[data.length];
		startIndex = new int[data.length];
		Arrays.fill(this.visible, false);
		visibleStack = new LinkedList<Integer>();
		for (int i : visible) {
			this.visible[i] = true;
			visibleStack.addFirst(i);
		}
		recalculate();
	}

	public void setStartDate(Date d) {
		startDate.setTime(d);
		recalculate();
	}

	public void setEndDate(Date d) {
		endDate.setTime(d);
		recalculate();
	}

	public void setMinCO2(double co2) {
		if (co2 >= 0)
			relativeMin = false;
		else
			relativeMin = true;
		minCO2 = co2;
		recalculate();
	}

	public void setMaxCO2(double co2) {
		if (co2 >= 0)
			relativeMax = false;
		else
			relativeMax = true;
		maxCO2 = co2;
		recalculate();
	}

	private void recalculate() {
		monthWidth = monthDif(this.startDate, this.endDate);
		int i;
		for (i = 0; i < data.length; i++)
			if (visible[i])
				startIndex[i] = searchOnAfter(data[i], startDate.getTime());
		if (relativeMin)
			minCO2 = Double.MAX_VALUE;
		if (relativeMax)
			maxCO2 = 0D;
		int n;
		if (relativeMin || relativeMax)
			for (i = 0; i < data.length; i++)
				if (visible[i])
					for (Datum dat = data[i].get(n = startIndex[i]); dat.d
							.compareTo(endDate.getTime()) < 0
							&& n < data[i].size(); dat = data[i].get(n++)) {
						if (dat.co2 < minCO2 && dat.co2 >= 0 && relativeMin)
							minCO2 = dat.co2;
						if (dat.co2 > maxCO2 && relativeMax)
							maxCO2 = dat.co2;
					}
		co2Span = maxCO2 - minCO2;
		double bestFit = 1D;
		for (int interval : POSSIBLE_INTERVAL) {
			if (Math.abs(interval / co2Span - 1D / PREFERRED_Y_INTERVALS) < Math
					.abs(bestFit - 1D / PREFERRED_Y_INTERVALS)) {
				yInterval = interval;
				bestFit = interval / co2Span;
			}
		}
		yStart = (int) (minCO2 - minCO2 % yInterval + yInterval);
		bestFit = 1D;
		for (int interval : POSSIBLE_X_INTERVAL) {
			if (Math.abs((double) interval / monthWidth - 1D
					/ PREFERRED_X_INTERVALS) < Math.abs(bestFit - 1D
					/ PREFERRED_X_INTERVALS)) {
				xInterval = interval;
				bestFit = (double) interval / monthWidth;
			}
		}
	}

	public void setVisible(int i, boolean b) {
		visible[i] = b;
		if (b) {
			if (!visibleStack.contains(i))
				visibleStack.add(i);
		} else
			visibleStack.remove(new Integer(i));
		recalculate();
	}

	private static int searchOnAfter(GraphDataSource data, Date startDate) {
		int first = 0;
		int last = data.size() - 1;
		int guess;
		if (startDate.compareTo(data.get(first).d) < 0)
			return 0;
		else if (startDate.compareTo(data.get(last).d) > 0)
			return data.size();
		while (first < last) {
			guess = (first + last) / 2;
			int compare = data.get(guess).d.compareTo(startDate);
			if (compare > 0)
				last = guess;
			else if (compare < 0)
				first = guess + 1;
			else if (compare == 0)
				return guess;
		}
		return first;
	}

	public void drawGraph(Graphics2D g2, Rectangle graphBounds, int sourceIndex) {
		int lastX = 0;
		int lastY = 0;
		int i;
		int xCoord, yCoord;
		Calendar cal = Calendar.getInstance();
		Calendar lastCal = Calendar.getInstance();
		boolean goodLast = false;
		for (Datum dat = data[sourceIndex].get(i = startIndex[sourceIndex]); endDate
				.getTime().compareTo(dat.d) >= 0
				&& (!hasInvisible || invisibleDate.compareTo(dat.d) >= 0)
				&& i < data[sourceIndex].size(); dat = data[sourceIndex]
				.get(i++)) {
			cal.setTime(dat.d);
			if (dat.co2 < 0D) {
				goodLast = false;
				continue;
			}
			xCoord = monthDif(startDate, cal) * graphBounds.width / monthWidth
					+ graphBounds.x;
			yCoord = graphBounds.height
					- (int) ((dat.co2 - minCO2) * graphBounds.height / co2Span)
					+ graphBounds.y;
			if (monthDif(lastCal, cal) == 1 && goodLast)
				g2.drawLine(lastX, lastY, xCoord, yCoord);
			lastX = xCoord;
			lastY = yCoord;
			goodLast = true;
			lastCal.setTime(dat.d);
		}
	}

	private int monthDif(Calendar d1, Calendar d2) {
		return (d2.get(Calendar.YEAR) - d1.get(Calendar.YEAR)) * 12
				+ d2.get(Calendar.MONTH) - d1.get(Calendar.MONTH);
	}

	public synchronized void paintComponent(Graphics g) {
		if (!startDate.before(endDate))
			return;
		Rectangle graphBounds = getGraphBounds();
		int graphTop = graphBounds.y + graphBounds.height;
		int graphRight = graphBounds.x + graphBounds.width;
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fill(graphBounds);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate.getTime());
		g2.setColor(Color.LIGHT_GRAY);
		for (; monthDif(cal, endDate) > 0; cal.add(Calendar.MONTH, xInterval)) {
			int xCoord = (int) (graphBounds.width * monthDif(startDate, cal)
					/ monthWidth + graphBounds.x);
			g2.drawLine(xCoord, graphBounds.y, xCoord, graphTop);
		}
		for (int y = yStart; y <= maxCO2; y += yInterval) {
			int yCoord = (int) (graphBounds.y + (graphBounds.height * (1 - (y - minCO2)
					/ co2Span)));
			g2.drawLine(graphBounds.x, yCoord, graphBounds.x
					+ graphBounds.width, yCoord);
		}
		g2.setColor(Color.BLACK);
		g2.drawRect(graphBounds.x, graphBounds.y, graphBounds.width,
				graphBounds.height);
		g2.setStroke(new BasicStroke(2));
		if (monthWidth > 0) {
			for (int i : visibleStack) {
				g2.setColor(data[i].color);
				drawGraph(g2, graphBounds, i);
			}
		}
		GradientPaint gradient = new GradientPaint(0, 0, Color.GREEN, 0,
				getHeight(), Color.CYAN);
		Paint dp = g2.getPaint();
		g2.setPaint(gradient);
		g2.fillRect(0, 0, graphBounds.x, getHeight());
		g2.fillRect(graphBounds.x, 0, graphBounds.width + 1, graphBounds.y);
		g2.fillRect(graphBounds.x, graphTop + 1, graphBounds.width + 1,
				getHeight() - graphTop);
		g2.fillRect(graphRight + 1, 0, getWidth() - (graphRight + 1),
				getHeight());
		g2.setPaint(dp);
		g2.setColor(Color.BLACK);
		cal.setTime(startDate.getTime());
		g2.setFont(g2.getFont().deriveFont(Font.BOLD));
		for (; monthDif(cal, endDate) > 0; cal.add(Calendar.MONTH, xInterval)) {
			String s;
			if (xInterval >= 12)
				s = Integer.toString(cal.get(Calendar.YEAR));
			else
				s = months[cal.get(Calendar.MONTH)] + " "
						+ cal.get(Calendar.YEAR);
			cal.add(Calendar.SECOND, (int) (xInterval / 1000));
			int xCoord = (int) (graphBounds.width * monthDif(startDate, cal)
					/ monthWidth + graphBounds.x);
                        System.out.println(s);
			g2.drawString(s, xCoord - 5, graphBounds.y + graphBounds.height + 15);
		}
		int wordWidth = (int) g2.getFont().createGlyphVector(
				g2.getFontRenderContext(), "Concentration ( in ppmv )")
				.getVisualBounds().getWidth();
		g2.drawString("Concentration ( in ppm )", 5, graphBounds.y - 10);
		wordWidth = (int) g2.getFont().createGlyphVector(
				g2.getFontRenderContext(), title).getVisualBounds().getWidth();
		g2.drawString(title, (getWidth() - wordWidth) / 2, 20);
		wordWidth = (int) g2.getFont().createGlyphVector(
				g2.getFontRenderContext(), "Date").getVisualBounds().getWidth();
		g2.drawString("Date", (getWidth() - wordWidth) / 2, getHeight() - 10);
		for (int y = yStart; y <= maxCO2; y += yInterval) {
			int yCoord = (int) (graphBounds.y + (graphBounds.height * (1 - (y - minCO2)
					/ co2Span)));
			wordWidth = (int) g2.getFont().createGlyphVector(
					g2.getFontRenderContext(), Integer.toString(y))
					.getVisualBounds().getWidth();
			g2.drawString(Integer.toString(y),
					(int) (graphBounds.x - wordWidth - 5), yCoord + 5);
		}
	}

	public Rectangle getGraphBounds() {
		return new Rectangle(60, 40, getWidth() - 80, getHeight() - 130);
	}

	public GraphDataSource[] getData() {
		return data;
	}

	public void setInvisiblePoint(Date time) {
		if (time == null)
			hasInvisible = false;
		else {
			invisibleDate = time;
			hasInvisible = true;
		}
	}

	public boolean isVisible(int i) {
		return visible[i];
	}
}
