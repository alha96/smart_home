package com.arkas.smarthomecontroller.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.arkas.smarthomecontroller.LedListItem;
import com.arkas.smarthomecontroller.LedListViewAdapter;
import com.arkas.smarthomecontroller.R;
import com.arkas.smarthomecontroller.RecyclerItemClickListener;
import com.arkas.smarthomecontroller.SharedPreferencesHelper;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class StateFragment extends Fragment {

    // TODO: Customize parameter argument names --> may be removed
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    SharedPreferencesHelper sph;

    LedListViewAdapter ledListViewAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StateFragment() {
    }

    @SuppressWarnings("unused")
    public static StateFragment newInstance(int columnCount) {
        StateFragment fragment = new StateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    List<LedListItem> listOfLeds;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        sph = new SharedPreferencesHelper(getActivity());
        listOfLeds = sph.getListFromSharedPreferences();

        final Context context = view.getContext();
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        ledListViewAdapter = new LedListViewAdapter(listOfLeds, mListener);
        recyclerView.setAdapter(ledListViewAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "Long press an item to delete it.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.i("StateFragment", "Removing element: " + position);
                Toast.makeText(getActivity(), "Item " + listOfLeds.get(position).getName() + " deleted.", Toast.LENGTH_SHORT).show();
                sph.deleteItemFromList(position);

                recyclerView.invalidate();
                recyclerView.removeViewAt(position);
                ledListViewAdapter.notifyItemRemoved(position);
            }
        }));


        //Adding the FAB
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("StateFragment", "Clicked FloatingActionButton");

                //Adding the DialogBuilder
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final LayoutInflater linf = getActivity().getLayoutInflater();

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View v = linf.inflate(R.layout.dialog_add_led, null);

                alert.setView(v);

                alert.setTitle("Add the information");
                alert.setMessage("of the LED to add");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final EditText etTopic = (EditText) v.findViewById(R.id.topic);
                        String topic = etTopic.getText().toString();

                        final EditText etName = (EditText) v.findViewById(R.id.name);
                        String name = etName.getText().toString();

                        Log.i("StateFragment", "Added Topic " + name + " with name " + name);
                        Toast.makeText(context, "Entered topic: " + topic + " with name " + name, Toast.LENGTH_SHORT).show();

                        LedListItem led = new LedListItem(name, topic);
                        sph.addItemToSharedPreferences(led);
                        recyclerView.invalidate();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(context, "No LED added!", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.show();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(LedListItem item);
    }

}
