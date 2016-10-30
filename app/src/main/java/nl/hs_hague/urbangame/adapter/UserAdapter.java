package nl.hs_hague.urbangame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.hs_hague.urbangame.R;
import nl.hs_hague.urbangame.model.User;

/**
 * Created by vural on 24.10.16.
 * Adapter to display members of room
 */
public class UserAdapter extends ArrayAdapter<User> {

    private LayoutInflater inflater;
    private int resource;
    private List<User> users;

    public UserAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.users = users;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Hier wird die einzelne Reihe inflated
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }
        TextView tvMemeberName = (TextView) convertView.findViewById(R.id.member_name);
        User user = users.get(position);
        tvMemeberName.setText(user.getEmail());
        notifyDataSetChanged();
        return convertView;
    }
}