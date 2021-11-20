package com.example.jdtractorcontrol.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.jdtractorcontrol.MainActivity;
import com.example.jdtractorcontrol.R;
import com.example.jdtractorcontrol.helperClasses.unsignedInt;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RemoteControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemoteControlFragment extends Fragment {
    ImageButton buttonDown;
    ImageButton buttonUp;
    ImageButton buttonRight;
    ImageButton buttonLeft;
    ImageButton buttonStop;
    private ArrayList<Integer> btInterruptionsQueue;
    private MainActivity mainActivity;
    private Context context;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RemoteControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RemoteControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemoteControlFragment newInstance(String param1, String param2) {
        RemoteControlFragment fragment = new RemoteControlFragment();
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
        return inflater.inflate(R.layout.fragment_remote_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btInterruptionsQueue = new ArrayList<>();
        context = getContext();
        mainActivity = (MainActivity) context;

        /* ========================== VIEW REFERENCING ======================= */
        buttonUp = view.findViewById(R.id.RCArrowUp);
        buttonDown = view.findViewById(R.id.RCArrowDown);
        buttonRight = view.findViewById(R.id.RCArrowRight);
        buttonLeft = view.findViewById(R.id.RCArrowLeft);
        buttonStop = view.findViewById(R.id.RCStop);
        /* ========================== ============= ======================= */
        HandleBt();

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btInterruptionsQueue.add(243);
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btInterruptionsQueue.add(246);
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btInterruptionsQueue.add(245);
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btInterruptionsQueue.add(244);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btInterruptionsQueue.add(247);
            }
        });

    }


    public void HandleBt(){
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            int newTemperature;
            unsignedInt lecture;
            @Override
            public void run() {
                if(mainActivity.connected && mainActivity.btInputStream != null && mainActivity.bottomMenu.getSelectedItemId() == R.id.remoteControlMenu){
                    try {
                        int bufferLenght;
                        byte b[] = new byte[8];
                        bufferLenght = mainActivity.btInputStream.read(b);
                        lecture = new unsignedInt(b[0]);

                        if(btInterruptionsQueue.size() >0){
                            mainActivity.btOutputStream.write(btInterruptionsQueue.get(0));
                            btInterruptionsQueue.remove(0);
                        }else {
                            mainActivity.btOutputStream.write(240);
                        }
                        //Log.e("BUFFER LENGHT IS >>", String.valueOf(bufferLenght));
                        //String readMsg = new String(b, 0, bufferLenght);
                        //Log.e("Message is >> ", readMsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this::run, 2000);
                }else{
                    // do nothing
                }
            }
        };
        handler.postDelayed(runnable, 2000);
    }
}