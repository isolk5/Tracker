package com.isol.app.tracker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ZoneFragment extends ListFragment {

	private ZoneListener mCallback = null;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ZoneFragment() {
	}

    // Container Activity must implement this interface
	//Interfaccia per modificare la lista portali quando si seleziona/deseleziona una zona
    public interface ZoneListener {
        public void onZoneSelectedListener(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ZoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ZoneListener");
        }
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
				zim.isResponsible = jObj.getBoolean("Flag_Responsible");
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
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

//		if (null != mListener) {
//			// Notify the active callbacks interface (the activity, if the
//			// fragment is attached to one) that an item has been selected.
//			mListener
//					.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
//		}
	}

	
	public class MySimpleArrayAdapter extends ArrayAdapter<ZoneItemModel>  {
		
		private final Context context;
		private final ArrayList<ZoneItemModel> values;
		
		public MySimpleArrayAdapter(Context aContext, ArrayList<ZoneItemModel> aValues) {
			super(aContext, R.layout.zone_item, aValues);
			this.context = aContext;
			this.values = aValues;
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
			
			//Le zone di competenza non possono essere tolte
			if (invItem.isResponsible) {
				viewHolder.cbZona.setEnabled(false);
				viewHolder.zoneDesc.setText(invItem.zoneDesc + " (responsabile)");
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
					
//					Context context = getApplicationContext();
					CharSequence text = "L'elenco dei tuoi portali è stato aggiornato !";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					
					mCallback.onZoneSelectedListener(position);
				}
			});
			
			return convertView;
		}
		
	}

	public class ZoneItemModel {
		public Boolean zoneAssociated;
		public String zoneDesc;
		public int zoneID;
		public boolean isResponsible;
	}

	public class InventarioModel {
		public ArrayList<ZoneItemModel> items;
	}

	static class ViewHolderItem {
		TextView zoneDesc;
		CheckBox cbZona;
	}

}
