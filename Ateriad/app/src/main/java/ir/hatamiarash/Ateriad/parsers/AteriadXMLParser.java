package ir.hatamiarash.Ateriad.parsers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ir.hatamiarash.Ateriad.model.Ateriad;

public class AteriadXMLParser {
    public static List<Ateriad> parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            Ateriad ateriad = null;
            List<Ateriad> ateriadList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("Employee")) {
                            inDataItemTag = true;
                            ateriad = new Ateriad();
                            ateriadList.add(ateriad);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Employee")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && ateriad != null) {
                            switch (currentTagName) {
                                case "id":
                                    ateriad.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "FirstName":
                                    ateriad.setFirstName(parser.getText());
                                    break;
                                case "LastName":
                                    ateriad.setLastName(parser.getText());
                                    break;
                                case "phone":
                                    ateriad.setphone(parser.getText());
                                    break;
                                case "email":
                                    ateriad.setemail(parser.getText());
                                    break;
                                case "job":
                                    ateriad.setjob(parser.getText());
                                    break;
                                case "photo":
                                    ateriad.setPhoto(parser.getText());
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return ateriadList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}