package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentAccountDAO implements AccountDAO {
    private Context context;
    private DbHandler handler;

    public PersistentAccountDAO(Context context) {
        this.context = context;
        handler = DbHandler.getInstance(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = handler.getReadableDatabase();
        String query = String.format("SELECT %s FROM %s ORDER BY %s ASC", handler.bankAccountNo, handler.accountTableName, handler.bankAccountNo);
        Cursor cursor = db.rawQuery(query , null);
        ArrayList<String> result = new ArrayList<String>();
        while(cursor.moveToNext())
        {
            result.add(cursor.getString(cursor.getColumnIndex(handler.bankAccountNo)));
        }
        cursor.close();
        return result;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = handler.getReadableDatabase();

        String query = String.format("SELECT * FROM %s ORDER BY %s ASC", handler.accountTableName, handler.bankAccountNo);
        Cursor cursor = db.rawQuery(query , null);

        ArrayList<Account> result = new ArrayList<>();

        while(cursor.moveToNext())
        {
            Account account = new Account(cursor.getString(cursor.getColumnIndex(handler.bankAccountNo)),
                    cursor.getString(cursor.getColumnIndex(handler.bankName)),
                    cursor.getString(cursor.getColumnIndex(handler.accountHolder)),
                    cursor.getDouble(cursor.getColumnIndex(handler.balance)));

            result.add(account);
        }
        cursor.close();
        return result;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = handler.getReadableDatabase();

        String query = "SELECT * FROM " + handler.accountTableName + " WHERE " + handler.bankAccountNo + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query , null);

        Account account = null;

        if(cursor.moveToFirst())
        {
            account = new Account(cursor.getString(cursor.getColumnIndex(handler.bankAccountNo)),
                    cursor.getString(cursor.getColumnIndex(handler.bankName)),
                    cursor.getString(cursor.getColumnIndex(handler.accountHolder)),
                    cursor.getDouble(cursor.getColumnIndex(handler.balance)));


        }
        else   {
            throw new InvalidAccountException("Invalid Acc. No.");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = handler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(handler.accountNo, account.getAccountNo());
        values.put(handler.bankName, account.getBankName());
        values.put(handler.accountHolder, account.getAccountHolderName());
        values.put(handler.balance, account.getBalance());

        db.insert(handler.accountTableName, null, values);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = handler.getWritableDatabase();

        String query = "SELECT * FROM " + handler.accountTableName + " WHERE " + handler.bankAccountNo + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;


        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(handler.bankAccountNo)),
                    cursor.getString(cursor.getColumnIndex(handler.bankName)),
                    cursor.getString(cursor.getColumnIndex(handler.accountHolder)),
                    cursor.getFloat(cursor.getColumnIndex(handler.balance)));
            db.delete(handler.accountTableName, handler.bankAccountNo + " = ?", new String[] { accountNo });
            cursor.close();

        }

        else {
            throw new InvalidAccountException("Account can not found");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();

        Account account = getAccount(accountNo);

        if (account!=null) {

            double new_amount=0;

            if (expenseType.equals(ExpenseType.EXPENSE)) {
                new_amount = account.getBalance() - amount;
            }

            else if (expenseType.equals(ExpenseType.INCOME)) {
                new_amount = account.getBalance() + amount;
            }

            String strSQL = "UPDATE "+ handler.accountTableName+" SET "+ handler.balance+" = "+new_amount+" WHERE "+ handler.bankAccountNo+" = '"+ accountNo+"'";

            db.execSQL(strSQL);

        }

        else {
            throw new InvalidAccountException("Account can not found");
        }


    }
}
