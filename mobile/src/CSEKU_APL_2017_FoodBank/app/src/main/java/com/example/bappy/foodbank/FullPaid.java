package com.example.bappy.foodbank;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FullPaid extends AppCompatActivity {

    TextView txt,txt2;
    String foodorder,datet,name,role,res;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ListView listView;
    StaffFoodAdapter staffFoodAdapter;

    int price2=0;
    String price3;

    private TextView startDateDisplay;
    private TextView endDateDisplay;
    private Button startPickDate;
    private Button endPickDate;
    private Calendar startDate;
    private Calendar endDate;
    static final int DATE_DIALOG_ID = 0;

    private TextView activeDateDisplay;
    private Calendar activeDate;

    String datetime="n",datetime2="n";

    ArrayList<StaffFood> addstaffFood;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_paid_layout);
        sharedPreferences=getSharedPreferences(getString(R.string.PREF_FILE), 0);
        editor=sharedPreferences.edit();

        name=getIntent().getExtras().getString("username");
        role=getIntent().getExtras().getString("role");
        datet=getIntent().getExtras().getString("datet");
        res=getIntent().getExtras().getString("res");
        foodorder=getIntent().getExtras().getString("order_details");

        txt=(TextView)findViewById(R.id.txtviw);
        txt2=(TextView)findViewById(R.id.paidprice);
        txt.setText(name+"("+role+")\n"+datet);
        listView=(ListView)findViewById(R.id.foodstafflist);
        staffFoodAdapter=new StaffFoodAdapter(this,R.layout.staff_food_layout);
        listView.setAdapter(staffFoodAdapter);
        try{
        jsonObject=new JSONObject(foodorder);
        jsonArray=jsonObject.getJSONArray("Server_response");

        int count=0;
        String clientname,foodname,quantity,orderdate,ispaid,phone,deliverydate,isdelivery,price,orderfrom,staff;
        while(count<jsonArray.length())
        {
            JSONObject jo=jsonArray.getJSONObject(count);
            clientname=jo.getString("clientname");
            foodname=jo.getString("foodname");
            quantity=jo.getString("quantity");
            orderdate=jo.getString("orderdate");
            ispaid=jo.getString("ispaid");
            phone=jo.getString("phonenumber");
            deliverydate=jo.getString("deliverydate");
            isdelivery=jo.getString("isdelivery");
            price=jo.getString("price");
            orderfrom=jo.getString("orderplace");
            staff=jo.getString("staffrole");
            int price3=Integer.parseInt(price);
            price3=price3*Integer.parseInt(quantity);
            price2=price2+price3;

            StaffFood staffFood=new StaffFood(clientname,foodname,quantity,orderdate,ispaid,phone,deliverydate,isdelivery,price,orderfrom,staff);
            staffFoodAdapter.add(staffFood);
            count++;
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }

        price3=Integer.toString(price2);
        txt2.setText(price3);

        /*  capture our View elements for the start date function   */
        startDateDisplay = (TextView) findViewById(R.id.startdateview);
        startPickDate = (Button) findViewById(R.id.startdate);

        startDate = Calendar.getInstance();

        /* add a click listener to the button   */
        startPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datetime=startDate.get(Calendar.YEAR)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+startDate.get(Calendar.DAY_OF_MONTH);
                Toast.makeText(FullPaid.this, datetime, Toast.LENGTH_SHORT).show();
                showDateDialog(startDateDisplay, startDate);
            }
        });

     /* capture our View elements for the end date function */
        endDateDisplay = (TextView) findViewById(R.id.enddateview);
        endPickDate = (Button) findViewById(R.id.enddate);

        /* get the current date */
        endDate = Calendar.getInstance();

        /* add a click listener to the button   */
        endPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datetime2=endDate.get(Calendar.YEAR)+"-"+(endDate.get(Calendar.MONTH)+1)+"-"+endDate.get(Calendar.DAY_OF_MONTH);
                Toast.makeText(FullPaid.this,datetime2 , Toast.LENGTH_SHORT).show();
                showDateDialog(endDateDisplay, endDate);
            }
        });

        /* display the current date (this method is below)  */

        updateDisplay(startDateDisplay, startDate);
        updateDisplay(endDateDisplay, endDate);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_staff_admin,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                editor.clear();
                editor.commit();
                startActivity(new Intent(this, staff_login_resistor.class));
                finish();
                return true;
            case R.id.my_profile:
                startActivity(new Intent(this, ShowProfile.class));
                return true;
            case R.id.new_restaurant:
                startActivity(new Intent(this, CreateNewRestaurant.class));
                return true;
            case R.id.edit_profile:
                Intent intent=new Intent(this, EditChangeProfile.class);
                intent.putExtra("op_type","Edit");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateDisplay(TextView dateDisplay, Calendar date) {
        dateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(date.get(Calendar.YEAR)).append("-")
                        .append(date.get(Calendar.MONTH) + 1).append("-")
                        .append(date.get(Calendar.DAY_OF_MONTH)));
    }

    public void showDateDialog(TextView dateDisplay, Calendar date) {
        activeDateDisplay = dateDisplay;
        activeDate = date;
        showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override

        public void onDateSet(android.widget.DatePicker view, int year,int monthOfYear, int dayOfMonth)
        {
            activeDate.set(Calendar.YEAR, year);
            activeDate.set(Calendar.MONTH, monthOfYear);
            activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDisplay(activeDateDisplay, activeDate);
            unregisterDateDisplay();
        }
    };

    private void unregisterDateDisplay() {
        activeDateDisplay = null;
        activeDate = null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }


    public class StaffFoodAdapter extends ArrayAdapter {
        List list = new ArrayList();
        Context ct;

        public StaffFoodAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
            ct = context;
        }

        @Override
        public void add(@Nullable Object object) {
            super.add(object);
            list.add(object);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View stafffoodview;
            stafffoodview = convertView;
            final StaffFoodHolder staffFoodHolder;
            if (stafffoodview == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                stafffoodview = layoutInflater.inflate(R.layout.staff_food_layout, parent, false);
                staffFoodHolder = new StaffFoodHolder();
                staffFoodHolder.clientname = (TextView) stafffoodview.findViewById(R.id.sname);
                staffFoodHolder.foodname = (TextView) stafffoodview.findViewById(R.id.sfood);
                staffFoodHolder.quantity = (TextView) stafffoodview.findViewById(R.id.squantity);
                staffFoodHolder.orderdate = (TextView) stafffoodview.findViewById(R.id.sorderdate);
                staffFoodHolder.ispaid = (TextView) stafffoodview.findViewById(R.id.spaid);
                staffFoodHolder.phone = (TextView) stafffoodview.findViewById(R.id.snumber);
                staffFoodHolder.deliverydate = (TextView) stafffoodview.findViewById(R.id.sdeliverydate);
                staffFoodHolder.isdelivery = (TextView) stafffoodview.findViewById(R.id.sdeliverytype);
                staffFoodHolder.price = (TextView) stafffoodview.findViewById(R.id.sprice);
                staffFoodHolder.orderplace = (TextView) stafffoodview.findViewById(R.id.sorderplace);
                staffFoodHolder.staffrole = (TextView) stafffoodview.findViewById(R.id.staff);
                stafffoodview.setTag(staffFoodHolder);
            } else {
                staffFoodHolder = (StaffFoodHolder) stafffoodview.getTag();
            }

            final StaffFood staffFood = (StaffFood) this.getItem(position);
            staffFoodHolder.clientname.setText(staffFood.getClientname());
            staffFoodHolder.foodname.setText(staffFood.getFoodname());
            staffFoodHolder.quantity.setText(staffFood.getQuantity());
            staffFoodHolder.orderdate.setText(staffFood.getOrderdate());
            staffFoodHolder.ispaid.setText(staffFood.getIspaid());
            staffFoodHolder.phone.setText(staffFood.getPhone());
            staffFoodHolder.deliverydate.setText(staffFood.getDeliverydate());
            staffFoodHolder.isdelivery.setText(staffFood.getIsdelivery());
            staffFoodHolder.price.setText(staffFood.getPrice());
            staffFoodHolder.orderplace.setText(staffFood.getOrderplace());
            staffFoodHolder.staffrole.setText(staffFood.getStaff());
            stafffoodview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Clicked on " + staffFood.getClientname(), Toast.LENGTH_SHORT).show();
                }
            });
            return stafffoodview;
        }

        class StaffFoodHolder {
            TextView clientname, foodname, quantity, orderdate, ispaid, phone, deliverydate, isdelivery, price, orderplace,staffrole;
        }
    }

    public class StaffFood {

        String clientname,foodname,quantity,orderdate,ispaid,phone,deliverydate,isdelivery,price,orderplace,staff;

        public StaffFood(String clientname, String foodname, String quantity, String orderdate, String ispaid, String phone, String deliverydate, String isdelivery, String price,String orderplace,String dateti) {
            this.clientname = clientname;
            this.foodname = foodname;
            this.quantity = quantity;
            this.orderdate = orderdate;
            this.ispaid = ispaid;
            this.phone = phone;
            this.deliverydate = deliverydate;
            this.isdelivery = isdelivery;
            this.price = price;
            this.orderplace=orderplace;
            this.staff=dateti;
        }

        public String getClientname() {
            return clientname;
        }

        public void setClientname(String clientname) {
            this.clientname = clientname;
        }

        public String getFoodname() {
            return foodname;
        }

        public void setFoodname(String foodname) {
            this.foodname = foodname;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getOrderdate() {
            return orderdate;
        }

        public void setOrderdate(String orderdate) {
            this.orderdate = orderdate;
        }

        public String getIspaid() {
            return ispaid;
        }

        public void setIspaid(String ispaid) {
            this.ispaid = ispaid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDeliverydate() {
            return deliverydate;
        }

        public void setDeliverydate(String deliverydate) {
            this.deliverydate = deliverydate;
        }

        public String getIsdelivery() {
            return isdelivery;
        }

        public void setIsdelivery(String isdelivery) {
            this.isdelivery = isdelivery;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getOrderplace() {
            return orderplace;
        }

        public void setOrderplace(String orderplace) {
            this.orderplace = orderplace;
        }

        public String getStaff() {
            return staff;
        }

        public void setStaff(String staff) {
            this.staff = staff;
        }
    }

    public void getpriceindate(View view){
        datetime=startDate.get(Calendar.YEAR)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+startDate.get(Calendar.DAY_OF_MONTH);
        datetime2=endDate.get(Calendar.YEAR)+"-"+(endDate.get(Calendar.MONTH)+1)+"-"+endDate.get(Calendar.DAY_OF_MONTH);
        if(datetime.equals(datetime2)){
            Toast.makeText(this, "Please Select Date Range", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, datetime + datetime2, Toast.LENGTH_SHORT).show();
            new BackgroundtaskOrderlist().execute(datetime, datetime2);
        }
    }


    public class BackgroundtaskOrderlist extends AsyncTask<String,Void,String>
    {

        String json_url;
        String JSON_STRING;

        @Override
        protected void onPreExecute() {
            json_url="http://"+getString(R.string.ip_address)+"/FoodBank/FullPaidRange.php";
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String date1=params[0];
                String date2=params[1];
                URL url=new URL(json_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedwritter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String postdata = URLEncoder.encode("date1", "UTF-8") + "=" + URLEncoder.encode(date1, "UTF-8") + "&" +
                        URLEncoder.encode("date2", "UTF-8") + "=" + URLEncoder.encode(date2 , "UTF-8")+ "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(res, "UTF-8");
                bufferedwritter.write(postdata);
                bufferedwritter.flush();
                bufferedwritter.close();
                outputstream.close();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder=new StringBuilder();
                while((JSON_STRING=bufferedReader.readLine())!=null){
                    stringBuilder.append(JSON_STRING+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(FullPaid.this, result, Toast.LENGTH_SHORT).show();
            price2=0;
            listView=(ListView)findViewById(R.id.foodstafflist);
            staffFoodAdapter=new StaffFoodAdapter(FullPaid.this,R.layout.staff_food_layout);
            listView.setAdapter(staffFoodAdapter);
            try {
                jsonObject=new JSONObject(result);
                jsonArray=jsonObject.getJSONArray("Server_response");

                int count=0;
                String clientname,foodname,quantity,orderdate,ispaid,phone,deliverydate,isdelivery,price,orderfrom,staff;
                while(count<jsonArray.length())
                {
                    JSONObject jo=jsonArray.getJSONObject(count);
                    clientname=jo.getString("clientname");
                    foodname=jo.getString("foodname");
                    quantity=jo.getString("quantity");
                    orderdate=jo.getString("orderdate");
                    ispaid=jo.getString("ispaid");
                    phone=jo.getString("phonenumber");
                    deliverydate=jo.getString("deliverydate");
                    isdelivery=jo.getString("isdelivery");
                    price=jo.getString("price");
                    orderfrom=jo.getString("orderplace");
                    staff=jo.getString("staffrole");
                    int price3=Integer.parseInt(price);
                    price3=price3*Integer.parseInt(quantity);
                    price2=price2+price3;

                    StaffFood staffFood=new StaffFood(clientname,foodname,quantity,orderdate,ispaid,phone,deliverydate,isdelivery,price,orderfrom,staff);
                    staffFoodAdapter.add(staffFood);
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            price3=Integer.toString(price2);
            txt2.setText(price3);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(FullPaid.this,Adminstaff.class);
        intent.putExtra("username", name);
        intent.putExtra("resname", res);
        intent.putExtra("role", role);
        startActivity(intent);
        finish();
    }

}