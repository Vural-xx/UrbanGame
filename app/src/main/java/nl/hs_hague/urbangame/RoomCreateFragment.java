package nl.hs_hague.urbangame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import nl.hs_hague.urbangame.model.Room;

/**
 * Fragment to create a Room.
 */
public class RoomCreateFragment extends DialogFragment {
    EditText etName;
    EditText etStart;
    EditText etEnd;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.fragment_room_create, null);
        etName = (EditText) convertView.findViewById(R.id.create_room_name);
        etStart = (EditText) convertView.findViewById(R.id.create_room_start);
        etEnd = (EditText) convertView.findViewById(R.id.create_room_end);


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
