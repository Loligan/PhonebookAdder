package dot.application.phonebookadder;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createMany(View view) throws JSONException {
        Intent intent = new Intent(this, MainActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText3);
        String text = String.valueOf(editText.getText());

        TextView textView = (TextView) findViewById(R.id.textView);

        JSONArray jsonArray = new JSONArray(text);
        try {
            InsertContacts(jsonArray);
            textView.setText("Json length: " + jsonArray.length());
        } catch (Exception exception) {
            textView.setText(exception.getMessage());
        }



    }

    private boolean InsertContacts(JSONArray jsonArray) {
        removeAllContacts();
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                InsertContact(String.valueOf(jsonArray.get(index)));
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private boolean InsertContact(String number) {
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, new ContentValues());
        long rawContactId = ContentUris.parseId(rawContactUri);
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Robert Smith");
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
//        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        return true;
    }

    private boolean removeAllContacts(){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cur.moveToNext()) {
            try{
                String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                System.out.println("The uri is " + uri.toString());
                cr.delete(uri, null, null);
            }
            catch(Exception e)
            {
                return  false;
            }
        }
        return true;
    }


    private void showAlert(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Важное сообщение!")
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
