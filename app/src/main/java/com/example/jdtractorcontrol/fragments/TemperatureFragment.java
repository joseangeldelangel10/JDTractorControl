package com.example.jdtractorcontrol.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jdtractorcontrol.MainActivity;
import com.example.jdtractorcontrol.R;
import com.example.jdtractorcontrol.helperClasses.unsignedInt;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TemperatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TemperatureFragment extends Fragment {
    public TextView currentTemperatureText;
    public TextView desiredTemperatureText;
    public GraphView temperatureGraph;
    public TextView currentTemperatureTv;
    public TextView desiredTemperatureTv;
    public ImageButton temperatureSPUp;
    public ImageButton temperatureSPDown;
    public Button changeSPButton;

    private ArrayList<Integer> btInterruptionsQueue = new ArrayList<>();
    private boolean initializing = false;
    private boolean changeSP = false;
    private boolean setPointResponseEnabled = false;
    public int currentTemperature;
    public int desiredTemperature;
    private Context context;
    private MainActivity mainActivity;
    private LineGraphSeries<DataPoint> temperatureSeries;
    private ArrayList<ArrayList<Integer>> temperatureSeriesArray;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
        temperatureSeriesArray = new ArrayList<>();
        changeSP = false;
        initializing = true;
        setPointResponseEnabled = false;
        context = getContext();
        mainActivity = ((MainActivity) context);
        desiredTemperature = 20;
        btInterruptionsQueue.add(250);

        /* ===========================================================
        *                       VIEW RRFERENCING
        *  =========================================================== */
        currentTemperatureText = view.findViewById(R.id.currentTemperatureText);
        desiredTemperatureText = view.findViewById(R.id.desiredTemperatureText);
        temperatureGraph = (GraphView) view.findViewById(R.id.TemperatureGraph);
        currentTemperatureTv = view.findViewById(R.id.CurrentTemperatureTv);
        desiredTemperatureTv = view.findViewById(R.id.DesiredTemperatureTv);
        temperatureSPUp = view.findViewById(R.id.TemperatureSPUp);
        temperatureSPDown = view.findViewById(R.id.TemperatureSPDown);
        changeSPButton = view.findViewById(R.id.changeSPButt);
        /* =========================================================== */
        setColors2NoSPChange();
        desiredTemperatureTv.setText("...");


        temperatureSeries = generateInitialSeries();
        temperatureGraph.addSeries(temperatureSeries);
        temperatureGraph.getViewport().setYAxisBoundsManual(true);
        temperatureGraph.getViewport().setMaxY(50.0);
        temperatureGraph.getViewport().setMinY(-10.0);
        temperatureGraph.getViewport().setScalableY(true);
        temperatureGraph.getViewport().setScrollable(true);


        if(mainActivity.connected && mainActivity.btInputStream != null){
            getRealTimeTemp();
        }else{
            currentTemperatureTv.setText("...");
        }

        changeSPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeSP == false) {
                    changeSP = true;
                    setColorsSPChange();
                    changeSPButton.setText("Enter");
                    btInterruptionsQueue.add(255);
                }else {
                    changeSP = false;
                    setColors2NoSPChange();
                    changeSPButton.setText("change set point");
                    btInterruptionsQueue.add(255);
                }
            }
        });


        temperatureSPUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeSP) {
                    desiredTemperature++;
                    updateDesiredTemperature();
                }
            }
        });

        temperatureSPDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeSP){
                    desiredTemperature--;
                    updateDesiredTemperature();
                }
            }
        });

    }

    private void updateDesiredTemperature(){
        desiredTemperatureTv.setText(String.valueOf(desiredTemperature));
    }

    private void setColors2NoSPChange() {
        currentTemperatureText.setTextColor(getResources().getColor(R.color.black));
        currentTemperatureTv.setTextColor(getResources().getColor(R.color.black));

        desiredTemperatureText.setTextColor(getResources().getColor(R.color.gray));
        desiredTemperatureTv.setTextColor(getResources().getColor(R.color.gray));
        temperatureSPUp.setClickable(false);
        temperatureSPDown.setClickable(false);
    }

    private void setColorsSPChange(){
        currentTemperatureText.setTextColor(getResources().getColor(R.color.gray));
        currentTemperatureTv.setTextColor(getResources().getColor(R.color.gray));

        desiredTemperatureText.setTextColor(getResources().getColor(R.color.black));
        desiredTemperatureTv.setTextColor(getResources().getColor(R.color.black));
        temperatureSPUp.setClickable(true);
        temperatureSPDown.setClickable(true);
    }

    private LineGraphSeries<DataPoint> generateInitialSeries(){
        temperatureSeriesArray.clear();
        for(int i = 0; i< 10; i++){
            ArrayList<Integer> coordinates = new ArrayList<>();
            coordinates.add(i);
            coordinates.add(0);
            temperatureSeriesArray.add(coordinates);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateNewDataPoints());
        return series;
    }

    private DataPoint[] generateNewDataPoints() {
        DataPoint[] dataPointsNatArr = new DataPoint[10];

        for(int i = 0; i< 10; i++){
            dataPointsNatArr[i] = new DataPoint(temperatureSeriesArray.get(i).get(0), temperatureSeriesArray.get(i).get(1));
        }

        return dataPointsNatArr;
    }

    private void push2TempArray(int newTemp){
        temperatureSeriesArray.remove(0);
        for(int i = 0; i<9 ; i++){
            temperatureSeriesArray.get(i).set(0,i);
        }
        ArrayList<Integer> newCoordinate = new ArrayList<>();
        newCoordinate.add(9);
        newCoordinate.add(newTemp);
        temperatureSeriesArray.add(newCoordinate);
    }


    public void getRealTimeTemp(){
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            int newTemperature;
            unsignedInt lecture;
            @Override
            public void run() {
                if(mainActivity.connected && mainActivity.btInputStream != null && mainActivity.bottomMenu.getSelectedItemId() == R.id.temperatureMenu){
                    try {
                        int bufferLenght;
                        byte b[] = new byte[8];
                        bufferLenght = mainActivity.btInputStream.read(b);
                        lecture = new unsignedInt(b[0]);

                        if(lecture.value >= 240 && lecture.value <=255){
                            if(lecture.value == 251 && initializing){
                                setPointResponseEnabled = true;
                            }
                            newTemperature = currentTemperature;
                        }else if(setPointResponseEnabled){
                            desiredTemperature = lecture.value;
                            setPointResponseEnabled = false;
                            initializing = false;
                            btInterruptionsQueue.add(250);
                            updateDesiredTemperature();
                            newTemperature = currentTemperature;
                        }else {
                            newTemperature = lecture.value;
                        }

                        if(btInterruptionsQueue.size() >0){
                            mainActivity.btOutputStream.write(btInterruptionsQueue.get(0));
                            btInterruptionsQueue.remove(0);
                        }else if(changeSP){
                            mainActivity.btOutputStream.write(desiredTemperature);
                        }else {
                            mainActivity.btOutputStream.write(240);
                        }
                        //Log.e("BUFFER LENGHT IS >>", String.valueOf(bufferLenght));
                        //String readMsg = new String(b, 0, bufferLenght);
                        //Log.e("Message is >> ", readMsg);

                        if(newTemperature != currentTemperature || currentTemperatureTv.getText().equals("...") ) {
                            currentTemperature = newTemperature;
                            currentTemperatureTv.setText(String.valueOf(currentTemperature));
                        }
                        push2TempArray(newTemperature);
                        temperatureSeries.resetData(generateNewDataPoints());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this::run, 2000);
                }else{
                    currentTemperatureTv.setText("...");
                }
            }
        };
        handler.postDelayed(runnable, 2000);
    }

}