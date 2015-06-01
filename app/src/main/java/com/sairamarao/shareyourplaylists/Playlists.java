package com.sairamarao.shareyourplaylists;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AbsListView.MultiChoiceModeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Playlists extends ListActivity{

    Map<Integer,String> getplayListIDs = new HashMap<Integer,String>();
    String shareText = " ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        String[] projection = {MediaStore.Audio.Playlists._ID,MediaStore.Audio.Playlists.NAME};
        Uri PlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(PlaylistURI,projection,null,null,null);
        //Cursor database has the data rows with the following columns playlistID and playlistName.
        if (cursor.getCount() == 0)
            Log.e("Playlists", "No Playlists available.");
        /* TODO: Add error message view element for no playlists*/
        Log.e("Playlists"," "+cursor.getCount());
        String s = null;
        int i = 1;
        //Maintain a Hashmap for Playlist position and its ID.
        for(boolean hasItem = cursor.moveToFirst();hasItem;hasItem = cursor.moveToNext())
        {
            s = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
            getplayListIDs.put(new Integer(i),s);
            Log.e("Playlists"," "+i+" "+s);
            i++;
        }

        String[] from = {MediaStore.Audio.Playlists.NAME};
        int[] to = {R.id.playlistname};
        //Adding All Songs option
        MatrixCursor extras = new MatrixCursor(new String[] {MediaStore.Audio.Playlists._ID,MediaStore.Audio.Playlists.NAME});
        extras.addRow(new String[] { "-1","All Songs"});
        getplayListIDs.put(new Integer(0),"-1");
        Cursor[] cursors = {extras, cursor};
        Cursor extendedcursor = new MergeCursor(cursors);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.row,extendedcursor,from,to,0);
        final ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        ListView list = (ListView)v;
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        int position = info.position;
        String PlaylistID = getplayListIDs.get(new Integer(position));
        Log.e("Playlists"," "+position+" "+ PlaylistID);
        if (position == 0)
        {
            String[] Allsongsproj = {MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION};
            Cursor AllSongsCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Allsongsproj,null,null,null);

            if (AllSongsCursor!=null)
            {
                int j = 1;
                String temp = " ";
                while(AllSongsCursor.moveToNext())
                {
                    temp = AllSongsCursor.getString(AllSongsCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    Log.e("OnCreateContextMenu: ", temp);
                    shareText = shareText + "\n" + j + " "+ temp;
                    j++;
                }
            }
            else
                Log.e("OnCreateContextMenu: ","AllSongsCursor is null");


        }
        else
        {

            String[] Playproj = {MediaStore.Audio.Playlists.Members.TITLE,
                    MediaStore.Audio.Playlists.Members.ARTIST,
                    MediaStore.Audio.Playlists.Members.AUDIO_ID,
                    MediaStore.Audio.Playlists.Members._ID
            };

            Uri Songs = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.valueOf(PlaylistID).longValue());
            Cursor c = getContentResolver().query(Songs, Playproj, null, null, null);
            if (c.getCount() == 0)
                Log.e("Songs", "No Songs available.");
            Log.e("Songs", " " + c.getCount());
            String s = null;
            int i = 1;
            for (boolean hasItem = c.moveToFirst(); hasItem; hasItem = c.moveToNext()) {
                s = c.getString(c.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
                Log.e("Songs", " " + i + " " + s);
                shareText = shareText + "\n" + i + " " + s;
                i++;
            }
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
            case R.id.action_share:

                Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Songs: \n");
                sendIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareText);
                startActivity(sendIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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
