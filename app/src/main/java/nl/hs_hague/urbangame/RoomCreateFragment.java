package nl.hs_hague.urbangame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import nl.hs_hague.urbangame.model.MarkerHolder;
import nl.hs_hague.urbangame.model.Room;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment to create a Room.
 */
public class RoomCreateFragment extends DialogFragment {
    private EditText etName;
    private EditText etStart;
    private EditText etEnd;
    private Button btnSetCheckpoints;
    private Context context;
    private Activity activity;
    private Date startDate;
    private Date endDate;
    private boolean startTime;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.US);
    public static final int GET_MARKERS_REQUEST = 1;  // The request code
    public static final String MARKER = "marker";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View convertView = inflater.inflate(R.layout.fragment_room_create, null);
        etName = (EditText) convertView.findViewById(R.id.create_room_name);
        etStart = (EditText) convertView.findViewById(R.id.create_room_start);
        etEnd = (EditText) convertView.findViewById(R.id.create_room_end);
        btnSetCheckpoints = (Button) convertView.findViewById(R.id.btnSetCheckpoints);


        etStart.setText(dateFormatter.format(Calendar.getInstance().getTime()));
        startDate = Calendar.getInstance().getTime();

        context = this.getContext();
        activity = getActivity();

        etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDateTimePicker();
                startTime = true;
                //fromDatePickerDialog.show();
            }
        });

        etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDateTimePicker();
                startTime = false;
               // toDatePickerDialog.show();
            }
        });

        btnSetCheckpoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                startActivityForResult(intent, GET_MARKERS_REQUEST);
            }
        });

        final AlertDialog d = new AlertDialog.Builder(context)
                .setView(convertView)
                .setTitle(R.string.create_room)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null).create();


        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(!etName.getText().toString().equals("") && startDate != null && endDate != null){
                            Room room = new Room(etName.getText().toString(), startDate, endDate);
                            RoomListActivity.databaseHandler.createRoom(room);
                            d.dismiss();
                            Toast.makeText(getContext(),"Succesfully created new Room", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(),"Please fill out the form", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return  d;

    }

    public void createDateTimePicker(){
        final View dialogView = View.inflate(activity, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());
                if(startTime){
                    startDate = calendar.getTime();
                    etStart.setText(dateFormatter.format(calendar.getTime()));
                }else{
                    endDate = calendar.getTime();
                    etEnd.setText(dateFormatter.format(calendar.getTime()));
                }
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(R.string.create_room);
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_MARKERS_REQUEST) {
            if (resultCode == RESULT_OK) {
                MarkerHolder markerHolder = (MarkerHolder) data.getSerializableExtra(MARKER);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
