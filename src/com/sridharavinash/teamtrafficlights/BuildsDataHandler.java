package com.sridharavinash.teamtrafficlights;


import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/** DataHandler for XML parsing */
class BuildsDataHandler extends DefaultHandler implements IDataInterface{
		private StringBuffer buffer = new StringBuffer();
		private ArrayList<TeamCityBuilds> TCBuilds = new ArrayList<TeamCityBuilds>();
		private TeamCityBuilds tcbuild;
		@Override
		public void startElement(String uri,
								 String localName,
								 String qName,
								 Attributes attrs) throws SAXException{
			buffer.setLength(0);
			if(localName.equals("build")){
				tcbuild = new TeamCityBuilds();
				tcbuild.buildId = attrs.getValue("id");
				tcbuild.buildCount = attrs.getValue("number");
				tcbuild.buildHref = attrs.getValue("href");
				tcbuild.startDate = attrs.getValue("startDate");
				tcbuild.status = attrs.getValue("status");
			}
			
			if(localName.equals("buildType")){
				tcbuild = new TeamCityBuilds();
				tcbuild.buildId = attrs.getValue("id");
				tcbuild.buildName = attrs.getValue("name");
			}
			
		}
		@Override
		public void characters(char[] ch, int start, int length)
		            throws SAXException {
			 buffer.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		            throws SAXException {
			if(localName.equals("build")){
				TCBuilds.add(tcbuild);
			}
			
			if(localName.equals("buildType")){
				TCBuilds.add(tcbuild);
			}
		}
		
		@Override
		public ArrayList<?> getData() {
			return TCBuilds;
		}
	}