package com.b2infosoft.giftcardup.fragments.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.b2infosoft.giftcardup.R;

import ru.noties.scrollable.CanScrollVerticallyDelegate;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Identification.OnFragmentIdentification} interface
 * to handle interaction events.
 * Use the {@link Identification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Identification extends Fragment implements CanScrollVerticallyDelegate {

    private final String TAG = Identification.class.getName();

    EditText f_name,l_name,email,mobile,city,state,zip_code,cmpny_name,paypal_id,address,suite_no;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentIdentification mListener;

    public Identification() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Identification.
     */
    // TODO: Rename and change types and number of parameters
    public static Identification newInstance(String param1, String param2) {
        Identification fragment = new Identification();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        view = inflater.inflate(R.layout.fragment_identification, container, false);

        f_name = (EditText)view.findViewById(R.id.identity_f_name);
        l_name = (EditText)view.findViewById(R.id.identity_l_name);
        email = (EditText)view.findViewById(R.id.identity_email);
        city = (EditText)view.findViewById(R.id.identity_city);
        state = (EditText)view.findViewById(R.id.identity_state);
        paypal_id = (EditText)view.findViewById(R.id.identity_paypal_id);
        suite_no = (EditText)view.findViewById(R.id.identity_suite_no);
        address = (EditText)view.findViewById(R.id.identity_address);
        mobile = (EditText)view.findViewById(R.id.identity_phone);
        cmpny_name = (EditText)view.findViewById(R.id.identity_cmpny_name);
        zip_code = (EditText)view.findViewById(R.id.identity_zip_code);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onIdentification(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentIdentification) {
            mListener = (OnFragmentIdentification) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentIdentification");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentIdentification {
        // TODO: Update argument type and name
        void onIdentification(Uri uri);
    }
}
