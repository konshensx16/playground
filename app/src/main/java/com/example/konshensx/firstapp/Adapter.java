package com.example.konshensx.firstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.myViewHolder> {
    public static final String EXTRA_MESSAGE = "com.example.konshensx.firstapp.MESSAGE";
    private static final String TAG = "Adapter";
    private Context myContext;

    private List<Currency> mData;
    private FragmentManager fragmentManager;

    public Adapter(Context context, List<Currency> mData, FragmentManager fragmentManager) {
        this.myContext = context;
        this.mData = mData;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Don't know what the code inside this does
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        TODO: further reading about inflater
        LayoutInflater inflater = LayoutInflater.from(myContext);

        View v = inflater.inflate(R.layout.card_item, viewGroup, false);

        return new myViewHolder(v);
    }

    /**
     * This one is pretty much self explanatory
     * @param myViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, final int i) {
        myViewHolder.symbol.setText(mData.get(i).getSymbol());
        myViewHolder.name.setText(mData.get(i).getName());
        myViewHolder.price.setText(String.format("%,f $", mData.get(i).getQuotes().getPrice()));
        if (mData.get(i).getQuotes().getPercentChange1H() > 0)
        {
            myViewHolder.change.setTextColor(Color.parseColor("#3AD084"));
        } else {
            myViewHolder.change.setTextColor(Color.parseColor("#F8748A"));
        }
        myViewHolder.change.setText(String.format("%,.2f %%", mData.get(i).getQuotes().getPercentChange1H()));
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked");
                // replace this with a fragment instead of an activity
                CurrencyDetails currencyDetailsFragment = CurrencyDetails.newInstance(mData.get(i).getId());
                loadFragment(currencyDetailsFragment, "currencyDetails");

            }
        });
    }

    private void loadFragment(Fragment fragment, String tag)
    {
        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frame_container, fragment, "fragment");
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * Gets the number of items that wil be displayed
     * @return Integer
     */
    @Override
    public int getItemCount() {
        // adding one for the loader at the bottom
//        return mData.size() + 1;
        return mData.size();
    }

    /**
     * Class which is used for the generic class Adapter
     */
    class myViewHolder extends RecyclerView.ViewHolder
    {
        ImageView background_img;
        TextView symbol;
        TextView name;
        TextView price;
        TextView change;

        public myViewHolder(View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbol);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            change = itemView.findViewById(R.id.change);
        }
    }

}
