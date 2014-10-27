package core;

import java.io.File;

public class FileDescriptor {
	private String name;
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FileDescriptor(File file) {
		path = file.getPath();
		name = file.getName();
	}
}
