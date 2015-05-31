package com.sairamarao.shareyourplaylists;

import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class Playlists extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        String[] projection = {MediaStore.Audio.Playlists._ID,MediaStore.Audio.Playlists.NAME};
        Uri PlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(PlaylistURI,projection,null,null,null);
        if (cursor.getCount() == 0)
            Log.e("Playlists", "No Playlists available.");
        Log.e("Playlists"," "+cursor.getCount());
        String s = null;
        for(boolean hasItem = cursor.moveToFirst();hasItem;hasItem = cursor.moveToNext())
        {
            s = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
            Log.e("Playlists", s);
        }
        String[] from = {MediaStore.Audio.Playlists.NAME};
        int[] to = {R.id.playlistname};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.row,cursor,from,to,0);
        ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlists, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
