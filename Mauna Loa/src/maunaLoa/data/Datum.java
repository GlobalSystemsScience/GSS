package maunaLoa.data;

import java.util.Date;

public final class Datum {

	protected final Date d;

	protected final double co2;

	public Datum(Date d, double co2) {
		this.d = d;
		this.co2 = co2;
	}

	public String toString() {
		return d.toString() + "," + co2;
	}
}
