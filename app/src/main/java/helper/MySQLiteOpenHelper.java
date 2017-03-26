package helper;

import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	 // Instances
    private static HashMap<Context, MySQLiteOpenHelper> mInstances;

    // Member object
    private Context mContext;

    // Database metadata
    public static final String DATABASE_NAME = "conditiondata.db";
    public static final int DATABASE_VERSION = 1;

    // Table names
//    public static final String TABLE_ONE = "TABLE_ONE";
//    public static final String TABLE_TWO = "TABLE_TWO";

    // Create table querys
//    private static final String QUERY_CREATE_TABLE_ONE = String.format(
//            "CREATE TABLE IF NOT EXISTS %s (" +
//                    "`%s` INTEGER primary key autoincrement, " +
//                    "`%s` INTEGER, " +
//                    "`%s` INTEGER, " +
//            ");",
//            TABLE_ONE,
//            "column_one", "column_two", "column_three");
//
//    private static final String QUERY_CREATE_TABLE_TWO = String.format(
//            "CREATE TABLE IF NOT EXISTS %s (" +
//                    "`%s` INTEGER primary key autoincrement, " +
//                    "`%s` INTEGER, " +
//                    "`%s` INTEGER, " +
//            ");",
//            TABLE_TWO,
//            "column_one", "column_two", "column_three");

    private MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context;
    }

    public static MySQLiteOpenHelper getInstance(Context context) {
        if(mInstances == null)
            mInstances = new HashMap<Context, MySQLiteOpenHelper>();

        if(mInstances.get(context) == null)
            mInstances.put(context, new MySQLiteOpenHelper(context));

        return mInstances.get(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(QUERY_CREATE_TABLE_ONE);
//        db.execSQL(QUERY_CREATE_TABLE_TWO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Upgrade your database here

    }


}
