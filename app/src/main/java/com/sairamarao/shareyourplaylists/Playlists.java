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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.TextView;

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
        Log.d("Playlists","Count: "+cursor.getCount());
        String s = null;
        int i = 1;//0 is reserved for All Songs position
        //Maintain a Hashmap for Playlist position and its ID.
        while(cursor.moveToNext())
        {
            s = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
            getplayListIDs.put(new Integer(i),s);
            Log.d("Playlists"," "+i+" "+s);
            i++;
        }

        //Adding All Songs option
        MatrixCursor extras = new MatrixCursor(new String[] {MediaStore.Audio.Playlists._ID,MediaStore.Audio.Playlists.NAME});
        extras.addRow(new String[] { "-1","All Songs"});
        getplayListIDs.put(new Integer(0),"-1");
        Cursor[] cursors = {extras, cursor};
        Cursor extendedcursor = new MergeCursor(cursors);

        String[] from = {MediaStore.Audio.Playlists.NAME};
        int[] to = {R.id.playlistname};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.row,extendedcursor,from,to,0);
        final ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        shareText = "";
        ListView list = (ListView)v;
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        int position = info.position;
        shareText += ((TextView)info.targetView.findViewById(R.id.playlistname)).getText().toString();
        String PlaylistID = getplayListIDs.get(new Integer(position));
        if (position == 0)//All Songs Option
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
                String name = " ";
                while(AllSongsCursor.moveToNext()) {
                    name = AllSongsCursor.getString(AllSongsCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    shareText = shareText + "\n" + j + " "+ name;
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
            String title = "";
            int i = 1;
            while(c.moveToNext()) {
                title = c.getString(c.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
                shareText = shareText + "\n" + i + " " + title;
                i++;
            }
        }

        Log.e("OnCreateContextMenu:", shareText);
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
