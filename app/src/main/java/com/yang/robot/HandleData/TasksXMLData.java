package com.yang.robot.HandleData;

import android.content.Context;

import com.yang.robot.R;
import com.yang.robot.entity.Tasks;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiPredicate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TasksXMLData {
    // ivars
    private Context content;
    private Tasks[] data;

    public TasksXMLData(Context content) {
        this.content = content;
        //get data from xml
        //1. establish a inputstream and parse the document
        InputStream stream = content.getResources().openRawResource(R.raw.tasks);
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //2. slice the document to tags
        NodeList idList = document.getElementsByTagName("id");
        NodeList task_nameList = document.getElementsByTagName("task_name");
        NodeList createTimeList = document.getElementsByTagName("createTime");
        NodeList task_detailList = document.getElementsByTagName("task_detail");
        NodeList checkedList = document.getElementsByTagName("checked");


        //3. make the data
        data = new Tasks[idList.getLength()];
        for (int i = 0; i < data.length; i++) {
            //extract string info
            int id = Integer.parseInt(idList.item(i).getFirstChild().getNodeValue());
            String task_name = task_nameList.item(i).getFirstChild().getNodeValue();
            Long timestamp = Long.valueOf(createTimeList.item(i).getFirstChild().getNodeValue());
            Date date = new Date(timestamp);
//            Timestamp createTime = Timestamp.valueOf(createTimeList.item(i).getFirstChild().getNodeValue());
            String task_detail = task_detailList.item(i).getFirstChild().getNodeValue();

            Boolean checked = Boolean.valueOf(checkedList.item(i).getFirstChild().getNodeValue());
//            data[i]  = new Tasks(id,task_name,date,task_detail,checked,);
        }
    }

    public Tasks getTasks(int i){return data[i];}
    public int getLength(){return data.length;}



}
