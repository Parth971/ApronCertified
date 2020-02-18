package com.example.aproncertified;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimingFrg extends Fragment {

    private static final String TAG = "TimingFrg";

    private Activity context;
    List<Timings> tempList;
    private int mHour, mMinute;

    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public TimingFrg(Activity context, List<Timings> tempList) {
        this.context = context;
        this.tempList = tempList;
    }



    public interface TimingListener {
        void getTiming(List<Timings> strings);
    }

    private  TimingListener mMenuListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_layout, container, false);

        Log.d(TAG, "onCreateView: ");

        Button btnDone = v.findViewById(R.id.btnDone);
        Button setFromForAll = v.findViewById(R.id.setFromForAll);
        Button setToForAll = v.findViewById(R.id.setToForAll);

        final Spinner fromSaturdayHr = v.findViewById(R.id.fromSaturdayHr);
        final Spinner fromSaturdayMin = v.findViewById(R.id.fromSaturdayMin);
//        Spinner fromSaturdayDayNight = v.findViewById(R.id.fromSaturdayDayNight);

        final Spinner fromSundayHr = v.findViewById(R.id.fromSundayHr);
        final Spinner fromSundayMin = v.findViewById(R.id.fromSundayMin);
//        Spinner fromSundayDayNight = v.findViewById(R.id.fromSundayDayNight);

        final Spinner fromMondayHr = v.findViewById(R.id.fromMondayHr);
        final Spinner fromMondayMin = v.findViewById(R.id.fromMondayMin);
//        Spinner fromMondayDayNight = v.findViewById(R.id.fromMondayDayNight);

        final Spinner fromTuesdayHr = v.findViewById(R.id.fromTuesdayHr);
        final Spinner fromTuesdayMin = v.findViewById(R.id.fromTuesdayMin);
//        Spinner fromTuesdayDayNight = v.findViewById(R.id.fromTuesdayDayNight);

        final Spinner fromWednesdayHr = v.findViewById(R.id.fromWednesdayHr);
        final Spinner fromWednesdayMin = v.findViewById(R.id.fromWednesdayMin);
//        Spinner fromWednesdayDayNight = v.findViewById(R.id.fromWednesdayDayNight);

        final Spinner fromThursdayHr = v.findViewById(R.id.fromThursdayHr);
        final Spinner fromThursdayMin = v.findViewById(R.id.fromThursdayMin);
//        Spinner fromThursdayDayNight = v.findViewById(R.id.fromThursdayDayNight);

        final Spinner fromFridayHr = v.findViewById(R.id.fromFridayHr);
        final Spinner fromFridayMin = v.findViewById(R.id.fromFridayMin);
//        Spinner fromFridayDayNight = v.findViewById(R.id.fromFridayDayNight);


        final Spinner toSaturdayHr = v.findViewById(R.id.toSaturdayHr);
        final Spinner toSaturdayMin = v.findViewById(R.id.toSaturdayMin);
//        Spinner toSaturdayDayNight = v.findViewById(R.id.toSaturdayDayNight);

        final Spinner toSundayHr = v.findViewById(R.id.toSundayHr);
        final Spinner toSundayMin = v.findViewById(R.id.toSundayMin);
//        Spinner toSundayDayNight = v.findViewById(R.id.toSundayDayNight);

        final Spinner toMondayHr = v.findViewById(R.id.toMondayHr);
        final Spinner toMondayMin = v.findViewById(R.id.toMondayMin);
//        Spinner toMondayDayNight = v.findViewById(R.id.toMondayDayNight);

        final Spinner toTuesdayHr = v.findViewById(R.id.toTuesdayHr);
        final Spinner toTuesdayMin = v.findViewById(R.id.toTuesdayMin);
//        Spinner toTuesdayDayNight = v.findViewById(R.id.toTuesdayDayNight);

        final Spinner toWednesdayHr = v.findViewById(R.id.toWednesdayHr);
        final Spinner toWednesdayMin = v.findViewById(R.id.toWednesdayMin);
//        Spinner toWednesdayDayNight = v.findViewById(R.id.toWednesdayDayNight);

        final Spinner toThursdayHr = v.findViewById(R.id.toThursdayHr);
        final Spinner toThursdayMin = v.findViewById(R.id.toThursdayMin);
//        Spinner toThursdayDayNight = v.findViewById(R.id.toThursdayDayNight);

        final Spinner toFridayHr = v.findViewById(R.id.toFridayHr);
        final Spinner toFridayMin = v.findViewById(R.id.toFridayMin);
