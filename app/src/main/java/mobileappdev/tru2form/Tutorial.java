package mobileappdev.tru2form;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.ex.chips.RecipientEditTextView;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;


public class Tutorial extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Hide the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Prevents user from interacting with ui
        View contacts = findViewById(R.id.contactsField);
        contacts.setEnabled(false);
        View first = findViewById(R.id.firstNameReplaceButton);
        first.setEnabled(false);
        View full = findViewById(R.id.fullNameReplaceButton);
        full.setEnabled(false);
        View message = findViewById(R.id.messageField);
        message.setEnabled(false);

        // Begin showing tutorial
        displayShowcaseViewOne();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
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

    private void displayShowcaseViewOne() {
        new ShowcaseView.Builder(this)
            .setContentTitle("Contacts Field")
            .setContentText("Search for recipients in your contacts")
            .setTarget(new ViewTarget(R.id.contactsField, this))
            .setShowcaseEventListener(new OnShowcaseEventListener() {

                @Override
                public void onShowcaseViewShow(final ShowcaseView scv) {
                }

                @Override
                public void onShowcaseViewHide(final ShowcaseView scv) {
                    scv.setVisibility(View.GONE);
                    displayShowcaseViewTwo();
                }

                @Override
                public void onShowcaseViewDidHide(final ShowcaseView scv) {
                }

            })
            .build().setButtonText("Next");
    }

    private void displayShowcaseViewTwo() {
        new ShowcaseView.Builder(this)
            .setContentTitle("Replacing First Name")
            .setContentText("Click this button to insert a placeholder that will be replaced with a recipient's first name upon sending the group message")
            .setTarget(new ViewTarget(R.id.firstNameReplaceButton, this))
            .setShowcaseEventListener(new OnShowcaseEventListener() {

                @Override
                public void onShowcaseViewShow(final ShowcaseView scv) {
                }

                @Override
                public void onShowcaseViewHide(final ShowcaseView scv) {
                    scv.setVisibility(View.GONE);
                    displayShowcaseViewThree();
                }

                @Override
                public void onShowcaseViewDidHide(final ShowcaseView scv) {
                }

            })
            .build().setButtonText("Next");
    }

    private void displayShowcaseViewThree() {
        new ShowcaseView.Builder(this)
            .setContentTitle("Replacing Full Name")
            .setContentText("Click this button to insert a placeholder that will be replaced with a recipient's full name upon sending the group message. If a recipient does not have a last name, then no last name will be added.")
            .setTarget(new ViewTarget(R.id.fullNameReplaceButton, this))
            .setShowcaseEventListener(new OnShowcaseEventListener() {

                @Override
                public void onShowcaseViewShow(final ShowcaseView scv) {
                }

                @Override
                public void onShowcaseViewHide(final ShowcaseView scv) {
                    scv.setVisibility(View.GONE);
                    displayShowcaseViewFour();
                }

                @Override
                public void onShowcaseViewDidHide(final ShowcaseView scv) {
                }

            })
            .build().setButtonText("Next");
    }
    private void displayShowcaseViewFour() {
        new ShowcaseView.Builder(this)
            .setContentTitle("Write Your Message")
            .setContentText("Compose your message here. Placeholders are represented with blue text and can be deleted like normal text.")
            .setTarget(new ViewTarget(R.id.messageField, this))
            .setShowcaseEventListener(new OnShowcaseEventListener() {

                @Override
                public void onShowcaseViewShow(final ShowcaseView scv) {
                }

                @Override
                public void onShowcaseViewHide(final ShowcaseView scv) {
                    scv.setVisibility(View.GONE);
                    // Return to main activity
                    finish();
                }

                @Override
                public void onShowcaseViewDidHide(final ShowcaseView scv) {
                }

            })
            .build().setButtonText("Finish");
    }
}
