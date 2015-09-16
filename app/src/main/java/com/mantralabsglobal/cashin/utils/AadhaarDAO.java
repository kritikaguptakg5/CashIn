package com.mantralabsglobal.cashin.utils;

import android.util.Log;

import com.mantralabsglobal.cashin.service.AadhaarService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by pk on 6/20/2015.
 */
public class AadhaarDAO {

    public static final int FATHER_SPOUSE_NAME_FIRST_PART_REMOVAL = 4;

    public static AadhaarService.AadhaarDetail getAadhaarDetailFromXML(String xml)
    {
        xml = fixAadhaarXMLString(xml);
        XmlPullParserFactory xmlFactoryObject;
        AadhaarService.AadhaarDetail aadhaarDetail = new AadhaarService.AadhaarDetail();

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser aadhaarparser = xmlFactoryObject.newPullParser();
            aadhaarparser.setInput(new StringReader(xml));


            int event = aadhaarparser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=aadhaarparser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if(name != null && name.equals("PrintLetterBarcodeData")){
                            aadhaarDetail.setAadhaarNumber(aadhaarparser.getAttributeValue(null, "uid"));
                            aadhaarDetail.setName(aadhaarparser.getAttributeValue(null, "name"));

                            String yob = aadhaarparser.getAttributeValue(null, "yob");

                            Calendar c = Calendar.getInstance();
                            c.set(Integer.parseInt(yob), 1, 1);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            aadhaarDetail.setDob(sdf.format(c.getTime()));

                            String gender = aadhaarparser.getAttributeValue(null, "gender");
                            if(Objects.equals(gender, "M") || Objects.equals(gender, "MALE"))
                                gender = "Male";
                            else if (Objects.equals(gender, "F") || Objects.equals(gender, "FEMALE"))
                                gender = "Female";

                            aadhaarDetail.setGender(gender);
                            Log.d("AadhaarDAO", "Gender: " + gender);

                            aadhaarDetail.setAddress(getAttributeValue(aadhaarparser, "house") +
                                            getAttributeValue(aadhaarparser, "street") +
                                            getAttributeValue(aadhaarparser, "lm") +
                                            getAttributeValue(aadhaarparser, "po") +
                                            getAttributeValue(aadhaarparser, "dist") +
                                            getAttributeValue(aadhaarparser, "subdist") +
                                            getAttributeValue(aadhaarparser, "state") +
                                            getAttributeValue(aadhaarparser, "pc")
                            );

                            aadhaarDetail.setSonOf(getAttributeValue(aadhaarparser,"co").trim().substring(FATHER_SPOUSE_NAME_FIRST_PART_REMOVAL));
                            //aadhaarDetail.setLoc(aadhaarparser.getAttributeValue(null, "loc"));
                            //aadhaarDetail.setVtc(aadhaarparser.getAttributeValue(null, "vtc"));
                            //aadhaarDetail.setPostOffice(aadhaarparser.getAttributeValue(null, "po"));
                            //aadhaarDetail.setDistrict(aadhaarparser.getAttributeValue(null, "dist"));
                            //aadhaarDetail.setSubDistrict(aadhaarparser.getAttributeValue(null, "subdist"));
                            //aadhaarDetail.setState(aadhaarparser.getAttributeValue(null, "state"));
                           // aadhaarDetail.setPincode(aadhaarparser.getAttributeValue(null, "pc"));

                        }
                        break;
                }
                event = aadhaarparser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return aadhaarDetail;
    }

    private static String getAttributeValue(XmlPullParser aadhaarParser, String attributeName)
    {
        String value = aadhaarParser.getAttributeValue(null, attributeName);
        if(value== null)
            value = "";
        return value;
    }

    private static String fixAadhaarXMLString(String xml)
    {
        return XmlUtils.removeDeclaration(xml);
    }
}
