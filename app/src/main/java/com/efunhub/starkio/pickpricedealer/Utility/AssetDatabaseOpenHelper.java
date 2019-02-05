package com.efunhub.starkio.pickpricedealer.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Admin on 18-12-2018.
 */

public class AssetDatabaseOpenHelper {

    private Context context;
    private String DB_NAME="pickpricedb.db";

    DataBaseHelper helper;
    String filePath = null;
    SQLiteDatabase database;

    public AssetDatabaseOpenHelper(Context context, String database_name) {
        this.context = context;
        this.DB_NAME = database_name;
    }

    public SQLiteDatabase saveDatabase() {

        helper = new DataBaseHelper(context,DB_NAME);
            database = helper.getWritableDatabase();
            filePath = database.getPath();
            database.close();

            Log.d("AssetDatabaseOpenHelper", "saveDatabase: "+filePath);

            if (filePath!=null) {
                try {
                    copyDatabase(filePath);
                } catch (IOException e) {
                    throw new RuntimeException("Error creating source database", e);
                }
            }
        return SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READONLY);
    }

    private void copyDatabase(String dbFile) throws IOException {


            InputStream is = context.getAssets().open(DB_NAME);
            OutputStream os = new FileOutputStream(dbFile);

            byte[] buffer = new byte[1024];
            while (is.read(buffer) > 0) {
                os.write(buffer);
            }

            os.flush();
            os.close();
            is.close();



    }

}
