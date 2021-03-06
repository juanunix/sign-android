package mn.signlanguage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

import pl.droidsonroids.gif.GifDrawable;

public class DetailsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] filelistInSubfolder;
    ImageView dImage;
    String catName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_vertical);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences(CircleMenuActivity.PREFER_NAME, 0);
        String item = sharedPreferences.getString("item", "");
        catName = sharedPreferences.getString("item_category", "");
        getSupportActionBar().setTitle(sharedPreferences.getString("item_title", ""));
        dImage = (ImageView)findViewById(R.id.details_image);

        if (item.toString().contains(".")) {
            dImage.setImageDrawable(loadGifDrawable(getApplicationContext(), item+"gif"));
        } else {
            dImage.setImageBitmap(loadBitmapFromAssets(getApplicationContext(), item+".JPG"));
        }

        final AssetManager assetManager = getAssets();

        try {
            String[] filelist = assetManager.list("");
            filelistInSubfolder = assetManager.list(sharedPreferences.getString("item_category", ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            Toast.makeText(getApplicationContext(), "Алдаа гарлаа !!!", Toast.LENGTH_LONG).show();
        }

        mAdapter = new MyAdapter(filelistInSubfolder,filelistInSubfolder);
        mRecyclerView.setAdapter(mAdapter);

    }

    public GifDrawable loadGifDrawable(Context context, String path) {
        GifDrawable gifFromAssets = null;
        try {
            gifFromAssets = new GifDrawable( getAssets(), path );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gifFromAssets;
    }

    public Bitmap loadBitmapFromAssets(Context context, String path)
    {
        InputStream stream = null;
        try
        {
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
        }
        catch (Exception ignored) {} finally
        {
            try
            {
                if(stream != null)
                {
                    stream.close();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mDataset;
        private String[] mImages;

        public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mTextView;
            public ImageView mImageView;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                mTextView = (TextView)v.findViewById(R.id.txt);
                mImageView = (ImageView) v.findViewById(R.id.img);

            }

            @Override
            public void onClick(View v) {
                if (mTextView.getText().toString().contains(".")) {
                    dImage.setImageDrawable(loadGifDrawable(getApplicationContext(), catName+"/"+mTextView.getText().toString()+"gif"));
                } else {

                    dImage.setImageBitmap(loadBitmapFromAssets(getApplicationContext(), catName+"/"+mTextView.getText().toString()+".JPG"));
                }
                getSupportActionBar().setTitle(mTextView.getText().toString().replace(".",""));
            }
        }

        public MyAdapter(String[] myDataset, String[] myImages) {
            mDataset = myDataset;
            mImages = myImages;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.details_list, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if(mImages[position].contains(".gif")) {
                holder.mTextView.setText(mDataset[position].replaceAll("([.])([A-Za-z])*", "."));
                holder.mImageView.setImageDrawable(loadGifDrawable(getApplicationContext(), catName+"/"+mImages[position]));
            } else {
                holder.mTextView.setText(mDataset[position].replaceAll("([.])([A-Za-z])*", ""));
                holder.mImageView.setImageBitmap(loadBitmapFromAssets(getApplicationContext(), catName+"/"+mImages[position]));
            }
        }

        public int getItemCount() {
            return mDataset.length;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }
}
