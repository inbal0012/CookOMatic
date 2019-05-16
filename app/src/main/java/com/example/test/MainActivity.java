package com.example.test;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button contactUsBtn, orderBtn, searchBtn, recipesBtn;
    EditText name_input_ET;
    Dialog contactUsDialog;

    //REQUESTS
    final int CALL_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        orderBtn = findViewById(R.id.orderBtn);
        searchBtn = findViewById(R.id.searchBtn);
        recipesBtn = findViewById(R.id.recipesBtn);
        name_input_ET = findViewById(R.id.user_name_input);

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainStartActivity(OrderActivity.class);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainStartActivity(OrderActivity.class);
            }
        });

        recipesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainStartActivity(AddRecipeActivity.class);
            }
        });


        //Contact us Handler
        contactUsDialog = new Dialog(this);
        contactUsBtn = findViewById(R.id.contactUsBtn);
        contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openContactUsDialog();
            }
        });
    }

    private void MainStartActivity(Class<?> i_class) {
        if(true) { //TODO checkUserName()
            Intent intent = new Intent(MainActivity.this, i_class);
            //intent.putExtra("name", name_input_ET.getText().toString());
            startActivity(intent);
        }
    }


    public void openContactUsDialog() {

        contactUsDialog.setContentView(R.layout.custom_dialog_contact_us);
        ImageView close = contactUsDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsDialog.dismiss();
            }
        });

        //call Handler
        ImageButton call = contactUsDialog.findViewById(R.id.call_IB);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {

                    int hasCallPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
                    if (hasCallPermission == PackageManager.PERMISSION_GRANTED) {
                        callPhone();
                    } else { //PERMISSION_DENIED

                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST);
                    }
                } else {
                    callPhone();
                }
            }
        });

        //email Handler
        ImageButton emailBtn = contactUsDialog.findViewById(R.id.mail_IB);
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO String subject = getResources(R.string.subject);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Hello, \n I'm using Cook o Matric.\nI have a question:\n ");
                intent.putExtra(Intent.EXTRA_SUBJECT, "question from Cook o Matric app");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"inbal0012@gmail.com"});
                //intent.setPackage(getPackageName());
                intent.setType("text/html");

                startActivity(Intent.createChooser(intent, "Send email with..."));
            }
        });

        //whatsapp Handler
        ImageButton whatsappBtn = contactUsDialog.findViewById(R.id.whatsapp_IB);
        whatsappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "question from Cook o Matric app";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.putExtra("jid", "972547779246" + "@s.whatsapp.net");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);

            }
        });

        contactUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactUsDialog.show();
    }


    private void callPhone() {
        String number = "0526577224";  //TODO change
        if(isWorkingHours()) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        }
    }

    private boolean isWorkingHours() {
        Calendar cal = Calendar.getInstance();

        String message = "Please call during working hours,\nor text them at any time.\nWorking hours are 8-17, Sunday to Thursday";
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if(weekDay == Calendar.FRIDAY || weekDay == Calendar.SATURDAY)
        {
            Toast.makeText(this, "Enjoy your weekend." + message, Toast.LENGTH_LONG).show();
            return false;
        }
        int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);
        if(HOUR_OF_DAY < 8 || HOUR_OF_DAY > 17)
        {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


}
