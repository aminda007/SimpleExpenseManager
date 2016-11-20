package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class PersistentTransactionDAO implements TransactionDAO {

    private Context context;
    private DbHandler handler;
    public PersistentTransactionDAO(Context context) {
        this.context = context;
        handler = DbHandler.getInstance(context);

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(handler.accountNo,accountNo);
        values.put(handler.date, dateFormatString(date));
        values.put(handler.amount, amount);
        values.put(handler.expenseType, expenseType.toString());
        db.insert(handler.transactionTableName , null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return getPaginatedTransactionLogs(0);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = handler.getReadableDatabase();
        String querySize = String.format("SELECT count(accountNo) FROM %s ", handler.transactionTableName);
        Cursor cursorSize = db.rawQuery(querySize, null);
        int size = cursorSize.getCount();
        String query;
        if(size<=limit){
            query = "SELECT "+ handler.accountNo + ", " + handler.date + ", " + handler.expenseType +", " + handler.amount +
                    " FROM " + handler.transactionTableName +
                    " ORDER BY " + handler.transactionId +
                    " DESC";
        }
        else {
            query = "SELECT "+ handler.accountNo + ", " + handler.date + ", " + handler.expenseType +", " + handler.amount +
                    " FROM " + handler.transactionTableName +
                    " ORDER BY " + handler.transactionId +
                    " DESC LIMIT" + limit;
        }

        Cursor cursor = db.rawQuery(query,null);

        ArrayList<Transaction> transactionLogData = new ArrayList<>();

        while (cursor.moveToNext())
        {
            try{
                ExpenseType expenseType = null;
                if(cursor.getString(cursor.getColumnIndex(handler.expenseType)).equals(ExpenseType.INCOME.toString())){
                    expenseType = ExpenseType.INCOME;
                }
                else {
                    expenseType = ExpenseType.EXPENSE;
                }

                String dateString = cursor.getString(cursor.getColumnIndex(handler.date));
                Date date = dateFormatDate(dateString);
                Transaction transaction = new Transaction(date,cursor.getString(cursor.getColumnIndex(handler.accountNo)),
                        expenseType,
                        cursor.getDouble(cursor.getColumnIndex(handler.amount))
                );
                transactionLogData.add(transaction);
            }
            catch (ParseException e){
                e.printStackTrace();
            }

        }
        return transactionLogData;
    }


    public static String dateFormatString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        return dateString;
    }

    public static Date dateFormatDate(String date) throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = dateFormat.parse(date);
        return strDate;
    }





}

