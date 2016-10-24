package nl.hs_hague.urbangame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.hs_hague.urbangame.R;
import nl.hs_hague.urbangame.model.Checkpoint;

/**
 * Created by vural on 24.10.16.
 */

public class FoundCheckpointAdapter extends ArrayAdapter<Checkpoint> {

    private LayoutInflater inflater;
    private int resource;
    private List<Checkpoint> checkpoints;

    public FoundCheckpointAdapter(Context context, int resource, List<Checkpoint> checkpoints) {
        super(context, resource, checkpoints);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.checkpoints = checkpoints;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Hier wird die einzelne Reihe inflated
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }
        TextView tvRoomName = (TextView) convertView.findViewById(R.id.markerTitle);
        final Checkpoint checkpoint = (Checkpoint) checkpoints.get(position);
        tvRoomName.setText(checkpoint.getName() + " Hint: " + checkpoint.getHint());
        notifyDataSetChanged();
        return convertView;
    }
}