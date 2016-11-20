package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHandler extends SQLiteOpenHelper {
    protected static final String dbName = "140011X";
    private static DbHandler dbConnect = null;
    private static final int dbVersion = 1;

    public static final String accountTableName = "Accounts";
    public static final String bankAccountNo = "accountNo";
    public static final String bankName = "bank";
    public static final String accountHolder = "accountHolder";
    public static final String balance = "balance";
    public static final String transactionTableName = "Transactions";
    public static final String transactionId = "transactionId";
    public static final String accountNo = "accountNo";
    public static final String date = "date";
    public static final String amount = "amount";
    public static final String expenseType = "expenseType";


    @Override
    public void onCreate(SQLiteDatabase db) {

        String accountTableSql = String.format("CREATE TABLE %s(%s VARCHAR(20) NOT NULL PRIMARY KEY, %s VARCHAR(100) NULL, %s VARCHAR(100) NULL, %s DECIMAL(10,2) NULL)"
                ,accountTableName,bankAccountNo,bankName,accountHolder,balance);

        String transactionTableSql = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,%s VARCHAR(100) NOT NULL, %s DATE NULL, %s DECIMAL(10,2) NULL,%s VARCHAR(100) NULL, FOREIGN KEY(%s) REFERENCES %s(%s))"
                ,transactionTableName,transactionId,accountNo, date,amount, expenseType,accountNo,accountTableName, bankAccountNo);

        db.execSQL(accountTableSql);
        db.execSQL(transactionTableSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+accountTableName);
        db.execSQL("DROP TABLE IF EXISTS"+transactionTableName);
        onCreate(db);
    }

    public DbHandler(Context context) {
        super(context, dbName, null, dbVersion);
    }

    public static DbHandler getInstance(Context context){
        if(dbConnect== null){
            dbConnect = new DbHandler(context);
        }
        return dbConnect;
    }
}
