package com.fic.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Item {
	
	private String title;
	private String description;
	private String defaultThumbnailUrl;
	private List<String> mediaList;
	
	public Item(){
		this.setMediaList(new ArrayList<String>());
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDefaultThumbnailUrl() {
		return defaultThumbnailUrl;
	}
	public void setDefaultThumbnailUrl(String defaultThumbnailUrl) {
		this.defaultThumbnailUrl = defaultThumbnailUrl;
	}

	public List<String> getMediaList() {
		return mediaList;
	}

	public void setMediaList(List<String> mediaList) {
		this.mediaList = mediaList;
	}


}
