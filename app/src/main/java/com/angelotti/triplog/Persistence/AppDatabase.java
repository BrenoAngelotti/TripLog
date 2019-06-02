package com.angelotti.triplog.Persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;
import com.angelotti.triplog.Model.*;

import java.util.concurrent.Executors;

@Database(entities = {Type.class, Trip.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract TypeDAO typeDAO();

    public abstract TripDAO tripDAO();

    private static AppDatabase instance;

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    Builder builder =  Room.databaseBuilder(context,
                            AppDatabase.class,
                            "triplog.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    loadInicialTypes(context);
                                }
                            });
                        }
                    });
                    instance = (AppDatabase) builder.build();
                }
            }
        }
        return instance;
    }


    private static void loadInicialTypes(final Context context){

        Type city = new Type("Cidade", "#ff8800");
        instance.typeDAO().insert(city);
    }

}
