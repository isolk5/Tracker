package com.isol.app.tracker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PortaliFragment extends ListFragment 
implements OnSharedPreferenceChangeListener {

final String PORTAL_SORT_NAME = "1";
final String PORTAL_SORT_ZONE = "2";	

/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PortaliFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ArrayList<InventarioItemModel> theList =getArrayList();
		setListAdapter(new MySimpleArrayAdapter(getActivity(), theList));
		
		PreferenceManager.getDefaultSharedPreferences(getActivity())
 			.registerOnSharedPreferenceChangeListener(this);
	}


	public ArrayList<InventarioItemModel> getArrayList() {
		
		//Recupero i settings
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String portalSort = sharedPref.getString("pref_portal_sort","");
		
		// Ottengo l'inventario
		MyApplication myapp = MyApplication.getInstance();

		JSONArray jArr = Utilita
				.getJSONArray("JSONInterface.aspx?function=GetPortals&groupID="
						+ myapp.getGroupID() + "&personID="
						+ myapp.getPersonID() + "&sort=" + portalSort);

		ArrayList<InventarioItemModel> theList = new ArrayList<InventarioItemModel>();

		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(i);
				InventarioItemModel iim = new InventarioItemModel();
				iim.descrizione = jObj.getString("Item_Desc");
				iim.descrizioneTipo = jObj.getString("Item_Type_Desc");
				iim.quantita = jObj.getInt("Item_Quantity");
				iim.item_id = jObj.getInt("Item_ID");
				iim.Flag_Item_Location = jObj.getInt("Flag_Item_Location");
				iim.Portal_Zone = jObj.getString("Portal_Zone");
				iim.statoCarica=jObj.getInt("ChargeState");
				iim.rating = jObj.getInt("Portal_Rating");
			    iim.note=jObj.getBoolean("Portal_Notes_Presence");
			    
				theList.add(iim);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return theList;
	
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onResume() {
		super.onResume();
		Principale mainActivity = (Principale)getActivity();
		if (mainActivity.flagUpdatePortalData) {
			mainActivity.flagUpdatePortalData=false;
			ArrayList<InventarioItemModel> theList = getArrayList();
			MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter)getListAdapter();
			theAdapter.clear();
			theAdapter.addAll(theList);
			theAdapter.notifyDataSetChanged();
		}
		
	}

