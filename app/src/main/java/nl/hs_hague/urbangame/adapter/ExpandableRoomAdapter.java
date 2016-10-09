package nl.hs_hague.urbangame.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.hs_hague.urbangame.R;
import nl.hs_hague.urbangame.model.Room;

/**
 * Created by vural on 06.10.16.
 */

public class ExpandableRoomAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Room>> listDataChild;

    public ExpandableRoomAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Room>> listChildData) {
        this._context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Room room = (Room)getChild(groupPosition,childPosition);

        final String childText = (String) room.getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.room_list_content, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.room_name);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(listDataChild != null && listDataHeader != null && listDataChild.size() != 0){
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void updateRooms(List<Room> newlist, int group) {
        if(listDataChild.get(group) != null){
            listDataChild.get(group).clear();
            listDataChild.get(group).addAll(newlist);
            this.notifyDataSetChanged();
        }else{
            listDataChild = new HashMap<String, List<Room>>();
            listDataChild.put(listDataHeader.get(0),new ArrayList<Room>());
            listDataChild.put(listDataHeader.get(1),new ArrayList<Room>());
            listDataChild.get(listDataHeader.get(group)).clear();
            listDataChild.get(listDataHeader.get(group)).addAll(newlist);
            this.notifyDataSetChanged();
        }
    }

}
