package ru.vtb.cllc.remote_banking_calculating.model;

import java.util.List;

public class Group {

	private final String name;
	private final List<Group> groups;


	public Group(String name, List<Group> groups) {
		this.name = name;
		this.groups = List.copyOf(groups);
	}
}
