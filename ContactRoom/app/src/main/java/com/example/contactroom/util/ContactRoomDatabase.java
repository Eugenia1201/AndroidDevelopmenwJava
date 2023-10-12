package com.example.contactroom.util;

import android.content.Context;

import androidx.annotation.NonNull;

import androidx.room.Database;

import com.example.contactroom.model.Contact;
import com.example.contactroom.data.ContactDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.callback.Callback;

@Database(entities = {Contact.class}, version=1,exportSchema = false)
public abstract class ContactRoomDatabase implements Database {
    public abstract ContactDao contactDao();
    private static volatile ContactRoomDatabase INSTANCE; //volatile means this instance will always to be able to remove itself if need be
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    //this gonna write things into the database, but not in the main thread.
    //Expansive operations have to happen in the background

    //method to return an instance of contactRoomDB
    public static ContactRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (ContactRoomDatabase.class) {//when dealing w background threads to make sure things work properly
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ContactRoomDatabase.class, "contact_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db){
                    super.onCreate(db);

                    databaseWriteExecutor.execute(() -> {
                        ContactDao contactDao = INSTANCE.contactDao();
                        contactDao.deleteAll();

                        Contact contact = new Contact("kaiqi","Student");
                        contactDao.insert(contact);

                        contact = new Contact("Bond", "Spy");
                        contactDao.insert(contact);
                    });
                }
            };
}

//Using the idea of observers and notifiers so that we don't directly run operations in the main thread.
//The main thread is only used to draw the user interface and interact with the users. All other activities happen at the backend.