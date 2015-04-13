package mobileappdev.tru2form;

import android.app.LauncherActivity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
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
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectGroup extends ActionBarActivity {

    ImageButton button;
    EditText editPhoneNum;
    EditText newGroupName;
    ArrayAdapter<String> adapter;
    ArrayList arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        // Ensures portrait modes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Creates the autocomplete field for the recipients field
        final RecipientEditTextView contactsField =
                (RecipientEditTextView) findViewById(R.id.groupContactsField);
        contactsField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        contactsField.setAdapter(new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this));

        ListView sampleGroups = (ListView) findViewById(R.id.listOfGroups);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        sampleGroups.setAdapter(adapter);

        sampleGroups.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long x){
                String item = (String)adapter.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(),Compose.class );
                intent.putExtra("group_name", item);
                startActivity(intent);
            }
        });
        System.out.println("About to get prefs");
        SharedPreferences groupList = getSharedPreferences("groupList", Context.MODE_PRIVATE);
        Map<String,?> keys = groupList.getAll();
        System.out.println("Got prefs in map");
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            arrayList.add(entry.getKey());
        }
        Collections.sort(arrayList);
        adapter.addAll(arrayList);
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

    public void processGroup(View view) {
        SharedPreferences groupList = getSharedPreferences("groupList", Context.MODE_PRIVATE);
        SharedPreferences.Editor groupListEditor = groupList.edit();
        newGroupName = (EditText) findViewById(R.id.groupNameField);
        String userGroupName = newGroupName.getText().toString();
        if (groupList.contains(userGroupName)) {
            Toast.makeText(getBaseContext(), "Please Provide Unique Group Name",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            groupListEditor.putString(userGroupName, userGroupName);
            groupListEditor.commit();
        }
        SharedPreferences contactList = getSharedPreferences(userGroupName, Context.MODE_PRIVATE);
        SharedPreferences.Editor contactListEditor = contactList.edit();
        final RecipientEditTextView contactsField =
                (RecipientEditTextView) findViewById(R.id.groupContactsField);
        DrawableRecipientChip[] chips = contactsField.getRecipients();

        for (DrawableRecipientChip chip : chips) {
            // Due to differences in CharSequence and String, the correct conversion follows
            CharSequence value = chip.getValue();
            StringBuilder builder = new StringBuilder(value.length());
            builder.append(value);
            String phoneNumRaw = builder.toString();
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                // "US" signifies US locale
                Phonenumber.PhoneNumber usNumberProto = phoneUtil.parse(phoneNumRaw, "US");
                // Get number and format it to string
                String phoneNumClean = Long.toString(usNumberProto.getNationalNumber());
                String fullName = chip.getEntry().getDisplayName();
                contactListEditor.putString(phoneNumClean, fullName);
            } catch (Exception e) {
                System.err.println("Exception was thrown: " + e.toString());
                // e.printStackTrace();
            }
        }
        contactListEditor.commit();
        arrayList.add(userGroupName);
        Collections.sort(arrayList);
        adapter.clear();
        adapter.addAll(arrayList);
        adapter.notifyDataSetChanged();

        return;
    }

    public void clickGroup (View view){

    }
};
/*public class SelectGroup extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        ListView sampleGroups = (ListView) findViewById(R.id.listOfGroups);
        ArrayList arrayList = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        sampleGroups.setAdapter(adapter);

        for (int i = 0; i < 20; ++i) {
            adapter.add("Testing");
        }
    }
*/
