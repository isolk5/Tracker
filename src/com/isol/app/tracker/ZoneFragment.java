package com.isol.app.tracker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.isol.app.tracker.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ZoneFragment extends ListFragment {

	private OnFragmentInteractionListener mListener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ZoneFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Ottengo l'inventario
		MyApplication myapp = MyApplication.getInstance();

		JSONArray jArr = Utilita
				.getJSONArray("JSONInterface.aspx?function=GetZonesByPerson&groupID="
						+ myapp.getGroupID() + "&personID=" + myapp.getPersonID());

		final ArrayList<ZoneItemModel> theList = new ArrayList<ZoneItemModel>();

		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(i);
				ZoneItemModel zim = new ZoneItemModel();
				zim.zoneID = jObj.getInt("Zone_ID");
				zim.zoneDesc = jObj.getString("Zone_Desc");
				zim.zoneAssociated = jObj.getString("Zone_Associated").equals("true")?true:false;
				theList.add(zim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		setListAdapter(new MySimpleArrayAdapter(getActivity(), theList));

	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// try {
	// mListener = (OnFragmentInteractionListener) activity;
	// } catch (ClassCastException e) {
	// throw new ClassCastException(activity.toString()
	// + " must implement OnFragmentInteractionListener");
	// }
	// }

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener
					.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
		}
	}

	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<ZoneItemModel>  {
		
		private final Context context;
		private final ArrayList<ZoneItemModel> values;

		public MySimpleArrayAdapter(Context context,
				ArrayList<ZoneItemModel> values) {
			super(context, R.layout.zone_item, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolderItem viewHolder;
			final ZoneItemModel invItem = values.get(position);

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.zone_item, parent,
						false);
				TextView tvNick = (TextView) convertView
						.findViewById(R.id.zoneDesc);
				CheckBox cbAss = (CheckBox) convertView.findViewById(R.id.cbZona);
				viewHolder = new ViewHolderItem();
				viewHolder.zoneDesc = tvNick;
				viewHolder.cbZona = cbAss;
				
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderItem) convertView.getTag();
			}

			viewHolder.zoneDesc.setText(invItem.zoneDesc);
			viewHolder.cbZona.setChecked(invItem.zoneAssociated);
			
			//La zona di competenza non puo' essere tolta
			MyApplication myapp = MyApplication.getInstance();
			if (myapp.getPersonZone()==invItem.zoneID) {
				viewHolder.cbZona.setEnabled(false);
			}
			
			viewHolder.cbZona.setId(position);			
			viewHolder.cbZona.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//Chiamo il servizio per aggiungere la zona
					MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter) getListAdapter();
					int position = v.getId();
					ZoneItemModel zim = theAdapter.getItem(position);
					
					// Aggiorno la zona per questa persona
					MyApplication myapp = MyApplication.getInstance();
					
					CheckBox cb = (CheckBox) v;
					zim.zoneAssociated = cb.isChecked();
					if (cb.isChecked()) {
						JSONArray jArr = Utilita
								.getJSONArray("JSONInterface.aspx?function=AddZoneByPerson&groupID="
										+ myapp.getGroupID() + "&personID=" + myapp.getPersonID() + "&zoneID=" + zim.zoneID);
					} else {
						JSONArray jArr = Utilita
								.getJSONArray("JSONInterface.aspx?function=DeleteZoneByPerson&groupID="
										+ myapp.getGroupID() + "&personID=" + myapp.getPersonID() + "&zoneID=" + zim.zoneID);
						
					}
				}
			});
			
			return convertView;
		}
		
	}

	public class ZoneItemModel {
		public Boolean zoneAssociated;
		public String zoneDesc;
		public int zoneID;
	}

	public class InventarioModel {
		public ArrayList<ZoneItemModel> items;
	}

	static class ViewHolderItem {
		TextView zoneDesc;
		CheckBox cbZona;
	}

}
