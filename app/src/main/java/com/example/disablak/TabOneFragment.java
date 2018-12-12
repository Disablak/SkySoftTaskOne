package com.example.disablak.skysofttaskone;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TabOneFragment extends Fragment {
    private static final String TAG = "TabOneFragment";

    private ArrayList<ItemTab> itemList = new ArrayList<>();
    private ItemsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        Convert();
        ListView mListView = view.findViewById(R.id.itemsList);
        adapter = new ItemsListAdapter(getContext(), R.layout.list_item, itemList);
        mListView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    public void Convert(){
        for (int i = 1; i <= 20; i++){
            new ConverterJson().execute("https://jsonplaceholder.typicode.com/photos/" + i);
        }

    }

    public class ConverterJson extends AsyncTask<String, String, String> {

        int ID ;

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);

                String finalURL = parentObject.getString("url");
                ID = parentObject.getInt("id");
                return finalURL;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();
                try {
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            itemList.add(new ItemTab(ID, s));
            adapter.notifyDataSetChanged();
            addImageInTwo(s);
        }
    }

    public void addImageInTwo(String url){
        ImageView iv = new ImageView(getContext());
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        int sizePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        Picasso.get().load(url).into(iv);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        iv.setPadding(sizePadding, sizePadding, sizePadding, sizePadding);
        iv.setLayoutParams(layoutParams);
        TabTwoFragment.gridLayout.addView(iv);
    }
}


