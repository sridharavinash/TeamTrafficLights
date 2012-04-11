package com.sridharavinash.teamtrafficlights;


import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/** DataHandler for XML parsing */
class TCDataHandler extends DefaultHandler implements IDataInterface{
		private StringBuffer buffer = new StringBuffer();
		private ArrayList<TeamCityProject> TCprojects = new ArrayList<TeamCityProject>();
		private TeamCityProject tcproject;
		@Override
		public void startElement(String uri,
								 String localName,
								 String qName,
								 Attributes attrs) throws SAXException{
			buffer.setLength(0);
			if(localName.equals("project")){
				tcproject = new TeamCityProject();
				tcproject.projectName = attrs.getValue("name");
				tcproject.projectId = attrs.getValue("id");
				tcproject.projectHref = attrs.getValue("href");

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
			if(localName.equals("project")){
				TCprojects.add(tcproject);
			}
		}
		@Override
		public ArrayList<?> getData() {
			return TCprojects;
		}
	}