package com.example.jdtractorcontrol.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jdtractorcontrol.MainActivity;
import com.example.jdtractorcontrol.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TemperatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TemperatureFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public GraphView temperatureGraph;
    public TextView currentTemperatureTv;
    public TextView desiredTemperatureTv;
    public ImageButton temperatureSPUp;
    public ImageButton temperatureSPDown;
    public int currentTemperature;
    public int desiredTemperature;
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TemperatureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TemperatureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TemperatureFragment newInstance(String param1, String param2) {
        TemperatureFragment fragment = new TemperatureFragment();
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
        return inflater.inflate(R.layout.fragment_temperature, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ==================== VIEW RRFERENCING ==================== */
        temperatureGraph = (GraphView) view.findViewById(R.id.TemperatureGraph);
        currentTemperatureTv = view.findViewById(R.id.CurrentTemperatureTv);
        desiredTemperatureTv = view.findViewById(R.id.DesiredTemperatureTv);
        temperatureSPUp = view.findViewById(R.id.TemperatureSPUp);
        temperatureSPDown = view.findViewById(R.id.TemperatureSPDown);


        context = getContext();
        MainActivity mainActivity = ((MainActivity) context);


        if(mainActivity.connected && mainActivity.btInputStream != null){
            try {
                byte b[] = new byte[1];
                //Log.e("Buffer read: ", String.valueOf(mainActivity.btInputStream.read()));
                mainActivity.btInputStream.read(b);
                currentTemperature = b[0];
                currentTemperatureTv.setText(String.valueOf(currentTemperature));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            currentTemperatureTv.setText("...");
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0,1),
                new DataPoint(1,5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 4),
                new DataPoint(5, 0),
        });
        temperatureGraph.addSeries(series);
        temperatureGraph.getViewport().setScrollable(true);
        //temperatureGraph.
    }
}