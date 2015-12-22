package com.thesis.mmtt2011.homemms.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.persistence.MessageContentProvider;

public class SearchResultsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView mListViewMessages;
    SimpleCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mListViewMessages = (ListView)findViewById(R.id.lv_messages);
        mListViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent messageIntent = new Intent(getApplicationContext(), MessageContentActivity.class);
                Uri data = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI, String.valueOf(id));

                messageIntent.setData(data);
                startActivity(messageIntent);
            }
        });

        // Defining CursorAdapter for the ListView
        mCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[] { android.R.id.text1}, 0);
        mListViewMessages.setAdapter(mCursorAdapter);
        Intent intent = getIntent();

        if(intent.getAction().equals(Intent.ACTION_VIEW)) {
            Intent messageIntent = new Intent(this, MessageContentActivity.class);
            messageIntent.setData(intent.getData());
            startActivity(messageIntent);
            finish();
        } else if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            // If this activity is invoked, when user presses "Go" in the Keyboard of Search Dialog
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
        //handleIntent(getIntent());
    }

    private void doSearch(String query) {
        Bundle data = new Bundle();
        data.putString("query", query);
        // Invoking onCreateLoader() in non-ui thread
        getSupportLoaderManager().initLoader(1, data, this);
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            //use the query to search your data somehow
        }
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        Uri uri = MessageContentProvider.CONTENT_URI;
        return new CursorLoader(getBaseContext(), uri, null, null, new String[]{data.getString("query")}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