//        Spinner toFridayDayNight = v.findViewById(R.id.toFridayDayNight);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.hours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> minAdapter = ArrayAdapter.createFromResource(context, R.array.minutes, android.R.layout.simple_spinner_item);
        minAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        toSaturdayHr.setAdapter(adapter);
        toSundayHr.setAdapter(adapter);
        toMondayHr.setAdapter(adapter);
        toTuesdayHr.setAdapter(adapter);
        toWednesdayHr.setAdapter(adapter);
        toThursdayHr.setAdapter(adapter);
        toFridayHr.setAdapter(adapter);

        fromSaturdayHr.setAdapter(adapter);
        fromSundayHr.setAdapter(adapter);
        fromMondayHr.setAdapter(adapter);
        fromTuesdayHr.setAdapter(adapter);
        fromWednesdayHr.setAdapter(adapter);
        fromThursdayHr.setAdapter(adapter);
        fromFridayHr.setAdapter(adapter);

        toSaturdayMin.setAdapter(minAdapter);
        toSundayMin.setAdapter(minAdapter);
        toMondayMin.setAdapter(minAdapter);
        toTuesdayMin.setAdapter(minAdapter);
        toWednesdayMin.setAdapter(minAdapter);
        toThursdayMin.setAdapter(minAdapter);
        toFridayMin.setAdapter(minAdapter);

        fromSaturdayMin.setAdapter(minAdapter);
        fromSundayMin.setAdapter(minAdapter);
        fromMondayMin.setAdapter(minAdapter);
        fromTuesdayMin.setAdapter(minAdapter);
        fromWednesdayMin.setAdapter(minAdapter);
        fromThursdayMin.setAdapter(minAdapter);
        fromFridayMin.setAdapter(minAdapter);


        if(tempList != null){
            int spinnerPosition;

            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(0).getFromHr()));
            fromMondayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(0).getFromMin()));
            fromMondayMin.setSelection(spinnerPosition);
            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(0).getToHr()));
            toMondayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(0).getToMin()));
            toMondayMin.setSelection(spinnerPosition);

            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(1).getFromHr()));
            fromTuesdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(1).getFromMin()));
            fromTuesdayMin.setSelection(spinnerPosition);
            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(1).getToHr()));
            toTuesdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(1).getToMin()));
            toTuesdayMin.setSelection(spinnerPosition);

            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(2).getFromHr()));
            fromWednesdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(2).getFromMin()));
            fromWednesdayMin.setSelection(spinnerPosition);
            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(2).getToHr()));
            toWednesdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(2).getToMin()));
            toWednesdayMin.setSelection(spinnerPosition);

            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(3).getFromHr()));
            fromThursdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(3).getFromMin()));
            fromThursdayMin.setSelection(spinnerPosition);
            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(3).getToHr()));
            toThursdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(3).getToMin()));
            toThursdayMin.setSelection(spinnerPosition);

            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(4).getFromHr()));
            fromFridayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(4).getFromMin()));
            fromFridayMin.setSelection(spinnerPosition);
            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(4).getToHr()));
            toFridayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(4).getToMin()));
            toFridayMin.setSelection(spinnerPosition);

            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(5).getFromHr()));
            fromSaturdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(5).getFromMin()));
            fromSaturdayMin.setSelection(spinnerPosition);
            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(5).getToHr()));
            toSaturdayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(5).getToMin()));
            toSaturdayMin.setSelection(spinnerPosition);

            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(6).getFromHr()));
            fromSundayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(6).getFromMin()));
            fromSundayMin.setSelection(spinnerPosition);
            spinnerPosition = adapter.getPosition(String.valueOf(tempList.get(6).getToHr()));
            toSundayHr.setSelection(spinnerPosition);
            spinnerPosition = minAdapter.getPosition(String.valueOf(tempList.get(6).getToMin()));
            toSundayMin.setSelection(spinnerPosition);

        }
        else {
            Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
        }
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempList = new ArrayList<>();
                tempList.clear();

                tempList.add(new Timings(days[0], Integer.valueOf(fromMondayHr.getSelectedItem().toString()),Integer.valueOf(fromMondayMin.getSelectedItem().toString()),
                        Integer.valueOf(toMondayHr.getSelectedItem().toString()), Integer.valueOf(toMondayMin.getSelectedItem().toString())  ));
                tempList.add(new Timings(days[1], Integer.valueOf(fromTuesdayHr.getSelectedItem().toString()),Integer.valueOf(fromTuesdayMin.getSelectedItem().toString()),
                        Integer.valueOf(toTuesdayHr.getSelectedItem().toString()), Integer.valueOf(toTuesdayMin.getSelectedItem().toString())  ));
                tempList.add(new Timings(days[2], Integer.valueOf(fromWednesdayHr.getSelectedItem().toString()),Integer.valueOf(fromWednesdayMin.getSelectedItem().toString()),
                        Integer.valueOf(toWednesdayHr.getSelectedItem().toString()), Integer.valueOf(toWednesdayMin.getSelectedItem().toString())  ));
                tempList.add(new Timings(days[3], Integer.valueOf(fromThursdayHr.getSelectedItem().toString()),Integer.valueOf(fromThursdayMin.getSelectedItem().toString()),
                        Integer.valueOf(toThursdayHr.getSelectedItem().toString()), Integer.valueOf(toThursdayMin.getSelectedItem().toString())  ));
                tempList.add(new Timings(days[4], Integer.valueOf(fromFridayHr.getSelectedItem().toString()),Integer.valueOf(fromFridayMin.getSelectedItem().toString()),
                        Integer.valueOf(toFridayHr.getSelectedItem().toString()), Integer.valueOf(toFridayMin.getSelectedItem().toString())  ));
                tempList.add(new Timings(days[5], Integer.valueOf(fromSaturdayHr.getSelectedItem().toString()),Integer.valueOf(fromSaturdayMin.getSelectedItem().toString()),
                        Integer.valueOf(toSaturdayHr.getSelectedItem().toString()), Integer.valueOf(toSaturdayMin.getSelectedItem().toString())  ));
                tempList.add(new Timings(days[6], Integer.valueOf(fromSundayHr.getSelectedItem().toString()),Integer.valueOf(fromSundayMin.getSelectedItem().toString()),
                        Integer.valueOf(toSundayHr.getSelectedItem().toString()), Integer.valueOf(toSundayMin.getSelectedItem().toString())  ));

                mMenuListener.getTiming(tempList);
            }
        });
        setFromForAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                int spinnerPosition = adapter.getPosition(hourOfDay + "");

                                fromSaturdayHr.setSelection(spinnerPosition);
                                fromSundayHr.setSelection(spinnerPosition);
                                fromMondayHr.setSelection(spinnerPosition);
                                fromTuesdayHr.setSelection(spinnerPosition);
                                fromWednesdayHr.setSelection(spinnerPosition);
                                fromThursdayHr.setSelection(spinnerPosition);
                                fromFridayHr.setSelection(spinnerPosition);

                                int spinnerPositionMin = minAdapter.getPosition(minute + "");

                                fromSaturdayMin.setSelection(spinnerPositionMin);
                                fromSundayMin.setSelection(spinnerPositionMin);
                                fromMondayMin.setSelection(spinnerPositionMin);
                                fromTuesdayMin.setSelection(spinnerPositionMin);
                                fromWednesdayMin.setSelection(spinnerPositionMin);
                                fromThursdayMin.setSelection(spinnerPositionMin);
                                fromFridayMin.setSelection(spinnerPositionMin);

                                Log.d(TAG, "onTimeSet: " + hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        setToForAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                int spinnerPosition = adapter.getPosition(hourOfDay + "");
                                toSaturdayHr.setSelection(spinnerPosition);
                                toSundayHr.setSelection(spinnerPosition);
                                toMondayHr.setSelection(spinnerPosition);
                                toTuesdayHr.setSelection(spinnerPosition);
                                toWednesdayHr.setSelection(spinnerPosition);
                                toThursdayHr.setSelection(spinnerPosition);
                                toFridayHr.setSelection(spinnerPosition);

                                int spinnerPositionMin = minAdapter.getPosition(minute + "");
                                toSaturdayMin.setSelection(spinnerPositionMin);
                                toSundayMin.setSelection(spinnerPositionMin);
                                toMondayMin.setSelection(spinnerPositionMin);
                                toTuesdayMin.setSelection(spinnerPositionMin);
                                toWednesdayMin.setSelection(spinnerPositionMin);
                                toThursdayMin.setSelection(spinnerPositionMin);
                                toFridayMin.setSelection(spinnerPositionMin);

                                Log.d(TAG, "onTimeSet: " + hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        return v;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mMenuListener = (TimingListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }



}
