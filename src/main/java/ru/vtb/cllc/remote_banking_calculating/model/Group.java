package ru.vtb.cllc.remote_banking_calculating.model;

import java.util.List;

public class Group {

	private final String name;
	private final List<String> indicatorNames;
	private final List<Group> groups;


	public Group(String name, List<String> indicatorNames, List<Group> groups) {
		this.name = name;
		this.indicatorNames = indicatorNames;
		this.groups = List.copyOf(groups);
	}
}
