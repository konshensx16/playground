package com.example.konshensx.firstapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.mySearchViewHolder> {
    public static final String EXTRA_MESSAGE = "com.example.konshensx.firstapp.MESSAGE";
    private Context myContext;
    private List<Listing> mySearchList;
    private FragmentManager fragmentManager;

    public SearchAdapter(Context myContext, List<Listing> myList, FragmentManager fragmentManager) {
        this.myContext = myContext;
        this.mySearchList = myList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SearchAdapter.mySearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(myContext);

        View v = inflater.inflate(R.layout.search_card_item, viewGroup, false);

        return new mySearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull mySearchViewHolder mySearchViewHolder, final int i) {
        mySearchViewHolder.name.setText(mySearchList.get(i).getName());
        mySearchViewHolder.symbol.setText(mySearchList.get(i).getSymbol());
        mySearchViewHolder.website_slug.setText(mySearchList.get(i).getWebsite_slug());

        // TODO: set an onClick event listener on the itemView
        // Also pass the id to the CurrencyDetails page and display it
        mySearchViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrencyDetails currencyDetailsFragment = CurrencyDetails.newInstance(mySearchList.get(i).getId());
                loadFragment(currencyDetailsFragment);

                /*
                Intent intent = new Intent(myContext, CurrencyDetails.class);
                int id = mySearchList.get(i).getId();
                intent.putExtra(EXTRA_MESSAGE, id);
                myContext.startActivity(intent);
                */
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frame_container, fragment, "fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public int getItemCount() {
        return mySearchList.size();
    }

    public class mySearchViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView symbol;
        TextView website_slug;

        public mySearchViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: set the result to the views (needs to be created)
            name = itemView.findViewById(R.id.search_card_name);
            symbol = itemView.findViewById(R.id.search_card_symbol);
            website_slug = itemView.findViewById(R.id.search_card_website_slug);
        }
    }
}
