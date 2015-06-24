package com.mantralabsglobal.cashin.dao;

import com.mantralabsglobal.cashin.businessobjects.AadharDetail;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by pk on 6/20/2015.
 */
public class AadharDAO {

    public static AadharDetail getAadharDetailFromXML(String xml)
    {
        XmlPullParserFactory xmlFactoryObject = null;
        AadharDetail aadharDetail = new AadharDetail();

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser aadharparser = xmlFactoryObject.newPullParser();
            aadharparser.setInput(new StringReader(xml));


            int event = aadharparser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=aadharparser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("PrintLetterBarcodeData")){
                            aadharDetail.setUid(aadharparser.getAttributeValue(null, "uid"));
                            aadharDetail.setName(aadharparser.getAttributeValue(null, "name"));
                            aadharDetail.setGender(aadharparser.getAttributeValue(null, "gender"));
                            aadharDetail.setYearOfBirth(aadharparser.getAttributeValue(null, "yob"));
                            aadharDetail.setHouse(aadharparser.getAttributeValue(null, "house"));
                            aadharDetail.setStreet(aadharparser.getAttributeValue(null, "street"));
                            aadharDetail.setLandmark(aadharparser.getAttributeValue(null, "lm"));
                            aadharDetail.setLoc(aadharparser.getAttributeValue(null, "loc"));
                            aadharDetail.setVtc(aadharparser.getAttributeValue(null, "vtc"));
                            aadharDetail.setPostOffice(aadharparser.getAttributeValue(null, "po"));
                            aadharDetail.setDistrict(aadharparser.getAttributeValue(null, "dist"));
                            aadharDetail.setSubDistrict(aadharparser.getAttributeValue(null, "subdist"));
                            aadharDetail.setState(aadharparser.getAttributeValue(null, "state"));
                            aadharDetail.setPincode(aadharparser.getAttributeValue(null, "pc"));

                        }
                        break;
                }
                event = aadharparser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aadharDetail;
    }
}