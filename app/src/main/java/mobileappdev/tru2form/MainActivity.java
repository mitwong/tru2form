package mobileappdev.tru2form;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.telephony.SmsManager;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;

public class MainActivity extends ActionBarActivity {

    ImageButton button;
    EditText editPhoneNum;
    EditText editSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates the autocomplete field for the recipients field
        final RecipientEditTextView contactsField =
                (RecipientEditTextView) findViewById(R.id.contactsField);
        contactsField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        contactsField.setAdapter(new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this));

        setContentView(R.layout.activity_main);

        button = (ImageButton) findViewById(R.id.sendTextButton);
        //editPhoneNum = (EditText) findViewById(R.id.);
        editSMS = (EditText) findViewById(R.id.editText);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // String phoneNo = editPhoneNum.getText().toString();
                String phoneNo = "5179158314";
                String sms = editSMS.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                    editSMS.setText("");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //MenuInflater inflater = getMenuInflater();
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Need to implement the actual replacement
    public void replaceFirstName(View view) {
        Context context = getApplicationContext();
        CharSequence text = "Auto Replacing First Name";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Need to implement the actual replacement
    public void replaceFullName(View view) {
        Context context = getApplicationContext();
        CharSequence text = "Auto Replacing Full Name";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Need to actual send the sms
    public void sendText(View view) {
        Context context = getApplicationContext();
        CharSequence text = "Text Sent!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
