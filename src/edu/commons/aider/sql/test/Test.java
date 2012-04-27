package edu.commons.aider.sql.test;

import edu.commons.aider.sql.DBAider;
import edu.commons.aider.sql.DataSet;
import edu.commons.aider.sql.SettingsLoader;

public class Test {
	public static void main(String[] args) {
		SettingsLoader settings = new SettingsLoader("app.settings");
		DBAider.init(settings);
		DataSet set = DBAider.read("select * from camera");
		System.out.println(set);
	}
}
