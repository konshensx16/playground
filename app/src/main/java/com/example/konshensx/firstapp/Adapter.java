package com.example.konshensx.firstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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

    public Adapter(Context context, List<Currency> mData) {
        this.myContext = context;
        this.mData = mData;
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
                Intent intent = new Intent(myContext, CurrencyDetails.class);
                int id = mData.get(i).getId();
                intent.putExtra(EXTRA_MESSAGE, id);
                myContext.startActivity(intent);
            }
        });
//        myViewHolder

    }

    /**
     * Gets the number of items that wil be displayed
     * @return
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Class which is used for the generic class Adapter
     */
    public class myViewHolder extends RecyclerView.ViewHolder
    {
        ImageView background_img;
        TextView symbol;
        TextView name;
        TextView price;
        TextView change;

        public myViewHolder(View itemView) {
            super(itemView);
            background_img = itemView.findViewById(R.id.background_img);
            symbol = itemView.findViewById(R.id.symbol);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            change = itemView.findViewById(R.id.change);
        }
    }

}
