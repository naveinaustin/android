package org.example.starbuzz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkActivity extends AppCompatActivity {

    public static final String EXTRA_DRINKNO = "EXTRA_DRINKNO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int drinkNo = (Integer) getIntent().getExtras().get(EXTRA_DRINKNO);

        /*Drink drink = Drink.drinks[drinkNo];

        ImageView photo = (ImageView)findViewById(R.id.photo);
        photo.setImageResource(drink.getImageResourceId());
        photo.setContentDescription(drink.getName());

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(drink.getName());
        TextView description = (TextView)findViewById(R.id.description);
        description.setText(drink.getDescription());*/
        try {
            SQLiteOpenHelper starbuzzDatbaseHelper = new StarbuzzDatabaseHelper(this);
            SQLiteDatabase db = starbuzzDatbaseHelper.getReadableDatabase();
            Cursor cursor = db.query ("DRINK",
                    new String[] {"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID", "FAVORITE"},
                    "_id = ?",
                    new String[] {Integer.toString(drinkNo)},
                    null, null,null);

            if(cursor.moveToFirst()) {
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int photoId = cursor.getInt(2);
                boolean isFavorite = (cursor.getInt(3) == 1);

                TextView name = (TextView)findViewById(R.id.name);
                name.setText(nameText);
                //Populate the drink description
                TextView description = (TextView)findViewById(R.id.description);
                description.setText(descriptionText);
                //Populate the drink image
                ImageView photo = (ImageView)findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);

                //Populate the favorite checkbox
                CheckBox favorite = (CheckBox)findViewById(R.id.favorite);
                favorite.setChecked(isFavorite);
            }

            cursor.close();
            db.close();

        } catch (SQLiteException sqle) {
            Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFavoriteClicked(View view){
        int drinkNo = (Integer) getIntent().getExtras().get(EXTRA_DRINKNO);
        //CheckBox favorite = (CheckBox)findViewById(R.id.favorite);

        /*ContentValues drinkValues = new ContentValues();
        drinkValues.put("FAVORITE", favorite.isChecked());

        SQLiteOpenHelper starbuzzDatbaseHelper = new StarbuzzDatabaseHelper(this);
        SQLiteDatabase db = null;

        try {
            db = starbuzzDatbaseHelper.getWritableDatabase();
            db.update("DRINK", drinkValues,
                    "_id=?",
                    new String[] {Integer.toString(drinkNo)});

        } catch (SQLiteException sqle) {
            Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();
        } finally {
            if(db != null) {
                db.close();
            }
        }*/

        new UpdateDrinkTask().execute(drinkNo);
    }

    private class UpdateDrinkTask extends AsyncTask<Integer, Void, Boolean> {
        ContentValues drinkValues;

        protected void onPreExecute() {
            CheckBox favorite = (CheckBox) findViewById(R.id.favorite);
            drinkValues = new ContentValues();
            drinkValues.put("FAVORITE", favorite.isChecked());
        }

        protected Boolean doInBackground(Integer... drinks) {
            int drinkNo = drinks[0];
            SQLiteOpenHelper starbuzzDatabaseHelper =
                    new StarbuzzDatabaseHelper(DrinkActivity.this);
            SQLiteDatabase db = null;
            try {
                db = starbuzzDatabaseHelper.getWritableDatabase();
                db.update("DRINK", drinkValues,
                        "_id = ?", new String[]{Integer.toString(drinkNo)});
                db.close();
                return true;
            } catch (SQLiteException e) {
                if(db != null) {
                    db.close();
                }
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(DrinkActivity.this,
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}