//	@Override
//	public void onPause() {
//	    super.onPause();
//		PreferenceManager.getDefaultSharedPreferences(getActivity())
//	            .unregisterOnSharedPreferenceChangeListener(this);
//	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
       // Make sure the request was successful
        if (resultCode == getActivity().RESULT_OK) {  
        	
        	//Verifico se si tratta di una ricarica
        	String ricarica = data.getStringExtra(Constants.PAR_ITEM_RECHARGED);
        	if (ricarica == null) {
        		
	        	//aggiorno la quantita' come modificata nella activity di dettaglio
	        	int theQta = data.getIntExtra(Constants.PAR_ITEM_QTA, 0);
	        	String note = data.getStringExtra(Constants.PAR_ITEM_NOTE);
	        	int stato = data.getIntExtra(Constants.PAR_ITEM_STATO, 0);
	        	int theIndex = data.getIntExtra(Constants.PAR_ARRAY_INDEX,-1);
	        	MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter)getListAdapter();
	        	theAdapter.getItem(theIndex).quantita=theQta;
	        	theAdapter.getItem(theIndex).rating=stato;
	        	if (note.trim().equals("")) {
	        		theAdapter.getItem(theIndex).note=false;
	        	} else {
	        		theAdapter.getItem(theIndex).note=true;
	        	}
	        	theAdapter.notifyDataSetChanged();
	        	        		
        	} else {
        		
	        	int theIndex = data.getIntExtra(Constants.PAR_ARRAY_INDEX,-1);
	        	MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter)getListAdapter();
	        	theAdapter.getItem(theIndex).statoCarica=4;
	        	theAdapter.notifyDataSetChanged();
        	}
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

	public class MySimpleArrayAdapter extends ArrayAdapter<InventarioItemModel> {
		
		private final Context context;
		private ArrayList<InventarioItemModel> values;

		public MySimpleArrayAdapter(Context context,
				ArrayList<InventarioItemModel> values) {
			super(context, R.layout.portalitem, values);
			this.context = context;
			this.values = values;
		}

	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolderItem viewHolder;
			final InventarioItemModel invItem = values.get(position);

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.portalitem, parent,
						false);
				TextView tvName = (TextView) convertView
						.findViewById(R.id.portalName);
				TextView tvZone = (TextView) convertView
						.findViewById(R.id.portalZone);
				TextView tvQta = (TextView) convertView
						.findViewById(R.id.qta);
				ImageView imStatoCarica = (ImageView) convertView
						.findViewById(R.id.imageCarica);
				ImageView imChiave = (ImageView) convertView
						.findViewById(R.id.imageChiave);
				ImageView imNote = (ImageView) convertView
						.findViewById(R.id.imageNote);
				ImageView imRating = (ImageView) convertView
						.findViewById(R.id.imageRating);
				viewHolder = new ViewHolderItem();
				viewHolder.portalName = tvName;
				viewHolder.portalZone = tvZone;
				viewHolder.qta = tvQta;
				viewHolder.FlagItemLocation = invItem.Flag_Item_Location;
				viewHolder.imgStatoCarica = imStatoCarica;
				viewHolder.imgChiave = imChiave;
				viewHolder.imgNote = imNote;
				viewHolder.imgRating = imRating;
				
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolderItem) convertView.getTag();
			}

			if (invItem != null) {
				viewHolder.portalName.setText(invItem.descrizione);
				viewHolder.portalZone.setText(invItem.Portal_Zone);
				viewHolder.qta.setText(Integer.toString(invItem.quantita));
				viewHolder.FlagItemLocation = invItem.Flag_Item_Location;
				viewHolder.imgStatoCarica.setImageResource(0);
				
				//Mostro l'immagine corretta per la carica
				int imageResId = -1;
				if (invItem.statoCarica == 0) {
					imageResId = R.drawable.pila0;
				} else if (invItem.statoCarica == 1) {
					imageResId = R.drawable.pila1;
				} else if (invItem.statoCarica == 2) {
					imageResId = R.drawable.pila2;
				} else if (invItem.statoCarica == 3) {
					imageResId = R.drawable.pila3;
				} else if (invItem.statoCarica == 4) {
					imageResId = R.drawable.pila4;
				} 
				
				viewHolder.qta.setBackgroundColor(Color.parseColor("#DADADA"));
				if (invItem.rating > 0) {
					viewHolder.imgStatoCarica.setImageResource(imageResId);
					viewHolder.imgStatoCarica.setAlpha(Color.parseColor("#DADADA"));
					
					if (invItem.quantita == 0) {
						viewHolder.qta.setBackgroundColor(Color.parseColor("#E11C1C"));
					}
				}

				viewHolder.imgChiave.setImageResource(R.drawable.chiave);
				
				if (invItem.note) {
					viewHolder.imgNote.setImageResource(R.drawable.note);
				} else {
					viewHolder.imgNote.setImageResource(-1);					
				}
				
				int imageRatingID = -1;
				if (invItem.rating == 0) {
					imageRatingID = R.drawable.rating0;
				} else if (invItem.rating == 1) {
					imageRatingID = R.drawable.rating1;
				} else if (invItem.rating == 2) {
					imageRatingID = R.drawable.rating2;
				} else if (invItem.rating == 3) {
					imageRatingID = R.drawable.rating3;
				} 
				viewHolder.imgRating.setImageResource(imageRatingID);
				
			}

			convertView.setId(position);
			
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
//					//chiamo l'activity user_item
					Intent intent = new Intent(context, PortalDetailActivity.class);
                    final int ind = v.getId();
                    int item_ID = values.get(ind).item_id;
					int FlagItemLocation = values.get(ind).Flag_Item_Location;
					intent.putExtra(Constants.PAR_ITEM_ID, item_ID);	
					intent.putExtra(Constants.PAR_FLAG_ITEM_LOCATION, FlagItemLocation);	
					intent.putExtra(Constants.PAR_ARRAY_INDEX, ind);	
					startActivityForResult(intent,Constants.REQUEST_CODE_PORTAL_DETAIL);
				}
			});
			
			return convertView;
		}
		
	}

	public class InventarioItemModel {
		public String descrizione; 	
		public String descrizioneTipo;
		public String Portal_Zone;
		public boolean note;
		public int quantita;
		public int item_id;
		public int Flag_Item_Location;
		public int statoCarica;
		public int rating;
	}

	public class InventarioModel {
		public ArrayList<InventarioItemModel> items;
	}

	static class ViewHolderItem {
		TextView portalName;
		TextView portalZone;
		TextView qta;
		ImageView imgStatoCarica;
		ImageView imgChiave;
		ImageView imgNote;
		ImageView imgRating;
		int FlagItemLocation;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
		// TODO Auto-generated method stub
		if (key.equals("pref_view_all_portals")) {
			
			Boolean value = sharedPref.getBoolean(key, false);
			
			//Invoco il servizio per modificare la preferenza sul server
			MyApplication myapp = MyApplication.getInstance();

			JSONArray jArr = Utilita
					.getJSONArray("JSONInterface.aspx?function=ChangePreference"
							+ "&personID=" + myapp.getPersonID()
							+ "&key=" + key + "&value=" + value.toString());
			
			//Devo refreshare la lista
        	MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter)getListAdapter();
        	theAdapter.clear();
        	theAdapter.addAll(getArrayList());
        	theAdapter.notifyDataSetChanged();
			
		}
		
		if (key.equals("pref_portal_sort")) {
			//Devo refreshare la lista
        	MySimpleArrayAdapter theAdapter = (MySimpleArrayAdapter)getListAdapter();
        	theAdapter.clear();
        	theAdapter.addAll(getArrayList());
        	theAdapter.notifyDataSetChanged();
        	
		}
	}

}
