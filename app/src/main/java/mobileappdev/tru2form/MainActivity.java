package mobileappdev.tru2form;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class MainActivity extends ActionBarActivity {

    ImageButton button;
    EditText editPhoneNum;
    EditText editSMS;

    final String firstNamePlaceholder = "<<<first_name>>>";
    final String fullNamePlaceholder = "<<<full_name>>>";

    final String firstNameChipText = "First Name";
    final String fullNameChipText = "Full Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates the autocomplete field for the recipients field
        final RecipientEditTextView contactsField =
                (RecipientEditTextView) findViewById(R.id.contactsField);
        contactsField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        contactsField.setAdapter(new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this));
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

        EditText message = (EditText) findViewById(R.id.editText);

        // For non-chip based edits
//        int start = Math.max(message.getSelectionStart(), 0);
//        int end = Math.max(message.getSelectionEnd(), 0);
//        message.getText().replace(Math.min(start, end), Math.max(start, end),
//                firstNamePlaceholder, 0, firstNamePlaceholder.length());

        createChip(message, firstNamePlaceholder, firstNameChipText);

        Context context = getApplicationContext();
        CharSequence text = "Auto Replace First Name";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Need to implement the actual replacement
    public void replaceFullName(View view) {
        EditText message = (EditText) findViewById(R.id.editText);

        // For non-chip based edits
//        int start = Math.max(message.getSelectionStart(), 0);
//        int end = Math.max(message.getSelectionEnd(), 0);
//        message.getText().replace(Math.min(start, end), Math.max(start, end),
//                fullNamePlaceholder, 0, fullNamePlaceholder.length());

        createChip(message, fullNamePlaceholder, fullNameChipText);

        Context context = getApplicationContext();
        CharSequence text = "Auto Replace Full Name";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Implemented in onCreate method. May refactor it to out here if time permitting.
    public void sendText(View view) {
        // Retrieve the text message
        editSMS = (EditText) findViewById(R.id.editText);
        String rawSms = editSMS.getText().toString();

        // Get the values of the contacts
        final RecipientEditTextView contactsField =
                (RecipientEditTextView) findViewById(R.id.contactsField);
        DrawableRecipientChip[] chips = contactsField.getRecipients();

        for (DrawableRecipientChip chip : chips) {
            // Due to differences in CharSequence and String, the correct conversion follows
            CharSequence value = chip.getValue();
            StringBuilder builder = new StringBuilder(value.length());
            builder.append(value);
            String phoneNumRaw = builder.toString();

            // If this evaluates to true, then there is not contact information for this person
            if (chip.getContactId() == -1) {
                continue;
            }

//            System.out.println(chip.toString());
//            System.out.println(chip.getEntry().getContactId());
//            System.out.println(chip.getContactId());

            // Use Google's libphonenumber to verify if number is correct and to normalize format
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                // "US" signifies US locale
                Phonenumber.PhoneNumber usNumberProto = phoneUtil.parse(phoneNumRaw, "US");

                // Verify the number format is correct
                // phoneUtil.parse may catch all errors already, also may be unnecessary
                boolean isPossible = phoneUtil.isPossibleNumber(usNumberProto);
                if (!isPossible) {
                    System.err.println("Provided number: " + phoneNumRaw + " is not a possible number");
                    continue;
                }

                boolean isValid = phoneUtil.isValidNumber(usNumberProto);
                if (!isValid) {
                    System.err.println("Provided number: " + phoneNumRaw + " is not valid");
                    continue;
                }

                // Get number and format it to string
                String phoneNumClean = Long.toString(usNumberProto.getNationalNumber());

                // Do the replacements for first/full names and send the message
                String replacedSms = doReplacement(rawSms, chip);

                SmsManager sms = SmsManager.getDefault();
                List<String> messages = sms.divideMessage(replacedSms);
                for (String single : messages) {
                    sendSMS(phoneNumClean, single);
                }


            } catch (NumberParseException e) {
                // The number provided was not a phone number so continue iterating
                System.err.println("NumberParseException was thrown: " + e.toString());
            } catch (Exception e) {
                System.err.println("Exception was thrown: " + e.toString());
                // e.printStackTrace();
            }
        }
        // Reset the text message field
        editSMS.setText("");
        // Reset the contacts field
        contactsField.setText("");
    }

    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        // Toast for each message made UI cluttered
//                        Toast.makeText(getBaseContext(), "SMS sent",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                // Otherwise you leak the listener objects
                unregisterReceiver(this);
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        // Only appears on real devices, clutters up UI
//                        Toast.makeText(getBaseContext(), "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private String doReplacement(String original, DrawableRecipientChip chip) {
        // Retrieve the full name associated with the chip
        String fullName = chip.getEntry().getDisplayName();
        Integer firstSpacePos = fullName.indexOf(' ');

        // Retrieve the first name associated with the chip
        String firstName = "";
        if (firstSpacePos == -1) {
            firstName = fullName;
        } else {
            firstName = fullName.substring(0, firstSpacePos);
        }

        // Replace any instances of <<first>> or <<full>>
        // Note, order is full then first because of case where the first name is <<first>>
        original = original.replace(fullNamePlaceholder, fullName);
        original = original.replace(firstNamePlaceholder, firstName);
        return original;
    }

    private void createChip(EditText editText, String placeholder, String chipText) {
        // get position to replace
        int start = Math.max(editText.getSelectionStart(), 0);
        int end = Math.max(editText.getSelectionEnd(), 0);
        editText.getText().replace(Math.min(start, end), Math.max(start, end),
                placeholder, 0, placeholder.length());

        SpannableStringBuilder ssb = new SpannableStringBuilder(editText.getText());

        float size = editText.getTextSize();
        Bitmap viewBmp = textAsBitmap(chipText, size);

        // create bitmap drawable for imagespan
        BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
        bmpDrawable.setBounds(0, 0, bmpDrawable.getIntrinsicWidth(), bmpDrawable.getIntrinsicHeight());

        // create and set imagespan
        ssb.setSpan(new ImageSpan(bmpDrawable),start ,start + placeholder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(ssb);
        editText.setSelection(start + placeholder.length());
        System.out.println(editText.getText().toString());
    }

    private Bitmap textAsBitmap(String text, float textSize) {
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(convertDpToPixel(textSize));
        paint.setColor(getResources().getColor(R.color.TEAL));
        paint.setTextAlign(Paint.Align.LEFT);

        int width = (int) (paint.measureText(text) + 0.5f); // round
        float baseline = (int) (-paint.ascent() + 0.5f); // ascent() is negative
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);

        return image;
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
