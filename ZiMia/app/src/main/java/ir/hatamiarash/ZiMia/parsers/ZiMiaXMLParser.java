package ir.hatamiarash.ZiMia.parsers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ir.hatamiarash.ZiMia.model.FastFood;
import ir.hatamiarash.ZiMia.model.FastFood_Food;
import ir.hatamiarash.ZiMia.model.Market;
import ir.hatamiarash.ZiMia.model.Market_Product;
import ir.hatamiarash.ZiMia.model.Market_Product_Type;
import ir.hatamiarash.ZiMia.model.Resturan;
import ir.hatamiarash.ZiMia.model.Resturan_Food;

public class ZiMiaXMLParser {
    public static List<FastFood> FastFood_parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            FastFood fastfood = null;
            List<FastFood> fastfoodList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("FastFood")) {
                            inDataItemTag = true;
                            fastfood = new FastFood();
                            fastfoodList.add(fastfood);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("FastFood")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && fastfood != null) {
                            switch (currentTagName) {
                                case "id":
                                    fastfood.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "Name":
                                    fastfood.setName(parser.getText());
                                    break;
                                case "Picture":
                                    fastfood.setPicture(parser.getText());
                                    break;
                                case "OpenHour":
                                    fastfood.setOpenHour(Integer.parseInt(parser.getText()));
                                    break;
                                case "CloseHour":
                                    fastfood.setCloseHour(Integer.parseInt(parser.getText()));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return fastfoodList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Market> Market_parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            Market market = null;
            List<Market> marketList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("Market")) {
                            inDataItemTag = true;
                            market = new Market();
                            marketList.add(market);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Market")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && market != null) {
                            switch (currentTagName) {
                                case "id":
                                    market.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "Name":
                                    market.setName(parser.getText());
                                    break;
                                case "Picture":
                                    market.setPicture(parser.getText());
                                    break;
                                case "OpenHour":
                                    market.setOpenHour(Integer.parseInt(parser.getText()));
                                    break;
                                case "CloseHour":
                                    market.setCloseHour(Integer.parseInt(parser.getText()));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return marketList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Resturan> Resturan_parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            Resturan resturan = null;
            List<Resturan> resturanList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("Resturan")) {
                            inDataItemTag = true;
                            resturan = new Resturan();
                            resturanList.add(resturan);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Resturan")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && resturan != null) {
                            switch (currentTagName) {
                                case "id":
                                    resturan.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "Name":
                                    resturan.setName(parser.getText());
                                    break;
                                case "Picture":
                                    resturan.setPicture(parser.getText());
                                    break;
                                case "OpenHour":
                                    resturan.setOpenHour(Integer.parseInt(parser.getText()));
                                    break;
                                case "CloseHour":
                                    resturan.setCloseHour(Integer.parseInt(parser.getText()));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return resturanList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Resturan_Food> Resturan_Food_parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            Resturan_Food resturan_food = null;
            List<Resturan_Food> resturan_foodList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("Resturan_Food")) {
                            inDataItemTag = true;
                            resturan_food = new Resturan_Food();
                            resturan_foodList.add(resturan_food);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Resturan_Food")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && resturan_food != null) {
                            switch (currentTagName) {
                                case "id":
                                    resturan_food.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "Name":
                                    resturan_food.setName(parser.getText());
                                    break;
                                case "Picture":
                                    resturan_food.setPicture(parser.getText());
                                    break;
                                case "Price":
                                    resturan_food.setPrice(Integer.parseInt(parser.getText()));
                                    break;
                                case "Specification":
                                    resturan_food.setSpecification(parser.getText());
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return resturan_foodList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Market_Product> Market_Product_parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            Market_Product market_product = null;
            List<Market_Product> market_productList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("Market_Product")) {
                            inDataItemTag = true;
                            market_product = new Market_Product();
                            market_productList.add(market_product);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Market_Product")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && market_product != null) {
                            switch (currentTagName) {
                                case "id":
                                    market_product.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "Name":
                                    market_product.setName(parser.getText());
                                    break;
                                case "Picture":
                                    market_product.setPicture(parser.getText());
                                    break;
                                case "Price":
                                    market_product.setPrice(Integer.parseInt(parser.getText()));
                                    break;
                                case "Specification":
                                    market_product.setSpecification(parser.getText());
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return market_productList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Market_Product_Type> Market_Product_Type_parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            Market_Product_Type market_product_type = null;
            List<Market_Product_Type> market_product_typeList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("Product-Type")) {
                            inDataItemTag = true;
                            market_product_type = new Market_Product_Type();
                            market_product_typeList.add(market_product_type);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Product-Type")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && market_product_type != null) {
                            switch (currentTagName) {
                                case "id":
                                    market_product_type.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "Name":
                                    market_product_type.setName(parser.getText());
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return market_product_typeList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<FastFood_Food> FastFood_Food_parseFeed(String content) {
        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            FastFood_Food fastfood_food = null;
            List<FastFood_Food> fastfood_foodList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("FastFood_Food")) {
                            inDataItemTag = true;
                            fastfood_food = new FastFood_Food();
                            fastfood_foodList.add(fastfood_food);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("FastFood_Food")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (inDataItemTag && fastfood_food != null) {
                            switch (currentTagName) {
                                case "id":
                                    fastfood_food.setid(Integer.parseInt(parser.getText()));
                                    break;
                                case "Name":
                                    fastfood_food.setName(parser.getText());
                                    break;
                                case "Picture":
                                    fastfood_food.setPicture(parser.getText());
                                    break;
                                case "Price":
                                    fastfood_food.setPrice(Integer.parseInt(parser.getText()));
                                    break;
                                case "Specification":
                                    fastfood_food.setSpecification(parser.getText());
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return fastfood_foodList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}