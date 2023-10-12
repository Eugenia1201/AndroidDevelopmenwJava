package com.example.contactroom.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;


import com.example.contactroom.model.Contact;

import java.util.List;
@Dao
public interface ContactDao {
    //This interface will take care of all the CRUD operation
    // This is an interface which means all those methods won't have a body, because they have to be implemented at a class level.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Contact contact);
    @Query ("DELETE FROM contact_table")
    void deleteAll();
    @Query ("SELECT * FROM contact_table ORDER BY name ASC")
    LiveData <List<Contact>> getAllContacts();

    @Query("SELECT * FROM contact_table WHERE contact_table.id == :id")
    LiveData<Contact> get(int id);

    @Update
    void update(Contact contact); //once implemented in class, it gets activated (no longer grey)
    @Delete
    void delete(Contact contact);


}
