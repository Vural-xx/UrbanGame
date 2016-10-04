package nl.hs_hague.urbangame;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.hs_hague.urbangame.model.Room;

/**
 * Fragment to create a Room.
 */
public class RoomCreateFragment extends DialogFragment {
    EditText etName;
    EditText etStart;
    EditText etEnd;
    private Context context;
    private Activity activity;
    DatePickerDialog fromDatePickerDialog;
    DatePickerDialog toDatePickerDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.fragment_room_create, null);
        etName = (EditText) convertView.findViewById(R.id.create_room_name);
        etStart = (EditText) convertView.findViewById(R.id.create_room_start);
        etEnd = (EditText) convertView.findViewById(R.id.create_room_end);

        //datepicker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.US);
        etStart.setText(dateFormatter.format(calendar.getTime()));

        context = this.getContext();
        activity = getActivity();

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //etStart.setOnClickListener(onDateClick(etStart.getVie));


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(convertView)
                // Add action buttons
                .setPositiveButton(R.string.menu_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Room room = new Room(etName.getText().toString(), new Date(), new Date());
                        RoomListActivity.databaseHandler.createRoom(room);
                        getDialog().cancel();
                        Toast.makeText(getContext(),"Succesfully created new Room", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();

                    }
                });
        return builder.create();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(R.string.create_room);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
