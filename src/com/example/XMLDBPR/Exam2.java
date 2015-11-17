package com.example.XMLDBPR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLDataException;

public class Exam2 extends Activity {
    String name = "";
    String minit = "";
    String surname = "";
    String ssn = "";
    String bdate = "";
    String address = "";
    String sex = "";
    String salary = "";
    String superssn = "";
    String dno = "";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }


    public void change (View v){
        SQLiteDatabase db = this.openOrCreateDatabase("exam2",MODE_PRIVATE,null);

        EditText where = (EditText)findViewById(R.id.Where);
        EditText from = (EditText)findViewById(R.id.From);
        EditText to = (EditText)findViewById(R.id.To);
        EditText what = (EditText)findViewById(R.id.What);

        String condition = where.getText().toString();
        String element = from.getText().toString();
        String newVal = to.getText().toString();
        String tabletochange = what.getText().toString();
        
        String sqlstatement = "update employee set "+tabletochange+" = '"+newVal+"' where "+condition+" = '"+element+"'";

        db.beginTransaction();
        try{
            db.execSQL(sqlstatement);
            db.setTransactionSuccessful();
        }catch (SQLException e){

        }finally{
            db.endTransaction();
        }


    }


    public void salary(View v){
        SQLiteDatabase db = this.openOrCreateDatabase("exam2", MODE_PRIVATE, null);
        TextView salary = (TextView)findViewById(R.id.salarytxt);

        db.beginTransaction();
        try{
           Cursor cur =  db.rawQuery("select * from employee ORDER BY salary DESC", null);
            cur.moveToFirst();
//            while(cur.getColumnName(0).equalsIgnoreCase("name")){
//                cur.moveToNext();
//            }
            String highestSalary  = cur.getString(1);
            salary.setText("Highest Salary: " + highestSalary);


            SharedPreferences myprefs = getSharedPreferences("my_preferences", Activity.MODE_PRIVATE);

            SharedPreferences.Editor edit = myprefs.edit();
            edit.putString("Highest Salary", highestSalary);

            edit.commit();


            db.setTransactionSuccessful();
        }catch (SQLException e){
            salary.setText("nope");
        }finally {
            db.endTransaction();
        }







    }



    public void button1 (View v) throws IOException, XmlPullParserException {
        SQLiteDatabase db = this.openOrCreateDatabase("exam2",MODE_PRIVATE,null);

        db.beginTransaction();
        try {
            db.execSQL("drop table if exists employee;");
            db.execSQL("create table employee (recid integer PRIMARY KEY autoincrement ,name text,minit text, surname text,ssn text, bdate text, address text,sex text,salary int, superssn text, dno text);");
          //  db.execSQL("insert into employee (name ,minit, surname ,ssn , bdate , address ,sex ,salary , superssn , dno ) values ('Test','Test1','Test2','Test3','Test4','Test5','test6','test7','test8','test9');");

            db.setTransactionSuccessful();
        } catch(SQLiteException e) {

        }finally{
            db.endTransaction();
        }
        Integer xmlResFile = R.xml.employee;

        String inner = "";
        int inout = 0;
        String nodeName  = "";

        XmlPullParser parser = getResources().getXml(xmlResFile);

        int eventType = -1;
        while(eventType != XmlPullParser.END_DOCUMENT){
            eventType = parser.next();

            if(eventType == XmlPullParser.START_DOCUMENT){
                //  txt1.append("\nStart_Document");
            }
            else if (eventType == XmlPullParser.END_DOCUMENT){
                //txt1.append("\nEnd_Document");
            }
            else if (eventType == XmlPullParser.START_TAG){
                inout++;
                nodeName = parser.getName();
                if(inout == 2);
                if(inout == 3)
                    inner = nodeName;
                getAttributes(parser);
            }
            else if(eventType == XmlPullParser.END_TAG){
                if(inout == 2){
                    String query = CreateQuery();
                    db.beginTransaction();
                    try{
                        db.execSQL(query);
                        db.setTransactionSuccessful();
                    }catch (SQLException e){
                    }finally{
                        db.endTransaction();
                    }

                    ClearStrings();
                }
                inout--;
            }
            else if(eventType == XmlPullParser.TEXT){
                if(inout == 2){
                }
                if(inout == 3){
                    if (inner.equalsIgnoreCase("FNAME"))
                        name = parser.getText();
                    else if (inner.equalsIgnoreCase("MINIT"))
                        minit = parser.getText();
                    else if (inner.equalsIgnoreCase("LNAME"))
                        surname = parser.getText();
                    else if (inner.equalsIgnoreCase("SSN")) {
                        ssn = parser.getText();
                    }
                    else if (inner.equalsIgnoreCase("BDATE")) {
                        bdate = parser.getText();
                    }
                    else if (inner.equalsIgnoreCase("ADDRESS")) {
                        address = parser.getText();
                    }
                    else if (inner.equalsIgnoreCase("SEX")) {
                        sex = parser.getText();
                    }
                    else if (inner.equalsIgnoreCase("SALARY")) {
                        salary = parser.getText();
                    }
                    else if (inner.equalsIgnoreCase("SUPERSSN")) {
                        superssn = parser.getText();
                    }
                    else if (inner.equalsIgnoreCase("DNO")) {
                        dno = parser.getText();
                    }
                }
            }

        }//While

        Context context = getApplicationContext();
        CharSequence text = "Database Created and populated";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }//On click listener

    public void button2 (View v){
        String Dbname = "exam2";
        new backgroundAsyncTask().execute(Dbname);
    }

    private class backgroundAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = new ProgressDialog(Exam2.this);

        @Override
        protected void onPostExecute(String result) {
            TextView txt3 = (TextView)findViewById(R.id.textView);
            super.onPostExecute(result);
            dialog.dismiss();
            txt3.setText(result.toString());
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            String Dbname = params[0];
            return GetDatabasePrintout(Dbname);
        }// doInBackground

    }// backgroundAsyncTask

    private String GetDatabasePrintout(String name){
        SQLiteDatabase db = this.openOrCreateDatabase(name,MODE_PRIVATE,null);

        String show = "select * from employee";

        Cursor C1 = db.rawQuery(show, null);
        String ret = showCursor(C1);

        return ret;
    }


    public String CreateQuery(){

        String Insert_data = "insert into employee(name ,minit, surname ,ssn , bdate , address ,sex ,salary , superssn , dno ) values ('"+name+"' , '"+minit+"' , '"+surname+"' , '"+ssn+"' , '"+bdate+"' , '"+address+"' , '"+sex+"' , '"+salary+"' , '"+superssn+"' , '"+dno+"' );";
        return Insert_data;
    }

    private void ClearStrings(){
        name = "";
        minit = "";
        surname = "";
        ssn = "";
        bdate = "";
        address = "";
        sex = "";
        salary = "";
        superssn = "";
        dno = "";

    }


    private void getAttributes(XmlPullParser parser){
        TextView txt1 = (TextView)findViewById(R.id.textView);
        String name = parser.getName();

        if(name != null){
            int size = parser.getAttributeCount();
            for(int i = 0; i < size; i++){
                String attributesname = parser.getAttributeName(i);
                String attributesvalue = parser.getAttributeValue(i);
                txt1.append("\n Attrib <Key, Value>"+ attributesname+", "+ attributesvalue);
            }


        }

    }

    private String showCursor( Cursor cursor) {
        // show SCHEMA (column names & types)
        cursor.moveToPosition(-1); //reset cursor's top
        String cursorData = "\nCursor: [";

        try {
            // get column names
            String[] colName = cursor.getColumnNames();
            for(int i=0; i<colName.length; i++){
                String dataType = getColumnType(cursor, i);
                cursorData += colName[i] + dataType;
                if (i<colName.length-1){
                    cursorData+= ", ";
                } }
        } catch (Exception e)
        { Log.e("<<SCHEMA>>", e.getMessage());
        }
        cursorData += "]";
// now get the rows
        cursor.moveToPosition(-1); //reset cursor's top
        while (cursor.moveToNext()) {
            String cursorRow = "\n[";
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                cursorRow += cursor.getString(i);
                if (i<cursor.getColumnCount()-1)
                    cursorRow += ", ";
            }
            cursorData += cursorRow + "]";
        }
        return cursorData + "\n";
    }


    private String getColumnType(Cursor cursor, int i) {
        try {
            //peek at a row holding valid data
            cursor.moveToFirst();
            int result = cursor.getType(i);
            String[] types = {":NULL", ":INT", ":FLOAT", ":STR", ":BLOB", ":UNK" };
            //backtrack - reset cursor's top
            cursor.moveToPosition(-1);
            return types[result]; } catch (Exception e) { return " "; }
    }


}
